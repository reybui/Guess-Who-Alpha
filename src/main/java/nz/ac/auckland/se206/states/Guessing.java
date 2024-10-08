package nz.ac.auckland.se206.states;

import java.io.IOException;
import javafx.scene.input.MouseEvent;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.controllers.RoomController;
import nz.ac.auckland.se206.speech.TextToSpeech;
import nz.ac.auckland.se206.speech.VoiceTypes.VoiceType;

/**
 * The Guessing state of the game. Handles the logic for when the player is making a guess about the
 * profession of the characters in the game.
 */
public class Guessing implements GameState {

  private final GameStateContext context;

  /**
   * Constructs a new Guessing state with the given game state context.
   *
   * @param context the context of the game state
   */
  public Guessing(GameStateContext context) {
    this.context = context;
  }

  /**
   * Handles the event when a rectangle is clicked. Checks if the clicked rectangle is a customer
   * and updates the game state accordingly.
   *
   * @param event the mouse event triggered by clicking a rectangle
   * @param rectangleId the ID of the clicked rectangle
   * @throws IOException if there is an I/O error
   */
  @Override
  public void handleRectangleClick(MouseEvent event, String rectangleId) throws IOException {
    String thiefId = context.getRectIdToGuess();
    String gameOvermsg = "";
    if (rectangleId.equals(thiefId)) {
      TextToSpeech.speak("Correct! You won! This is the thief", VoiceType.NARRORATOR);
      gameOvermsg = "You won! The thief has been caught!";
    } else {
      TextToSpeech.speak("You lost! The thief lives to steal another day", VoiceType.NARRORATOR);
      gameOvermsg = "You lost! The thief got away!";
    }
    context.setState(context.getGameOverState());
    RoomController roomController = (RoomController) App.getController();
    roomController.disableTimer();
    roomController.setGameOverText(gameOvermsg);
  }

  /**
   * Handles the event when the guess button is clicked. Since the player has already guessed, it
   * notifies the player.
   *
   * @throws IOException if there is an I/O error
   */
  @Override
  public void handleGuessClick() throws IOException {
    TextToSpeech.speak("Make a guess!", VoiceType.NARRORATOR);
  }
}
