package org.openmrs.module.chica.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Location;
import org.openmrs.User;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.ChirdlLocationAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping(value = "module/chica/greaseBoardMobile.form")
public class MobileGreaseBoardController {
    
    /** Logger for this class and any subclasses */
	private static final Logger log = LoggerFactory.getLogger(MobileGreaseBoardController.class);
    
    /** Form view */
    private static final String FORM_VIEW = "/module/chica/greaseBoardMobile";
    
    /** Success view */
    private static final String SUCCESS_VIEW = "greaseBoardMobile.form";
    
    /** Parameters */
    private static final String PARAMETER_REFRESH_PERIOD = "refreshPeriod";
    private static final String PARAMETER_CURRENT_USER = "currentUser";
    private static final String PARAMETER_DISPLAY_CONFIDENTIALITY_NOTICE = "displayConfidentialityNoticeMobileGreaseBoard";
	
    /** Error messages */
	private static final String ERROR_SELECTION = 
	        "An error occurred while selecting a patient from the list.</br>Please enter the passcode and try again.";
	
	/**
     * Handles submission of the page.
     * 
     * @param request The HTTP request information
     * @return The name of the next view
     */
    @RequestMapping(method = RequestMethod.POST)
    protected ModelAndView processSubmit(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		if (Context.getAuthenticatedUser() == null) {
			return new ModelAndView(new RedirectView(SUCCESS_VIEW), map);
		}
		
		String patientId = request.getParameter(ChirdlUtilConstants.PARAMETER_PATIENT_ID);
		String encounterId = request.getParameter(ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID);
		String sessionId = request.getParameter(ChirdlUtilConstants.PARAMETER_SESSION_ID);
		
		// DWE CHICA-488 Make sure we have values for patientId, encounterId, and sessionId
		if(patientId == null || patientId.isEmpty() 
				|| encounterId == null || encounterId.isEmpty() 
				|| sessionId == null || sessionId.isEmpty()) {
			log.error("Error processing form submission (patientId: {} encounterId: {} sessionId: {}).",patientId,encounterId,sessionId);
			map.put(ChirdlUtilConstants.PARAMETER_ERROR_MESSAGE, ERROR_SELECTION);
			return new ModelAndView(new RedirectView(SUCCESS_VIEW), map);
		}
				
		ChirdlUtilBackportsService backportsService = Context.getService(ChirdlUtilBackportsService.class);
		String key = patientId + ChirdlUtilConstants.GENERAL_INFO_UNDERSCORE + encounterId + ChirdlUtilConstants.GENERAL_INFO_UNDERSCORE;
		String formIdKey = key + ChirdlUtilConstants.PARAMETER_FORM_ID;
		String formIdStr = request.getParameter(formIdKey);
		
		
		
		String formInstanceIdKey = key + ChirdlUtilConstants.PARAMETER_FORM_INSTANCE_ID;
		String formInstanceIdStr = request.getParameter(formInstanceIdKey);
		
		String locationIdKey = key + ChirdlUtilConstants.PARAMETER_LOCATION_ID;
		String locationIdStr = request.getParameter(locationIdKey);
		
		// DWE CHICA-488 Catch NumberFormatException
		try {
			Integer formId = Integer.parseInt(formIdStr);
		
			FormInstance formInstance = new FormInstance(Integer.parseInt(locationIdStr), formId, 
				Integer.parseInt(formInstanceIdStr));
			List<PatientState> patientStates = backportsService.getPatientStatesByFormInstance(formInstance, false);
			Integer locationTagId = null;
			if (patientStates != null && !patientStates.isEmpty()) {
				for (PatientState patientState : patientStates) {
					locationTagId = patientState.getLocationTagId();
					if (locationTagId != null) {
						break;
					}
				}
			}
			
			if (locationTagId != null) {
			    String nextPage = Util.getFormUrl(formId);
	            map.put(ChirdlUtilConstants.PARAMETER_PATIENT_ID, patientId);
	            map.put(ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID, encounterId);
	            map.put(ChirdlUtilConstants.PARAMETER_SESSION_ID, sessionId);
	            map.put(ChirdlUtilConstants.PARAMETER_FORM_INSTANCE, locationIdStr + 
	                ChirdlUtilConstants.GENERAL_INFO_UNDERSCORE + locationTagId.toString() + 
	                ChirdlUtilConstants.GENERAL_INFO_UNDERSCORE + 
	                formIdStr + ChirdlUtilConstants.GENERAL_INFO_UNDERSCORE + formInstanceIdStr);
	            return new ModelAndView(new RedirectView(nextPage), map);
			}
			
		    log.error("Error processing form submission, locationTagId is null (patientId: {} formIdStr: {} formInstanceIdStr: {} locationIdStr: {}).",
		    		patientId,formIdStr,formInstanceIdStr,locationIdStr);
		    map.put(ChirdlUtilConstants.PARAMETER_ERROR_MESSAGE, ERROR_SELECTION);
            return new ModelAndView(new RedirectView(SUCCESS_VIEW), map);			     
			
		} catch(NumberFormatException nfe) {
		    log.error("Error processing form submission  (patientId: {} formIdStr: {} formInstanceIdStr: {} locationIdStr: {}).",
		    		patientId,formIdStr,formInstanceIdStr,locationIdStr,nfe);
			map.put(ChirdlUtilConstants.PARAMETER_ERROR_MESSAGE, ERROR_SELECTION);
			return new ModelAndView(new RedirectView(SUCCESS_VIEW), map);
		}
	}
	
    /**
     * Form initialization method.
     * 
     * @param request The HTTP request information
     * @param map The map to populate for return to the client
     * @return The form view name
     */
    @RequestMapping(method = RequestMethod.GET)
    protected String initForm(HttpServletRequest request, ModelMap map) {
		User user = Context.getUserContext().getAuthenticatedUser();
		if (user == null) {
			return null;
		}
		
		map.put(PARAMETER_CURRENT_USER, user.getUsername());
		// DWE CHICA-761
		map.put(PARAMETER_REFRESH_PERIOD, 
		    Context.getAdministrationService().getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_GREASEBOARD_REFRESH)); 
		
		// DWE CHICA-488
		if (request.getParameter(ChirdlUtilConstants.PARAMETER_ERROR_MESSAGE) != null) {
			map.put(ChirdlUtilConstants.PARAMETER_ERROR_MESSAGE, 
			    request.getParameter(ChirdlUtilConstants.PARAMETER_ERROR_MESSAGE));
			return FORM_VIEW;
		}
		
		// DWE CHICA-884
		// Look up location attribute to determine if the confidentiality pop-up should be displayed to the mobile 
		// greaseboard
		String locationString = user.getUserProperty(ChirdlUtilConstants.USER_PROPERTY_LOCATION);
		
		if (locationString != null) {
			try {
				LocationService locationService = Context.getLocationService();
				Location location = locationService.getLocation(locationString);
				if (location != null)
				{
					ChirdlUtilBackportsService chirdlUtilBackportsService = 
					        Context.getService(ChirdlUtilBackportsService.class);
					ChirdlLocationAttributeValue locationAttributeValue = 
							chirdlUtilBackportsService.getLocationAttributeValue(location.getLocationId(), 
							    ChirdlUtilConstants.LOCATION_ATTR_DISPLAY_CONFIDENTIALITY_NOTICE);
					
					if(locationAttributeValue != null)
					{
						map.put(PARAMETER_DISPLAY_CONFIDENTIALITY_NOTICE, locationAttributeValue.getValue());
					}
				}
			} catch(Exception e) {
				log.error("Error retrieving location attribute {}. The confidentiality notice will not be displayed on the mobile greaseboard.",
						ChirdlUtilConstants.LOCATION_ATTR_DISPLAY_CONFIDENTIALITY_NOTICE,e);
			}	
		}
		
		return FORM_VIEW;
	}
}

