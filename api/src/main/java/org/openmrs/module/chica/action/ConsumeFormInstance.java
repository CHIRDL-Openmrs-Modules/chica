/**
 * 
 */
package org.openmrs.module.chica.action;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.openmrs.Form;
import org.openmrs.FormField;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.FormService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.module.atd.datasource.FormDatasource;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.BaseStateActionHandler;
import org.openmrs.module.chirdlutilbackports.StateManager;
import org.openmrs.module.chirdlutilbackports.action.ProcessStateAction;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.StateAction;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tmdugan
 *
 */
public class ConsumeFormInstance implements ProcessStateAction
{	
	private static final Logger log = LoggerFactory.getLogger(ConsumeFormInstance.class);


	/* (non-Javadoc)
	 * @see org.openmrs.module.chica.action.ProcessStateAction#processAction(org.openmrs.module.atd.hibernateBeans.StateAction, org.openmrs.Patient, org.openmrs.module.atd.hibernateBeans.PatientState, java.util.HashMap)
	 */
	public void processAction(StateAction stateAction, Patient patient,
			PatientState patientState, HashMap<String, Object> parameters)
	{
		long totalTime = System.currentTimeMillis();
		//lookup the patient again to avoid lazy initialization errors
		PatientService patientService = Context.getPatientService();
		Integer patientId = patient.getPatientId();
		patient = patientService.getPatient(patientId);
		
		Integer locationTagId = patientState.getLocationTagId();
		Integer locationId = patientState.getLocationId();

		State currState = patientState.getState();
		Integer sessionId = patientState.getSessionId();
		FormInstance formInstance = (FormInstance) parameters.get("formInstance");
		FormService formService = Context.getFormService();
		Form form = formService.getForm(formInstance.getFormId());
		patientState.setFormInstance(formInstance);
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		chirdlutilbackportsService.updatePatientState(patientState);
		consume(sessionId,formInstance,patient,
				parameters,null,locationTagId);
		StateManager.endState(patientState);
		log.info("Consume: Total time to consume {}: {}",form.getName(), System.currentTimeMillis()-totalTime);
		BaseStateActionHandler.changeState(patient, sessionId, currState,
				stateAction,parameters,locationTagId,locationId);

	}

	public void changeState(PatientState patientState,
			HashMap<String, Object> parameters) {
		//deliberately empty because processAction changes the state
	}
	
	public static void consume(Integer sessionId, FormInstance formInstance, Patient patient,
	                           HashMap<String, Object> parameters, List<FormField> fieldsToConsume, Integer locationTagId) {
		AdministrationService adminService = Context.getAdministrationService();
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		ChicaService chicaService = Context.getService(ChicaService.class);
		Integer encounterId = chirdlutilbackportsService.getSession(sessionId).getEncounterId();
		String exportFilename = null;
		
		if (parameters != null) {
			exportFilename = (String) parameters.get(ChirdlUtilConstants.PARAMETER_FILE_NAME);
		}
		try {
			InputStream input = new FileInputStream(exportFilename);
			chicaService.consume(input, patient, encounterId, formInstance, sessionId, fieldsToConsume, locationTagId);
			input.close();
		}
		catch (Exception e) {
			log.error("Error consuming chica file: {} ", exportFilename, e);
		}
		
		// remove the parsed xml from the xml datasource
		try {
			Integer purgeXMLDatasourceProperty = null;
			try {
				purgeXMLDatasourceProperty = Integer.parseInt(adminService.getGlobalProperty("atd.purgeXMLDatasource"));
			}
			catch (Exception e) {}
			LogicService logicService = Context.getLogicService();
			
			FormDatasource formDatasource = (FormDatasource) logicService.getLogicDataSource("form");
			if (purgeXMLDatasourceProperty != null && purgeXMLDatasourceProperty == 1) {
				formDatasource.deleteForm(formInstance);
			}
		}
		catch (Exception e) {
			log.error("Exception removing parsed xml from datasource.", e);
		}
	}
}
