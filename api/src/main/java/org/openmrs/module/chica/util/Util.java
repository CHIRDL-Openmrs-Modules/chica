/**
 * 
 */
package org.openmrs.module.chica.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.result.EmptyResult;
import org.openmrs.logic.result.Result;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.atd.hibernateBeans.Statistics;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.Calculator;
import org.openmrs.module.chica.xmlBeans.viewEncountersConfig.FormsToDisplay;
import org.openmrs.module.chica.xmlBeans.viewEncountersConfig.ViewEncountersConfig;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.ObsComparator;
import org.openmrs.module.chirdlutil.util.XMLUtil;
import org.openmrs.module.chirdlutil.xmlBeans.serverconfig.MobileClient;
import org.openmrs.module.chirdlutil.xmlBeans.serverconfig.MobileClients;
import org.openmrs.module.chirdlutil.xmlBeans.serverconfig.MobileForm;
import org.openmrs.module.chirdlutil.xmlBeans.serverconfig.ServerConfig;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.EncounterAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstanceTag;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.LocationTagAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.ObsAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Program;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Session;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.module.chirdlutilbackports.util.PatientStateStartDateComparator;
import org.openmrs.module.dss.xmlBeans.physiciannote.HeadingOrder;
import org.openmrs.module.dss.xmlBeans.physiciannote.PhysicianNoteConfig;
import org.openmrs.module.sockethl7listener.hibernateBeans.HL7Outbound;
import org.openmrs.module.sockethl7listener.service.SocketHL7ListenerService;
import org.openmrs.parameter.EncounterSearchCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tammy Dugan
 */
public class Util {
	
	private static final int PRIMARY_FORM = 0;
	
	private static final int SECONDARY_FORMS = 1;
	
	public static final String YEAR_ABBR = "yo";
	
	public static final String MONTH_ABBR = "mo";
	
	public static final String WEEK_ABBR = "wk";
	
	public static final String DAY_ABBR = "do";
	
	private static final Logger log = LoggerFactory.getLogger(Util.class);
	
	public static final Random GENERATOR = new Random();
	
	private static final String START_STATE = "start_state";
	private static final String END_STATE = "end_state";
	
	private static ViewEncountersConfig viewEncountersConfig = null;
	private static long lastUpdatedViewEncountersConfig = System.currentTimeMillis();
	private static final long VIEW_ENCOUNTERS_CONFIG_UPDATE_CYCLE = 900000; // fifteen minutes
	private static final String GLOBAL_PROP_VIEW_ENCOUNTERS_CONFIG = "chica.ViewEncountersConfigFile";
	private static DaemonToken daemonToken;
	private static PhysicianNoteConfig physicianNoteConfig = null;
	private static long lastUpdatedPhysicianNoteConfig = System.currentTimeMillis();
	private static final long PHYSICIAN_NOTE_CONFIG_UPDATE_CYCLE = 3600000; // 1 hour
	
	/**
	 * 
	 * @param patient
	 * @param currConcept
	 * @param encounterId
	 * @param value
	 * @param formInstance
	 * @param ruleId
	 * @param locationTagId
	 * @param formFieldId - DWE CHICA-437 the form field id, pass null if the id is not available
	 * @return
	 */
	public static Obs saveObsWithStatistics(Patient patient, Concept currConcept, int encounterId, String value,
	                                        FormInstance formInstance, Integer ruleId, Integer locationTagId, Integer formFieldId) {
		
		String formName = null;
		if (formInstance != null) {
			if (formInstance.getFormId() == null) {
				log.error("Could not find form for statistics update.");
				return null;
			}
			
			FormService formService = Context.getFormService();
			Form form = formService.getForm(formInstance.getFormId());
			formName = form.getName();
			
			boolean usePrintedTimestamp = false;
	        
	        String formType = org.openmrs.module.chirdlutil.util.Util.getFormType(formInstance.getFormId(), locationTagId, formInstance.getLocationId());
	        if (formName != null && ChirdlUtilConstants.PHYSICIAN_FORM_TYPE.equalsIgnoreCase(formType) ){
	            usePrintedTimestamp = true;
	        }
	        return org.openmrs.module.atd.util.Util.saveObsWithStatistics(patient, currConcept, encounterId, value,
	            formInstance, ruleId, locationTagId, usePrintedTimestamp, formFieldId);
		}
		
		return null;
	}
	
	
	/**
	 * This method is duplicated in atd. It is in chica because the chica rules need it
	 * @param concept
	 * @param encounterId
	 * @param formFieldId - DWE CHICA-437 the form field id or null if one is not used
	 */
	public static void voidObsForConcept(Concept concept,Integer encounterId, Integer formFieldId)
	{
		voidObsForConcept(concept,encounterId, formFieldId,"voided due to rescan");
	}
	
	public static void voidObsForConcept(Concept concept,Integer encounterId, Integer formFieldId, String voidReason)
	{
		org.openmrs.module.atd.util.Util.voidObsForConcept(concept, encounterId, formFieldId, voidReason);
	}
	
	public static String sendPage(String message, String pagerNumber) {
		AdministrationService adminService = Context.getAdministrationService();
		String idParam = adminService.getGlobalProperty("chica.pagerUrlNumberParam");
		String textParam = adminService.getGlobalProperty("chica.pagerUrlMessageParam");
		String baseUrl = adminService.getGlobalProperty("chica.pagerBaseURL");
		
		String urlStr = baseUrl;
		BufferedReader rd = null;
		
		try {
			urlStr += "?" + idParam + "=" + URLEncoder.encode(pagerNumber, "UTF-8") + "&" + textParam + "="
			        + URLEncoder.encode(message, "UTF-8");
			if (baseUrl == null || baseUrl.length() == 0 || pagerNumber == null || pagerNumber.length() == 0
			        || message == null || message.length() == 0 || idParam == null || idParam.length() == 0
			        || textParam == null || textParam.length() == 0) {
				log.error("Page was not sent due to null url string or null parameters. URL string: {}", urlStr);
				return "";
			}
			
			URL url = new URL(urlStr);
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; .NET CLR 1.0.3705;)");
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			
			return sb.toString();
		}
		catch (Exception e) {
			log.error("Could not send page: {} to {}.", message, pagerNumber, e);
		}
		finally {
			if (rd != null) {
				try {
					rd.close();
				}
				catch (Exception e) {
					log.error("Error closing the reader.", e);
				}
			}
		}
		
		return "";
	}
	
	/**
	 * Returns patients that have primary forms available for the current authenticated user specified in the server  
	 * configuration file.
	 * 
	 * @param rows List that will be populated with any PatientRow objects found.
	 * @param sessionIdMatch If not null, only patient rows will be returned pertaining to the specified session ID.
	 * @param showAllPatients - true to show all patients for the user's location
	 * @return String containing any error messages encountered during the process.  If null, no errors occurred.
	 * @throws Exception
	 */
	public static String getPatientsWithPrimaryForms(ArrayList<PatientRow> rows, Integer sessionIdMatch, boolean showAllPatients) throws Exception {
		return getPatientsWithForms(rows, sessionIdMatch, PRIMARY_FORM, showAllPatients);
	}
	
	/**
	 * Returns patients that have secondary forms available for the current authenticated user specified in the server  
	 * configuration file.
	 * 
	 * @param rows List that will be populated with any PatientRow objects found.
	 * @param sessionIdMatch If not null, only patient rows will be returned pertaining to the specified session ID.
	 * @return String containing any error messages encountered during the process.  If null, no errors occurred.
	 * @throws Exception
	 */
	public static String getPatientSecondaryForms(ArrayList<PatientRow> rows, Integer sessionIdMatch) throws Exception {
		return getPatientsWithForms(rows, sessionIdMatch, SECONDARY_FORMS, false);
	}
	
	/**
	 * Returns patients that have secondary forms available for the current authenticated user specified in the server  
	 * configuration file.
	 * 
	 * @param rows List that will be populated with any PatientRow objects found.
	 * @param sessionIdMatch If not null, only patient rows will be returned pertaining to the specified session ID.
	 * @param locationId The location identifier
	 * @param locationTagId The location tag identifier
	 * @return String containing any error messages encountered during the process.  If null, no errors occurred.
	 * @throws Exception
	 */
	public static String getPatientSecondaryForms(ArrayList<PatientRow> rows, Integer sessionIdMatch, 
			Integer locationId, Integer locationTagId) throws Exception {
		List<Integer> locationTagIds = new ArrayList<>();
		if (locationTagId != null) {
			locationTagIds.add(locationTagId);
		}
		
		return getPatientsWithForms(rows, sessionIdMatch, SECONDARY_FORMS, false, locationId, locationTagIds, true);
	}
	
	/**
	 * Returns patients that have forms available for the current authenticated user specified in the server configuration 
	 * file.
	 * 
	 * @param rows List that will be populated with any PatientRow objects found.
	 * @param sessionIdMatch If not null, only patient rows will be returned pertaining to the specified session ID.
	 * @param formType Indicating if the forms are primary or secondary
	 * @param showAllPatients - true to show all patients for the user's location
	 * @return String containing any error messages encountered during the process.  If null, no errors occurred.
	 * @throws Exception
	 */
	private static String getPatientsWithForms(ArrayList<PatientRow> rows, Integer sessionIdMatch, int formType, 
			boolean showAllPatients) throws Exception {
		User user = Context.getUserContext().getAuthenticatedUser();
		String locationTags = user.getUserProperty("locationTags");
		String locationString = user.getUserProperty("location");
		ArrayList<Integer> locationTagIds = new ArrayList<>();
		LocationService locationService = Context.getLocationService();
		
		Integer locationId = null;
		Location location = null;
		if (locationString != null) {
			location = locationService.getLocation(locationString);
			if (location != null) {
				locationId = location.getLocationId();
				if(showAllPatients) // DWE CHICA-761 Add all tags to the list
				{
					for (LocationTag tag : location.getTags()) 
					{
						locationTagIds.add(tag.getLocationTagId());	
					}
				}
				else if (locationTags != null) {					
					StringTokenizer tokenizer = new StringTokenizer(locationTags, ",");
					while (tokenizer.hasMoreTokens()) {
						String locationTagName = tokenizer.nextToken();
						locationTagName = locationTagName.trim();
						Set<LocationTag> tags = location.getTags();
						for (LocationTag tag : tags) {
							if (tag.getName().equalsIgnoreCase(locationTagName)) {
								locationTagIds.add(tag.getLocationTagId());
							}
						}
					}
					
				}
			}
		}
		
		return getPatientsWithForms(rows, sessionIdMatch, formType, showAllPatients, locationId, locationTagIds, false);
	}
	
	/**
	 * Returns patients that have forms available for the current authenticated user specified in the server configuration 
	 * file.
	 * 
	 * @param rows List that will be populated with any PatientRow objects found.
	 * @param sessionIdMatch If not null, only patient rows will be returned pertaining to the specified session ID.
	 * @param formType PRIMARY_FORM or SECONDARY_FORM
	 * @param showAllPatients - true to show all patients for the user's location
	 * @param locationId location identifier
	 * @param locationTagIds list of location tag identifiers
	 * @param excludeFinishState if true, forms will not be returned if the FINISHED state exists.  Otherwise, it will 
	 * ignore checking for this state
	 * @return String containing any error messages encountered during the process.  If null, no errors occurred.
	 * @throws Exception
	 */
	private static String getPatientsWithForms(ArrayList<PatientRow> rows, Integer sessionIdMatch, int formType, 
			boolean showAllPatients, Integer locationId, List<Integer> locationTagIds, boolean excludeFinishState) 
					throws Exception {
		User user = Context.getUserContext().getAuthenticatedUser();
		ServerConfig config = org.openmrs.module.chirdlutil.util.Util.getServerConfig();
		if (config == null) {
			log.error("Server config file could not be loaded.  No patients will be returned.");
			return "Could not find server config file.";
		}
		
		MobileClients mobileClientsConfig = config.getMobileClients();
		if (mobileClientsConfig == null) {
			log.error("Server config contains no entry for mobileClients.  No patients will be returned.");
			return null;
		}
		
		List<MobileClient> mobileClients = mobileClientsConfig.getMobileClients();
		if (mobileClients == null || mobileClients.size() == 0) {
			log.error("Server config contains no entry for mobileClients with mobileClient elements. No patients will be returned.");
			return null;
		}
		
		String username = user.getUsername();
		MobileClient userClient = config.getMobileClient(username);
		if (userClient == null) {
			log.error("Server config contains no mobile clients for username: {}.",  username);
			return null;
		}
		
		ChirdlUtilBackportsService chirdlUtilBackportsService = Context.getService(ChirdlUtilBackportsService.class);
		EncounterService encounterService = Context.getEncounterService();
		
		Calendar todaysDate = Calendar.getInstance();
		
		todaysDate.set(Calendar.HOUR_OF_DAY, 0);
		todaysDate.set(Calendar.MINUTE, 0);
		todaysDate.set(Calendar.SECOND, 0);
		
		List<PatientState> unfinishedStates = new ArrayList<>();
		if(showAllPatients) // DWE CHICA-761 Get unfinished patient states for all patients by location so that all patients registered to the location can be displayed
		{
			try
			{
				// NOTE: This requires that the same program is being used for all location tags for a location
				// Performance benefits to this were minimal, if it becomes a problem where not all tags are using the same
				// program, we can switch back to the old behavior if needed
				Program program = chirdlUtilBackportsService.getProgramByLocation(locationId); 
				List<PatientState> currUnfinishedStates = chirdlUtilBackportsService.getLastPatientStateAllPatientsByLocation(
						todaysDate.getTime(), program.getProgramId(), program.getStartState().getName(), locationId);
					if (currUnfinishedStates != null) 
					{
						unfinishedStates.addAll(currUnfinishedStates);
					}
			}
			catch(Exception e)
			{
				log.error("Error occurred while generating a list of all patients for location (locationId: {}.", locationId, e);
				return null;
			}	
		}
		else
		{
			for (Integer locationTagId : locationTagIds) {
				if (sessionIdMatch != null) {
					PatientState patientState = chirdlUtilBackportsService.getLastPatientState(sessionIdMatch);
					if (patientState != null) {
						unfinishedStates.add(patientState);
						break;
					}
				} else {
					Program program = chirdlUtilBackportsService.getProgram(locationTagId, locationId);
					List<PatientState> currUnfinishedStates = chirdlUtilBackportsService.getLastPatientStateAllPatients(
						todaysDate.getTime(), program.getProgramId(), program.getStartState().getName(), locationTagId, locationId);
					if (currUnfinishedStates != null) {
						unfinishedStates.addAll(currUnfinishedStates);
					}
				}
			}
		}
		
		// DWE CHICA-761 Moved this block out of the loop below
		List<MobileForm> mobileFormsList = new ArrayList<>();
		switch(formType) {
		case PRIMARY_FORM:
			MobileForm primaryForm = config.getPrimaryForm(username);
			if (primaryForm != null) {
				mobileFormsList.add(primaryForm);
			}

			break;
		case SECONDARY_FORMS:
			List<MobileForm> mobileForms = config.getSecondaryForms(username);
			mobileFormsList.addAll(mobileForms);

			break;
		default:
		    break;
		}
		
		// DWE CHICA-761 Create a hashmap of mobile form start/end states so we don't query for them in the loop
		// Also create a map of forms so we don't have to query for them below
		FormService formService = Context.getFormService();
		Map<String, Form> formMap = new HashMap<>();
		Map<String, HashMap<String, State>> mobileFormStartEndStateMap = new HashMap<>();
		Map<String, State> stateNameToStateMap = new HashMap<>();
		for (MobileForm mobileForm : mobileFormsList)
		{
			Form form = formService.getForm(mobileForm.getName());
			if(form == null)
			{
				continue;
			}
			formMap.put(mobileForm.getName(), form);
			HashMap<String, State> startEndStateMap = new HashMap<>();
			String startStateName = mobileForm.getStartState();
			String endStateName = mobileForm.getEndState();
			State startState = stateNameToStateMap.get(startStateName);
			if (startState == null) {
				startState = chirdlUtilBackportsService.getStateByName(startStateName);
				stateNameToStateMap.put(startStateName, startState);
			}
			
			State endState = stateNameToStateMap.get(endStateName);
			if (endState == null) {
				endState = chirdlUtilBackportsService.getStateByName(endStateName);
				stateNameToStateMap.put(endStateName, endState);
			}
			
			startEndStateMap.put(START_STATE, startState);
			startEndStateMap.put(END_STATE, endState);
			mobileFormStartEndStateMap.put(mobileForm.getName(), startEndStateMap);	
		}
		
		stateNameToStateMap.clear();
		Map<String, PatientRow> patientEncounterRowMap = new HashMap<>();
		Map<Integer, Encounter> encounterMap = new HashMap<>();
		Map<Integer, Session> sessionMap = new HashMap<>();
		DecimalFormat decimalFormat = new DecimalFormat("#.#");
		Double maxWeight = userClient.getMaxSecondaryFormWeight();
		State checkoutState = null;
		if (!excludeFinishState) {
			String checkoutStateString = Context.getAdministrationService().getGlobalProperty(
				ChirdlUtilConstants.GLOBAL_PROP_GREASEBOARD_CHECKOUT_STATE);
			checkoutState = chirdlUtilBackportsService.getStateByName(checkoutStateString);
		}
		
		for (PatientState currState : unfinishedStates) {
			boolean addedForm = false;
			Integer sessionId = currState.getSessionId();
			if ((sessionIdMatch != null && !sessionIdMatch.equals(sessionId))) {
				continue;
			}
			
			// CHICA-1143 Make sure the patient isn't already in the finished/checkout state (PWS submitted)
			// This is how the desktop greaseboard behaves
			if (!excludeFinishState 
					&& !chirdlUtilBackportsService.getPatientStateBySessionState(
						sessionId, checkoutState.getStateId()).isEmpty()) {
				continue;
			}
			
			Integer patientId = currState.getPatientId();
			Session session = sessionMap.get(sessionId);
			if (session == null) {
			    session = chirdlUtilBackportsService.getSession(sessionId);
			    sessionMap.put(sessionId, session);
			}
			
			Integer encounterId = session.getEncounterId();
			Map<Integer, List<PatientState>> formPatientStateCreateMap = new HashMap<>();
			Map<Integer, List<PatientState>> formPatientStateProcessMap = new HashMap<>();
			PatientRow row = patientEncounterRowMap.get(patientId + "_" + encounterId);
			if (row == null) {
				row = new PatientRow();
				patientEncounterRowMap.put(patientId + "_" + encounterId, row);
			}
			
			Double accumWeight = 0.0d;
			Map<Integer, Double> formWeightMap = new HashMap<>();
			Set<Integer> completedFormIds = new HashSet<>();
			for (MobileForm mobileForm : mobileFormsList) {
				State startState;
				State endState;
				HashMap<String, State> startEndStateMap = mobileFormStartEndStateMap.get(mobileForm.getName());
				if(startEndStateMap == null)
				{
					continue;
				}
					
				startState = startEndStateMap.get(START_STATE);
				endState = startEndStateMap.get(END_STATE);
				if(startState == null || endState == null)
				{
					continue;
				}
				
				getPatientStatesByEncounterId(chirdlUtilBackportsService, formPatientStateCreateMap, encounterId,
				    startState.getStateId(), true);
				getPatientStatesByEncounterId(chirdlUtilBackportsService, formPatientStateProcessMap, encounterId,
				    endState.getStateId(), false);
				
				Form form = formMap.get(mobileForm.getName()); // DWE CHICA-761 Changed to pull from the map instead of querying
				if (form == null) {
					continue;
				}
				
				Integer formId = form.getFormId();
				boolean containsStartState = formPatientStateCreateMap.containsKey(formId);
				boolean containsEndState = formPatientStateProcessMap.containsKey(formId);
				
				Double formWeight = mobileForm.getWeight();
				formWeightMap.put(formId, formWeight);
				
				if (containsStartState) {
					List<PatientState> patientStates = null;
					if (!containsEndState) {
						patientStates = formPatientStateCreateMap.get(formId);
						if (patientStates != null) {
							for (PatientState patientState : patientStates) {
								FormInstance formInstance = patientState.getFormInstance();
								if (formInstance != null) {
									row.addFormInstance(formInstance);
									addedForm = true;
									break;
								}
							}
						}
					} else {
						patientStates = formPatientStateProcessMap.get(formId);
						if (patientStates != null) {
							int initialCompletedFormsSize = completedFormIds.size();
							for (PatientState patientState : patientStates) {
								if (patientState.getEndTime() == null) {
									FormInstance formInstance = patientState.getFormInstance();
									if (formInstance != null) {
										row.addFormInstance(formInstance);
										addedForm = true;
										break;
									}
								} else {
									completedFormIds.add(formId);
								}
							}
							
							// Only add weight for forms that have already been completed
							if ((completedFormIds.size() > initialCompletedFormsSize) && (!addedForm) && (formWeight != null)) {
								accumWeight += formWeight;
								accumWeight = Double.parseDouble(decimalFormat.format(accumWeight));
							}
						}
					}
				}
			}
			
			formPatientStateCreateMap.clear();
			formPatientStateProcessMap.clear();
			if (!addedForm || row.getFormInstances() == null || row.getFormInstances().size() == 0) {
				continue;
			}
			
			if (accumWeight >= maxWeight) {
				// We can't display any more secondary forms
				row.getFormInstances().clear();
				continue;
			}
			
			// Filter out forms based on weight
			Set<FormInstance> formInstances = new LinkedHashSet<>(row.getFormInstances());
			row.getFormInstances().clear();
			Iterator<FormInstance> iter = formInstances.iterator();
			while (iter.hasNext() && accumWeight < maxWeight) {
				FormInstance formInstance = iter.next();
				Integer formId = formInstance.getFormId();
				Double formWeight = formWeightMap.get(formId);
				// If a version of the form has already been completed, we do not want to add it 
				// to the accumulated weight because it's already been accounted for.
				if (formWeight != null && !completedFormIds.contains(formId)) {
					Double newWeight = accumWeight + formWeight;
					newWeight = Double.parseDouble(decimalFormat.format(newWeight));
					if (newWeight > maxWeight) {
						// Break out of the loop.  We want to stop even though there may be a form with a 
						// lower weight after this one.  It wouldn't make sense to display a form with 
						// a lower priority if the one before it is filtered out due to weight.
						break;
					} else {
						accumWeight = newWeight;
					}
				}
				
				row.addFormInstance(formInstance);
			}
			
			formWeightMap.clear();
			completedFormIds.clear();
			
			Patient patient = currState.getPatient();
			String lastName = org.openmrs.module.chirdlutil.util.Util.toProperCase(patient.getFamilyName());
			String firstName = org.openmrs.module.chirdlutil.util.Util.toProperCase(patient.getGivenName());
			
			// DWE CHICA-761 Replaced call to formatting rules with util methods to improve performance
			String mrn = org.openmrs.module.chirdlutil.util.Util.formatMRN(patient);
			
			// DWE CHICA-884 Get patient age. This will be used to determine if the confidentiality pop-up should be
			// displayed for patients >= 12 years old
			Integer ageInYears = org.openmrs.module.chirdlutil.util.Util.getAgeInUnits(patient.getBirthdate(), Calendar.getInstance().getTime(), YEAR_ABBR);
						
			Encounter encounter = encounterMap.get(encounterId);
			if (encounter == null) {
			    encounter = encounterService.getEncounter(encounterId);
			    encounterMap.put(encounterId, encounter);
			}
			
			Date encounterDate = null;
			if (encounter != null) {
				row.setEncounter(encounter);
					
				encounterDate = encounter.getEncounterDatetime();
				if (encounterDate != null && !org.openmrs.module.chirdlutil.util.Util.isToday(encounterDate)) {
					continue;
				}
			}
		
			row.setFirstName(firstName);
			row.setLastName(lastName);
			
			row.setMrn(mrn);
			row.setPatientId(patient.getPatientId());
			row.setSessionId(sessionId);
			row.setAgeInYears(ageInYears);
			
			rows.add(row);
		}
		
		patientEncounterRowMap.clear();
		encounterMap.clear();
		sessionMap.clear();
		//sort arraylist by encounterDatetime
		Collections.sort(rows, new PatientRowComparator());
		return null;
	}
	
	/**
	 * Gets patient states by encounter ID.
	 * 
	 * @param chirdlUtilBackportsService Service used to retrieve patient states.
	 * @param formIdToPatientStateMap Map that will be populated with the patient state information.
	 * @param encounterId The encounter ID.
	 * @param stateId The state ID.
	 */
	public static void getPatientStatesByEncounterId(ChirdlUtilBackportsService chirdlUtilBackportsService,
	                                           Map<Integer, List<PatientState>> formIdToPatientStateMap,
	                                           Integer encounterId, Integer stateId, boolean requireFinishedEndState) {
		List<PatientState> patientStates = chirdlUtilBackportsService.getPatientStateByEncounterState(encounterId, stateId);
		for (PatientState patientState : patientStates) {
			// Only add if the state has been ended
			if (requireFinishedEndState && patientState.getEndTime() == null) {
				continue;
			}
			
			Integer formId = patientState.getFormId();
			List<PatientState> foundStates = formIdToPatientStateMap.get(formId);
			if (foundStates == null) {
				foundStates = new ArrayList<>();
			}
			
			foundStates.add(patientState);
			formIdToPatientStateMap.put(formId, foundStates);
		}
	}
	
	public static void calculatePercentiles(Integer encounterId, Patient patient, Integer locationTagId) {
		EncounterService encounterService = Context.getEncounterService();
		Encounter encounter = encounterService.getEncounter(encounterId);
		ATDService atdService = Context.getService(ATDService.class);
		List<Encounter> encounters = new ArrayList<>();
		encounters.add(encounter);
		List<Concept> questions = new ArrayList<>();
		HashMap<String, Object> parameters = new HashMap<>();
		Calculator calculator = new Calculator();
		parameters.put(ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID, encounterId);
		ConceptService conceptService = Context.getConceptService();
		Concept concept = conceptService.getConcept("BMICentile");
		questions.add(concept);
		
		Result result = atdService.evaluateRule("bmi", patient, parameters);
		
		if (!(result instanceof EmptyResult)) {
			Double percentile = calculator.calculatePercentile(result.toNumber(), patient.getGender(),
			    patient.getBirthdate(), "bmi", null);
			if (percentile != null) {
				percentile = org.openmrs.module.chirdlutil.util.Util.round(percentile, 2); // round percentile to two places
				
				org.openmrs.module.chica.util.Util.voidObsForConcept(concept, encounterId, null); // DWE CHICA-437 Added formFieldId parameter - intentionally null here
				org.openmrs.module.chirdlutil.util.Util.saveObs(patient, concept, encounterId, percentile.toString(),
				    new Date());
			}
		}
		
		questions = new ArrayList<>();
		concept = conceptService.getConcept("HCCentile");
		questions.add(concept);
		
		parameters.put(ChirdlUtilConstants.PARAMETER_CONCEPT, "HC");
		result = atdService.evaluateRule(ChicaConstants.RULE_NAME_CONCEPT, patient, parameters);
		if (!(result instanceof EmptyResult)) {
			Double percentile = calculator.calculatePercentile(result.toNumber(), patient.getGender(),
			    patient.getBirthdate(), "hc", null);
			if (percentile != null) {
				percentile = org.openmrs.module.chirdlutil.util.Util.round(percentile, 2); // round percentile to two places
				org.openmrs.module.chica.util.Util.voidObsForConcept(concept, encounterId, null); // DWE CHICA-437 Added formFieldId parameter - intentionally null here
				org.openmrs.module.chirdlutil.util.Util.saveObs(patient, concept, encounterId, percentile.toString(),
				    new Date());
			}
		}
		
		questions = new ArrayList<>();
		concept = conceptService.getConcept("HtCentile");
		questions.add(concept);
		
		parameters.put(ChirdlUtilConstants.PARAMETER_CONCEPT, "HEIGHT");
		result = atdService.evaluateRule(ChicaConstants.RULE_NAME_CONCEPT, patient, parameters);
		if (!(result instanceof EmptyResult)) {
			Double percentile = calculator.calculatePercentile(result.toNumber(), patient.getGender(),
			    patient.getBirthdate(), "length", org.openmrs.module.chirdlutil.util.Util.MEASUREMENT_IN);
			if (percentile != null) {
				percentile = org.openmrs.module.chirdlutil.util.Util.round(percentile, 2); // round percentile to two places
				org.openmrs.module.chica.util.Util.voidObsForConcept(concept, encounterId, null); // DWE CHICA-437 Added formFieldId parameter - intentionally null here
				org.openmrs.module.chirdlutil.util.Util.saveObs(patient, concept, encounterId, percentile.toString(),
				    new Date());
			}
		}
		
		questions = new ArrayList<>();
		concept = conceptService.getConcept("WtCentile");
		questions.add(concept);
		
		parameters.put(ChirdlUtilConstants.PARAMETER_CONCEPT, "WEIGHT");
		result = atdService.evaluateRule(ChicaConstants.RULE_NAME_CONCEPT, patient, parameters);
		if (!(result instanceof EmptyResult)) {
			Double percentile = calculator.calculatePercentile(result.toNumber(), patient.getGender(),
			    patient.getBirthdate(), "weight", org.openmrs.module.chirdlutil.util.Util.MEASUREMENT_LB);
			if (percentile != null) {
				percentile = org.openmrs.module.chirdlutil.util.Util.round(percentile, 2); // round percentile to two places
				org.openmrs.module.chica.util.Util.voidObsForConcept(concept, encounterId, null); // DWE CHICA-437 Added formFieldId parameter - intentionally null here
				org.openmrs.module.chirdlutil.util.Util.saveObs(patient, concept, encounterId, percentile.toString(),
				    new Date());
			}
		}
		
		//save BP
		questions = new ArrayList<>();
		concept = conceptService.getConcept("BP");
		questions.add(concept);
		
		result = atdService.evaluateRule("bp", patient, parameters);
		if (!(result instanceof EmptyResult)) {
			org.openmrs.module.chica.util.Util.voidObsForConcept(concept, encounterId, null); // DWE CHICA-437 Added formFieldId parameter - intentionally null here
			org.openmrs.module.chirdlutil.util.Util.saveObs(patient, concept, encounterId, result.toString(), new Date());
		}
		
		//save BMI
		questions = new ArrayList<>();
		concept = conceptService.getConcept("BMI CHICA");
		questions.add(concept);
		
		result = atdService.evaluateRule("bmi", patient, parameters);
		if (!(result instanceof EmptyResult)) {
			org.openmrs.module.chica.util.Util.voidObsForConcept(concept, encounterId, null); // DWE CHICA-437 Added formFieldId parameter - intentionally null here
			org.openmrs.module.chirdlutil.util.Util.saveObs(patient, concept, encounterId, result.toString(), new Date());
		}
		
	}
    
    /**
	 * Gets the location tag id from the encounter.
	 * @param encounter
	 * @return
	 */
	public static Integer getLocationTagId(Encounter encounter) {
		if (encounter != null) {
			// lookup location tag id that matches printer location
			ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
			
			EncounterAttributeValue printerEncounterAttributeValue  = chirdlutilbackportsService.
					getEncounterAttributeValueByName( encounter.getEncounterId(), ChirdlUtilConstants.ENCOUNTER_ATTRIBUTE_PRINTER_LOCATION);
			
			if (printerEncounterAttributeValue == null) {
				log.error("Encounter attribute value for {} does not exist for encounter id: {}.", 
						ChirdlUtilConstants.ENCOUNTER_ATTRIBUTE_PRINTER_LOCATION, encounter.getEncounterId());
				return null;
			}
		
			String printerLocation = printerEncounterAttributeValue.getValueText();
			
			if (printerLocation == null) {
				log.error("Encounter attribute value for {} has no value for printer location. Encounter id: {}.", 
						ChirdlUtilConstants.ENCOUNTER_ATTRIBUTE_PRINTER_LOCATION, encounter.getEncounterId());
				return null;
			}
			
			Location location = encounter.getLocation();
			Set<LocationTag> tags = location.getTags();
			
			if (tags != null) {
				for (LocationTag tag : tags) {
					if (tag.getName().equalsIgnoreCase(printerLocation)) {
						return tag.getLocationTagId();
					}
				}
			}
			
			
		}
		return null;
	}
	/**
	 * Retrieves the last encounter for a patient or null if one does not exist.
	 * @param patient The patient used to find the encounter.
	 * @return Encounter object or null if one does not exist.
	 */
    public static org.openmrs.Encounter getLastEncounter(Patient patient) {
    	// Get last encounter with last day
		Calendar startCal = Calendar.getInstance();
		startCal.set(GregorianCalendar.DAY_OF_MONTH, startCal.get(GregorianCalendar.DAY_OF_MONTH) - 3);
		Date startDate = startCal.getTime();
		Date endDate = Calendar.getInstance().getTime();
		List<org.openmrs.Encounter> encounters = Context.getEncounterService().getEncounters(patient, null, startDate, endDate, null, 
				null, null, null, null, false); // CHICA-1151 Add null parameters for Collection<VisitType> and Collection<Visit>
		if (encounters == null || encounters.isEmpty()) {
			return null;
		} else if (encounters.size() == 1) {
			return encounters.get(0);
		}
		
		// Do a check to find the latest encounters with observations with a scanned timestamp for the PSF.
		ATDService atdService = Context.getService(ATDService.class);
		for (int i = encounters.size() - 1; i >= 0; i--) {
			org.openmrs.Encounter encounter = encounters.get(i);
			// Passing the enounterId instead of encounter to prevent possible ClassCastException while iterating through list of encounters.
			String patientForm = org.openmrs.module.chica.util.Util.getPrimaryFormNameByLocationTag(encounter.getEncounterId(), ChirdlUtilConstants.LOC_TAG_ATTR_PRIMARY_PATIENT_FORM);
			List<Statistics> stats = atdService.getStatsByEncounterForm(encounter.getEncounterId(), patientForm);
			if (stats == null || stats.isEmpty()) {
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
    
    /**
     * Validating encounterIds
     * @param encounterId The encounter identifier
     * @param result
     * @return
     */
    public static boolean equalEncounters(Integer encounterId, Result result) {
    	if (encounterId == null || result == null) {
    		return false;
    	}
    	
    	Result latestResult = result.latest();
    	if (latestResult == null) {
    		return false;
    	}
    	
    	if (!(latestResult.getResultObject() instanceof Obs)) {
    		return false;
    	}
    	
    	Obs obs = (Obs)latestResult.getResultObject();
    	if (obs == null) {
    		return false;
    	}
    	
    	org.openmrs.Encounter obsEncounter = obs.getEncounter();
    	if (obsEncounter == null) {
    		return false;
    	}
    	
    	if (encounterId.equals(obsEncounter.getEncounterId())) {
    		return true;
    	}
    	
    	return false;
    }
	
	/**
	 * Returns the Location Tag ID
	 * 
	 * @param encounterIdString encounter ID
	 * @return LocationTagId
	 */
	public static Integer getLocationTagId(Integer encounterId){
		
		String printerLocation = null;
		Integer locationTagId = null;

		if (encounterId != null)
		{
			EncounterService encounterService = Context.getEncounterService();
			Encounter encounter = encounterService.getEncounter(encounterId);
			ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);

			if (encounter != null)
			{
				// see if the encounter has a printer location
				// this will give us the location tag id
				EncounterAttributeValue encounterAttributeValue =  chirdlutilbackportsService
						.getEncounterAttributeValueByName( encounter.getEncounterId(), ChirdlUtilConstants.ENCOUNTER_ATTRIBUTE_PRINTER_LOCATION);	
				
				if (encounterAttributeValue != null) {
					printerLocation = encounterAttributeValue.getValueText();
				} else{
					log.error("Encounter attribute {} does not exist for encounter id: {} ",
							ChirdlUtilConstants.ENCOUNTER_ATTRIBUTE_PRINTER_LOCATION, encounter.getEncounterId());
				}

				// if the printer location is null, pick
				// any location tag id for the given location
				if (printerLocation == null)
				{
					Location location = encounter.getLocation();
					if (location != null)
					{
						Set<LocationTag> tags = location.getTags();

						if (tags != null && !tags.isEmpty())
						{
							printerLocation = ((LocationTag) tags.toArray()[0])
									.getName(); // CHICA-1151 replace getTag() with getName()
						}
					}
				}
				
				if (printerLocation != null)
				{
					LocationService locationService = Context
							.getLocationService();
					LocationTag tag = locationService
							.getLocationTagByName(printerLocation);
					if (tag != null)
					{
						locationTagId = tag.getLocationTagId();
					}
				}
			}
		}
	return locationTagId;
	}

	/**
	 * Displays the Stylesheet xsl 
	 * 
	 * @param formId The ID of the general form.
	 * @param locationTagId The location tag ID. 
	 * @param locationId The clinic location ID.
	 * @param formInstanceId The ID of the instance of the general form.
	 * @param strStylesheet The stylesheet file used for the transformation.
	 * @param fileLocationFormAttributeName String used to locate the file for the stylesheet 
	 * transformation (XMLUtil.DEFAULT_EXPORT_DIRECTORY or XMLUtil.DEFAULT_MERGE_DIRECTORY)
	 * @return strOutput returns the result of the transformation File.
	 */
	public static String displayStylesheet(Integer formId, Integer locationTagId, Integer locationId, Integer formInstanceId, 
	                                       String strStylesheet, String fileLocationFormAttributeName){

		ChirdlUtilBackportsService service = Context.getService(ChirdlUtilBackportsService.class);
		String strOutput = null;
		File stylesheetFile = null;
		File XmlFile = XMLUtil.getXmlFile(locationId, formId, formInstanceId, fileLocationFormAttributeName);
		FormAttributeValue formAttributeValue = service.getFormAttributeValue(formId, ChirdlUtilConstants.FORM_ATTR_STYLESHEET, locationTagId, locationId);
		if (formAttributeValue != null && formAttributeValue.getValue() != null && !formAttributeValue.getValue().isEmpty()) {
			try{
				stylesheetFile = new File(formAttributeValue.getValue());
			}catch (Exception e){
				log.error("The file path in the form attribute is not defined correctly for form id: {} formInstanceId: {} location id: {} location tag id: {} ",
						formId, formInstanceId, locationId, locationTagId, e);
			}				
		} else {
			stylesheetFile = XMLUtil.findStylesheet(strStylesheet);
		}
		if (stylesheetFile == null) {
			log.error("Error finding stylesheet to format the form: {}.", strStylesheet);
		}
		
		if (XmlFile != null && stylesheetFile != null) {
			try {
				strOutput = XMLUtil.transformFile(XmlFile, stylesheetFile);
			} catch (Exception e) {
				log.error("Error transforming xml: {} xslt: {}.", XmlFile.getAbsolutePath(), stylesheetFile.getAbsolutePath(),  e);
			}
		}
		return strOutput;
	}
	
	/**
	 * Gets the time frame for form from global property settings
	 * 
	 * @return formTimeLimit returns form time limit.
	 */
	public static Integer getFormTimeLimit() {
		Integer formTimeLimit = null;
		String formTimeLimitStr = Context.getAdministrationService().getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_FORM_TIME_LIMIT);
		if (formTimeLimitStr == null || formTimeLimitStr.trim().length() == 0) {
			log.error("No value set for global property: {}. A default of 2 days will be used.", ChirdlUtilConstants.GLOBAL_PROP_FORM_TIME_LIMIT);
			formTimeLimitStr = "2";
		}
		try {
			formTimeLimit = Integer.parseInt(formTimeLimitStr);
		} catch (NumberFormatException e) {
			log.error("Invalid number format for global property {}. A default of 2 days will be used.", ChirdlUtilConstants.GLOBAL_PROP_FORM_TIME_LIMIT, e);
			formTimeLimit = 2;
		}
		return formTimeLimit;
	}
	
	/**
	 * Store the HL7 message in the sockethl7listener_hl7_out_queue table
	 * so that it can be sent by the scheduled task
	 * 
	 * @param message
	 * @param encounterId
	 * @param host
	 * @param port
	 * @throws Exception
	 */
	public static void createHL7OutboundRecord(String message, Integer encounterId, String host, Integer port) throws Exception
	{
		EncounterService encounterService = Context.getEncounterService();
			Encounter openmrsEncounter = encounterService.getEncounter(encounterId);
			SocketHL7ListenerService socketHL7ListenerService = Context.getService(SocketHL7ListenerService.class);
			
			if(openmrsEncounter == null)
			{
				log.error("Error creating HL7Outbound record. Unable to locate encounterId: {}.", encounterId);
				return;
			}
			
			HL7Outbound hl7Outbound = new HL7Outbound();
			hl7Outbound.setHl7Message(message);
			hl7Outbound.setEncounter(openmrsEncounter);
			hl7Outbound.setAckReceived(null);
			hl7Outbound.setPort(port);
			hl7Outbound.setHost(host); 
			
			socketHL7ListenerService.saveMessageToDatabase(hl7Outbound);
	}
	
	/**
	 * Gets the primary form name based on location tag attribute (primaryPatientForm or primaryPhysicianForm)
	 * @param encounter
	 * @param attributeName Location Tag Attribute
	 * @return form form name
	 */
	public static String getPrimaryFormNameByLocationTag(Encounter encounter, String attributeName)
	{
		Integer locationId = encounter.getLocation().getLocationId();
		Integer locationTagId = getLocationTagId(encounter);
		
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		LocationTagAttributeValue locationTagAttributeValueForm = chirdlutilbackportsService.getLocationTagAttributeValue(locationTagId, 
				attributeName, locationId); 
		
		String formName = null;
     	if (locationTagAttributeValueForm != null && StringUtils.isNotBlank(locationTagAttributeValueForm.getValue())) {
     		formName = locationTagAttributeValueForm.getValue(); 
     	}
     	return formName;
	}
	
	/**
	 * Gets the primary form name based on location tag attribute (primaryPatientForm or primaryPhysicianForm)
	 * @param encounterId
	 * @param attributeName Location Tag Attribute
	 * @return form form name
	 */
	public static String getPrimaryFormNameByLocationTag(Integer encounterId, String attributeName)
	{
		EncounterService encounterService = Context.getEncounterService();
		Encounter encounter = encounterService.getEncounter(encounterId);
		
		return getPrimaryFormNameByLocationTag(encounter, attributeName);
	}
	
	/**
	 * Gets the primary form name based on the option that is selected in the drop-down
	 * @param encounter
	 * @param printOptionString option selected in the GreaseBoard action drop-down
	 * @return form Name
	 */
	public static String getFormNameByPrintOptionString(Encounter encounter, String printOptionString)
	{
		String locTagAttrName = null;
		if (printOptionString.equalsIgnoreCase(ChirdlUtilConstants.OPTION_PRINT_PATIENT_FORM)) {
 			locTagAttrName = ChirdlUtilConstants.LOC_TAG_ATTR_PRIMARY_PATIENT_FORM;
 		} else if (printOptionString.equalsIgnoreCase(ChirdlUtilConstants.OPTION_PRINT_PHYSICIAN_FORM)) {
 			locTagAttrName = ChirdlUtilConstants.LOC_TAG_ATTR_PRIMARY_PHYSICIAN_FORM;
 		}
		return getPrimaryFormNameByLocationTag(encounter, locTagAttrName);
	}
	
	/**
	 * Gets the primary form name based on the option that is selected in the drop-down
	 * @param encounterId
	 * @param printOptionString option selected in the GreaseBoard action drop-down
	 * @return form Name
	 */
	public static String getFormNameByPrintOptionString(Integer encounterId, String printOptionString)
	{
		EncounterService encounterService = Context.getEncounterService();
		Encounter encounter = encounterService.getEncounter(encounterId);
		
		return getFormNameByPrintOptionString(encounter, printOptionString);
	}
	
	/**
	 * Gets the form attribute for startState using the formId
	 * @param encounter
	 * @param formId
	 * @return Start State name
	 */
	public static String getStartStateName(Encounter encounter, Integer formId)
	{
		Integer locationId = encounter.getLocation().getLocationId();
		Integer locationTagId = getLocationTagId(encounter);
		
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		FormAttributeValue formAttributeValueStartStateName = null;
		if (formId != null && locationId != null && locationTagId != null) {
			formAttributeValueStartStateName = chirdlutilbackportsService.getFormAttributeValue(formId, ChirdlUtilConstants.FORM_ATTRIBUTE_START_STATE, 
					locationTagId, locationId);
		}
		String startStateName = null;
		if (formAttributeValueStartStateName != null && StringUtils.isNotBlank(formAttributeValueStartStateName.getValue())) {
			startStateName = formAttributeValueStartStateName.getValue();
		}
		
		return startStateName;
	}
	
	/**
	 * Gets the form attribute for startState using the formId
	 * @param encounterId
	 * @param formId
	 * @return start State name
	 */
	public static String getStartStateName(Integer encounterId, Integer formId)
	{
		EncounterService encounterService = Context.getEncounterService();
		Encounter encounter = encounterService.getEncounter(encounterId);
		
		return getStartStateName(encounter, formId);
	}
	
	/**
	 * Gets the form attribute for reprintState using the formId
	 * @param encounter
	 * @param formId
	 * @return reprint State name
	 */
	public static String getReprintStateName(Encounter encounter, Integer formId)
	{
		Integer locationId = encounter.getLocation().getLocationId();
		Integer locationTagId = getLocationTagId(encounter);
		
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		FormAttributeValue formAttributeValueReprintStateName = null;
		if (formId != null && locationId != null && locationTagId != null) {
			formAttributeValueReprintStateName = chirdlutilbackportsService.getFormAttributeValue(formId, ChirdlUtilConstants.FORM_ATTRIBUTE_REPRINT_STATE, 
					locationTagId, locationId);
		}
		String reprintStateName = null;
		if (formAttributeValueReprintStateName != null && StringUtils.isNotBlank(formAttributeValueReprintStateName.getValue())) {
			reprintStateName = formAttributeValueReprintStateName.getValue();
		}
		
		return reprintStateName;
	}
	
	/**
	 * Gets the form attribute for reprintState using the formId
	 * @param encounterId
	 * @param formId
	 * @return reprint State name
	 */
	public static String getReprintStateName(Integer encounterId, Integer formId)
	{
		EncounterService encounterService = Context.getEncounterService();
		Encounter encounter = encounterService.getEncounter(encounterId);
		
		return getReprintStateName(encounter, formId);
	}
		
	/**
	 * CHICA-1125
	 * Returns the view encounters configuration.
	 * 
	 * @return ViewEncountersConfig object.
	 * @throws JiBXException
	 * @throws FileNotFoundException
	 */
	public static ViewEncountersConfig getViewEncountersConfig() throws JiBXException, FileNotFoundException {
		long currentTime = System.currentTimeMillis();
    	if (viewEncountersConfig == null || (currentTime - lastUpdatedViewEncountersConfig) > VIEW_ENCOUNTERS_CONFIG_UPDATE_CYCLE) {
    		lastUpdatedViewEncountersConfig = currentTime;
			String configFileStr = Context.getAdministrationService().getGlobalProperty(GLOBAL_PROP_VIEW_ENCOUNTERS_CONFIG);
			if (configFileStr == null) {
				log.error("You must set a value for global property: {}",  GLOBAL_PROP_VIEW_ENCOUNTERS_CONFIG);
				return null;
			}
			
			File configFile = new File(configFileStr);
			if (!configFile.exists()) {
				log.error("The file location specified for the global property {} does not exist. ", GLOBAL_PROP_VIEW_ENCOUNTERS_CONFIG);
				return null;
			}
			
			IBindingFactory bfact = BindingDirectory.getFactory(ViewEncountersConfig.class);
			IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
			viewEncountersConfig = (ViewEncountersConfig)uctx.unmarshalDocument(new FileInputStream(configFile), null);
    	}
    	
    	return viewEncountersConfig;
	}
	
	/**
	 * CHICA-1125
	 * Utility method to get the FormsToDisplay section of the ViewEncountersConfig file
	 * 
	 * @return FormsToDisplay object
	 */
	public static FormsToDisplay getViewEncountersFormsToDisplayConfig()
	{
		FormsToDisplay formsToDisplayConfig = null;
		try
		{
			ViewEncountersConfig config = getViewEncountersConfig();
			if (config == null) 
			{
				log.error("View Encounters Config file could not be loaded.");
				return null;
			}
			
			formsToDisplayConfig = config.getFormsToDisplay();	
		}
		catch(Exception e)
		{
			log.error("View Encounters Config file could not be loaded.", e);
			return null;
		}
		return formsToDisplayConfig;
	}
	
	/**
	 * Retrieves the most recent encounters as a List within the past configured amount of days
	 * @param patient Patient object
	 * @return Encounter List or null if one is not found
	 */
	public static List<org.openmrs.Encounter> getEncounterList(Patient patient) {
		Calendar startCal = Calendar.getInstance();
		startCal.set(Calendar.DAY_OF_MONTH, startCal.get(Calendar.DAY_OF_MONTH) - getFormTimeLimit().intValue());
		Date startDate = startCal.getTime();
		Date endDate = Calendar.getInstance().getTime();
		EncounterSearchCriteria searchCriteria = 
				new EncounterSearchCriteria(patient, null, startDate, endDate, null, null, null, null, null, null, false);
		return Context.getEncounterService().getEncounters(searchCriteria);
   }
	
	/**
	 * Retrieves the primary physician form name configured for the provided
	 * location and location tag.
	 * 
	 * @param locationId    The location identifier
	 * @param locationTagId The location tag identifier
	 * @return The name of the primary physician form
	 */
	public static String getPrimaryPhysicianFormName(Integer locationId, Integer locationTagId) {
		return getPrimaryFormName(locationId, locationTagId, ChirdlUtilConstants.LOC_TAG_ATTR_PRIMARY_PHYSICIAN_FORM);
	}
	
	/**
	 * Retrieves the primary patient form name configured for the provided
	 * location and location tag.
	 * 
	 * @param locationId    The location identifier
	 * @param locationTagId The location tag identifier
	 * @return The name of the primary patient form
	 */
	public static String getPrimaryPatientFormName(Integer locationId, Integer locationTagId) {
		return getPrimaryFormName(locationId, locationTagId, ChirdlUtilConstants.LOC_TAG_ATTR_PRIMARY_PATIENT_FORM);
	}
	
	/**
	 * Retrieves the primary patient form name configured for the provided
	 * location and location tag.
	 * 
	 * @param locationId    The location identifier
	 * @param locationTagId The location tag identifier
	 * @param locationTagAttrName The name of the location tag attribute that defines the primary form requested
	 * @return The name of the primary patient form
	 */
	private static String getPrimaryFormName(Integer locationId, Integer locationTagId, String locationTagAttrName) {
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		LocationTagAttributeValue locationTagAttributeValueForm = null;
		if (locationTagId != null && locationId != null) {
			locationTagAttributeValueForm = chirdlutilbackportsService.getLocationTagAttributeValue(locationTagId,
				locationTagAttrName, locationId);
		}

		if (locationTagAttributeValueForm != null) {
			return locationTagAttributeValueForm.getValue();
		}

		return null;
	}
	
	/**
	 * Gets the form instance information from the data provided
	 * 
	 * @param encounterId      The encounter identifier
	 * @param formId           The form identifier
	 * @param startStateId     The start state identifier
	 * @param endStateId       The end state identifier
	 * @param backportsService ChirdlUtilBackportsService object
	 * @return FormInstanceTag object or null if form information cannot be found.
	 */
	public static FormInstanceTag getFormInstanceInfo(Integer encounterId, Integer formId, Integer startStateId,
			Integer endStateId, ChirdlUtilBackportsService backportsService) {
		return getFormInstanceInfo(encounterId, formId, startStateId, endStateId, backportsService, true, true);
	}
	
	/**
	 * Gets the form instance information from the data provided
	 * 
	 * @param encounterId      The encounter identifier
	 * @param formId           The form identifier
	 * @param startStateId     The start state identifier
	 * @param endStateId       The end state identifier
	 * @param backportsService ChirdlUtilBackportsService object
	 * @param requireStartStateEndTime Whether or not an end time must exist when searching for start states
	 * @param requireEndSttateEndTime Whether or not an end time must exist when searching for end states
	 * @return FormInstanceTag object or null if form information cannot be found.
	 */
	public static FormInstanceTag getFormInstanceInfo(Integer encounterId, Integer formId, Integer startStateId,
			Integer endStateId, ChirdlUtilBackportsService backportsService, boolean requireStartStateEndTime, 
			boolean requireEndStateEndTime) {
		Map<Integer, List<PatientState>> formIdToPatientStateMapStart = new HashMap<>();
		Map<Integer, List<PatientState>> formIdToPatientStateMapEnd = new HashMap<>();

		Util.getPatientStatesByEncounterId(backportsService, formIdToPatientStateMapStart, encounterId, startStateId,
			requireStartStateEndTime);
		Util.getPatientStatesByEncounterId(backportsService, formIdToPatientStateMapEnd, encounterId, endStateId, 
			requireEndStateEndTime);

		boolean containsStartState = formIdToPatientStateMapStart.containsKey(formId);
		boolean containsEndState = formIdToPatientStateMapEnd.containsKey(formId);

		if (containsStartState) {
			List<PatientState> patientStates = null;
			if (!containsEndState) {
				patientStates = formIdToPatientStateMapStart.get(formId);
				if (patientStates != null) {
					Collections.sort(patientStates,
							new PatientStateStartDateComparator(PatientStateStartDateComparator.DESCENDING));
					for (PatientState patientState : patientStates) {
						FormInstance formInstance = patientState.getFormInstance();
						if (formInstance != null) {
							return new FormInstanceTag(patientState.getLocationId(), formId,
									patientState.getFormInstanceId(), patientState.getLocationTagId());
						}
					}
				}
			} else {
				patientStates = formIdToPatientStateMapEnd.get(formId);
				if (patientStates != null) {
					Collections.sort(patientStates,
							new PatientStateStartDateComparator(PatientStateStartDateComparator.DESCENDING));
					for (PatientState patientState : patientStates) {
						if (patientState.getEndTime() == null) {
							FormInstance formInstance = patientState.getFormInstance();
							if (formInstance != null) {
								return new FormInstanceTag(patientState.getLocationId(), formId,
										patientState.getFormInstanceId(), patientState.getLocationTagId());
							}
						}
					}
				}
			}
		}

		return null;
	}
	
	/**
	 * @return the daemonToken
	 */
	public static DaemonToken getDaemonToken() {
		return daemonToken;
	}
	
	/**
	 * @param daemonToken the daemonToken to set
	 */
	public static void setDaemonToken(DaemonToken daemonToken) {
		Util.daemonToken = daemonToken;
	}
	
	/**
	 * Returns an integer from the provided map with the provided key name.
	 * 
	 * @param parameters The map to search
	 * @param paramName The key for the map value
	 * @return Integer or null if not found or not an integer
	 */
	public static Integer getIntegerFromMap(Map<String, Object> parameters, String paramName) {
    	Integer value = null;
		Object valueObj = parameters.get(paramName);
		if (valueObj instanceof Integer) {
			value = (Integer)valueObj;
		} else if (valueObj instanceof String) {
			String valueStr = (String)valueObj;
			try {
				value = Integer.valueOf(valueStr);
			} catch (NumberFormatException e) {
				log.error("Error parsing value {} to an integer.", valueStr, e);
				return null;
			}
		}
		
		return value;
    }
	
	/**
	 * Create a clinical note from the provided observations.
	 * 
	 * @param obs The observations used to create the note
	 * @return String representing the clinical note
	 */
	public static String createClinicalNote(List<Obs> obs) {
		StringBuilder noteBuilder = new StringBuilder();
		if (obs == null || obs.isEmpty()) {
			return noteBuilder.toString();
		}
		
		Map<String,Map<String,List<Obs>>> headingMap = new HashMap<>();
		List<Obs> addtnlObs = new ArrayList<>();
		ChirdlUtilBackportsService service = Context.getService(ChirdlUtilBackportsService.class);
		Set<String> ruleIdOrder = new LinkedHashSet<>();
		// Order the list by observation ID
		Collections.sort(obs, new ObsComparator());
		for (Obs ob :obs) {
			// Get the heading attribute
			ObsAttributeValue obsAttrVal = service.getObsAttributeValue(ob.getObsId(), "primaryHeading");
			if (obsAttrVal == null || obsAttrVal.getValue() == null || obsAttrVal.getValue().trim().length() == 0) {
				// No heading attribute was found.  We'll just add it to a generic heading.
				addtnlObs.add(ob);
				continue;
			}
			
			String heading = obsAttrVal.getValue();
			Map<String,List<Obs>> obsMap = headingMap.get(heading);
			if (obsMap == null) {
				obsMap = new HashMap<>();
				obsMap.put("default", new ArrayList<Obs>());
				headingMap.put(heading, obsMap);
			}
			
			// Get the ruleId attribute
			ObsAttributeValue ruleIdObsAttrVal = service.getObsAttributeValue(ob.getObsId(), "ruleId");
			if (ruleIdObsAttrVal == null || ruleIdObsAttrVal.getValue() == null || 
					ruleIdObsAttrVal.getValue().trim().length() == 0) {
				// No ruleId attribute was found.  We'll just add it to a generic heading.
				List<Obs> defaultList = obsMap.get("default");
				defaultList.add(ob);
				continue;
			}
			
			String ruleId = ruleIdObsAttrVal.getValue();
			List<Obs> obsList = obsMap.get(ruleId);
			if (obsList == null) {
				obsList = new ArrayList<>();
				obsMap.put(ruleId, obsList);
			}
			
			ruleIdOrder.add(ruleId);
			obsList.add(ob);
		}
		
		// Get the physician note configuration file
		PhysicianNoteConfig config = null;
		try {
	        config = getPhysicianNoteConfig();
        }
        catch (FileNotFoundException e) {
	        log.error("Physician note configuration file could not be found", e);
        }
        catch (JiBXException e) {
	        log.error("Exception occurred loading the physician note configuration file", e);
        }
		
        if (config == null) {
        	buildObsNoteFromMap(headingMap, noteBuilder, ruleIdOrder);
        } else {
        	HeadingOrder headingOrder = config.getHeadingOrder();
        	if (headingOrder == null) {
        		buildObsNoteFromMap(headingMap, noteBuilder, ruleIdOrder);
        	} else {
        		String[] headings = headingOrder.getHeadings();
        		if (headings == null || headings.length == 0) {
        			buildObsNoteFromMap(headingMap, noteBuilder, ruleIdOrder);
        		} else {
        			// Build the note in the order of the headings in the configuration file.
        			buildObsNoteFromMapWithHeadings(headingMap, noteBuilder, headings, ruleIdOrder);
        			// Run this in case there are some headings left that aren't in the configuration file.
        			buildObsNoteFromMap(headingMap, noteBuilder, ruleIdOrder);
        		}
        	}
        }
        
		if (!addtnlObs.isEmpty()) {
			int counter = 1;
			Set<String> noteSet = new HashSet<>();
			noteBuilder.append("ADDITIONAL OBSERVATIONS\n");
			for (Obs ob : addtnlObs) {
				String value = ob.getValueText();
				if (value != null && value.trim().length() > 0) {
					value = replaceRiskIndicators(value);
					boolean newAddition = noteSet.add(value);
					if (newAddition) {
						noteBuilder.append(counter++);
						noteBuilder.append(". ");
						noteBuilder.append(value);
						noteBuilder.append("\n");
					}
				}
			}
			
			noteSet.clear();
		}
    	
    	return noteBuilder.toString();
	}
	
	/**
	 * Builds a clinical note from the observations in the provided map.
	 * 
	 * @param headingMap Map of headings to observations
	 * @param noteBuilder The string builder containing the note text
	 * @param ruleIdOrder The order of rules
	 */
	private static void buildObsNoteFromMap(Map<String, Map<String, List<Obs>>> headingMap, StringBuilder noteBuilder,
	        Set<String> ruleIdOrder) {
		Set<Entry<String, Map<String, List<Obs>>>> mapEntries = headingMap.entrySet();
		Iterator<Entry<String, Map<String, List<Obs>>>> iter = mapEntries.iterator();
		while (iter.hasNext()) {
			Entry<String, Map<String, List<Obs>>> entry = iter.next();
			String heading = entry.getKey();
			Map<String, List<Obs>> obsMap = entry.getValue();
			writeObsToNote(heading, obsMap, noteBuilder, ruleIdOrder);
		}
	}
	
	/**
	 * Builds a clinical note from the observations in the provided map.
	 * 
	 * @param headingMap Map of headings to observations
	 * @param noteBuilder The string builder containing the note text
	 * @param headings Array of headings
	 * @param ruleIdOrder The order of rules
	 */
	private static void buildObsNoteFromMapWithHeadings(Map<String, Map<String, List<Obs>>> headingMap, 
			StringBuilder noteBuilder, String[] headings, Set<String> ruleIdOrder) {
		for (String heading : headings) {
			Map<String, List<Obs>> obsMap = headingMap.get(heading);
			if (obsMap != null) {
				writeObsToNote(heading, obsMap, noteBuilder, ruleIdOrder);
				headingMap.remove(heading);
			}
		}
	}
	
	/**
	 * Builds a clinical note from the observations in the provided map.
	 * 
	 * @param heading The heading for the notes
	 * @param obsMap Map of headings to observations
	 * @param noteBuilder The string builder containing the note text
	 * @param ruleIdOrder The order of rules
	 */
	private static void writeObsToNote(String heading, Map<String, List<Obs>> obsMap, StringBuilder noteBuilder,
	        Set<String> ruleIdOrder) {
		if (obsMap.isEmpty()) {
			return;
		}
		
		noteBuilder.append("==" + heading + "==");
		noteBuilder.append("\n");
		Iterator<String> iter = ruleIdOrder.iterator();
		while (iter.hasNext()) {
			String ruleId = iter.next();
			List<Obs> obsList = obsMap.get(ruleId);
			if (obsList != null && !obsList.isEmpty()) {
				Set<String> noteSet = new HashSet<>();
				for (Obs ob : obsList) {
					String value = ob.getValueText();
					if (value != null && value.trim().length() > 0) {
						value = replaceRiskIndicators(value);
						boolean newAddition = noteSet.add(value);
						if (newAddition) {
							noteBuilder.append(value);
							noteBuilder.append("  ");
						}
					}
				}
				
				noteSet.clear();
				noteBuilder.append("\n");
			}
			
		}
		
		noteBuilder.append("\n");
	}
	
	/**
	 * Returns the physician note configuration object.
	 * 
	 * @return PhysicianNoteConfig object or null if the XML file cannot be found.
	 * @throws JiBXException
	 * @throws FileNotFoundException
	 */
	private static PhysicianNoteConfig getPhysicianNoteConfig() throws JiBXException, FileNotFoundException {
		long currentTime = System.currentTimeMillis();
		if (physicianNoteConfig == null || (currentTime - lastUpdatedPhysicianNoteConfig) > PHYSICIAN_NOTE_CONFIG_UPDATE_CYCLE) {
			lastUpdatedPhysicianNoteConfig = currentTime;
			AdministrationService adminService = Context.getAdministrationService();
			String configFileStr = adminService.getGlobalProperty("dss.physicianNoteConfigFile");
			if (configFileStr == null) {
				log.error("You must set a value for global property: dss.physicianNoteConfigFile.");
				return physicianNoteConfig;
			}
			
			File configFile = new File(configFileStr);
			if (!configFile.exists()) {
				log.error(
				    "The file location specified for the global property dss.physicianNoteConfigFile does not exist.");
				return physicianNoteConfig;
			}
			
			IBindingFactory bfact = BindingDirectory.getFactory(PhysicianNoteConfig.class);
			
			IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
			physicianNoteConfig = (PhysicianNoteConfig) uctx.unmarshalDocument(new FileInputStream(configFile), null);
		}
		
		return physicianNoteConfig;
	}
	
	/**
	 * Replaces the high risk indicator symbols around a note.
	 * 
	 * @param note The String that will have the indicators replaced.
	 * @return String with the indicators replaced.
	 */
	private static String replaceRiskIndicators(String note) {
		if (note == null) {
			return null;
		}
		
		String updatedNote = note;
		if (updatedNote.startsWith("***")) {
			updatedNote = updatedNote.replaceFirst("\\*\\*\\*", "+++");
		} else if (updatedNote.startsWith("**")) {
			updatedNote = updatedNote.replaceFirst("\\*\\*", "++");
		} else if (updatedNote.startsWith("*")) {
			updatedNote = updatedNote.replaceFirst("\\*", "+");
		}
		
		if (updatedNote.endsWith("***")) {
			updatedNote = updatedNote.substring(0, updatedNote.length() - 3);
			updatedNote = updatedNote + "+++";
		} else if (updatedNote.endsWith("**")) {
			updatedNote = updatedNote.substring(0, updatedNote.length() - 2);
			updatedNote = updatedNote + "++";
		} else if (updatedNote.endsWith("*")) {
			updatedNote = updatedNote.substring(0, updatedNote.length() - 1);
			updatedNote = updatedNote + "+";
		}
		
		return updatedNote;
	}
}
