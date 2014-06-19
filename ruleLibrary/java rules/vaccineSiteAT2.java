package org.openmrs.module.chica.rule;

import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
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
import org.openmrs.module.chica.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.ObsAttribute;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.ObsAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.module.dss.logic.op.OperandObject;
import org.openmrs.module.dss.service.DssService;

public class consumeImmunizationGiven implements Rule
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
		org.openmrs.api.ObsService obsService = Context.getObsService();
		Patient patient = patientService.getPatient(patientId);
		FormInstance formInstance = null;
		String fieldName = null;
		String conceptName  = null;
		Integer encounterId = null;
		Integer ruleId = null;
		Integer locationTagId = null;
		Obs obs = null;

		if (parameters != null)
		{
			formInstance = (FormInstance) parameters.get("formInstance");
			fieldName = (String) parameters.get("fieldName");
			//concept name is actually the name of the vaccine, so it is the answer.
			vaccineName = (String) parameters.get("concept");
			
			if(conceptName == null)
			{
				return Result.emptyResult();
			}
			
			encounterId = (Integer) parameters.get("encounterId");
			locationTagId = (Integer) parameters.get("locationTagId");
			ruleId = (Integer) parameters.get("ruleId");
		}

		if (formInstance == null)
		{
			throw new LogicException(
					"The xml datasource requires a formInstanceId");
		}

		LogicCriteria formIdCriteria = new LogicCriteriaImpl(Operator.EQUALS, new OperandObject(formInstance));
	
		LogicCriteria fieldNameCriteria = new LogicCriteriaImpl(fieldName);
		formIdCriteria = formIdCriteria.and(fieldNameCriteria);

		Result ruleResult = context.read(patientId, this.logicService
				.getLogicDataSource("xml"), formIdCriteria);
		
		ConceptService conceptService = Context.getConceptService();
		
		if(ruleResult != null&&ruleResult.toString()!=null&&
				ruleResult.toString().length()>0)
		{
			
			String enteredValue = ruleResult.toString();
			//concept name has the name of the vaccine - so  it is actually the answer.
			if(enteredValue != null && !enteredValue.equalsIgnoreCase("")){;
			
			
			
				obs = org.openmrs.module.chica.util.Util.saveObsWithStatistics(patient, 
						conceptService.getConceptByName("CHICA Vaccine Given"),
						encounterId, conceptName,formInstance,ruleId,locationTagId);
				
				ChirdlUtilBackportsService service = Context.getService(ChirdlUtilBackportsService.class);
				
				//save obs attributes for left/right, arm/thigh, lot number
				
				if (obs == null){
					Result.emptyResult();
				}
				
				String attrName = "AT";
				if (enteredValue.equalsIgnoreCase("A")) {
					answer = "arm";
				}
				if (enteredValue.equalsIgnoreCase("T")) {
					answer = "thigh";
				}
				
				ObsAttribute obsAttr = service.getObsAttributeByName(attrName);
				if (obsAttr == null) {
					log.error("Observation attribute not found with name " + attrValue);
					
				}
				
				ObsAttributeValue obsAttrVal = new ObsAttributeValue();
				obsAttrVal.setObsAttributeId(obsAttr.getObsAttributeId());
				obsAttrVal.setObsId(obs.getObsId());
				obsAttrVal.setValue(attrValue);
				service.saveObsAttributeValue(obsAttrVal);
				
			}
			
		
		}	
		
		return Result.emptyResult();
	}
}