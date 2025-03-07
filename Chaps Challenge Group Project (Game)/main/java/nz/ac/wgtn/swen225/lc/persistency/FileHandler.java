package nz.ac.wgtn.swen225.lc.persistency;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

/**
 * Handles file operations for saving and loading levels.
 *
 * @author Benjamin Park
 */
public class FileHandler {
  /**
   * Reads a file to a String.
   *
   * @param fileName index of the level to be loaded.
   * @return String representing the file.
   */
  public static String loadLevelJson(URI fileName) {
    StringBuilder ret = new StringBuilder();
    try {
      File reader = new File(fileName);
      Scanner scanner = new Scanner(reader, "UTF-8");
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        ret.append(line).append('\n');
      }
    } catch (FileNotFoundException e) {
      throw new IllegalArgumentException(e);
    }
    return ret.toString();
  }

  /**
   * Reads a file to a JsonNode.
   *
   * @param fileName index of the level to be loaded.
   * @return JsonNode representing the file.
   */
  public static JsonNode loadLevelJsonNode(String fileName) {
    JsonNode ret;
    try {
      URI u = FileHandler.class.getClassLoader().getResource(fileName).toURI();
      File reader = new File(u);
      ObjectMapper mapper = new ObjectMapper();
      ret = mapper.readTree(reader);
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException(e);
    }
    return ret;
  }
}

