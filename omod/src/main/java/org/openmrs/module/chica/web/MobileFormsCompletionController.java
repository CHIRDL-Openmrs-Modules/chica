package org.openmrs.module.chica.web;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.util.ChicaConstants;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;


public class MobileFormsCompletionController extends SimpleFormController {
	
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
	protected ModelAndView processFormSubmission(HttpServletRequest request, HttpServletResponse response, Object object,
	                                             BindException errors) throws Exception {
		Map<String, Object> map = new HashMap<>();
		String view = getSuccessView();
                String language = request.getParameter(ChicaConstants.PARAMETER_LANGUAGE);
		map.put(ChicaConstants.PARAMETER_LANGUAGE, language);
		return new ModelAndView(new RedirectView(view), map);
	}
	
	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception {
		String patientIdStr = request.getParameter("patientId");
		Patient patient = Context.getPatientService().getPatient(Integer.parseInt(patientIdStr));
		Map<String, Object> map = new HashMap<>();
		map.put("patient", patient);
		return map;
	}
}
