package exam.persistence;


import exam.model.Entity;

import java.util.List;


public interface Repository<ID, E extends Entity<ID>> {
    E findOne(ID id);

    List<E> findAll();

    void save(E entity);


    void delete(ID id);


    void update(E entity);
}

