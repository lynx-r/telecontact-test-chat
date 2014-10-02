package me.alekspo.chat.client.message;

import me.alekspo.chat.client.Message;

import javax.json.Json;

/**
 * Created with IntelliJ IDEA.
 * User: alekspo
 * Date: 01.10.14
 * Time: 14:15
 */
public class ChatMessage implements Message {
  public static final String REGISTER_REQUEST = "register";
  public static final String REGISTER_RESPONSE = "register_response";
  public static final String LOGIN_REQUEST = "login";
  public static final String LOGIN_RESPONSE = "login_response";
  public static final String USERLIST_UPDATE_RESPONSE = "userlist_update";
  public static final String MESSAGE = "message";
  private String type;
  private String message;
  private String login;
  private Object data;
  private String password;

  public ChatMessage() {
  }

  public ChatMessage(String type, String login, String message) {
    this.type = type;
    this.login = login;
    this.message = message;
  }

  public ChatMessage(String type, String login) {
    this.type = type;
    this.login = login;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public Object getData() {
    return data;
  }

  @Override
  public void setData(Object data) {
    this.data = data;
  }

  @Override
  public String asString() {
    return Json.createObjectBuilder()
        .add("type", type != null ? type : "")
        .add("login", login != null ? login : "")
        .add("message", message != null ? message : "")
        .add("password", password != null ? password : "")
        .add("data", data != null ? data.toString() : "")
        .build().toString();
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

}
