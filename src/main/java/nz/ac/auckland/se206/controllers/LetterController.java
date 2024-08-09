package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.CountdownTimer;

public class LetterController {

  @FXML private Label lblTime;

  private CountdownTimer countdownTimer = CountdownTimer.getInstance();

  @FXML
  public void initialize() {
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

  private void handleTimerFinish() {
    // Handle what happens when the timer finishes
  }

  /**
   * Navigates back to the previous view.
   *
   * @param event the action event triggered by the go back button
   * @throws ApiProxyException if there is an error communicating with the API proxy
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void onBack(ActionEvent event) throws ApiProxyException, IOException {
    App.setRoot("room");
  }
}
