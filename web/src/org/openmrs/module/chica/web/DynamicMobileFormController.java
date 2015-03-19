package org.openmrs.module.chica.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.ParameterHandler;
import org.openmrs.module.chica.ChicaParameterHandler;
import org.openmrs.module.chica.DynamicFormAccess;
import org.openmrs.module.chica.advice.ChangeState;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstanceTag;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
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
			// Calculate all the percentiles
			org.openmrs.module.chica.util.Util.calculatePercentiles(encounterId, patient, locationTagId);
			FormInstance formInstance = new FormInstance(locationId, formId, formInstanceId);
			changeState(locationTagId, encounterId, sessionId, formInstance);
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
	}
	
	/**
	 * Changes to the next state in the state flow process.
	 * 
	 * @param locationTagId The location tag identifier.
	 * @param encounterId The encounter identifier.
	 * @param sessionId The session identifier.
	 * @param formInstance The FormInstance object containing relevant form information.
	 */
	private void changeState(Integer locationTagId, Integer encounterId, Integer sessionId,
	                         FormInstance formInstance) {
		ChirdlUtilBackportsService service = Context.getService(ChirdlUtilBackportsService.class);
		List<PatientState> states = service.getPatientStatesByFormInstance(formInstance, false);
		if (states != null && states.size() > 0) {
			Integer locationId = formInstance.getLocationId();
			Location location = Context.getLocationService().getLocation(locationId);
			for (PatientState formInstState : states) {
				
				// only process unfinished states for this sessionId
				if (formInstState.getEndTime() != null) {
					continue;
				}
				
				try {
					HashMap<String, Object> parameters = new HashMap<String, Object>();
					parameters.put(ChirdlUtilConstants.PARAMETER_SESSION_ID, sessionId);
					parameters.put(ChirdlUtilConstants.PARAMETER_FORM_INSTANCE, formInstance);
					parameters.put(ChirdlUtilConstants.PARAMETER_LOCATION_TAG_ID, locationTagId);
					parameters.put(ChirdlUtilConstants.PARAMETER_LOCATION_ID, locationId);
					parameters.put(ChirdlUtilConstants.PARAMETER_LOCATION, location.getName());
					parameters.put(ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID, encounterId);
					parameters.put(ChirdlUtilConstants.PARAMETER_MODE, ChirdlUtilConstants.PARAMETER_VALUE_CONSUME);
					Runnable runnable = new ChangeState(formInstState, parameters);
					Thread thread = new Thread(runnable);
					thread.start();
				}
				catch (Exception e) {
					log.error(e.getMessage());
					log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
				}
			}
		}
	}
	
}
