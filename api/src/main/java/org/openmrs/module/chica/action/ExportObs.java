package org.openmrs.module.chica.action;

import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.hl7.HL7ExportObsRunnable;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chirdlutil.threadmgmt.ThreadManager;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.BaseStateActionHandler;
import org.openmrs.module.chirdlutilbackports.StateManager;
import org.openmrs.module.chirdlutilbackports.action.ProcessStateAction;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.StateAction;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

/**
 * CHICA-1070 Used to create HL7 ORU message and store in the sockethl7listener_hl7_out_queue table to be picked up by the HL7OutboundHandler task
 */
public class ExportObs implements ProcessStateAction
{
	private Log log = LogFactory.getLog(this.getClass());
	private static final String CONCEPT_SOURCE_OUTBOUND_OBS = "Outbound Obs";
	
	@Override
	public void processAction(StateAction stateAction, Patient patient, PatientState patientState, HashMap<String, Object> parameters) {
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		Integer sessionId = patientState.getSessionId();
		Integer encounterId = chirdlutilbackportsService.getSession(sessionId).getEncounterId();
		State currState = patientState.getState();
		Integer locationTagId = patientState.getLocationTagId();
		Integer locationId = patientState.getLocationId();

		try
		{
			AdministrationService adminService = Context.getAdministrationService();
			String host = adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_EXPORT_OBS_HOST);
			String portString = adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_EXPORT_OBS_PORT);
			Integer port;
			
			// If host and port are not set, allow the record to be created with localhost and port 0
			if (host == null || host.isEmpty())
			{
				log.error("Error creating HL7Outbound record in " + this.getClass().getName() + ". Host has not been set.");
				host = ChirdlUtilConstants.DEFAULT_HOST;
			}
			
			if(portString == null || portString.isEmpty())
			{
				log.error("Error creating HL7Outbound record in " + this.getClass().getName() + ". Port has not been set.");
				port = ChirdlUtilConstants.DEFAULT_PORT;
			}
			
			try
			{
				port = Integer.parseInt(portString);
			}
			catch(NumberFormatException e)
			{
				log.error("Error creating HL7Outbound record in " + this.getClass().getName() + ". Port is not in a valid numeric format.");
				port = ChirdlUtilConstants.DEFAULT_PORT;
			}
			
			EncounterService encounterService = Context.getService(EncounterService.class);
			Encounter encounter = (Encounter) encounterService.getEncounter(encounterId);
			
			ThreadManager threadManager = ThreadManager.getInstance();
			threadManager.execute(new HL7ExportObsRunnable(patient, encounterId, CONCEPT_SOURCE_OUTBOUND_OBS, host, port), encounter.getLocation().getLocationId());
		}
		catch(Exception e)
		{
			log.error("Exception exporting obs for encounterId: " + encounterId, e);
		}
		finally
		{
			StateManager.endState(patientState);
			BaseStateActionHandler
			        .changeState(patient, sessionId, currState, stateAction, parameters, locationTagId, locationId);	
		}
	}

	/**
	 * @see org.openmrs.module.chirdlutilbackports.action.ProcessStateAction#changeState(org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState,
	 *      java.util.HashMap)
	 */
	@Override
	public void changeState(PatientState patientState, HashMap<String, Object> parameters) {
		// Deliberately empty because processAction changes the state	
	}
}
