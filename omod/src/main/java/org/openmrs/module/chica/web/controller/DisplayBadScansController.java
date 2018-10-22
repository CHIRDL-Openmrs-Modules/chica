package org.openmrs.module.chica.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.jfree.util.Log;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping(value = "module/chica/displayBadScans.form")
public class DisplayBadScansController {
    
    /** Form view */
    private static final String FORM_VIEW = "/module/chica/displayBadScans";
    
    /** Success view */
    private static final String SUCCESS_VIEW = "displayBadScans.form";
    
    /** Parameters */
    private static final String PARAMETER_RESCANNED_FORM = "rescannedForm";
    private static final String PARAMETER_MOVE_FORM = "moveForm";
    private static final String PARAMETER_SCANS = "scans";
    private static final String PARAMETER_BAD_SCANS_SELECTION = "badScansSelection";
    private static final String PARAMETER_MOVE_ERROR = "moveError";
    private static final String PARAMETER_SELECTED_FORM = "selectedForm";
    private static final String PARAMETER_BAD_SCANS = "badScans";
    
    /** URL Parameters */
    private static final String URL_PARAMETER_SELECTED_FORM = "&selectedForm=";
    private static final String URL_PARAMETER_BAD_SCANS = "?badScans=";


    /**
     * Form initialization method.
     * 
     * @param request The HTTP request information
     * @param map The map to populate for return to the client
     * @return The form view name
     */
    @RequestMapping(method = RequestMethod.GET)
    protected String initForm(HttpServletRequest request, ModelMap map) {
		String badScansStr = request.getParameter(PARAMETER_BAD_SCANS);
		List<String> badScans = parseBadScans(badScansStr);
		String selectedForm = request.getParameter(PARAMETER_SELECTED_FORM);
		if (selectedForm == null && !badScans.isEmpty()) {
			selectedForm = badScans.get(0);
		}
		
		map.put(PARAMETER_BAD_SCANS, badScans);
		map.put(PARAMETER_SELECTED_FORM, selectedForm);
		String moveError = request.getParameter(PARAMETER_MOVE_ERROR);
		map.put(PARAMETER_MOVE_ERROR, moveError);
		return FORM_VIEW;
	}
	
    /**
     * Handles submission of the page.
     * 
     * @param request The HTTP request information
     * @return The name of the next view
     */
    @RequestMapping(method = RequestMethod.POST)
    protected ModelAndView processSubmit(HttpServletRequest request) {
		String selectedForm = request.getParameter(PARAMETER_BAD_SCANS_SELECTION);
		String badScansStr = request.getParameter(PARAMETER_SCANS);
		String moveForm = request.getParameter(PARAMETER_MOVE_FORM);
		List<String> badScans = parseBadScans(badScansStr);
		if (ChirdlUtilConstants.GENERAL_INFO_TRUE.equalsIgnoreCase(moveForm)) {
			boolean rescannedForm = false;
			String rescannedFormStr = request.getParameter(PARAMETER_RESCANNED_FORM);
			if (ChirdlUtilConstants.GENERAL_INFO_TRUE.equalsIgnoreCase(rescannedFormStr)) {
				rescannedForm = true;
			}
			ATDService atdService = Context.getService(ATDService.class);
			try {
				atdService.moveBadScan(selectedForm, rescannedForm);
				badScans.remove(selectedForm);
				return new ModelAndView(
					new RedirectView(SUCCESS_VIEW+URL_PARAMETER_BAD_SCANS + createString(badScans)));
			} catch (Exception e) {
			    Log.error("Error moving/removing bad scans.  Selected form: " + selectedForm, e);
				return new ModelAndView(new RedirectView(SUCCESS_VIEW+URL_PARAMETER_BAD_SCANS + createString(badScans) + 
					    URL_PARAMETER_SELECTED_FORM + selectedForm + "&moveError=true"));
			}
		}
		
		return new ModelAndView(
			new RedirectView(SUCCESS_VIEW+URL_PARAMETER_BAD_SCANS + createString(badScans) + URL_PARAMETER_SELECTED_FORM + 
			    selectedForm));
	}
	
	private List<String> parseBadScans(String badScansStr) {
		List<String> badScans = new ArrayList<>();
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
		StringBuilder builder = new StringBuilder();
		int i = 0;
		for (String item : list) {
			if (i != 0) {
				builder.append(ChirdlUtilConstants.GENERAL_INFO_COMMA);
				builder.append(item);
			} else {
				builder.append(item);
			}
			
			i++;
		}
		
		return builder.toString();
	}
}
