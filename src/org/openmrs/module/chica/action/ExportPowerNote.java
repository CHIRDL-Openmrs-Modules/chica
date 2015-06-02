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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
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
import org.openmrs.module.sockethl7listener.HL7MessageConstructor;

import ca.uhn.hl7v2.model.v25.segment.OBX;

/**
 * Action class to initiate export of the physician note to the Cerner
 * PowerNote.
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
	public void changeState(PatientState patientState,
			HashMap<String, Object> parameters) {
		// Deliberately empty because processAction changes the state
	}

	/**
	 * @see org.openmrs.module.chirdlutilbackports.action.ProcessStateAction#processAction(org.openmrs.module.chirdlutilbackports.hibernateBeans.StateAction,
	 *      org.openmrs.Patient,
	 *      org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState,
	 *      java.util.HashMap)
	 */
	public void processAction(StateAction stateAction, Patient patient,
			PatientState patientState, HashMap<String, Object> parameters) {
		ChirdlUtilBackportsService chirdlutilbackportsService = Context
				.getService(ChirdlUtilBackportsService.class);
		Integer sessionId = patientState.getSessionId();
		Integer encounterId = chirdlutilbackportsService.getSession(sessionId)
				.getEncounterId();
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

		String message = createOutgoingHL7(encounterId, note,
				dataTypeAbbreviation, conceptName, resultStatusValue);

		System.out.println(message);
		// TODO save this message to a file and send with MIRTH

		StateManager.endState(patientState);

		BaseStateActionHandler.changeState(patient, sessionId, currState,
				stateAction, parameters, locationTagId, locationId);
	}

	private String createOutgoingHL7(Integer encounterId, String note,
			String hl7Abbreviation, String conceptName, String resultStatusValue) {

		EncounterService encounterService = Context
				.getService(EncounterService.class);

		try {

			Integer numberOfOBXSegments = 0;
			boolean sendHL7 = false;

			Encounter openmrsEncounter = (Encounter) encounterService
					.getEncounter(encounterId);

			HL7MessageConstructor constructor = new HL7MessageConstructor();

			constructor.AddSegmentMSH(openmrsEncounter);
			constructor.AddSegmentPID(openmrsEncounter.getPatient());

			// Create OBR and OBX segments for Vitals
			int orderRep = 0;
			Date dateTime = openmrsEncounter.getEncounterDatetime();

			BufferedReader reader = null;

			try {
				reader = new BufferedReader(new StringReader(note));
				String line = null;

				while ((line = reader.readLine()) != null) {
					OBX obx = constructor.AddSegmentOBX(conceptName,
							numberOfOBXSegments.toString(), null, null, line,
							null, dateTime, hl7Abbreviation, orderRep,
							numberOfOBXSegments);
					obx.getObservationResultStatus()
							.setValue(resultStatusValue);
					numberOfOBXSegments++;
				}

			} catch (Exception e) {

				e.printStackTrace();
			} finally {
				if (reader != null) {
					reader.close();
				}
			}

			if (numberOfOBXSegments > 0)
				sendHL7 = true;

			if (sendHL7) {
				return constructor.getMessage();
			}
		} catch (Exception e) {

			log.error("Error sending powerNote:", e);
		}
		return null;
	}

}
