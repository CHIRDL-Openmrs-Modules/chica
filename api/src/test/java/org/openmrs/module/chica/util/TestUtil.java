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

import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import  org.junit.Assert;


/**
 *
 */
public class TestUtil extends BaseModuleContextSensitiveTest {
	
	@Test
	public void testGetPatientsWithForms() throws Exception {
		initializeInMemoryDatabase();
		executeDataSet(org.openmrs.module.chica.test.TestUtil.PATIENT_FORMS_FILE);
		Context.authenticate("user1", "testpassword");
		Context.getAdministrationService().setGlobalProperty("chirdlutil.serverConfigFile", "src/test/resources/ServerConfig.xml");
		
		ArrayList<PatientRow> rows = new ArrayList<PatientRow>();
		String errorMessage = Util.getPatientSecondaryForms(rows, 23189);
		Assert.assertNull(errorMessage);
		Assert.assertEquals("Number of patient rows did not match", 0, rows.size());
		
		Encounter encounter = Context.getEncounterService().getEncounter(23189);
		encounter.setEncounterDatetime(new Date());
		Context.getEncounterService().saveEncounter(encounter);
		
		Patient patient = Context.getPatientService().getPatient(2298);
		ChirdlUtilBackportsService backportsService = Context.getService(ChirdlUtilBackportsService.class);
		State createState = backportsService.getState(98791);
		FormInstance formInstance = new FormInstance(8992, 8971, 298237);
		createPatientState(formInstance, patient, 23189, createState);
		
		rows.clear();
		errorMessage = Util.getPatientSecondaryForms(rows, 23189);
		Assert.assertNull("The error message was not null", errorMessage);
		Assert.assertEquals("Number of patient rows did not match", 1, rows.size());
		Assert.assertEquals("Number of form instances did not match", 1, rows.get(0).getFormInstances().size());
		
		rows.clear();
		FormInstance formInstance2 = new FormInstance(8992, 8972, 298238);
		createPatientState(formInstance2, patient, 23189, createState);
		errorMessage = Util.getPatientSecondaryForms(rows, 23189);
		Assert.assertNull("The error message was not null", errorMessage);
		Assert.assertEquals("Number of pattient rows did not match", 1, rows.size());
		Assert.assertEquals("Number of form instances did not match", 2, rows.get(0).getFormInstances().size());
		
		rows.clear();
		FormInstance formInstance3 = new FormInstance(8992, 8973, 298239);
		createPatientState(formInstance3, patient, 23189, createState);
		errorMessage = Util.getPatientSecondaryForms(rows, 23189);
		Assert.assertNull("The error message was not null", errorMessage);
		Assert.assertEquals("Number of pattient rows did not match", 1, rows.size());
		Assert.assertEquals("Number of form instances did not match", 2, rows.get(0).getFormInstances().size());
	}
	
	private PatientState createPatientState(FormInstance formInstance, Patient patient, Integer sessionId, State state) {
		PatientState createPatientState = new PatientState();
		createPatientState.setStartTime(new Date());
		createPatientState.setEndTime(new Date());
		createPatientState.setFormInstance(formInstance);
		createPatientState.setLocationTagId(2987);
		createPatientState.setPatient(patient);
		createPatientState.setPatientId(patient.getPatientId());
		createPatientState.setSessionId(23189);
		createPatientState.setState(state);
		return Context.getService(ChirdlUtilBackportsService.class).updatePatientState(createPatientState);
	}
}
