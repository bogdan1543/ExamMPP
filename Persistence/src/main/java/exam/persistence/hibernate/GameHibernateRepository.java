package exam.persistence.hibernate;

import exam.model.Game;
import exam.model.GameDTO;
import exam.persistence.GameRepository;
import exam.persistence.HibernateUtils;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;


import java.util.Comparator;
import java.util.List;

@Repository

public class GameHibernateRepository implements GameRepository {
    @Override
    public void save(Game game) {
        HibernateUtils.getSessionFactory().inTransaction(session -> session.persist(game));
    }
    @Override
    public void delete(Long id) {
        HibernateUtils.getSessionFactory().inTransaction(session -> {
            Game game = session.createQuery("from Game where id = ?1", Game.class)
                    .setParameter(1, id)
                    .uniqueResult();
            if (game != null) {
                session.remove(game);
                session.flush();
            }
        });
    }
    @Override
    public Game findOne(Long id) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createSelectionQuery("from Game where id = :gid", Game.class)
                    .setParameter("gid", id)
                    .getSingleResultOrNull();
        }
    }
    @Override
    public void update(Game updatedGame) {
        HibernateUtils.getSessionFactory().inTransaction(session -> {
            if (session.find(Game.class, updatedGame.getId()) != null) {
                session.merge(updatedGame);
                session.flush();
            }
        });
    }
    @Override
    public List<Game> findAll() {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery("from Game", Game.class).getResultList();
        }
    }
    @Override
    public List<GameDTO> findByAlias(String alias) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            List<Game> games = session.createQuery("from Game where alias = ?1", Game.class)
                    .setParameter(1, alias)
                    .getResultList();

            return games.stream()
                    .map(game -> {
                        float time = java.time.Duration
                                .between(game.getStartTime(), game.getEndTime())
                                .toMillis() / 1000.0f;
                        return new GameDTO(game.getAlias(), game.getScore(), time);
                    })
                    .sorted(Comparator.comparing(GameDTO::getTime))
                    .toList();

        }
    }

}
