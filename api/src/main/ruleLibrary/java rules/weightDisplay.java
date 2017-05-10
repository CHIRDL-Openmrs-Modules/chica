package org.openmrs.module.chica.rule;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chica.util.Util;

public class weightDisplay implements Rule {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#getParameterList()
	 */
	public Set<RuleParameterInfo> getParameterList() {
		return null;
	}
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#getDependencies()
	 */
	public String[] getDependencies() {
		return new String[] {};
	}
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#getTTL()
	 */
	public int getTTL() {
		return 0; // 60 * 30; // 30 minutes
	}
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#getDefaultDatatype()
	 */
	public Datatype getDefaultDatatype() {
		return Datatype.CODED;
	}
	
	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer,
	 *      java.util.Map)
	 */
	public Result eval(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		if (parameters == null) {
			return Result.emptyResult();
		}
		
		String conceptName = "WEIGHT";
		Result ruleResult = null;
		Integer encounterId = (Integer) parameters.get("encounterId");
		LogicCriteria conceptCriteria = new LogicCriteriaImpl(conceptName);
		LogicCriteria fullCriteria = null;
		if (encounterId != null) {
			LogicCriteria encounterCriteria = new LogicCriteriaImpl("encounterId").equalTo(encounterId.intValue());
			fullCriteria = conceptCriteria.and(encounterCriteria);
		} else {
			fullCriteria = conceptCriteria;
		}
		
		ruleResult = context.read(patientId, context.getLogicDataSource("obs"), fullCriteria.last());
		if (ruleResult == null || ruleResult.size() == 0) {
			return Result.emptyResult();
		}
		
		Result weightResult = ruleResult.get(0);
		Patient patient = Context.getPatientService().getPatient(patientId);
		if (patient == null) {
			return Result.emptyResult();
		}
		
		Date birthdate = patient.getBirthdate();
		if (birthdate == null) {
			return Result.emptyResult();
		}
		
		double weight = weightResult.toNumber();
		long iPart;
		double fPart;
		iPart = (long) weight;
		fPart = weight - iPart;
		int ageMonths = org.openmrs.module.chirdlutil.util.Util.getAgeInUnits(birthdate, null, Util.MONTH_ABBR);
		if (ageMonths > 18) {
			weight = org.openmrs.module.chirdlutil.util.Util.round(weight, 2);
			return new Result(weight + " lb.");
		} else {
			double ounces = fPart * 16;
			int intOunces = (int) Math.round(org.openmrs.module.chirdlutil.util.Util.round(ounces, 0));
			return new Result(iPart + " lb. " + intOunces + " oz.");
		}
	}
}
