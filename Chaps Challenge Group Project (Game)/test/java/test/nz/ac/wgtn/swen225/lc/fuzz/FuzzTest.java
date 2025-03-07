package test.nz.ac.wgtn.swen225.lc.fuzz;

import org.junit.jupiter.api.Test;

/**
 * Provides fuzz testing by providing semi-random inputs to the App module in order to provoke
 * errors.
 *
 * @author Jebadiah Dudfield 300629357
 */
public class FuzzTest {

  //Number of random movement tests to carry out on each level.
  private static final int iterations = 1000;
  //Number of random steps to take on each level.
  private static final int steps = 100;
  //Number of pause/unpause cycles to execute in one wave.
  private static final int pauses = 1;

  /**
   * Tests on level 1.
   */
  @Test
  public void test1() {
    testLevel(1);
  }

  /**
   * Tests on level 2. Level 2 has much added complexity due to unknown bonus actors.
   */
  @Test
  public void test2() {
    testLevel(2);
  }

  /**
   * Tests an arbitrary level, if it exists, otherwise throws an error.
   *
   * @param level the index of the level we wish to test.
   */
  public void testLevel(int level) {
    // random movement testing
    // pause/resume testing (in quick succession, make inputs while paused)
    // TODO setup/teardown, level loading and detection of success.
    for (int i = 0; i < iterations; i++) {
      //TODO Setup
      GameEnvoy envoy = getNewEnvoy(level);
      envoy.reset();
      FuzzMovementManager fuzzMovementManager = new FuzzMovementManager(envoy);
      for (int step = 0; step < steps; step++) {
        //5% chance of pausespam test.
        if (Math.random() < 0.05) {
          for (int j = 0; j < pauses; j++) {
            envoy.pause();
            envoy.unpause();
          }
        }
        fuzzMovementManager.generateMove();
        if (envoy.isStopped()) {
          break;
        }
      }
    }
  }

  /**
   * Generates a GameEnvoy for the tests to use to interact with the App.
   *
   * @param level the level to load. An error should be thrown if this is an invalid level.
   * @return An GameEnvoy.
   */
  private GameEnvoy getNewEnvoy(int level) {
    // TODO check level is real.
    return new AppEnvoy(level);
  }

  /**
   * Used for intelligent movement purposes.
   * If we are unable to move in a direction, we will not try to move in that direction again
   * until we have made a successful move OR tried all directions without success.
   */
  private static class FuzzMovementManager {

    final GameEnvoy envoy;
    /*
    0 = up, 1 = right, 2 = down, 3 = left
    Consistent with above
    */
    boolean[] directionPotentials = {true, true, true, true};

    private FuzzMovementManager(GameEnvoy envoy) {
      this.envoy = envoy;
    }

    public void generateMove() {
      int unattemptedDirections = 0;
      for (boolean b : directionPotentials) {
        if (b) {
          unattemptedDirections++;
        }
      }
      if (unattemptedDirections == 0) {
        //If we've tried every direction, start trying them all again.
        unattemptedDirections = 4;
        directionPotentials = new boolean[] {true, true, true, true};
      }
      int[] options = new int[unattemptedDirections];
      int optIndex = 0;
      for (int i = 0; i < 4; i++) {
        if (directionPotentials[i]) {
          options[optIndex] = i;
          optIndex++;
        }
      }

      int randDirIndex = (int) Math.floor(Math.random() * (unattemptedDirections));

      int moveDir = options[randDirIndex];

      boolean success = switch (moveDir) {
        case 0 -> envoy.moveUp();
        case 1 -> envoy.moveDown();
        case 2 -> envoy.moveLeft();
        case 3 -> envoy.moveRight();
        default -> throw new IllegalArgumentException();
      };

      if (success) {
        //Move successful, clear vars.
        directionPotentials = new boolean[] {true, true, true, true};
      } else {
        //Move unsuccessful, mark and return.
        directionPotentials[randDirIndex] = true;
      }
    }
  }

}
