package org.openmrs.module.chica.rule;

import java.util.Map;
import java.util.Set;

import org.openmrs.Form;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class hasJITBeenCreated implements Rule {
	
	private static final Logger log = LoggerFactory.getLogger(hasJITBeenCreated.class);
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getParameterList()
	 */
	@Override
	public Set<RuleParameterInfo> getParameterList() {
		return null;
	} 
	 
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getDependencies()
	 */
	@Override
	public String[] getDependencies() {
		return new String[] {};
	}
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getTTL()
	 */
	@Override
	public int getTTL() {
		return 0; // 60 * 30; // 30 minutes
	}
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getDatatype(String)
	 */
	@Override
	public Datatype getDefaultDatatype() {
		return Datatype.CODED;
	}
	
	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer, java.util.Map)
	 */
	@Override
	public Result eval(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		Integer encounterId = (Integer)parameters.get("encounterId");
		String formName = (String) parameters.get("param1");
		if (encounterId == null) {
			log.error("Encounter ID cannot be null");
			return Result.emptyResult();
		} else if (formName == null) {
			log.error("Form name cannot be null");
			return Result.emptyResult();
		}
		
		Form form = Context.getFormService().getForm(formName);
		if (form == null) {
			log.error("No form found with name: {}", formName);
			return Result.emptyResult();
		}
		
		PatientState patientState = org.openmrs.module.atd.util.Util.getProducePatientStateByEncounterFormAction(
			encounterId, form.getFormId());
		
		if (patientState != null) {
			return new Result("yes");
		}
		
		return new Result("no");
	}
}
