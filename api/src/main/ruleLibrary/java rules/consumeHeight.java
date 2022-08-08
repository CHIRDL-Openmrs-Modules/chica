package org.openmrs.module.chica.rule;

import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.logic.op.Operator;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.dss.logic.op.OperandObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class consumeHeight implements Rule
{
	private static final Logger log = LoggerFactory.getLogger(consumeHeight.class);
	private LogicService logicService = Context.getLogicService();

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getParameterList()
	 */
	@Override
	public Set<RuleParameterInfo> getParameterList()
	{
		return null;
	}

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getDependencies()
	 */
	@Override
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
	@Override
	public int getTTL()
	{
		return 0; // 60 * 30; // 30 minutes
	}

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getDatatype(String)
	 */
	@Override
	public Datatype getDefaultDatatype()
	{
		return Datatype.CODED;
	}
	@Override
	public Result eval(LogicContext context, Integer patientId,
			Map<String, Object> parameters) throws LogicException
	{
		PatientService patientService = Context.getPatientService();
		Patient patient = patientService.getPatient(patientId);
		FormInstance formInstance = null;
		String fieldName = null;
		String conceptName  = null;
		Integer encounterId = null;
		Integer ruleId = null;
		Integer locationTagId = null;
		Integer formFieldId = null;

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
			formFieldId = (Integer)parameters.get("formFieldId"); // DWE CHICA-437
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
			org.openmrs.module.chica.util.Util.voidObsForConcept(concept,encounterId, formFieldId); // DWE CHICA-437 Added formFieldId
			
			org.openmrs.module.chica.util.Util.saveObsWithStatistics(patient, concept,
					encounterId, fullResult,formInstance,ruleId,locationTagId, formFieldId); // DWE CHICA-437 Added formFieldId
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