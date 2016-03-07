package com.company;

/* This project implements an concurrent HTTP server
   The current version implements the second stage: a concurrent server
   The current server responds to the client with system time information */

import java.net.*;
import java.io.*;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ConcurrentHttpServer {

    public static int PORT = 2555;

    // This function respond to clients with system time information
    private static class respondClientRequest implements Runnable {

        private Socket connection;
        public respondClientRequest(Socket connection) {
            this.connection = connection;
        }

        @Override
        public void run(){
            try (OutputStream os = connection.getOutputStream();
                OutputStreamWriter out = new OutputStreamWriter(os)){
                Date today = new Date();
                out.write(today.toString() + "\n");
                out.flush();
                connection.close();
            }catch(IOException ex) {
                System.err.println(ex);
            }
        }
    }

    //The main function uses ExecutorService class to support concurrency
    public static void main(String[] args) {

        ExecutorService pool = Executors.newFixedThreadPool(10);

        try (ServerSocket server = new ServerSocket(PORT)) {
            while (true) {
                // don't do this, because try-with block will close the connection after it goes
                // out of scope and by the time you have that connection in the executor service
                // thread it will already be closed
                //
                // you should always close the connection after the client is handled
                try (Socket connection = server.accept()) {
                    Runnable task = new respondClientRequest(connection);
                    pool.submit(task);

                } catch (IOException ex) {
                    System.err.println(ex);
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
