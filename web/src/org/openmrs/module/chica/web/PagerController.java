package org.openmrs.module.chica.web;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Error;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.LocationAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.module.chirdlutil.service.ChirdlUtilService;
import org.springframework.web.servlet.mvc.SimpleFormController;



/**
 * Pager Controller sends a page message via an internet paging service.
 * The URL and parameter settings are stored as global properties.
 * @author msheley
 *
 */
public class PagerController extends SimpleFormController
{

	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	private static HashMap<Integer,Date> thresholdDateByLocation = new HashMap<Integer,Date>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception
	{
		return "paging";
	}

	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		
		String sendPage = request.getParameter("sendPage");
		
		if (sendPage != null) {
			AdministrationService adminService = Context.getAdministrationService();
			
			String pagerNumber = adminService.getGlobalProperty("chica.pagerNumber");
			
			String idParam = adminService.getGlobalProperty("chica.pagerUrlNumberParam");
			String textParam = adminService.getGlobalProperty("chica.pagerUrlMessageParam");
			String baseUrl = adminService.getGlobalProperty("chica.pagerBaseURL");
			String thresholdTime = adminService.getGlobalProperty("chica.pagerWaitTimeBeforeRepage");
			
			User user = Context.getAuthenticatedUser();
			ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
			LocationService locationService = Context.getLocationService();
			String locationString = user.getUserProperty("location");
			Integer locationId = null;
			if (locationString != null) {
				Location location = locationService.getLocation(locationString);
				if (location != null) {
					locationId = location.getLocationId();
				}
			}
			LocationAttributeValue locAttrValue = chirdlutilbackportsService.getLocationAttributeValue(locationId, "pagerMessage");
			String message = locAttrValue.getValue();
			
			String reporter = request.getParameter("reporter");
			String reporterMessage = request.getParameter("message");
			message+=" "+reporter+" "+reporterMessage;
						
			String pageResponse = sendPage(pagerNumber, message, baseUrl, idParam, textParam, 
				thresholdTime,locationId);
						
			if (pageResponse != null && pageResponse.contains("Send message results")
			        && !pageResponse.contains("send failed")) {
				map.put("pagerSuccess", true);
				Error error = new Error("Warning","Support Page","Support page sent: "+message,null,new java.util.Date(),null);
				chirdlutilbackportsService.saveError(error);
			} else {
				map.put("pagerSuccess", false);
				if(pageResponse != null){
					map.put("pageResponse", pageResponse);
				}
			}
			map.put("sendPage", sendPage);
		}
		return map;
	}
	
	private String sendPage(String pagerNumber,String message, String baseUrl, 
			String QryStrId, String QryStrText, String thresholdTime,Integer locationId)
	{
		Calendar thisCalendar = Calendar.getInstance();
		Date now = thisCalendar.getTime();
		boolean goAhead = true;
		Date thresholdDate = thresholdDateByLocation.get(locationId);
		
		if(thresholdDate != null)
		{
			
			if(now.after(thresholdDate))
			{
				goAhead = true;
			}
			else
			{
				goAhead = false;
			}
		}
		
		if(pagerNumber == null){
			log.error("Pager number is null. Could not send page.");
			return null;
		}
		
		if(goAhead == false)
		{
			return "Someone has already sent a message in the last "+thresholdTime+" minutes. "
			+"Chica Support will contact you shortly.";
			
		}
		
		String urlStr = baseUrl;
		BufferedReader rd = null;
		String response = null;
		
		try { 
			
			urlStr += "?" + QryStrId + "=" 
				+ URLEncoder.encode(pagerNumber, "UTF-8")
				+ "&" + QryStrText 
				+ "=" + URLEncoder.encode(message, "UTF-8");
			
			if (baseUrl == null || baseUrl.length()==0 || 
					pagerNumber == null || pagerNumber.length()==0 || 
					message == null || message.length()==0 || 
					QryStrId == null || QryStrId.length()==0||
					QryStrText == null || QryStrText.length()==0){
				this.log.warn("Page was not sent due to null url string or null parameters. "  + urlStr);
				
				return null;
			}
	
			URL url = new URL(urlStr );
			URLConnection conn =  url.openConnection();
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; .NET CLR 1.0.3705;)");
			rd = new BufferedReader(new InputStreamReader(conn
					.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null)
			{
				sb.append(line);
			}
			response = sb.toString();
			

		} catch (Exception e)
		{
			this.log.error("Could not send page: " + e.getMessage());
			this.log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
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
					this.log.error("Could not set Threshold Date for Page!" + e.getMessage());
				}
		}
		return response;
		
		
		
	
		
	}
	
	
	
	
}
