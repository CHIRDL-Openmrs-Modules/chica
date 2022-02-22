package org.openmrs.module.chica.db.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hibernate.SessionFactory;
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
	private static final Logger log = LoggerFactory.getLogger(HibernateEncounterDAO.class);
    private static final String ENCOUNTER_ENTITY_NAME = "chicaEncounter";	  
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
				.getCurrentSession().get("chicaEncounter", encounterId);

		if (encounter == null)
		{
			try
			{
				// try refreshing the session
				this.sessionFactory.getCurrentSession().clear();
				encounter = (org.openmrs.Encounter) this.sessionFactory
						.getCurrentSession().get("chicaEncounter", encounterId);
			} catch (Exception e)
			{
				log.error(e.getMessage());
				log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
			}
		}

		return encounter;
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
