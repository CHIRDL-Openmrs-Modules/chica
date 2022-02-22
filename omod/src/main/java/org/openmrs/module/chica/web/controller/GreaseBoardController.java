package org.openmrs.module.chica.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmrs.Concept;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.ConceptService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chica.util.ChicaConstants;
import org.openmrs.module.chica.util.Util;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.BaseStateActionHandler;
import org.openmrs.module.chirdlutilbackports.StateManager;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Session;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;


@Controller
@RequestMapping(value = "module/chica/greaseBoard.form")
public class GreaseBoardController {
	/** Logger for this class and subclasses */
	private static final Logger log = LoggerFactory.getLogger(GreaseBoardController.class);
	private static final String FORM = "greaseBoard.form";
	
	private static int numRefreshes = 0;

	@RequestMapping(method = RequestMethod.POST)
	protected ModelAndView processSubmit(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
	
		String optionsString = request.getParameter("options");
		
		String patientIdString = request.getParameter("greaseBoardPatientId");
		Integer patientId = null;
		try
		{
			if(patientIdString != null){
				patientId = Integer.parseInt(patientIdString);
			}
		} catch (NumberFormatException e) 
		{
			log.error("Error parsing patientId: " + patientIdString, e);
		}
		String sessionIdString = request.getParameter("greaseBoardSessionId");
		Integer sessionId = null;
		try
		{
			if(sessionIdString != null){
				sessionId = Integer.parseInt(sessionIdString);
			}
		} catch (NumberFormatException e) 
		{
			log.error("Error parsing sessionId: " + sessionIdString, e);
		}
		
		//Initiate an ADHD WU for the patient
		if (optionsString != null && optionsString.equalsIgnoreCase("ADHD WU")) {
			PatientService patientService = Context.getPatientService();
			Patient patient = patientService.getPatient(patientId);
			ConceptService conceptService = Context.getConceptService();
			Concept currConcept = conceptService.getConceptByName("CHICA_ADHD_SX");
			Session session = chirdlutilbackportsService.getSession(sessionId);
			Integer encounterId = session.getEncounterId();
			org.openmrs.module.chirdlutil.util.Util.saveObs(patient, currConcept, encounterId, 
				"workup_initiated",new Date());
			
			LogicService logicService = Context.getLogicService();
			
			//print the ADHD parent form
			User user = Context.getUserContext().getAuthenticatedUser();
			String locationString = user.getUserProperty("location");
			String locationTags = user.getUserProperty("locationTags");
			LocationService locationService = Context.getLocationService();
			
			Integer locationId = null;
			Location location = null;
			Integer locationTagId = null;
			if (locationString != null) {
				location = locationService.getLocation(locationString);
				if (location != null) {
					locationId = location.getLocationId();
					
					if (locationTags != null) {
						StringTokenizer tokenizer = new StringTokenizer(locationTags, ",");
						while (tokenizer.hasMoreTokens()) {
							String locationTagName = tokenizer.nextToken();
							locationTagName = locationTagName.trim();
							Set<LocationTag> tags = location.getTags();
							for (LocationTag tag : tags) {
								if (tag.getName().equalsIgnoreCase(locationTagName)) {
									locationTagId = tag.getLocationTagId();
								}
							}
						}
					}
					
				}
			}
			
			//print the ADHD parent form
			Map<String, Object> parameters = new HashMap<String,Object>();
			parameters.put("sessionId",sessionId);
			parameters.put("locationTagId",locationTagId); 
			FormInstance formInstance = new FormInstance();
			formInstance.setLocationId(locationId);
			parameters.put("formInstance",formInstance);
			parameters.put("param1","ADHD P");
			logicService.eval(patientId, "CREATE_JIT",parameters);
			
			//print the ADHD Spanish parent form
			parameters = new HashMap<String,Object>();
			parameters.put("sessionId",sessionId);
			parameters.put("locationTagId",locationTagId); 
			formInstance = new FormInstance();
			formInstance.setLocationId(locationId);
			parameters.put("formInstance",formInstance);
			parameters.put("param1","ADHD PS");
			logicService.eval(patientId, "CREATE_JIT",parameters);
			
			//print the ADHD teacher form
			parameters = new HashMap<String,Object>();
			parameters.put("sessionId",sessionId);
			parameters.put("locationTagId",locationTagId); 
			formInstance = new FormInstance();
			formInstance.setLocationId(locationId);
			parameters.put("formInstance",formInstance);
			parameters.put("param1","ADHD T");
			logicService.eval(patientId, "CREATE_JIT",parameters);
		}

		if (optionsString != null && (optionsString.equalsIgnoreCase(ChirdlUtilConstants.OPTION_PRINT_PATIENT_FORM)||
				optionsString.equalsIgnoreCase(ChirdlUtilConstants.OPTION_PRINT_PHYSICIAN_FORM)))
		{
			if (patientId != null && sessionId != null) 
			{
				FormService formService = Context.getFormService();
				Session session = chirdlutilbackportsService.getSession(sessionId);
				Integer encounterId = session.getEncounterId();
				EncounterService encounterService = Context.getService(EncounterService.class);
				Encounter encounter = (Encounter) encounterService.getEncounter(encounterId);
				String formName = Util.getFormNameByPrintOptionString(encounter, optionsString); 

				if (StringUtils.isNotBlank(formName)) {
					Form form = formService.getForm(formName);
					Integer formId = null;
					if (form != null) {
						formId = form.getFormId();
					} else {
						log.error("The locationTagAttributeValue "+formName+" is invalid");
						return new ModelAndView(new RedirectView(FORM));
					}
					
					PatientState patientStateProduce = 
						org.openmrs.module.atd.util.Util.getProducePatientStateByEncounterFormAction(encounterId, formId);

					String stateName = Util.getReprintStateName(encounter, formId);
					if (StringUtils.isBlank(stateName)) {
						log.error("A valid reprint State parameter was not provided to the CHICA system.");
						return new ModelAndView(new RedirectView(FORM));
					}

					State currState = null;
					if (optionsString.equalsIgnoreCase(ChirdlUtilConstants.OPTION_PRINT_PATIENT_FORM) || (optionsString.equalsIgnoreCase(ChirdlUtilConstants.OPTION_PRINT_PHYSICIAN_FORM) && patientStateProduce != null /*reprint if the state exists*/) ) {
						currState = chirdlutilbackportsService
								.getStateByName(stateName);
						if (currState == null) {
							log.error("A start state with name "+stateName+" cannot be found in the CHICA system.");
							return new ModelAndView(new RedirectView(FORM));
						}
						
					} else {
						// create for the first time if it does not exist
						currState = chirdlutilbackportsService.getStateByName(ChirdlUtilConstants.STATE_GREASE_BOARD_PRINT_PWS);
					}
					
					HashMap<String,Object> actionParameters = new HashMap<String,Object>();
					actionParameters.put("formName", formName);
					
					if(optionsString.equalsIgnoreCase(ChirdlUtilConstants.OPTION_PRINT_PHYSICIAN_FORM)) // DWE CHICA-821 Allow PWS to auto-print when "reprinting"
					{
						actionParameters.put(ChirdlUtilConstants.PARAMETER_FORCE_AUTO_PRINT, ChirdlUtilConstants.GENERAL_INFO_TRUE);
					}

					if (currState != null)
					{
						PatientState patientState = chirdlutilbackportsService.getLastPatientState(sessionId);
						PatientService patientService = Context.getPatientService();
						Patient patient = patientService.getPatient(patientId);

						StateManager.runState(patient, sessionId, currState,actionParameters,
								patientState.getLocationTagId(),
								patientState.getLocationId(),
								BaseStateActionHandler.getInstance());
					}
				}
			}
			
			return new ModelAndView(new RedirectView(FORM));
		}
		
		return new ModelAndView(new RedirectView(FORM));
		
	}

	@RequestMapping(method = RequestMethod.GET)
	protected String initForm(HttpServletRequest request, ModelMap map) throws Exception {
		User user = Context.getUserContext().getAuthenticatedUser();
		if(user == null) {
			return null;
		}

		map.put("refreshPeriod", Context.getAdministrationService().getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_GREASEBOARD_REFRESH));
		map.put("showManualCheckin", Context.getAdministrationService().getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_ENABLE_MANUAL_CHECKIN));
		map.put("currentUser", user.getUsername());
		
		numRefreshes++;
		if(numRefreshes%100==0){
			Context.clearSession();
		}
		
		return ChicaConstants.FORM_VIEW_GREASE_BOARD;
	}
}
