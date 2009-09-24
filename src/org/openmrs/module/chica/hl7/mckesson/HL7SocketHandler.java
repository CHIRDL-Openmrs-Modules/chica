/**
 * 
 */
package org.openmrs.module.chica.hl7.mckesson;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.hibernateBeans.ChicaError;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.service.ChicaService;
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

/**
 * @author tmdugan
 * 
 */
public class HL7SocketHandler extends
		org.openmrs.module.chica.hl7.sms.HL7SocketHandler {
	private String planCode = null;
	private String carrierCode = null;
	private String locationString = null;
	private Date appointmentTime = null;
	private String printerLocation = null;

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
	public Message processMessage(Message message) throws ApplicationException {
		ChicaService chicaService = Context.getService(ChicaService.class);
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
				ChicaError error = new ChicaError("Fatal", "Hl7 Parsing",
						"Error parsing the McKesson checkin hl7 "
								+ e.getMessage(),
						org.openmrs.module.dss.util.Util.getStackTrace(e),
						new Date(), null);
				chicaService.saveError(error);
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

		if (this.hl7EncounterHandler instanceof org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25) {
			this.locationString = ((org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25) this.hl7EncounterHandler)
					.getLocation(message);

			this.appointmentTime = ((org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25) this.hl7EncounterHandler)
					.getAppointmentTime(message);

			this.planCode = ((org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25) this.hl7EncounterHandler)
					.getInsurancePlan(message);

			this.carrierCode = ((org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25) this.hl7EncounterHandler)
					.getInsuranceCarrier(message);

			this.printerLocation = ((org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25) this.hl7EncounterHandler)
					.getPrinterLocation(message,incomingMessageString);
		}

		if (this.printerLocation != null && this.printerLocation.equals("0")) {
			// ignore this message because it is just kids getting shots
			return message;
		}
		return super.processMessage(message);
	}

	@Override
	protected org.openmrs.Encounter createEncounter(Patient resultPatient,
			org.openmrs.Encounter newEncounter, Provider provider) {
		LocationService locationService = Context.getLocationService();
		org.openmrs.Encounter encounter = super.createEncounter(resultPatient,
				newEncounter, provider);

		Integer encounterId = encounter.getEncounterId();
		EncounterService encounterService = Context
				.getService(EncounterService.class);
		encounter = encounterService.getEncounter(encounterId);
		Encounter chicaEncounter = (org.openmrs.module.chica.hibernateBeans.Encounter) encounter;

		chicaEncounter.setInsurancePlanCode(this.planCode);
		chicaEncounter.setInsuranceCarrierCode(this.carrierCode);
		chicaEncounter.setScheduledTime(this.appointmentTime);
		chicaEncounter.setPrinterLocation(this.printerLocation);

		Location location = null;

		if (this.locationString != null) {
			location = locationService.getLocation(this.locationString);

			if (location == null) {
				location = new Location();
				location.setName(this.locationString);
				locationService.saveLocation(location);
				logger.warn("Location '" + this.locationString
						+ "' does not exist in the Location table."
						+ "a new location was created for '"
						+ this.locationString + "'");
			}
		}

		chicaEncounter.setLocation(location);
		chicaEncounter.setInsuranceSmsCode(null);

		encounterService.saveEncounter(chicaEncounter);

		return chicaEncounter;
	}
}
