package org.example;

import com.sun.net.httpserver.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.time.Instant;

public class HttpServer {
    public static void main(String[] args) throws IOException {
        InetSocketAddress addr = new InetSocketAddress("localhost", 5555);
        com.sun.net.httpserver.HttpServer httpServer = com.sun.net.httpserver.HttpServer.create(addr, 0);
        httpServer.createContext("/status", new RequestHandler());
        httpServer.start();
        System.out.println("Server started, listening at: " + addr);
    }

    private static class RequestHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                System.out.println(exchange.getRequestURI());
                String response = Instant.now().toString();

                exchange.sendResponseHeaders(200, response.length());

                try (OutputStream responseBody = exchange.getResponseBody()) {
                    responseBody.write(response.getBytes());
                }
            }
        }
    }
}