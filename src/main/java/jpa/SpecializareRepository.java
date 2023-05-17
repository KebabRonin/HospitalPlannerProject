package jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;

public class SpecializareRepository extends DataRepository<Specializare, Integer>{
    private static final String PERSISTENCE_UNIT_NAME = "ExamplePU";
    private EntityManagerFactory entityManagerFactory;

    public SpecializareRepository(EntityManager em) {
        super(em);
    }

    @Override
    protected Class<Specializare> getEntityClass() {
        return Specializare.class;
    }

    private EntityManager getEntityManager() {
        if (entityManagerFactory == null) {
            entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        }
        return entityManagerFactory.createEntityManager();
    }

    public void create(Specializare specializare) {
        EntityManager entityManager = getEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(specializare);
            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }
    }

    public Specializare findById(Integer id) {
        EntityManager entityManager = getEntityManager();
        try {
            return entityManager.find(Specializare.class, id);
        } finally {
            entityManager.close();
        }
    }

    public List<Specializare> findByName(String namePattern) {
        EntityManager entityManager = getEntityManager();
        try {
            TypedQuery<Specializare> query = entityManager.createNamedQuery("Specializare.findByName", Specializare.class);
            query.setParameter("denumire", "%" + namePattern + "%");
            return query.getResultList();
        } finally {
            entityManager.close();
        }
    }
}

