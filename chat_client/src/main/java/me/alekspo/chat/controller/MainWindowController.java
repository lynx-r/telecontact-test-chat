package me.alekspo.chat.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import me.alekspo.chat.client.Message;
import me.alekspo.chat.client.endpoint.ChatEndpoint;
import me.alekspo.chat.client.message.ChatMessage;

import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
  public Button registerButton;
  private String login;
  private ChatEndpoint chatEndpoint;

  public String getLogin() {
    return login;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }

  public void loginAction(ActionEvent actionEvent) {
    login = loginTextField.getText();
    ChatMessage msg = new ChatMessage(ChatMessage.LOGIN, login);
    msg.setPassword(passwordTextField.getText());
    chatEndpoint.sendMessage(msg);
  }

  public void connectToServer() {
    try {
      chatEndpoint = new ChatEndpoint(new URI(SERVER_ENDPOINT_URI), this);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void sendMessageAction(ActionEvent actionEvent) {
    Message msg = new ChatMessage(ChatMessage.MESSAGE, login, messageTextField.getText());
    chatEndpoint.sendMessage(msg);
    messageTextField.clear();
  }

  public void addChatMessage(String message) {
    Platform.runLater(() -> {
      chatTextArea.appendText(message + "\n");
    });
  }

  public void registerAction(ActionEvent actionEvent) {
    login = loginTextField.getText();
    ChatMessage chatMessage = new ChatMessage(ChatMessage.REGISTER, login);

    MessageDigest messageDigest;
    try {
      messageDigest = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      return;
    }
    byte bDigest[] = messageDigest.digest(passwordTextField.getText().getBytes());
    BigInteger bi = new BigInteger(bDigest);
    chatMessage.setPassword(bi.toString(16));
    chatEndpoint.sendMessage(chatMessage);
  }

}
