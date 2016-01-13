package introsde.assignment.model;

import java.io.Serializable;
import java.text.ParseException;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import introsde.assignment.dao.LifeCoachDao;

/**
 * The persistent class for the "LifeStatus" database table.
 * 
 */
@Entity
@Table(name = "Goal")
@NamedQueries({
	@NamedQuery(name="Goal.findAllMatchesByIdPerson", query="SELECT g FROM Goal g WHERE g.person.idPerson = :idPerson"),
	@NamedQuery(name="Goal.findAllMatchesByIdPersonMeasure", query="SELECT g FROM Goal g WHERE g.person.idPerson = :idPerson AND g.measureDefinition.idMeasureDef = :idMeasureDef"),
	//@NamedQuery(name="HealthMeasureHistory.findAllMatchesByMidUpdate", query="SELECT h FROM HealthMeasureHistory h WHERE h.person.idPerson = :idPerson AND h.idMeasureHistory = :idMeasureMid"),
})
@NamedQuery(name = "Goal.findAll", query = "SELECT g FROM Goal g")
public class Goal implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator="sqlite_goal")
	@TableGenerator(name="sqlite_goal", table="sqlite_sequence",
	    pkColumnName="name", valueColumnName="seq",
	    pkColumnValue="Goal")
	@Column(name = "idGoal")
	private int idGoal;

	@Column(name = "value")
	private String value;
	
	@OneToOne
	@JoinColumn(name = "idMeasureDef", referencedColumnName = "idMeasureDef", insertable = true, updatable = true)
	private MeasureDefinition measureDefinition;
	
	@ManyToOne
	@JoinColumn(name="idPerson",referencedColumnName="idPerson")
	private Person person;

	public Goal() {
	}
	
	
	public Goal(MeasureDefinition def, String value){
		this.measureDefinition = def;
		this.value = value;
	}
	//@XmlTransient
	public int getIdGoal() {
		return this.idGoal;
	}

	public void setIdGoal(int idGoal) {
		this.idGoal = idGoal;
	}
	@XmlElement(name="goalValue")
	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@XmlElement(name="measureType")
	public MeasureDefinition getMeasureDefinition() {
		return measureDefinition;
	}
	
	//@XmlElement(name="measureType")
	public void setMeasureDefinition(MeasureDefinition param) {
		this.measureDefinition = param;
	}

	// we make this transient for JAXB to avoid and infinite loop on serialization
	@XmlTransient
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}
	
	// Database operations
	// Notice that, for this example, we create and destroy and entityManager on each operation. 
	// How would you change the DAO to not having to create the entity manager every time? 
	public static Goal getGoalById(int goalId) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		Goal l = em.find(Goal.class, goalId);
		LifeCoachDao.instance.closeConnections(em);
		return l;
	}
	
	public static List<Goal> getAll() {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
	    List<Goal> list = em.createNamedQuery("Goal.findAll", Goal.class).getResultList();
	    LifeCoachDao.instance.closeConnections(em);
	    return list;
	}
	
	public static Goal getGoalByIdPersonMeasure(int idPerson, String measure) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		
		int idMeasureDef = getIdMeasureDefinition(measure);
		
	    List<Goal> list = em.createNamedQuery("Goal.findAllMatchesByIdPersonMeasure", Goal.class)
        .setParameter("idPerson", idPerson)
        .setParameter("idMeasureDef", idMeasureDef)
        .getResultList();
	    
	    LifeCoachDao.instance.closeConnections(em);
	    return list.get(0);
	}
	
	public static Goal saveGoal(Goal g) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.persist(g);
		tx.commit();
	    LifeCoachDao.instance.closeConnections(em);
	    return g;
	}
	
	public static Goal updateGoal(Goal g) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		g=em.merge(g);
		tx.commit();
	    LifeCoachDao.instance.closeConnections(em);
	    return g;
	}
	
	public static void removeGoal(Goal g) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
	    g=em.merge(g);
	    em.remove(g);
	    tx.commit();
	    LifeCoachDao.instance.closeConnections(em);
	    System.err.println("Goal removed");
	}
	
	/**
	 * return the list of the history of a specific measurement of a specific person id
	 * @param id
	 * @param measureName
	 * @return
	 */
	public static List<Goal> getAllByIdPerson(int id) {
		
		EntityManager em = LifeCoachDao.instance.createEntityManager();
	    List<Goal> list = em.createNamedQuery("Goal.findAllMatchesByIdPerson")
	    	        .setParameter("idPerson", id)
	    	        .getResultList();
	    LifeCoachDao.instance.closeConnections(em);
	    
	    return list;
	}
	
	
	/**
	 * Return the id of a specified Measure name
	 * @param measure
	 * @return
	 */
	public static int getIdMeasureDefinition(String measure) {
    	List<MeasureDefinition> measureDef = MeasureDefinition.getAll();
    	String singleMeasureDefinition;

    	for(int i = 0; i < measureDef.size(); i++)
    	{
    		singleMeasureDefinition = measureDef.get(i).getMeasureName();
    		if(singleMeasureDefinition.equals(measure)){
    			return measureDef.get(i).getIdMeasureDef();
    		}
    	}

        return -1;
    }
	
	
	/**
	 * save a new goal object {goalNew} (e.g. weight) of Person identified by {idPerson}
	 * @param idPerson
	 * @param goalNew
	 * @return
	 * @throws ParseException 
	 */
	public static Goal savePersonGoal(int idPerson, Goal goalNew) throws ParseException{

    	Person p = Person.getPersonById(idPerson);
    	
    	String measureType = goalNew.getMeasureDefinition().getMeasureName();
    	
    	int idMeasureValue = getIdMeasureDefinition(measureType);
    	
    	MeasureDefinition measureDef = new MeasureDefinition();
    			
    	Goal singleNewGoal = new Goal();
    			
    	measureDef.setIdMeasureDef(idMeasureValue);
    	measureDef.setMeasureName(measureType);
    			
    	singleNewGoal.setMeasureDefinition(measureDef);
    	singleNewGoal.setPerson(p);
    	singleNewGoal.setValue(goalNew.getValue());
    			
    	Goal newGoal = Goal.saveGoal(singleNewGoal);

    	return newGoal;
	
    }
	
	
	/**
	 * update a goal object {goalUpate} (e.g. weight) of Person identified by {idPerson}
	 * @param idPerson
	 * @param goalUpdate
	 * @return
	 * @throws ParseException 
	 */
	public static Goal updatePersonGoal(int idPerson, Goal goalUpdate) throws ParseException{
		
		Person p = Person.getPersonById(idPerson);
    	
    	String measureType = goalUpdate.getMeasureDefinition().getMeasureName();
    	
    	int idMeasureValue = getIdMeasureDefinition(measureType);
		
    	List<Goal> personGoals = p.getGoal();
    	
    	for(int i = 0; i < personGoals.size(); i++){
    		Goal singleGoal = personGoals.get(i);
    		MeasureDefinition md = singleGoal.getMeasureDefinition();
    		int measureDefinition = md.getIdMeasureDef();
    		System.err.println("measureDefinition = "+measureDefinition);

    		if(measureDefinition == idMeasureValue){
    			
    			singleGoal.setValue(goalUpdate.getValue());
    			
    			Goal newGoal = Goal.updateGoal(singleGoal); 			

    			return newGoal;
    		}
    	}
		goalUpdate.setIdGoal(-1);
		return goalUpdate;
		
	}
	
}


