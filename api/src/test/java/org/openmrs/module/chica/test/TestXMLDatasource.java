package org.openmrs.module.chica.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.module.atd.datasource.FormDatasource;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.dss.hibernateBeans.Rule;
import org.openmrs.module.dss.service.DssService;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

/**
 * @author Tammy Dugan
 * 
 */
public class TestXMLDatasource extends BaseModuleContextSensitiveTest
{

	/**
	 * Set up the database with the initial dataset before every test method in
	 * this class.
	 * 
	 * Require authorization before every test method in this class
	 * 
	 */
	@BeforeEach
	public void runBeforeEachTest() throws Exception {
		// create the basic user and give it full rights
		initializeInMemoryDatabase();
//		executeDataSet(TestUtil.DBUNIT_SETUP_FILE);
		// authenticate to the temp database
		authenticate();
	}

	@Test
	@Disabled
	public void testXMLDatasource() throws FileNotFoundException, Exception
	{
		DssService dssService = Context.getService(DssService.class);
		LogicService logicService = Context.getLogicService();
		FormDatasource datasource = null;
		URL location = TestXMLDatasource.class.getProtectionDomain().getCodeSource().getLocation();
        System.out.println(location.getFile());

		String filename = TestUtil.EXPORT_PSF_FILE;
		File file = new File(filename);
		InputStream input = null;
		if (file.exists())
			input = new FileInputStream(filename);
		else {
			input = getClass().getClassLoader().getResourceAsStream(filename);
			if (input == null)
				throw new FileNotFoundException("Unable to find '" + filename + "' in the classpath");
		}
		
		Patient patient = Context.getPatientService().getPatient(30520);

		datasource = (FormDatasource) logicService
				.getLogicDataSource("xml");

		FormInstance formInstance = datasource.parseTeleformXmlFormat(input,
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
