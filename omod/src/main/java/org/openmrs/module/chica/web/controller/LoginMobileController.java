package org.openmrs.module.chica.web.controller;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping(value = "module/chica/loginMobile.form")
public class LoginMobileController {
    
    /** Form view */
    private static final String FORM_VIEW = "/module/chica/loginMobile";
    
    /**
     * Handles submission of the page.
     * 
     * @param request The HTTP request information
     * @return The name of the next view
     */
    @RequestMapping(method = RequestMethod.POST)
    protected ModelAndView processSubmit(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		String username = request.getParameter("username_field");
		String password = request.getParameter("password_field");
		Enumeration<String> paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String param = paramNames.nextElement();
			if ("username_field".equals(param) || "password_field".equals(param)) {
				continue;
			}
			
			map.put(param, request.getParameter(param));
		}
		
		if (username == null || username.trim().length() == 0 || password == null || password.trim().length() == 0) {
			map.put("errorMessage", "Invalid username/password");
			return new ModelAndView(FORM_VIEW, map);
		}
		
		try {
			Context.authenticate(username, password);
		} catch (ContextAuthenticationException e) {
			map.put("errorMessage", "Invalid username/password");
			return new ModelAndView(FORM_VIEW, map);
		}
		
		String view = request.getParameter("redirect");
		if (view == null || view.trim().length() == 0) {
			return new ModelAndView(FORM_VIEW, map);
		}
		
		return new ModelAndView(new RedirectView(view), map);
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
		return FORM_VIEW;
	}
}
