import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class GameClient {
//        private static String baseURI = "http://10.10.10.25:5555";    // Mairold&Janeli
//        private static String baseURI = "http://10.10.10.156:5555"; //Kristjan&Soren
    private static String baseURI = "http://10.10.10.80:5555";

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the numbers game!\n"
                + "Your goal is to guess the number from 1 to 100.\n"
                + "Write 'exit' to quit the game.\n"
                + "Write 'stats' to see your game statistics.\n"
                + "Guess the number:");

        String userInput = scanner.nextLine();
        startGame();

        loop:
        while (true) {
            if (userInput.equalsIgnoreCase("EXIT")) {
                endGame();
                System.out.println("Thank you for playing, see you soon!");
                break loop;
            } else if (userInput.equalsIgnoreCase("STATS")) {
                System.out.println(gameStats());
            } else {
                String response = guessGame(userInput);
                switch (response) {
                    case "LESS" -> {
                        System.out.println("Number to guess is bigger than your guess.");
                    }
                    case "EQUAL" -> {
                        System.out.println("Congratulations you guessed the number.");
                        System.out.println("The game has ended.");
                        System.out.println("Would you like to play again? Y/N?");
                        userInput = scanner.nextLine();
                        if (userInput.equalsIgnoreCase("Y") || userInput.equalsIgnoreCase("YES")) {
                            startGame();
                            System.out.println("A new game has started.");
                            System.out.println("Guess the number:");
                        } else {
                            System.out.println("Thank you for playing, see you soon!");
                            break loop;
                        }
                    }
                    case "BIGGER" -> {
                        System.out.println("Number to guess is smaller than your guess.");
                    }
                    default -> {
                        System.out.println(response);
                    }
                }
            }
            userInput = scanner.nextLine();
        }
    }

    public static boolean gameStatus() throws IOException, InterruptedException {
        URI HTTP_SERVER_URI = URI.create(baseURI + "/status");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(HTTP_SERVER_URI)
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return Boolean.parseBoolean(response.body());
    }

    public static String gameStats() throws IOException, InterruptedException {
        URI HTTP_SERVER_URI = URI.create(baseURI + "/stats");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(HTTP_SERVER_URI)
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public static void startGame() throws IOException, InterruptedException {
        URI HTTP_SERVER_URI = URI.create(baseURI + "/start-game");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.noBody())
                .uri(HTTP_SERVER_URI)
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static String guessGame(String userInput) throws IOException, InterruptedException {
        URI HTTP_SERVER_URI = URI.create(baseURI + "/guess");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(userInput))
                .uri(HTTP_SERVER_URI)
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public static void endGame() throws IOException, InterruptedException {
        URI HTTP_SERVER_URI = URI.create(baseURI + "/end-game");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.noBody())
                .uri(HTTP_SERVER_URI)
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}