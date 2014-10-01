package me.alekspo.chat.server.message;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * Created with IntelliJ IDEA.
 * User: alekspo
 * Date: 01.10.14
 * Time: 23:20
 */
public class ServerEncoder implements Encoder.Text<ServerMessage> {
  @Override
  public String encode(ServerMessage serverMessage) throws EncodeException {
    return serverMessage.asString();
  }

  @Override
  public void init(EndpointConfig endpointConfig) {

  }

  @Override
  public void destroy() {

  }
}
