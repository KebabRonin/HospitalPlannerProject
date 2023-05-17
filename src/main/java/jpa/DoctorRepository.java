package jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;

public class DoctorRepository extends DataRepository<Doctor, Integer>{
    private static final String PERSISTENCE_UNIT_NAME = "ExamplePU";
    private EntityManagerFactory entityManagerFactory;

    public DoctorRepository(EntityManager em) {
        super(em);
    }

    @Override
    protected Class<Doctor> getEntityClass() {
        return Doctor.class;
    }

    private EntityManager getEntityManager() {
        if (entityManagerFactory == null) {
            entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        }
        return entityManagerFactory.createEntityManager();
    }

    public void create(Doctor doctor) {
        EntityManager entityManager = getEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(doctor);
            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }
    }

    public Doctor findById(Integer id) {
        EntityManager entityManager = getEntityManager();
        try {
            return entityManager.find(Doctor.class, id);
        } finally {
            entityManager.close();
        }
    }

    public List<Doctor> findByName(String namePattern) {
        EntityManager entityManager = getEntityManager();
        try {
            TypedQuery<Doctor> query = entityManager.createNamedQuery("Doctor.findByName", Doctor.class);
            query.setParameter("nume", "%" + namePattern + "%");
            return query.getResultList();
        } finally {
            entityManager.close();
        }
    }
}

