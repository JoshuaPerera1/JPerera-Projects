package nz.ac.wgtn.swen225.lc.domain;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class serves as both the public interface to the Domain module And as its logical
 * controller.
 * TODO craft implementation here, make it an implementation of interface DomainView and refactor
 * TODO other modules to fit
 *
 * @author Riley West (300608942).
 * @author Jebadiah (300629357).
 */
public class Domain {

  private int levelNumber;
  private Maze maze;
  private Chap chap;
  private int treasureRemaining;
  private boolean won;
  private boolean failed;
  private String info;
  private List<EnemyActor> enemyActorList;


  /**
   * Default Constructor. Creates an empty maze and puts chap in the top left position.
   * TODO consider other constructors. More likely, some integration with Persistency or something.
   */
  public Domain() {
    this.maze = new Maze(9, 9);
    maze.generateEmptyMaze();
    chap = new Chap(0, 0);
    this.treasureRemaining = 0;
    won = false;
    failed = false;
    info = "Default Info String";
    enemyActorList = new ArrayList<>();
  }

  /**
   * Builds a maze and state using passed objects.
   * TODO Maze should store information about the current level like enemies and chap, move it
   *
   * @param mazeTiles     TileType grid for new level
   * @param chap          Chap object for new level
   * @param enemies       List of EnemyActor objects for new level
   * @param chapInventory List of keys that Chap starts holding
   * @param infoText      info string for new level.
   * @param levelNum      number of the current level. Used for saving/loading when determining
   *                      which jar to get enemies from
   */
  public void buildNewLevel(TileType[][] mazeTiles, Chap chap, List<EnemyActor> enemies,
                            List<TileType> chapInventory, String infoText, int levelNum) {

    //Validation
    //Validating maze size and gathering treasure count info.

    int rows = mazeTiles.length;
    if (rows <= 0) {
      throw new IllegalArgumentException("Maze must have at least one row.");
    }

    int cols = mazeTiles[0].length;
    if (cols <= 0) {
      throw new IllegalArgumentException("Maze must have at least one column.");
    }
    int treasureCount = 0;
    for (TileType[] mazeTile : mazeTiles) {
      if (mazeTile.length != cols) {
        throw new IllegalArgumentException("Maze rows must be of the same size");
      }
      for (int c = 0; c < cols; c++) {
        if (mazeTile[c] == TileType.TREASURE) {
          treasureCount++;
        }
      }
    }

    //Validation of Chap
    if (chap == null) {
      throw new IllegalArgumentException("Chap cannot be null");
    }
    Point chapPos = chap.getPosition();
    if (chapPos == null) {
      throw new IllegalArgumentException("Chap's position cannot be null");
    }
    if (chapPos.x < 0 || chapPos.x >= cols || chapPos.y < 0 || chapPos.y >= rows) {
      throw new IllegalArgumentException("Chap's initial position must be within bounds.");
    }
    Chap ourChap = new Chap(chapPos.x, chapPos.y);
    ourChap.setDirection(chap.getDirection());
    ourChap.setAlive(chap.isAlive());
    for (TileType t : chapInventory) {
      switch (t) {
        case BLUE_KEY, RED_KEY, GREEN_KEY, YELLOW_KEY -> ourChap.addKey(t);
        default -> throw new IllegalArgumentException("Chap should only be holding keys.");
      }
    }

    List<EnemyActor> ourEnemyList = new ArrayList<>();
    //Validation of Enemies.
    for (EnemyActor enemy : enemies) {
      if (enemy == null) {
        throw new IllegalArgumentException("Enemies cannot be null");
      }
      Point enemyPos = enemy.getPosition();
      if (enemyPos == null) {
        throw new IllegalArgumentException("An Enemy's position cannot be null");
      }
      if (enemyPos.x < 0 || enemyPos.x >= cols || enemyPos.y < 0 || enemyPos.y >= rows) {
        throw new IllegalArgumentException("An Enemy's initial position must be within bounds.");
      }
      ourEnemyList.add(enemy);
    }

    //Construction
    Maze newMaze = new Maze(rows, cols);
    newMaze.setTiles(mazeTiles);

    this.maze = newMaze;
    this.chap = ourChap;
    this.treasureRemaining = treasureCount;
    info = infoText;
    enemyActorList = ourEnemyList;

    //These are reset on load by default.
    won = false;
    failed = false;
    levelNumber = levelNum;
    notifyObservers(EventType.LEVEL_RESET, TileType.FREE);
  }

  /**
   * List of observers. Primarily used by the Renderer to play sound effects.
   */
  private final ArrayList<DomainObserver> observers = new ArrayList<>();

  /**
   * Adds an observer of events in Domain.
   *
   * @param observer the new DomainObserver
   */
  public void addObserver(DomainObserver observer) {
    observers.add(observer);
  }

  /**
   * Observer notification method.
   *
   * @param eventType the type of event.
   * @param itemType  the associated TileType.
   */
  private void notifyObservers(EventType eventType, TileType itemType) {
    for (DomainObserver observer : observers) {
      observer.handleEvent(eventType, itemType);
    }
  }

  /**
   * Attempts to move chap in the given Direction. Silently fails if it's an invalid move.
   * TODO move this to dedicated game logic class.
   * TODO throw errors if the move is invalid.
   *
   * @param dir the Direction of movement.
   * @throws IllegalArgumentException if move is invalid.
   */
  public void moveChap(Direction dir) {
    if (failed) {
      throw new IllegalStateException("Chap has failed.");
    }
    if (won) {
      throw new IllegalStateException("Chap has won.");
    }
    Point newPos = getAdjacentPoint(chap.getPosition(), dir);
    //Regardless of whether Chap is actually supposed to move, his facing should change.
    this.chap.setDirection(dir);
    TileType targetTile;
    try {
      targetTile = maze.getTiles()[newPos.y][newPos.x];
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new IllegalArgumentException("Can't move out of bounds.");
    }
    switch (targetTile) {
      case FREE, INFO -> {
      }
      case WALL -> throw new IllegalArgumentException("Impassable wall.");
      case RED_KEY, BLUE_KEY, GREEN_KEY, YELLOW_KEY -> {
        notifyObservers(EventType.PICKUP_ITEM, targetTile);
        maze.setTile(newPos.y, newPos.x, TileType.FREE);
        chap.addKey(targetTile);
      }
      case RED_DOOR -> {
        if (chap.hasKey(TileType.RED_KEY)) {
          notifyObservers(EventType.UNLOCK_DOOR, TileType.RED_DOOR);
          maze.setTile(newPos.y, newPos.x, TileType.FREE);
          chap.removeKey(TileType.RED_KEY);
        } else {
          notifyObservers(EventType.LOCKED_DOOR, TileType.RED_DOOR);
          throw new IllegalArgumentException("Can't unlock this red door.");
        }
      }
      case BLUE_DOOR -> {
        if (chap.hasKey(TileType.BLUE_KEY)) {
          notifyObservers(EventType.UNLOCK_DOOR, TileType.BLUE_DOOR);
          maze.setTile(newPos.y, newPos.x, TileType.FREE);
          chap.removeKey(TileType.BLUE_KEY);
        } else {
          notifyObservers(EventType.LOCKED_DOOR, TileType.BLUE_DOOR);
          throw new IllegalArgumentException("Can't unlock this blue door.");
        }
      }
      case GREEN_DOOR -> {
        if (chap.hasKey(TileType.GREEN_KEY)) {
          notifyObservers(EventType.UNLOCK_DOOR, TileType.GREEN_DOOR);
          maze.setTile(newPos.y, newPos.x, TileType.FREE);
          chap.removeKey(TileType.GREEN_KEY);
        } else {
          notifyObservers(EventType.LOCKED_DOOR, TileType.GREEN_DOOR);
          throw new IllegalArgumentException("Can't unlock this green door.");
        }
      }
      case YELLOW_DOOR -> {
        if (chap.hasKey(TileType.YELLOW_KEY)) {
          notifyObservers(EventType.UNLOCK_DOOR, TileType.YELLOW_DOOR);
          maze.setTile(newPos.y, newPos.x, TileType.FREE);
          chap.removeKey(TileType.YELLOW_KEY);
        } else {
          notifyObservers(EventType.LOCKED_DOOR, TileType.YELLOW_DOOR);
          throw new IllegalArgumentException("Can't unlock this yellow door.");
        }
      }
      case TREASURE -> {
        notifyObservers(EventType.PICKUP_ITEM, TileType.TREASURE);
        maze.setTile(newPos.y, newPos.x, TileType.FREE);
        treasureRemaining--;
      }
      case EXIT_LOCK -> {
        if (treasureRemaining > 0) {
          throw new IllegalArgumentException("Can't unlock this red door.");
        }
        maze.setTile(newPos.y, newPos.x, TileType.FREE);
      }
      case EXIT -> {
        this.won = true;
        notifyObservers(EventType.WIN, TileType.FREE);
      }
      default -> throw new IllegalArgumentException("Unhandled TileType in movement");
    }
    this.chap.setPosition(newPos.x, newPos.y);
    if (enemyCollision()) {
      this.failed = true;
      return;
    }
    moveEnemies();
    if (enemyCollision()) {
      this.failed = true;
      notifyObservers(EventType.DEATH, TileType.FREE);
    }

  }

  private void moveEnemies() {
    for (EnemyActor enemyActor : enemyActorList) {
      moveEnemy(enemyActor);
    }
  }

  private void moveEnemy(EnemyActor enemyActor) {
    Point newPos = getAdjacentPoint(enemyActor.getPosition(), enemyActor.getMove(this));
    TileType targetTile;
    try {
      targetTile = maze.getTiles()[newPos.y][newPos.x];
    } catch (ArrayIndexOutOfBoundsException e) {
      // Must silently fail here
      return;
    }
    switch (targetTile) {
      case FREE, INFO -> {
      }
      case WALL, TREASURE, EXIT_LOCK, EXIT, BLUE_KEY, RED_KEY, YELLOW_KEY, GREEN_KEY, BLUE_DOOR,
          RED_DOOR, YELLOW_DOOR, GREEN_DOOR -> {
        return;
      }
      default -> throw new IllegalStateException("Unexpected value: " + targetTile);
    }
    enemyActor.setPosition(newPos.x, newPos.y);
  }

  /**
   * Collision method for enemy actors.
   *
   * @return true if Chap has the same position as an EnemyActor, false otherwise.
   */
  private boolean enemyCollision() {
    for (EnemyActor enemyActor : enemyActorList) {
      if (enemyActor.getPosition().equals(chap.getPosition())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Getter for the current levelnum.
   *
   * @return current level number.
   */
  public int getLevelNumber() {
    return levelNumber;
  }


  private Point getAdjacentPoint(Point original, Direction dir) {

    return switch (dir) {
      case UP -> new Point(original.x, original.y - 1);
      case DOWN -> new Point(original.x, original.y + 1);
      case LEFT -> new Point(original.x - 1, original.y);
      case RIGHT -> new Point(original.x + 1, original.y);
      //In normal cases, should not trigger, as only these 4 enum values exist.
    };
  }

  /**
   * Getter method for the maze's tiles.
   *
   * @return 2d Array of the mazes Tile objects.
   */
  public TileType[][] getTiles() {
    return maze.getTiles();
  }

  /**
   * Getter method for chap object.
   *
   * @return chap object
   */
  public Chap getChap() {
    return chap;
  }

  /**
   * Getter method for enemies.
   *
   * @return unmodifiable list of enemies
   */
  public List<EnemyActor> getEnemyActorList() {
    return Collections.unmodifiableList(enemyActorList);
  }

  /**
   * Getter for checking if Info should be displayed.
   *
   * @return true if Chap is on a tile of type INFO, false otherwise.
   */
  public boolean isOnInfo() {
    Point chapPos = chap.getPosition();
    return maze.getTiles()[chapPos.y][chapPos.x] == TileType.INFO;
  }

  /**
   * Getter for info.
   *
   * @return infotext for the currently loaded level.
   */
  public String getInfo() {
    return info;
  }


  /**
   * Returns whether or not chap has won by reaching the exit.
   *
   * @return true if chap has won, false otherwise
   */
  public boolean getWon() {
    return won;
  }

  /**
   * Returns whether or not chap has failed: been eaten, etc.
   *
   * @return true if Chap has failed, false otherwise.
   */
  public boolean getFailed() {
    return failed;
  }

  //FUNCTIONS FOR JUNIT TESTS

  /**
   * Setter for the failure variable.
   *
   * @param set failed's new value.
   */
  public void setFailed(boolean set) {
    this.failed = set;
  }

  /**
   * Setter for the win variable.
   *
   * @param set won's new value.
   */
  public void setWon(boolean set) {
    this.won = set;
  }

  /**
   * Getter for the maze variable.
   *
   * @return the current maze
   */
  public Maze getMaze() {
    return this.maze;
  }

  /**
   * Setter for the maze variable.
   *
   * @param m the new Maze.
   */
  public void setMaze(Maze m) {
    this.maze = m;
  }

  /**
   * Getter for the remaining treasure variable.
   *
   * @return the amount of treasure remaining
   */
  public int getTreasureRemaining() {
    return this.treasureRemaining;
  }

  /**
   * Getter for the number of keys the player has collected.
   *
   * @return the number of keys
   */
  public int getKeysCollected() {
    return getChap().getKeys().size();
  }

}
