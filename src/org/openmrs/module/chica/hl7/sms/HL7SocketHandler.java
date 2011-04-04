/**
 * 
 */
package org.openmrs.module.chica.hl7.sms;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.hibernateBeans.ATDError;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.Session;
import org.openmrs.module.atd.hibernateBeans.State;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.QueryKite;
import org.openmrs.module.chica.QueryKiteException;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.sockethl7listener.HL7EncounterHandler;
import org.openmrs.module.sockethl7listener.HL7Filter;
import org.openmrs.module.sockethl7listener.HL7ObsHandler;
import org.openmrs.module.sockethl7listener.HL7PatientHandler;
import org.openmrs.module.sockethl7listener.PatientHandler;
import org.openmrs.module.sockethl7listener.Provider;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.EncodingNotSupportedException;
import ca.uhn.hl7v2.parser.PipeParser;

/**
 * @author tmdugan
 * 
 */
public class HL7SocketHandler extends org.openmrs.module.sockethl7listener.HL7SocketHandler
{
	protected final Log log = LogFactory.getLog(getClass());

	private static final String ATTRIBUTE_RELIGION = "Religion";
	private static final String ATTRIBUTE_MARITAL = "Civil Status";
	private static final String ATTRIBUTE_MAIDEN = "Mother's maiden name";

	/**
	 * @param parser
	 * @param patientHandler
	 */
	public HL7SocketHandler(ca.uhn.hl7v2.parser.Parser parser,
			PatientHandler patientHandler, HL7ObsHandler hl7ObsHandler,
			HL7EncounterHandler hl7EncounterHandler,
			HL7PatientHandler hl7PatientHandler,ArrayList<HL7Filter> filters)
	{
		super(parser, patientHandler, hl7ObsHandler, hl7EncounterHandler,
				hl7PatientHandler, filters);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmrs.module.sockethl7listener.HL7SocketHandler#processMessage(ca.uhn.hl7v2.model.Message)
	 */
	@Override
	public synchronized Message processMessage(Message message,HashMap<String,Object> parameters) throws ApplicationException
	{
		ATDService atdService = Context.getService(ATDService.class);

		parameters.put("processCheckinHL7Start",new java.util.Date());
		String incomingMessageString = null;
		AdministrationService adminService = Context.getAdministrationService();

		// change the message version so we can use default hl7 handlers
		if (message instanceof ca.uhn.hl7v2.model.v23.message.ADT_A01)
		{
			try
			{
				ca.uhn.hl7v2.model.v23.message.ADT_A01 adt = (ca.uhn.hl7v2.model.v23.message.ADT_A01) message;
				adt.getMSH().getVersionID().setValue("2.5");
				adt.getMSH().getMessageType().getTriggerEvent().setValue("A01");
				incomingMessageString = this.parser.encode(message);
				message = this.parser.parse(incomingMessageString);
			} catch (Exception e)
			{
				//Write the hl7 to a file in the error directory if it cannot be parsed
				ATDError error = new ATDError("Fatal", "Hl7 Parsing",
						"Error parsing the sms checkin hl7 " + e.getMessage(),
						org.openmrs.module.chirdlutil.util.Util.getStackTrace(e),
						new Date(), null);
				atdService.saveError(error);
				String smsParseErrorDirectory = IOUtil
						.formatDirectoryName(adminService
								.getGlobalProperty("chica.smsParseErrorDirectory"));
				if (smsParseErrorDirectory != null)
				{
					String filename = "r" + org.openmrs.module.chirdlutil.util.Util.archiveStamp() + ".hl7";

					FileOutputStream outputFile = null;

					try
					{
						outputFile = new FileOutputStream(smsParseErrorDirectory
								+ "/" + filename);
					} catch (FileNotFoundException e1)
					{
						this.log.error("Could not find file: "
								+ smsParseErrorDirectory + "/" + filename);
					}
					if (outputFile != null)
					{
						try
						{

							ByteArrayInputStream input = new ByteArrayInputStream(
									incomingMessageString.getBytes());
							IOUtil.bufferedReadWrite(input, outputFile);
							outputFile.flush();
							outputFile.close();
						} catch (Exception e1)
						{
							try
							{
								outputFile.flush();
								outputFile.close();
							} catch (Exception e2)
							{
							}
							this.log
									.error("There was an error writing the dump file");
							this.log.error(e1.getMessage());
							this.log.error(org.openmrs.module.chirdlutil.util.Util
									.getStackTrace(e));
						}
					}
				}
				return null;
			}
		}

		try
		{
			incomingMessageString = this.parser.encode(message);
			message.addNonstandardSegment("ZPV");
		} catch (HL7Exception e)
		{
			logger.error(e.getMessage());
			logger.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}

		if (this.hl7EncounterHandler instanceof org.openmrs.module.chica.hl7.sms.HL7EncounterHandler25)
		{
			String printerLocation = ((org.openmrs.module.chica.hl7.sms.HL7EncounterHandler25) this.hl7EncounterHandler)
					.getPrinterLocation(message,incomingMessageString);
			
			if (printerLocation != null && printerLocation.equals("0"))
			{
				// ignore this message because it is just kids getting shots
				return message;
			}
		}

		return super.processMessage(message,parameters);
	}

	//search for patient based on medical record number then
	//run alias query to see if any patient records to merge
	@Override
	public Patient findPatient(Patient hl7Patient, Date encounterDate,HashMap<String,Object> parameters)
	{
		Patient resultPatient = null;

		try
		{
			PatientIdentifier patientIdentifier = hl7Patient
					.getPatientIdentifier();
			if (patientIdentifier != null)
			{
				String mrn = patientIdentifier.getIdentifier();
				// look for matched patient
				Patient matchedPatient = findPatient(hl7Patient);
				if (matchedPatient == null)
				{
					resultPatient = createPatient(hl7Patient);
				} else
				{
					resultPatient = updatePatient(matchedPatient, hl7Patient,
							encounterDate);
				}
				
			    parameters.put("processCheckinHL7End",new java.util.Date());
				
			    parameters.put("queryKiteAliasStart",new java.util.Date());
				
				// merge alias medical record number patients with the matched
				// patient
				processAliasString(mrn, resultPatient);
				
				parameters.put("queryKiteAliasEnd",new java.util.Date());
			}

		} catch (RuntimeException e)
		{
			logger.error("Exception during patient lookup. " + e.getMessage());
			logger.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
			
		}
		return resultPatient;

	}

	private Integer getLocationTagId(Encounter encounter)
	{
		if (encounter != null)
		{
			//lookup location tag id that matches printer location
			if(encounter.getPrinterLocation()!= null){
				Location location = encounter.getLocation();
				Set<LocationTag> tags = location.getTags();
				
				if(tags != null){
					for(LocationTag tag:tags){
						if(tag.getTag().equalsIgnoreCase(encounter.getPrinterLocation())){
							return tag.getLocationTagId();
						}
					}
				}
			}
		}
		return null;
	}
	
	private Integer getLocationId(Encounter encounter)
	{
		if (encounter != null)
		{
			return encounter.getLocation().getLocationId();
		}
		return null;
	}
	
	private Session getSession(HashMap<String,Object> parameters)
	{
		Session session = (Session) parameters.get("session");
		if (session == null)
		{
			ATDService atdService = Context.getService(ATDService.class);
			session = atdService.addSession();
			parameters.put("session", session);
		}
		return session;
	}
	
	private Patient findPatient(Patient hl7Patient)
	{
		// Search by MRN
		PatientIdentifier patientIdentifier = hl7Patient.getPatientIdentifier();
		String mrn = patientIdentifier.getIdentifier();
		PatientService patientService = Context.getPatientService();
		List<Patient> lookupPatients = patientService.getPatients(null,
				mrn, null, true);

		if (lookupPatients != null && lookupPatients.size() > 0)
		{
			return lookupPatients.iterator().next();
		}
		
		// Search by SSN
		PatientIdentifier ssnIdent = hl7Patient.getPatientIdentifier("SSN");
		if (ssnIdent != null) 
		{
			String ssn = ssnIdent.getIdentifier();
			lookupPatients = patientService.getPatients(null, ssn, null, true);
			if (lookupPatients != null && lookupPatients.size() > 0)
			{
				Iterator<Patient> i = lookupPatients.iterator();
				while (i.hasNext())
				{
					Patient patient = i.next();
					if (matchPatients(patient, hl7Patient))
					{
						return patient;
					}
				}
				
				// If we didn't find a match, we need to remove the SSN because there's a duplicate.
				hl7Patient.removeIdentifier(ssnIdent);
				// Add a person attribute to store attempted SSN.
				PersonAttributeType personAttrType = Context.getPersonService().getPersonAttributeTypeByName("SSN");
				if (personAttrType != null) {
					PersonAttribute personAttr = new PersonAttribute(personAttrType, ssn);
					hl7Patient.addAttribute(personAttr);
				}
			}
		}

		return null;
	}
	
	private boolean matchPatients(Patient patient1, Patient patient2) 
	{
		String familyName1 = patient1.getFamilyName();
		String familyName2 = patient2.getFamilyName();
		if ((familyName1 != null && familyName2 == null) || (familyName1 == null && familyName2 != null)) {
			return false;
		}
		
		if (familyName1 != null) {
			if (!familyName1.equals(familyName2)) return false;
		} else if (familyName2 != null) {
			if (!familyName2.equals(familyName1)) return false;
		}
		
		String givenName1 = patient1.getGivenName();
		String givenName2 = patient2.getGivenName();
		if ((givenName1 != null && givenName2 == null) || (givenName1 == null && givenName2 != null)) {
			return false;
		}
		
		if (givenName1 != null) {
			if (!givenName1.equals(givenName2)) return false;
		} else if (givenName2 != null) {
			if (!givenName2.equals(givenName1)) return false;
		}
		
		Date birthDate1 = patient1.getBirthdate();
		Date birthDate2 = patient2.getBirthdate();
		if ((birthDate1 != null && birthDate2 == null) || (birthDate1 == null && birthDate2 != null)) {
			return false;
		}
		
		if ((birthDate1 == null && birthDate2 == null))
		{
			return true;
		}
		
		long time1 = birthDate1.getTime();
		long time2 = birthDate2.getTime();
		if (time1 != time2) return false;
		
		return true;
	}

	private void processAliasString(String mrn, Patient preferredPatient)
	{
		ATDService atdService = Context.getService(ATDService.class);
		PatientService patientService = Context.getPatientService();
		String aliasString = null;
		try {
			aliasString = QueryKite.aliasQuery(mrn);
		} catch (QueryKiteException e) {
			ATDError ce = e.getATDError();
			atdService.saveError(ce);
			
		}

		// alias query failed
		if (aliasString == null)
		{
			return;
		}
		String[] fields = PipeParser.split(aliasString, "|");
		if (fields != null)
		{
			int length = fields.length;

			if(length>=2){
				if(fields[1].equals("FAILED")){
					ATDError error = new ATDError("Error", "Query Kite Connection"
							, "Alias query returned FAILED for mrn: "+mrn
							, null, new Date(), null);
					atdService.saveError(error);
					return;
				}
				if(fields[1].equals("unknown_patient")){
					ATDError error = new ATDError("Warning", "Query Kite Connection"
							, "Alias query returned unknown_patient for mrn: "+mrn
							, null, new Date(), null);
					atdService.saveError(error);
					return;
				}
			}
			
			for (int i = 1; i < length; i++)
			{
				if (fields[i].contains("DONE"))
				{
					break;
				}
				
				//don't look up the preferred patient's mrn
				//so we don't merge a patient to themselves
				if(Util.removeLeadingZeros(fields[i]).equals(Util.removeLeadingZeros(mrn))||fields[i].equals("NONE")){
					continue;
				}

				List<Patient> lookupPatients = patientService.getPatients(
						null, Util.removeLeadingZeros(fields[i]), null);

				if (lookupPatients != null && lookupPatients.size() > 0)
				{
					Iterator<Patient> iter = lookupPatients.iterator();

					while (iter.hasNext())
					{
						Patient currPatient = iter.next();
						// only merge different patients
						if (!preferredPatient.getPatientId().equals(currPatient
								.getPatientId()))
						{
							patientService.mergePatients(preferredPatient,
									currPatient);
						}else{
							ATDError error = new ATDError("Error","General Error","Tried to merge patient: "+
									currPatient.getPatientId()+" with itself.",null,new Date(),null);
							atdService.saveError(error);
						}
					}
				}
			}
		}
	}
	
	@Override
	protected Patient updatePatient(Patient mp,
			Patient hl7Patient,Date encounterDate){
	
		Patient resultPatient = super.updatePatient(mp, hl7Patient, encounterDate);
		
		//ssn
		PatientService patientService = Context.getPatientService();
		PatientIdentifier newSSN = hl7Patient.getPatientIdentifier("SSN");
		
		if (newSSN != null && newSSN.getIdentifier() != null){
			
			PatientIdentifier currentSSN = resultPatient.getPatientIdentifier("SSN");
			
			if (currentSSN == null){
				// Check for a duplicate SSN.
				List<Patient> lookupPatients = patientService.getPatients(null, newSSN.getIdentifier(), null, true);
				if (lookupPatients == null || lookupPatients.size() == 0) {
					resultPatient.addIdentifier(newSSN);
				} else {
					// Add a person attribute to store attempted SSN.
					PersonAttributeType personAttrType = Context.getPersonService().getPersonAttributeTypeByName("SSN");
					if (personAttrType != null) {
						PersonAttribute personAttr = new PersonAttribute(personAttrType, newSSN.getIdentifier());
						resultPatient.addAttribute(personAttr);
					}
				}
			}
			else {
				//Check if hl7 SSN and existing SSN identical
				if (!currentSSN.getIdentifier().equalsIgnoreCase(newSSN.getIdentifier())){
					resultPatient.getPatientIdentifier("SSN").setVoided(true);
					resultPatient.addIdentifier(newSSN);
				}
				
			}
		}

		//religion
		PersonAttribute newReligionAttr = hl7Patient.getAttribute(ATTRIBUTE_RELIGION);
		PersonAttribute currentReligionAttr = resultPatient.getAttribute(ATTRIBUTE_RELIGION);
		if (newReligionAttr != null){
			String newReligion = newReligionAttr.getValue();
			if (newReligion != null && currentReligionAttr != null
					&& ! currentReligionAttr.getValue()
					.equalsIgnoreCase(newReligion)){
				resultPatient.addAttribute(newReligionAttr);
			}
		}
		
		//marital
		PersonAttribute newMaritalStatAttr = hl7Patient.getAttribute(ATTRIBUTE_MARITAL);
		PersonAttribute currentMaritalStatAttr = resultPatient.getAttribute(ATTRIBUTE_MARITAL);
		if (newMaritalStatAttr != null){
			String newMaritalStat = newMaritalStatAttr.getValue();
			if (newMaritalStat != null && currentMaritalStatAttr != null
					&& ! currentMaritalStatAttr.getValue()
					.equalsIgnoreCase(newMaritalStat)){
				resultPatient.addAttribute(newMaritalStatAttr);
			}
		}
		
		
		//maiden name
		
		PersonAttribute newMaidenNameAttr = hl7Patient.getAttribute(ATTRIBUTE_MAIDEN);
		PersonAttribute currentMaidenNameAttr = resultPatient.getAttribute(ATTRIBUTE_MAIDEN);
		if (newMaidenNameAttr != null){
			String newMaidenName = newMaidenNameAttr.getValue();
			if (newMaidenName != null && currentMaidenNameAttr != null
					&& currentMaritalStatAttr!=null&&
					! currentMaritalStatAttr.getValue()
					.equalsIgnoreCase(newMaidenName)){
				resultPatient.addAttribute(newMaidenNameAttr);
			}
		}
		
		Patient updatedPatient = patientService.savePatient(resultPatient);
		
		return updatedPatient;
	}

	@Override
	public org.openmrs.Encounter checkin(Provider provider, Patient patient,
			Date encounterDate, Message message, String incomingMessageString,
			org.openmrs.Encounter newEncounter, HashMap<String,Object> parameters)
	{
		Date processCheckinHL7Start = (Date) parameters.get("processCheckinHL7Start");
		if(processCheckinHL7Start == null){
			parameters.put("processCheckinHL7Start", new java.util.Date());
		}
		
		return super.checkin(provider, patient, encounterDate,  message,
				incomingMessageString, newEncounter,parameters);
	}
	

	@Override
	public Obs CreateObservation(org.openmrs.Encounter enc,
			boolean saveToDatabase, Message message, int orderRep, int obxRep,
			Location existingLoc, Patient resultPatient)
	{
		return super.CreateObservation(enc, saveToDatabase, message, orderRep, obxRep,
				existingLoc, resultPatient);
	}

	@Override
	public org.openmrs.Encounter processEncounter(String incomingMessageString,
			Patient p, Date encDate, org.openmrs.Encounter newEncounter,
			Provider provider,HashMap<String,Object> parameters)
	{
		ATDService atdService = Context.getService(ATDService.class);
		org.openmrs.Encounter encounter = super.processEncounter(
				incomingMessageString, p, encDate, newEncounter, provider,parameters);
		//store the encounter id with the session
		Integer encounterId = newEncounter.getEncounterId();
		getSession(parameters).setEncounterId(encounterId);
		atdService.updateSession(getSession(parameters));
		if(incomingMessageString == null){
			return encounter;
		}
		LocationService locationService = Context.getLocationService();

		String locationString = null;
		Date appointmentTime = null;
		String insuranceCode = null;
		String printerLocation = null;
		Message message;
		try
		{
			message = this.parser.parse(incomingMessageString);
			EncounterService encounterService = Context
					.getService(EncounterService.class);
			encounter = encounterService.getEncounter(encounter
					.getEncounterId());
			Encounter chicaEncounter = (org.openmrs.module.chica.hibernateBeans.Encounter) encounter;

			// load additional chica encounter attributes
			if (this.hl7EncounterHandler instanceof org.openmrs.module.chica.hl7.sms.HL7EncounterHandler25)
			{
				locationString = ((org.openmrs.module.chica.hl7.sms.HL7EncounterHandler25) this.hl7EncounterHandler)
						.getLocation(message);

				appointmentTime = ((org.openmrs.module.chica.hl7.sms.HL7EncounterHandler25) this.hl7EncounterHandler)
						.getAppointmentTime(message);

				insuranceCode = ((org.openmrs.module.chica.hl7.sms.HL7EncounterHandler25) this.hl7EncounterHandler)
						.getInsuranceCode(message);

				printerLocation = ((org.openmrs.module.chica.hl7.sms.HL7EncounterHandler25) this.hl7EncounterHandler)
						.getPrinterLocation(message, incomingMessageString);

				if (insuranceCode != null)
				{
					chicaEncounter.setInsuranceSmsCode(insuranceCode);
				}
				if (appointmentTime != null)
				{
					chicaEncounter.setScheduledTime(appointmentTime);
				}
				if (printerLocation != null)
				{
					chicaEncounter.setPrinterLocation(printerLocation);
				}

				if (locationString != null)
				{
					Location location = locationService
							.getLocation(locationString);

					if (location == null)
					{
						location = new Location();
						location.setName(locationString);
						locationService.saveLocation(location);
						logger.warn("Location '" + locationString
								+ "' does not exist in the Location table."
								+ "a new location was created for '"
								+ locationString + "'");
					}

					chicaEncounter.setLocation(location);
				}
					//This code must come after the code that sets the encounter values
					//because the states can't be created until the locationTagId and 
					//locationId have been set
					State state = atdService.getStateByName("Clinic Registration");
					PatientState patientState = atdService.addPatientState(p, state, 
							getSession(parameters).getSessionId(),getLocationTagId(chicaEncounter),
							getLocationId(chicaEncounter));
					patientState.setStartTime(chicaEncounter.getEncounterDatetime());
					patientState.setEndTime(chicaEncounter.getEncounterDatetime());
					atdService.updatePatientState(patientState);
					
					state = atdService.getStateByName("Process Checkin HL7");
					patientState = atdService.addPatientState(p, state, getSession(parameters).getSessionId(),
							getLocationTagId(chicaEncounter),getLocationId(chicaEncounter));
					Date processCheckinHL7Start = (Date) parameters.get("processCheckinHL7Start");
					Date processCheckinHL7End = (Date) parameters.get("processCheckinHL7End");
					patientState.setStartTime(processCheckinHL7Start);
					patientState.setEndTime(processCheckinHL7End);
					atdService.updatePatientState(patientState);

					state = atdService.getStateByName("QUERY KITE Alias");
					patientState = atdService.addPatientState(p, state, getSession(parameters).getSessionId(),
							getLocationTagId(chicaEncounter),getLocationId(chicaEncounter));
					Date queryKiteAliasStart = (Date) parameters.get("queryKiteAliasStart");
					Date queryKiteAliasEnd = (Date) parameters.get("queryKiteAliasEnd");
					patientState.setStartTime(queryKiteAliasStart);
					patientState.setEndTime(queryKiteAliasEnd);
					atdService.updatePatientState(patientState);
				
					encounterService.saveEncounter(chicaEncounter);
			}
		} catch (EncodingNotSupportedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HL7Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return encounter;
	}
	
	

}
