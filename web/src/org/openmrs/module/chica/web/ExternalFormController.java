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
import org.openmrs.module.atd.hibernateBeans.Statistics;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstanceTag;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
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
		String formName = request.getParameter("formName");
		String formPage = request.getParameter("formPage");
		String mrn = request.getParameter("mrn");
		map.put("formName", formName);
		map.put("formPage", formPage);
		map.put("mrn", mrn);
		
		if (formName == null) {
			map.put("hasErrors", "true");
			map.put("missingForm", "true");
			return map;
		}
		
		if (formPage == null) {
			map.put("hasErrors", "true");
			map.put("missingFormPage", "true");
			return map;
		}
		
		if (mrn == null) {
			map.put("hasErrors", "true");
			map.put("missingMRN", "true");
			return map;
		}
		
		if (Context.getAuthenticatedUser() != null) {
			return map;
		}
		
		String username = request.getParameter("username");
		if (username == null) {
			map.put("hasErrors", "true");
			map.put("missingUser", "true");
			return map;
		}
		
		String password = request.getParameter("password");
		if (password == null) {
			map.put("hasErrors", "true");
			map.put("missingPassword", "true");
			return map;
		}
		
		try {
			Context.authenticate(username, password);
		} catch (ContextAuthenticationException e) {
			// username/password not valid
			map.put("hasErrors", "true");
			map.put("failedAuthentication", "true");
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
    	String formName = request.getParameter("formName");
    	String formPage = request.getParameter("formPage");
    	String mrn = request.getParameter("mrn");
    	String startStateStr = request.getParameter("startState");
    	String endStateStr = request.getParameter("endState");
    	map.put("formName", formName);
		map.put("formPage", formPage);
		map.put("mrn", mrn);
		map.put("startState", startStateStr);
		map.put("endState", endStateStr);
		
		Form form = Context.getFormService().getForm(formName);
		if (form == null) {
			map.put("hasErrors", "true");
			map.put("invalidForm", "true");
			return new ModelAndView(view, map);
		}
		
		Patient patient = getPatientByMRN(mrn);
		if (patient == null) {
			map.put("hasErrors", "true");
			map.put("invalidPatient", "true");
			return new ModelAndView(view, map);
		}
		
		if (startStateStr == null) {
			map.put("hasErrors", "true");
			map.put("missingStartState", "true");
			return new ModelAndView(view, map);
		}
		
		if (endStateStr == null) {
			map.put("hasErrors", "true");
			map.put("missingEndState", "true");
			return new ModelAndView(view, map);
		}
		
		ChirdlUtilBackportsService backportsService = Context.getService(ChirdlUtilBackportsService.class);
		State startState = backportsService.getStateByName(startStateStr);
		if (startState == null) {
			map.put("hasErrors", "true");
			map.put("invalidStartState", "true");
			return new ModelAndView(view, map);
		}
		
		State endState = backportsService.getStateByName(endStateStr);
		if (endState == null) {
			map.put("hasErrors", "true");
			map.put("invalidEndState", "true");
			return new ModelAndView(view, map);
		}
		
		Encounter encounter = getRecentEncounter(patient);
		if (encounter == null) {
			map.put("hasErrors", "true");
			map.put("missingEncounter", "true");
			return new ModelAndView(view, map);
		}
		
		FormInstanceTag tag = getFormInstanceInfo(encounter.getEncounterId(), form.getFormId(), startState.getStateId(), 
			endState.getStateId(), backportsService);
		if (tag == null) {
			map.put("hasErrors", "true");
			map.put("missingFormInstance", "true");
			return new ModelAndView(view, map);
		}
		
		map.put("encounterId", encounter.getEncounterId());
		map.put("patientId", patient.getPatientId());
		map.put("formInstance", tag.toString());
    	
	    return new ModelAndView(new RedirectView(formPage), map);
    }
    
    private Patient getPatientByMRN(String mrn) {
    	PatientService patientService = Context.getPatientService();
		List<PatientIdentifierType> types = new ArrayList<PatientIdentifierType>();
	    types.add(patientService.getPatientIdentifierTypeByName("MRN_OTHER"));
	    List<Patient> patients = patientService.getPatients(null, mrn, types, true);
	    if (patients.size() > 0) {
	    	Patient patient = patients.get(0);
	    	return patient;
	    }
	    
	    // Patient not found by MRN.  Try spotting a dash.
	    int dashIndex = mrn.indexOf("-");
	    if (dashIndex < 0) {
	    	// Place a dash in the next-to-last character position and ask again.
	    	int length = mrn.length();
	    	int position = length - 1;
	    	if (position >= 0) {
	    		String firstPart = mrn.substring(0, position);
	    		String lastPart = mrn.substring(position, length);
	    		String newMrn = firstPart + "-" + lastPart;
	    		patients = patientService.getPatients(null, newMrn, types, true);
			    if (patients.size() > 0) {
			    	Patient patient = patients.get(0);
			    	return patient;
			    }
	    	}
	    }
	    
	    return null;
    }
    
    private Encounter getRecentEncounter(Patient patient) {
    	// Get last encounter with last day
		Calendar startCal = Calendar.getInstance();
		startCal.set(GregorianCalendar.DAY_OF_MONTH, startCal.get(GregorianCalendar.DAY_OF_MONTH) - 3);
		Date startDate = startCal.getTime();
		Date endDate = Calendar.getInstance().getTime();
		List<Encounter> encounters = Context.getEncounterService().getEncounters(patient, null, startDate, endDate, null, 
			null, null, false);
		if (encounters == null || encounters.size() == 0) {
			return null;
		} else if (encounters.size() == 1) {
			return encounters.get(0);
		}
		
		// Do a check to find the latest encounters with observations with a scanned timestamp for the PSF.
		ATDService atdService = Context.getService(ATDService.class);
		for (int i = encounters.size() - 1; i >= 0; i--) {
			Encounter encounter = encounters.get(i);
			List<Statistics> stats = atdService.getStatsByEncounterForm(encounter.getEncounterId(), "PSF");
			if (stats == null || stats.size() == 0) {
				continue;
			}
			
			for (Statistics stat : stats) {
				if (stat.getScannedTimestamp() != null) {
					return encounter;
				}
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
