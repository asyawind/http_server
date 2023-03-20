package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class HttpServerTutorial  {
    public static void main(String[] args) throws IOException {
        InetSocketAddress addr = new InetSocketAddress("localhost", 5555);
        HttpServer httpServer = HttpServer.create(addr, 0);
        httpServer.createContext("/numbers-game", new RequestHandler());
        httpServer.createContext("/home", new RequestHandler());
        httpServer.start();
        System.out.println("Server started, listening at: " + addr);


    }

    private static class RequestHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> parameters = mapQuery(query);

                System.out.println(parameters);
                System.out.println(exchange.getRequestURI());

                String response = "Welcome to the numbers game!\n" +
                        "I have generated a random number from 1 to 100.\n" +
                        "Guess the number.";
                exchange.sendResponseHeaders(200, response.length());

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

    public String guessNumber() {
        int randomNumber = (int) (Math.random() * 100) + 1;
        System.out.println("Guess a random number from 1 to 100 or type exit to stop");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (scanner.hasNextInt()) {
                int userGuess = scanner.nextInt();
                if (userGuess < 1 || userGuess > 100) {
                    System.out.println("The number is out of range, please guess a number between 0 and 100!");
                } else if (randomNumber > userGuess) {
                    System.out.println("The correct number is bigger. Guess again!");
                } else if (randomNumber < userGuess) {
                    System.out.println("The correct number is smaller. Guess again!");
                } else {
                    System.out.println("You guessed correctly, you are the master!");
                    return null;
                }
            } else if (scanner.next().equals("exit")) {
                System.out.println("Pity to see you going, see you soon!");
                return null;
            } else {
                System.out.println("Bad input, try Again.");
            }
        }
    }
}