/**
 * 
 */
package org.openmrs.module.chica.action;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.module.atd.StateManager;
import org.openmrs.module.atd.action.ProcessStateAction;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.State;
import org.openmrs.module.atd.hibernateBeans.StateAction;
import org.openmrs.module.chica.ChicaStateActionHandler;
import org.openmrs.module.chica.datasource.ObsChicaDatasource;

/**
 * @author tmdugan
 *
 */
public class ClearInmemoryObs implements ProcessStateAction
{
	private Log log = LogFactory.getLog(this.getClass());

	/* (non-Javadoc)
	 * @see org.openmrs.module.chica.action.ProcessStateAction#processAction(org.openmrs.module.atd.hibernateBeans.StateAction, org.openmrs.Patient, org.openmrs.module.atd.hibernateBeans.PatientState, java.util.HashMap)
	 */
	public void processAction(StateAction stateAction, Patient patient,
			PatientState patientState, HashMap<String, Object> parameters)
	{
		LogicService logicService = Context.getLogicService();

		ObsChicaDatasource xmlDatasource = (ObsChicaDatasource) logicService
				.getLogicDataSource("RMRS");
		
		Integer patientId = patient.getPatientId();
		Integer locationTagId = patientState.getLocationTagId();
		Integer locationId = patientState.getLocationId();
		State currState = patientState.getState();
		Integer sessionId = patientState.getSessionId();

		// clear the in-memory obs from the MRF dump
		xmlDatasource.deleteRegenObsByPatientId(patientId);
		StateManager.endState(patientState);
		ChicaStateActionHandler.changeState(patient, sessionId, currState, stateAction, parameters,
				locationTagId, locationId);
	}

	public void changeState(PatientState patientState,
			HashMap<String, Object> parameters) {
		//deliberately empty because processAction changes the state
	}

}
