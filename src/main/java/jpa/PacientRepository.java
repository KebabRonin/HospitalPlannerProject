package jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;

public class PacientRepository extends DataRepository<Pacient, Integer>{
    private static final String PERSISTENCE_UNIT_NAME = "ExamplePU";
    private EntityManagerFactory entityManagerFactory;

    public PacientRepository(EntityManager em) {
        super(em);
    }

    @Override
    protected Class<Pacient> getEntityClass() {
        return Pacient.class;
    }

    private EntityManager getEntityManager() {
        if (entityManagerFactory == null) {
            entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        }
        return entityManagerFactory.createEntityManager();
    }

    public void create(Pacient pacient) {
        EntityManager entityManager = getEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(pacient);
            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }
    }

    public Pacient findById(Integer id) {
        EntityManager entityManager = getEntityManager();
        try {
            return entityManager.find(Pacient.class, id);
        } finally {
            entityManager.close();
        }
    }

    public List<Pacient> findByName(String namePattern) {
        EntityManager entityManager = getEntityManager();
        try {
            TypedQuery<Pacient> query = entityManager.createNamedQuery("Pacient.findByName", Pacient.class);
            query.setParameter("nume", "%" + namePattern + "%");
            return query.getResultList();
        } finally {
            entityManager.close();
        }
    }
}

