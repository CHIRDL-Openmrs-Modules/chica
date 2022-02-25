package org.openmrs.module.chica.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.hibernateBeans.Study;
import org.openmrs.module.chica.hibernateBeans.StudyAttribute;
import org.openmrs.module.chica.hibernateBeans.StudyAttributeValue;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.test.TestUtil;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.XMLUtil;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.EncounterAttribute;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.EncounterAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.LocationTagAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

/**
 * This Class tests the ChicaServiceImpl class.
 * 
 * IMPORTANT: This test class needs more than eclipse's default amount of JVM
 * memory to run since it is compiling java files. Please increase you JVM
 * memory to 256 MB or higher.
 * 
 * @author tmdugan
 * 
 */
public class ChicaServiceImplTest extends BaseModuleContextSensitiveTest
{
	@Test
	public void getHighBP_shouldCheckHighBP() throws Exception
	{
		executeDataSet(TestUtil.BP_FILE);
		executeDataSet(TestUtil.LENGTH_AGE_FILE);
		Calendar onDate = Calendar.getInstance();
		onDate.set(2008, Calendar.DECEMBER, 22);
		ChicaService chicaService = Context.getService(ChicaService.class);
		Patient patient = new Patient();
		Calendar birthDate = null;
		Calendar baseBirthDate = Calendar.getInstance();
		baseBirthDate.set(2006, Calendar.DECEMBER, 22);
		patient.setBirthdate(baseBirthDate.getTime());
		patient.setGender("F"); // female
		Integer bpPercentile = 90; // high is 90th percentile
		String bpType = "systolic"; // systolic
		Double heightPercentile = 5.0;

		// test gender
		// test F
		patient.setGender("F");
		Double highBP = chicaService.getHighBP(patient, bpPercentile, bpType,
				heightPercentile, onDate.getTime());
		assertEquals(99, highBP, 0);

		// test M
		patient.setGender("M");
		highBP = chicaService.getHighBP(patient, bpPercentile, bpType,
				heightPercentile, onDate.getTime());
		assertEquals(98, highBP, 2);

		// test age
		// 0 years
		patient.setGender("F"); // female
		bpPercentile = 90; // high is 90th percentile
		bpType = "systolic"; // systolic
		heightPercentile = 5.0;

		birthDate = onDate;
		patient.setBirthdate(birthDate.getTime());
		highBP = chicaService.getHighBP(patient, bpPercentile, bpType,
				heightPercentile, onDate.getTime());
		assertEquals(97, highBP, 0);

		// 9 months
		birthDate = Calendar.getInstance();
		birthDate.set(2008, Calendar.MARCH, 22);
		patient.setBirthdate(birthDate.getTime());
		highBP = chicaService.getHighBP(patient, bpPercentile, bpType,
				heightPercentile, onDate.getTime());
		assertEquals(97, highBP, 0);

		// 1 year
		birthDate = Calendar.getInstance();
		birthDate.set(2007, Calendar.DECEMBER, 22);
		patient.setBirthdate(birthDate.getTime());
		highBP = chicaService.getHighBP(patient, bpPercentile, bpType,
				heightPercentile, onDate.getTime());
		assertEquals(97, highBP, 0);

		// 15 months
		birthDate = Calendar.getInstance();
		birthDate.set(2007, Calendar.SEPTEMBER, 22);
		patient.setBirthdate(birthDate.getTime());
		highBP = chicaService.getHighBP(patient, bpPercentile, bpType,
				heightPercentile, onDate.getTime());
		assertEquals(97.5, highBP, 0);

		// 16.5 years
		birthDate = Calendar.getInstance();
		birthDate.set(1992, Calendar.JUNE, 22);
		patient.setBirthdate(birthDate.getTime());
		highBP = chicaService.getHighBP(patient, bpPercentile, bpType,
				heightPercentile, onDate.getTime());
		assertEquals(122, highBP, 0);

		// 17 years
		birthDate = Calendar.getInstance();
		birthDate.set(1991, Calendar.DECEMBER, 22);
		patient.setBirthdate(birthDate.getTime());
		highBP = chicaService.getHighBP(patient, bpPercentile, bpType,
				heightPercentile, onDate.getTime());
		assertEquals(122, highBP, 0);

		// 18 years
		birthDate = Calendar.getInstance();
		birthDate.set(1990, Calendar.DECEMBER, 22);
		patient.setBirthdate(birthDate.getTime());
		highBP = chicaService.getHighBP(patient, bpPercentile, bpType,
				heightPercentile, onDate.getTime());
		assertEquals(null, highBP);

		// test systolic/diastolic
		// systolic
		patient.setBirthdate(baseBirthDate.getTime());
		patient.setGender("F"); // female
		bpPercentile = 90; // high is 90th percentile
		heightPercentile = 5.0;

		bpType = "systolic";
		highBP = chicaService.getHighBP(patient, bpPercentile, bpType,
				heightPercentile, onDate.getTime());
		assertEquals(99, highBP, 0);

		// diastolic
		bpType = "diastolic";
		highBP = chicaService.getHighBP(patient, bpPercentile, bpType,
				heightPercentile, onDate.getTime());
		assertEquals(57, highBP, 0);

		// test height percentile
		patient.setBirthdate(baseBirthDate.getTime());
		patient.setGender("F"); // female
		bpPercentile = 90; // high is 90th percentile
		bpType = "systolic"; // systolic

		// 0 height percentile
		heightPercentile = 0.0;
		highBP = chicaService.getHighBP(patient, bpPercentile, bpType,
				heightPercentile, onDate.getTime());
		assertEquals(99, highBP, 0);

		// 3 height percentile
		heightPercentile = 3.0;
		highBP = chicaService.getHighBP(patient, bpPercentile, bpType,
				heightPercentile, onDate.getTime());
		assertEquals(99, highBP, 0);

		// 5 height percentile
		heightPercentile = 5.0;
		highBP = chicaService.getHighBP(patient, bpPercentile, bpType,
				heightPercentile, onDate.getTime());
		assertEquals(99, highBP, 0);

		// 7 height percentile
		heightPercentile = 7.0;
		highBP = chicaService.getHighBP(patient, bpPercentile, bpType,
				heightPercentile, onDate.getTime());
		assertEquals(99, highBP, 0);

		// 10 height percentile
		heightPercentile = 10.0;
		highBP = chicaService.getHighBP(patient, bpPercentile, bpType,
				heightPercentile, onDate.getTime());
		assertEquals(99, highBP, 0);

		// 95 height percentile
		heightPercentile = 95.0;
		highBP = chicaService.getHighBP(patient, bpPercentile, bpType,
				heightPercentile, onDate.getTime());
		assertEquals(105, highBP, 0);

		// 97 height percentile
		heightPercentile = 97.0;
		highBP = chicaService.getHighBP(patient, bpPercentile, bpType,
				heightPercentile, onDate.getTime());
		assertEquals(105, highBP, 0);

		// test BP percentile
		// 90
		patient.setBirthdate(baseBirthDate.getTime());
		patient.setGender("F"); // female
		bpType = "systolic"; // systolic
		heightPercentile = 5.0;

		bpPercentile = 90;
		highBP = chicaService.getHighBP(patient, bpPercentile, bpType,
				heightPercentile, onDate.getTime());
		assertEquals(99, highBP, 0);

		// 95
		bpPercentile = 95;
		highBP = chicaService.getHighBP(patient, bpPercentile, bpType,
				heightPercentile, onDate.getTime());
		assertEquals(102, highBP, 0);
	}

	@BeforeEach
	public void runBeforeEachTest() throws Exception
	{
		// create the basic user and give it full rights
		initializeInMemoryDatabase();
//		executeDataSet(TestUtil.DBUNIT_SETUP_FILE);
		// authenticate to the temp database
		authenticate();
	}

	@Test
	@Disabled
	public void produce_shouldTestPSFProduce() throws Exception
	{
		LocationService locationService = Context.getLocationService();
		File dir1 = new File("ruleLibrary");

		AdministrationService adminService = Context.getAdministrationService();
	    adminService.setGlobalProperty("dss.javaRuleDirectory", dir1.getCanonicalPath());
	    adminService.setGlobalProperty("dss.classRuleDirectory", dir1.getCanonicalPath());
	    adminService.setGlobalProperty("dss.mlmRuleDirectory", dir1.getCanonicalPath());

		int patientId = 30520;
		Integer formInstanceId = 1;
		Integer sessionId = 1;

		EncounterService encounterService = Context.getService(EncounterService.class);
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		PatientService patientService = Context.getPatientService();

		Encounter encounter = new Encounter();
		encounter.setEncounterType(new EncounterType(3));
		encounter.setEncounterDatetime(new java.util.Date());
		Patient patient = patientService.getPatient(patientId);

		encounter.setLocation(locationService.getLocation("Unknown Location"));
		encounter.setPatient(patient);
		Encounter savedEncounter = encounterService.saveEncounter(encounter);
		Assertions.assertNotNull(savedEncounter);	
		
		Calendar scheduledTime = Calendar.getInstance();
		scheduledTime.set(2007, Calendar.NOVEMBER, 20, 8, 12);
		
		EncounterAttribute encounterAttributeScheduledTime = chirdlutilbackportsService.getEncounterAttributeByName(ChirdlUtilConstants.ENCOUNTER_ATTRIBUTE_APPOINTMENT_TIME);
		Assertions.assertNotNull(encounterAttributeScheduledTime);	
		EncounterAttributeValue encounterAttributeValue = new EncounterAttributeValue(encounterAttributeScheduledTime, savedEncounter.getEncounterId(), scheduledTime.getTime().toString());
		chirdlutilbackportsService.saveEncounterAttributeValue(encounterAttributeValue);
		encounterAttributeValue = chirdlutilbackportsService.getEncounterAttributeValueByName(savedEncounter.getEncounterId(),ChirdlUtilConstants.ENCOUNTER_ATTRIBUTE_APPOINTMENT_TIME);
		Assertions.assertNotNull(encounterAttributeValue);	

		Integer encounterId = encounter.getEncounterId();
		String generatedOutput = null;
		String booleanString = adminService
				.getGlobalProperty("atd.mergeTestCaseXML");
		boolean merge = Boolean.parseBoolean(booleanString);
	

		String PSFMergeDirectory = null;
		FormService formService = Context.getFormService();

		Integer psfFormId = formService.getForm("PSF").getFormId();
		Integer locationTagId = 1;
		Integer locationId = 1;
		
		try
		{
				FormAttributeValue formAttributeValue = chirdlutilbackportsService
						.getFormAttributeValue(psfFormId,
								"defaultMergeDirectory", locationTagId,locationId);

				if (formAttributeValue != null)
				{
					PSFMergeDirectory = formAttributeValue.getValue();
				}

				String PSFFilename = "test/testFiles/PSF.xml";
				String removeCurrentTimeXSLT = "test/testFiles/removeCurrentTime.xslt";

				ATDService atdService = Context.getService(ATDService.class);
				PatientState patientState = new PatientState();
				patientState.setPatient(patient);

				// test create PSF merge file
				String state = "PSF_create";
				LocationTagAttributeValue locTagAttrValue = 
					chirdlutilbackportsService.getLocationTagAttributeValue(locationTagId, chirdlutilbackportsService.getStateByName(state).getFormName(), locationId);
				
				Integer formId = null;
				
				if(locTagAttrValue != null){
					String value = locTagAttrValue.getValue();
					if(value != null){
						try
						{
							formId = Integer.parseInt(value);
						} catch (Exception e)
						{
						}
					}
				}
				FormInstance formInstance = new FormInstance();
				formInstance.setFormInstanceId(formInstanceId);

				formInstance.setFormId(formId);
				formInstance.setLocationId(locationId);
				patientState.setSessionId(sessionId);
				OutputStream generatedXML = new ByteArrayOutputStream();
				formAttributeValue = chirdlutilbackportsService.getFormAttributeValue(
						psfFormId, "numPrompts", locationTagId,locationId);
				int maxDssElements = 0;

				if (formAttributeValue != null)
				{
					maxDssElements = Integer.parseInt(formAttributeValue.getValue());
				}

				atdService.produce(generatedXML, patientState, patient,
						encounterId, "PSF", maxDssElements,sessionId);
				OutputStream targetXML = new ByteArrayOutputStream();
				IOUtil.bufferedReadWrite(new FileInputStream(PSFFilename),
						targetXML);
				generatedOutput = generatedXML.toString();
				if (merge && PSFMergeDirectory != null)
				{
					FileWriter writer = new FileWriter(PSFMergeDirectory
							+ "file1.xml");
					writer.write(generatedOutput);
					writer.close();
				}
				generatedXML = new ByteArrayOutputStream();
				XMLUtil.transformXML(new ByteArrayInputStream(generatedOutput
						.getBytes()), generatedXML, new FileInputStream(
						removeCurrentTimeXSLT), null);
				assertEquals(targetXML.toString(), generatedXML.toString());

				// test forms with younger child
				Calendar calendar = Calendar.getInstance();
				calendar.set(2007, Calendar.JANUARY, 1);
				patient.setBirthdate(calendar.getTime());
				PSFFilename = "test/testFiles/PSF_younger.xml";

				// test create PSF merge file
				state = "PSF_create";
				locTagAttrValue = 
					chirdlutilbackportsService.getLocationTagAttributeValue(locationTagId, chirdlutilbackportsService.getStateByName(state).getFormName(), locationId);
				
				if(locTagAttrValue != null){
					String value = locTagAttrValue.getValue();
					if(value != null){
						try
						{
							formId = Integer.parseInt(value);
						} catch (Exception e)
						{
						}
					}
				}
				formInstance = new FormInstance();
				formInstance.setFormInstanceId(formInstanceId);

				formInstance.setFormId(formId);
				formInstance.setLocationId(locationId);
				generatedXML = new ByteArrayOutputStream();
				atdService.produce(generatedXML, patientState, patient,
						encounterId, "PSF", maxDssElements,sessionId);
				targetXML = new ByteArrayOutputStream();
				IOUtil.bufferedReadWrite(new FileInputStream(PSFFilename),
						targetXML);
				generatedOutput = generatedXML.toString();
				if (merge && PSFMergeDirectory != null)
				{
					FileWriter writer = new FileWriter(PSFMergeDirectory
							+ "file2.xml");
					writer.write(generatedOutput);
			writer.flush();
					writer.close();
				}
				generatedXML = new ByteArrayOutputStream();
				XMLUtil.transformXML(new ByteArrayInputStream(generatedOutput
						.getBytes()), generatedXML, new FileInputStream(
						removeCurrentTimeXSLT), null);
				assertEquals(targetXML.toString(), generatedXML.toString());
			
		} catch (Exception e)
		{

		}
	}

	@Test
	@Disabled
	public void produce_shouldTestPWSProduce() throws Exception
	{
		LocationService locationService = Context.getLocationService();
		int patientId = 30520;
		Integer formInstanceId = 1;

		EncounterService encounterService = Context
				.getService(EncounterService.class);
		PatientService patientService = Context.getPatientService();
		ATDService atdService = Context.getService(ATDService.class);
		Encounter encounter = new Encounter();
		encounter.setEncounterType(new EncounterType(3));
		encounter.setEncounterDatetime(new java.util.Date());
		Patient patient = patientService.getPatient(patientId);

		encounter.setLocation(locationService.getLocation("Unknown Location"));
		encounter.setPatient(patient);
		Calendar scheduledTime = Calendar.getInstance();
		scheduledTime.set(2007, Calendar.NOVEMBER, 20, 8, 12);
		//encounter.setScheduledTime(scheduledTime.getTime());
		encounterService.saveEncounter(encounter);
		Integer encounterId = encounter.getEncounterId();
		String generatedOutput = null;
		AdministrationService adminService = Context.getAdministrationService();
		String booleanString = adminService
				.getGlobalProperty("atd.mergeTestCaseXML");
		boolean merge = Boolean.parseBoolean(booleanString);
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);

		String PWSMergeDirectory = null;
		FormService formService = Context.getFormService();

		Integer pwsFormId = formService.getForm("PWS").getFormId();
		Integer locationTagId = 1;
		Integer locationId = 1;
		
		try
		{
				FormAttributeValue formAttributeValue = chirdlutilbackportsService
						.getFormAttributeValue(pwsFormId,
								"defaultMergeDirectory", locationTagId,locationId);

				if (formAttributeValue != null)
				{
					PWSMergeDirectory = formAttributeValue.getValue();
				}

				String PWSFilename = "test/testFiles/PWS.xml";
				String removeCurrentTimeXSLT = "test/testFiles/removeCurrentTime.xslt";

				PatientState patientState = new PatientState();
				patientState.setPatient(patient);

				// test create PWS merge file
				String state = "PWS_create";
				LocationTagAttributeValue locTagAttrValue = 
					chirdlutilbackportsService.getLocationTagAttributeValue(locationTagId, chirdlutilbackportsService.getStateByName(state).getFormName(), locationId);
				Integer formId = null;
				if(locTagAttrValue != null){
					String value = locTagAttrValue.getValue();
					if(value != null){
						try
						{
							formId = Integer.parseInt(value);
						} catch (Exception e)
						{
						}
					}
				}
				FormInstance formInstance = new FormInstance();
				formInstance.setFormId(formId);
				formInstance.setFormInstanceId(formInstanceId);
				formInstance.setLocationId(locationId);
				patientState.setFormInstance(formInstance);

				OutputStream generatedXML = new ByteArrayOutputStream();
				formAttributeValue = chirdlutilbackportsService.getFormAttributeValue(
						pwsFormId, "numPrompts", locationTagId,locationId);
				int maxDssElements = 0;
				int sessionId = 1;

				if (formAttributeValue != null)
				{
					maxDssElements = Integer.parseInt(formAttributeValue
							.getValue());
				}

				atdService.produce(generatedXML, patientState, patient,
						encounterId, "PWS", maxDssElements,sessionId);
				OutputStream targetXML = new ByteArrayOutputStream();
				IOUtil.bufferedReadWrite(new FileInputStream(PWSFilename),
						targetXML);
				generatedOutput = generatedXML.toString();
				if (merge && PWSMergeDirectory != null)
				{
					FileWriter writer = new FileWriter(PWSMergeDirectory
							+ "file1.xml");
					writer.write(generatedOutput);
			writer.flush();
					writer.close();
				}
				generatedXML = new ByteArrayOutputStream();
				XMLUtil.transformXML(new ByteArrayInputStream(generatedOutput
						.getBytes()), generatedXML, new FileInputStream(
						removeCurrentTimeXSLT), null);
				assertEquals(targetXML.toString(), generatedXML.toString());

				// test forms with younger child
				Calendar calendar = Calendar.getInstance();
				calendar.set(2007, Calendar.JANUARY, 1);
				patient.setBirthdate(calendar.getTime());
				PWSFilename = "test/testFiles/PWS_younger.xml";

				// test create PWS merge file
				state = "PWS_create";
				locTagAttrValue = 
					chirdlutilbackportsService.getLocationTagAttributeValue(locationTagId, chirdlutilbackportsService.getStateByName(state).getFormName(), locationId);
				
				if(locTagAttrValue != null){
					String value = locTagAttrValue.getValue();
					if(value != null){
						try
						{
							formId = Integer.parseInt(value);
						} catch (Exception e)
						{
						}
					}
				}
				formInstance = new FormInstance();
				formInstance.setFormInstanceId(formInstanceId);

				formInstance.setFormId(formId);
				formInstance.setLocationId(locationId);
				generatedXML = new ByteArrayOutputStream();
				atdService.produce(generatedXML, patientState, patient,
						encounterId, "PWS", maxDssElements,sessionId);
				targetXML = new ByteArrayOutputStream();
				IOUtil.bufferedReadWrite(new FileInputStream(PWSFilename),
						targetXML);
				generatedOutput = generatedXML.toString();
				if (merge && PWSMergeDirectory != null)
				{
					FileWriter writer = new FileWriter(PWSMergeDirectory
							+ "file2.xml");
					writer.write(generatedOutput);
					writer.close();
				}
				generatedXML = new ByteArrayOutputStream();
				XMLUtil.transformXML(new ByteArrayInputStream(generatedOutput
						.getBytes()), generatedXML, new FileInputStream(
						removeCurrentTimeXSLT), null);
				assertEquals(targetXML.toString(), generatedXML.toString());
		
		} catch (Exception e)
		{

		}
	}

	@Test
	public void consume_shouldTestPSFConsume() throws Exception
	{
//		// TODO auto-generated
//		Assertions.fail("Not yet implemented");
	}

	@Test
	public void consume_shouldTestPWSConsume() throws Exception
	{
//		// TODO auto-generated
//		Assertions.fail("Not yet implemented");
	}
	
	@Test
	@Disabled
	public void testSaveStudyAttribute() throws Exception	{
		executeDataSet(TestUtil.STUDY_FILE);
		
		ChicaService chicaService = Context.getService(ChicaService.class);
		Study study = chicaService.getStudyByTitle("K22STUDY1");
		StudyAttribute studyAtt = new StudyAttribute();

		studyAtt.setName("TEST NAME");
		studyAtt.setDescription("DESCRIPTION");
		studyAtt.setCreator(Context.getAuthenticatedUser());
		studyAtt.setDateCreated(new Date());
		studyAtt.setRetired(false);
		studyAtt.setUuid(UUID.randomUUID().toString());
		chicaService.saveStudyAttribute(studyAtt);
		
		StudyAttributeValue studyAttVal = new StudyAttributeValue();
		
		studyAttVal.setStudyId(study.getStudyId());
		studyAttVal.setValue("DobGtSentinelDateK22Randomizer");
		studyAttVal.setStudyAttributeId(1);
		studyAttVal.setCreator(Context.getAuthenticatedUser());
		studyAttVal.setDateCreated(new Date());
		studyAttVal.setRetired(false);
		studyAttVal.setUuid(UUID.randomUUID().toString());
		chicaService.saveStudyAttributeValue(studyAttVal);
		
		StudyAttributeValue studyAttr = chicaService.getStudyAttributeValue(chicaService.getStudyByTitle("K22STUDY1"), "TEST NAME");
		assertEquals("Match","DobGtSentinelDateK22Randomizer", studyAttr.getValue());
	}
}
