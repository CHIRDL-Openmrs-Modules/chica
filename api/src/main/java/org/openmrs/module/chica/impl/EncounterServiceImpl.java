package org.openmrs.module.chica.impl;

import org.openmrs.annotation.Authorized;
import org.openmrs.module.chica.service.EncounterService;

/**
 * Encounter-related services
 * 
 * @author Tammy Dugan
 * @version 1.0
 */
public class EncounterServiceImpl extends org.openmrs.api.impl.EncounterServiceImpl implements EncounterService
{
	org.openmrs.module.chica.db.EncounterDAO dao = null;
	
	/**
	 * 
	 */
	public EncounterServiceImpl()
	{
	}

	/**
	 * @param dao
	 */
	public void setChicaEncounterDAO(org.openmrs.module.chica.db.EncounterDAO dao)
	{
		this.dao = dao;
		super.setEncounterDAO(this.dao);
	}
	
	
}
