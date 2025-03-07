package nz.ac.wgtn.swen225.lc.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.awt.Point;
import java.util.ArrayList;

/**
 * Handles the Chap class which is the main character of the game.
 *
 * @author Riley West (300608942).
 * @author Jebadiah (300629357).
 */

public class Chap implements Entity {

  //Chap's current position.
  private Point position;

  private Direction direction;

  //The keys that Chap is holding.
  private final ArrayList<TileType> keys;
  private boolean alive;

  /**
   * Constructor for the Chap class.
   *
   * @param initialCol Chap's starting column.
   * @param initialRow Chap's starting row.
   */
  public Chap(int initialCol, int initialRow) {
    this.position = new Point(initialCol, initialRow);
    this.keys = new ArrayList<>();
    this.alive = true;
    this.direction = Direction.DOWN;
  }

  /**
   * JSON Constructor for the Chap class.
   *
   * @param position  Chap's saved position.
   * @param keys      Chap's saved keys.
   * @param alive     Whether Chap is alive.
   * @param direction Which direction Chap is facing in.
   */
  @JsonCreator
  public Chap(@JsonProperty("position") Point position,
              @JsonProperty("keys") ArrayList<TileType> keys,
              @JsonProperty("alive") boolean alive,
              @JsonProperty("direction") Direction direction) {
    // Initialize fields with arguments
    this.position = position;
    this.keys = keys;
    this.alive = alive;
    this.direction = direction;
  }

  /**
   * Getter for position.
   *
   * @return Chap's current Position.
   */
  @Override
  public Point getPosition() {
    return position;
  }


  /**
   * Setter for position.
   *
   * @param row Chap's new row.
   * @param col Chap's new column.
   */
  @Override
  public void setPosition(int row, int col) {
    position = new Point(row, col);
  }

  /**
   * Getter for keys.
   *
   * @return Chap's currently-held keys.
   */
  public ArrayList<TileType> getKeys() {
    return keys;
  }

  /**
   * Adds a key to Chap's list of held keys.
   * TODO check typing here
   *
   * @param key the new key for Chap to hold.
   */
  public void addKey(TileType key) {
    keys.add(key);
  }

  /**
   * Checks whether Chap has at least one of a type of key.
   *
   * @param key the key type we're looking for.
   * @return true if keys contains a TileType that's the same as key, false otherwise
   */
  public boolean hasKey(TileType key) {
    for (TileType held : keys) {
      if (held == key) {
        return true;
      }
    }
    return false;
  }

  /**
   * Removes a key from Chap's list of held keys.
   * TODO check typing here
   *
   * @param key the new key for Chap to hold.
   */
  public void removeKey(TileType key) {
    keys.remove(key);
  }

  /**
   * Getter for Chap's alive state.
   * TODO expose this in Domain
   *
   * @return true if Chap is alive, false otherwise.
   */
  public boolean isAlive() {
    return alive;
  }

  /**
   * Setter for Chap's alive state.
   *
   * @param k Chap's new life status.
   */
  public void setAlive(boolean k) {
    this.alive = k;
  }

  /**
   * Setter for direction.
   *
   * @param direction Chap's new direction.
   */
  @Override
  public void setDirection(Direction direction) {
    this.direction = direction;
  }

  /**
   * Getter for direction.
   *
   * @return Chap's current direction.
   */
  @Override
  public Direction getDirection() {
    return direction;
  }
}
