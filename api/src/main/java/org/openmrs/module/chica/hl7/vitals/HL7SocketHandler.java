package org.openmrs.module.chica.hl7.vitals;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.ConceptNumeric;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.Daemon;
import org.openmrs.module.chica.hl7.mrfdump.HL7EncounterHandler23;
import org.openmrs.module.chica.hl7.mrfdump.HL7ObsHandler23;
import org.openmrs.module.chica.hl7.mrfdump.HL7PatientHandler23;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.BaseStateActionHandler;
import org.openmrs.module.chirdlutilbackports.StateManager;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.EncounterAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Session;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.StateAction;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.Application;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v23.datatype.CX;

/**
 * 
 * 
 */
public class HL7SocketHandler implements Application {
	
	private static final Logger log = LoggerFactory.getLogger(HL7SocketHandler.class);
	
	private Integer port;
	
	private String host;
	
	private ca.uhn.hl7v2.parser.Parser parser = null;
	
	private String source;
	
	private static final String VISION_L = "VISIONL";
	private static final String VISION_R = "VISIONR";
	
	public HL7SocketHandler() {
		
		if (this.port == null) {
			this.port = 0;
		}
		if (this.host == null) {
			this.host = "localhost";
		}
		if (this.source == null) {
			this.host = "";
		}
		
	}
	
	public HL7SocketHandler(ca.uhn.hl7v2.parser.Parser parser) {
		
		this.parser = parser;
	}
	
	/**
	 *  Returns true if the message is not null and is an instance of ORU_R01
	 * 
	 * @returns true
	 */
	@Override
	public boolean canProcess(Message message) {
		return message instanceof ca.uhn.hl7v2.model.v23.message.ORU_R01;
	}
	
	private void writeMessageToFile(String mrn, String incomingMessage) {
		AdministrationService adminService = Context.getAdministrationService();
		// save vitals dump to a file
		String vitalsDirectory = IOUtil.formatDirectoryName(adminService.getGlobalProperty("chica.vitalsArchiveDirectory"));
		if (vitalsDirectory != null) {
			String filename = "r" + Util.archiveStamp() + "_" + mrn + ChirdlUtilConstants.FILE_EXTENSION_HL7;
			
			
			try (FileOutputStream vitalsDumpFile = new FileOutputStream(vitalsDirectory + "/" + filename);
			        ByteArrayInputStream vitalsDumpInput = new ByteArrayInputStream(incomingMessage.getBytes())){		  	                
	                    IOUtil.bufferedReadWrite(vitalsDumpInput, vitalsDumpFile, false);   
			}
			catch (FileNotFoundException e) {
				log.error("Couldn't find vitals file: {} in directory: {}", filename , vitalsDirectory, e);
			}
            catch (IOException ioe) {
                log.error("IOException writing incoming HL7 message to file: {} in directory: {}.", filename , vitalsDirectory, ioe);
            }	
		}
	}
	
	/**
	 * @see ca.uhn.hl7v2.app.Application#processMessage(ca.uhn.hl7v2.model.Message)
	 */
	@Override
	public Message processMessage(Message message) throws ApplicationException {
		ProcessMessageRunnable processMessageRunnable = new ProcessMessageRunnable(message, this, this.parser);
		Thread messageProcessThread = 
				Daemon.runInDaemonThread(processMessageRunnable, org.openmrs.module.chica.util.Util.getDaemonToken());
		try {
			messageProcessThread.join();
		}
		catch (InterruptedException e) {
			log.error("Process message thread interrupted.", e);
			Thread.currentThread().interrupt();
		}
		
		Exception exception = processMessageRunnable.getException();
		if (exception instanceof ApplicationException) {
			throw (ApplicationException)exception;
		}
		
		return processMessageRunnable.getResult();
	}
	
	/**
	 * This method is synchronized and static to make sure that the state checking happens
	 * serially instead of concurrently
	 * 
	 * @param startTime
	 * @param patient
	 * @param state
	 * @param sessionId
	 * @param locationTagId
	 * @param locationId
	 * @param encounterId
	 * @return
	 */
	private static synchronized PatientState checkProcessVitalsState(Date startTime, Patient patient, State state,
	                                                                 Integer sessionId, Integer locationTagId,
	                                                                 Integer locationId,Integer encounterId) {
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		PatientState patientState = null;
		
		//get patient states for the encounter and state
		List<PatientState> patientStates = chirdlutilbackportsService.getPatientStateByEncounterState(encounterId, state.getStateId());
		
		if(patientStates != null&&!patientStates.isEmpty()){
			
			patientState = patientStates.get(0);
			
			if(patientState.getEndTime() != null){
				//open a vitals state since no open states exist
				patientState = chirdlutilbackportsService.addPatientState(patient, state, sessionId, locationTagId,
				    locationId, null);
				patientState.setStartTime(startTime);
				chirdlutilbackportsService.updatePatientState(patientState);
			}else{
				//a vitals state is still processing
				return null;
			}
		}else{
			//open a processing vitals state since none exist for this encounter
			patientState = chirdlutilbackportsService.addPatientState(patient, state, sessionId, locationTagId,
			    locationId, null);
			patientState.setStartTime(startTime);
			chirdlutilbackportsService.updatePatientState(patientState);
		}
		
		
		return patientState;
	}
	
	protected boolean processMessageSegments(Message message, String incomingMessageString,Date startTime) throws HL7Exception {
		
		PatientState patientState = null;
		Patient patient = null;
		Integer locationId = null;
		Integer locationTagId = null;
		Integer sessionId = null;
		State state = null;
		String locationName = "";
		Integer encounterId = null;
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		
		boolean error = false;
		Date starttime = new Date();
		
		try {
			
			HL7PatientHandler23 hl7PatientHandler = new HL7PatientHandler23();
			
			CX patId = hl7PatientHandler.getPID(message).getPatientIDInternalID(0);
			
			String mrn = hl7PatientHandler.getMRN(patId);
			
			//writeMessageToFile(mrn, incomingMessageString); // Commenting this out in case we want to turn it back on
			
			PatientService patientService = Context.getPatientService();
			
			 // DWE CHICA-635 Created list of identifier types and replaced call to deprecated method
			PatientIdentifierType mrnIdentifierType = patientService.getPatientIdentifierTypeByName(ChirdlUtilConstants.IDENTIFIER_TYPE_MRN);
			List<PatientIdentifierType>  typeList = new ArrayList<>();
			typeList.add(mrnIdentifierType);
			List<Patient> patients = patientService.getPatientsByIdentifier(null, mrn, typeList, false); // Do not match identifier exactly // CHICA-977 Use getPatientsByIdentifier() as a temporary solution to openmrs TRUNK-5089
			if (patients != null && patients.size() > 0) {
				patient = patients.get(0);
			}
			else{
				log.error("Unable to process vitals for patient with MRN: {}",  mrn);
				error = true;
			}
			
			state = chirdlutilbackportsService.getStateByName(ChirdlUtilConstants.STATE_PROCESS_VITALS);
			
			// DWE CHICA-635 Rearranged this code a little to 
			// make sure we have a valid patient, encounter, location, and session 
			// before creating the ChirdlUtilConstants.STATE_PROCESS_VITALS PatientState
			// and processing the obs
			if(patient != null){
				
				Encounter encounter = null;
				
				// Check global property to determine if we should look up the encounter using the visit number
				AdministrationService adminService = Context.getAdministrationService();
				String useVisitNumber = adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_VITALS_USE_VISIT_NUMBER);
				
				if(ChirdlUtilConstants.GENERAL_INFO_TRUE.equalsIgnoreCase(useVisitNumber))
				{
					encounter = getEncounterByVisitNumber(patient, message);
				}
				else
				{
					encounter = getRecentEncounter(patient);
				}

				if (encounter != null) {
					Location location = encounter.getLocation();
					encounterId = encounter.getEncounterId();
					if (location != null) {
						locationName = location.getName();
						locationId = location.getLocationId();
						locationTagId = org.openmrs.module.chica.util.Util.getLocationTagId(encounter);

						List<Session> sessions = chirdlutilbackportsService.getSessionsByEncounter(encounter.getEncounterId());
						if(sessions != null&&!sessions.isEmpty()){
							Session session = sessions.get(0);
							sessionId = session.getSessionId();

							patientState = checkProcessVitalsState(startTime, patient, state, sessionId, locationTagId, locationId,
									encounter.getEncounterId());

							ObsService obsService = Context.getObsService();
							ArrayList<Obs> obsList = parseHL7ToObs(message, patient);
							ConceptService conceptService = Context.getConceptService();

							for (Obs obs : obsList) {
								Concept answerConcept = obs.getValueCoded();
								//see if any answer concepts need mapped
								if (answerConcept != null) {

									String answerConceptName = answerConcept.getName().getName();
									Concept mappedConcept = conceptService.getConceptByMapping(answerConceptName, source);
									if(mappedConcept != null){
										obs.setValueCoded(mappedConcept);
									}
								}

								String conceptIdString = obs.getConcept().getConceptId().toString();
								Concept mappedConcept = conceptService.getConceptByMapping(conceptIdString, source);
								if (mappedConcept == null) {
									log.error("Could not map vitals concept: {}. Could not store vitals observation. Source : {}." , conceptIdString,  source);
								} else {
									if (obs.getValueCoded()!=null&&obs.getValueCoded().getConceptId() == 1) {
										obs.setValueCoded(null);
										if (answerConcept != null) {
											String answerConceptName = answerConcept.getName().getName();
											obs.setValueText(answerConceptName);
											log.error("Could not map vitals concept: {}. Could not store vitals observation. Source : {}.", answerConceptName, source);
										}
									}

									convertVitalsUnits(obs, mappedConcept);
									//void all previous values for this concept
									org.openmrs.module.chica.util.Util.voidObsForConcept(mappedConcept, encounterId, null, "voided due to new vitals");
									obs.setConcept(mappedConcept);
									obs.setLocation(location); 
									obs.setEncounter(encounter);
									
									saveObs(obsService, obs);
									
								}
							}

							org.openmrs.module.chica.util.Util.calculatePercentiles(encounter.getEncounterId(), patient, locationTagId);

							double duration = (new Date().getTime() - starttime.getTime()) / 1000.0;
							log.info("MESSAGE PROCESS TIME: {} sec",  duration);
						}
						else
						{
							log.error("Unable get session for encounter: {}.", encounterId);
							error = true;
						}
					}
					else
					{
						log.error("Unable get location from encounter: {}.", encounterId);
						error = true;
					}
				}
				else
				{
					log.error("Unable to locate recent encounter for patient with MRN: {}. ", mrn);
					error = true;
				}
			}
		}
		catch (RuntimeException e) {
			//Do not stop application. Start processing next hl7 message.
			log.error("RuntimeException processing ORU_RO1", e);
			error = true;
		}
		finally{
			if (patientState != null) {
				
				StateManager.endState(patientState);
				StateAction stateAction = state.getAction();
				
				// DWE CHICA-612 Adding parameters so that the PWS_PDF can be created when receiving vitals
				HashMap<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("sessionId", sessionId);
				parameters.put("locationTagId", locationTagId);
				parameters.put("locationId", locationId);
				parameters.put("location", locationName);
				parameters.put("encounterId", encounterId);			
				
				BaseStateActionHandler.changeState(patient, sessionId, state, stateAction, parameters, locationTagId, locationId);
			} else {
				log.error("Patient State is null for patient: {}",patient);
			}
		}
		
		return error;
	}

	private void saveObs(ObsService obsService, Obs obs) {
		try{
			obsService.saveObs(obs, null);
		}catch(APIException apie){
			// CHICA-1017 Catch the exception and log it so that we can continue processing the message
			log.error("APIException while saving obs.", apie);
		}
	}
	
	/**
	 * Get the most recent encounter for today
	 * @param patient
	 * @return
	 */
	private Encounter getRecentEncounter(Patient patient){
		EncounterService encounterService = Context.getEncounterService();
    	// Get latest encounter today
		Calendar startCal = Calendar.getInstance();
		startCal.set(GregorianCalendar.HOUR_OF_DAY, 0);
		startCal.set(GregorianCalendar.MINUTE, 0);
		startCal.set(GregorianCalendar.SECOND, 0);
		Date startDate = startCal.getTime();
		Date endDate = Calendar.getInstance().getTime();
		List<org.openmrs.Encounter> encounters = encounterService.getEncounters(patient, null, startDate, endDate, null, 
			null, null, null, null, false); // CHICA-1151 Add null parameters for Collection<VisitType> and Collection<Visit>
		if (encounters == null || encounters.size() == 0) {
			return null;
		} 
		
		return encounters.get(encounters.size()-1);
		
	}
	
	/**
	 * Parse OBXs from HL7 to Obs
	 * @param messageString
	 * @param patient
	 * @return
	 */
	private ArrayList<Obs> parseHL7ToObs(Message message, Patient patient) {
		ArrayList<Obs> allObs = new ArrayList<Obs>();
		HL7ObsHandler23 obsHandler = new HL7ObsHandler23();
		try {
			allObs = obsHandler.getObs(message, patient);
		}
		catch (Exception e) {
			log.error("Exception parsing HL7 to get obs",e);
		}
		return allObs;
	}
	
	/**
	 * @param port the port to set
	 */
	public void setPort(Integer port) {
		this.port = port;
	}
	
	/**
	 * DWE CHICA-635
	 * Convert vitals to the appropriate unit of measure
	 * Compare the units received in the HL7 to what is stored in the database for the mappedConcept
	 * If the units are different the numeric value will be converted using the utility methods
	 * 
	 * @param obs - the obs object created from the OBX in the HL7 message
	 * @param mappedConcept - the concept that the OBX is mapped to
	 */
	public static void convertVitalsUnits(Obs obs, Concept mappedConcept)
	{
		if(obs.getConcept() instanceof ConceptNumeric)
		{
			ConceptNumeric obsConcept = (ConceptNumeric)obs.getConcept();
			String obsConceptUnits = obsConcept.getUnits();
			
			// Check to see if the obs received in the HL7 has units before proceeding 
			if(obsConceptUnits != null && !obsConceptUnits.isEmpty())
			{ 
				// Get the units from the mapped concept
				String mappedConceptUnits = getUnitsFromConcept(mappedConcept);
				if(mappedConceptUnits.isEmpty())
				{ 
					// Need to fix the concept configuration if we received units in the HL7, 
					// but the mapped concept does not have units defined
					log.error("Units were received in HL7 message for concept ID: {} units received: {}. Need to define units for concept ID: {}",
							obsConcept.getConceptId(),obsConceptUnits,mappedConcept.getConceptId());
					return;
				}
				
				// We have units received in the HL7 and the concept stored in the DB has units, 
				// check to see if the received units are different than what is defined for the concept
				// Note: The unit string in the HL7 is supposed to be standard. We should not have to 
				// handle differences in upper/lower case or punctuation
				if(!obsConceptUnits.equals(mappedConceptUnits))
				{
					// Do the conversion
					switch(obsConceptUnits)
					{
					case org.openmrs.module.chirdlutil.util.Util.MEASUREMENT_KG:
					case org.openmrs.module.chirdlutil.util.Util.MEASUREMENT_CM:
					case org.openmrs.module.chirdlutil.util.Util.MEASUREMENT_CELSIUS:
					case org.openmrs.module.chirdlutil.util.Util.MEASUREMENT_DEG_C: // IUH sends DegC
					case org.openmrs.module.chirdlutil.util.Util.MEASUREMENT_CEL: // ISO standard
					case org.openmrs.module.chirdlutil.util.Util.MEASUREMENT_CELSIUS_C: // Epic send C
						double convertedValue = org.openmrs.module.chirdlutil.util.Util.convertUnitsToEnglish(obs.getValueNumeric(), 
								obsConceptUnits);
						obs.setValueNumeric(convertedValue);
						break;
						
					default:
							break;
					}
					
				}
				
			}
		}
		else if(VISION_L.equals(mappedConcept.getName().getName()) || VISION_R.equals(mappedConcept.getName().getName()))
		{
			// Special case for vision
			String answer = obs.getValueText();
			if (answer != null) {
				int index = answer.indexOf("/");
				if (index > -1) {
					try
					{
						obs.setValueNumeric(Double.parseDouble(answer.substring(index + 1).trim()));
					}
					catch(NumberFormatException e)
					{
						log.error("Vitals Conversion Error: Could not convert vitals measurement for {}", mappedConcept.getName(), e);
					}
				}
			}
		}
	}
	
	/**
	 * DWE CHICA-635
	 * Gets the units for the concept
	 * @param concept
	 * @return units or empty if the data type is not numeric or units has not been set
	 */
	private static String getUnitsFromConcept(Concept concept) {
		String units = "";
		if (concept.getDatatype() != null && concept.getDatatype().isNumeric()) {
			ConceptService cs = Context.getConceptService();
			ConceptNumeric numericConcept = cs.getConceptNumeric(concept.getConceptId());
			// If the concept is null, log the error because the datatype is set to numeric.
			if (numericConcept == null) {
				log.error("Concept defined as numeric, but was not found. Concept ID: {}", concept.getConceptId());
				return units;
			}
			
			units = numericConcept.getUnits();
		}
		
		return units;
	}
	
	/**
	 * Source defined by the task definition
	 * Used to look up concepts in the mapping table
	 * @param source
	 */
	public void setSource(String source)
	{
		this.source = source;
	}
	
	/**
	 * DWE CHICA-784
	 * Get the encounter using the visit number found in PV1-19
	 * If PV1-19 is empty, which should never happen, this method will default back to the old way of looking up the most recent encounter for the patient
	 * 
	 * @param patient
	 * @param message
	 * @return
	 */
	private Encounter getEncounterByVisitNumber(Patient patient, Message message)
	{
		Encounter encounter = null;
		HL7EncounterHandler23 hl7EncounterHandler23 = new HL7EncounterHandler23();
		String visitNumber = hl7EncounterHandler23.getVisitNumber(message);
		
		if(visitNumber != null && !visitNumber.isEmpty())
		{
			ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);

			try
			{	
				EncounterAttributeValue encounterAttributeValue = chirdlutilbackportsService.getEncounterAttributeValueByValue(visitNumber, ChirdlUtilConstants.ENCOUNTER_ATTRIBUTE_VISIT_NUMBER);
				
				if(encounterAttributeValue != null)
				{
					EncounterService encounterService = Context.getEncounterService();
					encounter = encounterService.getEncounter(encounterAttributeValue.getEncounterId());
					
					// Make sure the patientId for the encounter record matches the patient from the HL7 message
					if(patient.getPatientId().intValue() != encounter.getPatient().getPatientId().intValue()) // CHICA-1151 replace encounter.getPatientId() with encounter.getPatient().getPatientId()
					{
						log.error("Unable to match encounter to patientId: {}", patient.getPatientId());
						encounter = null;
					}
				}
				else
				{
					log.error("Unable to locate encounter for visit number: {}", visitNumber);					
				}
			}
			catch(Exception e)
			{
				log.error("Error occurred while locating encounter for visit number: {}", visitNumber, e);				
			}
		}
		else
		{
			log.error("Unable to locate visit number in HL7 message for patientId: {}", patient.getPatientId());
		}
		
		return encounter;
	}
}
