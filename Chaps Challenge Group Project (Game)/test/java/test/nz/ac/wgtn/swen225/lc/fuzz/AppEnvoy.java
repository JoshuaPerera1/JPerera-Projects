package test.nz.ac.wgtn.swen225.lc.fuzz;

import nz.ac.wgtn.swen225.lc.app.App;
import nz.ac.wgtn.swen225.lc.app.AppInput;

/**
 * The real-deal Envoy that deals with App. <<<<<<< HEAD
 *
 * @author Jebadiah Dudfield 300629357 ======= >>>>>>> 521aba9 (Switched in new AppEnvoy class)
 */
public class AppEnvoy implements GameEnvoy {

  private final App app;
  private final int level;

  AppEnvoy(int level) {
    this.level = level;
    app = new App();
    app.newGame(level);
  }

  @Override
  public void reset() {
    app.newGame(level);
  }

  @Override
  public boolean moveUp() {
    return app.handleInput(AppInput.MOVE_UP);
  }

  @Override
  public boolean moveDown() {
    return app.handleInput(AppInput.MOVE_DOWN);
  }

  @Override
  public boolean moveLeft() {
    return app.handleInput(AppInput.MOVE_LEFT);
  }

  @Override
  public boolean moveRight() {
    return app.handleInput(AppInput.MOVE_RIGHT);
  }

  @Override
  public void pause() {
    app.handleInput(AppInput.PAUSE);
  }

  @Override
  public void unpause() {
    app.handleInput(AppInput.UNPAUSE);
  }

  @Override
  public boolean isStopped() {
    return app.isGameOver();
  }
}
