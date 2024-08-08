package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionResult;
import nz.ac.auckland.apiproxy.chat.openai.ChatMessage;
import nz.ac.auckland.apiproxy.chat.openai.Choice;
import nz.ac.auckland.apiproxy.config.ApiProxyConfig;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.CountdownTimer;
import nz.ac.auckland.se206.prompts.PromptEngineering;
import nz.ac.auckland.se206.speech.TextToSpeech;
import nz.ac.auckland.se206.speech.VoiceTypes.VoiceType;

/**
 * Controller class for the chat view. Handles user interactions and communication with the GPT
 * model via the API proxy.
 */
public class ChatController {

  @FXML private TextArea txtaChat;
  @FXML private TextField txtInput;
  @FXML private Button btnSend;
  @FXML private Label lblTime;

  private ChatCompletionRequest chatCompletionRequest;
  private String role;
  private VoiceType voiceType;

  private CountdownTimer countdownTimer = CountdownTimer.getInstance();

  /**
   * Initializes the chat view.
   *
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  @FXML
  public void initialize() throws ApiProxyException {
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

  public void setVoiceType(String role) {
    switch (role) {
      case "Royal Advisor":
        this.voiceType = VoiceType.ROYAL_ADVISOR;
        break;
      case "Head of Security":
        this.voiceType = VoiceType.HEAD_OF_SECURITY;
        break;
      case "Foreign Ambassador":
        this.voiceType = VoiceType.FOREIGN_AMBASSADOR;
        break;
    }
  }

  /**
   * Generates the system prompt based on the profession.
   *
   * @return the system prompt string
   */
  private String getSystemPrompt() {
    Map<String, String> map = new HashMap<>();
    map.put("role", role);
    return PromptEngineering.getPrompt("chat.txt", map);
  }

  /**
   * Sets the role for the chat context and initializes the ChatCompletionRequest.
   *
   * @param role the role to set
   */
  public void setRole(String role) {
    this.role = role;
    setVoiceType(role);

    Task<Void> task =
        new Task<Void>() {
          @Override
          protected Void call() throws Exception {
            try {
              ApiProxyConfig config = ApiProxyConfig.readConfig();
              chatCompletionRequest =
                  new ChatCompletionRequest(config)
                      .setN(1)
                      .setTemperature(0.4)
                      .setTopP(0.5)
                      .setMaxTokens(100);
              runGpt(new ChatMessage("system", getSystemPrompt()));
            } catch (ApiProxyException e) {
              e.printStackTrace();
            }
            return null;
          }
        };

    task.setOnFailed(
        event -> {
          Throwable exception = task.getException();
          Platform.runLater(() -> exception.printStackTrace());
        });

    new Thread(task).start();
  }

  /**
   * Appends a chat message to the chat text area.
   *
   * @param msg the chat message to append
   */
  private void appendChatMessage(ChatMessage msg, String senderRole) {
    txtaChat.appendText(senderRole + ": " + msg.getContent() + "\n\n");
  }

  /**
   * Runs the GPT model with a given chat message.
   *
   * @param msg the chat message to process
   * @return the response chat message
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  private void runGpt(ChatMessage msg) {
    Task<ChatMessage> task =
        new Task<ChatMessage>() {
          @Override
          protected ChatMessage call() throws ApiProxyException {
            chatCompletionRequest.addMessage(msg);
            ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
            Choice result = chatCompletionResult.getChoices().iterator().next();
            chatCompletionRequest.addMessage(result.getChatMessage());
            return result.getChatMessage();
          }
        };

    task.setOnSucceeded(
        event -> {
          ChatMessage responseMsg = task.getValue();
          Platform.runLater(
              () -> {
                appendChatMessage(responseMsg, this.role);
                TextToSpeech.speak(responseMsg.getContent(), voiceType);
              });
        });

    task.setOnFailed(
        event -> {
          Throwable exception = task.getException();
          exception.printStackTrace();
        });

    new Thread(task).start();
  }

  /**
   * Sends a message to the GPT model.
   *
   * @param event the action event triggered by the send button
   * @throws ApiProxyException if there is an error communicating with the API proxy
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void onSendMessage(ActionEvent event) throws ApiProxyException, IOException {
    String message = txtInput.getText().trim();
    if (message.isEmpty()) {
      return;
    }
    txtInput.clear();
    ChatMessage msg = new ChatMessage("user", message);
    appendChatMessage(msg, "Detective");
    runGpt(msg);
  }

  /**
   * Navigates back to the previous view.
   *
   * @param event the action event triggered by the go back button
   * @throws ApiProxyException if there is an error communicating with the API proxy
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void onGoBack(ActionEvent event) throws ApiProxyException, IOException {
    App.setRoot("room");
  }
}
