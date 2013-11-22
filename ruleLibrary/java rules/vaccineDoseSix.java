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

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chica.ImmunizationForecastLookup;
import org.openmrs.module.chica.ImmunizationPrevious;
import org.openmrs.module.chica.ImmunizationQueryOutput;

public class vaccineDoseSix implements Rule
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
		String vaccineName = (String) parameters.get("concept");
		ImmunizationQueryOutput immunizations = 
			ImmunizationForecastLookup.getImmunizationList(patientId);
		
		if(immunizations != null){
			HashMap<String,HashMap<Integer,ImmunizationPrevious>> prevImmunizations = 
				immunizations.getImmunizationPrevious();
		
			if(prevImmunizations != null){
				HashMap<Integer,ImmunizationPrevious> prevImmunDoses = prevImmunizations.get(vaccineName);
				if(prevImmunDoses != null){
					ImmunizationPrevious doseOneImmunization = prevImmunDoses.get(6);
					if(doseOneImmunization != null){
						SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
						return new Result(formatter.format(doseOneImmunization.getDate()));
					}
				}
			}	
		}
		return Result.emptyResult();
	}
}