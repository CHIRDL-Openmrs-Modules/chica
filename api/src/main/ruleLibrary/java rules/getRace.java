package org.openmrs.module.chica.rule;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
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

public class getRace implements Rule {
	
	private final String SOURCE = "Wishard Race Codes";
	private final String RACE = "Race";
	private static final Logger log = LoggerFactory.getLogger(getRace.class);
	
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
		PersonAttributeType attributeType = Context.getPersonService().getPersonAttributeTypeByName(RACE);
		if (attributeType == null) {
			log.error("No person attribute type found with name: " + RACE);
			return Result.emptyResult();
		}
		
		PersonAttribute attribute = patient.getAttribute(attributeType.getId());
		if (attribute == null) {
			return Result.emptyResult();
		}
		
		Concept raceConcept = Context.getConceptService().getConceptByMapping(attribute.getValue(), SOURCE);
		if (raceConcept == null) {
			return Result.emptyResult();
		}
		
		ConceptName name = raceConcept.getName();
		if (name == null) {
			log.error("Concept " + raceConcept.getConceptId() + " does not have an associated concept name");
			return Result.emptyResult();
		}
		
		return new Result(name.getName());
	}
}
