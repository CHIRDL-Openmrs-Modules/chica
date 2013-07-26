/**
 * 
 */
package org.openmrs.module.chica.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;

/**
 * @author Tammy Dugan
 * 
 */
public class Util
{
	public static final String YEAR_ABBR = "yo";
	public static final String MONTH_ABBR = "mo";
	public static final String WEEK_ABBR = "wk";
	public static final String DAY_ABBR = "do";
	
	private static Log log = LogFactory.getLog( Util.class );
	public static final Random GENERATOR = new Random();
	
	public static Obs saveObsWithStatistics(Patient patient, Concept currConcept, int encounterId, String value,
	                                         FormInstance formInstance, Integer ruleId, Integer locationTagId) {
		
		String formName = null;
		if (formInstance != null) {
			if (formInstance.getFormId() == null) {
				log.error("Could not find form for statistics update");
				return null;
			}
			
			FormService formService = Context.getFormService();
			Form form = formService.getForm(formInstance.getFormId());
			formName = form.getName();
		}
		
		boolean usePrintedTimestamp = false;
		
		if(formName != null && formName.equalsIgnoreCase("PWS")){
			usePrintedTimestamp = true;
		}
		return org.openmrs.module.atd.util.Util.saveObsWithStatistics(patient, currConcept, encounterId, value,
            formInstance, ruleId, locationTagId,usePrintedTimestamp);
	}
	
	public static String sendPage(String message, String pagerNumber) {
		AdministrationService adminService = Context.getAdministrationService();
		String idParam = adminService.getGlobalProperty("chica.pagerUrlNumberParam");
		String textParam = adminService.getGlobalProperty("chica.pagerUrlMessageParam");
		String baseUrl = adminService.getGlobalProperty("chica.pagerBaseURL");
		
		String urlStr = baseUrl;
		BufferedReader rd = null;
		
		try {
			urlStr += "?" + idParam + "=" + URLEncoder.encode(pagerNumber, "UTF-8") + "&" + textParam + "="
			        + URLEncoder.encode(message, "UTF-8");	
			if (baseUrl == null || baseUrl.length() == 0 || pagerNumber == null || pagerNumber.length() == 0
			        || message == null || message.length() == 0 || idParam == null || idParam.length() == 0
			        || textParam == null || textParam.length() == 0) {
				log.error("Page was not sent due to null url string or null parameters. " + urlStr);
				return "";
			}
			
			URL url = new URL(urlStr);
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; .NET CLR 1.0.3705;)");
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			
			return sb.toString();
		}
		catch (Exception e) {
			log.error("Could not send page: " + message + " to " + pagerNumber + " " + e.getMessage());
			log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}
		finally {
			if (rd != null) {
				try {
					rd.close();
				}
				catch (Exception e) {
					log.error("Error closing the reader.");
				}
			}
		}
		
		return "";
	}
}
