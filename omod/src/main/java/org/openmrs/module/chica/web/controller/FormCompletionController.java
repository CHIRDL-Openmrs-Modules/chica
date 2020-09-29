package org.openmrs.module.chica.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.result.Result;
import org.openmrs.module.chica.TabletNotification;
import org.openmrs.module.chica.util.ChicaConstants;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.dss.hibernateBeans.Rule;
import org.openmrs.module.dss.service.DssService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class FormCompletionController {
    
    /** Form views */
    private static final String FORM_VIEW_FINISH_FORMS_MOBILE = "/module/chica/finishFormsMobile";
    private static final String FORM_VIEW_FINISH_FORMS_WEB = "/module/chica/finishFormsWeb";
    private static final String FORM_VIEW_FINISH_FORMS_NOTIFICATION_MOBILE = "/module/chica/finishFormsNotificationMobile";
    
    /** Success views */
    private static final String SUCCESS_VIEW_GREASEBOARD_MOBILE = "greaseBoardMobile.form";
    private static final String SUCCESS_VIEW_FINISH_FORMS_WEB = "finishFormsWeb.form";
    
    /** Parameters */
    private static final String PARAMETER_NOTIFICATIONS = "notifications";
    
    /** Staff */
    private static final String STAFF_NOTIFICATION = "Staff_notification";
	
    /**
     * Handles submission of the page.
     * 
     * @param request The HTTP request information
     * @return The name of the next view
     */
    @RequestMapping(value = "module/chica/finishFormsMobile.form", method = RequestMethod.POST)
    protected ModelAndView processMobileFormCompletionSubmit(HttpServletRequest request) {
		return new ModelAndView(new RedirectView(SUCCESS_VIEW_GREASEBOARD_MOBILE));
	}
    
    /**
     * Handles submission of the page.
     * 
     * @param request The HTTP request information
     * @return The name of the next view
     */
    @RequestMapping(value = "module/chica/finishFormsWeb.form", method = RequestMethod.POST)
    protected ModelAndView processWebFormCompletionSubmit(HttpServletRequest request) {
        return new ModelAndView(new RedirectView(SUCCESS_VIEW_FINISH_FORMS_WEB));
    }
    
    /**
     * Handles submission of the page.
     * 
     * @param request The HTTP request information
     * @return The name of the next view
     */
    @RequestMapping(value = "module/chica/finishFormsNotificationMobile.form", method = RequestMethod.POST)
    protected ModelAndView processMobileFormNotificationCompletionSubmit(HttpServletRequest request) {
        return new ModelAndView(new RedirectView(SUCCESS_VIEW_GREASEBOARD_MOBILE));
    }
	
    /**
     * Form initialization method.
     * 
     * @param request The HTTP request information
     * @param map The map to populate for return to the client
     * @return The form view name
     */
    @RequestMapping(value = "module/chica/finishFormsMobile.form", method = RequestMethod.GET)
    protected ModelAndView initFinishFormsMobile(HttpServletRequest request, ModelMap map) {
        return ControllerUtil.finishForm(request, map, FORM_VIEW_FINISH_FORMS_MOBILE);
	}
    
    /**
     * Form initialization method.
     * 
     * @param request The HTTP request information
     * @param map The map to populate for return to the client
     * @return The form view name
     */
    @RequestMapping(value = "module/chica/finishFormsWeb.form", method = RequestMethod.GET)
    protected ModelAndView initFinishFormsWeb(HttpServletRequest request, ModelMap map) {
        return ControllerUtil.finishForm(request, map, FORM_VIEW_FINISH_FORMS_WEB);
    }
    
    /**
     * Form initialization method.
     * 
     * @param request The HTTP request information
     * @param map The map to populate for return to the client
     * @return The form view name
     */
    @RequestMapping(value = "module/chica/finishFormsNotificationMobile.form", method = RequestMethod.GET)
    protected String initFinishFormsNotificationMobile(HttpServletRequest request, ModelMap map) {
        String patientIdStr = request.getParameter(ChirdlUtilConstants.PARAMETER_PATIENT_ID);
        String locationIdStr = request.getParameter(ChirdlUtilConstants.PARAMETER_LOCATION_ID);
        String encounterIdStr = request.getParameter(ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID);
        String locationTagIdStr = request.getParameter(ChirdlUtilConstants.PARAMETER_LOCATION_TAG_ID);
        String sessionIdStr = request.getParameter(ChirdlUtilConstants.PARAMETER_SESSION_ID);
        String language = request.getParameter(ChicaConstants.PARAMETER_LANGUAGE);
        String userQuitForm = request.getParameter(ChicaConstants.PARAMETER_USER_QUIT_FORM);
        Integer locationId = Integer.valueOf(locationIdStr);
        Patient patient = Context.getPatientService().getPatient(Integer.valueOf(patientIdStr));
        
        Map<String,Object> parameters = new HashMap<>();
        parameters.put(ChirdlUtilConstants.PARAMETER_MODE, ChirdlUtilConstants.PARAMETER_VALUE_PRODUCE);
        // The LocationAttributeLookup rule requires a location ID through a FormInstance object.
        FormInstance formInstance = new FormInstance(locationId, null, null);
        parameters.put(ChirdlUtilConstants.PARAMETER_FORM_INSTANCE, formInstance);
        parameters.put(ChirdlUtilConstants.PARAMETER_LOCATION_ID, locationId);
        parameters.put(ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID, Integer.valueOf(encounterIdStr));
        parameters.put(ChirdlUtilConstants.PARAMETER_LOCATION_TAG_ID, Integer.valueOf(locationTagIdStr));
        parameters.put(ChirdlUtilConstants.PARAMETER_SESSION_ID, Integer.valueOf(sessionIdStr));
        
        map.put(ChirdlUtilConstants.PARAMETER_PATIENT, patient);
        map.put(PARAMETER_NOTIFICATIONS, runRules(patient, parameters));
        map.put(ChicaConstants.PARAMETER_LANGUAGE, language);
        map.put(ChicaConstants.PARAMETER_USER_QUIT_FORM, userQuitForm);
        
        return FORM_VIEW_FINISH_FORMS_NOTIFICATION_MOBILE;
    }
    
    /**
     * Runs all rules with the rule type of Staff_notification and adds the result to a list.
     * 
     * @param patient The patient used to run the rules.
     * @param parameters Map of parameters that will be passed to the executing rules.
     * @return List of string results of the rules that are run.
     */
    private List<TabletNotification> runRules(Patient patient, Map<String,Object> parameters) {
        DssService dssService = Context.getService(DssService.class);
        List<Rule> rules = dssService.getRulesByType(STAFF_NOTIFICATION);
        if (rules == null || rules.isEmpty()) {
            return new ArrayList<>();
        }
        
        for (int i = rules.size() - 1; i >= 0; i--) {
            Rule foundRule = rules.get(i);
            if (foundRule.checkAgeRestrictions(patient)) {
                foundRule.setParameters(parameters);
            } else {
                rules.remove(i);
            }
        }
        
        List<Result> results = dssService.runRules(patient, rules);
        if (results == null || results.isEmpty()) {
            return new ArrayList<>();
        }
        
        return processRuleResults(results);
    }
    
    /**
     * Processes the rules results creates a list of notifications.
     * 
     * @param results The rule results to process
     * @return The list of tablet notifications created from the rule results
     */
    private List<TabletNotification> processRuleResults(List<Result> results) {
        List<TabletNotification> notifications = new ArrayList<>();
        for (Result result : results) {
            TabletNotification notification = new TabletNotification();
            boolean foundResults = false;
            for (int i = 0; i < result.size(); i++) {
                Result subResult = result.get(i);
                String value = subResult.toString().trim();
                if (value.length() > 0) {
                    foundResults = true;
                    if (i == 0) {
                        notification.setStatement(value);
                    } else {
                        notification.addSubStatement(value);
                    }
                }
            }
            
            if (foundResults) {
                notifications.add(notification);
            }
        }
        
        return notifications;
    }
}
