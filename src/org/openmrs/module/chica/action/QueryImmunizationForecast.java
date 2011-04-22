/**
 * 
 */
package org.openmrs.module.chica.action;

import java.io.FileOutputStream;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.StateManager;
import org.openmrs.module.atd.action.ProcessStateAction;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.Session;
import org.openmrs.module.atd.hibernateBeans.State;
import org.openmrs.module.atd.hibernateBeans.StateAction;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.ChicaStateActionHandler;
import org.openmrs.module.chica.ImmunizationForecastLookup;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.Util;

/**
 * @author tmdugan
 * This action class queries the mrf dump for immunizations
 * and sends the output from the query to the immunization forecasting
 * service and processes the forecasted immunization list
 */
public class QueryImmunizationForecast implements ProcessStateAction {
	
	private static Log log = LogFactory.getLog(ChicaStateActionHandler.class);
	
	/* (non-Javadoc)
	 * @see org.openmrs.module.chica.action.ProcessStateAction#processAction(org.openmrs.module.atd.hibernateBeans.StateAction, org.openmrs.Patient, org.openmrs.module.atd.hibernateBeans.PatientState, java.util.HashMap)
	 */
	public void processAction(StateAction stateAction, Patient patient, PatientState patientState,
	                          HashMap<String, Object> parameters) {
		//lookup the patient again to avoid lazy initialization errors
		PatientService patientService = Context.getPatientService();
		Integer patientId = patient.getPatientId();
		patient = patientService.getPatient(patientId);
		
		Integer locationTagId = patientState.getLocationTagId();
		Integer locationId = patientState.getLocationId();
		
		ATDService atdService = Context.getService(ATDService.class);
		State currState = patientState.getState();
		Integer sessionId = patientState.getSessionId();
		Session session = atdService.getSession(sessionId);
		FormService formService = Context.getFormService();
		ChicaService chicaService = Context.getService(ChicaService.class);
		Integer encounterId = session.getEncounterId();
		
		EncounterService encounterService = Context.getService(EncounterService.class);
		Encounter encounter = encounterService.getEncounter(encounterId);
		
		try {
			AdministrationService adminService = Context.getAdministrationService();
			Form immunizationForm = formService.getForms("immunization", null, null, false, null, null, null).get(0);
			Integer immunizationFormId = immunizationForm.getFormId();
			String directory = IOUtil.getDirectoryName(adminService.getGlobalProperty("chica.immunizationInputDirectory"));
			String mrn = patient.getPatientIdentifier().getIdentifier();
			
			String filename = "immunization_input_" + Util.archiveStamp() + "_" + mrn + ".xml";
			
			//create the input file for the immunization query
			FileOutputStream output = new FileOutputStream(directory + "/" + filename);
			chicaService.immunizationQuery(output, locationId, immunizationFormId, encounter,locationTagId,sessionId);
			output.close();
			
			//query the immunization forecasting service
			ImmunizationForecastLookup.queryImmunizationList(encounter, true,directory + "/" + filename);
		}
		catch (Exception e1) {
			log.error("Error in immunization query", e1);
		}
		
		StateManager.endState(patientState);
		ChicaStateActionHandler.changeState(patient, sessionId, currState, stateAction, parameters, locationTagId,
		    locationId);
		
	}
	
	public void changeState(PatientState patientState, HashMap<String, Object> parameters) {
		//deliberately empty because processAction changes the state
	}
}
