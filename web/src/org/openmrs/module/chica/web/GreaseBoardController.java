package org.openmrs.module.chica.web;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.result.Result;
import org.openmrs.module.atd.StateManager;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.Program;
import org.openmrs.module.atd.hibernateBeans.Session;
import org.openmrs.module.atd.hibernateBeans.State;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.ChicaStateActionHandler;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chirdlutil.hibernateBeans.LocationAttributeValue;
import org.openmrs.module.chirdlutil.service.ChirdlUtilService;
import org.openmrs.module.chirdlutil.util.Util;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

public class GreaseBoardController extends SimpleFormController
{
	private static final String WAIT_COLOR = "red_highlight";
	private static final String PROCESSING_COLOR = "yellow_highlight";
	private static final String READY_COLOR = "green_highlight";
	private static final String PSF_REPRINT_COLOR = "purple_highlight";
	private static final String PWS_REPRINT_COLOR = "blue_highlight";
	private static final String MANUAL_CHECKIN_COLOR = "almond_highlight";

	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	private static int numRefreshes = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception
	{
		return "testing";
	}

	
	@Override
	protected ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception
	{
		ATDService atdService = Context.getService(ATDService.class);
	
		String optionsString = request.getParameter("options");
		
		String patientIdString = request.getParameter("patientId");
		Integer patientId = null;
		try
		{
			if(patientIdString != null){
				patientId = Integer.parseInt(patientIdString);
			}
		} catch (Exception e)
		{
		}
		String sessionIdString = request.getParameter("sessionId");
		Integer sessionId = null;
		try
		{
			if(sessionIdString != null){
				sessionId = Integer.parseInt(sessionIdString);
			}
		} catch (Exception e)
		{
		}
		
		//Initiate an ADHD WU for the patient
		if (optionsString != null && optionsString.equalsIgnoreCase("ADHD WU")) {
			PatientService patientService = Context.getPatientService();
			Patient patient = patientService.getPatient(patientId);
			ConceptService conceptService = Context.getConceptService();
			Concept currConcept = conceptService.getConceptByName("CHICA_ADHD_SX");
			Session session = atdService.getSession(sessionId);
			Integer encounterId = session.getEncounterId();
			org.openmrs.module.chica.util.Util.saveObs(patient, currConcept, encounterId, 
				"workup_initiated", null,null, null);
			
			LogicService logicService = Context.getLogicService();
			
			//print the ADHD parent form
			User user = Context.getUserContext().getAuthenticatedUser();
			String locationString = user.getUserProperty("location");
			String locationTags = user.getUserProperty("locationTags");
			LocationService locationService = Context.getLocationService();
			
			Integer locationId = null;
			Location location = null;
			if(locationString != null){
				location = locationService.getLocation(locationString);
				if(location != null){
					locationId = location.getLocationId();
				}
			}
			
			Integer locationTagId = null;
			if(locationTags != null&location!=null){
				StringTokenizer tokenizer = new StringTokenizer(locationTags,",");
				while(tokenizer.hasMoreTokens()){
					String locationTagName = tokenizer.nextToken();
					locationTagName = locationTagName.trim();
					Set<LocationTag> tags = location.getTags();
					for(LocationTag tag:tags){
						if(tag.getTag().equalsIgnoreCase(locationTagName)){
							locationTagId = tag.getLocationTagId();
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
			logicService.eval(patient, "CREATE_JIT",parameters);
			
			//print the ADHD Spanish parent form
			parameters = new HashMap<String,Object>();
			parameters.put("sessionId",sessionId);
			parameters.put("locationTagId",locationTagId); 
			formInstance = new FormInstance();
			formInstance.setLocationId(locationId);
			parameters.put("formInstance",formInstance);
			parameters.put("param1","ADHD PS");
			logicService.eval(patient, "CREATE_JIT",parameters);
			
			//print the ADHD teacher form
			parameters = new HashMap<String,Object>();
			parameters.put("sessionId",sessionId);
			parameters.put("locationTagId",locationTagId); 
			formInstance = new FormInstance();
			formInstance.setLocationId(locationId);
			parameters.put("formInstance",formInstance);
			parameters.put("param1","ADHD T");
			logicService.eval(patient, "CREATE_JIT",parameters);
		}
		
		//Print a First Steps Referral form
		if (optionsString != null && optionsString.equalsIgnoreCase("Print FSR")) {
			PatientService patientService = Context.getPatientService();
			Patient patient = patientService.getPatient(patientId);
	
			LogicService logicService = Context.getLogicService();
			
			User user = Context.getUserContext().getAuthenticatedUser();
			String locationString = user.getUserProperty("location");
			String locationTags = user.getUserProperty("locationTags");
			LocationService locationService = Context.getLocationService();
			
			Integer locationId = null;
			Location location = null;
			if(locationString != null){
				location = locationService.getLocation(locationString);
				if(location != null){
					locationId = location.getLocationId();
				}
			}
			
			Integer locationTagId = null;
			if(locationTags != null&location!=null){
				StringTokenizer tokenizer = new StringTokenizer(locationTags,",");
				while(tokenizer.hasMoreTokens()){
					String locationTagName = tokenizer.nextToken();
					locationTagName = locationTagName.trim();
					Set<LocationTag> tags = location.getTags();
					for(LocationTag tag:tags){
						if(tag.getTag().equalsIgnoreCase(locationTagName)){
							locationTagId = tag.getLocationTagId();
						}
					}
				}
				
			}
			//print the First Steps Referral form
			Map<String, Object> parameters = new HashMap<String,Object>();
			parameters.put("sessionId",sessionId);
			parameters.put("locationTagId",locationTagId); 
			FormInstance formInstance = new FormInstance();
			formInstance.setLocationId(locationId);
			parameters.put("formInstance",formInstance);
			parameters.put("param1","FirstStepsReferral");
			logicService.eval(patient, "CREATE_JIT",parameters);
			
			return new ModelAndView(new RedirectView("greaseBoard.form"));
		}

		if (optionsString != null && optionsString.startsWith("Print"))
		{
			String formName = optionsString.replaceAll("Print", "");
			formName = formName.trim();
			

			if (patientId != null && sessionId != null && formName != null)
			{
				FormService formService = Context.getFormService();
				Session session = atdService.getSession(sessionId);
				Integer encounterId = session.getEncounterId();
				
				List<Form> forms = formService.getForms(formName,null,null,false,null,null,null);
				Integer formId = null;
				if (forms != null && forms.size() > 0)
				{
					formId = forms.get(0).getFormId();
				}
				String action = "PRODUCE FORM INSTANCE";
				
				PatientState patientStateProduce = atdService
						.getPatientStateByEncounterFormAction(encounterId, formId,
								action);

				State currState = null;

				
				//Don't generate a PSF with the print button on the greaseboard
				//We don't ever want more than one unique PSF
				
				String stateName = null;
				
				if(formName.equalsIgnoreCase("PSF")||
						formName.equalsIgnoreCase("PWS"))
				{
					stateName = formName+"_reprint";
				}else{
					stateName = "JIT_reprint";
				}
				
				if (formName.equalsIgnoreCase("PSF"))
				{
					currState = atdService
							.getStateByName(stateName);
				} else
				{
					// reprint if the state exists
					if (patientStateProduce != null)
					{
						currState = atdService.getStateByName(stateName);
					} else
					{
						// create for the first time if it does not exist
						currState = atdService.getStateByName("QUERY KITE "
								+ formName);
					}
				}
				
				HashMap<String,Object> actionParameters = new HashMap<String,Object>();
				actionParameters.put("formName", formName);

				if (currState != null)
				{
					PatientState patientState = atdService.getLastPatientState(sessionId);
					PatientService patientService = Context.getPatientService();
					Patient patient = patientService.getPatient(patientId);

					StateManager.runState(patient, sessionId, currState,actionParameters,
							patientState.getLocationTagId(),
							patientState.getLocationId(),
							ChicaStateActionHandler.getInstance());
				}
			}
			
			return new ModelAndView(new RedirectView("greaseBoard.form"));
		}
		
		return new ModelAndView(new RedirectView("greaseBoard.form"));
		
	}

	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception
	{
		if(Context.getUserContext().getAuthenticatedUser()== null){
			return null;
		}
		AdministrationService adminService = Context.getAdministrationService();
		numRefreshes++;
		Map<String, Object> map = new HashMap<String, Object>();
		Integer needVitals = 0;
		Integer waitingForMD = 0;
		User user = Context.getUserContext().getAuthenticatedUser();
		
		try
		{
			ATDService atdService = Context
					.getService(ATDService.class);
			ChicaService chicaService = Context.getService(ChicaService.class);
			
			Calendar todaysDate = Calendar.getInstance();
			Date now = todaysDate.getTime();
			String today =  new SimpleDateFormat("MMM dd, yyyy").format(now);
			map.put("today", today);
		
			todaysDate.set(Calendar.HOUR_OF_DAY, 0);
			todaysDate.set(Calendar.MINUTE, 0);
			todaysDate.set(Calendar.SECOND, 0);
			
			String locationTags = user.getUserProperty("locationTags");
			String locationString = user.getUserProperty("location");
			ArrayList<Integer> locationTagIds = new ArrayList<Integer>();
			LocationService locationService = Context.getLocationService();
			
			Integer locationId = null;
			Location location = null;
			if(locationString != null){
				location = locationService.getLocation(locationString);
				if(location != null){
					locationId = location.getLocationId();
					List<URL> badScans = chicaService.getBadScans(location.getName());
					map.put("badScans", badScans);
				}
			}
			
			if(locationTags != null&location!=null){
				StringTokenizer tokenizer = new StringTokenizer(locationTags,",");
				while(tokenizer.hasMoreTokens()){
					String locationTagName = tokenizer.nextToken();
					locationTagName = locationTagName.trim();
					Set<LocationTag> tags = location.getTags();
					for(LocationTag tag:tags){
						if(tag.getTag().equalsIgnoreCase(locationTagName)){
							locationTagIds.add(tag.getLocationTagId());
						}
					}
				}
				
			}
			
			List<PatientState> unfinishedStates = new ArrayList<PatientState>();

			for (Integer locationTagId : locationTagIds)
			{
				Program program = atdService.getProgram(locationTagId,locationId);
				List<PatientState> currUnfinishedStates = atdService
					.getLastPatientStateAllPatients(todaysDate.getTime(),
						program.getProgramId(),program.getStartState().getName(), 
						locationTagId,locationId);
				if(currUnfinishedStates != null){
					unfinishedStates.addAll(currUnfinishedStates);
				}
			}
			
			ArrayList<PatientRow> rows = new ArrayList<PatientRow>();
			
			for (PatientState currState : unfinishedStates)
			{
				String checkoutStateString = adminService.getGlobalProperty("chica.greaseboardCheckoutState");
				State checkoutState = atdService.getStateByName(checkoutStateString);
				Integer sessionId = currState.getSessionId();
				List<PatientState> checkoutPatientStates = atdService.getPatientStateBySessionState(sessionId, checkoutState.getStateId());
				if(checkoutPatientStates != null && checkoutPatientStates.size()>0){
					continue;
				}
				
				Patient patient = currState.getPatient();
				String lastName = Util.toProperCase(patient.getFamilyName());
				String firstName = Util.toProperCase(patient.getGivenName());

				String mrn = atdService.evaluateRule("medicalRecordNoFormatting", 
						patient, null, null).toString();
			

				String dob = atdService.evaluateRule("birthdate>fullDateFormat", 
						patient, null, null).toString();
				String sex = patient.getGender();
				State state = currState.getState();
				
				Session session = atdService.getSession(sessionId);
				Integer encounterId = session.getEncounterId();
				EncounterService encounterService = Context
						.getService(EncounterService.class);
				Encounter encounter = (Encounter) encounterService
						.getEncounter(encounterId);
				
				Map<String,Object> parameters = new HashMap<String,Object>();
				parameters.put("encounterId", encounterId);
				String appointment = atdService.evaluateRule("scheduledTime>fullTimeFormat", 
						patient, parameters, null).toString();
				PatientRow row = new PatientRow();
				Date encounterDate  = null;
				
				if (encounter != null)
				{
					row.setEncounter(encounter);
					encounterDate = encounter.getEncounterDatetime();
					if (encounterDate != null && !Util.isToday(encounterDate)) {
							continue;
					}
					EncounterType encType = encounter.getEncounterType();
					if (encType != null && encType.getName().equalsIgnoreCase("ManualCheckin")){
						row.setRowColor(MANUAL_CHECKIN_COLOR);
					}
					parameters.put("param0", new Result(encounter
							.getEncounterDatetime()));
					String checkin = atdService.evaluateRule("fullTimeFormat",
							patient, parameters, null).toString();
					User provider = encounter.getProvider();
					String mdName = "";
					//Ensure proper case even though we store provider names in proper case
					//Any provider names stored before the hl7sockethandler update need to be
					//adjusted to proper case for display on greaseboard
					if (provider != null)
					{
						String firstInit = Util.toProperCase(provider.getGivenName());			
						if (firstInit != null && firstInit.length() > 0)
						{
							firstInit = firstInit.substring(0, 1);
						}else {
							firstInit = "";
						}

						String middleInit = Util.toProperCase(provider.getMiddleName());
						if (middleInit != null && middleInit.length() > 0)
						{
							middleInit = middleInit.substring(0, 1);
						}
						else {
							middleInit = "";
						}
						if (firstInit != null && firstInit.length() > 0)
						{
							mdName += firstInit + ".";
							if (middleInit != null && middleInit.length() > 0)
							{
								mdName += " " + middleInit + ".";
							}
						}
						if (mdName.length() > 0)
						{
							mdName += " ";
						}
						String familyName = Util.toProperCase(provider.getFamilyName());
						if (familyName == null){
							familyName = "";
						}
						mdName += familyName;
						
					}
					row.setCheckin(checkin);
					row.setMdName(mdName);

				}
				
				List<PatientState> reprintRescanStates = new ArrayList<PatientState>();

				for (Integer locationTagId : locationTagIds)
				{
					List<PatientState> currReprintRescanStates = chicaService
					.getReprintRescanStatesByEncounter(encounterId,
							todaysDate.getTime(), locationTagId,locationId);
					if(currReprintRescanStates != null){
						reprintRescanStates.addAll(currReprintRescanStates);
					}
				}
				boolean reprint = false;
				if (reprintRescanStates.size() > 0)
				{
					reprint = true;
				}
				row.setReprintStatus(reprint);
				row.setAppointment(appointment);
				row.setDob(dob);
				row.setFirstName(firstName);
				row.setLastName(lastName);

				row.setMrn(mrn);
				row.setSex(sex);
				row.setPatientId(patient.getPatientId());
				row.setSessionId(sessionId);
				String stateName = state.getName();
				if(stateName.equalsIgnoreCase("PSF_wait_to_scan")){
					needVitals++;
				}
				if(stateName.equalsIgnoreCase("PWS_wait_to_scan")){
					waitingForMD++;
				}
				getStatus(state, row, sessionId,currState);
				
				ArrayList<String> printableJits = new ArrayList<String>();
				State createJitState = atdService.getStateByName("JIT_create");
				List<PatientState> jitStates = 
					atdService.getPatientStateByEncounterState(encounterId, createJitState.getStateId());
				
				for(PatientState jitState:jitStates){
					FormService formService = Context.getFormService();
					Integer formId = jitState.getFormId();
					
					if (formId != null) {
						Form form = formService.getForm(formId);
						String jitFormName = form.getName();
						//only display ADHD forms for reprint
						if(jitFormName.startsWith("ADHD")||
								jitFormName.startsWith("ASQ")||
								jitFormName.startsWith("MCHAT")){
							printableJits.add("Print " + jitFormName);
						}
					}
				}
				row.setPrintableJits(printableJits);
				rows.add(row);
			}
			
			//sort arraylist by encounterDatetime
			Collections.sort(rows, new PatientRowComparator());
			
			map.put("needVitals", needVitals);
			map.put("waitingForMD", waitingForMD);
			map.put("patientRows",rows);
			
			map.put("refreshPeriod", adminService.getGlobalProperty(
					"chica.greaseBoardRefresh"));
		
			boolean isADHDInterventionLocation = isInterventionLocation(locationId,"isADHDInterventionLocation");
			map.put("isADHDInterventionLocation", isADHDInterventionLocation);
			boolean isASQInterventionLocation = isInterventionLocation(locationId,"isASQInterventionLocation");
			map.put("isASQInterventionLocation", isASQInterventionLocation);
		}catch(UnexpectedRollbackException ex){
			//ignore this exception since it happens with an APIAuthenticationException
		}catch(APIAuthenticationException ex2){
			//ignore this exception. It happens during the redirect to the login page
		}
		
		if(numRefreshes%100==0){
			Context.clearSession();
		}
		
		return map;
	}

	private void getStatus(State state, PatientRow row, Integer sessionId,PatientState currState)
	{
		//see if an incomplete state exists for the JIT
		ATDService atdService = Context.getService(ATDService.class);
		State jitIncompleteState = atdService.getStateByName("JIT_incomplete");
		FormInstance formInstance = currState.getFormInstance();
		Integer formId = null;
		String formName = null;
		FormService formService = Context.getFormService();

		if(formInstance != null){
			formId = formInstance.getFormId();
			formName = formService.getForm(formId).getName();
		}
		
		List<PatientState> patientStates = atdService.getPatientStateBySessionState(sessionId,jitIncompleteState.getStateId());
		
		//color the row a different color if there is an incomplete JIT state
		if (patientStates != null) {
			for (PatientState patientState : patientStates) {
				if (patientState.getEndTime() == null) {
					row.setStatusColor(WAIT_COLOR);
					formId = patientState.getFormInstance().getFormId();
					formName = formService.getForm(formId).getName();
					row.setStatus(formName + " incomplete");
					return;
				}
			}
		}
		
		String stateName = state.getName();
		if (stateName.equals("CHECKIN"))
		{
			row.setStatusColor(WAIT_COLOR);
			row.setStatus("Arrived");
			return;
		}
		if (stateName.equals("QUERY KITE PWS")||
				stateName.equals("QUERY KITE PSF")||
				stateName.equals("QUERY KITE Alias"))
		{
			row.setStatusColor(PROCESSING_COLOR);
			row.setStatus("Searching Patient Data...");
			return;
		}
		if (stateName.equals("PSF_create") || stateName.equals("Randomize"))
		{
			row.setStatusColor(PROCESSING_COLOR);
			row.setStatus("Creating PSF...");
			return;
		}
		if (stateName.equals("PSF_printed"))
		{
			row.setStatusColor(PROCESSING_COLOR);
			row.setStatus("Printing PSF...");
			return;
		}
		if (stateName.equals("PSF_wait_to_scan"))
		{
			row.setStatusColor(READY_COLOR);
			row.setStatus("PSF Ready");
			return;
		}
		if (stateName.equals("PSF_process"))
		{
			row.setStatusColor(WAIT_COLOR);
			row.setStatus("PSF Scanned");
			return;
		}
		if (stateName.equals("PWS_create"))
		{
			row.setStatusColor(PROCESSING_COLOR);
			row.setStatus("Creating PWS...");
			return;
		}
		if (stateName.equals("PWS_printed"))
		{
			row.setStatusColor(PROCESSING_COLOR);
			row.setStatus("Printing PWS...");
			return;
		}
		if (stateName.equals("PWS_wait_to_scan"))
		{
			row.setStatusColor(READY_COLOR);
			row.setStatus("PWS Ready");
			return;
		}
		if (stateName.equals("PWS_process"))
		{
			row.setStatusColor(READY_COLOR);
			row.setStatus("PWS Scanned");
			return;
		}
		if (stateName.equals("FINISHED"))
		{
			row.setStatusColor(READY_COLOR);
			row.setStatus("Gone");
			return;
		}
		if (stateName.equals("ErrorState")){
			row.setStatusColor(WAIT_COLOR);
			row.setStatus("Error. Contact support");
		}
		
		if (stateName.equals("PSF_reprint")){
			row.setStatusColor(PSF_REPRINT_COLOR);
			row.setStatus("PSF reprint");
		}
		
		if (stateName.equals("PWS_reprint")){
			row.setStatusColor(PWS_REPRINT_COLOR);
			row.setStatus("PWS reprint");
		}
		
		//PSF_rescan and PWS_rescan states are currently not given
		//a status on the greaseboard
		
	}
	
	private boolean isInterventionLocation(Integer locationId,String interLocationAttributeName) {
		ChirdlUtilService chirdlUtilService = Context.getService(ChirdlUtilService.class);
		
		LocationAttributeValue locationAttributeValue = chirdlUtilService.getLocationAttributeValue(locationId,
			interLocationAttributeName);
		if (locationAttributeValue != null) {
			String interventionSiteString = locationAttributeValue.getValue();
			if (interventionSiteString.equalsIgnoreCase("true")) {
				return true;
			}
		}
		return false;
	}
}
