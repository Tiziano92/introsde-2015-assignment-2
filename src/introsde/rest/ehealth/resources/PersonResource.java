package introsde.rest.ehealth.resources;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import introsde.rest.ehealth.model.LifeStatus;
import introsde.rest.ehealth.model.Person;

@Stateless // only used if the the application is deployed in a Java EE container
@LocalBean // only used if the the application is deployed in a Java EE container
public class PersonResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    int id;

    EntityManager entityManager; // only used if the application is deployed in a Java EE container

    public PersonResource(UriInfo uriInfo, Request request,int id, EntityManager em) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.id = id;
        this.entityManager = em;
    }

    public PersonResource(UriInfo uriInfo, Request request,int id) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.id = id;
    }

    // Application integration
    @GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response getPerson() {
	       Person person = this.getPersonById(id);
	       if (person == null){
	        return Response.status(404).build();
	       }
	       return Response.ok(person).build();
	   }

    // for the browser
    @GET
    @Produces(MediaType.TEXT_XML)
    public Response getPersonHTML() {
	       Person person = this.getPersonById(id);
	       if (person == null){
	        return Response.status(404).build();
	       }
	       return Response.ok(person).build();
	   }

    @PUT
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response putPerson(Person person) {
        Response res;
        Person existing = getPersonById(this.id);
        Person newPerson = getPersonById(this.id);

        if (existing == null) {
            res = Response.noContent().build();
        } else {
            res = Response.created(uriInfo.getAbsolutePath()).build();
            newPerson.setIdPerson(this.id);
            
            if (person.getName() == null){
            	newPerson.setName(existing.getName());
            }
            else if (!(person.getName().equals(existing.getName()))){
            	newPerson.setName(person.getName());
            }
            else{
            	newPerson.setName(existing.getName());
            }
            
            if (person.getBirthdate() == null){
            	newPerson.setBirthdate(existing.getBirthdate());
            }
            else if (!(person.getBirthdate().equals(existing.getBirthdate()))){
            	newPerson.setBirthdate(person.getBirthdate());
            }
            else{
            	newPerson.setBirthdate(existing.getBirthdate());
            }
            
            if (person.getLastname() == null){
            	newPerson.setLastname(existing.getLastname());
            }
            else if (!(person.getLastname().equals(existing.getLastname()))){
            	newPerson.setLastname(person.getLastname());
            }
            else{
            	newPerson.setLastname(existing.getLastname());
            }
            
            if (person.getEmail() == null){
            	newPerson.setEmail(existing.getEmail());
            }
            else if (!(person.getEmail().equals(existing.getEmail()))){
            	newPerson.setEmail(person.getEmail());
            }
            else{
            	newPerson.setEmail(existing.getEmail());
            }
            
            if (person.getUsername() == null){
            	newPerson.setUsername(existing.getUsername());
            }
            else if (!(person.getUsername().equals(existing.getUsername()))){
            	newPerson.setUsername(person.getUsername());
            }
            else{
            	newPerson.setUsername(existing.getUsername());
            }
            
            Person.updatePerson(newPerson);
        }
        return res;
    }

    @DELETE
    public void deletePerson() {
        Person c = getPersonById(id);
        if (c == null)
            throw new RuntimeException("Delete: Person with " + id
                    + " not found");
        Person.removePerson(c);
        
       
    }

    public Person getPersonById(int personId) {
        //System.out.println("Reading person from DB with id: "+personId);

        // this will work within a Java EE container, where not DAO will be needed
        //Person person = entityManager.find(Person.class, personId); 

        Person person = Person.getPersonById(personId);
        //System.out.println("Person: "+person.toString());
        return person;
    }
    
    
    
}