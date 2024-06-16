package net.broscorp.gamelife;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import java.io.InputStreamReader;

public class GameOfLife {

    private Integer rowSize;
    private Integer colSize;
    private Integer iterationCount;

    private char[][] field;

    public void game(String fileNameInput, String fileNameOutput) {
        ClassLoader classLoader = GameOfLife.class.getClassLoader();
        InputStream gameDataInputStream = classLoader.getResourceAsStream(fileNameInput);
        if (gameDataInputStream != null) {
            parseGameData(gameDataInputStream);
            for (int i = 0; i < iterationCount; i++) {
                char[][] newField = new char[rowSize][colSize];

                for (int row = 0; row < rowSize; row++) {
                    for (int col = 0; col < colSize; col++) {
                        int liveNeighbors = countLiveNeighbors(field, row, col);
                        if (field[row][col] == 'X') {
                            if (liveNeighbors < 2 || liveNeighbors > 3) {
                                newField[row][col] = 'O';
                            } else {
                                newField[row][col] = 'X';
                            }
                        } else {
                            if (liveNeighbors == 3) {
                                newField[row][col] = 'X';
                            } else {
                                newField[row][col] = 'O';
                            }
                        }
                    }
                }
                field = newField;
            }
        }
        saveToFile("target/test-classes/" + fileNameOutput);
    }

    private void saveToFile(String fileNameOutput) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileNameOutput))) {
            for (int i = 0; i < field.length; i++) {
                for (int j = 0; j < field[i].length; j++) {
                    if (j == field[i].length - 1) {
                        writer.write(field[i][j]);
                    } else {
                        writer.write(field[i][j] + " ");
                    }
                }
                if (i != field.length - 1) {
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int getPeriodicIndex(int index, int limit) {
        if (index < 0) {
            return limit - 1;
        } else if (index >= limit) {
            return 0;
        }
        return index;
    }

    private int countLiveNeighbors(char[][] field, int row, int col) {
        int count = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }

                int neighborX = getPeriodicIndex(row + i, rowSize);
                int neighborY = getPeriodicIndex(col + j, colSize);

                if (field[neighborX][neighborY] == 'X') {
                    count++;
                }
            }
        }

        return count;
    }

    private void parseGameData(InputStream inputStream) {
        char[][] result;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String dataLine = bufferedReader.readLine();
            String[] dataValues = dataLine.split(",");

            rowSize = Integer.valueOf(dataValues[0]);
            colSize = Integer.valueOf(dataValues[1]);
            iterationCount = Integer.valueOf((dataValues[2]));

            result = new char[rowSize][colSize];
            int i = 0;
            while ((dataLine = bufferedReader.readLine()) != null) {
                dataValues = dataLine.split(" ");
                for (int j = 0; j < colSize; j++) {
                    result[i][j] = dataValues[j].toCharArray()[0];
                }
                i++;
            }
            field = result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
