package org.openmrs.module.chica.db;


import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.Obs;
import org.openmrs.Patient;
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
import org.openmrs.module.chica.hibernateBeans.OldRule;
import org.openmrs.module.chica.hibernateBeans.PatientFamily;
import org.openmrs.module.chica.hibernateBeans.Study;
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
		
	public StudyAttributeValue getStudyAttributeValue(Study study,String studyAttributeName);

	public List<OldRule> getAllOldRules();
	
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
	
	public void setChica1PatientObsvObsId(Chica1PatientObsv chica1PatientObsv);

	public List<Chica1PatientObsv> getUnloadedChicaPatientObs(Integer patientId,String date);

	public List<Chica1Appointment> getChica1AppointmentsByDate(Integer patientId, String date);

	public String getObsvNameByObsvId(String obsvId);
	
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
	
	public Integer getMergeFieldCount(String form_name, String vaccine_name);
	
	public List<ConceptMap> getConceptMapsByVaccine(Concept concept, String source);
	
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
	 * DWE CHICA-761
	 * Get reprint/rescan states by session Id
	 * @param sessionId
	 * @param optionalDateRestriction
	 * @param locationTagIds
	 * @param locationId
	 * @return
	 */
	public List<PatientState> getReprintRescanStatesBySessionId(Integer sessionId, Date optionalDateRestriction, List<Integer> locationTagIds,Integer locationId) throws HibernateException;
}
