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
package org.openmrs.module.chica.hl7.mckesson;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.QueryKite;
import org.openmrs.module.chica.QueryKiteException;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.hl7.mrfdump.HL7ToObs;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Error;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.LocationTagAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Session;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.module.sockethl7listener.HL7EncounterHandler;
import org.openmrs.module.sockethl7listener.HL7Filter;
import org.openmrs.module.sockethl7listener.HL7ObsHandler;
import org.openmrs.module.sockethl7listener.HL7PatientHandler;
import org.openmrs.module.sockethl7listener.PatientHandler;
import org.openmrs.module.sockethl7listener.Provider;
import org.openmrs.module.sockethl7listener.service.SocketHL7ListenerService;
import org.openmrs.util.OpenmrsConstants;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.parser.EncodingNotSupportedException;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.validation.impl.NoValidation;


/**
 * Processes in-bound HL7 registration messages to initiate check-in process.
 * @author tmdugan
 * 
 */

public class HL7SocketHandler extends
		org.openmrs.module.sockethl7listener.HL7SocketHandler {

	

	private static final String VOID_REASON_MRN_CORRECTION = "MRN Correction";

	private static final String VOID_REASON_MRN_LEADING_ZERO_CORRECTION = "MRN Leading Zero Correction";

	private static final String HYPHEN = "-";

	protected final static Log log = LogFactory.getLog(HL7SocketHandler.class);
	
	private static final String GLOBAL_PROPERTY_FILTER_ON_PRIOR_CHECKIN = "chica.filterHL7RegistrationOnPriorCheckin";
	private static final String GLOBAL_PROPERTY_PARSE_ERROR_DIRECTORY = "chica.mckessonParseErrorDirectory";
	
	private static final String STATE_CLINIC_REGISTRATION = "Clinic Registration";
	private static final String STATE_HL7_CHECKIN = "Process Checkin HL7";
	private static final String STATE_QUERY_ALIAS = "QUERY KITE Alias";
	
	private static final String CONCEPT_INSURANCE_NAME = "InsuranceName";
	

	private static final String ERROR_LEVEL_FATAL = "Fatal";
	
	private static final String PARAMETER_QUERY_ALIAS_START = "queryKiteAliasStart";
	private static final String PARAMETER_QUERY_ALIAS_STOP = "queryKiteAliasEnd";
	private static final String PARAMETER_SESSION = "session";
	
	private static final String PROCESS_HL7_CHECKIN_END = "processCheckinHL7End";
	private static final String PROCESS_HL7_CHECKIN_START = "processCheckinHL7Start";
	
	private static final String HL7_MESSAGE_TYPE_A01 = "A01";
	private static final String HL7_VERSION_2_5 = "2.5";
	private static final String ZPV_SEGMENT = "ZPV";
	
	private static final String PRINTER_LOCATION_FOR_SHOTS = "0";
	private static final String TRUE = "true";
	private static final String EMPTY_STRING = "";
	private static final String LEADING_ZERO = "0";


	

	/* (non-Javadoc)
	 * @see org.openmrs.module.sockethl7listener.HL7SocketHandler#findPatient(org.openmrs.Patient, java.util.Date, java.util.HashMap)
	 * Searches for patient based on medical record number and then runs an alias query for that patient.
	 */
	@Override
	public Patient findPatient(Patient hl7Patient, Date encounterDate,
			HashMap<String, Object> parameters) {
		Patient resultPatient = null;

		try {
			PatientIdentifier patientIdentifier = hl7Patient
					.getPatientIdentifier();
			
			if (patientIdentifier != null) {
				String mrn = patientIdentifier.getIdentifier();
				// look for matched patient
				Patient matchedPatient = findPatient(hl7Patient);
				
				if (matchedPatient == null) {
					resultPatient = createPatient(hl7Patient);	
				}
				else {
					resultPatient = updatePatient(matchedPatient, hl7Patient,
							encounterDate);
				}
				
				//Always query MRF and perform alias even if the patient already matched in CHICA.
				parameters.put(PARAMETER_QUERY_ALIAS_START, new java.util.Date());
				QueryKite.mrfQuery(mrn, resultPatient, true);
				parameters.put(PARAMETER_QUERY_ALIAS_STOP, new java.util.Date());
				parameters.put(PROCESS_HL7_CHECKIN_END, new java.util.Date());

			}

		} catch (RuntimeException e) {
			logger.error("Exception during patient lookup. " + e.getMessage());
			logger.error(org.openmrs.module.chirdlutil.util.Util
					.getStackTrace(e));

		} catch (QueryKiteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultPatient;

	}

	/**
	 * Gets the location tag id from the encounter.
	 * @param encounter
	 * @return
	 */
	private Integer getLocationTagId(Encounter encounter) {
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
	 * Gets the location id from encounter.
	 * @param encounter
	 * @return
	 */
	private Integer getLocationId(Encounter encounter) {
		if (encounter != null) {
			return encounter.getLocation().getLocationId();
		}
		return null;
	}

	/**
	 * Check if the patient from hl7 messages already exists in CHICA
	 * @param hl7Patient
	 * @return
	 */
	private Patient findPatient(Patient hl7Patient) {
		// Search by MRN
		PatientIdentifier patientIdentifier = hl7Patient.getPatientIdentifier();
		String mrn = patientIdentifier.getIdentifier();
		PatientService patientService = Context.getPatientService();
		
		List<Patient> lookupPatients = patientService.getPatients(null, mrn,
				null, true);
		
		if (lookupPatients == null || lookupPatients.size() == 0){
			lookupPatients = patientService.getPatients(null, LEADING_ZERO + mrn,
					null, true);
		}

		if (lookupPatients != null && lookupPatients.size() > 0) {
			return lookupPatients.iterator().next();
		}

		// Search by SSN
		PatientIdentifier ssnIdent = hl7Patient.getPatientIdentifier(ChirdlUtilConstants.IDENTIFIER_TYPE_SSN);
		if (ssnIdent != null) {
			String ssn = ssnIdent.getIdentifier();
			lookupPatients = patientService.getPatients(null, ssn, null, true);
			if (lookupPatients != null && lookupPatients.size() > 0) {
				Iterator<Patient> i = lookupPatients.iterator();
				while (i.hasNext()) {
					Patient patient = i.next();
					if (matchPatients(patient, hl7Patient)) {
						return patient;
					}
				}

				// If we didn't find a match, we need to remove the SSN because
				// there's a duplicate.
				hl7Patient.removeIdentifier(ssnIdent);
				// Add a person attribute to store attempted SSN.
				PersonAttributeType personAttrType = Context.getPersonService()
						.getPersonAttributeTypeByName(ChirdlUtilConstants.PERSON_ATTRIBUTE_SSN);
				if (personAttrType != null) {
					PersonAttribute personAttr = new PersonAttribute(
							personAttrType, ssn);
					hl7Patient.addAttribute(personAttr);
				}
			}
		}

		return null;
	}

	/**
	 * Check if patients match based on names and birth date.
	 * @param patient1
	 * @param patient2
	 * @return
	 */
	private boolean matchPatients(Patient patient1, Patient patient2) {
		String familyName1 = patient1.getFamilyName();
		String familyName2 = patient2.getFamilyName();
		if ((familyName1 != null && familyName2 == null)
				|| (familyName1 == null && familyName2 != null)) {
			return false;
		}

		if (familyName1 != null) {
			if (!familyName1.equals(familyName2))
				return false;
		} else if (familyName2 != null) {
			if (!familyName2.equals(familyName1))
				return false;
		}

		String givenName1 = patient1.getGivenName();
		String givenName2 = patient2.getGivenName();
		if ((givenName1 != null && givenName2 == null)
				|| (givenName1 == null && givenName2 != null)) {
			return false;
		}

		if (givenName1 != null) {
			if (!givenName1.equals(givenName2))
				return false;
		} else if (givenName2 != null) {
			if (!givenName2.equals(givenName1))
				return false;
		}

		Date birthDate1 = patient1.getBirthdate();
		Date birthDate2 = patient2.getBirthdate();
		if ((birthDate1 != null && birthDate2 == null)
				|| (birthDate1 == null && birthDate2 != null)) {
			return false;
		}

		if ((birthDate1 == null && birthDate2 == null)) {
			return true;
		}

		long time1 = birthDate1.getTime();
		long time2 = birthDate2.getTime();
		if (time1 != time2)
			return false;

		return true;
	}

	/**
	 * Parses the alias query response, and merges patients if needed.
	 * @param mrn
	 * @param preferredPatient
	 */
/*	private void processAliasString(String mrn, Patient preferredPatient) {
		
		String response = QueryKite.mrfQuery(mrn, preferredPatient, true);
		long startTime = System.currentTimeMillis();
		long startTime2 = System.currentTimeMillis();
		
		log.info("Starting mrf parsing");
		LogicService logicService = Context.getLogicService();

		ObsInMemoryDatasource xmlDatasource = (ObsInMemoryDatasource) logicService
				.getLogicDataSource("RMRS");
		
		HashMap<Integer, HashMap<String, Set<Obs>>> regenObs = xmlDatasource.getObs();
		//mrf dump has multiple messages
		List<String> messages = HL7ToObs.parseHL7Batch(response);
		for (String messageString : messages){
			HL7ToObs.processMessage(messageString, preferredPatient, regenObs);
			//checkAliases(mrn, messageString);
		}
		
		log.info("Elapsed time for mrf parsing is "+
				(System.currentTimeMillis()-startTime)/1000);
		

		// query failed
		if (response == null) {
			return;
		}

		//mrf dump has multiple messages
		List<String> messages = HL7ToObs.parseHL7Batch(response);

		for (String message : messages){
			
			//get identifiers and merge if necessary
			HL7PatientHandler25 patientHandler = new HL7PatientHandler25();
			List<String> identifiers = patientHandler.getIdentiferStrings(response);

			for (String identifier : identifiers){

				// don't look up the preferred patient's mrn
				// so we don't merge a patient to themselves
				if (Util.removeLeadingZeros(identifier).equals(
						Util.removeLeadingZeros(mrn))) {
					continue;
				}

				List<Patient> lookupPatients = patientService.getPatients(null,
						Util.removeLeadingZeros(identifier), null, false);

				if (lookupPatients != null && lookupPatients.size() > 0) {
					Iterator<Patient> iter = lookupPatients.iterator();

					while (iter.hasNext()) {
						Patient currPatient = iter.next();
						// only merge different patients
						if (!preferredPatient.getPatientId().equals(
								currPatient.getPatientId())) {
							patientService.mergePatients(preferredPatient,
									currPatient);
						} else {
							Error error = new Error(ERROR_LEVEL_ERROR, ChirdlUtilConstants.ERROR_GENERAL,
									"Tried to merge patient: "
											+ currPatient.getPatientId()
											+ " with itself.", null,
											new Date(), null);
							chirdlutilbackportsService.saveError(error);
						}
					}
				}

			}
			
			//parse the obs
			
		}

	}*/

	/**
	 * Update matched patient to values from hl7 message (non-Javadoc)
	 * 
	 * @see org.openmrs.module.sockethl7listener.HL7SocketHandler#updatePatient(org
	 *      .openmrs.Patient, org.openmrs.Patient, java.util.Date)
	 */
	@Override
	protected Patient updatePatient(Patient matchPatient, Patient hl7Patient,
			Date encounterDate) {

		PatientService patientService = Context.getPatientService();

		Patient currentPatient = patientService.getPatient(matchPatient
				.getPatientId());

		if (currentPatient == null || hl7Patient == null) {
			return matchPatient;
		}

		currentPatient.setCauseOfDeath(hl7Patient.getCauseOfDeath());
		currentPatient.setDead(hl7Patient.getDead());
		currentPatient.setDeathDate(hl7Patient.getDeathDate());
		currentPatient.setBirthdate(hl7Patient.getBirthdate());
		addGender(currentPatient, hl7Patient);
		addName(currentPatient, hl7Patient, encounterDate);
		addAddress(currentPatient, hl7Patient, encounterDate);
		addSSN(currentPatient, hl7Patient, encounterDate);
		addReligion(currentPatient, hl7Patient, encounterDate);
		addMaritalStatus(currentPatient, hl7Patient, encounterDate);
		addMaidenName(currentPatient, hl7Patient, encounterDate);
		addNK(currentPatient, hl7Patient, encounterDate);
		addTelephoneNumber(currentPatient, hl7Patient, encounterDate);
		AddCitizenship(currentPatient, hl7Patient, encounterDate);
		AddRace(currentPatient, hl7Patient, encounterDate);
		addMRN(currentPatient, hl7Patient, encounterDate);
		
		Patient updatedPatient = null;
		try {
			updatedPatient = patientService.savePatient(currentPatient);
		} catch (APIException e) {
			log.error("Exception saving updated patient.", e);
		}
		return updatedPatient;

	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.sockethl7listener.HL7SocketHandler#checkin(org.openmrs.module.sockethl7listener.Provider, org.openmrs.Patient, java.util.Date, ca.uhn.hl7v2.model.Message, java.lang.String, org.openmrs.Encounter, java.util.HashMap)
	 */
	@Override
	public org.openmrs.Encounter checkin(Provider provider, Patient patient,
			Date encounterDate, Message message, String incomingMessageString,
			org.openmrs.Encounter newEncounter,
			HashMap<String, Object> parameters) {
		Date processCheckinHL7Start = (Date) parameters
				.get(PROCESS_HL7_CHECKIN_START);
		if (processCheckinHL7Start == null) {
			parameters.put(PROCESS_HL7_CHECKIN_START, new java.util.Date());
		}

		return super.checkin(provider, patient, encounterDate, message,
				incomingMessageString, newEncounter, parameters);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.sockethl7listener.HL7SocketHandler#CreateObservation(org.openmrs.Encounter, boolean, ca.uhn.hl7v2.model.Message, int, int, org.openmrs.Location, org.openmrs.Patient)
	 */
	@Override
	public Obs CreateObservation(org.openmrs.Encounter enc,
			boolean saveToDatabase, Message message, int orderRep, int obxRep,
			Location existingLoc, Patient resultPatient) {
		return super.CreateObservation(enc, saveToDatabase, message, orderRep,
				obxRep, existingLoc, resultPatient);
	}

	
	/**
	 * @param parser
	 * @param patientHandler
	 * @param hl7ObsHandler
	 * @param hl7EncounterHandler
	 * @param hl7PatientHandler
	 * @param filters
	 */
	public HL7SocketHandler(ca.uhn.hl7v2.parser.Parser parser,
			PatientHandler patientHandler, HL7ObsHandler hl7ObsHandler,
			HL7EncounterHandler hl7EncounterHandler,
			HL7PatientHandler hl7PatientHandler, ArrayList<HL7Filter> filters) {

		super(parser, patientHandler, hl7ObsHandler, hl7EncounterHandler,
				hl7PatientHandler, filters);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openmrs.module.sockethl7listener.HL7SocketHandler#processMessage(
	 * ca.uhn.hl7v2.model.Message)
	 */
	@Override
	public Message processMessage(Message message,
			HashMap<String, Object> parameters) throws ApplicationException {
		
		AdministrationService adminService = Context.getAdministrationService();
		parameters.put(PROCESS_HL7_CHECKIN_START, new java.util.Date());
		boolean filterDuplicateCheckin = false;
		Context.openSession();
		String filterDuplicateRegistrationStr = adminService.getGlobalProperty(GLOBAL_PROPERTY_FILTER_ON_PRIOR_CHECKIN);
		if (filterDuplicateRegistrationStr != null && filterDuplicateRegistrationStr.equalsIgnoreCase(TRUE)){
			filterDuplicateCheckin = true;
		}
		Context.closeSession();
		
		String incomingMessageString = null;
		Segment inboundHeader = null;
		Message ackMessage = null;
		Message originalMessage = message;
		// switch message version and type to values for default hl7 handlers
		if (message instanceof ca.uhn.hl7v2.model.v22.message.ADT_A04 
				|| message instanceof ca.uhn.hl7v2.model.v24.message.ADT_A01) {
			try {
				if (message instanceof ca.uhn.hl7v2.model.v24.message.ADT_A01){
					ca.uhn.hl7v2.model.v24.message.ADT_A01 adt = (ca.uhn.hl7v2.model.v24.message.ADT_A01) message;
					adt.getMSH().getVersionID().getVersionID().setValue(HL7_VERSION_2_5);
					adt.getMSH().getMessageType().getTriggerEvent().setValue(HL7_MESSAGE_TYPE_A01);
					}
				
				if (message instanceof ca.uhn.hl7v2.model.v22.message.ADT_A04){
					ca.uhn.hl7v2.model.v22.message.ADT_A04 adt = (ca.uhn.hl7v2.model.v22.message.ADT_A04) message;
					adt.getMSH().getVersionID().setValue(HL7_VERSION_2_5);
					adt.getMSH().getMessageType().getTriggerEvent().setValue(HL7_MESSAGE_TYPE_A01);
				}
				
				incomingMessageString = this.parser.encode(message);
				message = this.parser.parse(incomingMessageString);
			
			} catch (Exception e) {
				Error error = new Error(ChirdlUtilConstants.ERROR_LEVEL_FATAL, ChirdlUtilConstants.ERROR_HL7_PARSING,
						"Error parsing the McKesson checkin hl7 "
								+ e.getMessage(),
						org.openmrs.module.chirdlutil.util.Util
								.getStackTrace(e), new Date(), null);
				ChirdlUtilBackportsService chirdlutilbackportsService = Context
						.getService(ChirdlUtilBackportsService.class);

				chirdlutilbackportsService.saveError(error);
				String mckessonParseErrorDirectory = IOUtil
						.formatDirectoryName(adminService
								.getGlobalProperty(GLOBAL_PROPERTY_PARSE_ERROR_DIRECTORY));
				if (mckessonParseErrorDirectory != null) {
					String filename = "r" + Util.archiveStamp() + ".hl7";

					FileOutputStream outputFile = null;

					try {
						outputFile = new FileOutputStream(
								mckessonParseErrorDirectory + "/" + filename);
					} catch (FileNotFoundException e1) {
						log.error("Could not find file: " + mckessonParseErrorDirectory + "/" + filename);
					}
					if (outputFile != null) {
						try {

							ByteArrayInputStream input = new ByteArrayInputStream(
									incomingMessageString.getBytes());
							IOUtil.bufferedReadWrite(input, outputFile);
							outputFile.flush();
							outputFile.close();
						} catch (Exception e1) {
							try {
								outputFile.flush();
								outputFile.close();
							} catch (Exception e2) {
							}
							log.error("There was an error writing the dump file");
							log.error(e1.getMessage());
							log.error(Util.getStackTrace(e));
						}
					}
				}
				try {
					//Return an ACK response instead of defaulting to the HAPI error response.
					inboundHeader = (Segment) originalMessage.get(originalMessage.getNames()[0]);
					ackMessage = makeACK(inboundHeader);
				} catch (Exception e2) {
					logger.error("Error sending an ack response after a parsing exception. " +
							e2.getMessage());
					logger.error(org.openmrs.module.chirdlutil.util.Util
							.getStackTrace(e2));
					ackMessage = originalMessage;
				}
				return ackMessage;
			}
		}

		try {		
			incomingMessageString = this.parser.encode(message);
			message.addNonstandardSegment(ZPV_SEGMENT);
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(org.openmrs.module.chirdlutil.util.Util
					.getStackTrace(e));
		}

		if (this.hl7EncounterHandler instanceof org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25) {
			String printerLocation = ((org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25) this.hl7EncounterHandler)
					.getPrinterLocation(message, incomingMessageString);
			String locationString = ((org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25) hl7EncounterHandler)
					.getLocation(message);

			if ((printerLocation != null && printerLocation.equals(PRINTER_LOCATION_FOR_SHOTS))||
					!(isValidAge(message, printerLocation, locationString)) ||
					(filterDuplicateCheckin && priorCheckinExists(message))) {

				try {
					inboundHeader = (Segment) originalMessage.get(originalMessage.getNames()[0]);
					ackMessage = makeACK(inboundHeader);
				} catch (Exception e) {
					log.error("Exception creating ACK for registration.", e);
					return originalMessage;
				}
				return ackMessage;
			}
			
		}
		return super.processMessage(message, parameters);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.sockethl7listener.HL7SocketHandler#processEncounter(java.lang.String, org.openmrs.Patient, java.util.Date, org.openmrs.Encounter, org.openmrs.module.sockethl7listener.Provider, java.util.HashMap)
	 */
	@Override
	public org.openmrs.Encounter processEncounter(String incomingMessageString,
			Patient p, Date encDate, org.openmrs.Encounter newEncounter,
			Provider provider, HashMap<String, Object> parameters) {
		ChirdlUtilBackportsService chirdlutilbackportsService = Context
				.getService(ChirdlUtilBackportsService.class);
		org.openmrs.Encounter encounter = super.processEncounter(
				incomingMessageString, p, encDate, newEncounter, provider,
				parameters);
		
		if (encounter == null){
			//Encounter will be null if encounter was not created or
			//it was a duplicate encounter.
			return null;
		}
		// store the encounter id with the session
		Integer encounterId = encounter.getEncounterId();
		getSession(parameters).setEncounterId(encounterId);
		chirdlutilbackportsService.updateSession(getSession(parameters));
		if (incomingMessageString == null) {
			return encounter;
		}

		LocationService locationService = Context.getLocationService();

		String locationString = null;
		Date appointmentTime = null;
		String planCode = null;
		String carrierCode = null;
		String printerLocation = null;
		String insuranceName = null;
		Message message;
		
			try {
				message = this.parser.parse(incomingMessageString);
				EncounterService encounterService = Context
						.getService(EncounterService.class);
				encounter = encounterService.getEncounter(encounter
						.getEncounterId());
				if (this.hl7EncounterHandler instanceof org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25) {
					locationString = ((org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25) this.hl7EncounterHandler)
							.getLocation(message);

					appointmentTime = ((org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25) this.hl7EncounterHandler)
							.getAppointmentTime(message);

					planCode = ((org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25) this.hl7EncounterHandler)
							.getInsurancePlan(message);

					carrierCode = ((org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25) this.hl7EncounterHandler)
							.getInsuranceCarrier(message);

					printerLocation = ((org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25) this.hl7EncounterHandler)
							.getPrinterLocation(message, incomingMessageString);
					
					insuranceName = ((org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25) this.hl7EncounterHandler)
							.getInsuranceName(message);
				}
			} catch (EncodingNotSupportedException e) {
				log.error("Encoding not supported when parsing incoming message.", e);
			} catch (HL7Exception e) {
				log.error("HL7 version not supported when parsing incoming message", e);
			} catch (Exception e){
				log.error("Exception getting encounter information from the incoming message", e);
			}

		

		EncounterService encounterService = Context
				.getService(EncounterService.class);
		encounter = encounterService.getEncounter(encounterId);
		Encounter chicaEncounter = (org.openmrs.module.chica.hibernateBeans.Encounter) encounter;

		chicaEncounter.setInsurancePlanCode(planCode);
		chicaEncounter.setInsuranceCarrierCode(carrierCode);
		chicaEncounter.setScheduledTime(appointmentTime);
		chicaEncounter.setPrinterLocation(printerLocation);

		Location location = null;

		if (locationString != null) {
			location = locationService.getLocation(locationString);

			if (location == null) {
				location = new Location();
				location.setName(locationString);
				locationService.saveLocation(location);
				logger.warn("Location '" + locationString
						+ "' does not exist in the Location table."
						+ "a new location was created for '" + locationString
						+ "'");
			}
		}

		chicaEncounter.setLocation(location);
		chicaEncounter.setInsuranceSmsCode(null);

		// This code must come after the code that sets the encounter values
		// because the states can't be created until the locationTagId and
		// locationId have been set
		State state = chirdlutilbackportsService
				.getStateByName(STATE_CLINIC_REGISTRATION);
		PatientState patientState = chirdlutilbackportsService
				.addPatientState(p, state, getSession(parameters)
						.getSessionId(), getLocationTagId(chicaEncounter),
						getLocationId(chicaEncounter), null);
		patientState.setStartTime(chicaEncounter.getEncounterDatetime());
		patientState.setEndTime(chicaEncounter.getEncounterDatetime());
		chirdlutilbackportsService.updatePatientState(patientState);

		state = chirdlutilbackportsService
				.getStateByName(STATE_HL7_CHECKIN);
		patientState = chirdlutilbackportsService
				.addPatientState(p, state, getSession(parameters)
						.getSessionId(), getLocationTagId(chicaEncounter),
						getLocationId(chicaEncounter), null);
		Date processCheckinHL7Start = (Date) parameters
				.get(PROCESS_HL7_CHECKIN_START);
		Date processCheckinHL7End = (Date) parameters
				.get(PROCESS_HL7_CHECKIN_END);
		patientState.setStartTime(processCheckinHL7Start);
		patientState.setEndTime(processCheckinHL7End);
		chirdlutilbackportsService.updatePatientState(patientState);

		state = chirdlutilbackportsService.getStateByName(STATE_QUERY_ALIAS);
		patientState = chirdlutilbackportsService
				.addPatientState(p, state, getSession(parameters)
						.getSessionId(), getLocationTagId(chicaEncounter),
						getLocationId(chicaEncounter), null);
		Date queryKiteAliasStart = (Date) parameters.get(PARAMETER_QUERY_ALIAS_START);
		if (queryKiteAliasStart == null){
			queryKiteAliasStart = new java.util.Date();
		}
		Date queryKiteAliasEnd = (Date) parameters.get(PARAMETER_QUERY_ALIAS_STOP);
		if (queryKiteAliasEnd == null){
			queryKiteAliasEnd = new java.util.Date();
		}
		
		patientState.setStartTime(queryKiteAliasStart);
		patientState.setEndTime(queryKiteAliasEnd);
		chirdlutilbackportsService.updatePatientState(patientState);

		encounterService.saveEncounter(chicaEncounter);
		ConceptService conceptService = Context.getConceptService();
		Concept concept = conceptService.getConceptByName(CONCEPT_INSURANCE_NAME);
		if (insuranceName != null){
			org.openmrs.module.chirdlutil.util.Util.saveObs(p, concept, encounterId, insuranceName,encDate);
		}else {
			log.error("Insurance Name is null for patient: " + p.getPatientId());
		}
		return encounter;
	}

	private Session getSession(HashMap<String, Object> parameters) {
		Session session = (Session) parameters.get(PARAMETER_SESSION);
		if (session == null) {
			ChirdlUtilBackportsService chirdlutilbackportsService = Context
					.getService(ChirdlUtilBackportsService.class);
			session = chirdlutilbackportsService.addSession();
			parameters.put(PARAMETER_SESSION, session);
		}
		return session;
	}

	/**
	 * Adds new name and sets most recent name to preferred. If the hl7 name
	 * already exists the date created is updated to the encounter date. When
	 * sorted the name with the latest date will be set as the preferred name
	 * 
	 * @param currentPatient
	 * @param hl7Patient
	 * @param encounterDate
	 * @should update date created if name already exists
	 */
	void addName(Patient currentPatient, Patient newPatient, Date encounterDate) {

		/*
		 * Condition where newest hl7 name matches an older existing name ( not
		 * currently the preferred name). OpenMRS equality checks will detect it
		 * as equal and not add the name in addName(). So, that name will never
		 * have an updated date, and will not get set as the preferred name.
		 * Then, the wrong name will be displayed on the form.
		 */
		try{
			PersonName newName = newPatient.getPersonName();
			if (newName == null
					|| (StringUtils.isBlank(newName.getFamilyName())
							&& StringUtils.isBlank(newName.getMiddleName())
							&& StringUtils.isBlank(newName.getGivenName())
							&& StringUtils.isBlank(newName.getFamilyName2())
							&& StringUtils.isBlank(newName.getFullName())
							&& StringUtils.isBlank(newName.getPrefix())
							&& StringUtils.isBlank(newName.getFamilyNamePrefix()) 
							&& StringUtils.isBlank(newName.getFamilyNameSuffix()))) {
				return;
			}

			boolean found = false;

			for (PersonName pn : currentPatient.getNames()) {
				if (!found && pn.equalsContent(newName)) {
					pn.setDateCreated(encounterDate);
					found = true;
					break;
				}

			}

			if (newName.getUuid() == null) {
				UUID uuid = UUID.randomUUID();
				newName.setUuid(uuid.toString());
			}

			currentPatient.addName(newName);
			Set<PersonName> names = currentPatient.getNames();

			// reset all addresses preferred status
			for (PersonName name : names) {
				name.setPreferred(false);
			}

			// Sort the list of names based on date
			List<PersonName> nameList = new ArrayList<PersonName>(names);

			Collections.sort(nameList, new Comparator<PersonName>() {
				public int compare(PersonName n1, PersonName n2) {
					Date date1 = n1.getDateCreated();
					Date date2 = n2.getDateCreated();
					return date1.compareTo(date2) > 0 ? 0 : 1;
				}
			});


			if (nameList.size() > 0 && nameList.get(0) != null) {
				// set latest to preferred
				nameList.get(0).setPreferred(true);
				Set<PersonName> nameSet = new TreeSet<PersonName>(nameList);
				if (nameSet.size() > 0) {
					currentPatient.getNames().clear();
					currentPatient.getNames().addAll(nameSet);
				}
			}


		} catch (Exception e) {
			log.error("Error updating patient name. MRN: "
					+ newPatient.getPatientIdentifier(), e);
		}
	}

	/**
	 * Adds new address and sets most recent address to preferred based on
	 * encounter time.
	 * 
	 * @param currentPatient
	 * @param hl7Patient
	 * @param encounterDate
	 * @should set latest address to preferred and add to addresses
	 */
	public void addAddress(Patient currentPatient, Patient newPatient,
			Date encounterDate) {

		PersonAddress newAddress = newPatient.getPersonAddress();

		try {
			if (newAddress == null
					|| (StringUtils.isBlank(newAddress.getAddress1())
							&& StringUtils.isBlank(newAddress.getAddress2())
							&& StringUtils.isBlank(newAddress.getCityVillage())
							&& StringUtils.isBlank(newAddress
									.getStateProvince())
							&& StringUtils.isBlank(newAddress.getCountry())
							&& StringUtils.isBlank(newAddress.getPostalCode())
							&& StringUtils.isBlank(newAddress.getCountyDistrict()) 
							&& StringUtils.isBlank(newAddress.getStateProvince()))) {
				return;
			}

			boolean found = false;

			for (PersonAddress pa : currentPatient.getAddresses()) {
				if (!found && pa.equalsContent(newAddress)) {
					pa.setDateCreated(encounterDate);
					found = true;
					break;
				}
			}

			if (!found) {
				PersonAddress address = newPatient.getPersonAddress();
				if (address.getUuid() == null) {
					UUID uuid = UUID.randomUUID();
					address.setUuid(uuid.toString());
				}
				address.setDateCreated(encounterDate);
				currentPatient.addAddress(address);
			}

			// reset all addresses preferred status
			Set<PersonAddress> addresses = currentPatient.getAddresses();
			for (PersonAddress address : addresses) {
				address.setPreferred(false);
			}

			// Sort the list of names based on date
			List<PersonAddress> addressList = new ArrayList<PersonAddress>(
					addresses);

			Collections.sort(addressList, new Comparator<PersonAddress>() {
				public int compare(PersonAddress a1, PersonAddress a2) {
					Date date1 = a1.getDateCreated();
					Date date2 = a2.getDateCreated();
					return date1.compareTo(date2) > 0 ? 0 : 1;
				}
			});

			if (addressList.size() > 0 && addressList.get(0) != null) {
				// set latest to preferred
				addressList.get(0).setPreferred(true);
				Set<PersonAddress> addressSet = new TreeSet<PersonAddress>(addressList);
				if (addressSet.size() > 0) {
					currentPatient.getAddresses().clear();
					currentPatient.getAddresses().addAll(addressSet);
				}

			}
		} catch (Exception e) {
			log.error("Error adding addresses to patient MRN: " + newPatient.getPatientIdentifier(), e);

		}

	}

	/**
	 * Add the new SSN from the hl7 message to the patient's identifiers.If SSN
	 * does not exist for any patients, add the identifier. If another patient
	 * uses the same SSN, store the SSN as an attribute.
	 * 
	 * @param currentPatient
	 * @param hl7Patient
	 * @param encounterDate
	 * @should add SSN identifier and add to attributes if duplicate
	 */
	private void addSSN(Patient currentPatient, Patient hl7Patient,
			Date encounterDate) {

		PatientService patientService = Context.getPatientService();

		PatientIdentifier newSSN = hl7Patient.getPatientIdentifier(ChirdlUtilConstants.IDENTIFIER_TYPE_SSN);
		PatientIdentifier currentSSN = currentPatient
				.getPatientIdentifier(ChirdlUtilConstants.IDENTIFIER_TYPE_SSN);

		//hl7 has no SSN. Or hl7 SSN is the same as existing SSN
		if (newSSN == null
				|| newSSN.getIdentifier() == null
				|| (currentSSN != null && currentSSN.getIdentifier()
						.equalsIgnoreCase(newSSN.getIdentifier()))) {
			return;
		}

		PersonAttributeType personAttrType = Context.getPersonService()
				.getPersonAttributeTypeByName(ChirdlUtilConstants.PERSON_ATTRIBUTE_SSN);

		// If another patient owns the SSN, do NOT void and add identifier
		// Instead, store the attempted SSN as an attribute for record

		List<Patient> lookupPatients = patientService.getPatients(null, newSSN
				.getIdentifier(), null, true);
		if (lookupPatients != null && lookupPatients.size() > 0) {
			if (personAttrType != null) {
				PersonAttribute personAttr = new PersonAttribute(
						personAttrType, newSSN.getIdentifier());
				currentPatient.addAttribute(personAttr);
			}
			return;
		}

		if (currentSSN == null) {
			// if patient has no SSN.
			currentPatient.addIdentifier(newSSN);
		} else {
			// if patient has a different SSN
				currentPatient.getPatientIdentifier(ChirdlUtilConstants.IDENTIFIER_TYPE_SSN).setVoided(true);
				currentPatient.addIdentifier(newSSN);
		}


	}

	/**
	 * Add religion from the hl7 patient to the existing patient attibutes
	 * 
	 * @param currentPatient
	 * @param hl7Patient
	 * @param encounterDate
	 * @should add religion attribute
	 */
	private void addReligion(Patient currentPatient, Patient hl7Patient,
			Date encounterDate) {
		PersonAttribute newReligionAttr = hl7Patient
			.getAttribute(ChirdlUtilConstants.PERSON_ATTRIBUTE_RELIGION);
		PersonAttribute currentReligionAttr = currentPatient
			.getAttribute(ChirdlUtilConstants.PERSON_ATTRIBUTE_RELIGION);
		if (newReligionAttr == null || newReligionAttr.getValue() == null
				|| newReligionAttr.getValue().equals(EMPTY_STRING)){
			return;
		}
		String newReligion = newReligionAttr.getValue();
		//if current attr does not exist or is different than hl7, need to update
		if (currentReligionAttr == null ||
				!currentReligionAttr.getValue().equalsIgnoreCase(
						newReligion)) {
			currentPatient.addAttribute(newReligionAttr);
		}

	}

	/**
	 * 
	 * Add marital status from the hl7 patient to the existing patient
	 * attributes.
	 * 
	 * @param currentPatient
	 * @param hl7Patient
	 * @param encounterDate
	 *
	 */

	private void addMaritalStatus(Patient currentPatient, Patient hl7Patient,
			Date encounterDate) {
		PersonAttribute newMaritalStatAttr = hl7Patient
			.getAttribute(ChirdlUtilConstants.PERSON_ATTRIBUTE_MARITAL_STATUS);
		PersonAttribute currentMaritalStatAttr = currentPatient
			.getAttribute(ChirdlUtilConstants.PERSON_ATTRIBUTE_MARITAL_STATUS);

		if (newMaritalStatAttr == null || newMaritalStatAttr.getValue() == null
				|| newMaritalStatAttr.getValue().equals(EMPTY_STRING)) {
			return;
		}
		String newMaritalStat = newMaritalStatAttr.getValue();
		if ( currentMaritalStatAttr == null 
				|| !currentMaritalStatAttr.getValue().equalsIgnoreCase(newMaritalStat)) {
			currentPatient.addAttribute(newMaritalStatAttr);
		}

	}

	/**
	 * Add maiden name from hl7 patient to the existing patient attributes.
	 * Openmrs addAttribute() voids previous attribute.
	 * 
	 * @param currentPatient
	 * @param hl7Patient
	 * @param encounterDate
	 */
	private void addMaidenName(Patient currentPatient, Patient hl7Patient,
			Date encounterDate) {
		PersonAttribute newMaidenNameAttr = hl7Patient
				.getAttribute(ChirdlUtilConstants.PERSON_ATTRIBUTE_MAIDEN_NAME);
		PersonAttribute currentMaidenNameAttr = currentPatient
				.getAttribute(ChirdlUtilConstants.PERSON_ATTRIBUTE_MAIDEN_NAME);
		
		if (newMaidenNameAttr == null || newMaidenNameAttr.getValue() == null
				|| newMaidenNameAttr.getValue().equals(EMPTY_STRING)){
			return;
		}
		String newMaidenName = newMaidenNameAttr.getValue();
		
		if (currentMaidenNameAttr == null
					|| currentMaidenNameAttr.getValue() == null
					|| !currentMaidenNameAttr.getValue().equalsIgnoreCase(
							newMaidenName)) {
				currentPatient.addAttribute(newMaidenNameAttr);
		}
		
	}

	/**
	 * Adds hl7 Next of Kin from NK1 hl7 segment to existing patient attributes.
	 * Openmrs addAttribute() voids previous attribute
	 * 
	 * @param currentPatient
	 * @param hl7Patient
	 * @param encounterDate
	 */
	private void addNK(Patient currentPatient, Patient hl7Patient,
			Date encounterDate) {
		PersonAttribute newNextOfKinNameAttr = hl7Patient
				.getAttribute(ChirdlUtilConstants.PERSON_ATTRIBUTE_NEXT_OF_KIN);
		PersonAttribute currentNextOfKinNameAttr = currentPatient
				.getAttribute(ChirdlUtilConstants.PERSON_ATTRIBUTE_NEXT_OF_KIN);

		if (newNextOfKinNameAttr == null || newNextOfKinNameAttr.getValue() == null
				|| newNextOfKinNameAttr.getValue().equals(EMPTY_STRING)){
			return;
		}
		
		String newNextOfKinName = newNextOfKinNameAttr.getValue();
		
		if (currentNextOfKinNameAttr == null
				|| currentNextOfKinNameAttr.getValue() == null
				|| !currentNextOfKinNameAttr.getValue().equalsIgnoreCase(
						newNextOfKinName)) {
					currentPatient.addAttribute(newNextOfKinNameAttr);
				}
	}

		
	

	/**
	 * Updates telephone number from PID segment of hl7 to existing patient.
	 * Openmrs addAttribute() voids previous attribute
	 * 
	 * @param currentPatient
	 * @param hl7Patient
	 * @param encounterDate
	 */
	private void addTelephoneNumber(Patient currentPatient, Patient hl7Patient,
			Date encounterDate) {
		PersonAttribute hl7TelNumAttr = hl7Patient
				.getAttribute(ChirdlUtilConstants.PERSON_ATTRIBUTE_TELEPHONE);
		PersonAttribute currentTelNumAttr = currentPatient
				.getAttribute(ChirdlUtilConstants.PERSON_ATTRIBUTE_TELEPHONE);

		if (hl7TelNumAttr == null || hl7TelNumAttr.getValue() == null 
				|| hl7TelNumAttr.getValue().equals(EMPTY_STRING)){
			return;
		}
		String newTelNumName = hl7TelNumAttr.getValue();
		if (currentTelNumAttr == null || currentTelNumAttr.getValue() == null
				||  !currentTelNumAttr.getValue().equals( newTelNumName)) {
					currentPatient.addAttribute(hl7TelNumAttr);
				}
		}

	/**
	 * Updates hl7 citizenship value to attributes. 
	 * @param currentPatient
	 * @param hl7Patient
	 * @param encounterDate
	 */
	private void AddCitizenship(Patient currentPatient, Patient hl7Patient,Date encounterDate){

		PersonAttribute currentCitizenshipAttr = currentPatient.getAttribute(ChirdlUtilConstants.PERSON_ATTRIBUTE_CITIZENSHIP);
		PersonAttribute hl7CitizenshipAttr = hl7Patient.getAttribute(ChirdlUtilConstants.PERSON_ATTRIBUTE_CITIZENSHIP);

		if (hl7CitizenshipAttr == null || hl7CitizenshipAttr.getValue() == null 
				|| hl7CitizenshipAttr.getValue().trim().equals(EMPTY_STRING)){
			return;
		}

		String hl7Citizenship = hl7CitizenshipAttr.getValue();

		if (currentCitizenshipAttr == null || currentCitizenshipAttr.getValue() == null
				|| ! currentCitizenshipAttr.getValue().equals(hl7Citizenship)){
			currentPatient.addAttribute(hl7CitizenshipAttr);
		}
	}
	
	/**
	 * Updates Race attribute from hl7 value. Literal value, not mapped.
	 * @param currentPatient
	 * @param hl7Patient
	 * @param encounterDate
	 */
	private void AddRace(Patient currentPatient, Patient hl7Patient, Date encounterDate){
		PersonAttribute currentRaceAttr = currentPatient.getAttribute(ChirdlUtilConstants.PERSON_ATTRIBUTE_RACE);
		PersonAttribute hl7RaceAttr = hl7Patient.getAttribute(ChirdlUtilConstants.PERSON_ATTRIBUTE_RACE);
		
		if (hl7RaceAttr == null || hl7RaceAttr.getValue() == null 
				|| hl7RaceAttr.getValue().trim().equals(EMPTY_STRING)){
			return;
		}
		
		String hl7Race = hl7RaceAttr.getValue();
		
		if (currentRaceAttr == null || currentRaceAttr.getValue() == null
			|| !currentRaceAttr.getValue().equals(hl7Race)){
			currentPatient.addAttribute(hl7RaceAttr);
		}
		
		
	}
	
	/**
	 * Updates Gender without overwriting known gender with "u"
	 * @param currentPatient
	 * @param hl7Patient
	 * @param encounterDate
	 */
	private void addGender(Patient currentPatient, Patient hl7Patient){
		String currentGender = currentPatient.getGender();
		String hl7Gender = hl7Patient.getGender();

		if (currentGender != null && 
				(currentGender.equalsIgnoreCase("M")|| currentGender.equalsIgnoreCase("F") )
				&& (hl7Gender == null || hl7Gender.trim().equalsIgnoreCase(EMPTY_STRING) 
						||hl7Gender.trim().equalsIgnoreCase("U"))
			){
			currentPatient.setGender(currentGender);
			return;
		}
		
		currentPatient.setGender(hl7Gender);
		return ;
			
	}
	
	
	/**
	 * Updates the existing patient's identifier to the new value from HL7 or manual checkin.
	 * Since this is a matched patient, if MRNs do not match: 
	 * Add the new hl7 MRN and set as preferred,  void the old MRN,  
	 * and write an error to the log.
	 * 
	 * @param existingPatient - Patient already exists in our records and was matched to the hl7 or manual checkin patient
	 * @param newPatient - Patient created from content of HL7 message or manual checkin 
	 * @param encounterDate
	 * @should remove leading zeros
	 */
	public void addMRN(Patient existingPatient, Patient newPatient, Date encounterDate){
		
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		PatientService patientService = Context.getPatientService();
		String newMRN = null;
		String existingMRN = null;
	
		try {

			//Get the existing preferred, non-voided identifier for comparison  
			PatientIdentifier existingPatientIdentifier = existingPatient.getPatientIdentifier(); 

			PatientIdentifier newPatientIdentifier= newPatient.getPatientIdentifier();

			existingMRN = existingPatientIdentifier.getIdentifier();
			newMRN = null;

			PatientIdentifierType identifierType = patientService.getPatientIdentifierTypeByName(ChirdlUtilConstants.IDENTIFIER_TYPE_MRN);
			List<PatientIdentifierType> identifierTypes = new ArrayList<PatientIdentifierType>();
			identifierTypes.add(identifierType);

			//If new MRN does not exist or matches the existing MRN, no need to update.
			//If the only difference is a leading 0, do not return. MRN must be updated.
			if (newPatientIdentifier == null
					|| (newMRN = newPatientIdentifier.getIdentifier()) == null
					|| existingMRN.trim().equals(newMRN.trim()) ){
				return;
			}

			//New MRNs will not have a leading zero. 
			try {
				if (Util.removeLeadingZeros(existingMRN.trim()).equals(Util.removeLeadingZeros(newMRN.trim()))){
					existingPatientIdentifier.setVoidReason(VOID_REASON_MRN_LEADING_ZERO_CORRECTION);
					Error error = new Error(ChirdlUtilConstants.ERROR_LEVEL_WARNING, ChirdlUtilConstants.ERROR_MRN_VALIDITY,
							"Leading Zero Correction." 
									+ "Previous MRN: " + existingMRN + " New MRN: " + newMRN,
									"The existing MRN and new MRN differ by only the leading zero. Save the MRN w/o leading zero. ", new Date(), null);
					chirdlutilbackportsService.saveError(error);
				} else {
					existingPatientIdentifier.setVoidReason(VOID_REASON_MRN_CORRECTION);
					Error error = new Error(ChirdlUtilConstants.ERROR_LEVEL_ERROR, ChirdlUtilConstants.ERROR_MRN_VALIDITY,
							"MRN correction required! Contact downstream data warehouse about possible corrupted data." 
									+ "Invalid MRN: " + existingMRN + " New MRN: " + newMRN,
									"HL7 or manual checkin indicate that an existing patient has an invalid MRN. ", new Date(), null);
					chirdlutilbackportsService.saveError(error);
				}
			} catch (Exception e) {

				log.error("Insert to error table failed. Error category = " + ChirdlUtilConstants.ERROR_MRN_VALIDITY 
						+  "Existing MRN: " + existingMRN + "; New MRN: " + newMRN, e);
			}
			//void the existing identifier

			existingPatientIdentifier.setPreferred(false);
			existingPatientIdentifier.setVoided(true);
			existingPatientIdentifier.setVoidedBy(Context.getAuthenticatedUser());
			existingPatientIdentifier.setDateVoided(new Date());

			//Create the new identifier object and add to existing patient
			PatientIdentifier newIdentifier = new PatientIdentifier();
			newIdentifier.setIdentifier(newMRN);
			newIdentifier.setIdentifierType( patientService.getPatientIdentifierTypeByName(ChirdlUtilConstants.IDENTIFIER_TYPE_MRN));
			newIdentifier.setLocation(newPatientIdentifier.getLocation());
			newIdentifier.setPatient(existingPatient);
			newIdentifier.setPreferred(true);
			newIdentifier.setCreator(Context.getAuthenticatedUser());
			newIdentifier.setDateCreated(new Date());
			existingPatient.addIdentifier(newIdentifier);

		} catch (Exception e) {
			log.error("Exception adding new MRN to existing patient. Existing MRN: " 
					+ existingMRN + "; New MRN: " + newMRN, e);
		}

	}
	
	
	/**
	 * Checks if patient from this hl7 message already has an encounter today.
	 * The message is saved to the sockethl7listener_patient_message table for record.
	 * @param message
	 * @return 
	 */
	private boolean priorCheckinExists(Message hl7message) {

		boolean encounterFound = true;

		org.openmrs.api.EncounterService encounterService = Context.getEncounterService();
		AdministrationService adminService = Context.getService(AdministrationService.class);
		ChirdlUtilBackportsService chirdlutilbackportsService  = Context.getService(ChirdlUtilBackportsService.class);

		try {

			Context.openSession();
			Context.authenticate(adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROPERTY_SCHEDULER_USERNAME), 
					adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROPERTY_SCHEDULER_PASSWORD));
			Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_IDENTIFIER_TYPES);
			
			//Pull the patient from the hl7 through identifiers
			Patient patient = getPatientFromMessage(hl7message);
			if (patient == null) {
				return !encounterFound;
			}
		
			//Get all encounters for that patient from the start of the day
			Date startOfDay = DateUtils.truncate(new Date(), Calendar.DATE);
			List<org.openmrs.Encounter> encounters = encounterService.getEncounters(patient, null, startOfDay, null,
					null, null, null, false);
			
			if (encounters == null || encounters.size() == 0){
				return !encounterFound;
			}

			//Save the hl7 message and error
			this.saveMessage(hl7message, patient, false, true);
			Error error = new Error(ChirdlUtilConstants.ERROR_LEVEL_ERROR, ChirdlUtilConstants. ERROR_GENERAL,
						"An HL7 registration message arrived for a patient that is already checkied in.  MRN =  "
								+ patient.getPatientIdentifier().getIdentifier(), null, new Date(), null);
			chirdlutilbackportsService.saveError(error);

			return encounterFound;
		

		} catch (Exception e) {
			log.error(Util.getStackTrace(e));
		} finally {
			Context.closeSession();
		}

		return !encounterFound;

	}
	
	/**
	 * If there is no location tag attribute value or it is not numeric, check-in the patient.
	 * If the age limit for the location tag exists as a numeric value, and the patient's age is greater than or equal to that limit,
	 * do not check-in patient.
	 * @param message
	 * @param locationId
	 * @param locationTagId
	 * @return ageOk
	 */
	private boolean isValidAge(Message message, String printerLocation, String locationString){

		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		AdministrationService adminService = Context.getAdministrationService();
		LocationService locationService = Context.getLocationService();
		
		Context.openSession();
		Context.authenticate(adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROPERTY_SCHEDULER_USERNAME), 
				adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROPERTY_SCHEDULER_PASSWORD));
		Context.addProxyPrivilege(OpenmrsConstants.PRIV_VIEW_LOCATIONS);
		String attribute = ChirdlUtilConstants.LOC_TAG_ATTR_AGE_LIMIT_AT_CHECKIN;
		
		boolean ageOk = true;

		try {
			
			if (printerLocation == null || locationString == null){
				return ageOk;
			}
			
			LocationTag locationTag = locationService.getLocationTagByName(printerLocation);
			Location location = locationService.getLocation(locationString);
			if (locationTag == null || location == null){
				return ageOk;
			}
			
			LocationTagAttributeValue ageLimitAttributeValue = chirdlutilbackportsService
					.getLocationTagAttributeValue(locationTag.getLocationTagId(), attribute ,location.getLocationId());
			
			if (ageLimitAttributeValue == null ) {
				return ageOk;
			}
			
			String ageLimitString = ageLimitAttributeValue.getValue();
			Integer ageLimit = Integer.valueOf(ageLimitString);
			
			HL7PatientHandler25 patientHandler = new HL7PatientHandler25();
			Date dob = patientHandler.getBirthdate(message);
			int age = Util.getAgeInUnits(dob, new java.util.Date(), ChirdlUtilConstants.YEAR_ABBR);

			if (age >= ageLimit){
				//save the message
				Patient patient = this.getPatientFromMessage(message);
				saveMessage(message, patient, false, false);
				return !ageOk;
			}
			
			
		} catch (NumberFormatException e) {
			//String was either null, empty, or not a digit
			//No age limit value could be retrieved from attributes, so do not filter
			return ageOk;
		} catch (Exception e){
			log.error("Exception while verifying patient age. ", e);
		} finally {
			Context.closeSession();
		}

		return ageOk;

	}
	

	/**
	 * Saves the hl7 registration message to the sockethl7listener_patient_message table.
	 * If patient is new to the system, no patient id will be saved.
	 * @param message
	 * @param patient
	 * @param duplicateString
	 * @param duplcateEncounter
	 */
	private void saveMessage(Message message, Patient patient, boolean duplicateString, boolean duplcateEncounter){
		

		SocketHL7ListenerService sockethl7listenerService = Context.getService(SocketHL7ListenerService.class);
		Integer patientId = null;
		if (patient != null) {
			patientId = patient.getPatientId();
		}
		
		try {
			sockethl7listenerService.setHl7Message(patientId, null, this.parser.encode(message),
					duplicateString, duplcateEncounter, super.getPort());
		} catch (HL7Exception e) {
			log.error("Error saving HL7 registration message.", e);
		}
	}
	
	/**
	 * Pulls patient identifier from the hl7 message, looks up identifier in CHICA,
	 * and returns the patient with that identifier.  If the identifier does not exist, returns null.
	 * @param message
	 * @return Patient
	 */
	private Patient getPatientFromMessage(Message message) {

		try {
			PatientService patientService = Context.getPatientService();
			HL7PatientHandler25 patientHandler = new HL7PatientHandler25();
			for (PatientIdentifier identifier : patientHandler
					.getIdentifiers(message)) {

				List<PatientIdentifierType> identifierTypes = new ArrayList<PatientIdentifierType>();
				identifierTypes.add(identifier.getIdentifierType());

				List<Patient> patients = patientService.getPatients(null,
						identifier.getIdentifier(), identifierTypes, true);

				if (patients != null && patients.size() == 1) {
					return patients.get(0);
				}

			}
		} catch (APIException e) {
			log.error("Error if existing patient");
		}
		return null;
	}
	
	/**
	 * Pulls identifiers from message string, and merges the patients if needed
	 * @param mrn
	 * @param patient
	 * @param messageString
	 */
	public static void mergeAliases(String mrn, Patient patient, String messageString){


		PatientService patientService = Context.getPatientService();
		HL7PatientHandler25 patientHandler = new HL7PatientHandler25();
		PipeParser pipeParser = new PipeParser();

		try {
			pipeParser.setValidationContext(new NoValidation());
			String newMessageString = HL7ToObs.replaceVersion(messageString);
			Message newMessage = null;
			newMessage = pipeParser.parse(newMessageString);

			if (newMessage == null){
				return;
			}

			String identifier = patientHandler.getIdentiferString(newMessage);


			if (!identifier.contains(HYPHEN)){
				identifier = new StringBuffer(identifier).insert(identifier.length()-1, HYPHEN).toString();
			}
			if (Util.removeLeadingZeros(identifier).equalsIgnoreCase(Util.removeLeadingZeros(mrn))){
				return;
			}

			List<Patient> lookupPatients = patientService.getPatients(null,
					Util.removeLeadingZeros(identifier), null, false);

			if (lookupPatients != null && lookupPatients.size() > 0) {

				for (Patient currentPatient : lookupPatients){

					// only merge different patients
					if (!patient.getPatientId().equals(
							currentPatient.getPatientId())) {
						patientService.mergePatients(patient,
								currentPatient);

					}

				}

			}

		}catch (Exception e){
			log.error("Alias merge error for patient " + patient.getId(), e );
		}

	}
	
	
	
}
