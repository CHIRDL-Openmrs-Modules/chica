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
package org.openmrs.module.chica.web.controller;

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

import org.apache.commons.lang3.StringUtils;
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
import org.openmrs.module.chica.util.ChicaConstants;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;


/**
 * Controller used to authenticate an external user before allowing view of a form.
 *
 * @author Steve McKee
 */
@Controller
@RequestMapping(value = "module/chica/externalFormLoader.form")
public class ExternalFormController {

	@RequestMapping(method = RequestMethod.GET)
    protected String initForm(HttpServletRequest request, ModelMap map) throws Exception {
		
		String vendorStr = request.getParameter(ChirdlUtilConstants.PARAMETER_VENDOR);
    	map.put(ChirdlUtilConstants.PARAMETER_VENDOR, vendorStr);
    	if (StringUtils.isBlank(vendorStr)) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_MISSING_VENDOR, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return ChicaConstants.FORM_VIEW_EXTERNAL_FORM_LOADER;
		}
    	
    	Vendor vendor = VendorFactory.getVendor(vendorStr, request);
    	if (vendor == null) {
    		map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_INVALID_VENDOR, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return ChicaConstants.FORM_VIEW_EXTERNAL_FORM_LOADER;
    	}
    	
		String mrn = vendor.getMrn();
		map.put(ChirdlUtilConstants.PARAMETER_MRN, mrn);
		if (StringUtils.isBlank(mrn)) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_MISSING_MRN, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return ChicaConstants.FORM_VIEW_EXTERNAL_FORM_LOADER;
		}
		
		String username = vendor.getUsername();
		if (StringUtils.isBlank(username)) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_MISSING_USER, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return ChicaConstants.FORM_VIEW_EXTERNAL_FORM_LOADER;
		}
		
		String password = vendor.getPassword();
		if (StringUtils.isBlank(password)) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_MISSING_PASSWORD, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return ChicaConstants.FORM_VIEW_EXTERNAL_FORM_LOADER;
		}
		
		String providerId = vendor.getProviderId();
		if (StringUtils.isBlank(providerId)) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_MISSING_PROVIDER_ID, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return ChicaConstants.FORM_VIEW_EXTERNAL_FORM_LOADER;
		}
		
		try {
			Context.authenticate(username, password);
		} catch (ContextAuthenticationException e) {
			// username/password not valid
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_FAILED_AUTHENTICATION, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return ChicaConstants.FORM_VIEW_EXTERNAL_FORM_LOADER;
		}

		return ChicaConstants.FORM_VIEW_EXTERNAL_FORM_LOADER;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	protected ModelAndView processSubmit(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
		
    	Map<String, Object> map = new HashMap<String, Object>();
    	
    	String vendorStr = request.getParameter(ChirdlUtilConstants.PARAMETER_VENDOR);
    	map.put(ChirdlUtilConstants.PARAMETER_VENDOR, vendorStr);
    	if (StringUtils.isBlank(vendorStr)) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_MISSING_VENDOR, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return new ModelAndView(ChicaConstants.FORM_VIEW_EXTERNAL_FORM_LOADER, map);
		}
    	
    	Vendor vendor = VendorFactory.getVendor(vendorStr, request);
    	if (vendor == null) {
    		map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_INVALID_VENDOR, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return new ModelAndView(ChicaConstants.FORM_VIEW_EXTERNAL_FORM_LOADER, map);
    	}

    	String mrn = vendor.getMrn();
    	String providerId = vendor.getProviderId(); 

		map.put(ChirdlUtilConstants.PARAMETER_MRN, mrn);
		map.put(ChirdlUtilConstants.PARAMETER_PROVIDER_ID, providerId);
		map.put(ChirdlUtilConstants.PARAMETER_VENDOR, vendor);
		
		Patient patient = getPatientByMRN(mrn);
		if (patient == null) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_INVALID_PATIENT, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return new ModelAndView(ChicaConstants.FORM_VIEW_EXTERNAL_FORM_LOADER, map);
		}
		
		if (StringUtils.isBlank(providerId)) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_MISSING_PROVIDER_ID, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return new ModelAndView(ChicaConstants.FORM_VIEW_EXTERNAL_FORM_LOADER, map);
		}
		
//		PersonAttribute pat = backportsService.getPersonAttributeByValue(PERSON_ATTR_TYPE_PROVIDER_ID, providerId);
//		if (pat == null) {
//			map.put("hasErrors", "true");
//			map.put("invalidProviderId", "true");
//			return new ModelAndView(view, map);
//		}
		
		map.put(ChirdlUtilConstants.PARAMETER_PATIENT_ID, patient.getPatientId());

		ChirdlUtilBackportsService backportsService = Context.getService(ChirdlUtilBackportsService.class);
		List<Encounter> encounterList = getEncounterList(patient); 
		org.openmrs.module.chica.hibernateBeans.Encounter encounter = null ;

		org.openmrs.module.chica.service.EncounterService encounterService = Context.getService(org.openmrs.module.chica.service.EncounterService.class);
		if (encounterList!=null && encounterList.size() == 1) { 
			Integer encounterId = encounterList.get(0).getEncounterId();
			encounter = (org.openmrs.module.chica.hibernateBeans.Encounter)encounterService.getEncounter(encounterId);
			setURLAttributes(vendor, encounter, map);
		} else if (encounterList!=null && encounterList.size() > 1) {
			encounter = getEncounterWithoutScannedTimeStamp(encounterList, backportsService, map, vendor);
		} 
		
		if (encounterList == null || encounter == null ) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_MISSING_ENCOUNTER, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			
			addHandoutsInfo(backportsService, patient, encounter, mrn, map);
			return new ModelAndView(ChicaConstants.FORM_VIEW_EXTERNAL_FORM_LOADER, map);
		}
		
		String formName = (String)map.get(ChirdlUtilConstants.PARAMETER_FORM_NAME);
		Form form = Context.getFormService().getForm(formName); 
		if (form == null) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_INVALID_FORM, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return new ModelAndView(ChicaConstants.FORM_VIEW_EXTERNAL_FORM_LOADER, map);
		}
		
		String startStateStr = (String)map.get(ChirdlUtilConstants.PARAMETER_START_STATE);
		if (StringUtils.isBlank(startStateStr)) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_MISSING_START_STATE, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return new ModelAndView(ChicaConstants.FORM_VIEW_EXTERNAL_FORM_LOADER, map);
		}
		
		String endStateStr = (String)map.get(ChirdlUtilConstants.PARAMETER_END_STATE);
		if (StringUtils.isBlank(endStateStr)) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_MISSING_END_STATE, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return new ModelAndView(ChicaConstants.FORM_VIEW_EXTERNAL_FORM_LOADER, map);
		}
		
		State startState = backportsService.getStateByName(startStateStr);
		if (startState == null) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_INVALID_START_STATE, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return new ModelAndView(ChicaConstants.FORM_VIEW_EXTERNAL_FORM_LOADER, map);
		}
		
		State endState = backportsService.getStateByName(endStateStr);
		if (endState == null) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_INVALID_END_STATE, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return new ModelAndView(ChicaConstants.FORM_VIEW_EXTERNAL_FORM_LOADER, map);
		}
		
		String formPage = (String)map.get(ChirdlUtilConstants.PARAMETER_FORM_PAGE);
		if (StringUtils.isBlank(formPage)) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_MISSING_FORM_PAGE, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			return new ModelAndView(ChicaConstants.FORM_VIEW_EXTERNAL_FORM_LOADER, map);
		}
 	
		map.put(ChirdlUtilConstants.PARAMETER_FORM_TIME_LIMIT, (Util.getFormTimeLimit())*24);
		
		FormInstanceTag tag = getFormInstanceInfo(encounter.getEncounterId(), form.getFormId(), startState.getStateId(), 
			endState.getStateId(), backportsService);
		if (tag == null) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_MISSING_FORM_INSTANCE, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			
			addHandoutsInfo(backportsService, patient, encounter, mrn, map);
			return new ModelAndView(ChicaConstants.FORM_VIEW_EXTERNAL_FORM_LOADER, map);
		}
		
		List<Session> sessions = backportsService.getSessionsByEncounter(encounter.getEncounterId());
		if (sessions != null && sessions.size() > 0) {
			map.put(ChirdlUtilConstants.PARAMETER_SESSION_ID, sessions.get(0).getSessionId());
		}
		
		map.put(ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID, encounter.getEncounterId());
		map.put(ChirdlUtilConstants.PARAMETER_FORM_INSTANCE, tag.toString());
    	
	    return new ModelAndView(new RedirectView(formPage), map);
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
			return Collections.emptyList(); 
		} 
		return encounters;
    }
	
	/**
	 * Retrieves the  latest encounters with observations that contains the provided start state but 
     * not the end state for the provided form and patient.
	 * @param encounters encounters list of latest encounters
	 * @param backportsService ChirdlUtilBackportsService object
     * @param map Map that will be returned to the client
     * @param vendor Vendor Object
     * @return Encounter object or null if one is not found.
	 */
	private org.openmrs.module.chica.hibernateBeans.Encounter getEncounterWithoutScannedTimeStamp (List<org.openmrs.Encounter> encounters, 
	                                                                                               ChirdlUtilBackportsService backportsService,
	                                                                                               Map<String, Object> map, Vendor vendor) {
		org.openmrs.module.chica.service.EncounterService encounterService = Context.getService(org.openmrs.module.chica.service.EncounterService.class);
		for (int i = encounters.size() - 1; i >= 0; i--) {
			Integer encounterId = encounters.get(i).getEncounterId();
			org.openmrs.module.chica.hibernateBeans.Encounter chicaEncounter = (org.openmrs.module.chica.hibernateBeans.Encounter)encounterService.getEncounter(encounterId);
			setURLAttributes(vendor, chicaEncounter, map);
			String formName = (String)map.get(ChirdlUtilConstants.PARAMETER_FORM_NAME);
			String startStateStr = (String)map.get(ChirdlUtilConstants.PARAMETER_START_STATE);
			String endStateStr = (String)map.get(ChirdlUtilConstants.PARAMETER_END_STATE);
			String formPage = (String)map.get(ChirdlUtilConstants.PARAMETER_FORM_PAGE);
			
			if (StringUtils.isBlank(formName) || StringUtils.isBlank(formPage) || 
					StringUtils.isBlank(startStateStr) || StringUtils.isBlank(endStateStr)) {
    			return chicaEncounter; 
    		}
			
			Map<Integer, List<PatientState>> formIdToPatientStateMapStart = new HashMap<Integer, List<PatientState>>();
	    	Map<Integer, List<PatientState>> formIdToPatientStateMapEnd = new HashMap<Integer, List<PatientState>>();
	    	State startState = backportsService.getStateByName(startStateStr);
	    	State endState = backportsService.getStateByName(endStateStr);
	    	if (startState != null && endState != null) {
	    		Util.getPatientStatesByEncounterId(
	    	    		backportsService, formIdToPatientStateMapStart, encounterId, startState.getStateId(), true);
    	    	Util.getPatientStatesByEncounterId(
    	    		backportsService, formIdToPatientStateMapEnd, encounterId, endState.getStateId(), true);
	    	}
	    	
	    	Form form = Context.getFormService().getForm(formName);
	    	if (form != null) {
	    		boolean containsStartState = formIdToPatientStateMapStart.containsKey(form.getFormId());
				boolean containsEndState = formIdToPatientStateMapEnd.containsKey(form.getFormId());
				
				if (containsStartState && !containsEndState) {
					return chicaEncounter;
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
    
    /**
     * Set URL attributes to Map
     * @param vendor Vendor Object
     * @param encounter Patient encounter object.
     * @param map Map that will be returned to the client.
     */
    private void setURLAttributes(Vendor vendor, org.openmrs.module.chica.hibernateBeans.Encounter encounter, Map<String, Object> map) {
    	Location location = encounter.getLocation();
    	if (location == null) {
    		return;
    	}
    	
    	Integer locationId = location.getLocationId();
    	String locationTagString = encounter.getPrinterLocation();
		LocationTag locationTag = null;
    	if (StringUtils.isNotBlank(locationTagString)) { 
    		LocationService locationService = Context.getLocationService();
    		locationTag = locationService.getLocationTagByName(locationTagString);
		}
    	
    	if (locationTag == null) {
    		return;
    	}
    	
    	Integer locationTagId = locationTag.getLocationTagId();
    	String formName = vendor.getFormName(locationId, locationTagId);
    	map.put(ChirdlUtilConstants.PARAMETER_FORM_NAME, formName);
		map.put(ChirdlUtilConstants.PARAMETER_FORM_PAGE, vendor.getFormPage(locationId, locationTagId, formName));
		map.put(ChirdlUtilConstants.PARAMETER_START_STATE, vendor.getStartState(locationId, locationTagId, formName));
		map.put(ChirdlUtilConstants.PARAMETER_END_STATE, vendor.getEndState(locationId, locationTagId, formName));
    }
 }
