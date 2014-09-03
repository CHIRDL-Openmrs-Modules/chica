package org.openmrs.module.chica.rule;

import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.Concept;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.op.Operator;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.dss.logic.op.OperandObject;

import org.openmrs.module.chica.impl.ChicaServiceImpl;
import org.openmrs.module.chica.util.Util;
import org.openmrs.module.atd.service.ATDService;

public class consumeHeight implements Rule
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
	public Result eval(LogicContext context, Integer patientId,
			Map<String, Object> parameters) throws LogicException
	{
		PatientService patientService = Context.getPatientService();
		Patient patient = patientService.getPatient(patientId);
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
					"The form datasource requires a formInstanceId");
		}

		LogicCriteria formIdCriteria = new LogicCriteriaImpl(Operator.EQUALS, new OperandObject(formInstance));

		LogicCriteria fieldNameCriteria = new LogicCriteriaImpl(fieldName);
		formIdCriteria = formIdCriteria.and(fieldNameCriteria);

		Result ruleResult = context.read(patientId, this.logicService
				.getLogicDataSource("form"), formIdCriteria);
		
		String primaryResult = ruleResult.toString();
		
		fieldName = (String) parameters.get("child0");
		formIdCriteria = new LogicCriteriaImpl(Operator.EQUALS, new OperandObject(formInstance));

		fieldNameCriteria = new LogicCriteriaImpl(fieldName);
		formIdCriteria = formIdCriteria.and(fieldNameCriteria);

		ruleResult = context.read(patientId, this.logicService
				.getLogicDataSource("form"), formIdCriteria);
		
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
		
		
		String fullResult = primaryResult+"."+secondaryResult;
		
		Integer locationId = (Integer) parameters.get("locationId");
		LocationService locationService = Context.getLocationService();
		Location location = locationService.getLocation(locationId);
		
		if(location!= null){
			//if this is Pecar, consume kilograms
			if(location.getName().equalsIgnoreCase("PEPS")){
				fullResult = consumeInchesOrCm(fullResult,patient,parameters);
			}else{
				//consume lb. or lb. and oz. based on age
				fullResult = consumeInches(fullResult);
			}
		}
		
		if(fullResult != null&&fullResult.length()>0)
		{
			//void height observations for encounter
			Concept concept = conceptService.getConceptByName(conceptName);
			org.openmrs.module.chica.util.Util.voidObsForConcept(concept,encounterId);
			
			org.openmrs.module.chica.util.Util.saveObsWithStatistics(patient, concept,
					encounterId, fullResult,formInstance,ruleId,locationTagId);
		}
		
		return Result.emptyResult();
	}
	
	private String consumeInches(String fullResult){
		return fullResult;
	}
	
	private String consumeInchesOrCm(String fullResult,Patient patient,
			Map<String, Object> parameters){
		ATDService atdService = Context.getService(ATDService.class);
		String heightUnit = atdService.evaluateRule( "birthdate>heightUnits", 
				  patient,parameters).toString();

		if(heightUnit != null && heightUnit.equals("cm."))
		{
			double measurement = Double.parseDouble(fullResult);
			double inches = 
				org.openmrs.module.chirdlutil.util.Util.convertUnitsToEnglish(measurement, 
						org.openmrs.module.chirdlutil.util.Util.MEASUREMENT_CM);
			fullResult = String.valueOf(inches);
		}
		return fullResult;
	}
}