package org.openmrs.module.chica.db.hibernate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.openmrs.Cohort;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.State;
import org.openmrs.module.chica.Percentile;
import org.openmrs.module.chica.db.ChicaDAO;
import org.openmrs.module.chica.hibernateBeans.Bmiage;
import org.openmrs.module.chica.hibernateBeans.Chica1Appointment;
import org.openmrs.module.chica.hibernateBeans.Chica1Patient;
import org.openmrs.module.chica.hibernateBeans.Chica1PatientObsv;
import org.openmrs.module.chica.hibernateBeans.ChicaError;
import org.openmrs.module.chica.hibernateBeans.ChicaHL7Export;
import org.openmrs.module.chica.hibernateBeans.DDST_Milestone;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.hibernateBeans.Family;
import org.openmrs.module.chica.hibernateBeans.Hcageinf;
import org.openmrs.module.chica.hibernateBeans.HighBP;
import org.openmrs.module.chica.hibernateBeans.InsuranceCategory;
import org.openmrs.module.chica.hibernateBeans.Lenageinf;
import org.openmrs.module.chica.hibernateBeans.LocationTagAttribute;
import org.openmrs.module.chica.hibernateBeans.LocationTagAttributeValue;
import org.openmrs.module.chica.hibernateBeans.OldRule;
import org.openmrs.module.chica.hibernateBeans.Statistics;
import org.openmrs.module.chica.hibernateBeans.PatientFamily;
import org.openmrs.module.chica.hibernateBeans.Study;
import org.openmrs.module.chica.hibernateBeans.StudyAttribute;
import org.openmrs.module.chica.hibernateBeans.StudyAttributeValue;
import org.openmrs.module.chica.hibernateBeans.Wtageinf;
import org.openmrs.module.dss.util.Util;

/**
 * Hibernate implementation of chica database methods.
 * 
 * @author Tammy Dugan
 * 
 */
public class HibernateChicaDAO implements ChicaDAO
{

	protected final Log log = LogFactory.getLog(getClass());

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

	public void addStatistics(Statistics statistics)
	{
		try
		{
			this.sessionFactory.getCurrentSession().save(statistics);
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
		}
	}

	public void updateStatistics(Statistics statistics)
	{
		try
		{
			this.sessionFactory.getCurrentSession().update(statistics);
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
		}
	}

	public List<Statistics> getStatByFormInstance(int formInstanceId,
			String formName)
	{
		try
		{
			String sql = "select * from chica_statistics where form_instance_id=? and form_name=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setInteger(0, formInstanceId);
			qry.setString(1, formName);
			qry.addEntity(Statistics.class);
			return qry.list();
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
		}
		return null;
	}

	public List<OldRule> getAllOldRules()
	{
		try
		{
			String sql = "select * from chica_old_rule";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.addEntity(OldRule.class);
			return qry.list();
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
		}
		return null;
	}

	public List<Statistics> getStatByIdAndRule(int formInstanceId, int ruleId,
			String formName)
	{
		try
		{
			String sql = "select * from chica_statistics where form_instance_id=? and rule_id=? and form_name=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setInteger(0, formInstanceId);
			qry.setInteger(1, ruleId);
			qry.setString(2, formName);
			qry.addEntity(Statistics.class);
			return qry.list();
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
		}
		return null;
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
			this.log.error(Util.getStackTrace(e));
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
			this.log.error(Util.getStackTrace(e));
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
			this.log.error(Util.getStackTrace(e));
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
			this.log.error(Util.getStackTrace(e));
		}
		return null;
	}

	public List<Study> getActiveStudies()
	{
		try
		{
			String sql = "select * from chica_study where status=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setInteger(0, 1);
			qry.addEntity(Study.class);
			return qry.list();
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
		}
		return null;
	}

	private StudyAttribute getStudyAttributeByName(String studyAttributeName)
	{
		try
		{
			String sql = "select * from chica_study_attribute "
					+ "where name=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setString(0, studyAttributeName);
			qry.addEntity(StudyAttribute.class);

			List<StudyAttribute> list = qry.list();

			if (list != null && list.size() > 0)
			{
				return list.get(0);
			}
			return null;
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
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

				String sql = "select * from chica_study_attribute_value where study_id=? and study_attribute_id=?";
				SQLQuery qry = this.sessionFactory.getCurrentSession()
						.createSQLQuery(sql);

				qry.setInteger(0, studyId);
				qry.setInteger(1, studyAttributeId);
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
			this.log.error(Util.getStackTrace(e));
		}
		return null;
	}

	public String getInsCategoryByCarrier(String carrierCode)
	{
		try
		{
			String sql = "select distinct category from chica_insurance_category where star_carrier_code=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setString(0, carrierCode);
			qry.addScalar("category");
			return (String) qry.uniqueResult();
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
		}
		return null;
	}

	public String getInsCategoryByInsCode(String insCode)
	{
		try
		{
			String sql = "select distinct category from chica_insurance_category where ins_code=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.addScalar("category");
			qry.setString(0, insCode);

			return (String) qry.uniqueResult();
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
		}
		return null;
	}

	public String getInsCategoryBySMS(String smsCode)
	{
		try
		{
			String sql = "select distinct category from chica_insurance_category where sms_code=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.addScalar("category");
			qry.setString(0, smsCode);

			return (String) qry.uniqueResult();
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
		}
		return null;
	}

	public List<String> getInsCategories()
	{
		try
		{
			String sql = "select distinct category from chica_insurance_category " +
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
			this.log.error(Util.getStackTrace(e));
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
			this.log.error(Util.getStackTrace(e));
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
			this.log.error(Util.getStackTrace(e));
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
			this.log.error(Util.getStackTrace(e));
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
			this.log.error(Util.getStackTrace(e));
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
			this.log.error(Util.getStackTrace(e));
		}
	}

	public void saveFamily(Family family)
	{
		try
		{
			this.sessionFactory.getCurrentSession().save(family);
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
		}
	}

	public void updateFamily(Family family)
	{
		try
		{
			this.sessionFactory.getCurrentSession().update(family);
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
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
			this.log.error(Util.getStackTrace(e));
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
			this.log.error(Util.getStackTrace(e));
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
			this.log.error(Util.getStackTrace(e));
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
			this.log.error(Util.getStackTrace(e));
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
			this.log.error(Util.getStackTrace(e));
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
			this.log.error(Util.getStackTrace(e));
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
			this.log.error(Util.getStackTrace(e));
		}
	}

	public void updateChica1Appointment(Chica1Appointment appointment)
	{
		try
		{
			this.sessionFactory.getCurrentSession().update(appointment);
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
		}
	}

	public void setChica1PatientObsvObsId(Chica1PatientObsv chica1PatientObsv)
	{
		try
		{
			Connection con = this.sessionFactory.getCurrentSession()
					.connection();
			String sql = "update chica1_patient_obsv set openmrs_obs_id=? where id_num=?";

			try
			{
				PreparedStatement stmt = con.prepareStatement(sql);
				stmt.setInt(1, chica1PatientObsv.getOpenmrsObsId());
				stmt.setInt(2, chica1PatientObsv.getIdNum());

				stmt.executeUpdate();
				stmt.close();
				con.commit();
			} catch (Exception e)
			{
				this.log.error(e.getMessage());
				this.log.error(Util.getStackTrace(e));
			}
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
		}
	}

	public String getObsvNameByObsvId(String obsvId)
	{
		try
		{
			Connection con = this.sessionFactory.getCurrentSession()
					.connection();
			String sql = "select obsv_name from chica1_obsv_dictionary where obsv_id = ?";

			try
			{
				PreparedStatement stmt = con.prepareStatement(sql);
				stmt.setString(1, obsvId);

				ResultSet rs = stmt.executeQuery();
				if (rs.next())
				{
					return rs.getString(1);
				}
				stmt.close();
			} catch (Exception e)
			{
				this.log.error(e.getMessage());
				this.log.error(Util.getStackTrace(e));
			}
			return null;
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
		}
		return null;
	}

	public void saveError(ChicaError error)
	{
		try
		{
			this.sessionFactory.getCurrentSession().save(error);
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
		}
	}

	public Integer getErrorCategoryIdByName(String name)
	{
		try
		{
			Connection con = this.sessionFactory.getCurrentSession()
					.connection();
			String sql = "select error_category_id from chica_error_category where name=?";
			try
			{
				PreparedStatement stmt = con.prepareStatement(sql);
				stmt.setString(1, name);
				ResultSet rs = stmt.executeQuery();
				if (rs.next())
				{
					return rs.getInt(1);
				}

			} catch (Exception e)
			{

			}
			return null;
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
		}
		return null;
	}

	public List<ChicaError> getChicaErrorsByLevel(String errorLevel,
			Integer sessionId)
	{
		try
		{
			String sql = "select * from chica_error where level=? and session_id=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setString(0, errorLevel);
			qry.setInteger(1, sessionId);
			qry.addEntity(ChicaError.class);
			return qry.list();
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
		}
		return null;
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

			String sql = "select " + bpColumn + "_HT" + heightPercentile
					+ " from chica_high_bp where Age=? and Sex=?"
					+ " and BP_Percentile=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setInteger(0, ageInYears);
			qry.setString(1, sex);
			qry.setInteger(2, bpPercentile);
			qry.addScalar(bpColumn + "_HT" + heightPercentile);
			return (Integer) qry.uniqueResult();
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
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
			this.log.error(Util.getStackTrace(e));
		}
		return null;
	}
	
	public List<Statistics> getStatsByEncounterForm(Integer encounterId,String formName)
	{
		try
		{
			String sql = "select * from chica_statistics where obsv_id is not null and encounter_id=? and form_name=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setInteger(0, encounterId);
			qry.setString(1, formName);
			qry.addEntity(Statistics.class);
			return qry.list();
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
		}
		return null;
	}
	
	public List<Statistics> getStatsByEncounterFormNotPrioritized(Integer encounterId,String formName)
	{
		try
		{
			String sql = "select * from chica_statistics where rule_id is null and obsv_id is not null and encounter_id=? and form_name=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setInteger(0, encounterId);
			qry.setString(1, formName);
			qry.addEntity(Statistics.class);
			return qry.list();
		} catch (Exception e)
		{
			this.log.error(Util.getStackTrace(e));
		}
		return null;
	}
	
	
	public void insertEncounterToHL7ExportQueue(ChicaHL7Export export){
		sessionFactory.getCurrentSession().saveOrUpdate(export);
		
	}
	
	public List <org.openmrs.Encounter> getEncountersPendingHL7Export(){
		
		SQLQuery qry = this.sessionFactory.getCurrentSession()
			.createSQLQuery("select * from encounter where encounter_id in " +
		"(select distinct encounter_id from chica_hl7_export" +
		" export where export.date_processed is null and export.voided = 0)");

		qry.addEntity(org.openmrs.Encounter.class);
		List <org.openmrs.Encounter> encounters = qry.list();
		return encounters;
	}

	
	public void saveChicaHL7ExportQueue(ChicaHL7Export export) {
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
	
	public List<String> getPrinterStations(){
		Query qry = this.sessionFactory.getCurrentSession()
			.createSQLQuery("select distinct name from atd_form_attribute where form_attribute_id in "
					+ " (select form_attribute_id from atd_form_attribute_value where form_id in "
					+ " (select form_id from form where name='PSF' and retired=0))"
					+ "and name like 'defaultPrinter%'");
		List<String> list =  qry.list();
		
		return list;
	}
	
	public List<PatientState> getReprintRescanStatesByEncounter(Integer encounterId, Date optionalDateRestriction){
		
		try
		{
			
			String dateRestriction = "";
			if (optionalDateRestriction != null)
			{
				dateRestriction = " and start_time >= ?";
			} 
			
			String sql = "select * from atd_patient_state a "+
						"inner join atd_session b on a.session_id=b.session_id where state in ("+
						"select state_id from atd_state where state_action_id in ("+
						"select state_action_id from atd_state_action where action_name in ('RESCAN','REPRINT')) "+
						") "+
						"and encounter_id=? and retired=? "+dateRestriction;
			
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			
			qry.setInteger(0, encounterId);
			qry.setBoolean(1, false);
			
			if (optionalDateRestriction != null)
			{
				qry.setDate(2, optionalDateRestriction);
			}
			
			qry.addEntity(PatientState.class);
			return qry.list();
		} catch (Exception e)
		{
			log.error(Util.getStackTrace(e));
		}
		return null;
	}
	
	public ArrayList<String> getImagesDirectory(String location){
		
		try
		{
			String sql = "select distinct value from atd_form_attribute_value where form_attribute_id in "
					+ "(select form_attribute_id from atd_form_attribute where name='imageDirectory')" 
					+ " && value like ?" ;
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setString(0, "%" + location + "%");
			List<String> list = qry.list();

			ArrayList<String> imagesDirectories = new ArrayList<String>();
			for (String currResult : list)
			{
				imagesDirectories.add(currResult);
			}

			return imagesDirectories;
		} catch (Exception e)
		{
			log.error(Util.getStackTrace(e));
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
			// TODO Auto-generated catch block
			log.error(Util.getStackTrace(e));
		}
		
		return appt;
	}
	public LocationTagAttributeValue getLocationTagAttributeValue(Integer locationTagId,
			String locationTagAttributeName)
	{
		try
		{
			LocationTagAttribute locationTagAttribute = this
					.getLocationTagAttributeByName(locationTagAttributeName);

			if (locationTagAttribute != null)
			{
				Integer locationTagAttributeId = locationTagAttribute.getLocationTagAttributeId();

				String sql = "select * from chica_location_tag_attribute_value where location_tag_id=? and location_tag_attribute_id=?";
				SQLQuery qry = this.sessionFactory.getCurrentSession()
						.createSQLQuery(sql);

				qry.setInteger(0, locationTagId);
				qry.setInteger(1, locationTagAttributeId);
				qry.addEntity(LocationTagAttributeValue.class);

				List<LocationTagAttributeValue> list = qry.list();

				if (list != null && list.size() > 0)
				{
					return list.get(0);
				}

			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	private LocationTagAttribute getLocationTagAttributeByName(String locationTagAttributeName)
	{
		try
		{
			String sql = "select * from chica_location_tag_attribute " + "where name=?";
			SQLQuery qry = this.sessionFactory.getCurrentSession()
					.createSQLQuery(sql);
			qry.setString(0, locationTagAttributeName);
			qry.addEntity(LocationTagAttribute.class);

			List<LocationTagAttribute> list = qry.list();

			if (list != null && list.size() > 0)
			{
				return list.get(0);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
