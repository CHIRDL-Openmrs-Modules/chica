package org.openmrs.module.chica.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.FaxStatus;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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


	@RequestMapping(method = RequestMethod.GET)
	protected String initForm(HttpServletRequest request,ModelMap map) throws Exception {
	
	List<FaxStatus> faxStatuses = getFaxStatusList( map);
		map.put("faxStatusRows",faxStatuses);
		
		return FORM_VIEW;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	protected ModelAndView processSubmit(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception{
		Map<String, Object> map = new HashMap<String, Object>();
		List<FaxStatus> faxStatuses = getFaxStatusList( map);
		map.put("faxStatusRows",faxStatuses);
		map.put("application", "Get Fax Status");
		return new ModelAndView(new RedirectView(SUCCESS_FORM_VIEW));
	}
	
	
	private List<FaxStatus> getFaxStatusList ( Map<String, Object> map) {
		
		AdministrationService administrationService = Context.getAdministrationService();
		String password = administrationService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_OUTGOING_FAX_PASSWORD);
		String username = administrationService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_OUTGOING_FAX_USERNAME); 
		
		FAXCOMX0020Service service = null;
		FAXCOMX0020ServiceSoap port = null;
		
		try {
			service = new FAXCOMX0020Service();

			port = service.getFAXCOMX0020ServiceSoap();
			List<MessageStatus> statusList = new ArrayList<MessageStatus>();
			ArrayOfMessageStatus faxCOMStatusList = port.loginAndGetMessageStatuses("", username, password, USER_TYPE, 
					SORT_COLUMN, ASCENDING, DEFAULT_START_COUNT, DEFAULT_COUNT);
			
			if (faxCOMStatusList ==null || (statusList = faxCOMStatusList.getMessageStatus()) == null ){
				map.put("faxComStatusList", new ArrayList<MessageStatus>());
			} else{
				map.put("faxComStatusList", statusList);
			}
			return filterFaxStatuses( map);
				
		} catch (Exception e) {
			
			log.error("Error connecting to web service. Check network connections. ", e);
			map.put("noAccessToHost", true);
			return new ArrayList<FaxStatus>();
			
		} finally{
			if (port != null){
				port.releaseSession();
			}
			
		}
		
	
	}
	
	@SuppressWarnings("unchecked")
	private List<FaxStatus>  filterFaxStatuses( Map<String, Object> map){
		
		
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
