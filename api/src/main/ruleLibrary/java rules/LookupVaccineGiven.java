
package org.openmrs.module.chica.rule;

import java.util.Date;
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
import org.openmrs.module.chica.ImmunizationForecastLookup;
import org.openmrs.module.chica.ImmunizationPrevious;
import org.openmrs.module.chica.ImmunizationQueryOutput;

public class LookupVaccineGiven implements Rule
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
	
	/*
	 * hpv_given:= call LookupVaccineGiven with "HPV", "3";
	 * mcv_given:= call LookupVaccineGiven with "MCV", "1";
	 */

	public Result eval(LogicContext context, Integer patientId,
			Map<String, Object> parameters) throws LogicException
		{ 
		
			int doses= 0;
			String shortName = null;
			String vaccineName = null;
			HashMap<String, String> map = this.setupVISNameLookup();
			
			if (parameters != null){
				
				shortName = (String) parameters.get("param1");
				if (shortName == null || (vaccineName = map.get(shortName) ) == null ){
					return Result.emptyResult();
				}
				
				ImmunizationQueryOutput immunizations = 
					ImmunizationForecastLookup.getImmunizationList(patientId);
				
				if(immunizations != null){
					//patient has immunization records in list
					HashMap<String,HashMap<Integer,ImmunizationPrevious>> prevImmunizations = 
							immunizations.getImmunizationPrevious();	
					
					if (prevImmunizations != null){
						HashMap<Integer,ImmunizationPrevious> vaccineDoses = prevImmunizations.get(vaccineName);
						if(vaccineDoses != null ){
							log.info("HPV Study: patient: # " + patientId + " , Vaccine: " + shortName 
									+ " , doses = " + vaccineDoses.size());
							 return new Result(vaccineDoses.size());
						}
					}
					log.info("HPV Study: patient: # " + patientId + " , Vaccine: " + shortName 
									+ " , doses =  0") ;
					return new Result(0);
					
				}
		}
		log.info("HPV Study: No records from CHIRP for patient id = " + patientId  );
		return  Result.emptyResult();
			
	}
	
	private HashMap<String, String> setupVISNameLookup() {

		HashMap<String, String> map = new HashMap<String, String>();

	
		map.put("DTaP", "DTaP, unspecified formulation");
		map.put("HepA", "Hep A, unspecified formulation");
		map.put("HepB", "Hep B, unspecified formulation");
		map.put("Hib","Hib, unspecified formulation");
		map.put("influenza", "Influenza, unspecified formulation");
		map.put("MMR", "MMR");
		map.put("PPV", "pneumococcal, unspecified formulation");
		map.put("PCV13", "Pneumococcal Conjugate, unspecified formulation");
		map.put("Rotavirus", "rotavirus, unspecified formulation");
		map.put("varicella", "Varicella");
		map.put("HPV", "HPV, unspecified formulation");
		map.put("flulive", "influenza, live, intranasal");
		map.put("IPV", "polio, unspecified formulation");
		map.put("MCV", "meningococcal MCV4, unspecified formulation");
		map.put("PPD", "TST-PPD intradermal");
		map.put("Tdap", "Td(adult) unspecified formulation");
		
		
		return map;
	}
	
	
}