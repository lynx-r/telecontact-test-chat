package me.alekspo.chat.client;

import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.io.StringReader;

/**
 * Created with IntelliJ IDEA.
 * User: alekspo
 * Date: 01.10.14
 * Time: 14:14
 */
public class ChatDecoder implements Decoder.Text<ChatMessage> {
  @Override
  public ChatMessage decode(String s) throws DecodeException {
    JsonObject jsonObject = Json.createReader(new StringReader(s)).readObject();
    ChatMessage message = new ChatMessage();
    message.setLogin(jsonObject.getString("login"));
    message.setMessage(jsonObject.getString("message"));
    message.setPassword(jsonObject.getString("password"));
    return message;
  }

  @Override
  public boolean willDecode(String s) {
    JsonObject jsonObject = Json.createReader(new StringReader(s)).readObject();
    String type = jsonObject.getString("type");
    return type.equals(ChatMessage.LOGIN)
        || type.equals(ChatMessage.MESSAGE)
        || type.equals(ChatMessage.REGISTER);
  }

  @Override
  public void init(EndpointConfig endpointConfig) {

  }

  @Override
  public void destroy() {

  }
}
