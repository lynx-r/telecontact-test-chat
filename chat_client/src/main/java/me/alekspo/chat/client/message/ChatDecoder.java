package me.alekspo.chat.client.message;

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
    message.setType(jsonObject.getString("type"));
    message.setLogin(jsonObject.getString("login"));
    message.setPassword(jsonObject.getString("password", ""));
    message.setMessage(jsonObject.getString("message", ""));
    message.setData(jsonObject.getString("data"));
    return message;
  }

  @Override
  public boolean willDecode(String s) {
    JsonObject jsonObject = Json.createReader(new StringReader(s)).readObject();
    String type = jsonObject.getString("type");
    return type.equals(ChatMessage.MESSAGE)
        || type.equals(ChatMessage.LOGIN)
        || type.equals(ChatMessage.REGISTER)
        || type.equals(ChatMessage.USERLIST_UPDATE);
  }

  @Override
  public void init(EndpointConfig endpointConfig) {

  }

  @Override
  public void destroy() {

  }
}
