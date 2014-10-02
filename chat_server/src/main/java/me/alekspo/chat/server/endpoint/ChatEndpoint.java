package me.alekspo.chat.server.endpoint;

import me.alekspo.chat.entity.ChatUser;
import me.alekspo.chat.server.message.ServerDecoder;
import me.alekspo.chat.server.message.ServerEncoder;
import me.alekspo.chat.server.message.ServerMessage;
import me.alekspo.chat.session.ChatUserFacade;

import javax.inject.Inject;
import javax.websocket.CloseReason;
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
  private ConcurrentHashMap<String, Session> connections = new ConcurrentHashMap<String, Session>();
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
      case ServerMessage.REGISTER:
        handleRegisterRequest(session, message);
        break;
      case ServerMessage.LOGIN:
        handleLoginRequest(session, message);
        break;
      case ServerMessage.MESSAGE:
        handleChatMessage(message);
        break;
      case ServerMessage.DISCONNECT:
        handleDisconnectRequest(message);
        break;
    }
  }

  private void handleRegisterRequest(Session session, ServerMessage message) {
    RemoteEndpoint.Basic remote = session.getBasicRemote();
    ServerMessage serverMessage = new ServerMessage(ServerMessage.REGISTER_RESPONSE, message.getLogin());
    if (chatUserFacade.findByUsername(message.getLogin()).size() > 0) {
      serverMessage.setData("duplicate");
    } else {
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
      connections.put(serverMessage.getLogin(), session);
      session.setMaxIdleTimeout(MAX_IDLE_TIMEOUT);

      ServerMessage responseMessage = new ServerMessage(ServerMessage.LOGIN_RESPONSE, serverMessage.getLogin());
      sendMessage(session, responseMessage);

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

  public void handleDisconnectRequest(ServerMessage serverMessage) {
    logger.info(serverMessage.getLogin() + " would like to leave ChatMessage");
    String login = serverMessage.getLogin();

    logger.info("Removing " + login + " from StepMessage.");
    Session nextSession = connections.get(login);

    if (nextSession == null) {
      return;
    }

    connections.remove(login);
    try {
      nextSession.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "User logged off"));
    } catch (IOException e) {
      e.printStackTrace();
    }

    broadcastUserList();
  }

  private void broadcastUserList() {
    logger.info("Broadcasting updated user list");
    if (!connections.isEmpty()) {
      ServerMessage serverMessage = new ServerMessage(ServerMessage.USERLIST_UPDATE);
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
