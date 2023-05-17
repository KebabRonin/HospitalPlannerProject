package jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.print.Doc;

public class Main {
    public static void testJPA(){
        EntityManagerFactory emf =
                Persistence.createEntityManagerFactory("ExamplePU");
        EntityManager em = emf.createEntityManager();
//        Doctor d = new Doctor();
//        d.setId(8);d.setName("dsadas");d.setPrenume("dassadsaf");
//        Specializare s = new Specializare();
//        s.setId(10);s.setDenumire("sdadsadsa");
        em.getTransaction().begin();
//        em.persist(d);
//        em.persist(s);
        Cabinet cabinet = new Cabinet("C210");
        em.persist(cabinet);

        Cabinet c = (Cabinet) em.createQuery(
                        "select e from Cabinet e where e.denumire='C210'")
                .getSingleResult();
        System.out.println(c.getDenumire());
        em.getTransaction().commit();
        em.close();
        emf.close();
    }
    public static void main(String[] args) {
        //testJPA();
//        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("ExamplePU");
//
//        EntityManager entityManager = entityManagerFactory.createEntityManager();
//
//        Cabinet cabinet = new Cabinet("dsadsdasd");
//
//        CabinetRepository cabinetRepository = new CabinetRepository(entityManager);
//
//        entityManager.getTransaction().begin();
//        cabinetRepository.create(cabinet);
//        entityManager.getTransaction().commit();
//
//        Cabinet cabinet1 = cabinetRepository.findById(cabinet.getId());
//
//        System.out.println(cabinet1.getDenumire());
//        entityManager.close();
//        entityManagerFactory.close();
    }
}

