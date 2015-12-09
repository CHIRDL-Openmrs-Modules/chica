package org.openmrs.module.chica.action;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.result.Result;
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
			Rule rule = new Rule();
			rule.setRuleType(PWS_POST_CREATE);
			List<Rule> rules = dssService.getRules(rule, true, false, null);
			if (rules == null || rules.size() == 0) {
				return;
			}

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
