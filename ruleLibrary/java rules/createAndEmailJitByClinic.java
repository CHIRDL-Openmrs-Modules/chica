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
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.openmrs.module.chirdlutil.util.MailSender;
import org.openmrs.module.chirdlutil.util.ZipUtil;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.LocationAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

public class createAndEmailJitByClinic implements Rule {
	
	private Log log = LogFactory.getLog(this.getClass());
	private final String MAIL_SENDER = "chica.notification@iu.edu";
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#getParameterList()
	 */
	public Set<RuleParameterInfo> getParameterList() {
		return null;
	}
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#getDependencies()
	 */
	public String[] getDependencies() {
		return new String[] {};
	}
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#getTTL()
	 */
	public int getTTL() {
		return 0; // 60 * 30; // 30 minutes
	}
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#getDefaultDatatype()
	 */
	public Datatype getDefaultDatatype() {
		return Datatype.CODED;
	}
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer,
	 *      java.util.Map)
	 */
	public Result eval(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		Integer locationId = (Integer) parameters.get("locationId");
		Integer locationTagId = (Integer) parameters.get("locationTagId");
		Integer encounterId = (Integer) parameters.get("encounterId");
		String locationAttr = (String) parameters.get("param1");
		String formName = (String) parameters.get("param2");
		String subject = (String) parameters.get("param3");
		String body = (String) parameters.get("param4");
		
		if (locationId == null) {
			log.error("Location ID parameter not found.  No one will be emailed for " + locationAttr + ".");
			return Result.emptyResult();
		} else if (encounterId == null) {
			log.error("Encounter ID parameter not found.  No one will be emailed for " + locationAttr + ".");
			return Result.emptyResult();
		} else if (locationAttr == null) {
			log.error("Location Attribute Value parameter not found.  No one will be emailed for " + locationAttr + ".");
			return Result.emptyResult();
		} else if (formName == null) {
			log.error("Form name parameter not found.  No one will be emailed for " + locationAttr + ".");
			return Result.emptyResult();
		} else if (subject == null) {
			log.error("Subject parameter not found.  No one will be emailed for " + locationAttr + ".");
			return Result.emptyResult();
		} else if (body == null) {
			log.error("Body parameter not found.  No one will be emailed for " + locationAttr + ".");
			return Result.emptyResult();
		}
		
		Location location = Context.getLocationService().getLocation(locationId);
		if (location == null) {
			log.error("No location found for location ID " + locationId + ".  No one will be emailed for " + locationAttr + 
				".");
			return Result.emptyResult();
		}
		
		Form form = Context.getFormService().getForm(formName);
		if (form == null) {
			log.error("No form found for form name " + formName + ".  No one will be emailed for " + locationAttr + ".");
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
		String smtpMailHost = Context.getAdministrationService().getGlobalProperty("chirdlutil.smtpMailHost");
		if (smtpMailHost == null) {
			log.error("Please specify global property chirdlutil.smtpMailHost for correct email operability.");
			return Result.emptyResult();
		}
		
		String zipPassword = Context.getAdministrationService().getGlobalProperty("chirdlutil.zipEncryptionPassword");
		if (zipPassword == null) {
			log.error("Please specify a valid value for the global property chirdlutil.zipEncryptionPassword.  No email " +
					"will be sent.");
		}
		
		// Find the email addresses
		Properties mailProps = new Properties();
		mailProps.put("mail.smtp.host", smtpMailHost);
		MailSender mailSender = new MailSender(mailProps);
		ChirdlUtilBackportsService service = Context.getService(ChirdlUtilBackportsService.class);
		LocationAttributeValue emailLav = service.getLocationAttributeValue(locationId, "diabetesEndoEmail");
		if (emailLav == null || emailLav.getValue() == null || emailLav.getValue().trim().length() == 0) {
			log.error("No location attribute value specified for location: " + locationId + " and attribute name " + 
				"diabetesEndoEmail.  No form will be emailed.");
			return Result.emptyResult();
		}
		
		// Get the email addresses
		LocationAttributeValue lav = service.getLocationAttributeValue(locationId, locationAttr);
		if (lav == null || lav.getValue() == null || lav.getValue().trim().length() == 0) {
			log.error("No valid " + locationAttr + " found for location " + locationId + ".  No one will be emailed.");
			return Result.emptyResult();
		}
		
		// Find the form instance ID
		Integer formId = form.getFormId();
		PatientState patientState = service.getPatientStateByEncounterFormAction(encounterId, formId, 
			"PRODUCE FORM INSTANCE");
		if (patientState == null) {
			log.error("No valid patient state could be found for patient: " + patientId + ", encounter ID: " + encounterId + 
				", form ID: " + formId + " location " + locationId + ".  No one will be emailed.");
			return Result.emptyResult();
		}
		
		Integer formInstanceId = patientState.getFormInstanceId();
		formInstance.setFormId(formId);
		formInstance.setFormInstanceId(formInstanceId);
		String mergeDirectory = IOUtil.formatDirectoryName(
			org.openmrs.module.chirdlutilbackports.util.Util.getFormAttributeValue(formId, "defaultMergeDirectory", 
				locationTagId, locationId));
		File pdfFile = new File(mergeDirectory, formInstance.toString() + ".pdf");
		int maxTime = 1;
		// Try to find the file for a maximum of 30 seconds.
		while (!pdfFile.exists() && maxTime < 30) {
			maxTime++;
			try {
	            Thread.sleep(1000);
            }
            catch (InterruptedException e) {
	            log.error("Interrupted thread error", e);
            }
		}
		
		if (!pdfFile.exists()) {
			log.error("Cannot find the following file to email: " + mergeDirectory + formInstance.toString() + ".pdf");
			return Result.emptyResult();
		}
		
		String emailAddys = emailLav.getValue();
		String[] emailList = emailAddys.split(",");
		
		// Zip PDF file and send the email
		ArrayList<File> filesToZip = new ArrayList<File>();
		filesToZip.add(pdfFile);
		File targetZipFile = null;
		try {
			// The zip utility does not work when creating a file using File.createTempFile(name, extension).  A file with 
			// the specified name cannot already exist or it will fail.
			File baseDir = new File(System.getProperty("java.io.tmpdir"));
	        String extension = ".zip";
	        targetZipFile = new File(baseDir, formName + "_" + UUID.randomUUID() + extension);
	        while (targetZipFile.exists()) {
	        	targetZipFile = new File(baseDir, formName + "_" + UUID.randomUUID() + extension);
	        }
	        
			ZipUtil.zipFilesWithPassword(targetZipFile, filesToZip, zipPassword);
			File[] attachments = new File[] {targetZipFile};
			mailSender.sendMail(MAIL_SENDER, emailList, subject, body, attachments);
		} catch (Exception e) {
			log.error("Error zipping and sending email", e);
			return Result.emptyResult();
		} finally {
			if (targetZipFile != null && targetZipFile.exists()) {
				targetZipFile.delete();
			}
		}
		
		return Result.emptyResult();
	}
}
