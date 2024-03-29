package org.openmrs.module.chica.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.XMLUtil;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.LocationTagAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

/**
 * @author Tammy Dugan
 * 
 */
public class TestConsumer extends BaseModuleContextSensitiveTest
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
	public void testConsume() throws Exception
	{
		LocationService locationService = Context.getLocationService();

		int patientId = 30520;
		EncounterService encounterService = Context.getEncounterService();
		PatientService patientService = Context.getPatientService();
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);


		Encounter encounter = new Encounter();
		encounter.setEncounterDatetime(new java.util.Date());
		Patient patient = patientService.getPatient(patientId);
		encounter.setLocation(locationService.getLocation(ChirdlUtilConstants.LOCATION_UNKNOWN));
		encounter.setPatient(patient);
		encounter.setDateCreated(new Date());
		encounterService.saveEncounter(encounter);
		
		Integer encounterId = encounter.getEncounterId();
		ATDService atdService = Context.getService(ATDService.class);
		ChicaService chicaService = Context.getService(ChicaService.class);
		String filename = "test/testFiles/export_PSF.xml";
		String state = null;
		OutputStream generatedXML = new ByteArrayOutputStream();
		String PWSFilename = "test/testFiles/PWS_based_on_PSF_export.xml";
		String removeCurrentTimeXSLT = "test/testFiles/removeCurrentTime.xslt";
		AdministrationService adminService = Context.getAdministrationService();
		String booleanString = adminService
				.getGlobalProperty("atd.mergeTestCaseXML");
		boolean merge = Boolean.parseBoolean(booleanString);
		Integer formInstanceId = 1;
		Integer formId = null;
		FormService formService = Context.getFormService();
		Integer pwsFormId = formService.getForm("PWS").getFormId();
		Integer psfFormId = formService.getForm("PSF").getFormId();

		String PWSMergeDirectory = null;
		Integer locationTagId = 1;
		Integer locationId = 1;
		FormAttributeValue formAttributeValue = chirdlutilbackportsService
						.getFormAttributeValue(pwsFormId,
								"defaultMergeDirectory", locationTagId,locationId);

				if (formAttributeValue != null)
				{
					PWSMergeDirectory = formAttributeValue.getValue();
				}

				PatientState patientState = new PatientState();
				patientState.setPatient(patient);

				// create the PSF form
				state = "PSF_create";
				LocationTagAttributeValue locTagAttrValue = 
					chirdlutilbackportsService.getLocationTagAttributeValue(locationTagId, chirdlutilbackportsService.getStateByName(state).getFormName(), locationId);
				
				if(locTagAttrValue != null){
					String value = locTagAttrValue.getValue();
					//Remove try/catch for test
					if(value != null){
						formId = Integer.parseInt(value);
					}
				}
				FormInstance formInstance = new FormInstance();
				
				formInstance.setFormInstanceId(formInstanceId);

				formInstance.setFormId(formId);
				formInstance.setLocationId(locationId);
				int maxPWSDssElements = 0;

				formAttributeValue = chirdlutilbackportsService.getFormAttributeValue(
						pwsFormId, "numPrompts", locationTagId,locationId);

				if (formAttributeValue != null)
				{
					maxPWSDssElements = Integer.parseInt(formAttributeValue
							.getValue());
				}

				int maxPSFDssElements = 0;
				int sessionId = 1;
				state = "PWS_create";
				locTagAttrValue = 
					chirdlutilbackportsService.getLocationTagAttributeValue(locationTagId, chirdlutilbackportsService.getStateByName(state).getFormName(), locationId);
				
				if(locTagAttrValue != null){
					String value = locTagAttrValue.getValue();
					if(value != null){
					formId = Integer.parseInt(value);
					}
				}
				formAttributeValue = chirdlutilbackportsService.getFormAttributeValue(
						psfFormId, "numPrompts", locationTagId,locationId);

				if (formAttributeValue != null)
				{
					maxPSFDssElements = Integer.parseInt(formAttributeValue
							.getValue());
				}
				
				patientState.setFormInstance(formInstance);

				atdService.produce(new ByteArrayOutputStream(), patientState,
						patient, encounterId, "PSF", maxPSFDssElements,sessionId);

				// read in the export xml and store observations
				chicaService.consume(new FileInputStream(filename), patient,
						encounterId, formInstance, 1, null,locationTagId);

				// create the PWS form
				state = "PWS_create";
				locTagAttrValue = 
					chirdlutilbackportsService.getLocationTagAttributeValue(locationTagId, chirdlutilbackportsService.getStateByName(state).getFormName(), locationId);
				
				if(locTagAttrValue != null){
					String value = locTagAttrValue.getValue();
					if(value != null){
					formId = Integer.parseInt(value);
					}
				}
				formInstance = new FormInstance();
				formInstance.setFormInstanceId(formInstanceId);

				formInstance.setFormId(formId);
				formInstance.setLocationId(locationId);
				generatedXML = new ByteArrayOutputStream();
				atdService.produce(generatedXML, patientState, patient,
						encounterId, "PWS", maxPWSDssElements,sessionId);
				ByteArrayOutputStream targetXML = new ByteArrayOutputStream();
				IOUtil.bufferedReadWrite(new FileInputStream(PWSFilename),
						targetXML);
				String generatedOutput = generatedXML.toString();
				if (merge && PWSMergeDirectory != null)
				{
					FileWriter writer = new FileWriter(PWSMergeDirectory
							+ "file5.xml");
					writer.write(generatedOutput);
					writer.close();
				}
				generatedXML = new ByteArrayOutputStream();
				XMLUtil.transformXML(new ByteArrayInputStream(generatedOutput
						.getBytes()), generatedXML, new FileInputStream(
						removeCurrentTimeXSLT), null);
				assertEquals(targetXML.toString(), generatedXML.toString());
	}
}
