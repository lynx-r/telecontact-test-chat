package me.alekspo.chat.server.endpoint;

import me.alekspo.chat.server.message.ServerDecoder;
import me.alekspo.chat.server.message.ServerEncoder;
import me.alekspo.chat.server.message.ServerMessage;

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
  private static ConcurrentHashMap<String, Session> connections = new ConcurrentHashMap<String, Session>();
  private long MAX_IDLE_TIMEOUT = 1000 * 60 * 60;

  @OnOpen
  public void init(Session s) {
    logger.info("############ Someone connected to chat... " + s.toString());
  }

  @OnMessage
  public void onMessage(Session session, ServerMessage message) {
    final String messageType = message.getType();
    logger.info("Get message " + message.asString());

    switch (messageType) {
      case ServerMessage.LOGIN:
        handleLoginRequest(session, message);
        break;
      case ServerMessage.MESSAGE:
        handleChatMessage(message);
        break;
      case ServerMessage.DISCONNECT:
        handleDisconnectRequest(session, message);
        break;
    }
  }

  public void handleLoginRequest(Session session, ServerMessage serverMessage) {
    connections.put(serverMessage.getLogin(), session);
    session.setMaxIdleTimeout(MAX_IDLE_TIMEOUT);

    logger.info("Signing " + serverMessage.getLogin() + " into ChatMessage.");
    broadcastUserList(session);
  }

  public void handleChatMessage(ServerMessage message) {
    logger.info("Receiving ChatMessage message from " + message.getLogin());
    logger.info("Broadcasting chat message " + message.asString());
    for (Session nextSession : connections.values()) {
      RemoteEndpoint.Basic remote = nextSession.getBasicRemote();
      if (remote != null) {
        try {
          remote.sendText(message.asString());
        } catch (IOException ioe) {
          logger.warning("Error updating a client " + remote + " : " + ioe.getMessage());
        }
      }
    }
  }

  public void handleDisconnectRequest(Session session, ServerMessage serverMessage) {
    logger.info(serverMessage.getLogin() + " would like to leave ChatMessage");
    removeUser(serverMessage.getLogin());
    broadcastUserList(session);
  }

  private void removeUser(String login) {
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
  }


  private void broadcastUserList(Session session) {
    logger.info("Broadcasting updated user list");
    if (!connections.isEmpty()) {
      ServerMessage serverMessage = new ServerMessage(ServerMessage.USERLIST_UPDATE);
      serverMessage.setData(connections.keySet());

      for (Session nextSession : connections.values()) {
        RemoteEndpoint.Basic remote = nextSession.getBasicRemote();
        try {
          remote.sendText(serverMessage.asString());
        } catch (IOException ioe) {
          logger.warning("Error updating a client " + remote + " : " + ioe.getMessage());
        }
      }
//      session.broadcast(stepMessage.toString());
    }
  }

  private String registerNewUsername(String newUsername, Session session) {
    if (connections.containsKey(newUsername)) {
      return registerNewUsername(newUsername + "1", session);
    }

    return newUsername;
  }
}
