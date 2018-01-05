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
	public void consume(InputStream input, Patient patient,
			Integer encounterId,FormInstance formInstance,Integer sessionId,
			List<FormField> fieldsToConsume,Integer locationTagId);

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

	public StudyAttributeValue getStudyAttributeValue(Study study,
			String studyAttributeName);

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

	public List<Chica1PatientObsv> getUnloadedChicaPatientObs(Integer patientId,String date);

	public List<Chica1Appointment> getChica1AppointmentsByDate(Integer patientId, String date);
	
	public String getInsCategoryByCarrier(String carrierCode, String sendingFacility,String sendingApplication);

	public String getInsCategoryByName(String insuranceName, String sendingFacility,String sendingApplication);
	
	public String getInsCategoryByInsCode(String insCode, String sendingFacility,String sendingApplication);
	
	public Double getHighBP(Patient patient, Integer bpPercentile, String bpType, org.openmrs.Encounter encounter);
	
	public Double getHighBP(Patient patient, Integer bpPercentile, String bpType, 
			Double heightPercentile, Date onDate);
		
	public String getDDSTLeaf(String category, Integer ageInDays);
	
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
	
	/**
	 * Gets a list of printer stations for a particular user.  This uses the "location" user property as well as the 
	 * "locationTags" property to determine the printer stations.
	 * 
	 * @param user The User used to determine the printer stations.
	 * @return List of printer station names.
	 */
	public List<String> getPrinterStations(User user);
	
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
	
	public Integer getMergeFieldCount(String form_name, String vaccine_name);
	
	public List<ConceptMap> getConceptMapsByVaccine(Concept concept, String source);
	
	public Map<String, Field> saveAnswers(Map<String, org.openmrs.module.atd.xmlBeans.Field> fieldMap, 
		FormInstance formInstance, int encounterId, Patient patient);
	
	public Map<String, Field> saveAnswers(Map<String, org.openmrs.module.atd.xmlBeans.Field> fieldMap, 
		FormInstance formInstance, int encounterId, Patient patient, Set<FormField> formFieldsToSave);
	
	public List<org.openmrs.module.chica.hibernateBeans.Encounter> getEncountersForEnrolledPatients(Concept concept,
			Date startDateTime, Date endDateTime);
	
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
	public StudySubject getStudySubject(Patient patient, Study study);
	
	/**
	 * Retrieve a Study by title.
	 * 
	 * @param studyTitle The title of the study.
	 * @return Study object with the provided title or null if one is not found with the provided title.
	 */
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
	public List<PatientState> getReprintRescanStatesBySessionId(Integer sessionId, Date optionalDateRestriction, List<Integer> locationTagIds,Integer locationId) throws HibernateException;
	
	/**
	 * CHICA-1063 
	 * Create the patient state that is used to query the glooko api
	 * The state will be created and executed from a separate thread
	 * @param glookoCode
	 * @param syncTimestamp
	 * @param dataType
	 */
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
	public void addGlookoCodePersonAttribute(String firstName, String lastName, String dateOfBirth, String glookoCode);
}