package org.openmrs.module.chica.rule;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Form;
import org.openmrs.FormField;
import org.openmrs.api.FormService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.atd.hibernateBeans.Statistics;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.atd.xmlBeans.Field;
import org.openmrs.module.atd.xmlBeans.Record;
import org.openmrs.module.atd.xmlBeans.Records;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutil.util.XMLUtil;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

/**
 * DWE CHICA-612 Reads the PWS xml file to get values to populate the PDF version of the PWS
 */
public class GetPWSFieldValuesFromXML implements Rule{

	private Log log = LogFactory.getLog(GetPWSFieldValuesFromXML.class);
	private static final String SORT_DESC = "DESC";
	private static final String PWS = "PWS";
	private static final String PWS_PDF = "PWS_PDF";
	private static final String RESULT_DELIM = "^^";
	private static final String AT_CHAR = "@";
	private static final Set<String> PERCENTILE_FIELDS = new HashSet<String>(Arrays.asList(
		     new String[] {"HeightP", "WeightP", "BMIP", "HCP"}));
	
	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer, java.util.Map)
	 */
	public Result eval(LogicContext logicContext, Integer patientId, Map<String, Object> parameters) throws LogicException {
		FormService fs =Context.getFormService();
		Form form = fs.getForm(PWS);
		
		if(form != null)
		{
			Integer encounterId = (Integer)parameters.get(ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID);
			if (encounterId == null) {
				return Result.emptyResult();
			}
			
			ATDService atdService = Context.getService(ATDService.class);
			
			// Get the list in ascending order so that we have the most recent PWS
			// This would be an issue since we receive vitals after the PSF has been submitted
			List<Statistics> stats = atdService.getAllStatsByEncounterForm(encounterId, PWS, SORT_DESC);
			if (stats == null || stats.size() == 0) {
				return Result.emptyResult();
			}
			
			for (Statistics stat : stats) {
				Integer formInstanceId = stat.getFormInstanceId();
				Integer locationId = stat.getLocationId();
				Integer locationTagId = stat.getLocationTagId();
				if (formInstanceId != null && locationId != null && locationTagId != null) {
					FormInstance formInstance = new FormInstance(locationId, form.getFormId(), formInstanceId);
				
					String pendingMergeDirectory = IOUtil.formatDirectoryName(org.openmrs.module.chirdlutilbackports.util.Util
					        .getFormAttributeValue(form.getFormId(), ChirdlUtilConstants.FORM_ATTR_DEFAULT_MERGE_DIRECTORY, locationTagId, locationId))
					        + ChirdlUtilConstants.FILE_PENDING + File.separator;
					
					if (pendingMergeDirectory == null || pendingMergeDirectory.length() == 0) {
						log.info("No " + ChirdlUtilConstants.FORM_ATTR_DEFAULT_MERGE_DIRECTORY + " found for Form: " + formInstance.getFormId() + " Location ID: " + locationId + 
								" Location Tag ID: " + locationTagId + ".  No scan XML file will be created.");
						return Result.emptyResult();
					}

					File file = new File(pendingMergeDirectory, formInstance.toString() + ChirdlUtilConstants.FILE_EXTENSION_XML);

					// Read the xml
					Records records = null;
					if (file.exists()) {
						try {
							records = (Records) XMLUtil.deserializeXML(Records.class, new FileInputStream(file));
						}
						catch (IOException e) {
							this.log.error(e.getMessage());
							this.log.error(Util.getStackTrace(e));
						}			
					} else {
						Record record = new Record();
						records = new Records(record);				
					}
					
					Record record = records.getRecord();
					if (record == null) {
						return Result.emptyResult();
					}
					
					// Get the <field> elements found within the file
					List<Field> currentFieldsInFile = record.getFields();
					if (currentFieldsInFile == null) {
						return Result.emptyResult();
					}
					
					// Create a list of fields for the current form definition
					FormService formService = Context.getFormService();
					form = formService.getForm(PWS_PDF);
					List<FormField> currentFormFields = Context.getService(ChirdlUtilBackportsService.class).getFormFields(form, formService.getAllFieldTypes(), true);
					
					List<String> currentFieldNames = new ArrayList<String>();
					for(FormField formField : currentFormFields)
					{
						org.openmrs.Field field = formField.getField();
						if(field != null)
						{
							currentFieldNames.add(field.getName());
						}
					}
					
					// Add the value and the field name to the return string only if it is found in the list of currentFieldNames
					StringBuilder builder = new StringBuilder();
					for(int i = 0; i < currentFieldsInFile.size(); i++)
					{
						Field field = currentFieldsInFile.get(i);
						if(currentFieldNames.contains(field.getId()))
						{
							if(builder.toString().length() > 0 && i < currentFieldsInFile.size() -1)
							{
								builder.append(RESULT_DELIM);
							}
							
							if(PERCENTILE_FIELDS.contains(field.getId()))
							{
								builder.append("(")
								.append(field.getValue())
								.append("%)");
							}
							else
							{
								builder.append(field.getValue());
							}
							
							builder.append(AT_CHAR);
							builder.append(field.getId());		
						}
					}
					
					return new Result(builder.toString());
				}
			}
		}
		
		return Result.emptyResult();
	}

	/**
	 * @see org.openmrs.logic.Rule#getDefaultDatatype()
	 */
	public Datatype getDefaultDatatype() {
		return Datatype.TEXT;
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getDependencies()
	 */
	public String[] getDependencies() {
		return new String[]{};
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getParameterList()
	 */
	public Set<RuleParameterInfo> getParameterList() {
		return new HashSet<RuleParameterInfo>();
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getTTL()
	 */
	public int getTTL() {
		return 0;
	}

}
