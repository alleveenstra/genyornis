package nl.alleveenstra.genyornis.controllers;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;

import nl.alleveenstra.genyornis.Genyornis;
import nl.alleveenstra.genyornis.httpd.HttpContext;
import nl.alleveenstra.genyornis.httpd.HttpRequest;
import nl.alleveenstra.genyornis.httpd.HttpResponse;
import nl.alleveenstra.genyornis.routing.Action;
import nl.alleveenstra.genyornis.routing.Controller;

/**
 * This handler serves static files to the client.
 *
 * @author alle.veenstra@gmail.com
 */
@Controller(prefix = "/static/")
public class Static {

    private static final String INVALID_PATH_MESSAGE = "false";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String READ_ERROR = "Error reading file";
    private static final String SERVE_FROM = "/static";
    private static final String PREFIX = "/static/";


    @Action(regex = ".*")
    public void handle(HttpContext context, HttpRequest request, HttpResponse response) {
        response.setContent(dirlist(request.getUri().replaceFirst(PREFIX, "/"), response));
    }

    /**
     * Resolve the current working directory.
     *
     * @return the current working directory
     */
    private String getCWD() {
        return Genyornis.getApplicationFolder() + SERVE_FROM;
    }

    /**
     * Resolve the requested path. In case this path points to a directory, it will be listed. In case the path points to
     * a file, its content will be returned.
     *
     * @param path
     * @param response
     * @return the content or a listing
     */
    private byte[] dirlist(String path, HttpResponse response) {
        path.replaceAll("\\.\\.", "");
        java.io.File dir = new java.io.File(getCWD() + path);
        String[] chld = dir.list();
        if (dir.isFile()) {
            return serveFile(dir, response);
        }
        if (chld == null) {
            return INVALID_PATH_MESSAGE.getBytes();
        } else {
            // TODO let velocity handle the rendering
            String listing = "<ul>";
            for (int i = 0; i < chld.length; i++) {
                listing += "<li><a href=\"/static" + path + "/" + chld[i] + "\">" + chld[i] + "</a></li>";
            }
            listing += "</ul>";
            return listing.getBytes();
        }
    }

    /**
     * This method serves a file.
     *
     * @param file
     * @param response
     * @return a byte array containing the file's content.
     */
    private byte[] serveFile(java.io.File file, HttpResponse response) {
        try {
            FileInputStream fis = new FileInputStream(file);
            DataInputStream dis = new DataInputStream(fis);
            byte[] data = new byte[(int) file.length()];
            dis.readFully(data);
            FileNameMap fileNameMap = URLConnection.getFileNameMap();
            String type = fileNameMap.getContentTypeFor(file.getAbsolutePath());
            response.getHeaders().put(CONTENT_TYPE, type);
            return data;
        } catch (FileNotFoundException e) {
            // TODO implement some decent logging, instead of this printStackTrace
            e.printStackTrace();
        } catch (IOException e) {
            // TODO implement some decent logging, instead of this printStackTrace
            e.printStackTrace();
        }
        return READ_ERROR.getBytes();
    }
}
