package org.openmrs.module.chica;

/**
 * 
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.module.chirdlutilbackports.datasource.ObsInMemoryDatasource;
import org.openmrs.module.chica.hl7.mrfdump.HL7ToObs;
import org.openmrs.module.chirdlutil.util.FileFilterByDate;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Error;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.Util;

/**
 * @author tmdugan
 * 
 */
public class QueryKite
{
	private static Log log = LogFactory.getLog(QueryKite.class);
	private static final String GLOBAL_PROPERTY_MRF_QUERY_TIMEOUT = "chica.kiteTimeout";

	public static String queryKite(String mrn)
			throws QueryKiteException
	{
		AdministrationService adminService = Context.getAdministrationService();
		// Set the default to 5 seconds.  We don't want it to last forever by setting it to 0.
		Integer timeout = 5000;
		try
		{
			timeout = Integer.parseInt( adminService.getGlobalProperty(GLOBAL_PROPERTY_MRF_QUERY_TIMEOUT));
			timeout = timeout * 1000; // convert seconds to
			// milliseconds
		} catch (NumberFormatException e)
		{
		}
		
		KiteQueryThread kiteQueryThread = new KiteQueryThread(mrn);
		Thread thread = new Thread(kiteQueryThread);
		thread.start();
		try {
			long startTime2 = System.currentTimeMillis();
			thread.join(timeout);
			log.info("Elapsed time for thread.join in queryKite "+
				(System.currentTimeMillis()-startTime2)/1000);
			startTime2 = System.currentTimeMillis();
			if (!thread.isAlive()) {
				//check for an exception
				if(kiteQueryThread.getException()!=null) {
					log.error("Exception");
					throw kiteQueryThread.getException();
				}
				//return the response if no exception
				log.info("Success");
				return kiteQueryThread.getResponse();
			}
			//the timeout was exceeded so return null
			log.warn("Timeout exceeded.");
			return null;
		} catch (InterruptedException e) {
			log.warn("Kite Query thread interrupted", e);
			return null;
		}
	}
	
	public static String aliasQuery(String mrn) throws  QueryKiteException
	{
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		AdministrationService adminService = Context.getAdministrationService();
		String response = null;
		try
		{
		    response = queryKite(mrn);
		} catch (Exception e)
		{
			Error error = new Error("Error", "Query Kite Connection"
					, e.getMessage()
					, Util.getStackTrace(e), new Date(), null);
			chirdlutilbackportsService.saveError(error);
			response = null;
		}
		
		//If the response is null this means the connection was broken
		//Try querying again
		if(response == null){
			response = queryKite(mrn);
			if(response != null){
				log.info("Re-query of FIND-ALIASES for mrn: "+mrn+" successful");
			}else{
				Error error = new Error("Error", "Query Kite Connection"
					, "Re-query of FIND-ALIASES after message dropped for mrn: "+mrn+" failed"
					, null, new Date(), null);
				chirdlutilbackportsService.saveError(error);
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
						+ filename);
			} catch (FileNotFoundException e1)
			{
				log.error("Could not find alias file: " + aliasDirectory 
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

	public static String mrfQuery(String mrn,Patient patient, boolean checkForCachedData) throws  QueryKiteException
	{
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		AdministrationService adminService = Context.getAdministrationService();
				
		String response = null;
		long startTime = System.currentTimeMillis();
		long startTime2 = System.currentTimeMillis();
		log.info("Starting mrf Query");
		
		//look to see if the mrf dump has already been found for today
		if (checkForCachedData) {
			String mrfDirectory = adminService.getGlobalProperty("chica.mrfArchiveDirectory");
			
			if (mrfDirectory != null) {
				//only look for a file within the past day
				File dir = new File(mrfDirectory);
				FileFilterByDate filter = new FileFilterByDate(24 * 60 * 60 * 1000, "_" + mrn + ".hl7");
				File[] matchingFiles = dir.listFiles(filter);
				if (matchingFiles != null && matchingFiles.length > 0) {
					try {
						FileInputStream input = new FileInputStream(matchingFiles[0].getPath());
						ByteArrayOutputStream output = new ByteArrayOutputStream();
						org.openmrs.module.chirdlutil.util.IOUtil.bufferedReadWrite(input, output);
						response = output.toString();
						if (response.length() == 0) {
							response = null;
						}
					}
					catch (Exception e) {
						
						log.error("File: " + matchingFiles[0].getPath() + "not found", e);
					}
				}
				
			} else {
				log.error("mrfDirectory is null!!");
			}
		} 
		
		log.info("Elapsed time for checkForCachedData in mrfQuery "+
			(System.currentTimeMillis()-startTime2)/1000);
		startTime2 = System.currentTimeMillis();
		
		if(response == null){
			
			try
			{
				response = queryKite(mrn);
			} 
			catch (Exception e)
			{
				Error error = new Error("Error", "Query Kite Connection"
					, e.getMessage()
					, Util.getStackTrace(e), new Date(), null);

				chirdlutilbackportsService.saveError(error);
			}
		}
		log.info("Elapsed time for queryKite (first) in mrfQuery "+
			(System.currentTimeMillis()-startTime2)/1000);
		startTime2 = System.currentTimeMillis();
		
		//If the response is null this means the connection was broken
		//Try querying again
		if (response == null)
		{
			response = queryKite(mrn);
			if (response != null)
			{
				log.info("Re-query of GET-MRF for mrn: " + mrn
						+ " successful");
			} else
			{
				Error error = new Error("Error",
						"Query Kite Connection",
						"Re-query of GET-MRF after message dropped for mrn: "
								+ mrn + " failed", null, new Date(), null);
				chirdlutilbackportsService.saveError(error);
			}
		}
		
		log.info("Elapsed time for queryKite (re-query) in mrfQuery "+
			(System.currentTimeMillis()-startTime2)/1000);
		startTime2 = System.currentTimeMillis();
		
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
						mrfDirectory  + filename);
			} catch (FileNotFoundException e1)
			{
				log.error("Couldn't find file: "+mrfDirectory + filename);
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
			log.info("Elapsed time for writing hl7 file in mrfQuery "+
				(System.currentTimeMillis()-startTime2)/1000);
			log.info("Elapsed time for mrf Query is "+
					(System.currentTimeMillis()-startTime)/1000);
		
			startTime = System.currentTimeMillis();
			log.info("Starting mrf parsing");
			LogicService logicService = Context.getLogicService();

			ObsInMemoryDatasource xmlDatasource = (ObsInMemoryDatasource) logicService
					.getLogicDataSource("RMRS");
			
			HashMap<Integer, HashMap<String, Set<Obs>>> regenObs = xmlDatasource.getObs();

			HL7ToObs.parseHL7ToObs(response,patient,mrn,regenObs);
			log.info("Elapsed time for mrf parsing is "+
					(System.currentTimeMillis()-startTime)/1000);
		}
	
		return response;
	}
	
}
