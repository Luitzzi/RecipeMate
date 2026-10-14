package de.example.backend.repository;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.Collection;

public interface PersistFragment<T> {

    /**
     * Only persist an entity. Avoids unexpected merge calls
     * if the entity already exists in the db.
     *
     * @param entity Entity that should be saved
     * @return Saved entity
     * @throws EntityExistsException If the entity already exists in the db
     */
    T persist(T entity);

    /**
     * Loops over all entities and calls persist.
     * Persist ensures that the entity is only inserted and
     * no entity is updated if it already exists.
     *
     * @param entities Entities that should be saved
     * @return Saved entities
     * @throws EntityExistsException If the entity already exists in the db
     */
    Collection<T> persistAll(Collection<T> entities);

}

class PersistFragmentImpl<T> implements PersistFragment<T> {
    @PersistenceContext
    private EntityManager entityManager;


    @Transactional
    @Override
    public T persist(T entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Transactional
    @Override
    public Collection<T> persistAll(Collection<T> entities) {
        entities.forEach(this::persist);
        return entities;
    }
}
