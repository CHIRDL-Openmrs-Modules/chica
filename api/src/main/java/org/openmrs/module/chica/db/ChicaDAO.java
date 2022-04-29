package org.openmrs.module.chica.db;


import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.db.DAOException;
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
import org.openmrs.module.chica.hibernateBeans.MDlenageinf;
import org.openmrs.module.chica.hibernateBeans.PatientFamily;
import org.openmrs.module.chica.hibernateBeans.Study;
import org.openmrs.module.chica.hibernateBeans.StudyAttribute;
import org.openmrs.module.chica.hibernateBeans.StudyAttributeValue;
import org.openmrs.module.chica.hibernateBeans.StudySubject;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.springframework.transaction.annotation.Transactional;


/**
 * Chica-related database functions
 * 
 * @author Tammy Dugan
 * @version 1.0
 */
@Transactional
public interface ChicaDAO {

	
		/**
	 * @param ageMos
	 * @param sex
	 * @return
	 */
	public Percentile getWtageinf(double ageMos,int sex);
	
	/**
	 * @param ageMos
	 * @param sex
	 * @return
	 */
	public Bmiage getBmiage(double ageMos,int sex);
	
	/**
	 * @param ageMos
	 * @param sex
	 * @return
	 */
	public Hcageinf getHcageinf(double ageMos,int sex);
	
	/**
	 * @param ageMos
	 * @param sex
	 * @return
	 */
	public Lenageinf getLenageinf(double ageMos,int sex);
	
	public List<Study> getActiveStudies();
	
	/**
	 * Retrieve a StudyAttribute by name.
	 * 
	 * @param studyAttributeName The name of the study attribute.
	 * @param includeRetired 
	 * @return StudyAttribute list with the provided study attribute name or null if one is not found with the provided name.
	 */
	public List<StudyAttribute> getStudyAttributesByName(String studyAttributeName, boolean includeRetired);
	
	/**
	 * Retrieve list of StudyAttributeValue by Study and StudyAttributeName.
	 * 
	 * @param studyList The study list object.
	 * @param studyAttributeList The study attribute list object.
	 * @param includeRetired 
	 * @return StudyAttributeValue list with the provided study and study attribute.
	 */
	public List<StudyAttributeValue> getStudyAttributeValues(List<Study> studyList,
			List<StudyAttribute> studyAttributeList, boolean includeRetired);
	
	public List<Chica1PatientObsv> getChicaPatientObsByPSF(Integer psfId,Integer patientId);
	public List<Chica1PatientObsv> getChicaPatientObsByPWS(Integer pwsId,Integer patientId);

	public List<Chica1Appointment> getChica1AppointmentsByPatient(Integer patientId);
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

	public Integer getHighBP(Integer ageInYears, String sex,
			Integer bpPercentile, String bpType, Integer heightPercentile);

	public String getDDSTLeaf(String category, Integer ageInDays);
	
	public ChicaHL7Export insertEncounterToHL7ExportQueue(ChicaHL7Export export);

	public List<ChicaHL7Export> getPendingHL7Exports();
	
	public void saveChicaHL7Export(ChicaHL7Export export);
	
	public List<ChicaHL7Export> getPendingHL7ExportsByEncounterId(Integer encounterId);
	
	public List<PatientState> getReprintRescanStatesByEncounter(Integer encounterId, Date optionalDateRestriction, 
			Integer locationTagId,Integer locationId);
		
	public Chica1Appointment getChica1AppointmentByEncounterId(Integer encId);
	
	/** Insert queued hl7 export to map table
	 * @param map
	 * @return
	 */
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
	
	public List<Encounter> getEncountersForEnrolledPatients(Concept concept, Date startDateTime, Date endDateTime);
	
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
	public StudySubject getStudySubject(Patient patient, Study study);
	
	/**
	 * Retrieve a Study by title.
	 * 
	 * @param studyTitle The title of the study.
	 * @return Study object with the provided title or null if one is not found with the provided title.
	 */
	public Study getStudyByTitle(String studyTitle);
	
	/**
	 * Retrieve a Study by title.
	 * 
	 * @param studyTitle The title of the study.
	 * @param includeRetired retired value
	 * @return Study list with the provided title or null if one is not found with the provided title.
	 */
	public List<Study> getStudiesByTitle(String studyTitle, boolean includeRetired);
	
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
	 * Saves a new chica study attribute
	 * 
	 * @param studyAttribute to be saved
	 * @throws DAOException
	 */
	public StudyAttribute saveStudyAttribute(StudyAttribute studyAttribute) ;
	
	/**
	 * Saves a chica study attribute value
	 * 
	 * @param studyAttributeValue to be saved
	 * @throws DAOException
	 */
	public StudyAttributeValue saveStudyAttributeValue(StudyAttributeValue studyAttributeValue);
	
	/**
	 * Saves a new chica study
	 * 
	 * @param study to be saved
	 * @throws DAOException
	 */
	public Study saveStudy(Study study) ;
	
	/**
     * @param meanAge
     * @return Muscular Dystrophy height mean age percentile
     */
    public MDlenageinf getMdlenageinf(double meanAge);
    
    /**
     * @param meanAge
     * @return Muscular Dystrophy height mean age left percentile
     */
    public MDlenageinf getMdlenageLeftinf(double meanAge);
    
    /**
     * @param meanAge
     * @return Muscular Dystrophy height mean age left percentile
     */
    public MDlenageinf getMdlenageRightinf(double meanAge);
	
}
