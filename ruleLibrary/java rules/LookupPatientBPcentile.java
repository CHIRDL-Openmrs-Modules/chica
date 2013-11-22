package org.openmrs.module.chica.rule;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.Duration;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.datasource.LogicDataSource;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chica.Calculator;
import org.openmrs.module.chirdlutil.util.Util;

public class LookupPatientBPcentile implements Rule {
	
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
		Patient patient = Context.getPatientService().getPatient(patientId);
		Date birthdate = patient.getBirthdate();
		String gender = patient.getGender();
		
		try {
			String bloodPressureType = (String) parameters.get("param1");
			
			LogicDataSource obsDataSource = context.getLogicDataSource("obs");
			String conceptName = "HEIGHT";
			LogicCriteria conceptCriteria = new LogicCriteriaImpl(conceptName);
			LogicCriteria fullCriteria = null;
			LogicCriteria encounterCriteria = null;
			Integer encounterId = (Integer) parameters.get("encounterId");
			if (encounterId != null) {
				encounterCriteria = new LogicCriteriaImpl("encounterId").equalTo(encounterId.intValue());
				
				fullCriteria = conceptCriteria.and(encounterCriteria);
			} else {
				fullCriteria = conceptCriteria;
			}
			
	    	Result heightResult = context.read(patientId, obsDataSource, fullCriteria.last());
			if (heightResult == null || heightResult.isEmpty()) {
				return Result.emptyResult();
			}
			
			Double height = heightResult.toNumber();
			
			String bpStr = (String) parameters.get("param2");
			Double observedBloodPressure;
			try {
				observedBloodPressure = Double.parseDouble(bpStr);
			} catch(NumberFormatException e) {
				log.error("Error formatting provided BP measurement: " + bpStr, e);
				return Result.emptyResult();
			}
			
			Calculator calculator = new Calculator();
			Double pcile = calculator.computeBloodPressurePercentile(birthdate, gender, height, observedBloodPressure, 
				bloodPressureType, Util.MEASUREMENT_IN);
			
			return new Result(pcile);
		}
		catch (Exception e) {
			log.error("Exception occurred calculating BP percentile", e);
			return Result.emptyResult();
		}
	}
	
}
