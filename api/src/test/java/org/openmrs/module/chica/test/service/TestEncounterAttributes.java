package org.openmrs.module.chica.test.service;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Encounter;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.test.TestUtil;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.EncounterAttribute;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.EncounterAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

/**
 * @author Meena Sheley
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
		Encounter encounter = encounterService.getEncounter(encounterId);
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(2022, Calendar.FEBRUARY, 11,1,20,30);
		calendar.clear(Calendar.MILLISECOND);
		Date appointmentDate = calendar.getTime();
	
		//Verify id
		encounterId = encounter.getEncounterId();
		Assertions.assertNotNull(encounter, "Encounter not found for encounter id: " + encounterId );	
		
		//Get the attribute by name
		EncounterAttribute encounterAttribute = chirdlutilbackporsService.getEncounterAttributeByName(ChirdlUtilConstants.ENCOUNTER_ATTRIBUTE_APPOINTMENT_TIME);
		Assertions.assertNotNull(encounterAttribute, "EncounterAttribute not found for attribute value = " + ChirdlUtilConstants.ENCOUNTER_ATTRIBUTE_APPOINTMENT_TIME);
				
		//Create attribute value for Date 
		EncounterAttributeValue scheduledDateTimeAttributeValue = new EncounterAttributeValue(encounterAttribute,encounterId,appointmentDate);
		
		//Save encounter attribute value
		chirdlutilbackporsService.saveEncounterAttributeValue(scheduledDateTimeAttributeValue);
		
		//Get saved encounter attribute value
		EncounterAttributeValue fetchedAttributeValue = chirdlutilbackporsService.getEncounterAttributeValueByName(
				encounterId, ChirdlUtilConstants.ENCOUNTER_ATTRIBUTE_APPOINTMENT_TIME);
		Assertions.assertNotNull(fetchedAttributeValue, "Encounter attribute value not found for " + ChirdlUtilConstants.ENCOUNTER_ATTRIBUTE_APPOINTMENT_TIME);	
		
		Date fetchedAppointmentDate = fetchedAttributeValue.getValueDateTime();
		Assertions.assertEquals(0, appointmentDate.compareTo(fetchedAppointmentDate));	
		
		
		//Test saving text value 
		String printerLocation = "INTTEST";
		
		//Save encounter attribute value
		EncounterAttribute encounterAttributePrinterLocation = chirdlutilbackporsService.getEncounterAttributeByName(ChirdlUtilConstants.ENCOUNTER_ATTRIBUTE_PRINTER_LOCATION);
		chirdlutilbackporsService.saveEncounterAttributeValue( new EncounterAttributeValue(encounterAttributePrinterLocation,encounterId,printerLocation));
		
		EncounterAttributeValue fetchPrinterLocationAttributeValue = chirdlutilbackporsService.getEncounterAttributeValueByName(
				encounterId, ChirdlUtilConstants.ENCOUNTER_ATTRIBUTE_PRINTER_LOCATION);
		Assertions.assertNotNull(fetchPrinterLocationAttributeValue, "Encounter attribute value not found for " 
				+ ChirdlUtilConstants.ENCOUNTER_ATTRIBUTE_PRINTER_LOCATION);	
		Assertions.assertEquals(printerLocation, fetchPrinterLocationAttributeValue.getValueText());
	
	}
	
}

