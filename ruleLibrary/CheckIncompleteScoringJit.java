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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.FieldType;
import org.openmrs.Form;
import org.openmrs.FormField;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.atd.TeleformTranslator;
import org.openmrs.module.atd.datasource.TeleformExportXMLDatasource;
import org.openmrs.module.atd.hibernateBeans.FormAttributeValue;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.State;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.util.Util;
import org.openmrs.module.chica.xmlBeans.Choose;
import org.openmrs.module.chica.xmlBeans.Field;
import org.openmrs.module.chica.xmlBeans.FormConfig;
import org.openmrs.module.chica.xmlBeans.Geq;
import org.openmrs.module.chica.xmlBeans.If;
import org.openmrs.module.chica.xmlBeans.Language;
import org.openmrs.module.chica.xmlBeans.LanguageAnswers;
import org.openmrs.module.chica.xmlBeans.Mean;
import org.openmrs.module.chica.xmlBeans.Plus;
import org.openmrs.module.chica.xmlBeans.Score;
import org.openmrs.module.chica.xmlBeans.Scores;
import org.openmrs.module.chica.xmlBeans.Then;
import org.openmrs.module.chica.xmlBeans.Value;
import org.openmrs.module.chirdlutil.util.XMLUtil;

/**
 * Calculates a person's age in years based from their date of birth to the index date
 */
public class CheckIncompleteScoringJit implements Rule {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, org.openmrs.Patient,
	 *      java.util.Map)
	 */
	public Result eval(LogicContext context, Patient patient, Map<String, Object> parameters) throws LogicException {
		
		FormInstance formInstance = (FormInstance) parameters.get("formInstance");
		Integer locationTagId = (Integer) parameters.get("locationTagId");
		Integer encounterId = (Integer) parameters.get("encounterId");
		Integer locationId = null;
		Integer formId = null;
		
		if (formInstance != null) {
			locationId = formInstance.getLocationId();
			formId = formInstance.getFormId();
		}
		
		ATDService atdService = Context.getService(ATDService.class);
		
		//parse the scan xml
		LogicService logicService = Context.getLogicService();
		TeleformExportXMLDatasource xmlDatasource = (TeleformExportXMLDatasource) logicService.getLogicDataSource("xml");
		HashMap<String, org.openmrs.module.atd.xmlBeans.Field> fieldMap = xmlDatasource.getParsedFile(formInstance);
		
		if(fieldMap == null){
			return Result.emptyResult();
		}
		
		//map fields to languages
		FormAttributeValue scorableFormConfigAttrVal = atdService.getFormAttributeValue(formId, "scorableFormConfigFile",
		    locationTagId, locationId);
		
		String scorableFormConfigFile = null;
		if (scorableFormConfigAttrVal != null) {
			scorableFormConfigFile = scorableFormConfigAttrVal.getValue();
		}
		if (scorableFormConfigFile == null) {
			log.error("Could not find scorableFormConfigFile for locationId: " + locationId + " and locationTagId: "
			        + locationTagId);
			return Result.emptyResult();
		}
		LanguageAnswers answersByLanguage = null;
		InputStream input = null;
		FormConfig formConfig = null;
		try {
			input = new FileInputStream(scorableFormConfigFile);
			formConfig = (FormConfig) XMLUtil.deserializeXML(FormConfig.class, input);
			answersByLanguage = formConfig.getLanguageAnswers();
		}
		catch (IOException e1) {
			log.error("", e1);
			return Result.emptyResult();
		}
		HashMap<String, Field> langFieldsToConsume = Util.getLanguageFieldsToConsume(fieldMap, formInstance,
		    answersByLanguage);
		
		HashMap<String, HashMap<String, FormField>> formFieldsMap = new HashMap<String, HashMap<String, FormField>>();
		FormService formService = Context.getFormService();
		Form form = formService.getForm(formId);
		
		//make a map of child to parent fields. This is used when figuring out
		//whether to score the spanish or english side
		//we assume the configuration file always configures using the english fields
		HashMap<String, FormField> childFields = null;
		for (org.openmrs.FormField currFormField : form.getFormFields()) {
			FormField parentField = currFormField.getParent();
			if (parentField != null) {
				String fieldName = currFormField.getField().getName();
				String parentName = parentField.getField().getName();
				childFields = formFieldsMap.get(parentName);
				if (childFields == null) {
					childFields = new HashMap<String, FormField>();
					formFieldsMap.put(parentName, childFields);
				}
				
				childFields.put(fieldName, currFormField);
			}
		}
		//parse the form configuration file
		if (scorableFormConfigAttrVal != null) {
			
			try {
				
				Scores scores = formConfig.getScores();
				Integer numBlankScoringFields = 0;

				//compute each score and save it to a concept in the database
				for (Score score : scores.getScores()) {
					
					Value value = score.getValue(); //value that should be saved to the concept
					Plus plus = value.getPlus();
					Mean mean = value.getMean();
					
					//compute the sum
					if (plus != null) {
						
						List<Choose> choices = plus.getChooses();
						
						//process conditional logic
						if (choices != null) {
							
							for (Choose choose : choices) {
								If ifObject = choose.getIf();
								
								if (ifObject != null) {
									Geq geq = ifObject.getGeq();
									
									if (geq != null) {
										Field fieldOperand = geq.getField();
										String cnOperand = geq.getCn();
										
										if (fieldOperand != null && cnOperand != null) {
											Field matchingField = pickFieldLanguage(fieldOperand, childFields,
											    langFieldsToConsume, formFieldsMap);
											if (matchingField != null && fieldMap != null) {
												org.openmrs.module.atd.xmlBeans.Field scorableFormField = fieldMap
												        .get(matchingField.getId());
												if (scorableFormField!=null&&
														scorableFormField.getValue() == null) {
													numBlankScoringFields++;
												}
											}
										}
									}
								}
								
							}
						}
						List<Field> fields = plus.getFields();
						
						//sum the fields
						if (fields != null) {
							
							numBlankScoringFields += computeSum(fields, childFields, langFieldsToConsume, fieldMap,
							    formFieldsMap);
						}
					}
					//compute the average
					if (mean != null) {
						
						List<Field> fields = mean.getFields();
						numBlankScoringFields += computeSum(fields, childFields, langFieldsToConsume, fieldMap,
						    formFieldsMap);
						
					}
				}
				Integer sessionId = (Integer) parameters.get("sessionId");
				
				//see if an incomplete state exists for the JIT
				State currState = atdService.getStateByName("JIT_incomplete");
				List<PatientState> patientStates = atdService.getPatientStateByFormInstanceState(formInstance, currState);
				
				PatientState openJITIncompleteState = null;
				
				//look for an open JIT_incomplete state
				for(PatientState patientState:patientStates){
					if(patientState.getEndTime()==null){
						openJITIncompleteState = patientState;
						break;
					}
				}
				
				//if the form is incomplete store an incomplete state
				if (numBlankScoringFields > 2) {
					
					//add a JIT_incomplete state if there is no open JIT_incomplete states
					if (openJITIncompleteState == null) {
						PatientState patientState = atdService.addPatientState(patient, currState, sessionId, locationTagId, locationId);
						patientState.setFormInstance(formInstance);
						atdService.updatePatientState(patientState);
					}
				} else {
					//if a JIT_incomplete state exists, make sure the state is ended
					if (openJITIncompleteState != null) {
						openJITIncompleteState.setEndTime(new java.util.Date());
						atdService.updatePatientState(openJITIncompleteState);
					}
				}
			}
			catch (Exception e) {
				log.error("", e);
			}
		}
		
		return Result.emptyResult();
		
	}
	
	private Field pickFieldLanguage(Field currField, HashMap<String, FormField> childFields,
	                                HashMap<String, Field> langFieldsToConsume,
	                                HashMap<String, HashMap<String, FormField>> formFieldsMap) {
		String fieldName = currField.getId();
		
		//field name in config file matches the preferred language
		//field name
		childFields = formFieldsMap.get(fieldName);
		Field matchingField = null;
		
		if (childFields != null) {
			
			//see which of the child fields is in the language list
			for (String currChildFieldName : childFields.keySet()) {
				
				matchingField = langFieldsToConsume.get(currChildFieldName);
				if (matchingField != null) {
					break;
				}
			}
		}
		if (matchingField == null) {
			matchingField = currField;
		}
		return matchingField;
	}
	
	private Integer computeSum(List<Field> fields, HashMap<String, FormField> childFields,
	                           HashMap<String, Field> langFieldsToConsume,
	                           HashMap<String, org.openmrs.module.atd.xmlBeans.Field> fieldMap,
	                           HashMap<String, HashMap<String, FormField>> formFieldsMap) {
		
		Integer numBlankScoringFields = 0;
		if(fields == null||fieldMap==null)
		{
			return numBlankScoringFields;
		}
		for (Field currField : fields) {
			
			Field matchingField = pickFieldLanguage(currField, childFields, langFieldsToConsume, formFieldsMap);
			org.openmrs.module.atd.xmlBeans.Field scorableFormField = fieldMap.get(matchingField.getId());
			
			if (scorableFormField.getValue() == null) {
				numBlankScoringFields++;
			}
		}

		return numBlankScoringFields;
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
		return new String[] { "%%patient.birthdate" };
	}
	
	/**
	 * @see org.openmrs.logic.rule.Rule#getTTL()
	 */
	public int getTTL() {
		return 60 * 60 * 24; // 1 day
	}
	
	/**
	 * @see org.openmrs.logic.rule.Rule#getDatatype(String)
	 */
	public Datatype getDefaultDatatype() {
		return Datatype.NUMERIC;
	}
	
}
