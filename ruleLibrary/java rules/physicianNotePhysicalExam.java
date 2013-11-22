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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.Duration;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.datasource.LogicDataSource;
import org.openmrs.logic.impl.LogicContextImpl;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.atd.hibernateBeans.PSFQuestionAnswer;
import org.openmrs.module.dss.ruleLibrary.getLastObs;
import org.openmrs.module.dss.service.DssService;


/**
 *
 * @author Steve McKee
 */
public class physicianNotePhysicalExam implements Rule {
	
	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer, java.util.Map)
	 */
	public Result eval(LogicContext logicContext, Integer patientId, Map<String, Object> parameters) throws LogicException {
		long startTime = System.currentTimeMillis();
		String examNote = buildPhysicalExamNote(patientId);
		if (examNote.trim().length() > 0) {
			System.out.println("chicaNotePhysicalExam: " + (System.currentTimeMillis() - startTime) + "ms");
			return new Result(examNote);
		}
		
		System.out.println("chicaNotePhysicalExam: " + (System.currentTimeMillis() - startTime) + "ms");
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
     * Builds the physical exam portion of the physician note.
     * 
     * @param patientId The ID of the patient used to lookup physical exam observations for the current day.
     * @return String containing the physical exam portion of the physician note.  This will not return null.
     */
    private static String buildPhysicalExamNote(Integer patientId) {
    	StringBuffer noteBuffer = new StringBuffer();
    	DssService dssService = Context.getService(DssService.class);
    	org.openmrs.module.dss.hibernateBeans.Rule rule = new org.openmrs.module.dss.hibernateBeans.Rule();
    	Patient patient = Context.getPatientService().getPatient(patientId);
    	LogicContext context = new LogicContextImpl(patientId);
    	LogicDataSource obsDataSource = context.getLogicDataSource("obs");
    	String conceptName = "VisitType";
    	Result result = context.read(patientId, obsDataSource, 
			new LogicCriteriaImpl(conceptName).within(Duration.days(-2)).last());
    	if (result != null && "SickVisit".equalsIgnoreCase(result.toString())) {
			noteBuffer.append("This is a sick visit\n");
    	}
    	
    	conceptName = "HEIGHT";
    	Result heightResult = context.read(patientId, obsDataSource, 
			new LogicCriteriaImpl(conceptName).within(Duration.days(-2)).last());
    	if (heightResult != null && !heightResult.isEmpty()) {
    		noteBuffer.append("Height: ");
    		Map<String,Object> map = new HashMap<String,Object>();
    		map.put("param0", heightResult);
    		map.put("concept", conceptName);
    		rule.setTokenName("roundOnePlace");
			rule.setParameters(map);
			Result roundedResult = dssService.runRule(patient, rule);
    		noteBuffer.append(roundedResult.toString());
    		noteBuffer.append(" in. (");
    		rule.setTokenName("percentile");
    		Result percentile = dssService.runRule(patient, rule);
    		noteBuffer.append(percentile.toString());
    		noteBuffer.append("%)\n");
    	}
    	
    	conceptName = "WEIGHT";
    	Result weightResult = context.read(patientId, obsDataSource, 
			new LogicCriteriaImpl(conceptName).within(Duration.days(-2)).last());
    	if (weightResult != null && !weightResult.isEmpty()) {
    		noteBuffer.append("Weight: ");
    		Map<String,Object> map = new HashMap<String,Object>();
    		map.put("param0", weightResult);
    		map.put("concept", conceptName);
    		rule.setTokenName("roundTwoPlace");
			rule.setParameters(map);
    		Result roundedResult = dssService.runRule(patient, rule);
    		noteBuffer.append(roundedResult.toString());
    		noteBuffer.append(" lbs. (");
    		rule.setTokenName("percentile");
    		Result percentile = dssService.runRule(patient, rule);
    		noteBuffer.append(percentile.toString());
    		noteBuffer.append("%)\n");
    	}
    	
    	if (heightResult != null && !heightResult.isEmpty() && weightResult != null && !weightResult.isEmpty()) {
	    	rule.setTokenName("bmi");
			rule.setParameters(new HashMap<String,Object>());
	    	result = dssService.runRule(patient, rule);
	    	if (result != null && result.toString() != null && result.toString().trim().length() > 0) {
	    		noteBuffer.append("BMI: ");
	    		Map<String,Object> map = new HashMap<String,Object>();
	    		map.put("param0", result);
	    		rule.setTokenName("roundOnePlace");
				rule.setParameters(map);
	    		Result roundedResult = dssService.runRule(patient, rule);
	    		noteBuffer.append(roundedResult.toString());
	    		noteBuffer.append(" (");
	    		rule.setTokenName("percentile");
	    		Result percentile = dssService.runRule(patient, rule);
	    		noteBuffer.append(percentile.toString());
	    		noteBuffer.append("%)\n");
	    	}
    	}
    	
    	conceptName = "HC";
    	result = context.read(patientId, obsDataSource, 
			new LogicCriteriaImpl(conceptName).within(Duration.days(-2)).last());
    	if (result != null && !result.isEmpty()) {
    		noteBuffer.append("Head Circumference: ");
    		Map<String,Object> map = new HashMap<String,Object>();
    		map.put("param0", result);
    		map.put("concept", conceptName);
    		rule.setTokenName("roundOnePlace");
			rule.setParameters(map);
    		Result roundedResult = dssService.runRule(patient, rule);
    		noteBuffer.append(roundedResult.toString());
    		noteBuffer.append(" cm. (");
    		rule.setTokenName("percentile");
    		Result percentile = dssService.runRule(patient, rule);
    		noteBuffer.append(percentile.toString());
    		noteBuffer.append("%)\n");
    	}
    	
    	result = context.read(patientId, obsDataSource, 
			new LogicCriteriaImpl("bp").within(Duration.days(-2)).last());
    	if (result != null && !result.isEmpty()) {
    		noteBuffer.append("Blood Pressure: ");
    		noteBuffer.append(result.toString());
    		Encounter encounter = getLastEncounter(patient);
    		if (encounter != null) {
    			org.openmrs.module.dss.hibernateBeans.Rule bpPercentRule = new org.openmrs.module.dss.hibernateBeans.Rule();
        		Map<String,Object> map = new HashMap<String,Object>();
	    		map.put("encounterId", encounter.getEncounterId());
	    		bpPercentRule.setTokenName("bpPercentage");
	    		bpPercentRule.setParameters(map);
	    		Result bpPercentResult = dssService.runRule(patient, bpPercentRule);
	    		if (result != null && !result.isEmpty()) {
	    			noteBuffer.append(" (");
	    			noteBuffer.append(bpPercentResult.toString());
	    			noteBuffer.append(")");
	    		}
    		}
    		noteBuffer.append("\n");
    	}
    	
    	result = context.read(patientId, obsDataSource, 
			new LogicCriteriaImpl("TEMPERATURE CHICA").within(Duration.days(-2)).last());
    	if (result != null && !result.isEmpty()) {
    		noteBuffer.append("Temperature: ");
    		Map<String,Object> map = new HashMap<String,Object>();
    		map.put("param0", result);
    		rule.setTokenName("roundOnePlace");
			rule.setParameters(map);
    		Result roundedResult = dssService.runRule(patient, rule);
    		noteBuffer.append(roundedResult.toString());
    		noteBuffer.append(" deg. F\n");
    	}
    	
    	result = context.read(patientId, obsDataSource, 
			new LogicCriteriaImpl("PULSE CHICA").within(Duration.days(-2)).last());
    	if (result != null && !result.isEmpty()) {
    		noteBuffer.append("Pulse: ");
    		Map<String,Object> map = new HashMap<String,Object>();
    		map.put("param0", result);
    		rule.setTokenName("integerResult");
			rule.setParameters(map);
    		Result roundedResult = dssService.runRule(patient, rule);
    		noteBuffer.append(roundedResult.toString());
    		noteBuffer.append("/min\n");
    	}
    	
    	result = context.read(patientId, obsDataSource, 
			new LogicCriteriaImpl("PULSEOX").within(Duration.days(-2)).last());
    	if (result != null && !result.isEmpty()) {
    		noteBuffer.append("Pulse Ox: ");
    		Map<String,Object> map = new HashMap<String,Object>();
    		map.put("param0", result);
    		rule.setTokenName("integerResult");
			rule.setParameters(map);
    		Result roundedResult = dssService.runRule(patient, rule);
    		noteBuffer.append(roundedResult.toString());
    		noteBuffer.append("%\n");
    	}
    	
    	result = context.read(patientId, obsDataSource, 
			new LogicCriteriaImpl("VISIONL").within(Duration.days(-2)).last());
    	if (result != null && !result.isEmpty()) {
    		noteBuffer.append("Vision Left: 20/");
    		Map<String,Object> map = new HashMap<String,Object>();
    		map.put("param0", result);
    		rule.setTokenName("integerResult");
			rule.setParameters(map);
    		Result roundedResult = dssService.runRule(patient, rule);
    		noteBuffer.append(roundedResult.toString());
    		noteBuffer.append("\n");
    	}
    	
    	result = context.read(patientId, obsDataSource, 
			new LogicCriteriaImpl("VISIONR").within(Duration.days(-2)).last());
    	if (result != null && !result.isEmpty()) {
    		noteBuffer.append("Vision Right: 20/");
    		Map<String,Object> map = new HashMap<String,Object>();
    		map.put("param0", result);
    		rule.setTokenName("integerResult");
			rule.setParameters(map);
    		Result roundedResult = dssService.runRule(patient, rule);
    		noteBuffer.append(roundedResult.toString());
    		noteBuffer.append("\n");
    	}
    	
    	result = context.read(patientId, obsDataSource, 
			new LogicCriteriaImpl("RR CHICA").within(Duration.days(-2)).last());
    	if (result != null && !result.isEmpty()) {
    		noteBuffer.append("RR: ");
    		Map<String,Object> map = new HashMap<String,Object>();
    		map.put("param0", result);
    		rule.setTokenName("integerResult");
			rule.setParameters(map);
    		Result roundedResult = dssService.runRule(patient, rule);
    		noteBuffer.append(roundedResult.toString());
    		noteBuffer.append("\n");
    	}
    	
    	result = context.read(patientId, obsDataSource, 
			new LogicCriteriaImpl("NoVision").within(Duration.days(-2)).last());
    	if (result != null && !result.isEmpty()) {
    		noteBuffer.append("Uncooperative/Unable to Screen Vision");
    		noteBuffer.append("\n");
    	}
    	
    	result = context.read(patientId, obsDataSource, 
			new LogicCriteriaImpl("NoHearing").within(Duration.days(-2)).last());
    	if (result != null && !result.isEmpty()) {
    		noteBuffer.append("Uncooperative/Unable to Screen Hearing");
    		noteBuffer.append("\n");
    	}
    	
    	result = context.read(patientId, obsDataSource, 
			new LogicCriteriaImpl("NoBP").within(Duration.days(-2)).last());
    	if (result != null && !result.isEmpty()) {
    		noteBuffer.append("Uncooperative/Unable to Screen Blood Pressure");
    		noteBuffer.append("\n");
    	}
    	
    	// Physical Exam portion
    	appendPhysicalExam(context, obsDataSource, patientId, noteBuffer, "General_Exam", "General: ");
    	appendPhysicalExam(context, obsDataSource, patientId, noteBuffer, "Head_Exam", "Head: ");
    	appendPhysicalExam(context, obsDataSource, patientId, noteBuffer, "Skin_Exam", "Skin: ");
    	appendPhysicalExam(context, obsDataSource, patientId, noteBuffer, "Eyes/Vision_Exam", "Eyes: ");
    	appendPhysicalExam(context, obsDataSource, patientId, noteBuffer, "Ears/Hearing_Exam", "Ears: ");
    	appendPhysicalExam(context, obsDataSource, patientId, noteBuffer, "Nose/Throat_Exam", "Nose/Throat: ");
    	appendPhysicalExam(context, obsDataSource, patientId, noteBuffer, "Teeth/Gums_Exam", "Teeth/Gums: ");
    	appendPhysicalExam(context, obsDataSource, patientId, noteBuffer, "Nodes_Exam", "Nodes: ");
    	appendPhysicalExam(context, obsDataSource, patientId, noteBuffer, "Chest/Lungs_Exam", "Chest/Lungs: ");
    	appendPhysicalExam(context, obsDataSource, patientId, noteBuffer, "Heart/Pulses_Exam", "Heart/Pulses: ");
    	appendPhysicalExam(context, obsDataSource, patientId, noteBuffer, "Abdomen_Exam", "Abdomen: ");
    	appendPhysicalExam(context, obsDataSource, patientId, noteBuffer, "ExtGenitalia_Exam", "Ext. Genitalia: ");
    	appendPhysicalExam(context, obsDataSource, patientId, noteBuffer, "Back_Exam", "Back: ");
    	appendPhysicalExam(context, obsDataSource, patientId, noteBuffer, "Neuro_Exam", "Neuro: ");
    	appendPhysicalExam(context, obsDataSource, patientId, noteBuffer, "Extremities_Exam", "Extremities: ");
    	
    	String note = noteBuffer.toString();
    	if (note.trim().length() > 0) {
    		return "PHYSICAL EXAMINATION\n" + note + "\n";
    	}
    	return note;
    }
	
    private static void appendPhysicalExam(LogicContext context, LogicDataSource obsDataSource, Integer patientId, 
                                    StringBuffer noteBuffer, String concept, String heading) {
    	Result result = context.read(patientId, obsDataSource, 
			new LogicCriteriaImpl(concept).within(Duration.days(-2)).last());
    	if (result != null && !result.isEmpty()) {
    		noteBuffer.append(heading);
    		noteBuffer.append(result.toString());
    		noteBuffer.append("\n");
    	}
    }
    
    private static Encounter getLastEncounter(Patient patient) {
    	// Get last encounter with last day
		Calendar startCal = Calendar.getInstance();
		startCal.set(GregorianCalendar.DAY_OF_MONTH, startCal.get(GregorianCalendar.DAY_OF_MONTH) - 2);
		Date startDate = startCal.getTime();
		Date endDate = Calendar.getInstance().getTime();
		List<Encounter> encounters = Context.getEncounterService().getEncounters(patient, null, startDate, endDate, null, 
			null, null, false);
		if (encounters == null || encounters.size() == 0) {
			return null;
		}
		
		return encounters.get(encounters.size() - 1);
    }
}
