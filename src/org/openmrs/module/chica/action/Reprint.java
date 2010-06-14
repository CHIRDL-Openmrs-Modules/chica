/**
 * 
 */
package org.openmrs.module.chica.action;

import java.io.File;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.StateManager;
import org.openmrs.module.atd.action.ProcessStateAction;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.Session;
import org.openmrs.module.atd.hibernateBeans.State;
import org.openmrs.module.atd.hibernateBeans.StateAction;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chirdlutil.hibernateBeans.LocationTagAttributeValue;
import org.openmrs.module.chirdlutil.service.ChirdlUtilService;
import org.openmrs.module.chica.ChicaStateActionHandler;
import org.openmrs.module.chirdlutil.util.IOUtil;

/**
 * @author tmdugan
 *
 */
public class Reprint implements ProcessStateAction
{
	private Log log = LogFactory.getLog(this.getClass());

	/* (non-Javadoc)
	 * @see org.openmrs.module.chica.action.ProcessStateAction#processAction(org.openmrs.module.atd.hibernateBeans.StateAction, org.openmrs.Patient, org.openmrs.module.atd.hibernateBeans.PatientState, java.util.HashMap)
	 */
	public void processAction(StateAction stateAction, Patient patient,
			PatientState patientState, HashMap<String, Object> parameters)
	{
		//lookup the patient again to avoid lazy initialization errors
		ChirdlUtilService chirdlUtilService = Context.getService(ChirdlUtilService.class);

		PatientService patientService = Context.getPatientService();
		Integer patientId = patient.getPatientId();
		patient = patientService.getPatient(patientId);
		
		Integer locationTagId = patientState.getLocationTagId();

		ATDService atdService = Context
				.getService(ATDService.class);
		Integer sessionId = patientState.getSessionId();
		
		Session session = atdService.getSession(sessionId);
		Integer encounterId = session.getEncounterId();
		Integer locationId = patientState.getLocationId();
		
		String formName = null;
		if(parameters != null){
			formName = (String) parameters.get("formName");
		}
		if(formName == null){
			formName = patientState.getState().getFormName();
		}
		LocationTagAttributeValue locTagAttrValue = 
			chirdlUtilService.getLocationTagAttributeValue(locationTagId, formName, locationId);
		
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
			State currState = atdService.getStateByName("ErrorState");
			atdService.addPatientState(patient,
					currState, sessionId,locationTagId,locationId);
			log.error(formName+
					" locationTagAttribute does not exist for locationTagId: "+
					locationTagId+" locationId: "+locationId);
			return;
		}
		PatientState patientStateProduce = atdService.getPatientStateByEncounterFormAction(
				encounterId, formId,"PRODUCE FORM INSTANCE");

		if (patientStateProduce != null)
		{
			FormInstance formInstance = patientStateProduce.getFormInstance();
			patientState.setFormInstance(formInstance);
			atdService.updatePatientState(patientState);
			String mergeDirectory = IOUtil
			.formatDirectoryName(org.openmrs.module.atd.util.Util
					.getFormAttributeValue(formInstance.getFormId(),
							"defaultMergeDirectory",locationTagId,formInstance.getLocationId()));
			if (mergeDirectory != null)
			{
				File dir = new File(mergeDirectory);
				for (String fileName : dir.list())
				{
					if (fileName.startsWith(formInstance.toString() + "."))
					{
						fileName = mergeDirectory + "/" + fileName;
						IOUtil.renameFile(fileName, fileName.substring(0,
								fileName.lastIndexOf("."))
								+ ".xml");
						StateManager.endState(patientState);
						State currState = patientState.getState();
						ChicaStateActionHandler.changeState(patient, sessionId, currState,
							stateAction,parameters,locationTagId,locationId);
						break;
					}
				}
			} else
			{
				log.error("Reprint failed for patient: "
						+ patient.getPatientId()
						+ " because merge directory was null.");
			}
		}

	}

	public void changeState(PatientState patientState,
			HashMap<String, Object> parameters) {
		//deliberately empty because processAction changes the state
	}

}
