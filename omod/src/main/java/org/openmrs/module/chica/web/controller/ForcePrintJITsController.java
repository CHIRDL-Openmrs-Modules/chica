package org.openmrs.module.chica.web.controller;


import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "module/chica/forcePrintJITs.form")
public class ForcePrintJITsController {
	
    /** Logger for this class and any subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	/** Form view */
    private static final String FORM_VIEW = "/module/chica/forcePrintJITs";
	
	/**
     * Form initialization method.
     * 
     * @param request The HTTP request information
     * @param map The map to populate for return to the client
     * @return The form view name
     */
    @RequestMapping(method = RequestMethod.GET)
    protected String initForm(HttpServletRequest request, ModelMap map) {
		String patientIdStr = request.getParameter(ChirdlUtilConstants.PARAMETER_PATIENT_ID);
		map.put(ChirdlUtilConstants.PARAMETER_PATIENT_ID, patientIdStr);
		map.put(ChirdlUtilConstants.PARAMETER_SESSION_ID, request.getParameter(ChirdlUtilConstants.PARAMETER_SESSION_ID));
		map.put(ChirdlUtilConstants.PARAMETER_LOCATION_ID, request.getParameter(ChirdlUtilConstants.PARAMETER_LOCATION_ID));
		map.put(ChirdlUtilConstants.PARAMETER_LOCATION_TAG_ID, 
		    request.getParameter(ChirdlUtilConstants.PARAMETER_LOCATION_TAG_ID));
		
		Integer patientId = null;
		if (patientIdStr != null && patientIdStr.trim().length() > 0) {
			try {
				patientId = Integer.parseInt(patientIdStr);
				Patient patient = Context.getPatientService().getPatient(patientId);
				if (patient == null) {
					String message = "No valid patient found for patientId: " + patientIdStr;
					log.error(message);
					throw new IllegalArgumentException(message);
				}
				
				String givenName = patient.getGivenName();
				String familyName = patient.getFamilyName();
				map.put(ChirdlUtilConstants.PARAMETER_PATIENT_NAME, givenName + 
				    ChirdlUtilConstants.GENERAL_INFO_SINGLE_SPACE + familyName);
			} catch (NumberFormatException e) {
				String message = "Invalid patientId parameter provided: " + patientIdStr;
				log.error(message);
				throw new IllegalArgumentException(message);
			}
		} else {
			String message = "Required parameter patientId is missing";
			log.error(message);
			throw new IllegalArgumentException(message);
		}
		
		return FORM_VIEW;
	}
}
