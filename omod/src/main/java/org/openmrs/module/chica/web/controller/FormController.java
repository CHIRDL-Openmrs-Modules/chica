package org.openmrs.module.chica.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.atd.xmlBeans.Field;
import org.openmrs.module.atd.xmlBeans.Record;
import org.openmrs.module.atd.xmlBeans.Records;
import org.openmrs.module.chica.web.ServletUtil;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstanceTag;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class FormController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	/** Parameters */
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
	
	/** Session attributes */
	private static final String SESSION_ATTRIBUTE_SUBMITTED_FORM_INSTANCES = "submittedFormInstances";
	
	/** Form views */
	private static final String FORM_VIEW_PWS = "/module/chica/pws";
	private static final String FORM_VIEW_PWS_IUH = "/module/chica/pwsIUHCerner";
	
	/** Success view */
	private static final String SUCCESS_VIEW = "finishFormsWeb.form";
	
	/**
     * Form initialization method.
     * 
     * @param request The HTTP request information
     * @param map The map to populate for return to the client
     * @return The form view name
     */
    @RequestMapping(value = "module/chica/pws.form", method = RequestMethod.GET)
    protected String initPws(HttpServletRequest request, ModelMap map) {
		loadFormData(request, map);
		return FORM_VIEW_PWS;
	}
    
    /**
     * Form initialization method.
     * 
     * @param request The HTTP request information
     * @param map The map to populate for return to the client
     * @return The form view name
     */
    @RequestMapping(value = "module/chica/pwsIUHCerner.form", method = RequestMethod.GET)
    protected String initPwsIuhCerner(HttpServletRequest request, ModelMap map) {
        loadFormData(request, map);
        return FORM_VIEW_PWS_IUH;
    }
	
    /**
     * Handles submission of the page.
     * 
     * @param request The HTTP request information
     * @return The name of the next view
     */
    @RequestMapping(value = "module/chica/pws.form", method = RequestMethod.POST)
    protected ModelAndView processPwsSubmit(HttpServletRequest request) {
		return handleSubmit(request, FORM_VIEW_PWS);
	}
    
    /**
     * Handles submission of the page.
     * 
     * @param request The HTTP request information
     * @return The name of the next view
     */
    @RequestMapping(value = "module/chica/pwsIUHCerner.form", method = RequestMethod.POST)
    protected ModelAndView processPwsIuhCernerSubmit(HttpServletRequest request) {
        return handleSubmit(request, FORM_VIEW_PWS_IUH);
    }
    
    /**
     * Handles submitting the form.
     * 
     * @param request The HTTP request information
     * @param formView The name of the view to return if errors occur
     * @return The name of the next view to display
     */
    private ModelAndView handleSubmit(HttpServletRequest request, String formView) {
        //parse out the location_id,form_id,location_tag_id, and form_instance_id
        //from the selected form
        Map<String, Object> map = new HashMap<>();
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
            return new ModelAndView(formView, map);
        }
        
        try {
            ControllerUtil.scanForm(formInstTag, request);
        } catch (Exception e) {
            String messagePart1 = "Error signing form.";
            String messagePart2 = "Please contact support with the following information: Form ID: " + 
                formInstTag.getFormId() + " Form Instance ID: " + formInstTag.getFormInstanceId() + 
                " Location ID: " + formInstTag.getLocationId() + " Location Tag ID: " + 
                formInstTag.getLocationTagId();
            String htmlMessage = ServletUtil.writeHtmlErrorMessage(null, e, log, messagePart1, messagePart2);
            loadFormData(request, map);
            map.put(PARAM_ERROR_MESSAGE, htmlMessage);
            return new ModelAndView(formView, map);
        }
        
        // Save who is submitting the form.
        if (providerId != null && providerId.trim().length() > 0) {
            Integer patientId = Integer.parseInt(patientIdStr);
            Patient patient = Context.getPatientService().getPatient(patientId);
            ControllerUtil.saveProviderSubmitter(patient, encounterId, providerId, formInstTag);
        } else {
            String message = "No valid providerId provided.  Cannot log who is submitting form ID: " + formInstTag.getFormId() + 
                    " form instance ID: " + formInstTag.getFormInstanceId() + " location ID: " + formInstTag.getLocationId() + 
                    " location tag ID: " + formInstTag.getLocationTagId();
            log.error(message);
        }
        
        map.put(PARAM_PATIENT_ID, patientIdStr);
        
        // CHICA-1004 Add the submitted form instance to the session
        ControllerUtil.addSubmittedFormInstance(request, formInstance);
        
        return new ModelAndView(new RedirectView(SUCCESS_VIEW), map);
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
			ControllerUtil.saveProviderViewer(patient, encounterId, providerId, formInstTag);
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
						+ "will be used as a default value", e);
				sessionTimeoutWarning = 180;
			}
		}
		
		map.put(PARAM_SESSION_TIMEOUT_WARNING, sessionTimeoutWarning);
	}
	
	private void showForm(Map<String, Object> map, FormInstanceTag formInstanceTag) {
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
