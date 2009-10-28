package org.openmrs.module.chica.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.patient.impl.LuhnIdentifierValidator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;



public class ManualCheckinSSNMRNController extends SimpleFormController
{

	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception
	{
		return "testing";
	}

	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception
	{
		Map<String, Object> map = new HashMap<String, Object>();
		
		String mrn = request.getParameter("mrnLookup");
		
		if (mrn != null){
			map.put("mrn", mrn);
			map.put("mrnLookup", mrn);
			map.put("validate", "valid");
		} 
		
		String validity = (String) request.getAttribute("validate");

		return map;
	}
	
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object obj, org.springframework.validation.BindException errors) throws Exception {
		
		HttpSession httpSession = request.getSession();
		
		String mrn = request.getParameter("mrnLookup");
		
		LuhnIdentifierValidator luhn = new LuhnIdentifierValidator();
		boolean valid = false;
		if (mrn != null && !mrn.contains("-") && mrn.length() > 1)
		{
			mrn = mrn.substring(0, mrn.length() - 1) + "-" + mrn.substring(mrn.length()-1);
		
		}
		
		try {
			if (mrn!= null && mrn.length()> 0 && !mrn.endsWith("-")){
				valid = luhn.isValid(mrn);
			}
		} catch (Exception e) {
			log.error("Error validating MRN: " + mrn);
		}
		
		if (valid){
			Map<String, Object> model = new HashMap<String, Object>();
		    model.put("mrnLookup", mrn);
		   
		 
		    return new ModelAndView(new RedirectView(getSuccessView()), model);
			
		}

		return showForm(request, response, errors);
	}
	
	
}
