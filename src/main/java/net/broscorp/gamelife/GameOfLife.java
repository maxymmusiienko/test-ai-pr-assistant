package net.broscorp.gamelife;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GameOfLife {
    private final char alive = 'X';
    private final char dead = 'O';
    char[][] field; // field for storing current board
    char[][] fieldNew; // field for storing next move board
    private int width;
    private int height;
    private int iterations;

    /**
     * This is the main method of GameOfLife class. It executes all necessary methods to proceed data
     * and save a result.
     */
    public void game(String fileNameInput, String fileNameOutput) {
        // Reading and using all data from file
        proceedInputData(fileNameInput);

        // "living" the game
        for (int i = 0; i < iterations; i++) {
            process();
        }

        // writing result to file
        saveResult(fileNameOutput);
    }

    private void proceedInputData(String fileNameInput) {
        try (BufferedReader reader =
                     new BufferedReader(new FileReader("src/test/resources/" + fileNameInput))) {
            String line = reader.readLine(); // reading first line, a.k.a. "rules"
            String[] rules = line.split(","); // splitting line into separate numbers

            // reading rules from file to class
            height = Integer.parseInt(rules[0]);
            width = Integer.parseInt(rules[1]);
            iterations = Integer.parseInt(rules[2]);

            // creating fields
            field = new char[height][width];
            fieldNew = new char[height][width];

            // filling the starting field
            for (int row = 0; row < height; row++) { // iterating rows
                line = reader.readLine();
                char[] chars = line.toCharArray();

                for (int col = 0; col < width; col++) { // iterating columns
                    // filling row
                    field[row][col] = chars[col * 2];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // creating next turn board depending on the current one

    private void process() {
        int neighbors; // number of neighbors of the current cell

        for (int row = 0; row < height; row++) { // iterating rows
            for (int col = 0; col < width; col++) { // iterating columns
                // counting neighbors for current cell
                neighbors = countNeighbors(row, col);

                // defining state of cell after this turn
                if (field[row][col] == alive) { // if cell is alive
                    if (neighbors == 2 || neighbors == 3) {
                        fieldNew[row][col] = alive;
                    } else {
                        fieldNew[row][col] = dead;
                    }
                } else { // if currently dead
                    if (neighbors == 3) {
                        fieldNew[row][col] = alive;
                    } else {
                        fieldNew[row][col] = dead;
                    }
                }
            }
        }

        // After processing, we can save the result into the current board
        for (int row = 0; row < height; row++) {
            System.arraycopy(fieldNew[row], 0, field[row], 0, width);
        }
    }

    private void saveResult(String fileNameOutput) {
        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter("target/test-classes/" + fileNameOutput))) {
            for (int row = 0; row < height; row++) { // iterating rows
                for (int col = 0; col < width; col++) { // iterating columns
                    // if last in column, add \n, else just space
                    writer.write(field[row][col] + ((col < width - 1) ? " " : "\n"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int countNeighbors(int row, int col) {
        int number = 0;

        int[] rowShifts = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] colShifts = {-1, 0, 1, -1, 1, -1, 0, 1};

        // checking all neighbors
        for (int i = 0; i < 8; i++) {
      /*Here's the logic of calculating neighbor coords
      - if we move outside a top or left border, we have index [-1], so we have to
      add width or height to the index to get the last cell
      (if its any other index it won't affect position,
      because we will use % operation)
      - if we move outside the bottom /right border, we have index [width or height].
      to handle it, we can use % width or % height operation, so we get index [0]
      */

            int resultRow = (row + rowShifts[i] + height) % height;
            int resultCol = (col + colShifts[i] + width) % width;

            // if neighbor not empty, add to number
            number += (field[resultRow][resultCol] == alive) ? 1 : 0;
        }

        return number;
    }
}
