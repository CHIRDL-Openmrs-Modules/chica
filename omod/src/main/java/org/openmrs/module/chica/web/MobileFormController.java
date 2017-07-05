package org.openmrs.module.chica.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.FieldType;
import org.openmrs.Form;
import org.openmrs.FormField;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.TeleformTranslator;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.atd.xmlBeans.Field;
import org.openmrs.module.atd.xmlBeans.Record;
import org.openmrs.module.atd.xmlBeans.Records;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstanceTag;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

public class MobileFormController extends SimpleFormController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	private static final String PROVIDER_VIEW = "_provider_view";
	private static final String PROVIDER_SUBMIT = "_provider_submit";
	
	private static final String PARAM_ENCOUNTER_ID = "encounterId";
	private static final String PARAM_SESSION_ID = "sessionId";
	private static final String PARAM_PATIENT_ID = "patientId";
	private static final String PARAM_PATIENT = "patient";
	private static final String PARAM_PROVIDER_ID = "providerId";
	private static final String PARM_FORM_INSTANCE = "formInstance";
	private static final String PARAM_FORM_ID = "formId";
	private static final String PARAM_FORM_INSTANCE_ID = "formInstanceId";
	private static final String PARAM_LOCATION_ID = "locationId";
	private static final String PARAM_LOCATION_TAG_ID = "locationTagId";
	private static final String PARAM_ERROR_MESSAGE = "errorMessage";
	private static final String PARAM_SESSION_TIMEOUT_WARNING = "sessionTimeoutWarning";
	private static final String PARAM_ERROR_PREVIOUS_SUBMISSION = "errorPreviousSubmission";
	private static final String SESSION_ATTRIBUTE_SUBMITTED_FORM_INSTANCES = "submittedFormInstances";
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		return "testing";
	}
	
	@Override
	protected Map<String, Object> referenceData(HttpServletRequest request) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		loadFormData(request, map);
		return map;
	}
	
	@Override
	protected ModelAndView processFormSubmission(HttpServletRequest request, HttpServletResponse response, Object object,
	                                             BindException errors) throws Exception {
		//parse out the location_id,form_id,location_tag_id, and form_instance_id
		//from the selected form
		Map<String, Object> map = new HashMap<String, Object>();
		String patientIdStr = request.getParameter(PARAM_PATIENT_ID);
		String formInstance = request.getParameter(PARM_FORM_INSTANCE);
		String providerId = request.getParameter(PARAM_PROVIDER_ID);
		Integer encounterId = Integer.parseInt(request.getParameter(PARAM_ENCOUNTER_ID));
		FormInstanceTag formInstTag = null;
		if (formInstance != null && formInstance.trim().length() > 0) {
			formInstTag = FormInstanceTag.parseFormInstanceTag(formInstance);
		} else {
			String messagePart1 = "Error signing form: form instance tag parameter not found.";
			String messagePart2 = "Please contact support.";
			String htmlMessage = ServletUtil.writeHtmlErrorMessage(null, null, log, messagePart1, messagePart2);
    		loadFormData(request, map);
    		map.put(PARAM_ERROR_MESSAGE, htmlMessage);
    		return new ModelAndView(getFormView(), map);
		}
		
		try {
			scanForm(formInstTag, request);
		} catch (Exception e) {
			String messagePart1 = "Error signing form.";
			String messagePart2 = "Please contact support with the following information: Form ID: " + 
				formInstTag.getFormId() + " Form Instance ID: " + formInstTag.getFormInstanceId() + 
				" Location ID: " + formInstTag.getLocationId() + " Location Tag ID: " + 
				formInstTag.getLocationTagId();
			String htmlMessage = ServletUtil.writeHtmlErrorMessage(null, e, log, messagePart1, messagePart2);
			loadFormData(request, map);
			map.put(PARAM_ERROR_MESSAGE, htmlMessage);
			return new ModelAndView(getFormView(), map);
		}
		
		String view = getSuccessView();
		
		// Save who is submitting the form.
		if (providerId != null && providerId.trim().length() > 0) {
			Integer patientId = Integer.parseInt(patientIdStr);
			Patient patient = Context.getPatientService().getPatient(patientId);
			saveProviderSubmitter(patient, encounterId, providerId, formInstTag);
		} else {
			String message = "No valid providerId provided.  Cannot log who is submitting form ID: " + formInstTag.getFormId() + 
					" form instance ID: " + formInstTag.getFormInstanceId() + " location ID: " + formInstTag.getLocationId() + 
					" location tag ID: " + formInstTag.getLocationTagId();
			log.error(message);
		}
		
		map.put(PARAM_PATIENT_ID, patientIdStr);
		
		addSubmittedFormInstance(request, formInstance); // CHICA-1004 Add the submitted form instance to the session
		
		return new ModelAndView(new RedirectView(view), map);
	}
	
	/**
	 * Loads the data for the page to display.
	 * 
	 * @param request Request information from the client
	 * @param map Map where all the page parameters will be written
	 */
	private void loadFormData(HttpServletRequest request, Map<String, Object> map) {
		map.put(PARAM_ERROR_MESSAGE, request.getParameter(PARAM_ERROR_MESSAGE));
		
		String encounterIdStr = request.getParameter(PARAM_ENCOUNTER_ID);
		map.put(PARAM_ENCOUNTER_ID, encounterIdStr);
		map.put(PARAM_SESSION_ID, request.getParameter(PARAM_SESSION_ID));
		
		String patientIdStr = request.getParameter(PARAM_PATIENT_ID);
		Patient patient = Context.getPatientService().getPatient(Integer.parseInt(patientIdStr));
		map.put(PARAM_PATIENT, patient);
		
		String providerId = request.getParameter(PARAM_PROVIDER_ID);
		map.put(PARAM_PROVIDER_ID, providerId);
		
		String formInstance = request.getParameter(PARM_FORM_INSTANCE);
		FormInstanceTag formInstTag = FormInstanceTag.parseFormInstanceTag(formInstance);
		Integer locationId = formInstTag.getLocationId();
		Integer formId = formInstTag.getFormId();
		Integer formInstanceId = formInstTag.getFormInstanceId();
		Integer locationTagId = formInstTag.getLocationTagId();
		Integer encounterId = Integer.parseInt(encounterIdStr);
		map.put(PARM_FORM_INSTANCE, formInstance);
		map.put(PARAM_FORM_ID, formId);
		map.put(PARAM_FORM_INSTANCE_ID, formInstanceId);
		map.put(PARAM_LOCATION_ID, locationId);
		map.put(PARAM_LOCATION_TAG_ID, locationTagId);
		
		// CHICA-1004 Check for previous form submission by checking the session variable
		// This will prevent the user from accessing a submitted form using the browser's back arrow
		if(checkForPreviousSubmission(request, formInstance))
		{
			map.put(PARAM_ERROR_PREVIOUS_SUBMISSION, true);
			map.put(PARAM_PATIENT_ID, patientIdStr);
			return;
		}
		
		//Run this to show the form
		try {
			showForm(map, formInstTag);
		} catch (Exception e) {
			String messagePart1 = "Error retrieving data to display the form.";
			String messagePart2 = "Please contact support with the following information: Form ID: " + 
				formId + " Form Instance ID: " + formInstanceId + " Location ID: " + locationId + " Location Tag ID: " + locationTagId;
			String htmlMessage = ServletUtil.writeHtmlErrorMessage(null, e, log, messagePart1, messagePart2);
			map.put(PARAM_ERROR_MESSAGE, htmlMessage);
			return;
		}
		
		// Save who is viewing the form.
		if (providerId != null && providerId.trim().length() > 0) {
			saveProviderViewer(patient, encounterId, providerId, formInstTag);
		} else {
			log.error("Error saving viewing provider ID for form ID: " + formId + " patient ID: " + patientIdStr + 
				" encounter ID: " + encounterId + " provider ID: " + providerId + " form instance ID: " + 
					formInstanceId + " location ID: " + locationId + " location tag ID: " + locationTagId);
		}
		
		// Add session timeout information
		Integer sessionTimeoutWarning = 180;
		String sessionTimeoutWarningStr = Context.getAdministrationService().getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_SESSION_TIMEOUT_WARNING);
		if (sessionTimeoutWarningStr == null || sessionTimeoutWarningStr.trim().length() == 0) {
			log.warn("The " + ChirdlUtilConstants.GLOBAL_PROP_SESSION_TIMEOUT_WARNING + " global property does not have a value set.  180 seconds "
					+ "will be used as a default value.");
		} else {
			try {
				sessionTimeoutWarning = Integer.parseInt(sessionTimeoutWarningStr);
			} catch (NumberFormatException e) {
				log.error("The " + ChirdlUtilConstants.GLOBAL_PROP_SESSION_TIMEOUT_WARNING + " global property is not a valid Integer.  180 seconds "
						+ "will be used as a default value");
				sessionTimeoutWarning = 180;
			}
		}
		
		map.put(PARAM_SESSION_TIMEOUT_WARNING, sessionTimeoutWarning);
	}
	
	private void showForm(Map<String, Object> map, FormInstanceTag formInstanceTag) throws Exception {
		Records records = Context.getService(ATDService.class).getFormRecords(formInstanceTag);
		if (records == null) {
			return;
		}
		
		Record record = records.getRecord();
		if (record == null) {
			return;
		}
		
		List<Field> fields = record.getFields();
		if (fields == null) {
			return;
		}
		
		for (Field field : fields) {
			if (field.getId() != null && field.getValue() != null) {
				map.put(field.getId(), field.getValue());
			}
		}
	}
	
	private void scanForm(FormInstanceTag formInstanceTag, HttpServletRequest request) throws Exception {
		//pull all the input fields from the database for the form
		FormService formService = Context.getFormService();
		HashSet<String> inputFields = new HashSet<String>();
		Form form = formService.getForm(formInstanceTag.getFormId());
		Set<FormField> formFields = form.getFormFields();
		TeleformTranslator translator = new TeleformTranslator();
		FieldType exportFieldType = translator.getFieldType(ChirdlUtilConstants.FORM_FIELD_TYPE_EXPORT);
		for (FormField formField : formFields) {
			org.openmrs.Field currField = formField.getField();
			FieldType fieldType = currField.getFieldType();
			if (fieldType != null && fieldType.equals(exportFieldType)) {
				inputFields.add(currField.getName());
			}
		}
		
		ATDService atdService = Context.getService(ATDService.class);
		Records records = atdService.getFormRecords(formInstanceTag);
		Record record = records.getRecord();
		for (String inputField : inputFields) {
			String inputVal = request.getParameter(inputField);
			if (inputVal == null) {
				// Create a new Field with no value
				Field field = new Field();
				field.setId(inputField);
				record.addField(field);
				continue;
			}
			
			// See if the field exists in the XML
			boolean found = false;
			for (Field currField : record.getFields()) {
				String name = currField.getId();
				if (inputField.equals(name)) {
					found = true;
					currField.setValue(inputVal);
					break;
				}
			}
			
			if (!found) {
				// Create a new Field
				Field field = new Field();
				field.setId(inputField);
				field.setValue(inputVal);
				record.addField(field);
			}
		}
		
		Context.getService(ATDService.class).saveFormRecords(formInstanceTag, records);
	}
	
	/**
	 * Saves the viewer's provider ID to an observation.
	 * 
	 * @param patient The patient who owns the form.
	 * @param formId The ID of the form being viewed.
	 * @param encounterId The encounter ID of the encounter where the form was created.
	 * @param providerId The ID of the provider to be stored.
	 * @param formInstTag The form instance tag information
	 */
	private void saveProviderViewer(Patient patient, Integer encounterId, String providerId, FormInstanceTag formInstTag) {
		Form form = Context.getFormService().getForm(formInstTag.getFormId());
		String conceptName = form.getName() + PROVIDER_VIEW;
		saveProviderInfo(patient, encounterId, providerId, conceptName, formInstTag);
	}
	
	/**
	 * Saves the submitter's provider ID to an observation.
	 * 
	 * @param patient The patient who owns the form.
	 * @param formId The ID of the form being viewed.
	 * @param encounterId The encounter ID of the encounter where the form was created.
	 * @param providerId The ID of the provider to be stored.
	 * @param formInstTag The form instance tag information
	 */
	private void saveProviderSubmitter(Patient patient, Integer encounterId, String providerId, FormInstanceTag formInstTag) {
		Form form = Context.getFormService().getForm(formInstTag.getFormId());
		String conceptName = form.getName() + PROVIDER_SUBMIT;
		saveProviderInfo(patient, encounterId, providerId, conceptName, formInstTag);
	}
	
	/**
	 * Saves the submitter's provider ID to an observation.
	 * 
	 * @param patient The patient who owns the form.
	 * @param formId The ID of the form being viewed.
	 * @param encounterId The encounter ID of the encounter where the form was created.
	 * @param providerId The ID of the provider to be stored.
	 * @param conceptName The name of the concept.
	 * @param formInstTag The form instance tag information
	 */
	private void saveProviderInfo(Patient patient, Integer encounterId, String providerId, String conceptName, FormInstanceTag formInstTag) {
		ConceptService conceptService = Context.getConceptService();
		Concept concept = conceptService.getConceptByName(conceptName);
		if (concept == null) {
			log.error("Could not log provider info.  Concept " + conceptName + " not found.");
			return;
		}
		FormInstance formInstance = new FormInstance(formInstTag.getLocationId(),formInstTag.getFormId(),formInstTag.getFormInstanceId());
		org.openmrs.module.chica.util.Util.saveObsWithStatistics(patient, concept, encounterId, providerId, formInstance, null, formInstTag.getLocationTagId(), null);
	}
	
	/**
	 * CHICA-1004 Store previously submitted form instance in the user's session
	 * @param request
	 */
	@SuppressWarnings("unchecked")
	private void addSubmittedFormInstance(HttpServletRequest request, String formInstance)
	{
		HttpSession session = request.getSession();
		List<String> submittedFormInstances = null;
		Object submittedFormInstancesObj = session.getAttribute(SESSION_ATTRIBUTE_SUBMITTED_FORM_INSTANCES);
		if(submittedFormInstancesObj == null)
		{
			submittedFormInstances = new ArrayList<String>();
			submittedFormInstances.add(formInstance.toString());
		}
		else if(submittedFormInstancesObj != null && submittedFormInstancesObj instanceof List)
		{
			submittedFormInstances =  (List<String>) submittedFormInstancesObj;
			submittedFormInstances.add(formInstance.toString());
		}
		session.setAttribute(SESSION_ATTRIBUTE_SUBMITTED_FORM_INSTANCES, submittedFormInstances);
	}
	
	/**
	 * CHICA-1004 Check for previous form submission by checking the session variable to see if the form instance exists
	 * 
	 * @param request
	 * @param formInstance
	 * @return returns true if the formInstance exists in the submittedFormInstances session variable
	 */
	@SuppressWarnings("unchecked")
	private boolean checkForPreviousSubmission(HttpServletRequest request, String formInstance)
	{
		HttpSession session = request.getSession();
		List<String> submittedFormInstances = null;
		Object submittedFormInstancesObj = session.getAttribute(SESSION_ATTRIBUTE_SUBMITTED_FORM_INSTANCES);

		if(submittedFormInstancesObj != null && submittedFormInstancesObj instanceof List)
		{
			submittedFormInstances =  (List<String>) submittedFormInstancesObj;
			if(submittedFormInstances.contains(formInstance))
			{
				return true;
			}
		}
		
		return false;
	}
}
