/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.chica.web;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.module.chica.util.Util;
import org.openmrs.module.chica.vendor.Vendor;
import org.openmrs.module.chica.vendor.VendorFactory;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstanceTag;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Session;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.module.chirdlutilbackports.util.PatientStateStartDateComparator;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;


/**
 * Controller used to authenticate an external user before allowing view of a form.
 *
 * @author Steve McKee
 */
public class ExternalFormController extends SimpleFormController {
	
	private static final String DASH = "-";
	private static final String PARAM_FORM_INSTANCE = "formInstance";
	private static final String PARAM_PATIENT_ID = "patientId";
	private static final String PARAM_ENCOUNTER_ID = "encounterId";
	private static final String PARAM_SESSION_ID = "sessionId";
	private static final String PARAM_MISSING_FORM_INSTANCE = "missingFormInstance";
	private static final String PARAM_MISSING_ENCOUNTER = "missingEncounter";
	private static final String PARAM_INVALID_END_STATE = "invalidEndState";
	private static final String PARAM_INVALID_START_STATE = "invalidStartState";
	private static final String PARAM_MISSING_END_STATE = "missingEndState";
	private static final String PARAM_MISSING_START_STATE = "missingStartState";
	private static final String PARAM_INVALID_PATIENT = "invalidPatient";
	private static final String PARAM_INVALID_FORM = "invalidForm";
	private static final String PARAM_PROVIDER_ID = "providerId";
	private static final String PARAM_END_STATE = "endState";
	private static final String PARAM_START_STATE = "startState";
	private static final String PARAM_FAILED_AUTHENTICATION = "failedAuthentication";
	private static final String PARAM_MISSING_PROVIDER_ID = "missingProviderId";
	private static final String PARAM_MISSING_PASSWORD = "missingPassword";
	private static final String PARAM_MISSING_USER = "missingUser";
	private static final String PARAM_MISSING_MRN = "missingMRN";
	private static final String PARAM_MISSING_FORM_PAGE = "missingFormPage";
	private static final String PARAM_MISSING_FORM = "missingForm";
	private static final String PARAM_MRN = "mrn";
	private static final String PARAM_FORM_PAGE = "formPage";
	private static final String PARAM_FORM_NAME = "formName";
	private static final String PARAM_INVALID_VENDOR = "invalidVendor";
	private static final String PARAM_MISSING_VENDOR = "missingVendor";
	private static final String PARAM_VAL_TRUE = "true";
	private static final String PARAM_HAS_ERRORS = "hasErrors";
	private static final String PARAM_VENDOR = "vendor";
	//private static final String PERSON_ATTR_TYPE_PROVIDER_ID = "Provider ID";
	
	/**
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		return "testing";
	}
	
	/**
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#referenceData(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Map<String, Object> referenceData(HttpServletRequest request) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		
		String vendorStr = request.getParameter(PARAM_VENDOR);
    	map.put(PARAM_VENDOR, vendorStr);
    	if (vendorStr == null || vendorStr.trim().length() == 0) {
			map.put(PARAM_HAS_ERRORS, PARAM_VAL_TRUE);
			map.put(PARAM_MISSING_VENDOR, PARAM_VAL_TRUE);
			return map;
		}
    	
    	Vendor vendor = VendorFactory.getVendor(vendorStr, request);
    	if (vendor == null) {
    		map.put(PARAM_HAS_ERRORS, PARAM_VAL_TRUE);
			map.put(PARAM_INVALID_VENDOR, PARAM_VAL_TRUE);
			return map;
    	}
    	
		String formName = vendor.getFormName();
		String formPage = vendor.getFormPage();
		String mrn = vendor.getMrn();
		map.put(PARAM_FORM_NAME, formName);
		map.put(PARAM_FORM_PAGE, formPage);
		map.put(PARAM_MRN, mrn);
		
		if (formName == null || formName.trim().length() == 0) {
			map.put(PARAM_HAS_ERRORS, PARAM_VAL_TRUE);
			map.put(PARAM_MISSING_FORM, PARAM_VAL_TRUE);
			return map;
		}
		
		if (formPage == null || formPage.trim().length() == 0) {
			map.put(PARAM_HAS_ERRORS, PARAM_VAL_TRUE);
			map.put(PARAM_MISSING_FORM_PAGE, PARAM_VAL_TRUE);
			return map;
		}
		
		if (mrn == null || mrn.trim().length() == 0) {
			map.put(PARAM_HAS_ERRORS, PARAM_VAL_TRUE);
			map.put(PARAM_MISSING_MRN, PARAM_VAL_TRUE);
			return map;
		}
		
		String username = vendor.getUsername();
		if (username == null || username.trim().length() == 0) {
			map.put(PARAM_HAS_ERRORS, PARAM_VAL_TRUE);
			map.put(PARAM_MISSING_USER, PARAM_VAL_TRUE);
			return map;
		}
		
		String password = vendor.getPassword();
		if (password == null || password.trim().length() == 0) {
			map.put(PARAM_HAS_ERRORS, PARAM_VAL_TRUE);
			map.put(PARAM_MISSING_PASSWORD, PARAM_VAL_TRUE);
			return map;
		}
		
		String providerId = vendor.getProviderId();
		if (providerId == null || providerId.trim().length() == 0) {
			map.put(PARAM_HAS_ERRORS, PARAM_VAL_TRUE);
			map.put(PARAM_MISSING_PROVIDER_ID, PARAM_VAL_TRUE);
			return map;
		}
		
		try {
			Context.authenticate(username, password);
		} catch (ContextAuthenticationException e) {
			// username/password not valid
			map.put(PARAM_HAS_ERRORS, PARAM_VAL_TRUE);
			map.put(PARAM_FAILED_AUTHENTICATION, PARAM_VAL_TRUE);
			return map;
		}
		
		return map;
	}

	/**
     * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
     */
    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command,
                                    BindException errors) throws Exception {
    	Map<String, Object> map = new HashMap<String, Object>();
    	String view = getFormView();
    	
    	String vendorStr = request.getParameter(PARAM_VENDOR);
    	map.put(PARAM_VENDOR, vendorStr);
    	if (vendorStr == null || vendorStr.trim().length() == 0) {
			map.put(PARAM_HAS_ERRORS, PARAM_VAL_TRUE);
			map.put(PARAM_MISSING_VENDOR, PARAM_VAL_TRUE);
			return new ModelAndView(view, map);
		}
    	
    	Vendor vendor = VendorFactory.getVendor(vendorStr, request);
    	if (vendor == null) {
    		map.put(PARAM_HAS_ERRORS, PARAM_VAL_TRUE);
			map.put(PARAM_INVALID_VENDOR, PARAM_VAL_TRUE);
			return new ModelAndView(view, map);
    	}
    	
    	String formName = vendor.getFormName();
    	String formPage = vendor.getFormPage();
    	String mrn = vendor.getMrn();
    	String startStateStr = vendor.getStartState();
    	String endStateStr = vendor.getEndState();
    	String providerId = vendor.getProviderId();
    	
    	map.put(PARAM_FORM_NAME, formName);
		map.put(PARAM_FORM_PAGE, formPage);
		map.put(PARAM_MRN, mrn);
		map.put(PARAM_START_STATE, startStateStr);
		map.put(PARAM_END_STATE, endStateStr);
		map.put(PARAM_PROVIDER_ID, providerId);
		map.put(PARAM_VENDOR, vendor);
		
		Form form = Context.getFormService().getForm(formName);
		if (form == null) {
			map.put(PARAM_HAS_ERRORS, PARAM_VAL_TRUE);
			map.put(PARAM_INVALID_FORM, PARAM_VAL_TRUE);
			return new ModelAndView(view, map);
		}
		
		Patient patient = getPatientByMRN(mrn);
		if (patient == null) {
			map.put(PARAM_HAS_ERRORS, PARAM_VAL_TRUE);
			map.put(PARAM_INVALID_PATIENT, PARAM_VAL_TRUE);
			return new ModelAndView(view, map);
		}
		
		if (startStateStr == null || startStateStr.trim().length() == 0) {
			map.put(PARAM_HAS_ERRORS, PARAM_VAL_TRUE);
			map.put(PARAM_MISSING_START_STATE, PARAM_VAL_TRUE);
			return new ModelAndView(view, map);
		}
		
		if (endStateStr == null || endStateStr.trim().length() == 0) {
			map.put(PARAM_HAS_ERRORS, PARAM_VAL_TRUE);
			map.put(PARAM_MISSING_END_STATE, PARAM_VAL_TRUE);
			return new ModelAndView(view, map);
		}
		
		ChirdlUtilBackportsService backportsService = Context.getService(ChirdlUtilBackportsService.class);
		State startState = backportsService.getStateByName(startStateStr);
		if (startState == null) {
			map.put(PARAM_HAS_ERRORS, PARAM_VAL_TRUE);
			map.put(PARAM_INVALID_START_STATE, PARAM_VAL_TRUE);
			return new ModelAndView(view, map);
		}
		
		State endState = backportsService.getStateByName(endStateStr);
		if (endState == null) {
			map.put(PARAM_HAS_ERRORS, PARAM_VAL_TRUE);
			map.put(PARAM_INVALID_END_STATE, PARAM_VAL_TRUE);
			return new ModelAndView(view, map);
		}
		
		if (providerId == null || providerId.trim().length() == 0) {
			map.put(PARAM_HAS_ERRORS, PARAM_VAL_TRUE);
			map.put(PARAM_MISSING_PROVIDER_ID, PARAM_VAL_TRUE);
			return new ModelAndView(view, map);
		}
		
//		PersonAttribute pat = backportsService.getPersonAttributeByValue(PERSON_ATTR_TYPE_PROVIDER_ID, providerId);
//		if (pat == null) {
//			map.put("hasErrors", "true");
//			map.put("invalidProviderId", "true");
//			return new ModelAndView(view, map);
//		}
		
		Encounter encounter = getRecentEncounter(
			patient, backportsService, startState.getStateId(), endState.getStateId(), form.getFormId());
		if (encounter == null) {
			map.put(PARAM_HAS_ERRORS, PARAM_VAL_TRUE);
			map.put(PARAM_MISSING_ENCOUNTER, PARAM_VAL_TRUE);
			return new ModelAndView(view, map);
		}
		
		FormInstanceTag tag = getFormInstanceInfo(encounter.getEncounterId(), form.getFormId(), startState.getStateId(), 
			endState.getStateId(), backportsService);
		if (tag == null) {
			map.put(PARAM_HAS_ERRORS, PARAM_VAL_TRUE);
			map.put(PARAM_MISSING_FORM_INSTANCE, PARAM_VAL_TRUE);
			return new ModelAndView(view, map);
		}
		
		List<Session> sessions = backportsService.getSessionsByEncounter(encounter.getEncounterId());
		if (sessions != null && sessions.size() > 0) {
			map.put(PARAM_SESSION_ID, sessions.get(0).getSessionId());
		}
		
		map.put(PARAM_ENCOUNTER_ID, encounter.getEncounterId());
		map.put(PARAM_PATIENT_ID, patient.getPatientId());
		map.put(PARAM_FORM_INSTANCE, tag.toString());
    	
	    return new ModelAndView(new RedirectView(formPage), map);
    }
    
    private Patient getPatientByMRN(String mrn) {
    	PatientService patientService = Context.getPatientService();
		List<PatientIdentifierType> types = new ArrayList<PatientIdentifierType>();
	    types.add(patientService.getPatientIdentifierTypeByName(ChirdlUtilConstants.IDENTIFIER_TYPE_MRN));
	    mrn = org.openmrs.module.chirdlutil.util.Util.removeLeadingZeros(mrn);
	    List<Patient> patients = patientService.getPatients(null, mrn, types, true);
	    if (patients.size() > 0) {
	    	Patient patient = patients.get(0);
	    	return patient;
	    }
	    
	    // Patient not found by MRN.  Try spotting a dash.
	    int dashIndex = mrn.indexOf(DASH);
	    if (dashIndex < 0) {
	    	// Place a dash in the next-to-last character position and ask again.
	    	int length = mrn.length();
	    	int position = length - 1;
	    	if (position >= 0) {
	    		String firstPart = mrn.substring(0, position);
	    		String lastPart = mrn.substring(position, length);
	    		String newMrn = firstPart + DASH + lastPart;
	    		patients = patientService.getPatients(null, newMrn, types, true);
			    if (patients.size() > 0) {
			    	Patient patient = patients.get(0);
			    	return patient;
			    }
	    	}
	    }
	    
	    return null;
    }
    
    private Encounter getRecentEncounter(Patient patient, ChirdlUtilBackportsService backportsService, Integer startStateId, 
                                         Integer endStateId, Integer formId) {
    	// Get last encounter with last day
		Calendar startCal = Calendar.getInstance();
		startCal.set(GregorianCalendar.DAY_OF_MONTH, startCal.get(GregorianCalendar.DAY_OF_MONTH) - 2);
		Date startDate = startCal.getTime();
		Date endDate = Calendar.getInstance().getTime();
		List<Encounter> encounters = Context.getEncounterService().getEncounters(patient, null, startDate, endDate, null, 
			null, null, false);
		if (encounters == null || encounters.size() == 0) {
			return null;
		} else if (encounters.size() == 1) {
			return encounters.get(0);
		}
		
		// Do a check to find the latest encounters with observations without a scanned timestamp for the PWS.
		for (int i = encounters.size() - 1; i >= 0; i--) {
			Encounter encounter = encounters.get(i);
			Map<Integer, List<PatientState>> formIdToPatientStateMapStart = new HashMap<Integer, List<PatientState>>();
	    	Map<Integer, List<PatientState>> formIdToPatientStateMapEnd = new HashMap<Integer, List<PatientState>>();
	    	Integer encounterId = encounter.getEncounterId();
	    	
	    	Util.getPatientStatesByEncounterId(
	    		backportsService, formIdToPatientStateMapStart, encounterId, startStateId, true);
	    	Util.getPatientStatesByEncounterId(
	    		backportsService, formIdToPatientStateMapEnd, encounterId, endStateId, true);
	    	
	    	boolean containsStartState = formIdToPatientStateMapStart.containsKey(formId);
			boolean containsEndState = formIdToPatientStateMapEnd.containsKey(formId);
			
			if (containsStartState && !containsEndState) {
				return encounter;
			}
		}
		
		return null;
    }
    
    private FormInstanceTag getFormInstanceInfo(Integer encounterId, Integer formId, Integer startStateId, 
                                                Integer endStateId, ChirdlUtilBackportsService backportsService) {
    	Map<Integer, List<PatientState>> formIdToPatientStateMapStart = new HashMap<Integer, List<PatientState>>();
    	Map<Integer, List<PatientState>> formIdToPatientStateMapEnd = new HashMap<Integer, List<PatientState>>();
    	
    	Util.getPatientStatesByEncounterId(
    		backportsService, formIdToPatientStateMapStart, encounterId, startStateId, true);
    	Util.getPatientStatesByEncounterId(
    		backportsService, formIdToPatientStateMapEnd, encounterId, endStateId, true);
    	
    	boolean containsStartState = formIdToPatientStateMapStart.containsKey(formId);
		boolean containsEndState = formIdToPatientStateMapEnd.containsKey(formId);
		
		if (containsStartState) {
			List<PatientState> patientStates = null;
			if (!containsEndState) {
				patientStates = formIdToPatientStateMapStart.get(formId);
				if (patientStates != null) {
					Collections.sort(patientStates, new PatientStateStartDateComparator(PatientStateStartDateComparator.DESCENDING));
					for (PatientState patientState : patientStates) {
						FormInstance formInstance = patientState.getFormInstance();
						if (formInstance != null) {
							FormInstanceTag tag = new FormInstanceTag(patientState.getLocationId(), formId, 
								patientState.getFormInstanceId(), patientState.getLocationTagId());
							return tag;
						}
					}
				}
			} else {
				patientStates = formIdToPatientStateMapEnd.get(formId);
				if (patientStates != null) {
					Collections.sort(patientStates, new PatientStateStartDateComparator(PatientStateStartDateComparator.DESCENDING));
					for (PatientState patientState : patientStates) {
						if (patientState.getEndTime() == null) {
							FormInstance formInstance = patientState.getFormInstance();
							if (formInstance != null) {
								FormInstanceTag tag = new FormInstanceTag(patientState.getLocationId(), formId, 
									patientState.getFormInstanceId(), patientState.getLocationTagId());
								return tag;
							}
						}
					}
				}
			}
		}
    	
    	return null;
    }
}