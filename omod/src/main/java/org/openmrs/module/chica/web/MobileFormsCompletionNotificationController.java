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
import org.openmrs.module.chica.TabletNotification;
import org.openmrs.module.chica.util.ChicaConstants;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.dss.hibernateBeans.Rule;
import org.openmrs.module.dss.service.DssService;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Controller for handling the display of any notifications to the MA when finished with patients.
 *
 * @author Steve McKee
 */
public class MobileFormsCompletionNotificationController extends SimpleFormController {
	
	private static final String STAFF_NOTIFICATION = "Staff_notification";
	private static final String PARAM_PATIENT_ID = "patientId";
	private static final String PARAM_LOCATION_ID = "locationId";
	private static final String PARAM_PATIENT = "patient";
	private static final String PARAM_NOTIFICATIONS = "notifications";
	private static final String PARAM_ENCOUNTER_ID = "encounterId";
	private static final String PARAM_LOCATION_TAG_ID = "locationTagId";
	private static final String PARAM_SESSION_ID = "sessionId";

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
		return new ModelAndView(new RedirectView(view), map);
	}
	
	@Override
	protected Map<String, Object> referenceData(HttpServletRequest request) throws Exception {
		String patientIdStr = request.getParameter(PARAM_PATIENT_ID);
		String locationIdStr = request.getParameter(PARAM_LOCATION_ID);
		String encounterIdStr = request.getParameter(PARAM_ENCOUNTER_ID);
		String locationTagIdStr = request.getParameter(PARAM_LOCATION_TAG_ID);
		String sessionIdStr = request.getParameter(PARAM_SESSION_ID);
        String language = request.getParameter(ChicaConstants.PARAMETER_LANGUAGE);
        String userQuitForm = request.getParameter(ChicaConstants.PARAMETER_USER_QUIT_FORM);
		Integer locationId = Integer.parseInt(locationIdStr);
		Patient patient = Context.getPatientService().getPatient(Integer.parseInt(patientIdStr));
		
		Map<String,Object> parameters = new HashMap<>();
		parameters.put(ChirdlUtilConstants.PARAMETER_MODE, ChirdlUtilConstants.PARAMETER_VALUE_PRODUCE);
		// The LocationAttributeLookup rule requires a location ID through a FormInstance object.
		FormInstance formInstance = new FormInstance(locationId, null, null);
		parameters.put(ChirdlUtilConstants.PARAMETER_FORM_INSTANCE, formInstance);
		parameters.put(ChirdlUtilConstants.PARAMETER_LOCATION_ID, locationId);
		parameters.put(ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID, Integer.parseInt(encounterIdStr));
		parameters.put(ChirdlUtilConstants.PARAMETER_LOCATION_TAG_ID, Integer.parseInt(locationTagIdStr));
		parameters.put(ChirdlUtilConstants.PARAMETER_SESSION_ID, Integer.parseInt(sessionIdStr));
		
		Map<String, Object> map = new HashMap<>();
		map.put(PARAM_PATIENT, patient);
		map.put(PARAM_NOTIFICATIONS, runRules(patient, parameters));
        map.put(ChicaConstants.PARAMETER_LANGUAGE, language);
        map.put(ChicaConstants.PARAMETER_USER_QUIT_FORM, userQuitForm);
		
		return map;
	}
	
	/**
	 * Runs all rules with the rule type of Staff_notification and adds the result to a list.
	 * 
	 * @param patient The patient used to run the rules.
	 * @param parameters Map of parameters that will be passed to the executing rules.
	 * @return List of string results of the rules that are run.
	 */
	private List<TabletNotification> runRules(Patient patient, Map<String,Object> parameters) {
		List<TabletNotification> notifications = new ArrayList<>();
		DssService dssService = Context.getService(DssService.class);
		List<Rule> rules = dssService.getRulesByType(STAFF_NOTIFICATION);
		if (rules == null || rules.isEmpty()) {
			return notifications;
		}
		
		for (int i = rules.size() - 1; i >= 0; i--) {
			Rule foundRule = rules.get(i);
			if (foundRule.checkAgeRestrictions(patient)) {
				foundRule.setParameters(parameters);
			} else {
				rules.remove(i);
			}
		}
		
		List<Result> results = dssService.runRules(patient, rules);
		if (results == null || results.isEmpty()) {
			return notifications;
		}
		
		for (Result result : results) {
			if (result.isEmpty() || result.isNull()) {
				continue;
			}
			
			TabletNotification notification = new TabletNotification();
			boolean foundResults = false;
			for (int i = 0; i < result.size(); i++) {
				Result subResult = result.get(i);
				String value = subResult.toString().trim();
				if (value.length() > 0) {
					foundResults = true;
					if (i == 0) {
						notification.setStatement(value);
					} else {
						notification.addSubStatement(value);
					}
				}
			}
			
			if (foundResults) {
				notifications.add(notification);
			}
		}
		
		return notifications;
	}
}
