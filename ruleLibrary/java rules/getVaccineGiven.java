/********************************************************************
 Translated from - mcad.mlm on Fri Dec 28 15:10:34 EST 2007

 Title : MCAD Reminder
 Filename:  mcad
 Version : 0 . 2
 Institution : Indiana University School of Medicine
 Author : Steve Downs
 Specialist : Pediatrics
 Date : 05 - 22 - 2007
 Validation :
 Purpose : Provides a specific reminder, tailored to the patient who identified one or more fatty acid disorders
 Explanation : Based on AAP screening recommendations
 Keywords : fatty, acid, fatty acid disorder
 Citations : Screening for fatty acid disorder AAP
 Links :

 ********************************************************************/
package org.openmrs.module.chica.rule;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.hl7.immunization.Vaccine;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.LocationAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;


public class getVaccineGiven implements Rule
{

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#getParameterList()
	 */
	public Set<RuleParameterInfo> getParameterList()
	{
		return null;
	}

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#getDependencies()
	 */
	public String[] getDependencies()
	{
		return new String[]
		{};
	}

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#getTTL()
	 */
	public int getTTL()
	{
		return 0; // 60 * 30; // 30 minutes
	}

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#getDefaultDatatype()
	 */
	public Datatype getDefaultDatatype()
	{
		return Datatype.CODED;
	}

	public Result eval(LogicContext context, Integer patientId,
	       			Map<String, Object> parameters) throws LogicException
	{
		EncounterService encounterService = Context.getService(EncounterService.class);
		ChirdlUtilBackportsService service = Context.getService(ChirdlUtilBackportsService.class);
		LocationService locationService = Context.getLocationService();
		ConceptService conceptService = Context.getConceptService();
		String lotNumber = "";
		String providerFN = "";
		String providerLN = "";
		String providerMN = "";
		String LR = null;
		String AT = null;
		String source = null;
		String displayLocation = "";
		String vaccineName = null;
		String vaccineCode = null;
		String address1 = "";
		String address2 = "";
		String city = "";
		String state = "";
		String zipcode = "";
		Date dateGiven = null;
		String route = null;
		String routeCode = null;
		Result finalResult = new Result();
		Vaccine vaccine = new Vaccine();
		
		Object object = parameters.get("param1");
		if (object != null && object instanceof Result){
			Result result = (Result) parameters.get("param1");
			if (result != null && result.toObject() instanceof Obs){
				Obs obs = ((Obs) result.toObject());
				if (obs != null){
					dateGiven = obs.getValueDatetime();
				}
			}
			parameters.put("param1", null);
		}
	
		return finalResult;
	}
	
	private void clearParameters(Map<String, Object> parameters){
		
	}
}