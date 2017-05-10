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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

import javax.xml.ws.soap.SOAPFaultException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.hl7.mrfdump.HL7ToObs;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.FileFilterByDate;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Error;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.regenstrief.services.DumpService;
import org.regenstrief.services.EntityIdentifier;
import org.regenstrief.services.GetDump;
import org.regenstrief.services.GetDumpResponse;
import org.regenstrief.services.IDumpService;

/**
 * @author tmdugan
 * 
 */
public class QueryKite
{
	private static final String ABBREVIATION_UNITS_MILLISECOND = " ms";

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
	
		try {
			timeout = Integer.parseInt( adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_MRF_QUERY_TIMEOUT));
			timeout = timeout * 1000; // convert seconds to
		} catch (Exception e){
			log.error("Global property for MRF dump timeout is invalid or does not exist.", e);
		}
		
		
		final KiteQueryThread kiteQueryThread = new KiteQueryThread(mrn);
		
		Thread thread = new Thread(kiteQueryThread);
		thread.start();
		try {
			long startTime = System.currentTimeMillis();
			thread.join(timeout);
			log.info("Elapsed time for thread.join in queryKite. "+
				(System.currentTimeMillis()-startTime)/1000);
			startTime = System.currentTimeMillis();
			if (!thread.isAlive()) {
				//check for an exception
				if(kiteQueryThread.getException()!=null) {
					log.error("Kite Query Exception", kiteQueryThread.getException());
					throw kiteQueryThread.getException();
				}
				//return the response if no exception
				log.info("Kite Query Successful");
				return kiteQueryThread.getResponse();
			}
			//the timeout was exceeded so return null
			log.warn("Kite query timeout exceeded.");
			return null;
		} catch (InterruptedException e) {
			log.warn("Kite query thread interrupted", e);
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
		boolean responseFromCache = false;
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
			FileFilterByDate filter = new FileFilterByDate(24 * 60 * 60 * 1000, "_" + mrn + ChirdlUtilConstants.FILE_EXTENSION_HL7);
			File[] matchingFiles = dir.listFiles(filter);
			if (matchingFiles != null && matchingFiles.length > 0) {
				try {
					FileInputStream input = new FileInputStream(matchingFiles[0].getPath());
					ByteArrayOutputStream output = new ByteArrayOutputStream();
					org.openmrs.module.chirdlutil.util.IOUtil.bufferedReadWrite(input, output);
					response = output.toString();
					if (response.length() > 0) {
						responseFromCache = true;
					}
				}
				catch (Exception e) {
					log.error("File: " + matchingFiles[0].getPath() + "not found", e);
				}
			}
		} 

		log.info("Elapsed time for checkForCachedData in mrfQuery: "+
				(System.currentTimeMillis()-startTime2) + ABBREVIATION_UNITS_MILLISECOND);
		startTime2 = System.currentTimeMillis();

		//No cached data
		if(!responseFromCache){

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


		log.info("Elapsed time for mrf dump query: " +
				(System.currentTimeMillis()-startTime2) + "ms");
		startTime2 = System.currentTimeMillis();


		if (response != null)
		{
			// Only save file if dump is not from cache
			if (!responseFromCache){

				String filename = "r" + Util.archiveStamp() + "_" + mrn + ChirdlUtilConstants.FILE_EXTENSION_HL7;

				FileOutputStream mrfDumpFile = null;
				try
				{
					mrfDumpFile = new FileOutputStream(
							mrfDirectory  + filename);
				} catch (FileNotFoundException e1)
				{
					log.error("Couldn't find file: "+ mrfDirectory + filename);
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
						log.error("There was an error writing the mrf dump file.", e);
					}
				}
			}
			
			log.info("Elapsed time for writing hl7 file in mrfQuery: "+
					(System.currentTimeMillis()-startTime2) + ABBREVIATION_UNITS_MILLISECOND);

			startTime = System.currentTimeMillis();
			
			HL7ToObs.parseHL7ToObs(response,patient,mrn);		
			
			log.info("Elapsed time for mrf parsing is "+
					(System.currentTimeMillis()-startTime) + ABBREVIATION_UNITS_MILLISECOND);
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
			String endpoint = adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_MRF_TARGET_ENDPOINT);
			
			if (password == null || password.trim().equals("")) {
				log.error("MRF query password is unknown. Please make sure global property is set for " + ChirdlUtilConstants.GLOBAL_PROP_MRF_QUERY_PASSWORD);
				return null;
			}
			if (endpoint == null || endpoint.trim().equals("")) {
				log.error("MRF query URL is unknown. Please make sure global property is set for " + ChirdlUtilConstants.GLOBAL_PROP_MRF_TARGET_ENDPOINT);
				return null;
			}
			if(configFile == null || endpoint.trim().equals("")){
				log.error("MRF query configuration filename is unknown. Please set global property " + ChirdlUtilConstants.GLOBAL_PROP_MRF_QUERY_CONFIG_FILE); 
				return null;
			}
			
			Properties props = null; 
			props = IOUtil.getProps(configFile);
			if (props == null) {
				return null;
			}
			
			String responseString = null;
			try{

				GetDump request = new GetDump();

				//Set the patient
				EntityIdentifier patientIdentifier = new EntityIdentifier();
				patientIdentifier.setId(mrn);
				patientIdentifier.setSystem(props.getProperty(MRF_PARAM_PATIENT_IDENTIFIER_SYSTEM));
				request.setPatient(patientIdentifier);

				//Set the User
				EntityIdentifier userIdentifier = new EntityIdentifier();
				userIdentifier.setId(props.getProperty(MRF_PARAM_USER_ID));
				userIdentifier.setSystem(props.getProperty(MRF_PARAM_SYSTEM));

				request.setUser(userIdentifier);

				//set password
				request.setPassword(password);
				
				//set classes to exclude/include
				request.setClassesToExclude(props.getProperty(MRF_PARAM_CLASSES_TO_EXCLUDE));
				request.setClassesToInclude(props.getProperty(MRF_PARAM_CLASSES_TO_INCLUDE));

				// Make the web service call
				URL url;

				url = new URL(endpoint); 
				DumpService service = new DumpService(url);
				IDumpService idumpService = service.getGetDump();
				GetDumpResponse response = null;
				if (idumpService != null  ){
					response = idumpService.getDump(request);
					if (response != null){
						responseString = response.getHl7();
					}	
				}

			} catch (MalformedURLException e) {
				log.error("URL for MRF dump  is not valid: " + endpoint, e);
				throw new QueryKiteException(e);
			} catch (SOAPFaultException e) {
				log.error("SOAP Fault Exception", e);
				throw new QueryKiteException(e);
			} catch(Exception e){
				log.error("MRF dump query exception.", e);
				throw new QueryKiteException(e);
			}
				
			return responseString;

		}

}
