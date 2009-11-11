package org.openmrs.module.chica;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.hibernateBeans.ATDError;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chirdlutil.util.Util;

/**
 * @author tmdugan
 * 
 */
public class KiteQueryThread implements Runnable
{
	private Log log = LogFactory.getLog(this.getClass());

	private String mrn = null;
	private String queryPrefix = null;
	private QueryKiteException exception = null;
	private String response = null;

	public KiteQueryThread(String mrn, String queryPrefix)
	{
		this.mrn = mrn;
		this.queryPrefix = queryPrefix;
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
			Context.authenticate(adminService
					.getGlobalProperty("scheduler.username"), adminService
					.getGlobalProperty("scheduler.password"));

			try
			{
				this.response = queryKite(this.mrn, this.queryPrefix);
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
		return this.response;
	}

	public QueryKiteException getException()
	{
		return this.exception;
	}

	private String queryKite(String mrn, String queryPrefix)
			throws QueryKiteException
	{
		AdministrationService adminService = Context.getAdministrationService();
		ChicaService chicaService = Context.getService(ChicaService.class);

		String host = adminService.getGlobalProperty("chica.kiteHost");
		if (host == null)
		{
			log.error("Could not query kite. No host provided.");
			return null;
		}
		Integer port = null;
		try
		{
			port = Integer.parseInt(adminService
					.getGlobalProperty("chica.kitePort"));
		} catch (NumberFormatException e)
		{
		}

		Integer timeout = null;
		try
		{
			timeout = Integer.parseInt(adminService
					.getGlobalProperty("chica.kiteTimeout"));
			timeout = timeout * 1000; // convert seconds to milliseconds
		} catch (NumberFormatException e)
		{
		}

		String response = null;
		String queryString = queryPrefix + "|" + mrn + "\n";

		KiteMessageHandler serverTest = new KiteMessageHandler(host, port,
				timeout);

		try
		{
			serverTest.openSocket();
			serverTest.sendMessage(queryString);
			response = serverTest.getMessage();

		} catch (Exception e)
		{
			ATDError error = new ATDError("Error", "Query Kite Connection",
					queryPrefix + ": " + e.getMessage(), Util.getStackTrace(e),
					new Date(), null);
			ATDService atdService = Context.getService(ATDService.class);
			atdService.saveError(error);
			throw new QueryKiteException("Query Kite Connection timed out",
					error);

		} finally
		{
			serverTest.closeSocket();
		}

		return response;
	}
}
