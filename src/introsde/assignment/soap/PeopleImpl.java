package introsde.assignment.soap;
import java.text.ParseException;
import java.util.List;

import javax.jws.WebService;

import introsde.assignment.model.CustomMeasureDefinition;
import introsde.assignment.model.HealthMeasureHistory;
import introsde.assignment.model.LifeStatus;
import introsde.assignment.model.Person;
import introsde.assignment.model.Goal;

//Service Implementation

@WebService(endpointInterface = "introsde.assignment.soap.People",
    serviceName="People")
public class PeopleImpl implements People {
	
	 //Method #1 => readPersonList()
	 @Override
	    public List<Person> getPeople() {
	        return Person.getAll();
	    }

	 
	//Method #2 => readPerson(Long id)
    @Override
    public Person readPerson(int id) {
        System.out.println("---> Reading Person by id = "+id);
        Person p = Person.getPersonById(id);
        if (p!=null) {
            System.out.println("---> Found Person by id = "+id+" => "+p.getName());
        } else {
            System.out.println("---> Didn't find any Person with  id = "+id);
        }
        
        return p;
    }
    
    //Method #10 => updatePersonMeasure(Long id, Measure m)
    @Override
    public HealthMeasureHistory updatePersonMeasure(int idPerson, HealthMeasureHistory hmh) {
    	return HealthMeasureHistory.updateHealthMeasureHistory(idPerson, hmh);
    }
    
    //Method #11 => readPersonGoals(int idPerson)
    @Override
    public List<Goal> readPersonGoals(int idPerson) {
    	return Goal.getAllByIdPerson(idPerson);
    }
    
    //Method #14 => updatePersonGoal(int idPerson, Goal g)
    @Override
    public Goal updatePersonGoal(int idPerson, Goal g) throws ParseException {
    	return Goal.updatePersonGoal(idPerson, g);
    }

}