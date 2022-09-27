package org.openmrs.module.chica.rule;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class patientIdentifierLookup implements Rule {
	
	private static final Logger log = LoggerFactory.getLogger(patientIdentifierLookup.class);
	
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
		return Datatype.TEXT;
	}
	
	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer, java.util.Map)
	 */
	@Override
	public Result eval(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		// Get the parameter passed into the rule.  This should be the patient identifier name.
		String patientIdentifierName = (String) parameters.get("param1");
		
		// If it's null or empty, return an empty result.
		if (StringUtils.isBlank(patientIdentifierName)) {
			return Result.emptyResult();
		}
		
		// Get the PatientIdentifierType from the database with the name provided.
		PatientIdentifierType identifierType = 
				Context.getPatientService().getPatientIdentifierTypeByName(patientIdentifierName);
		
		// If it doesn't exist, return an empty result.
		if (identifierType == null) {
			log.error("No patient identifier type found with name: {}", patientIdentifierName);
			return Result.emptyResult();
		}
		
		// Lookup the patient.
		Patient patient = Context.getPatientService().getPatient(patientId);
		
		// If the patient doesn't exist for some reason, return an empty result.
		if (patient == null) {
			log.error("No patient found with ID: {}", patientId);
			return Result.emptyResult();
		}
		
		// Get the identifier for the patient.
		PatientIdentifier identifier = patient.getPatientIdentifier(identifierType.getId());
		
		// If the patient doesn't have that identifier or it's null/empty, return an empty result.
		if (identifier == null || StringUtils.isBlank(identifier.getIdentifier())) {
			return Result.emptyResult();
		}
		
		// Return the patient identifier value.
		return new Result(identifier.getIdentifier());
	}
}
