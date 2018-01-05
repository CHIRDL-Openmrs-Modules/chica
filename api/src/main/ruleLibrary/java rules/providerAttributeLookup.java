package org.openmrs.module.chica.rule;

import java.util.Map;
import java.util.Set;

import org.openmrs.Encounter;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;

public class providerAttributeLookup implements Rule {
	
	private LogicService logicService = Context.getLogicService();
	
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
	
	/*
	 * hpvStudyArm := call personAttributeLookup With "hpvStudyArm";
		If (hpvStudyArm = null) OR NOT (hpvStudyArm = "prompt only arm") then conclude False;
		If (hpvStudyArm = null) OR NOT (hpvStudyArm = "prompt plus handout arm") then conclude False;
		If (hpvStudyArm = null) OR NOT (hpvStudyArm = "control") then conclude False;
	 */
	public Result eval(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
	
		PersonService personService = Context.getPersonService();
		EncounterService encounterService = Context.getEncounterService();
		String personAttributeName = (String) parameters.get("param1");
		Integer encounterId = (Integer) parameters.get("encounterId");
		try {
			Encounter encounter = encounterService.getEncounter(encounterId);
			if (encounter != null) {
				
				// CHICA-1151 Use the provider that has the "Attending Provider" role for the encounter
				Person person = null;
				org.openmrs.Provider provider = org.openmrs.module.chirdlutil.util.Util.getProviderByAttendingProviderEncounterRole(encounter);
				if(provider != null)
				{
					person = provider.getPerson();
				}
				 	
				if (person != null) {
					Integer personId = person.getPersonId();
					if (personAttributeName != null) {
						PersonAttribute personAttributeValue = personService.getPerson(personId).getAttribute(
						    personAttributeName);
						if (personAttributeValue != null) {
							return new Result(personAttributeValue.getValue());
						}
					}
				}
			}
		}
		catch (NumberFormatException e) {
			System.out.println("HPV study:  providerAttributeLookup person attribute lookup exception");
		}
		
		return Result.emptyResult();
	}
}
