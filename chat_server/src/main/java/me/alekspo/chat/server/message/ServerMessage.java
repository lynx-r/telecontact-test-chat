package me.alekspo.chat.server.message;

import me.alekspo.chat.server.Message;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: alekspo
 * Date: 01.10.14
 * Time: 23:13
 */
public class ServerMessage implements Message {
  public static final String REGISTER = "register";
  public static final String REGISTER_RESPONSE = "register_response";
  public static final String LOGIN = "login";
  public static final String LOGIN_RESPONSE = "login_response";
  public static final String DISCONNECT = "disconnect";
  public static final String MESSAGE = "message";
  public static final String USERLIST_UPDATE = "userlist_update";
  private String type;
  private String login;
  private String password;
  private String data;
  private String message;
  public ServerMessage() {}
  public ServerMessage(String type) {
    this.type = type;
  }

  public ServerMessage(String type, String login) {
    this.type = type;
    this.login = login;
  }

  public ServerMessage(String type, String login, String data) {
    this.type = type;
    this.login = login;
    this.data = data;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setData(Set<String> dataSet) {
    JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
    for (String user : dataSet) {
      jsonArrayBuilder.add(user);
    }
    data = jsonArrayBuilder.build().toString();
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public String asString() {
    return Json.createObjectBuilder()
        .add("type", type != null ? type : "")
        .add("login", login != null ? login : "")
        .add("message", message != null ? message : "")
        .add("password", password != null ? password : "")
        .add("data", data != null ? data : "")
        .build().toString();
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
