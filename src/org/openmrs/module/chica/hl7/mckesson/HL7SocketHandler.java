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
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.hl7.mrfdump.HL7ToObs;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.EncounterAttribute;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.EncounterAttributeValue;
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

/**
 * @author msheley
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
	
	private static final String CONCEPT_INSURANCE_NAME = "InsuranceName";
	
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
				
				parameters.put(PROCESS_HL7_CHECKIN_END, new java.util.Date());

			}

		} catch (RuntimeException e) {
			log.error("Exception during patient lookup. ", e);
		}
		return resultPatient;

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
		
		List<Patient> lookupPatients = patientService.getPatientsByIdentifier(null, mrn,
				null, true); // CHICA-977 Use getPatientsByIdentifier() as a temporary solution to openmrs TRUNK-5089
		
		if (lookupPatients == null || lookupPatients.size() == 0){
			lookupPatients = patientService.getPatientsByIdentifier(null, LEADING_ZERO + mrn,
					null, true); // CHICA-977 Use getPatientsByIdentifier() as a temporary solution to openmrs TRUNK-5089
		}

		if (lookupPatients != null && lookupPatients.size() > 0) {
			return lookupPatients.iterator().next();
		}

		// Search by SSN
		PatientIdentifier ssnIdent = hl7Patient.getPatientIdentifier(ChirdlUtilConstants.IDENTIFIER_TYPE_SSN);
		if (ssnIdent != null) {
			String ssn = ssnIdent.getIdentifier();
			lookupPatients = patientService.getPatientsByIdentifier(null, ssn, null, true); // CHICA-977 Use getPatientsByIdentifier() as a temporary solution to openmrs TRUNK-5089
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
					UUID uuid = UUID.randomUUID();
					personAttr.setUuid(uuid.toString());
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
		addMRNEHR(currentPatient, hl7Patient, encounterDate); // DWE Epic_Eskenazi release 10/1/16
		addPatientAccountNumber(currentPatient, hl7Patient, encounterDate); // DWE CHICA-406
		updateEthnicity(currentPatient, hl7Patient, encounterDate); // DWE CHICA-706
		
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
			HashMap<String, Object> parameters)  {
		
		AdministrationService adminService = Context.getAdministrationService();
		parameters.put(PROCESS_HL7_CHECKIN_START, new java.util.Date());
		boolean filterDuplicateCheckin = false;
		boolean processMessageError = false;
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
				|| message instanceof ca.uhn.hl7v2.model.v24.message.ADT_A01
				|| message instanceof ca.uhn.hl7v2.model.v22.message.ADT_A08) {
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
				
				if (message instanceof ca.uhn.hl7v2.model.v22.message.ADT_A08){
					ca.uhn.hl7v2.model.v22.message.ADT_A08 adt = (ca.uhn.hl7v2.model.v22.message.ADT_A08) message;
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
					String filename = "r" + Util.archiveStamp() + ChirdlUtilConstants.FILE_EXTENSION_HL7;

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
					processMessageError  = true;
					//Return an ACK response instead of defaulting to the HAPI error response.
					inboundHeader = (Segment) originalMessage.get(originalMessage.getNames()[0]);
					ackMessage = org.openmrs.module.sockethl7listener.util.Util.makeACK(inboundHeader, processMessageError, null, null);
				} catch (Exception e2) {
					log.error("Error sending an ack response after a parsing exception. " , e2);
					ackMessage = originalMessage;
				}
				return ackMessage;
			}
		}

		try {		
			incomingMessageString = this.parser.encode(message);
			message.addNonstandardSegment(ZPV_SEGMENT);
		} catch (Exception e) {
			log.error("Error adding Z segment", e);
		}

		if (this.hl7EncounterHandler instanceof org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25) {
			String printerLocation = ((org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25) this.hl7EncounterHandler)
					.getPrinterLocation(message, incomingMessageString);
			String locationString = ((org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25) hl7EncounterHandler)
					.getLocation(message);

			if ((printerLocation != null && printerLocation.equals(PRINTER_LOCATION_FOR_SHOTS))||
					!(isValidAge(message, printerLocation, locationString)) ||
					(filterDuplicateCheckin && priorCheckinExists(message, locationString))) {

				try {
					processMessageError = false;
					inboundHeader = (Segment) originalMessage.get(originalMessage.getNames()[0]);
					ackMessage = org.openmrs.module.sockethl7listener.util.Util.makeACK(inboundHeader, processMessageError, null, null);
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
		String visitNumber = null;
		String originalLocation = null;
		Message message = null;
		
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

					// DWE CHICA-492 Parse insurance plan code from IN1-35 if this is IUH
					if(locationString.equals(ChirdlUtilConstants.LOCATION_RIIUMG))
					{
						planCode = ((org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25) this.hl7EncounterHandler)
								.getInsuranceCompanyPlan(message);
					}
					else
					{
						planCode = ((org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25) this.hl7EncounterHandler)
								.getInsurancePlan(message);
					}
				
					// DWE CHICA-492 Do not parse the carrier code if this is IUH
					if(!locationString.equals(ChirdlUtilConstants.LOCATION_RIIUMG))
					{
						carrierCode = ((org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25) this.hl7EncounterHandler)
								.getInsuranceCarrier(message);
					}

					printerLocation = ((org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25) this.hl7EncounterHandler)
							.getPrinterLocation(message, incomingMessageString);
					
					insuranceName = ((org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25) this.hl7EncounterHandler)
							.getInsuranceName(message);
					
					// DWE CHICA-633 Parse visit number from PV1-19 if this is not IUH
					if(!locationString.equals(ChirdlUtilConstants.LOCATION_RIIUMG))
					{
						visitNumber = ((org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25) this.hl7EncounterHandler)
								.getVisitNumber(message);
						
						if(visitNumber != null && !visitNumber.isEmpty())
						{
							storeEncounterAttributeAsValueText(encounter, ChirdlUtilConstants.ENCOUNTER_ATTRIBUTE_VISIT_NUMBER, visitNumber);
						}
						else
						{
							log.error("Unable to parse visit number for encounterId: " + encounter.getEncounterId());
						}		
					}
					
					// DWE CHICA-751 Parse the original location from the location description field
					// which is copied from PV1-3.1 to PV1-3.9 by Mirth
					originalLocation = ((org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25) this.hl7EncounterHandler)
							.getLocationDescription(message);
					if(originalLocation != null && !originalLocation.isEmpty())
					{
						storeEncounterAttributeAsValueText(encounter, ChirdlUtilConstants.ENCOUNTER_ATTRIBUTE_ORIGINAL_LOCATION, originalLocation);
					}
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
				log.warn("Location '" + locationString
						+ "' does not exist in the Location table. "
						+ "A new location was created for '" + locationString
						+ "'");
			}
		}

		chicaEncounter.setLocation(location);
		chicaEncounter.setInsuranceSmsCode(null);
		
		//DWE CLINREQ-130 Removed encounter parameter
		// CAUTION: If an encounter object is needed in this thread in the future, 
		// use caution when calling setters on the object.
		if(getNumOBXSegments(message) > 0){ // DWE CHICA-635 Added check to make sure the message contains OBX segments before starting the new thread
			saveHL7Obs(p, message, location, getSession(parameters), printerLocation);
		}

		// This code must come after the code that sets the encounter values
		// because the states can't be created until the locationTagId and
		// locationId have been set
		State state = chirdlutilbackportsService
				.getStateByName(STATE_CLINIC_REGISTRATION);
		PatientState patientState = chirdlutilbackportsService
				.addPatientState(p, state, getSession(parameters)
						.getSessionId(), org.openmrs.module.chica.util.Util.getLocationTagId(chicaEncounter),
						getLocationId(chicaEncounter), null);
		patientState.setStartTime(chicaEncounter.getEncounterDatetime());
		patientState.setEndTime(chicaEncounter.getEncounterDatetime());
		chirdlutilbackportsService.updatePatientState(patientState);

		state = chirdlutilbackportsService
				.getStateByName(STATE_HL7_CHECKIN);
		patientState = chirdlutilbackportsService
				.addPatientState(p, state, getSession(parameters)
						.getSessionId(), org.openmrs.module.chica.util.Util.getLocationTagId(chicaEncounter),
						getLocationId(chicaEncounter), null);
		Date processCheckinHL7Start = (Date) parameters
				.get(PROCESS_HL7_CHECKIN_START);
		Date processCheckinHL7End = (Date) parameters
				.get(PROCESS_HL7_CHECKIN_END);
		patientState.setStartTime(processCheckinHL7Start);
		patientState.setEndTime(processCheckinHL7End);
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

			if(!found){
				if (newName.getUuid() == null) {
					UUID uuid = UUID.randomUUID();
					newName.setUuid(uuid.toString());
				}
				
				currentPatient.addName(newName);
			}
			
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
					return date2.compareTo(date1);
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
					return date2.compareTo(date1);
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

		List<Patient> lookupPatients = patientService.getPatientsByIdentifier(null, newSSN
				.getIdentifier(), null, true); // CHICA-977 Use getPatientsByIdentifier() as a temporary solution to openmrs TRUNK-5089
		if (lookupPatients != null && lookupPatients.size() > 0) {
			if (personAttrType != null) {
				PersonAttribute personAttr = new PersonAttribute(
						personAttrType, newSSN.getIdentifier());
				UUID uuid = UUID.randomUUID();
				personAttr.setUuid(uuid.toString());
				currentPatient.addAttribute(personAttr);
			}
			return;
		}

		if (currentSSN == null) {
			// if patient has no SSN.
			UUID uuid = UUID.randomUUID();
			newSSN.setUuid(uuid.toString());
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
			UUID uuid = UUID.randomUUID();
			newReligionAttr.setUuid(uuid.toString());
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
			UUID uuid = UUID.randomUUID();
			newMaritalStatAttr.setUuid(uuid.toString());
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
			UUID uuid = UUID.randomUUID();
			newMaidenNameAttr.setUuid(uuid.toString());
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
					UUID uuid = UUID.randomUUID();
					newNextOfKinNameAttr.setUuid(uuid.toString());
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
					UUID uuid = UUID.randomUUID();
					hl7TelNumAttr.setUuid(uuid.toString());		
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
			UUID uuid = UUID.randomUUID();
			hl7CitizenshipAttr.setUuid(uuid.toString());	
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
			UUID uuid = UUID.randomUUID();
			hl7RaceAttr.setUuid(uuid.toString());
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
			newMRN = newPatientIdentifier.getIdentifier();

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

			
			Set<PatientIdentifier> currIdentifiers = existingPatient.getIdentifiers();
			
			//See if the identifier already exists
			//If it does, set it as preferred
			boolean foundMatchingMRN = false;
			
			for(PatientIdentifier identifier:currIdentifiers){
				String identifierStr = identifier.getIdentifier();
				if(identifier.getIdentifierType().getName().equals(ChirdlUtilConstants.IDENTIFIER_TYPE_MRN)&&
						identifierStr!=null && identifierStr.equals(newMRN)){
					identifier.setPreferred(true);
					foundMatchingMRN = true;
					//unvoid the existing identifier
					identifier.setVoided(false);
					identifier.setVoidedBy(null);
					identifier.setDateVoided(null);
					break;
				}
			}
			
			//Create the new identifier object and add to existing patient
			if (!foundMatchingMRN) {
				PatientIdentifier newIdentifier = new PatientIdentifier();
				newIdentifier.setIdentifier(newMRN);
				newIdentifier.setIdentifierType(patientService.getPatientIdentifierTypeByName(ChirdlUtilConstants.IDENTIFIER_TYPE_MRN));
				newIdentifier.setLocation(newPatientIdentifier.getLocation());
				newIdentifier.setPatient(existingPatient);
				newIdentifier.setPreferred(true);
				newIdentifier.setCreator(Context.getAuthenticatedUser());
				newIdentifier.setDateCreated(new Date());
				
				existingPatient.addIdentifier(newIdentifier);
			}

		} catch (Exception e) {
			log.error("Exception adding new MRN to existing patient. Existing MRN: " 
					+ existingMRN + "; New MRN: " + newMRN, e);
		}

	}
	
	/**
	 * Checks if patient from this hl7 message already has an encounter today at the same location.
	 * The message is saved to the sockethl7listener_patient_message table for record.
	 * @param hl7message
	 * @param locationString
	 * @return
	 */
	private boolean priorCheckinExists(Message hl7message, String locationString) {

		boolean encounterFound = true;

		org.openmrs.api.EncounterService encounterService = Context.getEncounterService();
		AdministrationService adminService = Context.getService(AdministrationService.class);
		ChirdlUtilBackportsService chirdlutilbackportsService  = Context.getService(ChirdlUtilBackportsService.class);
		LocationService locationService = Context.getLocationService();

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
			//CHICA-721 Only filter if new registration location is the same as the first registration location.
			//If a patient is registered at one clinic in error, and registered in another clinic afterward,
			//allow that checkin.
			Date startOfDay = DateUtils.truncate(new Date(), Calendar.DATE);
			Location location = locationService.getLocation(locationString);
			List<org.openmrs.Encounter> encounters = encounterService.getEncounters(patient, location, startOfDay, null,
					null, null, null, false);
			
			if (encounters == null || encounters.size() == 0){
				return !encounterFound;
			}
			
			//Save the hl7 message and error
			this.saveMessage(hl7message, patient, false, true);
			Error error = new Error(ChirdlUtilConstants.ERROR_LEVEL_ERROR, ChirdlUtilConstants. ERROR_GENERAL,
						"An HL7 registration message arrived for a patient at the same location that is already checked in.  MRN =  "
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
			try {
				sockethl7listenerService.setHl7Message(patientId, null, this.parser.encode(message),
						duplicateString, duplcateEncounter, super.getPort());
			} catch (HL7Exception e) {
				log.error("Error saving HL7 registration message.", e);
			}
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

				List<Patient> patients = patientService.getPatientsByIdentifier(null,
						identifier.getIdentifier(), identifierTypes, true); // CHICA-977 Use getPatientsByIdentifier() as a temporary solution to openmrs TRUNK-5089

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
	 * DWE CHICA-406
	 * Updates Patient Account Number attribute from hl7 value.
	 * @param currentPatient
	 * @param hl7Patient
	 * @param encounterDate
	 */
	private void addPatientAccountNumber(Patient currentPatient, Patient hl7Patient, Date encounterDate){
		PersonAttribute currentAccountNumberAttr = currentPatient.getAttribute(ChirdlUtilConstants.PERSON_ATTRIBUTE_PATIENT_ACCOUNT_NUMBER);
		PersonAttribute hl7AccountNumberAttr = hl7Patient.getAttribute(ChirdlUtilConstants.PERSON_ATTRIBUTE_PATIENT_ACCOUNT_NUMBER);
		
		if (hl7AccountNumberAttr == null || hl7AccountNumberAttr.getValue() == null 
				|| hl7AccountNumberAttr.getValue().trim().equals(EMPTY_STRING)){
			return;
		}
		
		if (currentAccountNumberAttr == null || currentAccountNumberAttr.getValue() == null
			|| !currentAccountNumberAttr.getValue().equals(hl7AccountNumberAttr.getValue())){
			UUID uuid = UUID.randomUUID();
			hl7AccountNumberAttr.setUuid(uuid.toString());
			currentPatient.addAttribute(hl7AccountNumberAttr);
		}
	}
	
	/**
	 * MES  CHICA-358
	 * Pulls identifiers from message string, searches for matching patients,
	 * and merges existing patients if needed.
	 * @param mrn
	 * @param patient - new patient
	 * @param messageString - mrf dump hl7 string
	 * @should merge existing patient into new patient
	 */
	public static void checkAlias(String mrn, Patient newPatient, String messageString){


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

			String identifier = patientHandler.getIdentifierString(newMessage);

			if (!identifier.contains(HYPHEN)){
				identifier = new StringBuffer(identifier).insert(identifier.length()-1, HYPHEN).toString();
			}
			if (Util.removeLeadingZeros(identifier).equalsIgnoreCase(Util.removeLeadingZeros(mrn))){
				return;
			}

			List<Patient> existingPatients = patientService.getPatientsByIdentifier(null,
					Util.removeLeadingZeros(identifier), null, false); // CHICA-977 Use getPatientsByIdentifier() as a temporary solution to openmrs TRUNK-5089
			
			for (Patient existingPatient : existingPatients){
				
				if (!existingPatient.getId().equals(newPatient.getId())){
					patientService.mergePatients(newPatient, existingPatient);
				}
			}
		
		}catch (Exception e){
			log.error("Alias merge error for patient " + newPatient.getId(), e );
		}
	}
	
	/**
	 * Stores the observations from the HL7 message to applicable data sources on a separate thread.
	 * 
	 * @param patient The patient to whom the observations will be attached
	 * @param message The HL7 message
	 * @param location The location of the encounter
	 * @param session The patient session
	 * @param printerLocation The printer location for the encounter
	 */
	private void saveHL7Obs(Patient patient, Message message, Location location, Session session, 
	                        String printerLocation) {
		Runnable hl7ObsRunnable = new HL7StoreObsRunnable(patient.getPatientId(), location.getLocationId(), 
			 session.getSessionId(), message, printerLocation);
		Thread hl7ObsThread = new Thread(hl7ObsRunnable);
		hl7ObsThread.start();
	}
	
	/**
	 * DWE CHICA-635
	 * Determine the number of OBX segments in the message
	 * This method handles the default OBX that HAPI adds and excludes it from the count
	 * 
	 * @param message
	 * @return
	 */
	private int getNumOBXSegments(Message message)
	{
		
		int numReps = this.hl7ObsHandler.getReps(message);
		if(numReps == 1) // Need to check to see if this is the OBX that HAPI adds by default
		{
			String obsValueType = this.hl7ObsHandler.getObsValueType(message, 0, 0);
			if (obsValueType == null) 
			{
				numReps = 0;
			}
		}
		
		return numReps;
	}
	
	/**
	 * DWE CHICA-633
	 * 
	 * Store an encounter attribute value
	 * 
	 * @param encounter
	 * @param attributeName - the name of the encounter attribute
	 * @param valueText - the value to store in the value_text field
	 */
	private void storeEncounterAttributeAsValueText(org.openmrs.Encounter encounter, String attributeName, String valueText)
	{
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);

		try
		{
			EncounterAttribute encounterAttribute = chirdlutilbackportsService.getEncounterAttributeByName(attributeName);
			EncounterAttributeValue encounterAttributeValue = chirdlutilbackportsService.getEncounterAttributeValueByAttribute(encounter.getEncounterId(), encounterAttribute);
			
			if(encounterAttributeValue == null) // Attribute value doesn't exist for this encounter, create a new one
			{
				encounterAttributeValue = new EncounterAttributeValue(encounterAttribute, encounter.getEncounterId(), valueText);
				encounterAttributeValue.setCreator(encounter.getCreator());
				encounterAttributeValue.setDateCreated(encounter.getDateCreated());
				encounterAttributeValue.setUuid(UUID.randomUUID().toString());
				
				chirdlutilbackportsService.saveEncounterAttributeValue(encounterAttributeValue);
			}
			else
			{ 
				// I can't think of a case where the visit number would change or need to be updated
				// just log it for now
				log.error("Encounter attribute already exists for encounterId: " + encounter.getEncounterId() + " attributeName: " + attributeName);
			}	
		}
		catch(Exception e)
		{
			log.error("Error storing encounter attribute value encounterId: " + encounter.getEncounterId() + " attributeName: " + attributeName, e);
		}
	}
	
	/**
	 * DWE CHICA-706
	 * Updates Ethnicity attribute from hl7 value.
	 * @param currentPatient
	 * @param hl7Patient
	 * @param encounterDate
	 */
	private void updateEthnicity(Patient currentPatient, Patient hl7Patient, Date encounterDate){
		PersonAttribute currentEthnicityAttr = currentPatient.getAttribute(ChirdlUtilConstants.PERSON_ATTRIBUTE_ETHNICITY);
		PersonAttribute hl7EthnicityAttr = hl7Patient.getAttribute(ChirdlUtilConstants.PERSON_ATTRIBUTE_ETHNICITY);
		
		if (hl7EthnicityAttr == null || hl7EthnicityAttr.getValue() == null 
				|| hl7EthnicityAttr.getValue().trim().equals(EMPTY_STRING)){
			return;
		}
		
		if (currentEthnicityAttr == null || currentEthnicityAttr.getValue() == null
			|| !currentEthnicityAttr.getValue().equals(hl7EthnicityAttr.getValue())){
			UUID uuid = UUID.randomUUID();
			hl7EthnicityAttr.setUuid(uuid.toString());
			currentPatient.addAttribute(hl7EthnicityAttr);
		}
	}
	
	/**
	 * DWE Epic_Eskenazi release 10/1/16
	 * @param existingPatient
	 * @param newPatient
	 * @param encounterDate
	 */
	public void addMRNEHR(Patient existingPatient, Patient newPatient, Date encounterDate)
	{
		PatientService patientService = Context.getPatientService();
		String newMRNEHR = null;
		String existingMRNEHR = null;

		try {

			//Get the existing "MRN_EHR" identifier
			PatientIdentifier piExistingPatient = existingPatient.getPatientIdentifier(ChirdlUtilConstants.IDENTIFIER_TYPE_MRN_EHR); 

			PatientIdentifier piNewPatient = newPatient.getPatientIdentifier(ChirdlUtilConstants.IDENTIFIER_TYPE_MRN_EHR);
			
			if(piExistingPatient != null){
				existingMRNEHR = piExistingPatient.getIdentifier();
			
				//void the existing identifier
				piExistingPatient.setVoided(true);
				piExistingPatient.setVoidedBy(Context.getAuthenticatedUser());
				piExistingPatient.setDateVoided(new Date());
			}
			
			if(piNewPatient == null)
			{
				// Identifier not found in the HL7 message?
				return;
			}
			
			newMRNEHR = piNewPatient.getIdentifier();
			if(newMRNEHR == null)
			{
				return;
			}
			
			
			//Create the new identifier object and add to existing patient
			Set<PatientIdentifier> currIdentifiers = existingPatient.getIdentifiers();
			
			//See if the identifier already exists
			boolean foundMatchingMRN = false;
			
			for(PatientIdentifier identifier:currIdentifiers){
				String identifierStr = identifier.getIdentifier();
				if(identifier.getIdentifierType().getName().equals(ChirdlUtilConstants.IDENTIFIER_TYPE_MRN_EHR)&&
						identifierStr!=null && identifierStr.equals(newMRNEHR)){
					foundMatchingMRN = true;
					//unvoid the existing identifier
					identifier.setVoided(false);
					identifier.setVoidedBy(null);
					identifier.setDateVoided(null);
					break;
				}
			}
			
			//Create the new identifier object and add to existing patient
			if (!foundMatchingMRN) {
				PatientIdentifier newIdentifier = new PatientIdentifier();
				newIdentifier.setIdentifier(newMRNEHR);
				newIdentifier.setIdentifierType(patientService.getPatientIdentifierTypeByName(ChirdlUtilConstants.IDENTIFIER_TYPE_MRN_EHR));
				newIdentifier.setLocation(piNewPatient.getLocation());
				newIdentifier.setPatient(existingPatient);
				newIdentifier.setPreferred(false); // THIS SHOULD NOT BE SET AS THE PREFERRED IDENTIFIER
				newIdentifier.setCreator(Context.getAuthenticatedUser());
				newIdentifier.setDateCreated(new Date());
				UUID uuid = UUID.randomUUID();
				newIdentifier.setUuid(uuid.toString());
				
				existingPatient.addIdentifier(newIdentifier);
			}

		} catch (Exception e) {
			log.error("Exception adding new " + ChirdlUtilConstants.IDENTIFIER_TYPE_MRN_EHR + " to existing patient. Existing " + ChirdlUtilConstants.IDENTIFIER_TYPE_MRN_EHR + ": " 
					+ existingMRNEHR + "; New "+ ChirdlUtilConstants.IDENTIFIER_TYPE_MRN_EHR +": " + newMRNEHR, e);
		}

	}
}
