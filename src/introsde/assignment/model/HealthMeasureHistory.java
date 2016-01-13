package introsde.assignment.model;

import introsde.assignment.dao.LifeCoachDao;
import introsde.assignment.model.Person;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;





/**
 * The persistent class for the "HealthMeasureHistory" database table.
 * 
 */
@Entity
@Table(name="HealthMeasureHistory")
@NamedQueries({
	@NamedQuery(name="HealthMeasureHistory.findAllMatches", query="SELECT h FROM HealthMeasureHistory h WHERE h.person.idPerson = :idPerson AND h.measureDefinition.idMeasureDef = :idMeasureDef"),
	@NamedQuery(name="HealthMeasureHistory.findAllMatchesByMid", query="SELECT h FROM HealthMeasureHistory h WHERE h.person.idPerson = :idPerson AND h.measureDefinition.idMeasureDef = :idMeasureDef AND h.idMeasureHistory = :idMeasureMid"),
	@NamedQuery(name="HealthMeasureHistory.findAllMatchesByMidUpdate", query="SELECT h FROM HealthMeasureHistory h WHERE h.person.idPerson = :idPerson AND h.idMeasureHistory = :idMeasureMid"),
})
@XmlType(propOrder = {"idMeasureHistory", "timestamp","value","measureDefinition"})

//@XmlRootElement(name="measure")
public class HealthMeasureHistory implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator="sqlite_mhistory")
	@TableGenerator(name="sqlite_mhistory", table="sqlite_sequence",
	    pkColumnName="name", valueColumnName="seq",
	    pkColumnValue="HealthMeasureHistory")
	@Column(name="idMeasureHistory")
	private int idMeasureHistory;

	@Column(name="timestamp")
	private String timestamp;


	@Column(name="value")
	private String value;

	@ManyToOne
	@JoinColumn(name = "idMeasureDef", referencedColumnName = "idMeasureDef")
	private MeasureDefinition measureDefinition;

	// notice that we haven't included a reference to the history in Person
	// this means that we don't have to make this attribute XmlTransient
	@ManyToOne
	@JoinColumn(name = "idPerson", referencedColumnName = "idPerson")
	private Person person;
	
	public HealthMeasureHistory() {
	}

	@XmlElement(name="mid")
	public int getIdMeasureHistory() {
		return this.idMeasureHistory;
	}

	public void setIdMeasureHistory(int idMeasureHistory) {
		this.idMeasureHistory = idMeasureHistory;
	}
	
	@XmlElement(name="dateRegistered")
	public String getTimestamp(){
        return timestamp;
    }

	
	public void setTimestamp(String bd){
        this.timestamp = bd;
    }

	@XmlElement(name="measureValue")
	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	//@XmlTransient
	public MeasureDefinition getMeasureDefinition() {
	    return measureDefinition;
	}

	public void setMeasureDefinition(MeasureDefinition param) {
	    this.measureDefinition = param;
	}

	@XmlTransient
	public Person getPerson() {
	    return person;
	}

	public void setPerson(Person param) {
	    this.person = param;
	}


	// database operations
	public static HealthMeasureHistory getHealthMeasureHistoryById(int id) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		HealthMeasureHistory p = em.find(HealthMeasureHistory.class, id);
		LifeCoachDao.instance.closeConnections(em);
		return p;
	}
	
	public static List<HealthMeasureHistory> getAll() {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
	    List<HealthMeasureHistory> list = em.createNamedQuery("HealthMeasureHistory.findAll", HealthMeasureHistory.class).getResultList();
	    LifeCoachDao.instance.closeConnections(em);
	    return list;
	}
	
	/*public static List<HealthMeasureHistory> getAllByName(int id, int idMeasure) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		em.get
	    List<HealthMeasureHistory> list = em.createNamedQuery("HealthMeasureHistory.findAllMatches")
	    	        .setParameter("idPerson", id)
	    	        .setParameter("idMeasureDef", idMeasure)
	    	        .getResultList();
	    LifeCoachDao.instance.closeConnections(em);
	    return list;
	}
	
	public static List<HealthMeasureHistory> getAllByNameByMid(int id, int idMeasure, int mid) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
	    List<HealthMeasureHistory> list = em.createNamedQuery("HealthMeasureHistory.findAllMatchesByMid")
	    	        .setParameter("idPerson", id)
	    	        .setParameter("idMeasureDef", idMeasure)
	    	        .setParameter("idMeasureMid", mid)
	    	        .getResultList();
	    LifeCoachDao.instance.closeConnections(em);
	    return list;
	}*/
	
	public static HealthMeasureHistory saveHealthMeasureHistory(HealthMeasureHistory p) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.persist(p);
		tx.commit();
	    LifeCoachDao.instance.closeConnections(em);
	    return p;
	}
	
	public static HealthMeasureHistory updateHealthMeasureHistory(HealthMeasureHistory p) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		p=em.merge(p);
		tx.commit();
	    LifeCoachDao.instance.closeConnections(em);
	    return p;
	}
	
	public static void removeHealthMeasureHistory(HealthMeasureHistory p) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
	    p=em.merge(p);
	    em.remove(p);
	    tx.commit();
	    LifeCoachDao.instance.closeConnections(em);
	}
	
	
	/**
	 * return the list of the history of a specific measurement of a specific person id
	 * @param id
	 * @param measureName
	 * @return
	 */
	public static List<HealthMeasureHistory> getAllByName(int id, String measureName) {
		
		int idMeasure = getIdMeasureDefinition(measureName);
		
		EntityManager em = LifeCoachDao.instance.createEntityManager();
	    List<HealthMeasureHistory> list = em.createNamedQuery("HealthMeasureHistory.findAllMatches")
	    	        .setParameter("idPerson", id)
	    	        .setParameter("idMeasureDef", idMeasure)
	    	        .getResultList();
	    LifeCoachDao.instance.closeConnections(em);
	    
	    return list;
	}
	
	
	/**
	 * return the value of {measureType} (e.g. weight) identified by {mid} for Person identified by {id}
	 * @param id
	 * @param idMeasure
	 * @param mid
	 * @return
	 */
	public static HealthMeasureHistory getAllByNameByMid(int id, String measureType, int mid) {
		
		int idMeasure = getIdMeasureDefinition(measureType);
		
		EntityManager em = LifeCoachDao.instance.createEntityManager();
	    List<HealthMeasureHistory> list = em.createNamedQuery("HealthMeasureHistory.findAllMatchesByMid")
	    	        .setParameter("idPerson", id)
	    	        .setParameter("idMeasureDef", idMeasure)
	    	        .setParameter("idMeasureMid", mid)
	    	        .getResultList();
	    LifeCoachDao.instance.closeConnections(em);
	    
	    return list.get(0);
	}
	
	
	/**
	 * should update the measure identified with {m.mid}, related to the Person identified by {id}
	 * @param idPerson
	 * @param hmh
	 * @return
	 */
	public static HealthMeasureHistory updateHealthMeasureHistory(int idPerson, HealthMeasureHistory hmh) {
    	
    	List<HealthMeasureHistory> measureHistory;
    	
    	EntityManager em = LifeCoachDao.instance.createEntityManager();
    	
    	measureHistory = em.createNamedQuery("HealthMeasureHistory.findAllMatchesByMidUpdate")
	    	        .setParameter("idPerson", idPerson)
	    	        .setParameter("idMeasureMid", hmh.getIdMeasureHistory())
	    	        .getResultList();
    	LifeCoachDao.instance.closeConnections(em);
    	
    	HealthMeasureHistory healthNew = measureHistory.get(0);
    	healthNew.setValue(hmh.getValue());
    	
    	return HealthMeasureHistory.updateHealthMeasureHistory(healthNew);
  
    }
	
	
	/**
	 * return the id associated to a specific measurement
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
	
	
}