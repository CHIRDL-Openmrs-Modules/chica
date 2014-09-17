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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Hibernate;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
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
		if (Context.getAuthenticatedUser() != null) {
			return map;
		}
		
		String username = request.getParameter("username");
		if (username == null) {
			map.put("invalidUser", "true");
			return map;
		}
		
		String password = request.getParameter("password");
		if (password == null) {
			map.put("invalidPassword", "true");
			return map;
		}
		
		try {
			Context.authenticate(username, password);
		} catch (ContextAuthenticationException e) {
			// username/password not valid
			map.put("invalidPassword", "true");
			return map;
		}
		
		String formName = request.getParameter("formName");
		if (formName == null) {
			map.put("missingForm", "true");
			return map;
		}
		
		Form form = Context.getFormService().getForm(formName);
		if (form == null) {
			map.put("invalidForm", "true");
			return map;
		}
		
		String formPage = request.getParameter("formPage");
		if (formPage == null) {
			map.put("missingFormPage", "true");
			return map;
		}
		
		String mrn = request.getParameter("mrn");
		if (mrn == null) {
			map.put("missingMRN", "true");
			return map;
		}
		
		Patient patient = getPatientByMRN(mrn);
		if (patient == null) {
			map.put("invalidPatient", "true");
		}
		
		map.put("formName", formName);
		map.put("formPage", formPage);
		
		return map;
	}

	/**
     * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
     */
    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command,
                                    BindException errors) throws Exception {
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
}
