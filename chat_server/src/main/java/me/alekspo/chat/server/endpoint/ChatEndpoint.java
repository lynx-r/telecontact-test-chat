package me.alekspo.chat.server.endpoint;

import me.alekspo.chat.entity.ChatUser;
import me.alekspo.chat.server.message.ServerDecoder;
import me.alekspo.chat.server.message.ServerEncoder;
import me.alekspo.chat.server.message.ServerMessage;
import me.alekspo.chat.session.ChatUserFacade;

import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: alekspo
 * Date: 01.10.14
 * Time: 15:36
 */
@ServerEndpoint(
    value = "/chat",
    decoders = {ServerDecoder.class},
    encoders = {ServerEncoder.class}
)
public class ChatEndpoint {
  final static Logger logger = Logger.getLogger("application");
  private static ConcurrentHashMap<String, Session> connections = new ConcurrentHashMap<String, Session>();
  private long MAX_IDLE_TIMEOUT = 1000 * 60 * 60;

  @Inject
  private ChatUserFacade chatUserFacade;

  @OnOpen
  public void init(Session s) {
    logger.info("############ Someone connected to chat... " + s.toString());
  }

  @OnMessage
  public void onMessage(Session session, ServerMessage message) {
    final String messageType = message.getType();
    logger.info("Get message " + message.asString());

    switch (messageType) {
      case ServerMessage.REGISTER_REQUEST:
        handleRegisterRequest(session, message);
        break;
      case ServerMessage.LOGIN_REQUEST:
        handleLoginRequest(session, message);
        break;
      case ServerMessage.MESSAGE:
        handleChatMessage(message);
        break;
    }
  }

  @OnClose
  public void onClose(CloseReason reason) {
    logger.info("Close connection code: " + reason.getCloseCode());
    String login = reason.getReasonPhrase();
    connections.remove(login);
    broadcastUserList();
  }

  private void handleRegisterRequest(Session session, ServerMessage message) {
    RemoteEndpoint.Basic remote = session.getBasicRemote();
    ServerMessage serverMessage = new ServerMessage(ServerMessage.REGISTER_RESPONSE, message.getLogin());
    if (chatUserFacade.findByUsername(message.getLogin()).size() > 0) {
      serverMessage.setData("duplicate");
    } else {
      serverMessage.setData("success");
      ChatUser chatUser = new ChatUser(message.getLogin(), message.getPassword());
      chatUserFacade.create(chatUser);
    }

    try {
      remote.sendText(serverMessage.asString());
    } catch (IOException ioe) {
      logger.warning("Error sending message to client " + remote + " : " + ioe.getMessage());
    }
  }

  public void handleLoginRequest(Session session, ServerMessage serverMessage) {
    if (chatUserFacade.findByUsernameAndPassword(serverMessage.getLogin(), serverMessage.getPassword()).size() == 1) {
      ServerMessage responseMessage = new ServerMessage(ServerMessage.LOGIN_RESPONSE, serverMessage.getLogin());
      if (connections.get(serverMessage.getLogin()) != null) {
        responseMessage.setData("already_logged_in");
        sendMessage(session, responseMessage);
        return;
      } else {
        responseMessage.setData("logged_in");
        sendMessage(session, responseMessage);
      }

      connections.put(serverMessage.getLogin(), session);
      session.setMaxIdleTimeout(MAX_IDLE_TIMEOUT);

      logger.info("Signing " + serverMessage.getLogin() + " into ChatMessage.");
      broadcastUserList();
    } else {
      ServerMessage responseMessage = new ServerMessage(ServerMessage.LOGIN_RESPONSE, serverMessage.getLogin());
      responseMessage.setData("invalid_login_or_password");
      sendMessage(session, responseMessage);
    }
  }

  public void handleChatMessage(ServerMessage message) {
    logger.info("Receiving ChatMessage message from " + message.getLogin());
    logger.info("Broadcasting chat message " + message.asString());
    for (Session nextSession : connections.values()) {
      sendMessage(nextSession, message);
    }
  }

  private void broadcastUserList() {
    logger.info("Broadcasting updated user list");
    if (!connections.isEmpty()) {
      ServerMessage serverMessage = new ServerMessage(ServerMessage.USERLIST_UPDATE_RESPONSE);
      serverMessage.setData(connections.keySet());

      for (Session nextSession : connections.values()) {
        sendMessage(nextSession, serverMessage);
      }
//      session.broadcast(stepMessage.toString());
    }
  }

  private void sendMessage(Session session, ServerMessage message) {
    RemoteEndpoint.Basic remote = session.getBasicRemote();
    if (remote != null) {
      try {
        remote.sendText(message.asString());
      } catch (IOException ioe) {
        logger.warning("Error updating a client " + remote + " : " + ioe.getMessage());
      }
    }
  }

}
