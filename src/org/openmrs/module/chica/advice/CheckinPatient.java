/**
 * 
 */
package org.openmrs.module.chica.advice;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chica.util.Util;
import org.openmrs.module.chirdlutilbackports.BaseStateActionHandler;
import org.openmrs.module.chirdlutilbackports.StateManager;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Program;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Session;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable;

/**
 * @author tmdugan
 * 
 */
public class CheckinPatient implements ChirdlRunnable
{
	private Log log = LogFactory.getLog(this.getClass());
	private org.openmrs.Encounter encounter = null;
	private HashMap<String,Object> parameters = null;

	public CheckinPatient(org.openmrs.Encounter encounter, HashMap<String,Object> parameters)
	{
		this.encounter = encounter;
		this.parameters = parameters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		log.info("Started execution of " + getName() + "("+ Thread.currentThread().getName() + ", " + 
			new Timestamp(new Date().getTime()) + ")");
		Context.openSession();
		
		try
		{
			AdministrationService adminService = Context.getAdministrationService();
			Context.authenticate(adminService
					.getGlobalProperty("scheduler.username"), adminService
					.getGlobalProperty("scheduler.password"));
			ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);

			Patient patient = this.encounter.getPatient();
			Hibernate.initialize(patient); //fully initialize the patient to
				//prevent lazy initialization errors

			Integer encounterId = this.encounter.getEncounterId();
			
			//The session is unique because only 1 session exists at checkin
			List<Session> sessions = chirdlutilbackportsService.getSessionsByEncounter(encounterId);
			Session session = sessions.get(0);
			
			Integer sessionId = session.getSessionId();
			
			Integer locationTagId = null;
			Integer locationId = null;
			
			// lookup location tag id by printer location
			org.openmrs.module.chica.service.EncounterService encounterService = Context
					.getService(org.openmrs.module.chica.service.EncounterService.class);
			org.openmrs.module.chica.hibernateBeans.Encounter chicaEncounter = (Encounter) encounterService
					.getEncounter(this.encounter.getEncounterId());
			String printerLocation = chicaEncounter.getPrinterLocation();
			if (printerLocation != null)
			{
				Location location = this.encounter.getLocation();
				Set<LocationTag> tags = location.getTags();
				for(LocationTag tag:tags){
					if(printerLocation.equalsIgnoreCase(tag.getTag())){
						locationTagId = tag.getLocationTagId();
						locationId = location.getLocationId();
						break;
					}
				}
			}
		
			String sendingApplication =(String) parameters.get("sendingApplication");
			String sendingFacility = (String) parameters.get("sendingFacility");
			saveInsuranceInfo(encounterId, patient,locationTagId,sendingApplication,sendingFacility);
			Program program = chirdlutilbackportsService.getProgram(locationTagId,locationId);
			StateManager.changeState(patient, sessionId, null,
					program,null,
					locationTagId,locationId,BaseStateActionHandler.getInstance());
		} catch (Exception e)
		{
			this.log.error(e.getMessage());
			this.log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}finally{
			Context.closeSession();
			log.info("Finished execution of " + getName() + "("+ Thread.currentThread().getName() + ", " + 
				new Timestamp(new Date().getTime()) + ")");
		}
	}
	
	/**
	 * @see org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable#getName()
	 */
    public String getName() {
	    return "Checkin Patient (Encounter: " + encounter.getEncounterId() + ")";
    }

	/**
	 * @see org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable#getPriority()
	 */
    public int getPriority() {
	    return ChirdlRunnable.PRIORITY_ONE;
    }

    private void saveInsuranceInfo(Integer encounterId, Patient patient,Integer locationTagId, String sendingApplication, String sendingFacility)
    {
    	ChicaService chicaService = Context
    	.getService(ChicaService.class);
    	EncounterService encounterService = Context
    	.getService(EncounterService.class);
    	Encounter encounter = (Encounter) encounterService
    	.getEncounter(encounterId);
    	ObsService obsService = Context.getObsService();
    	List<org.openmrs.Encounter> encounters = new ArrayList<org.openmrs.Encounter>();
    	encounters.add(encounter);
    	List<Concept> questions = new ArrayList<Concept>();
    	ConceptService conceptService = Context.getConceptService();

    	String carrierCode = encounter.getInsuranceCarrierCode();
    	String smsCode = encounter.getInsuranceSmsCode();
    	String planCode = encounter.getInsurancePlanCode();
    	Concept concept = conceptService.getConceptByName("InsuranceName");
    	List<Person> persons = new ArrayList<Person>();
    	persons.add(patient);
    	questions = new ArrayList<Concept>();
    	questions.add(concept);
    	List<Obs> obs = obsService.getObservations(persons, null, questions, null, null, null, null, null, null, encounter.getEncounterDatetime(), encounter.getEncounterDatetime(),
    			false);

    	String insuranceName = null;
    	if (obs!=null && obs.size() == 1)
    	{
    		insuranceName = obs.get(0).getValueText();
    	}
    	String category = null;

		if (sendingApplication != null || sendingFacility != null) {
			
			//for McKesson Pecar messages
			if (carrierCode != null && carrierCode.length() > 0) {
				category = chicaService.getInsCategoryByCarrier(carrierCode, sendingApplication, sendingFacility);
				
			}
			
			if (category == null) {
				// for McKesson PCC messages
				if (planCode != null && planCode.length() > 0) {
					category = chicaService.getInsCategoryByInsCode(planCode, sendingApplication, sendingFacility);
				}
			}
			
			if (category == null) {
				// for SMS PCC messages
				if (smsCode != null && smsCode.length() > 0) {
					category = chicaService.getInsCategoryByInsCode(smsCode, sendingApplication, sendingFacility);
				}
			}
			
			//look up by ECW name
			if (category == null) {
				
				if (insuranceName != null && insuranceName.length() > 0) {
					category = chicaService.getInsCategoryByName(insuranceName, sendingApplication, sendingFacility);
				}
				
			}
		}

    	if (category != null)
    	{
    		concept = conceptService.getConcept("Insurance");
    		org.openmrs.module.chirdlutil.util.Util.saveObs(patient, concept, encounterId, category, 
    				encounter.getEncounterDatetime());
    	}else{
    		log.error("Could not map code: plan code: "+planCode+" insurance Name: "+ insuranceName+
    			" sending application: "+sendingApplication+" sending facility: "+sendingFacility);
    	}

    }
}
