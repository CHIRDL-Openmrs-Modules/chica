/**
 * 
 */
package org.openmrs.module.chica.action;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
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
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.FormService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.result.EmptyResult;
import org.openmrs.logic.result.Result;
import org.openmrs.module.atd.datasource.TeleformExportXMLDatasource;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.Calculator;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chica.util.Util;
import org.openmrs.module.chirdlutilbackports.BaseStateActionHandler;
import org.openmrs.module.chirdlutilbackports.StateManager;
import org.openmrs.module.chirdlutilbackports.action.ProcessStateAction;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.StateAction;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

/**
 * @author tmdugan
 *
 */
public class ConsumeFormInstance implements ProcessStateAction
{	
	private static Log log = LogFactory.getLog(ConsumeFormInstance.class);


	/* (non-Javadoc)
	 * @see org.openmrs.module.chica.action.ProcessStateAction#processAction(org.openmrs.module.atd.hibernateBeans.StateAction, org.openmrs.Patient, org.openmrs.module.atd.hibernateBeans.PatientState, java.util.HashMap)
	 */
	public void processAction(StateAction stateAction, Patient patient,
			PatientState patientState, HashMap<String, Object> parameters)
	{
		long totalTime = System.currentTimeMillis();
		long startTime = System.currentTimeMillis();
		//lookup the patient again to avoid lazy initialization errors
		PatientService patientService = Context.getPatientService();
		Integer patientId = patient.getPatientId();
		patient = patientService.getPatient(patientId);
		
		Integer locationTagId = patientState.getLocationTagId();
		Integer locationId = patientState.getLocationId();

		State currState = patientState.getState();
		Integer sessionId = patientState.getSessionId();
		FormInstance formInstance = (FormInstance) parameters.get("formInstance");
		FormService formService = Context.getFormService();
		Form form = formService.getForm(formInstance.getFormId());
		patientState.setFormInstance(formInstance);
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		chirdlutilbackportsService.updatePatientState(patientState);
		startTime = System.currentTimeMillis();
		consume(sessionId,formInstance,patient,
				parameters,null,locationTagId);
		startTime = System.currentTimeMillis();
		StateManager.endState(patientState);
		System.out.println("Consume: Total time to consume "+form.getName()+": "+(System.currentTimeMillis()-totalTime));
		BaseStateActionHandler.changeState(patient, sessionId, currState,
				stateAction,parameters,locationTagId,locationId);

	}

	public void changeState(PatientState patientState,
			HashMap<String, Object> parameters) {
		//deliberately empty because processAction changes the state
	}
	
	public static void consume(Integer sessionId, FormInstance formInstance, Patient patient,
	                           HashMap<String, Object> parameters, List<FormField> fieldsToConsume, Integer locationTagId) {
		long totalTime = System.currentTimeMillis();
		long startTime = System.currentTimeMillis();
		AdministrationService adminService = Context.getAdministrationService();
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		ChicaService chicaService = Context.getService(ChicaService.class);
		Integer encounterId = chirdlutilbackportsService.getSession(sessionId).getEncounterId();
		String exportFilename = null;
		
		if (parameters != null) {
			exportFilename = (String) parameters.get("filename");
		}
		try {
			InputStream input = new FileInputStream(exportFilename);
			
			startTime = System.currentTimeMillis();
			chicaService.consume(input, patient, encounterId, formInstance, sessionId, fieldsToConsume, locationTagId);
			startTime = System.currentTimeMillis();
			input.close();
		}
		catch (Exception e) {
			log.error("Error consuming chica file: " + exportFilename);
			log.error(e.getMessage());
			log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}
		
		// save specific observations
		saveObs(encounterId, patient, locationTagId);
		System.out.println("ConsumeFormInstance.consume: time of saveObs: " + (System.currentTimeMillis() - startTime));
		startTime = System.currentTimeMillis();
		// remove the parsed xml from the xml datasource
		try {
			Integer purgeXMLDatasourceProperty = null;
			try {
				purgeXMLDatasourceProperty = Integer.parseInt(adminService.getGlobalProperty("atd.purgeXMLDatasource"));
			}
			catch (Exception e) {}
			LogicService logicService = Context.getLogicService();
			
			TeleformExportXMLDatasource xmlDatasource = (TeleformExportXMLDatasource) logicService.getLogicDataSource("xml");
			if (purgeXMLDatasourceProperty != null && purgeXMLDatasourceProperty == 1) {
				xmlDatasource.deleteParsedFile(formInstance);
			}
		}
		catch (Exception e) {
			log.error(e.getMessage());
			log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}
	}

	private static void saveObs(Integer encounterId, Patient patient, Integer locationTagId) {
		EncounterService encounterService = Context.getService(EncounterService.class);
		Encounter encounter = (Encounter) encounterService.getEncounter(encounterId);
		ObsService obsService = Context.getObsService();
		ATDService atdService = Context.getService(ATDService.class);
		List<org.openmrs.Encounter> encounters = new ArrayList<org.openmrs.Encounter>();
		encounters.add(encounter);
		List<Concept> questions = new ArrayList<Concept>();
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		Calculator calculator = new Calculator();
		parameters.put("encounterId", encounterId);
		ConceptService conceptService = Context.getConceptService();
		Concept concept = conceptService.getConcept("BMICentile");
		questions.add(concept);
		List<Obs> obs = obsService.getObservations(null, encounters, questions, null, null, null, null, null, null, null,
		    null, false);
		
		if (obs == null || obs.size() == 0) {
			Result result = atdService.evaluateRule("bmi", patient, parameters);
			
			if (!(result instanceof EmptyResult)) {
				Double percentile = calculator.calculatePercentile(result.toNumber(), patient.getGender(), patient
				        .getBirthdate(), "bmi", null);
				if (percentile != null) {
					percentile = org.openmrs.module.chirdlutil.util.Util.round(percentile, 2); // round percentile to two places

					org.openmrs.module.chica.util.Util.voidObsForConcept(concept,encounterId);					
					org.openmrs.module.chirdlutil.util.Util.saveObs(patient, concept, encounterId, percentile.toString(),new Date());
				}
			}
		}
		
		questions = new ArrayList<Concept>();
		concept = conceptService.getConcept("HCCentile");
		questions.add(concept);
		obs = obsService.getObservations(null, encounters, questions, null, null, null, null, null, null, null, null, false);
		
		if (obs == null || obs.size() == 0) {
			parameters.put("concept", "HC");
			Result result = atdService.evaluateRule("conceptRule", patient, parameters);
			if (!(result instanceof EmptyResult)) {
				Double percentile = calculator.calculatePercentile(result.toNumber(), patient.getGender(), patient
				        .getBirthdate(), "hc", null);
				if (percentile != null) {
					percentile = org.openmrs.module.chirdlutil.util.Util.round(percentile, 2); // round percentile to two places
					org.openmrs.module.chica.util.Util.voidObsForConcept(concept,encounterId);
					org.openmrs.module.chirdlutil.util.Util.saveObs(patient, concept, encounterId, percentile.toString(),new Date());
				}
			}
		}
		
		questions = new ArrayList<Concept>();
		concept = conceptService.getConcept("HtCentile");
		questions.add(concept);
		obs = obsService.getObservations(null, encounters, questions, null, null, null, null, null, null, null, null, false);
		
		if (obs == null || obs.size() == 0) {
			parameters.put("concept", "HEIGHT");
			Result result = atdService.evaluateRule("conceptRule", patient, parameters);
			if (!(result instanceof EmptyResult)) {
				Double percentile = calculator.calculatePercentile(result.toNumber(), patient.getGender(), patient
				        .getBirthdate(), "length", org.openmrs.module.chirdlutil.util.Util.MEASUREMENT_IN);
				if (percentile != null) {
					percentile = org.openmrs.module.chirdlutil.util.Util.round(percentile, 2); // round percentile to two places
					org.openmrs.module.chica.util.Util.voidObsForConcept(concept,encounterId);
					org.openmrs.module.chirdlutil.util.Util.saveObs(patient, concept, encounterId, percentile.toString(),new Date());
				}
			}
		}
		
		questions = new ArrayList<Concept>();
		concept = conceptService.getConcept("WtCentile");
		questions.add(concept);
		obs = obsService.getObservations(null, encounters, questions, null, null, null, null, null, null, null, null, false);
		
		if (obs == null || obs.size() == 0) {
			parameters.put("concept", "WEIGHT");
			Result result = atdService.evaluateRule("conceptRule", patient, parameters);
			if (!(result instanceof EmptyResult)) {
				Double percentile = calculator.calculatePercentile(result.toNumber(), patient.getGender(), patient
				        .getBirthdate(), "weight", org.openmrs.module.chirdlutil.util.Util.MEASUREMENT_LB);
				if (percentile != null) {
					percentile = org.openmrs.module.chirdlutil.util.Util.round(percentile, 2); // round percentile to two places
					org.openmrs.module.chica.util.Util.voidObsForConcept(concept,encounterId);
					org.openmrs.module.chirdlutil.util.Util.saveObs(patient, concept, encounterId, percentile.toString(),new Date());
				}
			}
		}
		
		//save BP
		questions = new ArrayList<Concept>();
		concept = conceptService.getConcept("BP");
		questions.add(concept);
		obs = obsService.getObservations(null, encounters, questions, null, null, null, null, null, null, null, null, false);
		
		if (obs == null || obs.size() == 0) {
			Result result = atdService.evaluateRule("bp", patient, parameters);
			if (!(result instanceof EmptyResult)) {
				org.openmrs.module.chica.util.Util.voidObsForConcept(concept,encounterId);
				org.openmrs.module.chirdlutil.util.Util.saveObs(patient, concept, encounterId, result.toString(),new Date());
			}
		}
		
		//save BMI
		questions = new ArrayList<Concept>();
		concept = conceptService.getConcept("BMI CHICA");
		questions.add(concept);
		obs = obsService.getObservations(null, encounters, questions, null, null, null, null, null, null, null, null, false);
		
		if (obs == null || obs.size() == 0) {
			Result result = atdService.evaluateRule("bmi", patient, parameters);
			if (!(result instanceof EmptyResult)) {
				org.openmrs.module.chica.util.Util.voidObsForConcept(concept,encounterId);
				org.openmrs.module.chirdlutil.util.Util.saveObs(patient, concept, encounterId, result.toString(),new Date());
			}
		}
	}
}
