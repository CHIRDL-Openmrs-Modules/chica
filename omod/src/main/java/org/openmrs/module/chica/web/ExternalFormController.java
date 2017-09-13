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
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonName;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.module.chica.util.Util;
import org.openmrs.module.chica.vendor.Vendor;
import org.openmrs.module.chica.vendor.VendorFactory;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstanceTag;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.LocationTagAttributeValue;
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
		
		String vendorStr = request.getParameter(ChirdlUtilConstants.PARAMETER_VENDOR);
    	map.put(ChirdlUtilConstants.PARAMETER_VENDOR, vendorStr);
    	if (vendorStr == null || vendorStr.trim().length() == 0) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_MISSING_VENDOR, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return map;
		}
    	
    	Vendor vendor = VendorFactory.getVendor(vendorStr, request);
    	if (vendor == null) {
    		map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_INVALID_VENDOR, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return map;
    	}
    	
		String formName = vendor.getFormName(); //remove
		String formPage = vendor.getFormPage();
		String mrn = vendor.getMrn();
		map.put(ChirdlUtilConstants.PARAMETER_FORM_NAME, formName);
		map.put(ChirdlUtilConstants.PARAMETER_FORM_PAGE, formPage);
		map.put(ChirdlUtilConstants.PARAMETER_MRN, mrn);
		
		if (formName == null || formName.trim().length() == 0) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_MISSING_FORM, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return map;
		}
		
		if (formPage == null || formPage.trim().length() == 0) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_MISSING_FORM_PAGE, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return map;
		}
		
		if (mrn == null || mrn.trim().length() == 0) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_MISSING_MRN, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return map;
		}
		
		String username = vendor.getUsername();
		if (username == null || username.trim().length() == 0) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_MISSING_USER, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return map;
		}
		
		String password = vendor.getPassword();
		if (password == null || password.trim().length() == 0) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_MISSING_PASSWORD, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return map;
		}
		
		String providerId = vendor.getProviderId();
		if (providerId == null || providerId.trim().length() == 0) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_MISSING_PROVIDER_ID, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return map;
		}
		
		try {
			Context.authenticate(username, password);
		} catch (ContextAuthenticationException e) {
			// username/password not valid
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_FAILED_AUTHENTICATION, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
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
    	
    	String vendorStr = request.getParameter(ChirdlUtilConstants.PARAMETER_VENDOR);
    	map.put(ChirdlUtilConstants.PARAMETER_VENDOR, vendorStr);
    	if (vendorStr == null || vendorStr.trim().length() == 0) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_MISSING_VENDOR, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return new ModelAndView(view, map);
		}
    	
    	Vendor vendor = VendorFactory.getVendor(vendorStr, request);
    	if (vendor == null) {
    		map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_INVALID_VENDOR, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return new ModelAndView(view, map);
    	}
    	
    	//String formName = vendor.getFormName();
    	//String formPage = vendor.getFormPage();
    	String mrn = vendor.getMrn();
    	//String startStateStr = vendor.getStartState();
    	//String endStateStr = vendor.getEndState();
    	String providerId = vendor.getProviderId(); 
    	
    	//map.put(ChirdlUtilConstants.PARAMETER_FORM_NAME, formName);
		//map.put(ChirdlUtilConstants.PARAMETER_FORM_PAGE, formPage);
		map.put(ChirdlUtilConstants.PARAMETER_MRN, mrn);
		//map.put(ChirdlUtilConstants.PARAMETER_START_STATE, startStateStr);
		//map.put(ChirdlUtilConstants.PARAMETER_END_STATE, endStateStr);
		map.put(ChirdlUtilConstants.PARAMETER_PROVIDER_ID, providerId);
		map.put(ChirdlUtilConstants.PARAMETER_VENDOR, vendor);
		
		/*Form form = Context.getFormService().getForm(formName);
		if (form == null) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_INVALID_FORM, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return new ModelAndView(view, map);
		}*/
		
		Patient patient = getPatientByMRN(mrn);
		if (patient == null) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_INVALID_PATIENT, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return new ModelAndView(view, map);
		}
		
		/*if (startStateStr == null || startStateStr.trim().length() == 0) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_MISSING_START_STATE, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return new ModelAndView(view, map);
		}
		
		if (endStateStr == null || endStateStr.trim().length() == 0) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_MISSING_END_STATE, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return new ModelAndView(view, map);
		}
		
		ChirdlUtilBackportsService backportsService = Context.getService(ChirdlUtilBackportsService.class);
		State startState = backportsService.getStateByName(startStateStr);
		if (startState == null) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_INVALID_START_STATE, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return new ModelAndView(view, map);
		}
		
		State endState = backportsService.getStateByName(endStateStr);
		if (endState == null) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_INVALID_END_STATE, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return new ModelAndView(view, map);
		}*/
		
		if (providerId == null || providerId.trim().length() == 0) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_MISSING_PROVIDER_ID, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return new ModelAndView(view, map);
		}
		
//		PersonAttribute pat = backportsService.getPersonAttributeByValue(PERSON_ATTR_TYPE_PROVIDER_ID, providerId);
//		if (pat == null) {
//			map.put("hasErrors", "true");
//			map.put("invalidProviderId", "true");
//			return new ModelAndView(view, map);
//		}
		
		map.put(ChirdlUtilConstants.PARAMETER_PATIENT_ID, patient.getPatientId());
		
		/*Encounter encounter_R = getRecentEncounter(
			patient, backportsService, startState.getStateId(), endState.getStateId(), 438 );//form.getFormId());*/

		ChirdlUtilBackportsService backportsService = Context.getService(ChirdlUtilBackportsService.class);
		List<Encounter> encounterList = getEncounterList(patient); 

		org.openmrs.module.chica.hibernateBeans.Encounter encounter = null ;
		
		//String formName = null;
    	//String formPage = null;
		if (encounterList!=null && encounterList.size() == 1) { 
			encounter = (org.openmrs.module.chica.hibernateBeans.Encounter) encounterList.get(0); 

			getURLAttributes(encounter, vendor, map);
			
		} else if (encounterList!=null && encounterList.size() > 1) {
			
			encounter = (org.openmrs.module.chica.hibernateBeans.Encounter) getEncounterWithoutScannedTimeStamp(encounterList, backportsService, map, vendor);//, form.getFormId());  //do we need tot return

		} 
		
		if (encounterList == null || encounter == null ) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_MISSING_ENCOUNTER, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			
			addHandoutsInfo(backportsService, patient, encounter, mrn, map);
			return new ModelAndView(view, map);
		}
		
		Form form = Context.getFormService().getForm((String) map.get(ChirdlUtilConstants.PARAMETER_FORM_NAME)); //formName);
		if (form == null) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_INVALID_FORM, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return new ModelAndView(view, map);
		}
		
		State startState = backportsService.getStateByName((String) map.get(ChirdlUtilConstants.PARAMETER_START_STATE));
		if (startState == null) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_INVALID_START_STATE, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return new ModelAndView(view, map);
		}
		
		State endState = backportsService.getStateByName((String) map.get(ChirdlUtilConstants.PARAMETER_END_STATE));
		if (endState == null) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_INVALID_END_STATE, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return new ModelAndView(view, map);
		}
		
		 //String ggg = map.get(ChirdlUtilConstants.PARAMETER_FORM_PAGE));
 
		
		
		
    	
    	/***
    	 * end of new code
    	 */
 	
		map.put(ChirdlUtilConstants.PARAMETER_FORM_TIME_LIMIT, (Util.getFormTimeLimit())*24);
		
		FormInstanceTag tag = getFormInstanceInfo(encounter.getEncounterId(), form.getFormId(), startState.getStateId(), 
			endState.getStateId(), backportsService);
		if (tag == null) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_MISSING_FORM_INSTANCE, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			
			addHandoutsInfo(backportsService, patient, encounter, mrn, map);
			return new ModelAndView(view, map);
		}
		
		List<Session> sessions = backportsService.getSessionsByEncounter(encounter.getEncounterId());
		if (sessions != null && sessions.size() > 0) {
			map.put(ChirdlUtilConstants.PARAMETER_SESSION_ID, sessions.get(0).getSessionId());
		}
		
		map.put(ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID, encounter.getEncounterId());
		map.put(ChirdlUtilConstants.PARAMETER_FORM_INSTANCE, tag.toString());
    	
	    return new ModelAndView(new RedirectView((String) map.get(ChirdlUtilConstants.PARAMETER_FORM_PAGE)), map);
    }
    
    /**
     * Finds a patient in the system based on MRN.
     * 
     * @param mrn The patient's medical record number.
     * @return A Patient in the system with the provided MRN or null if one cannot be found.
     */
    private Patient getPatientByMRN(String mrn) {
    	PatientService patientService = Context.getPatientService();
		List<PatientIdentifierType> types = new ArrayList<PatientIdentifierType>();
	    types.add(patientService.getPatientIdentifierTypeByName(ChirdlUtilConstants.IDENTIFIER_TYPE_MRN));
	    mrn = org.openmrs.module.chirdlutil.util.Util.removeLeadingZeros(mrn);
	    List<Patient> patients = patientService.getPatientsByIdentifier(null, mrn, types, true); // CHICA-977 Use getPatientsByIdentifier() as a temporary solution to openmrs TRUNK-5089
	    if (patients.size() > 0) {
	    	Patient patient = patients.get(0);
	    	return patient;
	    }
	    
	    // Patient not found by MRN.  Try spotting a dash.
	    int dashIndex = mrn.indexOf(ChirdlUtilConstants.GENERAL_INFO_DASH);
	    if (dashIndex < 0) {
	    	// Place a dash in the next-to-last character position and ask again.
	    	int length = mrn.length();
	    	int position = length - 1;
	    	if (position >= 0) {
	    		String firstPart = mrn.substring(0, position);
	    		String lastPart = mrn.substring(position, length);
	    		String newMrn = firstPart + ChirdlUtilConstants.GENERAL_INFO_DASH + lastPart;
	    		patients = patientService.getPatientsByIdentifier(null, newMrn, types, true); // CHICA-977 Use getPatientsByIdentifier() as a temporary solution to openmrs TRUNK-5089
			    if (patients.size() > 0) {
			    	Patient patient = patients.get(0);
			    	return patient;
			    }
	    	}
	    }
	    
	    return null;
    }
    
    /**
     * Retrieves the most recent encounters as a List within the past two days
     * @param patient Patient object
     * @return Encounter List or null if one is not found
     */
    private List<org.openmrs.Encounter> getEncounterList(Patient patient) {

    	Calendar startCal = Calendar.getInstance();
		startCal.set(GregorianCalendar.DAY_OF_MONTH, startCal.get(GregorianCalendar.DAY_OF_MONTH) - Util.getFormTimeLimit());
		Date startDate = startCal.getTime();
		Date endDate = Calendar.getInstance().getTime();
		List<Encounter> encounters = Context.getEncounterService().getEncounters(patient, null, startDate, endDate, null, 
			null, null, false);
		if (encounters == null || encounters.size() == 0) {
			return null;
		} 
		return encounters;
    }
	
	/**
	 * Retrieves the  latest encounters with observations that contains the provided start state but 
     * not the end state for the provided form and patient.
	 * @param encounters encounters list of latest encounters
	 * @param backportsService ChirdlUtilBackportsService object
     * @param startStateId The start state identifier
     * @param endStateId The end state identifier
     * @param formId The form identifier
     * @return Encounter object or null if one is not found.
	 */
	private Encounter getEncounterWithoutScannedTimeStamp (List<org.openmrs.Encounter> encounters, ChirdlUtilBackportsService backportsService, Map<String, Object> map, Vendor vendor) { //, Integer formId) {
		for (int i = encounters.size() - 1; i >= 0; i--) {
			
			Encounter encounter = encounters.get(i);
			getURLAttributes((org.openmrs.module.chica.hibernateBeans.Encounter) encounter, vendor, map);
			if (map.get(ChirdlUtilConstants.PARAMETER_FORM_PAGE) == null || map.get(ChirdlUtilConstants.PARAMETER_START_STATE) == null || map.get(ChirdlUtilConstants.PARAMETER_END_STATE) == null) {
    			return encounter; 
    		}
			Map<Integer, List<PatientState>> formIdToPatientStateMapStart = new HashMap<Integer, List<PatientState>>();
	    	Map<Integer, List<PatientState>> formIdToPatientStateMapEnd = new HashMap<Integer, List<PatientState>>();
	    	Integer encounterId = encounter.getEncounterId();
	    	State startState = backportsService.getStateByName((String) map.get(ChirdlUtilConstants.PARAMETER_START_STATE));
	    	State endState = backportsService.getStateByName((String) map.get(ChirdlUtilConstants.PARAMETER_END_STATE));
	    	if (startState != null && endState != null) {
	    		Util.getPatientStatesByEncounterId(
	    	    		backportsService, formIdToPatientStateMapStart, encounterId, startState.getStateId(), true);
    	    	Util.getPatientStatesByEncounterId(
    	    		backportsService, formIdToPatientStateMapEnd, encounterId, endState.getStateId(), true);
	    	}
	    	Form form = Context.getFormService().getForm((String) map.get(ChirdlUtilConstants.PARAMETER_FORM_NAME));
	    	if (form != null) {
	    		boolean containsStartState = formIdToPatientStateMapStart.containsKey(form.getFormId());
				boolean containsEndState = formIdToPatientStateMapEnd.containsKey(form.getFormId());
				
				if (containsStartState && !containsEndState) {
					return encounter;
				}
	    	}
		}
		
		return null;
	}
    
    /**
     * Retrieves the last encounter for a patient or null if one does not exist.
     * 
     * @param patient The patient used to find the encounter.
     * @return Encounter object or null if one does not exist.
     */
    private Encounter getLastEncounter(Patient patient) {
    	List<Encounter> encounters = Context.getEncounterService().getEncountersByPatient(patient);
    	if (encounters == null || encounters.size() == 0) {
    		return null;
    	}
    	
    	return encounters.get(encounters.size() - 1);
    }
    
    /**
     * Gets the form instance information from the data provided
     * 
     * @param encounterId The encounter identifier
     * @param formId The form identifier
     * @param startStateId The start state identifier
     * @param endStateId The end state identifier
     * @param backportsService ChirdlUtilBackportsService object
     * @return FormInstanceTag object or null if form information cannot be found.
     */
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
    
    /**
     * Adds information to the map that will determine whether or not the user will be able to force print handouts on the JSP.
     * 
     * @param backportsService Service used to access data logic
     * @param patient The patient for the request
     * @param encounter The patient's encounter.  If null, an attempt will be made to find a valid encounter for the patient.
     * @param mrn The patient's medical record number.
     * @param map The HTTP map that will be returned to the client.
     */
    private void addHandoutsInfo(ChirdlUtilBackportsService backportsService, Patient patient, Encounter encounter, String mrn, 
                                 Map<String, Object> map) {
    	if (encounter == null) {
	    	// Check to see if the patient has at least one encounter to display the Handouts button on the page.
			encounter = getLastEncounter(patient); 
    	}
    	
		if (encounter != null) {
			Location location = encounter.getLocation();
			if (location != null) {
				map.put(ChirdlUtilConstants.PARAMETER_LOCATION_ID, location.getLocationId());
			} else {
				return;
			}
			
			Set<LocationTag> tags = location.getTags();
			if (tags != null && tags.size() > 0) {
				LocationTag tag = tags.iterator().next();
				map.put(ChirdlUtilConstants.PARAMETER_LOCATION_TAG_ID, tag.getLocationTagId());
			} else {
				return;
			}
			
			List<Session> sessions = backportsService.getSessionsByEncounter(encounter.getEncounterId());
			if (sessions != null && sessions.size() > 0) {
				map.put(ChirdlUtilConstants.PARAMETER_SESSION_ID, sessions.get(0).getSessionId());
			} else {
				return;
			}
			
			PersonName personName = patient.getPersonName();
			if (personName != null && personName.getGivenName() != null && personName.getFamilyName() != null) {
				map.put(ChirdlUtilConstants.PARAMETER_PATIENT_NAME, personName.getGivenName() + " " + personName.getFamilyName());
			} else {
				// Default to MRN if a name cannot be found.
				map.put(ChirdlUtilConstants.PARAMETER_PATIENT_NAME, mrn);
			}
			
			map.put(ChirdlUtilConstants.PARAMETER_SHOW_HANDOUTS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
		}
    }
    
    private void getURLAttributes(org.openmrs.module.chica.hibernateBeans.Encounter encounter, Vendor vendor, Map<String, Object> map) {

		String locationTagString = encounter.getPrinterLocation();
    	ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
    	if (locationTagString == null){
			
		}
    	LocationService locationService = Context.getLocationService();
    	LocationTag locationTag = locationService.getLocationTagByName(locationTagString);
    	Location location = encounter.getLocation();
    	//Integer locId = location.getLocationId();
    	LocationTagAttributeValue locationTagAttributeValueForm = null;
    	
     	if (locationTag != null && location != null) {
     		locationTagAttributeValueForm = chirdlutilbackportsService.getLocationTagAttributeValue(locationTag.getLocationTagId(), 
        			ChirdlUtilConstants.LOC_TAG_ATTR_PRIMARY_PHYSICIAN_FORM,location.getLocationId());
     	}

    	String formName = null;
    	String formPage = null;
    	String startStateStr = null;
    	String endStateStr = null;
    	Form form = null;
    	
    	if (locationTagAttributeValueForm != null && !locationTagAttributeValueForm.equals("") ) {
    		formName = locationTagAttributeValueForm.getValue(); 
    		
    		form = Context.getFormService().getForm(formName);
    		
    		FormAttributeValue formAttributeValueURL = chirdlutilbackportsService.getFormAttributeValue(form.getFormId(), ChirdlUtilConstants.FORM_ATTRIBUTE_URL, locationTag.getLocationTagId(), location.getLocationId());
    		FormAttributeValue formAttributeValueStartState = chirdlutilbackportsService.getFormAttributeValue(form.getFormId(), ChirdlUtilConstants.FORM_ATTRIBUTE_START_STATE, locationTag.getLocationTagId(), location.getLocationId());
    		FormAttributeValue formAttributeValueEndState = chirdlutilbackportsService.getFormAttributeValue(form.getFormId(), ChirdlUtilConstants.FORM_ATTRIBUTE_END_STATE, locationTag.getLocationTagId(), location.getLocationId());
    		
    		//Form form = formService.getForm(formInstance.getFormId());
    		if (formAttributeValueURL != null && !formAttributeValueURL.equals("") ) { //if formAttribute values are null
    			formPage = formAttributeValueURL.getValue();
    		} 
    		if (formAttributeValueStartState != null && !formAttributeValueStartState.equals("") ) {
    			startStateStr = formAttributeValueStartState.getValue();
    		}
    		if (formAttributeValueEndState != null && !formAttributeValueEndState.equals("") ) {
    			endStateStr = formAttributeValueEndState.getValue();
    		}
    		
    		
    		
    		
    		//formPage = locationTagAttributeValuePage.getValue();
    	} else {
    		
    		/*
    		formName = vendor.getFormName();
    		form = Context.getFormService().getForm(formName);
        	formPage = vendor.getFormPage();   
        	startStateStr = vendor.getStartState();
        	endStateStr = vendor.getEndState();*/
        	//Form form = Context.getFormService().getForm(formName);

    		/*if (form == null) {
    			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
    			map.put(ChirdlUtilConstants.PARAMETER_INVALID_FORM, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
    			return new ModelAndView(view, map);
    		}*/
    	}

    	
    	
    	map.put(ChirdlUtilConstants.PARAMETER_FORM_NAME, formName);
		map.put(ChirdlUtilConstants.PARAMETER_FORM_PAGE, formPage);
		map.put(ChirdlUtilConstants.PARAMETER_START_STATE, startStateStr);
		map.put(ChirdlUtilConstants.PARAMETER_END_STATE, endStateStr);

    }
 }
