package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.CountdownTimer;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.speech.TextToSpeech;
import nz.ac.auckland.se206.speech.VoiceTypes.VoiceType;
import nz.ac.auckland.se206.states.GameStarted;
import nz.ac.auckland.se206.states.Guessing;

/**
 * Controller class for the room view. Handles user interactions within the room where the user can
 * chat with customers and guess their profession.
 */
public class RoomController {

  private static boolean isFirstTimeInit = true;
  private static GameStateContext context = new GameStateContext();

  @FXML private Rectangle rectForeign;
  @FXML private Rectangle rectAdvisor;
  @FXML private Rectangle rectLetter;
  @FXML private Rectangle rectGuard;
  @FXML private Label lblTime;
  @FXML private Button btnGuess;
  @FXML private Label lblGameOver;

  private boolean guessClicked = false;
  private CountdownTimer countdownTimer = CountdownTimer.getInstance();

  /**
   * Initializes the room view. If it's the first time initialization, it will provide instructions
   * via text-to-speech.
   */
  @FXML
  public void initialize() {
    if (isFirstTimeInit) {
      TextToSpeech.speak(
          "Interrogate the three suspects, and guess who is the thief", VoiceType.NARRORATOR);
      isFirstTimeInit = false;
      countdownTimer.start();
    }
    countdownTimer.setOnTick(() -> Platform.runLater(this::updateTimerLabel));
    countdownTimer.setOnFinish(() -> Platform.runLater(this::handleTimerFinish));
    updateTimerLabel();
  }

  private void updateTimerLabel() {
    int remainingTime = countdownTimer.getRemainingTime();
    int minutes = remainingTime / 60;
    int seconds = remainingTime % 60;
    lblTime.setText(String.format("%02d:%02d", minutes, seconds));
  }

  public void handleTimerFinish() {
    // Handle what happens when the timer finishes
    if (context.getState() instanceof Guessing) {
      context.setState(context.getGameOverState());
      TextToSpeech.speak("No guess was made, you lost!", VoiceType.NARRORATOR);
    } else if (context.getState() instanceof GameStarted) {
      TextToSpeech.speak("Time's up! Make a guess on who the suspect is", VoiceType.NARRORATOR);
      countdownTimer.resetToGuessingTime();
      context.setState(context.getGuessingState());
      btnGuess.disableProperty().setValue(true);
    } else {
      return;
    }
  }

  /**
   * Handles the key pressed event.
   *
   * @param event the key event
   */
  @FXML
  public void onKeyPressed(KeyEvent event) {
    System.out.println("Key " + event.getCode() + " pressed");
  }

  /**
   * Handles the key released event.
   *
   * @param event the key event
   */
  @FXML
  public void onKeyReleased(KeyEvent event) {
    System.out.println("Key " + event.getCode() + " released");
  }

  /**
   * Handles mouse clicks on rectangles representing people in the room.
   *
   * @param event the mouse event triggered by clicking a rectangle
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void handleRectangleClick(MouseEvent event) throws IOException {
    Rectangle clickedRectangle = (Rectangle) event.getSource();
    context.handleRectangleClick(event, clickedRectangle.getId());
  }

  /**
   * Handles the guess button click event.
   *
   * @param event the action event triggered by clicking the guess button
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void handleGuessClick(ActionEvent event) throws IOException {
    context.handleGuessClick();
    if (guessClicked) {
      countdownTimer.setGuessingState(true);
      countdownTimer.resetToGuessingTime();
    }
  }

  @FXML
  private void handleMouseEnter(MouseEvent event) {
    ((Rectangle) event.getSource()).setCursor(Cursor.HAND);
  }

  @FXML
  private void handleMouseExit(MouseEvent event) {
    ((Rectangle) event.getSource()).setCursor(Cursor.DEFAULT);
  }

  public void disableFeatures() {
    btnGuess.disableProperty().setValue(true);
    rectLetter.setDisable(true);
  }

  public void guessClicked() {
    guessClicked = true;
  }

  public void disableTimer() {
    lblTime.setVisible(false);
  }

  public void setGameOverText(String text) {
    lblGameOver.setText(text);
  }
}
