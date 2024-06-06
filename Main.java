import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        // Create a Scanner object for reading user input
        Scanner scanner = new Scanner(System.in);
        // Display welcome message
        System.out.println("Welcome to Minesweeper!");

        int choice;
        int rows = 0, columns = 0, flags = 0;
        boolean validInput = false;

        while (!validInput) {
            System.out.println("Choose difficulty level:");
            System.out.println("1. Easy\n2. Medium\n3. Hard");

            choice = scanner.nextInt();

            if (choice == 1) {
                rows = 5;
                columns = 5;
                flags = 5;
                validInput = true;
            } else if (choice == 2) {
                rows = 9;
                columns = 9;
                flags = 12;
                validInput = true;
            } else if (choice == 3) {
                rows = 20;
                columns = 20;
                flags = 40;
                validInput = true;
            } else {
                System.out.println("Invalid choice. Please enter a valid choice (1, 2, or 3).");
            }
        }

        // Create a new minefield with the specified parameters
        Minefield minefield = new Minefield(rows, columns, flags);

        System.out.println("Enter starting coordinates (x y):");
        int startX = scanner.nextInt();
        int startY = scanner.nextInt();
        // Perform the initial guess to start the game
        minefield.guess(startX, startY, false);

        // Add a prompt for selecting debug mode
        System.out.println("Do you want to enable debug mode? (Y/N)");
        String debugInput = scanner.next();

        while (!debugInput.equalsIgnoreCase("Y") && !debugInput.equalsIgnoreCase("N")) {
            System.out.println("Invalid input. Please enter either 'Y' or 'N'.");
            debugInput = scanner.next();
        }

        if (debugInput.equalsIgnoreCase("Y")) {
            minefield.debugMode = true;
        } else {
            minefield.debugMode = false;
        }

        boolean gameLost = false;
        // Continue the game loop until the game is over
        while (!minefield.gameOver()) {
            if (minefield.flags > 0) {
            } else {
                System.out.println("No more flags left.");
            }
            if (minefield.debugMode) {
                minefield.printMinefield();
            } else {
                System.out.print(minefield.toString());
            }

            // Prompt the user for an action (Reveal or Flag) and coordinates
            int action = 0;
            while (action != 1 && action != 2) {
                System.out.println("Enter action ('1' to Reveal or '2' to Flag) (Remaining flags: " + minefield.flags
                        + ") and enter coordinates (x y) (example: 1 2 3): ");
                action = scanner.nextInt();
                if (action != 1 && action != 2) {
                    System.out.println("Invalid action. Please enter either '1' to Reveal or '2' to Flag.");
                }
            }

            int x = scanner.nextInt();
            int y = scanner.nextInt();

            if (x < 0 || x >= rows || y < 0 || y >= columns) {
                System.out.println("Invalid coordinates. Please enter valid coordinates.");
            } else {
                if (action == 1) {
                    gameLost = minefield.guess(x, y, false);
                } else {
                    if (minefield.flags > 0) {
                        minefield.guess(x, y, true);
                    }
                }
            }

            // Check if the game is over, and print the appropriate message
            if (minefield.gameOver()) {
                if (!gameLost) {
                    minefield.printMinefield();
                    System.out.println("Congratulations! You won!");

                } else {
                    minefield.printMinefield();
                    System.out.println("Game Over! You hit a mine.");

                }
                break;
            }
        }
        scanner.close();
    }
}
