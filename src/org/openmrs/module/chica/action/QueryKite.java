/**
 * 
 */
package org.openmrs.module.chica.action;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.StateManager;
import org.openmrs.module.atd.action.ProcessStateAction;
import org.openmrs.module.atd.hibernateBeans.ATDError;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.State;
import org.openmrs.module.atd.hibernateBeans.StateAction;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.ChicaStateActionHandler;
import org.openmrs.module.chica.QueryKiteException;
import org.openmrs.module.chica.service.ChicaService;

/**
 * @author tmdugan
 *
 */
public class QueryKite implements ProcessStateAction
{
	private static Log log = LogFactory.getLog(ChicaStateActionHandler.class);
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
		
		ATDService atdService = Context.getService(ATDService.class);
		State currState = patientState.getState();
		Integer sessionId = patientState.getSessionId();
				
		if (patient.getPatientIdentifier() == null)
		{
			log.error("Could not query kite. MRN is null.");
		} else
		{
			try
			{
				org.openmrs.module.chica.QueryKite.mrfQuery(patient.getPatientIdentifier()
						.getIdentifier(), patient.getPatientId());
			}catch (QueryKiteException e){
				ATDError ce = e.getATDError();
				ce.setSessionId(sessionId);
				atdService.saveError(ce);
				
			}catch (Exception e)
			{
				log.error("Error querying kite");
				log.error(e.getMessage());
				log.error(org.openmrs.module.chirdlutil.util.Util
								.getStackTrace(e));
			}
		}
		StateManager.endState(patientState);
		ChicaStateActionHandler.changeState(patient, sessionId, currState,
				stateAction,parameters,locationTagId,locationId);

	}
	public void changeState(PatientState patientState,
			HashMap<String, Object> parameters) {
		//deliberately empty because processAction changes the state
	}

}
