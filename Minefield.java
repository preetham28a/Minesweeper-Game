// Srinivas Preetham Addepalli, addep011; Suryansh Malik, malik180

import java.util.Random;

public class Minefield {

    // ANSI color codes for terminal output
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE_BRIGHT = "\u001b[34;1m";
    public static final String ANSI_BLUE = "\u001b[34m";
    public static final String ANSI_RED_BRIGHT = "\u001b[31;1m";
    public static final String ANSI_RED = "\u001b[31m";
    public static final String ANSI_GREEN = "\u001b[32m";
    public static final String ANSI_GREY_BG = "\u001b[0m";

    private int rows;
    private int columns;
    private int mines;
    int flags;
    private Cell[][] field;
    private boolean gameOver;
    private boolean firstMove;
    public boolean debugMode;

    // Constructor for creating a new Minefield object with the given number of
    // rows, columns, and flags.
    public Minefield(int rows, int columns, int flags) {
        this.rows = rows;
        this.columns = columns;
        this.mines = flags;
        this.flags = flags;
        this.field = new Cell[rows][columns];
        this.gameOver = false;
        this.firstMove = true;
        this.debugMode = false;

        // Initialize the field with empty cells
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                field[i][j] = new Cell(false, "");
            }
        }
    }

    // Evaluates the minefield by calculating the number of adjacent mines for each
    // cell.
    public void evaluateField() {
        // Define a 2D array for all possible 8 directions surrounding a cell
        int[][] directions = { { -1, -1 }, { -1, 0 }, { -1, 1 }, { 0, -1 }, { 0, 1 }, { 1, -1 }, { 1, 0 }, { 1, 1 } };
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                if (!field[row][col].getStatus().equals("M")) {
                    int mineCount = 0;
                    // Iterate through all 8 possible directions
                    for (int[] direction : directions) {
                        int newRow = row + direction[0];
                        int newCol = col + direction[1];
                        // Check if the new coordinates are within the field boundaries
                        if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < columns) {
                            if (field[newRow][newCol].getStatus().equals("M")) {
                                mineCount++;
                            }
                        }
                    }
                    // Set the status of the current cell to the number of mines found in the
                    // surrounding cells
                    field[row][col].setStatus(Integer.toString(mineCount));
                }
            }
        }
    }

    // Creates the specified number of mines at random locations in the minefield,
    // ensuring that the first clicked cell (x, y) does not contain a mine.
    public void createMines(int x, int y, int mines) {
        Random rand = new Random();
        for (int minesPlaced = 0; minesPlaced < mines;) {
            int row = rand.nextInt(rows);
            int col = rand.nextInt(columns);
            if (!field[row][col].getStatus().equals("M") && !field[row][col].getRevealed() && (row != x || col != y)) {
                field[row][col].setStatus("M");
                minesPlaced++;
            }
        }
    }

    // Attempts to reveal or flag the cell at the given coordinates (x, y).
    // Returns true if the game is over, false otherwise.
    public boolean guess(int x, int y, boolean flag) {
        // Check if the given coordinates are within the bounds of the field
        if (x >= 0 && x < rows && y >= 0 && y < columns) {
            // If the flag parameter is true, toggle the flag status of the cell
            if (flag) {
                if (!field[x][y].getRevealed()) {
                    // If the cell is already flagged, remove the flag and increment flags count
                    if (field[x][y].getStatus().equals("F")) {
                        field[x][y].setStatus("");
                        flags++;
                    } else { // If the cell is not flagged, set the flag and decrement flags count
                        field[x][y].setStatus("F");
                        flags--;
                    }
                }
            } else {
                // If it's the first move, create mines, evaluate the field, reveal the starting
                // cell and set firstMove to false
                if (firstMove) {
                    createMines(x, y, mines);
                    evaluateField();
                    revealStart(x, y);
                    firstMove = false;
                } else {
                    // If the cell contains a "0", reveal all connected zero cells
                    if (field[x][y].getStatus().equals("0")) {
                        revealZeroes(x, y);
                    } else if (field[x][y].getStatus().equals("M")) {
                        gameOver = true;
                        return true;
                    } else { // Otherwise, reveal the cell
                        field[x][y].setRevealed(true);
                    }
                }
            }
            // If the cell doesn't contain a mine or if it's flagged and contains a mine,
            // return false
            if (!field[x][y].getStatus().equals("M") || (flag && field[x][y].getStatus().equals("M"))) {
                return false;
            }
        }
        // If the given coordinates are out of bounds, return true
        return true;
    }

    // Determines if the game is over, either by revealing all non-mine cells or
    // clicking on a mine.
    // Returns true if the game is over, false otherwise.
    public boolean gameOver() {
        int nonMineRevealed = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                // If the current square is revealed and not a mine, increment the
                // nonMineRevealed counter
                if (field[i][j].getRevealed() && !field[i][j].getStatus().equals("M")) {
                    nonMineRevealed++;
                }
            }
        }

        // Calculate the total number of non-mine squares in the field
        int totalNonMineSquares = rows * columns - mines;

        // If all non-mine squares are revealed, set gameOver to true and return true
        if (nonMineRevealed == totalNonMineSquares) {
            gameOver = true;
            return true;
        }

        // If the game is not over and it's not the first move, return false
        if (!gameOver && !firstMove) {
            return false;
        }
        // In all other cases, return true
        return true;
    }

    // Reveals all connected cells with a value of 0, starting from the cell at the
    // given coordinates (x, y).
    public void revealZeroes(int x, int y) {
        // Define the possible directions to move in the grid (up, down, left, right).
        int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
        // Initialize a stack to store the cells to visit.
        Stack1Gen<int[]> stack = new Stack1Gen<>();
        stack.push(new int[] { x, y });

        // Continue until there are no more cells to visit.
        while (!stack.isEmpty()) {
            int[] current = stack.pop();
            int row = current[0];
            int col = current[1];

            if (!field[row][col].getRevealed()) {
                field[row][col].setRevealed(true);
                if (field[row][col].getStatus().equals("0")) {
                    for (int[] direction : directions) {
                        int newRow = row + direction[0];
                        int newCol = col + direction[1];
                        if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < columns) {
                            if (!field[newRow][newCol].getRevealed() && field[newRow][newCol].getStatus().equals("0")) {
                                stack.push(new int[] { newRow, newCol });
                            }
                        }
                    }
                }
            }
        }
    }

    // Reveals all mines in the minefield, starting from the cell at the given
    // coordinates (x, y).
    public void revealMines(int x, int y) {
        Q1Gen<int[]> queue = new Q1Gen<>();
        queue.add(new int[] { x, y });

        while (queue.length() != 0) {
            int[] current = queue.remove();
            int row = current[0];
            int col = current[1];

            if (!field[row][col].getRevealed()) {
                field[row][col].setRevealed(true);

                if (field[row][col].getStatus().equals("M")) {
                    break;
                }

                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int newRow = row + i;
                        int newCol = col + j;
                        if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < columns) {
                            if (!field[newRow][newCol].getRevealed()) {
                                queue.add(new int[] { newRow, newCol });
                            }
                        }
                    }
                }
            }
        }
    }

    // Reveals all cells connected to the starting cell (x, y).
    public void revealStart(int x, int y) {
        // Create a generic queue to store the cell coordinates to be revealed
        Q1Gen<int[]> queue = new Q1Gen<>();
        queue.add(new int[] { x, y });

        // Process cells in the queue until it is empty
        while (queue.length() != 0) {
            int[] current = queue.remove();
            int row = current[0];
            int col = current[1];

            if (!field[row][col].getRevealed()) {
                field[row][col].setRevealed(true);
                if (field[row][col].getStatus().equals("M")) {
                    break;
                }

                // Loop through the neighboring cells of the current cell
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int newRow = row + i;
                        int newCol = col + j;
                        if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < columns) {
                            if (!field[newRow][newCol].getRevealed()) {
                                queue.add(new int[] { newRow, newCol });
                            }
                        }
                    }
                }
            }
        }
    }

    // Prints the current state of the minefield to the console with appropriate
    // colors for each cell value.
    public void printMinefield() {
        System.out.print("  ");
        for (int j = 0; j < columns; j++) {
            System.out.print(j + " ");
        }
        System.out.println();

        for (int i = 0; i < rows; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < columns; j++) {
                if (debugMode || field[i][j].getRevealed()) {
                    String status = field[i][j].getStatus();
                    // Colorize the output based on the cell's status
                    switch (status) {
                        case "0":
                            System.out.print(ANSI_YELLOW + status + ANSI_GREY_BG + " ");
                            break;
                        case "1":
                            System.out.print(ANSI_BLUE + status + ANSI_GREY_BG + " ");
                            break;
                        case "2":
                            System.out.print(ANSI_GREEN + status + ANSI_GREY_BG + " ");
                            break;
                        case "3":
                            System.out.print(ANSI_RED + status + ANSI_GREY_BG + " ");
                            break;
                        case "4":
                            System.out.print(ANSI_BLUE_BRIGHT + status + ANSI_GREY_BG + " ");
                            break;
                        case "5":
                            System.out.print(ANSI_RED_BRIGHT + status + ANSI_GREY_BG + " ");
                            break;
                        case "6":
                            System.out.print(ANSI_YELLOW + status + ANSI_GREY_BG + " ");
                            break;
                        case "M":
                            System.out.print(ANSI_RED_BRIGHT + status + ANSI_GREY_BG + " ");
                            break;
                        default:
                            System.out.print(status + " ");
                            break;
                    }
                } else {
                    // If the cell is flagged, print a flag symbol; otherwise, print an unrevealed
                    // cell symbol
                    if (field[i][j].getStatus().equals("F")) {
                        System.out.print("- ");
                    } else {
                        System.out.print("- ");
                    }
                }
            }
            System.out.println();
        }
        // If debug mode is on, print the minefield's string representation
        if (debugMode) {
            System.out.println();
            System.out.println(toString());
        }
    }

    // Returns a string representation of the minefield with appropriate colors for
    // each cell value.
    public String toString() {
        StringBuilder result = new StringBuilder();
        // Print the column numbers with a two-space padding on the left
        result.append("  ");
        for (int j = 0; j < columns; j++) {
            result.append(j).append(" ");
        }
        result.append("\n");

        for (int i = 0; i < rows; i++) {
            result.append(i).append(" ");
            for (int j = 0; j < columns; j++) {
                if (field[i][j].getRevealed()) {
                    String status = field[i][j].getStatus();
                    switch (status) {
                        // Color codes for the different possible cell statuses
                        case "0":
                            result.append(ANSI_YELLOW).append(status).append(ANSI_GREY_BG).append(" ");
                            break;
                        case "1":
                            result.append(ANSI_BLUE).append(status).append(ANSI_GREY_BG).append(" ");
                            break;
                        case "2":
                            result.append(ANSI_GREEN).append(status).append(ANSI_GREY_BG).append(" ");
                            break;
                        case "3":
                            result.append(ANSI_RED).append(status).append(ANSI_GREY_BG).append(" ");
                            break;
                        case "4":
                            result.append(ANSI_BLUE_BRIGHT).append(status).append(ANSI_GREY_BG).append(" ");
                            break;
                        case "5":
                            result.append(ANSI_RED_BRIGHT).append(status).append(ANSI_GREY_BG).append(" ");
                            break;
                        case "6":
                            result.append(ANSI_YELLOW).append(status).append(ANSI_GREY_BG).append(" ");
                            break;
                        case "M":
                            result.append(ANSI_RED_BRIGHT).append(status).append(ANSI_GREY_BG).append(" ");
                            break;
                        case "F":
                            result.append(ANSI_BLUE_BRIGHT).append(status).append(ANSI_GREY_BG).append(" ");
                            break;
                        default:
                            result.append(status).append(" ");
                            break;
                    }
                } else {
                    // If the cell is not revealed and has a flag, display the flag
                    if (field[i][j].getStatus().equals("F")) {
                        result.append("F ");
                    } else {
                        result.append("- ");
                    }
                }
            }
            result.append("\n");
        }
        // Return the final string representation of the game board
        return result.toString();
    }

}
