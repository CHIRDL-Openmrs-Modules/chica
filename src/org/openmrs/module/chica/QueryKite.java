package org.openmrs.module.chica;

/**
 * 
 */

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.module.chica.datasource.ObsChicaDatasource;
import org.openmrs.module.atd.hibernateBeans.ATDError;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.dss.util.IOUtil;
import org.openmrs.module.dss.util.Util;

/**
 * @author tmdugan
 * 
 */
public class QueryKite
{
	private static Log log = LogFactory.getLog(QueryKite.class);

	public static String queryKite(String mrn, String queryPrefix)
			throws QueryKiteException
	{
		AdministrationService adminService = Context.getAdministrationService();
		Integer timeout = 0;
		try
		{
			timeout = Integer.parseInt(adminService
					.getGlobalProperty("chica.kiteTimeout"));
			timeout = timeout * 1000; // convert seconds to
			// milliseconds
		} catch (NumberFormatException e)
		{
		}
		KiteQueryThread kiteQueryThread = new KiteQueryThread(mrn, queryPrefix);
		Thread thread = new Thread(kiteQueryThread);
		thread.start();
		long startTime = System.currentTimeMillis();

		while (true)
		{
			//processing is done
			if(!thread.isAlive()){
				//check for an exception
				if(kiteQueryThread.getException()!=null){
					log.error("Exception");
					throw kiteQueryThread.getException();
				}else{
					//return the response if no exception
					log.info("Success");
					return kiteQueryThread.getResponse();
				}
			}
			
			if ((System.currentTimeMillis() - startTime) > timeout)
			{
				//the timeout was exceeded so return null
				log.warn("Timeout exceeded.");
				return null;
			}
			try
			{
				Thread.sleep(100);// wait for a tenth of a second

			} catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
	}
	
	public static String aliasQuery(String mrn) throws  QueryKiteException
	{
		ATDService atdService = Context.getService(ATDService.class); 
		AdministrationService adminService = Context.getAdministrationService();
		String response = null;
		try
		{
		    response = queryKite(mrn, "FIND-ALIASES");
		} catch (QueryKiteException qke) { 	
			//if query kite fails, retry
			response = queryKite(mrn, "FIND-ALIASES");
			//if query fails again, throw QueryKiteException up to next level for processing
		}
		catch (Exception e)
		{
			ATDError error = new ATDError("Error", "Query Kite Connection"
					, e.getMessage()
					, Util.getStackTrace(e), new Date(), null);
			atdService.saveError(error);
		}
		
		//If the response is null this means the connection was broken
		//Try querying again
		if(response == null){
			response = queryKite(mrn, "FIND-ALIASES");
			if(response != null){
				log.info("Re-query of FIND-ALIASES for mrn: "+mrn+" successful");
			}else{
				ATDError error = new ATDError("Error", "Query Kite Connection"
					, "Re-query of FIND-ALIASES after message dropped for mrn: "+mrn+" failed"
					, null, new Date(), null);
			atdService.saveError(error);
			}
		}
		
		if (response != null)
		{
			// save alias query results to a file
			String aliasDirectory = IOUtil.formatDirectoryName(adminService
					.getGlobalProperty("chica.aliasArchiveDirectory"));
			
			if (aliasDirectory != null)
			{
			String filename = "r" + Util.archiveStamp() + "_" + mrn + ".txt";

			FileOutputStream aliasFile = null;
			
			try
			{
				aliasFile = new FileOutputStream(aliasDirectory
						+ "/" + filename);
			} catch (FileNotFoundException e1)
			{
				log.error("Could not find alias file: " + aliasDirectory + "/"
						+ filename);
			}
			if (aliasFile != null)
			{
				try
				{

					ByteArrayInputStream aliasInput = new ByteArrayInputStream(
							response.getBytes());
					IOUtil.bufferedReadWrite(aliasInput, aliasFile);
					aliasFile.flush();
					aliasFile.close();
				} catch (Exception e)
				{
					try
					{
						aliasFile.flush();
						aliasFile.close();
					} catch (Exception e1)
					{
					}
					log.error("There was an error writing the dump file");
					log.error(e.getMessage());
					log.error(Util.getStackTrace(e));
				}
			}
		}
		}

		return response;
	}

	public static String mrfQuery(String mrn,Integer patientId) throws  QueryKiteException
	{
		ATDService atdService = Context.getService(ATDService.class);
		AdministrationService adminService = Context.getAdministrationService();
				
		String response = null;
		long startTime = System.currentTimeMillis();
		log.info("Starting mrf Query");
		try
		{
		    response = queryKite(mrn, "ZET-MRF");
		} catch (QueryKiteException qke) { 	
			//if query kite fails, retry
			response = queryKite(mrn, "ZET_MRF");
			//if query fails again, throw QueryKiteException up to next level for processing
		}
		catch (Exception e)
		{
			ATDError error = new ATDError("Error", "Query Kite Connection"
					, e.getMessage()
					, Util.getStackTrace(e), new Date(), null);

			atdService.saveError(error);
		}
		
		//If the response is null this means the connection was broken
		//Try querying again
		if (response == null)
		{
			response = queryKite(mrn, "ZET-MRF");
			if (response != null)
			{
				log.info("Re-query of GET-MRF for mrn: " + mrn
						+ " successful");
			} else
			{
				ATDError error = new ATDError("Error",
						"Query Kite Connection",
						"Re-query of GET-MRF after message dropped for mrn: "
								+ mrn + " failed", null, new Date(), null);
				atdService.saveError(error);
			}
		}
		
		if (response != null)
		{
			// save mrf dump to a file
			String mrfDirectory = IOUtil.formatDirectoryName(adminService
					.getGlobalProperty("chica.mrfArchiveDirectory"));
			if (mrfDirectory != null)
			{
			String filename = "r" + Util.archiveStamp() + "_"+mrn+".hl7";

			FileOutputStream mrfDumpFile = null;
			try
			{
				mrfDumpFile = new FileOutputStream(
						mrfDirectory + "/" + filename);
			} catch (FileNotFoundException e1)
			{
				log.error("Couldn't find file: "+mrfDirectory + "/" + filename);
			}
			if (mrfDumpFile != null)
			{
				try
				{

					ByteArrayInputStream mrfDumpInput = new ByteArrayInputStream(
							response.getBytes());
					IOUtil.bufferedReadWrite(mrfDumpInput, mrfDumpFile);
					mrfDumpFile.flush();
					mrfDumpFile.close();
				} catch (Exception e)
				{
					try
					{
						mrfDumpFile.flush();
						mrfDumpFile.close();
					} catch (Exception e1)
					{
					}
					log.error("There was an error writing the dump file");
					log.error(e.getMessage());
					log.error(Util.getStackTrace(e));
				}
			}
			}
			log.info("Elapsed time for mrf Query is "+
					(System.currentTimeMillis()-startTime)/1000);
		
			startTime = System.currentTimeMillis();
			log.info("Starting mrf parsing");
			LogicService logicService = Context.getLogicService();

			ObsChicaDatasource xmlDatasource = (ObsChicaDatasource) logicService
					.getLogicDataSource("RMRS");

			xmlDatasource.parseHL7ToObs(response,patientId,mrn);
			log.info("Elapsed time for mrf parsing is "+
					(System.currentTimeMillis()-startTime)/1000);
		}
	
		return response;
	}
	
}
