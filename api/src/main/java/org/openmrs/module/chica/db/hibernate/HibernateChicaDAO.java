package org.openmrs.module.chica.db.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.LocationService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.Percentile;
import org.openmrs.module.chica.db.ChicaDAO;
import org.openmrs.module.chica.hibernateBeans.Bmiage;
import org.openmrs.module.chica.hibernateBeans.Chica1Appointment;
import org.openmrs.module.chica.hibernateBeans.Chica1Patient;
import org.openmrs.module.chica.hibernateBeans.Chica1PatientObsv;
import org.openmrs.module.chica.hibernateBeans.ChicaHL7Export;
import org.openmrs.module.chica.hibernateBeans.ChicaHL7ExportMap;
import org.openmrs.module.chica.hibernateBeans.ChicaHL7ExportStatus;
import org.openmrs.module.chica.hibernateBeans.DDST_Milestone;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.hibernateBeans.Family;
import org.openmrs.module.chica.hibernateBeans.Hcageinf;
import org.openmrs.module.chica.hibernateBeans.Lenageinf;
import org.openmrs.module.chica.hibernateBeans.PatientFamily;
import org.openmrs.module.chica.hibernateBeans.Study;
import org.openmrs.module.chica.hibernateBeans.StudyAttribute;
import org.openmrs.module.chica.hibernateBeans.StudyAttributeValue;
import org.openmrs.module.chica.hibernateBeans.StudySubject;
import org.openmrs.module.chica.hibernateBeans.Wtageinf;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;

/**
 * Hibernate implementation of chica database methods.
 * 
 * @author Tammy Dugan
 * 
 */
public class HibernateChicaDAO implements ChicaDAO
{

	private static final Log LOG = LogFactory.getLog(HibernateChicaDAO.class);

	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;

	/**
	 * 
	 */
	public HibernateChicaDAO()
	{
	}

	/**
	 * Set session factory
	 * 
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
	}

	public Percentile getWtageinf(double ageMos, int sex)
	{
		try
		{
			String sql = "select * from chica_wtageinf where agemos=? and sex=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setDouble(0, ageMos);
			qry.setInteger(1, sex);
			qry.addEntity(Wtageinf.class);
			return (Percentile) qry.uniqueResult();
		} catch (Exception e)
		{
			this.LOG.error(Util.getStackTrace(e));
		}
		return null;
	}

	public Bmiage getBmiage(double ageMos, int sex)
	{
		try
		{
			String sql = "select * from chica_bmiage where agemos=? and sex=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setDouble(0, ageMos);
			qry.setInteger(1, sex);
			qry.addEntity(Bmiage.class);
			return (Bmiage) qry.uniqueResult();
		} catch (Exception e)
		{
			this.LOG.error(Util.getStackTrace(e));
		}
		return null;
	}

	public Hcageinf getHcageinf(double ageMos, int sex)
	{
		try
		{
			String sql = "select * from chica_hcageinf where agemos=? and sex=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setDouble(0, ageMos);
			qry.setInteger(1, sex);
			qry.addEntity(Hcageinf.class);
			return (Hcageinf) qry.uniqueResult();
		} catch (Exception e)
		{
			this.LOG.error(Util.getStackTrace(e));
		}
		return null;
	}

	public Lenageinf getLenageinf(double ageMos, int sex)
	{
		try
		{
			String sql = "select * from chica_lenageinf where agemos=? and sex=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setDouble(0, ageMos);
			qry.setInteger(1, sex);
			qry.addEntity(Lenageinf.class);
			return (Lenageinf) qry.uniqueResult();
		} catch (Exception e)
		{
			this.LOG.error(Util.getStackTrace(e));
		}
		return null;
	}

	public List<Study> getActiveStudies()
	{
		try
		{
			String sql = "select * from chica_study where status=? and retired=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setInteger(0, 1);
			qry.setBoolean(1, false);
			qry.addEntity(Study.class);
			return qry.list();
		} catch (Exception e)
		{
			this.LOG.error(Util.getStackTrace(e));
		}
		return null;
	}

	private StudyAttribute getStudyAttributeByName(String studyAttributeName)
	{
		try
		{
			String sql = "select * from chica_study_attribute "
					+ "where name=? and retired=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setString(0, studyAttributeName);
			qry.setBoolean(1, false);
			qry.addEntity(StudyAttribute.class);

			List<StudyAttribute> list = qry.list();

			if (list != null && list.size() > 0)
			{
				return list.get(0);
			}
			return null;
		} catch (Exception e)
		{
			this.LOG.error(Util.getStackTrace(e));
		}
		return null;
	}
	
	public StudyAttributeValue getStudyAttributeValue(Study study,
			String studyAttributeName)
	{
		try
		{
			StudyAttribute studyAttribute = this
					.getStudyAttributeByName(studyAttributeName);

			if (study != null && studyAttribute != null)
			{
				Integer studyId = study.getStudyId();
				Integer studyAttributeId = studyAttribute.getStudyAttributeId();

				String sql = "select * from chica_study_attribute_value where study_id=? and study_attribute_id=? and retired=?";
				SQLQuery qry = this.sessionFactory.getCurrentSession()
						.createSQLQuery(sql);

				qry.setInteger(0, studyId);
				qry.setInteger(1, studyAttributeId);
				qry.setBoolean(2, false);
				qry.addEntity(StudyAttributeValue.class);

				List<StudyAttributeValue> list = qry.list();

				if (list != null && list.size() > 0)
				{
					return list.get(0);
				}

			}
			return null;
		} catch (Exception e)
		{
			this.LOG.error(Util.getStackTrace(e));
		}
		return null;
	}
	
	/**
	 * @see org.openmrs.module.chica.db.ChicaDAO#getStudyAttributeByName(java.lang.String, boolean)
	 */
	public StudyAttribute getStudyAttributeByName(String studyAttributeName, boolean includeRetired)
	{
		try
		{
			String sql = "select * from chica_study_attribute "
					+ "where name=? and retired=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setString(0, studyAttributeName);
			qry.setBoolean(1, includeRetired);
			qry.addEntity(StudyAttribute.class);

			List<StudyAttribute> list = qry.list();

			if (list != null && list.size() > 0)
			{
				return list.get(0);
			}
			return null;
		} catch (Exception e)
		{
			this.LOG.error(Util.getStackTrace(e));
		}
		return null;
	}

	public List<StudyAttributeValue> getStudyAttributeValue(Study study,
			String studyAttributeName, boolean includeRetired)
	{
		try
		{
			StudyAttribute studyAttribute = this
					.getStudyAttributeByName(studyAttributeName);

			if (study != null && studyAttribute != null)
			{
				Integer studyId = study.getStudyId();
				Integer studyAttributeId = studyAttribute.getStudyAttributeId();

				String sql = "select * from chica_study_attribute_value where study_id=? and study_attribute_id=? and retired=?";
				SQLQuery qry = this.sessionFactory.getCurrentSession()
						.createSQLQuery(sql);

				qry.setInteger(0, studyId);
				qry.setInteger(1, studyAttributeId);
				qry.setBoolean(2, includeRetired);
				qry.addEntity(StudyAttributeValue.class);

				List<StudyAttributeValue> list = qry.list();

				if (list != null && list.size() > 0)
				{
					return list;
				}

			}
			return null;
		} catch (Exception e)
		{
			this.LOG.error(Util.getStackTrace(e));
		}
		return null;
	}

	public String getInsCategoryByCarrier(String carrierCode, String sendingFacility,String sendingApplication)
	{
		try
		{
			String sql = "select distinct category from chica_insurance_mapping where carrier_code=? and sending_application=? and sending_facility=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setString(0, carrierCode);
			qry.setString(1, sendingFacility);
			qry.setString(2, sendingApplication);
			qry.addScalar("category");
			return (String) qry.uniqueResult();
		} catch (Exception e)
		{
			this.LOG.error(Util.getStackTrace(e));
		}
		return null;
	}

	public String getInsCategoryByInsCode(String insCode, String sendingFacility,String sendingApplication)
	{
		try
		{
			String sql = "select distinct category from chica_insurance_mapping where ins_code=? and sending_application=? and sending_facility=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.addScalar("category");
			qry.setString(0, insCode);
			qry.setString(1, sendingFacility);
			qry.setString(2, sendingApplication);
			List<String> list = qry.list();
			// if result is not unique, return null
			if (list.size() == 1){
				return (String) list.get(0);
			}
		} catch (Exception e)
		{
			this.LOG.error(Util.getStackTrace(e));
		}
		return null;
	}

	public String getInsCategoryByName(String insuranceName, String sendingFacility,String sendingApplication)
	{
		try
		{
			String sql = "select distinct category from chica_insurance_mapping where ins_name=? and sending_application=? and sending_facility=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.addScalar("category");
			qry.setString(0, insuranceName);
			qry.setString(1, sendingFacility);
			qry.setString(2, sendingApplication);

			return (String) qry.uniqueResult();
		} catch (Exception e)
		{
			this.LOG.error(Util.getStackTrace(e));
		}
		return null;
	}
	public List<String> getInsCategories()
	{
		try
		{
			String sql = "select distinct category from chica_insurance_mapping " +
			"where category is not null and category <> '' order by category";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.addScalar("category");

			List<String> list = qry.list();
			ArrayList<String> categories = new ArrayList<String>();
			for (String currResult : list)
			{
				categories.add(currResult);
			}

			return categories;
		} catch (Exception e)
		{
			this.LOG.error(Util.getStackTrace(e));
		}
		return null;
	}

	public PatientFamily getPatientFamily(Integer patientId)
	{
		try
		{
			String sql = "select * from chica_patient_family where patient_id=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setInteger(0, patientId);
			qry.addEntity(PatientFamily.class);
			return (PatientFamily) qry.uniqueResult();
		} catch (Exception e)
		{
			this.LOG.error(Util.getStackTrace(e));
		}
		return null;
	}

	public Obs getStudyArmObs(Integer familyId, Concept studyConcept)
	{
		try
		{
			String sql = "select * from chica_patient_family where family_id=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setInteger(0, familyId);
			qry.addEntity(PatientFamily.class);
			List<PatientFamily> patientFamilies = qry.list();
			ObsService obsService = Context.getObsService();
			PatientService patientService = Context.getPatientService();

			if (patientFamilies != null && patientFamilies.size() > 0)
			{
				PatientFamily patientFamily = patientFamilies.get(0);
				Integer patientId = patientFamily.getPatientId();
				Patient patient = patientService.getPatient(patientId);
				List<Person> persons = new ArrayList<Person>();
				persons.add(patient);
				List<Concept> questions = new ArrayList<Concept>();
				questions.add(studyConcept);
				List<Obs> obs = obsService.getObservations(persons, null,
						questions, null, null, null, null, null, null, null,
						null, false);

				if (obs != null && obs.size() > 0)
				{
					return obs.get(0);
				}
			}
			return null;
		} catch (Exception e)
		{
			this.LOG.error(Util.getStackTrace(e));
		}
		return null;
	}

	public Family getFamilyByAddress(String address)
	{
		try
		{
			String sql = "select * from chica_family where street_address=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setString(0, address);
			qry.addEntity(Family.class);
			return (Family) qry.uniqueResult();
		} catch (Exception e)
		{
			this.LOG.error(Util.getStackTrace(e));
		}
		return null;
	}

	public Family getFamilyByPhone(String phone)
	{
		try
		{
			String sql = "select * from chica_family where phone_num=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setString(0, phone);
			qry.addEntity(Family.class);
			return (Family) qry.uniqueResult();
		} catch (Exception e)
		{
			this.LOG.error(Util.getStackTrace(e));
		}
		return null;
	}

	public void savePatientFamily(PatientFamily patientFamily)
	{
		try
		{
			this.sessionFactory.getCurrentSession().save(patientFamily);
		} catch (Exception e)
		{
			this.LOG.error(Util.getStackTrace(e));
		}
	}

	public void saveFamily(Family family)
	{
		try
		{
			this.sessionFactory.getCurrentSession().save(family);
		} catch (Exception e)
		{
			this.LOG.error(Util.getStackTrace(e));
		}
	}

	public void updateFamily(Family family)
	{
		try
		{
			this.sessionFactory.getCurrentSession().update(family);
		} catch (Exception e)
		{
			this.LOG.error(Util.getStackTrace(e));
		}
	}

	public List<Chica1Patient> getChica1Patients()
	{
		try
		{
			String sql = "select * from chica1_patient where skip_load_reason is null";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.addEntity(Chica1Patient.class);
			return qry.list();
		} catch (Exception e)
		{
			this.LOG.error(Util.getStackTrace(e));
		}
		return null;
	}

	public List<Chica1Appointment> getChica1AppointmentsByPatient(
			Integer patientId)
	{
		try
		{
			String sql = "select * from chica1_appointments "
					+ "where patient_id=? and skip_load_reason is null";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setInteger(0, patientId);
			qry.addEntity(Chica1Appointment.class);
			return qry.list();
		} catch (Exception e)
		{
			this.LOG.error(Util.getStackTrace(e));
		}
		return null;
	}

	public List<Chica1Appointment> getChica1AppointmentsByDate(
			Integer patientId, String date)
	{
		try
		{
			String sql = "select * from chica1_appointments "
					+ "where patient_id=? and date(substr(date_of_appt,1, length(date_of_appt)-3)) = "
					+ "date(substr(?,1, length(?)-3)) ";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setInteger(0, patientId);
			qry.setString(1, date);
			qry.setString(2, date);
			qry.addEntity(Chica1Appointment.class);
			return qry.list();
		} catch (Exception e)
		{
			this.LOG.error(Util.getStackTrace(e));
		}
		return null;
	}

	public List<Chica1PatientObsv> getChicaPatientObsByPSF(Integer psfId,
			Integer patientId)
	{
		try
		{
			String sql = "select openmrs_obs_id,patient_id,date_stamp,obsv_val,obsv_id,"
					+ "substring(obsv_source,9) as obsv_source,id_num,skip_load_reason from chica1_patient_obsv a "
					+ "where patient_id=? and obsv_source like 'PSF ID:%' and substring(obsv_source,9)=? and openmrs_obs_id is null and skip_load_reason is null";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setInteger(0, patientId);
			qry.setString(1, String.valueOf(psfId));
			qry.addEntity(Chica1PatientObsv.class);
			return qry.list();
		} catch (Exception e)
		{
			this.LOG.error(Util.getStackTrace(e));
		}
		return null;
	}

	public List<Chica1PatientObsv> getChicaPatientObsByPWS(Integer pwsId,
			Integer patientId)
	{
		try
		{
			String sql = "select openmrs_obs_id,patient_id,date_stamp,obsv_val,obsv_id,"
					+ "substring(obsv_source,9) obsv_source, id_num,skip_load_reason from chica1_patient_obsv a "
					+ "where patient_id=? and obsv_source like 'PWS ID:%' and substring(obsv_source,9)=? and openmrs_obs_id is null and skip_load_reason is null";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setInteger(0, patientId);
			qry.setString(1, String.valueOf(pwsId));
			qry.addEntity(Chica1PatientObsv.class);
			return qry.list();
		} catch (Exception e)
		{
			this.LOG.error(Util.getStackTrace(e));
		}
		return null;
	}

	public List<Chica1PatientObsv> getUnloadedChicaPatientObs(
			Integer patientId, String date)
	{
		try
		{
			String sql = "select * from chica1_patient_obsv a "
					+ "where patient_id=? and date(substr(date_stamp,1, length(date_stamp)-3)) = "
					+ "date(substr(?,1, length(?)-3))  and openmrs_obs_id is null and skip_load_reason is null";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setInteger(0, patientId);
			qry.setString(1, date);
			qry.setString(2, date);
			qry.addEntity(Chica1PatientObsv.class);
			return qry.list();
		} catch (Exception e)
		{
			this.LOG.error(Util.getStackTrace(e));
		}
		return null;
	}

	public void updateChica1Patient(Chica1Patient patient)
	{
		try
		{
			this.sessionFactory.getCurrentSession().update(patient);
		} catch (Exception e)
		{
			this.LOG.error(Util.getStackTrace(e));
		}
	}

	public void updateChica1Appointment(Chica1Appointment appointment)
	{
		try
		{
			this.sessionFactory.getCurrentSession().update(appointment);
		} catch (Exception e)
		{
			this.LOG.error(Util.getStackTrace(e));
		}
	}

	public Integer getHighBP(Integer ageInYears, String sex,
			Integer bpPercentile, String bpType, Integer heightPercentile)
	{
		try
		{
			String bpColumn = "Systolic";

			if (bpType.equalsIgnoreCase("diastolic"))
			{
				bpColumn = "Diastolic";
			}
			
			StringBuilder sql = new StringBuilder("select ")
			        .append(bpColumn)
			        .append("_HT")
			        .append(heightPercentile)
			        .append(" from chica_high_bp where Age=? and Sex=?")
			        .append(" and BP_Percentile=?");
			
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql.toString());
			qry.setInteger(0, ageInYears);
			qry.setString(1, sex);
			qry.setInteger(2, bpPercentile);
			qry.addScalar(bpColumn + "_HT" + heightPercentile);
			return (Integer) qry.uniqueResult();
		} catch (Exception e)
		{
			this.LOG.error(Util.getStackTrace(e));
		}
		return null;
	}

	public String getDDSTLeaf(String category, Integer ageInDays)
	{
		try
		{
			String sql = "select * from chica_ddst where category=? and cutoff_age <= ? order by cutoff_age desc";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setString(0, category);
			qry.setInteger(1, ageInDays);
			qry.addEntity(DDST_Milestone.class);
			if (qry.list().size() > 0)
			{
				DDST_Milestone milestone = (DDST_Milestone) qry.list().get(0);
				return milestone.getMilestone();
			}
		} catch (Exception e)
		{
			this.LOG.error(Util.getStackTrace(e));
		}
		return null;
	}
	
	
	
	public ChicaHL7Export insertEncounterToHL7ExportQueue(ChicaHL7Export export){
		sessionFactory.getCurrentSession().saveOrUpdate(export);
		return export;
	}
	
	public List <ChicaHL7Export> getPendingHL7Exports(){
		
		
		SQLQuery qry = this.sessionFactory.getCurrentSession()
			.createSQLQuery("select * from chica_hl7_export " +
		" where date_processed is null and voided = 0 and status = 1");

		qry.addEntity(ChicaHL7Export.class);
		List <ChicaHL7Export> exports = qry.list();
		return exports;
	}

	
	public void saveChicaHL7Export(ChicaHL7Export export) {
		this.sessionFactory.getCurrentSession().saveOrUpdate(export);
		return;
	}
	
	public List<ChicaHL7Export> getPendingHL7ExportsByEncounterId(Integer encounterId){
		SQLQuery qry = this.sessionFactory.getCurrentSession()
		.createSQLQuery("select * from chica_hl7_export where encounter_id = ? " + 
				" and date_processed is null and voided = 0 order by date_inserted desc");
		qry.setInteger(0, encounterId);
		qry.addEntity(ChicaHL7Export.class);
		List <ChicaHL7Export> exports = qry.list();
		return exports;
	}
	
	public List<PatientState> getReprintRescanStatesByEncounter(Integer encounterId, Date optionalDateRestriction, 
			Integer locationTagId,Integer locationId){
		
		try
		{
			
			String dateRestriction = "";
			if (optionalDateRestriction != null)
			{
				dateRestriction = " and start_time >= ?";
			} 
			
			String sql = "select * from chirdlutilbackports_patient_state a "+
						"inner join chirdlutilbackports_session b on a.session_id=b.session_id where state in ("+
						"select state_id from chirdlutilbackports_state where state_action_id in ("+
						"select state_action_id from chirdlutilbackports_state_action where action_name in ('RESCAN','REPRINT')) "+
						") "+
						"and encounter_id=? and retired=? and location_tag_id=? and location_id=? "+dateRestriction;
			
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			
			qry.setInteger(0, encounterId);
			qry.setBoolean(1, false);
			qry.setInteger(2, locationTagId);
			qry.setInteger(3, locationId);
			
			if (optionalDateRestriction != null)
			{
				qry.setDate(4, optionalDateRestriction);
			}
			
			qry.addEntity(PatientState.class);
			return qry.list();
		} catch (Exception e)
		{
			LOG.error(Util.getStackTrace(e));
		}
		return null;
	}
	
	public Chica1Appointment getChica1AppointmentByEncounterId(Integer encId){
		Chica1Appointment appt = null;
		try {
			String sql = "select * from chica1_appointments where openmrs_encounter_id = ?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
			.createSQLQuery(sql);
			qry.setInteger(0, encId);
			qry.addEntity(Chica1Appointment.class);
			appt = (Chica1Appointment) qry.uniqueResult();
		} catch (HibernateException e) {
			LOG.error(Util.getStackTrace(e));
		}
		
		return appt;
	}
	
	public void  saveHL7ExportMap (ChicaHL7ExportMap map){
		try
		{
			this.sessionFactory.getCurrentSession().save(map);
		} catch (Exception e)
		{
			this.LOG.error(Util.getStackTrace(e));
		}
	}
	
	public ChicaHL7ExportMap getChicaExportMapByQueueId(Integer queueId){
		try {
			SQLQuery qry = this.sessionFactory.getCurrentSession()
			.createSQLQuery("select * from chica_hl7_export_map " +
			" where hl7_export_queue_id = ?");
			qry.setInteger(0, queueId);
			qry.addEntity(ChicaHL7ExportMap.class);
			List<ChicaHL7ExportMap> list = qry.list();
			if (list != null && list.size() > 0) {
				return list.get(0);
			}
		}catch (Exception e) {
			LOG.error("Exception in getChicaExportMapByQueueId() (queueId: " + queueId + ")", e);
		}
		return null;
	}
	
	public ChicaHL7ExportStatus getChicaExportStatusByName (String name){
		/*try {
			SQLQuery qry = this.sessionFactory.getCurrentSession()
			.createSQLQuery("select * from chica_hl7_export_status " +
			" where name = ?");
			qry.setString(0, name);
			qry.addEntity(ChicaHL7ExportStatus.class);
			List<ChicaHL7ExportStatus> list = qry.list();
			if (list != null && list.size() > 0) {
				return list.get(0);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;*/
		
		
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(ChicaHL7ExportStatus.class).add(
			    Restrictions.eq("name", name));
			try {
				if (crit.list().size() < 1) {
					LOG.warn("No export status found with name: " + name);
					return null;
				}
			}catch (Exception e){
			    LOG.error("Exception in getChicaExportStatusByName() + (name: " + name + ")", e);
			}
			return (ChicaHL7ExportStatus) crit.list().get(0);
	}
	
	public ChicaHL7ExportStatus getChicaExportStatusById (Integer id){
		
		try {
			SQLQuery qry = this.sessionFactory.getCurrentSession()
			.createSQLQuery("select * from chica_hl7_export_status " +
			" where hl7_export_status_id = ?");
			qry.setInteger(0, id);
			qry.addEntity(ChicaHL7ExportStatus.class);
			List<ChicaHL7ExportStatus> list = qry.list();
			if (list != null && list.size() > 0) {
				return list.get(0);
			}
		}catch (Exception e) {
		    LOG.error("Exception in getChicaExportStatusById() (id: " + id + ")", e);
		}
		return null;
		
	}
	
	public List<Object[]> getFormsPrintedByWeek(String formName, String locationName) {
		try {
			LocationService locationService = Context.getLocationService();
			Integer locationId = locationService.getLocation(locationName).getLocationId();
			SQLQuery qry = this.sessionFactory.getCurrentSession().createSQLQuery(
			    "select start_date, end_date,count(*) as count from ( "
			            + "SELECT form_name,form_instance_id,DATE_FORMAT(DATE_SUB(printed_timestamp,"
			            + "INTERVAL  DAYOFWEEK(printed_timestamp)+2  DAY),'%Y-%m-%d') as start_date,"
			            + "DATE_FORMAT(DATE_SUB(printed_timestamp,INTERVAL  DAYOFWEEK(printed_timestamp)-4 DAY)"
			            + ",'%Y-%m-%d') as end_date,location_id from (select form_name, form_instance_id, "
			            + "max(printed_timestamp) as printed_timestamp,max(scanned_timestamp) as "
			            + "scanned_timestamp,location_id from atd_statistics where form_name=? and location_id=? and printed_timestamp is not null group by form_name,"
			            + "form_instance_id,location_id) a "
			            + ")a group by start_date,end_date order by start_date desc,end_date desc");
			qry.setString(0, formName);
			qry.setInteger(1, locationId);
			return qry.list();
		}
		catch (Exception e) {
		    LOG.error("Exception in getFormsPrintedByWeek() (formName: " + formName + " locationName: " + locationName + ")", e);
		}
		return null;
	}
	
	public List<Object[]> getFormsScannedByWeek(String formName, String locationName) {
		try {
			LocationService locationService = Context.getLocationService();
			Integer locationId = locationService.getLocation(locationName).getLocationId();
			SQLQuery qry = this.sessionFactory.getCurrentSession().createSQLQuery(
			    "select start_date, end_date,count(*) as count from ( "
			            + "SELECT form_name,form_instance_id,DATE_FORMAT(DATE_SUB(printed_timestamp,"
			            + "INTERVAL  DAYOFWEEK(printed_timestamp)+2  DAY),'%Y-%m-%d') as start_date,"
			            + "DATE_FORMAT(DATE_SUB(printed_timestamp,INTERVAL  DAYOFWEEK(printed_timestamp)-4 DAY)"
			            + ",'%Y-%m-%d') as end_date,scanned_timestamp,location_id from (select form_name, form_instance_id, "
			            + "max(printed_timestamp) as printed_timestamp,max(scanned_timestamp) as "
			            + "scanned_timestamp,location_id from atd_statistics where form_name=? and location_id=? "+
			            "and printed_timestamp is not null and scanned_timestamp is not null group by form_name,"
			            + "form_instance_id,location_id) a "
			            + ")a group by start_date,end_date order by start_date desc,end_date desc");
			qry.setString(0, formName);
			qry.setInteger(1, locationId);
			return qry.list();
		}
		catch (Exception e) {
		    LOG.error("Exception in getFormsScannedByWeek() (formName: " + formName + " locationName: " + locationName + ")", e);
		}
		return null;
	}
	
	public List<Object[]> getFormsScannedAnsweredByWeek(String formName, String locationName) {
		try {
			LocationService locationService = Context.getLocationService();
			Integer locationId = locationService.getLocation(locationName).getLocationId();
			SQLQuery qry = this.sessionFactory.getCurrentSession().createSQLQuery(
			    "select start_date, end_date,count(*) as count from ( "
			            + "SELECT form_name,form_instance_id,DATE_FORMAT(DATE_SUB(printed_timestamp,"
			            + "INTERVAL  DAYOFWEEK(printed_timestamp)+2  DAY),'%Y-%m-%d') as start_date,"
			            + "DATE_FORMAT(DATE_SUB(printed_timestamp,INTERVAL  DAYOFWEEK(printed_timestamp)-4 DAY)"
			            + ",'%Y-%m-%d') as end_date,scanned_timestamp,location_id from (select form_name, form_instance_id, "
			            + "max(printed_timestamp) as printed_timestamp,max(scanned_timestamp) as "
			            + "scanned_timestamp,location_id from atd_statistics where answer is "+
			            "not null and answer not in ('NoAnswer') and form_name=? and location_id=? "+
			            "and printed_timestamp is not null and scanned_timestamp is not null group by form_name,"
			            + "form_instance_id,location_id) a "
			            + ")a group by start_date,end_date order by start_date desc,end_date desc");
			qry.setString(0, formName);
			qry.setInteger(1, locationId);
			return qry.list();
		}
		catch (Exception e) {
		    LOG.error("Exception in getFormsScannedAnsweredByWeek() (formName: " + formName + " locationName: " + locationName + ")", e);
		}
		return null;
	}
	
	public List<Object[]> getFormsScannedAnythingMarkedByWeek(String formName, String locationName) {
		try {
			LocationService locationService = Context.getLocationService();
			Integer locationId = locationService.getLocation(locationName).getLocationId();
			SQLQuery qry = this.sessionFactory.getCurrentSession().createSQLQuery(
			    "select start_date, end_date,count(*) as count from ( "
			            + "SELECT form_name,form_instance_id,DATE_FORMAT(DATE_SUB(printed_timestamp,"
			            + "INTERVAL  DAYOFWEEK(printed_timestamp)+2  DAY),'%Y-%m-%d') as start_date,"
			            + "DATE_FORMAT(DATE_SUB(printed_timestamp,INTERVAL  DAYOFWEEK(printed_timestamp)-4 DAY)"
			            + ",'%Y-%m-%d') as end_date,scanned_timestamp,location_id from (select form_name, form_instance_id, "
			            + "max(printed_timestamp) as printed_timestamp,max(scanned_timestamp) as "
			            + "scanned_timestamp,a.location_id from atd_statistics a inner join obs e "+
			            "on a.obsv_id=e.obs_id where form_name=? and a.location_id=? "+
			            "and printed_timestamp is not null and scanned_timestamp is not null group by form_name,"
			            + "form_instance_id,location_id) a "
			            + ")a group by start_date,end_date order by start_date desc,end_date desc");
			qry.setString(0, formName);
			qry.setInteger(1, locationId);
			return qry.list();
		}
		catch (Exception e) {
		    LOG.error("Exception in getFormsScannedAnythingMarkedByWeek() (formName: " + formName + " locationName: " + locationName + ")", e);
		}
		return null;
	}

	public List<Object[]> getQuestionsScanned(String formName, String locationName) {
		try {
			LocationService locationService = Context.getLocationService();
			Integer locationId = locationService.getLocation(locationName).getLocationId();
			SQLQuery qry = this.sessionFactory.getCurrentSession().createSQLQuery(
			    "select start_date, end_date,count(*) as count from ( "+
			    "SELECT form_name,form_instance_id,rule_id,DATE_FORMAT(DATE_SUB(printed_timestamp, "+
			    "INTERVAL  DAYOFWEEK(printed_timestamp)+2  DAY),'%Y-%m-%d') as start_date, "+
			    "DATE_FORMAT(DATE_SUB(printed_timestamp,INTERVAL  DAYOFWEEK(printed_timestamp)-4 DAY) "+
			    ",'%Y-%m-%d') as end_date,scanned_timestamp,location_id from (select form_name, "+
			    "form_instance_id,rule_id,max(printed_timestamp) as printed_timestamp,"+
			    "max(scanned_timestamp) as scanned_timestamp,location_id from atd_statistics "+
			    "where rule_id is not null and form_name=? and location_id=? "+
			    "and printed_timestamp is not null and scanned_timestamp is not null group by form_name, "+
			    "form_instance_id,rule_id,location_id) a)a group by start_date,end_date "+
			    "order by start_date desc,end_date desc");
			qry.setString(0, formName);
			qry.setInteger(1, locationId);
			return qry.list();
		}
		catch (Exception e) {
		    LOG.error("Exception in getQuestionsScanned() (formName: " + formName + " locationName: " + locationName + ")", e);
		}
		return null;
	}
	
	public List<Object[]> getQuestionsScannedAnswered(String formName, String locationName) {
		try {
			LocationService locationService = Context.getLocationService();
			Integer locationId = locationService.getLocation(locationName).getLocationId();
			SQLQuery qry = this.sessionFactory.getCurrentSession().createSQLQuery(
			    "select start_date, end_date,count(*) as count from ( "
			            + "SELECT form_name,form_instance_id,rule_id,DATE_FORMAT(DATE_SUB(printed_timestamp,"
			            + "INTERVAL  DAYOFWEEK(printed_timestamp)+2  DAY),'%Y-%m-%d') as start_date,"
			            + "DATE_FORMAT(DATE_SUB(printed_timestamp,INTERVAL  DAYOFWEEK(printed_timestamp)-4 DAY)"
			            + ",'%Y-%m-%d') as end_date,scanned_timestamp,location_id from (select form_name, form_instance_id,rule_id, "
			            + "max(printed_timestamp) as printed_timestamp,max(scanned_timestamp) as "
			            + "scanned_timestamp,location_id from atd_statistics where rule_id is not null "+
			            "and answer is not null and answer not in ('NoAnswer') group by form_name,"
			            + "form_instance_id,rule_id,location_id) a where form_name=? and location_id=? "+
			            "and printed_timestamp is not null and scanned_timestamp is not null"
			            + ")a group by start_date,end_date order by start_date desc,end_date desc");
			qry.setString(0, formName);
			qry.setInteger(1, locationId);
			return qry.list();
		}
		catch (Exception e) {
		    LOG.error("Exception in getQuestionsScannedAnswered() (formName: " + formName + " locationName: " + locationName + ")", e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Encounter> getEncountersForEnrolledPatients(Concept concept,
			Date startDateTime, Date endDateTime){
	
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Encounter.class);
		criteria.createAlias("obs", "obsv")
			.add(Restrictions.eq("obsv.concept", concept));
		criteria.add(Restrictions.between("encounterDatetime", startDateTime, endDateTime ));

		return criteria.list();
	}
	
	/** (non-Javadoc)
	 * @see org.openmrs.module.chica.db.ChicaDAO#getEncountersForEnrolledPatientsExcludingConcepts(org.openmrs.Concept, org.openmrs.Concept, java.util.Date, java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	public List<Encounter> getEncountersForEnrolledPatientsExcludingConcepts(Concept includeConcept, Concept excludeConcept,
		Date startDateTime, Date endDateTime){
		
		Criteria criteria = null;
		
		DetachedCriteria exclusionCriteria= DetachedCriteria.forClass(Obs.class, "obsv")
				.add(Restrictions.eq("obsv.concept", excludeConcept))
				.add(Restrictions.eq("obsv.voided", false))
				.setProjection(Projections.distinct(Projections.property("obsv.encounter")));
		
		
		criteria = sessionFactory.getCurrentSession().createCriteria(Encounter.class, "en")
				.createAlias("en.obs", "obsv2")
				.add(Restrictions.eq("obsv2.concept", includeConcept));
				if (startDateTime != null ){
					criteria.add(Restrictions.ge("en.encounterDatetime", startDateTime));
				}
				if (endDateTime != null ){
					criteria.add(Restrictions.le("en.encounterDatetime", endDateTime));
				}
				criteria.add(Property.forName("en.encounterId").notIn(exclusionCriteria))
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.addOrder(Order.asc("en.encounterDatetime"));
				
		return criteria.list();
	}
	


	/**
	 * @see org.openmrs.module.chica.db.ChicaDAO#getStudySubject(org.openmrs.Patient, org.openmrs.module.chica.hibernateBeans.Study)
	 */
    @SuppressWarnings("unchecked")
    public StudySubject getStudySubject(Patient patient, Study study) {
    	if (patient == null || study == null) {
    		return null;
    	}
    	
    	Session session = sessionFactory.getCurrentSession();
    	Criteria criteria = session.createCriteria(StudySubject.class);
    	criteria.add(Restrictions.eq("patient", patient));
    	criteria.add(Restrictions.eq("study", study));

		List<StudySubject> list = criteria.list();
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		
		StudySubject subject = new StudySubject();
		subject.setPatient(patient);
		subject.setStudy(study);
		session.saveOrUpdate(subject);
		return subject;
    }
    
    /**
	 * @see org.openmrs.module.chica.db.ChicaDAO#getStudyByTitle(java.lang.String)
	 */
    @SuppressWarnings("unchecked")
    public Study getStudyByTitle(String studyTitle) {
		if (studyTitle == null) {
    		return null;
    	}
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Study.class);
		criteria.add(Restrictions.eq("title", studyTitle));
		criteria.add(Restrictions.eq("retired", false));
		
		List<Study> list = criteria.list();
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		
		return null;
    }

	/**
	 * @see org.openmrs.module.chica.db.ChicaDAO#getStudyByTitle(java.lang.String, boolean)
	 */
    @SuppressWarnings("unchecked")
    public Study getStudyByTitle(String studyTitle, boolean includeRetired) {
		if (studyTitle == null) {
    		return null;
    	}
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Study.class);
		criteria.add(Restrictions.eq("title", studyTitle));
		criteria.add(Restrictions.eq("retired", includeRetired));
		
		List<Study> list = criteria.list();
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		
		return null;
    }
    
    /**
     * DWE CHICA-761
     * @see org.openmrs.module.chica.db.ChicaDAO#getReprintRescanStatesBySessionId(Integer, Date, List, Integer)
     */
    public List<PatientState> getReprintRescanStatesBySessionId(Integer sessionId, Date optionalDateRestriction, List<Integer> locationTagIds,Integer locationId) throws HibernateException
    {
    	String dateRestriction = "";
    	if (optionalDateRestriction != null)
    	{
    		dateRestriction = " AND ps.start_time >= ?";
    	}
    	
    	String sql = "SELECT * from chirdlutilbackports_patient_state ps" +
    				" INNER JOIN chirdlutilbackports_state s ON ps.state = s.state_id" +
    				" INNER JOIN chirdlutilbackports_state_action sa ON s.state_action_id = sa.state_action_id" +
    				" WHERE ps.session_id =?" + 
    				" AND ps.retired =?" +  
    				" AND ps.location_id =?" + 
    				" AND (sa.action_name = 'RESCAN' OR sa.action_name = 'REPRINT')" + dateRestriction +
    				" AND ps.location_tag_id IN (:locationTagIds)";

    	SQLQuery qry = this.sessionFactory.getCurrentSession()
    			.createSQLQuery(sql);
    	
    	qry.setInteger(0, sessionId);
    	qry.setBoolean(1, false);
    	qry.setInteger(2, locationId);

    	if (optionalDateRestriction != null)
    	{
    		qry.setDate(3, optionalDateRestriction);
    	}
    	
    	qry.setParameterList("locationTagIds", locationTagIds);

    	qry.addEntity(PatientState.class);
    	return qry.list();
    }
    
    /**
	 * @see org.openmrs.module.chica.db.ChicaDAO#StudyAttribute(org.openmrs.module.chica.hibernateBeans.StudyAttribute)
	 */
    public StudyAttribute saveStudyAttribute(StudyAttribute studyAttribute) {
		try {
			this.sessionFactory.getCurrentSession().save(studyAttribute);
		} catch (Exception e){
			this.LOG.error(Util.getStackTrace(e));
		}
		return studyAttribute;
	}
    
    /**
	 * @see org.openmrs.module.chica.db.ChicaDAO#saveStudyAttributeValue(org.openmrs.module.chica.hibernateBeans.StudyAttributeValue)
	 */
    public StudyAttributeValue saveStudyAttributeValue(StudyAttributeValue studyAttributeValue) {
		try {
			this.sessionFactory.getCurrentSession().save(studyAttributeValue);
		} catch (Exception e){
			this.LOG.error(Util.getStackTrace(e));
		}
		return studyAttributeValue;
	}
    
    /**
	 * @see org.openmrs.module.chica.db.ChicaDAO#saveStudy(org.openmrs.module.chica.hibernateBeans.Study)
	 */
    public Study saveStudy(Study study) {
		try {
			this.sessionFactory.getCurrentSession().save(study);
		} catch (Exception e){
			this.LOG.error(Util.getStackTrace(e));
		}
		return study;
	}
  
}
