package org.openmrs.module.chica.rule;

import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.openmrs.module.chirdlutil.util.Util;

public class providerAttributeLookup implements Rule {
	
	private LogicService logicService = Context.getLogicService();
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
	
	/* Call from mlm to to look up provider's person attribute by attribute name.
	 * Can be used for studies randomized by provider to look up study arm.
	 * Sample mlm call:
	 *   attributeValue := call providerAttributeLookup With "[attribute name]";
		
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
				org.openmrs.Provider provider =Util.getProviderByAttendingProviderEncounterRole(encounter);
				if(provider != null)
				{
					person = provider.getPerson();
				}
				 	
				if (person != null) {
					Integer personId = person.getPersonId();
					if (personAttributeName != null) {
					    //verify that person attribute name exists
						PersonAttribute personAttributeValue = personService.getPerson(personId).getAttribute(
						    personAttributeName);
						if (personAttributeValue != null) {
							return new Result(personAttributeValue.getValue());
						}
					}
				}
			}
		}
		catch (Exception e) {
		    log.error("Unable to "(org.openmrs.module.chirdlutil.util.Util
                    .getStackTrace(e)));
		}
		
		return Result.emptyResult();
	}
}
