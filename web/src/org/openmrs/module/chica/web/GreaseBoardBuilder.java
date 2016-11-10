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

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.result.Result;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chica.util.PatientRow;
import org.openmrs.module.chica.util.PatientRowComparator;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Program;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Session;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.springframework.transaction.UnexpectedRollbackException;

/**
 * Gathers and constructs all the information for the grease board display.
 * 
 * @author Steve McKee
 */
public class GreaseBoardBuilder {
	
	private static final String WAIT_COLOR = "red_highlight";
	
	private static final String PROCESSING_COLOR = "yellow_highlight";
	
	private static final String READY_COLOR = "green_highlight";
	
	private static final String PSF_REPRINT_COLOR = "purple_highlight";
	
	private static final String PWS_REPRINT_COLOR = "blue_highlight";
	
	/**
	 * Creates the patient information needed for display on the Grease Board. All necessary
	 * information is placed in the provided map.
	 * 
	 * @param responseMap The map where all necessary information will be placed for the Grease
	 *            Board.
	 */
	public static void generatePatientRows(Map<String, Object> responseMap) {
		Integer needVitals = 0;
		Integer waitingForMD = 0;
		AdministrationService adminService = Context.getAdministrationService();
		ATDService atdService = Context.getService(ATDService.class);
		User user = Context.getUserContext().getAuthenticatedUser();
		
		try {
			ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
			ChicaService chicaService = Context.getService(ChicaService.class);
			
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
					String showBadScans = adminService.getGlobalProperty("chica.showBadScans");
					if (showBadScans != null && showBadScans.equals("true")) {
						List<URL> badScans = atdService.getBadScans(location.getName());
						responseMap.put("badScans", badScans);
					}
					
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
				Program program = chirdlutilbackportsService.getProgram(locationTagId, locationId);
				List<PatientState> currUnfinishedStates = chirdlutilbackportsService.getLastPatientStateAllPatients(
				    todaysDate.getTime(), program.getProgramId(), program.getStartState().getName(), locationTagId,
				    locationId);
				if (currUnfinishedStates != null) {
					unfinishedStates.addAll(currUnfinishedStates);
				}
			}
			
			List<PatientRow> rows = new ArrayList<PatientRow>();
			List<String> stateNames = new ArrayList<String>();
			stateNames.add(ChirdlUtilConstants.STATE_PSF_WAIT_FOR_ELECTRONIC_SUBMISSION);
			stateNames.add(ChirdlUtilConstants.STATE_PROCESS_VITALS);
			
			for (PatientState currState : unfinishedStates) {
				String checkoutStateString = adminService.getGlobalProperty("chica.greaseboardCheckoutState");
				State checkoutState = chirdlutilbackportsService.getStateByName(checkoutStateString);
				Integer sessionId = currState.getSessionId();
				List<PatientState> checkoutPatientStates = chirdlutilbackportsService.getPatientStateBySessionState(
				    sessionId, checkoutState.getStateId());
				if (checkoutPatientStates != null && checkoutPatientStates.size() > 0) {
					continue;
				}
				
				Patient patient = currState.getPatient();
				String lastName = Util.toProperCase(patient.getFamilyName());
				String firstName = Util.toProperCase(patient.getGivenName());
				
				String mrn = atdService.evaluateRule("medicalRecordNoFormatting", patient, null).toString();
				
				String dob = atdService.evaluateRule("birthdate>fullDateFormat", patient, null).toString();
				String sex = patient.getGender();
				State state = currState.getState();
				
				Session session = chirdlutilbackportsService.getSession(sessionId);
				Integer encounterId = session.getEncounterId();
				EncounterService encounterService = Context.getService(EncounterService.class);
				Encounter encounter = (Encounter) encounterService.getEncounter(encounterId);
				
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("encounterId", encounterId);
				String appointment = atdService.evaluateRule("scheduledTime>fullTimeFormat", patient, parameters).toString();
				PatientRow row = new PatientRow();
				Date encounterDate = null;
				
				if (encounter != null) {
					row.setEncounter(encounter);
					encounterDate = encounter.getEncounterDatetime();
					if (encounterDate != null && !Util.isToday(encounterDate)) {
						continue;
					}
					EncounterType encType = encounter.getEncounterType();
					if (encType != null && encType.getName().equalsIgnoreCase("ManualCheckin")) {
						row.setIsManualCheckin(true);
					}
					parameters.put("param0", new Result(encounter.getEncounterDatetime()));
					String checkin = atdService.evaluateRule("fullTimeFormat", patient, parameters).toString();
					Person provider = encounter.getProvider();
					String mdName = "";
					//Ensure proper case even though we store provider names in proper case
					//Any provider names stored before the hl7sockethandler update need to be
					//adjusted to proper case for display on greaseboard
					if (provider != null) {
						String firstInit = Util.toProperCase(provider.getGivenName());
						if (firstInit != null && firstInit.length() > 0) {
							firstInit = firstInit.substring(0, 1);
						} else {
							firstInit = "";
						}
						
						String middleInit = Util.toProperCase(provider.getMiddleName());
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
						String familyName = Util.toProperCase(provider.getFamilyName());
						if (familyName == null) {
							familyName = "";
						}
						mdName += familyName;
						
					}
					row.setCheckin(checkin);
					row.setMdName(mdName);
					
				}
				
				List<PatientState> reprintRescanStates = new ArrayList<PatientState>();
				
				for (Integer locationTagId : locationTagIds) {
					List<PatientState> currReprintRescanStates = chicaService.getReprintRescanStatesByEncounter(encounterId,
					    todaysDate.getTime(), locationTagId, locationId);
					if (currReprintRescanStates != null) {
						reprintRescanStates.addAll(currReprintRescanStates);
					}
				}
				boolean reprint = false;
				if (reprintRescanStates.size() > 0) {
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
				String strPWSGBIndicator = null;
				if (stateName.equalsIgnoreCase(ChirdlUtilConstants.STATE_PSF_WAIT_FOR_ELECTRONIC_SUBMISSION)) {
					needVitals++;
				}
				if (stateName.equalsIgnoreCase(ChirdlUtilConstants.STATE_PWS_WAIT_FOR_SUBMISSION)) {
					Map<String, List<PatientState>> patientState = chirdlutilbackportsService.getPatientStatesBySessionId(sessionId,stateNames,false);
				    if (patientState.containsKey(ChirdlUtilConstants.STATE_PROCESS_VITALS) && patientState.get(ChirdlUtilConstants.STATE_PROCESS_VITALS).get(0).getEndTime()!=null){
				    	List<PatientState> psfWaitPatientState = patientState.get(ChirdlUtilConstants.STATE_PSF_WAIT_FOR_ELECTRONIC_SUBMISSION);
				    	for (PatientState psfState : psfWaitPatientState) {
				    		if (psfState.getEndTime()==null) {
					    		strPWSGBIndicator = ChirdlUtilConstants.PWS_READY_AWAITING_PSF;
					    	} else {
					    		strPWSGBIndicator = ChirdlUtilConstants.PWS_READY;
					    		break;
					    	}
				    	}
					} else {
						needVitals++;
						strPWSGBIndicator = ChirdlUtilConstants.PWS_READY_AWAITING_VITALS;
					}
				}
				if (stateName.equalsIgnoreCase(ChirdlUtilConstants.STATE_PWS_WAIT_FOR_SUBMISSION)) {
					waitingForMD++;
				}
				setStatus(state, row, sessionId, currState, strPWSGBIndicator);
				row.setLocationId(currState.getLocationId());
				row.setLocationTagId(currState.getLocationTagId());
				rows.add(row);
			}
			
			//sort arraylist by encounterDatetime
			Collections.sort(rows, new PatientRowComparator());
			
			responseMap.put("needVitals", needVitals);
			responseMap.put("waitingForMD", waitingForMD);
			responseMap.put("patientRows", rows);
		}
		catch (UnexpectedRollbackException ex) {
			//ignore this exception since it happens with an APIAuthenticationException
		}
		catch (APIAuthenticationException ex2) {
			//ignore this exception. It happens during the redirect to the login page
		}
	}
	
	/**
	 * Sets the status field for the patient row.
	 * 
	 * @param state The patient row's current state.
	 * @param row The PatientRow that will have its status set.
	 * @param sessionId The sessionId for the patient row.
	 * @param currState The current PatientState of the row.
	 */
	private static void setStatus(State state, PatientRow row, Integer sessionId, PatientState currState, String strPWSGBIndicator) {
		//see if an incomplete state exists for the JIT
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		State jitIncompleteState = chirdlutilbackportsService.getStateByName("JIT_incomplete");
		FormInstance formInstance = currState.getFormInstance();
		Integer formId = null;
		String formName = null;
		FormService formService = Context.getFormService();
		
		if (formInstance != null) {
			formId = formInstance.getFormId();
			formName = formService.getForm(formId).getName();
		}
		
		List<PatientState> patientStates = chirdlutilbackportsService.getPatientStateBySessionState(sessionId,
		    jitIncompleteState.getStateId());
		
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
		if (stateName.equals(ChirdlUtilConstants.STATE_CHECKIN)) {
			row.setStatusColor(WAIT_COLOR);
			row.setStatus("Arrived");
			return;
		}
		if (stateName.equals(ChirdlUtilConstants.STATE_QUERY_KITE_PWS) || stateName.equals(ChirdlUtilConstants.STATE_QUERY_KITE_PSF) || stateName.equals(ChirdlUtilConstants.STATE_QUERY_KITE_ALIAS)) {
			row.setStatusColor(PROCESSING_COLOR);
			row.setStatus("Searching Patient Data...");
			return;
		}
		if (stateName.equals(ChirdlUtilConstants.STATE_PSF_CREATE) || stateName.equals(ChirdlUtilConstants.STATE_RANDOMIZE)) {
			row.setStatusColor(PROCESSING_COLOR);
			row.setStatus("Creating PSF...");
			return;
		}
		if (stateName.equals(ChirdlUtilConstants.STATE_PSF_PRINTED)) {
			row.setStatusColor(PROCESSING_COLOR);
			row.setStatus("Printing PSF...");
			return;
		}
		if (stateName.equals(ChirdlUtilConstants.STATE_PSF_WAIT_FOR_ELECTRONIC_SUBMISSION)) {
			row.setStatusColor(READY_COLOR);
			row.setStatus("PSF Tablet Ready");
			return;
		}
		if (stateName.equals(ChirdlUtilConstants.STATE_PSF_PROCESS)) {
			row.setStatusColor(WAIT_COLOR);
			row.setStatus("PSF Scanned");
			return;
		}
		if (stateName.equals(ChirdlUtilConstants.STATE_PWS_CREATE)) {
			row.setStatusColor(PROCESSING_COLOR);
			row.setStatus("Creating PWS...");
			return;
		}
		if (stateName.equals(ChirdlUtilConstants.STATE_PWS_PRINTED)) {
			row.setStatusColor(PROCESSING_COLOR);
			row.setStatus("Printing PWS...");
			return;
		}
		if (stateName.equals(ChirdlUtilConstants.STATE_PWS_WAIT_FOR_SUBMISSION)) {
			row.setStatusColor(READY_COLOR);
			row.setStatus(strPWSGBIndicator);
			return;
		}
		if (stateName.equals(ChirdlUtilConstants.STATE_PWS_PROCESS)) {
			row.setStatusColor(READY_COLOR);
			row.setStatus("PWS Scanned");
			return;
		}
		if (stateName.equals(ChirdlUtilConstants.STATE_FINISHED)) {
			row.setStatusColor(READY_COLOR);
			row.setStatus("Gone");
			return;
		}
		if (stateName.equals(ChirdlUtilConstants.STATE_ERROR_STATE)) {
			row.setStatusColor(WAIT_COLOR);
			row.setStatus("Error. Contact support");
		}
		
		if (stateName.equals(ChirdlUtilConstants.STATE_PSF_REPRINT)) {
			row.setStatusColor(PSF_REPRINT_COLOR);
			row.setStatus("PSF reprint");
		}
		
		if (stateName.equals(ChirdlUtilConstants.STATE_PWS_REPRINT)) {
			row.setStatusColor(PWS_REPRINT_COLOR);
			row.setStatus("PWS reprint");
		}
		
		//PSF_rescan and PWS_rescan states are currently not given
		//a status on the greaseboard
		
	}
}
