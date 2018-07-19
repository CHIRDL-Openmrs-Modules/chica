package org.openmrs.module.chica.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class NoActionController {
    
    /** Form views */
    private static final String FORM_VIEW_SESSION_TIMEOUT= "/module/chica/sessionTimeout";
	
    /**
     * Form initialization method.
     * 
     * @return The form view name
     */
    @RequestMapping(value = "module/chica/sessionTimeout.form", method = RequestMethod.GET)
    protected String initForm() {
		return FORM_VIEW_SESSION_TIMEOUT;
	}
}
