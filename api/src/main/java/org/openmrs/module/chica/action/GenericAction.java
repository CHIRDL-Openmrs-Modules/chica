package org.openmrs.module.chica.action;

import java.util.HashMap;

import org.openmrs.Patient;
import org.openmrs.module.chirdlutilbackports.BaseStateActionHandler;
import org.openmrs.module.chirdlutilbackports.StateManager;
import org.openmrs.module.chirdlutilbackports.action.ProcessStateAction;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.StateAction;

/**
 * Temporary placeholder for GenericAction action
 *
 */
public class GenericAction implements ProcessStateAction
{

	/**
	 * @see org.openmrs.module.chirdlutilbackports.action.ProcessStateAction#changeState(org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState,
	 *      java.util.HashMap)
	 */
	public void changeState(PatientState patientState, HashMap<String, Object> parameters) 
	{
		// Deliberately empty because processAction changes the state
	}

	@Override
	public void processAction(StateAction stateAction, Patient patient, PatientState patientState, HashMap<String, Object> parameters) 
	{
		Integer sessionId = patientState.getSessionId();
		State currState = patientState.getState();
		Integer locationTagId = patientState.getLocationTagId();
		Integer locationId = patientState.getLocationId();
		
		StateManager.endState(patientState);
		
		BaseStateActionHandler
		        .changeState(patient, sessionId, currState, stateAction, parameters, locationTagId, locationId);
		
	}
	
}
