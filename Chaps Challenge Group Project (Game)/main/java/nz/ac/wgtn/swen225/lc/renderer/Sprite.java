package nz.ac.wgtn.swen225.lc.renderer;

import java.awt.image.BufferedImage;

/**
 * Sprite.java
 * Enum for different sprites
 *
 * @author Leory Xue (300607821)
 */
public enum Sprite {
  /**
   * Sprite for the left facing chap.
   */
  chapL,

  /**
   * Sprite for the right facing chap.
   */
  chapR,

  /**
   * Sprite for the up facing chap.
   */
  chapU,

  /**
   * Sprite for the down facing chap.
   */
  chapD,

  /**
   * Sprite for the left facing actor.
   */
  actorL,

  /**
   * Sprite for the right facing actor.
   */
  actorR,

  /**
   * Sprite for the up facing actor.
   */
  actorU,

  /**
   * Sprite for the down facing actor.
   */
  actorD,

  /**
   * Sprite for the wall tile.
   */
  wallTile,

  /**
   * Sprite for the free tile.
   */
  freeTile1,

  /**
   * Sprite for the red Key.
   */
  redKey,

  /**
   * Sprite for the blue Key.
   */
  blueKey,

  /**
   * Sprite for the green Key.
   */
  greenKey,

  /**
   * Sprite for the yellow Key.
   */
  yellowKey,

  /**
   * Sprite for the red Locked door.
   */
  redLock,

  /**
   * Sprite for the blue Locked door.
   */
  blueLock,

  /**
   * Sprite for the green Locked door.
   */
  greenLock,

  /**
   * Sprite for the yellow Locked door.
   */
  yellowLock,

  /**
   * Sprite for the treasure tile.
   */
  treasure,

  /**
   * Sprite for the info tile.
   */
  info,

  /**
   * Sprite for the exit with a lock.
   */
  exitLock,

  /**
   * Sprite for the open exit.
   */
  exit;

  /**
   * The image to be loaded and displayed on screen.
   */
  public final BufferedImage sprite;

  /**
   * Sprite constructor to load an image.
   */
  Sprite() {
    sprite = loadImage(this.name());
  }

  private static BufferedImage loadImage(String name) {
    String filename = "resources/" + name + ".png";
    try {
      return javax.imageio.ImageIO.read(Sprite.class.getResource("/sprites/" + name + ".png"));
    } catch (Exception e) {
      throw new RuntimeException("Unable to load image: " + filename);
    }
  }
}
