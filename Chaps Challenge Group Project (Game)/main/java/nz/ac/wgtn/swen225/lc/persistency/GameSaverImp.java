package nz.ac.wgtn.swen225.lc.persistency;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import nz.ac.wgtn.swen225.lc.domain.Chap;
import nz.ac.wgtn.swen225.lc.domain.Domain;
import nz.ac.wgtn.swen225.lc.domain.EnemyActor;
import nz.ac.wgtn.swen225.lc.domain.TileType;

/**
 * Implementation for Saving running Games.
 *
 * @author Benjamin Park
 */
public class GameSaverImp implements GameSaver {

  /**
   * Save the current state of domain level to file.
   *
   * @param domain      the domain to save state of.
   * @param levelNumber number of the current level.
   * @throws IOException if saving fails.
   */
  public void saveGame(Domain domain, int levelNumber, File file) throws IOException {
    // Retrieve all the information from the domain.
    TileType[][] mazeTiles = domain.getTiles();
    Chap chap = domain.getChap();
    List<EnemyActor> enemies = domain.getEnemyActorList();
    List<TileType> chapInventory = chap.getKeys();
    String infoText = domain.getInfo();

    // Create a GameState object for saving.
    GameState gameState =
        new GameState(levelNumber, mazeTiles, chap, enemies, chapInventory, infoText);

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, gameState);
  }
}
