package org.openmrs.module.chica.web.controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.http.client.utils.URIBuilder;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.FaxStatus;
import org.openmrs.module.chica.web.ChicaServlet;
import org.openmrs.module.chica.web.ServletUtil;
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
	
	private static final Logger log = LoggerFactory.getLogger(FaxStatusController.class);
	
	private static final String FORM_VIEW = "/module/chica/faxStatus";
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
	
	/** Query webservice for fax status
	 * @param count 
	 * @param datepickerStart
	 * @param datepickerStop
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String processSubmit(@RequestParam("count") String count,   @RequestParam(value ="datepickerStart", required = false) String datepickerStart,
			@RequestParam(value = "datepickerStop", required = false) String datepickerStop,
			HttpServletRequest request, ModelMap model) throws Exception{
	
			int rowcount = DEFAULT_COUNT;
			
			try {
				
				if (StringUtils.isNotBlank(count) ){
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
	
	
	
	
	/**Query fax webservice for fax request history
	 * @param rowcount
	 * @param model
	 * @return
	 */
	private List<FaxStatus> queryFaxStatus ( int rowcount, ModelMap model ) {
		
		AdministrationService administrationService = Context.getAdministrationService();
		String password = administrationService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_OUTGOING_FAX_PASSPHRASE);
		String username = administrationService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_OUTGOING_FAX_USERNAME); 
		String wsdlURLString = administrationService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_OUTGOING_FAX_WSDL_LOCATION);
		
		List<FaxStatus> statuses = new ArrayList<FaxStatus>();
		URL wsdlURL = null;
		FAXCOMX0020Service service = null;
		FAXCOMX0020ServiceSoap port = null;
				
		try {
			if (StringUtils.isNotBlank(wsdlURLString)){
				wsdlURL = new URL(wsdlURLString);
				service = new FAXCOMX0020Service(wsdlURL);
			}else{
				service = new FAXCOMX0020Service();
			}
			
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
	
	
	/**Create a list of fax statuses filtered by date
	 * @param model
	 * @param faxComStatuses
	 * @return
	 */
	private List<FaxStatus> createFaxStatusList( ModelMap model, List<MessageStatus> faxComStatuses){
		
		String start = (String) model.get("startDate");
		String stop = (String) model.get("stopDate");
		Date startDate = null;
		Date stopDate = null;
		
		if (StringUtils.isNotBlank(start)){
			startDate = DateUtil.parseDate(start, ChirdlUtilConstants.DATE_FORMAT_MM_dd_YYYY);
		}
		if (StringUtils.isNotBlank(stop)){
			stopDate = DateUtil.parseDate(stop, ChirdlUtilConstants.DATE_FORMAT_MM_dd_YYYY);
			// The date assumes a time of 00:00. We want to
			// include all records until midnight of stopDate, so we will add one day.
			stopDate = DateUtils.addDays(stopDate, 1);
		}
		
		List<FaxStatus> statuses = new ArrayList<FaxStatus>();
		if (faxComStatuses != null){
			for (MessageStatus faxComStatus : faxComStatuses){
				if (!validDate(faxComStatus.getTransmitTime(), startDate, stopDate)){
					continue;
				}
				FaxStatus status = new FaxStatus(faxComStatus.getIDTag());
				status.setRecipientName(faxComStatus.getRecipientName());
				status.setFaxNumber(faxComStatus.getFaxNumber());
				status.setTransmitTime(faxComStatus.getTransmitTime());
				status.setTransmitTimeAsString(faxComStatus.getTransmitTime(), ChirdlUtilConstants.DATE_FORMAT_MM_dd_yyyy_hh_mm_ss);
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
	
	/**Use the IdTag from each status record to locate the fax image
	 * @param status
	 */
	public void setImageLocation(FaxStatus status) {
		FormInstance formInstance = status.getFormInstance();

		Integer locationTagId = status.getLocationTagId();
		if (formInstance == null || locationTagId == null) {
			return;
		}

		String imageDir = IOUtil.formatDirectoryName(org.openmrs.module.chirdlutilbackports.util.Util
				.getFormAttributeValue(formInstance.getFormId(), ChirdlUtilConstants.FORM_ATTRIBUTE_IMAGE_DIRECTORY,
						locationTagId, formInstance.getLocationId()));

		if (StringUtils.isNotBlank(imageDir)) {

			File imagefile = IOUtil.searchForImageFile(status.getIdTag(), imageDir);

			try {
				URIBuilder uriBuilder = new URIBuilder(ChicaServlet.CHICA_SERVLET_URL);
				uriBuilder.addParameter(ServletUtil.PARAM_ACTION, ServletUtil.CONVERT_TIFF_TO_PDF);
				uriBuilder.addParameter(ServletUtil.PARAM_TIFF_FILE_LOCATION, imagefile.getPath());

				String imageFilename = uriBuilder.toString() + ChicaServlet.CHICA_SERVLET_PDF_PARAMS;
				status.setImageFileLocation(imageFilename);
			} catch (Exception e) {
				log.error("Error generating URI form image filename for action: " + ServletUtil.CONVERT_TIFF_TO_PDF
						+ " tiff file location: " + imagefile.getPath(), e);
			}
		}

	}

	/**Check if fax status record date time is within start and stop date
	 * @param transmitTime
	 * @param startDate
	 * @param stopDate
	 * @return
	 */
	private boolean validDate(XMLGregorianCalendar transmitTime, Date startDate, Date stopDate) {

		try {
			Date transmitDate = transmitTime.toGregorianCalendar().getTime();
			if (startDate != null && transmitDate.before(startDate)) {
				return false;
			}
			if (stopDate != null && transmitDate.after(stopDate)) {
				return false;
			}

		} catch (Exception e) {
			log.error("Invalid date format for date fields to filter fax status query", e);
		}
		return true;
	}
	

}


