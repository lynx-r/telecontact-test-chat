package me.alekspo.chat.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import me.alekspo.chat.client.message.ChatMessage;
import me.alekspo.chat.client.Message;
import me.alekspo.chat.client.endpoint.ChatEndpoint;

import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: alekspo
 * Date: 01.10.14
 * Time: 12:57
 */
public class MainWindowController extends VBox implements Initializable {
  private static final String SERVER_ENDPOINT_URI = "ws://localhost:8080/ws/chat";

  public TextArea chatTextArea;
  public TextField loginTextField;
  public TextField passwordTextField;
  public Button loginButton;
  public Button sendMessageButton;
  public TextField messageTextField;
  private String login;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }

  public void loginAction(ActionEvent actionEvent) {
    login = loginTextField.getText();
    connectToServer();
    ChatMessage msg = new ChatMessage(ChatMessage.LOGIN, login);
    msg.setPassword(passwordTextField.getText());
    ChatEndpoint.sendMessage(msg);
  }

  public void sendMessageAction(ActionEvent actionEvent) {
    Message msg = new ChatMessage(ChatMessage.MESSAGE, login, messageTextField.getText());
    ChatEndpoint.sendMessage(msg);
    messageTextField.clear();
  }

  public void addChatMessage(String message) {
    Platform.runLater(() -> {
      chatTextArea.appendText(message + "\n");
    });
  }

  private void connectToServer() {
    try {
      new ChatEndpoint(new URI(SERVER_ENDPOINT_URI));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
