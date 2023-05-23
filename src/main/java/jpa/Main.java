package jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Main {
    public static void testJPA(){
        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("ExamplePU");
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();
        Pacient pacient = new Pacient("The","Beatles");
        em.persist(pacient);

        Pacient a = (Pacient) em.createQuery(
                        "select e from Pacient e where e.prenume='Beatles'")
                .getSingleResult();
        a.setPrenume("The Beatles");
        em.getTransaction().commit();
        em.close();
        emf.close();
    }
    public static void main(String[] args) {
        testJPA();
//        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("ExamplePU");
//
//        EntityManager entityManager = entityManagerFactory.createEntityManager();
//
//        Pacient pacient = new Pacient("Mircea","Aldanu", "20/10/2002", "Galati,Galati", "0757650548", "numanumaiei@mama.marti","parola");
//
//        PacientRepository pacientRepository = new PacientRepository(entityManager);
//
//        entityManager.getTransaction().begin();
//        pacientRepository.create(pacient);
//        entityManager.getTransaction().commit();
//
//        Pacient pacient1 = pacientRepository.findById(pacient.getId());
//
//        System.out.println(pacient1.getEmail());
//        entityManager.close();
//        entityManagerFactory.close();
    }
}

