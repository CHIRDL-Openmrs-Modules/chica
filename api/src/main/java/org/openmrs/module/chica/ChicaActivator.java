package org.openmrs.module.chica;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.EncounterType;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.DaemonTokenAware;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.cache.ApplicationCacheManager;

/**
 * Checks that all global properties for this module have been set.
 * 
 * @author Tammy Dugan
 *
 */
public class ChicaActivator extends BaseModuleActivator implements DaemonTokenAware {

	private Log log = LogFactory.getLog(this.getClass());
	private static final String CHICA = "chica";
	private static String encounterTypeValue = "";
	

	/**
	 * @see org.openmrs.module.BaseModuleActivator#started()
	 */
	@Override
	public void started() {
		this.log.info("Starting Chica Module");
		
		//check that all the required global properties are set
		checkGlobalProperties();
		setEncounterTypeValue(CHICA);
		
		ApplicationCacheManager.getInstance(); // CHICA-963 Prevent errors on shutdown by initializing on startup. This has to be in the ChicaActivator since the cache depends on classes found in the chica module
	}

	private void checkGlobalProperties()
	{
		try
		{
			AdministrationService adminService = Context.getAdministrationService();
			 
			Iterator<GlobalProperty> properties = adminService
					.getAllGlobalProperties().iterator();
			GlobalProperty currProperty = null;
			String currValue = null;
			String currName = null;

			while (properties.hasNext())
			{
				currProperty = properties.next();
				currName = currProperty.getProperty();
				if (currName.startsWith(CHICA))
				{
					currValue = currProperty.getPropertyValue();
					if (currValue == null || currValue.length() == 0)
					{
						this.log.error("You must set a value for global property: "
								+ currName);
					}
				}
			}
		} catch (Exception e)
		{
			this.log.error("Error checking global properties for chica module");
			this.log.error(e.getMessage());
			this.log.error(Util.getStackTrace(e));
		}
	}
	
	/**
	 * @see org.openmrs.module.BaseModuleActivator#stopped()
	 */
	@Override
	public void stopped() {
		this.log.info("Shutting down Chica Module");
	}

	/**
	 * @see org.openmrs.module.DaemonTokenAware#setDaemonToken(org.openmrs.module.DaemonToken)
	 */
	@Override
	public void setDaemonToken(DaemonToken token) {
		org.openmrs.module.chica.util.Util.setDaemonToken(token);
	}
	

	private void setEncounterTypeValue(String name) {
		EncounterType encounterType = Context.getEncounterService().getEncounterType(name);
		if (encounterType != null) {
			encounterTypeValue = encounterType.getEncounterTypeId().toString();
		} 
	//	org.openmrs.module.chica.util.Util.setEncounterTypeValue(encounterTypeValue);
	}
	
	public static String getEncounterTypeValue() {
		return encounterTypeValue;
	}

}
