package org.openmrs.module.chica.nonInMemoryTests;

import org.junit.Test;
import java.io.OutputStream;

import org.junit.Before;
import org.openmrs.GlobalProperty;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.dss.DssManager;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * @author Tammy Dugan
 * 
 */
@SkipBaseSetup
public class TestMergeXMLToTable extends BaseModuleContextSensitiveTest
{

	/**
	 * Set up the database with the initial dataset before every test method in
	 * this class.
	 * 
	 * Require authorization before every test method in this class
	 * 
	 */
	@Before
	public void runBeforeEachTest() throws Exception {
		// authenticate to the temp database
		authenticate();
	}
	@Test
	public void testMergeXMLToTable() throws Exception
	{
		AdministrationService adminService = Context.getAdministrationService();
		ATDService atdService = Context
				.getService(ATDService.class);
		Integer encounterId = null;
		OutputStream output = System.out;

		Integer formInstanceId = 1;
		Integer formId = 16;
		int patientId = 30520;
		PatientService patientService = Context.getPatientService();
		Patient patient = patientService.getPatient(patientId);
		DssManager dssManager = new DssManager(patient);
		int maxDssElements = 20;
		dssManager.setMaxDssElementsByType("PSF", maxDssElements);
		GlobalProperty property = new GlobalProperty("atd.mergeToTable", "true");
		adminService.saveGlobalProperty(property);

		atdService.produce(patient, formInstanceId, output, formId, dssManager,
				encounterId,null,null,true);
	}

	/* (non-Javadoc)
	 * @see org.openmrs.test.BaseContextSensitiveTest#useInMemoryDatabase()
	 */
	@Override
	public Boolean useInMemoryDatabase()
	{
		return false;
	}
	
}