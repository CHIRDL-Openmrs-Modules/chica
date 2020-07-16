package org.openmrs.module.chica.service;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.openmrs.Concept;
import org.openmrs.FormField;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
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
import org.openmrs.module.chica.hibernateBeans.StudyAttribute;
import org.openmrs.module.chica.hibernateBeans.StudyAttributeValue;
import org.openmrs.module.chica.hibernateBeans.StudySubject;
import org.openmrs.module.chica.hibernateBeans.DDST_Milestone;
import org.openmrs.module.chica.hibernateBeans.InsuranceMapping;
import org.openmrs.module.chica.hibernateBeans.Wtageinf;
import org.openmrs.module.chica.hibernateBeans.HighBP;
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
	public void savePatientFamily(PatientFamily patientFamily) throws APIException;

	@Authorized()
	public void saveFamily(Family family) throws APIException;

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
	
	/**
	 * Saves a new chica appointment or updates an existing chica appointment
	 * 
	 * @param chica1Appointment to be saved
	 * @throws APIException
	 */
	@Authorized()
	public Chica1Appointment saveChica1Appointment(Chica1Appointment chica1Appointment) throws APIException;
	
	/**
	 * voids chica appointment
	 * 
	 * @param chica1Appointment to be saved
	 * @param reason
	 * @throws APIException
	 */
	@Authorized()
	public Chica1Appointment voidChica1Appointment(Chica1Appointment chica1Appointment, String reason) throws APIException;
	
	/**
	 * unvoids chica appointment
	 * 
	 * @param chica1Appointment to be saved
	 * @throws APIException
	 */
	@Authorized()
	public Chica1Appointment unvoidChica1Appointment(Chica1Appointment chica1Appointment) throws APIException;
	
	/**
	 * Saves a new chica patient or updates an existing chica patient
	 * 
	 * @param chica1Patient to be saved
	 * @throws APIException
	 */
	@Authorized()
	public Chica1Patient saveChica1Patient(Chica1Patient chica1Patient) throws APIException;

	/**
	 * voids chica patient
	 * 
	 * @param chica1Patient to be saved
	 * @param reason to void
	 * @throws APIException
	 */
	@Authorized()
	public Chica1Patient voidChica1Patient(Chica1Patient chica1Patient, String reason) throws APIException;

	/**
	 * unvoids chica patient 
	 * 
	 * @param chica1Patient to be saved
	 * @throws APIException
	 */
	@Authorized()
	public Chica1Patient unvoidChica1Patient(Chica1Patient chica1Patient) throws APIException;
	
	/**
	 * Saves a new chica patient obsv or updates an existing chica patient obsv
	 * 
	 * @param chica1PatientObsv to be saved
	 * @throws APIException
	 */
	@Authorized()
	public Chica1PatientObsv saveChica1PatientObsv(Chica1PatientObsv chica1PatientObsv) throws APIException;
	
	/**
	 * voids a new chica patient obsv 
	 * 
	 * @param chica1PatientObsv to be saved
	 * @param reason to void chica1PatientObsv
	 * @throws APIException
	 */
	@Authorized()
	public Chica1PatientObsv voidChica1PatientObsv(Chica1PatientObsv chica1PatientObsv, String reason) throws APIException;
	
	/**
	 * unvoids chica patient obsv 
	 * 
	 * @param chica1PatientObsv to be saved
	 * @throws APIException
	 */
	@Authorized()
	public Chica1PatientObsv unvoidChica1PatientObsv(Chica1PatientObsv chica1PatientObsv) throws APIException;
	
	/**
	 * Saves a new chica study subject or updates an existing chica study subject
	 * 
	 * @param studySubject to be saved
	 * @throws APIException
	 */
	@Authorized()
	public StudySubject saveStudySubject(StudySubject studySubject) throws APIException;
	
	/**
	 * voids chica study subject 
	 * 
	 * @param studySubject to be saved
	 * @param reason to void study subject
	 * @throws APIException
	 */
	@Authorized()
	public StudySubject voidStudySubject(StudySubject studySubject, String reason) throws APIException;
	
	/**
	 * unvoids chica study subject
	 * 
	 * @param studySubject to be saved
	 * @throws APIException
	 */
	@Authorized()
	public StudySubject unvoidStudySubject(StudySubject studySubject) throws APIException;
	
	/**
	 * Saves a new chica HL7Export Status or updates an existing chica HL7Export Status
	 * 
	 * @param chicaHL7ExportStatus to be saved
	 * @throws APIException
	 */
	@Authorized()
	public ChicaHL7ExportStatus saveChicaHL7ExportStatus(ChicaHL7ExportStatus chicaHL7ExportStatus) throws APIException;
	
	/**
	 * voids HL7Export Status 
	 * 
	 * @param chicaHL7ExportStatus to be saved
	 * @param reason to retire chicaHL7ExportStatus
	 * @throws APIException
	 */
	@Authorized()
	public ChicaHL7ExportStatus retireChicaHL7ExportStatus(ChicaHL7ExportStatus chicaHL7ExportStatus, String reason) throws APIException;
	
	/**
	 * unretires chica HL7Export Status
	 * 
	 * @param chicaHL7ExportStatus to be saved
	 * @throws APIException
	 */
	@Authorized()
	public ChicaHL7ExportStatus unretireChicaHL7ExportStatus(ChicaHL7ExportStatus chicaHL7ExportStatus) throws APIException;
	
	/**
	 * Saves a new chica study attribute or updates an existing chica study attribute
	 * 
	 * @param studyAttribute to be saved
	 * @throws APIException
	 */
	@Authorized()
	public StudyAttribute saveStudyAttribute(StudyAttribute studyAttribute) throws APIException;

	/**
	 * retires chica study attribute
	 * 
	 * @param studyAttribute to be saved
	 * @param reason to retire study attribute
	 * @throws APIException
	 */
	@Authorized()
	public StudyAttribute retireStudyAttribute(StudyAttribute studyAttribute, String reason) throws APIException;

	/**
	 * unretires chica study attribute 
	 * 
	 * @param studyAttribute to be saved
	 * @throws APIException
	 */
	@Authorized()
	public StudyAttribute unretireStudyAttribute(StudyAttribute studyAttribute) throws APIException;
	
	/**
	 * Saves a new chica ddst or updates an existing chica ddst
	 * 
	 * @param ddst_Milestone to be saved
	 * @throws APIException
	 */
	@Authorized()
	public DDST_Milestone saveDDST_Milestone(DDST_Milestone ddst_Milestone) throws APIException;
	
	/**
	 * retires chica ddst 
	 * 
	 * @param ddst_Milestone to be saved
	 * @param reason to retire ddst_Milestone
	 * @throws APIException
	 */
	@Authorized()
	public DDST_Milestone retireDDST_Milestone(DDST_Milestone ddst_Milestone, String reason) throws APIException;
	
	/**
	 * unretires chica ddst 
	 * 
	 * @param ddst_Milestone to be saved
	 * @throws APIException
	 */
	@Authorized()
	public DDST_Milestone unretireDDST_Milestone(DDST_Milestone ddst_Milestone) throws APIException;
	
	/**
	 * Saves a new chica insurance mapping or updates an existing chica insurance mapping
	 * 
	 * @param insuranceMapping to be saved
	 * @throws APIException
	 */
	@Authorized()
	public InsuranceMapping saveInsuranceMapping(InsuranceMapping insuranceMapping) throws APIException;
	
	/**
	 * retires chica insurance mapping 
	 * 
	 * @param insuranceMapping to be saved
	 * @param reason to retire chica insurance mapping
	 * @throws APIException
	 */
	@Authorized()
	public InsuranceMapping retireInsuranceMapping(InsuranceMapping insuranceMapping, String reason) throws APIException;
	
	/**
	 * unretires chica insurance mapping 
	 * 
	 * @param insuranceMapping to be saved
	 * @throws APIException
	 */
	@Authorized()
	public InsuranceMapping unretireInsuranceMapping(InsuranceMapping insuranceMapping) throws APIException;
	
	/**
	 * Saves a new chica study or updates an existing chica study
	 * 
	 * @param study to be saved
	 * @throws APIException
	 */
	@Authorized()
	public Study saveStudy(Study study) throws APIException;

	/**
	 * retires chica study
	 * 
	 * @param study to be saved
	 * @param reason to retire chica study
	 * @throws APIException
	 */
	@Authorized()
	public Study retireStudy(Study study, String reason) throws APIException;

	/**
	 * unretires chica study 
	 * 
	 * @param study to be saved
	 * @throws APIException
	 */
	@Authorized()
	public Study unretireStudy(Study study) throws APIException;
	
	/**
	 * Saves a new chica study attribute value or updates an existing chica study attribute value
	 * 
	 * @param studyAttributeValue to be saved
	 * @throws APIException
	 */
	@Authorized()
	public StudyAttributeValue saveStudyAttributeValue(StudyAttributeValue studyAttributeValue) throws APIException;

	/**
	 * retires chica study attribute value
	 * 
	 * @param studyAttributeValue to be saved
	 * @param reason to retire chica study attribute value
	 * @throws APIException
	 */
	@Authorized()
	public StudyAttributeValue retireStudyAttributeValue(StudyAttributeValue studyAttributeValue, String reason) throws APIException;

	/**
	 * unretires chica study attribute value
	 * 
	 * @param studyAttributeValue to be saved
	 * @throws APIException
	 */
	@Authorized()
	public StudyAttributeValue unretireStudyAttributeValue(StudyAttributeValue studyAttributeValue) throws APIException;
	
	/**
	 * Saves a new chica wtageinf or updates an existing chica wtageinf
	 * 
	 * @param wtageinf to be saved
	 * @throws APIException
	 */
	@Authorized()
	public Wtageinf saveWtageinf(Wtageinf wtageinf) throws APIException;
	
	/**
	 * retires chica wtageinf 
	 * 
	 * @param wtageinf to be saved
	 * @param reason to retire wtageinf
	 * @throws APIException
	 */
	@Authorized()
	public Wtageinf retireWtageinf(Wtageinf wtageinf, String reason) throws APIException;
	
	/**
	 * unretires chica wtageinf 
	 * 
	 * @param wtageinf to be saved
	 * @throws APIException
	 */
	@Authorized()
	public Wtageinf unretireWtageinf(Wtageinf wtageinf) throws APIException;
	
	/**
	 * Saves a new chica bmiage or updates an existing chica bmiage
	 * 
	 * @param bmiage to be saved
	 * @throws APIException
	 */
	@Authorized()
	public Bmiage saveBmiage(Bmiage bmiage) throws APIException;

	/**
	 * retires chica bmiage 
	 * 
	 * @param bmiage to be saved
	 * @param reason to retire chica bmiage
	 * @throws APIException
	 */
	@Authorized()
	public Bmiage retireBmiage(Bmiage bmiage, String reason) throws APIException;

	/**
	 * unretires chica bmiage 
	 * 
	 * @param bmiage to be saved
	 * @throws APIException
	 */
	@Authorized()
	public Bmiage unretireBmiage(Bmiage bmiage) throws APIException;
	
	/**
	 * Saves a new chica hcageinf or updates an existing chica hcageinf
	 * 
	 * @param hcageinf to be saved
	 * @throws APIException
	 */
	@Authorized()
	public Hcageinf saveHcageinf(Hcageinf hcageinf) throws APIException;

	/**
	 * retires chica hcageinf 
	 * 
	 * @param hcageinf to be saved
	 * @param reason to retire chica hcageinf 
	 * @throws APIException
	 */
	@Authorized()
	public Hcageinf retireHcageinf(Hcageinf hcageinf, String reason) throws APIException;

	/**
	 * unretires chica hcageinf
	 * 
	 * @param hcageinf to be saved
	 * @throws APIException
	 */
	@Authorized()
	public Hcageinf unretireHcageinf(Hcageinf hcageinf) throws APIException;
	
	/**
	 * Saves a new chica lenageinf or updates an existing chica lenageinf
	 * 
	 * @param lenageinf to be saved
	 * @throws APIException
	 */
	@Authorized()
	public Lenageinf saveLenageinf(Lenageinf lenageinf) throws APIException;
	
	/**
	 * retires chica lenageinf 
	 * 
	 * @param lenageinf to be saved
	 * @param reason to retire chica lenageinf
	 * @throws APIException
	 */
	@Authorized()
	public Lenageinf retireLenageinf(Lenageinf lenageinf, String reason) throws APIException;
	
	/**
	 * unretires chica lenageinf
	 * 
	 * @param lenageinf to be saved
	 * @throws APIException
	 */
	@Authorized()
	public Lenageinf unretireLenageinf(Lenageinf lenageinf) throws APIException;
	
	/**
	 * Saves a new chica highBP or updates an existing chica highBP
	 * 
	 * @param highBP to be saved
	 * @throws APIException
	 */
	@Authorized()
	public HighBP saveHighBP(HighBP highBP) throws APIException;
	
	/**
	 * retires chica highBP 
	 * 
	 * @param highBP to be saved
	 * @param reason to retire chica highBP 
	 * @throws APIException
	 */
	@Authorized()
	public HighBP retireHighBP(HighBP highBP, String reason) throws APIException;
	
	/**
	 * unretires chica highBP 
	 * 
	 * @param highBP to be saved
	 * @throws APIException
	 */
	@Authorized()
	public HighBP unretireHighBP(HighBP highBP) throws APIException;
	
	/**
	 * voids chica patient family 
	 * 
	 * @param patientFamily to be saved
	 * @param reason to void chica patient family  
	 * @throws APIException
	 */
	@Authorized()
	public void voidPatientFamily(PatientFamily patientFamily, String reason) throws APIException;
	
	/**
	 * unvoids chica patient family 
	 * 
	 * @param patientFamily to be saved
	 * @throws APIException
	 */
	@Authorized()
	public void unvoidPatientFamily(PatientFamily patientFamily) throws APIException;
	
	/**
	 * voids chica family 
	 * 
	 * @param family to be saved
	 * @param reason to void family  
	 * @throws APIException
	 */
	@Authorized()
	public void voidFamily(Family family, String reason) throws APIException;
	
	/**
	 * unvoids chica family 
	 * 
	 * @param family to be saved
	 * @throws APIException
	 */
	@Authorized()
	public void unvoidFamily(Family family) throws APIException;
	
		
}