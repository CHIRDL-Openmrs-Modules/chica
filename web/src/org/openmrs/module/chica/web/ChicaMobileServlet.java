package org.openmrs.module.chica.web;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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
import org.openmrs.User;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.result.Result;
import org.openmrs.module.atd.ParameterHandler;
import org.openmrs.module.atd.xmlBeans.Field;
import org.openmrs.module.chica.ChicaParameterHandler;
import org.openmrs.module.chica.DynamicFormAccess;
import org.openmrs.module.chica.util.PatientRow;
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
 * Servlet giving access to CHICA information
 *
 * @author Steve McKee
 */
public class ChicaMobileServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static final int PRIMARY_FORM = 0;
	private static final int SECONDARY_FORMS = 1;
	private static final String CREATE_STATE = "JIT_create";
	private static final String OUTPUT_TYPE = "outputType";
	private static final String PDF_OUTPUT_TYPE = "pdf";
	private static final String TRIGGER = "trigger";
	private static final String FORCE_PRINT = "forcePrint";
	private static final String MERGE_DIRECTORY = "defaultMergeDirectory";
	private static final String DISPLAY_NAME = "displayName";
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean authenticated = authenticateUser(request);
		if (!authenticated) {
			response.setHeader("WWW-Authenticate", "BASIC realm=\"chica\"");  
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		}
		
		String action = request.getParameter("action");
		if ("patientsWithPrimaryForm".equals(action)) {
			getPatientsWithPrimaryForm(request, response);
		} else if ("getPatientSecondaryForms".equals(action)) {
			getPatientSecondaryForms(request, response);
		} else if ("verifyPasscode".equals(action)) {
			verifyPasscode(request, response);
		} else if ("isAuthenticated".equals(action)) {
			isUserAuthenticated(response);
		} else if ("authenticateUser".equals(action)) {
			authenticateUser(request, response);
		} else if ("getPrioritizedElements".equals(action)) {
			getPrioritizedElements(request, response);
		} else if ("saveExportElements".equals(action)) {
			saveExportElements(request, response);
		} else if ("getPatientJITs".equals(action)) {
			getPatientJITs(request, response);
		} else if ("getAvailablePatientJITs".equals(action)) {
			getAvailablePatientJITs(request, response);
		} else if ("getForcePrintForms".equals(action)) {
			getForcePrintForms(request, response);
		} else if ("forcePrintForm".equals(action)) {
			forcePrintForm(request, response);
		}
	}
	
	/**
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	private void getPatientsWithPrimaryForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
		getPatientsWithForms(request, response, PRIMARY_FORM);
	}
	
	private void getPatientSecondaryForms(HttpServletRequest request, HttpServletResponse response) throws IOException {
		getPatientsWithForms(request, response, SECONDARY_FORMS);
	}
	
	private void getPatientsWithForms(HttpServletRequest request, HttpServletResponse response, int formType) 
	throws IOException {
		Integer sessionId = null;
		String sessionIdStr = request.getParameter("sessionId");
		if (sessionIdStr != null) {
			try {
				sessionId = Integer.parseInt(sessionIdStr);
			} catch(NumberFormatException e) {
				log.error("Error parsing patientId: " + sessionIdStr, e);
			}
		}
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.write("<patientsWithForms>");
		ArrayList<PatientRow> rows = new ArrayList<PatientRow>();
		String result = "";
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		if (Context.getAuthenticatedUser() == null) {
			pw.write("<error>Please log in.</error>");
			pw.write("</patientsWithForms>");
			return;
		}
		
		try {
			switch (formType) {
				case PRIMARY_FORM:
					result = org.openmrs.module.chica.util.Util.getPatientsWithPrimaryForms(rows, sessionId);
					break;
				case SECONDARY_FORMS:
					result = org.openmrs.module.chica.util.Util.getPatientSecondaryForms(rows, sessionId);
					break;
			}
				
			if (result == null) {
				for (PatientRow row : rows) {
					printWriter.write("<patient>");
					writeTag("id", row.getPatientId(), printWriter);
					writeTag("mrn", row.getMrn(), printWriter);
					writeTag("firstName", escapeXML(row.getFirstName()), printWriter);
					writeTag("lastName", escapeXML(row.getLastName()), printWriter);
					writeTag("appointment", escapeXML(row.getAppointment()), printWriter);
					writeTag("checkin", escapeXML(row.getCheckin()), printWriter);
					writeTag("dob", escapeXML(row.getDob()), printWriter);
					writeTag("age", escapeXML(row.getAgeAtVisit()), printWriter);
					writeTag("mdName", escapeXML(row.getMdName()), printWriter);
					writeTag("sex", row.getSex(), printWriter);
					writeTag("station", escapeXML(row.getStation()), printWriter);
					writeTag("status", escapeXML(row.getStatus()), printWriter);
					writeTag("sessionId", row.getSessionId(), printWriter);
					writeTag("encounterId", row.getEncounter().getEncounterId(), printWriter);
					writeTag("reprintStatus", row.isReprintStatus(), printWriter);
					printWriter.write("<formInstances>");
					Set<FormInstance> formInstances = row.getFormInstances();
					if (formInstances != null) {
						for (FormInstance formInstance : formInstances) {
							printWriter.write("<formInstance>");
							writeTag("formId", formInstance.getFormId(), printWriter);
							writeTag("formInstanceId", formInstance.getFormInstanceId(), printWriter);
							writeTag("locationId", formInstance.getLocationId(), printWriter);
							// If we're looking for a specific patient, lookup the form url
							if (sessionId != null) {
								String url = Util.getFormUrl(formInstance.getFormId());
								if (url != null) {
									writeTag("url", url, printWriter);
								}
							}
							
							printWriter.write("</formInstance>");
						}
					}
					
					printWriter.write("</formInstances>");
					printWriter.write("</patient>");
				}
			}
			
			pw.write(stringWriter.toString());
		}
		catch (Exception e) {
			log.error("Error generating patients with forms", e);
			pw.write("<error>An error occurred retrieving the patient list</error>");
		}
		
		pw.write("</patientsWithForms>");
	}
	
	private void verifyPasscode(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String passcode = request.getParameter("passcode");
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.write("<passcodeResult>");
		pw.write("<result>");
		if (passcode == null || passcode.trim().length() == 0) {
			pw.write("Please enter a passcode.");
		} else if (Context.getAuthenticatedUser() == null) {
			pw.write("Please log in.");
		} else {
			String systemPasscode = Context.getAdministrationService().getGlobalProperty("chica.passcode");
			if (systemPasscode == null) {
				log.error("Please specify global propery chica.passcode");
				pw.write("Passcode not properly set on server.");
			} else {
				if (systemPasscode.equals(passcode)) {
					pw.write("success");
				} else {
					pw.write("Invalid passcode.");
				}
			}
		}
		
		pw.write("</result>");
		pw.write("</passcodeResult>");
	}
	
	private String escapeXML(String str) {
		if (str == null) {
			return str;
		}
		
		return str.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&quot;")
		        .replaceAll("'", "&apos;");
	}
	
	private void writeTag(String tagName, Object value, PrintWriter pw) {
		pw.write("<" + tagName + ">");
		if (value != null) {
			pw.write(value.toString());
		}
		
		pw.write("</" + tagName + ">");
	}
	
	private void isUserAuthenticated(HttpServletResponse response) throws IOException {
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
	
	private void authenticateUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.write("<userAuthenticated>");
		pw.write("<result>");
		pw.write("true");
		pw.write("</result>");
		pw.write("</userAuthenticated>");
	}
	
	private void getPrioritizedElements(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Integer formId = Integer.parseInt(request.getParameter("formId"));
		Integer formInstanceId = Integer.parseInt(request.getParameter("formInstanceId"));
		Integer encounterId = Integer.parseInt(request.getParameter("encounterId"));
		Integer maxElements = Integer.parseInt(request.getParameter("maxElements"));
		
		DynamicFormAccess formAccess = new DynamicFormAccess();
		List<Field> fields = formAccess.getPrioritizedElements(formId, formInstanceId, encounterId, maxElements);
		
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.write("<Records>");
		pw.write("<Record>");
		for(Field field : fields){
			pw.write("<Field id=\"" + field.getId() + "\">");
			pw.write("<Value>" + field.getValue() + "</Value>");
			pw.write("</Field>");
		}
		
		pw.write("</Record>");
		pw.write("</Records>");
	}
	
	private void saveExportElements(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Integer patientId = Integer.parseInt(request.getParameter("patientId"));
		Integer formId = Integer.parseInt(request.getParameter("formId"));
		Integer formInstanceId = Integer.parseInt(request.getParameter("formInstanceId"));
		Integer locationId = Integer.parseInt(request.getParameter("locationId"));
		Integer locationTagId = Integer.parseInt(request.getParameter("locationTagId"));
		Integer encounterId = Integer.parseInt(request.getParameter("encounterId"));
		
		Map<String, String[]> parameterMap = request.getParameterMap();
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.write("<saveResult>");
		try {
			ParameterHandler parameterHandler = new ChicaParameterHandler();
			DynamicFormAccess formAccess = new DynamicFormAccess();
			Patient patient = Context.getPatientService().getPatient(patientId);
			formAccess.saveExportElements(new FormInstance(locationId, formId, formInstanceId), locationTagId, encounterId, 
				patient, parameterMap, parameterHandler);
			pw.write("<result>true</result>");
		} catch (Exception e) {
			log.error("Error saving prioritized elements", e);
			pw.write("<result>false</result>");
		}
		
		pw.write("</saveResult>");
	}
	
	private boolean authenticateUser(HttpServletRequest request) throws IOException {
		if (Context.getAuthenticatedUser() != null) {
			return true;
		}
		
		String auth = request.getHeader("Authorization");
		if (auth == null) {
            return false;  // no auth
        }
        if (!auth.toUpperCase().startsWith("BASIC ")) { 
            return false;  // we only do BASIC
        }
        // Get encoded user and password, comes after "BASIC "
        String userpassEncoded = auth.substring(6);
        // Decode it, using any base 64 decoder
        sun.misc.BASE64Decoder dec = new sun.misc.BASE64Decoder();
        String userpassDecoded = new String(dec.decodeBuffer(userpassEncoded));
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
	
	private void getAvailablePatientJITs(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.write("<availableJITs>");
		
		Integer encounterId = Integer.parseInt(request.getParameter("encounterId"));
		
		ChirdlUtilBackportsService backportsService = Context.getService(ChirdlUtilBackportsService.class);
		
		State createState = backportsService.getStateByName(CREATE_STATE);
		if (createState == null) {
			log.error("The state " + CREATE_STATE + " does not exist.  No patient JITs will be retrieved.");
			pw.write("</availableJITs>");
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
			FormAttributeValue fav = backportsService.getFormAttributeValue(formId, OUTPUT_TYPE, locationTagId, locationId);
			if (fav == null || fav.getValue() == null) {
				continue;
			}
			
			String value = fav.getValue();
			String[] values = value.split(",");
			boolean isPdfType = false;
			for (String favValue : values) {
				if (PDF_OUTPUT_TYPE.equals(favValue)) {
					isPdfType = true;
					break;
				}
			}
			
			if (!isPdfType) {
				continue;
			}
			
			// Make sure the form wasn't force printed.
			fav = backportsService.getFormAttributeValue(formId, TRIGGER, locationTagId, locationId);
			if (fav != null && FORCE_PRINT.equals(fav.getValue())) {
				continue;
			}
			
			// Get the merge directory for the form.
			fav = backportsService.getFormAttributeValue(formId, MERGE_DIRECTORY, locationTagId, locationId);
			if (fav == null || fav.getValue() == null || fav.getValue().trim().length() == 0) {
				continue;
			}
			
			// Find the merge PDF file.
			String mergeDirectory = fav.getValue();
			File pdfDir = new File(mergeDirectory, "pdf");
			File mergeFile = new File(pdfDir, locationId + "_" + formId + "_" + formInstanceId + ".pdf");
			if (!mergeFile.exists()) {
				mergeFile = new File(pdfDir, "_" + locationId + "_" + formId + "_" + formInstanceId + "_.pdf");
				if (!mergeFile.exists()) {
					continue;
				}
			}
			
			Form form = Context.getFormService().getForm(formId);
			String formName = null;
			
			// Try to get a display name if one exists.
			fav = backportsService.getFormAttributeValue(formId, DISPLAY_NAME, locationTagId, locationId);
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
				pw.write("<availableJIT>");
				pw.write("<formName>" + formName + "</formName>");
				pw.write("<formId>" + tag.getFormId() + "</formId>");
				pw.write("<formInstanceId>" + tag.getFormInstanceId() + "</formInstanceId>");
				pw.write("<locationId>" + tag.getLocationId() + "</locationId>");
				pw.write("<locationTagId>" + tag.getLocationTagId() + "</locationTagId>");
				pw.write("</availableJIT>");
			}
		}
		
		pw.write("</availableJITs>");
	}
	
	private void getPatientJITs(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String formInstances = request.getParameter("formInstances");
		locatePatientJITs(response, formInstances);
	}
	
	private void locatePatientJITs(HttpServletResponse response, String formInstances) 
			throws IOException {
		if (formInstances == null) {
			return;
		}
		
		ChirdlUtilBackportsService backportsService = Context.getService(ChirdlUtilBackportsService.class);
		State createState = backportsService.getStateByName(CREATE_STATE);
		if (createState == null) {
			log.error("The state " + CREATE_STATE + " does not exist.  No patient JITs will be retrieved.");
			return;
		}
		
		List<String> filesToCombine = new ArrayList<String>();
		for (String formInstance : formInstances.split(",")) {
			FormInstanceTag formInstanceTag = 
					org.openmrs.module.chirdlutilbackports.util.Util.parseFormInstanceTag(formInstance);
			if (formInstanceTag == null) {
				continue;
			}
			
			Integer locationId = formInstanceTag.getLocationId();
			Integer formId = formInstanceTag.getFormId();
			Integer formInstanceId = formInstanceTag.getFormInstanceId();
			Integer locationTagId = formInstanceTag.getLocationTagId();
			
			// Get the merge directory for the form.
			FormAttributeValue fav = 
					backportsService.getFormAttributeValue(formId, MERGE_DIRECTORY, locationTagId, locationId);
			if (fav == null || fav.getValue() == null || fav.getValue().trim().length() == 0) {
				continue;
			}
			
			// Find the merge PDF file.
			String mergeDirectory = fav.getValue();
			File pdfDir = new File(mergeDirectory, "pdf");
			File mergeFile = new File(pdfDir, locationId + "_" + formId + "_" + formInstanceId + ".pdf");
			if (!mergeFile.exists()) {
				mergeFile = new File(pdfDir, "_" + locationId + "_" + formId + "_" + formInstanceId + "_.pdf");
				if (!mergeFile.exists()) {
					continue;
				}
			}
			
			filesToCombine.add(mergeFile.getAbsolutePath());
		}
		
		if (filesToCombine.size() == 0) {
			return;
		} 
		
		response.setContentType("application/pdf");
		response.addHeader("Content-Disposition", "inline;filename=patientJITS.pdf");
		response.addHeader("Set-Cookie", "fileDownload=true;path=/openmrs");
		
		if (filesToCombine.size() == 1) {
			String filePath = null;
			try {
				Document document = new Document();
		        PdfCopy copy = new PdfCopy(document, response.getOutputStream());
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
			} catch (BadPdfFormatException e) {
				log.error("Bad PDF found: " + filePath, e);
				throw new IOException(e);
			} catch (DocumentException e) {
				log.error("Error handling PDF document", e);
				throw new IOException(e);
			}
		} else {
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
	
	private static byte[] renamePdfFields(String datasheet, int i) throws IOException, DocumentException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// Create the stamper
		PdfStamper stamper = new PdfStamper(new PdfReader(datasheet), baos);
		// Get the fields
		AcroFields form = stamper.getAcroFields();
		// Loop over the fields
		Set<String> keys = new HashSet<String>(form.getFields().keySet());
		for (String key : keys) {
			// rename the fields
			form.renameField(key, String.format("%s_%d", key, i));
		}
		// close the stamper
		stamper.close();
		return baos.toByteArray();
	}
	
	private void getForcePrintForms(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.write("<forcePrintJITs>");
		
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		PatientService patientService = Context.getPatientService();
		String patientIdString = request.getParameter("patientId");
		Integer patientId = null;
		try {
			patientId = Integer.parseInt(patientIdString);
		}
		catch (Exception e) {
			String message = "Invalid patientId parameter provided: " + patientIdString;
			log.error(message);
			throw new IllegalArgumentException(message);
		}
		
		String sessionIdString = request.getParameter("sessionId");
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
			EncounterService encounterService = Context.getEncounterService();
			List<org.openmrs.Encounter> list = encounterService.getEncountersByPatientId(patientId);
			if (list != null && list.size() > 0) {
				Encounter encounter = list.get(0);
				ChirdlUtilBackportsService chirdlUtilBackportsService = Context
				        .getService(ChirdlUtilBackportsService.class);
				State checkinState = chirdlUtilBackportsService.getStateByName("CHECKIN");
				Integer encounterId = encounter.getEncounterId();
				List<PatientState> checkinStates = chirdlUtilBackportsService.getPatientStateByEncounterState(
				    encounterId, checkinState.getStateId());
				if (checkinStates != null && checkinStates.size() > 0) {
					PatientState patientState = checkinStates.get(0);
					sessionId = patientState.getSessionId();
				}
			}
		}
		
		if (sessionId == null) {
			String message = "Could not find a valid sessionId for patient: " + patientIdString;
			log.error(message);
			throw new IllegalArgumentException(message);
		}
		
		
		User user = Context.getUserContext().getAuthenticatedUser();
		Location location = null;
		String locationString = request.getParameter("locationId");
		LocationService locationService = Context.getLocationService();
		if (locationString == null || locationString.trim().length() == 0) {
			locationString = user.getUserProperty("location");
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
		
		String locationTags = request.getParameter("locationTagId");
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
				locationTags = user.getUserProperty("locationTags");
				if (locationTags != null) {
					StringTokenizer tokenizer = new StringTokenizer(locationTags, ",");
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
		
		Patient patient = patientService.getPatient(patientId);
		FormService formService = Context.getFormService();
		List<FormAttributeValue> attributes = chirdlutilbackportsService.getFormAttributesByName("forcePrintable");
		Map<String, Integer> ageUnitsMinMap = new HashMap<String, Integer>();
		Map<String, Integer> ageUnitsMaxMap = new HashMap<String, Integer>();
		Set<FormDisplay> printableJits = new TreeSet<FormDisplay>();
		for (FormAttributeValue attribute : attributes) {
			if (attribute.getValue().equalsIgnoreCase("true") && attribute.getLocationId().equals(locationId) && 
					attribute.getLocationTagId().equals(locationTagId)) {
				Form form = formService.getForm(attribute.getFormId());
				if (!form.getRetired()) {
					FormDisplay formDisplay = new FormDisplay();
					formDisplay.setFormName(form.getName());
					formDisplay.setFormId(form.getFormId());
					FormAttributeValue attributeValue = chirdlutilbackportsService.getFormAttributeValue(form.getFormId(), 
						"displayName", locationTagId, locationId);
					if (attributeValue == null || attributeValue.getValue() == null) {
						formDisplay.setDisplayName(form.getName());
					} else {
						formDisplay.setDisplayName(attributeValue.getValue());
					}
					
					FormAttributeValue ageMin = chirdlutilbackportsService.getFormAttributeValue(form.getFormId(), 
						"ageMin", locationTagId, locationId);
					FormAttributeValue ageMinUnits = chirdlutilbackportsService.getFormAttributeValue(form.getFormId(), 
						"ageMinUnits", locationTagId, locationId);
					FormAttributeValue ageMax = chirdlutilbackportsService.getFormAttributeValue(form.getFormId(), "ageMax",
						locationTagId, locationId);
					FormAttributeValue ageMaxUnits = chirdlutilbackportsService.getFormAttributeValue(form.getFormId(), 
						"ageMaxUnits", locationTagId, locationId);

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
			pw.write("<forcePrintJIT>");
			pw.write("<formId>" + formDisplay.getFormId() + "</formId>");
			pw.write("<displayName>" + formDisplay.getDisplayName() + "</displayName>");
			pw.write("</forcePrintJIT>");
		}
		
		ageUnitsMinMap.clear();
		ageUnitsMaxMap.clear();
		
		pw.write("</forcePrintJITs>");
	}
	
	private void forcePrintForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String patientIdString = request.getParameter("patientId");
		String formIdString = request.getParameter("formId");
		String sessionIdString = request.getParameter("sessionId");

		Integer patientId = null;
		try {
			if (patientIdString != null) {
				patientId = Integer.parseInt(patientIdString);
			}
		}
		catch (Exception e) {
			String message = "Invalid patientId parameter provided: " + patientIdString;
			log.error(message);
			throw new IllegalArgumentException(message);
		}
		
		Integer sessionId = null;
		try {
			if (sessionIdString != null) {
				sessionId = Integer.parseInt(sessionIdString);
			}
		}
		catch (Exception e) {
			String message = "Invalid sessionId parameter provided: " + sessionIdString;
			log.error(message);
			throw new IllegalArgumentException(message);
		}
		
		LogicService logicService = Context.getLogicService();
		
		//print the form
		User user = Context.getUserContext().getAuthenticatedUser();
		Location location = null;
		String locationString = request.getParameter("locationId");
		LocationService locationService = Context.getLocationService();
		if (locationString == null || locationString.trim().length() == 0) {
			locationString = user.getUserProperty("location");
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
		
		String locationTags = request.getParameter("locationTagId");
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
			locationTags = user.getUserProperty("locationTags");
			if (locationTags != null) {
				StringTokenizer tokenizer = new StringTokenizer(locationTags, ",");
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
		parameters.put("sessionId", sessionId);
		parameters.put("locationTagId", locationTagId);
		FormInstance formInstance = new FormInstance();
		formInstance.setLocationId(locationId);
		parameters.put("formInstance", formInstance);
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
		parameters.put("param1", formName);
		parameters.put("param2", "forcePrint");
		Result result = logicService.eval(patientId, "CREATE_JIT", parameters);
		
		// Check the output type
		FormAttributeValue fav = chirdlutilbackportsService.getFormAttributeValue(
			formId, "outputType", locationTagId, locationId);
		String outputType = null;
		if (fav == null || fav.getValue() == null || fav.getValue().trim().length() == 0) {
			outputType = Context.getAdministrationService().getGlobalProperty("atd.defaultOutputType");
		} else {
			String[] outputTypes = fav.getValue().split(",");
			outputType = outputTypes[0];
		}
		
		if ("pdf".equalsIgnoreCase(outputType)) {
			String formInstanceTag = result.toString();
			locatePatientJITs(response, formInstanceTag);
		} else if ("teleformXML".equalsIgnoreCase(outputType)) {
			response.setContentType("text/xml");
			response.setHeader("Cache-Control", "no-cache");
			PrintWriter pw = response.getWriter();
			FormAttributeValue attributeValue = chirdlutilbackportsService.getFormAttributeValue(form.getFormId(), 
				"displayName", locationTagId, locationId);
			if (attributeValue != null && attributeValue.getValue() != null && attributeValue.getValue().length() > 0) {
				formName = attributeValue.getValue();
			}
			
			String resultMessage = formName + " successfully sent to the printer.";
			pw.write("<span>" + resultMessage + "</span>");
		} else {
			response.setContentType("text/xml");
			response.setHeader("Cache-Control", "no-cache");
			PrintWriter pw = response.getWriter();
			String message = "<span>Invalid outputType attribute '" + outputType + "' found for form: " + formName + 
					"</span>";
			log.error(message);
			pw.write(message);
			return;
		}
	}
}
