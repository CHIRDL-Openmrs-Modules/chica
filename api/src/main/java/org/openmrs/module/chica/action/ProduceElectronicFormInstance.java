/**
 * 
 */
package org.openmrs.module.chica.action;

import java.util.HashMap;

import org.openmrs.Patient;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.StateAction;

/**
 * Produce class for forms that are meant to be displayed on electronic devices.
 * 
 * @author Steve McKee
 */
public class ProduceElectronicFormInstance extends org.openmrs.module.atd.action.ProduceElectronicFormInstance {
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmrs.module.chica.action.ProcessStateAction#processAction(org.openmrs.module.atd.hibernateBeans.StateAction,
	 *      org.openmrs.Patient,
	 *      org.openmrs.module.atd.hibernateBeans.PatientState,
	 *      java.util.HashMap)
	 */
	public void processAction(StateAction stateAction, Patient patient, PatientState patientState,
	                          HashMap<String, Object> parameters) {
		ProduceFormInstance.processProduceAction(stateAction, patient, patientState, parameters);
		
		super.processAction(stateAction, patient, patientState, parameters);
		//DON't clean out the medication list cache for the patient here
		//It causes problems if other forms like the medication reconciliation
		//form need the list. The cache will get purged once a day.
		//If we run into memory issues we can add a TTL to the medication
		//list data and purge it after a certain time period
	}
}
