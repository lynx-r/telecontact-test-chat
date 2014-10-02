package me.alekspo.chat.client.message;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

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

    String data = jsonObject.getString("data");

    switch (message.getType()) {
      case ChatMessage.USERLIST_UPDATE_RESPONSE:
        JsonArray jsonArray = Json.createReader(new StringReader(data)).readArray();
        List<String> stepDataArray = new ArrayList<String>();
        for (JsonValue val : jsonArray) {
          stepDataArray.add(((JsonString) val).getString());
        }
        message.setData(stepDataArray);
        break;
      default:
        message.setData(data);
        break;
    }

    return message;
  }

  @Override
  public boolean willDecode(String s) {
    JsonObject jsonObject = Json.createReader(new StringReader(s)).readObject();
    String type = jsonObject.getString("type");
    return type.equals(ChatMessage.MESSAGE)
        || type.equals(ChatMessage.LOGIN_REQUEST)
        || type.equals(ChatMessage.LOGIN_RESPONSE)
        || type.equals(ChatMessage.REGISTER_REQUEST)
        || type.equals(ChatMessage.REGISTER_RESPONSE)
        || type.equals(ChatMessage.USERLIST_UPDATE_RESPONSE);
  }

  @Override
  public void init(EndpointConfig endpointConfig) {

  }

  @Override
  public void destroy() {

  }
}
