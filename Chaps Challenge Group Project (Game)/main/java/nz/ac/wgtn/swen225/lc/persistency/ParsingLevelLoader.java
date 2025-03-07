package nz.ac.wgtn.swen225.lc.persistency;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nz.ac.wgtn.swen225.lc.domain.Chap;
import nz.ac.wgtn.swen225.lc.domain.Domain;
import nz.ac.wgtn.swen225.lc.domain.EnemyActor;
import nz.ac.wgtn.swen225.lc.domain.TileType;

/**
 * Handles initial loading of base level files from json.
 * GameLoaderImp and GameSaverImp handle loading and saving user-created savegames.
 */
public class ParsingLevelLoader implements LevelLoader {

  /**
   * Legacy code, used to produce an example level json file.
   *
   * @return String for level json
   */
  @Deprecated
  public static String writeDefaultMazeJson() {
    StringBuilder retS = new StringBuilder();
    retS.append("{\n").append("  \"levelnum\" : 1,").append("  \"info\" : \"testInfo\",")
        .append("  \"tiles\" : [\n");
    TileType[][] maze = new TileType[9][9];
    for (int i = 0; i < 9; i++) {
      for (int j = 0; j < 9; j++) {
        maze[i][j] = TileType.FREE;
      }
    }

    for (int i = 0; i < TILE_TYPES.length; i++) {
      int x = i % 9;
      int y = (i - i % 9) / 9;
      maze[y][x] = TILE_TYPES[i];
    }
    StringBuilder mazeStr = new StringBuilder();
    for (int i = 0; i < 9; i++) {
      mazeStr.append("    [");
      mazeStr.append(maze[i][0].ordinal());
      for (int j = 1; j < 9; j++) {
        mazeStr.append(',');
        mazeStr.append(maze[i][j].ordinal());
      }
      mazeStr.append("],\n");
    }
    retS.append(mazeStr.substring(0, mazeStr.length() - 2));


    retS.append("],\n").append("  \"numRows\" : 9,\n").append("  \"numCols\" : 9,\n")
        .append("  \"chap\" : {\n").append("    \"position\" : {\n").append("      \"x\" : 4,\n")
        .append("      \"y\" : 4\n").append("    }").append("  },\n")
        .append("  \"entities\" : [],\n").append("  \"inventory\" : [")
        .append(TileType.GREEN_KEY.ordinal()).append("],\n").append("  \"isAlive\" : true\n")
        .append("}");
    return retS.toString();
  }

  /**
   * Used for converting ordinal values into enum values.
   */
  private static final TileType[] TILE_TYPES = TileType.values();

  /**
   * Used for converting ordinal values into enum values.
   */
  private static final String[] EXPECTED_FIELDS = {
      "levelNum", "info", "tiles", "numRows", "numCols", "chap", "inventory", "isAlive",
      "entities"
  };

  @Override
  public void loadLevel(Domain domain, int levelNumber) throws IOException {
    if (levelNumber <= 0) {
      throw new IllegalArgumentException("Levels must have a positive index.");
    }
    JsonNode levelJsonRoot = FileHandler.loadLevelJsonNode("levels/level" + levelNumber + ".json");
    for (String expectedField : EXPECTED_FIELDS) {
      if (!levelJsonRoot.hasNonNull(expectedField)) {
        throw new JsonParseException("Couldn't find top-level field " + expectedField);
      }
    }

    //Validate level num

    if (!levelJsonRoot.get("levelNum").isIntegralNumber()) {
      throw new JsonParseException("Expected int for field \"levelNum\"");
    }
    if (levelNumber != levelJsonRoot.get("levelNum").asInt(-1)) {
      throw new JsonParseException(
          String.format("Level numbers don't match: expected %d, got %d", levelNumber,
              levelJsonRoot.get("levelNum").asInt(-1)));
    }

    //Load Info
    final JsonNode infoNode = levelJsonRoot.get("info");
    final String infoString = parseInfo(infoNode);

    //Load Tiles
    int rows = parseNumRows(levelJsonRoot.get("numRows"));
    int cols = parseNumCols(levelJsonRoot.get("numCols"));

    JsonNode tileGridRoot = levelJsonRoot.get("tiles");
    final TileType[][] mazeTiles = parseTileTypes(tileGridRoot, rows, cols);


    //load Chap, Keys, and
    JsonNode chapNode = levelJsonRoot.get("chap");
    final Chap chap = parseChap(chapNode);

    //load keys
    JsonNode inventoryNode = levelJsonRoot.get("inventory");
    final List<TileType> keys = parseKeys(inventoryNode);

    //load alive status
    JsonNode aliveNode = levelJsonRoot.get("isAlive");
    if (!aliveNode.isBoolean()) {
      throw new JsonParseException("Expected boolean for field \"isAlive\"");
    }

    // load Enemy Entities
    //TODO entities
    List<EnemyActor> enemies = new ArrayList<>();
    JsonNode enemiesRoot = levelJsonRoot.get("entities");

    if (!enemiesRoot.isArray()) {
      throw new JsonParseException("Expected Array for field \"entities\"");
    }
    // Check if we need to load any Enemies
    if (enemiesRoot.size() > 0) {

      //Run through children first to assemble list of names? or
      Map<String, Class> enemyNameMap = new HashMap<>();

      // Load the jar file for that level
      URL jarUrl =
          getClass().getClassLoader().getResource("levels/level" + levelNumber + ".jar");

      // Load classes from jar
      URL[] urls = new URL[] {jarUrl};
      try (URLClassLoader classLoader = new URLClassLoader(urls)) {

        // Create Instances of Enemy classes
        for (JsonNode child : enemiesRoot) {
          if (!child.isContainerNode()) {
            throw new JsonParseException("Expected container nodes in enemy array");
          }
          //Get Type of Entity
          if (!child.has("class")) {
            throw new JsonParseException("Expected name field in entity object.");
          }
          JsonNode childClassNode = child.get("class");
          if (!childClassNode.isTextual()) {
            throw new JsonParseException("Expected string in class field.");
          }
          String n = childClassNode.asText();

          if (!enemyNameMap.containsKey(n)) {
            Class<?> c = Class.forName("nz.ac.wgtn.swen225.lc.persistency." + n, true, classLoader);
            enemyNameMap.put(n, c);
          }

          // Get position of Entity
          JsonNode childPosNode = child.get("position");
          if (!childPosNode.isContainerNode()) {
            throw new JsonParseException("Expected container node inside position");
          }
          Object[] posArgs = {childPosNode.get("x").asInt(), childPosNode.get("y").asInt()};

          // Construct Entity (EnemyActor)
          EnemyActor e =
              (EnemyActor) enemyNameMap.get(n).getDeclaredConstructor(int.class, int.class)
                  .newInstance(posArgs);
          enemies.add(e);
        }
      } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
               | NoSuchMethodException | InvocationTargetException e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      }
    }


    domain.buildNewLevel(mazeTiles, chap, enemies, keys, infoString, levelNumber);
  }

  private String parseInfo(JsonNode infoNode) throws JsonParseException {
    if (!infoNode.isTextual()) {
      throw new JsonParseException("Info field should contain text.");
    }
    return infoNode.asText("ERROR");
  }

  private int parseNumRows(JsonNode numRowsNode) throws JsonParseException {
    if (!numRowsNode.isIntegralNumber()) {
      throw new JsonParseException("Expected int for field \"numRows\"");
    }
    int rows = numRowsNode.asInt(-1);

    if (rows <= 0) {
      throw new JsonParseException("Must have a positive number of rows.");
    }

    return rows;
  }

  private int parseNumCols(JsonNode numColsNode) throws JsonParseException {
    if (!numColsNode.isIntegralNumber()) {
      throw new JsonParseException("Expected int for field \"numCols\"");
    }
    int cols = numColsNode.asInt(-1);

    if (cols <= 0) {
      throw new JsonParseException("Must have a positive number of rows.");
    }

    return cols;
  }

  private TileType[][] parseTileTypes(JsonNode tileGridRoot, int rows, int cols)
      throws JsonParseException {

    TileType[][] mazeTiles = new TileType[rows][cols];

    if (!tileGridRoot.isArray()) {
      throw new JsonParseException("Expected 2D array in \"tiles\" field.");
    }
    if (tileGridRoot.size() != rows) {
      throw new JsonParseException("\"numRows\" and the size of \"tiles\" don't match.");
    }

    for (int i = 0; i < rows; i++) {
      JsonNode rowNode = tileGridRoot.get(i);

      if (!rowNode.isArray()) {
        throw new JsonParseException("Expected 2D array in \"tiles\" field.");
      }
      if (rowNode.size() != cols) {
        throw new JsonParseException("\"numCols\" and the size of \"tiles\" don't match.");
      }

      for (int j = 0; j < cols; j++) {
        JsonNode tileNode = rowNode.get(j);

        if (!tileNode.isIntegralNumber()) {
          throw new JsonParseException(
              "Expected int (to be parsed as enum) for value in \"tiles\" field at " + "(" + j
                  + "," + i + ").");
        }

        int tileVal = tileNode.asInt();

        if (tileVal < 0 || tileVal > TILE_TYPES.length) {
          throw new JsonParseException(
              "Out of bound TileType integer at " + "(" + j + "," + i + ").");
        }

        mazeTiles[i][j] = TILE_TYPES[tileVal];
      }

    }

    return mazeTiles;
  }

  private Chap parseChap(JsonNode chapNode) throws JsonParseException {
    if (!chapNode.hasNonNull("position")) {
      throw new JsonParseException("Couldn't find chap's position field.");
    }
    JsonNode posNode = chapNode.get("position");
    if (!posNode.hasNonNull("x")) {
      throw new JsonParseException("Couldn't find chap's x position field.");
    }
    if (!posNode.get("x").isIntegralNumber()) {
      throw new JsonParseException("Chap's x position field must be an integer.");
    }
    int chapX = posNode.get("x").asInt();
    if (!posNode.hasNonNull("y")) {
      throw new JsonParseException("Couldn't find chap's y position field.");
    }
    if (!posNode.get("y").isIntegralNumber()) {
      throw new JsonParseException("Chap's y position field must be an integer.");
    }
    int chapY = posNode.get("y").asInt();
    Chap ret = new Chap(chapX, chapY);
    return ret;
  }

  private List<TileType> parseKeys(JsonNode inventoryNode) throws JsonParseException {
    List<TileType> ret = new ArrayList<>();
    if (!inventoryNode.isArray()) {
      throw new JsonParseException("Expected array in \"inventory\" field.");
    }
    for (int i = 0; i < inventoryNode.size(); i++) {
      JsonNode keyNode = inventoryNode.get(i);
      if (!keyNode.isIntegralNumber()) {
        throw new JsonParseException(
            "Expected int (to be parsed as enum) in inventory field at " + "index " + i + ".");
      }
      int keyVal = keyNode.asInt();
      if (keyVal < 0 || keyVal > TILE_TYPES.length) {
        throw new JsonParseException("Invalid TileType ordinal at index " + i + "in inventory.");
      }
      TileType keyType = TILE_TYPES[keyVal];
      if (keyType != TileType.BLUE_KEY && keyType != TileType.RED_KEY
          && keyType != TileType.GREEN_KEY && keyType != TileType.YELLOW_KEY) {
        throw new JsonParseException("Found non-key ordinal in inventory.");
      }
      ret.add(keyType);
    }
    return ret;
  }
}


