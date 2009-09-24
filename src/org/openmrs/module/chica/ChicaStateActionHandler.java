/**
 * 
 */
package org.openmrs.module.chica;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Form;
import org.openmrs.FormField;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.FormService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.result.EmptyResult;
import org.openmrs.logic.result.Result;
import org.openmrs.module.atd.StateActionHandler;
import org.openmrs.module.atd.StateManager;
import org.openmrs.module.atd.TeleformFileMonitor;
import org.openmrs.module.atd.TeleformFileState;
import org.openmrs.module.atd.datasource.TeleformExportXMLDatasource;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.Program;
import org.openmrs.module.atd.hibernateBeans.Session;
import org.openmrs.module.atd.hibernateBeans.State;
import org.openmrs.module.atd.hibernateBeans.StateAction;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.advice.ThreadManager;
import org.openmrs.module.chica.datasource.ObsChicaDatasource;
import org.openmrs.module.chica.hibernateBeans.ChicaError;
import org.openmrs.module.chica.hibernateBeans.ChicaHL7Export;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.hibernateBeans.Statistics;
import org.openmrs.module.chica.hibernateBeans.Study;
import org.openmrs.module.chica.hibernateBeans.StudyAttributeValue;
import org.openmrs.module.chica.randomizer.BasicRandomizer;
import org.openmrs.module.chica.randomizer.Randomizer;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chica.util.Util;
import org.openmrs.module.dss.util.IOUtil;

/**
 * @author tmdugan
 * 
 */
public class ChicaStateActionHandler implements StateActionHandler
{
	private Log log = LogFactory.getLog(this.getClass());
	private Program program = null;
	private static ChicaStateActionHandler stateActionHandler = null;
	
	public void fillUnfinishedStates()
	{
		AdministrationService adminService = Context.getAdministrationService();
		Context.authenticate(adminService
				.getGlobalProperty("scheduler.username"), adminService
				.getGlobalProperty("scheduler.password"));
		if (StateManager.getStateActionHandler() == null)
		{
			StateManager
					.setStateActionHandler(ChicaStateActionHandler.getInstance());
		}
		ATDService atdService = Context.getService(ATDService.class);
		Calendar todaysDate = Calendar.getInstance();
		todaysDate.set(Calendar.HOUR_OF_DAY, 0);
		todaysDate.set(Calendar.MINUTE, 0);
		todaysDate.set(Calendar.SECOND, 0);
		List<PatientState> unfinishedStatesToday = atdService.getUnfinishedPatientStatesAllPatients(todaysDate.getTime());
				
		int numUnfinishedStates = unfinishedStatesToday.size();
		double processedStates = 0;
		
		this.log.info("fillUnfinishedStates(): Starting Today's state initialization....");
		for(PatientState currPatientState:unfinishedStatesToday)
		{	
			State state = currPatientState.getState();
			if (state != null)
			{
				StateAction stateAction = state.getAction();

				try
				{
					if (stateAction.getActionName().equalsIgnoreCase(
							"CONSUME FORM INSTANCE"))
					{
						PatientState prevProduceState = atdService.getPrevPatientStateByAction(currPatientState.getSessionId(), 
								currPatientState.getPatientStateId(),"PRODUCE FORM INSTANCE");
						if (prevProduceState != null)
						{
							Integer formId = prevProduceState.getFormId();
							Integer formInstanceId = prevProduceState.getFormInstanceId();
							TeleformFileState teleformFileState = TeleformFileMonitor
									.addToPendingStatesWithoutFilename(formId,
											formInstanceId);
							teleformFileState.addParameter("patientState",
									currPatientState);
						}else{
							log.error("Patient State: "+currPatientState.getPatientStateId()+" with action: "+
									stateAction.getActionName()+" could not be processed. No previous produce state.");
						}
					}
					processAction(stateAction, currPatientState.getPatient(),
							currPatientState,null);
				} catch (Exception e)
				{
					log.error(e.getMessage());
					log
							.error(org.openmrs.module.dss.util.Util
									.getStackTrace(e));
				}
			}
			if(processedStates%100==0){
				this.log.info("State initialization is: "+(int)((processedStates/numUnfinishedStates)*100)+"% complete. "+
						processedStates+" out of "+numUnfinishedStates+" processed.");
			}
			processedStates++;
		}
		
		this.log.info("Today's state initialization is: "+(int)((processedStates/numUnfinishedStates)*100)+"% complete.");
	
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
	
	public ChicaStateActionHandler(){
		AdministrationService adminService = Context.getAdministrationService();
		ATDService atdService = Context.getService(ATDService.class);
		this.program = atdService.getProgramByNameVersion(adminService.getGlobalProperty("chica.programName"),
				adminService.getGlobalProperty("chica.programVersion"));
	}
	
	public synchronized void processAction(StateAction stateAction, Patient patient,
			PatientState patientState,HashMap<String,Object> parameters) throws Exception
	{
		if(stateAction == null){
			return;
		}
		String action = stateAction.getActionName();
		
		//lookup the patient again to avoid lazy initialization errors
		PatientService patientService = Context.getPatientService();
		Integer patientId = patient.getPatientId();
		patient = patientService.getPatient(patientId);
		
		

		ChicaService chicaService = Context
				.getService(ChicaService.class);
		ATDService atdService = Context
				.getService(ATDService.class);
		State currState = patientState.getState();
		Integer sessionId = patientState.getSessionId();
		PatientState stateWithFormId = chicaService.getPrevProducePatientState(sessionId, 
				patientState.getPatientStateId());

		Integer formId = null;
		Integer formInstanceId = null;
		String exportDirectory = null;
		
		formId = patientState.getFormId();
		formInstanceId = patientState.getFormInstanceId();
		
		if(formId == null&&stateWithFormId != null)
		{
			formId = stateWithFormId.getFormId();
			formInstanceId = stateWithFormId.getFormInstanceId();
		}
		
		if(formId != null)
		{
			exportDirectory = IOUtil
					.formatDirectoryName(org.openmrs.module.atd.util.Util
							.getFormAttributeValue(formId,
									"defaultExportDirectory"));
		}
		
		Session session = atdService.getSession(sessionId);
		Integer encounterId = session.getEncounterId();
		
		if (action.equalsIgnoreCase("PRODUCE FORM INSTANCE"))
		{
			// write the form
			formInstanceId = atdService.addFormInstance(formId)
					.getFormInstanceId();
			patientState.setFormInstanceId(formInstanceId);
			String mergeDirectory = IOUtil
					.formatDirectoryName(org.openmrs.module.atd.util.Util
							.getFormAttributeValue(formId,
									"pendingMergeDirectory"));
			
			String mergeFilename = mergeDirectory + formInstanceId + ".xml";
			int maxDssElements = org.openmrs.module.chica.util.Util
					.getMaxDssElements(formId);
			String dsstype = org.openmrs.module.chica.util.Util
					.getDssType(currState.getName());
			FileOutputStream output = new FileOutputStream(mergeFilename);
			chicaService.produce(output, patientState, patient, encounterId,
					dsstype, maxDssElements);
			output.flush();
			output.close();
			LogicService logicService = Context.getLogicService();

			ObsChicaDatasource xmlDatasource = (ObsChicaDatasource) logicService
					.getLogicDataSource("RMRS");

			//clear the in-memory obs from the MRF dump at the end of each
			//produce state
			xmlDatasource.deleteRegenObsByPatientId(patientId);
			
			StateManager.endState(patientState);
			changeState(patient, sessionId, currState,stateAction,parameters);

			// update statistics
			List<Statistics> statistics = chicaService.getStatByFormInstance(
					formInstanceId, dsstype);

			for (Statistics currStat : statistics)
			{
				currStat.setPrintedTimestamp(patientState.getEndTime());
				chicaService.updateStatistics(currStat);
			}
			
			return;
		}
		
		if (action.equalsIgnoreCase("REPRINT"))
		{
			action = "PRODUCE FORM INSTANCE";
			PatientState patientStateProduce = atdService.getPatientStateByEncounterFormAction(
					encounterId, formId, action);

			if (patientStateProduce != null)
			{
				formInstanceId = patientStateProduce.getFormInstanceId();
				String mergeDirectory = IOUtil
				.formatDirectoryName(org.openmrs.module.atd.util.Util
						.getFormAttributeValue(formId,
								"defaultMergeDirectory"));
				if (mergeDirectory != null)
				{
					File dir = new File(mergeDirectory);
					for (String fileName : dir.list())
					{
						if (fileName.startsWith(formInstanceId + "."))
						{
							fileName = mergeDirectory + "/" + fileName;
							IOUtil.renameFile(fileName, fileName.substring(0,
									fileName.lastIndexOf("."))
									+ ".xml");
							StateManager.endState(patientState);
							break;
						}
					}
				} else
				{
					log.error("Reprint failed for patient: "
							+ patient.getPatientId()
							+ " because merge directory was null.");
				}
			}
			return;
		}

		if (action.equalsIgnoreCase("RESCAN"))
		{
			FormService formService = Context.getFormService();
			Form form = formService.getForm(formId);
			String formName = form.getName();
			List<Statistics> stats = null;
			if (formName != null && formName.equals("PSF"))
			{
				// void non question related obs for PSF
				stats = chicaService.getStatsByEncounterFormNotPrioritized(
						encounterId, formName);
				
				//void BMICentile, HCCentile, HtCentile, WtCentile, and	BP
				voidObsForConcept("BMICentile",encounterId);
				voidObsForConcept("HCCentile",encounterId);
				voidObsForConcept("HtCentile",encounterId);
				voidObsForConcept("WtCentile",encounterId);
				voidObsForConcept("BP",encounterId);
			} else
			{
				// void all obs for PWS
				stats = chicaService.getStatsByEncounterForm(encounterId,
						formName);
			}

			// void obs from previous scan
			ObsService obsService = Context.getObsService();
			for (Statistics currStat : stats)
			{
				Integer obsId = currStat.getObsvId();
				Obs obs = obsService.getObs(obsId);
				obsService.voidObs(obs, "voided due to rescan");
			}

			consume(sessionId, formInstanceId, formId, patient,
					exportDirectory, parameters,
					null);
			StateManager.endState(patientState);
			
			//start a new session if this was a PSF_RESCAN
			if (patientState.getState().getName()
					.equalsIgnoreCase("PSF_rescan"))
			{
				Session newSession = atdService.addSession();
				sessionId = newSession.getSessionId();
				newSession.setEncounterId(encounterId);
				atdService.updateSession(newSession);
				
				changeState(patient, sessionId, currState, stateAction,parameters);
			}
			return;
		}
		
		if (action.equalsIgnoreCase("CONSUME FORM INSTANCE"))
		{
			consume(sessionId,formInstanceId,formId,patient,
					exportDirectory,parameters,null);
			StateManager.endState(patientState);
			changeState(patient, sessionId, currState,stateAction,parameters);
			
			return;
		}

		if (action.equalsIgnoreCase("WAIT FOR PRINT"))
		{
			String mergeDirectory = IOUtil
					.formatDirectoryName(org.openmrs.module.atd.util.Util
							.getFormAttributeValue(formId,
									"defaultMergeDirectory"));
			TeleformFileState teleformFileState = TeleformFileMonitor.addToPendingStatesWithFilename(formId, formInstanceId, 
					mergeDirectory+formInstanceId+".20");
			teleformFileState.addParameter("patientState", patientState);
			return;
		}

		if (action.equalsIgnoreCase("WAIT FOR SCAN"))
		{
			TeleformFileState teleformFileState = TeleformFileMonitor.addToPendingStatesWithoutFilename(formId, formInstanceId);
			teleformFileState.addParameter("patientState", patientState);
			return;
		}

		if (action.equalsIgnoreCase("QUERY KITE"))
		{
			if (patient.getPatientIdentifier() == null)
			{
				log.error("Could not query kite. MRN is null.");
			} else
			{
				try
				{
					QueryKite.mrfQuery(patient.getPatientIdentifier()
							.getIdentifier(), patient.getPatientId());
				}catch (QueryKiteException e){
					ChicaError ce = e.getChicaError();
					ce.setSessionId(sessionId);
					chicaService.saveError(ce);
					
				}catch (Exception e)
				{
					log.error("Error querying kite");
					log.error(e.getMessage());
					log.error(org.openmrs.module.dss.util.Util
									.getStackTrace(e));
				}
			}
			StateManager.endState(patientState);
			changeState(patient, sessionId, currState,stateAction,parameters);
			return;
		}

		if (action.equalsIgnoreCase("RANDOMIZE"))
		{
			List<Study> activeStudies = chicaService.getActiveStudies();

			for (Study currActiveStudy : activeStudies)
			{
				StudyAttributeValue studyAttributeValue = chicaService
						.getStudyAttributeValue(currActiveStudy,
								"Custom Randomizer");

				Randomizer randomizer = null;

				if (studyAttributeValue != null)
				{
					String randomizerClassName = "org.openmrs.module.chica.randomizer."+
						studyAttributeValue.getValue();

					try
					{
						Class theClass = Class.forName(randomizerClassName);
						randomizer = (Randomizer) theClass.newInstance();
					} catch (Exception e)
					{
						log.error("Error creating custom randomizer: "
								+ randomizerClassName);
						log.error(e.getMessage());
						log.error(org.openmrs.module.dss.util.Util.getStackTrace(e));
					}
				} else
				{
					randomizer = new BasicRandomizer();
				}

				if (randomizer != null)
				{
					EncounterService encounterService = Context.getService(EncounterService.class);
					randomizer.randomize(currActiveStudy, patient, encounterService.getEncounter(encounterId));
				}
			}
			StateManager.endState(patientState);
			changeState(patient, sessionId, currState,stateAction,parameters);
			return;
		}
		
		if (action.equalsIgnoreCase("LOAD HL7 EXPORT QUEUE")){
			//TODO:add encounter to queue
			ChicaHL7Export export = new ChicaHL7Export();
			export.setDateInserted(new Date());
			export.setEncounterId(encounterId);
			export.setVoided(false);
			export.setStatus(1);
			chicaService.insertEncounterToHL7ExportQueue(export);
			
			StateManager.endState(patientState);
			changeState(patient, sessionId, currState,stateAction,parameters);
		}
	}

	private void consume(Integer sessionId,Integer formInstanceId, 
			Integer formId,Patient patient,String exportDirectory,
			HashMap<String,Object> parameters,List<FormField> fieldsToConsume)
	{
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
			
			chicaService.consume(input,patient,encounterId,formId,formInstanceId,sessionId,fieldsToConsume);
			input.close();
		} catch (Exception e)
		{
			log.error("Error consuming chica file: " + exportFilename);
			log.error(e.getMessage());
			log.error(org.openmrs.module.dss.util.Util.getStackTrace(e));
		}
		
		// save specific observations
		saveObs(encounterId, patient,formInstanceId,formId);

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
				xmlDatasource.deleteParsedFile(formInstanceId, formId);
			}
		} catch (Exception e)
		{
			log.error(e.getMessage());
			log.error(org.openmrs.module.dss.util.Util.getStackTrace(e));
		}
	}
	
	private void voidObsForConcept(String conceptName,Integer encounterId){
		EncounterService encounterService = Context.getService(EncounterService.class);
		Encounter encounter = (Encounter) encounterService.getEncounter(encounterId);
		ObsService obsService = Context.getObsService();
		List<org.openmrs.Encounter> encounters = new ArrayList<org.openmrs.Encounter>();
		encounters.add(encounter);
		List<Concept> questions = new ArrayList<Concept>();
		
		ConceptService conceptService = Context.getConceptService();
		Concept concept = conceptService.getConcept(conceptName);
		questions.add(concept);
		List<Obs> obs = obsService.getObservations(null, encounters, questions, null, null, null, null,
				null, null, null, null, false);
		
		for(Obs currObs:obs){
			obsService.voidObs(currObs, "voided due to rescan");
		}
	}
	
	private void saveObs(Integer encounterId,Patient patient,Integer formInstanceId,
			Integer formId){
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
					percentile = org.openmrs.module.dss.util.Util.round(
							percentile, 2); // round percentile to two places
					Util.saveObs(patient, concept, encounterId, percentile
							.toString(), null,
							null,null);
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
					percentile = org.openmrs.module.dss.util.Util.round(
							percentile, 2); // round percentile to two places
					Util.saveObs(patient, concept, encounterId, percentile
							.toString(), null,
							null,null);
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
						.getBirthdate(), "length", org.openmrs.module.dss.util.Util.MEASUREMENT_IN);
				if (percentile != null)
				{
					percentile = org.openmrs.module.dss.util.Util.round(
							percentile, 2); // round percentile to two places
					Util.saveObs(patient, concept, encounterId, percentile
							.toString() , null,
							null,null);
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
						org.openmrs.module.dss.util.Util.MEASUREMENT_LB);
				if (percentile != null)
				{
					percentile = org.openmrs.module.dss.util.Util.round(
							percentile, 2); // round percentile to two places
					Util.saveObs(patient, concept, encounterId, percentile
							.toString(), null,
							null,null);
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
							.toString(), formInstanceId,
							null,formId);
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
							.toString(), formInstanceId,
							null,formId);
			}
		}
	}
	
	public synchronized void processState(PatientState patientState,
			HashMap<String,Object> parameters)
	{
		ChicaService chicaService = Context.getService(ChicaService.class);

		try
		{
			Integer sessionId = patientState.getSessionId();
			PatientState stateWithFormId = chicaService.getPrevProducePatientState(sessionId, 
					patientState.getPatientStateId());

			Integer formInstanceId = null;
			
			if(stateWithFormId != null)
			{
				formInstanceId = stateWithFormId.getFormInstanceId();
			}

			State state = patientState.getState();
			StateAction stateAction = state.getAction();
			
			
			if (stateAction.getActionName().equalsIgnoreCase("WAIT FOR PRINT"))
			{
				processPrintedState(patientState,parameters);
			}
			if (stateAction.getActionName().equalsIgnoreCase("WAIT FOR SCAN"))
			{
				processScanState(patientState,formInstanceId,parameters);
			}
			if(stateAction.getActionName().equalsIgnoreCase("RESCAN")){
				processAction(stateAction,patientState.getPatient(),patientState,parameters);
			}
		} catch (Exception e)
		{
			log.error(e);
			log.error(org.openmrs.module.dss.util.Util.getStackTrace(e));
		}
	}

	public synchronized void processPrintedState(PatientState patientState,
			HashMap<String,Object> parameters)
			throws APIException, Exception
	{
		StateManager.endState(patientState);
		changeState(patientState.getPatient(), patientState
				.getSessionId(), patientState.getState(),patientState.getState().getAction(),parameters);

	}

	public synchronized void processScanState(PatientState patientState,
			Integer formInstanceId,HashMap<String,Object> parameters) throws APIException, Exception
	{
		ChicaService chicaService = Context
				.getService(ChicaService.class);

		StateManager.endState(patientState);

		String dsstype = org.openmrs.module.chica.util.Util
				.getDssType(patientState.getState().getName());

		List<Statistics> statistics = chicaService.getStatByFormInstance(
				formInstanceId, dsstype);

		for (Statistics currStat : statistics)
		{
			currStat.setScannedTimestamp(patientState.getEndTime());
			chicaService.updateStatistics(currStat);
		}
		
		changeState(patientState.getPatient(), patientState
				.getSessionId(), patientState.getState(),patientState.getState().getAction(),parameters);
	}
	
	public synchronized void changeState(Patient patient, Integer sessionId,
			State currState,StateAction action,HashMap<String,Object> parameters) throws Exception
	{
		ATDService atdService = Context.getService(ATDService.class);
		ChicaService chicaService = Context.getService(ChicaService.class);
		List<ChicaError> errors = null;
		// change to error state if fatal error exists for session
		//only look up errors for consume state, for now
		if (action!=null&&action.getActionName().equalsIgnoreCase("CONSUME FORM INSTANCE"))
		{
			errors = chicaService.getChicaErrorsByLevel(
					"Fatal", sessionId);
		}
		if (errors != null && errors.size() > 0)
		{
			//open an error state
			currState = atdService.getStateByName("ErrorState");
			atdService.addPatientState(patient,
					currState, sessionId, null);
		} else
		{
			StateManager.changeState(patient, sessionId, currState,program,parameters);
		}

	}

	public Program getProgram()
	{
		return this.program;
	}

	public void setProgram(Program program)
	{
		this.program = program;
	}
}
