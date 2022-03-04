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

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.ZipUtil;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.ChirdlLocationAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class createAndEmailJitByClinic implements Rule {
	
	private static final Logger log = LoggerFactory.getLogger(createAndEmailJitByClinic.class);
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#getParameterList()
	 */
	@Override
	public Set<RuleParameterInfo> getParameterList() {
		return null;
	}
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#getDependencies()
	 */
	@Override
	public String[] getDependencies() {
		return new String[] {};
	}
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#getTTL()
	 */
	@Override
	public int getTTL() {
		return 0; // 60 * 30; // 30 minutes
	}
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#getDefaultDatatype()
	 */
	@Override
	public Datatype getDefaultDatatype() {
		return Datatype.CODED;
	}
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer,
	 *      java.util.Map)
	 */
	@Override
	public Result eval(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		Integer locationId = (Integer) parameters.get("locationId");
		Integer locationTagId = (Integer) parameters.get("locationTagId");
		Integer encounterId = (Integer) parameters.get("encounterId");
		String locationAttr = (String) parameters.get("param1");
		String formName = (String) parameters.get("param2");
		String subject = (String) parameters.get("param3");
		String body = (String) parameters.get("param4");
		
		if (locationId == null) {
			log.error("Location ID parameter not found.  No one will be emailed for {} ",locationAttr);
			return Result.emptyResult();
		} else if (encounterId == null) {
			log.error("Encounter ID parameter not found.  No one will be emailed for {}", locationAttr);
			return Result.emptyResult();
		} else if (locationAttr == null) {
			log.error("Location Attribute Value parameter not found.  No one will be emailed for {}", locationAttr);
			return Result.emptyResult();
		} else if (formName == null) {
			log.error("Form name parameter not found.  No one will be emailed for {}", locationAttr);
			return Result.emptyResult();
		} else if (subject == null) {
			log.error("Subject parameter not found.  No one will be emailed for {}", locationAttr);
			return Result.emptyResult();
		} else if (body == null) {
			log.error("Body parameter not found.  No one will be emailed for ", locationAttr);
			return Result.emptyResult();
		}
		
		Location location = Context.getLocationService().getLocation(locationId);
		if (location == null) {
			log.error("No location found for location ID {}.  No one will be emailed for {}.", locationId, locationAttr);
			return Result.emptyResult();
		}
		
		Form form = Context.getFormService().getForm(formName);
		if (form == null) {
			log.error("No form found for form name {}.  No one will be emailed for {}.", formName, locationAttr);
			return Result.emptyResult();
		}
		
		// Create the JIT
		LogicService logicService = Context.getLogicService();
		parameters.put("mode", "PRODUCE");
		parameters.put("param1",formName);
		FormInstance formInstance = new FormInstance();
		formInstance.setLocationId(locationId);
		parameters.put("formInstance", formInstance);
		logicService.eval(patientId, "CREATE_JIT", parameters);
		
		// Check email parameters
		String zipPassword = Context.getAdministrationService().getGlobalProperty("chirdlutil.zipEncryptionPassword");
		if (zipPassword == null) {
			log.error("Please specify a valid value for the global property chirdlutil.zipEncryptionPassword.  No email " +
					"will be sent.");
		}
		
		// Find the email addresses
		ChirdlUtilBackportsService service = Context.getService(ChirdlUtilBackportsService.class);
		ChirdlLocationAttributeValue emailLav = service.getLocationAttributeValue(locationId, "diabetesEndoEmail");
		if (emailLav == null || emailLav.getValue() == null || emailLav.getValue().trim().length() == 0) {
			log.error("No location attribute value specified for location: {} and attribute name diabetesEndoEmail.  No form will be emailed.", locationId);
			return Result.emptyResult();
		}
		
		// Get the email addresses
		ChirdlLocationAttributeValue lav = service.getLocationAttributeValue(locationId, locationAttr);
		if (lav == null || lav.getValue() == null || lav.getValue().trim().length() == 0) {
			log.error("No valid {} found for location {}.  No one will be emailed.",locationAttr,locationId);
			return Result.emptyResult();
		}
		
		// Find the form instance ID
		Integer formId = form.getFormId();
		PatientState patientState = org.openmrs.module.atd.util.Util.getProducePatientStateByEncounterFormAction(
			encounterId, formId);
		if (patientState == null) {
			log.error("No valid patient state could be found for patient: {}, encounter ID: {}, form ID: {} location {}.  No one will be emailed.",
					patientId,encounterId,formId,locationId);
			return Result.emptyResult();
		}
		
		Integer formInstanceId = patientState.getFormInstanceId();
		formInstance.setFormId(formId);
		formInstance.setFormInstanceId(formInstanceId);
		String mergeDirectory = IOUtil.formatDirectoryName(
			org.openmrs.module.chirdlutilbackports.util.Util.getFormAttributeValue(formId, "defaultMergeDirectory", 
				locationTagId, locationId));
		File pdfFile = new File(mergeDirectory, formInstance.toString() + ".pdf");
		if (!pdfFile.exists()) {
			// Check the pdf directory
			File pdfDir = new File(mergeDirectory, "pdf");
			if (pdfDir.exists()) {
				pdfFile = new File(pdfDir, formInstance.toString() + ".pdf");
				if (!pdfFile.exists()) {
					log.error("Cannot find PDF file to zip and email. Directory: {} File: {}", mergeDirectory,formInstance.toString());
					return Result.emptyResult();
				}
			} else {
				log.error("Cannot find PDF file to zip and email. Directory: {} File: {}",mergeDirectory,formInstance.toString());
				return Result.emptyResult();
			}
		}
		
		String emailAddys = emailLav.getValue();
		String[] emailList = emailAddys.split(",");
		File[] files = new File[] { pdfFile };
		
		ZipUtil.zipAndEmailFiles(files, emailList, subject, body, zipPassword, formName, 30);
		
		return Result.emptyResult();
	}
}
