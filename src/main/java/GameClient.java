import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class GameClient {
    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the numbers game!\n"
                + "Your goal is to guess the number from 1 to 100.\n"
                + "Write 'exit' to quit the game.\n"
                + "Guess the number:");
        String userInput = scanner.nextLine();
        startGame();
        while (gameStatus()) {
            switch (userInput) {
                case "LESS" -> {
                    System.out.println("Number to guess is smaller than your guess.");
                }
                case "EQUAL" -> {
                    System.out.println("You guessed the number.");
                }
                case "BIGGER" -> {
                    System.out.println("Number to guess is bigger than your guess.");
                }
            }
        }


    }

    public static void startGame() throws IOException, InterruptedException {
        URI HTTP_SERVER_URI = URI.create("http://localhost:5555/start-game");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.noBody())
                .uri(HTTP_SERVER_URI)
                .build();
        System.out.println("Sending: " + request);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Received: " + response);
        System.out.println(response.body());
    }

    public static boolean gameStatus() throws IOException, InterruptedException {
        URI HTTP_SERVER_URI = URI.create("http://localhost:5555/status");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(HTTP_SERVER_URI)
                .build();
        System.out.println("Sending: " + request);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Received: " + response);
        System.out.println(response.body());
        return Boolean.parseBoolean(response.body());
    }

    public static String guessGame(String userInput) throws IOException, InterruptedException {
        URI HTTP_SERVER_URI = URI.create("http://localhost:5555/guess");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.noBody())
                .uri(HTTP_SERVER_URI)
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
