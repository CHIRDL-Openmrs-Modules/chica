package org.openmrs.module.chica.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;


@Controller
@RequestMapping(value = "module/chica/viewPatient.form")
public class ViewPatientController
{

	/** Form view */
	private static final String FORM_VIEW = "/module/chica/viewPatient";
	
	@RequestMapping(method = RequestMethod.GET)
	protected String initForm(HttpServletRequest request, ModelMap map) throws Exception
	{	
		return FORM_VIEW;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	protected ModelAndView processSubmit(HttpServletRequest request, HttpServletResponse response, Object obj) throws Exception {
		
		Map<String, Object> map = new HashMap<>();
		return new ModelAndView(new RedirectView(FORM_VIEW), map);
	}
	
}


