package nl.alleveenstra.genyornis.httpd;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.alleveenstra.genyornis.ServerContext;
import nl.alleveenstra.genyornis.channels.SocketHook;
import static nl.alleveenstra.genyornis.httpd.SocketState.HTTP;

public class NioServer implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(NioServer.class);

    // The host:port combination to listen on
    private InetAddress hostAddress;
    private int port;

    // The channel on which we'll accept connections
    private ServerSocketChannel serverChannel;

    // The selector we'll be monitoring
    private Selector selector;

    // The buffer into which we'll read data when it's available
    private ByteBuffer readBuffer = ByteBuffer.allocate(8192);

    private HttpWorker worker;

    // A list of PendingChange instances
    private List<ChangeRequest> pendingChanges = new LinkedList<ChangeRequest>();

    // Maps a SocketChannel to a list of ByteBuffer instances
    private static final SocketState DEFAULT_SOCKET_STATE = HTTP;
    private Map<SocketChannel, SocketState> socketStateMap = new HashMap<SocketChannel, SocketState>();
    private Map<SocketChannel, List<ByteBuffer>> pendingData = new HashMap<SocketChannel, List<ByteBuffer>>();
    private ServerContext context;
    private Map<SocketChannel, String> websocketUri = new HashMap<SocketChannel, String>();

    public NioServer(ServerContext context, InetAddress hostAddress, int port, HttpWorker worker) {
        this.hostAddress = hostAddress;
        this.port = port;
        this.context = context;
        this.worker = worker;
        try {
            this.selector = initSelector();
        } catch (IOException e) {
            log.error("Unable to initialize server", e);
            System.exit(0);
        }
    }

    public void send(SocketChannel socket, byte[] data) {
        synchronized (pendingChanges) {
            // Indicate we want the interest ops set changed
            pendingChanges.add(new ChangeRequest(socket, ChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));

            // And queue the data we want written
            synchronized (pendingData) {
                List<ByteBuffer> queue = pendingData.get(socket);
                if (queue == null) {
                    queue = new ArrayList<ByteBuffer>();
                    pendingData.put(socket, queue);
                }
                queue.add(ByteBuffer.wrap(data));
            }
        }

        // Finally, wake up our selecting thread so it can make the required changes
        this.selector.wakeup();
    }

    public SocketState getSocketState(final SocketChannel socket) {
        if (!socketStateMap.containsKey(socket)) {
            socketStateMap.put(socket, DEFAULT_SOCKET_STATE);
        }
        return socketStateMap.get(socket);
    }

    public void setSocketState(final SocketChannel socket, SocketState state) {
        socketStateMap.put(socket, state);
    }

    public void setWebsocketURI(final SocketChannel socket, String uri) {
        websocketUri.put(socket, uri);
    }

    public String getWebsocketURI(final SocketChannel socket) {
        return websocketUri.get(socket);
    }

    public void run() {
        while (true) {
            try {
                // Process any pending changes
                synchronized (this.pendingChanges) {
                    Iterator<ChangeRequest> changes = pendingChanges.iterator();
                    while (changes.hasNext()) {
                        ChangeRequest change = (ChangeRequest) changes.next();
                        switch (change.type) {
                            case ChangeRequest.CHANGEOPS:
                                SelectionKey key = change.socket.keyFor(selector);
                                key.interestOps(change.ops);
                        }
                    }
                    pendingChanges.clear();
                }

                // Wait for an event one of the registered channels
                selector.select();

                // Iterate over the set of keys for which events are available
                Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    SelectionKey key = selectedKeys.next();
                    selectedKeys.remove();

                    if (!key.isValid()) {
                        continue;
                    }

                    // Check what event is available and deal with it
                    if (key.isAcceptable()) {
                        this.accept(key);
                    } else if (key.isReadable()) {
                        this.read(key);
                    } else if (key.isWritable()) {
                        this.write(key);
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void accept(SelectionKey key) throws IOException {
        // For an accept to be pending the channel must be a server socket channel.
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

        // Accept the connection and make it non-blocking
        SocketChannel socketChannel = serverSocketChannel.accept();

        // Socket socket = socketChannel.socket();
        socketChannel.configureBlocking(false);

        // Register the new SocketChannel with our Selector, indicating
        // we'd like to be notified when there's data waiting to be read
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        // Clear out our read buffer so it's ready for new data
        readBuffer.clear();

        // Attempt to read off the channel
        int numRead;
        try {
            numRead = socketChannel.read(readBuffer);
        } catch (IOException e) {
            // The remote forcibly closed the connection, cancel
            // the selection key and close the channel.
            context.channelManager().remove(SocketHook.produce(context, socketChannel));
            key.cancel();
            socketChannel.close();
            return;
        }
        if (numRead == -1) {
            // Remote entity shut the socket down cleanly. Do the
            // same from our end and cancel the channel.
            context.channelManager().remove(SocketHook.produce(context, socketChannel));
            key.channel().close();
            key.cancel();
            return;
        }

        //
        readBuffer.flip();
        byte[] data = new byte[readBuffer.limit()];
        readBuffer.get(data);

        // Hand the data off to our worker thread
        this.worker.processData(this, socketChannel, data, numRead);
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        synchronized (this.pendingData) {
            List<ByteBuffer> queue = pendingData.get(socketChannel);

            // Write until there's not more data ...
            while (!queue.isEmpty()) {
                ByteBuffer buf = queue.get(0);
                socketChannel.write(buf);
                if (buf.remaining() > 0) {
                    // ... or the socket's buffer fills up
                    break;
                }
                queue.remove(0);
            }

            if (queue.isEmpty()) {
                // We wrote away all data, so we're no longer interested
                // in writing on this socket. Switch back to waiting for
                // data.
                key.interestOps(SelectionKey.OP_READ);
            }
        }
    }

    private Selector initSelector() throws IOException {
        // Create a new selector
        Selector socketSelector = SelectorProvider.provider().openSelector();

        // Create a new non-blocking server socket channel
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);

        // Bind the server socket to the specified address and port
        InetSocketAddress isa = new InetSocketAddress(hostAddress, port);
        serverChannel.socket().bind(isa);

        // Register the server socket channel, indicating an interest in
        // accepting new connections
        serverChannel.register(socketSelector, SelectionKey.OP_ACCEPT);

        return socketSelector;
    }

    public void sendWebSocket(final SocketChannel socket, final String data) {
        try {
            final byte[] frame = FrameReader.createTextFrame(data.getBytes("UTF-8"));
            log.info("Sending " + new String(Hex.encodeHex(frame)));
            send(socket, frame);
        } catch (UnsupportedEncodingException e) {
            log.error("Unsupported encoding exception", e);
        }
    }
}
