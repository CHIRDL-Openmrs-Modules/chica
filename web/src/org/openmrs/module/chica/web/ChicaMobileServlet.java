package org.openmrs.module.chica.web;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
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
		}
	}
	
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
			
			pw.write("<availableJIT>");
			pw.write("<formName>" + formName + "</formName>");
			pw.write("<formId>" + formId + "</formId>");
			pw.write("<formInstanceId>" + formInstanceId + "</formInstanceId>");
			pw.write("<locationId>" + locationId + "</locationId>");
			pw.write("<locationTagId>" + locationTagId + "</locationTagId>");
			String desc = form.getDescription();
			if (desc != null && desc.trim().length() > 0) {
				pw.write("<description>" + desc + "</description>");
			}
			
			pw.write("</availableJIT>");
		}
		
		pw.write("</availableJITs>");
	}
	
	private void getPatientJITs(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String formInstances = request.getParameter("formInstances");
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
		response.addHeader("Content-Disposition", "attachment;filename=patientJITS.pdf");
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
}
