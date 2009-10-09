package org.openmrs.module.chica.hl7.mckesson;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptName;
import org.openmrs.ConceptNumeric;
import org.openmrs.ConceptSet;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.hibernateBeans.Session;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.hibernateBeans.ChicaError;
import org.openmrs.module.chica.hibernateBeans.ChicaHL7Export;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chica.util.Util;
import org.openmrs.module.dss.util.IOUtil;
import org.openmrs.module.sockethl7listener.HL7MessageConstructor;
import org.openmrs.module.sockethl7listener.HL7SocketHandler;
import org.openmrs.module.sockethl7listener.hibernateBeans.HL7Outbound;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.scheduler.tasks.AbstractTask;

/**
 * Determines which encounters require an hl7 to be exported to RMRS, 
 * and creates the outbound hl7 messages to send on the specified port.
 * @author msheley
 *
 */
public class HL7Exporter extends AbstractTask {

	private Log log = LogFactory.getLog(this.getClass());
	private TaskDefinition taskConfig;
	private String host;
	private Integer port;
	private HL7SocketHandler socketHandler;
	
	
	@Override
	public void initialize(TaskDefinition config)
	{

		Context.openSession();
		
		try {
			if (Context.isAuthenticated() == false)
				authenticate();
			
			this.taskConfig = config;
			//port to export
			String portName = this.taskConfig.getProperty("port");
			host  = this.taskConfig.getProperty("host");
			
			if (host == null){
				host = "localhost";
			}
			
			if (portName != null){
				port = Integer.parseInt(portName);
			} else
			{
				port = 0;
			}
			
		}finally{
			
			Context.closeSession();
		}

	}

	/* 
	 * Executes loading the queue table for sessions requiring hl7 responses
	 * Constructs the hl7 messages, sends the message, and saves text to a file
	 */
	@Override
	public void execute()
	{
		
		ChicaService chicaService = Context.getService(ChicaService.class);
		EncounterService encounterService = Context.getService(EncounterService.class);
		ATDService atdService = Context.getService(ATDService.class);
		AdministrationService adminService = Context.getAdministrationService();
		ObsService obsService = Context.getObsService();
		socketHandler = new HL7SocketHandler();
		
		String vitalsBatteryId  = "";
		String vitalsBatteryName = "";
		String generalBatteryName = "";
		String generalBatteryId = "";
		String socketReadTimeoutStr = "";
		Integer socketReadTimeout = null;
		
		
		boolean vitalsBatteryOnly = true;
		Integer encid = null;
		List <Concept> concepts = null;
		Context.openSession();
		
		String hl7ConfigFile = adminService
				.getGlobalProperty("sockethl7listener.hl7ConfigFile");
			
		Properties hl7Prop = Util.getProps(hl7ConfigFile);
		if (hl7Prop != null){
			String vOnly = hl7Prop.getProperty("use_vitals_battery_only");
			if (vOnly != null) vitalsBatteryOnly = Boolean.valueOf(vOnly);
			vitalsBatteryName = hl7Prop.getProperty("vitals_battery_name");
			vitalsBatteryId = hl7Prop.getProperty("vitals_battery_id");
			generalBatteryName = hl7Prop.getProperty("general_battery_name");
			generalBatteryId = hl7Prop.getProperty("general_battery_id");
			socketReadTimeoutStr = hl7Prop.getProperty("socket_read_timeout");
			if (socketReadTimeoutStr != null) {
				socketReadTimeout = Integer.valueOf(socketReadTimeoutStr);
			}
		}
	
		try
		{
			
			if (Context.isAuthenticated() == false)
				authenticate();

			String conceptDictionaryMapFile = adminService
				.getGlobalProperty("chica.conceptDictionaryMapFile");
			
			socketHandler.openSocket(host, port);
			
			//get list of pending exports
			List <ChicaHL7Export> exportList = chicaService.getPendingHL7Exports();
			Iterator <ChicaHL7Export> it = exportList.iterator();
		
			while (it.hasNext()){
				HL7MessageConstructor constructor = new HL7MessageConstructor();
				
				ChicaHL7Export export = it.next();
				Integer encId = export.getEncounterId();
				Encounter enc = encounterService.getEncounter(encId);
				List<Encounter> queryEncounterList = new ArrayList<Encounter>();
				//create list with one entry for parameter in obs search only
				queryEncounterList.add(enc);
			
				constructor.AddSegmentMSH(enc);
				constructor.AddSegmentPID(enc.getPatient());
				constructor.AddSegmentPV1(enc);

				//Create a list of vitals concepts for the concept names defined in mapping xml
				Properties prop = Util.getProps(conceptDictionaryMapFile);

				//get list of concepts from map 
				concepts = getConceptListFromMap(prop, false);

				if (vitalsBatteryOnly){
					List<Concept> verifiedVitalsConcepts = getConceptsInSet(concepts, "PEDS CL DATA");
					if (verifiedVitalsConcepts != null){
						List<Obs> obsList = obsService.getObservations(null, queryEncounterList, 
								verifiedVitalsConcepts, null, null, null, null, null, null, null, null, false);
						if (obsList == null || obsList.size()== 0){
							export.setStatus(100);
							chicaService.saveChicaHL7Export(export);
							continue;
						}
						constructor.AddSegmentOBR(enc, vitalsBatteryId, vitalsBatteryName);
						addOBXBlock(obsList, constructor, prop);
					}
				} else {

					List<Concept> verifiedNonVitalsConcepts = getConceptsInSet(concepts, "MEDICAL RECORD FILE OBSERVATIONS");
					if (verifiedNonVitalsConcepts != null){
						constructor.AddSegmentOBR(enc, generalBatteryId, generalBatteryName);
						List<Obs> obsList = obsService.getObservations(null, queryEncounterList, 
								verifiedNonVitalsConcepts, null, null, null, null, null, null, null, null, false);
						addOBXBlock(obsList, constructor, prop);
					}
				}

				
				String message = constructor.getMessage();
				export.setStatus(2);
				export.setDateProcessed(new Date());
				chicaService.saveChicaHL7Export(export);
				
				if (message != null && !message.equals("")){
					Date ackDate = sendMessage(message, enc, socketHandler, socketReadTimeout);
					if (ackDate != null) { 
						export.setStatus(3);
						export.setAckDate(ackDate);
					}else {
						export.setStatus(4);
					}
					
					chicaService.saveChicaHL7Export(export);
					saveMessageFile(message,encId, ackDate);
				}
			}	
			
	    
		} catch (IOException e ){
			log.error("Error opening socket:" + e.getMessage());
			
		}catch (Exception e)
		
		{
			Integer sessionId = null;
			if (encid != null){

				Session session = atdService.getSessionByEncounter(encid);
				if (session != null){
					sessionId = session.getSessionId();
				}
				
			}
			String message = e.getMessage();
			ChicaError ce = new ChicaError("Error", "Hl7 Export", 
					message, "",
					 new Date(), sessionId);
			chicaService.saveError(ce);
			
		} finally
		{
			socketHandler.closeSocket();
			Context.closeSession();
		}

	}
	/**
	 * Saves message string to archive directory
	 * @param message
	 * @param encid
	 */
	public void saveMessageFile( String message, Integer encid, Date ackDate){
		AdministrationService adminService = Context.getAdministrationService();
		EncounterService es = Context.getService(EncounterService.class);

		
		org.openmrs.Encounter enc = es.getEncounter(encid);
		Patient patient = new Patient();
		patient = enc.getPatient();
		PatientIdentifier pi = patient.getPatientIdentifier();
		String mrn = "";
		String ack = "";
		if (ackDate != null){
			ack = "-ACK";
		}
		
		if (pi != null) mrn = pi.getIdentifier();
		String filename =  org.openmrs.module.dss.util.Util.archiveStamp() + "_"+ mrn + ack + ".hl7";
		
		String archiveDir = IOUtil.formatDirectoryName(adminService
				.getGlobalProperty("chica.outboundHl7ArchiveDirectory"));
		

		FileOutputStream archiveFile = null;
		try
		{
			archiveFile = new FileOutputStream(
					archiveDir + "/" + filename);
		} catch (FileNotFoundException e1)
		{
			log.error("Couldn't find file: "+archiveDir + "/" + filename);
		}
		if (archiveDir != null && archiveFile != null)
		{
			try
			{

				ByteArrayInputStream messageStream = new ByteArrayInputStream(
						message.getBytes());
				IOUtil.bufferedReadWrite(messageStream, archiveFile);
				archiveFile.flush();
				archiveFile.close();
			} catch (Exception e)
			{
				try
				{
					archiveFile.flush();
					archiveFile.close();
				} catch (Exception e1)
				{
				}
				log.error("There was an error writing the hl7 file");
				log.error(e.getMessage());
				log.error(org.openmrs.module.dss.util.Util.getStackTrace(e));
			}
		}
		return;
		
	}
	
	@Override
	public void shutdown()
	{
		super.shutdown();
		try
		{
			
			//this.server.stop();
		} catch (Exception e)
		{
			this.log.error(e.getMessage());
			this.log.error(org.openmrs.module.dss.util.Util.getStackTrace(e));
		}
	}
	
	
	private List<Concept> getConceptListFromMap(Properties prop, boolean vitals){
		ConceptService cs = Context.getConceptService();
		List<Concept> concepts = new ArrayList<Concept>();
		Enumeration <Object> names = prop.keys();
		
		while (names != null && names.hasMoreElements()){
			 String name = (String) names.nextElement();
			 //Get the concept for class CHICA only
			 List<Concept> conceptsWithSameName = cs.getConcepts(name, 
						Context.getLocale(), false, null, null);
			 for (Concept c : conceptsWithSameName){
				 concepts.add(c);
			 }
		}
		return concepts;
	}
	
	private Concept getRMRSConceptByName(String rmrsname){
		
		ConceptService cs = Context.getConceptService();
		Concept rmrsConcept = null;
		
		List<ConceptClass> classList = new ArrayList<ConceptClass>();
		ConceptClass rmrsClass = cs.getConceptClassByName("RMRS");
		classList.add(rmrsClass);
		
		List<Concept> conceptsWithSameName = cs.getConcepts(rmrsname, 
			Context.getLocale(), false, classList, null);
		
		if (conceptsWithSameName != null && conceptsWithSameName.size() >0){
			if (conceptsWithSameName.size()>1){
				log.error("More than one RMRS concept exist with exact name of " 
						+ rmrsname);
			}
			rmrsConcept = conceptsWithSameName.get(0);
		} else {
			log.error("No RMRS class concepts found with exact name of " + rmrsname);
		}
		
		return rmrsConcept;
	}
	
	private String getRMRSCodeFromConcept(Concept rmrsConcept){
		String rmrsCode = "";
		//get the mappings
		//get the source
		
		Iterator<ConceptMap> mapsIter = rmrsConcept.getConceptMappings().iterator(); 
		 if  (mapsIter.hasNext()){
			 rmrsCode = mapsIter.next().getSourceCode(); 
		 }
		 return rmrsCode;
	}
	
	private String getUnits(Concept concept){
		String units = "";
		ConceptService cs = Context.getConceptService();
		ConceptDatatype datatype = concept.getDatatype();
		Integer conceptId = concept.getConceptId();
		if (datatype != null && datatype.isNumeric()){
			ConceptNumeric rmrsNumericConcept =(ConceptNumeric) cs.getConcept(conceptId);
			units = rmrsNumericConcept.getUnits();
		}
		return units;
	}
	private boolean checkConceptSet(Concept conceptToTest, String batteryName){
		boolean match = false;
		ConceptService cs = Context.getConceptService();
		
		if (conceptToTest == null || batteryName == null || batteryName.equals("")){
			return false;
		}
		
		List<Concept> conceptsWithBatteryName = new ArrayList<Concept>();
		conceptsWithBatteryName = cs.getConcepts(batteryName, 
				 Context.getLocale(), false, null, null);
		
		Concept batteryConcept = new Concept();
		for (Concept concept : conceptsWithBatteryName){
			if (concept.isSet()){
				batteryConcept = concept;
				continue;
			}
		}
		Collection<ConceptSet> allConceptsInBattery = cs.getConceptSetsByConcept(batteryConcept);
		for (ConceptSet concept : allConceptsInBattery){
			
			if ( conceptToTest.getConceptId() == concept.getConcept().getConceptId()){
				match = true;
				continue;
			}
		
		}
			
		return match;
	
	}
	
	private List<Concept> getConceptsInSet(List<Concept> list, String setName){
		List<Concept> conceptsInSet = new ArrayList<Concept>();
		 for (Concept c : list){
			 if (checkConceptSet(c, setName)){
				 conceptsInSet.add(c);
			 }
		 }
		 return conceptsInSet;
	}
	
	private void addOBXBlock(List<Obs>obsList, HL7MessageConstructor constructor, Properties prop){
		//Get all obs for one encounter, where the concept is in the mapping properties xml
		//If an obs for that concept does not exist for an encounter, we do not create an OBX
			
		
		for (int rep = 0; rep < obsList.size(); rep++){
			
			Obs obs = obsList.get(rep);
			String chicaNameString = "";
			String rmrsName = "";
			String rmrsCode = "";
			String units = "";
			String hl7Abbreviation = "";
			ConceptDatatype conceptDatatype = null;

			String obsValue = obs.getValueAsString(null);
			Double obsValueNumeric = obs.getValueNumeric();
			if (obsValueNumeric != null  ){
				Double obsRounded =
					org.openmrs.module.dss.util.Util.round(Double.valueOf(obsValueNumeric), 1);
				if (obsRounded != null){
					obsValue = String.valueOf(obsRounded);
				}
			}
			
			Date datetime = obs.getObsDatetime();
			Concept chicaConcept = obs.getConcept();
			ConceptName chicaConceptName = chicaConcept.getName();

			if (chicaConceptName != null){	
				chicaNameString = chicaConceptName.getName();
				rmrsName = prop.getProperty(chicaNameString);
			}

			if (rmrsName != null){
				Concept rmrsConcept =  getRMRSConceptByName(rmrsName);
				if (rmrsConcept != null){
					conceptDatatype = rmrsConcept.getDatatype();
					hl7Abbreviation = conceptDatatype.getHl7Abbreviation();
					units = getUnits(rmrsConcept);
					rmrsCode = getRMRSCodeFromConcept(rmrsConcept);

				}
			}

			constructor.AddSegmentOBX(rmrsName, rmrsCode, null, obsValue, units, datetime, hl7Abbreviation,  rep + 1 );

		} 
	}
	private Date sendMessage(String message, Encounter enc, 
			HL7SocketHandler socketHandler, Integer socketReadTimeout ){
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
				
				if (hl7b != null && hl7b.getAckReceived() != null){
					ackDate = hl7b.getAckReceived();
					 log.error("Ack received host:" + host + "; port:" + port 
							 + "- first try. Encounter_id = " + enc.getEncounterId());
				}
					
			}
			
		} catch (Exception e){
			log.error("Error exporting message host:" + host + "; port:" + port 
					+ "- first try. Encounter_id = " + enc.getEncounterId());
			try {
				if (message != null) {
					
					hl7b = socketHandler.sendMessage(hl7b, socketReadTimeout);
					 if (hl7b != null && hl7b.getAckReceived() != null){
						 ackDate = hl7b.getAckReceived();
						 log.error("Ack received host:" + host + "; port:" + port 
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

		
	
}
