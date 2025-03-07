package test.nz.ac.wgtn.swen225.lc.persistency;

import java.util.Arrays;
import nz.ac.wgtn.swen225.lc.domain.Domain;
import nz.ac.wgtn.swen225.lc.domain.Maze;
import nz.ac.wgtn.swen225.lc.domain.TileType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import org.junit.jupiter.api.Test;

import nz.ac.wgtn.swen225.lc.persistency.*;
import java.io.File;

public class PersistencyTest {
  @Test
  public void saveGame() {
    Domain domain = new Domain();
    GameSaverImp gameSaverImp = new GameSaverImp();
    File file = new File("test.json");
    try {
      gameSaverImp.saveGame(domain, 1, file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void loadGame() {
    Domain domain = new Domain();
    GameLoaderImp gameLoaderImp = new GameLoaderImp();
    File file = new File("test.json");
    try {
      gameLoaderImp.loadGame(domain, file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void loadLevel1() {
    Domain dom = new Domain();
    LevelLoader l = new ParsingLevelLoader();
    try {
      l.loadLevel(dom, 1);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void loadLevel2() {
    Domain dom = new Domain();
    LevelLoader l = new ParsingLevelLoader();
    try {
      l.loadLevel(dom, 2);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
