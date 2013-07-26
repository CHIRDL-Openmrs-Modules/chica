package org.openmrs.module.chica.rule;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chica.MedicationListLookup;
import org.openmrs.module.rgccd.Medication;

/**
 * Calculates a person's age in years based from their date of birth to the index date
 */
public class medicationListName implements Rule {
	
	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer, java.util.Map)
	 */
	public Result eval(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		Integer index = null;
		Integer locationTagId = null;
		Integer locationId = null;
		
		if (parameters != null) {
			index = Integer.parseInt((String) parameters.get("param0"));
			locationTagId = (Integer) parameters.get("locationTagId");
			locationId = (Integer) parameters.get("locationId");
		}
		
		LinkedList<Medication> medicationList = MedicationListLookup.getMedicationList(patientId);
		
		if(medicationList == null||medicationList.size()==0){
			return Result.emptyResult();
		}
		MedicationListLookup.filterMedListByDate(medicationList,2,locationTagId,
            locationId);
		
		if (medicationList != null&&index != null&&index<medicationList.toArray().length) {
			
			Medication currDrug = (Medication) medicationList.toArray()[index];
			
			//name, ndc name, rx norm name, then original text name
			String name = currDrug.getName();
			if (name == null||name.length()==0) {
				name = currDrug.getNdcName();
			}
			
			if (name == null||name.length()==0) {
				name = currDrug.getRxNormName();
			}
			
			if (name == null||name.length()==0) {
				name = currDrug.getOriginalText();
			}
			
			// ignore null values
			if (name != null) {
				
				return new Result(name);
			}
		}
		
		return Result.emptyResult();
	}
	
	/**
	 * @see org.openmrs.logic.rule.Rule#getParameterList()
	 */
	public Set<RuleParameterInfo> getParameterList() {
		return null;
	}
	
	/**
	 * @see org.openmrs.logic.rule.Rule#getDependencies()
	 */
	public String[] getDependencies() {
		return new String[] {};
	}
	
	/**
	 * @see org.openmrs.logic.rule.Rule#getTTL()
	 */
	public int getTTL() {
		return 0;
	}
	
	/**
	 * @see org.openmrs.logic.rule.Rule#getDatatype(String)
	 */
	public Datatype getDefaultDatatype() {
		return Datatype.NUMERIC;
	}
	
}
