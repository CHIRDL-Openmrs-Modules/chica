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
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.context.Context;
import org.openmrs.logic.result.Result;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chirdlutilbackports.BaseStateActionHandler;
import org.openmrs.module.chirdlutilbackports.StateManager;
import org.openmrs.module.chirdlutilbackports.action.ProcessStateAction;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.StateAction;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.module.dss.hibernateBeans.Rule;
import org.openmrs.module.dss.service.DssService;

import ca.uhn.hl7v2.model.v25.datatype.TX;
import ca.uhn.hl7v2.model.v25.message.MDM_T02;
import ca.uhn.hl7v2.model.v25.segment.MSH;
import ca.uhn.hl7v2.model.v25.segment.OBX;
import ca.uhn.hl7v2.model.v25.segment.PID;
import ca.uhn.hl7v2.model.v25.segment.EVN;
import ca.uhn.hl7v2.parser.PipeParser;

/**
 * Action class to initiate export of the physician note to the Cerner PowerNote.
 * 
 * @author Steve McKee
 */
public class ExportPowerNote implements ProcessStateAction {
	
	private static final String PHYSICIAN_NOTE = "PhysicianNote";
	
	private static final String PRODUCE = "PRODUCE";
	
	private static final String MODE = "mode";
	
	private Log log = LogFactory.getLog(this.getClass());
	
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
		String dataTypeAbbreviation = "TX";
		String conceptName = "Power Note-CHICA";
		String resultStatusValue = "F";
		
		String message = createOutgoingHL7(encounterId, note, dataTypeAbbreviation, conceptName, resultStatusValue);
		
		System.out.println(message);
		// TODO save this message to a file and send with MIRTH
		
		StateManager.endState(patientState);
		
		BaseStateActionHandler
		        .changeState(patient, sessionId, currState, stateAction, parameters, locationTagId, locationId);
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
			addSegmentEVN(openmrsEncounter, mdm);
			
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
		String ourApplication = "";
		String ourFacility = "";
		String messageType = "";
		String triggerEvent = "";
		String version = "";
		String receivingApp = "";
		String receivingFacility = "";
		String ackType = "";
		String app_acknowledgement_type = "";
		String processing_id = "";
		
		// Get current date
		String dateFormat = "yyyyMMddHHmmss";
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
		String formattedDate = formatter.format(new Date());
		
		try {
			msh.getFieldSeparator().setValue("|");
			msh.getEncodingCharacters().setValue("^~\\&");
			msh.getDateTimeOfMessage().getTime().setValue(formattedDate);
			
			msh.getSendingApplication().getNamespaceID().setValue(ourApplication);
			msh.getSendingFacility().getNamespaceID().setValue(ourFacility);
			msh.getMessageType().getMessageCode().setValue(messageType);
			msh.getMessageType().getTriggerEvent().setValue(triggerEvent);
			msh.getMessageControlID().setValue("");
			msh.getVersionID().getVersionID().setValue(version);
			msh.getReceivingApplication().getNamespaceID().setValue(receivingApp);
			msh.getReceivingFacility().getNamespaceID().setValue(receivingFacility);
			msh.getAcceptAcknowledgmentType().setValue(ackType);
			msh.getApplicationAcknowledgmentType().setValue(app_acknowledgement_type);
			msh.getProcessingID().getProcessingID().setValue(processing_id);
			msh.getMessageControlID().setValue(ourApplication + "-" + formattedDate);
		}
		catch (Exception e) {
			log.error("Exception contructing export message MSH segment. EncounterId: " + enc.getEncounterId(), e);
		}
		
		return msh;
	}
	
	public PID addSegmentPID(Patient pat, MDM_T02 mdm) {
		
		PID pid = mdm.getPID();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		Date dob = pat.getBirthdate();
		String dobStr = "";
		if (dob != null)
			dobStr = df.format(dob);
		
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
			pid.getDateTimeOfBirth().getTime().setValue(dobStr);
			pid.getSetIDPID().setValue("1");
			
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
			obx.getObservationIdentifier().getText().setValue(name);
			obx.getObservationResultStatus().setValue("F");
			
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
		String dateFormat = "yyyyMMddHHmm";
		try {
			evn = mdm.getEVN();
			evn.getEventTypeCode().setValue("T02");
			
			SimpleDateFormat df = new SimpleDateFormat(dateFormat);
			Date encounterDate = encounter.getEncounterDatetime();
			String dateString = "";
			if (encounterDate != null)
				dateString = df.format(encounterDate);
			evn.getRecordedDateTime().getTime().setValue(dateString);
			
		}
		catch (Exception e) {
			log.error("Exception constructing EVN segment for concept.", e);
		}
		return evn;
		
	}
}
