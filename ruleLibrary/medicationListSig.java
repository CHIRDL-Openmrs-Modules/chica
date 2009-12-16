package org.openmrs.module.chica.rule;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.Patient;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.rgccd.MedicationListComparator;
import org.openmrs.module.chica.MedicationListLookup;
import org.openmrs.module.rgccd.Medication;

/**
 * Calculates a person's age in years based from their date of birth to the index date
 */
public class medicationListSig implements Rule {
	
	/**
	 * @see org.openmrs.logic.rule.Rule#eval(org.openmrs.Patient, org.openmrs.logic.LogicCriteria)
	 */
	public Result eval(LogicContext context, Patient patient, Map<String, Object> parameters) throws LogicException {
		Integer index = null;
		
		if (parameters != null) {
			index = Integer.parseInt((String) parameters.get("param0"));
		}
		
		Integer patientId = patient.getPatientId();
		List<Medication> medicationList = MedicationListLookup.getMedicationList(patientId);
		
		if(medicationList == null||medicationList.size()==0){
			return Result.emptyResult();
		}
		//sort by dispense date in descending order
		Collections.sort(medicationList,new MedicationListComparator());
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, -2);
		Date threshholdDate = calendar.getTime();
		
		//remove medications with dispense date older than 2 months
		System.out.println(medicationList.size()+ " size of medication list");
		Iterator<Medication> iter = medicationList.iterator();
		while(iter.hasNext()){
			Medication currMed = iter.next();
			System.out.println(currMed.getName()+" ("+currMed.getDispenseDate()+")");
			if(currMed.getDispenseDate().before(threshholdDate)){
				iter.remove();
			}
		}
		
		if (medicationList != null&&index != null&&index<medicationList.toArray().length) {
			
			Medication currDrug = (Medication) medicationList.toArray()[index];
			
			//rx norm name, then original text name, then ndc name
			String name = currDrug.getRxNormName();
			
			if (name == null||name.length()==0) {
				name = currDrug.getOriginalText();
			}
			
			if (name == null||name.length()==0) {
				name = currDrug.getNdcName();
			}
			
			// ignore null values
			if (name != null) {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

				return new Result(currDrug.getSig());
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
