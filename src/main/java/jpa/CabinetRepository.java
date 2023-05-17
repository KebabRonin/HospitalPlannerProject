package jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;

public class CabinetRepository extends DataRepository<Cabinet, Integer>{
    private static final String PERSISTENCE_UNIT_NAME = "ExamplePU";
    private EntityManagerFactory entityManagerFactory;

    public CabinetRepository(EntityManager em) {
        super(em);
    }

    @Override
    protected Class<Cabinet> getEntityClass() {
        return Cabinet.class;
    }

    private EntityManager getEntityManager() {
        if (entityManagerFactory == null) {
            entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        }
        return entityManagerFactory.createEntityManager();
    }

    public void create(Cabinet cabinet) {
        EntityManager entityManager = getEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(cabinet);
            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }
    }

    public Cabinet findById(Integer id) {
        EntityManager entityManager = getEntityManager();
        try {
            return entityManager.find(Cabinet.class, id);
        } finally {
            entityManager.close();
        }
    }

    public List<Cabinet> findByName(String namePattern) {
        EntityManager entityManager = getEntityManager();
        try {
            TypedQuery<Cabinet> query = entityManager.createNamedQuery("Cabinet.findByName", Cabinet.class);
            query.setParameter("denumire", "%" + namePattern + "%");
            return query.getResultList();
        } finally {
            entityManager.close();
        }
    }
}

