package edu.touro.mco152.bm.persist;

import Observer.ObserveFinishedRun;
import jakarta.persistence.EntityManager;

public class PersistObserver implements ObserveFinishedRun {
    @Override
    public void observeRun(DiskRun diskRun) {
        EntityManager em = EM.getEntityManager();
        em.getTransaction().begin();
        em.persist(diskRun);
        em.getTransaction().commit();
    }
}
