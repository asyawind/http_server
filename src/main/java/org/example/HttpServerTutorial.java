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
import java.util.Scanner;


public class HttpServerTutorial {

    private static final int randomNumber = (int) (Math.random() * 100) + 1;

    public static void main(String[] args) throws IOException {
        InetSocketAddress addr = new InetSocketAddress("localhost", 5555);
        HttpServer httpServer = HttpServer.create(addr, 0);
        httpServer.createContext("/numbers-game", new RequestHandler());
        httpServer.start();
        System.out.println("Server started, listening at: " + addr);


    }

    private static class RequestHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> parameters = mapQuery(query);

                String userGuess = "";
                if (parameters != null) {
                    userGuess = parameters.get("userGuess");
                }

                Response result = guessNumber(userGuess, randomNumber);
                System.out.println("Time: " + LocalTime.now()
                        + " Request Method: GET"
                        + " Request URL: " + exchange.getRequestURI().toString()
                        + " Status Code: " + result.status());

                String response = result.message();
                exchange.sendResponseHeaders(result.status(), response.length());

                try (OutputStream responseBody = exchange.getResponseBody()) {
                    responseBody.write(response.getBytes());
                }
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

    public static Response guessNumber(String userGuess, int gameNumber) {
        try {
            int guessNumber = Integer.parseInt(userGuess);
            if (guessNumber < 1 || guessNumber > 100) {
                return new Response(400, "The number is out of range, please guess a number between 0 and 100!");
            } else if (gameNumber > guessNumber) {
                return new Response(200, "The correct number is bigger. Guess again!");
            } else if (gameNumber < guessNumber) {
                return new Response(200, "The correct number is smaller. Guess again!");
            } else {
                return new Response(200, "You guessed correctly, you are the master!");
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
}