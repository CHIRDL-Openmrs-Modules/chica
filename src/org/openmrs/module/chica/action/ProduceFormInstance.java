/**
 * 
 */
package org.openmrs.module.chica.action;

import java.util.HashMap;
import java.util.List;

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
import org.openmrs.module.chica.MedicationListLookup;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.LocationTagAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Session;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.StateAction;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.module.rgccd.Medication;

/**
 * @author tmdugan
 * 
 */
public class ProduceFormInstance extends org.openmrs.module.atd.action.ProduceFormInstance
{
	private static Log log = LogFactory.getLog(ProduceFormInstance.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmrs.module.chica.action.ProcessStateAction#processAction(org.openmrs.module.atd.hibernateBeans.StateAction,
	 *      org.openmrs.Patient,
	 *      org.openmrs.module.atd.hibernateBeans.PatientState,
	 *      java.util.HashMap)
	 */
	public void processAction(StateAction stateAction, Patient patient,
			PatientState patientState, HashMap<String, Object> parameters)
	{
		processProduceAction(stateAction, patient, patientState, parameters);
		super.processAction(stateAction, patient, patientState, parameters);
		//DON't clean out the medication list cache for the patient here
		//It causes problems if other forms like the medication reconciliation
		//form need the list. The cache will get purged once a day.
		//If we run into memory issues we can add a TTL to the medication
		//list data and purge it after a certain time period
	}
	
	/**
	 * Processes the Produce Action.
	 * 
	 * @param stateAction StateAction object.
	 * @param patient Patient owning the action.
	 * @param patientState The patient state.
	 * @param parameters parameters map.
	 */
	protected static void processProduceAction(StateAction stateAction, Patient patient,
	                               			PatientState patientState, HashMap<String, Object> parameters) {
		//lookup the patient again to avoid lazy initialization errors
		PatientService patientService = Context.getPatientService();
		Integer patientId = patient.getPatientId();
		patient = patientService.getPatient(patientId);
		
		Integer locationTagId = patientState.getLocationTagId();
		Integer locationId = patientState.getLocationId();
		
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		State currState = patientState.getState();
		Integer sessionId = patientState.getSessionId();
		
		Session session = chirdlutilbackportsService.getSession(sessionId);
		Integer encounterId = session.getEncounterId();
		String formName = null;
		if(parameters != null){
			formName = (String) parameters.get("formName");
		}
		if(formName == null){
			formName = currState.getFormName();
		}
		LocationTagAttributeValue locTagAttrValue = 
			chirdlutilbackportsService.getLocationTagAttributeValue(locationTagId, formName, locationId);
		
		Integer formId = null;
		
		if(locTagAttrValue != null){
			String value = locTagAttrValue.getValue();
			if(value != null){
				try
				{
					formId = Integer.parseInt(value);
				} catch (Exception e)
				{
				}
			}
		}
		
		if(formId == null){
			//open an error state
			currState = chirdlutilbackportsService.getStateByName("ErrorState");
			chirdlutilbackportsService.addPatientState(patient,
					currState, sessionId,locationTagId,locationId, null);
			log.error(formName+
					" locationTagAttribute does not exist for locationTagId: "+
					locationTagId+" locationId: "+locationId);
			return;
		}
		
		FormService formService = Context.getFormService();
		Form form = formService.getForm(formId);
		long startTime = System.currentTimeMillis();
		AdministrationService adminService = Context.getAdministrationService();
		String queryMeds = adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_QUERY_MEDS);
		if(form.getName().equals("PWS") && ChirdlUtilConstants.GENERAL_INFO_TRUE.equalsIgnoreCase(queryMeds)){
			List<Medication> drugs = MedicationListLookup.getMedicationList(patientId);
			EncounterService encounterService = Context.getService(EncounterService.class);
			Encounter encounter = encounterService.getEncounter(encounterId);
			//if there is no drug list, call the ccd service again
			//to get the drug list
			if(drugs == null){
				State queryMedListState = chirdlutilbackportsService.getStateByName("Query medication list");
				PatientState state = chirdlutilbackportsService.addPatientState(patient, queryMedListState, 
					sessionId, locationTagId, locationId, null);
				try {
	                MedicationListLookup.queryMedicationList(encounter,true);
                }
                catch (Exception e) {
	               
	                log.error("Medication Query failed", e);
                }
				state.setEndTime(new java.util.Date());
				chirdlutilbackportsService.updatePatientState(patientState);
			}
			System.out.println("Produce: query medication list: "+(System.currentTimeMillis()-startTime));
		}
	}
}
