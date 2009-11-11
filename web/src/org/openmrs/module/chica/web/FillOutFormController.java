package org.openmrs.module.chica.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.FieldType;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.module.atd.TeleformTranslator;
import org.openmrs.module.atd.datasource.TeleformExportXMLDatasource;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.atd.xmlBeans.Field;
import org.openmrs.module.atd.xmlBeans.Record;
import org.openmrs.module.atd.xmlBeans.Records;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.XMLUtil;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class FillOutFormController extends SimpleFormController
{

	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception
	{
		return "testing";
	}

	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception
	{		
		Map<String, Object> map = new HashMap<String, Object>();
		ATDService atdService = Context
				.getService(ATDService.class);

		String idString = request.getParameter("formInstanceId");
		String formName = request.getParameter("formName");
		
		Integer chosenFormId = null;
		Integer chosenFormInstanceId = null;
		Integer chosenLocationId = null;
		Integer chosenLocationTagId = null;
		
		//parse out the location_id,form_id,location_tag_id, and form_instance_id
		//from the selected form
		if (idString != null)
		{
			StringTokenizer tokenizer = new StringTokenizer(idString,"_");
			if(tokenizer.hasMoreTokens()){
				try
				{
					chosenLocationId = Integer.parseInt(tokenizer.nextToken());
				} catch (NumberFormatException e)
				{
				}
			}
			
			if(tokenizer.hasMoreTokens()){
				try
				{
					chosenLocationTagId = Integer.parseInt(tokenizer.nextToken());
				} catch (NumberFormatException e)
				{
				}
			}
			if(tokenizer.hasMoreTokens()){
				try
				{
					chosenFormId = Integer.parseInt(tokenizer.nextToken());
				} catch (NumberFormatException e)
				{
				}
			}
			
			if(tokenizer.hasMoreTokens()){
				try
				{
					chosenFormInstanceId = Integer.parseInt(tokenizer.nextToken());
				} catch (NumberFormatException e)
				{
				}
			}
			
			
		}
		
		String showForm = request.getParameter("showForm");
		String submitAnswers = request.getParameter("submitAnswers");
		TeleformTranslator translator = new TeleformTranslator();
		ArrayList<String> possibleMergeFilenames = new ArrayList<String>();
		InputStream input = null;
		String mergeFilename = null;
		
		//if a form is chosen or scanned, find the merge file that goes with
		//that form
		if ((showForm != null && showForm.length() > 0)
				|| (submitAnswers != null && submitAnswers.length() > 0))
		{
			String defaultMergeDirectory = IOUtil
					.formatDirectoryName(org.openmrs.module.atd.util.Util
							.getFormAttributeValue(chosenFormId,
									"defaultMergeDirectory",
									chosenLocationTagId, chosenLocationId));
			String pendingMergeDirectory = IOUtil
					.formatDirectoryName(org.openmrs.module.atd.util.Util
							.getFormAttributeValue(chosenFormId,
									"pendingMergeDirectory",
									chosenLocationTagId, chosenLocationId));
			
			// Parse the merge file
			FormInstance formInstance = new FormInstance(chosenLocationId,chosenFormId,chosenFormInstanceId);
			possibleMergeFilenames.add(defaultMergeDirectory
					+ formInstance.toString() + ".xml");
			possibleMergeFilenames.add(defaultMergeDirectory
					+ formInstance.toString() + ".20");
			possibleMergeFilenames.add(defaultMergeDirectory
					+ formInstance.toString() + ".22");
			possibleMergeFilenames.add(defaultMergeDirectory
					+ formInstance.toString() + ".23");
			possibleMergeFilenames.add(defaultMergeDirectory
					+ formInstance.toString() + ".19");
			possibleMergeFilenames.add(pendingMergeDirectory
					+ formInstance.toString() + ".xml");
			possibleMergeFilenames.add(pendingMergeDirectory
					+ formInstance.toString() + ".20");
			possibleMergeFilenames.add(pendingMergeDirectory
					+ formInstance.toString() + ".22");
			possibleMergeFilenames.add(pendingMergeDirectory
					+ formInstance.toString() + ".23");
			possibleMergeFilenames.add(pendingMergeDirectory
					+ formInstance.toString() + ".19");
			
			for(String currFilename:possibleMergeFilenames){
				File file = new File(currFilename);
				if(file.exists()){
					input = new FileInputStream(currFilename);
					mergeFilename = currFilename;
					break;
				}
			}
			map.put("formInstanceId", idString);
		}
		
		//Run this if the form is scanned
		if (submitAnswers != null && submitAnswers.length() > 0)
		{
			scanForm(map,chosenFormId,chosenFormInstanceId,chosenLocationTagId,chosenLocationId,translator,
					input,request,mergeFilename);
			return map;
		}

		//Run this to show the form
		if (showForm != null && showForm.length() > 0)
		{
			showForm(map,chosenFormId,chosenFormInstanceId,chosenLocationId,
					translator,input);

			return map;
		}

		//get all printing and wait to scan states
		List<PatientState> totalStates = new ArrayList<PatientState>();
		
		List<PatientState> states = null;
		
		LocationService locationService = Context.getLocationService();
		List<Location> locations = locationService.getAllLocations();
		
		for(Location location:locations){
			Set<LocationTag> tags = location.getTags();
		for (LocationTag tag:tags)
		{
			Integer locationId = location.getLocationId();
			Integer locationTagId = tag.getLocationTagId();
			states = atdService
					.getUnfinishedPatientStateByStateName(formName+"_printed", null,
							locationTagId, locationId);
			if (states != null)
			{
				totalStates.addAll(states);
			}
			states = atdService.getUnfinishedPatientStateByStateName(
					formName+"_wait_to_scan", null, locationTagId, locationId);
			if (states != null)
			{
				totalStates.addAll(states);
			}
		}
		}
		ArrayList<String> forms = new ArrayList<String>();
		ChicaService chicaService = Context.getService(ChicaService.class);
		for (PatientState currState : totalStates)
		{
			PatientState stateWithId = chicaService.getPrevProducePatientState(currState.getSessionId(), 
					currState.getPatientStateId());
			if(stateWithId == null){
				log.error("State: "+currState.getPatientStateId()+
						" does not have previous produce state");
			}else{
				FormInstance formInstance = stateWithId.getFormInstance();
				if(formInstance != null){
					forms.add(formInstance.getLocationId()+"_"+stateWithId.getLocationTagId()+
							"_"+formInstance.getFormId()+"_"+formInstance.getFormInstanceId()
							);
				}
			}
		}
		map.put("forms", forms);
		showForm = "";

		map.put("showForm", showForm);

		return map;
	}

	private static void showForm(Map map,Integer formId,Integer formInstanceId,Integer locationId,
			TeleformTranslator translator,InputStream inputMergeFile) throws Exception{
		FormService formService = Context.getFormService();
		LogicService logicService = Context.getLogicService();
		TeleformExportXMLDatasource xmlDatasource = (TeleformExportXMLDatasource) logicService
				.getLogicDataSource("xml");
		HashMap<String, org.openmrs.module.atd.xmlBeans.Field> fieldMap = xmlDatasource
				.getParsedFile(new FormInstance(locationId,formId,formInstanceId));
		
		//Parse the merge file to get the field values to display
		FormInstance formInstance = xmlDatasource.parse(inputMergeFile,null,null);
		inputMergeFile.close();
		fieldMap = xmlDatasource.getParsedFile(formInstance);

		List<org.openmrs.Field> fields = formService.getAllFields();

		//store the values of fields in the jsp map
		for (org.openmrs.Field currField : fields)
		{
			FieldType fieldType = currField.getFieldType();
			if (fieldType==null||!fieldType.equals(
					translator.getFieldType("Export Field")))
			{
				Field lookupField = fieldMap.get(
						currField.getName());
				if(lookupField != null){
					map.put(currField.getName(), 
							lookupField.getValue());
				}
			}
		}
	}
	
	private static void scanForm(Map map,Integer formId,Integer formInstanceId,Integer locationTagId,
			Integer locationId,TeleformTranslator translator,InputStream inputMergeFile,
			HttpServletRequest request,String mergeFilename){
		try
		{
			//pull all the input fields from the database for the form
			FormService formService = Context.getFormService();
			HashSet<String> inputFields = new HashSet<String>();
			List<org.openmrs.Field> fields = formService.getAllFields();

			for (org.openmrs.Field currField : fields)
			{
				FieldType fieldType = currField.getFieldType();
				if (fieldType!=null&&fieldType.equals(
						translator.getFieldType("Export Field")))
				{
					inputFields.add(currField.getName());
				}
			}			
			
			Records records = (Records) XMLUtil.deserializeXML(Records.class,
					inputMergeFile);
			inputMergeFile.close();
			Record record = records.getRecord();
			
			//Link the values from the submitted answers to 
			//the form fields
			for (Field currField : record.getFields())
			{
				String name = currField.getId();

				if (inputFields.contains(name))
				{
					String inputVal = request.getParameter(name);
					currField.setValue(inputVal);
				}
			}
			String exportDirectory = IOUtil
			.formatDirectoryName(org.openmrs.module.atd.util.Util
					.getFormAttributeValue(formId,
							"defaultExportDirectory",
							locationTagId, locationId));
			String defaultMergeDirectory = IOUtil
			.formatDirectoryName(org.openmrs.module.atd.util.Util
					.getFormAttributeValue(formId,
							"defaultMergeDirectory",
							locationTagId, locationId));
			
			FormInstance formInstance = new FormInstance(locationId,formId,formInstanceId);
			//Write the xml for the export file
			String exportFilename = exportDirectory + formInstance.toString() + ".xml";

			OutputStream output = new FileOutputStream(exportFilename);
			XMLUtil.serializeXML(records, output);
			output.flush();
			output.close();
			
			//rename the merge file to trigger state change
			String newMergeFilename = defaultMergeDirectory + formInstance.toString() + ".20";
			IOUtil.copyFile(mergeFilename, newMergeFilename);
			IOUtil.deleteFile(mergeFilename);

			map.put("scanned", "scanned");
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
