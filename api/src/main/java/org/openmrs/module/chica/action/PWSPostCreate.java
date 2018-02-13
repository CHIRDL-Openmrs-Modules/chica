package org.openmrs.module.chica.action;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.BaseStateActionHandler;
import org.openmrs.module.chirdlutilbackports.StateManager;
import org.openmrs.module.chirdlutilbackports.action.ProcessStateAction;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.StateAction;
import org.openmrs.module.dss.hibernateBeans.Rule;
import org.openmrs.module.dss.service.DssService;

/**
 * 
 * DWE CHICA-612 Runs rules that have rule_type = "PWS_post_create"
 *
 */
public class PWSPostCreate implements ProcessStateAction
{
	private Log log = LogFactory.getLog(PWSPostCreate.class);
	private static final String PWS_POST_CREATE = "PWS_post_create";

	@Override
	public void changeState(PatientState patientState, HashMap<String, Object> parameters) {
		//deliberately empty because processAction changes the state	
	}

	/**
	 * * @see org.openmrs.module.chirdlutilbackports.action.ProcessStateAction#processAction(org.openmrs.module.atd.hibernateBeans.StateAction,
	 *      org.openmrs.Patient,
	 *      org.openmrs.module.atd.hibernateBeans.PatientState,
	 *      java.util.HashMap)
	 */
	public void processAction(StateAction stateAction, Patient patient, PatientState patientState, HashMap<String, Object> parameters) {
		try
		{
			DssService dssService = Context.getService(DssService.class);
			List<Rule> rules = dssService.getRulesByType(PWS_POST_CREATE);
			if (rules == null || rules.size() == 0) {
				return;
			}
			
			// DWE CHICA-682 Add locationId, locationTagId, and sessionId to the parameters because they may not be there
			// This could happen if "Print PWS" is selected from the drop-down on the GreaseBoard			
			parameters.put(ChirdlUtilConstants.PARAMETER_SESSION_ID, patientState.getSessionId());
			parameters.put(ChirdlUtilConstants.PARAMETER_LOCATION_ID, patientState.getLocationId());
			parameters.put(ChirdlUtilConstants.PARAMETER_LOCATION_TAG_ID, patientState.getLocationTagId());
			
			parameters.put(ChirdlUtilConstants.PARAMETER_MODE, ChirdlUtilConstants.PARAMETER_VALUE_PRODUCE);

			// Check age restrictions and set parameters
			for (Rule currRule : rules)
			{
				if (currRule.checkAgeRestrictions(patient))
				{
					currRule.setParameters(parameters);			
					dssService.runRule(patient, currRule);
				}
			}
		}
		catch(Exception e)
		{
			log.error("Error running rules PWSPostCreate.");
		}
		finally
		{
			StateManager.endState(patientState);
			BaseStateActionHandler.changeState(patientState.getPatient(), patientState
					.getSessionId(), patientState.getState(),
					patientState.getState().getAction(), parameters,
					patientState.getLocationTagId(),
					patientState.getLocationId());	
		}	
	}
}
