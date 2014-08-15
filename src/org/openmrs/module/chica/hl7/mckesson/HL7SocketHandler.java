/**
 * 
 */
package org.openmrs.module.chica.hl7.mckesson;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
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
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Error;
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
import org.openmrs.util.OpenmrsUtil;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.parser.EncodingNotSupportedException;
import ca.uhn.hl7v2.parser.PipeParser;

/**
 * @author tmdugan
 * 
 */
public class HL7SocketHandler extends
		org.openmrs.module.sockethl7listener.HL7SocketHandler {

	protected final Log log = LogFactory.getLog(getClass());

	// search for patient based on medical record number then
	// run alias query to see if any patient records to merge
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
					
					//Merge alias medical record number patients
					parameters.put("queryKiteAliasStart", new java.util.Date());
					processAliasString(mrn, resultPatient);
					parameters.put("queryKiteAliasEnd", new java.util.Date());
					
				} else {
					resultPatient = updatePatient(matchedPatient, hl7Patient,
							encounterDate);
				}

				parameters.put("processCheckinHL7End", new java.util.Date());

			}

		} catch (RuntimeException e) {
			logger.error("Exception during patient lookup. " + e.getMessage());
			logger.error(org.openmrs.module.chirdlutil.util.Util
					.getStackTrace(e));

		}
		return resultPatient;

	}

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

	private Integer getLocationId(Encounter encounter) {
		if (encounter != null) {
			return encounter.getLocation().getLocationId();
		}
		return null;
	}

	private Patient findPatient(Patient hl7Patient) {
		// Search by MRN
		PatientIdentifier patientIdentifier = hl7Patient.getPatientIdentifier();
		String mrn = patientIdentifier.getIdentifier();
		PatientService patientService = Context.getPatientService();
		List<Patient> lookupPatients = patientService.getPatients(null, mrn,
				null, true);

		if (lookupPatients != null && lookupPatients.size() > 0) {
			return lookupPatients.iterator().next();
		}

		// Search by SSN
		PatientIdentifier ssnIdent = hl7Patient.getPatientIdentifier(ChirdlUtilConstants.SSN_IDENTIFIER_TYPE);
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

	private void processAliasString(String mrn, Patient preferredPatient) {
		ChirdlUtilBackportsService chirdlutilbackportsService = Context
				.getService(ChirdlUtilBackportsService.class);

		PatientService patientService = Context.getPatientService();
		String aliasString = null;
		try {
			aliasString = QueryKite.aliasQuery(mrn);
		} catch (QueryKiteException e) {
			Error ce = e.getError();
			chirdlutilbackportsService.saveError(ce);

		}

		// alias query failed
		if (aliasString == null) {
			return;
		}
		String[] fields = PipeParser.split(aliasString, "|");
		if (fields != null) {
			int length = fields.length;

			if (length >= 2) {
				if (fields[1].equals("FAILED")) {
					Error error = new Error("Error", ChirdlUtilConstants.ERROR_QUERY_KITE_CONNECTION,
							"Alias query returned FAILED for mrn: " + mrn,
							null, new Date(), null);
					chirdlutilbackportsService.saveError(error);
					return;
				}
				if (fields[1].equals("unknown_patient")) {
					Error error = new Error("Warning", ChirdlUtilConstants.ERROR_QUERY_KITE_CONNECTION,
							"Alias query returned unknown_patient for mrn: "
									+ mrn, null, new Date(), null);
					chirdlutilbackportsService.saveError(error);
					return;
				}
			}

			for (int i = 1; i < length; i++) {
				if (fields[i].contains("DONE")) {
					break;
				}

				// don't look up the preferred patient's mrn
				// so we don't merge a patient to themselves
				if (Util.removeLeadingZeros(fields[i]).equals(
						Util.removeLeadingZeros(mrn))
						|| fields[i].equals("NONE")) {
					continue;
				}

				List<Patient> lookupPatients = patientService.getPatients(null,
						Util.removeLeadingZeros(fields[i]), null, false);

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
							Error error = new Error("Error", ChirdlUtilConstants.ERROR_GENERAL,
									"Tried to merge patient: "
											+ currPatient.getPatientId()
											+ " with itself.", null,
									new Date(), null);
							chirdlutilbackportsService.saveError(error);
						}
					}
				}
			}
		}
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
		
		Patient updatedPatient = null;
		try {
			updatedPatient = patientService.savePatient(currentPatient);
		} catch (APIException e) {
			log.error("Exception saving updated patient.", e);
		}
		return updatedPatient;

	}

	@Override
	public org.openmrs.Encounter checkin(Provider provider, Patient patient,
			Date encounterDate, Message message, String incomingMessageString,
			org.openmrs.Encounter newEncounter,
			HashMap<String, Object> parameters) {
		Date processCheckinHL7Start = (Date) parameters
				.get("processCheckinHL7Start");
		if (processCheckinHL7Start == null) {
			parameters.put("processCheckinHL7Start", new java.util.Date());
		}

		return super.checkin(provider, patient, encounterDate, message,
				incomingMessageString, newEncounter, parameters);
	}

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
		parameters.put("processCheckinHL7Start", new java.util.Date());
		Context.openSession();
//		String allowMessageSources = adminService.getGlobalProperty("chica.allowableHL7MessageSources");
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
					adt.getMSH().getVersionID().getVersionID().setValue("2.5");
					adt.getMSH().getMessageType().getTriggerEvent().setValue("A01");
					}
				
				if (message instanceof ca.uhn.hl7v2.model.v22.message.ADT_A04){
					ca.uhn.hl7v2.model.v22.message.ADT_A04 adt = (ca.uhn.hl7v2.model.v22.message.ADT_A04) message;
					adt.getMSH().getVersionID().setValue("2.5");
					adt.getMSH().getMessageType().getTriggerEvent().setValue("A01");
				}
				
				incomingMessageString = this.parser.encode(message);
				message = this.parser.parse(incomingMessageString);
			
			} catch (Exception e) {
				Error error = new Error("Fatal", ChirdlUtilConstants.ERROR_HL7_PARSING,
						"Error parsing the McKesson checkin hl7 "
								+ e.getMessage(),
						org.openmrs.module.chirdlutil.util.Util
								.getStackTrace(e), new Date(), null);
				ChirdlUtilBackportsService chirdlutilbackportsService = Context
						.getService(ChirdlUtilBackportsService.class);

				chirdlutilbackportsService.saveError(error);
				String mckessonParseErrorDirectory = IOUtil
						.formatDirectoryName(adminService
								.getGlobalProperty("chica.mckessonParseErrorDirectory"));
				if (mckessonParseErrorDirectory != null) {
					String filename = "r" + Util.archiveStamp() + ".hl7";

					FileOutputStream outputFile = null;

					try {
						outputFile = new FileOutputStream(
								mckessonParseErrorDirectory + "/" + filename);
					} catch (FileNotFoundException e1) {
						this.log.error("Could not find file: "
								+ mckessonParseErrorDirectory + "/" + filename);
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
							this.log
									.error("There was an error writing the dump file");
							this.log.error(e1.getMessage());
							this.log.error(Util.getStackTrace(e));
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
			message.addNonstandardSegment("ZPV");
		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(org.openmrs.module.chirdlutil.util.Util
					.getStackTrace(e));
		}

		if (this.hl7EncounterHandler instanceof org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25) {
			String printerLocation = ((org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25) this.hl7EncounterHandler)
					.getPrinterLocation(message, incomingMessageString);

			if (printerLocation != null && printerLocation.equals("0")) {
				// ignore this message because it is just kids getting shots
				return message;
			}
		}
		return super.processMessage(message, parameters);
	}

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
				.getStateByName("Clinic Registration");
		PatientState patientState = chirdlutilbackportsService
				.addPatientState(p, state, getSession(parameters)
						.getSessionId(), getLocationTagId(chicaEncounter),
						getLocationId(chicaEncounter), null);
		patientState.setStartTime(chicaEncounter.getEncounterDatetime());
		patientState.setEndTime(chicaEncounter.getEncounterDatetime());
		chirdlutilbackportsService.updatePatientState(patientState);

		state = chirdlutilbackportsService
				.getStateByName("Process Checkin HL7");
		patientState = chirdlutilbackportsService
				.addPatientState(p, state, getSession(parameters)
						.getSessionId(), getLocationTagId(chicaEncounter),
						getLocationId(chicaEncounter), null);
		Date processCheckinHL7Start = (Date) parameters
				.get("processCheckinHL7Start");
		Date processCheckinHL7End = (Date) parameters
				.get("processCheckinHL7End");
		patientState.setStartTime(processCheckinHL7Start);
		patientState.setEndTime(processCheckinHL7End);
		chirdlutilbackportsService.updatePatientState(patientState);

		state = chirdlutilbackportsService.getStateByName("QUERY KITE Alias");
		patientState = chirdlutilbackportsService
				.addPatientState(p, state, getSession(parameters)
						.getSessionId(), getLocationTagId(chicaEncounter),
						getLocationId(chicaEncounter), null);
		Date queryKiteAliasStart = (Date) parameters.get("queryKiteAliasStart");
		Date queryKiteAliasEnd = (Date) parameters.get("queryKiteAliasEnd");
		patientState.setStartTime(queryKiteAliasStart);
		patientState.setEndTime(queryKiteAliasEnd);
		chirdlutilbackportsService.updatePatientState(patientState);

		encounterService.saveEncounter(chicaEncounter);
		ConceptService conceptService = Context.getConceptService();
		Concept concept = conceptService.getConceptByName("InsuranceName");
		if (insuranceName != null){
			org.openmrs.module.chirdlutil.util.Util.saveObs(p, concept, encounterId, insuranceName,encDate);
		}else {
			log.error("Insurance Name is null for patient: " + p.getPatientId());
		}
		return encounter;
	}

	private Session getSession(HashMap<String, Object> parameters) {
		Session session = (Session) parameters.get("session");
		if (session == null) {
			ChirdlUtilBackportsService chirdlutilbackportsService = Context
					.getService(ChirdlUtilBackportsService.class);
			session = chirdlutilbackportsService.addSession();
			parameters.put("session", session);
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
	void addName(Patient currentPatient, Patient hl7Patient, Date encounterDate) {

		/*
		 * Condition where newest hl7 name matches an older existing name ( not
		 * currently the preferred name). OpenMRS equality checks will detect it
		 * as equal and not add the name in addName(). So, that name will never
		 * have an updated date, and will not get set as the preferred name.
		 * Then, the wrong name will be displayed on the form.
		 */

		for (PersonName pn : currentPatient.getNames()) {
			if (pn != null
					&& OpenmrsUtil.nullSafeEquals(pn.getFamilyName()
							, hl7Patient.getFamilyName())
					&& OpenmrsUtil.nullSafeEquals(pn.getGivenName()
							,hl7Patient.getGivenName())
					&& OpenmrsUtil.nullSafeEquals(pn.getMiddleName()
							, hl7Patient.getMiddleName())) {
				pn.setDateCreated(encounterDate);
			}

		}

		currentPatient.addName(hl7Patient.getPersonName());
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

		try {
			// Latest to preferred
			if (nameList.size() > 0 && nameList.get(0) != null) {
				nameList.get(0).setPreferred(true);
				Set<PersonName> nameSet = new TreeSet<PersonName>(nameList);
				if (nameSet.size()> 0){
					currentPatient.getNames().clear();
					currentPatient.getNames().addAll(nameSet);
				}else{
					//Safety check. If nameSet is empty, don't clear.  There should
					//at least be the new name from the hl7 message
					log.error("Name set is empty, do not clear." 
							+ "Name will not be updated.");
				}
			}
		} catch (Exception e) {
			log.error("Error setting preferred status to the updated patient name.",e);
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
	public void addAddress(Patient currentPatient, Patient hl7Patient,
			Date encounterDate) {

		/*
		 * OpenMRS addAddress() first checks for equality based on id only, so
		 * Patient.addAddress() will never result in a match between the
		 * hl7patient address and existing address and will always be added
		 * (because there are no ids for the hl7patient yet)
		 */

		PersonAddress hl7Address = hl7Patient.getPersonAddress();

		if (hl7Address == null) {
			return;
		}

		String hl7Address1 = hl7Address.getAddress1();
		String hl7Address2 = hl7Address.getAddress2();
		String hl7City = hl7Address.getCityVillage();
		String hl7County = hl7Address.getCountyDistrict();
		String hl7State = hl7Address.getStateProvince();
		String hl7Zip = hl7Address.getPostalCode();
		String hl7Country = hl7Address.getCountry();

		boolean found = false;

		for (PersonAddress pa : currentPatient.getAddresses()) {
			if (!found && OpenmrsUtil.nullSafeEquals(pa.getAddress1(), hl7Address1)
					&& OpenmrsUtil.nullSafeEquals(pa.getAddress2(), hl7Address2)
					&& OpenmrsUtil.nullSafeEquals(pa.getCityVillage(), hl7City)
					&& OpenmrsUtil.nullSafeEquals(pa.getCountyDistrict(), hl7County)
					&& OpenmrsUtil.nullSafeEquals(pa.getStateProvince(), hl7State)
					&& OpenmrsUtil.nullSafeEquals(pa.getPostalCode(), hl7Zip)
					&& OpenmrsUtil.nullSafeEquals(pa.getCountry(), hl7Country)) {
				pa.setDateCreated(encounterDate);
				found = true;
			}

		}

		if (!found) {
			hl7Patient.getPersonAddress().setDateCreated(encounterDate);
			currentPatient.addAddress(hl7Patient.getPersonAddress());
		}

		// reset all addresses preferred status
		Set<PersonAddress> addresses = currentPatient.getAddresses();
		for (PersonAddress address : addresses) {
			address.setPreferred(false);
		}

		// Sort the list of names based on date
		List<PersonAddress> addressList = new ArrayList<PersonAddress>(
				addresses);

		try {
			Collections.sort(addressList, new Comparator<PersonAddress>() {
				public int compare(PersonAddress a1, PersonAddress a2) {
					Date date1 = a1.getDateCreated();
					Date date2 = a2.getDateCreated();
					return date1.compareTo(date2) > 0 ? 0 : 1;
				}
			});
		} catch (Exception e) {
			log.error("Sort exception for address list", e);
			return;
		}

		try {
			if (addressList.size() > 0 && addressList.get(0) != null) {
				// Latest to preferred
				addressList.get(0).setPreferred(true);
				Set<PersonAddress> addressSet = new TreeSet<PersonAddress>(
						addressList);
				if (addressSet.size()> 0){
					currentPatient.getAddresses().clear();
					currentPatient.getAddresses().addAll(addressSet);
				}else{
					//Safety check.  The set should at least have the one hl7 address.
					//Do not clear addresses.
					log.error("Safety check.  The set should contain at least the new" +
							" hl7Address. If empty, do not clear. Addresses are not updated.");
				}
			}
		} catch (Exception e) {
			log.error("Error adding addresses to patient", e);

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

		PatientIdentifier newSSN = hl7Patient.getPatientIdentifier(ChirdlUtilConstants.SSN_IDENTIFIER_TYPE);
		PatientIdentifier currentSSN = currentPatient
				.getPatientIdentifier(ChirdlUtilConstants.SSN_IDENTIFIER_TYPE);

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
				currentPatient.getPatientIdentifier(ChirdlUtilConstants.SSN_IDENTIFIER_TYPE).setVoided(true);
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
				|| newReligionAttr.getValue().equals("")){
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
				|| newMaritalStatAttr.getValue().equals("")) {
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
				|| newMaidenNameAttr.getValue().equals("")){
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
				|| newNextOfKinNameAttr.getValue().equals("")){
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
				|| hl7TelNumAttr.getValue().equals("")){
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

		PersonAttribute currentCitizenshipAttr = currentPatient.getAttribute(ChirdlUtilConstants.PERSON_CITIZENSHIP);
		PersonAttribute hl7CitizenshipAttr = hl7Patient.getAttribute(ChirdlUtilConstants.PERSON_CITIZENSHIP);

		if (hl7CitizenshipAttr == null || hl7CitizenshipAttr.getValue() == null 
				|| hl7CitizenshipAttr.getValue().trim().equals("")){
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
				|| hl7RaceAttr.getValue().trim().equals("")){
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
				&& (hl7Gender == null || hl7Gender.trim().equalsIgnoreCase("") 
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
	 */
	private void addMRN(Patient existingPatient, Patient newPatient, Date encounterDate){
		ChirdlUtilBackportsService chirdlutilbackportsService 
				= Context.getService(ChirdlUtilBackportsService.class);
		PatientService patientService = Context.getPatientService();
	
		try {
			
			//Get the existing preferred, non-voided identifier for comparison  
			PatientIdentifier existingPatientIdentifier = existingPatient.getPatientIdentifier(); 
			PatientIdentifier newPatientIdentifier= newPatient.getPatientIdentifier();
			
			String existingMRN = existingPatientIdentifier.getIdentifier();
			String newMRN = null;
			
			//If new MRN does not exist or matches the existing MRN, no need to update.
			if (newPatientIdentifier == null
					|| (newMRN = newPatientIdentifier.getIdentifier()) == null
					|| Util.removeLeadingZeros(existingMRN.trim()).equals(
					Util.removeLeadingZeros(newMRN.trim()))){
				return;
			}
			
			//void the existing identifier
			existingPatientIdentifier.setPreferred(false);
			existingPatientIdentifier.setVoided(true);
			existingPatientIdentifier.setVoidedBy(Context.getAuthenticatedUser());
			existingPatientIdentifier.setVoidReason("MRN Correction");
			existingPatientIdentifier.setDateVoided(new Date());
			
			//Create the new identifier object and add to existing patient
			PatientIdentifier newIdentifier = new PatientIdentifier();
			newIdentifier.setIdentifier(newMRN);
			newIdentifier.setIdentifierType( patientService.getPatientIdentifierTypeByName(ChirdlUtilConstants.MRN_IDENTIFIER_TYPE));
			newIdentifier.setLocation(newPatientIdentifier.getLocation());
			newIdentifier.setPatient(existingPatient);
			newIdentifier.setPreferred(true);
			newIdentifier.setUuid(UUID.randomUUID().toString());
			newIdentifier.setCreator(Context.getAuthenticatedUser());
			newIdentifier.setDateCreated(new Date());
			existingPatient.addIdentifier(newIdentifier);
			
			
			Error error = new Error("Error", ChirdlUtilConstants.ERROR_MRN_VALIDITY,
					"MRN correction required! Contact Regenstrief about possible corrupted data." 
					+ "Invalid MRN: " + existingMRN + " New MRN: " + newMRN,
					"HL7 or manual checkin indicate that an existing patient has an invalid MRN. ", new Date(), null);
			chirdlutilbackportsService.saveError(error);
			
		} catch (Exception e) {
			log.error("Exception adding new MRN to existing patient.", e);
		}
		
	}
	
}
