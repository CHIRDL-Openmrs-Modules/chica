package org.openmrs.module.chica.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.ParameterHandler;
import org.openmrs.module.atd.xmlBeans.Field;
import org.openmrs.module.chica.ChicaParameterHandler;
import org.openmrs.module.chica.DynamicFormAccess;
import org.openmrs.module.chica.util.PatientRow;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;

/**
 * Servlet giving access to CHICA Mobile clients
 *
 * @author Steve McKee
 */
public class ChicaMobileServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private static final int PRIMARY_FORM = 0;
	private static final int SECONDARY_FORMS = 1;
	private static final String PATIENTS_WITH_PRIMARY_FORM = "patientsWithPrimaryForm";
	private static final String GET_PATTIENT_SECONDARY_FORMS = "getPatientSecondaryForms";
	private static final String VERIFY_PASSCODE = "verifyPasscode";
	private static final String IS_AUTHENTICATED = "isAuthenticated";
	private static final String AUTHENTICATE_USER = "authenticateUser";
	private static final String GET_PRIORITIZED_ELEMENTS = "getPrioritizedElements";
	private static final String SAVE_EXPORT_ELEMENTS = "saveExportElements";
	
	private static final String PARAM_ACTION = "action";
	private static final String PARAM_ENCOUNTER_ID = "encounterId";
	private static final String PARAM_SESSION_ID = "sessionId";
	private static final String PARAM_PASSCODE = "passcode";
	private static final String PARAM_FORM_ID = "formId";
	private static final String PARAM_FORM_INSTANCE_ID = "formInstanceId";
	private static final String PARAM_MAX_ELEMENTS = "maxElements";
	private static final String PARAM_PATIENT_ID = "patientId";
	private static final String PARAM_LOCATION_ID = "locationId";
	private static final String PARAM_LOCATION_TAG_ID = "locationTagId";
	
	private static final String XML_PATIENTS_WITH_FORMS_START = "<patientsWithForms>";
	private static final String XML_PATIENTS_WITH_FORMS_END = "</patientsWithForms>";
	private static final String XML_ERROR_START = "<error>";
	private static final String XML_ERROR_END = "</error>";
	private static final String XML_PATIENT_START = "<patient>";
	private static final String XML_PATIENT_END = "</patient>";
	private static final String XML_ID = "id";
	private static final String XML_MRN = "mrn";
	private static final String XML_FIRST_NAME = "firstName";
	private static final String XML_LAST_NAME = "lastName";
	private static final String XML_APPOINTMENT = "appointment";
	private static final String XML_CHECKIN = "checkin";
	private static final String XML_DATE_OF_BIRTH = "dob";
	private static final String XML_AGE = "age";
	private static final String XML_MD_NAME = "mdName";
	private static final String XML_SEX = "sex";
	private static final String XML_STATION = "station";
	private static final String XML_STATUS = "status";
	private static final String XML_SESSION_ID = "sessionId";
	private static final String XML_ENCOUNTER_ID = "encounterId";
	private static final String XML_REPRINT_STATUS = "reprintStatus";
	private static final String XML_FORM_INSTANCES_START = "<formInstances>";
	private static final String XML_FORM_INSTANCES_END = "</formInstances>";
	private static final String XML_FORM_INSTANCE_START = "<formInstance>";
	private static final String XML_FORM_INSTANCE_END = "</formInstance>";
	private static final String XML_FORM_ID = "formId";
	private static final String XML_FORM_INSTANCE_ID = "formInstanceId";
	private static final String XML_LOCATION_ID = "locationId";
	private static final String XML_URL = "url";
	private static final String XML_PASSCODE_RESULT_START = "<passcodeResult>";
	private static final String XML_PASSCODE_RESULT_END = "</passcodeResult>";
	private static final String XML_RESULT_START = "<result>";
	private static final String XML_RESULT_END = "</result>";
	private static final String XML_RECORDS_START = "<Records>";
	private static final String XML_RECORDS_END = "</Records>";
	private static final String XML_RECORD_START = "<Record>";
	private static final String XML_RECORD_END = "</Record>";
	private static final String XML_VALUE = "Value";
	private static final String XML_FIELD = "Field";
	private static final String XML_FIELD_END = "</Field>";
	private static final String XML_SAVE_RESULT_START = "<saveResult>";
	private static final String XML_SAVE_RESULT_END = "</saveResult>";
	private static final String XML_RESULT = "result";
	private static final String XML_AGE_IN_YEARS = "ageInYears";
	
	private static final Log LOG = LogFactory.getLog(ChicaMobileServlet.class);
	
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
		if (PATIENTS_WITH_PRIMARY_FORM.equals(action)) {
			getPatientsWithPrimaryForm(request, response);
		} else if (GET_PATTIENT_SECONDARY_FORMS.equals(action)) {
			getPatientSecondaryForms(request, response);
		} else if (VERIFY_PASSCODE.equals(action)) {
			verifyPasscode(request, response);
		} else if (IS_AUTHENTICATED.equals(action)) {
			ServletUtil.isUserAuthenticated(response);
		} else if (AUTHENTICATE_USER.equals(action)) {
			ServletUtil.authenticateUser(request, response);
		} else if (GET_PRIORITIZED_ELEMENTS.equals(action)) {
			getPrioritizedElements(request, response);
		} else if (SAVE_EXPORT_ELEMENTS.equals(action)) {
			saveExportElements(request, response);
		}
	}
	
	/**
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	/**
	 * Retrieves patient encounters that have an existing instance of their primary form for the day.
	 * 
	 * @param request HttServletRequest
	 * @param response HttpServletResponse
	 * @throws IOException
	 */
	private void getPatientsWithPrimaryForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
		getPatientsWithForms(request, response, PRIMARY_FORM);
	}
	
	/**
	 * Retrieves instances of a patient's secondary forms for the day.
	 * 
	 * @param request HttServletRequest
	 * @param response HttpServletResponse
	 * @throws IOException
	 */
	private void getPatientSecondaryForms(HttpServletRequest request, HttpServletResponse response) throws IOException {
		getPatientsWithForms(request, response, SECONDARY_FORMS);
	}
	
	/**
	 * Retrieves patient's primary or secondary forms for the day.
	 * 
	 * @param request HttServletRequest
	 * @param response HttpServletResponse
	 * @param formType The type of form (PRIMARY_FORM or SECONDARY_FORMS).
	 * @throws IOException
	 */
	private void getPatientsWithForms(HttpServletRequest request, HttpServletResponse response, int formType) 
	throws IOException {
		Integer sessionId = null;
		String sessionIdStr = request.getParameter(PARAM_SESSION_ID);
		if (sessionIdStr != null) {
			try {
				sessionId = Integer.parseInt(sessionIdStr);
			} catch(NumberFormatException e) {
			    LOG.error("Error parsing sessionId: " + sessionIdStr, e);
			}
		}
		response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_XML);
		response.setHeader(
			ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL, ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL_NO_CACHE);
		PrintWriter pw = response.getWriter();
		pw.write(XML_PATIENTS_WITH_FORMS_START);
		ArrayList<PatientRow> rows = new ArrayList<PatientRow>();
		String result = "";
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		if (Context.getAuthenticatedUser() == null) {
			pw.write(XML_ERROR_START + "Please log in." + XML_ERROR_END);
			pw.write(XML_PATIENTS_WITH_FORMS_END);
			return;
		}
		
		// DWE CHICA-761
		boolean showAllPatients = request.getParameter("showAllPatients") != null && request.getParameter("showAllPatients").equalsIgnoreCase(ChirdlUtilConstants.GENERAL_INFO_TRUE) ? true : false;
		
		try {
			switch (formType) {
				case PRIMARY_FORM:
					result = org.openmrs.module.chica.util.Util.getPatientsWithPrimaryForms(rows, sessionId, showAllPatients);
					break;
				case SECONDARY_FORMS:
					result = org.openmrs.module.chica.util.Util.getPatientSecondaryForms(rows, sessionId);
					break;
			}
				
			if (result == null) {
				PatientRowLoop:
				for (PatientRow row : rows) {
					
					// DWE CHICA-488 Make sure we have a valid formId, formInstanceId, and locationId
					// for each of the form instances before adding this patient to the greaseBoard
					Set<FormInstance> formInstances = row.getFormInstances();
					if(formInstances != null)
					{
						for(FormInstance formInstance : formInstances)
						{
							if(formInstance.getFormId() == null || formInstance.getFormInstanceId() == null || formInstance.getLocationId() == null)
							{
							    LOG.error("Error getting forms for patientId: " + row.getPatientId() + " formId: " 
										+ formInstance.getFormId() 
										+ " formInstanceId: " + formInstance.getFormInstanceId() 
										+ " locationId: " + formInstance.getLocationId() 
										+ ". The patient will not be added to the mobile greaseBoard.");
								continue PatientRowLoop;
							}
						}
					}
					else
					{
					    LOG.error("Error getting forms for patientId: " + row.getPatientId() + ". The patient will not be added to the mobile greaseBoard.");
						continue;
					}
					
					printWriter.write(XML_PATIENT_START);
					ServletUtil.writeTag(XML_ID, row.getPatientId(), printWriter);
					ServletUtil.writeTag(XML_MRN, row.getMrn(), printWriter);
					ServletUtil.writeTag(XML_FIRST_NAME, ServletUtil.escapeXML(row.getFirstName()), printWriter);
					ServletUtil.writeTag(XML_LAST_NAME, ServletUtil.escapeXML(row.getLastName()), printWriter);
					ServletUtil.writeTag(XML_APPOINTMENT, ServletUtil.escapeXML(row.getAppointment()), printWriter);
					ServletUtil.writeTag(XML_CHECKIN, ServletUtil.escapeXML(row.getCheckin()), printWriter);
					ServletUtil.writeTag(XML_DATE_OF_BIRTH, ServletUtil.escapeXML(row.getDob()), printWriter);
					ServletUtil.writeTag(XML_AGE, ServletUtil.escapeXML(row.getAgeAtVisit()), printWriter);
					ServletUtil.writeTag(XML_AGE_IN_YEARS, row.getAgeInYears(), printWriter); // DWE CHICA-884 patient age used to determine if confidentiality pop-up should be displayed
					ServletUtil.writeTag(XML_MD_NAME, ServletUtil.escapeXML(row.getMdName()), printWriter);
					ServletUtil.writeTag(XML_SEX, row.getSex(), printWriter);
					ServletUtil.writeTag(XML_STATION, ServletUtil.escapeXML(row.getStation()), printWriter);
					ServletUtil.writeTag(XML_STATUS, ServletUtil.escapeXML(row.getStatus()), printWriter);
					ServletUtil.writeTag(XML_SESSION_ID, row.getSessionId(), printWriter);
					ServletUtil.writeTag(XML_ENCOUNTER_ID, row.getEncounter().getEncounterId(), printWriter);
					ServletUtil.writeTag(XML_REPRINT_STATUS, row.isReprintStatus(), printWriter);
					printWriter.write(XML_FORM_INSTANCES_START);
					
					if (formInstances != null) {
						for (FormInstance formInstance : formInstances) {
							printWriter.write(XML_FORM_INSTANCE_START);
							ServletUtil.writeTag(XML_FORM_ID, formInstance.getFormId(), printWriter);
							ServletUtil.writeTag(XML_FORM_INSTANCE_ID, formInstance.getFormInstanceId(), printWriter);
							ServletUtil.writeTag(XML_LOCATION_ID, formInstance.getLocationId(), printWriter);
							// If we're looking for a specific patient, lookup the form url
							if (sessionId != null) {
								String url = Util.getFormUrl(formInstance.getFormId());
								if (url != null) {
									ServletUtil.writeTag(XML_URL, ServletUtil.escapeXML(url), printWriter);
								}
							}
							
							printWriter.write(XML_FORM_INSTANCE_END);
						}
					}
					
					printWriter.write(XML_FORM_INSTANCES_END);
					printWriter.write(XML_PATIENT_END);
				}
			}
			
			pw.write(stringWriter.toString());
		}
		catch (Exception e) {
		    LOG.error("Error generating patients with forms", e);
			pw.write(XML_ERROR_START + "An error occurred retrieving the patient list" + XML_ERROR_END);
		}
		
		pw.write(XML_PATIENTS_WITH_FORMS_END);
	}
	
	/**
	 * Verifies the CHICA passcode.
	 * 
	 * @param request HttServletRequest
	 * @param response HttpServletResponse
	 * @throws IOException
	 */
	private void verifyPasscode(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String passcode = request.getParameter(PARAM_PASSCODE);
		response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_XML);
		response.setHeader(ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL, ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL_NO_CACHE);
		PrintWriter pw = response.getWriter();
		pw.write(XML_PASSCODE_RESULT_START);
		pw.write(XML_RESULT_START);
		if (passcode == null || passcode.trim().length() == 0) {
			pw.write("Please enter a passcode.");
		} else if (Context.getAuthenticatedUser() == null) {
			pw.write("Please log in.");
		} else {
			String systemPasscode = Context.getAdministrationService().getGlobalProperty(
				ChirdlUtilConstants.GLOBAL_PROP_PASSCODE);
			if (systemPasscode == null) {
			    LOG.error("Please specify global propery chica.passcode");
				pw.write("Passcode not properly set on server.");
			} else {
				if (systemPasscode.equals(passcode)) {
					pw.write("success");
				} else {
					pw.write("Invalid passcode.");
				}
			}
		}
		
		pw.write(XML_RESULT_END);
		pw.write(XML_PASSCODE_RESULT_END);
	}
	
	/**
	 * Retrieves prioritized elements for a form.
	 * 
	 * @param request HttServletRequest
	 * @param response HttpServletResponse
	 * @throws IOException
	 */
	private void getPrioritizedElements(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Integer formId = Integer.parseInt(request.getParameter(PARAM_FORM_ID));
		Integer formInstanceId = Integer.parseInt(request.getParameter(PARAM_FORM_INSTANCE_ID));
		Integer encounterId = Integer.parseInt(request.getParameter(PARAM_ENCOUNTER_ID));
		Integer maxElements = Integer.parseInt(request.getParameter(PARAM_MAX_ELEMENTS));
		
		DynamicFormAccess formAccess = new DynamicFormAccess();
		List<Field> fields = formAccess.getPrioritizedElements(formId, formInstanceId, encounterId, maxElements);
		
		response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_XML);
		response.setHeader(ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL, ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL_NO_CACHE);
		PrintWriter pw = response.getWriter();
		pw.write(XML_RECORDS_START);
		pw.write(XML_RECORD_START);
		for(Field field : fields){
			pw.write(ChirdlUtilConstants.XML_START_TAG + XML_FIELD + " " + XML_ID + "=\"" + field.getId() + "\"" + 
					ChirdlUtilConstants.XML_END_TAG);
			ServletUtil.writeTag(XML_VALUE, ServletUtil.escapeXML(field.getValue()), pw);
			pw.write(XML_FIELD_END);
		}
		
		pw.write(XML_RECORD_END);
		pw.write(XML_RECORDS_END);
	}
	
	/**
	 * Saves a form's export elements to the database.
	 * 
	 * @param request HttServletRequest
	 * @param response HttpServletResponse
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
    private void saveExportElements(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Integer patientId = Integer.parseInt(request.getParameter(PARAM_PATIENT_ID));
		Integer formId = Integer.parseInt(request.getParameter(PARAM_FORM_ID));
		Integer formInstanceId = Integer.parseInt(request.getParameter(PARAM_FORM_INSTANCE_ID));
		Integer locationId = Integer.parseInt(request.getParameter(PARAM_LOCATION_ID));
		Integer locationTagId = Integer.parseInt(request.getParameter(PARAM_LOCATION_TAG_ID));
		Integer encounterId = Integer.parseInt(request.getParameter(PARAM_ENCOUNTER_ID));
		
		Map<String, String[]> parameterMap = request.getParameterMap();
		response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_XML);
		response.setHeader(ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL, ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL_NO_CACHE);
		PrintWriter pw = response.getWriter();
		pw.write(XML_SAVE_RESULT_START);
		try {
			ParameterHandler parameterHandler = new ChicaParameterHandler();
			DynamicFormAccess formAccess = new DynamicFormAccess();
			Patient patient = Context.getPatientService().getPatient(patientId);
			formAccess.saveExportElements(new FormInstance(locationId, formId, formInstanceId), locationTagId, encounterId, 
				patient, parameterMap, parameterHandler);
			ServletUtil.writeTag(XML_RESULT, ChirdlUtilConstants.FORM_ATTR_VAL_TRUE, pw);
		} catch (Exception e) {
		    LOG.error("Error saving prioritized elements", e);
			ServletUtil.writeTag(XML_RESULT, ChirdlUtilConstants.FORM_ATTR_VAL_FALSE, pw);
		}
		
		pw.write(XML_SAVE_RESULT_END);
	}
}
