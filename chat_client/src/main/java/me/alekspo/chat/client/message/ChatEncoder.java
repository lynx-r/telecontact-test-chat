package me.alekspo.chat.client.message;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * Created with IntelliJ IDEA.
 * User: alekspo
 * Date: 01.10.14
 * Time: 14:14
 */
public class ChatEncoder implements Encoder.Text<ChatMessage> {
  @Override
  public String encode(ChatMessage chatMessage) throws EncodeException {
    return chatMessage.asString();
  }

  @Override
  public void init(EndpointConfig endpointConfig) {

  }

  @Override
  public void destroy() {

  }
}
