package nz.ac.wgtn.swen225.lc.domain;

/**
 * Enumerator for the different types of event that Domain can report via DomainObserver.
 *
 * @author Riley West (300608942).
 * @author Jebadiah (300629357).
 */
public enum EventType {
  /**
   * Indicates that a door has been unlocked.
   */
  UNLOCK_DOOR,
  /**
   * Indicates that the player has died.
   */
  DEATH,
  /**
   * Indicates that the player has tried to unlock a door.
   */
  LOCKED_DOOR,
  /**
   * Indicates that the player has taken damage.
   */
  DAMAGE,
  /**
   * Indicates that the player has picked up an item.
   */
  PICKUP_ITEM,

  /**
   * Indicates that the player has won.
   */
  WIN,
  /**
   * Indicates that the level has been successfully reset.
   */
  LEVEL_RESET
}
