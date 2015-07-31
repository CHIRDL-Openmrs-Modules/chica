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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.module.chica.hl7.mrfdump.HL7ToObs;
import org.openmrs.module.chica.mrfservices.DumpServiceStub;
import org.openmrs.module.chica.mrfservices.DumpServiceStub.GetDumpE;
import org.openmrs.module.chica.mrfservices.DumpServiceStub.GetDumpResponseE;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.FileFilterByDate;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.datasource.ObsInMemoryDatasource;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Error;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

/**
 * @author tmdugan
 * 
 */
public class QueryKite
{
	private static final String CHARACTER_ENCODING_UTF_8 = "UTF-8";

	private static Log log = LogFactory.getLog(QueryKite.class);

	private static final String MRF_PARAM_SYSTEM = "system";
	private static final String MRF_PARAM_USER_ID = "id";
	private static final String MRF_PARAM_PATIENT_IDENTIFIER_SYSTEM = "patient_identifier_system";
	private static final String MRF_PARAM_CLASSES_TO_INCLUDE = "classes_to_include";
	private static final String MRF_PARAM_CLASSES_TO_EXCLUDE = "classes_to_exclude";


	public static String queryKite(String mrn)
			throws QueryKiteException
	{
		AdministrationService adminService = Context.getAdministrationService();
		// Set the default to 5 seconds.  We don't want it to last forever by setting it to 0.
		Integer timeout = 5000;
		try
		{
			timeout = Integer.parseInt( adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_MRF_QUERY_TIMEOUT));
			timeout = timeout * 1000; // convert seconds to
			// milliseconds
		} catch (NumberFormatException e)
		{
		}
		
		KiteQueryThread kiteQueryThread = new KiteQueryThread(mrn);
		Thread thread = new Thread(kiteQueryThread);
		thread.start();
		try {
			long startTime = System.currentTimeMillis();
			thread.join(timeout);
			log.info("Elapsed time for thread.join in queryKite "+
				(System.currentTimeMillis()-startTime)/1000);
			startTime = System.currentTimeMillis();
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
	
		/**
		 * Check if a mrf dump file exists already for that patient today.  
		 * If no previous mrf dump exists, call the method that starts a new thread to
		 * query for the MRF dump.
		 * @param mrn
		 * @param patient
		 * @param checkForCachedData
		 * @return
		 * @throws QueryKiteException
		 */
		public static String mrfQuery(String mrn,Patient patient, boolean checkForCachedData) throws  QueryKiteException
			{
				ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
				AdministrationService adminService = Context.getAdministrationService();

				String response = null;
				long startTime = System.currentTimeMillis();
				long startTime2 = System.currentTimeMillis();
				String mrfDirectory = adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_MRF_ARCHIVE_DIRECTORY);
				if (mrfDirectory == null || mrfDirectory.trim().equals("")){
					log.error("Mrf query archive directory is unknown.");
					return null;
				}
				
				//look to see if the mrf dump has already been found for today
				if (checkForCachedData) {
					
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
				} 

				log.info("Elapsed time for checkForCachedData in mrfQuery "+
						(System.currentTimeMillis()-startTime2)/1000);
				startTime2 = System.currentTimeMillis();

				//No cached data
				if(response == null){

					try
					{
						response = queryKite(mrn);
					} 
					catch (Exception e)
					{
						Error error = new Error(ChirdlUtilConstants.ERROR_LEVEL_ERROR, ChirdlUtilConstants.ERROR_QUERY_KITE_CONNECTION
								, e.getMessage()
								, Util.getStackTrace(e), new Date(), null);

						chirdlutilbackportsService.saveError(error);
					}
				}
				
				
				log.info("Elapsed time for mrf query "+
						(System.currentTimeMillis()-startTime2)/1000);
				startTime2 = System.currentTimeMillis();

			
				if (response != null)
				{
					// save mrf dump to a file
					
					
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
					
					//mrf dump has multiple messages.		
					/*String[] messages = response.split("MSH");	
					
					for (String message : messages){
						
						if (message.startsWith("MSH|")){
							HL7ToObs.processMessage(message, patient, regenObs);
							HL7SocketHandler.mergeAliases(mrn, patient, message);;
							log.info("Elapsed time for mrf parsing is "+
									(System.currentTimeMillis()-startTime)/1000);
						}
					}*/

				}
				return response;
			}
		
		/**
		 * Query web service for MRF dump
		 * @param mrn
		 * @return
		 * @throws QueryKiteException
		 */
		public static String getMRFDump(String mrn) throws QueryKiteException
		{
			AdministrationService adminService = Context.getAdministrationService();
			String configFile = adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_MRF_QUERY_CONFIG_FILE);   
			String password = adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_MRF_QUERY_PASSWORD);
			String responseString = null;

			Properties props = null; 
			if(configFile == null){
				log.error("Could not find MRF query config file. Please set global property " + ChirdlUtilConstants.GLOBAL_PROP_MRF_QUERY_CONFIG_FILE); 
				return null;
			}

			props = IOUtil.getProps(configFile);
			if (props == null) {
				return null;
			}

			try{

				DumpServiceStub service = new DumpServiceStub();
				GetDumpE dumpE = new GetDumpE();
				DumpServiceStub.GetDump dump = new DumpServiceStub.GetDump();
				dumpE.setGetDump(dump);
				
				DumpServiceStub.EntityIdentifier login = new DumpServiceStub.EntityIdentifier();
				login.setId(props.getProperty(MRF_PARAM_USER_ID));
				login.setSystem(props.getProperty(MRF_PARAM_SYSTEM));
				dump.setUser(login);

				DumpServiceStub.EntityIdentifier patient = new DumpServiceStub.EntityIdentifier();
				//patient.setId(props.getProperty(MRF_PARAM_MRN));
				patient.setId(mrn);
				patient.setSystem(props.getProperty(MRF_PARAM_PATIENT_IDENTIFIER_SYSTEM));
				dump.setPatient(patient);
				dump.setPassword(password);
				dump.setClassesToExclude(props.getProperty(MRF_PARAM_CLASSES_TO_EXCLUDE));
				dump.setClassesToInclude(props.getProperty(MRF_PARAM_CLASSES_TO_INCLUDE));

				long start = Calendar.getInstance().getTimeInMillis();
				GetDumpResponseE response = service.getDump(dumpE);

				long stop = Calendar.getInstance().getTimeInMillis();
				responseString = response.getGetDumpResponse().getHl7();

				//LOGGING
				System.out.println(responseString);
				byte[] utf8Bytes = responseString.getBytes(CHARACTER_ENCODING_UTF_8);
				log.info("Size: " + utf8Bytes.length);
				log.info("query time: " + (stop - start));
				log.info("Size: " + utf8Bytes.length);
				log.info("query time: " + (stop - start));
				String[] timings = response.getGetDumpResponse().getTiming();
				for (String timing : timings){
					log.info(timing);
				}

			} catch (Exception e){
				log.error("Exception during MRF query", e);
			}
			return responseString;
		}
}
