package introsde.assignment.soap;
import java.text.ParseException;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.WebResult;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import introsde.assignment.model.CustomMeasureDefinition;
import introsde.assignment.model.Goal;
import introsde.assignment.model.HealthMeasureHistory;
import introsde.assignment.model.LifeStatus;
import introsde.assignment.model.Person;

@WebService
@SOAPBinding(style = Style.DOCUMENT, use=Use.LITERAL) //optional
public interface People {
	
	/**
	 * Return the list of all the people in the database
	 * @return
	 */
	@WebMethod(operationName="readPersonList")
    @WebResult(name="person") 
    public List<Person> getPeople();
	
	/**
	 * Return the person specified by an id
	 * @param id
	 * @return
	 */
    @WebMethod(operationName="readPerson")
    @WebResult(name="person") 
    public Person readPerson(@WebParam(name="personId") int id);

    /**
     * Update the information about a specified life status
     * @param person
     * @return
     * @throws ParseException 
     */
    @WebMethod(operationName="savePersonMeasure")
    @WebResult(name="newValueMeasurement") 
    public LifeStatus savePersonMeasure(@WebParam(name="personId") int idPerson, @WebParam(name="lifeStatus") LifeStatus ls) throws ParseException;

    /**
	 * return the goals of a specific person identified by Id
	 * @param idPerson
	 * @return
	 */
    @WebMethod(operationName="readPersonGoals")
    @WebResult(name="Goal") 
    public List<Goal> readPersonGoals(@WebParam(name="personId") int id);

    
    /**
     * Update a goal and return the information related to the updated goal
     * @param person
     * @return
     * @throws ParseException 
     */
    @WebMethod(operationName="updatePersonGoal")
    @WebResult(name="updateGoal") 
    public Goal updatePersonGoal(@WebParam(name="personId") int idPerson, @WebParam(name="singleGoal") Goal goal) throws ParseException;
}