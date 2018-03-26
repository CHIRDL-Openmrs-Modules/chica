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
import org.openmrs.annotation.Authorized;
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
//		executeDataSet(TestUtil.DBUNIT_SETUP_FILE);
		// authenticate to the temp database
		authenticate();
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testEncounterService() throws Exception
	{
		executeDataSet(TestUtil.PATIENT_PROVIDER_FILE);
		executeDataSet(TestUtil.ENCOUNTERS_FILE);
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
}

