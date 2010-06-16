package org.openmrs.module.chica.hl7.mckesson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNumeric;
import org.openmrs.ConceptSet;
import org.openmrs.LocationTag;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.atd.hibernateBeans.ATDError;
import org.openmrs.module.chica.hibernateBeans.ChicaHL7Export;
import org.openmrs.module.chica.hibernateBeans.ChicaHL7ExportMap;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chirdlutil.hibernateBeans.LocationAttributeValue;
import org.openmrs.module.chirdlutil.hibernateBeans.LocationTagAttributeValue;
import org.openmrs.module.chirdlutil.service.ChirdlUtilService;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chica.util.Util;
import org.openmrs.module.chirdlutil.util.Base64;
import org.openmrs.module.chirdlutil.util.FileDateComparator;
import org.openmrs.module.chirdlutil.util.FileListFilter;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.sockethl7listener.HL7MessageConstructor;
import org.openmrs.module.sockethl7listener.HL7SocketHandler;
import org.openmrs.module.sockethl7listener.hibernateBeans.HL7Outbound;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.scheduler.tasks.AbstractTask;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ca.uhn.hl7v2.model.v25.segment.OBX;

/**
 * Determines which encounters require an hl7 to be exported to RMRS, and
 * creates the outbound hl7 messages to send on the specified port.
 * 
 * @author msheley
 * 
 */
public class HL7Exporter extends AbstractTask {

	private Log log = LogFactory.getLog(this.getClass());
	private TaskDefinition taskConfig;
	private String host;
	private Integer port;
	private Integer socketReadTimeout;
	private HL7SocketHandler socketHandler;

	@Override
	public void initialize(TaskDefinition config) {

		Context.openSession();

		try {
			if (Context.isAuthenticated() == false)
				authenticate();

			this.taskConfig = config;
			// port to export
			String portName = this.taskConfig.getProperty("port");
			host = this.taskConfig.getProperty("host");
			String socketReadTimeoutString = this.taskConfig.getProperty("socketReadTimeout");

			if (host == null) {
				host = "localhost";
			}

			if (portName != null) {
				port = Integer.parseInt(portName);
			} else {
				port = 0;
			}
			if (socketReadTimeoutString != null) {
				socketReadTimeout = Integer.parseInt(socketReadTimeoutString);
			} else {
				socketReadTimeout = 5; // seconds
			}

		} finally {

			Context.closeSession();
		}

	}

	/*
	 * Executes loading the queue table for sessions requiring hl7 responses
	 * Constructs the hl7 messages, sends the message, and saves text to a file
	 */
	@Override
	public void execute() {

		ChicaService chicaService = Context.getService(ChicaService.class);
		ATDService atdService = Context.getService(ATDService.class);
		EncounterService encounterService = Context.getService(EncounterService.class);
		socketHandler = new HL7SocketHandler();

		String conceptCategory = "";
		ChicaHL7Export export = new ChicaHL7Export();
		Context.openSession();

		try {

			if (Context.isAuthenticated() == false)
				authenticate();

			// get list of pending exports
			List<ChicaHL7Export> exportList = chicaService.getPendingHL7Exports();
			Iterator<ChicaHL7Export> it = exportList.iterator();

			while (it.hasNext()) {

				export = it.next();

				// Get location of hl7 configuration file from
				// location_tag_attribute_value
				Integer encId = export.getEncounterId();
				Integer hl7ExportQueueId = export.getQueueId();

				Document doc;
				// Get the hl7 config file
				String configFileName;
				try {
					// Get the mapping files
					String conceptMapFile = getChicaExportConceptMapByQueueId(hl7ExportQueueId);
					if (conceptMapFile == null) {
						export.setStatus(chicaService
								.getChicaExportStatusByName("concept_map_location_unknown"));
						chicaService.saveChicaHL7Export(export);
						continue;
					}

					doc = getDocument(conceptMapFile);
					if (doc == null) {
						export.setStatus(chicaService
								.getChicaExportStatusByName("XML_parsing_error"));
						chicaService.saveChicaHL7Export(export);
						continue;
					}

					configFileName = getFileLocation(encId);
					if (configFileName == null || configFileName.equalsIgnoreCase("")) {
						export.setStatus(chicaService
								.getChicaExportStatusByName("hl7_config_file_not_found"));
						chicaService.saveChicaHL7Export(export);
						continue;
					}

					Hashtable<String, String> mappings = this.loadHashTable(doc);
					conceptCategory = doc.getDocumentElement().getAttribute("category");
					Properties hl7Properties = Util.getProps(configFileName);
					if (hl7Properties == null) {
						export.setStatus(chicaService
								.getChicaExportStatusByName("no_hl7_config_properties"));
						chicaService.saveChicaHL7Export(export);
						continue;
					}

					int numberOfOBXSegments = 0;
					boolean sendObs = false;

					Encounter openmrsEncounter = (Encounter) encounterService.getEncounter(encId);

					HL7MessageConstructor constructor = new HL7MessageConstructor(configFileName);

					if (conceptCategory != null
							&& (conceptCategory.equalsIgnoreCase("PSF TIFF") || conceptCategory
									.equalsIgnoreCase("PWS TIFF"))) {
						constructor.setImage(true);
						String sendImage = hl7Properties.getProperty("send_form_images");
						if (sendImage != null && sendImage.equalsIgnoreCase("false")) {
							export.setStatus(chicaService
											.getChicaExportStatusByName("do_not_send"));
							chicaService.saveChicaHL7Export(export);
							continue;
						}

					}

					List<Encounter> queryEncounterList = new ArrayList<Encounter>();
					queryEncounterList.add(openmrsEncounter);

					constructor.AddSegmentMSH(openmrsEncounter);
					constructor.AddSegmentPID(openmrsEncounter.getPatient());
					constructor.AddSegmentPV1(openmrsEncounter);

					// Construct TIFFS

					if (conceptCategory != null && conceptCategory.equalsIgnoreCase("PSF TIFF")) {

						if (!addOBXForTiff(constructor, openmrsEncounter, "PSF", mappings,
								hl7Properties)) {
							export.setStatus(chicaService
									.getChicaExportStatusByName("Image_not_found"));
							chicaService.saveChicaHL7Export(export);
							continue;
						}

					}

					if (conceptCategory != null && conceptCategory.equalsIgnoreCase("PWS TIFF")) {
						
						if (!addOBXForTiff(constructor, openmrsEncounter, "PWS", mappings,
								hl7Properties)) {
							export.setStatus(chicaService
									.getChicaExportStatusByName("Image_not_found"));
							chicaService.saveChicaHL7Export(export);
							continue;
						}

					}

					// Get observations for vitals and general observations.
					// Create an OBR and block of OBX vitals
					// Create an OBR and block of OBX for non vitals

					if (conceptCategory != null && conceptCategory.equalsIgnoreCase("Vitals")
							|| conceptCategory.equalsIgnoreCase("POC")) {

						// First the vital battery
						String batteryName = hl7Properties.getProperty("vitals_battery_name");

						List<Obs> obsList = getObsListByBattery(openmrsEncounter, mappings,
								batteryName);

						// Create OBR and OBX segments for Vitals
						int orderRep = 0;
						if (obsList != null && obsList.size() > 0) {
							numberOfOBXSegments = addOBXBlock(constructor, openmrsEncounter,
									obsList, mappings, batteryName, orderRep);
							orderRep++;
							if (numberOfOBXSegments > 0)
								sendObs = true;
						}

						// general -- MEDICAL RECORD FILE OBSERVATIONS
						batteryName = hl7Properties.getProperty("general_battery_name");
						List<Obs> obsListMRF = getObsListByBattery(openmrsEncounter, mappings,
								batteryName);

						// Create OBR and OBX segments for MEDICAL RECORD FILE
						// OBSERVATIONS
						if (obsListMRF != null && obsListMRF.size() > 0) {
							numberOfOBXSegments = addOBXBlock(constructor, openmrsEncounter,
									obsListMRF, mappings, batteryName, orderRep);
							if (numberOfOBXSegments > 0)
								sendObs = true;
						}

						// If no observations, do not create message
						if (!sendObs) {
							export.setStatus(chicaService.getChicaExportStatusByName("no_obs"));
							chicaService.saveChicaHL7Export(export);
							continue;
						}

					}
					
					String message = constructor.getMessage();
					export.setStatus(chicaService.getChicaExportStatusByName("hl7_sent"));
					export.setDateProcessed(new Date());

					try {
						socketHandler.openSocket(host, port);

					} catch (IOException e) {
						log.error("Error opening socket: " + host + "|" + port + e.getMessage());
						continue;
					}

					if (message != null && !message.equals("")) {
						Date ackDate = sendMessage(message, openmrsEncounter, socketHandler);
						if (ackDate != null) {
							export.setStatus(chicaService
									.getChicaExportStatusByName("ACK_received"));
							export.setAckDate(ackDate);

						} else {
							export.setStatus(chicaService
									.getChicaExportStatusByName("ACK_not_received"));
						}
						saveMessageFile(message, encId, ackDate);
					}
					chicaService.saveChicaHL7Export(export);

				} catch (Exception e) {
					Integer sessionId = export.getSessionId();
					ATDError ce = new ATDError("Error", "Hl7 Export",
							"Error sending or saving hl7 export message: " + e.getMessage(),
							org.openmrs.module.chirdlutil.util.Util.getStackTrace(e), new Date(),
							sessionId);
					atdService.saveError(ce);
					log.error("Error sending or saving hl7 export message:", e);
				}

			}
		} catch (Exception e)

		{
			Integer sessionId = export.getSessionId();
			ATDError ce = new ATDError("Error", "Hl7 Export", "Error creating hl7 export: "
					+ e.getMessage(), org.openmrs.module.chirdlutil.util.Util.getStackTrace(e),
					new Date(), sessionId);
			atdService.saveError(ce);
			log.error("Error creating hl7 export:", e);

		} finally {
			socketHandler.closeSocket();
			Context.closeSession();
		}

	}

	/**
	 * Saves message string to archive directory
	 * 
	 * @param message
	 * @param encid
	 */
	public void saveMessageFile(String message, Integer encid, Date ackDate) {
		AdministrationService adminService = Context.getAdministrationService();
		EncounterService es = Context.getService(EncounterService.class);

		org.openmrs.Encounter enc = es.getEncounter(encid);
		Patient patient = new Patient();
		patient = enc.getPatient();
		PatientIdentifier pi = patient.getPatientIdentifier();
		String mrn = "";
		String ack = "";
		if (ackDate != null) {
			ack = "-ACK";
		}

		if (pi != null)
			mrn = pi.getIdentifier();
		String filename = org.openmrs.module.chirdlutil.util.Util.archiveStamp() + "_" + mrn + ack
				+ ".hl7";

		String archiveDir = IOUtil.formatDirectoryName(adminService
				.getGlobalProperty("chica.outboundHl7ArchiveDirectory"));

		FileOutputStream archiveFile = null;
		try {
			archiveFile = new FileOutputStream(archiveDir + "/" + filename);
		} catch (FileNotFoundException e1) {
			log.error("Couldn't find file: " + archiveDir + "/" + filename);
		}
		if (archiveDir != null && archiveFile != null) {
			try {

				ByteArrayInputStream messageStream = new ByteArrayInputStream(message.getBytes());
				IOUtil.bufferedReadWrite(messageStream, archiveFile);
				archiveFile.flush();
				archiveFile.close();
			} catch (Exception e) {
				try {
					archiveFile.flush();
					archiveFile.close();
				} catch (Exception e1) {
				}
				log.error("There was an error writing the hl7 file");
				log.error(e.getMessage());
				log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
			}
		}
		return;

	}

	@Override
	public void shutdown() {
		super.shutdown();
		try {

			// this.server.stop();
		} catch (Exception e) {
			this.log.error(e.getMessage());
			this.log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}
	}

	private Concept getRMRSConceptByName(String rmrsname) {

		ConceptService cs = Context.getConceptService();
		Concept rmrsConcept = null;

		List<ConceptClass> classList = new ArrayList<ConceptClass>();
		try {
			ConceptClass rmrsClass = cs.getConceptClassByName("RMRS");
			classList.add(rmrsClass);

			List<Concept> conceptsWithSameName = cs.getConcepts(rmrsname, Context.getLocale(),
					false, classList, null);

			if (conceptsWithSameName != null && conceptsWithSameName.size() > 0) {
				if (conceptsWithSameName.size() > 1) {
					log.error("More than one RMRS concept exist with exact name of " + rmrsname);
				}
				rmrsConcept = conceptsWithSameName.get(0);
			} else {
				log.error("No RMRS class concepts found with exact name of " + rmrsname);
			}
		} catch (APIException e) {
			log.error("ConceptClass api exception." + e.getMessage());

		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return rmrsConcept;
	}

	private String getRMRSCodeFromConcept(Concept rmrsConcept) {
		String rmrsCode = "";
		// get the mappings
		// get the source

		Iterator<ConceptMap> mapsIter = rmrsConcept.getConceptMappings().iterator();
		if (mapsIter.hasNext()) {
			rmrsCode = mapsIter.next().getSourceCode();
		}
		return rmrsCode;
	}

	private String getUnits(Concept concept) {
		String units = "";
		ConceptService cs = Context.getConceptService();
		ConceptDatatype datatype = concept.getDatatype();
		Integer conceptId = concept.getConceptId();
		if (datatype != null && datatype.isNumeric()) {
			ConceptNumeric rmrsNumericConcept = (ConceptNumeric) cs.getConcept(conceptId);
			units = rmrsNumericConcept.getUnits();
		}
		return units;
	}

	private boolean checkConceptSet(Concept conceptToTest, String batteryName) {
		boolean match = false;
		ConceptService cs = Context.getConceptService();

		if (conceptToTest == null || batteryName == null || batteryName.equals("")) {
			return false;
		}

		List<Concept> conceptsWithBatteryName = new ArrayList<Concept>();
		conceptsWithBatteryName = cs.getConcepts(batteryName + " CHICA", Context.getLocale(),
				false, null, null);

		Concept batteryConcept = new Concept();
		for (Concept concept : conceptsWithBatteryName) {
			if (concept.isSet()) {
				batteryConcept = concept;
				continue;
			}
		}
		Collection<ConceptSet> allConceptsInBattery = cs.getConceptSetsByConcept(batteryConcept);
		for (ConceptSet concept : allConceptsInBattery) {

			if (conceptToTest.getConceptId().equals(concept.getConcept().getConceptId())) {
				match = true;
				continue;
			}

		}

		return match;

	}

	private Hashtable<String, String> loadHashTable(Document doc) {

		String chicaConceptName = null;
		String rmrsConceptName = null;
		NodeList chicaNodes = null;
		if (doc != null) {
			chicaNodes = doc.getElementsByTagName("CHICA");
		}
		Hashtable<String, String> conceptMapping = new Hashtable<String, String>();

		for (int i = 0; i < chicaNodes.getLength(); i++) {
			Node chicaTextNode = chicaNodes.item(i).getFirstChild();
			if (chicaTextNode != null) {
				chicaConceptName = chicaTextNode.getNodeValue();
			}
			Node rmrsNode = chicaNodes.item(i).getNextSibling();

			if (rmrsNode != null) {
				Node rmrsTextNode = rmrsNode.getFirstChild();
				if (rmrsTextNode != null) {
					rmrsConceptName = rmrsTextNode.getNodeValue();
				}
			}
			conceptMapping.put(chicaConceptName, rmrsConceptName);

		}
		return conceptMapping;
	}

	private List<Obs> getObsListByBattery(Encounter encounter, Hashtable<String, String> table,
			String batteryName) {

		ConceptService conceptService = Context.getConceptService();
		ObsService obsService = Context.getObsService();
		List<Concept> concepts = new ArrayList<Concept>();
		List<Obs> obsList = null;

		try {

			Enumeration<String> en = table.keys();
			while (en.hasMoreElements()) {
				String chicaConceptName = (String) en.nextElement();
				Concept chicaConcept = conceptService.getConceptByName(chicaConceptName);
				if (chicaConcept != null && checkConceptSet(chicaConcept, batteryName)) {
					concepts.add(chicaConcept);
				}

			}

			if (concepts.size() > 0) {
				List<org.openmrs.Encounter> encounters = new ArrayList<org.openmrs.Encounter>();
				encounters.add(encounter);

				obsList = obsService.getObservations(null, encounters, concepts, null, null, null,
						null, null, null, null, null, false);
			}
		} catch (Exception e) {
			log.error("Error collecting obs list from concept map.");
		}

		return obsList;

	}

	private int addOBXBlock(HL7MessageConstructor constructor, Encounter encounter,
			List<Obs> obsList, Hashtable<String, String> mappings, String batteryName, int orderRep) {
		// Get all obs for one encounter, where the concept is in the mapping
		// properties xml
		// If an obs for that concept does not exist for an encounter, we do not
		// create an OBX

		Locale locale = new Locale("en_US");
		String units = "";
		String rmrsCode = "";
		String hl7Abbreviation = "";
		ConceptDatatype conceptDatatype = null;
		int obsRep = 0;
		addOBRSegment(constructor, encounter, batteryName, orderRep);

		for (Obs obs : obsList) {
			Concept chicaConcept = obs.getConcept();
			ConceptName cname = chicaConcept.getName(locale);
			if (cname != null) {
				String rmrsName = mappings.get(cname.getName());
				if (rmrsName == null)
					continue;
				Concept rmrsConcept = getRMRSConceptByName(rmrsName);
				if (rmrsConcept != null) {
					rmrsCode = getRMRSCodeFromConcept(rmrsConcept);
				}
				String value = obs.getValueAsString(locale);

				if (chicaConcept.isNumeric()) {
					Double obsRounded = org.openmrs.module.chirdlutil.util.Util.round(Double
							.valueOf(obs.getValueNumeric()), 1);
					if (obsRounded != null) {
						value = String.valueOf(obsRounded);
					}
				}
				conceptDatatype = rmrsConcept.getDatatype();
				String sourceCode = "";

				if (conceptDatatype != null && conceptDatatype.isCoded()) {
					Concept answer = obs.getValueCoded();

					Collection<ConceptMap> maps = answer.getConceptMappings();
					ConceptMap map = null;
					if (maps != null) {
						Iterator<ConceptMap> it = maps.iterator();
						if (it.hasNext()) {
							// get first
							map = it.next();
						}
						if (map != null) {
							sourceCode = map.getSourceCode();
						}
					}

				}
				hl7Abbreviation = conceptDatatype.getHl7Abbreviation();
				units = getUnits(rmrsConcept);
				Date datetime = obs.getObsDatetime();
				constructor.AddSegmentOBX(rmrsName, rmrsCode, null, sourceCode, value, units,
						datetime, hl7Abbreviation, orderRep, obsRep);
				obsRep++;

			}

		}

		return obsRep;
	}

	private Date sendMessage(String message, Encounter enc, HL7SocketHandler socketHandler) {
		Date ackDate = null;

		HL7Outbound hl7b = new HL7Outbound();
		hl7b.setHl7Message(message);
		hl7b.setEncounter(enc);
		hl7b.setAckReceived(null);
		hl7b.setPort(port);
		hl7b.setHost(host);

		try {
			if (message != null) {
				hl7b = socketHandler.sendMessage(hl7b, socketReadTimeout);

				if (hl7b != null && hl7b.getAckReceived() != null) {
					ackDate = hl7b.getAckReceived();
					log.info("Ack received host:" + host + "; port:" + port
							+ "- first try. Encounter_id = " + enc.getEncounterId());
				}

			}

		} catch (Exception e) {
			log.error("Error exporting message host:" + host + "; port:" + port
					+ "- first try. Encounter_id = " + enc.getEncounterId());
			try {
				if (message != null) {

					hl7b = socketHandler.sendMessage(hl7b, socketReadTimeout);
					if (hl7b != null && hl7b.getAckReceived() != null) {
						ackDate = hl7b.getAckReceived();
						log.info("Ack received host:" + host + "; port:" + port
								+ "- second try. Encounter_id = " + enc.getEncounterId());
					}
				}
			} catch (Exception e1) {
				log.error("Error exporting message host:" + host + "; port:" + port
						+ "- second try. Encounter_id = " + enc.getEncounterId());
			}
		}
		return ackDate;
	}

	private String getFileLocation(Integer encId) {
		String filename = "";
		EncounterService encounterService = Context.getService(EncounterService.class);
		LocationService locationService = Context.getLocationService();
		ChirdlUtilService chirdlUtilService = Context.getService(ChirdlUtilService.class);
		LocationTag locTag = null;

		org.openmrs.module.chica.hibernateBeans.Encounter chicaEncounter = (org.openmrs.module.chica.hibernateBeans.Encounter) encounterService
				.getEncounter(encId);

		Location loc = chicaEncounter.getLocation();
		Integer locId = null;
		if (loc != null) {
			locId = loc.getLocationId();
		}

		String printerLocation = chicaEncounter.getPrinterLocation();
		if (printerLocation != null) {
			locTag = locationService.getLocationTagByName(printerLocation);
		}
		if (printerLocation == null) {
			LocationAttributeValue locAttrValue = chirdlUtilService.getLocationAttributeValue(
					locId, "defaultPrinterLocation");
			if (locAttrValue != null) {
				String locTagStr = locAttrValue.getValue();
				if (locTagStr != null && !locTagStr.equals("")) {
					locTag = locationService.getLocationTag(Integer.valueOf(locTagStr));
				}
			}

		}

		if (locTag != null) {
			LocationTagAttributeValue locationTagValue = chirdlUtilService
					.getLocationTagAttributeValue(locTag.getId(), "HL7ConfigFile", chicaEncounter
							.getLocation().getLocationId());
			if (locationTagValue != null)
				filename = locationTagValue.getValue();
		}
		return filename;
	}

	private String getBatteryIdByConcept(Concept batteryConcept) {

		String sourceCode = "";
		Collection<ConceptMap> maps = batteryConcept.getConceptMappings();
		Iterator<ConceptMap> it = maps.iterator();
		if (it.hasNext()) {
			sourceCode = it.next().getSourceCode();
		}
		return sourceCode;

	}

	private String getChicaExportConceptMapByQueueId(Integer hl7ExportQueueId) {
		String filename = null;
		ChirdlUtilService chirdlUtilService = Context.getService(ChirdlUtilService.class);
		ChicaService chicaService = Context.getService(ChicaService.class);
		ChicaHL7ExportMap exportmap = chicaService.getChicaExportMapByQueueId(hl7ExportQueueId);

		if (exportmap == null) {
			return null;
		}

		String locationTagAttributeIdStr = exportmap.getValue();
		if (locationTagAttributeIdStr != null) {
			Integer locationTagAttributeId = Integer.valueOf(locationTagAttributeIdStr);
			LocationTagAttributeValue value = chirdlUtilService
					.getLocationTagAttributeValueById(locationTagAttributeId);
			if (value != null) {
				filename = value.getValue();
			}
		}

		return filename;
	}

	private Document getDocument(String conceptMapFile) {
		File file = new File(conceptMapFile);
		Document doc = null;
		if (file.exists()) {
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				doc = builder.parse(file);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			} catch (ParserConfigurationException e) {

				log.error("Unable to parse XML map file");
			} catch (SAXException e) {

				log.error("SAX handler exeception when parsing XML map file");
			}
		}
		return doc;
	}
	
  	
	private boolean addOBXForTiff(HL7MessageConstructor constructor, Encounter encounter,
			String form, Hashtable<String, String> mappings, Properties hl7Properties) {
		boolean obxcreated = false;
		Integer locationTagId = null;
		String attachmentConceptName = hl7Properties.getProperty("attachment_battery_name");
		String attachmentText = hl7Properties.getProperty("attachment_text");

		ATDService atdService = Context.getService(ATDService.class);

		String formDir = "";
		String hl7Abbreviation = "ED";
		Integer encounterId = encounter.getEncounterId();
		locationTagId = getLocationTagIdByEncounter(encounterId);

		
		
		List<PatientState> patientStates = atdService.getPatientStatesWithFormInstances(form,
				encounterId);

		FormInstance formInstance = null;
		Integer formId = null;
		Integer formInstanceId = null;
		Integer formLocationId = null;

		if (patientStates == null) {
			return false;
		}

		Iterator<PatientState> psIterator = patientStates.iterator();
		if (psIterator.hasNext()) {
			formInstance = psIterator.next().getFormInstance();
			if (formInstance == null) {
				return false;
			}
		}

		String filename = "";
		String encodedForm = "";
		try {
			formId = formInstance.getFormId();
			formInstanceId = formInstance.getFormInstanceId();
			formLocationId = formInstance.getLocationId();

			if (formId == null || locationTagId == null || formLocationId == null
					|| formInstanceId == null) {
				return false;
			}

			formDir = IOUtil
					.formatDirectoryName(org.openmrs.module.atd.util.Util.getFormAttributeValue(
							formId, "imageDirectory", locationTagId, formLocationId));

			if (formDir == null || formDir.equals("")) {
				return false;
			}
			filename = formLocationId + "-" + formId + "-" + formInstanceId;
			
			//This FilenameFilter will get ALL tifs starting with the filename
			//including of rescan versions nnn_1.tif, nnn_2.tif, etc
			FilenameFilter filtered = new FileListFilter(filename, "tif");
			File dir = new File(formDir);
			File[] files = dir.listFiles(filtered); 
			if (files == null || files.length == 0){
				return false;
			}
			
			//This FileDateComparator will list in order
			//with newest file first.
			Arrays.sort(files, new FileDateComparator());
		
			encodedForm = encodeForm(files[0]);
			if (encodedForm == null) {
				return false;
			}

			String rmrsName = "";
			int obsRep = 0;
			Collection<String> values = mappings.values();
			Iterator<String> it = values.iterator();
			if (it.hasNext()) {
				rmrsName = it.next();
			}
			
			Concept rmrsConcept = getRMRSConceptByName(rmrsName);
			if (rmrsConcept != null) {

				String rmrsCode = getRMRSCodeFromConcept(rmrsConcept);
				Date datetime = encounter.getDateCreated();

				Concept attachmentConcept = getRMRSConceptByName(attachmentConceptName);
				if (attachmentConcept == null) {
					log.error("Concept for OBX containing attachment text does not exist. "
							+ "Check that the Concept for the obx attachment has been created.");
					return false;
				}
				String attachmentCode = getRMRSCodeFromConcept(attachmentConcept);

				int orderRep = 0;
				addOBRSegment(constructor, encounter, rmrsName, orderRep);

				constructor.AddSegmentOBX(attachmentConceptName, attachmentCode, null, null,
						attachmentText, null, datetime, "ST", orderRep, obsRep);
				OBX resultOBX = constructor.AddSegmentOBX(rmrsName, rmrsCode, null, "",
						encodedForm, "", datetime, hl7Abbreviation, orderRep, obsRep + 1);
				if (resultOBX != null) {
					obxcreated = true;
				}
			}
		} catch (Exception e) {
			log.error("Exception adding OBX for tiff image. " + e.getMessage());
		}

		return obxcreated;

	}

	private Integer getLocationTagIdByEncounter(Integer encId) {
		Integer locationTagId = null;
		String printerLocation = null;

		EncounterService encounterService = Context.getService(EncounterService.class);
		Encounter encounter = (Encounter) encounterService.getEncounter(encId);

		try {
			if (encId != null && encounter != null) {
				// see if the encounter has a printer location
				// this will give us the location tag id
				printerLocation = encounter.getPrinterLocation();

				// if the printer location is null, pick
				// any location tag id for the given location
				if (printerLocation == null) {
					Location location = encounter.getLocation();
					if (location != null) {
						Set<LocationTag> tags = location.getTags();

						if (tags != null && tags.size() > 0) {
							printerLocation = ((LocationTag) tags.toArray()[0]).getTag();
						}
					}
				}
				if (printerLocation != null) {
					LocationService locationService = Context.getLocationService();
					LocationTag tag = locationService.getLocationTagByName(printerLocation);
					if (tag != null) {
						locationTagId = tag.getLocationTagId();
					}
				}

			}
		} catch (APIException e) {
			log.error("LocationTag api exception: " + e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return locationTagId;
	}

	private String encodeForm(File file) {
		String encodedForm = null;
		try {

			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bas = new ByteArrayOutputStream();

			int c;
			while ((c = fis.read()) != -1) {
				bas.write(c);
			}
			encodedForm = Base64.byteArrayToBase64(bas.toByteArray(), false);

			fis.close();
			bas.flush();
			bas.close();

		} catch (FileNotFoundException e) {
			log.error("Tiff file not found");
		} catch (IOException e) {
			log.error("Unable to read tiff file.");
		}

		return encodedForm;

	}

	private void addOBRSegment(HL7MessageConstructor constructor, Encounter openmrsEncounter,
			String batteryName, int orderRep) {

		ConceptService cs = Context.getConceptService();

		String batteryId = null;

		if (batteryName != null) {
			Concept batteryConcept = cs.getConceptByName(batteryName);
			batteryId = getBatteryIdByConcept(batteryConcept);
		}

		constructor.AddSegmentOBR(openmrsEncounter, batteryId, batteryName, orderRep);

	}

}
