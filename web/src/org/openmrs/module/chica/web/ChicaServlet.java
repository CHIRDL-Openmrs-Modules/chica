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
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.User;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.result.Result;
import org.openmrs.module.chica.util.PatientRow;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstanceTag;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BadPdfFormatException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfCopyFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

/**
 * Servlet giving access to CHICA information.
 *
 * @author Steve McKee
 */
public class ChicaServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private static final String IS_AUTHENTICATED = "isAuthenticated";
	private static final String AUTHENTICATE_USER = "authenticateUser";
	private static final String GET_PATIENT_JITS = "getPatientJITs";
	private static final String GET_AVAILABLE_PATIENT_JITS = "getAvailablePatientJITs";
	private static final String GET_FORCE_PRINT_FORMS = "getForcePrintForms";
	private static final String FORCE_PRINT_FORM = "forcePrintForm";
	private static final String GET_GREASEBOARD_PATIENTS = "getGreaseboardPatients";
	private static final String VERIFY_MRN = "verifyMRN";
	private static final String GET_MANUAL_CHECKIN = "getManualCheckin";
	private static final String SAVE_MANUAL_CHECKIN = "saveManualCheckin";
	private static final String SEND_PAGE_REQUEST = "sendPageRequest";
	
	private static final String PARAM_ACTION = "action";
	private static final String PARAM_ENCOUNTER_ID = "encounterId";
	private static final String PARAM_SESSION_ID = "sessionId";
	private static final String PARAM_FORM_ID = "formId";
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
	
	private static final String XML_AVAILABLE_JITS_START = "<availableJITs>";
	private static final String XML_AVAILABLE_JITS_END = "</availableJITs>";
	private static final String XML_AVAILABLE_JIT_START = "<availableJIT>";
	private static final String XML_AVAILABLE_JIT_END = "</availableJIT>";
	private static final String XML_FORM_NAME = "formName";
	private static final String XML_FORM_ID = "formId";
	private static final String XML_FORM_INSTANCE_ID = "formInstanceId";
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
	
	private static final String CONTENT_DISPOSITION_PDF = "inline;filename=patientJITS.pdf";
	
	private static final String MAX_CACHE_AGE = "600";
	
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
		if (GET_PATIENT_JITS.equals(action)) {
			response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_APPLICATION_PDF);
			response.addHeader(ChirdlUtilConstants.HTTP_HEADER_CONTENT_DISPOSITION, CONTENT_DISPOSITION_PDF);
		} else if (FORCE_PRINT_FORM.equals(action)) {
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
		} else if (FORCE_PRINT_FORM.equals(action)) {
			forcePrintForm(request, response);
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
		}
	}
	
	/**
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	/**
	 * Retrieves the available form instances for a patient .
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
				if (ChirdlUtilConstants.FORM_ATTR_VAL_PDF.equals(favValue)) {
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
			
			Form form = Context.getFormService().getForm(formId);
			String formName = null;
			
			// Try to get a display name if one exists.
			fav = backportsService.getFormAttributeValue(formId, ChirdlUtilConstants.FORM_ATTR_DISPLAY_NAME, 
				locationTagId, locationId);
			if (fav != null && fav.getValue() != null && fav.getValue().trim().length() > 0) {
				formName = fav.getValue();
			} else {
				formName = form.getName();
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
		if (formInstances == null) {
			return;
		}
		
		ChirdlUtilBackportsService backportsService = Context.getService(ChirdlUtilBackportsService.class);
		State createState = backportsService.getStateByName(ChirdlUtilConstants.STATE_JIT_CREATE);
		if (createState == null) {
			log.error("The state " + ChirdlUtilConstants.STATE_JIT_CREATE + " does not exist.  No patient JITs will be "
					+ "retrieved.");
			return;
		}
		
		List<String> filesToCombine = new ArrayList<String>();
		for (String formInstance : formInstances.split(ChirdlUtilConstants.GENERAL_INFO_COMMA)) {
			FormInstanceTag formInstanceTag = FormInstanceTag.parseFormInstanceTag(formInstance);
			if (formInstanceTag == null) {
				continue;
			}
			
			Integer locationId = formInstanceTag.getLocationId();
			Integer formId = formInstanceTag.getFormId();
			Integer formInstanceId = formInstanceTag.getFormInstanceId();
			Integer locationTagId = formInstanceTag.getLocationTagId();
			
			// Get the merge directory for the form.
			FormAttributeValue fav = 
					backportsService.getFormAttributeValue(formId, ChirdlUtilConstants.FORM_ATTR_DEFAULT_MERGE_DIRECTORY, 
						locationTagId, locationId);
			if (fav == null || fav.getValue() == null || fav.getValue().trim().length() == 0) {
				continue;
			}
			
			// Find the merge PDF file.
			String mergeDirectory = fav.getValue();
			File pdfDir = new File(mergeDirectory, ChirdlUtilConstants.FILE_PDF);
			File mergeFile = new File(pdfDir, locationId + ChirdlUtilConstants.GENERAL_INFO_UNDERSCORE + formId + 
				ChirdlUtilConstants.GENERAL_INFO_UNDERSCORE + formInstanceId + ChirdlUtilConstants.FILE_EXTENSION_PDF);
			if (!mergeFile.exists()) {
				mergeFile = new File(pdfDir, ChirdlUtilConstants.GENERAL_INFO_UNDERSCORE + locationId + 
					ChirdlUtilConstants.GENERAL_INFO_UNDERSCORE + formId + ChirdlUtilConstants.GENERAL_INFO_UNDERSCORE + 
					formInstanceId + ChirdlUtilConstants.GENERAL_INFO_UNDERSCORE + ChirdlUtilConstants.FILE_EXTENSION_PDF);
				if (!mergeFile.exists()) {
					continue;
				}
			}
			
			filesToCombine.add(mergeFile.getAbsolutePath());
		}
		
		if (filesToCombine.size() == 0) {
			return;
		} 
		
		response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_APPLICATION_PDF);
		response.addHeader(ChirdlUtilConstants.HTTP_HEADER_CONTENT_DISPOSITION, CONTENT_DISPOSITION_PDF);
		response.addHeader(ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL, ChirdlUtilConstants.HTTP_CACHE_CONTROL_PUBLIC + ", " + 
				ChirdlUtilConstants.HTTP_CACHE_CONTROL_MAX_AGE + "=" + MAX_CACHE_AGE);
		
		if (filesToCombine.size() == 1) {
			String filePath = null;
			try {
				Document document = new Document();
				ByteArrayOutputStream output = new ByteArrayOutputStream();
		        PdfCopy copy = new PdfCopy(document, output);
		        document.open();
		        PdfReader reader;
		        int n;
	        	filePath = filesToCombine.get(0);
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
		        response.addHeader("Accept-Ranges", "bytes");
		        response.setContentLength(output.size());
		        response.getOutputStream().write(output.toByteArray());
			} catch (BadPdfFormatException e) {
				log.error("Bad PDF found: " + filePath, e);
				throw new IOException(e);
			} catch (DocumentException e) {
				log.error("Error handling PDF document: " + filePath, e);
				throw new IOException(e);
			}
		} else {
			int contentSize = 0;
			for (int i = 0; i < filesToCombine.size(); i++) {
	        	String filePath = filesToCombine.get(i);
	            try {
	            	contentSize += renamePdfFields(filePath, i).length;
	            }
	            catch (DocumentException e) {
	            	log.error("Error handling PDF document: " + filePath, e);
					throw new IOException(e);
	            }
	        }
			
			response.setContentLength(contentSize);
			String filePath = null;
			try {
				PdfCopyFields copy = new PdfCopyFields(response.getOutputStream());
		        for (int i = 0; i < filesToCombine.size(); i++) {
		        	filePath = filesToCombine.get(i);
		        	PdfReader reader = new PdfReader(renamePdfFields(filePath, i));
		            copy.addDocument(reader);
		            reader.close();
		        }
		        
		        copy.close();
			} catch (BadPdfFormatException e) {
				log.error("Bad PDF found: " + filePath, e);
				throw new IOException(e);
			} catch (DocumentException e) {
				log.error("Error handling PDF document", e);
				throw new IOException(e);
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
			String mrn = request.getParameter("mrn");
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
		List<FormAttributeValue> attributes = chirdlutilbackportsService.getFormAttributesByName(
			ChirdlUtilConstants.FORM_ATTR_FORCE_PRINTABLE);
		Map<String, Integer> ageUnitsMinMap = new HashMap<String, Integer>();
		Map<String, Integer> ageUnitsMaxMap = new HashMap<String, Integer>();
		Set<FormDisplay> printableJits = new TreeSet<FormDisplay>();
		for (FormAttributeValue attribute : attributes) {
			if (attribute.getValue().equalsIgnoreCase(ChirdlUtilConstants.FORM_ATTR_VAL_TRUE) && 
					attribute.getLocationId().equals(locationId) && 
					attribute.getLocationTagId().equals(locationTagId)) {
				Form form = formService.getForm(attribute.getFormId());
				if (!form.getRetired()) {
					FormDisplay formDisplay = new FormDisplay();
					formDisplay.setFormName(form.getName());
					formDisplay.setFormId(form.getFormId());
					FormAttributeValue attributeValue = chirdlutilbackportsService.getFormAttributeValue(form.getFormId(), 
						ChirdlUtilConstants.FORM_ATTR_DISPLAY_NAME, locationTagId, locationId);
					if (attributeValue == null || attributeValue.getValue() == null) {
						formDisplay.setDisplayName(form.getName());
					} else {
						formDisplay.setDisplayName(attributeValue.getValue());
					}
					
					FormAttributeValue ageMin = chirdlutilbackportsService.getFormAttributeValue(form.getFormId(), 
						ChirdlUtilConstants.FORM_ATTR_AGE_MIN, locationTagId, locationId);
					FormAttributeValue ageMinUnits = chirdlutilbackportsService.getFormAttributeValue(form.getFormId(), 
						ChirdlUtilConstants.FORM_ATTR_AGE_MIN_UNITS, locationTagId, locationId);
					FormAttributeValue ageMax = chirdlutilbackportsService.getFormAttributeValue(form.getFormId(), 
						ChirdlUtilConstants.FORM_ATTR_AGE_MAX, locationTagId, locationId);
					FormAttributeValue ageMaxUnits = chirdlutilbackportsService.getFormAttributeValue(form.getFormId(), 
						ChirdlUtilConstants.FORM_ATTR_AGE_MAX_UNITS, locationTagId, locationId);

					if(ageMin!=null && ageMin.getValue()!=null && ageMinUnits!=null && ageMinUnits.getValue()!=null &&
							ageMax!=null && ageMax.getValue()!=null && ageMaxUnits!=null && ageMaxUnits.getValue()!=null){
						Integer nowAgeWithMinUnits = ageUnitsMinMap.get(ageMinUnits.getValue());
						if (nowAgeWithMinUnits == null) {
							nowAgeWithMinUnits = Util.getAgeInUnits(patient.getBirthdate(), new Date(), 
								ageMinUnits.getValue());
							ageUnitsMinMap.put(ageMinUnits.getValue(), nowAgeWithMinUnits);
						}
						
						Integer nowAgeWithMaxUnits = ageUnitsMaxMap.get(ageMaxUnits.getValue());
						if (nowAgeWithMaxUnits == null) {
							nowAgeWithMaxUnits = Util.getAgeInUnits(patient.getBirthdate(), new Date(), 
								ageMaxUnits.getValue());
							ageUnitsMaxMap.put(ageMaxUnits.getValue(), nowAgeWithMaxUnits);
						}
						
						try{

							if(nowAgeWithMinUnits.intValue()<Integer.parseInt(ageMin.getValue())){
								continue;
							}
							if(nowAgeWithMaxUnits.intValue()>= Integer.parseInt(ageMax.getValue())){
								continue;
							}
						}
						catch(NumberFormatException e){
							continue;
						}
					}

					printableJits.add(formDisplay);
				}
			}
		}
		
		for (FormDisplay formDisplay: printableJits) {
			pw.write(XML_FORCE_PRINT_JIT_START);
			ServletUtil.writeTag(XML_FORM_ID, formDisplay.getFormId(), pw);
			ServletUtil.writeTag(XML_DISPLAY_NAME, ServletUtil.escapeXML(formDisplay.getDisplayName()), pw);
			pw.write(XML_FORCE_PRINT_JIT_END);
		}
		
		ageUnitsMinMap.clear();
		ageUnitsMaxMap.clear();
		
		pw.write(XML_FORCE_PRINT_JITS_END);
	}
	
	/**
	 * Force prints a specified form for a patient..
	 * 
	 * @param request HttServletRequest
	 * @param response HttpServletResponse
	 * @throws IOException
	 */
	private void forcePrintForm(HttpServletRequest request, HttpServletResponse response) throws IOException {	
		String patientIdString = request.getParameter(PARAM_PATIENT_ID);
		String formIdString = request.getParameter(PARAM_FORM_ID);
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
		String formName = null;
		Form form = null;

		// print the form
		Integer formId = null;
		try {
			if (formIdString != null) {
				formId = Integer.parseInt(formIdString);
			}
		} catch (Exception e) {
			String message = "Invalid formId parameter: " + formIdString;
			log.error(message);
			throw new IllegalArgumentException(message);
		}
		
		form = formService.getForm(formId);
		if (form == null) {
			String message = "No form found for formId: " + formIdString;
			log.error(message);
			throw new IllegalArgumentException(message);
		}
		
		formName = form.getName();
		parameters.put(ChirdlUtilConstants.PARAMETER_1, formName);
		parameters.put(ChirdlUtilConstants.PARAMETER_2, ChirdlUtilConstants.FORM_INST_ATTR_VAL_FORCE_PRINT);
		Result result = logicService.eval(patientId, ChirdlUtilConstants.RULE_CREATE_JIT, parameters);
		
		// Check the output type
		FormAttributeValue fav = chirdlutilbackportsService.getFormAttributeValue(
			formId, ChirdlUtilConstants.FORM_ATTR_OUTPUT_TYPE, locationTagId, locationId);
		String outputType = null;
		if (fav == null || fav.getValue() == null || fav.getValue().trim().length() == 0) {
			outputType = Context.getAdministrationService().getGlobalProperty(
				ChirdlUtilConstants.GLOBAL_PROP_DEFAULT_OUTPUT_TYPE);
		} else {
			String[] outputTypes = fav.getValue().split(ChirdlUtilConstants.GENERAL_INFO_COMMA);
			outputType = outputTypes[0];
		}
		
		if (ChirdlUtilConstants.FORM_ATTR_VAL_PDF.equalsIgnoreCase(outputType) || 
				ChirdlUtilConstants.FORM_ATTR_VAL_TELEFORM_PDF.equalsIgnoreCase(outputType)) {
			String formInstanceTag = result.toString();
			locatePatientJITs(response, formInstanceTag);
		} else if (ChirdlUtilConstants.FORM_ATTR_VAL_TELEFORM_XML.equalsIgnoreCase(outputType)) {
			response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_XML);
			response.setHeader(
				ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL, ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL_NO_CACHE);
			PrintWriter pw = response.getWriter();
			FormAttributeValue attributeValue = chirdlutilbackportsService.getFormAttributeValue(form.getFormId(), 
				ChirdlUtilConstants.FORM_ATTR_DISPLAY_NAME, locationTagId, locationId);
			if (attributeValue != null && attributeValue.getValue() != null && attributeValue.getValue().length() > 0) {
				formName = attributeValue.getValue();
			}
			
			String resultMessage = formName + " successfully sent to the printer.";
			response.setContentLength(resultMessage.getBytes().length);
			pw.write(ChirdlUtilConstants.HTML_SPAN_START + resultMessage + ChirdlUtilConstants.HTML_SPAN_END);
		} else {
			response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_XML);
			response.setHeader(
				ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL, ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL_NO_CACHE);
			PrintWriter pw = response.getWriter();
			String message = ChirdlUtilConstants.HTML_SPAN_START + "Invalid outputType attribute '" + outputType + 
					"' found for form: " + formName + ChirdlUtilConstants.HTML_SPAN_END;
			response.setContentLength(message.getBytes().length);
			log.error(message);
			pw.write(message);
			return;
		}
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
		String formIdStr = request.getParameter(PARAM_FORM_ID);
		String locationIdStr = request.getParameter(PARAM_LOCATION_ID);
		String locationTagIdStr = request.getParameter(PARAM_LOCATION_TAG_ID);
		Integer formId = null;
		Integer locationId = null;
		Integer locationTagId = null;
		
		try {
			formId = Integer.parseInt(formIdStr);
		} catch (NumberFormatException e) {
			log.error("Invalid argument formId: " + formIdStr);
			response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_XML);
			response.getWriter().write("Invalid argument formId: " + formIdStr);
			return;
		}
		
		try {
			locationId = Integer.parseInt(locationIdStr);
		} catch (NumberFormatException e) {
			log.error("Invalid argument locationId: " + locationIdStr);
			response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_XML);
			response.getWriter().write("Invalid argument locationId: " + locationIdStr);
			return;
		}
		
		try {
			locationTagId = Integer.parseInt(locationTagIdStr);
		} catch (NumberFormatException e) {
			log.error("Invalid argument locationTagId: " + locationTagIdStr);
			response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_XML);
			response.getWriter().write("Invalid argument locationTagId: " + locationTagIdStr);
			return;
		}
		
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		FormAttributeValue fav = chirdlutilbackportsService.getFormAttributeValue(
			formId, ChirdlUtilConstants.FORM_ATTR_OUTPUT_TYPE, locationTagId, locationId);
		String outputType = null;
		if (fav == null || fav.getValue() == null || fav.getValue().trim().length() == 0) {
			outputType = Context.getAdministrationService().getGlobalProperty(
				ChirdlUtilConstants.GLOBAL_PROP_DEFAULT_OUTPUT_TYPE);
		} else {
			String[] outputTypes = fav.getValue().split(ChirdlUtilConstants.GENERAL_INFO_COMMA);
			outputType = outputTypes[0];
		}
		
		if (ChirdlUtilConstants.FORM_ATTR_VAL_PDF.equalsIgnoreCase(outputType)) {
			response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_APPLICATION_PDF);
		} else {
			response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_XML);
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
}
