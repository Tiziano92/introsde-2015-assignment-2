package introsde.rest.ehealth.resources;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import introsde.rest.ehealth.dao.LifeCoachDao;
import introsde.rest.ehealth.model.HealthMeasureHistory;
import introsde.rest.ehealth.model.LifeStatus;
import introsde.rest.ehealth.model.MeasureDefinition;
import introsde.rest.ehealth.model.Person;

@Stateless // only used if the the application is deployed in a Java EE container
@LocalBean // only used if the the application is deployed in a Java EE container
public class HealthMeasureHistoryResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    int id;
    int idMid;
    String measure;
    
    EntityManager entityManager = LifeCoachDao.instance.createEntityManager();

    
    public HealthMeasureHistoryResource(UriInfo uriInfo, Request request,int id, EntityManager em, String measure) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.id = id;
        this.measure = measure;
        this.entityManager = em;
    }

    public HealthMeasureHistoryResource(UriInfo uriInfo, Request request,int id, String measure) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.id = id;
        this.measure = measure;
    }
    
    public HealthMeasureHistoryResource(UriInfo uriInfo, Request request,int id, String measure, int idMid) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.id = id;
        this.measure = measure;
        this.idMid = idMid;
    }
    
    
 // Application integration
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public List<HealthMeasureHistory> getMeasureTypesValue() {
    	
    	int idMeasureValue = this.getIdMeasureDefinition();
    	
    	//System.out.println("idPerson --> "+this.id);
 	    //System.out.println("idMeasureDef --> "+idMeasureValue);
 	   List<HealthMeasureHistory> measureHistory;
 	    
 	    if(this.idMid != 0){
 	    	measureHistory = entityManager.createNamedQuery("HealthMeasureHistory.findAllMatchesByMid")
 	    	        .setParameter("idPerson", this.id)
 	    	        .setParameter("idMeasureDef", idMeasureValue)
 	    	        .setParameter("idMeasureMid", this.idMid)
 	    	        .getResultList();
 	    }
 	    else{
 	    	measureHistory = entityManager.createNamedQuery("HealthMeasureHistory.findAllMatches")
 	    	        .setParameter("idPerson", this.id)
 	    	        .setParameter("idMeasureDef", idMeasureValue)
 	    	        .getResultList();
 	    }

 	    return measureHistory;

    }
    


    /**
     * Get the id of measure specified my Name
     * @return
     */
    public int getIdMeasureDefinition() {
    	List<MeasureDefinition> measureDef = MeasureDefinition.getAll();
    	String singleMeasureDefinition;
    	
    	
    	for(int i = 0; i < measureDef.size(); i++)
    	{
    		singleMeasureDefinition = measureDef.get(i).getMeasureName();
    		if(singleMeasureDefinition.equals(this.measure)){
    			return measureDef.get(i).getIdMeasureDef();
    		}
    	}

        return -1;
    }
    
    @POST
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Consumes({MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML })
	public HealthMeasureHistory newStatus(HealthMeasureHistory hmh){

    	boolean find = false;
    	Person p = Person.getPersonById(this.id);
    	
    	int idMeasureValue = this.getIdMeasureDefinition();
    	
    	List<LifeStatus> ls = p.getLifeStatus();
    	
    	
    	for(int i = 0; i < ls.size(); i++){
    		LifeStatus singleLifeStatus = ls.get(i);
    		MeasureDefinition md = singleLifeStatus.getMeasureDefinition();
    		int measureDefinition = md.getIdMeasureDef();
    		
    		System.out.println("VALUE LIFE = "+ measureDefinition);
    		System.out.println("ID MEASURE = "+idMeasureValue);

    		if(measureDefinition == idMeasureValue){

    			find = true;
    			HealthMeasureHistory hmhNew = new HealthMeasureHistory();
    			
    			hmhNew.setValue(singleLifeStatus.getValue());
    			hmhNew.setTimestamp(hmh.getTimestamp());
    			hmhNew.setPerson(p);
    			hmhNew.setMeasureDefinition(md);
    			
    			singleLifeStatus.setValue(hmh.getValue());
    			System.out.println("INDEX = "+i);
    			
    			LifeStatus.updateLifeStatus(singleLifeStatus);
    			
    			
    			HealthMeasureHistory newHealthMeasureHistory = HealthMeasureHistory.saveHealthMeasureHistory(hmhNew);    			

    			return newHealthMeasureHistory;
    		}
    	}
    	
    		if(find == false && idMeasureValue != -1){
    			
    			Person person = Person.getPersonById(this.id);

    			HealthMeasureHistory hmhNew = new HealthMeasureHistory();
    			MeasureDefinition measureDef = new MeasureDefinition();
    			
    			LifeStatus singleNewLifeStatus = new LifeStatus();
    			
    			measureDef.setIdMeasureDef(idMeasureValue);
    			measureDef.setMeasureName(this.measure);
    			
    			//singleNewLifeStatus.setIdMeasure(idMeasureValue);
    			singleNewLifeStatus.setMeasureDefinition(measureDef);
    			singleNewLifeStatus.setPerson(person);
    			singleNewLifeStatus.setValue(hmh.getValue());
    			
    			LifeStatus.saveLifeStatus(singleNewLifeStatus);
    			
    			/*hmhNew.setValue("0");
    			hmhNew.setTimestamp("");
    			hmhNew.setPerson(p);
    			hmhNew.setMeasureDefinition(measureDef);
    			
    			HealthMeasureHistory.saveHealthMeasureHistory(hmhNew); */
    			return hmh;
    	}

    	return null;
    	
    }
    
    
    @PUT
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response putHealthMeasureHistory(@PathParam("mid") int mid, HealthMeasureHistory hmh) {
        
    	Response res = null;
    	
    	int idMeasureValue = this.getIdMeasureDefinition();
    	
    	List<HealthMeasureHistory> measureHistory;
    	
    	measureHistory = entityManager.createNamedQuery("HealthMeasureHistory.findAllMatchesByMid")
	    	        .setParameter("idPerson", this.id)
	    	        .setParameter("idMeasureDef", idMeasureValue)
	    	        .setParameter("idMeasureMid", this.idMid)
	    	        .getResultList();
    	
    	HealthMeasureHistory healthNew = measureHistory.get(0);
    	
    	if(measureHistory.get(0).getIdMeasureHistory() == mid){
    		
    		res = Response.created(uriInfo.getAbsolutePath()).build();
    		
    		healthNew.setValue(hmh.getValue());

    		HealthMeasureHistory.updateHealthMeasureHistory(healthNew);

    	}
    	else{
    		res = Response.noContent().build();
    	}
    	
    	return res;
  
    }
    
}