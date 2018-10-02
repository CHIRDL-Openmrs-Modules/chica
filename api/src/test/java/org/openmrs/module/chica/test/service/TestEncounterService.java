package org.openmrs.module.chica.test.service;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chica.test.TestUtil;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.SkipBaseSetup;

/**
 * @author Tammy Dugan
 * 
 */
public class TestEncounterService extends BaseModuleContextSensitiveTest
{
	/**
	 * Set up the database with the initial dataset before every test method in
	 * this class.
	 * 
	 * Require authorization before every test method in this class
	 * 
	 */
	@Before
	public void runBeforeEachTest() throws Exception 
	{
		// create the basic user and give it full rights
		initializeInMemoryDatabase();
		executeDataSet(TestUtil.PATIENT_FORMS_FILE);
		executeDataSet(TestUtil.PATIENT_PROVIDER_FILE);
        executeDataSet(TestUtil.ENCOUNTERS_FILE);
		// authenticate to the temp database
		authenticate();
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testEncounterService() throws Exception
	{
		EncounterService encounterService = Context
				.getService(EncounterService.class);
		int patientId = 30520;
		Calendar calendar = Calendar.getInstance();
		calendar.set(1, Calendar.OCTOBER, 2007);
		Iterator<Encounter> iter = null;

		// test all methods that return encounters from chica Encounter Service

		Encounter encounter = encounterService.getEncounter(1);
		assertTrue(encounter instanceof org.openmrs.module.chica.hibernateBeans.Encounter);

		List<Encounter> encounterList = encounterService
				.getEncountersByPatientId(patientId);

		if (encounterList == null)
		{
			fail();
		} else
		{
			iter = encounterList.iterator();
			while (iter.hasNext())
			{
				assertTrue(iter.next() instanceof org.openmrs.module.chica.hibernateBeans.Encounter);
			}
		}

	}
	
	@Test
	@SkipBaseSetup
	public void checkAuthorizationAnnotations() throws Exception {
		Method[] allMethods = EncounterService.class.getDeclaredMethods();
		for (Method method : allMethods) {
		    if (Modifier.isPublic(method.getModifiers())) {
		        Authorized authorized = method.getAnnotation(Authorized.class);
		        Assert.assertNotNull("Authorized annotation not found on method " + method.getName(), authorized);
		    }
		}
	}
	
   @Test
    public void testSaveEncounter() {
       int patientId = 2298;
       PatientService patientService = Context.getPatientService();
       org.openmrs.module.chica.hibernateBeans.Encounter encounter = new org.openmrs.module.chica.hibernateBeans.Encounter();
       encounter.setEncounterType(new EncounterType(6));
       encounter.setEncounterDatetime(new java.util.Date());
       Patient patient = patientService.getPatient(patientId);
       LocationService locationService = Context.getLocationService();
       encounter.setLocation(locationService.getLocation("PCPS"));
       encounter.setPatient(patient);
       Calendar scheduledTime = Calendar.getInstance();
       scheduledTime.set(2018, Calendar.SEPTEMBER, 20, 8, 12);
       encounter.setScheduledTime(scheduledTime.getTime());
       Context.getEncounterService().saveEncounter(encounter);
       Integer encounterId = encounter.getEncounterId();
       Assert.assertNotNull("Encounter Id found", encounterId);
    }
    
    @Test
    public void testGetEncounter() {
        Encounter encounter = Context.getEncounterService().getEncounter(30);
        Assert.assertNotNull("Encounter found ", encounter);
    }
    
    @Test
    public void testGetEncountersByUuid() {
        String uuid = "5e76b7e2-1894-11e7-93ae-92361f002674";
        Encounter encounter = Context.getEncounterService().getEncounterByUuid(uuid);
        Assert.assertEquals(30, (int) encounter.getEncounterId());
    }
    
    @Test
    public void testGetEncountersByPatient() {
        List<Encounter> encounters = Context.getEncounterService().getEncountersByPatient(new Patient(2298));
        Assert.assertEquals(1, encounters.size());
    }
    
    @Test
    public void testGetEncountersByPatientId() {
        List<org.openmrs.Encounter> list = Context.getEncounterService().getEncountersByPatientId(2298);
        if (list != null && list.size() > 0) {
            for (Encounter  encounter : list) {
                Assert.assertNotNull("Encounter found ", encounter);
                Assert.assertEquals(30, (int) encounter.getEncounterId());
            }
        }
    }
     
    @Test
    public void testVoidEncounter() {
        Encounter encounter = Context.getEncounterService().getEncounter(23189);
       
        Encounter voidedEnc = Context.getEncounterService().voidEncounter(encounter, "new value");
        Assert.assertEquals(voidedEnc, encounter);

        Assert.assertNotNull(voidedEnc.getDateVoided());
        Assert.assertEquals(Context.getAuthenticatedUser(), voidedEnc.getVoidedBy());
        Assert.assertEquals("new value", voidedEnc.getVoidReason());
    }
    
    @Test
    public void testUnvoidEncounter() {
        Encounter encounter = Context.getEncounterService().getEncounter(1);
        Assert.assertNotNull(encounter.getVoidedBy());
        Assert.assertNotNull(encounter.getVoidReason());
        Assert.assertNotNull(encounter.getDateVoided());
        
        Encounter unvoidedEnc = Context.getEncounterService().unvoidEncounter(encounter);

        Assert.assertEquals(unvoidedEnc, encounter);
        Assert.assertNull(unvoidedEnc.getDateVoided());
        Assert.assertNull(unvoidedEnc.getVoidedBy());
        Assert.assertNull(unvoidedEnc.getVoidReason());
    }
    
    @Test
    public void testPurgeEncounterEncounter() {

        Encounter encounterToDelete = Context.getEncounterService().getEncounter(1);
        
        Context.getEncounterService().purgeEncounter(encounterToDelete);
        Encounter e = Context.getEncounterService().getEncounter(encounterToDelete.getEncounterId());
        Assert.assertNull("Encounter deleted ", e);
    }
     
    @Test
    public void testGetAllEncounterTypesBoolean() {
        List<EncounterType> types = Context.getEncounterService().getAllEncounterTypes(true);
        Assert.assertNotNull(types);
        Assert.assertFalse(types.isEmpty());
    }
    
    @Test
    public void testFindEncounterTypes() {
        List<EncounterType> types = Context.getEncounterService().findEncounterTypes("ADULTINITIAL");
        Assert.assertNotNull(types);
    }
    
    @Test
    public void testRetireEncounterType() {
        EncounterType type = Context.getEncounterService().getEncounterType(2);
        EncounterType retiredEncType = Context.getEncounterService().retireEncounterType(type, "Just Testing");

        Assert.assertEquals(retiredEncType, type);

        Assert.assertNotNull(retiredEncType.getDateRetired());
        Assert.assertEquals("Just Testing", retiredEncType.getRetireReason());
    }
    
    @Test
    public void testUnretireEncounterType() {
        EncounterType type = Context.getEncounterService().getEncounterType(3);
        Assert.assertNotNull(type.getRetiredBy());
        Assert.assertNotNull(type.getRetireReason());
        Assert.assertNotNull(type.getDateRetired());
        
        EncounterType unretiredEncType = Context.getEncounterService().unretireEncounterType(type);

        Assert.assertEquals(unretiredEncType, type);
        
        Assert.assertFalse(unretiredEncType.getRetired());
        Assert.assertNull(unretiredEncType.getDateRetired());
        Assert.assertNull(unretiredEncType.getRetiredBy());
        Assert.assertNull(unretiredEncType.getRetireReason());
    }
    

}

