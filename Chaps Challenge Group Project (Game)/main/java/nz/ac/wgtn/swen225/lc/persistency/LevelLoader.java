package nz.ac.wgtn.swen225.lc.persistency;

import java.io.IOException;
import nz.ac.wgtn.swen225.lc.domain.Domain;

/**
 * Interface for loading Levels as part of new or loaded Game.
 */
public interface LevelLoader {

  /**
   * Loads a level into the passed domain.
   *
   * @param domain the domain to load the level into.
   * @param levelNumber number of the level to load.
   * @throws IOException if loading fails.
   */
  public void loadLevel(Domain domain, int levelNumber) throws IOException;
}