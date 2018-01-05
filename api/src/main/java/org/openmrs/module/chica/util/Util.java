/**
 * 
 */
package org.openmrs.module.chica.util;

import java.io.BufferedReader;
import java.io.File;
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
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.result.EmptyResult;
import org.openmrs.logic.result.Result;
import org.openmrs.module.atd.hibernateBeans.Statistics;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.Calculator;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.DateUtil;
import org.openmrs.module.chirdlutil.util.XMLUtil;
import org.openmrs.module.chirdlutil.xmlBeans.serverconfig.MobileClient;
import org.openmrs.module.chirdlutil.xmlBeans.serverconfig.MobileClients;
import org.openmrs.module.chirdlutil.xmlBeans.serverconfig.MobileForm;
import org.openmrs.module.chirdlutil.xmlBeans.serverconfig.ServerConfig;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.LocationTagAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Program;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Session;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.module.sockethl7listener.hibernateBeans.HL7Outbound;
import org.openmrs.module.sockethl7listener.service.SocketHL7ListenerService;

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
	
	private static Log log = LogFactory.getLog(Util.class);
	
	public static final Random GENERATOR = new Random();
	
	private static final String START_STATE = "start_state";
	private static final String END_STATE = "end_state";
	
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
				log.error("Could not find form for statistics update");
				return null;
			}
			
			FormService formService = Context.getFormService();
			Form form = formService.getForm(formInstance.getFormId());
			formName = form.getName();
		}
		
		boolean usePrintedTimestamp = false;
		
		String formType = org.openmrs.module.chica.util.Util.getFormType(formInstance.getFormId(), locationTagId, formInstance.getLocationId());
		if (formName != null && 
				(ChirdlUtilConstants.PHYSICIAN_FORM_TYPE.equalsIgnoreCase(formType) 
				|| formName.equalsIgnoreCase("ImmunizationSchedule")
				|| formName.equalsIgnoreCase("ImmunizationSchedule7yrOrOlder"))) {
			usePrintedTimestamp = true;
		}
		return org.openmrs.module.atd.util.Util.saveObsWithStatistics(patient, currConcept, encounterId, value,
		    formInstance, ruleId, locationTagId, usePrintedTimestamp, formFieldId);
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
				log.error("Page was not sent due to null url string or null parameters. " + urlStr);
				return "";
			}
			
			URL url = new URL(urlStr);
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; .NET CLR 1.0.3705;)");
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			
			return sb.toString();
		}
		catch (Exception e) {
			log.error("Could not send page: " + message + " to " + pagerNumber + " " + e.getMessage());
			log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}
		finally {
			if (rd != null) {
				try {
					rd.close();
				}
				catch (Exception e) {
					log.error("Error closing the reader.");
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
	 * Returns patients that have forms available for the current authenticated user specified in the server configuration 
	 * file.
	 * 
	 * @param rows List that will be populated with any PatientRow objects found.
	 * @param sessionIdMatch If not null, only patient rows will be returned pertaining to the specified session ID.
	 * @param showAllPatients - true to show all patients for the user's location
	 * @return String containing any error messages encountered during the process.  If null, no errors occurred.
	 * @throws Exception
	 */
	private static String getPatientsWithForms(ArrayList<PatientRow> rows, Integer sessionIdMatch, int formType, boolean showAllPatients) 
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
			log.error("Server config contains no entry for mobileClients with mobileClient elements.  "
			        + "No patients will be returned.");
			return null;
		}
		
		String username = user.getUsername();
		MobileClient userClient = config.getMobileClient(username);
		if (userClient == null) {
			log.error("Server config contains no mobile clients for username: " + username + ".");
			return null;
		}
		
		ChirdlUtilBackportsService chirdlUtilBackportsService = Context.getService(ChirdlUtilBackportsService.class);
		ChicaService chicaService = Context.getService(ChicaService.class);
		EncounterService encounterService = Context.getService(EncounterService.class);
		
		Calendar todaysDate = Calendar.getInstance();
		
		todaysDate.set(Calendar.HOUR_OF_DAY, 0);
		todaysDate.set(Calendar.MINUTE, 0);
		todaysDate.set(Calendar.SECOND, 0);
		
		String locationTags = user.getUserProperty("locationTags");
		String locationString = user.getUserProperty("location");
		ArrayList<Integer> locationTagIds = new ArrayList<Integer>();
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
		
		List<PatientState> unfinishedStates = new ArrayList<PatientState>();
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
				log.error("Error occurred while generating a list of all patients for location (locationId: " + locationId + ").", e);
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
		List<MobileForm> mobileFormsList = new ArrayList<MobileForm>();
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
		}
		
		// DWE CHICA-761 Create a hashmap of mobile form start/end states so we don't query for them in the loop
		// Also create a map of forms so we don't have to query for them below
		FormService formService = Context.getFormService();
		Map<String, Form> formMap = new HashMap<String, Form>();
		Map<String, HashMap<String, State>> mobileFormStartEndStateMap = new HashMap<String, HashMap<String, State>>();
		Map<String, State> stateNameToStateMap = new HashMap<String, State>();
		for (MobileForm mobileForm : mobileFormsList)
		{
			Form form = formService.getForm(mobileForm.getName());
			if(form == null)
			{
				continue;
			}
			formMap.put(mobileForm.getName(), form);
			HashMap<String, State> startEndStateMap = new HashMap<String, State>();
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
		Map<String, PatientRow> patientEncounterRowMap = new HashMap<String, PatientRow>();
		DecimalFormat decimalFormat = new DecimalFormat("#.#");
		Double maxWeight = userClient.getMaxSecondaryFormWeight();
		for (PatientState currState : unfinishedStates) {
			boolean addedForm = false;
			Integer sessionId = currState.getSessionId();
			if ((sessionIdMatch != null && !sessionIdMatch.equals(sessionId))) {
				continue;
			}
			
			Integer patientId = currState.getPatientId();
			Session session = chirdlUtilBackportsService.getSession(sessionId);
			Integer encounterId = session.getEncounterId();
			Map<Integer, List<PatientState>> formPatientStateCreateMap = new HashMap<Integer, List<PatientState>>();
			Map<Integer, List<PatientState>> formPatientStateProcessMap = new HashMap<Integer, List<PatientState>>();
			PatientRow row = patientEncounterRowMap.get(patientId + "_" + encounterId);
			if (row == null) {
				row = new PatientRow();
				patientEncounterRowMap.put(patientId + "_" + encounterId, row);
			}
			
			Double accumWeight = 0.0d;
			Map<Integer, Double> formWeightMap = new HashMap<Integer, Double>();
			Set<Integer> completedFormIds = new HashSet<Integer>();
			for (MobileForm mobileForm : mobileFormsList) {
				State startState;
				State endState;
				HashMap<String, State> startEndStateMap = mobileFormStartEndStateMap.get(mobileForm.getName());
				if(startEndStateMap == null)
				{
					continue;
				}
				else
				{
					startState = startEndStateMap.get(START_STATE);
					endState = startEndStateMap.get(END_STATE);
					if(startState == null || endState == null)
					{
						continue;
					}
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
			Set<FormInstance> formInstances = new LinkedHashSet<FormInstance>(row.getFormInstances());
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
			String dob = DateUtil.formatDate(patient.getBirthdate(), ChirdlUtilConstants.DATE_FORMAT_MMM_d_yyyy);
			
			// DWE CHICA-884 Get patient age. This will be used to determine if the confidentiality pop-up should be
			// displayed for patients >= 12 years old
			Integer ageInYears = org.openmrs.module.chirdlutil.util.Util.getAgeInUnits(patient.getBirthdate(), Calendar.getInstance().getTime(), YEAR_ABBR);
						
			String sex = patient.getGender();
			Encounter encounter = (Encounter) encounterService.getEncounter(encounterId);
			
			// DWE CHICA-761 Replaced call to formatting rules with util methods to improve performance
			String appointment = "";
			if(encounter != null)
			{
				appointment = DateUtil.formatDate(encounter.getScheduledTime(), ChirdlUtilConstants.DATE_FORMAT_h_mm_a);
			}
			
			Date encounterDate = null;
			
			if (encounter != null) {
				row.setEncounter(encounter);
					
				encounterDate = encounter.getEncounterDatetime();
				if (encounterDate != null && !org.openmrs.module.chirdlutil.util.Util.isToday(encounterDate)) {
					continue;
				}
				// DWE CHICA-761 Replaced call to formatting rules with util methods to improve performance
				String checkin = DateUtil.formatDate(encounter.getEncounterDatetime(), ChirdlUtilConstants.DATE_FORMAT_h_mm_a);
				
				// CHICA-221 Use the provider that has the "Attending Provider" role for the encounter
				org.openmrs.Provider provider = org.openmrs.module.chirdlutil.util.Util.getProviderByAttendingProviderEncounterRole(encounter);
				
				String mdName = "";
				if (provider != null) {
					Person person = provider.getPerson();
					String firstInit = org.openmrs.module.chirdlutil.util.Util.toProperCase(person.getGivenName());
					if (firstInit != null && firstInit.length() > 0) {
						firstInit = firstInit.substring(0, 1);
					} else {
						firstInit = "";
					}
					
					String middleInit = org.openmrs.module.chirdlutil.util.Util.toProperCase(person.getMiddleName());
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
					String familyName = org.openmrs.module.chirdlutil.util.Util.toProperCase(person.getFamilyName());
					if (familyName == null) {
						familyName = "";
					}
					mdName += familyName;
				}
				
				row.setCheckin(checkin);
				row.setMdName(mdName);
			}
			
			boolean reprint = false;
			// DWE CHICA-761 Changed this query to scan for reprint states using a list of locationTagIds
			// Also changed this so that we query by sessionId instead of encounterId
			try
			{
				List<PatientState> currReprintRescanStates = chicaService.getReprintRescanStatesBySessionId(sessionId,
					    todaysDate.getTime(), locationTagIds, locationId);
					if (currReprintRescanStates != null && currReprintRescanStates.size() > 0) {
						reprint = true;;
					}
			}
			catch(Exception e)
			{
				log.error("Error getting reprint/rescan states", e);
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
			row.setAgeInYears(ageInYears);
			
			rows.add(row);
		}
		
		patientEncounterRowMap.clear();
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
				foundStates = new ArrayList<PatientState>();
			}
			
			foundStates.add(patientState);
			formIdToPatientStateMap.put(formId, foundStates);
		}
	}
	
	public static void calculatePercentiles(Integer encounterId, Patient patient, Integer locationTagId) {
		EncounterService encounterService = Context.getService(EncounterService.class);
		Encounter encounter = (Encounter) encounterService.getEncounter(encounterId);
		ObsService obsService = Context.getObsService();
		ATDService atdService = Context.getService(ATDService.class);
		List<org.openmrs.Encounter> encounters = new ArrayList<org.openmrs.Encounter>();
		encounters.add(encounter);
		List<Concept> questions = new ArrayList<Concept>();
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		Calculator calculator = new Calculator();
		parameters.put("encounterId", encounterId);
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
		
		questions = new ArrayList<Concept>();
		concept = conceptService.getConcept("HCCentile");
		questions.add(concept);
		
		parameters.put("concept", "HC");
		result = atdService.evaluateRule("conceptRule", patient, parameters);
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
		
		questions = new ArrayList<Concept>();
		concept = conceptService.getConcept("HtCentile");
		questions.add(concept);
		
		parameters.put("concept", "HEIGHT");
		result = atdService.evaluateRule("conceptRule", patient, parameters);
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
		
		questions = new ArrayList<Concept>();
		concept = conceptService.getConcept("WtCentile");
		questions.add(concept);
		
		parameters.put("concept", "WEIGHT");
		result = atdService.evaluateRule("conceptRule", patient, parameters);
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
		questions = new ArrayList<Concept>();
		concept = conceptService.getConcept("BP");
		questions.add(concept);
		
		result = atdService.evaluateRule("bp", patient, parameters);
		if (!(result instanceof EmptyResult)) {
			org.openmrs.module.chica.util.Util.voidObsForConcept(concept, encounterId, null); // DWE CHICA-437 Added formFieldId parameter - intentionally null here
			org.openmrs.module.chirdlutil.util.Util.saveObs(patient, concept, encounterId, result.toString(), new Date());
		}
		
		//save BMI
		questions = new ArrayList<Concept>();
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
			if (encounter.getPrinterLocation() != null) {
				Location location = encounter.getLocation();
				Set<LocationTag> tags = location.getTags();

				if (tags != null) {
					for (LocationTag tag : tags) {
						if (tag.getName().equalsIgnoreCase(
								encounter.getPrinterLocation())) {
							return tag.getLocationTagId();
						}
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
		if (encounters == null || encounters.size() == 0) {
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
    	
    	if (latestResult.getResultObject() == null || !(latestResult.getResultObject() instanceof Obs)) {
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
    	
    	if (encounterId == obsEncounter.getEncounterId()) {
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
			EncounterService encounterService = Context
					.getService(EncounterService.class);
			Encounter encounter = (Encounter) encounterService
					.getEncounter(encounterId);

			if (encounter != null)
			{
				// see if the encounter has a printer location
				// this will give us the location tag id
				printerLocation = encounter.getPrinterLocation();

				// if the printer location is null, pick
				// any location tag id for the given location
				if (printerLocation == null)
				{
					Location location = encounter.getLocation();
					if (location != null)
					{
						Set<LocationTag> tags = location.getTags();

						if (tags != null && tags.size() > 0)
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
				log.error("The file path in the form attribute is not defined correctly. "+ e);
			}				
		} else {
			stylesheetFile = XMLUtil.findStylesheet(strStylesheet);
		}
		if (stylesheetFile == null) {
			log.error("Error finding stylesheet to format the form: " + strStylesheet);
		}
		
		if (XmlFile != null && stylesheetFile != null) {
			try {
				strOutput = XMLUtil.transformFile(XmlFile, stylesheetFile);
			} catch (Exception e) {
				log.error("Error transforming xml: " + XmlFile.getAbsolutePath() + " xslt: " + 
					stylesheetFile.getAbsolutePath(), e);
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
			log.error("No value set for global property: "+ChirdlUtilConstants.GLOBAL_PROP_FORM_TIME_LIMIT+". A default of 2 days will be used.");
			formTimeLimitStr = "2";
		}
		try {
			formTimeLimit = Integer.parseInt(formTimeLimitStr);
		} catch (NumberFormatException e) {
			log.error("Invalid number format for global property "+ChirdlUtilConstants.GLOBAL_PROP_FORM_TIME_LIMIT+". A default of 2 days will be used.");
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
			EncounterService encounterService = Context.getService(EncounterService.class);
			Encounter openmrsEncounter = (Encounter) encounterService.getEncounter(encounterId);
			SocketHL7ListenerService socketHL7ListenerService = Context.getService(SocketHL7ListenerService.class);
			
			if(openmrsEncounter == null)
			{
				log.error("Error creating HL7Outbound record. Unable to locate encounterId: " + encounterId);
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
		EncounterService encounterService = Context.getService(EncounterService.class);
		Encounter encounter = (Encounter) encounterService.getEncounter(encounterId);
		
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
		
		String formName = getPrimaryFormNameByLocationTag(encounter, locTagAttrName);
		return formName;
	}
	
	/**
	 * Gets the primary form name based on the option that is selected in the drop-down
	 * @param encounterId
	 * @param printOptionString option selected in the GreaseBoard action drop-down
	 * @return form Name
	 */
	public static String getFormNameByPrintOptionString(Integer encounterId, String printOptionString)
	{
		EncounterService encounterService = Context.getService(EncounterService.class);
		Encounter encounter = (Encounter) encounterService.getEncounter(encounterId);
		
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
		EncounterService encounterService = Context.getService(EncounterService.class);
		Encounter encounter = (Encounter) encounterService.getEncounter(encounterId);
		
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
		EncounterService encounterService = Context.getService(EncounterService.class);
		Encounter encounter = (Encounter) encounterService.getEncounter(encounterId);
		
		return getReprintStateName(encounter, formId);
	}
		
	/**
	 * Retrieves the form type for PrimaryPatientForm and PrimaryPhysicianForm
	 * @param formId
	 * @param locationTagId
	 * @param locationId
	 * @return Form Type
	 */
	public static String getFormType(Integer formId, Integer locationTagId, Integer locationId) {
		
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		FormAttributeValue primaryPatientFormfav = null;
		FormAttributeValue primaryPhysicianFormfav = null;
		if (formId != null && locationId != null && locationTagId != null) {
			primaryPatientFormfav = chirdlutilbackportsService.getFormAttributeValue(formId, ChirdlUtilConstants.FORM_ATTRIBUTE_IS_PRIMARY_PATIENT_FORM, locationTagId, locationId);
			primaryPhysicianFormfav = chirdlutilbackportsService.getFormAttributeValue(formId, ChirdlUtilConstants.FORM_ATTRIBUTE_IS_PRIMARY_PHYSICIAN_FORM, locationTagId, locationId);
		}
		if (primaryPatientFormfav != null && StringUtils.isNotBlank(primaryPatientFormfav.getValue()) && 
				ChirdlUtilConstants.FORM_ATTR_VAL_TRUE.equalsIgnoreCase(primaryPatientFormfav.getValue())) { 
			return ChirdlUtilConstants.PATIENT_FORM_TYPE;
		} else if (primaryPhysicianFormfav != null && StringUtils.isNotBlank(primaryPhysicianFormfav.getValue()) && 
				ChirdlUtilConstants.FORM_ATTR_VAL_TRUE.equalsIgnoreCase(primaryPhysicianFormfav.getValue())) {
			return ChirdlUtilConstants.PHYSICIAN_FORM_TYPE;
		}
		return null;
	}
	
}
