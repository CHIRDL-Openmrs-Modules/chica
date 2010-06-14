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
public class ScoreJit implements Rule {
	
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
		//see if an incomplete state exists for the JIT
		State currState = atdService.getStateByName("JIT_incomplete");
		List<PatientState> patientStates = atdService.getPatientStateByFormInstanceState(formInstance, currState);
		
		for(PatientState patientState:patientStates){
			//if there is an open JIT_incomplete state then don't score
			if(patientState.getEndTime()==null){
				log.error("Cannot score jit: "+formInstance+" because it is incomplete.");
				return Result.emptyResult();
			}
		}
		
		Util.scoreJit(formInstance, locationTagId, encounterId, patient);
		
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
