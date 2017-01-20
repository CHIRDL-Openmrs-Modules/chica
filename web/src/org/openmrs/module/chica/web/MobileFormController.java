package org.openmrs.module.chica.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.FieldType;
import org.openmrs.Form;
import org.openmrs.FormField;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.TeleformTranslator;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.atd.xmlBeans.Field;
import org.openmrs.module.atd.xmlBeans.Record;
import org.openmrs.module.atd.xmlBeans.Records;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutil.util.XMLUtil;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstanceTag;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

public class MobileFormController extends SimpleFormController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	private static final String PROVIDER_VIEW = "_provider_view";
	private static final String PROVIDER_SUBMIT = "_provider_submit";
	
	private static final String PARAM_ENCOUNTER_ID = "encounterId";
	private static final String PARAM_SESSION_ID = "sessionId";
	private static final String PARAM_PATIENT_ID = "patientId";
	private static final String PARAM_PATIENT = "patient";
	private static final String PARAM_PROVIDER_ID = "providerId";
	private static final String PARM_FORM_INSTANCE = "formInstance";
	private static final String PARAM_FORM_ID = "formId";
	private static final String PARAM_FORM_INSTANCE_ID = "formInstanceId";
	private static final String PARAM_LOCATION_ID = "locationId";
	private static final String PARAM_LOCATION_TAG_ID = "locationTagId";
	private static final String PARAM_ERROR_MESSAGE = "errorMessage";
	private static final String PARAM_SESSION_TIMEOUT_WARNING = "sessionTimeoutWarning";
	
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
	protected Map<String, Object> referenceData(HttpServletRequest request) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		
		String encounterIdStr = request.getParameter(PARAM_ENCOUNTER_ID);
		map.put(PARAM_ENCOUNTER_ID, encounterIdStr);
		map.put(PARAM_SESSION_ID, request.getParameter(PARAM_SESSION_ID));
		
		String patientIdStr = request.getParameter(PARAM_PATIENT_ID);
		Patient patient = Context.getPatientService().getPatient(Integer.parseInt(patientIdStr));
		map.put(PARAM_PATIENT, patient);
		
		String providerId = request.getParameter(PARAM_PROVIDER_ID);
		map.put(PARAM_PROVIDER_ID, providerId);
		
		String formInstance = request.getParameter(PARM_FORM_INSTANCE);
		FormInstanceTag formInstTag = FormInstanceTag.parseFormInstanceTag(formInstance);
		Integer locationId = formInstTag.getLocationId();
		Integer formId = formInstTag.getFormId();
		Integer formInstanceId = formInstTag.getFormInstanceId();
		Integer locationTagId = formInstTag.getLocationTagId();
		Integer encounterId = Integer.parseInt(encounterIdStr);
		map.put(PARM_FORM_INSTANCE, formInstance);
		map.put(PARAM_FORM_ID, formId);
		map.put(PARAM_FORM_INSTANCE_ID, formInstanceId);
		map.put(PARAM_LOCATION_ID, locationId);
		map.put(PARAM_LOCATION_TAG_ID, locationTagId);
		
		//Run this to show the form
		try {
			showForm(map, formInstTag);
		} catch (Exception e) {
			String message = 
				"Error retrieving data to display a form. Please contact support with the following information: Form ID: " + 
				formId + " Form Instance ID: " + formInstanceId + " Location ID: " + locationId;
			log.error(message);
			map.put(PARAM_ERROR_MESSAGE, message);
			return map;
		}
		
		// Save who is viewing the form.
		if (providerId != null && providerId.trim().length() > 0) {
			saveProviderViewer(patient, encounterId, providerId, formInstTag);
		} else {
			log.error("No valid providerId provided.  Cannot log who is viewing form: " + formId);
		}
		
		// Add session timeout information
		Integer sessionTimeoutWarning = 180;
		String sessionTimeoutWarningStr = Context.getAdministrationService().getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_SESSION_TIMEOUT_WARNING);
		if (sessionTimeoutWarningStr == null || sessionTimeoutWarningStr.trim().length() == 0) {
			log.warn("The " + ChirdlUtilConstants.GLOBAL_PROP_SESSION_TIMEOUT_WARNING + " global property does not have a value set.  180 seconds "
					+ "will be used as a default value.");
		} else {
			try {
				sessionTimeoutWarning = Integer.parseInt(sessionTimeoutWarningStr);
			} catch (NumberFormatException e) {
				log.error("The " + ChirdlUtilConstants.GLOBAL_PROP_SESSION_TIMEOUT_WARNING + " global property is not a valid Integer.  180 seconds "
						+ "will be used as a default value");
				sessionTimeoutWarning = 180;
			}
		}
		
		map.put(PARAM_SESSION_TIMEOUT_WARNING, sessionTimeoutWarning);
		
		return map;
	}
	
	@Override
	protected ModelAndView processFormSubmission(HttpServletRequest request, HttpServletResponse response, Object object,
	                                             BindException errors) throws Exception {
		Integer chosenFormId = null;
		Integer chosenFormInstanceId = null;
		Integer chosenLocationId = null;
		Integer chosenLocationTagId = null;
		
		//parse out the location_id,form_id,location_tag_id, and form_instance_id
		//from the selected form
		String formInstance = request.getParameter(PARM_FORM_INSTANCE);
		FormInstanceTag formInstTag = null;
		if (formInstance != null && formInstance.trim().length() > 0) {
			formInstTag = FormInstanceTag.parseFormInstanceTag(formInstance);
			chosenLocationId = formInstTag.getLocationId();
			chosenLocationTagId = formInstTag.getLocationTagId();
			chosenFormId = formInstTag.getFormId();
			chosenFormInstanceId = formInstTag.getFormInstanceId();
		}
		
		TeleformTranslator translator = new TeleformTranslator();
		File mergeFile = IOUtil.getMergeFile(formInstTag);
		if (mergeFile != null) {
			InputStream input = new FileInputStream(mergeFile);
			try {
				scanForm(chosenFormId, chosenFormInstanceId, chosenLocationTagId, chosenLocationId, translator, input,
				    request, mergeFile);
			} catch (Exception e) {
				log.error("Error scanning form", e);
			}
		} 
		
		Map<String, Object> map = new HashMap<String, Object>();
		String patientIdStr = request.getParameter(PARAM_PATIENT_ID);
		map.put(PARAM_PATIENT_ID, patientIdStr);
		String view = getSuccessView();
		
		// Save who is submitting the form.
		String providerId = request.getParameter(PARAM_PROVIDER_ID);
		if (providerId != null && providerId.trim().length() > 0) {
			Integer patientId = Integer.parseInt(patientIdStr);
			Patient patient = Context.getPatientService().getPatient(patientId);
			Integer encounterId = Integer.parseInt(request.getParameter(PARAM_ENCOUNTER_ID));
			saveProviderSubmitter(patient, encounterId, providerId, formInstTag);
		} else {
			log.error("No valid providerId provided.  Cannot log who is submitting form: " + chosenFormId);
		}
		
		return new ModelAndView(new RedirectView(view), map);
	}
	
	private void showForm(Map<String, Object> map, FormInstanceTag formInstanceTag) throws Exception {
		Records records = Context.getService(ATDService.class).getFormRecords(formInstanceTag);
		if (records == null) {
			return;
		}
		
		Record record = records.getRecord();
		if (record == null) {
			return;
		}
		
		List<Field> fields = record.getFields();
		if (fields == null) {
			return;
		}
		
		for (Field field : fields) {
			if (field.getId() != null && field.getValue() != null) {
				map.put(field.getId(), field.getValue());
			}
		}
	}
	
	private void scanForm(Integer formId, Integer formInstanceId, Integer locationTagId, Integer locationId,
	                             TeleformTranslator translator, InputStream inputMergeFile, HttpServletRequest request,
	                             File mergeFile) throws Exception {
		//pull all the input fields from the database for the form
		FormService formService = Context.getFormService();
		HashSet<String> inputFields = new HashSet<String>();
		Form form = formService.getForm(formId);
		Set<FormField> formFields = form.getFormFields();
		FieldType exportFieldType = translator.getFieldType(ChirdlUtilConstants.FORM_FIELD_TYPE_EXPORT);
		for (FormField formField : formFields) {
			org.openmrs.Field currField = formField.getField();
			FieldType fieldType = currField.getFieldType();
			if (fieldType != null && fieldType.equals(exportFieldType)) {
				inputFields.add(currField.getName());
			}
		}
		
		Records records = (Records) XMLUtil.deserializeXML(Records.class, inputMergeFile);
		inputMergeFile.close();
		Record record = records.getRecord();
		for (String inputField : inputFields) {
			String inputVal = request.getParameter(inputField);
			if (inputVal == null) {
				// Create a new Field with no value
				Field field = new Field();
				field.setId(inputField);
				record.addField(field);
				continue;
			}
			
			// See if the field exists in the XML
			boolean found = false;
			for (Field currField : record.getFields()) {
				String name = currField.getId();
				if (inputField.equals(name)) {
					found = true;
					currField.setValue(inputVal);
					break;
				}
			}
			
			if (!found) {
				// Create a new Field
				Field field = new Field();
				field.setId(inputField);
				field.setValue(inputVal);
				record.addField(field);
			}
		}
		
		String exportDirectory = IOUtil.formatDirectoryName(org.openmrs.module.chirdlutilbackports.util.Util
		        .getFormAttributeValue(formId, ChirdlUtilConstants.FORM_ATTR_DEFAULT_EXPORT_DIRECTORY, locationTagId, locationId));
		String defaultMergeDirectory = IOUtil.formatDirectoryName(org.openmrs.module.chirdlutilbackports.util.Util
		        .getFormAttributeValue(formId, ChirdlUtilConstants.FORM_ATTR_DEFAULT_MERGE_DIRECTORY, locationTagId, locationId));
		
		FormInstance formInstance = new FormInstance(locationId, formId, formInstanceId);
		//Write the xml for the export file
		//Use xmle extension to represent form completion through electronic means.
		String exportFilename = exportDirectory + formInstance.toString() + ChirdlUtilConstants.FILE_EXTENSION_XMLE;
		
		OutputStream output = new FileOutputStream(exportFilename);
		XMLUtil.serializeXML(records, output);
		output.flush();
		output.close();
		
		//rename the merge file to trigger state change
		String newMergeFilename = defaultMergeDirectory + formInstance.toString() + ChirdlUtilConstants.FILE_EXTENSION_20;
		File newFile = new File(newMergeFilename);
		if (!newFile.exists()) {
			IOUtil.copyFile(mergeFile.getAbsolutePath(), newMergeFilename);
			IOUtil.deleteFile(mergeFile.getAbsolutePath());
		}
	}
	
	/**
	 * Saves the viewer's provider ID to an observation.
	 * 
	 * @param patient The patient who owns the form.
	 * @param formId The ID of the form being viewed.
	 * @param encounterId The encounter ID of the encounter where the form was created.
	 * @param providerId The ID of the provider to be stored.
	 * @param formInstTag The form instance tag information
	 */
	private void saveProviderViewer(Patient patient, Integer encounterId, String providerId, FormInstanceTag formInstTag) {
		Form form = Context.getFormService().getForm(formInstTag.getFormId());
		String conceptName = form.getName() + PROVIDER_VIEW;
		saveProviderInfo(patient, encounterId, providerId, conceptName, formInstTag);
	}
	
	/**
	 * Saves the submitter's provider ID to an observation.
	 * 
	 * @param patient The patient who owns the form.
	 * @param formId The ID of the form being viewed.
	 * @param encounterId The encounter ID of the encounter where the form was created.
	 * @param providerId The ID of the provider to be stored.
	 * @param formInstTag The form instance tag information
	 */
	private void saveProviderSubmitter(Patient patient, Integer encounterId, String providerId, FormInstanceTag formInstTag) {
		Form form = Context.getFormService().getForm(formInstTag.getFormId());
		String conceptName = form.getName() + PROVIDER_SUBMIT;
		saveProviderInfo(patient, encounterId, providerId, conceptName, formInstTag);
	}
	
	/**
	 * Saves the submitter's provider ID to an observation.
	 * 
	 * @param patient The patient who owns the form.
	 * @param formId The ID of the form being viewed.
	 * @param encounterId The encounter ID of the encounter where the form was created.
	 * @param providerId The ID of the provider to be stored.
	 * @param conceptName The name of the concept.
	 * @param formInstTag The form instance tag information
	 */
	private void saveProviderInfo(Patient patient, Integer encounterId, String providerId, String conceptName, FormInstanceTag formInstTag) {
		ConceptService conceptService = Context.getConceptService();
		Concept concept = conceptService.getConceptByName(conceptName);
		if (concept == null) {
			log.error("Could not log provider info.  Concept " + conceptName + " not found.");
			return;
		}
		FormInstance formInstance = new FormInstance(formInstTag.getLocationId(),formInstTag.getFormId(),formInstTag.getFormInstanceId());
		org.openmrs.module.chica.util.Util.saveObsWithStatistics(patient, concept, encounterId, providerId, formInstance, null, formInstTag.getLocationTagId(), null);
	}
}
