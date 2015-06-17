package org.openmrs.module.chica.web;


import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class ForcePrintJITsController extends SimpleFormController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
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
	protected Map<String, Object> referenceData(HttpServletRequest request) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		String patientIdStr = request.getParameter("patientId");
		map.put("patientId", patientIdStr);
		map.put("sessionId", request.getParameter("sessionId"));
		map.put("locationId", request.getParameter("locationId"));
		map.put("locationTagId", request.getParameter("locationTagId"));
		
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
				map.put("patientName", givenName + " " + familyName);
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
		
		return map;
	}
}
