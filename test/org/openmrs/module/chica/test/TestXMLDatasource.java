package org.openmrs.module.chica.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.module.atd.datasource.TeleformExportXMLDatasource;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.dss.hibernateBeans.Rule;
import org.openmrs.module.dss.service.DssService;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * @author Tammy Dugan
 * 
 */
@SkipBaseSetup
public class TestXMLDatasource extends BaseModuleContextSensitiveTest
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
		executeDataSet(TestUtil.DBUNIT_SETUP_FILE);
		// authenticate to the temp database
		authenticate();
	}

	@Test
	public void testXMLDatasource() throws FileNotFoundException, Exception
	{
		DssService dssService = Context.getService(DssService.class);
		LogicService logicService = Context.getLogicService();
		TeleformExportXMLDatasource datasource = null;
		String filename = "test\\testFiles\\export_PSF.xml";
		Patient patient = Context.getPatientService().getPatient(30520);
		FileInputStream input = new FileInputStream(filename);

		datasource = (TeleformExportXMLDatasource) logicService
				.getLogicDataSource("xml");

		FormInstance formInstance = datasource.parse(input,
				null,null);
		ArrayList<Rule> ruleList = new ArrayList<Rule>();
		Rule rule = new Rule();
		rule.setTokenName("testingXMLDatasource");
		Map<String, Object> parameters = new HashMap<String,Object>();

		parameters.put("formInstance", formInstance);
		parameters.put("fieldName", "HearR_Cho_4");
		rule.setParameters(parameters );
		ruleList.add(rule);
		String stringResult = dssService.runRulesAsString(patient,ruleList);
		System.out.println(stringResult);
	}
}