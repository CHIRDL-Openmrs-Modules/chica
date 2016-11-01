/**
 * 
 */
package org.openmrs.module.chica.action;

import java.util.HashMap;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.BaseStateActionHandler;
import org.openmrs.module.chirdlutilbackports.StateManager;
import org.openmrs.module.chirdlutilbackports.action.ProcessStateAction;
import org.openmrs.module.chirdlutilbackports.datasource.ObsInMemoryDatasource;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.StateAction;

/**
 * @author tmdugan
 *
 */
public class ClearInmemoryObs implements ProcessStateAction
{

	/* (non-Javadoc)
	 * @see org.openmrs.module.chica.action.ProcessStateAction#processAction(org.openmrs.module.atd.hibernateBeans.StateAction, org.openmrs.Patient, org.openmrs.module.atd.hibernateBeans.PatientState, java.util.HashMap)
	 */
	public void processAction(StateAction stateAction, Patient patient,
			PatientState patientState, HashMap<String, Object> parameters)
	{
		LogicService logicService = Context.getLogicService();

		ObsInMemoryDatasource xmlDatasource = (ObsInMemoryDatasource) logicService
				.getLogicDataSource(ChirdlUtilConstants.DATA_SOURCE_IN_MEMORY);
		
		Integer patientId = patient.getPatientId();
		Integer locationTagId = patientState.getLocationTagId();
		Integer locationId = patientState.getLocationId();
		State currState = patientState.getState();
		Integer sessionId = patientState.getSessionId();

		// clear the in-memory obs from the MRF dump
		xmlDatasource.deleteObsByPatientId(patientId);
		StateManager.endState(patientState);
		BaseStateActionHandler.changeState(patient, sessionId, currState, stateAction, parameters,
				locationTagId, locationId);
	}

	public void changeState(PatientState patientState,
			HashMap<String, Object> parameters) {
		//deliberately empty because processAction changes the state
	}

}
