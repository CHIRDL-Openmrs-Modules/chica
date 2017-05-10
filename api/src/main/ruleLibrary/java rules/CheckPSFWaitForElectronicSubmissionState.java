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
 * DWE CHICA-671
 * Checks to see if the PatientState for "PSF WAIT FOR ELECTRONIC SUBMISSION" 
 * has an end_time stamp to determine if the PSF has been submitted
 * We'll use this in combination with the "Processed Vitals HL7" state to determine if 
 * we should auto-print the PDF version of the PWS
 */
public class CheckPSFWaitForElectronicSubmissionState implements Rule{

	/**
	 * Checks to see if the patient has a PatientState record for STATE_PSF_WAIT_FOR_ELECTRONIC_SUBMISSION
	 * with an end_time
	 */
	@Override
	public Result eval(LogicContext context, Integer patientId, Map<String, Object> params) throws LogicException {
		if(params == null){
			return Result.emptyResult();
		}
		
		Integer encounterId = params.get("encounterId") != null ? (Integer) params.get("encounterId") : null;
		if(encounterId == null){
			return Result.emptyResult();
		}
		
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		State state = chirdlutilbackportsService.getStateByName(ChirdlUtilConstants.STATE_PSF_WAIT_FOR_ELECTRONIC_SUBMISSION);
		if(state != null)
		{
			// Get patient states for the encounter and state
			List<PatientState> patientStates = chirdlutilbackportsService.getPatientStateByEncounterState(encounterId, state.getStateId());
			if (patientStates != null && patientStates.size() > 0) {
				for(PatientState psfState : patientStates)
				{
					if(psfState.getEndTime() != null)
					{
						return new Result(ChirdlUtilConstants.GENERAL_INFO_TRUE);
					}
				}
				return new Result(ChirdlUtilConstants.GENERAL_INFO_FALSE);
			}
			else{
				return new Result(ChirdlUtilConstants.GENERAL_INFO_FALSE);
			}
		}
		
		return Result.emptyResult();
	}

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

}
