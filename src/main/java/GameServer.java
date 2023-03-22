import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;


public class GameServer {

    private static int randomNumber = 0;
    private static boolean hasGameStarted = false;

    private enum Result {LESS, EQUAL, BIGGER, OUT_OF_RANGE, NOT_NUMBER}

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
            String requestAndPath = exchange.getRequestMethod() + " " + exchange.getRequestURI().getPath();

            switch (requestAndPath) {
                case ("GET /status") -> handleStatusRequest(exchange);
                case ("POST /start-game") -> handleStartRequest(exchange);
                case ("POST /guess") -> handleGuessRequest(exchange);
                case ("POST /end-game") -> handleEndRequest(exchange);
                default -> handleNotFound(exchange);
            }
            System.out.println(exchange.getRequestMethod() + " " + exchange.getRequestURI() + " " + exchange.getResponseCode());
        }
    }

    private static void handleStatusRequest(HttpExchange exchange) throws IOException {
        String response;
        int status = 200;

        if (hasGameStarted) {
            response = "true";

        } else {
            response = "false";
        }

        exchange.sendResponseHeaders(status, response.length());

        try (OutputStream responseBody = exchange.getResponseBody()) {
            responseBody.write(response.getBytes());
            exchange.close();
        }
    }

    private static void handleStartRequest(HttpExchange exchange) throws IOException {
        String response;
        int status;

        if (!hasGameStarted) {
            hasGameStarted = true;
            randomNumber = (int) (Math.random() * 100) + 1;
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
        String userGuess = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
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
            randomNumber = 0;
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
                return new Response(400, Result.OUT_OF_RANGE.toString());
            } else if (gameNumber > guessNumber) {
                return new Response(200, Result.BIGGER.toString());
            } else if (gameNumber < guessNumber) {
                return new Response(200, Result.LESS.toString());
            } else {
                hasGameStarted = false;
                return new Response(200, Result.EQUAL.toString());
            }
        } catch (NumberFormatException e) {
            return new Response(400, Result.NOT_NUMBER.toString());
        }
    }
}