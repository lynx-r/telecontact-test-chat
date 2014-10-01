package me.alekspo.chat.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: alekspo
 * Date: 01.10.14
 * Time: 12:57
 */
public class MainWindowController extends VBox implements Initializable {
  public TextArea chatTextArea;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }

  public void loginAction(ActionEvent actionEvent) {

  }

  public void sendMessageAction(ActionEvent actionEvent) {

  }

  public void addChatMessage(String message) {
    Platform.runLater(() -> {
      chatTextArea.appendText(message + "\n");
    });
  }
}
