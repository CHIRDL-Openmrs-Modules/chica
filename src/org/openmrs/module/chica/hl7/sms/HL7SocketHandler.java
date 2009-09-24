/**
 * 
 */
package org.openmrs.module.chica.hl7.sms;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttribute;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.Session;
import org.openmrs.module.atd.hibernateBeans.State;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.QueryKite;
import org.openmrs.module.chica.QueryKiteException;
import org.openmrs.module.chica.hibernateBeans.ChicaError;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.hl7.ZPV;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chica.util.Util;
import org.openmrs.module.dss.util.IOUtil;
import org.openmrs.module.sockethl7listener.HL7EncounterHandler;
import org.openmrs.module.sockethl7listener.HL7Filter;
import org.openmrs.module.sockethl7listener.HL7ObsHandler;
import org.openmrs.module.sockethl7listener.HL7PatientHandler;
import org.openmrs.module.sockethl7listener.PatientHandler;
import org.openmrs.module.sockethl7listener.Provider;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.PipeParser;

/**
 * @author tmdugan
 * 
 */
public class HL7SocketHandler extends org.openmrs.module.sockethl7listener.HL7SocketHandler
{
	protected final Log log = LogFactory.getLog(getClass());

	private String locationString = null;
	private Date appointmentTime = null;
	private String insuranceCode = null;
	private Integer sessionId = null;
	private Date timeCheckinHL7Received = null;
	
	private String printerLocation = null;
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
	public Message processMessage(Message message) throws ApplicationException
	{
		this.timeCheckinHL7Received = null;
		this.sessionId = null;
		String incomingMessageString = null;
		AdministrationService adminService = Context.getAdministrationService();
		ChicaService chicaService = Context.getService(ChicaService.class);

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
				ChicaError error = new ChicaError("Fatal", "Hl7 Parsing",
						"Error parsing the sms checkin hl7 " + e.getMessage(),
						org.openmrs.module.dss.util.Util.getStackTrace(e),
						new Date(), null);
				chicaService.saveError(error);
				String smsParseErrorDirectory = IOUtil
						.formatDirectoryName(adminService
								.getGlobalProperty("chica.smsParseErrorDirectory"));
				if (smsParseErrorDirectory != null)
				{
					String filename = "r" + org.openmrs.module.dss.util.Util.archiveStamp() + ".hl7";

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
							this.log.error(org.openmrs.module.dss.util.Util
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
			logger.error(org.openmrs.module.dss.util.Util.getStackTrace(e));
		}

		//load additional chica encounter attributes
		if (this.hl7EncounterHandler instanceof org.openmrs.module.chica.hl7.sms.HL7EncounterHandler25)
		{
			this.locationString = ((org.openmrs.module.chica.hl7.sms.HL7EncounterHandler25) this.hl7EncounterHandler)
					.getLocation(message);

			this.appointmentTime = ((org.openmrs.module.chica.hl7.sms.HL7EncounterHandler25) this.hl7EncounterHandler)
					.getAppointmentTime(message);

			this.insuranceCode = ((org.openmrs.module.chica.hl7.sms.HL7EncounterHandler25) this.hl7EncounterHandler)
					.getInsuranceCode(message);

			this.printerLocation = ((org.openmrs.module.chica.hl7.sms.HL7EncounterHandler25) this.hl7EncounterHandler)
					.getPrinterLocation(message,incomingMessageString);
		}

		if (this.printerLocation != null && this.printerLocation.equals("0"))
		{
			// ignore this message because it is just kids getting shots
			return message;
		}
		return super.processMessage(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmrs.module.sockethl7listener.HL7SocketHandler#createEncounter(ca.uhn.hl7v2.model.v25.segment.MSH,
	 *      ca.uhn.hl7v2.model.v25.segment.PID, org.openmrs.Location,
	 *      org.openmrs.Patient, java.util.Date,
	 *      org.openmrs.module.sockethl7listener.Provider)
	 */
	@Override
	protected org.openmrs.Encounter createEncounter(Patient resultPatient,
			org.openmrs.Encounter newEncounter, Provider provider)
	{
		LocationService locationService = Context.getLocationService();
		org.openmrs.Encounter encounter = super.createEncounter(resultPatient,newEncounter,provider);
		ATDService atdService = Context.getService(ATDService.class);
		Session session = atdService.getSession(getSessionId());
		session.setEncounterId(encounter.getEncounterId());
		atdService.updateSession(session);
		
		State state = atdService.getStateByName("Clinic Registration");
		PatientState patientState = atdService.addPatientState(resultPatient, state, getSessionId(), null);
		patientState.setStartTime(encounter.getEncounterDatetime());
		patientState.setEndTime(encounter.getEncounterDatetime());
		atdService.updatePatientState(patientState);
		
		Integer encounterId = encounter.getEncounterId();
		EncounterService encounterService = Context
				.getService(EncounterService.class);
		encounter = encounterService.getEncounter(encounterId);
		Encounter chicaEncounter = (org.openmrs.module.chica.hibernateBeans.Encounter) encounter;

		if(this.insuranceCode != null){
			chicaEncounter.setInsuranceSmsCode(this.insuranceCode);
		}
		if(this.appointmentTime != null){
			chicaEncounter.setScheduledTime(this.appointmentTime);
		}
		if(this.printerLocation != null){
			chicaEncounter.setPrinterLocation(this.printerLocation);
		}

		if (this.locationString != null)
		{
			Location location = locationService
					.getLocation(this.locationString);

			if (location == null)
			{
				location = new Location();
				location.setName(this.locationString);
				locationService.saveLocation(location);
				logger.warn("Location '" + this.locationString 
						+ "' does not exist in the Location table." 
						+ "a new location was created for '" + this.locationString + "'");
			}

			chicaEncounter.setLocation(location);
		}

		encounterService.saveEncounter(chicaEncounter);

		return chicaEncounter;
	}

	//search for patient based on medical record number then
	//run alias query to see if any patient records to merge
	@Override
	public Patient findPatient(Patient hl7Patient, Date encounterDate)
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
				Patient matchedPatient = findPatient(mrn);
				if (matchedPatient == null)
				{
					resultPatient = createPatient(hl7Patient);
				} else
				{
					resultPatient = updatePatient(matchedPatient, hl7Patient,
							encounterDate);
				}
				
				ATDService atdService = Context.getService(ATDService.class);
				State state = atdService.getStateByName("Process Checkin HL7");
				PatientState patientState = atdService.addPatientState(resultPatient, state, getSessionId(), null);
				patientState.setStartTime(getTimeCheckinHL7Received());
				patientState.setEndTime(new java.util.Date());
				atdService.updatePatientState(patientState);
				
				state = atdService.getStateByName("QUERY KITE Alias");
				patientState = atdService.addPatientState(resultPatient, state, getSessionId(), null);
				
				// merge alias medical record number patients with the matched
				// patient
				processAliasString(mrn, resultPatient);
				patientState.setEndTime(new java.util.Date());
				atdService.updatePatientState(patientState);
			}

		} catch (RuntimeException e)
		{
			logger.error("Exception during patient lookup. " + e.getMessage());
			logger.error(org.openmrs.module.dss.util.Util.getStackTrace(e));
			
		}
		return resultPatient;

	}

	/**
	 * @return
	 */
	private Date getTimeCheckinHL7Received()
	{
		if(this.timeCheckinHL7Received == null){
			this.timeCheckinHL7Received = new java.util.Date();
		}
		return this.timeCheckinHL7Received;
	}

	private Integer getSessionId()
	{
		if (this.sessionId == null)
		{
			ATDService atdService = Context.getService(ATDService.class);
			Session session = atdService.addSession();
			this.sessionId = session.getSessionId();
		}
		return this.sessionId;
	}
	
	private Patient findPatient(String mrn)
	{
		PatientService patientService = Context.getPatientService();
		List<Patient> lookupPatients = patientService.getPatients(null,
				mrn, null, true);

		if (lookupPatients != null && lookupPatients.size() > 0)
		{
			return lookupPatients.iterator().next();
		}

		return null;
	}

	private void processAliasString(String mrn, Patient preferredPatient)
	{
		PatientService patientService = Context.getPatientService();
		ChicaService chicaService = Context.getService(ChicaService.class);
		String aliasString = null;
		try {
			aliasString = QueryKite.aliasQuery(mrn);
		} catch (QueryKiteException e) {
			ChicaError ce = e.getChicaError();
			chicaService.saveError(ce);
			
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
					ChicaError error = new ChicaError("Error", "Query Kite Connection"
							, "Alias query returned FAILED for mrn: "+mrn
							, null, new Date(), null);
					chicaService.saveError(error);
					return;
				}
				if(fields[1].equals("unknown_patient")){
					ChicaError error = new ChicaError("Warning", "Query Kite Connection"
							, "Alias query returned unknown_patient for mrn: "+mrn
							, null, new Date(), null);
					chicaService.saveError(error);
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
							ChicaError error = new ChicaError("Error","General Error","Tried to merge patient: "+
									currPatient.getPatientId()+" with itself.",null,new Date(),null);
							chicaService.saveError(error);
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
				resultPatient.addIdentifier(newSSN);
			}
			else {
				//Check if hl7 SSN and existing SSN identical
				if (!currentSSN.getIdentifier().equalsIgnoreCase(newSSN.getIdentifier())){
					resultPatient.getPatientIdentifier("SSN").setVoided(true);
					resultPatient.addIdentifier(newSSN);
				}
				
			}
			
			for (PatientIdentifier pi : resultPatient.getIdentifiers()){
				boolean voided = pi.getVoided();
				PatientIdentifierType pit = pi.getIdentifierType();
				String pitstring = pit.getName();
				boolean preferred = pi.getPreferred();
				String value = pi.getIdentifier();
				
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
			org.openmrs.Encounter newEncounter)
	{
		return super.checkin(provider, patient, encounterDate,  message,
				incomingMessageString, newEncounter);
	}

	@Override
	public Obs CreateObservation(org.openmrs.Encounter enc,
			boolean saveToDatabase, Message message, int orderRep, int obxRep,
			Location existingLoc, Patient resultPatient)
	{
		return super.CreateObservation(enc, saveToDatabase, message, orderRep, obxRep,
				existingLoc, resultPatient);
	}

}
