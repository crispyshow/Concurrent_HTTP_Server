package com.company;

/* This project implements an concurrent HTTP server
   The current version implements the first stage: a server socket
   The current server responds to the client with system time information */

import java.net.*;
import java.io.*;
import java.util.Date;

public class ConcurrentHttpServer {
    public static int PORT = 2555;

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)) {
            while (true) {
                try (Socket connection = server.accept()) {
                    respondClientRequest(connection);
                } catch (IOException ex) {
                }
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    // This function respond to clients with system time information
    private static void respondClientRequest(Socket connection) throws IOException {
        OutputStream os = connection.getOutputStream();
        OutputStreamWriter out = new OutputStreamWriter(os);
        Date today = new Date();
        out.write(today.toString() + "\n");
        out.flush();
        connection.close();
    }
}
