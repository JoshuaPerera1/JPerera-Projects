package test.nz.ac.wgtn.swen225.lc.fuzz;

/**
 * Defines ways in which the FuzzTest class can interact with the game. Introduced to assist with
 * refactoring and allow for development of the FuzzTest module before the App module is finished.
 * TODO Introduce new methods to retrieve information for smarter testing.
 *
 * @author Jebadiah Dudfield 300629357
 */
public interface GameEnvoy {

  /**
   * Resets the envoy to its "start state".
   */
  void reset();

  /**
   * Called to test upwards movement.
   *
   * @return whether the move was successful.
   */
  boolean moveUp();


  /**
   * Called to test downwards movement.
   *
   * @return whether the move was successful.
   */
  boolean moveDown();


  /**
   * Called to test leftwards movement.
   *
   * @return whether the move was successful.
   */
  boolean moveLeft();



  /**
   * Called to test rightwards movement.
   *
   * @return whether the move was successful.
   */
  boolean moveRight();


  /**
   * Called to test pausing.
   */
  void pause();


  /**
   * Called to test unpausing.
   */
  void unpause();



  /**
   * Returns whether the game simulation has stopped.
   *
   * @return whether the pause was successful.
   */
  boolean isStopped();
}


