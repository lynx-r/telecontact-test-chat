package me.alekspo.chat.session;

import me.alekspo.chat.entity.ChatUser;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: alekspo
 * Date: 06.09.14
 * Time: 19:35
 */
@Stateless
public class ChatUserFacade extends AbstractFacade<ChatUser> {
  @Inject
  private EntityManager em;

  public ChatUserFacade() {
    super(ChatUser.class);
  }

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public List<ChatUser> findByUsername(String username) {
    return em.createNamedQuery("ChatUser.findByName", ChatUser.class)
        .setParameter("username", username)
        .getResultList();
  }

}
