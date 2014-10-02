package me.alekspo.chat.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

/**
 * Created with IntelliJ IDEA.
 * User: alekspo
 * Date: 02.10.14
 * Time: 13:46
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "ChatUser.findByName",
        query = "SELECT c FROM ChatUser c WHERE c.username = :username")
})
public class ChatUser {

  @Id
  @GeneratedValue
  private Long id;
  @Version
  private Integer version;
  @NotNull
  @Column(unique = true)
  private String username;
  @NotNull
  private String passwordHash;

  public ChatUser() {}

  public ChatUser(String login, String password) {
    this.username = login;
    this.passwordHash = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return passwordHash;
  }

  public void setPassword(String password) {
    this.passwordHash = password;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

}
