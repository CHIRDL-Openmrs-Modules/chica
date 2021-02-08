package org.openmrs.module.chica.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.Daemon;
import org.openmrs.module.atd.ParameterHandler;
import org.openmrs.module.chica.ChicaParameterHandler;
import org.openmrs.module.chica.DynamicFormAccess;
import org.openmrs.module.chica.util.ChicaConstants;
import org.openmrs.module.chica.util.Util;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstanceTag;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Utility class for mobile form controllers.
 * 
 * @author Steve McKee
 */
public class MobileFormControllerUtil {
    
    /** Logger for this class and subclasses */
    protected static final Log log = LogFactory.getLog(MobileFormControllerUtil.class);
    
    /**
     * Constructor method
     */
    private MobileFormControllerUtil() {
        // Empty private constructor
    }
    
    /**
     * Handles for submission for mobile forms
     * @param request The HTTP request containing the information from the client
     * @param successView The page to display after the form is submitted
     * @param patientId The patient identifier
     * @param encounterId The encounter identifier
     * @param sessionId The session identifier
     * @param formInstanceTag The information needed to locate the form instance being submitted
     * @return The view to be displayed next
     */
    public static ModelAndView handleMobileFormSubmission(HttpServletRequest request, String successView, 
    		Integer patientId, Integer encounterId, Integer sessionId, FormInstanceTag formInstanceTag) {
    	@SuppressWarnings("unchecked")
		Map<String, String[]> parameterMap = request.getParameterMap();
    	Integer locationId = formInstanceTag.getLocationId();
    	Integer formId = formInstanceTag.getFormId();
    	Integer formInstanceId = formInstanceTag.getFormInstanceId();
    	Integer locationTagId = formInstanceTag.getLocationTagId();
        try {
            ParameterHandler parameterHandler = new ChicaParameterHandler();
            DynamicFormAccess formAccess = new DynamicFormAccess();
            Patient patient = Context.getPatientService().getPatient(patientId);
            formAccess.saveExportElements(new FormInstance(locationId, formId, formInstanceId), locationTagId, 
            	encounterId, patient, parameterMap, parameterHandler);
            
            // Run null priority rules
            HashMap<String, Object> parameters = new HashMap<>();
            FormInstance formInstance = new FormInstance(locationId, formId, formInstanceId);
            parameters.put(ChirdlUtilConstants.PARAMETER_FORM_INSTANCE, formInstance);
            parameters.put(ChirdlUtilConstants.PARAMETER_SESSION_ID, sessionId);
            parameters.put(ChirdlUtilConstants.PARAMETER_LOCATION_TAG_ID, locationTagId);
            parameters.put(ChirdlUtilConstants.PARAMETER_LOCATION_ID, locationId);
            parameters.put(ChirdlUtilConstants.PARAMETER_LOCATION, 
                Context.getLocationService().getLocation(locationId).getName());
            parameters.put(ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID, encounterId);
            parameters.put(ChirdlUtilConstants.PARAMETER_MODE, ChirdlUtilConstants.PARAMETER_VALUE_CONSUME);
            completeForm(formId, patient, parameters, formInstance);
        } catch (Exception e) {
            log.error("Error saving form", e);
        }
        
        Map<String, Object> map = new HashMap<>();
        map.put(ChirdlUtilConstants.PARAMETER_PATIENT_ID, patientId);
        map.put(ChirdlUtilConstants.PARAMETER_LOCATION_ID, locationId);
        map.put(ChirdlUtilConstants.PARAMETER_LOCATION_TAG_ID, locationTagId);
        map.put(ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID, encounterId);
        map.put(ChirdlUtilConstants.PARAMETER_SESSION_ID, sessionId);
        String language = request.getParameter(ChicaConstants.PARAMETER_LANGUAGE);
        map.put(ChicaConstants.PARAMETER_LANGUAGE, language);
        String userQuitForm = request.getParameter(ChicaConstants.PARAMETER_USER_QUIT_FORM);
        map.put(ChicaConstants.PARAMETER_USER_QUIT_FORM, userQuitForm);
        
        return new ModelAndView(new RedirectView(successView), map);
    }
    
    /**
     * Handles for submission for mobile forms
     * @param request The HTTP request containing the information from the client
     * @param successView The page to display after the form is submitted
     * @return The view to be displayed next
     */
    public static ModelAndView handleMobileFormSubmission(HttpServletRequest request, String successView) {
        String patientIdStr = request.getParameter(ChirdlUtilConstants.PARAMETER_PATIENT_ID);
        Integer patientId = Integer.valueOf(patientIdStr);
        Integer formId = Integer.valueOf(request.getParameter(ChirdlUtilConstants.PARAMETER_FORM_ID));
        Integer formInstanceId = Integer.valueOf(request.getParameter(ChirdlUtilConstants.PARAMETER_FORM_INSTANCE_ID));
        String locationIdStr = request.getParameter(ChirdlUtilConstants.PARAMETER_LOCATION_ID);
        Integer locationId = Integer.valueOf(locationIdStr);
        String locationTagIdStr = request.getParameter(ChirdlUtilConstants.PARAMETER_LOCATION_TAG_ID);
        Integer locationTagId = Integer.valueOf(locationTagIdStr);
        String encounterIdStr = request.getParameter(ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID);
        Integer encounterId = Integer.valueOf(encounterIdStr);
        String sessionIdStr = request.getParameter(ChirdlUtilConstants.PARAMETER_SESSION_ID);
        Integer sessionId = Integer.valueOf(sessionIdStr);
        
        FormInstanceTag formInstanceTag = new FormInstanceTag(locationId, formId, formInstanceId, locationTagId);
        return handleMobileFormSubmission(request, successView, patientId, encounterId, sessionId, formInstanceTag);
    }
    
    /**
     * Loads the form information for client use.
     * 
     * @param map Model map to be populated for client use
     * @param formView The view to display once the form is loaded.
     * @param encounterId The encounter identifier
     * @param sessionid The session identifier
     * @param patientid The patient identifier
     * @param language The language of the patient
     * @param formInstanceTag Contains the relevant form information.
     * @return The view to display once the form is loaded.
     */
    public static String loadMobileFormInformation(ModelMap map, String formView, Integer encounterId, 
    		Integer sessionId, Integer patientId, String language, FormInstanceTag formInstanceTag) {
    	Integer locationId = formInstanceTag.getLocationId();
        Integer formId = formInstanceTag.getFormId();
        Integer formInstanceId = formInstanceTag.getFormInstanceId();
        map.put(ChirdlUtilConstants.PARAMETER_FORM_ID, formId);
        map.put(ChirdlUtilConstants.PARAMETER_FORM_INSTANCE_ID, formInstanceId);
        map.put(ChirdlUtilConstants.PARAMETER_LOCATION_ID, locationId);
        map.put(ChirdlUtilConstants.PARAMETER_LOCATION_TAG_ID, formInstanceTag.getLocationTagId());
        map.put(ChirdlUtilConstants.PARAMETER_FORM_INSTANCE, formInstanceTag);
        map.put(ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID, encounterId);
        map.put(ChirdlUtilConstants.PARAMETER_SESSION_ID, sessionId);
        map.put(ChicaConstants.PARAMETER_LANGUAGE, language);
        Patient patient = Context.getPatientService().getPatient(patientId);
        map.put(ChirdlUtilConstants.PARAMETER_PATIENT, patient);
        
        //Run this to show the form
        try {
            showForm(map, formId, formInstanceId, encounterId);
        } catch (Exception e) {
            String message = 
                "Error retrieving data to display a form. Please contact support with the following information: "
                + "Form ID: " + formId + " Form Instance ID: " + formInstanceId + " Location ID: " + locationId;
            log.error(message, e);
            map.put(ChirdlUtilConstants.PARAMETER_ERROR_MESSAGE, message);
        }
        
        return formView;
    }
    
    /**
     * Loads the form information for client use.
     * 
     * @param request The HTTP request containing the information from the client
     * @param map Model map to be populated for client use
     * @param formView The view to display once the form is loaded.
     * @return The view to display once the form is loaded.
     */
    public static String loadMobileFormInformation(HttpServletRequest request, ModelMap map, String formView) {
        String encounterIdStr = request.getParameter(ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID);
        Integer encounterId = Integer.valueOf(encounterIdStr);
        String sessionIdStr = request.getParameter(ChirdlUtilConstants.PARAMETER_SESSION_ID);
        Integer sessionId = Integer.valueOf(sessionIdStr);
        String language = request.getParameter(ChicaConstants.PARAMETER_LANGUAGE);
        String patientIdStr = request.getParameter(ChirdlUtilConstants.PARAMETER_PATIENT_ID);
        Integer patientId = Integer.valueOf(patientIdStr);
        String formInstance = request.getParameter(ChirdlUtilConstants.PARAMETER_FORM_INSTANCE);
        FormInstanceTag formInstTag = FormInstanceTag.parseFormInstanceTag(formInstance);
        
        return loadMobileFormInformation(map, formView, encounterId, sessionId, patientId, language, formInstTag);
    }
    
    /**
     * Runs all null priority rules and changes the patient state.
     * 
     * @param formId The identifier of the form to run the rules for.
     * @param patient The patient to run the rules for.
     * @param parameters Map containing parameters needed for the rules to execute.
     * @param formInstance The instance of the form.
     */
    private static void completeForm(Integer formId, Patient patient, HashMap<String, Object> parameters, FormInstance formInstance) {
        Runnable runnable = new CompleteForm(patient.getPatientId(), formId, parameters, formInstance); 
        Daemon.runInDaemonThread(runnable, Util.getDaemonToken()); 
    }
    
    /**
     * Build the form information from the merge fields of the form.
     * 
     * @param map The map for the HTTP response.
     * @param formId The form identifier.
     * @param formInstanceId The form instance identifier.
     * @param encounterId The encounter identifier.
     */
    private static void showForm(Map<String, Object> map, Integer formId, Integer formInstanceId, Integer encounterId) {
        DynamicFormAccess formAccess = new DynamicFormAccess();
        
        List<org.openmrs.module.atd.xmlBeans.Field> fields = 
            formAccess.getMergeElements(formId, formInstanceId, encounterId);
        for (org.openmrs.module.atd.xmlBeans.Field field : fields) {
            map.put(field.getId(), field.getValue());
        }
        
        // DWE CHICA-430 Get existing values for "Export Field" types
        Integer locationTagId = (Integer)map.get(ChirdlUtilConstants.PARAMETER_LOCATION_TAG_ID);
        fields = formAccess.getExportElements(formId, formInstanceId, encounterId, locationTagId);
        for (org.openmrs.module.atd.xmlBeans.Field field : fields) 
        {
            map.put(field.getId(), field.getValue());
        }
    }
}
