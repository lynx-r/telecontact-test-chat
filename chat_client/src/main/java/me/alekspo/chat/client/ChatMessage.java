package me.alekspo.chat.client;

import javax.json.Json;

/**
 * Created with IntelliJ IDEA.
 * User: alekspo
 * Date: 01.10.14
 * Time: 14:15
 */
public class ChatMessage {
  public static final String REGISTER = "register";
  public static final String LOGIN = "login";
  public static final String MESSAGE = "message";
  private String type;
  private String message;
  private String login;
  private String password;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public String toString() {
    return Json.createObjectBuilder()
        .add("login", login != null ? login : "")
        .add("password", password != null ? password : "")
        .add("message", message != null ? message : "")
        .build().toString();
  }
}
