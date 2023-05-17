package jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.io.Serializable;

public abstract class DataRepository<T extends AbstractEntity, ID extends Serializable> {

    private final EntityManager em;

    public DataRepository(EntityManager em) {
        this.em = em;
    }

    public T findById(ID id) {
        return em.find(getEntityClass(), id);
    }

    public void persist(T entity) {
        executeInTransaction(() -> em.persist(entity));
    }

    public void update(T entity) {
        executeInTransaction(() -> em.merge(entity));
    }

    public void delete(T entity) {
        executeInTransaction(() -> em.remove(entity));
    }

    public void deleteById(ID id) {
        T entity = findById(id);
        if (entity != null) {
            delete(entity);
        }
    }

    protected abstract Class<T> getEntityClass();

    private void executeInTransaction(Runnable action) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            action.run();
            tx.commit();
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        }
    }
}


