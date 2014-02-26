package org.openmrs.module.chica.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jibx.runtime.JiBXException;
import org.openmrs.FieldType;
import org.openmrs.Form;
import org.openmrs.FormField;
import org.openmrs.Patient;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.module.atd.TeleformTranslator;
import org.openmrs.module.atd.datasource.TeleformExportXMLDatasource;
import org.openmrs.module.atd.xmlBeans.Field;
import org.openmrs.module.atd.xmlBeans.Record;
import org.openmrs.module.atd.xmlBeans.Records;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutil.util.XMLUtil;
import org.openmrs.module.chirdlutil.xmlBeans.serverconfig.MobileClient;
import org.openmrs.module.chirdlutil.xmlBeans.serverconfig.MobileClients;
import org.openmrs.module.chirdlutil.xmlBeans.serverconfig.MobileForm;
import org.openmrs.module.chirdlutil.xmlBeans.serverconfig.MobileForms;
import org.openmrs.module.chirdlutil.xmlBeans.serverconfig.ServerConfig;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

public class MobileFormQueueController extends SimpleFormController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
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
	protected Map referenceData(HttpServletRequest request) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		
		String formInstancesStr = request.getParameter("formInstances");
		map.put("formInstances", formInstancesStr);
		
		String patientIdStr = request.getParameter("patientId");
		Patient patient = Context.getPatientService().getPatient(Integer.parseInt(patientIdStr));
		map.put("patient", patient);
		
		TeleformTranslator translator = new TeleformTranslator();
		FormInstancePlus formInstPlus = getFirstFormInstance(formInstancesStr);
		Integer locationId = formInstPlus.getLocationId();
		Integer formId = formInstPlus.getFormId();
		Integer formInstanceId = formInstPlus.getFormInstanceId();
		map.put("formInstance", locationId + "_" + formId + "_" + formInstanceId);
		String mergeFilename = getMergeFilename(formInstPlus.getLocationId(), formInstPlus.getLocationTagId(), 
			formInstPlus.getFormId(), formInstPlus.getFormInstanceId());
		if (mergeFilename == null) {
			String message = 
				"Could not locate form instance. Please contact support with the following information: Form ID: " + formId + 
				" Form Instance ID: " + formInstanceId + " Location ID: " + locationId;
			log.error(message);
			map.put("errorMessage", message);
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
			map.put("errorMessage", message);
			return map;
		}
		
		return map;
	}
	
	@Override
	protected ModelAndView processFormSubmission(HttpServletRequest request, HttpServletResponse response, Object object,
	                                             BindException errors) throws Exception {
		String formInstancesStr = request.getParameter("formInstances");
		Integer chosenFormId = null;
		Integer chosenFormInstanceId = null;
		Integer chosenLocationId = null;
		Integer chosenLocationTagId = null;
		String newFormInstancesStr = "";
		
		//parse out the location_id,form_id,location_tag_id, and form_instance_id
		//from the selected form
		if (formInstancesStr != null && formInstancesStr.trim().length() > 0) {
			StringTokenizer instTokenizer = new StringTokenizer(formInstancesStr, ",");
			int i = 0;
			while (instTokenizer.hasMoreTokens()) {
				String instance = instTokenizer.nextToken();
				if (i == 0) {
					StringTokenizer tokenizer = new StringTokenizer(instance, "_");
					try {
						chosenLocationId = Integer.parseInt(tokenizer.nextToken());
						chosenLocationTagId = Integer.parseInt(tokenizer.nextToken());
						chosenFormId = Integer.parseInt(tokenizer.nextToken());
						chosenFormInstanceId = Integer.parseInt(tokenizer.nextToken());
					}
					catch (NumberFormatException e) {}
				} else {
					if (i > 1) {
						newFormInstancesStr += ",";
					}
					
					newFormInstancesStr += instance;
				}
				
				i++;
			}
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
		String patientIdStr = request.getParameter("patientId");
		map.put("patientId", patientIdStr);
		String view = null;
		if (newFormInstancesStr.trim().length() > 0) {
			FormInstancePlus formInstPlus = getFirstFormInstance(newFormInstancesStr);
			if (formInstPlus == null) {
				view = getSuccessView();
			} else {
				view = getFormUrl(formInstPlus.getFormId());
				if (view == null) {
					view = getSuccessView();
				}
				
				map.put("formInstances", newFormInstancesStr);
			}
		} else {
			view = getSuccessView();
		}
		
		return new ModelAndView(new RedirectView(view), map);
	}
	
	private String getMergeFilename(Integer chosenLocationId, Integer chosenLocationTagId, Integer chosenFormId, 
	                                Integer chosenFormInstanceId) {
		String mergeFilename = null;
		ArrayList<String> possibleMergeFilenames = new ArrayList<String>();
		String defaultMergeDirectory = IOUtil.formatDirectoryName(org.openmrs.module.chirdlutilbackports.util.Util
		        .getFormAttributeValue(chosenFormId, "defaultMergeDirectory", chosenLocationTagId, chosenLocationId));
		String pendingMergeDirectory = IOUtil.formatDirectoryName(org.openmrs.module.chirdlutilbackports.util.Util
		        .getFormAttributeValue(chosenFormId, "defaultMergeDirectory", chosenLocationTagId, chosenLocationId))
		        + "Pending/";
		
		// Parse the merge file
		FormInstance formInstance = new FormInstance(chosenLocationId, chosenFormId, chosenFormInstanceId);
		possibleMergeFilenames.add(defaultMergeDirectory + formInstance.toString() + ".xml");
		possibleMergeFilenames.add(defaultMergeDirectory + formInstance.toString() + ".20");
		possibleMergeFilenames.add(defaultMergeDirectory + formInstance.toString() + ".22");
		possibleMergeFilenames.add(defaultMergeDirectory + formInstance.toString() + ".23");
		possibleMergeFilenames.add(defaultMergeDirectory + formInstance.toString() + ".19");
		possibleMergeFilenames.add(pendingMergeDirectory + formInstance.toString() + ".xml");
		possibleMergeFilenames.add(pendingMergeDirectory + formInstance.toString() + ".20");
		possibleMergeFilenames.add(pendingMergeDirectory + formInstance.toString() + ".22");
		possibleMergeFilenames.add(pendingMergeDirectory + formInstance.toString() + ".23");
		possibleMergeFilenames.add(pendingMergeDirectory + formInstance.toString() + ".19");
		
		for (String currFilename : possibleMergeFilenames) {
			File file = new File(currFilename);
			if (file.exists()) {
				mergeFilename = currFilename;
				break;
			}
		}
		
		return mergeFilename;
	}
	
	private static void showForm(Map map, Integer formId, Integer formInstanceId, Integer locationId,
	                             TeleformTranslator translator, InputStream inputMergeFile) throws Exception {
		FormService formService = Context.getFormService();
		LogicService logicService = Context.getLogicService();
		TeleformExportXMLDatasource xmlDatasource = (TeleformExportXMLDatasource) logicService.getLogicDataSource("xml");
		HashMap<String, org.openmrs.module.atd.xmlBeans.Field> fieldMap = xmlDatasource.getParsedFile(new FormInstance(
		        locationId, formId, formInstanceId));
		
		//Parse the merge file to get the field values to display
		FormInstance formInstance = xmlDatasource.parse(inputMergeFile, null, null);
		inputMergeFile.close();
		fieldMap = xmlDatasource.getParsedFile(formInstance);
		
		Form form = formService.getForm(formId);
		Set<FormField> formFields = form.getFormFields();
		
		//store the values of fields in the jsp map
		for (FormField formField : formFields) {
			org.openmrs.Field currField = formField.getField();
			FieldType fieldType = currField.getFieldType();
			if (fieldType == null || !fieldType.equals(translator.getFieldType("Export Field"))) {
				Field lookupField = fieldMap.get(currField.getName());
				if (lookupField != null) {
					map.put(currField.getName(), lookupField.getValue());
				}
			}
		}
	}
	
	private static void scanForm(Integer formId, Integer formInstanceId, Integer locationTagId, Integer locationId,
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
			if (fieldType != null && fieldType.equals(translator.getFieldType("Export Field"))) {
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
		        .getFormAttributeValue(formId, "defaultExportDirectory", locationTagId, locationId));
		String defaultMergeDirectory = IOUtil.formatDirectoryName(org.openmrs.module.chirdlutilbackports.util.Util
		        .getFormAttributeValue(formId, "defaultMergeDirectory", locationTagId, locationId));
		
		FormInstance formInstance = new FormInstance(locationId, formId, formInstanceId);
		//Write the xml for the export file
		//Use xmle extension to represent form completion through electronic means.
		String exportFilename = exportDirectory + formInstance.toString() + ".xmle";
		
		OutputStream output = new FileOutputStream(exportFilename);
		XMLUtil.serializeXML(records, output);
		output.flush();
		output.close();
		
		//rename the merge file to trigger state change
		String newMergeFilename = defaultMergeDirectory + formInstance.toString() + ".20";
		IOUtil.copyFile(mergeFilename, newMergeFilename);
		IOUtil.deleteFile(mergeFilename);
	}
	
	private String getFormUrl(Integer formId) {
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
	
	private FormInstancePlus getFirstFormInstance(String formInstances) {
		Integer chosenFormId = null;
		Integer chosenFormInstanceId = null;
		Integer chosenLocationId = null;
		Integer chosenLocationTagId = null;
		
		//parse out the location_id,form_id,location_tag_id, and form_instance_id
		//from the selected form
		if (formInstances != null && formInstances.trim().length() > 0) {
			StringTokenizer tokenizer = new StringTokenizer(formInstances, ",");
			if (tokenizer.hasMoreTokens()) {
				tokenizer = new StringTokenizer(tokenizer.nextToken(), "_");
					try {
						chosenLocationId = Integer.parseInt(tokenizer.nextToken());
						chosenLocationTagId = Integer.parseInt(tokenizer.nextToken());
						chosenFormId = Integer.parseInt(tokenizer.nextToken());
						chosenFormInstanceId = Integer.parseInt(tokenizer.nextToken());
					}
					catch (NumberFormatException e) {
					}
			}
		}
		
		if (chosenFormId != null && chosenFormInstanceId != null && chosenLocationId != null && chosenLocationTagId != null) {
			return new FormInstancePlus(chosenLocationId, chosenFormId, chosenFormInstanceId, chosenLocationTagId);
		}
		
		return null;
	}
	
	private class FormInstancePlus extends FormInstance {
		
		private Integer locationTagId;
		public FormInstancePlus() {
		}
		
		public FormInstancePlus(Integer locationId, Integer formId, Integer formInstanceId, Integer locationTagId) {
			super(locationId, formId, formInstanceId);
			this.locationTagId = locationTagId;
		}
		
        /**
         * @return the locationTagId
         */
        public Integer getLocationTagId() {
        	return locationTagId;
        }
		
        /**
         * @param locationTagId the locationTagId to set
         */
        public void setLocationTagId(Integer locationTagId) {
        	this.locationTagId = locationTagId;
        }
	}
}
