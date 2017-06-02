package org.openmrs.module.chica.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Form;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.util.FormAttributeValueDescriptor;
import org.openmrs.module.atd.util.Util;
import org.openmrs.module.chica.FaxStatus;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
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
@SuppressWarnings("deprecation")
public class FaxStatusController extends SimpleFormController {
	
	private final Log log = LogFactory.getLog(getClass());
	
	private static final boolean ASCENDING = false;
	private static final int DEFAULT_COUNT = 100;
	private static final int DEFAULT_START_COUNT = 0;
	private static final int SORT_COLUMN = 2;
	private static final int USER_TYPE = 2;


	
	/**
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		return "testing";
	}
	
	/**
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#onSubmit(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	@Override
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command,
	                                BindException errors) throws Exception {
		
		return new ModelAndView(new RedirectView(getSuccessView()));
	}
	
	
	
	
	/**
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#referenceData(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Map<String, Object> referenceData(HttpServletRequest request) throws Exception {
		
		LocationService locService = Context.getLocationService();
		Map<String, Object> map = new HashMap<String, Object>();
		
		List<FaxStatus> faxStatuses = getFaxStatusList(request, map);
		map.put("faxStatusRows",faxStatuses);
		
		return map;
	}
	
	
	
	private List<FaxStatus> getFaxStatusList (HttpServletRequest request, Map<String, Object> map) {
		
		AdministrationService administrationService = Context.getAdministrationService();
		String password = administrationService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_OUTGOING_FAX_PASSWORD);
		String username = administrationService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_OUTGOING_FAX_USERNAME); 
		
		FAXCOMX0020Service service = null;
		FAXCOMX0020ServiceSoap port = null;
		
		service = new FAXCOMX0020Service();

		port = service.getFAXCOMX0020ServiceSoap();
		ArrayOfMessageStatus statuses = port.loginAndGetMessageStatuses("", username, password, USER_TYPE, 
				SORT_COLUMN, ASCENDING, DEFAULT_START_COUNT, DEFAULT_COUNT);
		port.releaseSession();
		List<MessageStatus> faxComStatusList = statuses.getMessageStatus();
		map.put("faxComStatusList", faxComStatusList);
		
		return filterFaxStatuses(request, map);
		
	}
	
	@SuppressWarnings("unchecked")
	private List<FaxStatus>  filterFaxStatuses(HttpServletRequest request, Map<String, Object> map){
		
		
		List<FaxStatus> statuses = new ArrayList<FaxStatus>();
		List<MessageStatus> faxComStatuses = (List<MessageStatus>) map.get("faxComStatusList");
		
		for (MessageStatus faxComStatus : faxComStatuses){

			FaxStatus status = new FaxStatus();
			status.setFormInstanceByIdTag(faxComStatus.getIDTag());
			status.setImageLocation(faxComStatus.getIDTag());
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
			statuses.add(status);		
			
		}
		return statuses;
	}
	
	
	
	
	
	

}
