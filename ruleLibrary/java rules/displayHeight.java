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

import java.util.Map;
import java.util.Set;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;

public class displayHeight implements Rule
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
		PatientService patientService = Context.getPatientService();
		Patient patient = patientService.getPatient(patientId);
		if(parameters == null)
		{
			return Result.emptyResult();
		}
		
		String conceptName = (String) parameters.get("concept");
		
		if(conceptName == null)
		{
			return Result.emptyResult();
		}
		Result ruleResult = null;
		
		Integer encounterId = (Integer) parameters.get("encounterId");
		
		LogicCriteria conceptCriteria = new LogicCriteriaImpl(
				conceptName);
		
		LogicCriteria fullCriteria = null;
		
		if(encounterId != null)
		{
			LogicCriteria encounterCriteria = 
				new LogicCriteriaImpl("encounterId").equalTo(encounterId.intValue());
			
			fullCriteria = conceptCriteria.and(encounterCriteria);
		}else
		{
			fullCriteria = conceptCriteria;
		}
		ruleResult = context.read(patientId,context.getLogicDataSource("obs"), 
				fullCriteria.last());

		if (ruleResult != null&&ruleResult.size()>0)
		{
			ruleResult = ruleResult.get(0);
			Integer locationId = (Integer) parameters.get("locationId");
			LocationService locationService = Context.getLocationService();
			Location location = locationService.getLocation(locationId);
			
			if(location!= null){
				//if this is Pecar or IU Health, just return inches or cm based on age
				String locationName = location.getName();
				if(locationName.equalsIgnoreCase("PEPS") || locationName.equalsIgnoreCase(ChirdlUtilConstants.LOCATION_RIIUMG)){
					return inchesOrCmResult(ruleResult,
							parameters,patient);
				}else{
					//return inches
					return inchesResult(ruleResult);
				}
			}
		}
		return Result.emptyResult();
	}
	
	private Result inchesResult(Result ruleResult){
		return ruleResult;
	}
	
	private Result inchesOrCmResult(Result ruleResult,
			Map<String, Object> parameters,Patient patient){
		ATDService atdService = Context.getService(ATDService.class);
		String heightUnit = atdService.evaluateRule( "birthdate>heightUnits", 
				  patient,parameters).toString();
		if(heightUnit != null && heightUnit.equals("cm."))
		{
			double inches = ruleResult.toNumber();
			double cm = 
				org.openmrs.module.chirdlutil.util.Util.convertUnitsToMetric(inches, 
						org.openmrs.module.chirdlutil.util.Util.MEASUREMENT_IN);
			ruleResult = new Result(String.valueOf(cm));
		}
		return ruleResult;
	}
}
