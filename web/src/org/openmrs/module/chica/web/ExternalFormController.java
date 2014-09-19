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
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;


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
		map.put("formName", request.getParameter("formName"));
		map.put("formPage", request.getParameter("formPage"));
		map.put("mrn", request.getParameter("mrn"));
		if (Context.getAuthenticatedUser() != null) {
			return map;
		}
		
		String username = request.getParameter("username");
		if (username == null) {
			map.put("missingUser", "true");
			return map;
		}
		
		String password = request.getParameter("password");
		if (password == null) {
			map.put("missingPassword", "true");
			return map;
		}
		
		try {
			Context.authenticate(username, password);
		} catch (ContextAuthenticationException e) {
			// username/password not valid
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
    	map.put("formName", formName);
		map.put("formPage", formPage);
		map.put("mrn", mrn);
		
		if (formName == null) {
			map.put("missingForm", "true");
			return new ModelAndView(view, map);
		}
		
		Form form = Context.getFormService().getForm(formName);
		if (form == null) {
			map.put("invalidForm", "true");
			return new ModelAndView(view, map);
		}
		
		if (formPage == null) {
			map.put("missingFormPage", "true");
			return new ModelAndView(view, map);
		}
		
		if (mrn == null) {
			map.put("missingMRN", "true");
			return new ModelAndView(view, map);
		}
		
		Patient patient = getPatientByMRN(mrn);
		if (patient == null) {
			map.put("invalidPatient", "true");
			return new ModelAndView(view, map);
		}
		
		Encounter encounter = getRecentEncounter(patient);
		if (encounter == null) {
			map.put("missingEncounter", "true");
			return new ModelAndView(view, map);
		}
		
		
    	
	    return super.onSubmit(request, response, command, errors);
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
}
