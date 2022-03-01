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
package org.openmrs.module.chica.web;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Field;
import org.openmrs.FieldType;
import org.openmrs.Form;
import org.openmrs.FormField;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.result.Result;
import org.openmrs.module.atd.ParameterHandler;
import org.openmrs.module.atd.TeleformTranslator;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.atd.xmlBeans.Record;
import org.openmrs.module.atd.xmlBeans.Records;
import org.openmrs.module.chica.ChicaParameterHandler;
import org.openmrs.module.chica.DynamicFormAccess;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutil.util.XMLUtil;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormAttribute;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstanceAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstanceTag;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BadPdfFormatException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;


/**
 *
 * @author Steve McKee
 */
public class ServletUtil {
	
	public static final String WEEKS = "weeks";
	public static final String DAYS = "days";
	public static final String MONTHS = "months";
	public static final String YEARS = "years";

	public static final String IS_AUTHENTICATED = "isAuthenticated";
	public static final String AUTHENTICATE_USER = "authenticateUser";
	public static final String GET_PATIENT_JITS = "getPatientJITs";
	public static final String GET_AVAILABLE_PATIENT_JITS = "getAvailablePatientJITs";
	public static final String GET_FORCE_PRINT_FORMS = "getForcePrintForms";
	public static final String FORCE_PRINT_FORMS = "forcePrintForms";
	public static final String GET_GREASEBOARD_PATIENTS = "getGreaseboardPatients";
	public static final String VERIFY_MRN = "verifyMRN";
	public static final String GET_MANUAL_CHECKIN = "getManualCheckin";
	public static final String SAVE_MANUAL_CHECKIN = "saveManualCheckin";
	public static final String SEND_PAGE_REQUEST = "sendPageRequest";
	public static final String DISPLAY_FORCE_PRINT_FORMS = "displayForcePrintForms";
	public static final String KEEP_ALIVE = "keepAlive";
	public static final String CLEAR_CACHE = "clearCache";
	public static final String CLEAR_FORM_INSTANCE_FROM_FORM_CACHE = "clearFormInstanceFromFormCache";
	public static final String SAVE_FORM_DRAFT = "saveFormDraft";
	public static final String CONVERT_TIFF_TO_PDF = "convertTiffToPDF";
	public static final String TRANSFORM_FORM_XML = "transformFormXML";
	public static final String GET_PRIORITIZED_ELEMENTS = "getPrioritizedElements";
	public static final String SAVE_EXPORT_ELEMENTS = "saveExportElements";
	public static final String SAVE_PSF_COMPLETED_BY = "savePSFCompletedBy";
	
	public static final String PARAM_SESSION_ID = "sessionId";
	public static final String PARAM_FORM_IDS = "formIds";
	public static final String PARAM_LOCATION_ID = "locationId";
	public static final String PARAM_LOCATION_TAG_ID = "locationTagId";
	public static final String PARAM_FORM_INSTANCES = "formInstances";
	public static final String PARAM_FORM_INSTANCE = "formInstance";
	public static final String PARAM_PATIENT_ID = "patientId";
	public static final String PARAM_MRN = "mrn";
	public static final String PARAM_PATIENT_ROWS = "patientRows";
	public static final String PARAM_NEED_VITALS = "needVitals";
	public static final String PARAM_WAITING_FOR_MD = "waitingForMD";
	public static final String PARAM_BAD_SCANS = "badScans";
	public static final String PARAM_CACHE_NAME = "cacheName";
	public static final String PARAM_CACHE_KEY_TYPE = "cacheKeyType";
	public static final String PARAM_CACHE_VALUE_TYPE = "cacheValueType";
	public static final String PARAM_PROVIDER_ID = "providerId";
	public static final String PARAM_ACTION = "action";
	public static final String PARAM_TIFF_FILE_LOCATION = "tiffFileLocation";
	public static final String PARAM_MAX_ELEMENTS = "maxElements";
	
	public static final String XML_AVAILABLE_JITS_START = "<availableJITs>";
	public static final String XML_AVAILABLE_JITS_END = "</availableJITs>";
	public static final String XML_AVAILABLE_JIT_START = "<availableJIT>";
	public static final String XML_AVAILABLE_JIT_END = "</availableJIT>";
	public static final String XML_FORM_NAME = "formName";
	public static final String XML_FORM_ID = "formId";
	public static final String XML_FORM_INSTANCE_ID = "formInstanceId";
	public static final String XML_FORM_INSTANCE_TAG = "formInstanceTag";
	public static final String XML_LOCATION_ID = "locationId";
	public static final String XML_LOCATION_TAG_ID = "locationTagId";
	public static final String XML_FORCE_PRINT_JITS_START = "<forcePrintJITs>";
	public static final String XML_FORCE_PRINT_JITS_END = "</forcePrintJITs>";
	public static final String XML_FORCE_PRINT_JIT_START = "<forcePrintJIT>";
	public static final String XML_FORCE_PRINT_JIT_END = "</forcePrintJIT>";
	public static final String XML_GROUP = "group";
	public static final String XML_GROUP_NAME = "name";
	public static final String XML_GROUP_END = "</group>";
	public static final String XML_DISPLAY_NAME = "displayName";
	public static final String XML_PATIENT_ROWS_START = "<patientRows>";
	public static final String XML_PATIENT_ROWS_END = "</patientRows>";
	public static final String XML_GREASEBOARD_START = "<greaseboard>";
	public static final String XML_GREASEBOARD_END = "</greaseboard>";
	public static final String XML_NEED_VITALS_START = "<needVitals>";
	public static final String XML_NEED_VITALS_END = "</needVitals>";
	public static final String XML_WAITING_FOR_MD_START = "<waitingForMD>";
	public static final String XML_WAITING_FOR_MD_END = "</waitingForMD>";
	public static final String XML_BAD_SCANS_START = "<badScans>";
	public static final String XML_BAD_SCANS_END = "</badScans>";
	public static final String XML_URL_START = "<url>";
	public static final String XML_URL_END = "</url>";
	public static final String XML_OUTPUT_TYPE_START = "<outputType>";
	public static final String XML_OUTPUT_TYPE_END = "</outputType>";
	public static final String XML_OUTPUT_TYPE = "outputType";
	public static final String XML_ERROR_MESSAGES_START = "<errorMessages>";
	public static final String XML_ERROR_MESSAGES_END = "</errorMessages>";
	public static final String XML_ERROR_MESSAGE = "errorMessage";
	public static final String XML_RECORDS_START = "<Records>";
	public static final String XML_RECORDS_END = "</Records>";
	public static final String XML_RECORD_START = "<Record>";
	public static final String XML_RECORD_END = "</Record>";
	public static final String XML_VALUE = "Value";
	public static final String XML_FIELD = "Field";
	public static final String XML_FIELD_END = "</Field>";
	public static final String XML_ID = "id";
	public static final String XML_SAVE_RESULT_START = "<saveResult>";
	public static final String XML_SAVE_RESULT_END = "</saveResult>";
	public static final String XML_RESULT = "result";
	
	public static final String STYLESHEET = "stylesheet";
	public static final String FORM_DIRECTORY = "formDirectory";
	public static final String MAX_CACHE_AGE = "600";
	public static final String RESULT_SUCCESS = "success";
	
	private static final Logger LOG = LoggerFactory.getLogger(ServletUtil.class);
	
	public static void isUserAuthenticated(HttpServletResponse response) throws IOException {
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		// The servlet has already checked authentication by this point.
		PrintWriter pw = response.getWriter();
		pw.write("<userAuthenticated>");
		pw.write("<result>");
		pw.write("true");
		pw.write("</result>");
		pw.write("</userAuthenticated>");
	}
	
	public static void authenticateUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.write("<userAuthenticated>");
		pw.write("<result>");
		pw.write("true");
		pw.write("</result>");
		pw.write("</userAuthenticated>");
	}
	
	public static boolean authenticateUser(HttpServletRequest request) throws IOException {
		if (Context.getAuthenticatedUser() != null) {
			return true;
		}
		
		String auth = request.getHeader(ChirdlUtilConstants.HTTP_AUTHORIZATION_HEADER);
		if (auth == null) {
            return false;  // no auth
        }
        if (!auth.toUpperCase().startsWith("BASIC ")) { 
            return false;  // we only do BASIC
        }
        // Get encoded user and password, comes after "BASIC "
        String userpassEncoded = auth.substring(6);
        // Decode it, using any base 64 decoder
              
        /**
         * Edited sun.misc.Base64Decoder to org.apache.commons.codec.binary.Base64.decodeBase64
         */
        byte[] bytes = userpassEncoded.getBytes();//"UTF-8");
		byte[] b = org.apache.commons.codec.binary.Base64.decodeBase64(bytes);
		String userpassDecoded = new String(b);
        
        String[] userpass = userpassDecoded.split(":");
        if (userpass.length != 2) {
        	return false;
        }
        
        String user = userpass[0];
        String pass = userpass[1];
        try {
        	Context.authenticate(user, pass);
        } catch (ContextAuthenticationException e) {
        	return false;
        }
        
        return true;
	}
	
	public static String escapeXML(String str) {
		if (str == null) {
			return str;
		}
		
		return str.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
		        .replace("'", "&apos;");
	}
	
	public static void writeTag(String tagName, Object value, PrintWriter pw) {
		pw.write("<" + tagName + ">");
		if (value != null) {
			pw.write(value.toString());
		}
		
		pw.write("</" + tagName + ">");
	}
	
	/**
	 * Utility method for logging messages to a physical log as well as formatting the message for display to a user 
	 * using HTML.
	 * 
	 * @param pw Printer used to write the HTML version.  This can be null if there's no intention for an HTML version
	 * of the message.
	 * @param e An exception to be logged.  This can be null if the exception logging is not needed.
	 * @param log Log object used to write the message to disk.  This can be null if there's no intention to write the 
	 * message to disk.
	 * @param errorMessageParts The pieces used to build the log messages.
	 * @return The HTML formatted message
	 */
	public static String writeHtmlErrorMessage(PrintWriter pw, Exception e, Logger log, String... errorMessageParts) {
		if (errorMessageParts == null || errorMessageParts.length == 0) {
			return ChirdlUtilConstants.GENERAL_INFO_EMPTY_STRING;
		}
		
		StringBuilder htmlMessageBuffer = new StringBuilder("<b>");
		StringBuilder messageBuffer = new StringBuilder();
		for (String errorMessagePart : errorMessageParts) {
			htmlMessageBuffer.append("<p>");
			htmlMessageBuffer.append(errorMessagePart);
			htmlMessageBuffer.append("</p>");
			messageBuffer.append(errorMessagePart);
			messageBuffer.append(" ");
		}
		
		if (log != null) {
			if (e != null) {
				log.error(messageBuffer.toString(), e);
			} else {
				log.error(messageBuffer.toString());
			}
		}
		
		htmlMessageBuffer.append("</b>");
		if (pw != null) {
			pw.write(htmlMessageBuffer.toString());
		}
		
		return htmlMessageBuffer.toString();
	}
	
	/**
	 * Returns the name of a form.  If a form has a display name set, that will be returned first.  Otherwise
	 * the forms actual name will be returned.  Null will be returned if the form cannot be found.
	 * 
	 * @param formId The ID of the form.
	 * @param locationId The ID of the location.
	 * @param locationTagId The ID of the location tag.
	 * @return The name of the form.  Display name will be returned firstly, form name will be returned secondly, 
	 * and null will be returned thirdly if the form cannot be found.
	 */
	public static String getFormName(Integer formId, Integer locationId, Integer locationTagId) {
		String formName = null;
		
		// Try to get a display name if one exists.
		FormAttributeValue fav = Context.getService(ChirdlUtilBackportsService.class).getFormAttributeValue(
			formId, ChirdlUtilConstants.FORM_ATTR_DISPLAY_NAME, locationTagId, locationId);
		if (fav != null && fav.getValue() != null && fav.getValue().trim().length() > 0) {
			formName = fav.getValue();
		} else {
			Form form = Context.getFormService().getForm(formId);
			if (form != null) {
				formName = form.getName();
			}
		}
		
		return formName;
	}
	
	/**
	 * Retrieves the available form instances for a patient.
	 * 
	 * @param request HttServletRequest
	 * @param response HttpServletResponse
	 * @throws IOException
	 */
	public static void getAvailablePatientJITs(HttpServletRequest request, HttpServletResponse response) 
			throws IOException {
		Integer encounterId = Integer.valueOf(request.getParameter(ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID));
		getAvailablePatientJITs(encounterId, response);
	}
	
	/**
	 * Retrieves the available form instances for a patient.
	 * 
	 * @param ecounterId Encounter identifier
	 * @param response HttpServletResponse
	 * @throws IOException
	 */
	public static void getAvailablePatientJITs(Integer encounterId, HttpServletResponse response) 
			throws IOException {
		response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_XML);
		response.setHeader(
			ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL, ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL_NO_CACHE);
		PrintWriter pw = response.getWriter();
		pw.write(XML_AVAILABLE_JITS_START);
		
		ChirdlUtilBackportsService backportsService = Context.getService(ChirdlUtilBackportsService.class);
		
		State createState = backportsService.getStateByName(ChirdlUtilConstants.STATE_JIT_CREATE);
		if (createState == null) {
		    LOG.error("The state " + ChirdlUtilConstants.STATE_JIT_CREATE + " does not exist.  No patient JITs will be "
					+ "retrieved.");
			pw.write(XML_AVAILABLE_JITS_END);
			return;
		}
		
		Map<String, FormInstanceTag> formInfoMap = new HashMap<>();
		List<PatientState> patientStates = 
				backportsService.getPatientStateByEncounterState(encounterId, createState.getStateId());
		for (PatientState patientState : patientStates) {
			FormInstance formInstance = patientState.getFormInstance();
			if (formInstance == null) {
				continue;
			}
			
			Integer locationId = formInstance.getLocationId();
			Integer formId = formInstance.getFormId();
			Integer formInstanceId = formInstance.getFormInstanceId();
			Integer locationTagId = patientState.getLocationTagId();
			
			String formName = getFormName(formId, locationId, locationTagId);
			
			// Only want the latest form instance.  The patient states are ordered by start/end time descending.
			if (formInfoMap.get(formName) != null) {
				continue;
			}
			
			// Check to make sure the form is type PDF.
			FormAttributeValue fav = backportsService.getFormAttributeValue(formId, ChirdlUtilConstants.FORM_ATTR_OUTPUT_TYPE, 
				locationTagId, locationId);
			if (fav == null || fav.getValue() == null || fav.getValue().trim().length() == 0) {
				continue;
			}
			
			String value = fav.getValue();
			String[] values = value.split(ChirdlUtilConstants.GENERAL_INFO_COMMA);
			boolean isPdfType = false;
			for (String favValue : values) {
				if (ChirdlUtilConstants.FORM_ATTR_VAL_PDF.equalsIgnoreCase(favValue.trim())) {
					isPdfType = true;
					break;
				}
			}
			
			if (!isPdfType) {
				continue;
			}
			
			// Make sure the form wasn't force printed.
			FormInstanceAttributeValue fiav = backportsService.getFormInstanceAttributeValue(formId, formInstanceId, locationId, ChirdlUtilConstants.FORM_INST_ATTR_TRIGGER);
			
			if (fiav != null && ChirdlUtilConstants.FORM_INST_ATTR_VAL_FORCE_PRINT.equals(fiav.getValue())) {
				continue;
			}
			
			// Get the merge directory for the form.
			fav = backportsService.getFormAttributeValue(formId, ChirdlUtilConstants.FORM_ATTR_DEFAULT_MERGE_DIRECTORY, 
				locationTagId, locationId);
			if (fav == null || fav.getValue() == null || fav.getValue().trim().length() == 0) {
			    LOG.error("{} global property not defined for formId: {} locationId: {} locationTagId: {}",
			    		ChirdlUtilConstants.FORM_ATTR_DEFAULT_MERGE_DIRECTORY,formId,locationId,locationTagId);
				continue;
			}
			
			// Find the merge PDF file.
			String mergeDirectory = fav.getValue();
			File pdfDir = new File(mergeDirectory, ChirdlUtilConstants.FILE_PDF);
			File mergeFile = new File(pdfDir, locationId + ChirdlUtilConstants.GENERAL_INFO_UNDERSCORE + formId + 
				ChirdlUtilConstants.GENERAL_INFO_UNDERSCORE + formInstanceId + ChirdlUtilConstants.FILE_EXTENSION_PDF);
			if (!mergeFile.exists()) {
				File secondMergeFile = new File(pdfDir, ChirdlUtilConstants.GENERAL_INFO_UNDERSCORE + locationId + 
					ChirdlUtilConstants.GENERAL_INFO_UNDERSCORE + formId + ChirdlUtilConstants.GENERAL_INFO_UNDERSCORE + 
					formInstanceId + ChirdlUtilConstants.GENERAL_INFO_UNDERSCORE + ChirdlUtilConstants.FILE_EXTENSION_PDF);
				if (!secondMergeFile.exists()) {
				    LOG.error("Cannot locate PDF merge file for formId: {} locationId: {} locationTagId: {} merge file: {}",
				    		formId,locationId,locationTagId,mergeFile.getAbsolutePath());
					continue;
				}
			}
			
			FormInstanceTag tag = new FormInstanceTag(locationId, formId, formInstanceId, locationTagId);
			formInfoMap.put(formName, tag);
		}
		
		// Sort the form names and write them to the print writer.
		if (!formInfoMap.isEmpty()) {
			Set<String> formNameSet = formInfoMap.keySet();
			List<String> formNameList = new ArrayList<>(formNameSet);
			Collections.sort(formNameList);
			
			for (String formName : formNameList) {
				FormInstanceTag tag = formInfoMap.get(formName);
				pw.write(XML_AVAILABLE_JIT_START);
				writeTag(XML_FORM_NAME, escapeXML(formName), pw);
				writeTag(XML_FORM_ID, tag.getFormId(), pw);
				writeTag(XML_FORM_INSTANCE_ID, tag.getFormInstanceId(), pw);
				writeTag(XML_LOCATION_ID, tag.getLocationId(), pw);
				writeTag(XML_LOCATION_TAG_ID, tag.getLocationTagId(), pw);
				pw.write(XML_AVAILABLE_JIT_END);
			}
		}
		
		pw.write(XML_AVAILABLE_JITS_END);
	}
	
	/**
	 * Retrieves the list of possible force print forms for a patient..
	 * 
	 * @param request HttServletRequest
	 * @param response HttpServletResponse
	 * @throws IOException
	 */
	public static void getForcePrintForms(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String patientIdString = request.getParameter(PARAM_PATIENT_ID);
		Integer patientId = null;
		if (StringUtils.isNotBlank(patientIdString)) {
			try {
				patientId = Integer.valueOf(patientIdString);
			}
			catch (Exception e) {
				String message = "Invalid patientId parameter provided: " + patientIdString;
				LOG.error(message, e);
				throw new IllegalArgumentException(message);
			}
		}
		
		String sessionIdString = request.getParameter(PARAM_SESSION_ID);
		Integer sessionId = null;
		if (StringUtils.isNotBlank(sessionIdString)) {
			try {
				sessionId = Integer.valueOf(sessionIdString);
			}
			catch (Exception e) {
				String message = "Invalid sessionId parameter provided: " + sessionIdString;
				LOG.error(message, e);
				throw new IllegalArgumentException(message);
			}
		}
		
		String locationString = request.getParameter(PARAM_LOCATION_ID);
		Integer locationId = null;
		if (StringUtils.isNotBlank(locationString)) {
			try {
				locationId = Integer.valueOf(locationString);
			} catch (NumberFormatException e) {
				String message = "Invalid locationId parameter: " + locationString;
				LOG.error(message, e);
				throw new IllegalArgumentException(message);
			}
		}
		
		String locationTags = request.getParameter(PARAM_LOCATION_TAG_ID);
		Integer locationTagId = null;
		if (StringUtils.isNotBlank(locationTags)) {
			try {
				locationTagId = Integer.valueOf(locationTags);
			} catch (NumberFormatException e) {
				String message = "Invalid locationTagId parameter: " + locationTags;
				LOG.error(message, e);
				throw new IllegalArgumentException(message);
			}
		}
		
		String mrn = request.getParameter(PARAM_MRN);
		getForcePrintForms(patientId, sessionId, locationId, locationTagId, mrn, response);
	}
	
	/**
	 * Retrieves the list of possible force print forms for a patient..
	 * 
	 * @param patientId Patient identifier
	 * @param sessionId Session identifier
	 * @param locationId Location identifier
	 * @param locationTagId Location tag identifier
	 * @param mrn Patient's medical record number
	 * @param response HttpServletResponse
	 * @throws IOException
	 */
	public static void getForcePrintForms(Integer patientId, Integer sessionId, Integer locationId, 
			Integer locationTagId, String mrn, HttpServletResponse response) throws IOException {
		response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_XML);
		response.setHeader(
			ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL, ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL_NO_CACHE);
		PrintWriter pw = response.getWriter();
		pw.write(XML_FORCE_PRINT_JITS_START);
		
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		Patient patient = null;
		if (patientId == null) {
			if (StringUtils.isNotBlank(mrn)) {
				patient = getPatientByMRN(mrn);
			}
		} else {
			patient = Context.getPatientService().getPatient(patientId);
		}
		
		if (patient == null) {
			String message = "No valid patient could be located.";
			
			// DWE CHICA-576 Add some additional logging
			if(patientId != null)
			{
				message += " patientId: " + patientId;
			}
			if(sessionId != null)
			{
				message += " sessionId: " + sessionId;
			}
			if(locationId != null)
			{
				message += " locationId: " + locationId;
			}
			if(locationTagId != null)
			{
				message += " locationTagId: " + locationTagId;
			}
			LOG.error(message);
			throw new IllegalArgumentException(message);
		}
		
		Integer foundSessionId = sessionId;
		if (sessionId == null) {
			foundSessionId = getEncounterSessionId(patient.getPatientId());
		}
		
		if (foundSessionId == null) {
			String message = "Could not find a valid sessionId for patient: " + patientId;
			LOG.error(message);
			throw new IllegalArgumentException(message);
		}
		
		
		User user = Context.getUserContext().getAuthenticatedUser();
		Location location = null;
		LocationService locationService = Context.getLocationService();
		if (locationId == null) {
			String locationString = user.getUserProperty(ChirdlUtilConstants.USER_PROPERTY_LOCATION);
			location = locationService.getLocation(locationString);
		} else {
			location = locationService.getLocation(locationId);
		}
		
		Integer foundLocationTagId = locationTagId;
		if (location != null) {
			if (locationTagId == null) {
				String locationTags = user.getUserProperty(ChirdlUtilConstants.USER_PROPERTY_LOCATION_TAGS);
				if (locationTags != null) {
					StringTokenizer tokenizer = new StringTokenizer(locationTags, ChirdlUtilConstants.GENERAL_INFO_COMMA);
					while (tokenizer.hasMoreTokens()) {
						String locationTagName = tokenizer.nextToken();
						locationTagName = locationTagName.trim();
						Set<LocationTag> tags = location.getTags();
						for (LocationTag tag : tags) {
							if (tag.getName().equalsIgnoreCase(locationTagName)) {
								foundLocationTagId = tag.getLocationTagId();
							}
						}
					}
				}
			}
		}
		
		FormService formService = Context.getFormService();
		FormAttribute forcePrintAttr = chirdlutilbackportsService.getFormAttributeByName(
			ChirdlUtilConstants.FORM_ATTR_FORCE_PRINTABLE);
		if (forcePrintAttr == null) {
			return;
		}
		
		List<FormAttributeValue> attributes = chirdlutilbackportsService.getFormAttributeValues(
			forcePrintAttr.getFormAttributeId(), locationId, foundLocationTagId);
		Map<String, Integer> ageUnitsMinMap = new HashMap<>();
		Map<String, Integer> ageUnitsMaxMap = new HashMap<>();
		Set<FormDisplay> printableJits = new TreeSet<>();
		
		FormAttribute ageMinAttr = chirdlutilbackportsService.getFormAttributeByName(
			ChirdlUtilConstants.FORM_ATTR_AGE_MIN);
		FormAttribute ageMaxAttr = chirdlutilbackportsService.getFormAttributeByName(
			ChirdlUtilConstants.FORM_ATTR_AGE_MAX);
		FormAttribute ageMinUnitsAttr = chirdlutilbackportsService.getFormAttributeByName
				(ChirdlUtilConstants.FORM_ATTR_AGE_MIN_UNITS);
		FormAttribute ageMaxUnitsAttr = chirdlutilbackportsService.getFormAttributeByName(
			ChirdlUtilConstants.FORM_ATTR_AGE_MAX_UNITS);
		FormAttribute displayNameAttr = chirdlutilbackportsService.getFormAttributeByName(
			ChirdlUtilConstants.FORM_ATTR_DISPLAY_NAME);
		FormAttribute outputTypeAttr = chirdlutilbackportsService.getFormAttributeByName(
			ChirdlUtilConstants.FORM_ATTR_OUTPUT_TYPE);
		FormAttribute displayGpHeaderAttr = chirdlutilbackportsService.getFormAttributeByName(
			ChirdlUtilConstants.FORM_ATTRIBUTE_DISPLAY_GP_HEADER);
		
		Map<Integer, String> formAttrValAgeMinMap = getFormAttributeValues(
			chirdlutilbackportsService, ageMinAttr.getFormAttributeId(), locationId, foundLocationTagId);
		Map<Integer, String> formAttrValAgeMinUnitsMap = getFormAttributeValues(
			chirdlutilbackportsService, ageMinUnitsAttr.getFormAttributeId(), locationId, foundLocationTagId);
		Map<Integer, String> formAttrValAgeMaxMap = getFormAttributeValues(
			chirdlutilbackportsService, ageMaxAttr.getFormAttributeId(), locationId, foundLocationTagId);
		Map<Integer, String> formAttrValAgeMaxUnitsMap = getFormAttributeValues(
			chirdlutilbackportsService, ageMaxUnitsAttr.getFormAttributeId(), locationId, foundLocationTagId);
		Map<Integer, String> formAttrValDisplayNameMap = getFormAttributeValues(
			chirdlutilbackportsService, displayNameAttr.getFormAttributeId(), locationId, foundLocationTagId);
		Map<Integer, String> formAttrValDisplayGpHeaderMap = getFormAttributeValues(
			chirdlutilbackportsService, displayGpHeaderAttr.getFormAttributeId(), locationId, foundLocationTagId);
		Map<Integer, String> formAttrValOutputTypeMap = getFormAttributeValues(
			chirdlutilbackportsService, outputTypeAttr.getFormAttributeId(), locationId, foundLocationTagId);

		String defaultOutputType = Context.getAdministrationService().getGlobalProperty(
			ChirdlUtilConstants.GLOBAL_PROP_DEFAULT_OUTPUT_TYPE);
		if (defaultOutputType == null) {
			defaultOutputType = "";
		}
		for (FormAttributeValue attribute : attributes) {
			if (attribute.getValue().equalsIgnoreCase(ChirdlUtilConstants.FORM_ATTR_VAL_TRUE) && 
					attribute.getLocationId().equals(locationId) && 
					attribute.getLocationTagId().equals(foundLocationTagId)) {
				Form form = formService.getForm(attribute.getFormId());
				Integer formId = form.getFormId();
				if (!form.getRetired()) {
					FormDisplay formDisplay = new FormDisplay();
					formDisplay.setFormName(form.getName());
					formDisplay.setFormId(form.getFormId());
					String displayName = formAttrValDisplayNameMap.get(formId);
					if (displayName == null || displayName.trim().length() == 0) {
						formDisplay.setDisplayName(form.getName());
					} else {
						formDisplay.setDisplayName(displayName);
					}
					String displayGpHeader = formAttrValDisplayGpHeaderMap.get(formId);
					if (displayGpHeader != null && displayGpHeader.trim().length() != 0) {
						formDisplay.setDisplayGpHeader(displayGpHeader);
					} 
					String strOutputType = null;
					String outputType = formAttrValOutputTypeMap.get(formId);
					if (outputType != null && outputType.trim().length() != 0) {
						strOutputType = outputType;
					} else {
						strOutputType = defaultOutputType;
					}
					formDisplay.setOutputType(strOutputType);
					
					String ageMin = formAttrValAgeMinMap.get(formId);
					if (ageMin == null || ageMin.trim().length() == 0) {
						printableJits.add(formDisplay);
						continue;
					}
					
					String ageMinUnits = formAttrValAgeMinUnitsMap.get(formId);
					if (ageMinUnits == null || ageMinUnits.trim().length() == 0) {
						printableJits.add(formDisplay);
						continue;
					}
					
					String ageMax = formAttrValAgeMaxMap.get(formId);
					if (ageMax == null || ageMax.trim().length() == 0) {
						printableJits.add(formDisplay);
						continue;
					}
					
					String ageMaxUnits = formAttrValAgeMaxUnitsMap.get(formId);
					if (ageMaxUnits == null || ageMaxUnits.trim().length() == 0) {
						printableJits.add(formDisplay);
						continue;
					}

					Integer nowAgeWithMinUnits = ageUnitsMinMap.get(ageMinUnits);
					if (nowAgeWithMinUnits == null) {
						nowAgeWithMinUnits = Util.getAgeInUnits(patient.getBirthdate(), new Date(), 
							convertUnits(ageMinUnits));
						ageUnitsMinMap.put(ageMinUnits, nowAgeWithMinUnits);
					}
					
					Integer nowAgeWithMaxUnits = ageUnitsMaxMap.get(ageMaxUnits);
					if (nowAgeWithMaxUnits == null) {
						nowAgeWithMaxUnits = Util.getAgeInUnits(patient.getBirthdate(), new Date(), 
							convertUnits(ageMaxUnits));
						ageUnitsMaxMap.put(ageMaxUnits, nowAgeWithMaxUnits);
					}
					
					try{

						if(nowAgeWithMinUnits.intValue()<Integer.parseInt(ageMin)){
							continue;
						}
						if(nowAgeWithMaxUnits.intValue()>= Integer.parseInt(ageMax)){
							continue;
						}
					}
					catch(NumberFormatException e){
						continue;
					}
					printableJits.add(formDisplay);
				}
			}
		}
		List<String> generalFrmsArray = new ArrayList<>();
		HashMap<String, List<String>> groupMap = new HashMap<>();
		for (FormDisplay formDisplay: printableJits) {
			
			String strFormDisplay = formDisplay.getFormId()+","+formDisplay.getDisplayName()+","+formDisplay.getOutputType();
			if (formDisplay.getDisplayGpHeader() != null && !formDisplay.getDisplayGpHeader().isEmpty()) {
				if (!groupMap.containsKey(formDisplay.getDisplayGpHeader())) { 
					List<String> list = new ArrayList<>();
				    list.add(strFormDisplay);
				    groupMap.put(formDisplay.getDisplayGpHeader(), list);
				}  else {
					groupMap.get(formDisplay.getDisplayGpHeader()).add(strFormDisplay);
				}
			} else {
				generalFrmsArray.add(strFormDisplay);
			}
		}
		
		List<String> generalFormNames = new ArrayList<>();
		Map<String, String> generalMap = new HashMap<>();
		
		for (String value : generalFrmsArray) {
			String[] values = value.split(ChirdlUtilConstants.GENERAL_INFO_COMMA);
			generalFormNames.add(values[1]);
			generalMap.put(values[1], values[0]+","+values[2]);
		}
		
		List<String> frmHeaderLst = new ArrayList<>();
		frmHeaderLst.addAll(generalFormNames);
		frmHeaderLst.addAll(groupMap.keySet());
		Collections.sort(frmHeaderLst);
		
		for (String value : frmHeaderLst) {
			if (generalFormNames.contains(value)) {
				pw.write(ChirdlUtilConstants.XML_START_TAG + XML_GROUP + ChirdlUtilConstants.XML_END_TAG);
				pw.write(XML_FORCE_PRINT_JIT_START);
				String[] generalValues = generalMap.get(value).split(ChirdlUtilConstants.GENERAL_INFO_COMMA);
				writeTag(XML_FORM_ID, generalValues[0], pw);
				writeTag(XML_DISPLAY_NAME, escapeXML(value), pw);
				pw.write(XML_OUTPUT_TYPE_START);
				pw.write(generalValues[1]);
				pw.write(XML_OUTPUT_TYPE_END);
				pw.write(XML_FORCE_PRINT_JIT_END);
				pw.write(XML_GROUP_END);
			} else {
				List<String> groupForms  = groupMap.get(value);
				List<String> displayName = new ArrayList<>();
				Map<String, String> branchMap = new HashMap<>();
				
				pw.write(ChirdlUtilConstants.XML_START_TAG + XML_GROUP + " " + XML_GROUP_NAME + 
					"=\"" + value + "\"" + ChirdlUtilConstants.XML_END_TAG);
				for (String gpForm : groupForms) {
					String[] gpFrmValues = gpForm.split(ChirdlUtilConstants.GENERAL_INFO_COMMA);
					displayName.add(gpFrmValues[1]);
					branchMap.put(gpFrmValues[1], gpFrmValues[0]+","+gpFrmValues[2]);
				}
				Collections.sort(displayName);
				for (String name : displayName) {
					String[] formSplit = branchMap.get(name).split(ChirdlUtilConstants.GENERAL_INFO_COMMA);
					pw.write(XML_FORCE_PRINT_JIT_START);
					writeTag(XML_FORM_ID, formSplit[0], pw);
					writeTag(XML_DISPLAY_NAME, escapeXML(name), pw);
					pw.write(XML_OUTPUT_TYPE_START);
					pw.write(formSplit[1]);
					pw.write(XML_OUTPUT_TYPE_END);
					pw.write(XML_FORCE_PRINT_JIT_END);
				}
				pw.write(XML_GROUP_END);
			}
		}
		ageUnitsMinMap.clear();
		ageUnitsMaxMap.clear();
		formAttrValAgeMinMap.clear();
		formAttrValAgeMinUnitsMap.clear();
		formAttrValAgeMaxMap.clear();
		formAttrValAgeMaxUnitsMap.clear();
		
		pw.write(XML_FORCE_PRINT_JITS_END);
		
	}
	
	/**
	 * Retrieves a patient based on MRN.
	 * 
	 * @param mrn MRN used to find a patient.
	 * @return The patient or null if a patient cannot be found with the specified MRN.
	 */
	public static Patient getPatientByMRN(String mrn) {
		PatientService patientService = Context.getPatientService();
		Patient patient = null;
		String formattedMrn = Util.removeLeadingZeros(mrn);
		if (formattedMrn != null && !formattedMrn.contains("-") && formattedMrn.length() > 1) {
			formattedMrn = formattedMrn.substring(
				0, formattedMrn.length() - 1) + "-" + formattedMrn.substring(formattedMrn.length() - 1);
		}
		
		PatientIdentifierType identifierType = patientService
				.getPatientIdentifierTypeByName(ChirdlUtilConstants.IDENTIFIER_TYPE_MRN);
		List<PatientIdentifierType> identifierTypes = new ArrayList<>();
		identifierTypes.add(identifierType);
		List<Patient> patients = patientService.getPatientsByIdentifier(null, formattedMrn,
				identifierTypes,true); // CHICA-977 Use getPatientsByIdentifier() as a temporary solution to openmrs TRUNK-5089
		if (patients.size() == 0){
			patients = patientService.getPatientsByIdentifier(null, "0" + formattedMrn,
					identifierTypes,true); // CHICA-977 Use getPatientsByIdentifier() as a temporary solution to openmrs TRUNK-5089
		}

		if (patients.size() > 0) {
			patient = patients.get(0);
		}
		
		return patient;
	}
	
	/**
	 * Returns the latest session ID for a patient based on their latest encounter with a checkin state.
	 * 
	 * @param patientId The patient ID used to determine the session ID returned.
	 * @return session ID.
	 */
	public static Integer getEncounterSessionId(Integer patientId) {
		EncounterService encounterService = Context.getEncounterService();
		List<org.openmrs.Encounter> list = encounterService.getEncountersByPatientId(patientId);
		if (list != null && list.size() > 0) {
			Encounter encounter = list.get(0);
			ChirdlUtilBackportsService chirdlUtilBackportsService = Context
			        .getService(ChirdlUtilBackportsService.class);
			State checkinState = chirdlUtilBackportsService.getStateByName(ChirdlUtilConstants.STATE_CHECKIN);
			Integer encounterId = encounter.getEncounterId();
			List<PatientState> checkinStates = chirdlUtilBackportsService.getPatientStateByEncounterState(
			    encounterId, checkinState.getStateId());
			if (checkinStates != null && checkinStates.size() > 0) {
				PatientState patientState = checkinStates.get(0);
				return patientState.getSessionId();
			}
		}
		
		return null;
	}
	
	public static Map<Integer, String> getFormAttributeValues(ChirdlUtilBackportsService backportsService,
	        Integer attributeId, Integer locationId, Integer locationTagId) {
		Map<Integer, String> formToValueMap = new HashMap<>();
		List<FormAttributeValue> values = backportsService.getFormAttributeValues(attributeId, locationId, locationTagId);
		if (values == null) {
			return formToValueMap;
		}
		
		for (FormAttributeValue value : values) {
			String entry = value.getValue();
			if (entry != null && entry.length() > 0)
				formToValueMap.put(value.getFormId(), entry);
		}
		
		return formToValueMap;
	}
	
	/**
	 * Convert database units into units used by the Util.getAgeInUnits class.  This is extremely cheesy.
	 * 
	 * @param currentUnits The units to convert
	 * @return The units provided converted into the units expected by the Util.getAgeInUnits method.
	 */
	public static String convertUnits(String currentUnits) {
		if (currentUnits.compareToIgnoreCase(YEARS) == 0) {
			return Util.YEAR_ABBR;
		} else if (currentUnits.compareToIgnoreCase(MONTHS) == 0) {
			return Util.MONTH_ABBR;
		} else if (currentUnits.compareToIgnoreCase(DAYS) == 0) {
			return Util.DAY_ABBR;
		} else if (currentUnits.compareToIgnoreCase(WEEKS) == 0) {
			return Util.WEEK_ABBR;
		}
		
		return null;
	}
	
	/**
	 * Force prints a specified form for a patient..
	 * 
	 * @param request HttServletRequest
	 * @param response HttpServletResponse
	 * @throws IOException
	 */
	public static void forcePrintForms(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String patientIdString = request.getParameter(PARAM_PATIENT_ID);
		Integer patientId = null;
		if (StringUtils.isNotBlank(patientIdString)) {
			try {
				patientId = Integer.valueOf(patientIdString);
			}
			catch (Exception e) {
				String message = "Invalid patientId parameter provided: " + patientIdString;
				LOG.error(message, e);
				throw new IllegalArgumentException(message);
			}
		}
		
		String sessionIdString = request.getParameter(PARAM_SESSION_ID);
		Integer sessionId = null;
		try {
			sessionId = Integer.valueOf(sessionIdString);
		}
		catch (Exception e) {
			String message = "Invalid sessionId parameter provided: " + sessionIdString;
			LOG.error("Invalid sessionId parameter provided: {}", sessionIdString, e);
			throw new IllegalArgumentException(message);
		}
		
		User user = Context.getUserContext().getAuthenticatedUser();
		LocationService locationService = Context.getLocationService();
		Location location = null;
		String locationString = request.getParameter(PARAM_LOCATION_ID);
		Integer locationId = null;
		if (StringUtils.isBlank(locationString)) {
			locationString = user.getUserProperty(ChirdlUtilConstants.USER_PROPERTY_LOCATION);
			location = locationService.getLocation(locationString);
			if (location != null) {
				locationId = location.getLocationId();
			}
		} else {
			try {
				locationId = Integer.valueOf(locationString);
			} catch (NumberFormatException e) {
				String message = "Invalid locationId parameter: " + locationString;
				LOG.error(message, e);
				throw new IllegalArgumentException(message);
			}
		}
		
		String locationTags = request.getParameter(PARAM_LOCATION_TAG_ID);
		Integer locationTagId = null;
		if (locationTags != null && locationTags.trim().length() > 0) {
			try {
				locationTagId = Integer.valueOf(locationTags);
			} catch (NumberFormatException e) {
				String message = "Invalid locationTagId parameter: " + locationTags;
				LOG.error(message, e);
				throw new IllegalArgumentException(message);
			}
		} else {
			locationTags = user.getUserProperty(ChirdlUtilConstants.USER_PROPERTY_LOCATION_TAGS);
			if (location != null && locationTags != null) {
				StringTokenizer tokenizer = new StringTokenizer(locationTags, ChirdlUtilConstants.GENERAL_INFO_COMMA);
				while (tokenizer.hasMoreTokens()) {
					String locationTagName = tokenizer.nextToken();
					locationTagName = locationTagName.trim();
					Set<LocationTag> tags = location.getTags();
					for (LocationTag tag : tags) {
						if (tag.getName().equalsIgnoreCase(locationTagName)) {
							locationTagId = tag.getLocationTagId();
						}
					}
				}
			}
		}
		
		String mrn = request.getParameter(PARAM_MRN);
		forcePrintForms(request, response, patientId, sessionId, locationId, locationTagId, mrn);
	}
	
	/**
	 * Force prints a specified form for a patient..
	 * 
	 * @param request HttServletRequest
	 * @param response HttpServletResponse
	 * @param patientId Patient identifier
	 * @param sessionId Session identifier
	 * @param locationId Location identifier
	 * @param locationTagId Location tag identifier
	 * @param mrn Patient's medical record number
	 * @throws IOException
	 */
	public static void forcePrintForms(HttpServletRequest request, HttpServletResponse response, Integer patientId, 
			Integer sessionId, Integer locationId, Integer locationTagId, String mrn) throws IOException {	
		response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_XML);
		response.setHeader(
			ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL, ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL_NO_CACHE);
		PrintWriter pw = response.getWriter();
		pw.write(XML_FORCE_PRINT_JITS_START);
		
		String formIdsString = request.getParameter(PARAM_FORM_IDS);

		Integer foundPatientId = patientId;
		if (patientId == null) {
			if (StringUtils.isNotBlank(mrn)) {
				Patient patient = getPatientByMRN(mrn);
				if (patient != null) {
					foundPatientId = patient.getPatientId();
				}
			}
		}
		
		if (foundPatientId == null) {
			String message = "No valid patient could be located.";
			LOG.error(message);
			throw new IllegalArgumentException(message);
		}
		
		Integer foundSessionId = sessionId;
		if (sessionId == null) {
			foundSessionId = getEncounterSessionId(foundPatientId);
		}
		
		if (foundSessionId == null) {
			String message = "Could not find a valid sessionId for patient: " + foundPatientId;
			LOG.error(message);
			throw new IllegalArgumentException(message);
		}
		
		if (locationId == null) {
			String message = "Location not found: " + locationId;
			LOG.error(message);
			throw new IllegalArgumentException(message);
		}
		
		Map<String, Object> parameters = new HashMap<>();
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		FormService formService = Context.getFormService();
		
		parameters = new HashMap<>();
		parameters.put(PARAM_SESSION_ID, foundSessionId);
		parameters.put(PARAM_LOCATION_TAG_ID, locationTagId);
		FormInstance formInstance = new FormInstance();
		formInstance.setLocationId(locationId);
		parameters.put(PARAM_FORM_INSTANCE, formInstance);

		if (formIdsString == null || formIdsString.trim().length() == 0) {
			String message = "formIdsString is null or empty";
			LOG.error(message);
			throw new IllegalArgumentException(message);
		}
		
		List<String> errorList = new ArrayList<>();
		String[] formIds = formIdsString.split(ChirdlUtilConstants.GENERAL_INFO_COMMA);
		LogicService logicService = Context.getLogicService();
		for (String formIdStr : formIds) {
			// print the form
			formIdStr = formIdStr.trim();
			Integer formId = null;
			try {
				formId = Integer.valueOf(formIdStr);
			} catch (Exception e) {
				String message = "Invalid formId parameter: " + formIdStr;
				LOG.error(message, e);
				continue;
			}
			
			Form form = formService.getForm(formId);
			if (form == null) {
				String message = "No form found for formId: " + formIdStr;
				LOG.error(message);
				continue;
			}
			
			String formName = form.getName();
			parameters.put(ChirdlUtilConstants.PARAMETER_1, formName);
			parameters.put(ChirdlUtilConstants.PARAMETER_2, ChirdlUtilConstants.FORM_INST_ATTR_VAL_FORCE_PRINT);
			parameters.put(ChirdlUtilConstants.PARAMETER_3, ChirdlUtilConstants.GENERAL_INFO_FALSE);
			parameters.put(ChirdlUtilConstants.PARAMETER_4, ChirdlUtilConstants.GENERAL_INFO_TRUE);
			parameters.put(ChirdlUtilConstants.PARAMETER_5, ChirdlUtilConstants.GENERAL_INFO_FALSE);
			Result result = logicService.eval(foundPatientId, ChirdlUtilConstants.RULE_CREATE_JIT, parameters);
			
			// Check the output type
			FormAttributeValue fav = chirdlutilbackportsService.getFormAttributeValue(
				formId, ChirdlUtilConstants.FORM_ATTR_OUTPUT_TYPE, locationTagId, locationId);
			String[] outputTypes = null;
			if (fav == null || fav.getValue() == null || fav.getValue().trim().length() == 0) {
				outputTypes = new String[] {Context.getAdministrationService().getGlobalProperty(
					ChirdlUtilConstants.GLOBAL_PROP_DEFAULT_OUTPUT_TYPE)};
			} else {
				outputTypes = fav.getValue().split(ChirdlUtilConstants.GENERAL_INFO_COMMA);
			}
			
			String formInstanceTag = result.toString();
			
			for (String outputType : outputTypes) {
				outputType = outputType.trim();
				if (ChirdlUtilConstants.FORM_ATTR_VAL_PDF.equalsIgnoreCase(outputType) || 
						ChirdlUtilConstants.FORM_ATTR_VAL_TELEFORM_PDF.equalsIgnoreCase(outputType) ||
						    ChirdlUtilConstants.FORM_ATTR_VAL_TELEFORM_XML.equalsIgnoreCase(outputType)) {
					pw.write(XML_FORCE_PRINT_JIT_START);
					writeTag(XML_FORM_INSTANCE_TAG, formInstanceTag, pw);
					writeTag(XML_FORM_NAME, escapeXML(formName), pw);
					writeTag(XML_OUTPUT_TYPE, outputType, pw);
					pw.write(XML_FORCE_PRINT_JIT_END);
				} else {
					formName = getFormName(formId, locationId, locationTagId);
					String message = "Invalid outputType attribute '" + outputType + "' found for form: " + formName;
					LOG.error(message);
					errorList.add(message);
				}
			}
			
			if (errorList.isEmpty()) {
				pw.write(XML_ERROR_MESSAGES_START);
				for (String error : errorList) {
					writeTag(XML_ERROR_MESSAGE, error, pw);
				}
				
				pw.write(XML_ERROR_MESSAGES_END);
			}
		}
		
		pw.write(XML_FORCE_PRINT_JITS_END);
	}
	
	/**
     * Saves a draft of a form. Text written to the response will indicate if the procedure completed
     * successfully.  The procedure executed normally if "success" is returned in the response.  
     * A message containing an error message to display to the client will be returned otherwise.
     * 
     * @param request The request from the client
     * @param response The response that will be sent back to the client
     * @throws IOException
     */
    public static void saveFormDraft(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	String formInstance = request.getParameter(ChirdlUtilConstants.PARAMETER_FORM_INSTANCE);
    	saveFormDraft(request, response, formInstance);
    }
	
	/**
     * Saves a draft of a form. Text written to the response will indicate if the procedure completed
     * successfully.  The procedure executed normally if "success" is returned in the response.  
     * A message containing an error message to display to the client will be returned otherwise.
     * 
     * @param request The request from the client
     * @param response The response that will be sent back to the client
     * @param formInstance The form instance of the form to be saved
     * @throws IOException
     */
    public static void saveFormDraft(HttpServletRequest request, HttpServletResponse response, String formInstance) 
    		throws IOException {
    	response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_HTML);
		response.setHeader(
			ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL, ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL_NO_CACHE);
		
    	PrintWriter pw = response.getWriter();
    	Integer formId = null;
		
		//parse out the location_id,form_id,location_tag_id, and form_instance_id
		//from the selected form
		FormInstanceTag formInstTag = null;
		if (formInstance != null && formInstance.trim().length() > 0) {
			formInstTag = FormInstanceTag.parseFormInstanceTag(formInstance);
			formId = formInstTag.getFormId();
		} else {
			String messagePart1 = "Error saving form draft: form instance tag parameter not found.";
			String messagePart2 = "Please contact support.";
			ServletUtil.writeHtmlErrorMessage(pw, null, LOG, messagePart1, messagePart2);
    		return;
		}
		
		FormService formService = Context.getFormService();
		Form form = formService.getForm(formId);
		
		ATDService atdService = Context.getService(ATDService.class);
		Records records = null;
		try {
			records = atdService.getFormRecords(formInstTag);
		} catch (Exception e) {
			String messagePart1 = "Error saving form draft: unable to load form from disk.";
			String messagePart2 = "Please contact support with the following information: Form ID: " + formId + 
					" Form Instance ID: " + formInstTag.getFormInstanceId() + " Location ID: " + 
					formInstTag.getLocationId() + " Location Tag ID: " + formInstTag.getLocationTagId();
			ServletUtil.writeHtmlErrorMessage(pw, e, LOG, messagePart1, messagePart2);
    		return;
		}
		
		if (records == null) {
			String messagePart1 = "Error saving form draft: unable to load form from disk.";
    		String messagePart2 = "Please contact support with the following information: Form ID: " + formId + 
					" Form Instance ID: " + formInstTag.getFormInstanceId() + " Location ID: " + 
					formInstTag.getLocationId() + " Location Tag ID: " + formInstTag.getLocationTagId();
    		ServletUtil.writeHtmlErrorMessage(pw, null, LOG, messagePart1, messagePart2);
    		return;
		}
		
		Record record = records.getRecord();
		if (record == null) {
			String messagePart1 = "Error saving form draft: unable to load form from disk.";
    		String messagePart2 = "Please contact support with the following information: Form ID: " + formId + 
					" Form Instance ID: " + formInstTag.getFormInstanceId() + " Location ID: " + 
					formInstTag.getLocationId() + " Location Tag ID: " + formInstTag.getLocationTagId();
    		ServletUtil.writeHtmlErrorMessage(pw, null, LOG, messagePart1, messagePart2);
    		return;
		}
		
		Map<String, org.openmrs.module.atd.xmlBeans.Field> recordFieldMap = org.openmrs.module.atd.util.Util.createRecordFieldMap(records);
		Set<FormField> formFields = form.getFormFields();
		TeleformTranslator translator = new TeleformTranslator();
		FieldType exportFieldType = translator.getFieldType(ChirdlUtilConstants.FORM_FIELD_TYPE_EXPORT);
		for (FormField formField : formFields) {
			Field currField = formField.getField();
			FieldType fieldType = currField.getFieldType();
			if (fieldType != null && fieldType.equals(exportFieldType)) {
				String fieldName = currField.getName();
				org.openmrs.module.atd.xmlBeans.Field recordField = recordFieldMap.get(fieldName);
				String value = request.getParameter(fieldName);
				if (recordField != null) {
					recordField.setValue(value);
				} else {
					org.openmrs.module.atd.xmlBeans.Field field = new org.openmrs.module.atd.xmlBeans.Field();
					field.setId(fieldName);
					field.setValue(value);
					record.addField(field);
				}
			}
		}
		
		try {
			atdService.saveFormRecordsDraft(formInstTag, records);
		} catch (Exception e) {
			String messagePart1 = "Error saving form draft: unable to load form from disk.";
			String messagePart2 = "Please contact support with the following information: Form ID: " + formId + 
					" Form Instance ID: " + formInstTag.getFormInstanceId() + " Location ID: " + 
					formInstTag.getLocationId() + " Location Tag ID: " + formInstTag.getLocationTagId();
			ServletUtil.writeHtmlErrorMessage(pw, e, LOG, messagePart1, messagePart2);
    		return;
		}
		
		pw.write(RESULT_SUCCESS);
    }
    
    /**
	 * Provides the necessary information for the servlet header for force printing a form.
	 * 
	 * @param request HttServletRequest
	 * @param response HttpServletResponse
	 * @throws IOException
	 */
	public static void getForcePrintFormHeader(HttpServletRequest request, HttpServletResponse response) 
			throws IOException {
		String formIdsStr = request.getParameter(ServletUtil.PARAM_FORM_IDS);
		String locationIdStr = request.getParameter(ServletUtil.PARAM_LOCATION_ID);
		String locationTagIdStr = request.getParameter(ServletUtil.PARAM_LOCATION_TAG_ID);
		Integer formId = null;
		Integer locationId = null;
		Integer locationTagId = null;
		
		if (formIdsStr == null) {
			LOG.error("Invalid argument formId: {}",formIdsStr);
			response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_HTML);
			response.getWriter().write("Invalid argument formId: " + formIdsStr);
			return;
		}
		
		List<String> errorList = new ArrayList<>();
		String[] formIds = formIdsStr.split(ChirdlUtilConstants.GENERAL_INFO_COMMA);
		for (String formIdStr : formIds) {
			try {
				formId = Integer.valueOf(formIdStr);
			} catch (NumberFormatException e) {
				LOG.error("Invalid argument formId: {}", formIdStr, e);
				errorList.add(formIdStr);
				continue;
			}
			
			try {
				locationId = Integer.valueOf(locationIdStr);
			} catch (NumberFormatException e) {
				// DWE CHICA-576 Add some additional logging
				StringBuilder errorMsg = new StringBuilder();
				errorMsg.append("Invalid argument locationId: " + locationIdStr);
				if(formId != null)
				{
					errorMsg.append(" formId: ")
					.append(formId);
				}
				if(locationTagIdStr != null)
				{
					errorMsg.append(" locationTagId: ")
					.append(locationTagIdStr);
				}
				LOG.error(errorMsg.toString(), e);
				errorList.add(formIdStr);
				continue;
			}
			
			try {
				locationTagId = Integer.valueOf(locationTagIdStr);
			} catch (NumberFormatException e) {
				LOG.error("Invalid argument locationTagId: " + locationTagIdStr, e);
				errorList.add(formIdStr);
				continue;
			}
			
			ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
			FormAttributeValue fav = chirdlutilbackportsService.getFormAttributeValue(
				formId, ChirdlUtilConstants.FORM_ATTR_OUTPUT_TYPE, locationTagId, locationId);
			String[] outputTypes = null;
			if (fav == null || fav.getValue() == null || fav.getValue().trim().length() == 0) {
				outputTypes = new String[] {Context.getAdministrationService().getGlobalProperty(
					ChirdlUtilConstants.GLOBAL_PROP_DEFAULT_OUTPUT_TYPE)};
			} else {
				outputTypes = fav.getValue().split(ChirdlUtilConstants.GENERAL_INFO_COMMA);
			}
			
			// if there's at least one PDF type, return PDF as the content type.
			for (String outputType : outputTypes) {
				outputType = outputType.trim();
				if (ChirdlUtilConstants.FORM_ATTR_VAL_PDF.equalsIgnoreCase(outputType) || 
						ChirdlUtilConstants.FORM_ATTR_VAL_TELEFORM_PDF.equals(outputType)) {
					response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_APPLICATION_PDF);
					return;
				}
			}
		}
		
		response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_HTML);
		if (!errorList.isEmpty()) {
			response.getWriter().write("There were errors encountered processing form(s).");
		}
	}
	
	/**
	 * Retrieves prioritized elements for a form.
	 * 
	 * @param formId The form identifier
	 * @param formInstanceId The form instance identifier
	 * @param encounterId The encounter identifier
	 * @param maxElements The maximum number of elements to provide
	 * @param request HttServletRequest
	 * @param response HttpServletResponse
	 * @throws IOException
	 */
	public static void getPrioritizedElements(Integer formId, Integer formInstanceId, Integer encounterId, 
			Integer maxElements, HttpServletResponse response) throws IOException {
		DynamicFormAccess formAccess = new DynamicFormAccess();
		List<org.openmrs.module.atd.xmlBeans.Field> fields = 
				formAccess.getPrioritizedElements(formId, formInstanceId, encounterId, maxElements);
		
		response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_XML);
		response.setHeader(
			ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL, ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL_NO_CACHE);
		PrintWriter pw = response.getWriter();
		pw.write(XML_RECORDS_START);
		pw.write(XML_RECORD_START);
		for(org.openmrs.module.atd.xmlBeans.Field field : fields){
			pw.write(ChirdlUtilConstants.XML_START_TAG + XML_FIELD + " " + XML_ID + "=\"" + field.getId() + "\"" + 
					ChirdlUtilConstants.XML_END_TAG);
			writeTag(XML_VALUE, ServletUtil.escapeXML(field.getValue()), pw);
			pw.write(XML_FIELD_END);
		}
		
		pw.write(XML_RECORD_END);
		pw.write(XML_RECORDS_END);
	}
	
	/**
	 * Retrieves prioritized elements for a form.
	 * 
	 * @param request HttServletRequest
	 * @param response HttpServletResponse
	 * @throws IOException
	 */
	public static void getPrioritizedElements(HttpServletRequest request, HttpServletResponse response) 
			throws IOException {
		Integer formId = Integer.valueOf(request.getParameter(ChirdlUtilConstants.PARAMETER_FORM_ID));
		Integer formInstanceId = Integer.valueOf(request.getParameter(ChirdlUtilConstants.PARAMETER_FORM_INSTANCE_ID));
		Integer encounterId = Integer.valueOf(request.getParameter(ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID));
		Integer maxElements = Integer.valueOf(request.getParameter(PARAM_MAX_ELEMENTS));
		
		getPrioritizedElements(formId, formInstanceId, encounterId, maxElements, response);
	}
	
	/**
	 * Saves a form's export elements to the database.
	 * 
	 * @param request HttServletRequest
	 * @param response HttpServletResponse
	 * @param patientId The patient identifier
	 * @param encounterId The encounter identifier
	 * @param formInstance The form instance information needed to locate the form instance
	 * @throws IOException
	 */
    @SuppressWarnings("unchecked")
	public static void saveExportElements(HttpServletRequest request, HttpServletResponse response, Integer patientId, 
    		Integer encounterId, FormInstanceTag formInstance) throws IOException {
		response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_XML);
		response.setHeader(
			ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL, ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL_NO_CACHE);
		PrintWriter pw = response.getWriter();
		pw.write(XML_SAVE_RESULT_START);
		try {
			ParameterHandler parameterHandler = new ChicaParameterHandler();
			DynamicFormAccess formAccess = new DynamicFormAccess();
			Patient patient = Context.getPatientService().getPatient(patientId);
			Map<String, String[]> parameterMap = request.getParameterMap();
			formAccess.saveExportElements(new FormInstance(formInstance.getLocationId(), formInstance.getFormId(), 
				formInstance.getFormInstanceId()), formInstance.getLocationTagId(), encounterId, patient, parameterMap, 
				parameterHandler);
			ServletUtil.writeTag(XML_RESULT, ChirdlUtilConstants.FORM_ATTR_VAL_TRUE, pw);
		} catch (Exception e) {
		    LOG.error("Error saving prioritized elements", e);
			ServletUtil.writeTag(XML_RESULT, ChirdlUtilConstants.FORM_ATTR_VAL_FALSE, pw);
		}
		
		pw.write(XML_SAVE_RESULT_END);
	}
	
	/**
	 * Saves a form's export elements to the database.
	 * 
	 * @param request HttServletRequest
	 * @param response HttpServletResponse
	 * @throws IOException
	 */
	public static void saveExportElements(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Integer patientId = Integer.valueOf(request.getParameter(PARAM_PATIENT_ID));
		Integer formId = Integer.valueOf(request.getParameter(ChirdlUtilConstants.PARAMETER_FORM_ID));
		Integer formInstanceId = Integer.valueOf(request.getParameter(ChirdlUtilConstants.PARAMETER_FORM_INSTANCE_ID));
		Integer locationId = Integer.valueOf(request.getParameter(PARAM_LOCATION_ID));
		Integer locationTagId = Integer.valueOf(request.getParameter(PARAM_LOCATION_TAG_ID));
		Integer encounterId = Integer.valueOf(request.getParameter(ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID));
		
		FormInstanceTag formInstance = new FormInstanceTag(locationId, formId, formInstanceId, locationTagId);
		saveExportElements(request, response, patientId, encounterId, formInstance);
	}
	
	/**
     * Saves who completes the PSF form.
     * 
     * @param request HttServletRequest
     * @param locationId The location identifier
     * @param patientId The patient identifier
     * @param encounterId The encounter identifier
     */
    public static void savePSFCompletedBy(HttpServletRequest request, Integer locationId, Integer patientId, Integer encounterId) {
        String completedby = request.getParameter(ChirdlUtilConstants.PARAMETER_COMPLETED_BY);
        String ageStr = request.getParameter(ChirdlUtilConstants.PARAMETER_PATIENT_AGE);
        if (StringUtils.isNotBlank(ageStr)) {
            int age = 0;
            try {
                age = Integer.parseInt(ageStr);
            } catch (NumberFormatException e) {
                LOG.error("Error formatting age: " + ageStr, e); 
            }
            if (age < 12) {
                completedby = ChirdlUtilConstants.PARAMETER_CAREGIVER;
            }
        }
        
        Obs obs = new Obs();
        ConceptService conceptService = Context.getConceptService();
        Concept concept = conceptService.getConceptByName(ChirdlUtilConstants.PARAMETER_SCREENER_COMPLETED_BY);
        Concept answer = null;
        if (StringUtils.isNotBlank(completedby)) {
            answer = conceptService.getConceptByName(completedby);
        }
        if (concept == null || answer == null) {
            LOG.error("Could not save the concept: " + ChirdlUtilConstants.PARAMETER_SCREENER_COMPLETED_BY + 
                " and answer: "  + completedby);
        } else {
            obs.setConcept(concept);
            obs.setLocation(Context.getLocationService().getLocation(locationId));
            obs.setObsDatetime(new Date());
            obs.setPerson(Context.getPatientService().getPatient(patientId));
            obs.setEncounter(Context.getEncounterService().getEncounter(encounterId));
            obs.setValueCoded(answer);
            try {
                Context.getObsService().saveObs(obs, null);
            } catch (APIException e) {
                LOG.error("Error saving answer " + answer.getConceptId() + " for concept " + concept.getConceptId(), e); 
            }
        }
    }
    
    /**
	 * Retrieves the patient's JITs based on form instances provided.
	 * 
	 * @param request HttServletRequest
	 * @param response HttpServletResponse
	 * @throws IOException
	 */
	public static void getPatientJITs(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String formInstances = request.getParameter(ServletUtil.PARAM_FORM_INSTANCES);
		String formName = request.getParameter(ChirdlUtilConstants.PARAMETER_FORM_NAME);
		locatePatientJITs(response, formInstances, formName);
	}
	
	/**
	 * Locates and loads the patient JITs based on the formInstances provided.
	 * 
	 * @param response HttpServletResponse
	 * @param formInstances The form instances to load.  This should be a comma delimited list.
	 * @throws IOException
	 */
	public static void locatePatientJITs(HttpServletResponse response, String formInstances, String formName) 
			throws IOException {	
		response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_APPLICATION_PDF);
		String contentDispositionPdf = "inline;filename=" + formName.replaceAll("\\s+", "") + ChirdlUtilConstants.FILE_EXTENSION_PDF;
		response.addHeader(ChirdlUtilConstants.HTTP_HEADER_CONTENT_DISPOSITION, contentDispositionPdf);
		response.addHeader(ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL, ChirdlUtilConstants.HTTP_CACHE_CONTROL_PUBLIC + ", " + 
				ChirdlUtilConstants.HTTP_CACHE_CONTROL_MAX_AGE + "=" + ServletUtil.MAX_CACHE_AGE);
		
		if (formInstances == null) {
			try {
	            writePdfTextToResponse("There are no forms to display.", response);
            }
            catch (DocumentException e) {
                LOG.error("Error creating error message PDF", e);
	            return;
            }
			
			return;
		}
		
		ChirdlUtilBackportsService backportsService = Context.getService(ChirdlUtilBackportsService.class);
		List<String> pdfFiles = new ArrayList<>();
		List<FormInstanceTag> teleformFiles = new ArrayList<>();
		List<FormInstanceTag> failedFiles = new ArrayList<>();
		for (String formInstance : formInstances.split(ChirdlUtilConstants.GENERAL_INFO_COMMA)) {
			FormInstanceTag formInstanceTag = FormInstanceTag.parseFormInstanceTag(formInstance);
			if (formInstanceTag == null) {
				continue;
			}
			
			File mergeFile = locatePatientJIT(formInstanceTag, backportsService);
			if (mergeFile == null) {
				failedFiles.add(formInstanceTag);
			} else if (mergeFile.getAbsolutePath().toLowerCase().endsWith(ChirdlUtilConstants.FILE_PDF)) {
				pdfFiles.add(mergeFile.getAbsolutePath());
			} else {
				teleformFiles.add(formInstanceTag);
			}
		}
		
		int pdfListSize = pdfFiles.size();
		if (pdfListSize == 0 && teleformFiles.size() > 0) {
			loadPatientTeleformJITs(response, teleformFiles);
			return;
		} else if (pdfListSize == 0 && failedFiles.size() > 0) {
			loadPatientErrorJITs(response, failedFiles);
			return;
		} else if (pdfListSize == 0) {
			String message = "An error occurred locating the file(s) to display.";
			try {
	            writePdfTextToResponse(message, response);
            }
            catch (DocumentException e) {
                LOG.error("Error creating error PDF document", e);
            }
			
			return;
		} 
		
		loadPatientPdfJITs(response, pdfFiles);
	}
	
	/**
	 * Creates a new PDF with the provided text and writes it to the HttpServletResponse object
	 * 
	 * @param text The text to insert into the PDF
	 * @param response The HttpServletResponse object where the PDF will be written
	 * @throws DocumentException
	 * @throws IOException
	 */
	public static void writePdfTextToResponse(String text, HttpServletResponse response) 
			throws DocumentException, IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		Document document = new Document();
		try {
	        PdfWriter.getInstance(document, output);
	        document.open();
	        document.add(new Paragraph(text));
	        document.close();
		    response.setContentLength(output.size());
		    response.getOutputStream().write(output.toByteArray());
		} finally {
			output.flush();
		    output.close();
		}
	}
	
	/**
	 * Locates the merge file for the JIT whether it be PDF or Teleform XML.
	 * 
	 * @param formInstanceTag The form instance tag information needed to locate the merge file.
	 * @param backportsService ChirdlUtilBackportsService object used to find the form's merge directory.
	 * @return File containing the merge file location or null if it could not be found.
	 */
	public static File locatePatientJIT(FormInstanceTag formInstanceTag, ChirdlUtilBackportsService backportsService) {
		Integer locationId = formInstanceTag.getLocationId();
		Integer formId = formInstanceTag.getFormId();
		Integer formInstanceId = formInstanceTag.getFormInstanceId();
		Integer locationTagId = formInstanceTag.getLocationTagId();
		
		// Get the merge directory for the form.
		FormAttributeValue fav = 
				backportsService.getFormAttributeValue(formId, ChirdlUtilConstants.FORM_ATTR_DEFAULT_MERGE_DIRECTORY, 
					locationTagId, locationId);
		if (fav == null || fav.getValue() == null || fav.getValue().trim().length() == 0) {
			return null;
		}
		
		// Attempt to find the merge PDF file.
		String mergeDirectory = fav.getValue();
		File pdfDir = new File(mergeDirectory, ChirdlUtilConstants.FILE_PDF);
		File mergeFile = new File(pdfDir, locationId + ChirdlUtilConstants.GENERAL_INFO_UNDERSCORE + formId + 
			ChirdlUtilConstants.GENERAL_INFO_UNDERSCORE + formInstanceId + ChirdlUtilConstants.FILE_EXTENSION_PDF);
		if (!mergeFile.exists() || mergeFile.length() == 0) {
			mergeFile = new File(pdfDir, ChirdlUtilConstants.GENERAL_INFO_UNDERSCORE + locationId + 
				ChirdlUtilConstants.GENERAL_INFO_UNDERSCORE + formId + ChirdlUtilConstants.GENERAL_INFO_UNDERSCORE + 
				formInstanceId + ChirdlUtilConstants.GENERAL_INFO_UNDERSCORE + ChirdlUtilConstants.FILE_EXTENSION_PDF);
			if (mergeFile.exists() && mergeFile.length() > 0) {
				return mergeFile;
			}
		} else {
			return mergeFile;
		}
		
		// Attempt to find the XML file.
		mergeFile = XMLUtil.findMergeXmlFile(mergeDirectory, locationId, formId, formInstanceId);
		if (mergeFile != null && mergeFile.exists() && mergeFile.length() > 0) {
			return mergeFile;
		}
		
		return null;
	}
	
	/**
	 * Creates a PDF with an informational message about the Teleform files that were processed 
	 * and writes it to the HTTP response.
	 * 
	 * @param response The HttpServletResponse where the PDF will be written.
	 * @param teleformFiles List of FormInstanceTag objects that used for Teleform.
	 * @throws IOException
	 */
	public static void loadPatientTeleformJITs(HttpServletResponse response, List<FormInstanceTag> teleformFiles) 
			throws IOException {
		String subject = "form";
		String verb = "has";
		if (teleformFiles.size() > 1) {
			subject = "forms";
			verb = "have";
		}
		
		StringBuilder message = new StringBuilder("The following ").append(subject).append(" ").append(verb).append(
			" been successfully sent to the printer: ");
		for (int i = 0; i < teleformFiles.size(); i++) {
			FormInstanceTag formInstanceTag = teleformFiles.get(i);
			if (i != 0) {
				message.append(", ");
			}
			
			String formName = ServletUtil.getFormName(
				formInstanceTag.getFormId(), formInstanceTag.getLocationId(), formInstanceTag.getLocationTagId());
			message.append(formName);
		}
		
		message.append(".");
		
		// CHICA-962
		message.append(ChirdlUtilConstants.GENERAL_INFO_CARRIAGE_RETURN_LINE_FEED)
		.append(ChirdlUtilConstants.GENERAL_INFO_CARRIAGE_RETURN_LINE_FEED)
		.append("Please pick the ").append(subject).append(" up at the printer. DO NOT print this page.");
		
		try {
            writePdfTextToResponse(message.toString(), response);
        }
        catch (DocumentException e) {
            LOG.error("Error creating error PDF document", e);
        }
	}
	
	/**
	 * Creates a PDF with an error message for the forms the failed and writes it to the HTTP response.
	 * 
	 * @param response The HttpServletResponse where the PDF will be written.
	 * @param errorFiles List of FormInstanceTag objects that failed.
	 * @throws IOException
	 */
	public static void loadPatientErrorJITs(HttpServletResponse response, List<FormInstanceTag> errorFiles) 
			throws IOException {
		String subject = "form";
		if (errorFiles.size() > 1) {
			subject = "forms";
		}
		
		StringBuffer message = new StringBuffer("An error occurred creating the following ").append(subject).append(": ");
		for (int i = 0; i < errorFiles.size(); i++) {
			FormInstanceTag formInstanceTag = errorFiles.get(i);
			if (i != 0) {
				message.append(", ");
			}
			
			String formName = ServletUtil.getFormName(
				formInstanceTag.getFormId(), formInstanceTag.getLocationId(), formInstanceTag.getLocationTagId());
			message.append(formName);
		}
		
		message.append(".");
		try {
            writePdfTextToResponse(message.toString(), response);
        }
        catch (DocumentException e) {
            LOG.error("Error creating error PDF document", e);
        }
	}
	
	/**
	 * Combines the provided PDF files into one PDF document and writes it to the HTTP response.
	 * 
	 * @param response The HttpServletResponse where the PDF will be written.
	 * @param pdfFiles List of files locations for PDF forms to combine.
	 * @throws IOException
	 */
	public static void loadPatientPdfJITs(HttpServletResponse response, List<String> pdfFiles) throws IOException {
		if (pdfFiles.size() == 1) {
			String filePath = null;
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			try {
				Document document = new Document();
		        PdfCopy copy = new PdfCopy(document, output);
		        document.open();
		        PdfReader reader;
		        int n;
	        	filePath = pdfFiles.get(0);
	            reader = new PdfReader(filePath);
	            // loop over the pages in that document
	            n = reader.getNumberOfPages();
	            for (int page = 0; page < n; ) {
	            	try {
	            		copy.addPage(copy.getImportedPage(reader, ++page));
	            	} catch (Exception e) {
	            		LOG.error("Error adding page", e);
	            	}
	            }
	            
	            copy.freeReader(reader);
	            reader.close();
	
		        document.close();
		        copy.close();
		        response.setContentLength(output.size());
		        response.getOutputStream().write(output.toByteArray());
			} catch (BadPdfFormatException e) {
				LOG.error("Bad PDF found: " + filePath, e);
				throw new IOException(e);
			} catch (DocumentException e) {
				LOG.error("Error handling PDF document: " + filePath, e);
				throw new IOException(e);
			} finally {
				output.flush();
		        output.close();
			}
		} else {
			// DWE CHICA-500 Allow multiple PDFs to be selected/combined into 
			// a single document for printing. If the document has an odd number of pages,
			// a blank page will be added so that the next document will not be printed on the back of the 
			// previous document when printing duplex
			String filePath = "";
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				Document doc = new Document();
		        PdfCopy copy = new PdfCopy(doc, baos);
		        doc.open();
		        PdfReader reader = null;
		        
		        for (int i = 0; i < pdfFiles.size(); i++) {
		        	filePath = pdfFiles.get(i);
		        	reader = new PdfReader(renamePdfFields(filePath, i));
		        	
		        	// Loop over the pages in the document
		            int numOfPages = reader.getNumberOfPages();
		            
		            for (int page = 1; page <= numOfPages; page++) {
		            	try {		            		
		            		// When forms are combined, we need to check to see if the view is landscape
		            		// If it is, we need to rotate it otherwise Firefox will shrink the pages to fit in portrait
		            		// which causes pages that should be printed in portrait to be shrunk as well
		            		int rot = reader.getPageRotation(page);
		            		if(rot == 90 || rot == 270)
		            		{
		            			PdfDictionary pageDict = reader.getPageN(page);
			            		pageDict.put(PdfName.ROTATE, new PdfNumber(0));
		            		}
		            					            	
		                copy.addPage(copy.getImportedPage(reader, page));
		            	} catch (Exception e) {
		            		LOG.error("Error adding page", e);
		            	}
		            }
		            
		            // Add blank page if the document has an odd number of pages
		            if(numOfPages % 2 != 0)
		            {		            	
		            	copy.addPage(reader.getPageSize(1), reader.getPageRotation(1));
		            }
		        }
		        		        
		        copy.freeReader(reader);
	            reader.close();
		        doc.close();		        
		        copy.close();
		       
		        response.setContentLength(baos.size());
		        response.getOutputStream().write(baos.toByteArray());
			} catch (BadPdfFormatException e) {
				LOG.error("Bad PDF found: " + filePath, e);
				throw new IOException(e);
			} catch (DocumentException e) {
				LOG.error("Error handling PDF document", e);
				throw new IOException(e);
			} finally {
				baos.flush();
		        baos.close();
			}
		}
	}
	
	/**
	 * Renames the fields in the PDF.  This needs to be done due to fields with the same name in the document.
	 * 
	 * @param pdfFile The PDF file that will have its fields renamed.
	 * @param instance The instance of the field in the PDF document.
	 * @return Array of bytes of the PDF document after the fields are renamed.
	 * @throws IOException
	 * @throws DocumentException
	 */
	public static byte[] renamePdfFields(String pdfFile, int instance) throws IOException, DocumentException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// Create the stamper
		PdfStamper stamper = new PdfStamper(new PdfReader(pdfFile), baos);
		// Get the fields
		AcroFields form = stamper.getAcroFields();
		// Loop over the fields
		Set<String> keys = new HashSet<>(form.getFields().keySet());
		for (String key : keys) {
			// rename the fields
			form.renameField(key, String.format("%s_%d", key, instance));
		}
		// close the stamper
		stamper.close();
		return baos.toByteArray();
	}
}
