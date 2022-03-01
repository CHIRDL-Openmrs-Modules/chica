package org.openmrs.module.chica.nonInMemoryTests;

import java.io.OutputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openmrs.GlobalProperty;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.dss.DssManager;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

/**
 * @author Tammy Dugan
 * 
 */
@SkipBaseSetup
@Disabled
public class TestMergeXMLToTable extends BaseModuleContextSensitiveTest 
{

	/**
	 * Set up the database with the initial dataset before every test method in
	 * this class.
	 * 
	 * Require authorization before every test method in this class
	 * 
	 */
	@BeforeEach
	public void runBeforeEachTest() throws Exception
	{
//		// authenticate to the temp database
//		authenticate();
	}

	@Test
	public void testMergeXMLToTable() throws Exception
	{
		AdministrationService adminService = Context.getAdministrationService();
		ATDService atdService = Context.getService(ATDService.class);
		Integer encounterId = null;
		OutputStream output = System.out;

		Integer formInstanceId = 1;
		Integer locationId = 1;
		Integer formId = 16;
		int patientId = 30520;
		PatientService patientService = Context.getPatientService();
		Patient patient = patientService.getPatient(patientId);
		DssManager dssManager = new DssManager(patient);
		int maxDssElements = 20;
		dssManager.setMaxDssElementsByType("PSF", maxDssElements);
		GlobalProperty property = new GlobalProperty("atd.mergeToTable", "true");
		adminService.saveGlobalProperty(property);
		Integer locationTagId = 1;
		Integer sessionId = null;
		
		try
		{				
				FormInstance formInstance = new FormInstance();
				formInstance.setFormId(formId);
				formInstance.setFormInstanceId(formInstanceId);
				formInstance.setLocationId(locationId);
				atdService.produce(patient, formInstance, output,
						dssManager, encounterId, null,
						locationTagId, sessionId);
			
		} catch (Exception e)
		{
			System.out.println(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openmrs.test.BaseContextSensitiveTest#useInMemoryDatabase()
	 */
	@Override
	public Boolean useInMemoryDatabase()
	{
		return false;
	}

}
