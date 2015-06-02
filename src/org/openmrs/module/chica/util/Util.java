/**
 * 
 */
package org.openmrs.module.chica.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.result.EmptyResult;
import org.openmrs.logic.result.Result;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.Calculator;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chirdlutil.xmlBeans.serverconfig.MobileClient;
import org.openmrs.module.chirdlutil.xmlBeans.serverconfig.MobileClients;
import org.openmrs.module.chirdlutil.xmlBeans.serverconfig.MobileForm;
import org.openmrs.module.chirdlutil.xmlBeans.serverconfig.ServerConfig;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Program;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Session;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

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
	
	public static Obs saveObsWithStatistics(Patient patient, Concept currConcept, int encounterId, String value,
	                                        FormInstance formInstance, Integer ruleId, Integer locationTagId) {
		
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
		
		if (formName != null && 
				(formName.equalsIgnoreCase("PWS") 
				|| formName.equalsIgnoreCase("ImmunizationSchedule")
				|| formName.equalsIgnoreCase("ImmunizationSchedule7yrOrOlder"))) {
			usePrintedTimestamp = true;
		}
		return org.openmrs.module.atd.util.Util.saveObsWithStatistics(patient, currConcept, encounterId, value,
		    formInstance, ruleId, locationTagId, usePrintedTimestamp);
	}
	
	public static void voidObsForConcept(Concept concept,Integer encounterId){
		EncounterService encounterService = Context.getService(EncounterService.class);
		Encounter encounter = (Encounter) encounterService.getEncounter(encounterId);
		ObsService obsService = Context.getObsService();
		List<org.openmrs.Encounter> encounters = new ArrayList<org.openmrs.Encounter>();
		encounters.add(encounter);
		List<Concept> questions = new ArrayList<Concept>();
		
		questions.add(concept);
		List<Obs> obs = obsService.getObservations(null, encounters, questions, null, null, null, null,
				null, null, null, null, false);
		
		for(Obs currObs:obs){
			obsService.voidObs(currObs, "voided due to rescan");
		}
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
	 * @return String containing any error messages encountered during the process.  If null, no errors occurred.
	 * @throws Exception
	 */
	public static String getPatientsWithPrimaryForms(ArrayList<PatientRow> rows, Integer sessionIdMatch) throws Exception {
		return getPatientsWithForms(rows, sessionIdMatch, PRIMARY_FORM);
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
		return getPatientsWithForms(rows, sessionIdMatch, SECONDARY_FORMS);
	}
	
	/**
	 * Returns patients that have forms available for the current authenticated user specified in the server configuration 
	 * file.
	 * 
	 * @param rows List that will be populated with any PatientRow objects found.
	 * @param sessionIdMatch If not null, only patient rows will be returned pertaining to the specified session ID.
	 * @return String containing any error messages encountered during the process.  If null, no errors occurred.
	 * @throws Exception
	 */
	private static String getPatientsWithForms(ArrayList<PatientRow> rows, Integer sessionIdMatch, int formType) 
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
		ATDService atdService = Context.getService(ATDService.class);
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
				if (locationTags != null) {
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
		for (Integer locationTagId : locationTagIds) {
			Program program = chirdlUtilBackportsService.getProgram(locationTagId, locationId);
			if (sessionIdMatch != null) {
				PatientState patientState = chirdlUtilBackportsService.getLastPatientState(sessionIdMatch);
				if (patientState != null) {
					unfinishedStates.add(patientState);
				}
			} else {
				List<PatientState> currUnfinishedStates = chirdlUtilBackportsService.getLastPatientStateAllPatients(
				    todaysDate.getTime(), program.getProgramId(), program.getStartState().getName(), locationTagId, locationId);
				if (currUnfinishedStates != null) {
					unfinishedStates.addAll(currUnfinishedStates);
				}
			}
		}
		
		Map<String, PatientRow> patientEncounterRowMap = new HashMap<String, PatientRow>();
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
			
			List<MobileForm> mobileFormsList = new ArrayList<MobileForm>();
			switch(formType) {
				case PRIMARY_FORM:
					MobileForm primaryForm = config.getPrimaryForm(username);
					if (primaryForm == null) {
						continue;
					} else {
						mobileFormsList.add(primaryForm);
					}
					
					break;
				case SECONDARY_FORMS:
					List<MobileForm> mobileForms = config.getSecondaryForms(username);
					if (mobileForms == null) {
						continue;
					} else {
						mobileFormsList.addAll(mobileForms);
					}
					
					break;
			}
			
			for (MobileForm mobileForm : mobileFormsList) {
				State startState = chirdlUtilBackportsService.getStateByName(mobileForm.getStartState());
				State endState = chirdlUtilBackportsService.getStateByName(mobileForm.getEndState());
				
				getPatientStatesByEncounterId(chirdlUtilBackportsService, formPatientStateCreateMap, encounterId,
				    startState.getStateId(), true);
				getPatientStatesByEncounterId(chirdlUtilBackportsService, formPatientStateProcessMap, encounterId,
				    endState.getStateId(), false);
				
				Form form = Context.getFormService().getForm(mobileForm.getName());
				if (form == null) {
					continue;
				}
				
				Integer formId = form.getFormId();
				boolean containsStartState = formPatientStateCreateMap.containsKey(formId);
				boolean containsEndState = formPatientStateProcessMap.containsKey(formId);
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
							for (PatientState patientState : patientStates) {
								if (patientState.getEndTime() == null) {
									FormInstance formInstance = patientState.getFormInstance();
									if (formInstance != null) {
										row.addFormInstance(formInstance);
										addedForm = true;
										break;
									}
								}
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
			
			Patient patient = currState.getPatient();
			String lastName = org.openmrs.module.chirdlutil.util.Util.toProperCase(patient.getFamilyName());
			String firstName = org.openmrs.module.chirdlutil.util.Util.toProperCase(patient.getGivenName());
			
			String mrn = atdService.evaluateRule("medicalRecordWithFormatting", patient, null).toString();
			
			String dob = atdService.evaluateRule("birthdate>fullDateFormat", patient, null).toString();
			String sex = patient.getGender();
			Encounter encounter = (Encounter) encounterService.getEncounter(encounterId);
			
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("encounterId", encounterId);
			String appointment = atdService.evaluateRule("scheduledTime>fullTimeFormat", patient, parameters).toString();
			Date encounterDate = null;
			
			if (encounter != null) {
				row.setEncounter(encounter);
				encounterDate = encounter.getEncounterDatetime();
				if (encounterDate != null && !org.openmrs.module.chirdlutil.util.Util.isToday(encounterDate)) {
					continue;
				}
				parameters.put("param0", new Result(encounter.getEncounterDatetime()));
				String checkin = atdService.evaluateRule("fullTimeFormat", patient, parameters).toString();
				List<User> providers = Context.getUserService().getUsersByPerson(encounter.getProvider(), true);
				String mdName = "";
				if (providers != null && providers.size() > 0) {
					User provider = providers.get(0);
					String firstInit = org.openmrs.module.chirdlutil.util.Util.toProperCase(provider.getGivenName());
					if (firstInit != null && firstInit.length() > 0) {
						firstInit = firstInit.substring(0, 1);
					} else {
						firstInit = "";
					}
					
					String middleInit = org.openmrs.module.chirdlutil.util.Util.toProperCase(encounter.getProvider().getMiddleName());
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
					String familyName = org.openmrs.module.chirdlutil.util.Util.toProperCase(provider.getFamilyName());
					if (familyName == null) {
						familyName = "";
					}
					mdName += familyName;
				}
				
				row.setCheckin(checkin);
				row.setMdName(mdName);
			}
			
			boolean reprint = false;
			for (Integer locationTagId : locationTagIds) {
				List<PatientState> currReprintRescanStates = chicaService.getReprintRescanStatesByEncounter(encounterId,
				    todaysDate.getTime(), locationTagId, locationId);
				if (currReprintRescanStates != null && currReprintRescanStates.size() > 0) {
					reprint = true;;
				}
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
				
				org.openmrs.module.chica.util.Util.voidObsForConcept(concept, encounterId);
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
				org.openmrs.module.chica.util.Util.voidObsForConcept(concept, encounterId);
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
				org.openmrs.module.chica.util.Util.voidObsForConcept(concept, encounterId);
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
				org.openmrs.module.chica.util.Util.voidObsForConcept(concept, encounterId);
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
			org.openmrs.module.chica.util.Util.voidObsForConcept(concept, encounterId);
			org.openmrs.module.chirdlutil.util.Util.saveObs(patient, concept, encounterId, result.toString(), new Date());
		}
		
		//save BMI
		questions = new ArrayList<Concept>();
		concept = conceptService.getConcept("BMI CHICA");
		questions.add(concept);
		
		result = atdService.evaluateRule("bmi", patient, parameters);
		if (!(result instanceof EmptyResult)) {
			org.openmrs.module.chica.util.Util.voidObsForConcept(concept, encounterId);
			org.openmrs.module.chirdlutil.util.Util.saveObs(patient, concept, encounterId, result.toString(), new Date());
		}
		
	}
}
