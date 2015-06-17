
package org.openmrs.module.chica.rule;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
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
		log.info("starting rule");
		int doses= 0;
			String vaccineName = null;
			Integer doseNumber = null;
			
			if (parameters != null){
				log.info("parameters exist");
				vaccineName = (String) parameters.get("param1");
				log.info("vaccine name " + vaccineName);
				if (vaccineName == null){
					log.info("no vaccine name.");
					return new Result(0);
				}
				HashMap<String, String> fullNames = this.setFullNameLookup();
				String vaccineFullName = fullNames.get(vaccineName);
				ImmunizationQueryOutput immunizations = 
					ImmunizationForecastLookup.getImmunizationList(patientId);
				
				if(immunizations != null){
					log.info("has immunications");
					//patient has immunization records
					HashMap<String,ImmunizationForecast> forecastedImmunizations = immunizations.getImmunizationForecast();
					if (forecastedImmunizations == null){
						log.info("no forecasts");
						//Patient was matched,received a VXR, but no forecast immunizations.
						return new Result(0);
					}
					log.info("has forecasts");
					 log.info("size of forecasts = " + forecastedImmunizations.size());
					HashMap<String,ImmunizationForecast> forcasts = immunizations.getImmunizationForecast();
					if (forcasts == null){
						log.info("forcast map is null");
					} else{
						log.info("forecast map is not null");
						;
						Collection<ImmunizationForecast> col = forcasts.values();
						for (ImmunizationForecast f : col){
							log.info("forloop");
							log.info (f.getDose() + " " +
							f.getVaccineCode()+ " " +
							f.getDose()+ " " +
							f.getVaccineName() + " " );
						}
						
						log.info("exiting while loop.");
					}
					
					ImmunizationForecast immunForecast = forcasts.get(vaccineFullName);
					if (immunForecast == null){
						log.info("forecast is null for that vaccine");
					} else {
						
					}
					
					
					java.util.Date todaysDate = new java.util.Date();

					log.info(immunForecast.getDateDue() + " date due");
					if(immunForecast != null&&immunForecast.getDateDue().compareTo(todaysDate)<=0){
						
						doseNumber = immunForecast.getDose();
						log.info("due for dose" + doseNumber);
						return new Result(doseNumber);
						//or
						//return new Result("yes");
						//
					}
					//up to date - Returns empty Result object.
					
						
					log.info("up to date");
				}
		}
			log.info("end");
		
		return  Result.emptyResult();
		

			
			}

	private String getName(String vaccineName){
		String shortName = null;
		ConceptService conceptService = Context.getConceptService();
		Concept concept = conceptService.getConceptByName(vaccineName);
		if (concept == null || concept.getConceptId() == null){
			return "";
		}


		return shortName;
	}
	
	private HashMap<String, String> setupVISNameLookup() {

		HashMap<String, String> map = new HashMap<String, String>();

		map.put("DTaP, unspecified formulation", "DTaP");
		map.put("Hep A, unspecified formulation", "HepA");
		map.put("Hep B, unspecified formulation", "HepB");
		map.put("Hib, unspecified formulation","Hib");
		map.put("influenza, unspecified formulation", "Influenza");
		map.put("MMR", "MMR");
		map.put("pneumococcal, unspecified formulation", "PPV");
		map.put("pneumococcal polysaccharide PPV23", "PPV");
		map.put("Pneumococcal Conjugate, unspecified formulation", "PCV13");
		map.put("rotavirus, unspecified formulation", "Rotavirus");
		map.put("varicella", "Varicella");
		map.put("Varicella", "Varicella");
		map.put("HPV, unspecified formulation", "HPV");
		map.put("influenza, live, intranasal", "flulive");
		map.put("polio, unspecified formulation", "IPV");
		map.put("meningococcal MCV4, unspecified formulation", "MCV");
		map.put("TST-PPD intradermal", "PPD");
		map.put("Td(adult) unspecified formulation", "Tdap");
		
		
		return map;
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