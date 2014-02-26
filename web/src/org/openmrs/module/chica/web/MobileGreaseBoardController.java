package org.openmrs.module.chica.web;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jibx.runtime.JiBXException;
import org.openmrs.Form;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.util.PatientRow;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutil.xmlBeans.serverconfig.MobileClient;
import org.openmrs.module.chirdlutil.xmlBeans.serverconfig.MobileClients;
import org.openmrs.module.chirdlutil.xmlBeans.serverconfig.MobileForm;
import org.openmrs.module.chirdlutil.xmlBeans.serverconfig.MobileForms;
import org.openmrs.module.chirdlutil.xmlBeans.serverconfig.ServerConfig;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;


public class MobileGreaseBoardController extends SimpleFormController {
	
	private Log log = LogFactory.getLog(this.getClass());

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		return "testing";
	}
	
	@Override
	protected ModelAndView processFormSubmission(HttpServletRequest request, HttpServletResponse response, Object object,
	                                             BindException errors) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		if (Context.getAuthenticatedUser() == null) {
			return new ModelAndView(new RedirectView(getSuccessView()), map);
		}
		
		String patientId = request.getParameter("patientId");
		String formInstances = "";
		Integer firstFormId = null;
		ChirdlUtilBackportsService backportsService = Context.getService(ChirdlUtilBackportsService.class);
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			String formId = patientId + "_formId_" + i;
			String formIdStr = request.getParameter(formId);
			if (formIdStr == null) {
				break;
			}
			
			String formInstanceId = patientId + "_formInstanceId_" + i;
			String formInstanceIdStr = request.getParameter(formInstanceId);
			if (formInstanceIdStr == null) {
				break;
			}
			
			String locationId = patientId + "_locationId_" + i;
			String locationIdStr = request.getParameter(locationId);
			if (locationIdStr == null) {
				break;
			}
			
			FormInstance formInstance = new FormInstance(Integer.parseInt(locationIdStr), Integer.parseInt(formIdStr), 
				Integer.parseInt(formInstanceIdStr));
			List<PatientState> patientStates = backportsService.getPatientStatesByFormInstance(formInstance, false);
			Integer locationTagId = null;
			if (patientStates != null && patientStates.size() > 0) {
				for (PatientState patientState : patientStates) {
					locationTagId = patientState.getLocationTagId();
					if (locationTagId != null) {
						break;
					}
				}
			}
			
			String value = locationIdStr + "_" + locationTagId.toString() + "_" + formIdStr + "_" + formInstanceIdStr;
			if (i != 0) {
				formInstances += ",";
			} else {
				firstFormId = Integer.parseInt(formIdStr);
			}
			
			formInstances += value;
		}
		
		String nextPage = getFormUrl(firstFormId);
		map.put("patientId", patientId);
		map.put("formInstances", formInstances);
		return new ModelAndView(new RedirectView(nextPage), map);
	}
	
	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception {
		if (Context.getUserContext().getAuthenticatedUser() == null) {
			return null;
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			ArrayList<PatientRow> rows = new ArrayList<PatientRow>();
			String result = org.openmrs.module.chica.util.Util.getPatientsWithForms(rows, null);
			if (result != null) {
				map.put("errorMessage", result);
				return map;
			}
			
			map.put("patientRows", rows);
		}
		catch (UnexpectedRollbackException ex) {
			//ignore this exception since it happens with an APIAuthenticationException
		}
		catch (APIAuthenticationException ex2) {
			//ignore this exception. It happens during the redirect to the login page
		}
		catch (Throwable e) {
			log.error("Error retrieving awaiting patients", e);
			map.put("errorMessage", e.getMessage());
		}
		
		return map;
	}
	
	public String getFormUrl(Integer formId) {
		String url = null;
		try {
	        ServerConfig config = Util.getServerConfig();
	        String username = Context.getAuthenticatedUser().getUsername();
	        MobileClients clients = config.getMobileClients();
	        for (MobileClient client : clients.getMobileClients()) {
	        	if (username.equals(client.getUser())) {
	        		Form form = Context.getFormService().getForm(formId);
	        		String formName = form.getName();
	        		MobileForms forms = client.getMobileForms();
	        		for (MobileForm mobileForm : forms.getMobileForms()) {
	        			if (formName.equals(mobileForm.getName())) {
	        				url = mobileForm.getPageUrl();
	        				break;
	        			}
	        		}
	        		
	        		break;
	        	}
	        }
	        
        }
        catch (FileNotFoundException e) {
	        log.error("Error finding server config file", e);
        }
        catch (JiBXException e) {
	        log.error("Error parsing server config file", e);
        }
        
        return url;
	}
}
