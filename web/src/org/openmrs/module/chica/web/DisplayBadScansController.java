package org.openmrs.module.chica.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.api.context.Context;
import org.openmrs.module.atd.service.ATDService;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;


public class DisplayBadScansController extends SimpleFormController {
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		return "testing";
	}

	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception {
		Map<String,Object> map = new HashMap<String,Object>();
		String badScansStr = request.getParameter("badScans");
		List<String> badScans = parseBadScans(badScansStr);
		String selectedForm = request.getParameter("selectedForm");
		if (selectedForm == null && badScans.size() > 0) {
			selectedForm = badScans.get(0);
		}
		
		map.put("badScans", badScans);
		map.put("selectedForm", selectedForm);
		String moveError = request.getParameter("moveError");
		map.put("moveError", moveError);
		return map;
	}
	
	@Override
	protected ModelAndView processFormSubmission(HttpServletRequest request, HttpServletResponse response, Object object, 
	                                             BindException errors) throws Exception {		
		String selectedForm = request.getParameter("badScansSelection");
		String badScansStr = request.getParameter("scans");
		String moveForm = request.getParameter("moveForm");
		List<String> badScans = parseBadScans(badScansStr);
		String view = getSuccessView();
		if ("true".equalsIgnoreCase(moveForm)) {
			boolean rescannedForm = false;
			String rescannedFormStr = request.getParameter("rescannedForm");
			if ("true".equalsIgnoreCase(rescannedFormStr)) {
				rescannedForm = true;
			}
			ATDService atdService = Context.getService(ATDService.class);
			try {
				atdService.moveBadScan(selectedForm, rescannedForm);
				badScans.remove(selectedForm);
				return new ModelAndView(
					new RedirectView(view+"?badScans=" + createString(badScans)));
			} catch (Exception e) {
				return new ModelAndView(
					new RedirectView(view+"?badScans=" + createString(badScans) + "&selectedForm=" + selectedForm + 
						"&moveError=true"));
			}
		}
		
		return new ModelAndView(
			new RedirectView(view+"?badScans=" + createString(badScans) + "&selectedForm=" + selectedForm));
	}
	
	private List<String> parseBadScans(String badScansStr) {
		List<String> badScans = new ArrayList<String>();
		if (badScansStr != null) {
			StringTokenizer tokenizer = new StringTokenizer(badScansStr, ",");
			while (tokenizer.hasMoreTokens()) {
				String fileLoc = tokenizer.nextToken();
				badScans.add(fileLoc);
			}
		}
		
		return badScans;
	}
	
	private String createString(List<String> list) {
		StringBuffer buffer = new StringBuffer();
		int i = 0;
		for (String item : list) {
			if (i != 0) {
				buffer.append(",");
				buffer.append(item);
			} else {
				buffer.append(item);
			}
			
			i++;
		}
		
		return buffer.toString();
	}
}
