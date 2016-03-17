package com.company;

/* This project implements an concurrent HTTP server
   The following changes were made to the last version:
    1) changes the improper use of try-with-resources on client socket, as pointed out by Jiayu
    2) gets file path from browser request instead of command line input
    3) supports the MIME type of .txt
    Bug remained: the header doesn't print properly
*/

import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ConcurrentHttpServer {

    private static int PORT = 2555;


    // This function respond to clients with system time information
    private static class respondClientRequest implements Runnable {

        private Socket connection;
        public respondClientRequest(Socket connection) {
            this.connection = connection;
        }

        String statusLine;
        String elements[];
        String method;
        String resourcePath;
        String contentType;
        String httpVersion;
        byte[] content;


        @Override
        public void run(){
            try {
                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                OutputStream os = connection.getOutputStream();

                statusLine = br.readLine();
                elements = statusLine.split("\\s+");
                httpVersion = elements [2];
                if (httpVersion.equals("HTTP/1.0")){
                    method = elements[0];
                    if (method.equals("GET")) {
                        resourcePath = elements[1];
                        contentType = URLConnection.getFileNameMap().getContentTypeFor(resourcePath);
                        File file = new File(resourcePath);
                            if (file.canRead()){
                                content = Files.readAllBytes(file.toPath());
                                //create status line and header
                                Date today = new Date();
                                // BUG: header cannot print properly on browser
                                String header = "HTTP/1.0 200 OK\r\n";
                                header = header.concat("Server: ConcurrentHTTPServer\r\n")
                                        .concat("Date:" + today + "\r\n")
                                        .concat("Content-length:" + content.length + "\r\n")
                                        .concat("Content-type" + contentType)
                                        .concat("\r\n\r\n");
                                os.write(header.getBytes());
                                //output resource
                                os.write(content);
                                os.flush();
                            }
                            else{
                                // print status line for file not existed
                            }
                }else {
                        // print status line for methods other than "GET"
                    }
                }else{
                    // print status line for protocol other than "HTTP/1.0"
                }
            }catch(IOException ex) {
                System.err.println(ex);
            }finally{
                try{connection.close();
                }catch(IOException e){
                    System.err.println(e);
                }
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
                try {
                    Socket connection = server.accept();
                    Runnable task = new respondClientRequest(connection);
                    pool.submit(task);

                }catch (IOException ex) {
                    System.err.println(ex);
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
