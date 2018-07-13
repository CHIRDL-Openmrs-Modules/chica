package org.openmrs.module.chica.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for forms that have their parts built dynamically.
 *
 * @author Steve McKee
 */
@Controller
public class DynamicMobileFormNotificationController {
    
    /** Form views */
    private static final String FORM_VIEW_PSF_MOBILE_DYNAMIC = "/module/chica/psfMobileDynamic";
    
    /**Success view */
    private static final String SUCCESS_VIEW = "finishFormsNotificationMobile.form";
	
	
	/**
     * Form initialization method.
     * 
     * @param request The HTTP request information
     * @param map The map to populate for return to the client
     * @return The form view name
     */
	@RequestMapping(value = "module/chica/psfMobileDynamic.form", method = RequestMethod.GET)
    protected String initPsfMobileDynamicForm(HttpServletRequest request, ModelMap map) {
		return FormControllerUtil.loadFormInformation(request, map, FORM_VIEW_PSF_MOBILE_DYNAMIC);
	}
	
    /**
     * Handles submission of the page.
     * 
     * @param request The HTTP request information
     * @return The name of the next view
     */
    @RequestMapping(method = RequestMethod.POST)
    protected ModelAndView processSubmit(HttpServletRequest request) {
        return FormControllerUtil.handleMobileFormSubmission(request, SUCCESS_VIEW);
	}
}
