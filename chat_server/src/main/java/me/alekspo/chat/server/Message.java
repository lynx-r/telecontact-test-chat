package me.alekspo.chat.server;

/**
 * Created with IntelliJ IDEA.
 * User: alekspo
 * Date: 01.10.14
 * Time: 22:51
 */
public abstract interface Message {

  public String getType();

  public void setType(String type);

  public String getData();

  public void setData(String data);

  public String asString();

}
