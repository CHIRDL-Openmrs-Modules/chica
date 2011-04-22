package org.openmrs.module.chica.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.FormField;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.chica.Percentile;
import org.openmrs.module.chica.hibernateBeans.Bmiage;
import org.openmrs.module.chica.hibernateBeans.Chica1Appointment;
import org.openmrs.module.chica.hibernateBeans.Chica1Patient;
import org.openmrs.module.chica.hibernateBeans.Chica1PatientObsv;
import org.openmrs.module.chica.hibernateBeans.ChicaHL7Export;
import org.openmrs.module.chica.hibernateBeans.ChicaHL7ExportMap;
import org.openmrs.module.chica.hibernateBeans.ChicaHL7ExportStatus;
import org.openmrs.module.chica.hibernateBeans.Family;
import org.openmrs.module.chica.hibernateBeans.Hcageinf;
import org.openmrs.module.chica.hibernateBeans.Lenageinf;
import org.openmrs.module.chica.hibernateBeans.OldRule;
import org.openmrs.module.chica.hibernateBeans.PatientFamily;
import org.openmrs.module.chica.hibernateBeans.Statistics;
import org.openmrs.module.chica.hibernateBeans.Study;
import org.openmrs.module.chica.hibernateBeans.StudyAttributeValue;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ChicaService
{
	public void consume(InputStream input, Patient patient,
			Integer encounterId,FormInstance formInstance,Integer sessionId,
			List<FormField> fieldsToConsume,Integer locationTagId);

	public void produce(OutputStream output, PatientState state,
			Patient patient,Integer encounterId,String dssType,
			int maxDssElements,Integer sessionId);

	public void updateStatistics(Statistics statistics);

	public void createStatistics(Statistics statistics);
	
	/**
	 * @param ageMos
	 * @param sex
	 * @return
	 */
	public Percentile getWtageinf(double ageMos, int sex);

	/**
	 * @param ageMos
	 * @param sex
	 * @return
	 */
	public Bmiage getBmiage(double ageMos, int sex);
	
	/**
	 * @param ageMos
	 * @param sex
	 * @return
	 */
	public Hcageinf getHcageinf(double ageMos, int sex);

	/**
	 * @param ageMos
	 * @param sex
	 * @return
	 */
	public Lenageinf getLenageinf(double ageMos, int sex);

	public List<Study> getActiveStudies();

	public List<Statistics> getStatByFormInstance(int formInstanceId,String formName,
			Integer locationId);

	public StudyAttributeValue getStudyAttributeValue(Study study,
			String studyAttributeName);

	public List<Statistics> getStatByIdAndRule(int formInstanceId,int ruleId,String formName,
			Integer locationId);

	
	public List<OldRule> getAllOldRules();

	public List<Chica1PatientObsv> getChicaPatientObsByPSF(Integer psfId,
			Integer patientId);

	public List<Chica1PatientObsv> getChicaPatientObsByPWS(Integer pwsId,
			Integer patientId);

	public List<Chica1Appointment> getChica1AppointmentsByPatient(
			Integer patientId);

	public List<Chica1Patient> getChica1Patients();

	public PatientFamily getPatientFamily(Integer patientId);

	public Family getFamilyByAddress(String address);
	
	public Family getFamilyByPhone(String phone);

	public void savePatientFamily(PatientFamily patientFamily);

	public void saveFamily(Family family);

	public void updateFamily(Family family);
	
	public Obs getStudyArmObs(Integer familyId,Concept studyConcept);
	
	public List<String> getInsCategories();
	
	public void updateChica1Patient(Chica1Patient patient);
	
	public void updateChica1Appointment(Chica1Appointment appointment);
	
	public void setChica1PatientObsvObsId(Chica1PatientObsv chica1PatientObsv);

	public List<Chica1PatientObsv> getUnloadedChicaPatientObs(Integer patientId,String date);

	public List<Chica1Appointment> getChica1AppointmentsByDate(Integer patientId, String date);

	public String getObsvNameByObsvId(String obsvId);
	
	public String getInsCategoryByCarrier(String carrierCode);

	public String getInsCategoryBySMS(String smsCode);
	
	public String getInsCategoryByInsCode(String insCode);
	
	public PatientState getPrevProducePatientState(Integer sessionId, Integer patientStateId );
	
	public Double getHighBP(Patient patient, Integer bpPercentile, String bpType, org.openmrs.Encounter encounter);
	
	public Double getHighBP(Patient patient, Integer bpPercentile, String bpType, 
			Double heightPercentile, Date onDate);
		
	public String getDDSTLeaf(String category, Integer ageInDays);
	
	public List<Statistics> getStatsByEncounterForm(Integer encounterId,String formName);

	public List<Statistics> getStatsByEncounterFormNotPrioritized(Integer encounterId,String formName);
	
	public ChicaHL7Export insertEncounterToHL7ExportQueue(ChicaHL7Export export);

	public List<ChicaHL7Export> getPendingHL7Exports();
	
	public void saveChicaHL7Export(ChicaHL7Export export);
	
	public List<ChicaHL7Export> getPendingHL7ExportsByEncounterId(Integer encounterId);
	
	/**
	 * @param patientId
	 * @param optionalDateRestrictio
	 * 
	 * Search patient states to determine if a reprint has ever been performed during that
	 * session.
	 * 
	 * @return
	 */
	public List<PatientState> getReprintRescanStatesByEncounter(Integer encounterId, Date optionalDateRestriction, Integer locationTagId, Integer locationId);
	
	/**
	 * Gets a list of the printer stations for PSF
	 * @return List of form attributes
	 */
	public List<String> getPrinterStations(Location location);
	
	public Chica1Appointment getChica1AppointmentByEncounterId(Integer encId);
	
	public void  saveHL7ExportMap (ChicaHL7ExportMap map);
	
	public ChicaHL7ExportMap getChicaExportMapByQueueId(Integer queue_id);
	
	public ChicaHL7ExportStatus getChicaExportStatusByName (String name);
	
	public ChicaHL7ExportStatus getChicaExportStatusById (Integer id);
	
	public List<Object[]> getFormsPrintedByWeek(String formName, String locationName);
	
	public List<Object[]> getFormsScannedByWeek(String formName, String locationName);
	
	public List<Object[]> getFormsScannedAnsweredByWeek(String formName, String locationName);
	
	public List<Object[]> getFormsScannedAnythingMarkedByWeek(String formName, String locationName);
	
	public List<Object[]> getQuestionsScanned(String formName, String locationName);

	public List<Object[]> getQuestionsScannedAnswered(String formName, String locationName);
	
	/**
	 * Retrieves a list of URL objects referencing bad scans found for the provided location.
	 * 
	 * @param locationName The name of the location to search for bad scans.
	 * @return List of URL objects of the bad scans.
	 */
	public List<URL> getBadScans(String locationName);
	
	/**
	 * Moves the provided file to its parent directory named "resolved bad scans".
	 * 
	 * @param url The file (in URL format) to move to the "resolved bad scans" folder.
	 * @param formRescanned Whether or not the form was attempted to be rescanned.  If so, 
	 * the form file will be moved to the rescanned folder.  Otherwise, it will be moved to 
	 * the ignored folder.
	 * 
	 * @throws Exception
	 */
	public void moveBadScan(String url, boolean formRescanned) throws Exception;

	/**
	 * Query the mrf dump to find the list of immunizations for the patient
     * @see org.openmrs.module.chica.service.ChicaService#immunizationQuery(java.io.OutputS
	 * 
	 * @param outputFile
	 * @param locationId
	 * @param formId
	 * @param encounter
	 * @param locationTagId
	 * @param sessionId
	 */
	public void immunizationQuery(OutputStream outputFile, Integer locationId,
	                              Integer formId, org.openmrs.Encounter encounter,
	                              Integer locationTagId, Integer sessionId);
}