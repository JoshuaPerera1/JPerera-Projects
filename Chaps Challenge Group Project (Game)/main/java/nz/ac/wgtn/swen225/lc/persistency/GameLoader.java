package nz.ac.wgtn.swen225.lc.persistency;

import java.io.File;
import java.io.IOException;
import nz.ac.wgtn.swen225.lc.domain.Domain;

/**
 * Interface for loading Levels as part of new or loaded Game.
 */
public interface GameLoader {

  /**
   * Loads a Game into the passed domain.
   *
   * @param domain the domain to load the level into.
   * @throws IOException if loading fails.
   */
  public void loadGame(Domain domain, File file) throws IOException;
}