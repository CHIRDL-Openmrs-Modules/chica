package org.openmrs.module.chica;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Error;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.mrfservices.DumpServiceStub;
import org.openmrs.module.chica.mrfservices.DumpServiceStub.GetDumpE;
import org.openmrs.module.chica.mrfservices.DumpServiceStub.GetDumpResponseE;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.xmlBeans.StatsConfig;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutil.util.XMLUtil;

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
	private static final String GLOBAL_PROPERTY_MRF_QUERY_TIMEOUT = "chica.kiteTimeout";
	private static final String GLOBAL_PROPERTY_MRF_QUERY_CONFIG_FILE = "chica.mrfQueryConfigFile";
	private static final String GLOBAL_PROPERTY_MRF_QUERY_PASSWORD = "chica.MRFQueryPassword";
	private static final String MRF_PARAM_SYSTEM = "system";
	private static final String MRF_PARAM_USER_ID = "id";
	private static final String MRF_PARAM_PATIENT_IDENTIFIER_SYSTEM = "patient_identifier_system";
	private static final String MRF_PARAM_CLASSES_TO_INCLUDE = "classes_to_include";
	private static final String MRF_PARAM_CLASSES_TO_EXCLUDE = "classes_to_exclude";

	private static final String MRF_PARAM_MRN = "mrn";

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
			Context.authenticate(adminService
					.getGlobalProperty("scheduler.username"), adminService
					.getGlobalProperty("scheduler.password"));

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

	/**
	 * @param mrn
	 * @param queryPrefix
	 * @return
	 * @throws QueryKiteException
	 */
	private String queryKite(String mrn)
			throws QueryKiteException
			{
		AdministrationService adminService = Context.getAdministrationService();
		String configFile = adminService.getGlobalProperty(GLOBAL_PROPERTY_MRF_QUERY_CONFIG_FILE);   
		String password = adminService.getGlobalProperty(GLOBAL_PROPERTY_MRF_QUERY_PASSWORD);

		Properties props = null; 
		if(configFile == null){
			log.error("Could not find MRF query config file. Please set global property " + GLOBAL_PROPERTY_MRF_QUERY_CONFIG_FILE); 
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
			log.error(responseString);
			byte[] utf8Bytes = responseString.getBytes("UTF-8");
			log.info("Size: " + utf8Bytes.length);
			log.info("query time: " + (stop - start));
			log.info("Size: " + utf8Bytes.length);
			log.info("query time: " + (stop - start));
			String[] timings = response.getGetDumpResponse().getTiming();
			for (String timing : timings){
				System.out.println(timing);
				log.info(timing);
			}

		} catch (AxisFault e) {
			log.error(Util.getStackTrace(e));
		} catch (RemoteException e) {
			log.error(Util.getStackTrace(e));
		} catch (UnsupportedEncodingException e) {
			log.error(Util.getStackTrace(e));
		} catch (Exception e){
			log.error(Util.getStackTrace(e));
		}


		return responseString;
	}
}
