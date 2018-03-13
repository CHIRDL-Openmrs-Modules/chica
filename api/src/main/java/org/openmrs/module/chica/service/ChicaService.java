package org.openmrs.module.chica.service;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.FormField;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.annotation.Authorized;
import org.openmrs.module.atd.xmlBeans.Field;
import org.openmrs.module.chica.Percentile;
import org.openmrs.module.chica.hibernateBeans.Bmiage;
import org.openmrs.module.chica.hibernateBeans.Chica1Appointment;
import org.openmrs.module.chica.hibernateBeans.Chica1Patient;
import org.openmrs.module.chica.hibernateBeans.Chica1PatientObsv;
import org.openmrs.module.chica.hibernateBeans.ChicaHL7Export;
import org.openmrs.module.chica.hibernateBeans.ChicaHL7ExportMap;
import org.openmrs.module.chica.hibernateBeans.ChicaHL7ExportStatus;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.hibernateBeans.Family;
import org.openmrs.module.chica.hibernateBeans.Hcageinf;
import org.openmrs.module.chica.hibernateBeans.Lenageinf;
import org.openmrs.module.chica.hibernateBeans.PatientFamily;
import org.openmrs.module.chica.hibernateBeans.Study;
import org.openmrs.module.chica.hibernateBeans.StudyAttributeValue;
import org.openmrs.module.chica.hibernateBeans.StudySubject;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;

public interface ChicaService
{
	@Authorized()
	public void consume(InputStream input, Patient patient,
			Integer encounterId,FormInstance formInstance,Integer sessionId,
			List<FormField> fieldsToConsume,Integer locationTagId);

	/**
	 * @param ageMos
	 * @param sex
	 * @return
	 */
	@Authorized()
	public Percentile getWtageinf(double ageMos, int sex);

	/**
	 * @param ageMos
	 * @param sex
	 * @return
	 */
	@Authorized()
	public Bmiage getBmiage(double ageMos, int sex);
	
	/**
	 * @param ageMos
	 * @param sex
	 * @return
	 */
	@Authorized()
	public Hcageinf getHcageinf(double ageMos, int sex);

	/**
	 * @param ageMos
	 * @param sex
	 * @return
	 */
	@Authorized()
	public Lenageinf getLenageinf(double ageMos, int sex);

	@Authorized()
	public List<Study> getActiveStudies();

	@Authorized()
	public StudyAttributeValue getStudyAttributeValue(Study study,
			String studyAttributeName);

	@Authorized()
	public List<Chica1PatientObsv> getChicaPatientObsByPSF(Integer psfId,
			Integer patientId);

	@Authorized()
	public List<Chica1PatientObsv> getChicaPatientObsByPWS(Integer pwsId,
			Integer patientId);

	@Authorized()
	public List<Chica1Appointment> getChica1AppointmentsByPatient(
			Integer patientId);

	@Authorized()
	public List<Chica1Patient> getChica1Patients();

	@Authorized()
	public PatientFamily getPatientFamily(Integer patientId);

	@Authorized()
	public Family getFamilyByAddress(String address);
	
	@Authorized()
	public Family getFamilyByPhone(String phone);

	@Authorized()
	public void savePatientFamily(PatientFamily patientFamily);

	@Authorized()
	public void saveFamily(Family family);

	@Authorized()
	public void updateFamily(Family family);
	
	@Authorized()
	public Obs getStudyArmObs(Integer familyId,Concept studyConcept);
	
	@Authorized()
	public List<String> getInsCategories();
	
	@Authorized()
	public void updateChica1Patient(Chica1Patient patient);
	
	@Authorized()
	public void updateChica1Appointment(Chica1Appointment appointment);

	@Authorized()
	public List<Chica1PatientObsv> getUnloadedChicaPatientObs(Integer patientId,String date);

	@Authorized()
	public List<Chica1Appointment> getChica1AppointmentsByDate(Integer patientId, String date);
	
	@Authorized()
	public String getInsCategoryByCarrier(String carrierCode, String sendingFacility,String sendingApplication);

	@Authorized()
	public String getInsCategoryByName(String insuranceName, String sendingFacility,String sendingApplication);
	
	@Authorized()
	public String getInsCategoryByInsCode(String insCode, String sendingFacility,String sendingApplication);
	
	@Authorized()
	public Double getHighBP(Patient patient, Integer bpPercentile, String bpType, org.openmrs.Encounter encounter);
	
	@Authorized()
	public Double getHighBP(Patient patient, Integer bpPercentile, String bpType, 
			Double heightPercentile, Date onDate);
		
	@Authorized()
	public String getDDSTLeaf(String category, Integer ageInDays);
	
	@Authorized()
	public ChicaHL7Export insertEncounterToHL7ExportQueue(ChicaHL7Export export);

	@Authorized()
	public List<ChicaHL7Export> getPendingHL7Exports();
	
	@Authorized()
	public void saveChicaHL7Export(ChicaHL7Export export);
	
	@Authorized()
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
	@Authorized()
	public List<PatientState> getReprintRescanStatesByEncounter(Integer encounterId, Date optionalDateRestriction, Integer locationTagId, Integer locationId);
	
	/**
	 * Gets a list of the printer stations for PSF
	 * @return List of form attributes
	 */
	@Authorized()
	public List<String> getPrinterStations(Location location);
	
	/**
	 * Gets a list of printer stations for a particular user.  This uses the "location" user property as well as the 
	 * "locationTags" property to determine the printer stations.
	 * 
	 * @param user The User used to determine the printer stations.
	 * @return List of printer station names.
	 */
	@Authorized()
	public List<String> getPrinterStations(User user);
	
	@Authorized()
	public Chica1Appointment getChica1AppointmentByEncounterId(Integer encId);
	
	@Authorized()
	public void  saveHL7ExportMap (ChicaHL7ExportMap map);
	
	@Authorized()
	public ChicaHL7ExportMap getChicaExportMapByQueueId(Integer queue_id);
	
	@Authorized()
	public ChicaHL7ExportStatus getChicaExportStatusByName (String name);
	
	@Authorized()
	public ChicaHL7ExportStatus getChicaExportStatusById (Integer id);
	
	@Authorized()
	public List<Object[]> getFormsPrintedByWeek(String formName, String locationName);
	
	@Authorized()
	public List<Object[]> getFormsScannedByWeek(String formName, String locationName);
	
	@Authorized()
	public List<Object[]> getFormsScannedAnsweredByWeek(String formName, String locationName);
	
	@Authorized()
	public List<Object[]> getFormsScannedAnythingMarkedByWeek(String formName, String locationName);
	
	@Authorized()
	public List<Object[]> getQuestionsScanned(String formName, String locationName);

	@Authorized()
	public List<Object[]> getQuestionsScannedAnswered(String formName, String locationName);
	
	@Authorized()
	public Integer getMergeFieldCount(String form_name, String vaccine_name);
	
	@Authorized()
	public List<ConceptMap> getConceptMapsByVaccine(Concept concept, String source);
	
	@Authorized()
	public Map<String, Field> saveAnswers(Map<String, org.openmrs.module.atd.xmlBeans.Field> fieldMap, 
		FormInstance formInstance, int encounterId, Patient patient);
	
	@Authorized()
	public Map<String, Field> saveAnswers(Map<String, org.openmrs.module.atd.xmlBeans.Field> fieldMap, 
		FormInstance formInstance, int encounterId, Patient patient, Set<FormField> formFieldsToSave);
	
	@Authorized()
	public List<org.openmrs.module.chica.hibernateBeans.Encounter> getEncountersForEnrolledPatients(Concept concept,
			Date startDateTime, Date endDateTime);
	
	@Authorized()
	public List<Encounter> getEncountersForEnrolledPatientsExcludingConcepts(Concept includeConcept, Concept excludeConcept,
			Date startDateTime, Date endDateTime);
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
	/* 
     * Used by Vivienne's immunization forecasting service
     * Commenting out since we are using CHIRP's
     * 
	public void immunizationQuery(OutputStream outputFile, Integer locationId,
	                              Integer formId, org.openmrs.Encounter encounter,
	                              Integer locationTagId, Integer sessionId);

	*/
	
	/**
	 * Retrieve the patient's study subject ID based on patient and study.  This will create a new StudySubject for the patient 
	 * if one cannot be found.
	 * 
	 * @param patient The patient used to find or create a Subject.
	 * @param study The study.
	 * @return A StudySubject or null if the patient or Study is null.
	 */
	@Authorized()
	public StudySubject getStudySubject(Patient patient, Study study);
	
	/**
	 * Retrieve a Study by title.
	 * 
	 * @param studyTitle The title of the study.
	 * @return Study object with the provided title or null if one is not found with the provided title.
	 */
	@Authorized()
	public Study getStudyByTitle(String studyTitle);
	
	/**
	 * DWE CHICA-761
	 * Get reprint/rescan states by session Id
	 * @param sessionId
	 * @param optionalDateRestriction
	 * @param locationTagIds
	 * @param locationId
	 * @return
	 */
	@Authorized()
	public List<PatientState> getReprintRescanStatesBySessionId(Integer sessionId, Date optionalDateRestriction, List<Integer> locationTagIds,Integer locationId) throws HibernateException;
	
	/**
	 * CHICA-1063 
	 * Create the patient state that is used to query the glooko api
	 * The state will be created and executed from a separate thread
	 * @param glookoCode
	 * @param syncTimestamp
	 * @param dataType
	 */
	@Authorized()
	public void createPatientStateQueryGlooko(String glookoCode, String syncTimestamp, String dataType);
	
	/**
	 * CHICA-1063
	 * Create the GlookoCode person attribute
	 * The GlookoCode person attribute is used to query the Glooko api for device data
	 * @param firstName
	 * @param lastName
	 * @param dateOfBirth
	 * @param glookoCode
	 */
	@Authorized()
	public void addGlookoCodePersonAttribute(String firstName, String lastName, String dateOfBirth, String glookoCode);
}