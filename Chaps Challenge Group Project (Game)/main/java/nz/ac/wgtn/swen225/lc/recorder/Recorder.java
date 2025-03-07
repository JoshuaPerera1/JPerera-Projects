package nz.ac.wgtn.swen225.lc.recorder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JFileChooser;
import nz.ac.wgtn.swen225.lc.app.App;
import nz.ac.wgtn.swen225.lc.app.AppInput;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Recorder.java
 *
 * <p>Recorder class is used to record the game and replay it.
 * It will record the game and save it to a JSON file.
 *
 * @author Neeraj Patel (300604056).
 */
public class Recorder {
  // ----------------------------------- VARIABLES ----------------------------------- //
  private int replaySpeed; // 1 (fastest auto replay speed) to 10 (slowest auto replay speed)

  private HashMap<Integer, RecordItem> currentRecording;

  private HashMap<Integer, RecordItem> loadedRecording;

  private int currentSequenceNumber = 0;

  int manualReplayIndex;

  private final App app;


  // ----------------------------------- CONSTRUCTOR ----------------------------------- //

  /**
   * The constructor for the Recorder class.
   * Set initial values.
   */
  public Recorder(App inApp) {
    replaySpeed = 5;
    app = inApp;
    manualReplayIndex = 0;
  }

  // ----------------------------------- METHODS ----------------------------------- //

  /**
   * When called this will start recording and save the recording to a file.
   * Reset the current recording.
   *
   *
   * @param level - the level that the user is currently playing.
   */
  public void startRecording(String level) {
    // ---- Setup recording ---- //
    currentRecording = new HashMap<>();
    currentSequenceNumber = 0;

    // ---- Record the initial game ---- //
    RecordItem currentChange = new RecordItem(currentSequenceNumber, "START", level);
    currentRecording.put(currentSequenceNumber, currentChange);
    currentSequenceNumber++;
  }

  /**
   * When called this will add the data to the recording.
   *
   *
   * @param data - the data to added to the recording.
   */
  public void addToRecording(String data) {
    // ---- Check if the game has started ---- //
    if (currentRecording == null) {
      System.out.println("Error: No recording has been started.");
      return;
    }

    // ---- Split the data up ---- //
    String[] dataArray = data.split("\\|");
    String actor = dataArray[0].trim();

    // ---- Check type of data ---- //
    if (dataArray[0].equals("END")) {
      endRecording();
      return;
    }

    // ---- Record Player movement ---- //
    String move = dataArray[1].trim();
    RecordItem newItem = new RecordItem(currentSequenceNumber, actor, move);
    currentRecording.put(currentSequenceNumber, newItem);
    currentSequenceNumber++;
  }

  /**
   * When called this will load the recording from a fileChooser.
   *
   * @param fileFinder - the file to load the recording from.
   */
  public HashMap<Integer, RecordItem> loadRecordingJson(JFileChooser fileFinder) {
    HashMap<Integer, RecordItem> loadedRecording = new HashMap<>();
    try {
      // ---- Read the json file ---- //
      String filePath = fileFinder.getSelectedFile().getAbsolutePath();
      String jsonData = Files.readString(Paths.get(filePath));
      JSONObject json = new JSONObject(jsonData);

      // -- Break down the json data -- //
      for (String key : json.keySet()) {
        int sequenceNumber = Integer.parseInt(key);
        JSONObject recordData = json.getJSONObject(key);
        String move = recordData.getString("move");
        String actor = recordData.getString("actor");

        // -- Check if the game has ended -- //
        if (actor.equals("END")) {
          break;
        }

        // -- Add the data -- //
        RecordItem newRecordItem = new RecordItem(sequenceNumber, actor, move);
        loadedRecording.put(sequenceNumber, newRecordItem);
      }
    } catch (IOException | JSONException e) {
      System.out.println("Error: Loading Json file: " + e.getMessage());
    }
    return loadedRecording;
  }

  /**
   * When called this will end the recording and save the recording to a file.
   */
  private void endRecording() {
    // ---- Ask user the location and file name ---- //
    JFileChooser fileFinder = new JFileChooser();
    fileFinder.setDialogTitle("Select location to save for REPLAY:");

    // ---- Validate the file ---- //
    int userAction = fileFinder.showSaveDialog(null);
    if (userAction == JFileChooser.APPROVE_OPTION) {
      String filePath = fileFinder.getSelectedFile().getAbsolutePath();

      // ---- Save the current game in JSON format ---- //
      try {
        JSONObject gameJson = new JSONObject(currentRecording);
        Files.writeString(Paths.get(filePath), gameJson.toString());
      } catch (IOException e) {
        System.out.println("Error: Saving Json file: " + e.getMessage());
      }
    }
  }


  /**
   * When called this will replay the recording in steps when the user presses a button.
   */
  public void stepUpManualReplay(HashMap<Integer, RecordItem> toLoadRecording) {
    // ---- Load the game ---- //
    loadedRecording = toLoadRecording;
    if (loadedRecording.isEmpty()) {
      System.out.println("Error: No recording loaded.");
      return;
    }

    // ---- Initialise the level ---- //
    manualReplayIndex = 0;
    RecordItem currentRecord = loadedRecording.get(0);

    if (currentRecord.getActor().equals("START")) {
      app.newGame(Integer.parseInt(currentRecord.getMove()));
      manualReplayIndex = 1;
    } else {
      System.out.println("Error: No level loaded.");
    }
  }

  /**
   * When called this will replay the recording in steps when the user presses a button.
   */
  public void manualStepReplay() {
    // ---- Check there is something to replay ---- //
    if (loadedRecording == null) {
      System.out.println("Error: No recording loaded.");
      return;
    }

    // ---- Check if the game has ended ---- //
    if (manualReplayIndex >= loadedRecording.size()) {
      System.out.println("Error: End of replay.");
      return;
    }

    // ---- Replay the move ---- //
    sendToApp(loadedRecording.get(manualReplayIndex));
    manualReplayIndex++;
  }


  /**
   * When called will replay the recording at the speed set by the user.
   */
  public void autoReplay(HashMap<Integer, RecordItem> toLoadRecording) {
    // ---- Load the game ---- //
    loadedRecording = toLoadRecording;
    if (loadedRecording.isEmpty()) {
      System.out.println("Error: No recording loaded.");
      return;
    }

    // ---- Initialise the level ---- //
    int currentSequenceNumber = 0;
    RecordItem currentRecord = loadedRecording.get(0);

    if (currentRecord.getActor().equals("START")) {
      app.newGame(Integer.parseInt(currentRecord.getMove()));
      currentSequenceNumber++;
    } else {
      System.out.println("Error: No level loaded.");
      return;
    }

    // ---- Replay actions  ---- //
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    for (int i = currentSequenceNumber; i < loadedRecording.size(); i++) {
      int finalI = i;
      executor.schedule(() -> {
        sendToApp(loadedRecording.get(finalI));
        app.markUpdated();
      }, (long) replaySpeed / 2 * (finalI - currentSequenceNumber), TimeUnit.SECONDS);
    }
    executor.shutdown();
  }

  /**
   * When called this will send the data to App for which action to take.
   */
  private void sendToApp(RecordItem currentRecord) {
    // ---- Extract the data ---- //
    String move = currentRecord.getMove().trim();

    // ---- Send the user move data to App ---- //
    switch (move) {
      case "MOVE_UP":
        app.handleInput(AppInput.MOVE_UP);
        break;
      case "MOVE_DOWN":
        app.handleInput(AppInput.MOVE_DOWN);
        break;
      case "MOVE_LEFT":
        app.handleInput(AppInput.MOVE_LEFT);
        break;
      case "MOVE_RIGHT":
        app.handleInput(AppInput.MOVE_RIGHT);
        break;
      default:
        throw new IllegalArgumentException("Error: Invalid move: " + move);
    }
  }


  // ----------------------------------- SETTERS ----------------------------------- //

  /**
   * Sets the replay speed to the given value.
   *
   * @param replaySpeed - the replay speed to set.
   */
  public void setReplaySpeed(int replaySpeed) {
    this.replaySpeed = replaySpeed;
  }

}
