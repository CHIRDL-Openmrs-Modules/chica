package org.openmrs.module.chica.rule;

import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.op.Operator;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.atd.hibernateBeans.FormInstance;

import org.openmrs.module.chica.impl.ChicaServiceImpl;
import org.openmrs.module.chica.util.Util;
import org.openmrs.module.atd.service.ATDService;

public class consumeWeight implements Rule
{
	private Log log = LogFactory.getLog(this.getClass());
	private LogicService logicService = Context.getLogicService();

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getParameterList()
	 */
	public Set<RuleParameterInfo> getParameterList()
	{
		return null;
	}

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getDependencies()
	 */
	public String[] getDependencies()
	{
		return new String[]
		{};
	}

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getTTL()
	 */
	public int getTTL()
	{
		return 0; // 60 * 30; // 30 minutes
	}

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getDatatype(String)
	 */
	public Datatype getDefaultDatatype()
	{
		return Datatype.CODED;
	}
	public Result eval(LogicContext context, Patient patient,
			Map<String, Object> parameters) throws LogicException
	{
		FormInstance formInstance = null;
		String fieldName = null;
		String conceptName  = null;
		Integer encounterId = null;
		ATDService atdService = Context.getService(ATDService.class);
		Integer ruleId = null;
		Integer locationTagId = null;

		if (parameters != null)
		{
			formInstance = (FormInstance) parameters.get("formInstance");
			fieldName = (String) parameters.get("fieldName");
			conceptName = (String) parameters.get("concept");
			ruleId = (Integer) parameters.get("ruleId");

			if(conceptName == null)
			{
				return Result.emptyResult();
			}
			
			encounterId = (Integer) parameters.get("encounterId");
			locationTagId = (Integer) parameters.get("locationTagId");
		}

		if (formInstance == null)
		{
			throw new LogicException(
					"The xml datasource requires a formInstanceId");
		}

		LogicCriteria formIdCriteria = new LogicCriteria("formInstance").equalTo(formInstance);

		LogicCriteria fieldNameCriteria = new LogicCriteria(fieldName);
		formIdCriteria = formIdCriteria.and(fieldNameCriteria);

		Result ruleResult = context.read(patient, this.logicService
				.getLogicDataSource("xml"), formIdCriteria);
		
		String primaryResult = ruleResult.toString();
		
		fieldName = (String) parameters.get("child0");
		formIdCriteria = new LogicCriteria("formInstance").equalTo(formInstance);

		fieldNameCriteria = new LogicCriteria(fieldName);
		formIdCriteria = formIdCriteria.and(fieldNameCriteria);

		ruleResult = context.read(patient, this.logicService
				.getLogicDataSource("xml"), formIdCriteria);
		
		String secondaryResult = ruleResult.toString();
			
		ConceptService conceptService = Context.getConceptService();
		
		if((primaryResult == null || primaryResult.length() == 0) && 
				(secondaryResult == null || secondaryResult.length() == 0)){
			return Result.emptyResult();
		}
		
		if(primaryResult == null || primaryResult.length() == 0)
		{
			primaryResult = "0";
		}
		
		if(secondaryResult == null || secondaryResult.length() == 0)
		{
			secondaryResult = "0";
		}
		
		Integer locationId = (Integer) parameters.get("locationId");
		LocationService locationService = Context.getLocationService();
		Location location = locationService.getLocation(locationId);
		String fullResult = null;
		
		if(location!= null){
			//if this is Pecar, consume kilograms
			if(location.getName().equalsIgnoreCase("PEPS")){
				fullResult = consumeKilo(primaryResult,secondaryResult);
			}else{
				//consume lb. or lb. and oz. based on age 
				fullResult = consumeLbOrLbOz(primaryResult,secondaryResult,patient,parameters);
			}	
		}

		if(fullResult != null&&fullResult.length()>0)
		{
			Util.saveObs(patient, conceptService.getConceptByName(conceptName),
					encounterId, fullResult,formInstance,
					ruleId,locationTagId);
		}
		
		return Result.emptyResult();
	}
	
	private String consumeLbOrLbOz(String primaryResult,
			String secondaryResult,Patient patient,
			Map<String, Object> parameters){
		ATDService atdService = Context.getService(ATDService.class);
		ConceptService conceptService = Context.getConceptService();
		String fullResult = null;
		String weightUnit = atdService.evaluateRule( "birthdate>weightSF", 
				  patient,parameters,null).toString();
		
		if(weightUnit != null && weightUnit.equals("oz."))
		{
			fullResult = processWeight(primaryResult,secondaryResult);
		}else
		{
			fullResult = primaryResult+"."+secondaryResult;
		}
		
		return fullResult;
		
	}
	
	private String consumeKilo(String primaryResult,
			String secondaryResult){
		String fullResult = primaryResult+"."+secondaryResult;
		ConceptService conceptService = Context.getConceptService();
		//convert kilograms to lbs
		try{
			double kilograms = (new Double(fullResult)).doubleValue();
			double pounds = org.openmrs.module.chirdlutil.util.Util.convertUnitsToEnglish(
					kilograms, org.openmrs.module.chirdlutil.util.Util.MEASUREMENT_KG);
			fullResult = String.valueOf(pounds);
		}catch(Exception e){
			log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}
		return fullResult;
	}
	
	private String processWeight(String poundsString,String ouncesString)
	{
		double pounds = 0;
		int ounces = 0;
		if (ouncesString != null)
		{
			ounces = Integer.parseInt(ouncesString);
		}
		if (poundsString != null)
		{
			pounds = Integer.parseInt(poundsString);
		}

		pounds += ounces / 16.0;
		String value = String.valueOf(pounds);
		if ((ounces / 16.0) > 1)
		{
			this.log.warn("More than 16 ounces entered for weight");
		}
		
		return value;
	}
}