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

import java.util.Calendar;
import java.util.Date;
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
import org.openmrs.module.chirdlutil.util.Util;

public class ageMonDayDays implements Rule
{
	private LogicService logicService = Context.getLogicService();

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

	
	public Result eval(LogicContext context, Patient patient,
			Map<String, Object> parameters) throws LogicException
	{
		Date birthdate = context.read(patient,
            context.getLogicDataSource("person"), "BIRTHDATE").toDatetime();

		if(birthdate == null){
			return Result.emptyResult();
		}
		
		Calendar bdate = Calendar.getInstance();
		bdate.setTime(birthdate);

		Calendar now = Calendar.getInstance();
		now.setTime(context.getIndexDate());
    
		Double ageInMonthsPrecise = 
			org.openmrs.module.chirdlutil.util.Util.getFractionalAgeInUnits(
				birthdate, now.getTime(), Util.MONTH_ABBR);
		
		Double ageInMonths = Math.floor(ageInMonthsPrecise);
		
		//subtract the months
		Double ageInDays = ageInMonthsPrecise-ageInMonths;
		
		//figure out how many days are in the month to convert 
		//the decimal portion of the month into the number of days
		int daysInMonth = now.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		return new Result(String.valueOf(Util.round(ageInDays*daysInMonth, 0).intValue()));
	}
}