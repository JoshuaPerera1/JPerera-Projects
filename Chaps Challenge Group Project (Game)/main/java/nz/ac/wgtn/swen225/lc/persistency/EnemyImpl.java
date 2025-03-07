package nz.ac.wgtn.swen225.lc.persistency;

import java.awt.Point;
import nz.ac.wgtn.swen225.lc.domain.Direction;
import nz.ac.wgtn.swen225.lc.domain.Domain;
import nz.ac.wgtn.swen225.lc.domain.EnemyActor;


/**
 * Implementation of EnemyActor for level 2.
 * Simple "AI" that moves towards the player.
 */
public class EnemyImpl implements EnemyActor {

  private int index;

  private Point position;
  private Direction direction;

  /**
   * Constructor.
   *
   * @param x POSIX
   * @param y POSIY
   */
  public EnemyImpl(int x, int y) {
    this.position = new Point(x, y);
    this.index = 0;
    this.direction = Direction.DOWN;
  }

  @Override
  public Direction getMove(Domain context) {
    Point chapPos = context.getChap().getPosition();
    int xDiff = this.position.x - chapPos.x;
    int yDiff = this.position.y - chapPos.y;
    Direction move;
    if (Math.abs(xDiff) >= Math.abs(yDiff)) {
      //Greater difference in  indicates that chap is to the left or right.
      if (xDiff < 0) {
        //We are to the left, move right.
        move = Direction.RIGHT;
      } else {
        //We are to the right, move left.
        move = Direction.LEFT;
      }
    } else {
      //Chap is up or down
      if (yDiff < 0) {
        //We are above, move down.
        move = Direction.DOWN;
      } else {
        //We are below, move up
        move = Direction.UP;
      }
    }

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
