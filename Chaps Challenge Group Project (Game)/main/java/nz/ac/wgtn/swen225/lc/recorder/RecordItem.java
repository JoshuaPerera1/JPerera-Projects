package nz.ac.wgtn.swen225.lc.recorder;

/**
 * RecordItem.java.
 *
 * <p>RecordItem class is used to store information about the activity of the game.
 *
 * @author Neeraj Patel (300604056).
 */
public class RecordItem {
  // ----------------------------------- VARIABLES ----------------------------------- //
  int sequenceNumber;
  String actor;
  String move;

  /**
   * Constructor for RecordItem class.
   *
   * @param seqNumber - the sequence number of the action.
   * @param actor     - the actor that took the action.
   * @param move    - movement information about the actor
   */
  public RecordItem(int seqNumber, String actor, String move) {
    this.sequenceNumber = seqNumber;
    this.actor = actor;
    this.move = move;
  }

  /**
   * Returns the actor that took the action.
   *
   * @return actor - the actor that took the action.
   */
  public String getActor() {
    return actor;
  }


  /**
   * Returns any movement information that is needed to be stored.
   *
   * @return movement - the move made by the actor.
   */
  public String getMove() {
    return move;
  }

  /**
   * Returns a string representation of the RecordItem.
   *
   * @return string - a string representation of the RecordItem.
   */
  @Override
  public String toString() {
    return "RecordItem["
            + "sequenceNumber="
            + sequenceNumber
            + ", actor='"
            + actor
            + '\''
            + ", movement='"
            + move
            + '\''
            + ']';
  }

}
