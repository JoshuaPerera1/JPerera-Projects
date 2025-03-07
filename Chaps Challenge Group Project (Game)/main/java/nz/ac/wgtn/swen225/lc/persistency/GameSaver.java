package nz.ac.wgtn.swen225.lc.persistency;

import java.io.File;
import java.io.IOException;
import nz.ac.wgtn.swen225.lc.domain.Domain;

/**
 * Interface for loading Levels as part of new or loaded Game.
 */
public interface GameSaver {

  /**
   * Saves a game into the passed domain.
   *
   * @param domain      the domain to save the level of.
   * @param levelNumber number of the level to save.
   * @throws IOException if loading fails.
   */
  public void saveGame(Domain domain, int levelNumber, File file) throws IOException;
}