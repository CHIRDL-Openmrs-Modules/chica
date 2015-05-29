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

import java.util.HashMap;
import java.util.Map;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.result.Result;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.BaseStateActionHandler;
import org.openmrs.module.chirdlutilbackports.StateManager;
import org.openmrs.module.chirdlutilbackports.action.ProcessStateAction;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.StateAction;
import org.openmrs.module.dss.hibernateBeans.Rule;
import org.openmrs.module.dss.service.DssService;


/**
 * Action class to initiate export of the physician note to the Cerner PowerNote.
 * 
 * @author Steve McKee
 */
public class ExportPowerNote implements ProcessStateAction {
	
	private static final String PHYSICIAN_NOTE = "PhysicianNote";
	private static final String PRODUCE = "PRODUCE";
	private static final String MODE = "mode";

	/**
	 * @see org.openmrs.module.chirdlutilbackports.action.ProcessStateAction#changeState(org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState, java.util.HashMap)
	 */
	public void changeState(PatientState patientState, HashMap<String, Object> parameters) {
		// Deliberately empty because processAction changes the state
	}
	
	/**
	 * @see org.openmrs.module.chirdlutilbackports.action.ProcessStateAction#processAction(org.openmrs.module.chirdlutilbackports.hibernateBeans.StateAction, org.openmrs.Patient, org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState, java.util.HashMap)
	 */
	public void processAction(StateAction stateAction, Patient patient, PatientState patientState, 
	                          HashMap<String, Object> parameters) {
		Integer sessionId = patientState.getSessionId();
		State currState = patientState.getState();
		Integer locationTagId = patientState.getLocationTagId();
		Integer locationId = patientState.getLocationId();
		
		// Get the note
		DssService dssService = Context.getService(DssService.class);
		
		Map<String,Object> ruleParams = new HashMap<String,Object>();
		ruleParams.put(MODE, PRODUCE);
		
		Rule rule = new Rule();
    	rule.setTokenName(PHYSICIAN_NOTE);
		rule.setParameters(ruleParams);
		
		Result result = dssService.runRule(patient, rule);
		String note = result.toString();
		
		//********************************
		
		
			// TODO: This section is currently unimplemented until the hooks are there to export the actual HL7 message.
		
		
		//********************************
		
		StateManager.endState(patientState);
		
		BaseStateActionHandler.changeState(patient, sessionId, currState, 
			stateAction, parameters, locationTagId, locationId); 
	}
	
}
