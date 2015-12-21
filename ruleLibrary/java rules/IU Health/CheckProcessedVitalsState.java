package org.openmrs.module.chica.rule;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

/**
 * DWE CHICA-600 Checks to see if vitals have been processed
 */
public class CheckProcessedVitalsState implements Rule{
	/**
	 * @see org.openmrs.logic.rule.Rule#getDatatype(String)
	 */
	@Override
	public Datatype getDefaultDatatype() {
		return Datatype.TEXT;
	}

	/**
	 * @see org.openmrs.logic.rule.Rule#getParameterList()
	 */
	@Override
	public Set<RuleParameterInfo> getParameterList(){
		return null;
	}

	/**
	 * @see org.openmrs.logic.rule.Rule#getDependencies()
	 */
	@Override
	public String[] getDependencies(){
		return new String[]{};
	}

	/** 
	 * @see org.openmrs.logic.rule.Rule#getTTL()
	 */
	@Override
	public int getTTL(){
		return 0; // 60 * 30; // 30 minutes
	}

	/**
	 * Checks to see if the patient has a PatientState record for STATE_PROCESS_VITALS
	 */
	@Override
	public Result eval(LogicContext context, Integer patientId, Map<String, Object> params)throws LogicException {	
		if(params == null){
			return Result.emptyResult();
		}
		
		Integer encounterId = params.get("encounterId") != null ? (Integer) params.get("encounterId") : null;
		if(encounterId == null){
			return Result.emptyResult();
		}
		
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		State state = chirdlutilbackportsService.getStateByName(ChirdlUtilConstants.STATE_PROCESS_VITALS);
		if(state != null)
		{
			// Get patient states for the encounter and state
			List<PatientState> patientStates = chirdlutilbackportsService.getPatientStateByEncounterState(encounterId, state.getStateId());
			
			if (patientStates != null && patientStates.size() > 0) {
				PatientState patientState = patientStates.get(0);
				if(patientState != null && patientState.getEndTime() != null){
					return new Result(ChirdlUtilConstants.GENERAL_INFO_TRUE);
				}
				else{
					return new Result(ChirdlUtilConstants.GENERAL_INFO_FALSE);
				}
			}
			else{
				new Result(ChirdlUtilConstants.GENERAL_INFO_FALSE);
			}
		}
		
		return Result.emptyResult();
	}
}
