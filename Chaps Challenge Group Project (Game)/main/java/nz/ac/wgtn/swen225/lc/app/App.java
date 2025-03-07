package nz.ac.wgtn.swen225.lc.app;

import java.io.File;
import java.io.IOException;
import nz.ac.wgtn.swen225.lc.domain.Direction;
import nz.ac.wgtn.swen225.lc.domain.Domain;
import nz.ac.wgtn.swen225.lc.persistency.GameLoader;
import nz.ac.wgtn.swen225.lc.persistency.GameLoaderImp;
import nz.ac.wgtn.swen225.lc.persistency.GameSaver;
import nz.ac.wgtn.swen225.lc.persistency.GameSaverImp;
import nz.ac.wgtn.swen225.lc.persistency.LevelLoader;
import nz.ac.wgtn.swen225.lc.persistency.ParsingLevelLoader;
import nz.ac.wgtn.swen225.lc.recorder.Recorder;

/**
 * The App module.
 *
 * @author Jack Gallagher (300615528)
 */
public class App {

  private final Domain domain;
  private final Recorder recorder;
  private Runnable updateCallback;

  private final LevelLoader levelLoader;
  private final GameLoader gameLoader;
  private final GameSaver gameSaver;


  /**
   * Constructor.
   */
  public App() {
    this.domain = new Domain();
    this.recorder = new Recorder(this);
    this.updateCallback = null;
    this.levelLoader = new ParsingLevelLoader();
    this.gameLoader = new GameLoaderImp();
    this.gameSaver = new GameSaverImp();
  }

  /**
   * Gets the domain.
   *
   * @return the Domain object.
   */
  public Domain getDomain() {
    return domain;
  }

  /**
   * Gets the recorder.
   *
   * @return the Recorder object.
   */
  public Recorder getRecorder() {
    return recorder;
  }

  /**
   * Sets the update callback (internal to the App module).
   *
   * @param callback - the callback
   */
  public void setUpdateCallback(Runnable callback) {
    assert this.updateCallback == null;
    this.updateCallback = callback;
  }

  /**
   * Mark the app as being updated since the last render.
   */
  public void markUpdated() {
    if (this.updateCallback != null) {
      updateCallback.run();
    }
  }

  /**
   * Handles an input.
   *
   * @param inputType the input to handle.
   * @return whether the input was valid.
   */
  public boolean handleInput(AppInput inputType) {
    // Check if the game has already been won or failed
    if (isGameOver()) {
      return false;
    }

    // TODO: Translate / Validate input with Domain
    try {
      switch (inputType) {
        case MOVE_UP:
          domain.moveChap(Direction.UP);
          break;
        case MOVE_DOWN:
          domain.moveChap(Direction.DOWN);
          break;
        case MOVE_LEFT:
          domain.moveChap(Direction.LEFT);
          break;
        case MOVE_RIGHT:
          domain.moveChap(Direction.RIGHT);
          break;
        default:
          break;
      }
    } catch (IllegalArgumentException | IllegalStateException e) {
      return false;
    }

    recorder.addToRecording("CHAP" + "|" + inputType);
    markUpdated();
    System.out.print("");

    return true;
  }

  /**
   * Starts a new game.
   *
   * @param level - the level to start at.
   */
  public void newGame(int level) {
    try {
      levelLoader.loadLevel(domain, level);
    } catch (IOException e) {
      e.printStackTrace();
    }
    // TODO: Move this somewhere else
    recorder.startRecording(Integer.toString(level));
  }

  /**
   * Saves the game.
   *
   * @param file the file to save to.
   * @return whether saving was successful.
   */
  public boolean saveGame(File file) {
    try {
      if (!file.exists()) {
        file.createNewFile();
      }
      gameSaver.saveGame(domain, domain.getLevelNumber(), file);
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }

    return true;
  }

  /**
   * Loads the game.
   *
   * @param file the file to load.
   * @return whether loading was successful.
   */
  public boolean loadGame(File file) {
    System.out.println("[APP DEBUG] Loading game from " + file.getAbsolutePath());
    try {
      gameLoader.loadGame(domain, file);
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }

    markUpdated();
    return true;
  }

  /**
   * Ends the game.
   */
  public void endGame() {
    recorder.addToRecording("END"); // Lets recorder know when to stop
    markUpdated();
  }

  /**
   * Get whether the current game is over.
   *
   * @return whether the game is over.
   */
  public boolean isGameOver() {
    return this.domain.getFailed() || this.domain.getWon();
  }

  /**
   * Entry Point.
   *
   * @param args command line arguments.
   */
  public static void main(String[] args) {
    App app = new App();
    app.newGame(1);

    AppWindow window = new AppWindow(app);
    window.setVisible(true);
  }
}
