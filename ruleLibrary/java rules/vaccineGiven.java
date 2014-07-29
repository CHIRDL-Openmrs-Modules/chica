package org.openmrs.module.chica.rule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.openmrs.module.chica.gis.Placemark;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.ObsAttribute;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.ObsAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.module.dss.logic.op.OperandObject;

public class vaccineSiteAT implements Rule {

	private Log log = LogFactory.getLog(this.getClass());
	private LogicService logicService = Context.getLogicService();

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getParameterList()
	 */
	public Set<RuleParameterInfo> getParameterList() {
		return null;
	}

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getDependencies()
	 */
	public String[] getDependencies() {
		return new String[] {};
	}

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getTTL()
	 */
	public int getTTL() {
		return 0; // 60 * 30; // 30 minutes
	}

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getDatatype(String)
	 */
	public Datatype getDefaultDatatype() {
		return Datatype.CODED;
	}

	public Result eval(LogicContext context, Integer patientId,
			Map<String, Object> parameters) throws LogicException {
		PatientService patientService = Context.getPatientService();
		Patient patient = patientService.getPatient(patientId);
		EncounterService encounterService = Context.getEncounterService();
		ConceptService conceptService = Context.getConceptService();
		ObsService obsService = Context.getObsService();
		FormInstance formInstance = null;
		String fieldName = null;
		String conceptName = null;
		Integer encounterId = null;
		Integer ruleId = null;
		Integer locationTagId = null;
		Obs obs = null;

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
			List<Person> persons = new ArrayList<Person>();
			persons.add(patient);

			List<Concept> answers = new ArrayList<Concept>();
			answers.add(conceptService.getConceptByName(conceptName));

			List<Concept> questions = new ArrayList<Concept>();
			questions.add(conceptService
					.getConceptByName("CHICA Vaccine Given"));

			List<Encounter> encounters = new ArrayList<Encounter>();
			encounters.add(encounterService.getEncounter(encounterId));

			
			String enteredValue = ruleResult.toString();
			// We only want to save the obs if there was a result to
			// arm/thigh, right/left, or lot#. If a value was entered, save
			// CHICA Vaccine Given with an answer of this vaccine concept name.
			if (enteredValue != null && !enteredValue.equalsIgnoreCase("")) {
				;
				obs = org.openmrs.module.chica.util.Util.saveObsWithStatistics(
						patient, conceptService
								.getConceptByName("CHICA Vaccine Given"),
						encounterId, conceptName, formInstance, ruleId,
						locationTagId);

				ChirdlUtilBackportsService service = Context
						.getService(ChirdlUtilBackportsService.class);

				// Save obs attributes for left/right, arm/thigh, or lot number
				if (obs == null) {
					Result.emptyResult();
				}

				if (enteredValue.equalsIgnoreCase("A")) {
					enteredValue = "arm";
				}
				if (enteredValue.equalsIgnoreCase("T")) {
					enteredValue = "thigh";
				}

				ObsAttribute armThighAttr = service
						.getObsAttributeByName("site_arm_thigh");
				if (armThighAttr == null) {
					log
							.error("No observation attribute found with name armThighAttr."
									+ " Unable to save vaccine site arm or thigh");
					return Result.emptyResult();
				}
				// Save the observation attribute with the resource rank
				Integer obsAttrId = armThighAttr.getObsAttributeId();
				Integer obsId = obs.getObsId();
				ObsAttributeValue siteArmThighVal = new ObsAttributeValue();
				siteArmThighVal.setObsId(obsId);
				siteArmThighVal.setValue(enteredValue);
				siteArmThighVal.setObsAttributeId(obsAttrId);
				service.saveObsAttributeValue(siteArmThighVal);

			}

		}

		return Result.emptyResult();
	}
}