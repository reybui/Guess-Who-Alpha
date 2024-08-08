package nz.ac.auckland.se206.states;

import java.io.IOException;
import javafx.scene.input.MouseEvent;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameStateContext;
import nz.ac.auckland.se206.speech.TextToSpeech;
import nz.ac.auckland.se206.speech.VoiceTypes.VoiceType;

/**
 * The GameStarted state of the game. Handles the initial interactions when the game starts,
 * allowing the player to chat with characters and prepare to make a guess.
 */
public class GameStarted implements GameState {

  private Boolean hasInteractedPerson = false;
  private Boolean hasInteractedObject = false;
  private final GameStateContext context;

  /**
   * Constructs a new GameStarted state with the given game state context.
   *
   * @param context the context of the game state
   */
  public GameStarted(GameStateContext context) {
    this.context = context;
  }

  /**
   * Handles the event when a rectangle is clicked. Depending on the clicked rectangle, it either
   * provides an introduction or transitions to the chat view.
   *
   * @param event the mouse event triggered by clicking a rectangle
   * @param rectangleId the ID of the clicked rectangle
   * @throws IOException if there is an I/O error
   */
  @Override
  public void handleRectangleClick(MouseEvent event, String rectangleId) throws IOException {
    switch (rectangleId) {
      case "rectLetter":
        App.openLetter(event);
        hasInteractedObject = true;
        return;
    }
    hasInteractedPerson = true;
    App.openChat(event, context.getSuspectName(rectangleId));
  }

  /**
   * Handles the event when the guess button is clicked. Prompts the player to make a guess and
   * transitions to the guessing state.
   *
   * @throws IOException if there is an I/O error
   */
  @Override
  public void handleGuessClick() throws IOException {
    if (hasInteractedPerson && hasInteractedObject) {
      TextToSpeech.speak("Make a guess, click on the thief!", VoiceType.NARRORATOR);
      context.setState(context.getGuessingState());

    } else {
      TextToSpeech.speak(
          "You must interact with at least one suspect and one object, to be able to guess",
          VoiceType.NARRORATOR);
    }
  }
}
