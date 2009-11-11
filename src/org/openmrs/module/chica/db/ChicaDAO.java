package org.openmrs.module.chica.db;


import java.util.Date;
import java.util.List;

import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.chica.Percentile;
import org.openmrs.module.chica.hibernateBeans.Bmiage;
import org.openmrs.module.chica.hibernateBeans.Chica1Appointment;
import org.openmrs.module.chica.hibernateBeans.Chica1Patient;
import org.openmrs.module.chica.hibernateBeans.Chica1PatientObsv;
import org.openmrs.module.chica.hibernateBeans.ChicaHL7Export;
import org.openmrs.module.chica.hibernateBeans.Family;
import org.openmrs.module.chica.hibernateBeans.Hcageinf;
import org.openmrs.module.chica.hibernateBeans.Lenageinf;
import org.openmrs.module.chica.hibernateBeans.OldRule;
import org.openmrs.module.chica.hibernateBeans.PatientFamily;
import org.openmrs.module.chica.hibernateBeans.Statistics;
import org.openmrs.module.chica.hibernateBeans.Study;
import org.openmrs.module.chica.hibernateBeans.StudyAttributeValue;


/**
 * Chica-related database functions
 * 
 * @author Tammy Dugan
 * @version 1.0
 */
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
	
	public void addStatistics(Statistics statistics);
	
	public void updateStatistics(Statistics statistics);
	
	public List<Study> getActiveStudies();
	
	public List<Statistics> getStatByFormInstance(int formInstanceId,String formName, Integer locationId);

	public List<Statistics> getStatByIdAndRule(int formInstanceId,int ruleId,String formName, Integer locationId);
		
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
	
	public String getInsCategoryByCarrier(String carrierCode);
	
	public String getInsCategoryBySMS(String smsCode);
	
	public String getInsCategoryByInsCode(String insCode);
	
	public Integer getHighBP(Integer ageInYears, String sex,
			Integer bpPercentile, String bpType, Integer heightPercentile);

	public String getDDSTLeaf(String category, Integer ageInDays);
	
	public List<Statistics> getStatsByEncounterForm(Integer encounterId,String formName);

	public List<Statistics> getStatsByEncounterFormNotPrioritized(Integer encounterId,String formName);
	
	public void insertEncounterToHL7ExportQueue(ChicaHL7Export export);

	public List<ChicaHL7Export> getPendingHL7Exports();
	
	public void saveChicaHL7Export(ChicaHL7Export export);
	
	public List<ChicaHL7Export> getPendingHL7ExportsByEncounterId(Integer encounterId);
	
	public List<PatientState> getReprintRescanStatesByEncounter(Integer encounterId, Date optionalDateRestriction, 
			Integer locationTagId,Integer locationId);
		
	public Chica1Appointment getChica1AppointmentByEncounterId(Integer encId);

}
