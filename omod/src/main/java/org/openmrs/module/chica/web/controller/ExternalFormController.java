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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.module.chica.util.ChicaConstants;
import org.openmrs.module.chica.util.Util;
import org.openmrs.module.chica.vendor.Vendor;
import org.openmrs.module.chica.vendor.VendorFactory;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstanceTag;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Session;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
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
			map.put(ChirdlUtilConstants.PARAMETER_MISSING_PASSPHRASE, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
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
		
		Patient patient = org.openmrs.module.chirdlutil.util.Util.getPatientByMRNOther(mrn);
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
		List<Encounter> encounterList = Util.getEncounterList(patient); 
		Encounter encounter = null ;
		if (encounterList!=null && encounterList.size() == 1) { 
			// Look up the encounter through the CHICA encounter service to prevent class cast exceptions.
			EncounterService encounterService = Context.getEncounterService();
			Integer encounterId = encounterList.get(0).getEncounterId();
			encounter = encounterService.getEncounter(encounterId);
			ControllerUtil.setPhysicianFormURLAttributes(encounter, map);
		} else if (encounterList!=null && encounterList.size() > 1) {
			encounter = ControllerUtil.getPhysicianEncounterWithoutScannedTimeStamp(encounterList, backportsService, map);
		} 
		
		if (encounterList == null || encounter == null ) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_MISSING_ENCOUNTER, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_FORM_TIME_LIMIT, (Util.getFormTimeLimit())*24);
			ControllerUtil.addHandoutsInfo(backportsService, patient, encounter, mrn, map);
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
 	
		
		FormInstanceTag tag = Util.getFormInstanceInfo(encounter.getEncounterId(), form.getFormId(), 
				startState.getStateId(), endState.getStateId(), backportsService);
		if (tag == null) {
			map.put(ChirdlUtilConstants.PARAMETER_HAS_ERRORS, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			map.put(ChirdlUtilConstants.PARAMETER_MISSING_FORM_INSTANCE, ChirdlUtilConstants.PARAMETER_VAL_TRUE);
			
			ControllerUtil.addHandoutsInfo(backportsService, patient, encounter, mrn, map);
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
 }
