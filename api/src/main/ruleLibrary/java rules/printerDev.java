package org.openmrs.module.chica.rule;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.util.Util;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.service.EncounterService;

public class printerDev implements Rule
{
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
		Result ruleResult = null;

		ChirdlUtilBackportsService chirdlUtilBackportsService = Context.getService(ChirdlUtilBackportsService.class);
		FormInstance formInstance = (FormInstance) parameters
				.get("formInstance");
		Integer encounterId = (Integer) parameters.get("encounterId");
		Integer locationTagId = (Integer) parameters.get("locationTagId");
		
		if (encounterId != null)
		{
			EncounterService encounterService = Context
					.getService(EncounterService.class);
			Encounter encounter = (Encounter) encounterService.getEncounter(encounterId);
			String printerLocation = encounter.getPrinterLocation();
			
			if (formInstance != null)
			{
				Integer formId = formInstance.getFormId();
				Integer locationId = formInstance.getLocationId();
				FormAttributeValue formAttrValue = chirdlUtilBackportsService.getFormAttributeValue(formId, 
						"useAlternatePrinter",locationTagId,locationId);
				
				String formAttributeName = "defaultPrinter";
				
				//use the alternate printer if useAlternatePrinter attribute is true
				if(formAttrValue != null){
					if(formAttrValue.getValue() != null && 
							formAttrValue.getValue().equalsIgnoreCase("true")){
						formAttributeName = "alternatePrinter";
					}
				}
				
				formAttrValue = chirdlUtilBackportsService
						.getFormAttributeValue(formId, formAttributeName,locationTagId,locationId);
				if (formAttrValue != null)
				{
					return new Result(formAttrValue.getValue());
				}
			}
		}
		return Result.emptyResult();
	}
}