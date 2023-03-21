package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;


public class HttpServerTutorial {

    private static final int randomNumber = (int) (Math.random() * 100) + 1;
    private static boolean hasGameStarted = false;
    private enum Result {LESS, EQUAL, BIGGER}

    public static void main(String[] args) throws IOException {
        InetSocketAddress addr = new InetSocketAddress("localhost", 5555);
        HttpServer httpServer = HttpServer.create(addr, 0);
        httpServer.createContext("/", new RequestHandler());
        httpServer.start();
        System.out.println("Server started, listening at: " + addr);
    }

    private static class RequestHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            switch (exchange.getRequestURI().getPath()) {
                case "/start-game" -> {
                    switch (exchange.getRequestMethod()) {
                        case "POST" -> handleStartRequest(exchange);
                        default -> handleNotFound(exchange);
                    }
                }

                case "/guess" -> {
                    switch (exchange.getRequestMethod()) {
                        case "POST" -> handleGuessRequest(exchange);
                        default -> handleNotFound(exchange);
                    }
                }

                case "/end-game" -> {
                    switch (exchange.getRequestMethod()) {
                        case "POST" -> handleEndRequest(exchange);
                        default -> handleNotFound(exchange);
                    }
                }

                default -> handleNotFound(exchange);
            }
            System.out.println(exchange.getRequestMethod() + " " + exchange.getRequestURI() + " " + exchange.getResponseCode());
        }
    }

    private static void handleStartRequest(HttpExchange exchange) throws IOException {
        String response;
        int status;

        if (!hasGameStarted) {
            hasGameStarted = true;
            response = "New game started.";
            status = 200;

        } else {
            response = "Game already running.";
            status = 400;
        }

        exchange.sendResponseHeaders(status, response.length());

        try (OutputStream responseBody = exchange.getResponseBody()) {
            responseBody.write(response.getBytes());
            exchange.close();
        }
    }

    private static void handleGuessRequest(HttpExchange exchange) throws IOException {
        String userGuess = exchange.getRequestBody().toString();
        System.out.println(userGuess);

        Response result = guessNumber(userGuess, randomNumber);
        String response = result.message();
        exchange.sendResponseHeaders(result.status(), response.length());

        try (OutputStream responseBody = exchange.getResponseBody()) {
            responseBody.write(response.getBytes());
            exchange.close();
        }
    }

    private static void handleEndRequest(HttpExchange exchange) throws IOException {
        String response;
        int status;

        if (hasGameStarted) {
            hasGameStarted = false;
            response = "Game ended.";
            status = 200;
        } else {
            response = "Game already ended.";
            status = 400;
        }

        exchange.sendResponseHeaders(status, response.length());

        try (OutputStream responseBody = exchange.getResponseBody()) {
            responseBody.write(response.getBytes());
            exchange.close();
        }
    }

    private static void handleNotFound(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(404, 0);
        exchange.close();
    }

    public static Response guessNumber(String userGuess, int gameNumber) {
        try {
            int guessNumber = Integer.parseInt(userGuess);
            if (guessNumber < 1 || guessNumber > 100) {
                return new Response(400, "The number is out of range, please guess a number between 0 and 100!");
            } else if (gameNumber > guessNumber) {
                return new Response(200, Result.BIGGER.toString());
            } else if (gameNumber < guessNumber) {
                return new Response(200, Result.LESS.toString());
            } else {
                return new Response(200, Result.EQUAL.toString());
            }

        } catch (NumberFormatException e) {
            if (userGuess.equals("")) {
                return new Response(200, "Welcome to the numbers game!\n" + "I have generated a random number from 1 to 100.\n" + "Guess the number.");
            } else if (userGuess.equals("exit")) {
                return new Response(200, "Pity to see you going, see you soon!");
            } else {
                return new Response(400, "Bad input, try Again.");
            }
        }
    }

    public static Map<String, String> mapQuery(String query) {
        if (query == null) {
            return null;
        }
        String[] parameters = query.split("&");
        Map<String, String> result = new HashMap<>();
        for (String parameter : parameters) {
            result.put(parameter.split("=")[0], parameter.split("=")[1]);
        }
        return result;
    }
}