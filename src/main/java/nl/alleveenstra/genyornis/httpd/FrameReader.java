package nl.alleveenstra.genyornis.httpd;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public class FrameReader {

    public static byte[] createTextFrame(byte[] payload) {
        ByteBuffer b = ByteBuffer.allocate(payload.length + 2);
        b.put((byte) 0x81);
        b.put((byte) payload.length);
        b.put(payload);
        b.flip();
        byte[] frame = new byte[b.limit()];
        b.get(frame);
        return frame;
    }

    /*
    The WebSocket frame

    From http://tools.ietf.org/html/rfc6455

      0                   1                   2                   3
      0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     +-+-+-+-+-------+-+-------------+-------------------------------+
     |F|R|R|R| opcode|M| Payload len |    Extended payload length    |
     |I|S|S|S|  (4)  |A|     (7)     |             (16/64)           |
     |N|V|V|V|       |S|             |   (if payload len==126/127)   |
     | |1|2|3|       |K|             |                               |
     +-+-+-+-+-------+-+-------------+ - - - - - - - - - - - - - - - +
     |     Extended payload length continued, if payload len == 127  |
     + - - - - - - - - - - - - - - - +-------------------------------+
     |                               |Masking-key, if MASK set to 1  |
     +-------------------------------+-------------------------------+
     | Masking-key (continued)       |          Payload Data         |
     +-------------------------------- - - - - - - - - - - - - - - - +
     :                     Payload Data continued ...                :
     + - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - +
     |                     Payload Data continued ...                |
     +---------------------------------------------------------------+

     */

    public static byte[] translateSingleFrame(byte[] data) throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(data);

        int maxpacketsize = buffer.limit() - buffer.position();
        int realpacketsize = 2;
        if (maxpacketsize < realpacketsize) {
            throw new Exception("incomplete packet received");
        }

        byte data_0 = buffer.get(); // read data[0]
        boolean finBit = (data_0 >> 8 != 0); // fetch first bit from first byte

        // for now, we ignore rsv

        final Opcode optcode = Opcode.fromByte((byte) (data_0 & 15)); // fetch the opcode

        byte data_1 = buffer.get(); // read data[1]
        boolean maskBit = ((data_1 & -128) != 0);

        int payloadLen = (byte) (data_1 & ~(byte) 128);

        if (!finBit) {
            if (optcode == Opcode.PING || optcode == Opcode.PONG || optcode == Opcode.CLOSING) {
                throw new Exception("control frames may no be fragmented");
            }
        }

        if (payloadLen >= 0 && payloadLen <= 125) {
            // nothing?
        } else {
            if (optcode == Opcode.PING || optcode == Opcode.PONG || optcode == Opcode.CLOSING) {
                throw new Exception("more than 125 octets");
            }
            if (payloadLen == 126) {
                realpacketsize += 2; // additional length bytes
                if (maxpacketsize < realpacketsize) {
                    throw new Exception("maxpacketsize smaller than realpacketsize");
                }
                byte[] sizebytes = new byte[3];
                sizebytes[1] = buffer.get(); // fetch first size byte
                sizebytes[2] = buffer.get(); // fetch second size byte
                payloadLen = new BigInteger(sizebytes).intValue();
            } else {
                realpacketsize += 8; // additional length bytes
                if (maxpacketsize < realpacketsize) {
                    throw new Exception("Incomplete " + realpacketsize);
                }
                byte[] bytes = new byte[8];
                for (int i = 0; i < 8; i++) {
                    bytes[i] = buffer.get();
                }
                long length = new BigInteger(bytes).longValue();
                if (length > Integer.MAX_VALUE) {
                    throw new Exception("Payloadsize is too large...");
                } else {
                    payloadLen = (int) length;
                }
            }
        }

        // int maskskeystart = foff + realpacketsize;
        realpacketsize += (maskBit ? 4 : 0);
        // int payloadstart = foff + realpacketsize;
        realpacketsize += payloadLen;

        if (maxpacketsize < realpacketsize) {
            throw new Exception("Incomplete " + realpacketsize);
        }

        if (payloadLen < 0) {
            throw new Exception("Payload length negative");
        }

        ByteBuffer payload = ByteBuffer.allocate(payloadLen);
        if (maskBit) {
            byte[] maskskey = new byte[4];
            buffer.get(maskskey);
            for (int i = 0; i < payloadLen; i++) {
                payload.put((byte) (buffer.get() ^ maskskey[i % 4]));
            }
        } else {
            payload.put(buffer.array(), buffer.position(), payload.limit());
            buffer.position(buffer.position() + payload.limit());
        }

        if (optcode == Opcode.CLOSING) {
            // close ?
        }

        return payload.array();
    }

    public enum Opcode {
        CONTINIOUS(0), TEXT(1), BINARY(2), PING(9), PONG(10), CLOSING(8);

        private int code;

        Opcode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static Opcode fromByte(byte opcode) throws Exception {
            switch (opcode) {
                case 0:
                    return Opcode.CONTINIOUS;
                case 1:
                    return Opcode.TEXT;
                case 2:
                    return Opcode.BINARY;
                case 8:
                    return Opcode.CLOSING;
                case 9:
                    return Opcode.PING;
                case 10:
                    return Opcode.PONG;
                default:
                    throw new IllegalArgumentException("No such opcode " + (int) opcode);
            }
        }
    }
}
