package org.openmrs.module.chica.test.service;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.Form;
import org.openmrs.FormField;
import org.openmrs.Patient;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.ConceptService;
import org.openmrs.api.FormService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.hibernateBeans.Study;
import org.openmrs.module.chica.hibernateBeans.StudyAttributeValue;
import org.openmrs.module.chica.hibernateBeans.StudySubject;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.test.TestUtil;
import org.openmrs.module.chirdlutil.util.DateUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * @author Tammy Dugan
 * 
 */
public class TestChicaService extends BaseModuleContextSensitiveTest
{

    //ChicaService chicaService = Context.getService(ChicaService.class);
    private static final String PROPERTY_KEY_CONCEPT = "concept";
    public static final String DBUNIT_SETUP_FILE = "dbunitFiles/chicaServiceTableSetup.xml";

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
		executeDataSet(TestUtil.PATIENT_FORMS_FILE);
		executeDataSet(DBUNIT_SETUP_FILE);
		
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
    public void test_getActiveStudies() {
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
        StudyAttributeValue studyAttributeValue = null;
        for (Study study : studies) {
            studyAttributeValue = chicaService.getStudyAttributeValue(study,"Custom Randomizer");
            Assert.assertNotNull("Active Studies not found.", studyAttributeValue);
            Assert.assertEquals("FamilyBasedStudyRandomizer", studyAttributeValue.getValue());
        }
    }
    
    /**
     * Tests retrieving EncountersForEnrolledPatients
     * @see org.openmrs.module.chica.service.ChicaService#getEncountersForEnrolledPatients(concept, startDateTime,  endDateTime)
     * @throws Exception
     */
    @Test
    public void should_getEncountersForEnrolledPatients() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE,  -4);
        Date startDateTime = DateUtil.getStartOfDay(c.getTime());
        Date endDateTime = DateUtil.getEndOfDay(c.getTime());
        ConceptService conceptService = Context.getConceptService();
        Concept concept = conceptService.getConceptByName("testConcept");
        ChicaService chicaService = Context.getService(ChicaService.class);
        List<Encounter> encounters = chicaService.getEncountersForEnrolledPatients(concept, startDateTime, endDateTime);
        Assert.assertNotNull("Encounters for Enrolled Patients not found.", encounters);
    }
    
    /**
     * Tests retrieving StudySubject
     * @see org.openmrs.module.chica.service.ChicaService#getStudySubject()
     * @throws Exception
     */
    @Test
    public void should_getStudySubject() {
        PatientService patientService = Context.getPatientService();
        int patientId = 2298;
        Patient patient = patientService.getPatient(patientId);
        ChicaService chicaService = Context.getService(ChicaService.class);
        Study study = chicaService.getStudyByTitle("Smoking Cessation Study");
        StudySubject studySubject = chicaService.getStudySubject(patient,study);
        Assert.assertNotNull("Study subject not found.", studySubject);
    }
    
    /**
     * Tests retrieving StudySubject by StudyTitle
     * @see org.openmrs.module.chica.service.ChicaService#getStudyByTitle()
     * @throws Exception
     */
    @Test
    public void should_getStudyByTitle(){
        ChicaService chicaService = Context.getService(ChicaService.class);
        Study study = chicaService.getStudyByTitle("Smoking Cessation Study");
        Assert.assertNotNull("Study Title not found.", study);
    }
    
    
    
}
