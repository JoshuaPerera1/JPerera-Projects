package nz.ac.wgtn.swen225.lc.domain;

/**
 * Interface for autonomously moving Entities that kill Chap on touch. Capable of using a passed
 * Domain object to make smart decisions.
 *
 * @author Jebadiah (300629357).
 */
public interface EnemyActor extends Entity {
  /**
   * Semi-intelligently makes a decision on which direction to move in given a contextual domain.
   *
   * @param context the domain that this EnemyActor exists in.
   * @return the direction of choice.
   */
  Direction getMove(Domain context);
}
