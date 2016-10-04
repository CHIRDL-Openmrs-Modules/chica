
package org.openmrs.module.chica.rule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chica.ImmunizationForecast;
import org.openmrs.module.chica.ImmunizationForecastLookup;
import org.openmrs.module.chica.ImmunizationQueryOutput;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;

public class vaccineStarRule implements Rule
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
		
		String vaccineName = (String) parameters.get("concept");
		HashMap<String, String> map = this.setupVISNameLookup();
		String shortName = map.get(vaccineName);
		String relatedVaccine = getRelatedVaccine(vaccineName);
		
		Integer locationId = (Integer) parameters.get("locationId");
		ImmunizationQueryOutput immunizations = 
			ImmunizationForecastLookup.getImmunizationList(patientId);

		if(immunizations != null){
			HashMap<String,ImmunizationForecast> forecastedImmunizations = 
				immunizations.getImmunizationForecast();

			if(forecastedImmunizations != null){
		
				ImmunizationForecast immunForecast = forecastedImmunizations.get(vaccineName);
				ImmunizationForecast immunForecastRelated  =  forecastedImmunizations.get(relatedVaccine);		
				java.util.Date todaysDate = new java.util.Date();

				if((immunForecast != null && immunForecast.getDateDue().compareTo(todaysDate)<=0)
						||(immunForecastRelated != null && immunForecastRelated.getDateDue().compareTo(todaysDate)<=0)){
					// Create the JIT		
					
					// Find out if patient is Spanish speaking
					LogicCriteria conceptCriteria = new LogicCriteriaImpl("preferred_language");
					Result languageResult = context.read(patientId, context.getLogicDataSource("obs"), conceptCriteria.last());
					String language = null;
					if (languageResult != null && languageResult.toString().length() > 0) {
						language = languageResult.toString();
					}
					LogicService logicService = Context.getLogicService();
					parameters.put("mode", "PRODUCE");
					
					if (shortName != null && !shortName.equalsIgnoreCase("") ){
						String jitLanguage = "";
						if (language != null && language.equalsIgnoreCase("Spanish")){
							jitLanguage = "_SP";
						}
						if (shortName.equalsIgnoreCase("HPV")){
							//clinic requests only Gardasil HPV  
							shortName = "Hpv-Gardasil";
						}
						
						if (shortName.equalsIgnoreCase("Influenza")){
							//clinic requests window for flu shot
							Calendar calendar = Calendar.getInstance();	 
							if (calendar.get(Calendar.MONTH)>= Calendar.APRIL
									&& calendar.get(Calendar.MONTH) <= Calendar.AUGUST){
								return new Result(" ");
							}
							
						}
						parameters.put("param1","VIS_" + shortName + "_JIT" + jitLanguage);
						log.info("VIS_" + shortName+ "_JIT" + jitLanguage);
						FormInstance formInstance = new FormInstance();
						formInstance.setLocationId(locationId);
						parameters.put("formInstance", formInstance);
						parameters.put(ChirdlUtilConstants.PARAMETER_3, "false");
						logicService.eval(patientId, "CREATE_JIT", parameters);
					}

					return new Result("*");
				}else{
					return new Result(" ");
				}   
			}
		}   
		return new Result(" ");
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
		map.put("meningococcal B, unspecified formulation", "MenB");
		map.put("TST-PPD intradermal", "PPD");
		map.put("Td(adult) unspecified formulation", "Tdap");
		
		
		return map;
	}
	
	private String getRelatedVaccine(String vaccine) {
	
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("meningococcal MCV4, unspecified formulation", "meningococcal B, unspecified formulation");	
		return map.get(vaccine);
	}
	
}