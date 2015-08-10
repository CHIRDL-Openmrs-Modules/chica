package org.openmrs.module.chica.hl7.iuHealthVitals;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.hl7.HL7Constants;
import org.openmrs.module.chica.hl7.mrfdump.HL7ObsHandler23;
import org.openmrs.module.chica.hl7.mrfdump.HL7PatientHandler23;
import org.openmrs.module.chica.hl7.mrfdump.HL7ToObs;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.BaseStateActionHandler;
import org.openmrs.module.chirdlutilbackports.StateManager;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Session;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.StateAction;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.module.sockethl7listener.HL7ObsHandler25;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.Application;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.primitive.CommonTS;
import ca.uhn.hl7v2.model.v23.datatype.CX;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.sourcegen.SourceGenerator;
import ca.uhn.hl7v2.util.MessageIDGenerator;
import ca.uhn.hl7v2.util.Terser;
import ca.uhn.hl7v2.validation.impl.NoValidation;

/**
 * 
 * 
 */
@SuppressWarnings("deprecation")
public class HL7SocketHandler implements Application {
	
	protected static final Logger logger = Logger.getLogger("SocketHandlerLogger");
	
	private static final String STATE_PROCESS_VITALS = "Processed Vitals HL7";

	private Integer port;
	
	private String host;
	
	private ca.uhn.hl7v2.parser.Parser parser = null;
	
	public HL7SocketHandler() {
		
		if (port == null) {
			port = 0;
		}
		if (host == null) {
			host = "localhost";
		}
		
	}
	
	public HL7SocketHandler(ca.uhn.hl7v2.parser.Parser parser) {
		
		this.parser = parser;
	}
	
	/**
	 * Always returns true,assuming that the router calling this handler will only call this handler
	 * with ORU_R01 messages.
	 * 
	 * @returns true
	 */
	public boolean canProcess(Message message) {
		return message != null && "ORU_R01".equals(message.getName());
	}
	
	private void writeMessageToFile(String mrn, String incomingMessage) {
		AdministrationService adminService = Context.getAdministrationService();
		// save vitals dump to a file
		String vitalsDirectory = IOUtil.formatDirectoryName(adminService.getGlobalProperty("chica.vitalsArchiveDirectory"));
		if (vitalsDirectory != null) {
			String filename = "r" + Util.archiveStamp() + "_" + mrn + ".hl7";
			
			FileOutputStream vitalsDumpFile = null;
			try {
				vitalsDumpFile = new FileOutputStream(vitalsDirectory + "/" + filename);
			}
			catch (FileNotFoundException e1) {
				logger.error("Couldn't find file: " + vitalsDirectory + "/" + filename);
			}
			if (vitalsDumpFile != null) {
				try {
					
					ByteArrayInputStream vitalsDumpInput = new ByteArrayInputStream(incomingMessage.getBytes());
					IOUtil.bufferedReadWrite(vitalsDumpInput, vitalsDumpFile);
					vitalsDumpFile.flush();
					vitalsDumpFile.close();
				}
				catch (Exception e) {
					try {
						vitalsDumpFile.flush();
						vitalsDumpFile.close();
					}
					catch (Exception e1) {}
					logger.error("There was an error writing the vitals dump file");
					logger.error(e.getMessage());
					logger.error(Util.getStackTrace(e));
				}
			}
		}
	}
	
	public Message processMessage(Message message) throws ApplicationException {
		Date startTime = Calendar.getInstance().getTime();
		Message response = null;
		AdministrationService adminService = Context.getAdministrationService();
		try {
			Context.openSession();
			boolean error = false;
			if (canProcess(message)) {
				String incomingMessageString = "";
				
				incomingMessageString = this.parser.encode(message);
				
				Context.authenticate(adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROPERTY_SCHEDULER_USERNAME),
				    adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROPERTY_SCHEDULER_PASSWORD));
				Context.addProxyPrivilege(HL7Constants.PRIV_ADD_HL7_IN_QUEUE);
				if (!Context.hasPrivilege(HL7Constants.PRIV_ADD_HL7_IN_QUEUE)) {
					logger.error("You do not have HL7 add privilege!!");
					System.exit(0);
				}
				
				error = processMessageSegments(message, incomingMessageString,startTime);
			}
			try {
				ca.uhn.hl7v2.model.v25.segment.MSH msh = HL7ObsHandler25.getMSH(message);
				response = makeACK(msh);
				fillDetails(response, error);
			}
			catch (IOException e) {
				logger.error("Error creating ACK message." + e.getMessage());
			}
			catch (ApplicationException e) {
				logger.error("Error filling in the details of an Application Response or reject message:" + e);
			}
			catch (HL7Exception e) {
				logger.error("Parser error constructing ACK.", e);
			}
			
			Context.clearSession();
			
		}
		catch (ContextAuthenticationException e) {
			logger.error("Context Authentication exception: ", e);
			Context.closeSession();
			System.exit(0);
		}
		catch (ClassCastException e) {
			logger.error("Error casting to " + message.getClass().getName() + " ", e);
			throw new ApplicationException("Invalid message type for handler");
		}
		catch (HL7Exception e) {
			logger.error("Error while processing hl7 message", e);
			throw new ApplicationException(e);
		}
		finally {
			if (response == null) {
				try {
					ca.uhn.hl7v2.model.v25.segment.MSH msh = HL7ObsHandler25.getMSH(message);
					response = makeACK(msh);
				}
				catch (Exception e) {
					logger.error("Could not send acknowledgement", e);
				}
			}
			Context.closeSession();
		}
		
		return response;
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
		
		if(patientStates != null&&patientStates.size()>0){
			
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
	
	private boolean processMessageSegments(Message message, String incomingMessageString,Date startTime) throws HL7Exception {
		
		PatientState patientState = null;
		String newMessageString = incomingMessageString;
		final String SOURCE = "IU Health Vitals";
		Patient patient = null;
		Integer locationId = null;
		Integer locationTagId = null;
		Integer sessionId = null;
		State state = null;
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		AdministrationService adminService = Context.getAdministrationService();
		
		//convert hl7 to version 2.3 so it can be parsed like vitals dump messages
		newMessageString = HL7ToObs.replaceVersion(newMessageString);
		try {
			message = parser.parse(newMessageString);
		}
		catch (Exception e) {
			logger.error(e);
		}
		
		boolean error = false;
		Date starttime = new Date();
		
		try {
			
			HL7PatientHandler23 hl7PatientHandler = new HL7PatientHandler23();
			
			CX patId = hl7PatientHandler.getPID(message).getPatientIDInternalID(0);
			
			String mrn = hl7PatientHandler.getMRN(patId);
			
			//add the dash
			if (mrn.indexOf("-") == -1) {
				mrn = mrn.substring(0, mrn.length() - 1) + "-" + mrn.substring(mrn.length() - 1);
			}
			
			writeMessageToFile(mrn, incomingMessageString);
			
			PatientService patientService = Context.getPatientService();
			List<Patient> patients = patientService.getPatientsByIdentifier(mrn, false);
			if (patients != null && patients.size() > 0) {
				patient = patients.get(0);
			}
			
			state = chirdlutilbackportsService.getStateByName(STATE_PROCESS_VITALS);
			
			org.openmrs.module.chica.hibernateBeans.Encounter encounter = getRecentEncounter(patient);
			
			if (encounter != null) {
				Location location = encounter.getLocation();
				if (location != null) {
					locationId = location.getLocationId();
					locationTagId = org.openmrs.module.chica.util.Util.getLocationTagId(encounter);
				}
				
				List<Session> sessions = chirdlutilbackportsService.getSessionsByEncounter(encounter.getEncounterId());
				if(sessions != null&&sessions.size()>0){
					Session session = sessions.get(0);
					sessionId = session.getSessionId();
				}
				
				patientState = checkProcessVitalsState(startTime, patient, state, sessionId, locationTagId, locationId,
				    encounter.getEncounterId());
			}

			ObsService obsService = Context.getObsService();
			ArrayList<Obs> obsList = parseHL7ToObs(newMessageString, patient);
			ConceptService conceptService = Context.getConceptService();
			
			for (Obs obs : obsList) {
				
				Integer conceptId = obs.getConcept().getConceptId();
				
				switch(conceptId){
					case 635271: //WEIGHT (kg)
						double kilograms = obs.getValueNumeric();
						double pounds = org.openmrs.module.chirdlutil.util.Util.convertUnitsToEnglish(
								kilograms, org.openmrs.module.chirdlutil.util.Util.MEASUREMENT_KG);
						obs.setValueNumeric(pounds);//weight in chica in pounds 
						break;
					case 635268: //height (cm)
						double measurement = obs.getValueNumeric();
						double inches = 
							org.openmrs.module.chirdlutil.util.Util.convertUnitsToEnglish(measurement, 
									org.openmrs.module.chirdlutil.util.Util.MEASUREMENT_CM);
						obs.setValueNumeric(inches);//height in chica in pounds
						break;
					case 39822143: //Temp (Cel)
						double tempC = obs.getValueNumeric();
						double tempF = 
							org.openmrs.module.chirdlutil.util.Util.convertUnitsToEnglish(tempC, 
									org.openmrs.module.chirdlutil.util.Util.MEASUREMENT_CELSIUS);
						obs.setValueNumeric(tempF);//temperature in Fahrenheit
						break;
					default:
						
				}
				
				String conceptIdString = obs.getConcept().getConceptId().toString();
				Concept mappedConcept = conceptService.getConceptByMapping(conceptIdString, SOURCE);
				if (mappedConcept == null) {
					logger.error("Could not map IU Health Cerner vitals concept: " + conceptId
					        + ". Could not store vitals observation.");
				} else {
					obs.setConcept(mappedConcept);
					LocationService locationService = Context.getLocationService();
					Location location = locationService.getLocation("RIIUMG");
					obs.setLocation(location);
					obs.setEncounter(encounter);
					obsService.saveObs(obs, null);
				}
			}
			
			double duration = (new Date().getTime() - starttime.getTime()) / 1000.0;
			logger.info("MESSAGE PROCESS TIME: " + duration + " sec");
			
		}
		catch (RuntimeException e) {
			//Do not stop application. Start processing next hl7 message.
			logger.error("RuntimeException processing ORU_RO1", e);
			error = true;
		}
		
		//If a new process vitals state was created, wait 
		//for a certain number of seconds to close the state
		//and switch to the next state to create the PWS.
		//This helps avoid making multiple PWSs
		if (patientState != null) {
			
			String vitalsDelay =  adminService.getGlobalProperty("chica.vitalsDelay");
			
			if (vitalsDelay != null) {
				Integer timeInSeconds = null;
				try {
					timeInSeconds = Integer.parseInt(vitalsDelay);
					long millis = timeInSeconds * 1000;
					try {
						Thread.sleep(millis);
					}
					catch (InterruptedException e) {
						logger.error("Error in thread sleep", e);
					}
				}
				catch (NumberFormatException e1) {
					logger.error("Could not parse chica.vitalsDelay: " + vitalsDelay, e1);
				}
				
			}
			StateManager.endState(patientState);
			StateAction stateAction = state.getAction();
			
			BaseStateActionHandler.changeState(patient, sessionId, state, stateAction, null, locationTagId, locationId);
		} else {
			logger.error("Patient State is null for patient: " + patient);
		}
		return error;
	}
	
	private org.openmrs.module.chica.hibernateBeans.Encounter getRecentEncounter(Patient patient){
		EncounterService encounterService = Context.getService(EncounterService.class);
    	// Get last encounter with last day
		Calendar startCal = Calendar.getInstance();
		startCal.set(GregorianCalendar.DAY_OF_MONTH, startCal.get(GregorianCalendar.DAY_OF_MONTH) - 2);
		Date startDate = startCal.getTime();
		Date endDate = Calendar.getInstance().getTime();
		List<org.openmrs.Encounter> encounters = encounterService.getEncounters(patient, null, startDate, endDate, null, 
			null, null, false);
		if (encounters == null || encounters.size() == 0) {
			return null;
		} else {
			return (org.openmrs.module.chica.hibernateBeans.Encounter) encounters.get(0);
		}
	}
	
	private ArrayList<Obs> parseHL7ToObs(String messageString, Patient patient) {
		ArrayList<Obs> allObs = null;
		
		if (messageString != null) {
			messageString = messageString.trim();
		}
		
		if (messageString == null || messageString.length() == 0) {
			return allObs;
		}
		String newMessageString = messageString;
		PipeParser pipeParser = new PipeParser();
		pipeParser.setValidationContext(new NoValidation());
		newMessageString = HL7ToObs.replaceVersion(newMessageString);
		Message message = null;
		try {
			message = pipeParser.parse(newMessageString);
		}
		catch (Exception e) {
			logger.error(e);
			return allObs;
		}
		HL7ObsHandler23 obsHandler = new HL7ObsHandler23();
		try {
			allObs = obsHandler.getObs(message, patient);
			return allObs;
		}
		catch (Exception e) {
			logger.error(e);
		}
		return null;
	}
	
	/**
	 * Creates an ACK message with the minimum required information from an inbound message.
	 * Optional fields can be filled in afterwards, before the message is returned. Pleaase note
	 * that MSH-10, the outbound message control ID, is also set using the class
	 * <code>ca.uhn.hl7v2.util.MessageIDGenerator</code>. Also note that the ACK messages returned
	 * is the same version as the version stated in the inbound MSH if there is a generic ACK for
	 * that version, otherwise a version 2.4 ACK is returned. MSA-1 is set to AA by default.
	 * 
	 * @param inboundHeader the MSH segment if the inbound message
	 * @throws IOException if there is a problem reading or writing the message ID file
	 * @throws DataTypeException if there is a problem setting ACK values
	 */
	@SuppressWarnings("unchecked")
	public static Message makeACK(Segment inboundHeader) throws HL7Exception, IOException {
		if (!inboundHeader.getName().equals("MSH"))
			throw new HL7Exception("Need an MSH segment to create a response ACK (got " + inboundHeader.getName() + ")");
		
		String version = "2.5";
		
		String ackClassName = SourceGenerator.getVersionPackageName(version) + "message.ACK";
		
		Message out = null;
		try {
			Class ackClass = Class.forName(ackClassName);
			out = (Message) ackClass.newInstance();
		}
		catch (Exception e) {
			throw new HL7Exception("Can't instantiate ACK of class " + ackClassName + ": " + e.getClass().getName());
		}
		Terser terser = new Terser(out);
		
		// populate outbound MSH using data from inbound message ...
		Segment outHeader = (Segment) out.get("MSH");
		fillResponseHeader(inboundHeader, outHeader);
		
		terser.set("/MSH-9", "ACK");
		terser.set("/MSH-12", version);
		terser.set("/MSA-1", "AA");
		terser.set("/MSA-2", terser.get(inboundHeader, 10, 0, 1, 1));
		
		return out;
	}
	
	/**
	 * Populates certain required fields in a response message header, using information from the
	 * corresponding inbound message. The current time is used for the message time field, and
	 * <code>MessageIDGenerator</code> is used to create a unique message ID. Version and message
	 * type fields are not populated.
	 */
	public static void fillResponseHeader(Segment inbound, Segment outbound) throws HL7Exception, IOException {
		if (!inbound.getName().equals("MSH") || !outbound.getName().equals("MSH"))
			throw new HL7Exception("Need MSH segments.  Got " + inbound.getName() + " and " + outbound.getName());
		
		// get MSH data from incoming message ...
		String encChars = Terser.get(inbound, 2, 0, 1, 1);
		String fieldSep = Terser.get(inbound, 1, 0, 1, 1);
		String procID = Terser.get(inbound, 11, 0, 1, 1);
		String sendingApp = Terser.get(inbound, 3, 0, 1, 1);
		
		// populate outbound MSH using data from inbound message ...
		Terser.set(outbound, 2, 0, 1, 1, encChars);
		Terser.set(outbound, 1, 0, 1, 1, fieldSep);
		GregorianCalendar now = new GregorianCalendar();
		now.setTime(new Date());
		Terser.set(outbound, 7, 0, 1, 1, CommonTS.toHl7TSFormat(now));
		Terser.set(outbound, 10, 0, 1, 1, MessageIDGenerator.getInstance().getNewID());
		Terser.set(outbound, 11, 0, 1, 1, procID);
		Terser.set(outbound, 3, 0, 1, 1, sendingApp);
	}
	
	/**
	 * @param port the port to set
	 */
	public void setPort(Integer port) {
		this.port = port;
	}
	
	/**
	 * Fills in the details of an Application Reject message, including response and error codes,
	 * and a text error message. This is the method to override if you want to respond differently.
	 */
	public void fillDetails(Message ack, boolean error) throws ApplicationException {
		try {
			// populate MSA and ERR with generic error ...
			if (error) {
				Segment msa = (Segment) ack.get("MSA");
				Terser.set(msa, 1, 0, 1, 1, "AA");
				Terser.set(msa, 3, 0, 1, 1, "Unable to create or update patient in openmrs database.");
			} else {
				Segment msa = (Segment) ack.get("MSA");
				Terser.set(msa, 1, 0, 1, 1, "AA");
				Terser.set(msa, 3, 0, 1, 1, "Message created or updated Patient in openmrs database.");
				// this is max length
				
			}
		}
		
		catch (HL7Exception e) {
			throw new ApplicationException("Error trying to create Application ACK message: " + e.getMessage());
		}
	}
}
