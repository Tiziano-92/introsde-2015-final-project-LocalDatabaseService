package introsde.assignment.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public enum LifeCoachDao {
	instance;
	private EntityManagerFactory emf;
	
	private LifeCoachDao() {
		if (emf!=null) {
			emf.close();
		}
		emf = Persistence.createEntityManagerFactory("introsde-jpa");
	}
	
	public EntityManager createEntityManager() {
		return emf.createEntityManager();
	}

	public void closeConnections(EntityManager em) {
		em.close();
	}

	public EntityTransaction getTransaction(EntityManager em) {
		return em.getTransaction();
	}
	
	public EntityManagerFactory getEntityManagerFactory() {
		return emf;
	}

}