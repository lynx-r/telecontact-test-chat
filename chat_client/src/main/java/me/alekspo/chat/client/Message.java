package me.alekspo.chat.client;

/**
 * Created with IntelliJ IDEA.
 * User: alekspo
 * Date: 01.10.14
 * Time: 22:29
 */
public abstract interface Message {

  public String getType();

  public void setType(String type);

  public Object getData();

  public void setData(Object data);

  public String asString();

}
