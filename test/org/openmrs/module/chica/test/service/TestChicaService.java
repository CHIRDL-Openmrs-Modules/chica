package org.openmrs.module.chica.test.service;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Form;
import org.openmrs.FormField;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.test.TestUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * @author Tammy Dugan
 * 
 */
@SkipBaseSetup
public class TestChicaService extends BaseModuleContextSensitiveTest
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

	/**
	 * @throws Exception
	 */
	@Test
	public void testTeleformXMLToDatabaseForm() throws Exception
	{
		final int PWS_FORM_ID = 38;
		final int PSF_FORM_ID = 37;
		ATDService atdService = Context
				.getService(ATDService.class);

		String templateXMLFilename = "test/testFiles/DocWorkSheet.xml";
		Form pwsForm = atdService.teleformXMLToDatabaseForm("testForm",
				templateXMLFilename);
		compareFormFields(pwsForm, PWS_FORM_ID);
		
		templateXMLFilename = "test/testFiles/PreScreener.xml";
		Form psfForm = atdService.teleformXMLToDatabaseForm("testForm",
				templateXMLFilename);
		compareFormFields(psfForm, PSF_FORM_ID);	
	}

	private void compareFormFields(Form newForm, int databaseFormId)
	{
		FormService formService = Context.getFormService();
		Iterator<FormField> newFields = newForm.getFormFields().iterator();
		FormField currFormField = null;
		// store fields for new and old form in TreeMap
		TreeMap<Integer, String> newFormMap = new TreeMap<Integer, String>();
		TreeMap<Integer, String> oldFormMap = new TreeMap<Integer, String>();

		while (newFields.hasNext())
		{
			currFormField = newFields.next();
			newFormMap.put(currFormField.getFieldNumber(), currFormField
					.getField().getName());
		}

		Form oldPSFForm = formService.getForm(databaseFormId);
		Iterator<FormField> oldFields = oldPSFForm.getFormFields().iterator();
		
		while (oldFields.hasNext())
		{
			currFormField = oldFields.next();
			oldFormMap.put(currFormField.getFieldNumber(), currFormField
					.getField().getName());
		}
		
		Iterator<String> oldFormFields = oldFormMap.values().iterator();
		Iterator<String> newFormFields = newFormMap.values().iterator();
		
		while(oldFormFields.hasNext())
		{
			if(newFormFields.hasNext())
			{
				String oldFormFieldName = oldFormFields.next();
				String newFormFieldName = newFormFields.next();
				assertEquals(oldFormFieldName,newFormFieldName);
			}
		}
		
	}
}