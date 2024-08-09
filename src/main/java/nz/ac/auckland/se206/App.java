package nz.ac.auckland.se206;

import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import nz.ac.auckland.se206.controllers.ChatController;

/**
 * This is the entry point of the JavaFX application. This class initializes and runs the JavaFX
 * application.
 */
public class App extends Application {

  private static Scene scene;
  private static Object currentController;

  /**
   * The main method that launches the JavaFX application.
   *
   * @param args the command line arguments
   */
  public static void main(final String[] args) {
    launch();
  }

  /**
   * Sets the root of the scene to the specified FXML file.
   *
   * @param fxml the name of the FXML file (without extension)
   * @throws IOException if the FXML file is not found
   */
  public static void setRoot(String fxml) throws IOException {
    // scene.setRoot(loadFxml(fxml));
    FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml"));
    Parent root = loader.load();
    scene.setRoot(root);
    currentController = loader.getController();
  }

  /**
   * Returns the controller of the current scene.
   *
   * @return the controller of the current scene
   */
  public static Object getController() {
    return currentController;
  }

  /**
   * Loads the FXML file and returns the associated node. The method expects that the file is
   * located in "src/main/resources/fxml".
   *
   * @param fxml the name of the FXML file (without extension)
   * @return the root node of the FXML file
   * @throws IOException if the FXML file is not found
   */
  private static Parent loadFxml(final String fxml) throws IOException {
    return new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml")).load();
  }

  /**
   * Opens the chat view and sets the profession in the chat controller.
   *
   * @param event the mouse event that triggered the method
   * @param role the profession to set in the chat controller
   * @throws IOException if the FXML file is not found
   */
  public static void openChat(MouseEvent event, String role) throws IOException {
    Task<Void> task =
        new Task<Void>() {
          @Override
          protected Void call() throws IOException {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/chat.fxml"));
            Parent root = loader.load();

            ChatController chatController = loader.getController();
            chatController.setRole(role);

            Platform.runLater(
                () -> {
                  Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                  Scene scene = new Scene(root);
                  stage.setScene(scene);
                  stage.show();
                });

            return null;
          }
        };

    new Thread(task).start();
  }

  /**
   * This method is invoked when the application starts. It loads and shows the "room" scene.
   *
   * @param stage the primary stage of the application
   * @throws IOException if the "src/main/resources/fxml/room.fxml" file is not found
   */
  @Override
  public void start(final Stage stage) throws IOException {
    Parent root = loadFxml("room");
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
    root.requestFocus();
  }

  public static void openLetter(MouseEvent event) throws IOException {
    FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/letter.fxml"));
    Parent root = loader.load();

    // LetterController letterController = loader.getController();

    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }
}
