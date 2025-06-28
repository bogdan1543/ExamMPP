package exam.persistence.hibernate;

import exam.model.Question;
import exam.model.User;
import exam.persistence.HibernateUtils;
import exam.persistence.QuestionRepository;
import org.hibernate.Session;

import java.util.List;

public class QuestionHibernateRepository implements QuestionRepository {
    @Override
    public Question findOne(Long aLong) {
        return null;
    }

    @Override
    public List<Question> findAll() {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("from Question", Question.class).getResultList();
        }
    }

    @Override
    public void save(Question entity) {

    }

    @Override
    public void delete(Long aLong) {

    }

    @Override
    public void update(Question entity) {

    }
}
