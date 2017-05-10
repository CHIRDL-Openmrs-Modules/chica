package org.openmrs.module.chica.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Error;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.ChirdlLocationAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

/**
 * Pager class sends a page message via an internet paging service. The URL and parameter settings
 * are stored as global properties.
 * 
 * @author msheley
 */
public class Pager {
	
	private static final String XML_PAGER_RESPONSE_START = "<pagerResponse>";
	private static final String XML_PAGER_RESPONSE_END = "</pagerResponse>";
	private static final String XML_RESULT = "result";
	private static final String XML_RESPONSE = "response";
	
	private static final String PARAM_REPORTER = "reporter";
	private static final String PARAM_MESSAGE = "message";
	
	private static final String SUCCESS = "success";
	private static final String FAIL = "fail";
	
	/** Logger for this class and subclasses */
	protected final static Log log = LogFactory.getLog(Pager.class);
	private static HashMap<Integer, Date> thresholdDateByLocation = new HashMap<Integer, Date>();
	
	public static void sendPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_XML);
		response.setHeader(ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL,
		    ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL_NO_CACHE);
		PrintWriter pw = response.getWriter();
		
		User user = Context.getAuthenticatedUser();
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		LocationService locationService = Context.getLocationService();
		String locationString = user.getUserProperty(ChirdlUtilConstants.USER_PROPERTY_LOCATION);
		Integer locationId = null;
		if (locationString != null) {
			Location location = locationService.getLocation(locationString);
			if (location != null) {
				locationId = location.getLocationId();
			}
		}
		
		ChirdlLocationAttributeValue locAttrValue = chirdlutilbackportsService.getLocationAttributeValue(locationId,
			ChirdlUtilConstants.LOCATION_ATTR_PAGER_MESSAGE);
		String message = locAttrValue.getValue();
		
		String reporter = request.getParameter(PARAM_REPORTER);
		String reporterMessage = request.getParameter(PARAM_MESSAGE);
		message += " " + reporter + " " + reporterMessage;
		
		String pageResponse = sendPage(message, locationId);
		pw.write(XML_PAGER_RESPONSE_START);
		if (pageResponse != null && pageResponse.contains("Send message results") && !pageResponse.contains("send failed")
		        && !pageResponse.contains("Could not send page") && !pageResponse.contains("Chica Support")) {
			ServletUtil.writeTag(XML_RESULT, SUCCESS, pw);
			Error error = new Error("Warning", "Support Page", "Support page sent: " + message, null, new java.util.Date(),
			        null);
			chirdlutilbackportsService.saveError(error);
		} else {
			ServletUtil.writeTag(XML_RESULT, FAIL, pw);
			if (pageResponse != null && !pageResponse.contains("send failed")) {
				ServletUtil.writeTag(XML_RESPONSE, ServletUtil.escapeXML(pageResponse), pw);
			} else {
				ServletUtil.writeTag(XML_RESPONSE, ServletUtil.escapeXML("An error occurred sending the page request."), pw);
			}
		}
		
		pw.write(XML_PAGER_RESPONSE_END);
	}
	
	private static String sendPage(String message, Integer locationId) {
		Calendar thisCalendar = Calendar.getInstance();
		Date now = thisCalendar.getTime();
		boolean goAhead = true;
		Date thresholdDate = thresholdDateByLocation.get(locationId);
		
		if (thresholdDate != null) {
			if (now.after(thresholdDate)) {
				goAhead = true;
			} else {
				goAhead = false;
			}
		}
		
		AdministrationService adminService = Context.getAdministrationService();
		String pagerNumber = adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_PAGER_NUMBER);
		String idParam = adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_PAGER_NUMBER_URL_PARAM);
		String textParam = adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_PAGER_NUMBER_MESSAGE_PARAM);
		String baseUrl = adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_PAGER_BASE_URL);
		String thresholdTime = adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_PAGER_WAIT_TIME_BEFORE_REPAGE);
		
		if (pagerNumber == null || pagerNumber.trim().length() == 0) {
			log.error("Pager number is null. Could not send page.");
			return "The pager number is not set in the database.  Could not send page.";
		}
		
		if (goAhead == false) {
			return "Someone has already sent a message in the last " + thresholdTime + " minutes. "
			        + "Chica Support will contact you shortly.";
		}
		
		String urlStr = baseUrl;
		BufferedReader rd = null;
		String response = null;
		
		try {
			urlStr += "?" + idParam + "=" + URLEncoder.encode(pagerNumber, "UTF-8") + "&" + textParam + "="
			        + URLEncoder.encode(message, "UTF-8");
			
			if (baseUrl == null || baseUrl.length() == 0 || pagerNumber == null || pagerNumber.length() == 0
			        || message == null || message.length() == 0 || idParam == null || idParam.length() == 0
			        || textParam == null || textParam.length() == 0) {
				log.error("Page was not sent due to null url string or null parameters. " + urlStr);
				
				return "Invalid parameters provided.  Could not send page.";
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
			response = sb.toString();
		}
		catch (Exception e) {
			log.error("Could not send page: " + e.getMessage());
			log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
			return "Could not send page: " + e.getMessage();
		}
		finally {
			if (rd != null)
				try {
					rd.close();
					Calendar threshold = Calendar.getInstance();
					Integer timeToWait = Integer.parseInt(thresholdTime);
					threshold.add(Calendar.MINUTE, timeToWait.intValue());
					thresholdDate = threshold.getTime();
					thresholdDateByLocation.put(locationId, thresholdDate);
				}
				catch (Exception e) { /* pass */
					log.error("Could not set Threshold Date for Page!" + e.getMessage());
				}
		}
		
		return response;
	}
}
