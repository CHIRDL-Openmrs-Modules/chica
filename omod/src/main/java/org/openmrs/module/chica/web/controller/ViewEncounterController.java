package org.openmrs.module.chica.web.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.APIException;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.util.ChicaConstants;
import org.openmrs.module.chica.util.PatientRow;
import org.openmrs.module.chica.xmlBeans.viewEncountersConfig.FormsToDisplay;
import org.openmrs.module.chica.xmlBeans.viewEncountersConfig.ViewEncounterForm;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.DateUtil;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.EncounterAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping(value = "module/chica/viewEncounter.form") 
public class ViewEncounterController {

	/** Logger for this class and subclasses */
	private static final Logger log = LoggerFactory.getLogger(ViewEncounterController.class);
	
	private static final String FORM_VIEW = "/module/chica/viewEncounter";
	private static final String FORM_VIEW_NAME = "viewEncounter.form";
	private static final String REDIRECT_FORM_VIEW = "displayViewEncounterForm.form";
	private static final String DISPLAY_POSITION_LEFT = "left";
	private static final String DISPLAY_POSITION_RIGHT = "right";
	private static final String TODAY = "Today";
	
	private static final String PARAMATER_TITLE_MRN = "titleMRN";
	private static final String PARAMATER_TITLE_LAST_NAME = "titleLastName";
	private static final String PARAMATER_TITLE_FIRST_NAME = "titleFirstName";
	private static final String PARAMATER_TITLE_DOB = "titleDOB";
	private static final String PARAMATER_TITLE_PATIENT_ROWS = "patientRows";
	private static final String PARAMATER_TITLE_FORM_NAME_MAP = "formNameMap";
	private static final String PARAMATER_OPTIONS = "options";
	private static final String PARAMATER_VIEW_ENCOUNTERS_ERROR_MSG = "viewEncountersErrorMsg";
	
	@RequestMapping(method = RequestMethod.POST)
	protected ModelAndView processSubmit(HttpServletRequest request, HttpServletResponse response, Object command) throws Exception {

		Map<String, Object> map = new HashMap<>();
		String optionsString = request.getParameter(PARAMATER_OPTIONS);
		String patientIdString = request.getParameter(ChirdlUtilConstants.PARAMETER_PATIENT_ID);
		Integer patientId = null;
		String errorMsg = "A server error occurred. No forms will be displayed.";
		
		try 
		{
			patientId = Integer.parseInt(patientIdString);
		} 
		catch (Exception e) 
		{
			log.error("Error displaying form in View Encounters. Unable to parse patientId (patientIdString: {}", patientIdString, e);
			map.put(PARAMATER_VIEW_ENCOUNTERS_ERROR_MSG, errorMsg);
			return new ModelAndView(new RedirectView(FORM_VIEW_NAME), map);
		}

		String encounterIdString = request.getParameter(ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID);
		Integer encounterId = null;
		try 
		{
			encounterId = Integer.parseInt(encounterIdString);
		} 
		catch (Exception e) 
		{
			log.error("Error displaying form in View Encounters. Unable to parse encounterId (encounterIdString: {}).",encounterIdString, e);
			map.put(PARAMATER_VIEW_ENCOUNTERS_ERROR_MSG, errorMsg);
			return new ModelAndView(new RedirectView(FORM_VIEW_NAME), map);
		}
		
		if(optionsString == null)
		{
			log.error("Error displaying form in View Encounters for encounterId: {} (optionsString: {})",encounterId,optionsString);
			map.put(PARAMATER_VIEW_ENCOUNTERS_ERROR_MSG, errorMsg);
			return new ModelAndView(new RedirectView(FORM_VIEW_NAME), map);
		}

		map.put(ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID, encounterId);
		map.put(ChirdlUtilConstants.PARAMETER_PATIENT_ID, patientId);
		StringTokenizer tokenizer = new StringTokenizer(optionsString,"_");
		Integer locationId = null;
		Integer formId = null;
		Integer formInstanceId = null;

		try
		{
			locationId = Integer.parseInt(tokenizer.nextToken());
			formId = Integer.parseInt(tokenizer.nextToken());
			formInstanceId = Integer.parseInt(tokenizer.nextToken());
		}
		catch(Exception e)
		{
			log.error("Error displaying form in View Encounters for encounterId: {} (optionsString: {})",encounterId,optionsString,e);
			map.put(PARAMATER_VIEW_ENCOUNTERS_ERROR_MSG, errorMsg);
			return new ModelAndView(new RedirectView(FORM_VIEW_NAME), map);
		}

		FormService formService = Context.getFormService();
		Form form = formService.getForm(formId);
		String formName = form.getName();
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		Integer leftFormLocationId = null;
		Integer leftFormFormId = null;
		Integer leftFormFormInstanceId = null;
		String leftFormStylesheet = null;
		String rightFormStylesheet = null;
		String leftFormDirectory = null;
		String rightFormDirectory = null;
		Integer rightFormLocationId = null;
		Integer rightFormFormId = null;
		Integer rightFormFormInstanceId = null;

		FormsToDisplay formsToDisplayConfig = org.openmrs.module.chica.util.Util.getViewEncountersFormsToDisplayConfig();
		if(formsToDisplayConfig == null)
		{
			map.put(PARAMATER_VIEW_ENCOUNTERS_ERROR_MSG, errorMsg);
			return new ModelAndView(new RedirectView(FORM_VIEW_NAME), map);
		}

		Map<String, ViewEncounterForm> viewEncounterFormMap = formsToDisplayConfig.getViewEncounterFormMap();				

		// Determine if the selected form should display on the left or the right of the page
		ViewEncounterForm viewEncounterForm = viewEncounterFormMap.get(formName);
		if(DISPLAY_POSITION_LEFT.equalsIgnoreCase(viewEncounterForm.getDisplayPosition()))
		{
			leftFormLocationId = locationId;
			leftFormFormId = formId;
			leftFormFormInstanceId = formInstanceId;
			leftFormStylesheet = viewEncounterForm.getStylesheet();
			leftFormDirectory = viewEncounterForm.getDirectory();

			// Now query for patient states with the rightForm name
			// Note: There are no current cases where this list would have more than one possible right form to display
			if(viewEncounterForm.getRelatedForms() != null)
			{
				rightFormLoop:
					for(ViewEncounterForm rightForm : viewEncounterForm.getRelatedForms())
					{
						if(DISPLAY_POSITION_RIGHT.equalsIgnoreCase(rightForm.getDisplayPosition())) // Make sure this is configured properly as a right form
						{
							String rightName = rightForm.getName();
							List<String> stateNames = rightForm.getStateNames();
							List<PatientState> patientStates = null;
							try
							{
								// CHICA-1169 Query for patient states by form name and state
								patientStates = chirdlutilbackportsService.getPatientStatesByFormNameAndState(rightName, stateNames, encounterId, true);
								if(patientStates != null && !patientStates.isEmpty())
								{
									for(PatientState currState : patientStates)
									{
										if(currState.getEndTime() != null)
										{
											rightFormLocationId = currState.getLocationId();
											rightFormFormId = currState.getFormId();
											rightFormFormInstanceId = currState.getFormInstanceId();
											rightFormStylesheet = rightForm.getStylesheet();
											rightFormDirectory = rightForm.getDirectory();
											break rightFormLoop;
										}
									}
								}

								// We didn't break out of the outer loop (meaning we didn't find a "finished" state WITH and end time
								// This is not an else to the if statement above because we could have found a list of patientStates, 
								// but maybe none of them had an end time (which really shouldn't happen, but is possible)
								// Now try the old way so that we can at least display the formId and name even if it wasn't actually submitted or scanned
								patientStates = chirdlutilbackportsService.getPatientStatesWithFormInstances(rightName, encounterId); 
								if (patientStates != null && !patientStates.isEmpty()) 
								{
									HashMap<Date, FormInstance> tempFormInstancesMap = new HashMap<Date, FormInstance>();
									for (PatientState currState : patientStates) 
									{
										if (currState.getEndTime() != null) 
										{
											// Keep track of all form instances so that we can make sure the most recent instance is displayed 
											tempFormInstancesMap.put(currState.getEndTime(), currState.getFormInstance());										
										}
									}

									if (!tempFormInstancesMap.isEmpty())
									{
										FormInstance formInstance = tempFormInstancesMap.get(Collections.max(tempFormInstancesMap.keySet()));
										rightFormLocationId = formInstance.getLocationId();
										rightFormFormId = formInstance.getFormId();
										rightFormFormInstanceId = formInstance.getFormInstanceId();
										rightFormStylesheet = rightForm.getStylesheet();
										rightFormDirectory = rightForm.getDirectory();
									}
								} 
							}
							catch(APIException e)
							{
								log.error("{}: Error finding form information for the form to be displayed on the right side.",this.getClass().getName(), e);
							}	
						}	
					}
			}	
		}
		else if(DISPLAY_POSITION_RIGHT.equalsIgnoreCase(viewEncounterForm.getDisplayPosition()))
		{
			rightFormLocationId = locationId;
			rightFormFormId = formId;
			rightFormFormInstanceId = formInstanceId;
			rightFormStylesheet = viewEncounterForm.getStylesheet();
			rightFormDirectory = viewEncounterForm.getDirectory();

			if(viewEncounterForm.getRelatedForms() != null)
			{
				leftFormLoop:
					for(ViewEncounterForm leftForm : viewEncounterForm.getRelatedForms())
					{
						if(DISPLAY_POSITION_LEFT.equalsIgnoreCase(leftForm.getDisplayPosition())) // Make sure this is configured properly as a left form
						{
							String leftName = leftForm.getName();
							List<String> stateNames = leftForm.getStateNames();
							List<PatientState> patientStates = null;
							try
							{
								// CHICA-1169 Query for patient states by form name and state
								patientStates = chirdlutilbackportsService.getPatientStatesByFormNameAndState(leftName, stateNames, encounterId, true);
								if(patientStates != null && !patientStates.isEmpty())
								{
									for(PatientState currState : patientStates)
									{
										if(currState.getEndTime() != null)
										{
											leftFormLocationId = currState.getLocationId();
											leftFormFormId = currState.getFormId();
											leftFormFormInstanceId = currState.getFormInstanceId();
											leftFormStylesheet = leftForm.getStylesheet();
											leftFormDirectory = leftForm.getDirectory();
											break leftFormLoop;
										}
									}
								}

								// We didn't break out of the outer loop (meaning we didn't find a "finished" state WITH and end time
								// This is not an else to the if statement above because we could have found a list of patientStates, 
								// but maybe none of them had an end time (which really shouldn't happen, but is possible)
								// Now try the old way so that we can at least display the formId and name even if it wasn't actually submitted or scanned
								patientStates = chirdlutilbackportsService.getPatientStatesWithFormInstances(leftName, encounterId);
								if (patientStates != null && !patientStates.isEmpty()) 
								{
									HashMap<Date, FormInstance> tempFormInstancesMap = new HashMap<Date, FormInstance>();
									for (PatientState currState : patientStates) 
									{
										if (currState.getEndTime() != null) 
										{
											// Keep track of all form instances so that we can make sure the most recent instance is displayed 
											tempFormInstancesMap.put(currState.getEndTime(), currState.getFormInstance());
										}
									}

									if (!tempFormInstancesMap.isEmpty())
									{
										FormInstance formInstance = tempFormInstancesMap.get(Collections.max(tempFormInstancesMap.keySet()));
										leftFormLocationId = formInstance.getLocationId();
										leftFormFormId = formInstance.getFormId();
										leftFormFormInstanceId = formInstance.getFormInstanceId();
										leftFormStylesheet = leftForm.getStylesheet();
										leftFormDirectory = leftForm.getDirectory();
									}
								} 
							}
							catch(APIException e)
							{
								log.error("{}: Error finding form information for the form to be displayed on the left side.",this.getClass().getName(), e);
							}		
						}		
					}
			}
		}
									
		map.put(ChicaConstants.PARAMETER_RIGHT_FORM_LOCATION_ID, rightFormLocationId);
		map.put(ChicaConstants.PARAMETER_RIGHT_FORM_FORM_ID, rightFormFormId);
		map.put(ChicaConstants.PARAMETER_RIGHT_FORM_FORM_INSTANCE_ID, rightFormFormInstanceId);
		map.put(ChicaConstants.PARAMETER_RIGHT_FORM_STYLESHEET, rightFormStylesheet);
		map.put(ChicaConstants.PARAMETER_RIGHT_FORM_DIRECTORY, rightFormDirectory);
		map.put(ChicaConstants.PARAMETER_LEFT_FORM_LOCATION_ID, leftFormLocationId);
		map.put(ChicaConstants.PARAMETER_LEFT_FORM_FORM_ID, leftFormFormId);
		map.put(ChicaConstants.PARAMETER_LEFT_FORM_FORM_INSTANCE_ID, leftFormFormInstanceId);
		map.put(ChicaConstants.PARAMETER_LEFT_FORM_STYLESHEET, leftFormStylesheet);
		map.put(ChicaConstants.PARAMETER_LEFT_FORM_DIRECTORY, leftFormDirectory);

		return new ModelAndView(new RedirectView(REDIRECT_FORM_VIEW), map);
	}

	@RequestMapping(method = RequestMethod.GET) 
	protected String initForm(HttpServletRequest request, ModelMap map) throws Exception {
		
		String errorMsg = request.getParameter(PARAMATER_VIEW_ENCOUNTERS_ERROR_MSG);
		if(StringUtils.isNotBlank(errorMsg)) // An error occurred in processSubmit() pass it back through to the client
		{
			map.put(PARAMATER_VIEW_ENCOUNTERS_ERROR_MSG, errorMsg);
			return FORM_VIEW;
		}
		
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);		
		HashMap<Integer,String> displayNameMap = new HashMap<Integer,String>();

		try {
			FormsToDisplay formsToDisplayConfig = org.openmrs.module.chica.util.Util.getViewEncountersFormsToDisplayConfig();
			
			if(formsToDisplayConfig == null)
			{
				map.put(PARAMATER_VIEW_ENCOUNTERS_ERROR_MSG, "A server error occurred. No encounters will be displayed.");
				return FORM_VIEW;
			}
			
			Patient patient = null;
			String patientIdParam = request.getParameter(ChirdlUtilConstants.PARAMETER_PATIENT_ID);
			if(StringUtils.isNotBlank(patientIdParam))
			{
				try
				{
					Integer pid = Integer.parseInt(patientIdParam);
					PatientService patientService = Context.getPatientService();
					patient = patientService.getPatient(Integer.valueOf(pid));
				}
				catch(NumberFormatException nfe)
				{
					log.error("{}: unable to parse parameter for patientId: {}",this.getClass().getName(), patientIdParam,nfe);
				}
			}
			else
			{
				String mrn = request.getParameter(ChirdlUtilConstants.PARAMETER_MRN);
				patient = org.openmrs.module.chirdlutil.util.Util.getPatientByMRNOther(mrn);
			}
			
			
			if (patient == null) {
				return FORM_VIEW;
			}
			
			// title name, mrn, and dob
			String dobString = DateUtil.formatDate(patient.getBirthdate(), ChirdlUtilConstants.DATE_FORMAT_HYPHEN_yyyy_MM_dd, ChirdlUtilConstants.GENERAL_INFO_EMPTY_STRING);
			
			map.put(PARAMATER_TITLE_MRN, Util.getDisplayMRN(patient));
			map.put(PARAMATER_TITLE_LAST_NAME, patient.getFamilyName());
			map.put(PARAMATER_TITLE_FIRST_NAME, patient.getGivenName());
			map.put(PARAMATER_TITLE_DOB, dobString);

			// encounter rows
			EncounterService encounterService = Context.getEncounterService();
			List<org.openmrs.Encounter> list = encounterService.getEncountersByPatientId(patient.getPatientId());
			List<PatientRow> rows = new ArrayList<PatientRow>();
			
			List<String> formsToProcess = new ArrayList<String>();
			formsToProcess = formsToDisplayConfig.getFormNames();
			
			for (org.openmrs.Encounter enc : list) {
				Integer encounterId = enc.getEncounterId();
				PatientRow row = new PatientRow();
				
				Encounter enct = encounterService.getEncounter(encounterId);
				if (enct == null)
				{
					continue;
				}
				
				Integer locationId = enct.getLocation().getLocationId();
				Integer locationTagId = org.openmrs.module.chica.util.Util.getLocationTagId(enct);
				
				// Set checkin date text
				Date checkin = enct.getEncounterDatetime();
				String checkinDateString = "";
				if (checkin != null) {
					if (Util.isToday(checkin)) {
						checkinDateString = TODAY;
					} else {
						checkinDateString = DateUtil.formatDate(checkin, ChirdlUtilConstants.DATE_FORMAT_MMM_dd_comma_yyyy, ChirdlUtilConstants.GENERAL_INFO_EMPTY_STRING);
					}
				}

				// CHICA-221 Use the provider that has the "Attending Provider" role for the encounter
				org.openmrs.Provider provider = org.openmrs.module.chirdlutil.util.Util.getProviderByAttendingProviderEncounterRole(enct);
				String providerName = getProviderName(provider);
				
				//Get printer location from encounter attribute
				String printerLocation = null;
				EncounterAttributeValue printerLocationAttributeValue = chirdlutilbackportsService
						.getEncounterAttributeValueByName(enct.getEncounterId(),
								ChirdlUtilConstants.ENCOUNTER_ATTRIBUTE_PRINTER_LOCATION);
				if (printerLocationAttributeValue != null) {
					 printerLocation = printerLocationAttributeValue.getValueText();
				}
				
				row.setPatientId(patient.getPatientId());
				row.setFirstName(patient.getGivenName());
				row.setLastName(patient.getFamilyName());
				row.setMdName(providerName);
				row.setCheckin(checkinDateString);
				row.setEncounter(enct);
				row.setStation(printerLocation);
				row.setAgeAtVisit(org.openmrs.module.chirdlutil.util.Util.adjustAgeUnits(patient.getBirthdate(), checkin));

				// This section will add form instances to the row, which is used to populate the Action drop-down
				// It will also set the PSF and PWS id for the row
				// Loop over the forms listed in the config file and query for patient states for that form
				// All form instances of a form will be added to the drop-down,
				// except for the PWS, which will only have the most recently created or most recently submitted version added to the drop-down
				for(String formName : formsToProcess)
				{
					List<PatientState> patientStates = chirdlutilbackportsService.getPatientStatesByFormNameAndState(formName, null, encounterId, true);
					if (patientStates != null && !patientStates.isEmpty()) {

						HashMap<Date, FormInstance> pwsTempFormInstancesMap = new HashMap<Date, FormInstance>();
						HashMap<Integer, String> formTypeMap = new HashMap<Integer, String>();
						HashMap<Integer, String> endStateMap = new HashMap<Integer, String>();

						for (PatientState currState : patientStates) 
						{

							if (currState.getEndTime() != null) {
								Integer formId = currState.getFormId();

								// Determine if this is the primary physician form, primary patient form, or neither
								String formType = formTypeMap.get(formId);
								if(formType == null)
								{
									formType = org.openmrs.module.chirdlutil.util.Util.getFormType(formId, locationTagId, locationId);
								}

								// Get endState name from the form attribute
								String endStateName = endStateMap.get(formId);
								if(endStateName == null)
								{
									FormAttributeValue formAttributeValueEndStateName = chirdlutilbackportsService.getFormAttributeValue(formId, ChirdlUtilConstants.FORM_ATTRIBUTE_END_STATE, 
											locationTagId, locationId);
									if(formAttributeValueEndStateName != null && StringUtils.isNotBlank(formAttributeValueEndStateName.getValue()))
									{
										endStateName = formAttributeValueEndStateName.getValue();
									}
								}

								// Get the display name for this form
								String displayName = displayNameMap.get(formId);
								if(displayName == null)
								{
									// Use the display name from the form attribute
									FormAttributeValue fav = chirdlutilbackportsService.getFormAttributeValue(formId, ChirdlUtilConstants.FORM_ATTR_DISPLAY_NAME, locationTagId, locationId);
									if(fav != null && StringUtils.isNotBlank(fav.getValue()))
									{
										displayNameMap.put(formId, fav.getValue());
									}
								}

								//make sure you only get the most recent psf/pws pair
								if(ChirdlUtilConstants.PATIENT_FORM_TYPE.equals(formType))
								{
									if (row.getPsfId() == null) 
									{
										row.setPsfId(currState.getFormInstance());
									}
								}

								// If this is the PWS, set the pwsId to the most recently submitted PWS for the row
								// Only add the most recently submitted instance to the list of form instances for the row
								if (ChirdlUtilConstants.PHYSICIAN_FORM_TYPE.equals(formType)) 
								{
									if (row.getPwsId() == null) 
									{
										if (ChirdlUtilConstants.PHYSICIAN_FORM_TYPE.equals(formType)) 
										{
											if (currState.getState().getName().trim().equals(endStateName))
											{ 
												row.setPwsScanned(true);
												row.setPwsId(currState.getFormInstance());
												row.addFormInstance(currState.getFormInstance());
												pwsTempFormInstancesMap.clear();
											}
											else
											{
												// This isn't the scanned state add the form instance to the temp map
												pwsTempFormInstancesMap.put(currState.getEndTime(), currState.getFormInstance());
											}
										}
									}
								}
								else
								{
									// This is not the PWS, just add the form instance to the list 
									// so that all instances of the form show up in the drop-down
									row.addFormInstance(currState.getFormInstance());
								}	
							}
						}

						if (!row.isPwsScanned() && !pwsTempFormInstancesMap.isEmpty()) {
							FormInstance formInstance = pwsTempFormInstancesMap.get(Collections.max(pwsTempFormInstancesMap.keySet()));
							row.setPwsId(formInstance);
							row.addFormInstance(formInstance);
						}
					}
				}
			
				rows.add(row);
			}

			map.put(PARAMATER_TITLE_PATIENT_ROWS, rows);
			map.put(PARAMATER_TITLE_FORM_NAME_MAP,displayNameMap);

		} catch (UnexpectedRollbackException ex) {
			// ignore this exception since it happens with an
			// APIAuthenticationException
		} catch (APIAuthenticationException ex2) {
			// ignore this exception. It happens during the redirect to the
			// login page
		}

		return FORM_VIEW;
	}

	private String getProviderName(org.openmrs.Provider prov) {
		String mdName = "";
		if (prov != null) {
			Person person = prov.getPerson();
			String firstInit = Util.toProperCase(person.getGivenName());
			if (firstInit != null && firstInit.length() > 0) {
				firstInit = firstInit.substring(0, 1);
			} else {
				firstInit = "";
			}

			String middleInit = Util.toProperCase(person.getMiddleName());
			if (middleInit != null && middleInit.length() > 0) {
				middleInit = middleInit.substring(0, 1);
			} else {
				middleInit = "";
			}
			if (firstInit != null && firstInit.length() > 0) {
				mdName += firstInit + ".";
				if (middleInit != null && middleInit.length() > 0) {
					mdName += " " + middleInit + ".";
				}
			}
			if (mdName.length() > 0) {
				mdName += " ";
			}
			String familyName = Util.toProperCase(person.getFamilyName());
			if (familyName == null) {
				familyName = "";
			}
			mdName += familyName;

		}

		return mdName;

	}
}
