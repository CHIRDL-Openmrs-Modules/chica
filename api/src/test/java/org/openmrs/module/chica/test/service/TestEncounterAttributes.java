package org.openmrs.module.chica.test.service;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Encounter;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.test.TestUtil;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.EncounterAttribute;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.EncounterAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.test.SkipBaseSetup;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

/**
 * @author Tammy Dugan
 * 
 */
public class TestEncounterAttributes extends BaseModuleContextSensitiveTest
{
	/**
	 * Set up the database with the initial dataset before every test method in
	 * this class.
	 * 
	 * Require authorization before every test method in this class
	 * 
	 */
	@BeforeEach
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
	public void testEncounterAttributes() throws Exception
	{
		//Test saving and getting encounter attributes that used to be in chica's extended encounter table/class
		executeDataSet(TestUtil.PATIENT_PROVIDER_FILE);
		executeDataSet(TestUtil.ENCOUNTERS_FILE);
		ChirdlUtilBackportsService chirdlutilbackporsService = Context.getService(ChirdlUtilBackportsService.class);
		EncounterService encounterService = Context.getEncounterService();

		int encounterId = 1;
		
		LocalDateTime scheduledDate = LocalDateTime.of(2022, Month.JANUARY, 1,9,15);
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(ChirdlUtilConstants.DATE_FORMAT_HYPHEN_yyyy_MM_dd_hh_mm_ss); 
		String formattedScheduledDateText = scheduledDate.format(dateFormatter);
		System.out.println("Scheduled appointment time : " + formattedScheduledDateText);
		
		Encounter encounter = encounterService.getEncounter(encounterId);
		//Verify id
		encounterId = encounter.getEncounterId();
		Assertions.assertNotNull(encounter, "Encounter not found for encounter id: " + encounterId );	
		
		//Get the attribute by name
		EncounterAttribute encounterAttribute = chirdlutilbackporsService.getEncounterAttributeByName(ChirdlUtilConstants.ENCOUNTER_ATTRIBUTE_APPOINTMENT_TIME);
		Assertions.assertNotNull(encounterAttribute, "EncounterAttribute not found for attribute value = " + ChirdlUtilConstants.ENCOUNTER_ATTRIBUTE_APPOINTMENT_TIME);
		
		//Save attribute with date as string value
		EncounterAttributeValue encounterAttributeValue = new EncounterAttributeValue(encounterAttribute, encounterId, formattedScheduledDateText);
		EncounterAttributeValue savedEncounterAttributeValue = chirdlutilbackporsService.saveEncounterAttributeValue(encounterAttributeValue);
		Assertions.assertNotNull(savedEncounterAttributeValue);	
		
		//Get the attribute value for date
		encounterAttributeValue = chirdlutilbackporsService.getEncounterAttributeValueByAttribute(encounterId, encounterAttribute, false);
		Assertions.assertNotNull(encounterAttributeValue);	
		
		//Verify before and after match
		String fetchedAttributeValueText = encounterAttributeValue.getValueText();
		Assertions.assertEquals(formattedScheduledDateText,fetchedAttributeValueText);	
		System.out.println("Scheduled appointment time from attribute: " + fetchedAttributeValueText);
		
		//Convert to date
	    Date date =new SimpleDateFormat(ChirdlUtilConstants.DATE_FORMAT_HYPHEN_yyyy_MM_dd_hh_mm_ss).parse(fetchedAttributeValueText);  

	}
	
}

