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
import java.util.Objects;
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
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chica.util.PatientRow;
import org.openmrs.module.chica.util.PatientRowComparator;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.DateUtil;
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
			Map<HashMapAttributesKey, String> hMap = new HashMap<HashMapAttributesKey, String>();
			
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
			
			String checkoutStateString = adminService.getGlobalProperty("chica.greaseboardCheckoutState");
			State checkoutState = chirdlutilbackportsService.getStateByName(checkoutStateString);
			State jitIncompleteState = chirdlutilbackportsService.getStateByName("JIT_incomplete");
			EncounterService encounterService = Context.getService(EncounterService.class);
			FormService formService = Context.getFormService();
			for (PatientState currState : unfinishedStates) {
				Integer sessionId = currState.getSessionId();
				List<PatientState> checkoutPatientStates = chirdlutilbackportsService.getPatientStateBySessionState(
				    sessionId, checkoutState.getStateId());
				if (checkoutPatientStates != null && checkoutPatientStates.size() > 0) {
					continue;
				}
				
				Patient patient = currState.getPatient();
				String lastName = Util.toProperCase(patient.getFamilyName());
				String firstName = Util.toProperCase(patient.getGivenName());
				
				String mrn = Util.getMedicalRecordNoFormatting(patient);
				
				Date birthDate = patient.getBirthdate();
				String dob = DateUtil.formatDate(birthDate, ChirdlUtilConstants.DATE_FORMAT_MMM_d_yyyy);
				String sex = patient.getGender();
				State state = currState.getState();
				
				Session session = chirdlutilbackportsService.getSession(sessionId);
				Integer encounterId = session.getEncounterId();
				Encounter encounter = (Encounter) encounterService.getEncounter(encounterId);
				
				Date appointmentDate = encounter.getScheduledTime();
				String appointment = DateUtil.formatDate(appointmentDate, ChirdlUtilConstants.DATE_FORMAT_h_mm_a);
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
					Date encounterDateTime = encounter.getEncounterDatetime();
					String checkin = DateUtil.formatDate(encounterDateTime, ChirdlUtilConstants.DATE_FORMAT_h_mm_a);
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
				
				List<PatientState> reprintRescanStates = chicaService.getReprintRescanStatesBySessionId(sessionId, null, locationTagIds, locationId);
				
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
								
				Map<String, List<PatientState>> psfAndVitalsStatesMap = null;
				if(stateName.equalsIgnoreCase(ChirdlUtilConstants.STATE_PSF_WAIT_FOR_ELECTRONIC_SUBMISSION) || stateName.equalsIgnoreCase(ChirdlUtilConstants.STATE_PWS_WAIT_FOR_SUBMISSION)) {
					psfAndVitalsStatesMap = chirdlutilbackportsService.getPatientStatesBySessionId(sessionId,stateNames,false);
					List<PatientState> vitalsStates = psfAndVitalsStatesMap.get(ChirdlUtilConstants.STATE_PROCESS_VITALS);
					if(vitalsStates == null || (vitalsStates != null && vitalsStates.get(0).getEndTime() == null)) {
						needVitals++;
					}
					if (stateName.equalsIgnoreCase(ChirdlUtilConstants.STATE_PWS_WAIT_FOR_SUBMISSION)) {
						waitingForMD++;
					}	
				}

				String printerLocation = encounter.getPrinterLocation();
				Integer formId = currState.getFormId();
				Integer locationTagId = currState.getLocationTagId();
				
				HashMapAttributesKey startStateKey = new HashMapAttributesKey(location, printerLocation, formId, ChirdlUtilConstants.FORM_ATTRIBUTE_START_STATE);

				if (!hMap.containsKey(startStateKey)) // If the map doesn't contain the startStateKey, its probably safe to assume it doesn't contain the others
				{              
	                // Create the keys and then look up the values and put them in the map
					HashMapAttributesKey reprintStateKey = new HashMapAttributesKey(location, printerLocation, formId, ChirdlUtilConstants.FORM_ATTRIBUTE_REPRINT_STATE);
					HashMapAttributesKey isPrimaryPatientFormKey = new HashMapAttributesKey(location, printerLocation, formId, ChirdlUtilConstants.FORM_ATTRIBUTE_IS_PRIMARY_PATIENT_FORM); 
					HashMapAttributesKey isPrimaryPhysicianFormKey = new HashMapAttributesKey(location, printerLocation, formId, ChirdlUtilConstants.FORM_ATTRIBUTE_IS_PRIMARY_PHYSICIAN_FORM);
	                
	                String startStateValue = org.openmrs.module.chica.util.Util.getStartStateName(encounter, formId);
	                String reprintStateValue = org.openmrs.module.chica.util.Util.getReprintStateName(encounter, formId);
	                String isPrimaryPatientFormValue = null;
	                String isPrimaryPhysicianFormValue = null;
	                if (formId != null && locationId != null && locationTagId != null) {
		                isPrimaryPatientFormValue = chirdlutilbackportsService.getFormAttributeValue(formId, ChirdlUtilConstants.FORM_ATTRIBUTE_IS_PRIMARY_PATIENT_FORM, locationTagId, locationId).getValue();
		                isPrimaryPhysicianFormValue = chirdlutilbackportsService.getFormAttributeValue(formId, ChirdlUtilConstants.FORM_ATTRIBUTE_IS_PRIMARY_PHYSICIAN_FORM, locationTagId, locationId).getValue();
	                }
	                hMap.put(startStateKey, startStateValue);
	                hMap.put(reprintStateKey, reprintStateValue);
	                hMap.put(isPrimaryPatientFormKey, isPrimaryPatientFormValue);
	                hMap.put(isPrimaryPhysicianFormKey, isPrimaryPhysicianFormValue);
				}
				setStatus(state, row, sessionId, currState, psfAndVitalsStatesMap, chirdlutilbackportsService, formService, jitIncompleteState, encounter, hMap);
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
	 * @param psfAndVitalsStatesMap
	 * @param chirdlutilbackportsService
	 * @param formService
	 * @param jitIncompleteState
	 * @param encounter
	 * @param hMap
	 */

	private static void setStatus(State state, PatientRow row, Integer sessionId, PatientState currState, Map<String, 
	                              List<PatientState>> psfAndVitalsStatesMap, ChirdlUtilBackportsService chirdlutilbackportsService,
	                              FormService formService, State jitIncompleteState, Encounter encounter, Map<HashMapAttributesKey, String> hMap) {
		//see if an incomplete state exists for the JIT
		FormInstance formInstance = currState.getFormInstance();
		Integer formId = null;
		String formName = null;
		
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
		
		HashMapAttributesKey startStateKey = new HashMapAttributesKey(encounter.getLocation(), encounter.getPrinterLocation(), formId, ChirdlUtilConstants.FORM_ATTRIBUTE_START_STATE);
		HashMapAttributesKey reprintStateKey = new HashMapAttributesKey(encounter.getLocation(), encounter.getPrinterLocation(), formId, ChirdlUtilConstants.FORM_ATTRIBUTE_REPRINT_STATE);
		HashMapAttributesKey isPrimaryPatientFormKey = new HashMapAttributesKey(encounter.getLocation(), encounter.getPrinterLocation(), formId, ChirdlUtilConstants.FORM_ATTRIBUTE_IS_PRIMARY_PATIENT_FORM);
		HashMapAttributesKey isPrimaryPhysicianFormKey = new HashMapAttributesKey(encounter.getLocation(), encounter.getPrinterLocation(), formId, ChirdlUtilConstants.FORM_ATTRIBUTE_IS_PRIMARY_PHYSICIAN_FORM);

		String startStateValue = hMap.get(startStateKey);
		String reprintStateValue = hMap.get(reprintStateKey);
		String isPrimaryPatientFormValue = hMap.get(isPrimaryPatientFormKey);
		String isPrimaryPhysicianFormValue = hMap.get(isPrimaryPhysicianFormKey);

		
		String stateName = state.getName();
		if (stateName.equals(ChirdlUtilConstants.STATE_CHECKIN)) {
			row.setStatusColor(WAIT_COLOR);
			row.setStatus("Arrived");
			return;
		}
		
		if (stateName.equals(ChirdlUtilConstants.STATE_RANDOMIZE) || (ChirdlUtilConstants.GENERAL_INFO_TRUE.equalsIgnoreCase(isPrimaryPatientFormValue) && (stateName.equals(startStateValue)))) {
            row.setStatusColor(PROCESSING_COLOR);
            row.setStatus("Creating PSF...");
            return;
		}
		
		if (stateName.equals(ChirdlUtilConstants.STATE_PSF_WAIT_FOR_ELECTRONIC_SUBMISSION)) {
			row.setStatusColor(READY_COLOR);
			row.setStatus("PSF Tablet Ready");
			return;
		}
		
		if (ChirdlUtilConstants.GENERAL_INFO_TRUE.equalsIgnoreCase(isPrimaryPhysicianFormValue) && (stateName.equals(startStateValue))) {
			row.setStatusColor(PROCESSING_COLOR);
			row.setStatus("Creating PWS...");
			return;
		}
		
		if (stateName.equals(ChirdlUtilConstants.STATE_PWS_WAIT_FOR_SUBMISSION)) {
			row.setStatusColor(READY_COLOR);
			if (psfAndVitalsStatesMap != null) {
				List<PatientState> vitalsStates = psfAndVitalsStatesMap.get(ChirdlUtilConstants.STATE_PROCESS_VITALS);
				if(vitalsStates == null || (vitalsStates != null && vitalsStates.get(0).getEndTime() == null)) {
					row.setStatus(ChirdlUtilConstants.PWS_READY_AWAITING_VITALS);
				} else {
					List<PatientState> psfWaitForSubmissionStates = psfAndVitalsStatesMap.get(ChirdlUtilConstants.STATE_PSF_WAIT_FOR_ELECTRONIC_SUBMISSION);
					row.setStatus(ChirdlUtilConstants.PWS_READY_AWAITING_PSF);
					if (psfWaitForSubmissionStates != null) {
						for (PatientState psfState : psfWaitForSubmissionStates) {
				    		if (psfState.getEndTime() != null) {
				    			row.setStatus(ChirdlUtilConstants.PWS_READY);
				    			return;
					    	} 
				    	}
					}
				}
			} else {
				row.setStatusColor(WAIT_COLOR);
				row.setStatus("Error. Contact support");
			}
			return;
		}
		
		if (stateName.equals(ChirdlUtilConstants.STATE_PWS_PROCESS)) {
			row.setStatusColor(READY_COLOR);
			row.setStatus("PWS Scanned");
			return;
		}
		
		if (stateName.equals(ChirdlUtilConstants.STATE_ERROR_STATE)) {
			row.setStatusColor(WAIT_COLOR);
			row.setStatus("Error. Contact support");
		}
		
		if (ChirdlUtilConstants.GENERAL_INFO_TRUE.equalsIgnoreCase(isPrimaryPatientFormValue) && (stateName.equals(reprintStateValue))) {
			row.setStatusColor(PSF_REPRINT_COLOR);
			row.setStatus("PSF reprint");
		}
		
		if (ChirdlUtilConstants.GENERAL_INFO_TRUE.equalsIgnoreCase(isPrimaryPhysicianFormValue) && (stateName.equals(reprintStateValue))) {
			row.setStatusColor(PWS_REPRINT_COLOR);
			row.setStatus("PWS reprint");
		}
		
		//PSF_rescan and PWS_rescan states are currently not given
		//a status on the greaseboard
		
	}
	
	/**
	 * 
	 * @author ssarala
	 *
	 */
	private static class HashMapAttributesKey {
		
	    private Location location;
	    private String printerLocation;
	    private Integer formId;
	    private String type;
	    
	    /**
	     * Constructor method
	     * @param location
	     * @param printerLocation
	     * @param formId
	     * @param type
	     */
	    public HashMapAttributesKey(Location location, String printerLocation, Integer formId, String type) {
	    	this.location = location;
	    	this.printerLocation = printerLocation;
	    	this.formId = formId;
	    	this.type = type;
	    }
	    
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "HashMapAttributesKey [location=" + location + ", printerLocation=" + printerLocation + ", formId="
					+ formId + ", type=" + type + "]";
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return Objects.hash(this.location, this.printerLocation, this.formId, this.type);
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof HashMapAttributesKey)) {
				return false;
			}
			HashMapAttributesKey other = (HashMapAttributesKey) obj;
			if (type == null) {
				if (other.type != null) {
					return false;
				}
			} else if (!type.equals(other.type)) {
				return false;
			}
			if (formId == null) {
				if (other.formId != null) {
					return false;
				}
			} else if (!formId.equals(other.formId)) {
				return false;
			}
			if (location == null) {
				if (other.location != null) {
					return false;
				}
			} else if (!location.equals(other.location)) {
				return false;
			}
			if (printerLocation == null) {
				if (other.printerLocation != null) {
					return false;
				}
			} else if (!printerLocation.equals(other.printerLocation)) {
				return false;
			}
			return true;
		}
	}
}
