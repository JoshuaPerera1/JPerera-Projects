package nz.ac.wgtn.swen225.lc.domain;

import java.awt.Point;

/**
 * Used for anything on the board that may move. Chap or Actors.
 *
 * @author Jebadiah (300629357).
 */
public interface Entity {

  /**
   * Setter for position.
   *
   * @param row The Entity's new row.
   * @param col The Entity's new column.
   */
  void setPosition(int row, int col);

  /**
   * Getter for position.
   *
   * @return The Entity's current Position.
   */
  Point getPosition();

  /**
   * Setter for direction.
   *
   * @param direction The Entity's new direction.
   */
  void setDirection(Direction direction);

  /**
   * Getter for direction.
   *
   * @return The Entity's current direction.
   */
  Direction getDirection();
}
