/**
 * 
 */
package org.openmrs.module.chica;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.FormField;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.result.EmptyResult;
import org.openmrs.logic.result.Result;
import org.openmrs.module.atd.BaseStateActionHandler;
import org.openmrs.module.atd.StateActionHandler;
import org.openmrs.module.atd.StateManager;
import org.openmrs.module.atd.TeleformFileMonitor;
import org.openmrs.module.atd.TeleformFileState;
import org.openmrs.module.atd.action.ProcessStateAction;
import org.openmrs.module.atd.datasource.TeleformExportXMLDatasource;
import org.openmrs.module.atd.hibernateBeans.ATDError;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.Program;
import org.openmrs.module.atd.hibernateBeans.State;
import org.openmrs.module.atd.hibernateBeans.StateAction;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.advice.ThreadManager;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chica.util.Util;

/**
 * @author tmdugan
 * 
 */
public class ChicaStateActionHandler extends BaseStateActionHandler
{
	private static Log log = LogFactory.getLog(ChicaStateActionHandler.class);
	private static ChicaStateActionHandler stateActionHandler = null;
	
	public void fillUnfinishedStates()
	{
		AdministrationService adminService = Context.getAdministrationService();
		Context.authenticate(adminService
				.getGlobalProperty("scheduler.username"), adminService
				.getGlobalProperty("scheduler.password"));

		ATDService atdService = Context.getService(ATDService.class);
		Calendar todaysDate = Calendar.getInstance();
		todaysDate.set(Calendar.HOUR_OF_DAY, 0);
		todaysDate.set(Calendar.MINUTE, 0);
		todaysDate.set(Calendar.SECOND, 0);
		LocationService locationService = Context.getLocationService();

		List<Location> locations = locationService.getAllLocations();
		
		for(Location location:locations){
		
		Set<LocationTag> tags = location.getTags();
		
		if(tags != null){
		
			for(LocationTag tag:tags){
				Integer locationId = location.getLocationId();
				Integer locationTagId = tag.getLocationTagId();
		List<PatientState> unfinishedStatesToday = atdService.
			getUnfinishedPatientStatesAllPatients(todaysDate.getTime(),locationTagId,locationId);
				
		int numUnfinishedStates = unfinishedStatesToday.size();
		double processedStates = 0;
		
		log.info("fillUnfinishedStates(): Starting Today's state initialization....");
		for(PatientState currPatientState:unfinishedStatesToday)
		{	
			State state = currPatientState.getState();
			if (state != null)
			{
				StateAction stateAction = state.getAction();

				try
				{
					if (stateAction!=null&&stateAction.getActionName().equalsIgnoreCase(
							"CONSUME FORM INSTANCE"))
					{
						TeleformFileState teleformFileState = TeleformFileMonitor
							.addToPendingStatesWithoutFilename(
								currPatientState.getFormInstance());
						teleformFileState.addParameter("patientState",
								currPatientState);
					}
					HashMap<String,Object> parameters = new HashMap<String,Object>();
					parameters.put("formInstance", currPatientState.getFormInstance());
					processAction(stateAction, currPatientState.getPatient(),
							currPatientState,parameters);
				} catch (Exception e)
				{
					log.error(e.getMessage());
					log
							.error(org.openmrs.module.chirdlutil.util.Util
									.getStackTrace(e));
				}
			}
			if(processedStates%100==0){
				log.info("State initialization is: "+(int)((processedStates/numUnfinishedStates)*100)+"% complete. "+
						processedStates+" out of "+numUnfinishedStates+" processed.");
			}
			processedStates++;
		}
		
		log.info("Today's state initialization is: "+(int)((processedStates/numUnfinishedStates)*100)+"% complete.");
		}}}
		Thread thread = new Thread(new InitializeOldStates());
		ThreadManager.startThread(thread);
	}
	
	public static ChicaStateActionHandler getInstance()
	{
		if(stateActionHandler == null){
			stateActionHandler = new ChicaStateActionHandler();
		}
		return stateActionHandler;
	}
	
	//deliberately public for scheduled task configuration
	public ChicaStateActionHandler(){
		
	}
		
	public synchronized void changeState(PatientState patientState,
			HashMap<String,Object> parameters){
		StateAction stateAction = patientState.getState().getAction();
		if (stateAction == null)
		{
			return;
		}

		ProcessStateAction processStateAction = loadProcessStateAction(stateAction);

		if (processStateAction != null)
		{
			processStateAction.changeState(patientState, parameters);
		}	
	}

	public static synchronized void consume(Integer sessionId,FormInstance formInstance,Patient patient,
			HashMap<String,Object> parameters,List<FormField> fieldsToConsume,
			Integer locationTagId)
	{
		long totalTime = System.currentTimeMillis();
		long startTime = System.currentTimeMillis();
		AdministrationService adminService = Context.getAdministrationService();
		ATDService atdService = Context.getService(ATDService.class);
		ChicaService chicaService = Context.getService(ChicaService.class);
		Integer encounterId = atdService.getSession(sessionId).getEncounterId();
		String exportFilename = null;
		
		if(parameters != null){
			exportFilename = (String) parameters.get("filename");
		}
		try
		{
			InputStream input = new FileInputStream(exportFilename);
			
			startTime = System.currentTimeMillis();
			chicaService.consume(input,patient,encounterId,
					formInstance,sessionId,fieldsToConsume,locationTagId);
			startTime = System.currentTimeMillis();
			input.close();
		} catch (Exception e)
		{
			log.error("Error consuming chica file: " + exportFilename);
			log.error(e.getMessage());
			log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}
		
		// save specific observations
		saveObs(encounterId, patient,locationTagId);
		System.out.println("chicaStateActionHandler.consume: time of saveObs: "+
			(System.currentTimeMillis()-startTime));
		startTime = System.currentTimeMillis();
		// remove the parsed xml from the xml datasource
		try
		{
			Integer purgeXMLDatasourceProperty = null;
			try
			{
				purgeXMLDatasourceProperty = Integer.parseInt(adminService
						.getGlobalProperty("atd.purgeXMLDatasource"));
			} catch (Exception e)
			{
			}
			LogicService logicService = Context.getLogicService();

			TeleformExportXMLDatasource xmlDatasource = (TeleformExportXMLDatasource) logicService
					.getLogicDataSource("xml");
			if (purgeXMLDatasourceProperty != null
					&& purgeXMLDatasourceProperty == 1)
			{
				xmlDatasource.deleteParsedFile(formInstance);
			}
		} catch (Exception e)
		{
			log.error(e.getMessage());
			log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}
	}
	
	private static synchronized void saveObs(Integer encounterId,Patient patient,
			Integer locationTagId){
		EncounterService encounterService = Context.getService(EncounterService.class);
		Encounter encounter = (Encounter) encounterService.getEncounter(encounterId);
		ObsService obsService = Context.getObsService();
		ATDService atdService = Context.getService(ATDService.class);
		List<org.openmrs.Encounter> encounters = new ArrayList<org.openmrs.Encounter>();
		encounters.add(encounter);
		List<Concept> questions = new ArrayList<Concept>();
		HashMap<String,Object> parameters = new HashMap<String,Object>();
		Calculator calculator = new Calculator();
		parameters.put("encounterId", encounterId);
		ConceptService conceptService = Context.getConceptService();
		Concept concept = conceptService.getConcept("BMICentile");
		questions.add(concept);
		List<Obs> obs = obsService.getObservations(null, encounters, questions, null, null, null, null,
				null, null, null, null, false);
		
		if (obs == null || obs.size() == 0)
		{
			Result result = atdService.evaluateRule("bmi", patient, parameters,
					null);

			if (!(result instanceof EmptyResult))
			{
				Double percentile = calculator.calculatePercentile(result
						.toNumber(), patient.getGender(), patient
						.getBirthdate(), "bmi", null);
				if (percentile != null)
				{
					percentile = org.openmrs.module.chirdlutil.util.Util.round(
							percentile, 2); // round percentile to two places
					Util.saveObs(patient, concept, encounterId, percentile
							.toString(), null,null,locationTagId);
				}
			}
		}
		
		questions = new ArrayList<Concept>();
		concept = conceptService.getConcept("HCCentile");
		questions.add(concept);
		obs = obsService.getObservations(null, encounters, questions, null, null, null, null,
				null, null, null, null, false);
		
		if(obs == null || obs.size()==0){
			parameters.put("concept", "HC");
			Result result = atdService.evaluateRule("conceptRule", patient, parameters, null);
			if (!(result instanceof EmptyResult))
			{
				Double percentile = calculator.calculatePercentile(result
						.toNumber(), patient.getGender(), patient
						.getBirthdate(), "hc", null);
				if (percentile != null)
				{
					percentile = org.openmrs.module.chirdlutil.util.Util.round(
							percentile, 2); // round percentile to two places
					Util.saveObs(patient, concept, encounterId, percentile
							.toString(), null,null,locationTagId);
				}
			}
		}
		
		questions = new ArrayList<Concept>();
		concept = conceptService.getConcept("HtCentile");
		questions.add(concept);
		obs = obsService.getObservations(null, encounters, questions, null, null, null, null,
				null, null, null, null, false);
		
		if(obs == null || obs.size()==0){
			parameters.put("concept", "HEIGHT");
			Result result = atdService.evaluateRule("conceptRule", patient, parameters, null);
			if (!(result instanceof EmptyResult))
			{
				Double percentile = calculator.calculatePercentile(result
						.toNumber(), patient.getGender(), patient
						.getBirthdate(), "length", org.openmrs.module.chirdlutil.util.Util.MEASUREMENT_IN);
				if (percentile != null)
				{
					percentile = org.openmrs.module.chirdlutil.util.Util.round(
							percentile, 2); // round percentile to two places
					Util.saveObs(patient, concept, encounterId, percentile
							.toString() , null,null,locationTagId);
				}
			}
		}
		
		questions = new ArrayList<Concept>();
		concept = conceptService.getConcept("WtCentile");
		questions.add(concept);
		obs = obsService.getObservations(null, encounters, questions, null, null, null, null,
				null, null, null, null, false);
		
		if(obs == null || obs.size()==0){
			parameters.put("concept", "WEIGHT");
			Result result = atdService.evaluateRule("conceptRule", patient, parameters, null);
			if (!(result instanceof EmptyResult))
			{
				Double percentile = calculator.calculatePercentile(result
						.toNumber(), patient.getGender(), patient
						.getBirthdate(), "weight", 
						org.openmrs.module.chirdlutil.util.Util.MEASUREMENT_LB);
				if (percentile != null)
				{
					percentile = org.openmrs.module.chirdlutil.util.Util.round(
							percentile, 2); // round percentile to two places
					Util.saveObs(patient, concept, encounterId, percentile
							.toString(), null,null,locationTagId);
				}
			}
		}
		
		//save BP
		questions = new ArrayList<Concept>();
		concept = conceptService.getConcept("BP");
		questions.add(concept);
		obs = obsService.getObservations(null, encounters, questions, null, null, null, null,
				null, null, null, null, false);
		
		if(obs == null || obs.size()==0){
			Result result = atdService.evaluateRule("bp", patient, parameters, null);
			if (!(result instanceof EmptyResult))
			{
				Util.saveObs(patient, concept, encounterId, result
							.toString(), null,
							null,locationTagId);
			}
		}
		
		//save BMI
		questions = new ArrayList<Concept>();
		concept = conceptService.getConcept("BMI CHICA");
		questions.add(concept);
		obs = obsService.getObservations(null, encounters, questions, null, null, null, null,
				null, null, null, null, false);
		
		if(obs == null || obs.size()==0){
			Result result = atdService.evaluateRule("bmi", patient, parameters, null);
			if (!(result instanceof EmptyResult))
			{
				Util.saveObs(patient, concept, encounterId, result
							.toString(), null,
							null,locationTagId);
			}
		}
	}
	
	public static synchronized void changeState(Patient patient, Integer sessionId,
			State currState,StateAction action,
			HashMap<String,Object> parameters,
			Integer locationTagId,Integer locationId)
	{
		ATDService atdService = Context.getService(ATDService.class);
		List<ATDError> errors = null;
		// change to error state if fatal error exists for session
		//only look up errors for consume state, for now
		if (action!=null&&action.getActionName().equalsIgnoreCase("CONSUME FORM INSTANCE"))
		{
			errors = atdService.getATDErrorsByLevel(
					"Fatal", sessionId);
		}
		if (errors != null && errors.size() > 0)
		{
			//open an error state
			currState = atdService.getStateByName("ErrorState");
			atdService.addPatientState(patient,
					currState, sessionId,locationTagId,locationId);
		} else
		{
			Program program = atdService.getProgram(locationTagId,locationId);
			StateManager.changeState(patient, sessionId, currState,program,
					parameters,locationTagId,locationId,ChicaStateActionHandler.getInstance());
		}

	}
}
