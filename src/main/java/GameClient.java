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
            if (userInput.equals("exit")) {
                endGame();
                System.out.println("Thank you for playing, see you soon!");
            } else {
                switch (guessGame(userInput)) {
                    case "LESS" -> {
                        System.out.println("Number to guess is smaller than your guess.");
                    }
                    case "EQUAL" -> {
                        System.out.println("Congratulations you guessed the number.");
                        System.out.println("The game has ended.");
                    }
                    case "BIGGER" -> {
                        System.out.println("Number to guess is bigger than your guess.");
                    }
                    case "OUT_OF_RANGE" -> {
                        System.out.println("The number is out of range, please guess a number between 1 and 100!");
                    }
                    case "NOT_NUMBER" -> {
                        System.out.println("Bad input, try again.");
                    }
                }
            }
            userInput = scanner.nextLine();
        }
    }

    public static boolean gameStatus() throws IOException, InterruptedException {
        URI HTTP_SERVER_URI = URI.create("http://localhost:5555/status");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(HTTP_SERVER_URI)
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return Boolean.parseBoolean(response.body());
    }

    public static void startGame() throws IOException, InterruptedException {
        URI HTTP_SERVER_URI = URI.create("http://localhost:5555/start-game");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.noBody())
                .uri(HTTP_SERVER_URI)
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static String guessGame(String userInput) throws IOException, InterruptedException {
        URI HTTP_SERVER_URI = URI.create("http://localhost:5555/guess");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(userInput))
                .uri(HTTP_SERVER_URI)
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public static void endGame() throws IOException, InterruptedException {
        URI HTTP_SERVER_URI = URI.create("http://localhost:5555/end-game");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.noBody())
                .uri(HTTP_SERVER_URI)
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
