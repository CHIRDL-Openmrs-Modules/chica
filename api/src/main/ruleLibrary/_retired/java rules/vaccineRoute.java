package org.openmrs.module.chica.rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
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
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.ObsAttribute;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.ObsAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.module.dss.logic.op.OperandObject;

public class vaccineRoute implements Rule
{
	
	private static final Logger log = LoggerFactory.getLogger(vaccineRoute.class);
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
			Map<String, Object> parameters) throws LogicException {
		PatientService patientService = Context.getPatientService();
		Patient patient = patientService.getPatient(patientId);
		ConceptService conceptService = Context.getConceptService();
		FormInstance formInstance = null;
		String fieldName = null;
		String conceptName = null;
		Integer encounterId = null;
		Integer ruleId = null;
		Integer locationTagId = null;
		Integer formFieldId = null;
		
		if (parameters != null) {
			formInstance = (FormInstance) parameters.get("formInstance");
			fieldName = (String) parameters.get("fieldName");
			// concept name is actually the name of the vaccine, so it is the
			// answer.
			conceptName = (String) parameters.get("concept");

			if (conceptName == null) {
				return Result.emptyResult();
			}

			encounterId = (Integer) parameters.get("encounterId");
			locationTagId = (Integer) parameters.get("locationTagId");
			ruleId = (Integer) parameters.get("ruleId");
			formFieldId = (Integer)parameters.get("formFieldId"); // DWE CHICA-437
		}

		if (formInstance == null) {
			throw new LogicException(
					"The form datasource requires a formInstanceId");
		}

		LogicCriteria formIdCriteria = new LogicCriteriaImpl(Operator.EQUALS,
				new OperandObject(formInstance));

		LogicCriteria fieldNameCriteria = new LogicCriteriaImpl(fieldName);
		formIdCriteria = formIdCriteria.and(fieldNameCriteria);

		Result ruleResult = context.read(patientId, this.logicService
				.getLogicDataSource("form"), formIdCriteria);

		if (ruleResult != null && ruleResult.toString() != null
				&& ruleResult.toString().length() > 0) {
			// Test if observation exists already for this patient, encounter,
			// vaccine

			String enteredValue = ruleResult.toString();
			
			if (enteredValue != null && !enteredValue.equalsIgnoreCase("")) {
				if (enteredValue.equalsIgnoreCase("IM")) {
					enteredValue = "Intramuscular";
				}
				if (enteredValue.equalsIgnoreCase("SC")) {
					enteredValue = "Subcutaneous";
				}
				if (enteredValue.equalsIgnoreCase("PO")) {
					enteredValue = "Oral Route";
				}
				org.openmrs.module.chica.util.Util.saveObsWithStatistics(
						patient, conceptService
								.getConceptByName(conceptName),
						encounterId, enteredValue, formInstance, ruleId,
						locationTagId, formFieldId); // DWE CHICA-437 Added formFieldId

				/*ChirdlUtilBackportsService service = Context
						.getService(ChirdlUtilBackportsService.class);

				// Save obs attributes for left/right, arm/thigh, or lot number
				if (obs == null) {
					Result.emptyResult();
				}

				if (enteredValue.equalsIgnoreCase("R")) {
					enteredValue = "right";
				}
				if (enteredValue.equalsIgnoreCase("L")) {
					enteredValue = "left";
				}

				ObsAttribute leftRightAttr = service
						.getObsAttributeByName("Site_left_right");
				if (leftRightAttr == null) {
					log
							.error("No observation attribute found with name Site_left_right."
									+ " Unable to save vaccine site left or right");
					return Result.emptyResult();
				}
				// Save the observation attribute with the resource rank
				Integer obsAttrId = leftRightAttr.getObsAttributeId();
				Integer obsId = obs.getObsId();
				ObsAttributeValue siteLeftRightVal = new ObsAttributeValue();
				siteLeftRightVal.setObsId(obsId);
				siteLeftRightVal.setValue(enteredValue);
				siteLeftRightVal.setObsAttributeId(obsAttrId);
				service.saveObsAttributeValue(siteLeftRightVal);*/

			}

		}

		return Result.emptyResult();
	}
}