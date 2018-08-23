package org.openmrs.module.chica.test.service;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Form;
import org.openmrs.FormField;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.hibernateBeans.Study;
import org.openmrs.module.chica.hibernateBeans.StudyAttributeValue;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.test.TestUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * @author Tammy Dugan
 * 
 */
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
//		executeDataSet(TestUtil.DBUNIT_SETUP_FILE);
		// authenticate to the temp database
		authenticate();
	}

	/**
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testTeleformXMLToDatabaseForm() throws Exception
	{
		final int PWS_FORM_ID = 38;
		final int PSF_FORM_ID = 37;
		ATDService atdService = Context
				.getService(ATDService.class);

		String templateXMLFilename = TestUtil.WORK_DOC_SHEET_FILE;
		Form pwsForm = atdService.teleformXMLToDatabaseForm("testForm",
				templateXMLFilename);
		compareFormFields(pwsForm, PWS_FORM_ID);
		
		templateXMLFilename = TestUtil.PRESCREENER_FILE;
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
	
	@Test
	@SkipBaseSetup
	public void checkAuthorizationAnnotations() throws Exception {
		Method[] allMethods = ChicaService.class.getDeclaredMethods();
		for (Method method : allMethods) {
		    if (Modifier.isPublic(method.getModifiers())) {
		        Authorized authorized = method.getAnnotation(Authorized.class);
		        Assert.assertNotNull("Authorized annotation not found on method " + method.getName(), authorized);
		    }
		}
	}
	
	/**
     * Tests retrieving list of ActiveStudies
     * @see org.openmrs.module.chica.service.ChicaService#getActiveStudies()
     * @throws Exception
     */
    @Test
    public void should_getActiveStudies() {
        ChicaService chicaService = Context.getService(ChicaService.class);
        List<Study> activeStudies = chicaService.getActiveStudies();
        Assert.assertNotNull("Active Studies not found.", activeStudies);
    }
    
    /**
     * Tests retrieving StudyAttributeValue
     * @see org.openmrs.module.chica.service.ChicaService#getStudyAttributeValue()
     * @throws Exception
     */
    @Test
    public void should_getStudyAttributeValue() {
        ChicaService chicaService = Context.getService(ChicaService.class);
        List<Study> studies = chicaService.getActiveStudies();
        String studyName = null;
        StudyAttributeValue studyAttributeValue = null;
        for (Study study : studies) {
            studyAttributeValue = chicaService.getStudyAttributeValue(study,"Test Study");
        }
        Assert.assertNotNull("Active Studies not found.", studyAttributeValue.);
    }
}
