package org.openmrs.module.chica.rule;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Encounter;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.logic.op.OperandConcept;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;

/**
 * Calculates a person's age in years based from their date of birth to the index date
 */
public class newbornHepB implements Rule {
	
	/**
	 * @see org.openmrs.logic.Rule#eval(LogicContext context, Integer patientId, Map<String, Object>
	 *      parameters)
	 */
	public Result eval(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		
		PatientService patientService = Context.getPatientService();
		Patient patient = patientService.getPatient(patientId);
		String conceptName = "NEWBORN MEDS";
		String conceptAnswer = "Hepatitis B Vaccine (10 mcg/mL)";
		ConceptService conceptService = Context.getConceptService();
		Concept answer = conceptService.getConceptByName(conceptAnswer);
		
		LogicCriteria criteria = new LogicCriteriaImpl(conceptName).equalTo(new OperandConcept(answer));
		
		Result result = context.read(patientId, context.getLogicDataSource("RMRS"), criteria);
		
		String resultString = "";
	
		for (Result currResult : result) {
		
			Concept currConcept = conceptService.getConceptByName(conceptName);
			ObsService obsService = Context.getObsService();
			Obs obs = new Obs();
			String datatypeName = currConcept.getDatatype().getName();
			
			if (datatypeName.equalsIgnoreCase("Numeric")) {
				
				obs.setValueNumeric(currResult.toNumber());
			} else if (datatypeName.equalsIgnoreCase("Coded")) {
				obs.setValueCoded(currResult.toConcept());
			} else {
				obs.setValueText(currResult.toString());
			}
			String obsString = obs.getValueAsString(Context.getLocale());
						
			if (obsString != null && obsString.length() > 0) {
				
				EncounterService encounterService = Context.getService(EncounterService.class);
				Integer encounterId = (Integer) parameters.get("encounterId");
				
				Encounter encounter = encounterService.getEncounter(encounterId);
				
				Location location = encounter.getLocation();
				
				obs.setPerson(patient);
				obs.setConcept(currConcept);
				obs.setLocation(location);
				obs.setEncounter(encounter);
				obs.setObsDatetime(currResult.getResultDate());
				obsService.saveObs(obs, null);
				String pattern = "yyyy-MM-dd";
				
				SimpleDateFormat dateForm = 
					new SimpleDateFormat(pattern);
				if (datatypeName.equalsIgnoreCase("Coded")){
					conceptName = currResult.toConcept().getName().getName();
				}
				resultString += "<row>";
				resultString += "<Vacc_term>" + conceptName + "</Vacc_term>\n";
				resultString += "<Vacc_date>" + dateForm.format(currResult.getResultDate()) + "</Vacc_date>\n";
				resultString += "<PatientID>1</PatientID>";
				resultString += "</row>";
			}
		}
	return new Result(resultString);
		
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getParameterList()
	 */
	public Set<RuleParameterInfo> getParameterList() {
		return null;
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getDependencies()
	 */
	public String[] getDependencies() {
		return new String[] {};
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getTTL()
	 */
	public int getTTL() {
		return 0;
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getDefaultDatatype()
	 */
	public Datatype getDefaultDatatype() {
		return Datatype.NUMERIC;
	}
	
}
