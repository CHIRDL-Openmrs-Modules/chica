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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.FieldType;
import org.openmrs.Form;
import org.openmrs.api.FormService;
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
import org.openmrs.module.dss.util.IOUtil;
import org.openmrs.module.dss.util.XMLUtil;
import org.springframework.web.servlet.mvc.SimpleFormController;

public class FillOutPSFController extends SimpleFormController
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

		FormService formService = Context.getFormService();
		//List<Form> forms = formService.findForms("PSF", false, false);
		List<Form> forms = formService.getForms("PSF", false, null, false, null, null, null);
		Form form = forms.get(0);
		Integer formId = form.getFormId();
		String idString = request.getParameter("psf_id");
		Integer formInstanceId = null;
		if (idString != null)
		{
			formInstanceId = Integer.parseInt(idString);
		}
		String defaultMergeDirectory = IOUtil
				.formatDirectoryName(org.openmrs.module.atd.util.Util
						.getFormAttributeValue(formId, "defaultMergeDirectory"));
		String pendingMergeDirectory = IOUtil
				.formatDirectoryName(org.openmrs.module.atd.util.Util
						.getFormAttributeValue(formId, "pendingMergeDirectory"));
		String exportDirectory = IOUtil
				.formatDirectoryName(org.openmrs.module.atd.util.Util
						.getFormAttributeValue(formId, "defaultExportDirectory"));

		String showForm = request.getParameter("showForm");
		String submitAnswers = request.getParameter("submitAnswers");
		TeleformTranslator translator = new TeleformTranslator();
		
		//Run this if the form is scanned
		if (submitAnswers != null && submitAnswers.length() > 0)
		{
			//pull all the input fields from the database for the PSF form
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
			
			//Parse the PSF file
			ArrayList<String> possibleMergeFilenames = new ArrayList<String>();
			
			possibleMergeFilenames.add(defaultMergeDirectory + formInstanceId + ".xml");
			possibleMergeFilenames.add(defaultMergeDirectory + formInstanceId + ".20");
			possibleMergeFilenames.add(defaultMergeDirectory + formInstanceId + ".22");
			possibleMergeFilenames.add(pendingMergeDirectory + formInstanceId + ".xml");
			possibleMergeFilenames.add(pendingMergeDirectory + formInstanceId + ".20");
			possibleMergeFilenames.add(pendingMergeDirectory + formInstanceId + ".22");
			
			InputStream input = null;
			
			String psfMergeFilename = null;
			
			for(String currFilename:possibleMergeFilenames){
				File file = new File(currFilename);
				if(file.exists()){
					input = new FileInputStream(currFilename);
					psfMergeFilename = currFilename;
					break;
				}
			}
			
			Records records = (Records) XMLUtil.deserializeXML(Records.class,
					input);
			input.close();
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
			
			//Write the xml for the export file
			String exportFilename = exportDirectory + formInstanceId + ".xml";

			OutputStream output = new FileOutputStream(exportFilename);
			XMLUtil.serializeXML(records, output);
			output.flush();
			output.close();
			
			//rename the PSF merge file to trigger state change
			String newPSFMergeFilename = defaultMergeDirectory + formInstanceId + ".20";
			IOUtil.copyFile(psfMergeFilename, newPSFMergeFilename);
			IOUtil.deleteFile(psfMergeFilename);

			map.put("scanned", "scanned");
			map.put("psf_id", idString);

			return map;
		}

		//Run this to show the form
		if (showForm != null && showForm.length() > 0)
		{
			LogicService logicService = Context.getLogicService();
			TeleformExportXMLDatasource xmlDatasource = (TeleformExportXMLDatasource) logicService
					.getLogicDataSource("xml");
			HashMap<String, org.openmrs.module.atd.xmlBeans.Field> fieldMap = xmlDatasource
					.getParsedFile(formInstanceId, formId);
			
			//Parse the merge file to get the field values to display
			ArrayList<String> possibleMergeFilenames = new ArrayList<String>();
			
			possibleMergeFilenames.add(defaultMergeDirectory + formInstanceId + ".xml");
			possibleMergeFilenames.add(defaultMergeDirectory + formInstanceId + ".20");
			possibleMergeFilenames.add(defaultMergeDirectory + formInstanceId + ".22");
			possibleMergeFilenames.add(pendingMergeDirectory + formInstanceId + ".xml");
			possibleMergeFilenames.add(pendingMergeDirectory + formInstanceId + ".20");
			possibleMergeFilenames.add(pendingMergeDirectory + formInstanceId + ".22");
			
			InputStream input = null;
					
			for(String currFilename:possibleMergeFilenames){
				File file = new File(currFilename);
				if(file.exists()){
					input = new FileInputStream(currFilename);
					break;
				}
			}

			FormInstance formInstance = xmlDatasource.parse(input,
					formInstanceId, formId);
			formInstanceId = formInstance.getFormInstanceId();
			formId = formInstance.getFormId();
			fieldMap = xmlDatasource.getParsedFile(formInstanceId, formId);

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

			map.put("psf_id", idString);
			return map;

		}

		//get all PSF printing and PSF wait to scan states
		List<PatientState> states = atdService
				.getUnfinishedPatientStateByStateName("PSF_printed",null);
		states.addAll(atdService.getUnfinishedPatientStateByStateName("PSF_wait_to_scan",null));
		ArrayList<Integer> psfs = new ArrayList<Integer>();
		ChicaService chicaService = Context.getService(ChicaService.class);
		for (PatientState currState : states)
		{
			PatientState stateWithId = chicaService.getPrevProducePatientState(currState.getSessionId(), 
					currState.getPatientStateId());
			if(stateWithId == null){
				log.error("State: "+currState.getPatientStateId()+
						" does not have previous produce state");
			}else{
				psfs.add(stateWithId.getFormInstanceId());
			}
		}
		map.put("psfs", psfs);
		showForm = "";

		map.put("showForm", showForm);

		return map;
	}

}
