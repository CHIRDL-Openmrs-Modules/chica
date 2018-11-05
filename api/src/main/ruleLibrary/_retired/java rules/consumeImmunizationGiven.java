package org.openmrs.module.chica.rule;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
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
import org.openmrs.module.dss.logic.op.OperandObject;

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
		EncounterService encounterService = Context.getEncounterService();
		Patient patient = patientService.getPatient(patientId);
		System.out.println("Running consumeImmunizationGiven");
		FormInstance formInstance = null;
		String fieldName = null;
		String conceptName  = null;
		Integer encounterId = null;
		Integer ruleId = null;
		Integer locationTagId = null;
		Obs obs = null;
		Encounter encounter = null;
		Integer formFieldId = null;
		
		if (parameters != null)
		{
			formInstance = (FormInstance) parameters.get("formInstance");
			fieldName = (String) parameters.get("fieldName");
			
			conceptName =  (String) parameters.get("concept");
			if(conceptName == null)
			{
				return Result.emptyResult();
			}
			
			encounterId = (Integer) parameters.get("encounterId");
			if (encounterId == null || (encounter = encounterService.getEncounter(encounterId) )== null){
				return Result.emptyResult();
			}
			
			locationTagId = (Integer) parameters.get("locationTagId");
			ruleId = (Integer) parameters.get("ruleId");
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
		
		ConceptService conceptService = Context.getConceptService();
		
		
		
		if(ruleResult != null&&ruleResult.toString()!=null&&
				ruleResult.toString().length()>0)
		{
			
			String enteredValue = ruleResult.toString();
			
			//concept name has the name of the vaccine - so  it is actually the answer.
			if(enteredValue != null && !enteredValue.equalsIgnoreCase("")){
				String conceptAnswer = lookupVaccineName(enteredValue);
				if (conceptAnswer == null || conceptAnswer.trim().equals("") ){
					log.info("Scanned vaccine name " + enteredValue + 
							" not mapped in rule. Use scanned text. ");
					conceptAnswer = enteredValue;
					
				}
				log.info("concept answer=" + conceptAnswer + "-");
				//obs = org.openmrs.module.chirdlutil.util.Util
				//	.saveObs(patient, null, getTTL(), conceptAnswer, encounter.getEncounterDatetime());
				obs = org.openmrs.module.chica.util.Util.saveObsWithStatistics(patient, 
						conceptService.getConceptByName(conceptName),
					encounterId, conceptAnswer,formInstance,ruleId,locationTagId, formFieldId); // DWE CHICA-437 Added formFieldId
				
				
			}
			
		
		}	
		
		return Result.emptyResult();
	}
	
	private HashMap<String, String> setupVaccineConceptNameMap() {

		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put("DTaP/Hep B/ IPV", "DTaP/Hep B/IPV");
		map.put("DTaP/Hib/IPV", "DTaP/Hib/IPV");
		map.put("DTaP", "DTaP");
		map.put("DTap/IPV", "DTaP/IPV");
		map.put("Hep A 2 dose", "Hep A 2 dose - Ped/Adol");
		map.put("Hep A 3 dose", "Hep A 3 dose - Ped/Adol");
		map.put("Hep B/Hib", "Hep B/Hib");
		map.put("Hep B", "Hep B");
		map.put("Hib-PRP-T", "Hib--PRP-T");
		map.put("Hib-PRP-OMP", "Hib--PRP-OMP");
		map.put("Influ Inact 48+ mos", "Influ Inact 48+ mos pres free");
		map.put("Influ nasal spray", "Influenza Nasal Spray");
		map.put("Influ Nasal", "Influenza Nasal Spray");
		map.put("Influ Nasal Spray", "Influenza Nasal Spray");
		map.put("Influ Split", "Influenza Split");
		map.put("IPV", "IPV");
		map.put("Mening. (MCV4P)", "Mening. (MCV4P)");
		map.put("Mening. (MCV4O)", "Mening. (MCV4O)");
		map.put("MMR", "MMR");
		map.put("MMR/Varicella", "MMR/Varicella");
		map.put("Pneumococcal (PCV7)", "Pneumococcal(PCV7)");
		map.put("Pneumococcal (PPSV)", "Pneumococcal(PPSV)");
		map.put("Pneumococcal (PCV13)", "Pneumococcal(PCV13)");
		map.put("Rotavirus, mono", "Rotavirus, monovalent RV1");
		map.put("Rotavirus, pentavalent RV5", "Rotavirus, pentavalent RV5");
		map.put("Rotavirus, penta", "Rotavirus, pentavalent RV5");
		map.put("Rotavirus, tetra", "Rotavirus, tetravalent");
		map.put("Td (Adsorbed)", "Td (adult), adsorbed");
		map.put("HPV (Quadrivalent)", "HPV, quadrivalent");
		map.put("HPV (Bivalent)", "HPV, bivalent");
		map.put("Tdap", "Tdap");
		map.put("Td", "Td (adult)");
		map.put("Td (Preservative free)", "Td (adult) preservative free");
		map.put("Td (Adsorbed)", "Td (adult), adsorbed");
		map.put("Influ Inact 36+ mos pres free", "Influ Inact 36+ mos pres free");
		map.put("Hep A 3 dose - Ped/Adol", "Hep A 3 dose - Ped/Adol");
		map.put("Varicella", "Varicella");
		map.put("varicella", "Varicella");
		map.put("PPD_Test", "TST-PPD intradermal");

		
		return map;

	}
	
	private String lookupVaccineName(String name) {
		HashMap<String, String> map = setupVaccineConceptNameMap();
		return map.get(name);
	}
	
}