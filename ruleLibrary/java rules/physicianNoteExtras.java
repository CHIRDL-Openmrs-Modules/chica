/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.chica.rule;

import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.datasource.LogicDataSource;
import org.openmrs.logic.impl.LogicContextImpl;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.logic.op.OperandConcept;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chica.util.Util;


/**
 *
 * @author Seema Sarala
 */
public class physicianNoteExtras implements Rule {
	
	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer, java.util.Map)
	 */
	public Result eval(LogicContext logicContext, Integer patientId, Map<String, Object> parameters) throws LogicException {
		
		String examNote = buildPhysicalExamExtrasNote(patientId,parameters);
		if (examNote.trim().length() > 0) {
			return new Result(examNote);
		}
		return Result.emptyResult();
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getDefaultDatatype()
	 */
	public Datatype getDefaultDatatype() {
		return Datatype.CODED;
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getDependencies()
	 */
	public String[] getDependencies() {
		return new String[]{};
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getParameterList()
	 */
	public Set<RuleParameterInfo> getParameterList() {
		return null;
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getTTL()
	 */
	public int getTTL() {
		return 0; // 60 * 30; // 30 minutes
	}
	
	/**
     * Builds the history and physical exam portion of the physician note.
     * 
     * @param patientId The ID of the patient used to lookup physical exam observations for the current day.
     * @return String containing the physical exam portion of the physician note.  This will not return null.
     */
    private static String buildPhysicalExamExtrasNote(Integer patientId, Map<String, Object> parameters) {
    	StringBuffer noteBuffer = new StringBuffer();
    	Patient patient = Context.getPatientService().getPatient(patientId);
    	LogicContext context = new LogicContextImpl(patientId);
    	LogicDataSource obsDataSource = context.getLogicDataSource("obs");
    	
    	Encounter encounter = Util.getLastEncounter(patient);
    	if (encounter == null) {
    		return noteBuffer.toString();
    	}
    	
    	Integer encounterId = encounter.getEncounterId();
    	
    	appendPhysicalExamsExtraNote(context, obsDataSource, patientId, noteBuffer, "SPECIAL NEEDS", "Special Need Child: ", encounterId, null);
    	appendPhysicalExamsExtraNote(context, obsDataSource, patientId, noteBuffer, "MATwoIDsChecked", "Two ID's Checked by MA/Nurse: ", encounterId, null);
    	appendPhysicalExamsExtraNote(context, obsDataSource, patientId, noteBuffer, "MDTwoIDsChecked", "Two ID's Checked: ", encounterId, null);
    	appendPhysicalExamsExtraNote(context, obsDataSource, patientId, noteBuffer, "Abuse_Concern", "Screened for abuse: ", encounterId, null);
    	appendPhysicalExamsExtraNote(context, obsDataSource, patientId, noteBuffer, "Counseling", "Discussed Physical Activity: ", encounterId, "Physical Activity");
    	appendPhysicalExamsExtraNote(context, obsDataSource, patientId, noteBuffer, "Counseling", "Discussed Healthy Diet: ", encounterId, "Healthy Diet");
    	appendPhysicalExamsExtraNote(context, obsDataSource, patientId, noteBuffer, "MedicationEducationPerformed", "Medication Education Performed and/or Counseled on Vaccines: ", encounterId, null);
    	
    	String note = noteBuffer.toString();
    	if (note.trim().length() > 0) {
    		return "PHYSICAL EXAMINATION\n" + note + "\n";
    	}
    	return note;
    }
	/**
	 * Appends Physical Exam portion to the Physician Note
	 * @param patientId The ID of the patient used to lookup physical exam observations for the current day.
	 * @param noteBuffer To append the physical exam header and value 
	 * @param concept Concept Name
	 * @param heading Physical exam header 
	 * @param codedConcept Concept name code
	 */
    private static void appendPhysicalExamsExtraNote(LogicContext context, LogicDataSource obsDataSource, Integer patientId, 
              StringBuffer noteBuffer, String concept, String heading, Integer encounterId, String codedConcept) {
    	ConceptService conceptService = Context.getConceptService();
    	Concept valueCodedConcept = null;
		Result result=null;
		LogicCriteria criteria = null;
		LogicCriteria encounterCriteria = null;
				
		if (codedConcept!=null){
			valueCodedConcept = conceptService.getConceptByName(codedConcept);
			criteria = new LogicCriteriaImpl(concept).equalTo(new OperandConcept(valueCodedConcept));
			encounterCriteria = new LogicCriteriaImpl("encounterId").equalTo(encounterId.intValue());
			criteria = criteria.and(encounterCriteria);
			result =context.read(patientId, obsDataSource, criteria.last()); 

		}else {
			criteria = new LogicCriteriaImpl(concept);
			encounterCriteria = new LogicCriteriaImpl("encounterId").equalTo(encounterId.intValue());
			criteria = criteria.and(encounterCriteria);
			result = context.read(patientId, obsDataSource, criteria.last());
		}
    	appendNote(result, noteBuffer, heading, encounterId);
    }
	 /**
	 * Appends Header and value to the physician note
	 * @param result LogicContext Result
	 * @param noteBuffer 
	 * @param heading Physical Exam Extras Heading
	 * @param encounterId The encounter identifier
	 */
    private static void appendNote(Result result, StringBuffer noteBuffer, String heading, Integer encounterId){
    	if (result != null && !result.isEmpty() && Util.equalEncounters(encounterId, result)) {
			String value = result.toString();
			if (value.equals("Physical Activity") || value.equals("Healthy Diet")){
				value = "yes";
			}
			noteBuffer.append(heading);
			noteBuffer.append(value);
			noteBuffer.append("\n");
		}
    }
}
