package nz.ac.wgtn.swen225.lc.renderer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JPanel;
import nz.ac.wgtn.swen225.lc.domain.Direction;
import nz.ac.wgtn.swen225.lc.domain.Domain;
import nz.ac.wgtn.swen225.lc.domain.DomainObserver;
import nz.ac.wgtn.swen225.lc.domain.EventType;
import nz.ac.wgtn.swen225.lc.domain.TileType;

/**
 * Renderer.java
 * Handles the rendering of the game.
 *
 * @author Leory Xue (300607821)
 */
public class Renderer extends JPanel implements DomainObserver {
  private static final HashMap<Direction, Sprite> CHAP_SPRITES = new HashMap<>();
  private static final HashMap<Direction, Sprite> ENEMY_SPRITES = new HashMap<>();
  private static final HashMap<TileType, Sprite> TILE_SPRITES = new HashMap<>();
  private static final int FOCUS_SIZE = 9;  //9x9 focus area
  private int gridSize = 0;
  private final Domain domain;
  private final Sound sound = new Sound();
  private final ArrayList<TileType> chapInventory = new ArrayList<>();

  /**
   * Constructor for the Renderer class.
   *
   * @param domain The domain to be rendered.
   */
  public Renderer(Domain domain) {
    this.domain = domain;
    domain.addObserver(this);
    sound.playBackgroundMusic();
    gridSize = domain.getTiles().length;
    chapInventory.clear();
    setPreferredSize(new Dimension(FOCUS_SIZE * 50, (FOCUS_SIZE + 1) * 50 + 25));
  }

  static {
    TILE_SPRITES.put(TileType.WALL, Sprite.wallTile);
    TILE_SPRITES.put(TileType.FREE, Sprite.freeTile1);
    TILE_SPRITES.put(TileType.RED_DOOR, Sprite.redLock);
    TILE_SPRITES.put(TileType.RED_KEY, Sprite.redKey);
    TILE_SPRITES.put(TileType.BLUE_DOOR, Sprite.blueLock);
    TILE_SPRITES.put(TileType.BLUE_KEY, Sprite.blueKey);
    TILE_SPRITES.put(TileType.GREEN_DOOR, Sprite.greenLock);
    TILE_SPRITES.put(TileType.GREEN_KEY, Sprite.greenKey);
    TILE_SPRITES.put(TileType.YELLOW_DOOR, Sprite.yellowLock);
    TILE_SPRITES.put(TileType.YELLOW_KEY, Sprite.yellowKey);
    TILE_SPRITES.put(TileType.TREASURE, Sprite.treasure);
    TILE_SPRITES.put(TileType.INFO, Sprite.info);
    TILE_SPRITES.put(TileType.EXIT, Sprite.exit);
    TILE_SPRITES.put(TileType.EXIT_LOCK, Sprite.exitLock);

    CHAP_SPRITES.put(Direction.UP, Sprite.chapU);
    CHAP_SPRITES.put(Direction.DOWN, Sprite.chapD);
    CHAP_SPRITES.put(Direction.LEFT, Sprite.chapL);
    CHAP_SPRITES.put(Direction.RIGHT, Sprite.chapR);

    ENEMY_SPRITES.put(Direction.UP, Sprite.actorU);
    ENEMY_SPRITES.put(Direction.DOWN, Sprite.actorD);
    ENEMY_SPRITES.put(Direction.LEFT, Sprite.actorL);
    ENEMY_SPRITES.put(Direction.RIGHT, Sprite.actorR);
  }

  @Override
  protected void paintComponent(Graphics g) {
    gridSize = domain.getTiles().length;
    super.paintComponent(g);

    // First check if the game is over or won
    if (domain.getFailed()) {
      drawGameOverScreen(g, "Game Over");
    } else if (domain.getWon()) {
      drawGameOverScreen(g, "You Won!");

    } else {
      // Draw the game board and characters
      setBackground(new Color(152, 106, 147, 255));
      int cellWidth = getWidth() / FOCUS_SIZE;
      int cellHeight = (getHeight() - 25) / (FOCUS_SIZE + 1);
      // Calculate the top row and left column of the focus area
      int focusTopRow = Math.max(0, Math.min(domain.getChap().getPosition().y - (FOCUS_SIZE / 2), gridSize - FOCUS_SIZE));
      int focusLeftCol = Math.max(0, Math.min(domain.getChap().getPosition().x - (FOCUS_SIZE / 2), gridSize - FOCUS_SIZE));

      drawTiles(g, cellWidth, cellHeight, focusTopRow, focusLeftCol);
      drawCharacters(g, cellWidth, cellHeight, focusTopRow, focusLeftCol);
      drawInventory(g, cellWidth, cellHeight);

      if (domain.isOnInfo()) {
        drawInfoMessage(g);
      }
    }
  }

  /**
   * Draw the tiles in the focus area.
   *
   * @param g            Graphics
   * @param cellWidth    the width of each cell
   * @param cellHeight   the height of each cell
   * @param focusTopRow  the top row of the focus area
   * @param focusLeftCol the left column of the focus area
   */
  private void drawTiles(Graphics g, int cellWidth, int cellHeight, int focusTopRow, int focusLeftCol) {
    // Loop through the focus area and draw tiles
    for (int row = focusTopRow; row < focusTopRow + FOCUS_SIZE && row < gridSize; row++) {
      for (int col = focusLeftCol; col < focusLeftCol + FOCUS_SIZE && col < gridSize; col++) {
        TileType tile = domain.getTiles()[row][col];
        Sprite sprite = TILE_SPRITES.get(tile);

        if (sprite != null) {
          int drawX = (col - focusLeftCol) * cellWidth;
          int drawY = (row - focusTopRow) * cellHeight;
          g.drawImage(sprite.sprite, drawX, drawY, cellWidth, cellHeight, this);
        }
      }
    }
  }

  /**
   * Draw the game over screen.
   *
   * @param g Graphics
   */
  private void drawGameOverScreen(Graphics g, String message) {
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, getWidth(), getHeight());
    g.setColor(Color.WHITE);
    g.drawString(message, (getWidth() / 2)
        - (g.getFontMetrics().stringWidth(message) / 2), getHeight() / 2);
  }

  /**
   * Draw the info message.
   *
   * @param g Graphics
   */
  private void drawInfoMessage(Graphics g) {
    //draw black outline
    g.setColor(Color.BLACK);
    g.setFont(new Font("Dialog", Font.BOLD, 20));
    g.drawString(domain.getInfo(), (getWidth() / 2)
        - (g.getFontMetrics().stringWidth(domain.getInfo()) / 2), getHeight() / 2);
    g.setColor(Color.WHITE);
    g.setFont(new Font("Dialog", Font.BOLD, 20));
    g.drawString(domain.getInfo(), (getWidth() / 2)
        - (g.getFontMetrics().stringWidth(domain.getInfo()) / 2) - 1, getHeight() / 2 - 1);
  }

  /**
   * Draw the characters in the focus area.
   *
   * @param g            Graphics
   * @param cellWidth    the width of each cell
   * @param cellHeight   the height of each cell
   * @param focusTopRow  the top row of the focus area
   * @param focusLeftCol the left column of the focus area
   */
  private void drawCharacters(Graphics g, int cellWidth, int cellHeight, int focusTopRow, int focusLeftCol) {
    drawChap(g, cellWidth, cellHeight, focusTopRow, focusLeftCol);
    drawEnemies(g, cellWidth, cellHeight, focusTopRow, focusLeftCol);
  }

  /**
   * Draw Chap in the focus area.
   *
   * @param g            Graphics
   * @param cellWidth    the width of each cell
   * @param cellHeight   the height of each cell
   * @param focusTopRow  the top row of the focus area
   * @param focusLeftCol the left column of the focus area
   */
  private void drawChap(Graphics g, int cellWidth, int cellHeight, int focusTopRow, int focusLeftCol) {
    int chapDrawX = (domain.getChap().getPosition().x - focusLeftCol) * cellWidth;
    int chapDrawY = (domain.getChap().getPosition().y - focusTopRow) * cellHeight;
    Direction chapDirection = domain.getChap().getDirection();
    Sprite chapSprite = CHAP_SPRITES.get(chapDirection);
    g.drawImage(chapSprite.sprite, chapDrawX, chapDrawY, cellWidth, cellHeight, this);
  }

  /**
   * Draw the enemies in the focus area.
   *
   * @param g           Graphics
   * @param cellWidth  the width of each cell
   * @param cellHeight the height of each cell
   * @param focusTopRow the top row of the focus area
   * @param focusLeftCol the left column of the focus area
   */
  private void drawEnemies(Graphics g, int cellWidth, int cellHeight, int focusTopRow, int focusLeftCol) {
    for (int i = 0; i < domain.getEnemyActorList().size(); i++) {
      int enemyDrawX = (domain.getEnemyActorList().get(i).getPosition().x - focusLeftCol) * cellWidth;
      int enemyDrawY = (domain.getEnemyActorList().get(i).getPosition().y - focusTopRow) * cellHeight;
      Direction enemyDirection = domain.getEnemyActorList().get(i).getDirection();
      Sprite enemySprite = ENEMY_SPRITES.get(enemyDirection);
      g.drawImage(enemySprite.sprite, enemyDrawX, enemyDrawY, cellWidth, cellHeight, this);
    }
  }

  /**
   * Draw the inventory.
   *
   * @param g          Graphics
   * @param cellWidth  the width of each cell
   * @param cellHeight the height of each cell
   */
  private void drawInventory(Graphics g, int cellWidth, int cellHeight) {
    // Draw the inventory separator line
    g.setColor(new Color(53, 18, 46));
    g.fillRect(0, FOCUS_SIZE * cellHeight + 8, getWidth(), 10); // Add 8 to draw the line below the

    int lastRow = FOCUS_SIZE;
    for (int col = 0; col < FOCUS_SIZE; col++) {
      if (col < chapInventory.size()) {
        Sprite sprite = TILE_SPRITES.get(chapInventory.get(col));
        int drawX = col * cellWidth;
        int drawY = lastRow * cellHeight + 25;  // Add 25 to draw the inventory below the game
        g.drawImage(sprite.sprite, drawX, drawY, cellWidth, cellHeight, this);
      } else {
        // Empty inventory slots are drawn with a free tile
        TileType freeTile = TileType.FREE;
        Sprite freeTileSprite = TILE_SPRITES.get(freeTile);

        if (freeTileSprite != null) {
          int drawX = col * cellWidth;
          int drawY = lastRow * cellHeight + 25;  // Add 25 to draw the inventory below the game
          g.drawImage(freeTileSprite.sprite, drawX, drawY, cellWidth, cellHeight, this);
        }
      }
    }
  }

  /**
   * Update the Renderer.
   */
  public void updateRenderer() {
    ArrayList<TileType> chapInventory = domain.getChap().getKeys();
    updateChapInventory(chapInventory);
    repaint();
  }

  /**
   * Update chaps inventory.
   *
   * @param inventory the new inventory.
   */
  public void updateChapInventory(ArrayList<TileType> inventory) {
    chapInventory.clear();
    chapInventory.addAll(inventory);
  }

  /**
   * Implementation of the handleEvent method from DomainObserver.
   *
   * @param eventType The type of event.
   * @param itemType  The type of item.
   */
  @Override
  public void handleEvent(EventType eventType, TileType itemType) {
    switch (eventType) {
      case UNLOCK_DOOR:
        sound.playUnlockSound();
        break;
      case PICKUP_ITEM:
        sound.playPickupSound();
        break;
      case LOCKED_DOOR:
        sound.playLockedSound();
        break;
      case DAMAGE:
        sound.playDamageSound();
        break;
      case DEATH:
        sound.stopBackgroundMusic();
        sound.playDeathSound();
        break;
      case WIN:
        sound.stopBackgroundMusic();
        sound.playUnlockSound();
        break;
      case LEVEL_RESET:
        sound.playBackgroundMusic();
        break;
      default:
        break;
    }
  }
}
