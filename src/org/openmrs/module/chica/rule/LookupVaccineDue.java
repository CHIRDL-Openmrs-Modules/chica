
package org.openmrs.module.chica.rule;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chica.ImmunizationForecast;
import org.openmrs.module.chica.ImmunizationForecastLookup;
import org.openmrs.module.chica.ImmunizationQueryOutput;

public class LookupVaccineDue implements Rule
{

	private Log log = LogFactory.getLog(this.getClass());
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

		String vaccineName = null;
		Integer doseNumber = null;

		if (parameters != null){

			vaccineName = (String) parameters.get("param1");

			if (vaccineName == null || vaccineName.trim().equalsIgnoreCase("")){

				return  Result.emptyResult();
			}

			HashMap<String, String> fullNames = this.setFullNameLookup();
			String vaccineFullName = fullNames.get(vaccineName);
			ImmunizationQueryOutput immunizations = 
					ImmunizationForecastLookup.getImmunizationList(patientId);

			if(immunizations != null){

				//patient has immunization records
				HashMap<String,ImmunizationForecast> forecastedImmunizations = immunizations.getImmunizationForecast();
				if (forecastedImmunizations == null){

					//Patient was matched,received a VXR, but no forecast immunizations.
					return  Result.emptyResult();
				}


				HashMap<String,ImmunizationForecast> forcasts = immunizations.getImmunizationForecast();
				ImmunizationForecast immunForecast = forcasts.get(vaccineFullName);
				java.util.Date todaysDate = new java.util.Date();


				if(immunForecast != null&&immunForecast.getDateDue().compareTo(todaysDate)<=0){

					doseNumber = immunForecast.getDose();
					return new Result(doseNumber);

				}
			}
		}


		return  Result.emptyResult();

	}


	private HashMap<String, String> setFullNameLookup() {

		HashMap<String, String> map = new HashMap<String, String>();

		map.put("DTaP","DTaP, unspecified formulation");
		map.put("HepA", "Hep A, unspecified formulation" );
		map.put("HepB", "Hep B, unspecified formulation" );
		map.put("Hib","Hib, unspecified formulation");
		map.put("Influenza", "influenza, unspecified formulation" );
		map.put("MMR", "MMR");
		map.put("PPV","pneumococcal, unspecified formulation");
		map.put("PPV","pneumococcal polysaccharide PPV23");
		map.put("PCV13", "Pneumococcal Conjugate, unspecified formulation");
		map.put("Rotavirus", "rotavirus, unspecified formulation");
		map.put("Varicella", "Varicella");
		map.put( "HPV","HPV, unspecified formulation");
		map.put("flulive", "influenza, live, intranasal");
		map.put("IPV", "polio, unspecified formulation");
		map.put( "MCV", "meningococcal MCV4, unspecified formulation");
		map.put("PPD", "TST-PPD intradermal");
		map.put("Tdap", "Td(adult) unspecified formulation");
		
		
		return map;
	}
}