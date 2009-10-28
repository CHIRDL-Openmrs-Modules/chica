/**
 * 
 */
package org.openmrs.module.chica.hl7.mckesson;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.hibernateBeans.ATDError;
import org.openmrs.module.atd.hibernateBeans.Session;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.dss.util.IOUtil;
import org.openmrs.module.dss.util.Util;
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

/**
 * @author tmdugan
 * 
 */
public class HL7SocketHandler extends
		org.openmrs.module.chica.hl7.sms.HL7SocketHandler {
	
	/**
	 * @param parser
	 * @param patientHandler
	 */
	public HL7SocketHandler(ca.uhn.hl7v2.parser.Parser parser,
			PatientHandler patientHandler, HL7ObsHandler hl7ObsHandler,
			HL7EncounterHandler hl7EncounterHandler,
			HL7PatientHandler hl7PatientHandler,
			ArrayList<HL7Filter> filters) {
		
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
	public synchronized Message processMessage(Message message,
			HashMap<String,Object> parameters) throws ApplicationException {
		AdministrationService adminService = Context.getAdministrationService();
		String incomingMessageString = null;

		// switch message version and type to values for default hl7 handlers
		if (message instanceof ca.uhn.hl7v2.model.v22.message.ADT_A04) {
			try {
				ca.uhn.hl7v2.model.v22.message.ADT_A04 adt = (ca.uhn.hl7v2.model.v22.message.ADT_A04) message;
				adt.getMSH().getVersionID().setValue("2.5");
				adt.getMSH().getMessageType().getTriggerEvent().setValue("A01");
				incomingMessageString = this.parser.encode(message);
				message = this.parser.parse(incomingMessageString);
			} catch (Exception e) {
				ATDError error = new ATDError("Fatal", "Hl7 Parsing",
						"Error parsing the McKesson checkin hl7 "
								+ e.getMessage(),
						org.openmrs.module.dss.util.Util.getStackTrace(e),
						new Date(), null);
				ATDService atdService = Context.getService(ATDService.class);

				atdService.saveError(error);
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
				return null;
			}
		}

		try {
			incomingMessageString = this.parser.encode(message);
			message.addNonstandardSegment("ZPV");
		} catch (HL7Exception e) {
			logger.error(e.getMessage());
			logger.error(org.openmrs.module.dss.util.Util.getStackTrace(e));
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
	

	@Override
	public org.openmrs.Encounter processEncounter(String incomingMessageString,
			Patient p, Date encDate, org.openmrs.Encounter newEncounter,
			Provider provider, HashMap<String, Object> parameters)
	{
		ATDService atdService = Context.getService(ATDService.class);
		org.openmrs.Encounter encounter = super.processEncounter(
				incomingMessageString, p, encDate, newEncounter, provider,
				parameters);
		//store the encounter id with the session
		Integer encounterId = newEncounter.getEncounterId();
		getSession(parameters).setEncounterId(encounterId);
		atdService.updateSession(getSession(parameters));
		if (incomingMessageString == null)
		{
			return encounter;
		}

		LocationService locationService = Context.getLocationService();

		String locationString = null;
		Date appointmentTime = null;
		String planCode = null;
		String carrierCode = null;
		String printerLocation = null;
		Message message;
		try
		{
			message = this.parser.parse(incomingMessageString);
			EncounterService encounterService = Context
					.getService(EncounterService.class);
			encounter = encounterService.getEncounter(encounter
					.getEncounterId());
			if (this.hl7EncounterHandler instanceof org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25)
			{
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
						+ "a new location was created for '"
						+ locationString + "'");
			}
		}

		chicaEncounter.setLocation(location);
		chicaEncounter.setInsuranceSmsCode(null);

		encounterService.saveEncounter(chicaEncounter);
		
		return encounter;
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
}
