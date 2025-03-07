package test.nz.ac.wgtn.swen225.lc.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import nz.ac.wgtn.swen225.lc.domain.Chap;
import nz.ac.wgtn.swen225.lc.domain.Direction;
import nz.ac.wgtn.swen225.lc.domain.Domain;
import nz.ac.wgtn.swen225.lc.domain.EnemyActor;
import nz.ac.wgtn.swen225.lc.domain.Maze;
import nz.ac.wgtn.swen225.lc.domain.TileType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests Domain's in-game logic.
 *
 * @author Riley West (300608942).
 * @author Jebadiah (300629357).
 */
public class DomainTest {
  private Domain domain;

  /**
   * Setup method.
   */
  @BeforeEach
  public void setUp() {
    domain = new Domain();
  }

  /**
   * Test moving Chap to a valid position.
   */
  @Test
  public void testMoveChapValidMove() {
    assertDoesNotThrow(() -> domain.moveChap(Direction.RIGHT));
    assertEquals(1, domain.getChap().getPosition().x);
    assertEquals(0, domain.getChap().getPosition().y);
  }

  /**
   * Test moving Chap out of bounds.
   */
  @Test
  public void testMoveChapInvalidMove() {
    assertThrows(IllegalArgumentException.class, () -> domain.moveChap(Direction.LEFT));
  }

  /**
   * Test moving Chap when in a failed state.
   */
  @Test
  public void testMoveChapFailedState() {
    domain.setFailed(true);

    assertThrows(IllegalStateException.class, () -> domain.moveChap(Direction.RIGHT));
  }

  /**
   * Test moving Chap when in a succeeded state.
   */
  @Test
  public void testMoveChapWonState() {
    domain.setWon(true);

    assertThrows(IllegalStateException.class, () -> domain.moveChap(Direction.RIGHT));
  }


  /**
   * Test that Chap can win.
   */
  @Test
  public void testGetWon() {
    Maze tester = domain.getMaze();
    tester.setTile(0, 1, TileType.EXIT);
    domain.setMaze(tester);

    domain.moveChap(Direction.RIGHT);
    assertTrue(domain.getWon());
  }

  /**
   * Test that Chap can pick up keys.
   */
  @Test
  public void pickupTest() {
    Maze tester = domain.getMaze();
    tester.setTile(0, 1, TileType.BLUE_KEY);
    domain.setMaze(tester);

    domain.moveChap(Direction.RIGHT);
    assertTrue(domain.getChap().hasKey(TileType.BLUE_KEY));
  }


  /**
   * Test that Chap can unlock doors.
   */
  @Test
  public void doorUnlockTest() {
    Maze tester = domain.getMaze();
    tester.setTile(0, 1, TileType.BLUE_KEY);
    tester.setTile(0, 2, TileType.BLUE_DOOR);
    domain.setMaze(tester);

    domain.moveChap(Direction.RIGHT);
    domain.moveChap(Direction.RIGHT);
    assertFalse(domain.getChap().hasKey(TileType.BLUE_KEY));
  }

  /**
   * Test that chap can pick up treasure.
   */
  @Test
  public void treasurePickupTest() {
    TileType[][] maze = domain.getTiles();
    maze[0][1] = TileType.TREASURE;

    domain.buildNewLevel(maze, domain.getChap(), domain.getEnemyActorList(),
                         domain.getChap().getKeys(), "Y", 0
    );
    int inittreasure = domain.getTreasureRemaining();

    domain.moveChap(Direction.RIGHT);
    assertEquals(inittreasure - 1, domain.getTreasureRemaining());
  }

  /**
   * Tests that Domain properly detects Info tiles.
   */
  @Test
  public void infoTileTest() {
    TileType[][] maze = domain.getTiles();
    maze[0][1] = TileType.INFO;
    String infoText = Double.toString(Math.random());
    domain.buildNewLevel(maze, domain.getChap(), domain.getEnemyActorList(),
                         domain.getChap().getKeys(), infoText, 0
    );

    domain.moveChap(Direction.RIGHT);
    assertTrue(domain.isOnInfo());
    assertEquals(domain.getInfo(), infoText);

  }

  /**
   * Tests that enemies can kill Chap.
   */
  @Test
  public void enemyKillTest() {
    TileType[][] maze = domain.getTiles();
    int initialTreasure = domain.getTreasureRemaining();
    Chap newChap = new Chap(0, 0);
    DomainTestActor killer = new DomainTestActor(1, 1);

    List<EnemyActor> enemyList = List.of(killer);
    domain.buildNewLevel(maze, newChap, enemyList,
                         domain.getChap().getKeys(), "Y", 0
    );
    assertEquals(initialTreasure, domain.getTreasureRemaining());

    System.out.println(domain.getChap().getPosition());
    System.out.println(domain.getEnemyActorList().get(0).getPosition());

    domain.moveChap(Direction.RIGHT);

    System.out.println(domain.getChap().getPosition());
    System.out.println(domain.getEnemyActorList().get(0).getPosition());

    assertTrue(domain.getFailed());
    assertFalse(domain.getWon());
    for (Direction dir : Direction.values()) {
      assertThrows(IllegalStateException.class, () -> domain.moveChap(dir));
    }
  }
}
