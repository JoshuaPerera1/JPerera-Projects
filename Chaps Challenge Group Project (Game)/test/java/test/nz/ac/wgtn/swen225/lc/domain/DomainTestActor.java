package test.nz.ac.wgtn.swen225.lc.domain;

import java.awt.Point;
import nz.ac.wgtn.swen225.lc.domain.Direction;
import nz.ac.wgtn.swen225.lc.domain.Domain;
import nz.ac.wgtn.swen225.lc.domain.EnemyActor;
import org.junit.jupiter.api.Assertions;

/**
 * Implementation of EnemyActor for unit testing.
 */
public class DomainTestActor implements EnemyActor {

  private int index;

  private Point position;
  private Direction direction;

  /**
   * Constructor.
   *
   * @param x POSIX
   * @param y POSIY
   */
  public DomainTestActor(int x, int y) {
    this.position = new Point(x, y);
    this.index = 0;
    this.direction = Direction.DOWN;
  }


  @Override
  public Direction getMove(Domain context) {
    Direction move;
    move = switch (index) {
      case 0 -> Direction.UP;
      case 1 -> Direction.RIGHT;
      case 2 -> Direction.DOWN;
      case 3 -> Direction.LEFT;
      default -> Assertions.fail();
    };
    index = (index + 1) % 4;
    this.direction = move;
    return move;
  }

  @Override
  public void setPosition(int row, int col) {
    this.position = new Point(row, col);
  }

  @Override
  public Point getPosition() {
    return this.position;
  }

  @Override
  public void setDirection(Direction direction) {
    this.direction = direction;
  }

  @Override
  public Direction getDirection() {
    return this.direction;
  }
}
