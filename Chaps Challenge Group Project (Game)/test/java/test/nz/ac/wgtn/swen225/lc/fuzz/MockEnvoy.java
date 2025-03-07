package test.nz.ac.wgtn.swen225.lc.fuzz;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock envoy used before App is developed enough for a true envoy.
 *
 * @author Jebadiah Dudfield 300629357
 */
public class MockEnvoy implements GameEnvoy {

  StringBuilder out;
  boolean paused;

  /**
   * Resets the Envoy. Currently unused, but just in case.
   */
  public void reset() {
    positions.clear();
    positions.add(new int[] {0, 0});
    out = new StringBuilder();
  }

  final List<int[]> positions = new ArrayList<>();

  MockEnvoy() {
    positions.add(new int[] {0, 0});
    out = new StringBuilder();
  }

  @Override
   public boolean moveUp() {
    int[] oldP = positions.get(positions.size() - 1);
    int[] newP = {oldP[0], oldP[1] - 1};
    positions.add(newP);
    return true;
  }

  @Override
   public boolean moveDown() {
    int[] oldP = positions.get(positions.size() - 1);
    int[] newP = {oldP[0], oldP[1] + 1};
    positions.add(newP);
    return true;
  }

  @Override
   public boolean moveLeft() {
    int[] oldP = positions.get(positions.size() - 1);
    int[] newP = {oldP[0] - 1, oldP[1]};
    positions.add(newP);
    return true;
  }

  @Override
   public boolean moveRight() {
    int[] oldP = positions.get(positions.size() - 1);
    int[] newP = {oldP[0] + 1, oldP[1]};
    positions.add(newP);
    return true;
  }

  @Override
   public void pause() {
    out.append("+P\n");
    paused = true;
  }

  @Override
   public void unpause() {
    out.append("-P\n");
    paused = false;
  }

  @Override
  public boolean isStopped() {
    return paused;
  }
}
