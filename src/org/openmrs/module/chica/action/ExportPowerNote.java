/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.chica.action;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonAttribute;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.result.Result;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chica.util.Util;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutilbackports.BaseStateActionHandler;
import org.openmrs.module.chirdlutilbackports.StateManager;
import org.openmrs.module.chirdlutilbackports.action.ProcessStateAction;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.EncounterAttribute;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.EncounterAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.StateAction;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.module.dss.hibernateBeans.Rule;
import org.openmrs.module.dss.service.DssService;
import org.openmrs.module.sockethl7listener.hibernateBeans.HL7Outbound;
import org.openmrs.module.sockethl7listener.service.SocketHL7ListenerService;

import ca.uhn.hl7v2.model.v25.datatype.TX;
import ca.uhn.hl7v2.model.v25.message.MDM_T02;
import ca.uhn.hl7v2.model.v25.segment.MSH;
import ca.uhn.hl7v2.model.v25.segment.OBX;
import ca.uhn.hl7v2.model.v25.segment.PID;
import ca.uhn.hl7v2.model.v25.segment.PV1;
import ca.uhn.hl7v2.model.v25.segment.EVN;
import ca.uhn.hl7v2.model.v25.segment.TXA;
import ca.uhn.hl7v2.parser.PipeParser;

/**
 * Action class to initiate export of the physician note to the Cerner PowerNote.
 * 
 * @author Steve McKee
 */
public class ExportPowerNote implements ProcessStateAction {
	
	private static final String TXA_DOCUMENT_COMPLETION_STATUS = "A";
	private static final String TXA_DOCUMENT_CONFIDENTIALITY_STATUS = "U";
	private static final String TXA_DOCUMENT_STORAGE_STATUS = "AC";
	private static final String TXA_DOCUMENT_CONTENT_PRESENTATION = "FT";
	private static final String CHICA_POWER_NOTE_CONCEPT = "112358"; //Code for Power Note-CHICA

	private static final String PHYSICIAN_NOTE = "PhysicianNote";
	
	private static final String PRODUCE = "PRODUCE";
	
	private static final String MODE = "mode";
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private static final String DEFAULT_HOST = "localhost";
	private static final Integer DEFAULT_PORT = 0;
	private static final String TXA_ID = "1";
	private static final String PV1_PATIENT_CLASS = "Outpatient";
	private static final String DEFAULT_TXA_19 = "AV";
	private static final String PROPERTY_TXA_16_PROVIDER_ID = "PROVIDER_ID";
	private static final String MSH_PROCESSING_ID = "P"; // Production;
	
	/**
	 * @see org.openmrs.module.chirdlutilbackports.action.ProcessStateAction#changeState(org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState,
	 *      java.util.HashMap)
	 */
	public void changeState(PatientState patientState, HashMap<String, Object> parameters) {
		// Deliberately empty because processAction changes the state
	}
	
	/**
	 * @see org.openmrs.module.chirdlutilbackports.action.ProcessStateAction#processAction(org.openmrs.module.chirdlutilbackports.hibernateBeans.StateAction,
	 *      org.openmrs.Patient, org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState,
	 *      java.util.HashMap)
	 */
	public void processAction(StateAction stateAction, Patient patient, PatientState patientState,
	                          HashMap<String, Object> parameters) {
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		Integer sessionId = patientState.getSessionId();
		Integer encounterId = chirdlutilbackportsService.getSession(sessionId).getEncounterId();
		State currState = patientState.getState();
		Integer locationTagId = patientState.getLocationTagId();
		Integer locationId = patientState.getLocationId();
		
		// Get the note
		DssService dssService = Context.getService(DssService.class);
		
		Map<String, Object> ruleParams = new HashMap<String, Object>();
		ruleParams.put(MODE, PRODUCE);
		
		Rule rule = new Rule();
		rule.setTokenName(PHYSICIAN_NOTE);
		rule.setParameters(ruleParams);
		
		Result result = dssService.runRule(patient, rule);
		String note = result.toString();
		
		String message = createOutgoingHL7(encounterId, note, ChirdlUtilConstants.HL7_DATATYPE_TX, CHICA_POWER_NOTE_CONCEPT, ChirdlUtilConstants.HL7_RESULT_STATUS);
		
		createHL7OutboundRecord(message, encounterId); // DWE CHICA-636 Store message in the sockethl7listner_hl7_out_queue to be processed by the scheduled task
		
		StateManager.endState(patientState);
		
		BaseStateActionHandler
		        .changeState(patient, sessionId, currState, stateAction, parameters, locationTagId, locationId);
	}
	
	/**
	 * 
	 * The file will be picked up by MIRTH and sent
	 * 
	 * @param mrn
	 * @param outgoingMessage
	 */
	private void writeHL7File(String outgoingMessage) {
		
		AdministrationService adminService = Context.getAdministrationService();
		
		// save outgoingHL7 dump to a file
		String outgoingHL7Directory = IOUtil.formatDirectoryName(adminService.getGlobalProperty("chica.outboundHl7Directory"));
		if (outgoingHL7Directory != null&&outgoingMessage!=null&&outgoingMessage.length()>0) {
			String filename = "r" + org.openmrs.module.chirdlutil.util.Util.archiveStamp()+ ChirdlUtilConstants.FILE_EXTENSION_HL7;
			
			FileOutputStream outgoingHL7DumpFile = null;
			try {
				outgoingHL7DumpFile = new FileOutputStream(outgoingHL7Directory + "/" + filename);
			}
			catch (FileNotFoundException e1) {
				log.error("Couldn't find file: " + outgoingHL7Directory + "/" + filename);
			}
			if (outgoingHL7DumpFile != null) {
				try {
					
					ByteArrayInputStream outgoingHL7DumpInput = new ByteArrayInputStream(outgoingMessage.getBytes());
					IOUtil.bufferedReadWrite(outgoingHL7DumpInput, outgoingHL7DumpFile);
					outgoingHL7DumpFile.flush();
					outgoingHL7DumpFile.close();
				}
				catch (Exception e) {
					try {
						outgoingHL7DumpFile.flush();
						outgoingHL7DumpFile.close();
					}
					catch (Exception e1) {}
					log.error("There was an error writing the dump file");
					log.error(e.getMessage());
					log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
				}
			}
		}
	}
	
	private String createOutgoingHL7(Integer encounterId, String note, String hl7Abbreviation, String conceptName,
	                                 String resultStatusValue) {
		
		EncounterService encounterService = Context.getService(EncounterService.class);
		
		try {
			
			Integer numberOfOBXSegments = 0;
			boolean sendHL7 = false;
			
			Encounter openmrsEncounter = (Encounter) encounterService.getEncounter(encounterId);
			
			MDM_T02 mdm = new MDM_T02();
			
			addSegmentMSH(openmrsEncounter, mdm);
			addSegmentPID(openmrsEncounter.getPatient(), mdm);
			addSegmentPV1(openmrsEncounter, mdm); // DWE CHICA-636 Add PV1 segment
			addSegmentEVN(openmrsEncounter, mdm);
			addSegmentTXA(openmrsEncounter, conceptName, mdm);
			BufferedReader reader = null;
			
			try {
				reader = new BufferedReader(new StringReader(note));
				String line = null;
				
				while ((line = reader.readLine()) != null) {
					addSegmentOBX(conceptName, line, hl7Abbreviation, numberOfOBXSegments, mdm);
					
					numberOfOBXSegments++;
				}
				
			}
			catch (Exception e) {
				
				e.printStackTrace();
			}
			finally {
				if (reader != null) {
					reader.close();
				}
			}
			
			if (numberOfOBXSegments > 0)
				sendHL7 = true;
			
			if (sendHL7) {
				return getMessage(mdm);
			}
		}
		catch (Exception e) {
			
			log.error("Error sending powerNote:", e);
		}
		return null;
	}
	
	private MSH addSegmentMSH(Encounter enc, MDM_T02 mdm) {
		
		MSH msh = mdm.getMSH();
		
		// Get current date
		String formattedDate = DateUtil.formatDate(new Date(), ChirdlUtilConstants.DATE_FORMAT_yyyy_MM_dd_HH_mm_ss);
		
		try {
			msh.getFieldSeparator().setValue(ChirdlUtilConstants.HL7_FIELD_SEPARATOR);
			msh.getEncodingCharacters().setValue(ChirdlUtilConstants.HL7_ENCODING_CHARS);
			msh.getDateTimeOfMessage().getTime().setValue(formattedDate);
			
			msh.getSendingApplication().getNamespaceID().setValue(ChirdlUtilConstants.CONCEPT_CLASS_CHICA);
			msh.getSendingFacility().getNamespaceID().setValue(enc.getLocation().getName());
			msh.getMessageType().getMessageCode().setValue(ChirdlUtilConstants.HL7_MDM);
			msh.getMessageType().getTriggerEvent().setValue(ChirdlUtilConstants.HL7_EVENT_CODE_T02);
			msh.getMessageControlID().setValue("");
			msh.getVersionID().getVersionID().setValue(ChirdlUtilConstants.HL7_VERSION_2_2);
			
			msh.getProcessingID().getProcessingID().setValue(MSH_PROCESSING_ID);
			msh.getMessageControlID().setValue(ChirdlUtilConstants.CONCEPT_CLASS_CHICA + "-" + formattedDate);
			
		}
		catch (Exception e) {
			log.error("Exception constructing export message MSH segment. EncounterId: " + enc.getEncounterId(), e);
		}
		
		return msh;
	}
	
	public PID addSegmentPID(Patient pat, MDM_T02 mdm) {
		
		PID pid = mdm.getPID();
	
		try {
			// Name
			pid.getPatientName(0).getFamilyName().getSurname().setValue(pat.getFamilyName());
			pid.getPatientName(0).getGivenName().setValue(pat.getGivenName());
			
			// Identifiers
			PatientIdentifier pi = pat.getPatientIdentifier();
			
			// Identifier PID-3
			// MRN
			if (pi != null) {
				String identString = pi.getIdentifier();
				if (identString != null) {
					Integer dash = identString.indexOf("-");
					if (dash >= 0) {
						identString = identString.substring(0, dash) + identString.substring(dash + 1);
					}
				}
				pid.getPatientIdentifierList(0).getIDNumber().setValue(identString);
			}
			
			// gender
			pid.getAdministrativeSex().setValue(pat.getGender());
			
			// dob
			pid.getDateTimeOfBirth().getTime().setValue(DateUtil.formatDate(pat.getBirthdate(), ChirdlUtilConstants.DATE_FORMAT_yyyy_MM_dd));
			pid.getSetIDPID().setValue("1");
			
			
			// DWE CHICA-406
			// Patient Account Number PID-18
			PersonAttribute personAttribute = pat.getAttribute(ChirdlUtilConstants.PERSON_ATTRIBUTE_PATIENT_ACCOUNT_NUMBER);
			if(personAttribute != null && personAttribute.getValue() != null)
			{
				pid.getPatientAccountNumber().getIDNumber().setValue(personAttribute.getValue());
			}
			
			return pid;
			
		}
		catch (Exception e) {
			log.error("Exception adding PID segment to hl7.  PatientId: " + pat.getPatientId(), e);
			return null;
		}
	}
	
	public String getMessage(MDM_T02 mdm) {
		PipeParser pipeParser = new PipeParser();
		String msg = null;
		try {
			msg = pipeParser.encode(mdm);
		}
		catch (Exception e) {
			log.error("Exception parsing constructed message.", e);
		}
		return msg;
		
	}
	
	public OBX addSegmentOBX(String name, String value, String hl7Abbreviation, int obsRep, MDM_T02 mdm) {
		OBX obx = null;
		
		try {
			obx = mdm.getOBXNTE(obsRep).getOBX();
			obx.getSetIDOBX().setValue(String.valueOf(obsRep + 1));
			obx.getValueType().setValue(hl7Abbreviation);
			obx.getObservationIdentifier().getIdentifier().setValue(name);
			obx.getObservationResultStatus().setValue(ChirdlUtilConstants.HL7_RESULT_STATUS);
			
			TX tx = new TX(mdm);
			tx.setValue(value);
			obx.getObservationValue(0).setData(tx);
			
		}
		catch (Exception e) {
			log.error("Exception constructing OBX segment for concept ." + name, e);
		}
		return obx;
		
	}
	
	public EVN addSegmentEVN(Encounter encounter, MDM_T02 mdm) {
		EVN evn = null;
		try {
			evn = mdm.getEVN();
			evn.getEventTypeCode().setValue(ChirdlUtilConstants.HL7_EVENT_CODE_T02);
			evn.getRecordedDateTime().getTime().setValue(DateUtil.formatDate(encounter.getEncounterDatetime(), ChirdlUtilConstants.DATE_FORMAT_yyyy_MM_dd_HH_mm));	
		}
		catch (Exception e) {
			log.error("Exception constructing EVN segment for concept.", e);
		}
		return evn;
		
	}
	
	public TXA addSegmentTXA(Encounter encounter, String conceptName, MDM_T02 mdm) {
		
		// DWE CHICA-636 Added global property for TXA-19
		AdministrationService adminService = Context.getAdministrationService();
		String availabilityStatus = adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_OUTGOING_NOTE_TXA_DOC_AVAILABILITY_STATUS);
		if(availabilityStatus == null || availabilityStatus.isEmpty())
		{
			availabilityStatus = DEFAULT_TXA_19;
		}
		
		TXA txa = null;
		try {
			txa = mdm.getTXA();
			
			String dateString = DateUtil.formatDate(encounter.getEncounterDatetime(), ChirdlUtilConstants.DATE_FORMAT_yyyy_MM_dd_HH_mm);
			
			txa.getSetIDTXA().setValue(TXA_ID);
			txa.getDocumentType().setValue(conceptName);
			txa.getDocumentContentPresentation().setValue(TXA_DOCUMENT_CONTENT_PRESENTATION);
			txa.getActivityDateTime().getTime().setValue(dateString);
			txa.getOriginationDateTime().getTime().setValue(dateString);
			
			// DWE CHICA-636 Added global property for TXA-16
			// Property should be set to "PROVIDER_ID", which matches a concept in the DB
			// Will default to unique ID if no value is specified in the global properties
			// This should make it flexible enough to add other options for TXA-16 in the future
			String txa16Value = "";
			String propertProviderID = adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_OUTGOING_NOTE_TXA_UNIQUE_DOC_NUMBER);
			if(PROPERTY_TXA_16_PROVIDER_ID.equalsIgnoreCase(propertProviderID))
			{
				try
				{
					ObsService obsService = Context.getObsService();
					List<org.openmrs.Encounter> encounters = new ArrayList<org.openmrs.Encounter>();
					encounters.add(encounter);
					List<Concept> questions = new ArrayList<Concept>();
					
					ConceptService conceptService = Context.getConceptService();
					Concept concept = conceptService.getConcept(propertProviderID);
					questions.add(concept);
					List<Obs> obs = obsService.getObservations(null, encounters, questions, null, null, null, null,
							null, null, null, null, false);
					
					if(obs != null && obs.size() > 0)
					{
						txa16Value = obs.get(0).getValueText();
					}	
				}
				catch(Exception e)
				{
					log.error("Error occurred while adding " + PROPERTY_TXA_16_PROVIDER_ID + " to TXA segment.", e);
				}
			}
			else
			{
				Integer uniqueId = -1;
				while(uniqueId < 0){
					uniqueId = Util.GENERATOR.nextInt();
				}
				
				txa16Value = uniqueId.toString();
			}
			
			txa.getUniqueDocumentNumber().getEntityIdentifier().setValue(txa16Value);
			txa.getDocumentCompletionStatus().setValue(TXA_DOCUMENT_COMPLETION_STATUS);
			txa.getDocumentConfidentialityStatus().setValue(TXA_DOCUMENT_CONFIDENTIALITY_STATUS);
			txa.getDocumentAvailabilityStatus().setValue(availabilityStatus);
			txa.getDocumentStorageStatus().setValue(TXA_DOCUMENT_STORAGE_STATUS);
		}
		catch (Exception e) {
			log.error("Exception constructing TXA segment for concept.", e);
		}
		return txa;
		
	}
	
	/**
	 * DWE CHICA-636
	 * Create PV1 segment if the global property is set to true
	 * @param encounter
	 * @param mdm
	 * @return
	 */
	private PV1 addSegmentPV1(Encounter encounter, MDM_T02 mdm)
	{
		AdministrationService adminService = Context.getAdministrationService();
		String includePV1 = adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_OUTGOING_NOTE_INCLUDE_PV1);
		if(ChirdlUtilConstants.GENERAL_INFO_TRUE.equalsIgnoreCase(includePV1))
		{
			try
			{
				ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);			
				EncounterAttribute encounterAttribute = chirdlutilbackportsService.getEncounterAttributeByName(ChirdlUtilConstants.ENCOUNTER_ATTRIBUTE_VISIT_NUMBER);
				EncounterAttributeValue encounterAttributeValue = chirdlutilbackportsService.getEncounterAttributeValueByAttribute(encounter.getEncounterId(), encounterAttribute);

				if(encounterAttributeValue == null)
				{
					log.error("Error creating PV1 segment for outgoing note. Unable to locate " + ChirdlUtilConstants.ENCOUNTER_ATTRIBUTE_VISIT_NUMBER + " attribute for encounterId: " + encounter.getId());		
					return null;
				}

				PV1 pv1 = null;

				pv1 = mdm.getPV1();
				pv1.getSetIDPV1().setValue(TXA_ID);
				pv1.getPatientClass().setValue(PV1_PATIENT_CLASS);
				pv1.getVisitNumber().getIDNumber().setValue(encounterAttributeValue.getValueText());

				return pv1;

			}
			catch(Exception e)
			{
				log.error("Exception constructing PV1 segment for outgoing note.", e);
			}
		}

		return null;
	}
	
	/**
	 * DWE CHICA-636 Store the outbound note in the sockethl7listener_hl7_out_queue table
	 * so that it can be sent by the scheduled task
	 * 
	 * @param message
	 * @param encounterId
	 */
	private void createHL7OutboundRecord(String message, Integer encounterId)
	{
		AdministrationService adminService = Context.getAdministrationService();
		String host = adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_OUTGOING_NOTE_HOST);
		String portString = adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_OUTGOING_NOTE_PORT);
		Integer port;
		
		// If host and port are not set, allow the record to be created with localhost and port 0
		if (host == null || host.isEmpty())
		{
			log.error("Error creating HL7Outbound record. Host has not been set.");
			host = DEFAULT_HOST;
		}
		
		if(portString == null || portString.isEmpty())
		{
			log.error("Error creating HL7Outbound record. Port has not been set.");
			port = DEFAULT_PORT;
		}
		
		try
		{
			port = Integer.parseInt(portString);
		}
		catch(NumberFormatException e)
		{
			log.error("Error creating HL7Outbound record. Port is not in a valid numeric format.");
			return;
		}
		
		try
		{
			EncounterService encounterService = Context.getService(EncounterService.class);
			Encounter openmrsEncounter = (Encounter) encounterService.getEncounter(encounterId);
			SocketHL7ListenerService socketHL7ListenerService = Context.getService(SocketHL7ListenerService.class);
			
			if(openmrsEncounter == null)
			{
				log.error("Error creating HL7Outbound record. Unable to locate encounterId: " + encounterId);
				return;
			}
			
			HL7Outbound hl7Outbound = new HL7Outbound();
			hl7Outbound.setHl7Message(message);
			hl7Outbound.setEncounter(openmrsEncounter);
			hl7Outbound.setAckReceived(null);
			hl7Outbound.setPort(port);
			hl7Outbound.setHost(host); 
			
			socketHL7ListenerService.saveMessageToDatabase(hl7Outbound);
		}
		catch(Exception e)
		{
			log.error("Error creating HL7Outbound record.", e);
		}
	}
}
