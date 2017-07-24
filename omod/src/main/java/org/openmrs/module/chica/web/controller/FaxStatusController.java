package org.openmrs.module.chica.web.controller;

import java.io.File;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang.time.DateUtils;
import org.apache.http.client.utils.URIBuilder;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.FaxStatus;
import org.openmrs.module.chica.web.ChicaServlet;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.DateUtil;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.biscom.ArrayOfMessageStatus;
import com.biscom.FAXCOMX0020Service;
import com.biscom.FAXCOMX0020ServiceSoap;
import com.biscom.MessageStatus;


/**
 * Controller for handling fax status display.
 *
 * @author Meena Sheley
 */
@Controller
@RequestMapping(value = "module/chica/faxStatus.form") 
public class FaxStatusController {
	
	private final Log log = LogFactory.getLog(getClass());
	
	private static final String FORM_VIEW = "/module/chica/faxStatus";
	private static final String SUCCESS_FORM_VIEW = "faxStatus.form"; 
	private static final boolean ASCENDING = false;
	private static final int DEFAULT_COUNT = 100;
	private static final int DEFAULT_START_COUNT = 0;
	private static final int SORT_COLUMN = 2;
	private static final int USER_TYPE = 2;
	

	@ModelAttribute("faxStatusRows")
	public List<FaxStatus> getFaxStatuses(){
		return new ArrayList<FaxStatus>();
	}
	
	@RequestMapping(method = RequestMethod.GET)
	protected String initForm(HttpServletRequest request,ModelMap map) throws Exception {
	
		return FORM_VIEW;
	}
	
	@RequestMapping(value = "/submit" , method = RequestMethod.POST)
	public String processSubmit(@RequestParam("count") String count,   @RequestParam(value ="datepickerStart", required = false) String datepickerStart,
			@RequestParam(value = "datepickerStop", required = false) String datepickerStop,
			HttpServletRequest request, ModelMap model) throws Exception{
	
			int rowcount = DEFAULT_COUNT;
			
			try {
				
				if (count != null && ! count.trim().equals("") ){
					rowcount = Integer.parseInt(count);
					model.addAttribute("rowcount",rowcount);
				}
				model.addAttribute("startDate", datepickerStart);
				model.addAttribute("stopDate", datepickerStop);
				model.addAttribute("faxStatusRows", queryFaxStatus( rowcount, model));
				model.addAttribute("validInteger", true);
				
				
			} catch (NumberFormatException e) {
				model.addAttribute("validInteger", false);
			}
		
		
		return FORM_VIEW;
	}
	
	
	
	
	private List<FaxStatus> queryFaxStatus ( int rowcount, ModelMap model ) {
		
		AdministrationService administrationService = Context.getAdministrationService();
		String password = administrationService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_OUTGOING_FAX_PASSWORD);
		String username = administrationService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_OUTGOING_FAX_USERNAME); 
		List<FaxStatus> statuses = new ArrayList<FaxStatus>();
		
		FAXCOMX0020Service service = null;
			FAXCOMX0020ServiceSoap port = null;
			
		
		try {
			service = new FAXCOMX0020Service();

			port = service.getFAXCOMX0020ServiceSoap();
			List<MessageStatus> statusList = new ArrayList<MessageStatus>();
			ArrayOfMessageStatus faxCOMStatusList = port.loginAndGetMessageStatuses("", username, password, USER_TYPE, 
					SORT_COLUMN, ASCENDING, DEFAULT_START_COUNT, rowcount);
			statusList = faxCOMStatusList.getMessageStatus();
			if (faxCOMStatusList != null ){
				statusList = faxCOMStatusList.getMessageStatus();
			}
			if (statusList != null){
				statuses = createFaxStatusList(model, statusList);
			}
			return statuses;
				
		} catch (Exception e) {
			
			log.error("Error connecting to web service. Check network connections. ", e);
			model.addAttribute("noAccessToHost", true);
			return statuses;
			
		} finally{
			if (port != null){
				port.releaseSession();
			}
			
		}
		
		
	
	}
	
	@SuppressWarnings("unchecked")
	private List<FaxStatus> createFaxStatusList( ModelMap model, List<MessageStatus> faxComStatuses){
		
		String startDate = (String) model.get("startDate");
		String stopDate = (String) model.get("stopDate");
		List<FaxStatus> statuses = new ArrayList<FaxStatus>();
		if (faxComStatuses != null){
			for (MessageStatus faxComStatus : faxComStatuses){
				if (!validDate(faxComStatus.getTransmitTime(), startDate, stopDate)){
					continue;
				}
				FaxStatus status = new FaxStatus(faxComStatus.getIDTag());
				status.setFaxNumber(faxComStatus.getFaxNumber());
				status.setTransmitTime(faxComStatus.getTransmitTime());
				status.setTransmitTimeAsString(faxComStatus.getTransmitTime(), "MM/dd/yyyy HH:MM:ss");
				status.setSubject(faxComStatus.getSubject());
				status.setStatusText(faxComStatus.getStatusText());
				status.setAttachmentCount(faxComStatus.getAttachmentCount());
				status.setId(faxComStatus.getID());
				status.setNumberOfAttempts(faxComStatus.getNumberOfAttempts());
				status.setPagesTransmitted(faxComStatus.getPagesTransmitted());
				status.setStatusName(faxComStatus.getStatusName());
				status.setUniqueJobID(faxComStatus.getUniqueJobID());
				status.setIdTag(faxComStatus.getIDTag());
				status.setId(faxComStatus.getID());
				status.setTransmissionStatus(faxComStatus.getTransmissionStatus());
				status.setConnectTime(faxComStatus.getConnectTime());
				setImageLocation(status);
				statuses.add(status);	
			}
		}
		
		
		return statuses;
	}
	
	public void setImageLocation(FaxStatus status){
		FormInstance formInstance = status.getFormInstance();

		Integer locationTagId = status.getLocationTagId();
		if (formInstance == null || locationTagId == null){
			return;
		}

		String imageDir = IOUtil.formatDirectoryName(org.openmrs.module.chirdlutilbackports.util.Util.getFormAttributeValue(formInstance.getFormId(),
				ChirdlUtilConstants.FORM_ATTRIBUTE_IMAGE_DIRECTORY, locationTagId, formInstance.getLocationId()));	

		if (imageDir != null && !imageDir.equals("") ) {

			File imagefile = IOUtil.searchForImageFile(status.getIdTag(),imageDir);

			try {
				URIBuilder uriBuilder = new URIBuilder(ChicaServlet.CHICA_SERVLET_URL);
				uriBuilder.addParameter(ChicaServlet.PARAM_ACTION, ChicaServlet.CONVERT_TIFF_TO_PDF);
				uriBuilder.addParameter(ChicaServlet.PARAM_TIFF_FILE_LOCATION, imagefile.getPath());

				String imageFilename = uriBuilder.toString() + ChicaServlet.CHICA_SERVLET_PDF_PARAMS;
				status.setImageFileLocation(imageFilename);
			}
			catch (URISyntaxException e) {
				log.error("Error generating URI form image filename for action: " + ChicaServlet.CONVERT_TIFF_TO_PDF + 
						" tiff file location: " + imagefile.getPath(), e);
			}
		}


	}
	
	private boolean validDate(XMLGregorianCalendar transmitTime, String start, String stop){
	
		try {
			Date transmitDate = transmitTime.toGregorianCalendar().getTime();
			if (start != null && !start.trim().equals(ChirdlUtilConstants.GENERAL_INFO_EMPTY_STRING)){
				Date startDate = DateUtil.parseDate(start, "MM/dd/yyyy");
				if ( startDate != null && transmitDate.before(startDate)){
					return false;
				}
			}
			
			if (stop != null && !stop.trim().equals(ChirdlUtilConstants.GENERAL_INFO_EMPTY_STRING)){
				Date stopDate = DateUtil.parseDate(stop, "MM/dd/yyyy");
				// With no time specified, the date assumes 00:00. We want to include the entire day for stop date.
				stopDate = DateUtils.addDays(stopDate, 1); 
				if ( stopDate != null && transmitDate.after(stopDate)){
					return false;
				}
			}
			
		} catch (Exception e) {
			log.error("Invalid date format for date fields to filter fax status query",e);
		}
		return true;
	}
	
	

}


