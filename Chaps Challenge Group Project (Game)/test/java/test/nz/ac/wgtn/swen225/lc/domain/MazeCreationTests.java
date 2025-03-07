package test.nz.ac.wgtn.swen225.lc.domain;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import nz.ac.wgtn.swen225.lc.domain.Chap;
import nz.ac.wgtn.swen225.lc.domain.Domain;
import nz.ac.wgtn.swen225.lc.domain.EnemyActor;
import nz.ac.wgtn.swen225.lc.domain.Maze;
import nz.ac.wgtn.swen225.lc.domain.TileType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests domain's BuildNewLevel method, which is used for loading levels and gamestates.
 *
 * @author Riley West (300608942).
 * @author Jebadiah (300629357).
 */
public class MazeCreationTests {

  private TileType[][] insertionGrid;
  private Domain domain;

  /**
   * Setup function.
   */
  @BeforeEach
  public void setUp() {
    domain = new Domain();
    insertionGrid = new Maze(7, 9).getTiles();
  }

  /**
   * Tests the creation of a maze without enemies and a single wall.
   */
  @Test
  public void testValidCreation() {
    insertionGrid[0][3] = TileType.WALL;
    Chap ourChap;
    ourChap = new Chap(0, 1);
    List<EnemyActor> enemies;
    enemies = new ArrayList<>();
    enemies.add(new DomainTestActor(1, 0));
    List<TileType> keys = new ArrayList<>();
    keys.add(TileType.RED_KEY);
    Assertions.assertEquals(new Point(0, 0), domain.getChap().getPosition());
    Assertions.assertTrue(domain.getEnemyActorList().isEmpty());
    domain.buildNewLevel(insertionGrid, ourChap, enemies, keys, "Short", 0);

    for (int row = 0; row < insertionGrid.length; row++) {
      for (int col = 0; col < insertionGrid[row].length; col++) {
        Assertions.assertEquals(insertionGrid[row][col], domain.getTiles()[row][col]);
      }
    }

    Assertions.assertEquals(new Point(0, 1), domain.getChap().getPosition());
    Assertions.assertTrue(domain.getChap().hasKey(TileType.RED_KEY));
  }

  /**
   * Tests the creation of various invalid mazes.
   */
  @Test
  public void testInvalidCreation() {
    insertionGrid[0][3] = TileType.WALL;
    Chap ourChap;
    ourChap = new Chap(0, 1);
    List<EnemyActor> enemies;
    enemies = new ArrayList<>();
    enemies.add(new DomainTestActor(1, 0));
    List<TileType> keys;
    keys = new ArrayList<>();
    keys.add(TileType.RED_KEY);
    domain = new Domain();

    //Badly shaped grid
    TileType[][] badGrid = new TileType[3][];
    for (int i = 0; i < 2; i++) {
      badGrid[i] = new TileType[3];
      for (int j = 0; j < 3; j++) {
        badGrid[i][j] = TileType.FREE;
      }
    }
    badGrid[2] = new TileType[1];
    badGrid[2][0] = TileType.EXIT;
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> domain.buildNewLevel(badGrid, ourChap, enemies, keys, "Y", 0)
    );

    //Invalid chap position
    Chap badChap = new Chap(99, 99);
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> domain.buildNewLevel(insertionGrid, badChap, enemies, keys, "Y", 0)
    );

    //Invalid enemy position
    enemies.add(new DomainTestActor(99, 99));
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> domain.buildNewLevel(insertionGrid, ourChap, enemies, keys, "Y", 0)
    );
    enemies.remove(enemies.size() - 1);

    //Non-Key passed into keys
    keys.add(TileType.WALL);
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> domain.buildNewLevel(insertionGrid, ourChap, enemies, keys, "Y", 0)
    );
  }
}
