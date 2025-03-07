package nz.ac.wgtn.swen225.lc.domain;

/**
 * Enum for the different types of tile in-game. Used for quick checking of a tile's type without
 * instanceof calls.
 *
 * @author Riley West (300608942).
 * @author Jebadiah (300629357).
 */
public enum TileType {
  /**
   * Impassable wall.
   */
  WALL,
  /**
   * Empty tile.
   */
  FREE,
  /**
   * Tile with a blue key in it.
   */
  BLUE_KEY,
  /**
   * Tile with a red key in it.
   */
  RED_KEY,
  /**
   * Tile with a green key in it.
   */
  GREEN_KEY,
  /**
   * Tile with a yellow key in it.
   */
  YELLOW_KEY,
  /**
   * Locked blue door.
   */
  BLUE_DOOR,
  /**
   * Locked red door.
   */
  RED_DOOR,
  /**
   * Locked green door.
   */
  GREEN_DOOR,
  /**
   * Locked yellow door.
   */
  YELLOW_DOOR,
  /**
   * Tile that displays information when Chap steps on it.
   */
  INFO,
  /**
   * Tile with a treasure on it.
   */
  TREASURE,
  /**
   * Tile that blocks exit until there are no treasures remaining.
   */
  EXIT_LOCK,
  /**
   * Tile that ends the level when entered.
   */
  EXIT
}
