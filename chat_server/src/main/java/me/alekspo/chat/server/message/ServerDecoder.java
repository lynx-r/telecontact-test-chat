package me.alekspo.chat.server.message;

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
 * Time: 23:16
 */
public class ServerDecoder implements Decoder.Text<ServerMessage> {
  @Override
  public void init(EndpointConfig endpointConfig) {

  }

  @Override
  public void destroy() {

  }

  @Override
  public ServerMessage decode(String s) throws DecodeException {
    JsonObject jsonObject = Json.createReader(new StringReader(s)).readObject();

    ServerMessage serverMessage = new ServerMessage();
    serverMessage.setType(jsonObject.getString("type"));
    serverMessage.setLogin(jsonObject.getString("login"));
    serverMessage.setData(jsonObject.getString("data"));

    serverMessage.setPassword(jsonObject.getString("password", ""));
    serverMessage.setMessage(jsonObject.getString("message", ""));

    return serverMessage;
  }

  @Override
  public boolean willDecode(String s) {
    JsonObject jsonObject;
    try {
      jsonObject = Json.createReader(new StringReader(s)).readObject();
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    String type = jsonObject.getString("type");
    return type.equals(ServerMessage.LOGIN)
        || type.equals(ServerMessage.REGISTER)
        || type.equals(ServerMessage.REGISTER_RESPONSE)
        || type.equals(ServerMessage.MESSAGE)
        || type.equals(ServerMessage.USERLIST_UPDATE);
  }

}
