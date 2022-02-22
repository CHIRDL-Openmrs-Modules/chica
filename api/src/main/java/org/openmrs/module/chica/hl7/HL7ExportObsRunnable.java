package org.openmrs.module.chica.hl7;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptSource;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable;
import org.openmrs.module.sockethl7listener.util.Util;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v25.segment.OBX;

/**
 * CHICA-1070
 * Thread used to create HL7 ORU message and store in the sockethl7listener_hl7_out_queue table to be picked up by the HL7OutboundHandler task
 * Priority of ChirdlRunnable.PRIORITY_THREE in the ThreadManager
 */
public class HL7ExportObsRunnable implements ChirdlRunnable
{
	private static final Logger log = LoggerFactory.getLogger(HL7ExportObsRunnable.class);
	private Integer encounterId = null;
	private Integer patientId = null;
	private String conceptSourceString = null;
	private String host = null;
	private Integer port = null;
	
	/**
	 * Constructor
	 * @param patientId
	 * @param encounterId
	 * @param conceptSourceString - the name of the concept source used to determine which obs to create OBX segments for, such as "Outbound Obs"
	 * @param host - the host destination, the HL7OutboundHandler will query by destination and port
	 * @param port - port to send to, the HL7OutboundHandler will query by destination and port
	 */
	public HL7ExportObsRunnable(Integer patientId, Integer encounterId, String conceptSourceString, String host, Integer port)
	{
		this.patientId = patientId;
		this.encounterId = encounterId;
		this.conceptSourceString = conceptSourceString;
		this.host = host;
		this.port = port;
	}
	
	/**
	 * Create the message and store it in the sockethl7listener_hl7_out_queue table
	 */
	@Override
	public void run() 
	{
		try
		{
			String message = createHL7ORU();
			
			if(message != null)
			{
				org.openmrs.module.chica.util.Util.createHL7OutboundRecord(message, this.encounterId, this.host, this.port); // Store message in the sockethl7listner_hl7_out_queue to be processed by the scheduled task
			}
		}
		catch(Exception e)
		{
			this.log.error("Exception exporting obs for encounterId: " + this.encounterId, e);
		}
	}
	
	/**
	 * Creates an HL7 ORU_R01 message
	 * @return String representation of the ORU_R01 message
	 */
	private String createHL7ORU()
	{
		EncounterService encounterService = Context.getService(EncounterService.class);
		Encounter encounter = (Encounter) encounterService.getEncounter(this.encounterId);
		PatientService patientService = Context.getPatientService();
		Patient patient = patientService.getPatient(this.patientId);
		
		int orderRep = 0;
		HL7ORU hl7ORU = new HL7ORU(patient, encounter);
		hl7ORU.addSegmentMSH();
		hl7ORU.addSegmentPID();
		hl7ORU.addSegmentPV1();
		hl7ORU.addSegmentOBR(orderRep);
		
		// Query for obs for this encounter that are mapped to the source
		// and create OBX segments
		ObsService obsService = Context.getObsService();
		ConceptService conceptService = Context.getConceptService();
		ConceptSource conceptSource = conceptService.getConceptSourceByName(this.conceptSourceString);
		List<ConceptMap> mappedConcepts = conceptService.getConceptMappingsToSource(conceptSource);
		List<org.openmrs.Encounter> encounters = new ArrayList<>();
		encounters.add(encounter);
		List<Concept> concepts = new ArrayList<>();
		int numOBXs = 0;
		for(ConceptMap conceptMap : mappedConcepts)
		{
			concepts.clear();
			concepts.add(conceptMap.getConcept());
			
			// Get all obs for current concept, then create an OBX for each obs that is found
			List<Obs> obs = obsService.getObservations(null, encounters, concepts, null, null, null, null,
					null, null, null, null, false);
			
			// Add an OBX segments
			for(Obs observation : obs)
			{
				OBX obx = hl7ORU.addSegmentOBX(conceptMap, observation, orderRep, numOBXs);
				if(obx != null)
				{
					numOBXs++;
				}	
			}
		}
		
		if(hl7ORU.getORU().getPATIENT_RESULT().getORDER_OBSERVATION().getOBSERVATIONReps() == 0)
		{
			this.log.info("Error creating ORU message. No OBX segments were created for encounterId: " + this.encounterId + " conceptSource: " + this.conceptSourceString);
			return null; // We don't want to send a message that doesn't have at least 1 OBX
		}

		String message = null;
		try
		{
			message = Util.getMessage(hl7ORU.getORU());
		}
		catch(HL7Exception e)
		{
			this.log.error("Exception parsing HL7 message for encounter: " + this.encounterId + ".", e);
		}
		
		return message;
	}
	
	/**
	 * @see org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable#getPriority()
	 */
	@Override
	public int getPriority() 
	{
		 return ChirdlRunnable.PRIORITY_THREE;
	}

	/**
	 * @see org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable#getName()
	 */
	@Override
	public String getName() 
	{
		return this.getClass().getName() + " (Encounter: " + this.encounterId + ")";
	}

}
