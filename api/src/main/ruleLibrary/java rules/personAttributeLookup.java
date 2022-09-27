package org.openmrs.module.chica.rule;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class personAttributeLookup implements Rule {
	
	private static final Logger log = LoggerFactory.getLogger(personAttributeLookup.class);
	
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
		// Get the parameter passed into the rule.  This should be the person attribute name.
		String personAttributeName = (String) parameters.get("param1");
		
		// If it's null or empty, return an empty result.
		if (StringUtils.isBlank(personAttributeName)) {
			return Result.emptyResult();
		}
		
		// Get the PersonAttributeType from the database with the name provided.
		PersonAttributeType attributeType = Context.getPersonService().getPersonAttributeTypeByName(personAttributeName);
		
		// If it doesn't exist, return an empty result.
		if (attributeType == null) {
			log.error("No person attribute type found with name: {}", personAttributeName);
			return Result.emptyResult();
		}
		
		// Lookup the patient.
		Patient patient = Context.getPatientService().getPatient(patientId);
		
		// If the patient doesn't exist for some reason, return an empty result.
		if (patient == null) {
			log.error("No patient found with ID: {}", patientId);
			return Result.emptyResult();
		}
		
		// Get the attribute for the patient.
		PersonAttribute attribute = patient.getAttribute(attributeType.getId());
		
		// If the patient doesn't have that attribute or it's null/empty, return an empty result.
		if (attribute == null || StringUtils.isBlank(attribute.getValue())) {
			return Result.emptyResult();
		}
		
		// Return the patient attribute value.
		return new Result(attribute.getValue());
	}
}
