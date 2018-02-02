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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.FormService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chica.util.ChicaConstants;
import org.openmrs.module.chica.util.PatientRow;
import org.openmrs.module.chica.xmlBeans.viewEncountersConfig.FormsToDisplay;
import org.openmrs.module.chica.xmlBeans.viewEncountersConfig.ViewEncounterForm;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.DateUtil;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
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
	protected final Log log = LogFactory.getLog(getClass());
	
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

		Map<String, Object> map = new HashMap<String, Object>();
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
			log.error("Error displaying form in View Encounters. Unable to parse patientId (patientIdString: " + patientIdString + ").", e);
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
			log.error("Error displaying form in View Encounters. Unable to parse encounterId (encounterIdString: " + encounterIdString + ").", e);
			map.put(PARAMATER_VIEW_ENCOUNTERS_ERROR_MSG, errorMsg);
			return new ModelAndView(new RedirectView(FORM_VIEW_NAME), map);
		}
		
		if(optionsString == null)
		{
			log.error("Error displaying form in View Encounters for encounterId: " + encounterId + "(optionsString: " + optionsString + ")");
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
			log.error("Error displaying form in View Encounters for encounterId: " + encounterId + "(optionsString: " + optionsString + ")", e);
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
				for(ViewEncounterForm rightForm : viewEncounterForm.getRelatedForms())
				{
					if(DISPLAY_POSITION_RIGHT.equalsIgnoreCase(rightForm.getDisplayPosition())) // Make sure this is configured properly as a right form
					{
						String rightName = rightForm.getName();
						// NOTE: This query needs to change and will be fixed by CHICA-1169, 
						// we'll need to support a list of end states to query for due to paper vs electronic versions of the same form
						List<PatientState> patientStates = chirdlutilbackportsService.getPatientStatesWithFormInstances(rightName, encounterId); 
						if (patientStates != null && !patientStates.isEmpty()) 
						{
							boolean checkPWSProcess = false;
							HashMap<Date, FormInstance> pwsTempFormInstancesMap = new HashMap<Date, FormInstance>();
							for (PatientState currState : patientStates) 
							{
								if (currState.getEndTime() != null) 
								{
									if (currState.getState().getName().trim().equals(ChirdlUtilConstants.STATE_PWS_PROCESS))
									{
										checkPWSProcess = true; 
									}

									if (!checkPWSProcess) 
									{ 
										pwsTempFormInstancesMap.put(currState.getEndTime(), currState.getFormInstance());
									} 
									else 
									{
										rightFormLocationId = currState.getLocationId();
										rightFormFormId = currState.getFormId();
										rightFormFormInstanceId = currState.getFormInstanceId();
										rightFormStylesheet = rightForm.getStylesheet();
										rightFormDirectory = rightForm.getDirectory();
										pwsTempFormInstancesMap.clear();
										break;
									}
								}
							}

							if (!pwsTempFormInstancesMap.isEmpty())
							{
								FormInstance formInstance = pwsTempFormInstancesMap.get(Collections.max(pwsTempFormInstancesMap.keySet()));
								rightFormLocationId = formInstance.getLocationId();
								rightFormFormId = formInstance.getFormId();
								rightFormFormInstanceId = formInstance.getFormInstanceId();
								rightFormStylesheet = rightForm.getStylesheet();
								rightFormDirectory = rightForm.getDirectory();
							}
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
				for(ViewEncounterForm leftForm : viewEncounterForm.getRelatedForms())
				{
					if(DISPLAY_POSITION_LEFT.equalsIgnoreCase(leftForm.getDisplayPosition())) // Make sure this is configured properly as a left form
					{
						String leftName = leftForm.getName();
						
						// NOTE: This query needs to change and will be fixed by CHICA-1169, 
						// we'll need to support a list of end states to query for due to paper vs electronic versions of the same form
						List<PatientState> patientStates = chirdlutilbackportsService.getPatientStatesWithFormInstances(leftName, encounterId);
						if (patientStates != null && !patientStates.isEmpty()) 
						{
							for (PatientState currState : patientStates) 
							{
								if (currState.getEndTime() != null) 
								{
									leftFormLocationId = currState.getLocationId();
									leftFormFormId = currState.getFormId();
									leftFormFormInstanceId = currState.getFormInstanceId();
									leftFormStylesheet = leftForm.getStylesheet();
									leftFormDirectory = leftForm.getDirectory();
									break;
								}
							}
						} 
					}		
				}
			}
		}
				
//				if (formName.equals("PSF") || formName.equals("ADHD P") || formName.equals("ADHD PS") ||formName.equals("MCHAT") || formName.equals("MCHAT-R") || formName.equals("SummaryReportMchatR") || 
//						formName.equals("ADHD PFU") || formName.equals("ADHD PSFU") || 
//						formName.equals("ParentSummaryReport") || formName.equals("ImmunizationSchedule7yrOrOlder") ||
//						formName.equals("ImmunizationSchedule") || formName.equals("PHQ9_JIT_MOBILE")) 
//				{
//					leftImageLocationId = locationId;
//					leftImageFormId = formId;
//					leftImageFormInstanceId = formInstanceId;
//					
//					if (formName.equals("ParentSummaryReport"))
//					{
//						leftImageStylesheet = "parentSummaryReport.xsl";
//						displayMergeForms = true;
//					} 
//					else if (formName.equals("PSF")) 
//					{
//						leftImageStylesheet = "psf.xsl";
//					} 
//					else if (formName.equals("PHQ9_JIT_MOBILE")) 
//					{
//						leftImageStylesheet = "PHQ9_JIT_MOBILE.xsl";
//						displayScanForms = true;
//					} 
//					else if (formName.equals("MCHAT")) 
//					{
//						leftImageStylesheet = "mchat.xsl";
//						displayScanForms = true;
//					}
//					else if (formName.equals("MCHAT-R")) 
//					{
//						leftImageStylesheet = "mchat-r.xsl";
//						displayScanForms = true;
//					} 
//					else if (formName.equals("SummaryReportMchatR")) 
//					{
//						leftImageStylesheet = "mchat-rSummaryReport.xsl";
//						displayMergeForms = true;
//					}
//					
//				} 
//				else 
//				{
//					ArrayList<String> leftNames = new ArrayList<String>();
//					
//					if(formName.equals("ADHD T")){
//						leftNames.add("ADHD P");
//						leftNames.add("ADHD PS");
//					}
//					
//					if(formName.equals("ADHD TFU")){
//						leftNames.add("ADHD PFU");
//						leftNames.add("ADHD PSFU");
//					}
//					
//					if(formName.equals("PWS")){
//						leftNames.add("PSF");
//						leftImageStylesheet = "psf.xsl";
//					}
//					
//					if (formName.equals("TeacherSummaryReport")) {
//						leftNames.add("ParentSummaryReport");
//						leftImageStylesheet = "parentSummaryReport.xsl";
//						displayMergeForms = true;
//					}
//					
//					if (formName.equals("ImmunizationSchedule")) {
//						leftNames.add("ImmunizationSchedule");
//						leftImageStylesheet = "ImmunizationSchedule.xsl";
//						displayMergeForms = true;
//					}
//					
//					if (formName.equals("ImmunizationSchedule7yrOrOlder")) {
//						leftNames.add("ImmunizationSchedule7yrOrOlder");
//						leftImageStylesheet = "ImmunizationSchedule7yrOrOlder.xsl";
//						displayMergeForms = true;
//					}
//					
//					for (String leftName : leftNames) {
//						List<PatientState> patientStates = chirdlutilbackportsService.getPatientStatesWithFormInstances(leftName,
//						    encounterId);
//						if (patientStates != null && !patientStates.isEmpty()) {
//							
//							for (PatientState currState : patientStates) {
//								if (currState.getEndTime() != null) {
//									leftImageLocationId = currState.getLocationId();
//									leftImageFormId = currState.getFormId();
//									leftImageFormInstanceId = currState.getFormInstanceId();
//									break;
//								}
//							}
//						} else {
//							if (leftName!=null&&leftName.equals("PSF")) {
//								Chica1Appointment chica1Appt = chicaService.getChica1AppointmentByEncounterId(encounterId);
//								
//								if (chica1Appt != null) {
//									leftImageFormInstanceId = chica1Appt.getApptPsfId();
//									leftImageLocationId = encounter.getLocation().getLocationId();
//									
//									if (leftImageFormInstanceId != null) {
//										form = formService.getForm("PSF");
//										if (form != null) {
//											leftImageFormId = form.getFormId();
//										}
//									}
//								}
//							}
//						}
//					}
//				}
				
//				Integer rightImageLocationId = null;
//				Integer rightImageFormId = null;
//				Integer rightImageFormInstanceId = null;
				
				//don't set a right image for MCHAT or MCHAT-R
//				if (!formName.equals("MCHAT") && !formName.equals("MCHAT-R") && !formName.equals("SummaryReportMchatR") && 
//						!formName.equals("ImmunizationSchedule7yrOrOlder") && !formName.equals("ImmunizationSchedule") && 
//						!formName.endsWith("PHQ9_JIT_MOBILE"))
//				{
//					if (formName.equals("PWS") || formName.equals("ADHD T")|| formName.equals("ADHD TFU") || formName.equals("TeacherSummaryReport"))
//					{
//							rightImageLocationId = locationId;
//							rightImageFormId = formId;
//							rightImageFormInstanceId = formInstanceId;
//							if (formName.equals("TeacherSummaryReport")) {
//								rightImageStylesheet = "teacherSummaryReport.xsl";
//								displayMergeForms = true;
//							} else if (formName.equals("PWS")) {
//								rightImageStylesheet = "pws.xsl";
//							}
//					} 
//					else 
//					{
//						
//						String rightName = null;
//						
//						if (formName.equals("ADHD P")) {
//							rightName = "ADHD T";
//						}
//						
//						if (formName.equals("ADHD PFU")) {
//							rightName = "ADHD TFU";
//						}
//						
//						if (formName.equals("ADHD PS")) {
//							rightName = "ADHD T";
//						}
//						
//						if (formName.equals("ADHD PSFU")) {
//							rightName = "ADHD TFU";
//						}
//						
//						if (formName.equals("PSF")) {
//							rightName = "PWS";
//							rightImageStylesheet = "pws.xsl";
//							rightImageDirectory = "defaultExportDirectory";// TODO CHICA-1125 Testing
//						}
//						
//						if (formName.equals("ParentSummaryReport")) {
//							rightName = "TeacherSummaryReport";
//							rightImageStylesheet = "teacherSummaryReport.xsl";
//							displayMergeForms = true;
//						}
//						
//						List<PatientState> patientStates = chirdlutilbackportsService.getPatientStatesWithFormInstances(rightName,
//						    encounterId);
//						if (patientStates != null && !patientStates.isEmpty()) {
//							boolean checkPWSProcess = false;
//							HashMap<Date, FormInstance> pwsTempFormInstancesMap = new HashMap<Date, FormInstance>();
//							for (PatientState currState : patientStates) {
//								if (currState.getEndTime() != null) {
//									if (currState.getState().getName().trim().equals(ChirdlUtilConstants.STATE_PWS_PROCESS)){
//										checkPWSProcess = true; 
//									}
//									if (!checkPWSProcess) { 
//										pwsTempFormInstancesMap.put(currState.getEndTime(), currState.getFormInstance());
//									} else {
//										rightImageLocationId = currState.getLocationId();
//										rightImageFormId = currState.getFormId();
//										rightImageFormInstanceId = currState.getFormInstanceId();
//										pwsTempFormInstancesMap.clear();
//										break;
//									}
//								}
//							}
//							if (!pwsTempFormInstancesMap.isEmpty()) {
//								FormInstance formInstance = pwsTempFormInstancesMap.get(Collections.max(pwsTempFormInstancesMap.keySet()));
//								rightImageLocationId = formInstance.getLocationId();
//								rightImageFormId = formInstance.getFormId();
//								rightImageFormInstanceId = formInstance.getFormInstanceId();
//							}
//						} else {
//							if (rightName != null && rightName.equals("PWS")) {
//								Chica1Appointment chica1Appt = chicaService.getChica1AppointmentByEncounterId(encounterId);
//								
//								if (chica1Appt != null) {
//									rightImageFormInstanceId = chica1Appt.getApptPwsId();
//									rightImageLocationId = encounter.getLocation().getLocationId();
//									
//									if (rightImageFormInstanceId != null) {
//										form = formService.getForm("PWS");
//										if (form != null) {
//											rightImageFormId = form.getFormId();
//										}
//									}
//								}
//							}
//						}
//					}
//				}
										
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
		if(StringUtils.isNotEmpty(errorMsg)) // An error occurred in processSubmit() pass it back through to the client
		{
			map.put(PARAMATER_VIEW_ENCOUNTERS_ERROR_MSG, errorMsg);
			return FORM_VIEW;
		}
		
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);		
		FormService formService = Context.getFormService();
		PatientService patientService = Context.getService(PatientService.class);
		HashMap<Integer,String> formNameMap = new HashMap<Integer,String>();

		try {
			Patient patient = null;
			String mrn = request.getParameter(ChirdlUtilConstants.PARAMETER_MRN);
			if (StringUtils.isNotEmpty(mrn)) {
				mrn = Util.removeLeadingZeros(mrn);
				if (!mrn.contains(ChirdlUtilConstants.GENERAL_INFO_DASH) && mrn.length() > 1) {
					mrn = mrn.substring(0, mrn.length() - 1) + ChirdlUtilConstants.GENERAL_INFO_DASH + mrn.substring(mrn.length()-1);
				}

				PatientIdentifierType identifierType = patientService.getPatientIdentifierTypeByName(ChirdlUtilConstants.IDENTIFIER_TYPE_MRN);
				List<PatientIdentifierType> identifierTypes = new ArrayList<PatientIdentifierType>();
				identifierTypes.add(identifierType);

				List<Patient> patients = patientService.getPatientsByIdentifier(null, mrn, identifierTypes,true); // CHICA-977 Use getPatientsByIdentifier() as a temporary solution to openmrs TRUNK-5089
				if (patients.size() == 0){
					patients = patientService.getPatientsByIdentifier(null, "0" + mrn, identifierTypes,true); // CHICA-977 Use getPatientsByIdentifier() as a temporary solution to openmrs TRUNK-5089
				}

				if (patients.size() > 0)
				{
					patient = patients.get(0);
				}
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
			EncounterService encounterService = Context.getService(EncounterService.class);
			List<org.openmrs.Encounter> list = encounterService.getEncountersByPatientId(patient.getPatientId());
			List<PatientRow> rows = new ArrayList<PatientRow>();
			
			List<String> formsToProcess = new ArrayList<String>();
			FormsToDisplay formsToDisplayConfig = org.openmrs.module.chica.util.Util.getViewEncountersFormsToDisplayConfig();
			
			if(formsToDisplayConfig == null)
			{
				map.put(PARAMATER_VIEW_ENCOUNTERS_ERROR_MSG, "A server error occurred. No encounters will be displayed.");
				return FORM_VIEW;
			}
			
			formsToProcess = formsToDisplayConfig.getFormNames();
			
			for (org.openmrs.Encounter enc : list) {
				Integer encounterId = enc.getEncounterId();
				PatientRow row = new PatientRow();
				
				Encounter enct = (Encounter) encounterService.getEncounter(encounterId);
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
				
				row.setPatientId(patient.getPatientId());
				row.setFirstName(patient.getGivenName());
				row.setLastName(patient.getFamilyName());
				row.setMdName(providerName);
				row.setCheckin(checkinDateString);
				row.setEncounter(enct);
				row.setStation(enct.getPrinterLocation());
				row.setAgeAtVisit(org.openmrs.module.chirdlutil.util.Util.adjustAgeUnits(patient.getBirthdate(), checkin));

				// This section will add form instances to row, which is used to populate the Action drop-down
				// It will also set the PSF and PWS id for the row
				List<PatientState> patientStates = chirdlutilbackportsService.getPatientStatesWithFormInstances(null,encounterId);
				if (patientStates != null && !patientStates.isEmpty()) {
					HashMap<Date, FormInstance> pwsTempFormInstancesMap = new HashMap<Date, FormInstance>();
					boolean checkPWSProcess = false;
					for (PatientState currState : patientStates) {
						if (currState.getEndTime() != null) {
							Integer formId = currState.getFormId();
							String formName = formNameMap.get(formId);
							if(formName == null){
								Form form = formService.getForm(formId);
								formName = form.getName();
								
								// Use the display name from the form attribute
								FormAttributeValue fav = chirdlutilbackportsService.getFormAttributeValue(formId, ChirdlUtilConstants.FORM_ATTR_DISPLAY_NAME, locationTagId, locationId);
								if(fav != null && StringUtils.isNotEmpty(fav.getValue()))
								{
									formNameMap.put(formId, fav.getValue());
								}							
							}
							
							//make sure you only get the most recent psf/pws pair
							if (row.getPsfId() == null) {
								if (formName.equals("PSF")) {
									row.setPsfId(currState.getFormInstance());
								}
							}
							if (row.getPwsId() == null) {
								if (formName.equals("PWS")) {
									if (currState.getState().getName().trim().equals(ChirdlUtilConstants.STATE_PWS_PROCESS)){
										checkPWSProcess = true;
									}
									if (!checkPWSProcess) { 
										pwsTempFormInstancesMap.put(currState.getEndTime(), currState.getFormInstance());
									} else {
										row.setPwsId(currState.getFormInstance());
									}
								}
							}
							if (formsToProcess.contains(formName)) {
								if (formName.equals("PWS") ){
									if (checkPWSProcess && row.getPwsId() != null) { 
											row.addFormInstance(currState.getFormInstance());
											pwsTempFormInstancesMap.clear();
											checkPWSProcess = false;
									} 
								} else {
									row.addFormInstance(currState.getFormInstance());
								}
							}
						}
					}
					
					if (!pwsTempFormInstancesMap.isEmpty()) {
						FormInstance formInstance = pwsTempFormInstancesMap.get(Collections.max(pwsTempFormInstancesMap.keySet()));
						row.setPwsId(formInstance);
						row.addFormInstance(formInstance);
					}
				}
			
				FormInstance pwsId = row.getPwsId();
				if (pwsId != null) {
					State currState = chirdlutilbackportsService.getStateByName(ChirdlUtilConstants.STATE_PWS_PROCESS);
					List<PatientState> pwsScanStates = chirdlutilbackportsService.getPatientStateByFormInstanceState(pwsId,
						currState,true);
					if (pwsScanStates != null && pwsScanStates.size() > 0) {
						PatientState pwsScanState = pwsScanStates.get(0);
						if (pwsScanState.getEndTime() != null) {
							row.setPwsScanned(true);
						}
					}
				}
				rows.add(row);
			}

			map.put(PARAMATER_TITLE_PATIENT_ROWS, rows);
			map.put(PARAMATER_TITLE_FORM_NAME_MAP,formNameMap);

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
