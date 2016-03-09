package com.company;

/* This project implements an concurrent HTTP server
   The current version adds some features on the second stage (a concurrent server) :
    1) get contentType,content and encoding from command line input
    2) make HTTP response Header
   The current server responds to the client with system time information
   * Bugs do remain. The function responseHeader needs adjustments.
*/

import java.net.*;
import java.io.*;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ConcurrentHttpServer {

    private static int PORT = 2555;
    private static byte[] header;

    //response首部制作函数
    private void responseHeader(String encoding, String contentType, byte[] content){
        String header = "HTTP/1.0 200 OK\r\n" + "Server: ConcurrentHTTPServer\r\n"
                +"Content-length:" + content.length + "\r\n" + "Content-type" + contentType
                + "; charset=" + encoding + "\r\n\r\n";
        this.header = header.getBytes(encoding);
    }


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

                //需要静态化函数,考虑将responseHeader以constructor形式构造
                responseHeader(encoding, contentType, content);
                Date today = new Date();
                out.write(header);
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
                try (Socket connection = server.accept()) {
                    //通过command line的输入解析contentType,content,encoding
                    String contentType = URLConnection.getFileNameMap().getContentTypeFor(args[0]);
                    //byte[] content = ..   通过path读取数据
                    String encoding = "UTF-8";
                    If(args.length>2)
                        encoding = args.[2];

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
