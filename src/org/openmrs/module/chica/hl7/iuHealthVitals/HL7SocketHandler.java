package org.openmrs.module.chica.hl7.iuHealthVitals;

import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.hl7.HL7Constants;
import org.openmrs.module.chica.hl7.mrfdump.HL7ToObs;
import org.openmrs.module.chica.hl7.mrfdump.PatientHandler;
import org.openmrs.module.sockethl7listener.HL7EncounterHandler;
import org.openmrs.module.sockethl7listener.HL7ObsHandler25;
import org.openmrs.module.sockethl7listener.HL7PatientHandler;
import org.openmrs.module.sockethl7listener.ZLR;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.Application;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.primitive.CommonTS;
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
	
	protected PatientHandler patientHandler; //setters
	
	private Integer port;
	
	private String host;
	
	private ca.uhn.hl7v2.parser.Parser parser = null;
	
	private HL7EncounterHandler hl7EncounterHandler = null; //getters
	
	private HL7PatientHandler hl7PatientHandler = null; //getters
	
	public HL7SocketHandler() {
		
		if (port == null) {
			port = 0;
		}
		if (host == null) {
			host = "localhost";
		}
		
	}
	
	public HL7SocketHandler(ca.uhn.hl7v2.parser.Parser parser, PatientHandler patientHandler,
	    HL7EncounterHandler hl7EncounterHandler, HL7PatientHandler hl7PatientHandler) {
		
		this.patientHandler = patientHandler;
		this.parser = parser;
		this.hl7EncounterHandler = hl7EncounterHandler;
		this.hl7PatientHandler = hl7PatientHandler;
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
	
	public Message processMessage(Message message) throws ApplicationException {
		Message response = null;
		AdministrationService adminService = Context.getAdministrationService();
		try {
			Context.openSession();
			boolean error = false;
			if (canProcess(message)) {
				String incomingMessageString = "";
				
				incomingMessageString = this.parser.encode(message);
				message.addNonstandardSegment("ZLR");
				ZLR zlr = new ZLR(message);
				zlr.loadZLRSegment(incomingMessageString);
				Context.authenticate(adminService.getGlobalProperty("scheduler.username"),
				    adminService.getGlobalProperty("scheduler.password"));
				Context.addProxyPrivilege(HL7Constants.PRIV_ADD_HL7_IN_QUEUE);
				if (!Context.hasPrivilege(HL7Constants.PRIV_ADD_HL7_IN_QUEUE)) {
					logger.error("You do not have HL7 add privilege!!");
					System.exit(0);
				}
				
				error = processMessageSegments(message, incomingMessageString);
			}
			try {
				ca.uhn.hl7v2.model.v25.segment.MSH msh = HL7ObsHandler25.getMSH( message);
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
					ca.uhn.hl7v2.model.v25.segment.MSH msh = HL7ObsHandler25.getMSH( message);
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
	
	private boolean processMessageSegments(Message message, String incomingMessageString) throws HL7Exception {
		
		//convert hl7 to version 2.3 so it can be parsed like mrf dump messages
		String newMessageString = incomingMessageString;
		newMessageString = HL7ToObs.replaceVersion(newMessageString);
		try {
			message = parser.parse(newMessageString);
		}catch(Exception e){
			logger.error(e);
		}
		
		boolean error = false;
		Date starttime = new Date();
		ZLR zlr = new ZLR(message);
		
		try {
			
			String institutionId = zlr.getOrderingFacilityIDNum();
			LocationService locationService = Context.getLocationService();
			
			Location encounterLocation = locationService.getLocation(institutionId);

			Date encounterDate = hl7EncounterHandler.getEncounterDate(message);
			Patient hl7Patient = patientHandler.setPatientFromHL7(message, encounterDate, encounterLocation,
			    hl7PatientHandler);
			PatientService patientService = Context.getPatientService();
			List<Patient> patients = patientService
					.getPatientsByIdentifier(mrn, false);
			Patient patient = null;
			Integer patientId = null;
			if (patients != null && patients.size() > 0)
			{
				patient = patients.get(0);
				patientId = patient.getPatientId();
			}
			HashMap<Integer, HashMap<String, Set<Obs>>> regenObs = new HashMap<Integer, HashMap<String, Set<Obs>>>();
			
			HL7ToObs.parseHL7ToObs(incomingMessageString, patient, mrn, regenObs);
			
			//TODO save obs to database, map first
			
			double duration = (new Date().getTime() - starttime.getTime()) / 1000.0;
			logger.info("MESSAGE PROCESS TIME: " + duration + " sec");
			
		}
		catch (RuntimeException e) {
			//Do not stop application. Start processing next hl7 message.
			logger.error("RuntimeException processing ORU_RO1", e);
			error = true;
		}
		return error;
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
