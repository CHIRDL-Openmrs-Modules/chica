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
package org.openmrs.module.chica;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.Encounter;
import org.openmrs.FieldType;
import org.openmrs.Form;
import org.openmrs.FormField;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.result.Result;
import org.openmrs.module.atd.FormWriter;
import org.openmrs.module.atd.ParameterHandler;
import org.openmrs.module.atd.datasource.FormDatasource;
import org.openmrs.module.atd.hibernateBeans.PatientATD;
import org.openmrs.module.atd.hibernateBeans.Statistics;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.atd.xmlBeans.Field;
import org.openmrs.module.atd.xmlBeans.Records;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable;
import org.openmrs.module.chirdlutil.threadmgmt.ThreadManager;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutil.util.XMLUtil;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.module.dss.DssElement;
import org.openmrs.module.dss.DssManager;
import org.openmrs.module.dss.hibernateBeans.Rule;
import org.openmrs.module.dss.hibernateBeans.RuleEntry;
import org.openmrs.module.dss.service.DssService;

/**
 * Class used to access the specific portions of form on the fly.
 * 
 * @author Steve McKee
 */
public class DynamicFormAccess {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * Default Constructor
	 */
	public DynamicFormAccess() {
	}
	
	/**
	 * Returns the results of running rules for the form fields marked as "Prioritized Merge Field". 
	 * 
	 * @param formId The identifier of the form.
	 * @param formInstanceId The form instance Identifier of the form.
	 * @param encounterId The encounter identifier associated with the form.
	 * @param maxElements The maximum number of elements to populate.
	 * @return List of Field objects containing the field information as well as the result information from the rules.
	 */
	public List<Field> getPrioritizedElements(Integer formId, Integer formInstanceId, Integer encounterId,
	                                          Integer maxElements) {
		List<Field> fields = new ArrayList<Field>(maxElements);
		EncounterService encounterService = Context.getEncounterService();
		Encounter encounter = encounterService.getEncounter(encounterId);
		Integer locationId = encounter.getLocation().getLocationId();
		
		LocationService locationService = Context.getLocationService();
		Location location = locationService.getLocation(locationId);
		
		FormService formService = Context.getFormService();
		Form form = formService.getForm(formId);
		String formName = form.getName();
		
		FormInstance formInstance = new FormInstance(locationId, formId, formInstanceId);
		PatientState patientState = org.openmrs.module.atd.util.Util
		        .getProducePatientStateByFormInstanceAction(formInstance);
		Integer locationTagId = patientState.getLocationTagId();
		
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("sessionId", patientState.getSessionId());
		parameters.put("formInstance", new FormInstance(locationId, formId, formInstanceId));
		parameters.put("locationTagId", locationTagId);
		parameters.put("locationId", locationId);
		parameters.put("location", location.getName());
		parameters.put("encounterId", encounterId);
		parameters.put("mode", "PRODUCE");
		
		Patient patient = patientState.getPatient();
		DssManager dssManager = new DssManager(patient);
		
		FieldType mergeType = getFieldType("Merge Field");
		FieldType priorMergeType = getFieldType("Prioritized Merge Field");
		LinkedHashMap<FormField, Object> fieldToResult = new LinkedHashMap<FormField, Object>();
		Map<String, Integer> dssMergeCounters = new HashMap<String, Integer>();
		List<FieldType> fieldTypes = new ArrayList<FieldType>();
		fieldTypes.add(mergeType);
		fieldTypes.add(priorMergeType);
		List<FormField> formFields = Context.getService(ChirdlUtilBackportsService.class).getFormFields(form, fieldTypes, 
			true);
		List<Integer> fieldIds = new ArrayList<Integer>();
		for (FormField formField : formFields) {
			fieldIds.add(formField.getField().getFieldId());
		}
		
		Map<Integer, PatientATD> fieldIdToPatientATDMap = org.openmrs.module.atd.util.Util.getPatientATDs(
			formInstance, fieldIds);
		List<DssElement> elementList = new ArrayList<DssElement>();
		ATDService atdService = Context.getService(ATDService.class);
		Integer startPriority = getStartPriority(atdService, formInstanceId, locationId, formName);
		Map<Integer, PatientATD> waitForScanFieldIdToAtdMap = new HashMap<Integer, PatientATD>();
		
		String numPrompts = org.openmrs.module.chirdlutilbackports.util.Util.getFormAttributeValue(formId,
		    "numPrompts", locationTagId, locationId);
		if (numPrompts == null) {
			log.error("Missing form attribute 'numPrompts' for form: " + formId);
			return fields;
		}
		
		int populatedElements = 0;
		int maxDssElements = 0;
		try {
			maxDssElements = Integer.parseInt(numPrompts);
		}
		catch (NumberFormatException e) {}
		
		for (FormField currFormField : formFields) {
			org.openmrs.Field currField = currFormField.getField();
			String defaultValue = currField.getDefaultValue();
			if (defaultValue == null) {
				continue;
			}
			
			FieldType currFieldType = currField.getFieldType();
			HashMap<String, Object> ruleParameters = new HashMap<String, Object>(parameters);
			
			//fix lazy initialization error
			//currField = formService.getField(currField.getFieldId());
			
			if (currFieldType.equals(priorMergeType)) {
				// check to see if the current field has already been populated
				PatientATD patientATD = fieldIdToPatientATDMap.get(currField.getFieldId());
				if (patientATD != null) {
					if (++populatedElements == maxDssElements) {
						break;
					}
					
					// check to see if it's been answered yet
					List<Statistics> stats = atdService.getStatByIdAndRule(formInstanceId, patientATD.getRule().getRuleId(),
					    formName, locationId);
					if (stats != null && stats.size() > 0) {
						Statistics stat = stats.get(0);
						if (stat.getScannedTimestamp() != null) {
							// add null as a place keeper so we can keep the position for the atd statistics.
							elementList.add(null);
							continue;
						}
						
						waitForScanFieldIdToAtdMap.put(currField.getFieldId(), patientATD);
					}
				}
				
				//---------start set concept rule parameter
				Concept concept = currField.getConcept();
				if (concept != null) {
					try {
						String elementString = ((ConceptName) concept.getNames().toArray()[0]).getName();
						ruleParameters.put("concept", elementString);
					}
					catch (Exception e) {
						ruleParameters.put("concept", null);
					}
				} else {
					ruleParameters.put("concept", null);
				}
				//---------end set concept rule parameter
				
				//process prioritized merge type fields
				String ruleType = defaultValue;
				Integer dssMergeCounter = dssMergeCounters.get(ruleType);
				if (dssMergeCounter == null) {
					dssMergeCounter = 0;
				}
				
				//fill in the dss elements for this type
				//(this will only get executed once even though it is called for each field)
				//We need to set the max number of prioritized elements to generate
				if (dssManager.getMaxDssElementsByType(ruleType) == 0) {
					dssManager.setMaxDssElementsByType(ruleType, maxDssElements);
				}
				
				dssManager.getPrioritizedDssElements(ruleType, startPriority, maxElements, ruleParameters);
				//get the result for this field
				Result result = processDssElements(dssManager, dssMergeCounter, currField.getFieldId(), ruleType);
				//				if (result != null) {
				
				elementList.add(dssManager.getDssElement(dssMergeCounter, ruleType));
				//				}
				
				dssMergeCounter++;
				dssMergeCounters.put(ruleType, dssMergeCounter);
				
				fieldToResult.put(currFormField, result);
			} else if (currFieldType.equals(mergeType)) {
				//store the leaf index as the result if the
				//current field has a parent
				if (currFormField.getParent() != null) {
					fieldToResult.put(currFormField, defaultValue);
				}
			}
		}
		
		//process Results
		LinkedHashMap<String, String> fieldNameResult = new LinkedHashMap<String, String>();
		for (FormField currFormField : fieldToResult.keySet()) {
			String resultString = null;
			
			//fix lazy initialization error
			//org.openmrs.Field currField = formService.getField(currFormField.getField().getFieldId());
			org.openmrs.Field currField = currFormField.getField();
			String fieldName = currField.getName();
			Object fieldResult = fieldToResult.get(currFormField);
			
			if (fieldResult == null) {
				continue;
			}
			
			if (fieldResult instanceof Result) {
				resultString = ((Result) fieldResult).get(0).toString();
			} else {
				//if the field has a parent, process the result as a leaf index
				//of the parent results
				if (currFormField.getParent() == null) {
					resultString = (String) fieldResult;
				} else {
					try {
						int leafPos = Integer.parseInt((String) fieldResult);
						FormField parentField = currFormField.getParent();
						Result parentResult = (Result) fieldToResult.get(parentField);
						
						if (parentResult != null) {
							resultString = parentResult.get(leafPos).toString();
						} else {
							resultString = null;
						}
					}
					catch (NumberFormatException e) {}
				}
			}
			
			if (resultString != null) {
				//remove everything at or after the @ symbol (i.e. @Spanish)
				int atIndex = resultString.indexOf("@");
				if (atIndex >= 0) {
					resultString = resultString.substring(0, atIndex);
				}
			}
			
			fieldNameResult.put(fieldName, resultString);
		}
		
		for (String fieldName : fieldNameResult.keySet()) {
			String resultString = fieldNameResult.get(fieldName);
			if (resultString == null) {
				continue;
			}
			
			Field field = new Field();
			field.setId(fieldName);
			field.setValue(resultString);
			fields.add(field);
		}
		
		saveDssElementsToDatabase(patient, formInstance, elementList, encounter, locationTagId, formName,
		    waitForScanFieldIdToAtdMap);
		serializeFields(formInstance, locationTagId, fields, new ArrayList<String>()); // DWE CHICA-430 Add new ArrayList<String>()
		
		return fields;
	}
	
	/**
	 * Populate all the form fields marked as "Merge Field".
	 * 
	 * @param formId The form identifier.
	 * @param formInstanceId The form instance identifier.
	 * @param encounterId The encounter identifier associated with the form.
	 * @return List of Field object containing field information as well the results of populating the fields.
	 */
	public List<Field> getMergeElements(Integer formId, Integer formInstanceId, Integer encounterId) {
		List<Field> fields = new ArrayList<Field>();
		EncounterService encounterService = Context.getEncounterService();
		Encounter encounter = encounterService.getEncounter(encounterId);
		Integer locationId = encounter.getLocation().getLocationId();
		
		LocationService locationService = Context.getLocationService();
		Location location = locationService.getLocation(locationId);
		
		FormService formService = Context.getFormService();
		Form form = formService.getForm(formId);
		
		FormInstance formInstance = new FormInstance(locationId, formId, formInstanceId);
		PatientState patientState = org.openmrs.module.atd.util.Util
		        .getProducePatientStateByFormInstanceAction(formInstance);
		Integer locationTagId = patientState.getLocationTagId();
		
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("sessionId", patientState.getSessionId());
		parameters.put("formInstance", new FormInstance(locationId, formId, formInstanceId));
		parameters.put("locationTagId", locationTagId);
		parameters.put("locationId", locationId);
		parameters.put("location", location.getName());
		parameters.put("encounterId", encounterId);
		parameters.put("mode", "PRODUCE");
		
		Patient patient = patientState.getPatient();
		ATDService atdService = Context.getService(ATDService.class);
		FieldType mergeType = getFieldType("Merge Field");
		List<FieldType> fieldTypes = new ArrayList<FieldType>();
		fieldTypes.add(mergeType);
		LinkedHashMap<FormField, Object> fieldToResult = new LinkedHashMap<FormField, Object>();
		List<FormField> formFields = Context.getService(ChirdlUtilBackportsService.class).getFormFields(form, fieldTypes, false);
		for (FormField currFormField : formFields) {
			org.openmrs.Field currField = currFormField.getField();
			
			//fix lazy initialization error
			//currField = formService.getField(currField.getFieldId());
			
			String defaultValue = currField.getDefaultValue();
			if (defaultValue == null) {
				continue;
			}
			
			parameters.put("formFieldId", currFormField.getFormFieldId()); // DWE CHICA-437
			
			//store the leaf index as the result if the
			//current field has a parent
			if (currFormField.getParent() == null) {
				Result result = atdService.evaluateRule(defaultValue, patient, parameters);
				fieldToResult.put(currFormField, result);
			}
		}
		
		//process Results
		LinkedHashMap<String, String> fieldNameResult = new LinkedHashMap<String, String>();
		for (FormField currFormField : fieldToResult.keySet()) {
			String resultString = null;
			
			//fix lazy initialization error
			//org.openmrs.Field currField = formService.getField(currFormField.getField().getFieldId());
			org.openmrs.Field currField = currFormField.getField();
			String fieldName = currField.getName();
			Object fieldResult = fieldToResult.get(currFormField);
			
			if (fieldResult == null) {
				continue;
			}
			
			if (fieldResult instanceof Result) {
				resultString = ((Result) fieldResult).get(0).toString();
			} else {
				resultString = (String) fieldResult;
			}
			
			if (resultString != null) {
				//remove everything at or after the @ symbol (i.e. @Spanish)
				int atIndex = resultString.indexOf("@");
				if (atIndex >= 0) {
					resultString = resultString.substring(0, atIndex);
				}
			}
			
			fieldNameResult.put(fieldName, resultString);
		}
		
		for (String fieldName : fieldNameResult.keySet()) {
			String resultString = fieldNameResult.get(fieldName);
			if (resultString == null) {
				continue;
			}
			
			Field field = new Field();
			field.setId(fieldName);
			field.setValue(resultString);
			fields.add(field);
		}
		
		//Process rules with null priority that have @ value in write action
		//These rules directly write results to a specific field
		DssService dssService = Context.getService(DssService.class);
		List<Rule> nonPriorRules = dssService.getNonPrioritizedRules(form.getName());
		
		for (Rule currRule : nonPriorRules) {
			if (currRule.checkAgeRestrictions(patient)) {
				currRule.setParameters(parameters);
				Result result = dssService.runRule(patient, currRule);
				for (Result currResult : result) {
					String resultString = currResult.toString();
					mapResult(resultString, fields);
				}
			}
		}
		
		serializeFields(formInstance, locationTagId, fields, new ArrayList<String>()); // DWE CHICA-430 Add new ArrayList<String>()
		return fields;
	}
	
	/**
	 * Save the results of the fields marked as "Export Field".
	 * 
	 * @param formInstance FormInstance object containing the relevant form information.
	 * @param locationTagId The location tag identifier.
	 * @param encounterId The encounter identifier associated with the form.
	 * @param patient The patient the form belongs to.
	 * @param formFieldMap Map from the HTTP request that contains the field name to values.
	 * @param parameterHandler The parameterHandler used for rule execution.
	 */
	public void saveExportElements(FormInstance formInstance, Integer locationTagId, Integer encounterId,
	                                    Patient patient, Map<String, String[]> formFieldMap,
	                                    ParameterHandler parameterHandler) {
		HashMap<String, Field> fieldMap = new HashMap<String, Field>();
		FormService formService = Context.getFormService();
		Form form = formService.getForm(formInstance.getFormId());
		LinkedHashMap<FormField, String> formFieldToValue = new LinkedHashMap<FormField, String>();
		FieldType exportType = getFieldType("Export Field");
		List<FieldType> fieldTypes = new ArrayList<FieldType>();
		fieldTypes.add(exportType);
		List<FormField> formFields = Context.getService(ChirdlUtilBackportsService.class).getFormFields(form, fieldTypes, 
			false);
		List<Integer> fieldIds = new ArrayList<Integer>();
		for (FormField formField : formFields) {
			fieldIds.add(formField.getField().getFieldId());
		}
		
		Iterator<FormField> formFieldIterator = formFields.iterator();
		while (formFieldIterator.hasNext()) {
			FormField formField = formFieldIterator.next();
			org.openmrs.Field field = formField.getField();
			String fieldName = field.getName();
			if (!formFieldMap.containsKey(fieldName)) {
				continue;
			}
			
			Field valueField = new Field(fieldName);
			fieldMap.put(fieldName, valueField);
			String[] valueObj = formFieldMap.get(fieldName);
			if (valueObj == null || valueObj.length == 0) {
				formFieldToValue.put(formField, null);
				continue;
			}
			
			String value = valueObj[0];
			formFieldToValue.put(formField, value);
			valueField.setValue(value);
		}
		
		consume(formInstance, patient, locationTagId, encounterId, fieldMap, formFieldToValue, 
			parameterHandler, form);
		Context.getService(ChicaService.class).saveAnswers(fieldMap, formInstance, encounterId, patient, 
			formFieldToValue.keySet());
		
		fieldMap.clear();
		formFieldToValue.clear();
	}
	
	/**
	 * Consume the information populated on a form.
	 * 
	 * @param formInstance FormInstance object containing the relevant form information.
	 * @param patient The patient the form belongs to.
	 * @param locationTagId The location tag identifier.
	 * @param encounterId The associated encounter identifier.
	 * @param fieldMap Map of field name to Field object.
	 * @param formFieldToValue Map of FormField to field value.
	 * @param parameterHandler The parameter handler used for rule execution.
	 * @param form The form containing the data to consume.
	 */
	private void consume(FormInstance formInstance, Patient patient, Integer locationTagId, Integer encounterId,
	                     HashMap<String, Field> fieldMap, LinkedHashMap<FormField, String> formFieldToValue, 
	                     ParameterHandler parameterHandler, Form form) {
		ATDService atdService = Context.getService(ATDService.class);
		Integer locationId = formInstance.getLocationId();
		PatientState patientState = org.openmrs.module.atd.util.Util
		        .getProducePatientStateByFormInstanceAction(formInstance);
		Integer sessionId = patientState.getSessionId();
		FieldType prioritizedMergeType = getFieldType("Prioritized Merge Field");
		
		String mode = "CONSUME";
		LinkedHashMap<String, LinkedHashMap<String, Rule>> rulesToRunByField = new LinkedHashMap<String, LinkedHashMap<String, Rule>>();
		LogicService logicService = Context.getLogicService();
		FormDatasource formDatasource = (FormDatasource) logicService.getLogicDataSource("form");
		try {
			formInstance = formDatasource.setFormFields(fieldMap, formInstance, locationTagId);
		}
		catch (Exception e) {
			this.log.error("Error setting form fields to be consumed");
			this.log.error(e.getMessage());
			this.log.error(Util.getStackTrace(e));
			return;
		}
		
		if (formInstance == null) {
			log.error("Form instance came back null");
			return;
		}
		
		Encounter encounter = Context.getEncounterService().getEncounter(encounterId);
		locationId = encounter.getLocation().getLocationId();
		Location location = Context.getLocationService().getLocation(locationId);
		String locationName = null;
		if (location != null) {
			locationName = location.getName();
		}
		
		List<Field> fieldsToAdd = new ArrayList<Field>();
		Map<Integer, PatientATD> fieldIdToPatientAtdMap = new HashMap<Integer, PatientATD>();
		for (FormField currField : formFieldToValue.keySet()) {
			org.openmrs.Field field = currField.getField();
			String fieldName = field.getName();
			Concept currConcept = field.getConcept();
			String ruleName = field.getDefaultValue();
			LinkedHashMap<String, Rule> rulesToRun = null;
			Map<String, Object> parameters = new HashMap<String, Object>();
			
			FormField parentField = currField.getParent();
			
			//if parent field is not null look at parent
			//field for rule to execute
			Rule rule = null;
			if (parentField != null) {
				FieldType currFieldType = field.getFieldType();
				
				if (currFieldType.equals(prioritizedMergeType)) {
					ruleName = null;//no rule to execute unless patientATD finds one	
				}
				
				Integer fieldId = parentField.getField().getFieldId();
				PatientATD patientATD = fieldIdToPatientAtdMap.get(fieldId);
				if (patientATD == null) {
					patientATD = atdService.getPatientATD(formInstance, fieldId);
				}
				
				if (patientATD != null) {
					rule = patientATD.getRule();
					ruleName = rule.getTokenName();
					fieldIdToPatientAtdMap.put(fieldId, patientATD);
				}
			}
			
			String lookupFieldName = null;
			Integer formFieldId = null; // DWE CHICA-437 Get the form field id here so that it can be used to determine if obs records should be voided when rules are evaluated
			if (parentField != null) {
				lookupFieldName = parentField.getField().getName();
				formFieldId = parentField.getFormFieldId();
			} else {
				lookupFieldName = fieldName;
				formFieldId = currField.getFormFieldId();
			}
			
			if (ruleName != null) {
				rulesToRun = rulesToRunByField.get(lookupFieldName);
				if (rulesToRun == null) {
					rulesToRun = new LinkedHashMap<String, Rule>();
					rulesToRunByField.put(lookupFieldName, rulesToRun);
				}
				
				Rule ruleLookup = rulesToRun.get(ruleName);
				if (ruleLookup == null) {
					if (rule != null) {
						ruleLookup = rule;
					} else {
						ruleLookup = new Rule();
						ruleLookup.setTokenName(ruleName);
					}
					ruleLookup.setParameters(parameters);
					rulesToRun.put(ruleName, ruleLookup);
				} else {
					parameters = ruleLookup.getParameters();
				}
			}
			
			//------------start set rule parameters
			parameters.put("sessionId", sessionId);
			parameters.put("formInstance", formInstance);
			parameters.put("locationTagId", locationTagId);
			parameters.put("locationId", locationId);
			parameters.put("location", locationName);
			parameters.put("mode", mode);
			parameters.put("encounterId", encounterId);
			if (rule != null) {
				parameters.put("ruleId", rule.getRuleId());
			}
			
			if (currConcept != null) {
				try {
					String elementString = ((ConceptName) currConcept.getNames().toArray()[0]).getName();
					parameters.put("concept", elementString);
				}
				catch (Exception e) {
					parameters.put("concept", null);
				}
			} else {
				parameters.put("concept", null);
			}
			
			if (fieldName != null) {
				parameters.put("fieldName", lookupFieldName);
				String value = formFieldToValue.get(currField);
				parameters.put(lookupFieldName, value);
				Field saveField = new Field();
				saveField.setId(fieldName);
				saveField.setValue(value);
				fieldsToAdd.add(saveField);
			}
			
			// DWE CHICA-437 
			if(formFieldId != null)
			{
				parameters.put("formFieldId", formFieldId);
			}
						
			//----------end set rule parameters
		}
		
		HashMap<String, Integer> childIndex = new HashMap<String, Integer>();
		
		for (FormField currField : formFieldToValue.keySet()) {
			LinkedHashMap<String, Rule> rulesToRun = null;
			Map<String, Object> parameters = new HashMap<String, Object>();
			FormField parentField = currField.getParent();
			
			//look for parentField
			if (parentField != null) {
				FieldType parentFieldType = parentField.getField().getFieldType();
				
				String parentRuleName = parentField.getField().getDefaultValue();
				String parentFieldName = parentField.getField().getName();
				
				if (parentFieldType.equals(prioritizedMergeType)) {
					parentRuleName = null;//no rule to execute unless patientATD finds one	
				}
				
				Integer fieldId = parentField.getField().getFieldId();
				PatientATD patientATD = fieldIdToPatientAtdMap.get(fieldId);
				if (patientATD == null) {
					patientATD = atdService.getPatientATD(formInstance, fieldId);
				}
				
				if (patientATD != null) {
					Rule rule = patientATD.getRule();
					parentRuleName = rule.getTokenName();
					fieldIdToPatientAtdMap.put(fieldId, patientATD);
				}
				//if there is a parent rule, add a parameter for the child's fieldname
				//add the parent rule if it is not in rules to run
				if (parentRuleName != null) {
					rulesToRun = rulesToRunByField.get(parentFieldName);
					if (rulesToRun == null) {
						rulesToRun = new LinkedHashMap<String, Rule>();
						rulesToRunByField.put(parentFieldName, rulesToRun);
					}
					
					Rule ruleLookup = rulesToRun.get(parentRuleName);
					
					if (ruleLookup == null) {
						ruleLookup = new Rule();
						ruleLookup.setParameters(parameters);
						ruleLookup.setTokenName(parentRuleName);
						rulesToRun.put(parentRuleName, ruleLookup);
					} else {
						parameters = ruleLookup.getParameters();
					}
					
					String childFieldName = currField.getField().getName();
					Integer index = childIndex.get(parentFieldName);
					if (index == null) {
						index = 0;
					}
					parameters.put("child" + index, childFieldName);
					parameters.put(childFieldName, formFieldToValue.get(currField));
					childIndex.put(parentFieldName, ++index);
				}
			}
		}
		
		//run all the consume rules
		Integer formInstanceId = formInstance.getFormInstanceId();
		String formName = form.getName();
		for (LinkedHashMap<String, Rule> rulesToRun : rulesToRunByField.values()) {
			for (String currRuleName : rulesToRun.keySet()) {
				Rule rule = rulesToRun.get(currRuleName);
				Map<String, Object> parameters = rule.getParameters();
				parameterHandler.addParameters(parameters, fieldMap);
				atdService.evaluateRule(currRuleName, patient, parameters);
				setScannedTimestamps(formInstanceId, rule.getRuleId(), formName, locationId);
			}
		}
		
		// DWE CHICA-430 Now that rules have run and obs records have been added/updated/voided
		// create the list of fields to remove from the xml
		List<String> elementsToRemoveList = createElementsToRemoveList(form, formInstanceId, encounter, locationTagId, locationId);
		
		fieldIdToPatientAtdMap.clear();
		serializeFields(formInstance, locationTagId, fieldsToAdd, elementsToRemoveList); // DWE CHICA-430 Add elementsToRemoveList
	}
	
	/**
	 * Looks up an openmrs FieldType by name
	 * 
	 * @param fieldTypeName name of the field type
	 * @return FieldType openmrs field type with the given name
	 */
	public FieldType getFieldType(String fieldTypeName) {
		FormService formService = Context.getFormService();
		List<FieldType> fieldTypes = formService.getAllFieldTypes();
		Iterator<FieldType> iter = fieldTypes.iterator();
		FieldType currFieldType = null;
		
		while (iter.hasNext()) {
			currFieldType = iter.next();
			if (currFieldType.getName().equals(fieldTypeName)) {
				return currFieldType;
			}
		}
		return null;
	}
	
	/**
	 * Return the Result object based Rule that was ran for a field.
	 * 
	 * @param dssManager DssManager handling the context information.
	 * @param dssMergeCounter The position of the DssElement in the list.
	 * @param fieldId The identifier of the field.
	 * @param type The rule type.
	 * @return Result object or null if one cannot be found.
	 */
	private Result processDssElements(DssManager dssManager, int dssMergeCounter, Integer fieldId, String type) {
		DssElement dssElement = dssManager.getDssElement(dssMergeCounter, type);
		
		if (dssElement != null) {
			dssElement.addParameter("fieldId", fieldId);
			return dssElement.getResult();
		}
		
		return null;
	}
	
	/**
	 * Saves the necessary information to the database for the rule execution.
	 * 
	 * @param patient The patient the rules were executed against.
	 * @param formInstance The FormInstance object containing the relevant form information.
	 * @param elementList List of DssElement object containing the Result information for the form fields.
	 * @param encounter The encounter associated with the form.
	 * @param locationTagId The location tag identifier.
	 * @param formName The name of the form.
	 * @param waitForScanFieldIdToAtdMap Map of field identifier to PatientATD object for fields that are awaiting scans.
	 */
	private void saveDssElementsToDatabase(Patient patient, FormInstance formInstance, List<DssElement> elementList,
	                                       Encounter encounter, Integer locationTagId, String formName,
	                                       Map<Integer, PatientATD> waitForScanFieldIdToAtdMap) {
		Integer patientId = patient.getPatientId();
		ATDService atdService = Context.getService(ATDService.class);
		DssService dssService = Context.getService(DssService.class);
		Integer formInstanceId = formInstance.getFormInstanceId();
		Integer locationId = formInstance.getLocationId();
		Integer encounterId = encounter.getEncounterId();
		
		for (int i = 0; i < elementList.size(); i++) {
			DssElement currDssElement = elementList.get(i);
			if (currDssElement == null) {
				continue;
			}
			
			Integer fieldId = (Integer) currDssElement.getParameter("fieldId");
			PatientATD patientATD = waitForScanFieldIdToAtdMap.get(fieldId);
			if (patientATD == null) {
				atdService.addPatientATD(patientId, formInstance, currDssElement, encounterId);
				addStatistic(dssService, atdService, patient, currDssElement, formInstanceId, i, encounter, formName,
				    locationTagId, locationId);
			} else if (currDssElement.getRuleId() != patientATD.getRule().getRuleId()) {
				Integer ruleId = currDssElement.getRuleId();
				Rule rule = null;
				Integer priority = null;
				RuleEntry ruleEntry = dssService.getRuleEntry(ruleId, formName);
				if (ruleEntry != null) {
					rule = ruleEntry.getRule();
					priority = ruleEntry.getPriority();
				} else {
					rule = dssService.getRule(ruleId);
				}
				
				patientATD.setRule(rule);
				Result result = currDssElement.getResult();
				if (result != null) {
					if (result.get(0) != null) {
						patientATD.setText(result.get(0).toString());
					} else {
						patientATD.setText(result.toString());
					}
				}
				
				patientATD.setCreationTime(new Date());
				atdService.updatePatientATD(patientATD);
				
				List<Statistics> stats = atdService.getStatByIdAndRule(formInstanceId, ruleId, formName, locationId);
				for (Statistics stat : stats) {
					stat.setRuleId(ruleId);
					stat.setPriority(priority);
					stat.setPrintedTimestamp(new Date());
					atdService.updateStatistics(stat);
				}
			}
		}
	}
	
	/**
	 * Adds statistical information for field rule execution.
	 * 
	 * @param dssService DssService used to access Rule information.
	 * @param atdService AtdService used to create and save statistical information.
	 * @param patient The patient associated with the process.
	 * @param currDssElement The current DssElement element to save statistical information.
	 * @param formInstanceId The form instance identifier.
	 * @param questionPosition The position of the question on the form.
	 * @param encounter The encounter associated with the statistical information.
	 * @param formName The name of the form.
	 * @param locationTagId The location tag identifier.
	 * @param locationId The location identifier.
	 */
	private void addStatistic(DssService dssService, ATDService atdService, Patient patient, DssElement currDssElement,
	                          Integer formInstanceId, int questionPosition, Encounter encounter, String formName,
	                          Integer locationTagId, Integer locationId) {
		Integer ruleId = currDssElement.getRuleId();
		// Try to get rule entry to determine priority
		Integer priority = null;
		RuleEntry ruleEntry = dssService.getRuleEntry(ruleId, formName);
		if (ruleEntry != null) {
			priority = ruleEntry.getPriority();
		}
		
		Statistics statistics = new Statistics();
		statistics.setAgeAtVisit(Util.adjustAgeUnits(patient.getBirthdate(), null));
		statistics.setPriority(priority);
		statistics.setFormInstanceId(formInstanceId);
		statistics.setLocationTagId(locationTagId);
		statistics.setPosition(questionPosition + 1);
		
		statistics.setRuleId(ruleId);
		statistics.setPatientId(patient.getPatientId());
		statistics.setFormName(formName);
		statistics.setEncounterId(encounter.getEncounterId());
		statistics.setLocationId(locationId);
		statistics.setPrintedTimestamp(new Date());
		
		atdService.createStatistics(statistics);
	}
	
	/**
	 * Retrieves the start priority of the rule execution based on other rules that have already been executed.
	 * 
	 * @param atdService ATDService object used to access statistical information.
	 * @param formInstanceId The form instance identifier.
	 * @param locationId The location identifier.
	 * @param formName The form name.
	 * @return The start priority of the rule execution.
	 */
	private Integer getStartPriority(ATDService atdService, Integer formInstanceId, Integer locationId, String formName) {
		Integer startPriority = new Integer(-1);
		List<Statistics> stats = atdService.getStatByFormInstance(formInstanceId, formName, locationId);
		for (Statistics stat : stats) {
			Integer priority = stat.getPriority();
			Date scannedDate = stat.getScannedTimestamp();
			if (priority == null || scannedDate == null) {
				continue;
			}
			
			if (priority > startPriority) {
				startPriority = priority;
			}
		}
		
		return startPriority + 1;
	}
	
	/**
	 * Sets the scanned timestamps for a specific form and rule.
	 * 
	 * @param formInstanceId The form instance identifier.
	 * @param ruleId The rule identifier.
	 * @param formName The name of the form.
	 * @param locationId The location identifier.
	 */
	private void setScannedTimestamps(Integer formInstanceId, Integer ruleId, String formName, Integer locationId) {
		if (ruleId == null) {
			return;
		}
		
		ATDService atdService = Context.getService(ATDService.class);
		List<Statistics> stats = atdService.getStatByIdAndRule(formInstanceId, ruleId, formName, locationId);
		if (stats == null) {
			return;
		}
		
		for (Statistics stat : stats) {
			if (stat.getScannedTimestamp() == null) {
				stat.setScannedTimestamp(new Date());
				atdService.updateStatistics(stat);
			}
		}
	}
	
	/**
	 * Parses a rule result string into a Field object and adds it to the provided list of fields.
	 * 
	 * @param resultString The result string to parse.
	 * @param fields The list to add the parsed result string to.
	 */
	private void mapResult(String resultString, List<Field> fields) {
		
		StringTokenizer tokenizer = new StringTokenizer(resultString, ",");
		while (tokenizer.hasMoreTokens()) {
			String currResult = tokenizer.nextToken();
			
			int atIndex = currResult.indexOf("@");
			if (atIndex >= 0 && atIndex + 1 < currResult.length()) {
				String fieldName = currResult.substring(atIndex + 1, currResult.length()).trim();
				currResult = currResult.substring(0, atIndex).trim();
				if (currResult.length() > 0) {
					Field field = new Field();
					field.setId(fieldName);
					field.setValue(currResult);
					fields.add(field);
				}
			}
		}
		
	}
	
	/**
	 * Serializes the fields to disk.
	 * 
	 * @param formInstance FormInstance object to identify the form that will have the fields add/updated.
	 * @param locationTagId The locationTagId where the form was created.
	 * @param fields The fields to add/update.
	 * @param elementsToRemoveList - DWE CHICA-430 Added parameter that contains a list of xml elements to remove
	 */
	private void serializeFields(FormInstance formInstance, Integer locationTagId, List<Field> fields, List<String> elementsToRemoveList) {
		ChirdlRunnable runnable = new FormWriter(formInstance, locationTagId, fields, elementsToRemoveList); // DWE CHICA-430 Add elementsToRemoveList;
		ThreadManager manager = ThreadManager.getInstance();
		manager.execute(runnable, formInstance.getLocationId());
	}
	
	/**
	 * DWE CHICA-430
	 * Gets a list of xml elements to remove. Elements will be removed if the obs record has been voided.
	 * 
	 * Currently only used for the PSF
	 * 
	 * @param formId
	 * @param formInstanceId
	 * @param encounterId
	 * @param locationTagId
	 * @param locationId
	 * @return list of element ids to remove
	 */
	private List<String> createElementsToRemoveList(Form form, Integer formInstanceId, Encounter encounter, Integer locationTagId, Integer locationId)
	{
		String displayAndUpdatePreviousValues = org.openmrs.module.chirdlutilbackports.util.Util.getFormAttributeValue(form.getFormId(), ChirdlUtilConstants.FORM_ATTR_DISPLAY_AND_UPDATE_PREVIOUS_VALUES, 
				locationTagId, locationId);
		
		if(displayAndUpdatePreviousValues != null && displayAndUpdatePreviousValues.equalsIgnoreCase(ChirdlUtilConstants.FORM_ATTR_VAL_TRUE)) // Currently only used for the PSF
		{
			try
			{
				List<String> elementsToRemoveList = new ArrayList<String>();
				ATDService atdService = Context.getService(ATDService.class);
				//ObsService obsService = Context.getObsService();
				List<org.openmrs.Encounter> encounters = new ArrayList<org.openmrs.Encounter>();
				encounters.add(encounter);
				
				FieldType mergeType = getFieldType(ChirdlUtilConstants.FORM_FIELD_TYPE_EXPORT);
				List<FieldType> fieldTypes = new ArrayList<FieldType>();
				fieldTypes.add(mergeType);
				
				// Get the list of export fields for this form
				List<FormField> formFields = Context.getService(ChirdlUtilBackportsService.class).getFormFields(form, fieldTypes, false);
				
				for(FormField formField : formFields)
				{
					Concept concept = formField.getField().getConcept();
					if(concept != null)
					{
						// Find obs records using the atd_statistics table and the form field id
						List<Obs> obsList = atdService.getObsWithStatistics(encounter.getEncounterId(), concept.getConceptId(), formField.getFormFieldId(), true);
					
						// Check to see if any of the obs records have been voided for the encounter
						// If an obs has been voided, but a new one was created, it will be added back to the xml
						// with the "fieldsToAdd" list
						for(Obs obs : obsList)
						{
							if(obs.getVoided())
							{
								elementsToRemoveList.add(formField.getField().getName());
								break;
							}
						}
					}
				}
				
				return elementsToRemoveList;
			}
			catch(Exception e)
			{
				log.error("Unable to create list of xml elements to remove (formId = " + form.getId() + " formInstanceId = " + formInstanceId + ").", e);
				return new ArrayList<String>();
			}	
		}
		else
		{
			return new ArrayList<String>();
		}	
	}
	
	/**
	 * DWE CHICA-430
	 * Get a list of "Export Field" types with values that were previously entered for the form 
	 * for this encounter
	 * 
	 * Currently only used for the PSF
	 * 
	 * @param formId
	 * @param formInstanceId
	 * @param encounterId
	 * @return list of fields with values that were previously entered for the form
	 */
	public List<Field> getExportElements(Integer formId, Integer formInstanceId, Integer encounterId, Integer locationTagId)
	{
		try
		{
			EncounterService encounterService = Context.getEncounterService();
			Encounter encounter = encounterService.getEncounter(encounterId);
			Integer locationId = encounter.getLocation().getId();
			List<Field> fields = new ArrayList<Field>();
			FormService formService = Context.getFormService();
			Form form = formService.getForm(formId);

			String displayAndUpdatePreviousValues = org.openmrs.module.chirdlutilbackports.util.Util.getFormAttributeValue(formId, ChirdlUtilConstants.FORM_ATTR_DISPLAY_AND_UPDATE_PREVIOUS_VALUES, 
					locationTagId, locationId);
			
			if(displayAndUpdatePreviousValues != null && displayAndUpdatePreviousValues.equalsIgnoreCase(ChirdlUtilConstants.FORM_ATTR_VAL_TRUE)) // Currently only used for the PSF
			{
				FormInstance formInstance = new FormInstance(locationId, formId, formInstanceId);

				String scanDirectory = IOUtil.formatDirectoryName(
						org.openmrs.module.chirdlutilbackports.util.Util.getFormAttributeValue(formId, XMLUtil.DEFAULT_EXPORT_DIRECTORY, 
								locationTagId, locationId));

				if (scanDirectory == null) 
				{
					log.info("No " + XMLUtil.DEFAULT_EXPORT_DIRECTORY + " found for Form: " + formId + " Location ID: " + locationId + 
							" Location Tag ID: " + locationTagId + ".");
					return new ArrayList<Field>();
				}

				File file = new File(scanDirectory, formInstance.toString() + ChirdlUtilConstants.FILE_EXTENSION_20);
				if (file.exists() && file.length() > 0) 
				{
					try
					{
						Records records = (Records) XMLUtil.deserializeXML(Records.class, new FileInputStream(file));
						Map<String, Field> fieldMap = new HashMap<String, Field>();
						List<Field> currentFields = records.getRecord().getFields();

						// Create a map with the current fields so the map can be used to lookup field values
						for(Field field : currentFields)
						{
							fieldMap.put(field.getId(), field);
						}

						FieldType exportType = getFieldType(ChirdlUtilConstants.FORM_FIELD_TYPE_EXPORT);
						List<FieldType> fieldTypes = new ArrayList<FieldType>();
						fieldTypes.add(exportType);

						// Get the list of export fields for this form
						List<FormField> formFields = Context.getService(ChirdlUtilBackportsService.class).getFormFields(form, fieldTypes, false);

						// Locate values for the export fields
						for(FormField formField : formFields)
						{
							String fieldName = formField.getField().getName();
							Field field = new Field();
							field.setId(fieldName);
							Field currentField = fieldMap.get(fieldName);
							if(currentField != null)
							{
								field.setValue(currentField.getValue());
							}
							else
							{
								field.setValue("");
							}

							fields.add(field);
						}	
					}
					catch(IOException ioe)
					{
						log.error(ioe.getMessage() + "(length = " + file.length() + ")");
						log.error(Util.getStackTrace(ioe));
						return new ArrayList<Field>();
					}

					return fields;
				}
				else 
				{
					return new ArrayList<Field>();
				}	
			}
			else
			{
				return new ArrayList<Field>();
			}
		}
		catch(APIException apie)
		{
			log.error(apie.getMessage());
			log.error(Util.getStackTrace(apie));
			return new ArrayList<Field>();
		}
	}
}
