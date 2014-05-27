package org.openmrs.module.chica.web;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;


public class LoginMobileController extends SimpleFormController {

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
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object object,
	                                             BindException errors) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
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
			return new ModelAndView(getFormView(), map);
		}
		
		try {
			Context.authenticate(username, password);
		} catch (ContextAuthenticationException e) {
			map.put("errorMessage", "Invalid username/password");
			return new ModelAndView(getFormView(), map);
		}
		
		String view = request.getParameter("redirect");
		if (view == null || view.trim().length() == 0) {
			return new ModelAndView(getFormView(), map);
		}
		
		return new ModelAndView(new RedirectView(view), map);
	}
	
	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		
		return map;
	}
}
