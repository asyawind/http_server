import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;


public class GameServer {

    GameServer gameServer = new GameServer();

    private static int randomNumber = 0;
    private static boolean hasGameStarted = false;

    private enum Result {LESS, EQUAL, BIGGER}

    public static void main(String[] args) throws IOException {
        InetSocketAddress addr = new InetSocketAddress("0.0.0.0", 5555);
        HttpServer httpServer = HttpServer.create(addr, 0);
        httpServer.createContext("/", new RequestHandler());
        httpServer.start();
        System.out.println("Server started, listening at: " + addr);
    }

    private static class RequestHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestAndPath = exchange.getRequestMethod() + " " + exchange.getRequestURI().getPath();

            try {
                switch (requestAndPath) {
                    case ("GET /status") -> handleStatusRequest(exchange);
                    case ("POST /start-game") -> handleStartRequest(exchange);
                    case ("POST /guess") -> handleGuessRequest(exchange);
                    case ("POST /end-game") -> handleEndRequest(exchange);
                    default -> handleNotFound(exchange);
                }
            } catch (Exception exception) {
                System.out.println(Arrays.toString(exception.getStackTrace()));
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            System.out.println(formatter.format(LocalDateTime.now()) + " "
                    + exchange.getRequestMethod() + " "
                    + exchange.getRequestURI() + " "
                    + exchange.getResponseCode());
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
        System.out.println("User input: " + userGuess);

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
                return new Response(400, "The number is out of range, please guess a number between 1 and 100!");
            } else if (gameNumber < guessNumber) {
                return new Response(200, Result.BIGGER.toString());
            } else if (gameNumber > guessNumber) {
                return new Response(200, Result.LESS.toString());
            } else {
                hasGameStarted = false;
                return new Response(200, Result.EQUAL.toString());
            }
        } catch (NumberFormatException e) {
            return new Response(400, "Bad input, try again.");
        }
    }
}