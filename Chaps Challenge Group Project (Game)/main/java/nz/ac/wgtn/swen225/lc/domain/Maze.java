package nz.ac.wgtn.swen225.lc.domain;

/**
 * Handles the generation of the level and most of the logic for the game. Should ONLY be handled
 * directly by Domain and Persistency.
 * TODO: export a lot of this to Domain. Maze != game logic.
 * TODO: run spotbugs, do a bunch of immutability.
 *
 * @author Riley West (300608942).
 * @author Jebadiah (300629357).
 */
public class Maze {
  private TileType[][] tiles;
  private int numRows;
  private int numCols;

  /**
   * "Normal" constructor.
   *
   * @param numRows row count of the maze.
   * @param numCols column count of the maze.
   */
  public Maze(int numRows, int numCols) {
    this.numRows = numRows;
    this.numCols = numCols;
    this.tiles = new TileType[numRows][numCols];
  }

  //GETTER METHODS

  /**
   * Getter method for the maze tiles.
   *
   * @return 2d Array of the maze
   */
  public TileType[][] getTiles() {
    TileType[][] ret = new TileType[numRows][numCols];
    for (int row = 0; row < numRows; row++) {
      for (int col = 0; col < numCols; col++) {
        ret[row][col] = tiles[row][col];
      }
    }
    return ret;
  }

  /**
   * Getter method for number of rows in a maze.
   *
   * @return Number of rows
   */
  public int getNumRows() {
    return numRows;
  }

  /**
   * Getter method for number of columns in the maze.
   *
   * @return Number of columns
   */
  public int getNumCols() {
    return numCols;
  }

  // SETTER METHODS

  /**
   * Setter for the tiles of the maze grid.
   *
   * @param tiles new tile grid array.
   */
  public void setTiles(TileType[][] tiles) {
    int rows = tiles.length;
    int cols = tiles[0].length;
    for (int i = 0; i < rows; i++) {
      if (tiles[i].length != cols) {
        throw new IllegalArgumentException("Rows must all be of the same size.");
      }
    }
    TileType[][] ourTiles = new TileType[rows][cols];
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        ourTiles[row][col] = tiles[row][col];
      }
    }
    this.tiles = ourTiles;
    this.numCols = cols;
    this.numRows = rows;
  }

  /**
   * Setter for a single tile of the maze.
   *
   * @param row  row to place tile on.
   * @param col  column to place tile at.
   * @param tile Tile to place.
   */
  public void setTile(int row, int col, TileType tile) {
    this.tiles[row][col] = tile;
  }

  //OTHER METHODS

  /**
   * Utility method to fill the grid with blank tiles.
   */
  public void generateEmptyMaze() {
    for (int i = 0; i < numRows; i++) {
      for (int j = 0; j < numCols; j++) {
        tiles[i][j] = TileType.FREE;
      }
    }
  }
}
