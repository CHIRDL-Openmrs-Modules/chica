package org.openmrs.module.chica;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;

/**
 * @author tmdugan
 * 
 */
public class KiteQueryThread implements Runnable
{
	private Log log = LogFactory.getLog(this.getClass());

	private String mrn = null;
	private QueryKiteException exception = null;
	private String responseString = null;

	public KiteQueryThread(String mrn)
	{
		this.mrn = mrn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		Context.openSession();
		try
		{
			AdministrationService adminService = Context.getAdministrationService();
			Context.authenticate(adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROPERTY_SCHEDULER_USERNAME), 
				adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROPERTY_SCHEDULER_PASSWORD));

			try
			{
				responseString = QueryKite.getMRFDump(this.mrn);
			
			} catch (QueryKiteException e)
			{
				this.exception = e;
			}
		} catch (Exception e)
		{

		}finally{
			Context.closeSession();
		}
	}

	public String getResponse()
	{
		return this.responseString;
	}

	public QueryKiteException getException()
	{
		return this.exception;
	}

	
}
