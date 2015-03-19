package org.openmrs.module.chica.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.result.Result;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.dss.hibernateBeans.Rule;
import org.openmrs.module.dss.service.DssService;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;


public class MobileFormsCompletionController extends SimpleFormController {
	
	private static final String STAFF_NOTIFICATION = "Staff_notification";
	private static final String PARAM_PATIENT_ID = "patientId";
	private static final String PARAM_LOCATION_ID = "locationId";
	private static final String PARAM_PATIENT = "patient";
	private static final String PARAM_NOTIFICATIONS = "notifications";

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
		Map<String, Object> map = new HashMap<String, Object>();
		String view = getSuccessView();
		return new ModelAndView(new RedirectView(view), map);
	}
	
	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception {
		String patientIdStr = request.getParameter(PARAM_PATIENT_ID);
		String locationIdStr = request.getParameter(PARAM_LOCATION_ID);
		Integer locationId = Integer.parseInt(locationIdStr);
		Patient patient = Context.getPatientService().getPatient(Integer.parseInt(patientIdStr));
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(PARAM_PATIENT, patient);
		map.put(PARAM_NOTIFICATIONS, runRules(patient, locationId));
		return map;
	}
	
	/**
	 * Runs all rules with the rule type of Staff_notification and adds the result to a list.
	 * 
	 * @param patient The patient used to run the rules.
	 * @param locationId The clinic location of the patient.
	 * @return List of string results of the rules that are run.
	 */
	private List<String> runRules(Patient patient, Integer locationId) {
		List<String> notifications = new ArrayList<String>();
		DssService dssService = Context.getService(DssService.class);
		Rule rule = new Rule();
		rule.setRuleType(STAFF_NOTIFICATION);
		List<Rule> rules = dssService.getRules(rule, true, false, null);
		if (rules == null || rules.size() == 0) {
			return notifications;
		}
		
		Map<String,Object> parameters = new HashMap<String,Object>();
		parameters.put(ChirdlUtilConstants.PARAMETER_MODE, ChirdlUtilConstants.PARAMETER_VALUE_PRODUCE);
		
		// The LocationAttributeLookup rule requires a location ID through a FormInstance object.
		FormInstance formInstance = new FormInstance(locationId, null, null);
		parameters.put(ChirdlUtilConstants.PARAMETER_FORM_INSTANCE, formInstance);
		for (Rule foundRule : rules) {
			foundRule.setParameters(parameters);
		}
		
		List<Result> results = dssService.runRules(patient, rules);
		if (results == null || results.size() == 0) {
			return notifications;
		}
		
		for (Result result : results) {
			if (result.isEmpty() || result.isNull()) {
				continue;
			}
			
			for ( Result subResult : result) {
				String value = subResult.toString().trim();
				if (value.length() > 0) {
					notifications.add(value);
				}
			}
		}
		
		return notifications;
	}
}
