package org.openmrs.module.chica.test;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.result.Result;
import org.openmrs.module.dss.hibernateBeans.Rule;
import org.openmrs.module.dss.service.DssService;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * @author Tammy Dugan
 * 
 */
public class TestChicaObsDatasource extends BaseModuleContextSensitiveTest
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
		// create the basic user and give it full rights
		initializeInMemoryDatabase();
//		executeDataSet(TestUtil.DBUNIT_SETUP_FILE);
		// authenticate to the temp database
		authenticate();
	}

	@Test
	public void testChicaObsDatasource()throws Exception
	{
		executeDataSet(TestUtil.PATIENT_PROVIDER_FILE);
		executeDataSet(TestUtil.PATIENT_FORMS_FILE);
		executeDataSet(TestUtil.ENCOUNTERS_FILE);
		executeDataSet(TestUtil.CONCEPTS_FILE);
		executeDataSet(TestUtil.RULES_FILE);
		DssService dssService = Context
				.getService(DssService.class);
		Integer patientId = 30520;
		Patient patient = Context.getPatientService().getPatient(patientId);
		String mrn = "999995";
		ArrayList<Rule> ruleList = new ArrayList<Rule>();
		Rule rule = new Rule();
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("conceptName", "CALCIUM (SMA)");
		rule.setTokenName("testChicaObsDatasource");
		rule.setParameters(parameters);

		ruleList.add(rule);
		ArrayList<Result> results = dssService.runRules(patient, ruleList);

		for (Result result : results)
		{
			if (result != null)
			{
				for (int i = 0; i < result.size(); i++)
				{
					System.out.println(result.get(i).toNumber());
					System.out.println(result.get(i).getResultDate());
				}
			}
		}
	}
}
