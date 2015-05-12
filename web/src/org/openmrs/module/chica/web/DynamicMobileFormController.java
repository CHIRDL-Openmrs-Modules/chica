package org.openmrs.module.chica.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.ParameterHandler;
import org.openmrs.module.chica.ChicaParameterHandler;
import org.openmrs.module.chica.DynamicFormAccess;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.BaseStateActionHandler;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstanceTag;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.module.dss.hibernateBeans.Rule;
import org.openmrs.module.dss.service.DssService;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Controller for forms that have their parts built dynamically.
 *
 * @author Steve McKee
 */
public class DynamicMobileFormController extends SimpleFormController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		return "testing";
	}
	
	/**
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#referenceData(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Map<String, Object> referenceData(HttpServletRequest request) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		
		String encounterIdStr = request.getParameter("encounterId");
		Integer encounterId = Integer.parseInt(encounterIdStr);
		map.put("encounterId", encounterIdStr);
		
		String sessionIdStr = request.getParameter("sessionId");
		map.put("sessionId", sessionIdStr);
		
		String language = request.getParameter("language");
		map.put("language", language);
		
		String patientIdStr = request.getParameter("patientId");
		Patient patient = Context.getPatientService().getPatient(Integer.parseInt(patientIdStr));
		map.put("patient", patient);
		
		String formInstance = request.getParameter("formInstance");
		FormInstanceTag formInstTag = FormInstanceTag.parseFormInstanceTag(formInstance);
		Integer locationId = formInstTag.getLocationId();
		Integer formId = formInstTag.getFormId();
		Integer formInstanceId = formInstTag.getFormInstanceId();
		map.put("formId", formId);
		map.put("formInstanceId", formInstanceId);
		map.put("locationId", locationId);
		map.put("locationTagId", formInstTag.getLocationTagId());
		map.put("formInstance", formInstance);
		
		//Run this to show the form
		try {
			showForm(map, formId, formInstanceId, locationId, encounterId);
		} catch (Exception e) {
			String message = 
				"Error retrieving data to display a form. Please contact support with the following information: Form ID: " + 
				formId + " Form Instance ID: " + formInstanceId + " Location ID: " + locationId;
			log.error(message);
			map.put("errorMessage", message);
			return map;
		}
		
		return map;
	}
	
	/**
	 * @see org.springframework.web.servlet.mvc.SimpleFormController#processFormSubmission(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.validation.BindException)
	 */
	@Override
	protected ModelAndView processFormSubmission(HttpServletRequest request, HttpServletResponse response, Object object,
	                                             BindException errors) throws Exception {
		String patientIdStr = request.getParameter("patientId");
		Integer patientId = Integer.parseInt(patientIdStr);
		Integer formId = Integer.parseInt(request.getParameter("formId"));
		Integer formInstanceId = Integer.parseInt(request.getParameter("formInstanceId"));
		Integer locationId = Integer.parseInt(request.getParameter("locationId"));
		Integer locationTagId = Integer.parseInt(request.getParameter("locationTagId"));
		Integer encounterId = Integer.parseInt(request.getParameter("encounterId"));
		Integer sessionId = Integer.parseInt(request.getParameter("sessionId"));
		
		Map<String, String[]> parameterMap = request.getParameterMap();
		try {
			ParameterHandler parameterHandler = new ChicaParameterHandler();
			DynamicFormAccess formAccess = new DynamicFormAccess();
			Patient patient = Context.getPatientService().getPatient(patientId);
			formAccess.saveExportElements(new FormInstance(locationId, formId, formInstanceId), locationTagId, encounterId, 
				patient, parameterMap, parameterHandler);
			// Calculate all the percentiles
			org.openmrs.module.chica.util.Util.calculatePercentiles(encounterId, patient, locationTagId);
			
			// Run null priority rules
			HashMap<String, Object> parameters = new HashMap<String, Object>();
			FormInstance formInstance = new FormInstance(locationId, formId, formInstanceId);
			parameters.put("formInstance", formInstance);
			parameters.put("sessionId", sessionId);
			parameters.put("locationTagId", locationTagId);
			parameters.put("locationId", locationId);
			parameters.put("location", Context.getLocationService().getLocation(locationId).getName());
			parameters.put("encounterId", encounterId);
			parameters.put("mode", "CONSUME");
			completeForm(formId, patient, parameters, formInstance);
		} catch (Exception e) {
			log.error("Error saving form", e);
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("patientId", patientIdStr);
		String language = request.getParameter("language");
		map.put("language", language);
		
		String view = getSuccessView();
		return new ModelAndView(new RedirectView(view), map);
	}
	
	/**
	 * Build the form information from the merge fields of the form.
	 * 
	 * @param map The map for the HTTP response.
	 * @param formId The form identifier.
	 * @param formInstanceId The form instance identifier.
	 * @param locationId The location identifier.
	 * @param encounterId The encounter identifier.
	 * @throws Exception
	 */
	private static void showForm(Map<String, Object> map, Integer formId, Integer formInstanceId, Integer locationId, 
	                             Integer encounterId) throws Exception {
		DynamicFormAccess formAccess = new DynamicFormAccess();
		
		List<org.openmrs.module.atd.xmlBeans.Field> fields = 
			formAccess.getMergeElements(formId, formInstanceId, encounterId);
		for (org.openmrs.module.atd.xmlBeans.Field field : fields) {
			map.put(field.getId(), field.getValue());
		}
	}
	
	/**
	 * Changes to the next state in the state flow process.
	 * 
	 * @param formInstance The FormInstance object containing relevant form information.
	 * @param parameters Map containing parameters needed for the rules to execute.
	 */
	private void changeState(FormInstance formInstance, HashMap<String, Object> parameters) {
		ChirdlUtilBackportsService service = Context.getService(ChirdlUtilBackportsService.class);
		List<PatientState> states = service.getPatientStatesByFormInstance(formInstance, false);
		if (states != null && states.size() > 0) {
			for (PatientState formInstState : states) {
				
				// only process unfinished states for this sessionId
				if (formInstState.getEndTime() != null) {
					continue;
				}
				
				try {
					BaseStateActionHandler.getInstance().changeState(formInstState, parameters);
				}
				catch (Exception e) {
					log.error(e.getMessage());
					log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
				}
			}
		}
	}
	
	/**
	 * Runs all null priority rules and changes the patient state.
	 * 
	 * @param formId The identifier of the form to run the rules for.
	 * @param patient The patient to run the rules for.
	 * @param parameters Map containing parameters needed for the rules to execute.
	 * @param formInstance The instance of the form.
	 */
	private void completeForm(Integer formId, Patient patient, HashMap<String, Object> parameters, FormInstance formInstance) {
		Runnable runnable = new CompleteForm(patient.getPatientId(), formId, parameters, formInstance); 
		Thread thread = new Thread(runnable);  
		thread.start();  
	}
	
	/**
	 * Runs null priority rules for a patient/form and changes the state.
	 *
	 * @author Steve McKee
	 */
	public class CompleteForm implements Runnable {
		private Log log = LogFactory.getLog(this.getClass());
		private Integer patientId;
		private Integer formId;
		private HashMap<String, Object> parameters;
		private FormInstance formInstance;

		/**
		 * Constructor method
		 * 
		 * @param patientId Patient identifier
		 * @param formId Form identifier
		 * @param parameters HashMap of parameters for the rule execution
		 * @param formInstance The instance of the form
		 */
		public CompleteForm(Integer patientId, Integer formId, HashMap<String, Object> parameters, 
		                              FormInstance formInstance) {
			this.patientId = patientId;
			this.formId = formId;
			this.parameters = parameters;
			this.formInstance = formInstance;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			Context.openSession();
			try {
				try {
					AdministrationService adminService = Context.getAdministrationService();
					Context.authenticate(adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROPERTY_SCHEDULER_USERNAME), 
						adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROPERTY_SCHEDULER_PASSWORD));
	
					Patient patient = Context.getPatientService().getPatient(patientId);
					Form form = Context.getFormService().getForm(formId);
	
					DssService dssService = Context.getService(DssService.class);
					List<Rule> nonPriorRules = dssService.getNonPrioritizedRules(form.getName());
					
					for (Rule currRule : nonPriorRules) {
						if (currRule.checkAgeRestrictions(patient)) {
							currRule.setParameters(parameters);
							dssService.runRule(patient, currRule);
						}
					}
				} catch (Exception e) {
					this.log.error(e.getMessage());
					this.log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
				} 
			
				try {
					changeState(formInstance, parameters);
				} catch (Exception e) {
					this.log.error(e.getMessage());
					this.log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
				}
			} finally {
				Context.closeSession();
			}
		}
	}
}
