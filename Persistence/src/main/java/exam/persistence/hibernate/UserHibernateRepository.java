package exam.persistence.hibernate;


import exam.model.User;
import exam.persistence.HibernateUtils;
import exam.persistence.UserRepository;
import org.hibernate.Session;

import java.util.List;

public class UserHibernateRepository implements UserRepository {

    @Override
    public void save(User user) {
        HibernateUtils.getSessionFactory().inTransaction(session -> session.persist(user));
    }
    @Override
    public void delete(String username) {
        HibernateUtils.getSessionFactory().inTransaction(session -> {
            User user = session.createQuery("from User where username=?1", User.class)
                    .setParameter(1, username)
                    .uniqueResult();
            if (user != null) {
                session.remove(user);
                session.flush();
            }
        });
    }
    @Override
    public User findOne(String username) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createSelectionQuery("from User where username = :uname", User.class)
                    .setParameter("uname", username)
                    .getSingleResultOrNull();
        }
    }
    @Override
    public void update(User updatedUser) {
        HibernateUtils.getSessionFactory().inTransaction(session -> {
            if (session.find(User.class, updatedUser.getId()) != null) {
                session.merge(updatedUser);
                session.flush();
            }
        });
    }
    @Override
    public List<User> findAll() {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("from User", User.class).getResultList();
        }
    }
}

