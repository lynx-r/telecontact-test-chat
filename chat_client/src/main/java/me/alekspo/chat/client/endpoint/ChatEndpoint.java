package me.alekspo.chat.client.endpoint;

import javafx.application.Platform;
import me.alekspo.chat.client.Message;
import me.alekspo.chat.client.message.ChatDecoder;
import me.alekspo.chat.client.message.ChatEncoder;
import me.alekspo.chat.client.message.ChatMessage;
import me.alekspo.chat.controller.MainWindowController;
import org.controlsfx.dialog.Dialogs;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: alekspo
 * Date: 01.10.14
 * Time: 14:08
 */
@ClientEndpoint(
    decoders = {ChatDecoder.class},
    encoders = {ChatEncoder.class})
public class ChatEndpoint {

  final static Logger logger = Logger.getLogger("application");

  private Session userSession;

  private MainWindowController mainWindowController;

  public ChatEndpoint(URI endpointURI, MainWindowController mainWindowController) {
    try {
      WebSocketContainer container = ContainerProvider
          .getWebSocketContainer();
      container.connectToServer(this, endpointURI);
//      container.setDefaultMaxSessionIdleTimeout(10000);
    } catch (DeploymentException ignored) {
      ignored.printStackTrace();
      Dialogs.create().title("Ошибка").masthead("Не удалось подключиться к серверу!").showError();
    } catch (Exception e) {
      e.printStackTrace();
    }
    this.mainWindowController = mainWindowController;
  }

  @OnOpen
  public void onOpen(Session session) {
    userSession = session;
    logger.info("Opened new userSession: " + session.toString());
  }

  @OnMessage
  public void onMessage(ChatMessage message) {
    logger.info("Received message: " + message.asString());

    switch (message.getType()) {
      case ChatMessage.REGISTER_RESPONSE:
        handleRegisterResponse(message);
        break;
      case ChatMessage.LOGIN_RESPONSE:
        handleLoginResponse(message);
        break;
      case ChatMessage.MESSAGE:
        handleChatMessage(message);
        break;
      case ChatMessage.USERLIST_UPDATE_RESPONSE:
        handleUserlistUpdate(message);
    }
  }

  private void handleUserlistUpdate(ChatMessage message) {
    List updateString = (List) message.getData();
    String username = mainWindowController.getLogin();
    updateString.remove(username);
    mainWindowController.updateUserlistView(updateString);
  }

  private void handleLoginResponse(ChatMessage message) {
    if (message.getLogin().equals(mainWindowController.getLogin())) {
      String data = (String) message.getData();
      switch (data) {
        case "logged_in":
          Platform.runLater(() -> {
            Dialogs.create().title("Информация").masthead("Вы успешно вошли в чат.").showInformation();
          });
          break;
        case "invalid_login_or_password":
          Platform.runLater(() -> {
            Dialogs.create().title("Информация").masthead("Неверное имя пользователя или пароль.").showInformation();
          });
          break;
        case "already_logged_in":
          Platform.runLater(() -> {
            Dialogs.create().title("Информация").masthead("Вы уже вошли!").showInformation();
          });
          break;
      }
    }
  }

  private void handleRegisterResponse(ChatMessage message) {
    if (message.getLogin().equals(mainWindowController.getLogin())) {
      String data = (String) message.getData();
      switch (data) {
        case "duplicate":
          Platform.runLater(() -> {
            Dialogs.create().title("Информация").masthead("Имя " + message.getLogin() + " уже занято!").showInformation();
          });
          break;
        case "success":
          Platform.runLater(() -> {
            Dialogs.create().title("Информация").masthead("Вы успешно зарегистрировались.").showInformation();
          });
          break;
      }
    }
  }

  private void handleChatMessage(ChatMessage message) {
    mainWindowController.addChatMessage(message.getLogin() + "> " + message.getMessage());
  }

  public void sendMessage(Message msg) {
    if (!userSession.isOpen()) {
      mainWindowController.connectToServer();
      Dialogs.create().title("Информация").masthead(
          "Соединение с сервером утерено. Восстанавливаем... Повторите попытку."
      ).showInformation();
    }
    if (userSession != null) {
      userSession.getAsyncRemote().sendText(msg.asString());
      logger.info("Sent message: " + msg.asString());
    }
  }

  public void closeSession() {
    try {
      userSession.close(new CloseReason(CloseReason.CloseCodes.GOING_AWAY, mainWindowController.getLogin()));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
