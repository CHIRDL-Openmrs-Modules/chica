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
package org.openmrs.module.chica.util;

import java.util.ArrayList;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

import  org.junit.jupiter.api.Assertions;


/**
 *
 */
public class TestUtil extends BaseModuleContextSensitiveTest {
	
	@Test
	public void testGetPatientsWithForms() throws Exception {
		executeDataSet(org.openmrs.module.chica.test.TestUtil.PATIENT_FORMS_FILE);
		Context.authenticate("user1", "testpassword");
		Context.getAdministrationService().setGlobalProperty("chirdlutil.serverConfigFile", "src/test/resources/ServerConfig.xml");
		Context.getAdministrationService().setGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_GREASEBOARD_CHECKOUT_STATE, ChirdlUtilConstants.STATE_FINISHED); // CHICA-1143 Set the global property for tests
		
		ArrayList<PatientRow> rows = new ArrayList<PatientRow>();
		String errorMessage = Util.getPatientSecondaryForms(rows, 23189);
		Assertions.assertNull(errorMessage);
		Assertions.assertEquals(0, rows.size(), "Number of patient rows did not match");
		
		Encounter encounter = Context.getEncounterService().getEncounter(23189);
		Assertions.assertNotNull(encounter);
		encounter.setEncounterDatetime(new Date());
		Context.getEncounterService().saveEncounter(encounter);
		
		//JIT_mobile_create
		Patient patient = Context.getPatientService().getPatient(2298);
		ChirdlUtilBackportsService backportsService = Context.getService(ChirdlUtilBackportsService.class);
		State createState = backportsService.getState(98791);
		FormInstance formInstance = new FormInstance(8992, 8971, 298237);
		createPatientState(formInstance, patient, 23189, createState);
		
		rows.clear();
		errorMessage = Util.getPatientSecondaryForms(rows, 23189);
		Assertions.assertNull(errorMessage);
		Assertions.assertEquals(1, rows.size(), "Number of patient rows did not match");
		Assertions.assertEquals(1, rows.get(0).getFormInstances().size(), "Number of form instances did not match");
		
		rows.clear();
		FormInstance formInstance2 = new FormInstance(8992, 8972, 298238);
		createPatientState(formInstance2, patient, 23189, createState);
		errorMessage = Util.getPatientSecondaryForms(rows, 23189);
		Assertions.assertNull(errorMessage);
		Assertions.assertEquals(1, rows.size(), "Number of patient rows did not match");
		Assertions.assertEquals(2, rows.get(0).getFormInstances().size(), "Number of form instances did not match");
		
		rows.clear();
		FormInstance formInstance3 = new FormInstance(8992, 8973, 298239);
		createPatientState(formInstance3, patient, 23189, createState);
		errorMessage = Util.getPatientSecondaryForms(rows, 23189);
		Assertions.assertNull( errorMessage);
		Assertions.assertEquals(1, rows.size(),"Number of patient rows did not match");
		Assertions.assertEquals(2, rows.get(0).getFormInstances().size(),"Number of form instances did not match");
		
		// CHICA-1143 Create the finished state and make sure the patient is no longer in the list of rows
		
		//FINISHED
		rows.clear();
		FormInstance formInstance4 = new FormInstance(8992, 8974, 298240);
		State finishedState = backportsService.getState(98793);
		createPatientState(formInstance4, patient, 23189, finishedState);
		errorMessage = Util.getPatientSecondaryForms(rows, 23189);
		Assertions.assertEquals(0, rows.size(), "Number of patient rows did not match");
		
	}
	
	private PatientState createPatientState(FormInstance formInstance, Patient patient, Integer sessionId, State state) {
		ChirdlUtilBackportsService chirdlUtilBackportsService = Context.getService(ChirdlUtilBackportsService.class);
		
		//Check if chirdlutilbackports service method recognized PatientState
		PatientState patientStateTest = chirdlUtilBackportsService.getPatientState(1);
		if (patientStateTest == null) {
			System.out.println("patientStateTest is null");
		} else {
			System.out.println("patientStateTest is " + patientStateTest.getPatientId());
		}
		
		PatientState createPatientState = new PatientState();
	//	createPatientState.setPatientStateId(2);
		createPatientState.setStartTime(new Date());
		createPatientState.setEndTime(new Date());
		createPatientState.setFormInstance(formInstance);
		createPatientState.setLocationTagId(2987);
		createPatientState.setPatientId(patient.getPatientId());
		createPatientState.setSessionId(sessionId);
		createPatientState.setState(state);
		createPatientState.setRetired(false);
		return chirdlUtilBackportsService.updatePatientState(createPatientState);
	}
}
