package ru.atom.lecture08.websocket.dao;

import org.springframework.stereotype.Repository;
import ru.atom.lecture08.websocket.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager em;


    public User getByLogin(String login) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> userCriteria = cb.createQuery(User.class);
        Root<User> userRoot = userCriteria.from(User.class);
        userCriteria.select(userRoot);
        userCriteria.where(cb.equal(userRoot.get("login"), login));
        List<User> list = em.createQuery(userCriteria).getResultList();
        if (list.size() == 0)
            return null;
        else {
            System.out.println(list.get(0).getLogin());
            return list.get(0);
        }
    }

    public String getUsersOnline() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> userCriteria = cb.createQuery(User.class);
        Root<User> userRoot = userCriteria.from(User.class);
        userCriteria.select(userRoot);
        userCriteria.where(cb.equal(userRoot.get("online"), 1));
        List<User> list = em.createQuery(userCriteria).getResultList();
        return list.stream()
                .map(User::getLogin)
                .reduce("", (e1,e2) -> e1 + "\n" + e2);
    }


    public void refresh(User user) {
        em.merge(user);
    }

    public void save(User user) {
        em.persist(user);
    }


}
