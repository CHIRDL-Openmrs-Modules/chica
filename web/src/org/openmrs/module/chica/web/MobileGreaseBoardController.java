package org.openmrs.module.chica.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.User;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.util.PatientRow;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;


public class MobileGreaseBoardController extends SimpleFormController {
	
	private Log log = LogFactory.getLog(this.getClass());

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
		if (Context.getAuthenticatedUser() == null) {
			return new ModelAndView(new RedirectView(getSuccessView()), map);
		}
		
		String patientId = request.getParameter("patientId");
		String encounterId = request.getParameter("encounterId");
		String sessionId = request.getParameter("sessionId");
		ChirdlUtilBackportsService backportsService = Context.getService(ChirdlUtilBackportsService.class);
		String key = patientId + ChirdlUtilConstants.GENERAL_INFO_UNDERSCORE + encounterId + ChirdlUtilConstants.GENERAL_INFO_UNDERSCORE;
		String formIdKey = key + "formId";
		String formIdStr = request.getParameter(formIdKey);
		Integer formId = Integer.parseInt(formIdStr);
		
		String formInstanceIdKey = key + "formInstanceId";
		String formInstanceIdStr = request.getParameter(formInstanceIdKey);
		
		String locationIdKey = key + "locationId";
		String locationIdStr = request.getParameter(locationIdKey);
		
		FormInstance formInstance = new FormInstance(Integer.parseInt(locationIdStr), formId, 
			Integer.parseInt(formInstanceIdStr));
		List<PatientState> patientStates = backportsService.getPatientStatesByFormInstance(formInstance, false);
		Integer locationTagId = null;
		if (patientStates != null && patientStates.size() > 0) {
			for (PatientState patientState : patientStates) {
				locationTagId = patientState.getLocationTagId();
				if (locationTagId != null) {
					break;
				}
			}
		}
		
		String nextPage = Util.getFormUrl(formId);
		map.put("patientId", patientId);
		map.put("encounterId", encounterId);
		map.put("sessionId", sessionId);
		map.put("formInstance", locationIdStr + ChirdlUtilConstants.GENERAL_INFO_UNDERSCORE + locationTagId.toString() + ChirdlUtilConstants.GENERAL_INFO_UNDERSCORE + 
			formIdStr + ChirdlUtilConstants.GENERAL_INFO_UNDERSCORE + formInstanceIdStr);
		return new ModelAndView(new RedirectView(nextPage), map);
	}
	
	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception {
		User user = Context.getUserContext().getAuthenticatedUser();
		if (user == null) {
			return null;
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			ArrayList<PatientRow> rows = new ArrayList<PatientRow>();
			String result = org.openmrs.module.chica.util.Util.getPatientsWithPrimaryForms(rows, null);
			if (result != null) {
				map.put("errorMessage", result);
				return map;
			}
			
			map.put("patientRows", rows);
			map.put("currentUser", user.getUsername());
		}
		catch (UnexpectedRollbackException ex) {
			//ignore this exception since it happens with an APIAuthenticationException
		}
		catch (APIAuthenticationException ex2) {
			//ignore this exception. It happens during the redirect to the login page
		}
		catch (Throwable e) {
			log.error("Error retrieving awaiting patients", e);
			map.put("errorMessage", e.getMessage());
		}
		
		return map;
	}
}
