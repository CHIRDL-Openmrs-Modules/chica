/**
 * 
 */
package org.openmrs.module.chica.action;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.QueryKiteException;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.BaseStateActionHandler;
import org.openmrs.module.chirdlutilbackports.StateManager;
import org.openmrs.module.chirdlutilbackports.action.ProcessStateAction;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Error;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.StateAction;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

/**
 * @author tmdugan
 *
 */
public class QueryKite implements ProcessStateAction
{
	private static Log log = LogFactory.getLog(QueryKite.class);
	/* (non-Javadoc)
	 * @see org.openmrs.module.chica.action.ProcessStateAction#processAction(org.openmrs.module.atd.hibernateBeans.StateAction, org.openmrs.Patient, org.openmrs.module.atd.hibernateBeans.PatientState, java.util.HashMap)
	 */
	public void processAction(StateAction stateAction, Patient patient,
			PatientState patientState, HashMap<String, Object> parameters)
	{
		//lookup the patient again to avoid lazy initialization errors
		PatientService patientService = Context.getPatientService();
		Integer patientId = patient.getPatientId();
		patient = patientService.getPatient(patientId);
		
		Integer locationTagId = patientState.getLocationTagId();
		Integer locationId = patientState.getLocationId();
		
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		AdministrationService adminService = Context.getAdministrationService();
		State currState = patientState.getState();
		Integer sessionId = patientState.getSessionId();
		String performMRFQuery = adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_PERFORM_MRF_QUERY);
		
		if (performMRFQuery != null && performMRFQuery.trim().equalsIgnoreCase("true")){
			if (patient.getPatientIdentifier() == null)
			{
				log.error("Could not query kite. MRN is null.");
			} else
			{
				try
				{
					org.openmrs.module.chica.QueryKite.mrfQuery(patient.getPatientIdentifier()
							.getIdentifier(), patient,true);
				}catch (QueryKiteException e){
					Error ce = e.getError();
					ce.setSessionId(sessionId);
					chirdlutilbackportsService.saveError(ce);

				}catch (Exception e)
				{
					log.error("Error querying kite", e);
				}
			}
		}
		StateManager.endState(patientState);
		BaseStateActionHandler.changeState(patient, sessionId, currState,
				stateAction,parameters,locationTagId,locationId);

	}
	public void changeState(PatientState patientState,
			HashMap<String, Object> parameters) {
		//deliberately empty because processAction changes the state
	}

}
