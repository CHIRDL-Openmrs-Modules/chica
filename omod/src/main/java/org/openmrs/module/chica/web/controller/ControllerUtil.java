package org.openmrs.module.chica.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.FieldType;
import org.openmrs.Form;
import org.openmrs.FormField;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Patient;
import org.openmrs.PersonName;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.TeleformTranslator;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.atd.xmlBeans.Field;
import org.openmrs.module.atd.xmlBeans.Record;
import org.openmrs.module.atd.xmlBeans.Records;
import org.openmrs.module.chica.util.ChicaConstants;
import org.openmrs.module.chica.util.Util;
import org.openmrs.module.chica.web.ServletUtil;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.ChirdlLocationAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.EncounterAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstanceTag;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Session;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

/**
 * Utility class for shared methods between controllers.
 * 
 * @author Steve McKee
 */
public class ControllerUtil {

	private static final Logger log = LoggerFactory.getLogger(ControllerUtil.class);
	
	/**
	 * Private constructor
	 */
	private ControllerUtil() {
		// Purposefully left blank
	}

	/**
	 * Loads the data for the page to display.
	 * 
	 * @param request Request information from the client
	 * @param map     Map where all the page parameters will be written
	 */
	public static void loadFormData(HttpServletRequest request, Map<String, Object> map) {
		String errorMessage = request.getParameter(ChirdlUtilConstants.PARAMETER_ERROR_MESSAGE);
		String providerId = request.getParameter(ChirdlUtilConstants.PARAMETER_PROVIDER_ID);
		String formInstance = request.getParameter(ChirdlUtilConstants.PARAMETER_FORM_INSTANCE);
		String encounterIdStr = request.getParameter(ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID);
		Integer encounterId = Integer.valueOf(encounterIdStr);
		String sessionIdStr = request.getParameter(ChirdlUtilConstants.PARAMETER_SESSION_ID);
		Integer sessionId = Integer.valueOf(sessionIdStr);
		String patientIdStr = request.getParameter(ChirdlUtilConstants.PARAMETER_PATIENT_ID);
		Integer patientId = Integer.valueOf(patientIdStr);
		
		loadFormData(
				request.getSession(), errorMessage, providerId, formInstance, encounterId, sessionId, patientId, map);
	}
	
	/**
	 * Loads the data for the page to display.
	 * 
	 * @param session The HTTP session
	 * @param errorMessage An error message to load if present
	 * @param providerId The provider identifier
	 * @param formInstance The form instance identifier
	 * @param encounterId The encounter identifier
	 * @param sessionId The session identifier
	 * @param patientId the patient identifier
	 * @param map Map where all the page parameters will be written
	 */
	public static void loadFormData(HttpSession session, String errorMessage, String providerId, String formInstance, 
			Integer encounterId, Integer sessionId, Integer patientId, Map<String, Object> map) {
		map.put(ChirdlUtilConstants.PARAMETER_ERROR_MESSAGE, errorMessage);

		map.put(ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID, encounterId);
		map.put(ChirdlUtilConstants.PARAMETER_SESSION_ID, sessionId);

		org.openmrs.Patient patient = Context.getPatientService().getPatient(patientId);
		map.put(ChirdlUtilConstants.PARAMETER_PATIENT, patient);

		map.put(ChirdlUtilConstants.PARAMETER_PROVIDER_ID, providerId);

		FormInstanceTag formInstTag = FormInstanceTag.parseFormInstanceTag(formInstance);
		Integer locationId = formInstTag.getLocationId();
		Integer formId = formInstTag.getFormId();
		Integer formInstanceId = formInstTag.getFormInstanceId();
		Integer locationTagId = formInstTag.getLocationTagId();
		map.put(ChirdlUtilConstants.PARAMETER_FORM_INSTANCE, formInstance);
		map.put(ChirdlUtilConstants.PARAMETER_FORM_ID, formId);
		map.put(ChirdlUtilConstants.PARAMETER_FORM_INSTANCE_ID, formInstanceId);
		map.put(ChirdlUtilConstants.PARAMETER_LOCATION_ID, locationId);
		map.put(ChirdlUtilConstants.PARAMETER_LOCATION_TAG_ID, locationTagId);
		map.put(ChirdlUtilConstants.PARAMETER_PDF_VIEWER, getPdfViewer(locationId));

		// CHICA-1004 Check for previous form submission by checking the session
		// variable
		// This will prevent the user from accessing a submitted form using the
		// browser's back arrow
		if (checkForPreviousSubmission(session, formInstance)) {
			map.put(ChicaConstants.PARAMETER_ERROR_PREVIOUS_SUBMISSION, Boolean.TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_PATIENT_ID, patientId);
			return;
		}

		// Run this to show the form
		try {
			showForm(map, formInstTag);
		} catch (Exception e) {
			String messagePart1 = "Error retrieving data to display the form.";
			String messagePart2 = "Please contact support with the following information: Form ID: " + formId
					+ " Form Instance ID: " + formInstanceId + " Location ID: " + locationId + " Location Tag ID: "
					+ locationTagId;
			String htmlMessage = ServletUtil.writeHtmlErrorMessage(null, e, log, messagePart1, messagePart2);
			map.put(ChirdlUtilConstants.PARAMETER_ERROR_MESSAGE, htmlMessage);
			return;
		}

		// Save who is viewing the form.
		if (providerId != null && providerId.trim().length() > 0) {
			saveProviderViewer(patient, encounterId, providerId, formInstTag);
		} else {
			log.error("Error saving viewing provider ID for form ID: {} patient ID: {} encounter ID: {} provider ID: {} form instance ID:{} location ID: {} location tag ID: {}",
					formId,patientId,encounterId,providerId,formInstanceId,locationId,locationTagId);
		}

		map.put(ChicaConstants.PARAMETER_SESSION_TIMEOUT_WARNING, getSessionTimeoutWarning());
	}

	/**
	 * CHICA-1004 Check for previous form submission by checking the session
	 * variable to see if the form instance exists
	 * 
	 * @param session
	 * @param formInstance
	 * @return returns true if the formInstance exists in the submittedFormInstances
	 *         session variable
	 */
	@SuppressWarnings("unchecked")
	public static boolean checkForPreviousSubmission(HttpSession session, String formInstance) {
		List<String> submittedFormInstances = null;
		Object submittedFormInstancesObj = session
				.getAttribute(ChicaConstants.SESSION_ATTRIBUTE_SUBMITTED_FORM_INSTANCES);

		if (submittedFormInstancesObj instanceof List) {
			submittedFormInstances = (List<String>) submittedFormInstancesObj;
			if (submittedFormInstances.contains(formInstance)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Loads the form fields and values into the provided map.
	 * 
	 * @param map The map with the key as the field ID and the value as the field value.
	 * @param formInstanceTag The form instance and location tag information to find the form file.
	 */
	public static void showForm(Map<String, Object> map, FormInstanceTag formInstanceTag) {
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

	/**
	 * Saves the viewer's provider ID to an observation.
	 * 
	 * @param patient     The patient who owns the form.
	 * @param formId      The ID of the form being viewed.
	 * @param encounterId The encounter ID of the encounter where the form was
	 *                    created.
	 * @param providerId  The ID of the provider to be stored.
	 * @param formInstTag The form instance tag information
	 */
	public static void saveProviderViewer(org.openmrs.Patient patient, Integer encounterId, String providerId,
			FormInstanceTag formInstTag) {
		Form form = Context.getFormService().getForm(formInstTag.getFormId());
		String conceptName = form.getName() + ChicaConstants.PROVIDER_VIEW;
		saveProviderInfo(patient, encounterId, providerId, conceptName, formInstTag);
	}

	/**
	 * Saves the submitter's provider ID to an observation.
	 * 
	 * @param patient     The patient who owns the form.
	 * @param formId      The ID of the form being viewed.
	 * @param encounterId The encounter ID of the encounter where the form was
	 *                    created.
	 * @param providerId  The ID of the provider to be stored.
	 * @param formInstTag The form instance tag information
	 */
	public static void saveProviderSubmitter(org.openmrs.Patient patient, Integer encounterId, String providerId,
			FormInstanceTag formInstTag) {
		Form form = Context.getFormService().getForm(formInstTag.getFormId());
		String conceptName = form.getName() + ChicaConstants.PROVIDER_SUBMIT;
		saveProviderInfo(patient, encounterId, providerId, conceptName, formInstTag);
	}

	/**
	 * Saves the submitter's provider ID to an observation.
	 * 
	 * @param patient     The patient who owns the form.
	 * @param formId      The ID of the form being viewed.
	 * @param encounterId The encounter ID of the encounter where the form was
	 *                    created.
	 * @param providerId  The ID of the provider to be stored.
	 * @param conceptName The name of the concept.
	 * @param formInstTag The form instance tag information
	 */
	public static void saveProviderInfo(org.openmrs.Patient patient, Integer encounterId, String providerId,
			String conceptName, FormInstanceTag formInstTag) {
		ConceptService conceptService = Context.getConceptService();
		Concept concept = conceptService.getConceptByName(conceptName);
		if (concept == null) {
			log.error("Could not log provider info.  Concept {} not found.",conceptName);
			return;
		}
		FormInstance formInstance = new FormInstance(formInstTag.getLocationId(), formInstTag.getFormId(),
				formInstTag.getFormInstanceId());
		org.openmrs.module.chica.util.Util.saveObsWithStatistics(patient, concept, encounterId.intValue(), providerId,
				formInstance, null, formInstTag.getLocationTagId(), null);
	}

	/**
	 * Saves form data saved as parameters in the provided HTTP servlet request object.
	 * 
	 * @param formInstanceTag The form instance and location tag information to find the form file.
	 * @param request The HTTP request object used to extract the saved form field values.
	 */
	public static void scanForm(FormInstanceTag formInstanceTag, HttpServletRequest request) {
		// pull all the input fields from the database for the form
		FormService formService = Context.getFormService();
		Set<String> inputFields = new HashSet<>();
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
	 * CHICA-1004 Store previously submitted form instance in the user's session
	 * 
	 * @param request
	 */
	@SuppressWarnings("unchecked")
	public static void addSubmittedFormInstance(HttpServletRequest request, String formInstance) {
		HttpSession session = request.getSession();
		List<String> submittedFormInstances = null;
		Object submittedFormInstancesObj = session
				.getAttribute(ChicaConstants.SESSION_ATTRIBUTE_SUBMITTED_FORM_INSTANCES);
		if (submittedFormInstancesObj == null) {
			submittedFormInstances = new ArrayList<>();
			submittedFormInstances.add(formInstance);
		} else if (submittedFormInstancesObj instanceof List) {
			submittedFormInstances = (List<String>) submittedFormInstancesObj;
			submittedFormInstances.add(formInstance);
		}
		session.setAttribute(ChicaConstants.SESSION_ATTRIBUTE_SUBMITTED_FORM_INSTANCES, submittedFormInstances);
	}
	
	/**
	 * Retrieves the latest encounters with observations that contains the provided
	 * start state but not the end state for the provided form and patient.
	 * 
	 * @param encounters       encounters list of latest encounters
	 * @param backportsService ChirdlUtilBackportsService object
	 * @param map              Map that will be returned to the client
	 * @return Encounter object or null if one is not found.
	 */
	public static Encounter getPhysicianEncounterWithoutScannedTimeStamp(
			List<Encounter> encounters, ChirdlUtilBackportsService backportsService,
			Map<String, Object> map) {
		return getEncounterWithoutScannedTimeStamp(encounters, backportsService, map, true);
	}
	
	/**
	 * Retrieves the latest encounters with observations that contains the provided
	 * start state but not the end state for the provided form and patient.
	 * 
	 * @param encounters       encounters list of latest encounters
	 * @param backportsService ChirdlUtilBackportsService object
	 * @param map              Map that will be returned to the client
	 * @return Encounter object or null if one is not found.
	 */
	public static Encounter getPatientEncounterWithoutScannedTimeStamp(
			List<Encounter> encounters, ChirdlUtilBackportsService backportsService,
			Map<String, Object> map) {
		return getEncounterWithoutScannedTimeStamp(encounters, backportsService, map, false);
	}
	
	/**
	 * Retrieves the latest encounters with observations that contains the provided
	 * start state but not the end state for the provided form and patient.
	 * 
	 * @param encounters       encounters list of latest encounters
	 * @param backportsService ChirdlUtilBackportsService object
	 * @param map              Map that will be returned to the client
	 * @return Encounter object or null if one is not found.
	 */
	private static Encounter getEncounterWithoutScannedTimeStamp(
			List<Encounter> encounters, ChirdlUtilBackportsService backportsService,
			Map<String, Object> map, boolean physicianForm) {
		    EncounterService encounterService = Context.getEncounterService();
		for (int i = encounters.size() - 1; i >= 0; i--) {
			// Look up the encounter through the CHICA encounter service to prevent class
			// cast exceptions.
			Integer encounterId = encounters.get(i).getEncounterId();
			Encounter encounter = encounterService.getEncounter(encounterId);
			if (physicianForm) {
				setPhysicianFormURLAttributes(encounter, map);
			} else {
				setPatientFormURLAttributes(encounter, map);
			}
			
			String formName = (String) map.get(ChirdlUtilConstants.PARAMETER_FORM_NAME);
			String startStateStr = (String) map.get(ChirdlUtilConstants.PARAMETER_START_STATE);
			String endStateStr = (String) map.get(ChirdlUtilConstants.PARAMETER_END_STATE);
			String formPage = (String) map.get(ChirdlUtilConstants.PARAMETER_FORM_PAGE);

			if (StringUtils.isBlank(formName) || StringUtils.isBlank(formPage) || StringUtils.isBlank(startStateStr)
					|| StringUtils.isBlank(endStateStr)) {
				return encounter;
			}

			Map<Integer, List<PatientState>> formIdToPatientStateMapStart = new HashMap<>();
			Map<Integer, List<PatientState>> formIdToPatientStateMapEnd = new HashMap<>();
			State startState = backportsService.getStateByName(startStateStr);
			State endState = backportsService.getStateByName(endStateStr);
			if (startState != null && endState != null) {
				Util.getPatientStatesByEncounterId(backportsService, formIdToPatientStateMapStart, encounterId,
						startState.getStateId(), true);
				Util.getPatientStatesByEncounterId(backportsService, formIdToPatientStateMapEnd, encounterId,
						endState.getStateId(), true);
			}

			Form form = Context.getFormService().getForm(formName);
			if (form != null) {
				boolean containsStartState = formIdToPatientStateMapStart.containsKey(form.getFormId());
				boolean containsEndState = formIdToPatientStateMapEnd.containsKey(form.getFormId());

				if (containsStartState && !containsEndState) {
					return encounter;
				}
			}
		}

		return null;
	}
	
	/**
	 * Set URL attributes for the primary physician form to the provided Map.
	 * 
	 * @param encounter Patient encounter object.
	 * @param map       Map that will be returned to the client.
	 */
	public static void setPhysicianFormURLAttributes(Encounter encounter,
			Map<String, Object> map) {
		setURLAttributes(encounter, map, true);
	}
	
	/**
	 * Set URL attributes for the primary physician form to the provided Map.
	 * 
	 * @param encounter Patient encounter object.
	 * @param map       Map that will be returned to the client.
	 */
	public static void setPatientFormURLAttributes(Encounter encounter,
			Map<String, Object> map) {
		setURLAttributes(encounter, map, false);
	}
	
	/**
	 * Set URL attributes for the primary physician form to the provided Map.
	 * 
	 * @param encounter Patient encounter object.
	 * @param map       Map that will be returned to the client.
	 * @param formName  The form name used to load URL attributes.
	 */
	private static void setURLAttributes(Encounter encounter,
			Map<String, Object> map, boolean physicianForm) {
		
		ChirdlUtilBackportsService chirdlUtilBackportsService = Context
				.getService(ChirdlUtilBackportsService.class);
		
		Location location = encounter.getLocation();
		if (location == null) {
			return;
		}

		Integer locationId = location.getLocationId();
		
		EncounterAttributeValue attributeValue = chirdlUtilBackportsService
				.getEncounterAttributeValueByName( encounter.getEncounterId(),ChirdlUtilConstants.ENCOUNTER_ATTRIBUTE_PRINTER_LOCATION);

		String printerLocation =  null;	
		
		if (attributeValue != null) {
			printerLocation =  attributeValue.getValueText();	
		}
			
		LocationTag locationTag = null;
		if (StringUtils.isNotBlank(printerLocation)) {
			LocationService locationService = Context.getLocationService();
			locationTag = locationService.getLocationTagByName(printerLocation);
		}

		if (locationTag == null) {
			return;
		}

		Integer locationTagId = locationTag.getLocationTagId();
		String formName = null;
		if (physicianForm) {
			formName = Util.getPrimaryPhysicianFormName(locationId, locationTagId);
		} else {
			formName = Util.getPrimaryPatientFormName(locationId, locationTagId);
		}
		
		map.put(ChirdlUtilConstants.PARAMETER_FORM_NAME, formName);
		map.put(ChirdlUtilConstants.PARAMETER_FORM_PAGE, org.openmrs.module.chirdlutil.util.Util.getFormAttributeValue(
				locationId, locationTagId, ChirdlUtilConstants.FORM_ATTRIBUTE_URL, formName));
		map.put(ChirdlUtilConstants.PARAMETER_START_STATE, org.openmrs.module.chirdlutil.util.Util.getFormAttributeValue(
				locationId, locationTagId, ChirdlUtilConstants.FORM_ATTRIBUTE_START_STATE, formName));
		map.put(ChirdlUtilConstants.PARAMETER_END_STATE, org.openmrs.module.chirdlutil.util.Util.getFormAttributeValue(
				locationId, locationTagId, ChirdlUtilConstants.FORM_ATTRIBUTE_END_STATE, formName));
	}
	
	/**
	 * Adds information to the map that will determine whether or not the user will
	 * be able to force print handouts on the JSP.
	 * 
	 * @param backportsService Service used to access data logic
	 * @param patient          The patient for the request
	 * @param encounter        The patient's encounter. If null, an attempt will be
	 *                         made to find a valid encounter for the patient.
	 * @param mrn              The patient's medical record number.
	 * @param map              The HTTP map that will be returned to the client.
	 */
	public static void addHandoutsInfo(ChirdlUtilBackportsService backportsService, Patient patient,
			org.openmrs.Encounter encounter, String mrn, Map<String, Object> map) {
		org.openmrs.Encounter handoutsEncounter = null;
		if (encounter == null) {
			// Check to see if the patient has at least one encounter to display the
			// Handouts button on the page.
			handoutsEncounter = org.openmrs.module.chirdlutil.util.Util.getLastEncounter(patient);
		} else {
			handoutsEncounter = encounter;
		}

		if (handoutsEncounter != null) {
			map.put(ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID, handoutsEncounter.getEncounterId());
			Location location = handoutsEncounter.getLocation();
			if (location != null) {
				map.put(ChirdlUtilConstants.PARAMETER_LOCATION_ID, location.getLocationId());
			} else {
				return;
			}

			Set<LocationTag> tags = location.getTags();
			if (tags != null && !tags.isEmpty()) {
				LocationTag tag = tags.iterator().next();
				map.put(ChirdlUtilConstants.PARAMETER_LOCATION_TAG_ID, tag.getLocationTagId());
			} else {
				return;
			}

			List<Session> sessions = backportsService.getSessionsByEncounter(handoutsEncounter.getEncounterId());
			if (sessions != null && !sessions.isEmpty()) {
				map.put(ChirdlUtilConstants.PARAMETER_SESSION_ID, sessions.get(0).getSessionId());
			} else {
				return;
			}

			PersonName personName = patient.getPersonName();
			if (personName != null && personName.getGivenName() != null && personName.getFamilyName() != null) {
				map.put(ChirdlUtilConstants.PARAMETER_PATIENT_NAME,
						personName.getGivenName() + " " + personName.getFamilyName());
			} else {
				// Default to MRN if a name cannot be found.
				map.put(ChirdlUtilConstants.PARAMETER_PATIENT_NAME, mrn);
			}

			map.put(ChirdlUtilConstants.PARAMETER_SHOW_HANDOUTS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
		}
	}
	
	/**
     * Completes the form and returns the next view.
     * 
     * @param patientId The patient identifier
     * @param language The language of the patient
     * @param userQuitForm Indicates if the user quit the form
     * @param map map to populate for return to the client
     * @param formView The form view to display next
     * @return The form view to display next
     */
    public static ModelAndView finishForm(
    		Integer patientId, String language, String userQuitForm, ModelMap map, String formView) {
    	Patient patient = Context.getPatientService().getPatient(patientId);
        map.put(ChirdlUtilConstants.PARAMETER_PATIENT, patient);
        map.put(ChicaConstants.PARAMETER_LANGUAGE, language);
        map.put(ChicaConstants.PARAMETER_USER_QUIT_FORM, userQuitForm);
        return new ModelAndView(formView, map);
    }
	
	/**
     * Completes the form and returns the next view.
     * 
     * @param request The HTTP request information
     * @param map map to populate for return to the client
     * @param formView The form view to display next
     * @return The form view to display next
     */
    public static ModelAndView finishForm(HttpServletRequest request, ModelMap map, String formView) {
    	String patientIdStr = request.getParameter(ChirdlUtilConstants.PARAMETER_PATIENT_ID);
    	Integer patientId = Integer.valueOf(patientIdStr);
    	String language = request.getParameter(ChicaConstants.PARAMETER_LANGUAGE);
    	String userQuitForm = request.getParameter(ChicaConstants.PARAMETER_USER_QUIT_FORM);
    	return finishForm(patientId, language, userQuitForm, map, formView);
    }
    
    /**
     * Returns the session timeout warning.
     * 
     * @return The session timeout warning
     */
    public static Integer getSessionTimeoutWarning() {
		Integer sessionTimeoutWarning = new Integer(180);
		String sessionTimeoutWarningStr = Context.getAdministrationService()
				.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_SESSION_TIMEOUT_WARNING);
		if (sessionTimeoutWarningStr == null || sessionTimeoutWarningStr.trim().length() == 0) {
			log.warn("The {} global property does not have a value set.  180 seconds will be used as a default value.",ChirdlUtilConstants.GLOBAL_PROP_SESSION_TIMEOUT_WARNING);
		} else {
			try {
				sessionTimeoutWarning = Integer.valueOf(sessionTimeoutWarningStr);
			} catch (NumberFormatException e) {
				log.error("The {} global property is not a valid Integer.  180 seconds will be used as a default value", ChirdlUtilConstants.GLOBAL_PROP_SESSION_TIMEOUT_WARNING,e);
				sessionTimeoutWarning = new Integer(180);
			}
		}
		
		return sessionTimeoutWarning;
    }
    
    /**
     * Returns the pdf viewer display format
     * 
     * @param locationId
     * @return Pdf Viewer display format
     */
    public static String getPdfViewer(Integer locationId) {
        ChirdlLocationAttributeValue attrVal = 
                Context.getService(ChirdlUtilBackportsService.class).getLocationAttributeValue(
                    locationId, ChirdlUtilConstants.LOCATION_ATTRIBUTE_PDF_VIEWER);
        if (attrVal == null || StringUtils.isBlank(attrVal.getValue())) {
            log.error("No location attribute value specified for location {} and attribute name {}.",locationId,ChirdlUtilConstants.LOCATION_ATTRIBUTE_PDF_VIEWER);
            return null;
        }
        return attrVal.getValue();
    }
}
