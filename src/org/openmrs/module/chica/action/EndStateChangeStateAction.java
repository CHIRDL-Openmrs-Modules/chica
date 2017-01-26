package org.openmrs.module.chica.action;

import java.util.HashMap;

import org.openmrs.Patient;
import org.openmrs.module.chirdlutilbackports.BaseStateActionHandler;
import org.openmrs.module.chirdlutilbackports.StateManager;
import org.openmrs.module.chirdlutilbackports.action.ProcessStateAction;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.StateAction;

/**
 * CHICA-886 
 * This is a generic action class that can be used to end the current state and change to the next state
 * 
 * Currently, this class serves as a starting point when printing the PWS from the GreaseBoard
 * Each program will map to the appropriate state to follow so that different implementations can have different state flows
 */
public class EndStateChangeStateAction implements ProcessStateAction
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
		StateManager.endState(patientState);
		
		BaseStateActionHandler
		        .changeState(patient, patientState.getSessionId(), patientState.getState(), 
		        		stateAction, parameters, patientState.getLocationTagId(), patientState.getLocationId());	
	}
}
