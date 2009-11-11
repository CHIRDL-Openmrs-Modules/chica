package org.openmrs.module.chica.db.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.SessionFactory;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.chica.db.EncounterDAO;
import org.openmrs.module.chica.hibernateBeans.Encounter;

/**
 * @author Tammy Dugan
 * 
 */
public class HibernateEncounterDAO extends
		org.openmrs.api.db.hibernate.HibernateEncounterDAO implements
		EncounterDAO
{

	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;

	/**
	 * 
	 */
	public HibernateEncounterDAO()
	{
	}

	/**
	 * Set session factory
	 * 
	 * @param sessionFactory
	 */
	@Override
	public void setSessionFactory(SessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
		super.setSessionFactory(this.sessionFactory);
	}

	private ArrayList<org.openmrs.Encounter> convertOpenMRSEncToModuleEnc(
			Collection<org.openmrs.Encounter> openMRSEncounters)
	{
		ArrayList<org.openmrs.Encounter> moduleEncounters = new ArrayList<org.openmrs.Encounter>();
		org.openmrs.Encounter currOpenMRSEncounter = null;

		Iterator<org.openmrs.Encounter> iter = openMRSEncounters.iterator();

		while (iter.hasNext())
		{
			currOpenMRSEncounter = iter.next();
			moduleEncounters.add(this.getEncounter(currOpenMRSEncounter
					.getEncounterId()));
		}
		return moduleEncounters;
	}

	@Override
	public org.openmrs.Encounter getEncounter(Integer encounterId)
	{
		org.openmrs.Encounter encounter = (org.openmrs.Encounter) this.sessionFactory
				.getCurrentSession().get(Encounter.class, encounterId);

		if (encounter == null)
		{
			try
			{
				// try refreshing the session
				this.sessionFactory.getCurrentSession().clear();
				encounter = (org.openmrs.Encounter) this.sessionFactory
						.getCurrentSession().get(Encounter.class, encounterId);
			} catch (Exception e)
			{
				this.log.error(e.getMessage());
				this.log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
			}
		}

		return encounter;
	}

	@Override
	public List<org.openmrs.Encounter> getEncounters(Patient patient,
			Location location, Date fromDate, Date toDate,
			Collection<Form> enteredViaForms,
			Collection<EncounterType> encounterTypes, Collection<User> providers, boolean includeVoided)
	{
		Collection<org.openmrs.Encounter> openMRSEncounters = super
				.getEncounters(patient, location, fromDate, toDate,
						enteredViaForms, encounterTypes, providers, includeVoided);

		return convertOpenMRSEncToModuleEnc(openMRSEncounters);
	}

	@Override
	public List<org.openmrs.Encounter> getEncountersByPatientId(
			Integer patientId) throws DAOException
	{
		Collection<org.openmrs.Encounter> openMRSEncounters = super
				.getEncountersByPatientId(patientId);

		return convertOpenMRSEncToModuleEnc(openMRSEncounters);
	}

}
