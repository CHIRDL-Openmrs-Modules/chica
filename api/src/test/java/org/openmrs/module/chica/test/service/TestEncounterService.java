package org.openmrs.module.chica.test.service;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chica.test.TestUtil;
import org.openmrs.parameter.EncounterSearchCriteria;
import org.openmrs.parameter.EncounterSearchCriteriaBuilder;
import org.openmrs.test.BaseModuleContextSensitiveTest;

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
		
		//Test new getEncounters() method versus the deprecated method
	
		 
		Patient p1 = new Patient();
		Patient p2 = new Patient();
		Patient p3 = new Patient();
		Patient p4 = new Patient();
		p1.setPatientId(1);
		p2.setPatientId(2);
		p3.setPatientId(3);
		p3.setPatientId(4);
		
		Encounter encounterLaterToday = new Encounter(4);
		Encounter encounterToday = new Encounter(3);
		Encounter encounterYesterday = new Encounter(2);
		Encounter encounterOlder = new Encounter(1);
		
		
		
		//12 am today
		Calendar cal1 = Calendar.getInstance();
		cal1.set(Calendar.HOUR_OF_DAY, 0);
		cal1.set(Calendar.MINUTE, 0);
		cal1.set(Calendar.SECOND, 0);
		Date dateStartToday = cal1.getTime();
		
		 //first patient today
		cal1.set(Calendar.HOUR_OF_DAY, 7);
		encounterToday.setPatient(p3);
		encounterToday.setEncounterDatetime(cal1.getTime());
		encounterToday.setVoided(false);
		
		//second patient today
		cal1.set(Calendar.HOUR_OF_DAY, 10);
		encounterLaterToday.setPatient(p4);
		encounterLaterToday.setEncounterDatetime(cal1.getTime());
		
		//end of today
		cal1.set(Calendar.HOUR_OF_DAY, 16);
		Date dateEndToday = cal1.getTime();
		
		//Yesterday
		cal1.add(Calendar.DAY_OF_YEAR, -1);
		cal1.set(Calendar.HOUR_OF_DAY, 0);
		Date dateStartYesterday = cal1.getTime();
		
		// patient yesterday
		cal1.set(Calendar.HOUR_OF_DAY, 7);
		encounterYesterday.setPatient(p2);
		encounterYesterday.setEncounterDatetime(cal1.getTime());
		
		//Previous
		cal1.add(Calendar.DAY_OF_YEAR, -5);
		cal1.set(Calendar.HOUR_OF_DAY, 0);
		Date dateStartPrevious = cal1.getTime();
		
		//patient previous
		cal1.set(Calendar.HOUR_OF_DAY, 8);
		encounterOlder.setPatient(p1);
		encounterOlder.setEncounterDatetime(cal1.getTime());
		
	    //Tests usage based on methods that use the deprecated method in chica and sockethl7listener
		verifyGetEncounters(null, dateStartPrevious, dateEndToday);
		verifyGetEncounters(null, dateStartToday, dateEndToday);
	    verifyGetEncounters(p4, dateStartToday, null);
	    verifyGetEncounters(p2, null, null);
	    verifyGetEncounters(p3, dateStartToday, dateEndToday);
	    verifyGetEncounters(p1, dateStartYesterday, dateEndToday);


	}
	
	private boolean areEqual(List<org.openmrs.Encounter> encounters1, List<org.openmrs.Encounter> encounters2){
		if (encounters1.size() != encounters2.size()){
			return false;
		}
		for (int i = 0; i< encounters1.size(); i++){
			if (encounters1.get(i).getEncounterId() != encounters2.get(i).getEncounterId() ){
				return false;
			}
		}
		return true;
	}
	
	private void verifyGetEncounters(Patient p, Date startDate, Date endDate){
		
		EncounterService encounterService = Context.getService(EncounterService.class);
		//get encounters that should have been submitted but have not been processed by the task (patient is null)
		List<org.openmrs.Encounter> encountersDeprecated = encounterService.getEncounters(p, null, startDate, endDate, null, null,
		    null, null, null, false); 
		
		//MES CHICA-1156 Replace deprecated getEncounters method by using new EncounterSearchCriteria class
		EncounterSearchCriteria encounterSearchCriteria = new EncounterSearchCriteriaBuilder().setPatient(p).setFromDate(startDate).setToDate(endDate)
						.setIncludeVoided(false).createEncounterSearchCriteria();
		List<org.openmrs.Encounter> encountersNewVersion = encounterService.getEncounters(encounterSearchCriteria);
		
		assertTrue(areEqual(encountersDeprecated,encountersNewVersion));
						
	}
}

