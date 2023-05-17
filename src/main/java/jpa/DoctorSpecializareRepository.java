package jpa;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

public class DoctorSpecializareRepository {
    private static final String PERSISTENCE_UNIT_NAME = "ExamplePU";
    private EntityManagerFactory entityManagerFactory;

    public DoctorSpecializareRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    private EntityManager getEntityManager() {
        if (entityManagerFactory == null) {
            entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        }
        return entityManagerFactory.createEntityManager();
    }

    public void create(ProgramDoctor doctorSpecializare) {
        EntityManager entityManager = getEntityManager();
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(doctorSpecializare);
            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }
    }

    public List<ProgramDoctor> findByDoctorId(int doctor_id) {
        EntityManager entityManager = getEntityManager();
        try {
            TypedQuery<ProgramDoctor> query = entityManager.createQuery(
                    "SELECT ag FROM DoctorSpecializare ag WHERE ag.doctor_id = :doctor_id", ProgramDoctor.class);
            query.setParameter("doctor_id", doctor_id);
            return query.getResultList();
        } finally {
            entityManager.close();
        }
    }

    public List<ProgramDoctor> findBySpecializareId(int specializare_id) {
        EntityManager entityManager = getEntityManager();
        try {
            TypedQuery<ProgramDoctor> query = entityManager.createQuery(
                    "SELECT ag FROM DoctorSpecializare ag WHERE ag.specializare_id = :specializare_id", ProgramDoctor.class);
            query.setParameter("specializare_id", specializare_id);
            return query.getResultList();
        } finally {
            entityManager.close();
        }
    }
}


