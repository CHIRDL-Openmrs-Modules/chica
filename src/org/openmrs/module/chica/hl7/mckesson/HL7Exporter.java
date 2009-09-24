package org.openmrs.module.chica.hl7.mckesson;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
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
import org.openmrs.module.sockethl7listener.service.SocketHL7ListenerService;
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
		ATDService atdService = Context.getService(ATDService.class);
		AdministrationService adminService = Context.getAdministrationService();
		ObsService obsService = Context.getObsService();
		HL7SocketHandler  socketHandler = new HL7SocketHandler();
		Integer encid = null;
		List <Concept> concepts = null;
		String set = "";
		
		Context.openSession();
		
		try
		{
			
			if (Context.isAuthenticated() == false)
				authenticate();

			String conceptDictionaryMapFile = adminService
				.getGlobalProperty("chica.conceptDictionaryMapFile");
			
			HL7MessageConstructor constructor = new HL7MessageConstructor();
			
			//get the list of encounters
			List<Encounter> allPendingEncounterList = chicaService.getEncountersPendingHL7Export();
			Iterator<Encounter> it = allPendingEncounterList.iterator();
			while (it.hasNext()){
				Encounter enc = (Encounter) it.next();
				List<Encounter> queryEncounterList = new ArrayList<Encounter>();
				queryEncounterList.add(enc);
				
				//Create a list of vitals concepts for the concept names defined in mapping xml
				Properties prop = Util.getProps(conceptDictionaryMapFile);
				concepts =	  getConceptListFromMap(prop, true);
				 
				 //Get all obs for one encounter, where the concept is in the mapping properties xml
				 //If an obs for that concept does not exist for an encounter, we do not create an OBX
				 List<Obs> obsList = obsService.getObservations(null, queryEncounterList, concepts, null, null, null, null, null, null, null, null, false);
				 constructor.AddSegmentMSH(enc);
				 constructor.AddSegmentPID(enc.getPatient());
				 constructor.AddSegmentPV1(enc);
				 
				 //TODO: concept for set id will be determined. For now hard code to max concept_id  + 1.
				 set = "18296";
				 constructor.AddSegmentOBR(enc,set);

				 int rep = 1;
				 
				 for (Obs obs : obsList ){
					 
					 String chicaNameString = "";
					 String rmrsName = "";
					 String rmrsCode = "";
					 String units = "";
					 String hl7Abbreviation = "";
					 ConceptDatatype conceptDatatype = null;
					
					 String obsValue = obs.getValueAsString(null);
					 Date datetime = obs.getObsDatetime();
					 Concept chicaConcept = obs.getConcept();
					 ConceptName chicaConceptName = chicaConcept.getName();
					 
					 if (chicaConceptName != null){	
						chicaNameString = chicaConceptName.getName();
						rmrsName = prop.getProperty(chicaNameString);
					 }
					// if vitals
					if (rmrsName != null){
						Concept rmrsConcept =  getRMRSConceptByName(rmrsName);
						if (rmrsConcept != null){
							conceptDatatype = rmrsConcept.getDatatype();
							hl7Abbreviation = conceptDatatype.getHl7Abbreviation();
							units = getUnits(rmrsConcept);
							rmrsCode = getRMRSCodeFromConcept(rmrsConcept);
	
						}
					}
						
					constructor.AddSegmentOBX(rmrsName, rmrsCode, null, obsValue, units, datetime, hl7Abbreviation,  rep);
					rep++;
				 } 
				 
				   
				 String message = constructor.getMessage();
				 if (message != null) {
					 socketHandler.sendMessage(host,port, message);
				 }
				 List<ChicaHL7Export> exports = chicaService.getPendingHL7ExportsByEncounterId(enc.getId());
				 ChicaHL7Export lastExport = null;
				 
				 Iterator<ChicaHL7Export> exportIter = exports.iterator();
				 if (exportIter.hasNext()) {
					 lastExport = exportIter.next();
					 //lastExport = exports.get(0);
					 lastExport.setStatus(2);
					 lastExport.setDateProcessed(new Date());
					 lastExport.setVoided(false);
					 lastExport.setDateVoided(null);
					 chicaService.saveChicaHL7ExportQueue(lastExport);
				 }
				 while (exportIter.hasNext()){
					 ChicaHL7Export earlierExport = new ChicaHL7Export();
					 earlierExport = exportIter.next();
					 earlierExport.setStatus(3);
					 earlierExport.setDateProcessed(new Date());
					 earlierExport.setVoided(false);
					 earlierExport.setDateVoided(null);
					 chicaService.saveChicaHL7ExportQueue(earlierExport);
				 }
				 
				 SocketHL7ListenerService hl7ListService = Context.getService(SocketHL7ListenerService.class);
				 hl7ListService.saveMessageToDatabase(enc, message);
				 saveMessage(message,enc.getId());
				
			}	
			
	    
		} catch (Exception e)
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
			Context.closeSession();
		}

	}
	/**
	 * Saves message string to archive directory
	 * @param message
	 * @param encid
	 */
	public void saveMessage( String message, Integer encid){
		AdministrationService adminService = Context.getAdministrationService();
		EncounterService es = Context.getService(EncounterService.class);

		
		org.openmrs.Encounter enc = es.getEncounter(encid);
		Patient patient = new Patient();
		patient = enc.getPatient();
		PatientIdentifier pi = patient.getPatientIdentifier();
		String mrn = "";
		
		if (pi != null) mrn = pi.getIdentifier();
		String filename =  org.openmrs.module.dss.util.Util.archiveStamp() + "_"+ mrn + ".hl7";
		
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
		List<ConceptClass> classList = new ArrayList<ConceptClass>();
		ConceptClass chicaClass = cs.getConceptClassByName("CHICA");
		classList.add(chicaClass);
		Enumeration <Object> names = prop.keys();
		while (names.hasMoreElements()){
			 String name = (String) names.nextElement();
			 //Get the concept for class CHICA only
			 List<Concept> conceptsWithSameName = cs.getConcepts(name, 
						Context.getLocale(), false, classList, null);
			 if (conceptsWithSameName.size()>0){
				 concepts.add(conceptsWithSameName.get(0));
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
	
}
