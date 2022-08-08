/*
 Title : createVXUMessage
 Filename:  createVXUMessage.java
 Version : 0 . 0
 Institution : Indiana University School of Medicine
 Author : Meena Sheley
 Specialist : 
 Date : 
 Validation :
 Purpose : Creates a VXU^V04 hl7 message to update a patients immuninzation records 
 the CHIRP registry
 Keywords : 
 Citations : 
 Links :
 */
package org.openmrs.module.chica.rule;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmrs.Encounter;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.Duration;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chica.hl7.immunization.ImmunizationRegistryQuery;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstanceAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;





public class queryCHIRP implements Rule
{
	private static final Logger log = LoggerFactory.getLogger(queryCHIRP.class);
	
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
		try{ 
			PatientService patientService = Context.getPatientService();
			EncounterService encounterService = Context.getEncounterService();
			Integer encounterId = null;
			Integer locationId = null;
			Integer formId = null;
			Integer formInstanceId = null;
			
			if (parameters != null) {
				encounterId = (Integer) parameters.get("encounterId");
				locationId = (Integer) parameters.get("locationId");
				FormInstance formInstance = (FormInstance) parameters.get("formInstance");
				
				if (formInstance != null) {
					formId = formInstance.getFormId();
					formInstanceId = formInstance.getFormInstanceId();
				}
			}
			
			Encounter encounter = encounterService.getEncounter(encounterId);
			parameters.get(encounter);
			ChirdlUtilBackportsService chirdlUtilBackportsService = Context.getService(ChirdlUtilBackportsService.class);
			log.info(" Immunization query chirp : form id: " + formId + " form instance = " + formInstanceId + " location id = ");
			FormInstanceAttributeValue attributeValue = chirdlUtilBackportsService.getFormInstanceAttributeValue(formId, formInstanceId, locationId, "trigger");
			if (attributeValue != null && attributeValue.getValue().equals("forcePrint")){
				//Requery chirp if print is triggered by a force print
				ImmunizationRegistryQuery.queryCHIRP(encounter);
				
				log.info("Force print requested for fid: " + formInstanceId);
			} else {
				log.info("Not a force print for  fid: " + formInstanceId);
			}
			Result chirp_status=context.read(
					patientId,context.getLogicDataSource("obs"),
					new LogicCriteriaImpl("CHIRP_Status").within(Duration.days(-1)).last());
			return chirp_status;
			
			
		} catch (Exception e){
			log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}
		return Result.emptyResult();
	}
	
	
	
	
}
	