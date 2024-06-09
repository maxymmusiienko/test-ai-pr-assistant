package net.broscorp.gamelife;

import java.util.List;
import net.broscorp.gamelife.model.Game;
import net.broscorp.gamelife.service.FileReader;
import net.broscorp.gamelife.service.FileWriter;
import net.broscorp.gamelife.service.GameParser;
import net.broscorp.gamelife.service.GameService;

public class GameOfLife {
  private final FileReader reader = new FileReader();
  private final GameParser parser = new GameParser();
  private final GameService gameService = new GameService();
  private final FileWriter writer = new FileWriter();

  public void game(String fileNameInput, String fileNameOutput) {
    List<String> input = reader.read(fileNameInput);
    Game game = parser.parseGame(input);
    int iterations = game.getIterations();

    for (int start = 0; start < iterations; start++) {
      gameService.processIteration(game);
    }

    writer.write(game, fileNameOutput);
  }
}
