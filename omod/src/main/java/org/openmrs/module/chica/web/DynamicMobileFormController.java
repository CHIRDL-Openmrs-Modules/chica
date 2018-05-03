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
import org.openmrs.module.dss.hibernateBeans.RuleEntry;
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
	
	private static final String PARAM_ERROR_MESSAGE = "errorMessage";
	private static final String PARAM_LOCATION_TAG_ID = "locationTagId";
	private static final String PARAM_LOCATION_ID = "locationId";
	private static final String PARAM_FORM_INSTANCE_ID = "formInstanceId";
	private static final String PARAM_FORM_ID = "formId";
	private static final String PARAM_FORM_INSTANCE = "formInstance";
	private static final String PARAM_PATIENT = "patient";
	private static final String PARAM_PATIENT_ID = "patientId";
	private static final String PARAM_LANGUAGE = "language";
	private static final String PARAM_SESSION_ID = "sessionId";
	private static final String PARAM_ENCOUNTER_ID = "encounterId";
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
		
		String encounterIdStr = request.getParameter(PARAM_ENCOUNTER_ID);
		Integer encounterId = Integer.parseInt(encounterIdStr);
		map.put(PARAM_ENCOUNTER_ID, encounterIdStr);
		
		String sessionIdStr = request.getParameter(PARAM_SESSION_ID);
		map.put(PARAM_SESSION_ID, sessionIdStr);
		
		String language = request.getParameter(PARAM_LANGUAGE);
		map.put(PARAM_LANGUAGE, language);
		
		String patientIdStr = request.getParameter(PARAM_PATIENT_ID);
		Patient patient = Context.getPatientService().getPatient(Integer.parseInt(patientIdStr));
		map.put(PARAM_PATIENT, patient);
		
		String formInstance = request.getParameter(PARAM_FORM_INSTANCE);
		FormInstanceTag formInstTag = FormInstanceTag.parseFormInstanceTag(formInstance);
		Integer locationId = formInstTag.getLocationId();
		Integer formId = formInstTag.getFormId();
		Integer formInstanceId = formInstTag.getFormInstanceId();
		map.put(PARAM_FORM_ID, formId);
		map.put(PARAM_FORM_INSTANCE_ID, formInstanceId);
		map.put(PARAM_LOCATION_ID, locationId);
		map.put(PARAM_LOCATION_TAG_ID, formInstTag.getLocationTagId());
		map.put(PARAM_FORM_INSTANCE, formInstance);
		
		//Run this to show the form
		try {
			showForm(map, formId, formInstanceId, locationId, encounterId);
		} catch (Exception e) {
			String message = 
				"Error retrieving data to display a form. Please contact support with the following information: Form ID: " + 
				formId + " Form Instance ID: " + formInstanceId + " Location ID: " + locationId;
			log.error(message);
			map.put(PARAM_ERROR_MESSAGE, message);
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
		String patientIdStr = request.getParameter(PARAM_PATIENT_ID);
		Integer patientId = Integer.parseInt(patientIdStr);
		Integer formId = Integer.parseInt(request.getParameter(PARAM_FORM_ID));
		Integer formInstanceId = Integer.parseInt(request.getParameter(PARAM_FORM_INSTANCE_ID));
		String locationIdStr = request.getParameter(PARAM_LOCATION_ID);
		Integer locationId = Integer.parseInt(locationIdStr);
		String locationTagIdStr = request.getParameter(PARAM_LOCATION_TAG_ID);
		Integer locationTagId = Integer.parseInt(locationTagIdStr);
		String encounterIdStr = request.getParameter(PARAM_ENCOUNTER_ID);
		Integer encounterId = Integer.parseInt(encounterIdStr);
		String sessionIdStr = request.getParameter(PARAM_SESSION_ID);
		Integer sessionId = Integer.parseInt(sessionIdStr);
		
		Map<String, String[]> parameterMap = request.getParameterMap();
		try {
			ParameterHandler parameterHandler = new ChicaParameterHandler();
			DynamicFormAccess formAccess = new DynamicFormAccess();
			Patient patient = Context.getPatientService().getPatient(patientId);
			formAccess.saveExportElements(new FormInstance(locationId, formId, formInstanceId), locationTagId, encounterId, 
				patient, parameterMap, parameterHandler);
			
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
		map.put(PARAM_PATIENT_ID, patientIdStr);
		map.put(PARAM_LOCATION_ID, locationIdStr);
		map.put(PARAM_LOCATION_TAG_ID, locationTagIdStr);
		map.put(PARAM_ENCOUNTER_ID, encounterIdStr);
		map.put(PARAM_SESSION_ID, sessionIdStr);
		String language = request.getParameter(PARAM_LANGUAGE);
		map.put(PARAM_LANGUAGE, language);
		
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
		
		// DWE CHICA-430 Get existing values for "Export Field" types
		Integer locationTagId = (Integer)map.get("locationTagId");
		fields = formAccess.getExportElements(formId, formInstanceId, encounterId, locationTagId);
		for (org.openmrs.module.atd.xmlBeans.Field field : fields) 
		{
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
					List<RuleEntry> nonPriorRuleEntries = dssService.getNonPrioritizedRuleEntries(form.getName());
					
					for (RuleEntry currRuleEntry : nonPriorRuleEntries) {
						Rule currRule = currRuleEntry.getRule();
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
