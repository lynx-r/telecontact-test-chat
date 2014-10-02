package me.alekspo.chat.client.endpoint;

import javafx.application.Platform;
import me.alekspo.chat.client.Message;
import me.alekspo.chat.client.message.ChatDecoder;
import me.alekspo.chat.client.message.ChatEncoder;
import me.alekspo.chat.client.message.ChatMessage;
import me.alekspo.chat.controller.MainWindowController;
import org.controlsfx.dialog.Dialogs;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
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
//      case ChatMessage.LOGIN_RESPONSE:
//        username = message.getUsername();
//        break;
//      case ChatMessage.USERLIST_UPDATE:
//        List<String> updateString = (List) message.getData();
//        refreshUserlist(updateString);
//        break;
      case ChatMessage.MESSAGE:
        handleChatMessage(message.getLogin(), message.getMessage());
        break;
    }
  }

  private void handleRegisterResponse(ChatMessage message) {
    if (message.getLogin().equals(mainWindowController.getLogin())) {
      if (message.getData().isEmpty()) {
        Platform.runLater(() -> {
          Dialogs.create().title("Информация").masthead("Вы успешно зарегистрировались.").showInformation();
        });
      } else {
        Platform.runLater(() -> {
          Dialogs.create().title("Информация").masthead("Имя " + message.getLogin() + " уже занято!").showInformation();
        });
      }
    }
  }


  private void handleChatMessage(String username, String messageData) {
    mainWindowController.addChatMessage(username + "> " + messageData);
  }

  public void sendMessage(Message msg) {
    if (!userSession.isOpen()) {
      mainWindowController.connectToServer();
      Dialogs.create().title("Информация").masthead(
          "Соединение с сервером утерено. Восстанавливаем соединение... Повторите попытку."
      ).showInformation();
    }
    if (userSession != null) {
      try {
        userSession.getBasicRemote().sendText(msg.asString());
        logger.info("Sent message: " + msg.asString());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
