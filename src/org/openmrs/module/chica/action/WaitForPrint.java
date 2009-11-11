/**
 * 
 */
package org.openmrs.module.chica.action;

import java.util.HashMap;

import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.StateManager;
import org.openmrs.module.atd.TeleformFileMonitor;
import org.openmrs.module.atd.TeleformFileState;
import org.openmrs.module.atd.action.ProcessStateAction;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.StateAction;
import org.openmrs.module.chica.ChicaStateActionHandler;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chirdlutil.util.IOUtil;

/**
 * @author tmdugan
 *
 */
public class WaitForPrint implements ProcessStateAction
{

	/* (non-Javadoc)
	 * @see org.openmrs.module.chica.action.ProcessStateAction#processAction(org.openmrs.module.atd.hibernateBeans.StateAction, org.openmrs.Patient, org.openmrs.module.atd.hibernateBeans.PatientState, java.util.HashMap)
	 */
	public void processAction(StateAction stateAction, Patient patient,
			PatientState patientState, HashMap<String, Object> parameters)
			throws Exception
	{
		//lookup the patient again to avoid lazy initialization errors
		PatientService patientService = Context.getPatientService();
		Integer patientId = patient.getPatientId();
		patient = patientService.getPatient(patientId);
		
		Integer locationTagId = patientState.getLocationTagId();
	
		ChicaService chicaService = Context
				.getService(ChicaService.class);
		Integer sessionId = patientState.getSessionId();
		PatientState stateWithFormId = chicaService.getPrevProducePatientState(sessionId, 
				patientState.getPatientStateId());
		
		FormInstance formInstance = patientState.getFormInstance();

		if(formInstance == null&&stateWithFormId != null)
		{
			formInstance = stateWithFormId.getFormInstance();
		}
				
		String mergeDirectory = IOUtil
				.formatDirectoryName(org.openmrs.module.atd.util.Util
						.getFormAttributeValue(formInstance.getFormId(),
								"defaultMergeDirectory", locationTagId,
								formInstance.getLocationId()));
		TeleformFileState teleformFileState = TeleformFileMonitor
				.addToPendingStatesWithFilename(formInstance, mergeDirectory
						+ formInstance.toString() + ".20");
		teleformFileState.addParameter("patientState", patientState);
	}

	public void changeState(PatientState patientState,
			HashMap<String, Object> parameters) throws Exception {
		StateManager.endState(patientState);
		ChicaStateActionHandler.changeState(patientState.getPatient(), patientState
				.getSessionId(), patientState.getState(),
				patientState.getState().getAction(),parameters,
				patientState.getLocationTagId(),
				patientState.getLocationId());
		
	}

}
