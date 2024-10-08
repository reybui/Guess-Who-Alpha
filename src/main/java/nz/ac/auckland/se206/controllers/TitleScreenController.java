package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.speech.TextToSpeech;
import nz.ac.auckland.se206.speech.VoiceTypes.VoiceType;

public class TitleScreenController {

  @FXML
  private void initialize() {
    TextToSpeech.speak(
        "Detective, Someone has stolen the kings crown! It is your job to get to the bottom of"
            + " this",
        VoiceType.NARRORATOR);
  }

  @FXML
  private void startGame() throws IOException {
    App.setRoot("room");
  }

  @FXML
  private void onEnter(MouseEvent event) {
    ((Button) event.getSource()).setCursor(Cursor.HAND);
  }

  @FXML
  private void onExit(MouseEvent event) {
    ((Button) event.getSource()).setCursor(Cursor.DEFAULT);
  }
}
