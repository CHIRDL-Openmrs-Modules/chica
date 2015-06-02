package org.openmrs.module.chica.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import org.openmrs.logic.LogicService;
import org.openmrs.module.atd.TeleformTranslator;
import org.openmrs.module.atd.datasource.FormDatasource;
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
		
		TeleformTranslator translator = new TeleformTranslator();
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
		String mergeFilename = getMergeFilename(locationId, locationTagId, formId, formInstanceId);
		if (mergeFilename == null) {
			String message = 
				"Could not locate form instance. Please contact support with the following information: Form ID: " + formId + 
				" Form Instance ID: " + formInstanceId + " Location ID: " + locationId;
			log.error(message);
			map.put(PARAM_ERROR_MESSAGE, message);
			return map;
		}
		
		InputStream input = new FileInputStream(mergeFilename);
		
		//Run this to show the form
		try {
			showForm(map, formId, formInstanceId, locationId, translator, input);
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
			saveProviderViewer(patient, formId, encounterId, providerId);
		} else {
			log.error("No valid providerId provided.  Cannot log who is viewing form: " + formId);
		}
		
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
		if (formInstance != null && formInstance.trim().length() > 0) {
			FormInstanceTag formInstTag = FormInstanceTag.parseFormInstanceTag(formInstance);
			chosenLocationId = formInstTag.getLocationId();
			chosenLocationTagId = formInstTag.getLocationTagId();
			chosenFormId = formInstTag.getFormId();
			chosenFormInstanceId = formInstTag.getFormInstanceId();
		}
		
		TeleformTranslator translator = new TeleformTranslator();
		String mergeFilename = getMergeFilename(chosenLocationId, chosenLocationTagId, chosenFormId, chosenFormInstanceId);
		if (mergeFilename != null) {
			InputStream input = new FileInputStream(mergeFilename);
			try {
				scanForm(chosenFormId, chosenFormInstanceId, chosenLocationTagId, chosenLocationId, translator, input,
				    request, mergeFilename);
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
			saveProviderSubmitter(patient, chosenFormId, encounterId, providerId);
		} else {
			log.error("No valid providerId provided.  Cannot log who is submitting form: " + chosenFormId);
		}
		
		return new ModelAndView(new RedirectView(view), map);
	}
	
	private String getMergeFilename(Integer chosenLocationId, Integer chosenLocationTagId, Integer chosenFormId, 
	                                Integer chosenFormInstanceId) {
		String mergeFilename = null;
		ArrayList<String> possibleMergeFilenames = new ArrayList<String>();
		String defaultMergeDirectory = IOUtil.formatDirectoryName(org.openmrs.module.chirdlutilbackports.util.Util
		        .getFormAttributeValue(chosenFormId, ChirdlUtilConstants.FORM_ATTR_DEFAULT_MERGE_DIRECTORY, chosenLocationTagId, chosenLocationId));
		String pendingMergeDirectory = IOUtil.formatDirectoryName(org.openmrs.module.chirdlutilbackports.util.Util
		        .getFormAttributeValue(chosenFormId, ChirdlUtilConstants.FORM_ATTR_DEFAULT_MERGE_DIRECTORY, chosenLocationTagId, chosenLocationId))
		        + ChirdlUtilConstants.FILE_PENDING + File.separator;
		
		// Parse the merge file
		FormInstance formInstance = new FormInstance(chosenLocationId, chosenFormId, chosenFormInstanceId);
		possibleMergeFilenames.add(defaultMergeDirectory + formInstance.toString() + ChirdlUtilConstants.FILE_EXTENSION_XML);
		possibleMergeFilenames.add(defaultMergeDirectory + formInstance.toString() + ChirdlUtilConstants.FILE_EXTENSION_20);
		possibleMergeFilenames.add(defaultMergeDirectory + formInstance.toString() + ChirdlUtilConstants.FILE_EXTENSION_22);
		possibleMergeFilenames.add(defaultMergeDirectory + formInstance.toString() + ChirdlUtilConstants.FILE_EXTENSION_23);
		possibleMergeFilenames.add(defaultMergeDirectory + formInstance.toString() + ChirdlUtilConstants.FILE_EXTENSION_19);
		possibleMergeFilenames.add(pendingMergeDirectory + formInstance.toString() + ChirdlUtilConstants.FILE_EXTENSION_XML);
		possibleMergeFilenames.add(pendingMergeDirectory + formInstance.toString() + ChirdlUtilConstants.FILE_EXTENSION_20);
		possibleMergeFilenames.add(pendingMergeDirectory + formInstance.toString() + ChirdlUtilConstants.FILE_EXTENSION_22);
		possibleMergeFilenames.add(pendingMergeDirectory + formInstance.toString() + ChirdlUtilConstants.FILE_EXTENSION_23);
		possibleMergeFilenames.add(pendingMergeDirectory + formInstance.toString() + ChirdlUtilConstants.FILE_EXTENSION_19);
		
		for (String currFilename : possibleMergeFilenames) {
			File file = new File(currFilename);
			if (file.exists()) {
				mergeFilename = currFilename;
				break;
			}
		}
		
		return mergeFilename;
	}
	
	private void showForm(Map<String, Object> map, Integer formId, Integer formInstanceId, Integer locationId,
	                             TeleformTranslator translator, InputStream inputMergeFile) throws Exception {
		FormService formService = Context.getFormService();
		LogicService logicService = Context.getLogicService();
		FormDatasource formDatasource = (FormDatasource) logicService.getLogicDataSource(ChirdlUtilConstants.DATA_SOURCE_FORM);
		HashMap<String, org.openmrs.module.atd.xmlBeans.Field> fieldMap = formDatasource.getFormFields(new FormInstance(
		        locationId, formId, formInstanceId));
		
		//Parse the merge file to get the field values to display
		FormInstance formInstance = formDatasource.parseTeleformXmlFormat(inputMergeFile, null, null);
		inputMergeFile.close();
		fieldMap = formDatasource.getFormFields(formInstance);
		
		Form form = formService.getForm(formId);
		Set<FormField> formFields = form.getFormFields();
		
		//store the values of fields in the jsp map
		for (FormField formField : formFields) {
			org.openmrs.Field currField = formField.getField();
			FieldType fieldType = currField.getFieldType();
			if (fieldType == null || !fieldType.equals(translator.getFieldType(ChirdlUtilConstants.FORM_FIELD_TYPE_EXPORT))) {
				Field lookupField = fieldMap.get(currField.getName());
				if (lookupField != null) {
					map.put(currField.getName(), lookupField.getValue());
				}
			}
		}
	}
	
	private void scanForm(Integer formId, Integer formInstanceId, Integer locationTagId, Integer locationId,
	                             TeleformTranslator translator, InputStream inputMergeFile, HttpServletRequest request,
	                             String mergeFilename) throws Exception {
		//pull all the input fields from the database for the form
		FormService formService = Context.getFormService();
		HashSet<String> inputFields = new HashSet<String>();
		Form form = formService.getForm(formId);
		Set<FormField> formFields = form.getFormFields();
		for (FormField formField : formFields) {
			org.openmrs.Field currField = formField.getField();
			FieldType fieldType = currField.getFieldType();
			if (fieldType != null && fieldType.equals(translator.getFieldType(ChirdlUtilConstants.FORM_FIELD_TYPE_EXPORT))) {
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
			IOUtil.copyFile(mergeFilename, newMergeFilename);
			IOUtil.deleteFile(mergeFilename);
		}
	}
	
	/**
	 * Saves the viewer's provider ID to an observation.
	 * 
	 * @param patient The patient who owns the form.
	 * @param formId The ID of the form being viewed.
	 * @param encounterId The encounter ID of the encounter where the form was created.
	 * @param providerId The ID of the provider to be stored.
	 */
	private void saveProviderViewer(Patient patient, Integer formId, Integer encounterId, String providerId) {
		Form form = Context.getFormService().getForm(formId);
		String conceptName = form.getName() + PROVIDER_VIEW;
		saveProviderInfo(patient, formId, encounterId, providerId, conceptName);
	}
	
	/**
	 * Saves the submitter's provider ID to an observation.
	 * 
	 * @param patient The patient who owns the form.
	 * @param formId The ID of the form being viewed.
	 * @param encounterId The encounter ID of the encounter where the form was created.
	 * @param providerId The ID of the provider to be stored.
	 */
	private void saveProviderSubmitter(Patient patient, Integer formId, Integer encounterId, String providerId) {
		Form form = Context.getFormService().getForm(formId);
		String conceptName = form.getName() + PROVIDER_SUBMIT;
		saveProviderInfo(patient, formId, encounterId, providerId, conceptName);
	}
	
	/**
	 * Saves the submitter's provider ID to an observation.
	 * 
	 * @param patient The patient who owns the form.
	 * @param formId The ID of the form being viewed.
	 * @param encounterId The encounter ID of the encounter where the form was created.
	 * @param providerId The ID of the provider to be stored.
	 * @param conceptName The name of the concept.
	 */
	private void saveProviderInfo(Patient patient, Integer formId, Integer encounterId, String providerId, String conceptName) {
		ConceptService conceptService = Context.getConceptService();
		Concept concept = conceptService.getConceptByName(conceptName);
		if (concept == null) {
			log.error("Could not log provider info.  Concept " + conceptName + " not found.");
			return;
		}
		
		Util.saveObs(patient, concept, encounterId, providerId, new Date());
	}
}
