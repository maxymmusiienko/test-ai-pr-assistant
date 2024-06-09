package net.broscorp.gamelife;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GameOfLife {

    private static final String LOG_FILE = "execution-logs.txt";

    private final ClassLoader classLoader = GameOfLife.class.getClassLoader();

    private int rows;

    private int cols;

    private int generations;

    private boolean[][] gameField;


    /**
     * Main method to start Game of Life.
     *
     * @param fileNameInput  name of input file
     * @param fileNameOutput name of output file
     */
    public void game(String fileNameInput, String fileNameOutput) {

        initGame(fileNameInput);

        handleGenerations();

        writeResultToFile(fileNameOutput);
    }

    /**
     * Reads input file, initializes init params for game: gameField, row, column and generation
     * count.
     *
     * @param inputFile name of input file
     */
    private void initGame(String inputFile) {

        try {

            if (classLoader.getResource(inputFile) == null) {
                throw new IOException("File doesn't exists: " + inputFile);
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(classLoader.getSystemResourceAsStream(inputFile)))) {
                String line = reader.readLine();

                if (line != null) {
                    String[] initParams = line.split(",");

                    this.rows = Integer.parseInt(initParams[0]);
                    this.cols = Integer.parseInt(initParams[1]);
                    this.generations = Integer.parseInt(initParams[2]);
                }

                this.gameField = new boolean[this.rows][this.cols];

                int i = 0;

                line = reader.readLine();
                while (line != null && i < this.rows) {
                    initRowInGameField(i, line.split(" "));
                    line = reader.readLine();
                    i++;
                }
            }

        } catch (IOException e) {
            logErrorsToFile(e);
        }
    }

    /**
     * Logs an error message if any exception occurs on program startup.
     *
     * @param ex thrown exception
     */
    private void logErrorsToFile(Throwable ex) {
        try {
            Path newFile = Paths.get(this.getClass().getResource("/").toURI()).resolve(LOG_FILE);

            if (!Files.exists(newFile)) {
                Files.createFile(newFile);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(newFile)) {
                writer.write(ex.getMessage());
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inits current game field row with retrieved data from file.
     *
     * @param currentRow current row from file
     * @param row        current row index in game field
     */
    private void initRowInGameField(int currentRow, String[] row) {
        for (int i = 0; i < cols; i++) {
            this.gameField[currentRow][i] = mapCellToBoolean(row[i]);
        }
    }

    /**
     * Converts current cell from byte to String.
     *
     * @param cell current cell to map
     * @return cell representation with String datatype
     */
    private String mapCellToString(boolean cell) {
        return cell ? "X" : "O";
    }

    /**
     * Converts current cell from String to byte.
     *
     * @param cell current cell to map
     * @return cell representation with byte datatype
     */
    private boolean mapCellToBoolean(String cell) {
        return cell.equals("X");
    }

    /**
     * Writes state of gameFiled in output file.
     *
     * @param fileNameOutput name of output file
     */
    private void writeResultToFile(String fileNameOutput) {

        try {
            Path newFile = Paths.get(this.getClass().getResource("/").toURI()).resolve(fileNameOutput);

            if (!Files.exists(newFile)) {
                Files.createFile(newFile);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(newFile)) {
                writer.write(mapGameFieldToString());
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

    }

    private String mapGameFieldToString() {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sb.append(mapCellToString(gameField[i][j]));

                if (j < cols - 1) {
                    sb.append(" ");
                }
            }
            sb.append(System.lineSeparator());
        }

        return sb.toString();
    }

    /**
     * Counts amount of alive neighbors of current item.
     *
     * @param neighbors neighbors of current item
     * @return int - amount of alive neighbors
     */
    private int aliveCellsCount(boolean[] neighbors) {
        int count = 0;

        for (boolean cell : neighbors) {
            if (cell) {
                count++;
            }
        }
        return count;
    }

    private void handleGenerations() {

        for (int i = 0; i < this.generations; i++) {
            boolean[][] nextGenerationField = new boolean[this.rows][this.cols];

            nextGeneration(nextGenerationField);

            this.gameField = nextGenerationField;
        }
    }

    /**
     * Computes next generation based on previous.
     *
     * @param nextGenerationField next generation
     */
    private void nextGeneration(boolean[][] nextGenerationField) {
        for (int j = 0; j < this.rows; j++) {
            for (int k = 0; k < this.cols; k++) {

                boolean[] neighbors = getNeighbors(j, k);

                int aliveNeighbors = aliveCellsCount(neighbors);

                if (gameField[j][k]) {
                    nextGenerationField[j][k] = !(aliveNeighbors < 2 || aliveNeighbors > 3);
                } else {
                    nextGenerationField[j][k] = (aliveNeighbors == 3);
                }
            }
        }
    }

    /**
     * Finds all neighbors of given current item.
     *
     * @param row current row index
     * @param col current column index
     * @return array of String - all neighbors of the current item
     */
    private boolean[] getNeighbors(int row, int col) {
        boolean[] neighbors = new boolean[8];

        int index = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }

                int neighborRow = (row + i + this.rows) % this.rows;
                int neighborCol = (col + j + this.cols) % this.cols;

                neighbors[index++] = this.gameField[neighborRow][neighborCol];
            }
        }
        return neighbors;
    }

}