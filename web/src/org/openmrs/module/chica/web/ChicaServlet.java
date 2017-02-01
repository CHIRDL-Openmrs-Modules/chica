package org.openmrs.module.chica.web;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Field;
import org.openmrs.FieldType;
import org.openmrs.Form;
import org.openmrs.FormField;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.User;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.result.Result;
import org.openmrs.module.atd.TeleformTranslator;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.atd.xmlBeans.Record;
import org.openmrs.module.atd.xmlBeans.Records;
import org.openmrs.module.chica.util.PatientRow;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutil.util.XMLUtil;
import org.openmrs.module.chirdlutilbackports.cache.ApplicationCacheManager;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormAttribute;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstanceTag;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

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
 * Servlet giving access to CHICA information.
 *
 * @author Steve McKee
 */
public class ChicaServlet extends HttpServlet {
	
	private static final String WEEKS = "weeks";
	private static final String DAYS = "days";
	private static final String MONTHS = "months";
	private static final String YEARS = "years";

	private static final long serialVersionUID = 1L;
	
	private static final String IS_AUTHENTICATED = "isAuthenticated";
	private static final String AUTHENTICATE_USER = "authenticateUser";
	private static final String GET_PATIENT_JITS = "getPatientJITs";
	private static final String GET_AVAILABLE_PATIENT_JITS = "getAvailablePatientJITs";
	private static final String GET_FORCE_PRINT_FORMS = "getForcePrintForms";
	private static final String FORCE_PRINT_FORMS = "forcePrintForms";
	private static final String GET_GREASEBOARD_PATIENTS = "getGreaseboardPatients";
	private static final String VERIFY_MRN = "verifyMRN";
	private static final String GET_MANUAL_CHECKIN = "getManualCheckin";
	private static final String SAVE_MANUAL_CHECKIN = "saveManualCheckin";
	private static final String SEND_PAGE_REQUEST = "sendPageRequest";
	private static final String DISPLAY_FORCE_PRINT_FORMS = "displayForcePrintForms";
	private static final String KEEP_ALIVE = "keepAlive";
	private static final String CLEAR_CACHE = "clearCache";
	private static final String SAVE_FORM_DRAFT = "saveFormDraft";
	
	private static final String PARAM_ACTION = "action";
	private static final String PARAM_ENCOUNTER_ID = "encounterId";
	private static final String PARAM_SESSION_ID = "sessionId";
	private static final String PARAM_FORM_IDS = "formIds";
	private static final String PARAM_LOCATION_ID = "locationId";
	private static final String PARAM_LOCATION_TAG_ID = "locationTagId";
	private static final String PARAM_FORM_INSTANCES = "formInstances";
	private static final String PARAM_FORM_INSTANCE = "formInstance";
	private static final String PARAM_PATIENT_ID = "patientId";
	private static final String PARAM_MRN = "mrn";
	private static final String PARAM_PATIENT_ROWS = "patientRows";
	private static final String PARAM_NEED_VITALS = "needVitals";
	private static final String PARAM_WAITING_FOR_MD = "waitingForMD";
	private static final String PARAM_BAD_SCANS = "badScans";
	private static final String PARAM_CACHE_NAME = "cacheName";
	private static final String PARAM_CACHE_KEY_TYPE = "cacheKeyType";
	private static final String PARAM_CACHE_VALUE_TYPE = "cacheValueType";
	private static final String PARAM_PROVIDER_ID = "providerId";
	
	private static final String RESULT_SUCCESS = "success";
	
	private static final String XML_AVAILABLE_JITS_START = "<availableJITs>";
	private static final String XML_AVAILABLE_JITS_END = "</availableJITs>";
	private static final String XML_AVAILABLE_JIT_START = "<availableJIT>";
	private static final String XML_AVAILABLE_JIT_END = "</availableJIT>";
	private static final String XML_FORM_NAME = "formName";
	private static final String XML_FORM_ID = "formId";
	private static final String XML_FORM_INSTANCE_ID = "formInstanceId";
	private static final String XML_FORM_INSTANCE_TAG = "formInstanceTag";
	private static final String XML_LOCATION_ID = "locationId";
	private static final String XML_LOCATION_TAG_ID = "locationTagId";
	private static final String XML_FORCE_PRINT_JITS_START = "<forcePrintJITs>";
	private static final String XML_FORCE_PRINT_JITS_END = "</forcePrintJITs>";
	private static final String XML_FORCE_PRINT_JIT_START = "<forcePrintJIT>";
	private static final String XML_FORCE_PRINT_JIT_END = "</forcePrintJIT>";
	private static final String XML_DISPLAY_NAME = "displayName";
	private static final String XML_PATIENT_ROWS_START = "<patientRows>";
	private static final String XML_PATIENT_ROWS_END = "</patientRows>";
	private static final String XML_GREASEBOARD_START = "<greaseboard>";
	private static final String XML_GREASEBOARD_END = "</greaseboard>";
	private static final String XML_NEED_VITALS_START = "<needVitals>";
	private static final String XML_NEED_VITALS_END = "</needVitals>";
	private static final String XML_WAITING_FOR_MD_START = "<waitingForMD>";
	private static final String XML_WAITING_FOR_MD_END = "</waitingForMD>";
	private static final String XML_BAD_SCANS_START = "<badScans>";
	private static final String XML_BAD_SCANS_END = "</badScans>";
	private static final String XML_URL_START = "<url>";
	private static final String XML_URL_END = "</url>";
	private static final String XML_OUTPUT_TYPE_START = "<outputType>";
	private static final String XML_OUTPUT_TYPE_END = "</outputType>";
	private static final String XML_OUTPUT_TYPE = "outputType";
	private static final String XML_ERROR_MESSAGES_START = "<errorMessages>";
	private static final String XML_ERROR_MESSAGES_END = "</errorMessages>";
	private static final String XML_ERROR_MESSAGE = "errorMessage";
	
	private static final String CONTENT_DISPOSITION_PDF = "inline;filename=patientJITS.pdf";
	
	private static final String MAX_CACHE_AGE = "600";
	
	private static final String WILL_KEEP_ALIVE = "OK";
	
	private static final String PROVIDER_SAVE_DRAFT = "_provider_save_draft";
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean authenticated = ServletUtil.authenticateUser(request);
		if (!authenticated) {
			response.setHeader(
				ChirdlUtilConstants.HTTP_HEADER_AUTHENTICATE, ChirdlUtilConstants.HTTP_HEADER_AUTHENTICATE_BASIC_CHICA);  
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		}
		
		String action = request.getParameter(PARAM_ACTION);
		if (GET_PATIENT_JITS.equals(action) || DISPLAY_FORCE_PRINT_FORMS.equals(action)) {
			response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_APPLICATION_PDF);
			response.addHeader(ChirdlUtilConstants.HTTP_HEADER_CONTENT_DISPOSITION, CONTENT_DISPOSITION_PDF);
		} else if (FORCE_PRINT_FORMS.equals(action)) {
			getForcePrintFormHeader(request, response);
		}
	}
	
	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean authenticated = ServletUtil.authenticateUser(request);
		if (!authenticated) {
			response.setHeader(
				ChirdlUtilConstants.HTTP_HEADER_AUTHENTICATE, ChirdlUtilConstants.HTTP_HEADER_AUTHENTICATE_BASIC_CHICA);  
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		}
		
		String action = request.getParameter(PARAM_ACTION);
		if (IS_AUTHENTICATED.equals(action)) {
			ServletUtil.isUserAuthenticated(response);
		} else if (AUTHENTICATE_USER.equals(action)) {
			ServletUtil.authenticateUser(request, response);
		} else if (GET_PATIENT_JITS.equals(action)) {
			getPatientJITs(request, response);
		} else if (GET_AVAILABLE_PATIENT_JITS.equals(action)) {
			getAvailablePatientJITs(request, response);
		} else if (GET_FORCE_PRINT_FORMS.equals(action)) {
			getForcePrintForms(request, response);
		} else if (FORCE_PRINT_FORMS.equals(action)) {
			forcePrintForms(request, response);
		} else if (DISPLAY_FORCE_PRINT_FORMS.equals(action)) {
			getPatientJITs(request, response);
		} else if (GET_GREASEBOARD_PATIENTS.equals(action)) {
			getGreaseboardPatients(request, response);
		} else if (VERIFY_MRN.equals(action)) {
			ManualCheckinSSNMRN.verifyMRN(request, response);
		} else if (GET_MANUAL_CHECKIN.equals(action)) {
			ManualCheckin.getManualCheckinPatient(request, response);
		} else if (SAVE_MANUAL_CHECKIN.equals(action)) {
			ManualCheckin.saveManualCheckinPatient(request, response);
		} else if (SEND_PAGE_REQUEST.equals(action)) {
			Pager.sendPage(request, response);
		} else if (KEEP_ALIVE.equals(action)) {
			keepAlive(response);
		} else if (CLEAR_CACHE.equals(action)) {
			clearCache(request, response);
		} else if (SAVE_FORM_DRAFT.equals(action)) {
			saveFormDraft(request, response);
		}
	}
	
	/**
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	/**
	 * Retrieves the available form instances for a patient.
	 * 
	 * @param request HttServletRequest
	 * @param response HttpServletResponse
	 * @throws IOException
	 */
	private void getAvailablePatientJITs(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_XML);
		response.setHeader(
			ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL, ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL_NO_CACHE);
		PrintWriter pw = response.getWriter();
		pw.write(XML_AVAILABLE_JITS_START);
		
		Integer encounterId = Integer.parseInt(request.getParameter(PARAM_ENCOUNTER_ID));
		
		ChirdlUtilBackportsService backportsService = Context.getService(ChirdlUtilBackportsService.class);
		
		State createState = backportsService.getStateByName(ChirdlUtilConstants.STATE_JIT_CREATE);
		if (createState == null) {
			log.error("The state " + ChirdlUtilConstants.STATE_JIT_CREATE + " does not exist.  No patient JITs will be "
					+ "retrieved.");
			pw.write(XML_AVAILABLE_JITS_END);
			return;
		}
		
		Map<String, FormInstanceTag> formInfoMap = new HashMap<String, FormInstanceTag>();
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
			fav = backportsService.getFormAttributeValue(formId, ChirdlUtilConstants.FORM_INST_ATTR_TRIGGER, locationTagId, 
				locationId);
			if (fav != null && ChirdlUtilConstants.FORM_INST_ATTR_VAL_FORCE_PRINT.equals(fav.getValue())) {
				continue;
			}
			
			// Get the merge directory for the form.
			fav = backportsService.getFormAttributeValue(formId, ChirdlUtilConstants.FORM_ATTR_DEFAULT_MERGE_DIRECTORY, 
				locationTagId, locationId);
			if (fav == null || fav.getValue() == null || fav.getValue().trim().length() == 0) {
				log.error(ChirdlUtilConstants.FORM_ATTR_DEFAULT_MERGE_DIRECTORY + " global property not defined for "
						+ "formId: " + formId + " locationId: " + locationId + " locationTagId: " + locationTagId);
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
					log.error("Cannot locate PDF merge file for formId: " + formId + " locationId: " + locationId +
						" locationTagId: " + locationTagId + " " + mergeFile.getAbsolutePath());
					continue;
				}
			}
			
			FormInstanceTag tag = new FormInstanceTag(locationId, formId, formInstanceId, locationTagId);
			formInfoMap.put(formName, tag);
		}
		
		// Sort the form names and write them to the print writer.
		if (!formInfoMap.isEmpty()) {
			Set<String> formNameSet = formInfoMap.keySet();
			List<String> formNameList = new ArrayList<String>(formNameSet);
			Collections.sort(formNameList);
			
			for (String formName : formNameList) {
				FormInstanceTag tag = formInfoMap.get(formName);
				pw.write(XML_AVAILABLE_JIT_START);
				ServletUtil.writeTag(XML_FORM_NAME, ServletUtil.escapeXML(formName), pw);
				ServletUtil.writeTag(XML_FORM_ID, tag.getFormId(), pw);
				ServletUtil.writeTag(XML_FORM_INSTANCE_ID, tag.getFormInstanceId(), pw);
				ServletUtil.writeTag(XML_LOCATION_ID, tag.getLocationId(), pw);
				ServletUtil.writeTag(XML_LOCATION_TAG_ID, tag.getLocationTagId(), pw);
				pw.write(XML_AVAILABLE_JIT_END);
			}
		}
		
		pw.write(XML_AVAILABLE_JITS_END);
	}
	
	/**
	 * Retrieves the patient's JITs based on form instances provided.
	 * 
	 * @param request HttServletRequest
	 * @param response HttpServletResponse
	 * @throws IOException
	 */
	private void getPatientJITs(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String formInstances = request.getParameter(PARAM_FORM_INSTANCES);
		locatePatientJITs(response, formInstances);
	}
	
	/**
	 * Locates and loads the patient JITs based on the formInstances provided.
	 * 
	 * @param response HttpServletResponse
	 * @param formInstances The form instances to load.  This should be a comma delimited list.
	 * @throws IOException
	 */
	private void locatePatientJITs(HttpServletResponse response, String formInstances) 
			throws IOException {	
		response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_APPLICATION_PDF);
		response.addHeader(ChirdlUtilConstants.HTTP_HEADER_CONTENT_DISPOSITION, CONTENT_DISPOSITION_PDF);
		response.addHeader(ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL, ChirdlUtilConstants.HTTP_CACHE_CONTROL_PUBLIC + ", " + 
				ChirdlUtilConstants.HTTP_CACHE_CONTROL_MAX_AGE + "=" + MAX_CACHE_AGE);
		
		if (formInstances == null) {
			try {
	            writePdfTextToResponse("There are no forms to display.", response);
            }
            catch (DocumentException e) {
	            log.error("Error creating error message PDF", e);
	            return;
            }
			
			return;
		}
		
		ChirdlUtilBackportsService backportsService = Context.getService(ChirdlUtilBackportsService.class);
		List<String> pdfFiles = new ArrayList<String>();
		List<FormInstanceTag> teleformFiles = new ArrayList<FormInstanceTag>();
		List<FormInstanceTag> failedFiles = new ArrayList<FormInstanceTag>();
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
	            log.error("Error creating error PDF document", e);
            }
			
			return;
		} 
		
		loadPatientPdfJITs(response, pdfFiles);
	}
	
	/**
	 * Locates the merge file for the JIT whether it be PDF or Teleform XML.
	 * 
	 * @param formInstanceTag The form instance tag information needed to locate the merge file.
	 * @param backportsService ChirdlUtilBackportsService object used to find the form's merge directory.
	 * @return File containing the merge file location or null if it could not be found.
	 */
	private File locatePatientJIT(FormInstanceTag formInstanceTag, ChirdlUtilBackportsService backportsService) {
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
	 * Renames the fields in the PDF.  This needs to be done due to fields with the same name in the document.
	 * 
	 * @param pdfFile The PDF file that will have its fields renamed.
	 * @param instance The instance of the field in the PDF document.
	 * @return Array of bytes of the PDF document after the fields are renamed.
	 * @throws IOException
	 * @throws DocumentException
	 */
	private static byte[] renamePdfFields(String pdfFile, int instance) throws IOException, DocumentException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// Create the stamper
		PdfStamper stamper = new PdfStamper(new PdfReader(pdfFile), baos);
		// Get the fields
		AcroFields form = stamper.getAcroFields();
		// Loop over the fields
		Set<String> keys = new HashSet<String>(form.getFields().keySet());
		for (String key : keys) {
			// rename the fields
			form.renameField(key, String.format("%s_%d", key, instance));
		}
		// close the stamper
		stamper.close();
		return baos.toByteArray();
	}
	
	/**
	 * Retrieves the list of possible force print forms for a patient..
	 * 
	 * @param request HttServletRequest
	 * @param response HttpServletResponse
	 * @throws IOException
	 */
	private void getForcePrintForms(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_XML);
		response.setHeader(ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL, ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL_NO_CACHE);
		PrintWriter pw = response.getWriter();
		pw.write(XML_FORCE_PRINT_JITS_START);
		
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		String patientIdString = request.getParameter(PARAM_PATIENT_ID);
		Patient patient = null;
		if (patientIdString == null || patientIdString.trim().length()==0) {
			String mrn = request.getParameter(PARAM_MRN);
			if (mrn != null && mrn.trim().length() > 0) {
				patient = getPatientByMRN(mrn);
			}
		} else {
			try {
				Integer patientId = Integer.parseInt(patientIdString);
				patient = Context.getPatientService().getPatient(patientId);
			}
			catch (Exception e) {
				String message = "Invalid patientId parameter provided: " + patientIdString;
				log.error(message);
				throw new IllegalArgumentException(message);
			}
		}
		
		if (patient == null) {
			String message = "No valid patient could be located.";
			
			// DWE CHICA-576 Add some additional logging
			if(patientIdString != null)
			{
				message += " patientId: " + patientIdString;
			}
			if(request.getParameter(PARAM_SESSION_ID) != null)
			{
				message += " sessionId: " + request.getParameter(PARAM_SESSION_ID);
			}
			if(request.getParameter(PARAM_LOCATION_ID) != null)
			{
				message += " locationId: " + request.getParameter(PARAM_LOCATION_ID);
			}
			if(request.getParameter(PARAM_LOCATION_TAG_ID) != null)
			{
				message += " locationTagId: " + request.getParameter(PARAM_LOCATION_TAG_ID);
			}
			log.error(message);
			throw new IllegalArgumentException(message);
		}
		
		String sessionIdString = request.getParameter(PARAM_SESSION_ID);
		Integer sessionId = null;
		if (sessionIdString != null && sessionIdString.trim().length() > 0) {
			try {
				sessionId = Integer.parseInt(sessionIdString);
			}
			catch (Exception e) {
				String message = "Invalid sessionId parameter provided: " + sessionIdString;
				log.error(message);
				throw new IllegalArgumentException(message);
			}
		} else {
			sessionId = getEncounterSessionId(patient.getPatientId());
		}
		
		if (sessionId == null) {
			String message = "Could not find a valid sessionId for patient: " + patientIdString;
			log.error(message);
			throw new IllegalArgumentException(message);
		}
		
		
		User user = Context.getUserContext().getAuthenticatedUser();
		Location location = null;
		String locationString = request.getParameter(PARAM_LOCATION_ID);
		LocationService locationService = Context.getLocationService();
		if (locationString == null || locationString.trim().length() == 0) {
			locationString = user.getUserProperty(ChirdlUtilConstants.USER_PROPERTY_LOCATION);
			location = locationService.getLocation(locationString);
		} else {
			try {
				Integer locationId = Integer.parseInt(locationString);
				location = locationService.getLocation(locationId);
			} catch (NumberFormatException e) {
				String message = "Invalid locationId parameter: " + locationString;
				log.error(message);
				throw new IllegalArgumentException(message);
			}
		}
		
		String locationTags = request.getParameter(PARAM_LOCATION_TAG_ID);
		Integer locationId = null;
		Integer locationTagId = null;
		if (location != null) {
			locationId = location.getLocationId();
			if (locationTags != null && locationTags.trim().length() > 0) {
				try {
					locationTagId = Integer.parseInt(locationTags);
				} catch (NumberFormatException e) {
					String message = "Invalid locationTagId parameter: " + locationTags;
					log.error(message);
					throw new IllegalArgumentException(message);
				}
			} else {
				locationTags = user.getUserProperty(ChirdlUtilConstants.USER_PROPERTY_LOCATION_TAGS);
				if (locationTags != null) {
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
		}
		
		FormService formService = Context.getFormService();
		FormAttribute forcePrintAttr = chirdlutilbackportsService.getFormAttributeByName(ChirdlUtilConstants.FORM_ATTR_FORCE_PRINTABLE);
		if (forcePrintAttr == null) {
			return;
		}
		
		List<FormAttributeValue> attributes = chirdlutilbackportsService.getFormAttributeValues(
			forcePrintAttr.getFormAttributeId(), locationId, locationTagId);
		Map<String, Integer> ageUnitsMinMap = new HashMap<String, Integer>();
		Map<String, Integer> ageUnitsMaxMap = new HashMap<String, Integer>();
		Set<FormDisplay> printableJits = new TreeSet<FormDisplay>();
		
		FormAttribute ageMinAttr = chirdlutilbackportsService.getFormAttributeByName(ChirdlUtilConstants.FORM_ATTR_AGE_MIN);
		FormAttribute ageMaxAttr = chirdlutilbackportsService.getFormAttributeByName(ChirdlUtilConstants.FORM_ATTR_AGE_MAX);
		FormAttribute ageMinUnitsAttr = chirdlutilbackportsService.getFormAttributeByName(ChirdlUtilConstants.FORM_ATTR_AGE_MIN_UNITS);
		FormAttribute ageMaxUnitsAttr = chirdlutilbackportsService.getFormAttributeByName(ChirdlUtilConstants.FORM_ATTR_AGE_MAX_UNITS);
		FormAttribute displayNameAttr = chirdlutilbackportsService.getFormAttributeByName(ChirdlUtilConstants.FORM_ATTR_DISPLAY_NAME);
		FormAttribute outputTypeAttr = chirdlutilbackportsService.getFormAttributeByName(ChirdlUtilConstants.FORM_ATTR_OUTPUT_TYPE);
		
		Map<Integer, String> formAttrValAgeMinMap = getFormAttributeValues(chirdlutilbackportsService, ageMinAttr.getFormAttributeId(), 
			locationId, locationTagId);
		Map<Integer, String> formAttrValAgeMinUnitsMap = getFormAttributeValues(chirdlutilbackportsService, ageMinUnitsAttr.getFormAttributeId(), 
			locationId, locationTagId);
		Map<Integer, String> formAttrValAgeMaxMap = getFormAttributeValues(chirdlutilbackportsService, ageMaxAttr.getFormAttributeId(), 
			locationId, locationTagId);
		Map<Integer, String> formAttrValAgeMaxUnitsMap = getFormAttributeValues(chirdlutilbackportsService, ageMaxUnitsAttr.getFormAttributeId(), 
			locationId, locationTagId);
		for (FormAttributeValue attribute : attributes) {
			if (attribute.getValue().equalsIgnoreCase(ChirdlUtilConstants.FORM_ATTR_VAL_TRUE) && 
					attribute.getLocationId().equals(locationId) && 
					attribute.getLocationTagId().equals(locationTagId)) {
				Form form = formService.getForm(attribute.getFormId());
				Integer formId = form.getFormId();
				if (!form.getRetired()) {
					FormDisplay formDisplay = new FormDisplay();
					formDisplay.setFormName(form.getName());
					formDisplay.setFormId(form.getFormId());
					FormAttributeValue attributeValue = chirdlutilbackportsService.getFormAttributeValue(form.getFormId(), 
						displayNameAttr, locationTagId, locationId);
					if (attributeValue == null || attributeValue.getValue() == null) {
						formDisplay.setDisplayName(form.getName());
					} else {
						formDisplay.setDisplayName(attributeValue.getValue());
					}
					
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
		
		String defaultOutputType = Context.getAdministrationService().getGlobalProperty(
			ChirdlUtilConstants.GLOBAL_PROP_DEFAULT_OUTPUT_TYPE);
		if (defaultOutputType == null) {
			defaultOutputType = "";
		}
		
		for (FormDisplay formDisplay: printableJits) {
			pw.write(XML_FORCE_PRINT_JIT_START);
			ServletUtil.writeTag(XML_FORM_ID, formDisplay.getFormId(), pw);
			ServletUtil.writeTag(XML_DISPLAY_NAME, ServletUtil.escapeXML(formDisplay.getDisplayName()), pw);
			FormAttributeValue outputType = chirdlutilbackportsService.getFormAttributeValue(formDisplay.getFormId(), 
				outputTypeAttr, locationTagId, locationId);
			pw.write(XML_OUTPUT_TYPE_START);
			if (outputType != null && outputType.getValue() != null && !outputType.getValue().isEmpty()) {
				pw.write(outputType.getValue());
			} else {
				pw.write(defaultOutputType);
			}
			
			pw.write(XML_OUTPUT_TYPE_END);
			pw.write(XML_FORCE_PRINT_JIT_END);
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
	 * Force prints a specified form for a patient..
	 * 
	 * @param request HttServletRequest
	 * @param response HttpServletResponse
	 * @throws IOException
	 */
	private void forcePrintForms(HttpServletRequest request, HttpServletResponse response) throws IOException {	
		response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_XML);
		response.setHeader(
			ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL, ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL_NO_CACHE);
		PrintWriter pw = response.getWriter();
		pw.write(XML_FORCE_PRINT_JITS_START);
		
		String patientIdString = request.getParameter(PARAM_PATIENT_ID);
		String formIdsString = request.getParameter(PARAM_FORM_IDS);
		String sessionIdString = request.getParameter(PARAM_SESSION_ID);

		Integer patientId = null;
		if (patientIdString == null || patientIdString.trim().length()==0) {
			String mrn = request.getParameter(PARAM_MRN);
			if (mrn != null && mrn.trim().length() > 0) {
				Patient patient = getPatientByMRN(mrn);
				if (patient != null) {
					patientId = patient.getPatientId();
				}
			}
		} else {
			try {
				patientId = Integer.parseInt(patientIdString);
			}
			catch (Exception e) {
				String message = "Invalid patientId parameter provided: " + patientIdString;
				log.error(message);
				throw new IllegalArgumentException(message);
			}
		}
		
		if (patientId == null) {
			String message = "No valid patient could be located.";
			log.error(message);
			throw new IllegalArgumentException(message);
		}
		
		Integer sessionId = null;
		if (sessionIdString != null && sessionIdString.trim().length() > 0) {
			try {
				sessionId = Integer.parseInt(sessionIdString);
			}
			catch (Exception e) {
				String message = "Invalid sessionId parameter provided: " + sessionIdString;
				log.error(message);
				throw new IllegalArgumentException(message);
			}
		} else {
			sessionId = getEncounterSessionId(patientId);
		}
		
		if (sessionId == null) {
			String message = "Could not find a valid sessionId for patient: " + patientIdString;
			log.error(message);
			throw new IllegalArgumentException(message);
		}
		
		LogicService logicService = Context.getLogicService();
		
		//print the form
		User user = Context.getUserContext().getAuthenticatedUser();
		Location location = null;
		String locationString = request.getParameter(PARAM_LOCATION_ID);
		LocationService locationService = Context.getLocationService();
		if (locationString == null || locationString.trim().length() == 0) {
			locationString = user.getUserProperty(ChirdlUtilConstants.USER_PROPERTY_LOCATION);
			location = locationService.getLocation(locationString);
		} else {
			try {
				Integer locationId = Integer.parseInt(locationString);
				location = locationService.getLocation(locationId);
			} catch (NumberFormatException e) {
				String message = "Invalid locationId parameter: " + locationString;
				log.error(message);
				throw new IllegalArgumentException(message);
			}
		}
		
		if (location == null) {
			String message = "Location not found: " + locationString;
			log.error(message);
			throw new IllegalArgumentException(message);
		}
		
		String locationTags = request.getParameter(PARAM_LOCATION_TAG_ID);
		Integer locationId = null;
		Integer locationTagId = null;
		locationId = location.getLocationId();
		if (locationTags != null && locationTags.trim().length() > 0) {
			try {
				locationTagId = Integer.parseInt(locationTags);
			} catch (NumberFormatException e) {
				String message = "Invalid locationTagId parameter: " + locationTags;
				log.error(message);
				throw new IllegalArgumentException(message);
			}
		} else {
			locationTags = user.getUserProperty(ChirdlUtilConstants.USER_PROPERTY_LOCATION_TAGS);
			if (locationTags != null) {
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
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		FormService formService = Context.getFormService();
		
		parameters = new HashMap<String, Object>();
		parameters.put(PARAM_SESSION_ID, sessionId);
		parameters.put(PARAM_LOCATION_TAG_ID, locationTagId);
		FormInstance formInstance = new FormInstance();
		formInstance.setLocationId(locationId);
		parameters.put(PARAM_FORM_INSTANCE, formInstance);

		if (formIdsString == null || formIdsString.trim().length() == 0) {
			String message = "formIdsString is null or empty";
			log.error(message);
			throw new IllegalArgumentException(message);
		}
		
		List<String> errorList = new ArrayList<String>();
		String[] formIds = formIdsString.split(ChirdlUtilConstants.GENERAL_INFO_COMMA);
		for (String formIdStr : formIds) {
			// print the form
			formIdStr = formIdStr.trim();
			Integer formId = null;
			try {
				formId = Integer.parseInt(formIdStr);
			} catch (Exception e) {
				String message = "Invalid formId parameter: " + formIdStr;
				log.error(message);
				continue;
			}
			
			Form form = formService.getForm(formId);
			if (form == null) {
				String message = "No form found for formId: " + formIdStr;
				log.error(message);
				continue;
			}
			
			String formName = form.getName();
			parameters.put(ChirdlUtilConstants.PARAMETER_1, formName);
			parameters.put(ChirdlUtilConstants.PARAMETER_2, ChirdlUtilConstants.FORM_INST_ATTR_VAL_FORCE_PRINT);
			parameters.put(ChirdlUtilConstants.PARAMETER_3, ChirdlUtilConstants.GENERAL_INFO_FALSE);
			parameters.put(ChirdlUtilConstants.PARAMETER_4, ChirdlUtilConstants.GENERAL_INFO_TRUE);
			Result result = logicService.eval(patientId, ChirdlUtilConstants.RULE_CREATE_JIT, parameters);
			
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
						ChirdlUtilConstants.FORM_ATTR_VAL_TELEFORM_PDF.equalsIgnoreCase(outputType)) {
					pw.write(XML_FORCE_PRINT_JIT_START);
					ServletUtil.writeTag(XML_FORM_INSTANCE_TAG, formInstanceTag, pw);
					ServletUtil.writeTag(XML_OUTPUT_TYPE, outputType, pw);
					pw.write(XML_FORCE_PRINT_JIT_END);
				} else if (ChirdlUtilConstants.FORM_ATTR_VAL_TELEFORM_XML.equalsIgnoreCase(outputType)) {
					pw.write(XML_FORCE_PRINT_JIT_START);
					ServletUtil.writeTag(XML_FORM_INSTANCE_TAG, formInstanceTag, pw);
					ServletUtil.writeTag(XML_OUTPUT_TYPE, outputType, pw);
					pw.write(XML_FORCE_PRINT_JIT_END);
				} else {
					formName = getFormName(formId, locationId, locationTagId);
					String message = "Invalid outputType attribute '" + outputType + "' found for form: " + formName;
					log.error(message);
					errorList.add(message);
				}
			}
			
			if (errorList.size() > 0) {
				pw.write(XML_ERROR_MESSAGES_START);
				for (String error : errorList) {
					ServletUtil.writeTag(XML_ERROR_MESSAGE, error, pw);
				}
				
				pw.write(XML_ERROR_MESSAGES_END);
			}
		}
		
		pw.write(XML_FORCE_PRINT_JITS_END);
	}
	
	/**
	 * Returns the latest session ID for a patient based on their latest encounter with a checkin state.
	 * 
	 * @param patientId The patient ID used to determine the session ID returned.
	 * @return session ID.
	 */
	private Integer getEncounterSessionId(Integer patientId) {
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
	
	/**
	 * Retrieves a patient based on MRN.
	 * 
	 * @param mrn MRN used to find a patient.
	 * @return The patient or null if a patient cannot be found with the specified MRN.
	 */
	private Patient getPatientByMRN(String mrn) {
		PatientService patientService = Context.getPatientService();
		Patient patient = null;
		mrn = Util.removeLeadingZeros(mrn);
		if (mrn != null && !mrn.contains("-") && mrn.length() > 1) {
			mrn = mrn.substring(0, mrn.length() - 1) + "-" + mrn.substring(mrn.length() - 1);
		}
		
		PatientIdentifierType identifierType = patientService
				.getPatientIdentifierTypeByName(ChirdlUtilConstants.IDENTIFIER_TYPE_MRN);
		List<PatientIdentifierType> identifierTypes = new ArrayList<PatientIdentifierType>();
		identifierTypes.add(identifierType);
		List<Patient> patients = patientService.getPatients(null, mrn,
				identifierTypes,true);
		if (patients.size() == 0){
			patients = patientService.getPatients(null, "0" + mrn,
					identifierTypes,true);
		}

		if (patients.size() > 0) {
			patient = patients.get(0);
		}
		
		return patient;
	}
	
	/**
	 * Provides the necessary information for the servlet header for force printing a form.
	 * 
	 * @param request HttServletRequest
	 * @param response HttpServletResponse
	 * @throws IOException
	 */
	private void getForcePrintFormHeader(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String formIdsStr = request.getParameter(PARAM_FORM_IDS);
		String locationIdStr = request.getParameter(PARAM_LOCATION_ID);
		String locationTagIdStr = request.getParameter(PARAM_LOCATION_TAG_ID);
		Integer formId = null;
		Integer locationId = null;
		Integer locationTagId = null;
		
		if (formIdsStr == null) {
			log.error("Invalid argument formId: " + formIdsStr);
			response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_HTML);
			response.getWriter().write("Invalid argument formId: " + formIdsStr);
			return;
		}
		
		List<String> errorList = new ArrayList<String>();
		String[] formIds = formIdsStr.split(ChirdlUtilConstants.GENERAL_INFO_COMMA);
		for (String formIdStr : formIds) {
			try {
				formId = Integer.parseInt(formIdStr);
			} catch (NumberFormatException e) {
				log.error("Invalid argument formId: " + formIdStr);
				errorList.add(formIdStr);
				continue;
			}
			
			try {
				locationId = Integer.parseInt(locationIdStr);
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
				log.error(errorMsg.toString());
				errorList.add(formIdStr);
				continue;
			}
			
			try {
				locationTagId = Integer.parseInt(locationTagIdStr);
			} catch (NumberFormatException e) {
				log.error("Invalid argument locationTagId: " + locationTagIdStr);
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
	 * Retrieves the greaseboard patient information for the day..
	 * 
	 * @param request HttServletRequest
	 * @param response HttpServletResponse
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
    private void getGreaseboardPatients(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		GreaseBoardBuilder.generatePatientRows(map);
		List<PatientRow> patientRows = (List<PatientRow>)map.get(PARAM_PATIENT_ROWS);
		response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_XML);
		response.setHeader(ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL, ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL_NO_CACHE);
		PrintWriter pw = response.getWriter();
		pw.write(XML_GREASEBOARD_START);
		pw.write(XML_NEED_VITALS_START + map.get(PARAM_NEED_VITALS) + XML_NEED_VITALS_END);
		pw.write(XML_WAITING_FOR_MD_START + map.get(PARAM_WAITING_FOR_MD) + XML_WAITING_FOR_MD_END);
		pw.write(XML_PATIENT_ROWS_START);
		if (patientRows != null) {
			for (PatientRow row : patientRows) {
				pw.write(row.toXml());
			}
		}
		pw.write(XML_PATIENT_ROWS_END);
		pw.write(XML_BAD_SCANS_START);
		List<URL> badScans = (List<URL>)map.get(PARAM_BAD_SCANS);
		if (badScans != null) {
			for (URL badScan : badScans) {
				pw.write(XML_URL_START + badScan + XML_URL_END);
			}
		}
		pw.write(XML_BAD_SCANS_END);
		pw.write(XML_GREASEBOARD_END);
	}
	
	private Map<Integer, String> getFormAttributeValues(ChirdlUtilBackportsService backportsService, Integer attributeId, 
		Integer locationId, Integer locationTagId) {
		Map<Integer, String> formToValueMap = new HashMap<Integer, String>();
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
	private String convertUnits(String currentUnits) {
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
	 * Creates a new PDF with the provided text and writes it to the HttpServletResponse object
	 * 
	 * @param text The text to insert into the PDF
	 * @param response The HttpServletResponse object where the PDF will be written
	 * @throws DocumentException
	 * @throws IOException
	 */
	private void writePdfTextToResponse(String text, HttpServletResponse response) throws DocumentException, IOException {
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
	 * Combines the provided PDF files into one PDF document and writes it to the HTTP response.
	 * 
	 * @param response The HttpServletResponse where the PDF will be written.
	 * @param pdfFiles List of files locations for PDF forms to combine.
	 * @throws IOException
	 */
	private void loadPatientPdfJITs(HttpServletResponse response, List<String> pdfFiles) throws IOException {
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
	            		log.error("Error adding page", e);
	            	}
	            }
	            
	            copy.freeReader(reader);
	            reader.close();
	
		        document.close();
		        copy.close();
		        response.setContentLength(output.size());
		        response.getOutputStream().write(output.toByteArray());
			} catch (BadPdfFormatException e) {
				log.error("Bad PDF found: " + filePath, e);
				throw new IOException(e);
			} catch (DocumentException e) {
				log.error("Error handling PDF document: " + filePath, e);
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
		            		log.error("Error adding page", e);
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
				log.error("Bad PDF found: " + filePath, e);
				throw new IOException(e);
			} catch (DocumentException e) {
				log.error("Error handling PDF document", e);
				throw new IOException(e);
			} finally {
				baos.flush();
		        baos.close();
			}
		}
	}
	
	/**
	 * Creates a PDF with an informational message about the Teleform files that were processed 
	 * and writes it to the HTTP response.
	 * 
	 * @param response The HttpServletResponse where the PDF will be written.
	 * @param teleformFiles List of FormInstanceTag objects that used for Teleform.
	 * @throws IOException
	 */
	private void loadPatientTeleformJITs(HttpServletResponse response, List<FormInstanceTag> teleformFiles) throws IOException {
		String subject = "form";
		String verb = "has";
		if (teleformFiles.size() > 1) {
			subject = "forms";
			verb = "have";
		}
		
		StringBuffer message = new StringBuffer("The following ").append(subject).append(" ").append(verb).append(" been successfully sent to the printer: ");
		for (int i = 0; i < teleformFiles.size(); i++) {
			FormInstanceTag formInstanceTag = teleformFiles.get(i);
			if (i != 0) {
				message.append(", ");
			}
			
			String formName = getFormName(
				formInstanceTag.getFormId(), formInstanceTag.getLocationId(), formInstanceTag.getLocationTagId());
			message.append(formName);
		}
		
		message.append(".");
		try {
            writePdfTextToResponse(message.toString(), response);
        }
        catch (DocumentException e) {
            log.error("Error creating error PDF document", e);
        }
	}
	
	/**
	 * Creates a PDF with an error message for the forms the failed and writes it to the HTTP response.
	 * 
	 * @param response The HttpServletResponse where the PDF will be written.
	 * @param errorFiles List of FormInstanceTag objects that failed.
	 * @throws IOException
	 */
	private void loadPatientErrorJITs(HttpServletResponse response, List<FormInstanceTag> errorFiles) throws IOException {
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
			
			String formName = getFormName(
				formInstanceTag.getFormId(), formInstanceTag.getLocationId(), formInstanceTag.getLocationTagId());
			message.append(formName);
		}
		
		message.append(".");
		try {
            writePdfTextToResponse(message.toString(), response);
        }
        catch (DocumentException e) {
            log.error("Error creating error PDF document", e);
        }
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
	private String getFormName(Integer formId, Integer locationId, Integer locationTagId) {
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
	 * Method to keep the session alive.
	 * 
	 * @param response The HttpServletResponse where the response will be written.
	 * @throws IOException
	 */
	private void keepAlive(HttpServletResponse response) throws IOException {
		response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_HTML);
		response.setHeader(
			ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL, ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL_NO_CACHE);
		PrintWriter pw = response.getWriter();
		pw.write(WILL_KEEP_ALIVE);
	}
	
	/**
	 * Clears the given cache in the request
	 * 
	 * @param request HttServletRequest
	 * @param response HttpServletResponse
	 * @throws IOException
	 */
    private void clearCache(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_HTML);
		response.setHeader(ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL, ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL_NO_CACHE);
		
    	PrintWriter pw = response.getWriter();
    	String cacheName = request.getParameter(PARAM_CACHE_NAME);
    	if (cacheName == null || cacheName.isEmpty()) {
    		String message = "Please specify a " + PARAM_CACHE_NAME + " parameter";
    		log.error(message);
    		pw.write(message);
    		return;
    	}
    	
    	String cacheKeyType = request.getParameter(PARAM_CACHE_KEY_TYPE);
    	if (cacheKeyType == null || cacheKeyType.isEmpty()) {
    		String message = "Please specify a " + PARAM_CACHE_KEY_TYPE + " parameter";
    		log.error(message);
    		pw.write(message);
    		return;
    	}
    	
    	String cacheValueType = request.getParameter(PARAM_CACHE_VALUE_TYPE);
    	if (cacheValueType == null || cacheValueType.isEmpty()) {
    		String message = "Please specify a " + PARAM_CACHE_VALUE_TYPE + " parameter";
    		log.error(message);
    		pw.write(message);
    		return;
    	}
    	
    	Class<?> keyType = null;
    	Class<?> valueType = null;
    	try {
    		keyType = Class.forName(cacheKeyType);
    	}
    	catch (LinkageError | ClassNotFoundException e) {
    		String message = "Error creating class from reflection using parameter " + PARAM_CACHE_KEY_TYPE + " " + keyType + 
    				" for cache " + cacheName;
    		log.error(message, e);
    		pw.write(message);
    		return;
    	}
    	
    	try {
    		valueType = Class.forName(cacheValueType);
    	}
    	catch (LinkageError | ClassNotFoundException e) {
    		String message = "Error creating class from reflection using parameter " + PARAM_CACHE_VALUE_TYPE + " " + valueType + 
    				" for cache " + cacheName;
    		log.error(message, e);
    		pw.write(message);
    		return;
    	}
    	
    	ApplicationCacheManager cacheManager = ApplicationCacheManager.getInstance();
    	try {
    		cacheManager.clearCache(cacheName, keyType, valueType);
    	}
    	catch (Exception e) {
    		String message = "Error clearing cache " + cacheName;
    		log.error(message, e);
    		pw.write(message);
    		return;
    	}
    	
    	pw.write(RESULT_SUCCESS);
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
    private void saveFormDraft(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_HTML);
		response.setHeader(ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL, ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL_NO_CACHE);
		
    	PrintWriter pw = response.getWriter();
    	Integer formId = null;
		
		//parse out the location_id,form_id,location_tag_id, and form_instance_id
		//from the selected form
		String formInstance = request.getParameter(ChirdlUtilConstants.PARAMETER_FORM_INSTANCE);
		FormInstanceTag formInstTag = null;
		if (formInstance != null && formInstance.trim().length() > 0) {
			formInstTag = FormInstanceTag.parseFormInstanceTag(formInstance);
			formId = formInstTag.getFormId();
		} else {
			String messagePart1 = "Error saving form draft: form instance tag parameter not found.";
			String messagePart2 = "Please contact support.";
			ServletUtil.writeHtmlErrorMessage(pw, null, log, messagePart1, messagePart2);
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
			ServletUtil.writeHtmlErrorMessage(pw, e, log, messagePart1, messagePart2);
    		return;
		}
		
		if (records == null) {
			String messagePart1 = "Error saving form draft: unable to load form from disk.";
    		String messagePart2 = "Please contact support with the following information: Form ID: " + formId + 
					" Form Instance ID: " + formInstTag.getFormInstanceId() + " Location ID: " + 
					formInstTag.getLocationId() + " Location Tag ID: " + formInstTag.getLocationTagId();
    		ServletUtil.writeHtmlErrorMessage(pw, null, log, messagePart1, messagePart2);
    		return;
		}
		
		Record record = records.getRecord();
		if (record == null) {
			String messagePart1 = "Error saving form draft: unable to load form from disk.";
    		String messagePart2 = "Please contact support with the following information: Form ID: " + formId + 
					" Form Instance ID: " + formInstTag.getFormInstanceId() + " Location ID: " + 
					formInstTag.getLocationId() + " Location Tag ID: " + formInstTag.getLocationTagId();
    		ServletUtil.writeHtmlErrorMessage(pw, null, log, messagePart1, messagePart2);
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
			ServletUtil.writeHtmlErrorMessage(pw, e, log, messagePart1, messagePart2);
    		return;
		}
		
		String providerId = request.getParameter(PARAM_PROVIDER_ID);
		String patientId = request.getParameter(PARAM_PATIENT_ID);
		String encounterId = request.getParameter(PARAM_ENCOUNTER_ID);
		// Save who created a draft.
		if (providerId != null && providerId.trim().length() > 0 && patientId != null && patientId.trim().length() > 0 && 
				encounterId != null && encounterId.trim().length() > 0) {
			try {
				saveProviderDraft(Integer.parseInt(patientId), Integer.parseInt(encounterId), providerId, formInstTag);
			} catch (NumberFormatException e) {
				log.error("Error saving provider ID for draft form ID: " + formId + " patient ID: " + patientId + 
					" encounter ID: " + encounterId + " provider ID: " + providerId + " form instance ID: " + 
						formInstTag.getFormInstanceId() + " location ID: " + formInstTag.getLocationId() + 
						" location tag ID: " + formInstTag.getLocationTagId());
			}
		} else {
			log.error("Cannot log who is saving a draft for form ID: " + formId + " patient ID: " + patientId + 
				" encounter ID: " + encounterId + " provider ID: " + providerId + " form instance ID: " + 
					formInstTag.getFormInstanceId() + " location ID: " + formInstTag.getLocationId() + 
					" location tag ID: " + formInstTag.getLocationTagId());
		}
		
		pw.write(RESULT_SUCCESS);
    }
    
    /**
	 * Saves the user's provider ID to an observation for saving a draft.
	 * 
	 * @param patientId The ID of the patient who owns the form.
	 * @param formId The ID of the form being viewed.
	 * @param encounterId The encounter ID of the encounter where the form was created.
	 * @param providerId The ID of the provider to be stored.
	 * @param formInstTag The form instance tag information
	 */
	private void saveProviderDraft(Integer patientId, Integer encounterId, String providerId, FormInstanceTag formInstTag) {
		Form form = Context.getFormService().getForm(formInstTag.getFormId());
		String conceptName = form.getName() + PROVIDER_SAVE_DRAFT;
		saveProviderInfo(patientId, encounterId, providerId, conceptName, formInstTag);
	}
	
	/**
	 * Saves the submitter's provider ID to an observation.
	 * 
	 * @param patient The ID of the patient who owns the form.
	 * @param formId The ID of the form being viewed.
	 * @param encounterId The encounter ID of the encounter where the form was created.
	 * @param providerId The ID of the provider to be stored.
	 * @param conceptName The name of the concept.
	 * @param formInstTag The form instance tag information
	 */
	private void saveProviderInfo(Integer patientId, Integer encounterId, String providerId, String conceptName, FormInstanceTag formInstTag) {
		PatientService patientService = Context.getPatientService();
		Patient patient = patientService.getPatient(patientId);
		if (patient == null) {
			log.error("Could not log provider info.  Patient " + patientId + " not found.");
			return;
		}
		
		ConceptService conceptService = Context.getConceptService();
		Concept concept = conceptService.getConceptByName(conceptName);
		if (concept == null) {
			log.error("Could not log provider info.  Concept " + conceptName + " not found.");
			return;
		}
		FormInstance formInstance = new FormInstance(formInstTag.getLocationId(),formInstTag.getFormId(),formInstTag.getFormInstanceId());
		org.openmrs.module.chica.util.Util.saveObsWithStatistics(patient, concept, encounterId, providerId, formInstance, null, formInstTag.getLocationTagId(), null);
	}
}
