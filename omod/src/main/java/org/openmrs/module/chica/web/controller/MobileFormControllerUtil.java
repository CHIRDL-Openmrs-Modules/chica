package org.openmrs.module.chica.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.ParameterHandler;
import org.openmrs.module.chica.ChicaParameterHandler;
import org.openmrs.module.chica.DynamicFormAccess;
import org.openmrs.module.chica.util.ChicaConstants;
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
     * Handles for submission for mobile forms
     * @param request The HTTP request containing the information from the client
     * @param successView The page to display after the form is submitted
     * @return The view to be displayed next
     */
    public static ModelAndView handleMobileFormSubmission(HttpServletRequest request, String successView) {
        String patientIdStr = request.getParameter(ChirdlUtilConstants.PARAMETER_PATIENT_ID);
        Integer patientId = Integer.parseInt(patientIdStr);
        Integer formId = Integer.parseInt(request.getParameter(ChirdlUtilConstants.PARAMETER_FORM_ID));
        Integer formInstanceId = Integer.parseInt(request.getParameter(ChirdlUtilConstants.PARAMETER_FORM_INSTANCE_ID));
        String locationIdStr = request.getParameter(ChirdlUtilConstants.PARAMETER_LOCATION_ID);
        Integer locationId = Integer.parseInt(locationIdStr);
        String locationTagIdStr = request.getParameter(ChirdlUtilConstants.PARAMETER_LOCATION_TAG_ID);
        Integer locationTagId = Integer.parseInt(locationTagIdStr);
        String encounterIdStr = request.getParameter(ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID);
        Integer encounterId = Integer.parseInt(encounterIdStr);
        String sessionIdStr = request.getParameter(ChirdlUtilConstants.PARAMETER_SESSION_ID);
        Integer sessionId = Integer.parseInt(sessionIdStr);
        
        Map<String, String[]> parameterMap = request.getParameterMap();
        try {
            ParameterHandler parameterHandler = new ChicaParameterHandler();
            DynamicFormAccess formAccess = new DynamicFormAccess();
            Patient patient = Context.getPatientService().getPatient(patientId);
            formAccess.saveExportElements(new FormInstance(locationId, formId, formInstanceId), locationTagId, encounterId, 
                patient, parameterMap, parameterHandler);
            
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
        map.put(ChirdlUtilConstants.PARAMETER_PATIENT_ID, patientIdStr);
        map.put(ChirdlUtilConstants.PARAMETER_LOCATION_ID, locationIdStr);
        map.put(ChirdlUtilConstants.PARAMETER_LOCATION_TAG_ID, locationTagIdStr);
        map.put(ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID, encounterIdStr);
        map.put(ChirdlUtilConstants.PARAMETER_SESSION_ID, sessionIdStr);
        String language = request.getParameter(ChicaConstants.PARAMETER_LANGUAGE);
        map.put(ChicaConstants.PARAMETER_LANGUAGE, language);
        String userQuitForm = request.getParameter(ChicaConstants.PARAMETER_USER_QUIT_FORM);
        map.put(ChicaConstants.PARAMETER_USER_QUIT_FORM, userQuitForm);
        
        return new ModelAndView(new RedirectView(successView), map);
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
        Integer encounterId = Integer.parseInt(encounterIdStr);
        map.put(ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID, encounterIdStr);
        
        String sessionIdStr = request.getParameter(ChirdlUtilConstants.PARAMETER_SESSION_ID);
        map.put(ChirdlUtilConstants.PARAMETER_SESSION_ID, sessionIdStr);
        
        String language = request.getParameter(ChicaConstants.PARAMETER_LANGUAGE);
        map.put(ChicaConstants.PARAMETER_LANGUAGE, language);
        
        String patientIdStr = request.getParameter(ChirdlUtilConstants.PARAMETER_PATIENT_ID);
        Patient patient = Context.getPatientService().getPatient(Integer.parseInt(patientIdStr));
        map.put(ChirdlUtilConstants.PARAMETER_PATIENT, patient);
        
        String formInstance = request.getParameter(ChirdlUtilConstants.PARAMETER_FORM_INSTANCE);
        FormInstanceTag formInstTag = FormInstanceTag.parseFormInstanceTag(formInstance);
        Integer locationId = formInstTag.getLocationId();
        Integer formId = formInstTag.getFormId();
        Integer formInstanceId = formInstTag.getFormInstanceId();
        map.put(ChirdlUtilConstants.PARAMETER_FORM_ID, formId);
        map.put(ChirdlUtilConstants.PARAMETER_FORM_INSTANCE_ID, formInstanceId);
        map.put(ChirdlUtilConstants.PARAMETER_LOCATION_ID, locationId);
        map.put(ChirdlUtilConstants.PARAMETER_LOCATION_TAG_ID, formInstTag.getLocationTagId());
        map.put(ChirdlUtilConstants.PARAMETER_FORM_INSTANCE, formInstance);
        
        //Run this to show the form
        try {
            showForm(map, formId, formInstanceId, locationId, encounterId);
        } catch (Exception e) {
            String message = 
                "Error retrieving data to display a form. Please contact support with the following information: Form ID: " + 
                formId + " Form Instance ID: " + formInstanceId + " Location ID: " + locationId;
            log.error(message, e);
            map.put(ChirdlUtilConstants.PARAMETER_ERROR_MESSAGE, message);
        }
        
        return formView;
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
        Thread thread = new Thread(runnable);  
        thread.start();  
    }
    
    /**
     * Build the form information from the merge fields of the form.
     * 
     * @param map The map for the HTTP response.
     * @param formId The form identifier.
     * @param formInstanceId The form instance identifier.
     * @param locationId The location identifier.
     * @param encounterId The encounter identifier.
     * @throws Exception
     */
    private static void showForm(Map<String, Object> map, Integer formId, Integer formInstanceId, Integer locationId, 
                                 Integer encounterId) throws Exception {
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
