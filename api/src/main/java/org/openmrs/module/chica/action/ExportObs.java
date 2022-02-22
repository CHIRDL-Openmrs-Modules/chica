package org.openmrs.module.chica.action;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.Daemon;
import org.openmrs.module.chica.hl7.HL7ExportObsRunnable;
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
	private static final Logger log = LoggerFactory.getLogger(ExportObs.class);
	
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
			String host = getHost();
            String portString = getPort();
			Integer port;
			
			// If host and port are not set, allow the record to be created with localhost and port 0
			if(StringUtils.isBlank(host))
			{
				this.log.error("Error creating HL7Outbound record in " + this.getClass().getName() + ". Host has been set to " + ChirdlUtilConstants.DEFAULT_HOST + ".");
				host = ChirdlUtilConstants.DEFAULT_HOST;
			}
			
			if(StringUtils.isBlank(portString))
			{
				this.log.error("Error creating HL7Outbound record in " + this.getClass().getName() + ". Port has been set to " + ChirdlUtilConstants.DEFAULT_PORT + ".");
				port = ChirdlUtilConstants.DEFAULT_PORT;
			}
			
			try
			{
				port = Integer.valueOf(portString);
			}
			catch(NumberFormatException e)
			{
				this.log.error("Error creating HL7Outbound record in " + this.getClass().getName() + ". Port is not in a valid numeric format (portString: " + portString + "). Port will be set to default value " + ChirdlUtilConstants.DEFAULT_PORT + ".", e);
				port = ChirdlUtilConstants.DEFAULT_PORT;
			}
			
			Runnable export = new HL7ExportObsRunnable(patient.getPatientId(), encounterId, getConceptSource(), host, port);
			Daemon.runInDaemonThread(export, org.openmrs.module.chica.util.Util.getDaemonToken());
		}
		catch(Exception e)
		{
			this.log.error("Exception exporting obs for encounterId: " + encounterId, e);
		}
		finally
		{
			StateManager.endState(patientState);
			BaseStateActionHandler
			        .changeState(patient, sessionId, currState, stateAction, parameters, locationTagId, locationId);	
		}
	}

	/**
     * Gets the Concept Source for Outbound Obs
     * @return Concept Source Outbound Obs
     */
    public String getConceptSource() {
        return ChirdlUtilConstants.CONCEPT_SOURCE_OUTBOUND_OBS;
    }
    
    /**
     * Gets the Host for Export Obs
     * @return exportObsHost
     */
    public String getHost() {
        return Context.getAdministrationService().getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_EXPORT_OBS_HOST);
    }
    
    /**
     * Gets the Port for Export Obs
     * @return exportObsPort
     */
    public String getPort() {
        return Context.getAdministrationService().getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_EXPORT_OBS_PORT);
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
