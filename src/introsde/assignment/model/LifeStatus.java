package introsde.assignment.model;

import introsde.assignment.dao.LifeCoachDao;
import introsde.assignment.model.MeasureDefinition;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.persistence.OneToOne;

/**
 * The persistent class for the "LifeStatus" database table.
 * 
 */
@Entity
@Table(name = "LifeStatus")
@NamedQuery(name = "LifeStatus.findAll", query = "SELECT l FROM LifeStatus l")
//@XmlRootElement(name="LifeStatus")


public class LifeStatus implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator="sqlite_lifestatus")
	@TableGenerator(name="sqlite_lifestatus", table="sqlite_sequence",
	    pkColumnName="name", valueColumnName="seq",
	    pkColumnValue="LifeStatus")
	@Column(name = "idMeasure")
	private int idMeasure;

	@Column(name = "value")
	private String value;
	
	@Column(name="timestamp")
	private String timestamp;
	
	@OneToOne
	@JoinColumn(name = "idMeasureDef", referencedColumnName = "idMeasureDef", insertable = true, updatable = true)
	private MeasureDefinition measureDefinition;
	
	@ManyToOne
	@JoinColumn(name="idPerson",referencedColumnName="idPerson")
	private Person person;

	public LifeStatus() {
	}
	
	
	public LifeStatus(MeasureDefinition def, String value){
		this.measureDefinition = def;
		this.value = value;
	}
	//@XmlTransient
	public int getIdMeasure() {
		return this.idMeasure;
	}

	public void setIdMeasure(int idMeasure) {
		this.idMeasure = idMeasure;
	}
	@XmlElement(name="measureValue")
	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@XmlElement(name="dateRegistered")
	public String getTimestamp(){
        return timestamp;
    }
	
	public void setTimestamp(String bd){
        this.timestamp = bd;
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
	public static LifeStatus getLifeStatusById(int lifestatusId) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		LifeStatus p = em.find(LifeStatus.class, lifestatusId);
		LifeCoachDao.instance.closeConnections(em);
		return p;
	}
	
	public static List<LifeStatus> getAll() {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
	    List<LifeStatus> list = em.createNamedQuery("LifeStatus.findAll", LifeStatus.class).getResultList();
	    LifeCoachDao.instance.closeConnections(em);
	    return list;
	}
	
	public static LifeStatus saveLifeStatus(LifeStatus p) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.persist(p);
		tx.commit();
	    LifeCoachDao.instance.closeConnections(em);
	    return p;
	}
	
	public static LifeStatus updateLifeStatus(LifeStatus p) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		p=em.merge(p);
		tx.commit();
	    LifeCoachDao.instance.closeConnections(em);
	    return p;
	}
	
	public static void removeLifeStatus(LifeStatus p) {
		EntityManager em = LifeCoachDao.instance.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
	    p=em.merge(p);
	    em.remove(p);
	    tx.commit();
	    LifeCoachDao.instance.closeConnections(em);
	    System.err.println("life status removed");
	}
	
	
	/**
	 * save a new measure object {lsNew} (e.g. weight) of Person identified by {id} and archive the old value in the history
	 * @param idPerson
	 * @param hmh
	 * @return
	 * @throws ParseException 
	 */
	public static LifeStatus savePersonMeasure(int idPerson, LifeStatus lsNew) throws ParseException{

    	boolean find = false;
    	Person p = Person.getPersonById(idPerson);
    	
    	String measureType = lsNew.getMeasureDefinition().getMeasureName();
    	
    	int idMeasureValue = getIdMeasureDefinition(measureType);
    	
    	List<LifeStatus> personLifeStatus = p.getLifeStatus();
    	
    	
    	for(int i = 0; i < personLifeStatus.size(); i++){
    		LifeStatus singleLifeStatus = personLifeStatus.get(i);
    		MeasureDefinition md = singleLifeStatus.getMeasureDefinition();
    		int measureDefinition = md.getIdMeasureDef();

    		if(measureDefinition == idMeasureValue){

    			find = true;
    			HealthMeasureHistory hmhNew = new HealthMeasureHistory();
    			
    			hmhNew.setValue(singleLifeStatus.getValue());
    			hmhNew.setTimestamp(lsNew.getTimestamp());
    			hmhNew.setPerson(p);
    			hmhNew.setMeasureDefinition(md);
    			
    			singleLifeStatus.setValue(lsNew.getValue());
    			singleLifeStatus.setTimestamp(lsNew.getTimestamp());
    			
    			LifeStatus newLifeStatus = LifeStatus.updateLifeStatus(singleLifeStatus);
    			
    			HealthMeasureHistory.saveHealthMeasureHistory(hmhNew);    			

    			return newLifeStatus;
    		}
    	}
    	
    		if(find == false && idMeasureValue != -1){

    			MeasureDefinition measureDef = new MeasureDefinition();
    			
    			LifeStatus singleNewLifeStatus = new LifeStatus();
    			
    			measureDef.setIdMeasureDef(idMeasureValue);
    			measureDef.setMeasureName(measureType);
    			
    			singleNewLifeStatus.setMeasureDefinition(measureDef);
    			singleNewLifeStatus.setPerson(p);
    			singleNewLifeStatus.setValue(lsNew.getValue());
    			
    			LifeStatus newLifeStatus = LifeStatus.saveLifeStatus(singleNewLifeStatus);

    			return newLifeStatus;
    	}

    	return null;
    	
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
	
}
