package org.openmrs.module.chica.rule;

import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Form;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

public class hasJITBeenCreated implements Rule {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getParameterList()
	 */
	public Set<RuleParameterInfo> getParameterList() {
		return null;
	}
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getDependencies()
	 */
	public String[] getDependencies() {
		return new String[] {};
	}
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getTTL()
	 */
	public int getTTL() {
		return 0; // 60 * 30; // 30 minutes
	}
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getDatatype(String)
	 */
	public Datatype getDefaultDatatype() {
		return Datatype.CODED;
	}
	
	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer, java.util.Map)
	 */
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
			log.error("No form found with name: " + formName);
			return Result.emptyResult();
		}
		
		ChirdlUtilBackportsService service = Context.getService(ChirdlUtilBackportsService.class);
		PatientState patientState = 
			service.getPatientStateByEncounterFormAction(encounterId, form.getFormId(), "PRODUCE FORM INSTANCE");
		
		if (patientState != null) {
			return new Result("yes");
		}
		
		return new Result("no");
	}
}
