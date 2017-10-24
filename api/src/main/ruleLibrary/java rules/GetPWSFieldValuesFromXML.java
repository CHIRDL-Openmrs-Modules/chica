package org.openmrs.module.chica.rule;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.FieldType;
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
import org.openmrs.module.atd.xmlBeans.Field;
import org.openmrs.module.atd.xmlBeans.Record;
import org.openmrs.module.atd.xmlBeans.Records;
import org.openmrs.module.chica.DynamicFormAccess;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutil.util.XMLUtil;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

/**
 * DWE CHICA-612 Reads the PWS xml file to get values to populate the PDF version of the PWS
 */
public class GetPWSFieldValuesFromXML implements Rule{

	private Log log = LogFactory.getLog(GetPWSFieldValuesFromXML.class);
	private static final String PWS_PDF = "PWS_PDF";
	private static final String RESULT_DELIM = "^^";
	private static final String AT_CHAR = "@";
	private static final String HEIGHTP_FIELD = "HeightP";
	private static final String WEIGHTP_FIELD = "WeightP";
	private static final String BMIP_FIELD = "BMIP";
	private static final String HCP_FIELD = "HCP";
	private static final String WEIGHTKG_FIELD = "WeightKG";
	private static final String HC_FIELD = "HC";
	private static final String PULSEOX_FIELD = "PulseOx";
	private static final String BPP_FIELD = "BPP";
	private static final String TEMPERATURE_METHOD_FIELD = "Temperature_Method";
	private static final String PREV_WEIGHT_DATE_FIELD = "PrevWeightDate";
	private static final int MAX_TRIES = 2;
	private static final String STATE_PWS_CREATE = "PWS_create";

	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer, java.util.Map)
	 */
	public Result eval(LogicContext logicContext, Integer patientId, Map<String, Object> parameters) throws LogicException {
		FormService fs =Context.getFormService();
		Integer encounterId = (Integer) parameters.get(ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID);
		String physicianForm = org.openmrs.module.chica.util.Util.getPrimaryPhysicianForm(encounterId);
		Form form = fs.getForm(physicianForm);

		if(form != null)
		{
			if (encounterId == null) {
				this.log.error("Error while creating " + PWS_PDF + ". Unable to locate encounterId.");
				return Result.emptyResult();
			}

			// Get a list of patient states for the encounter and PWS_create
			// The query orders them by the most recent
			ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
			State state = chirdlutilbackportsService.getStateByName(STATE_PWS_CREATE);
			if(state == null){
				this.log.error("Error while creating " + PWS_PDF + ". Unable to locate " + STATE_PWS_CREATE + ".");
				return Result.emptyResult();
			}
			
			List<PatientState> states = Context.getService(ChirdlUtilBackportsService.class).getPatientStateByEncounterState(encounterId, state.getStateId());
			if (states == null || states.size() == 0) {
				this.log.error("Error while creating " + PWS_PDF + ". Unable to locate patient state for encounterId: " + encounterId + ".");
				return Result.emptyResult();
			}

			// Use the first one in the list in case of reprints 
			// or vitals processing from IUH where there might be an outdated PWS
			PatientState patientState = states.get(0); 
			Integer formInstanceId = patientState.getFormInstanceId();
			Integer locationId = patientState.getLocationId();
			Integer locationTagId = patientState.getLocationTagId();
			if (patientState.getEndTime() != null && formInstanceId != null && locationId != null && locationTagId != null) {
				FormInstance formInstance = new FormInstance(locationId, form.getFormId(), formInstanceId);

				String mergeDirectory = IOUtil.formatDirectoryName(org.openmrs.module.chirdlutilbackports.util.Util
						.getFormAttributeValue(form.getFormId(), ChirdlUtilConstants.FORM_ATTR_DEFAULT_MERGE_DIRECTORY, locationTagId, locationId));

				if (mergeDirectory == null || mergeDirectory.length() == 0) {
					log.error("No " + ChirdlUtilConstants.FORM_ATTR_DEFAULT_MERGE_DIRECTORY + " found for Form: " + formInstance.getFormId() + " Location ID: " + locationId + 
							" Location Tag ID: " + locationTagId + ".");
					return Result.emptyResult();
				}

				File file = new File(mergeDirectory, formInstance.toString() + ChirdlUtilConstants.FILE_EXTENSION_XML);

				// DWE CHICA-682 Added some additional logging 
				// and made changes to try again if the file was 
				// not found in either directory on the first try
				int numTries = 1;
				while (!file.exists() && numTries <= MAX_TRIES)
				{
					// Check the pending directory
					String pendingDirectory = IOUtil.formatDirectoryName(org.openmrs.module.chirdlutilbackports.util.Util
							.getFormAttributeValue(form.getFormId(), ChirdlUtilConstants.FORM_ATTR_DEFAULT_MERGE_DIRECTORY, locationTagId, locationId)) 
							+ ChirdlUtilConstants.FILE_PENDING + File.separator;
					file = new File(pendingDirectory, formInstance.toString() + ChirdlUtilConstants.FILE_EXTENSION_XML);
					if(!file.exists()) // File was not found in the pending directory either, return empty result, if we've tried the max number of tries
					{
						try{
							// Wait and then look in the merge directory again
							Thread.sleep(1000);
							file = new File(mergeDirectory, formInstance.toString() + ChirdlUtilConstants.FILE_EXTENSION_XML);
						}
						catch (InterruptedException e) {
							log.error("Interrupted thread error", e);
						}

						if(numTries == MAX_TRIES){
							log.error("Unable to locate " + physicianForm + " while creating " + PWS_PDF + "(formInstanceId: " + formInstanceId + " locationId: " + locationId + " locationTagId: " + locationTagId + ")");
							return Result.emptyResult();
						}
					}
					numTries++;
				}

				// Read the xml
				Records records = null;
				try {
					records = (Records) XMLUtil.deserializeXML(Records.class, new FileInputStream(file));
				}
				catch (IOException e) {
					// Try again
					try{
						Thread.sleep(1000);
						records = (Records) XMLUtil.deserializeXML(Records.class, new FileInputStream(file));
					}
					catch (InterruptedException ie) {
						log.error("Interrupted thread error", ie);
					}
					catch(IOException ioe){
						log.error("Unable to read " + physicianForm + " while creating " + PWS_PDF + "(formInstanceId: " + formInstanceId + " locationId: " + locationId + " locationTagId: " + locationTagId + ")");
						this.log.error(ioe.getMessage());
						this.log.error(Util.getStackTrace(ioe));
					}
				}			

				if(records != null)
				{
					Record record = records.getRecord();
					if (record == null) {
						this.log.error("Error while creating " + PWS_PDF + ". Unable to locate record in xml for (formInstanceId: " + formInstanceId + " locationId: " + locationId + " locationTagId: " + locationTagId + ")");
						return Result.emptyResult();
					}

					// Get the <field> elements found within the file
					List<Field> currentFieldsInFile = record.getFields();
					if (currentFieldsInFile == null) {
						this.log.error("Error while creating " + PWS_PDF + ". Unable to locate fields in xml for (formInstanceId: " + formInstanceId + " locationId: " + locationId + " locationTagId: " + locationTagId + ")");
						return Result.emptyResult();
					}

					List<String> currentFieldNames = getFieldNamesCurrentFormDefinition();

					return new Result(createFieldValueString(currentFieldsInFile, currentFieldNames));
				}	
			}
			else{
				this.log.error("Error while creating " + PWS_PDF + ". Unable to get form instance id from PatientState: " + patientState.getPatientStateId());
				return Result.emptyResult();
			}
		}

		this.log.error("Error while creating " + PWS_PDF + ".");
		return Result.emptyResult();	
	}

	/**
	 * Creates the string of field names and values formatted as "value@fieldName^^value@fieldName"
	 * The value and field name are only added to the string if it is found in the current form definition,
	 * otherwise it is skipped
	 * 
	 * @param currentFieldsInFile
	 * @param currentFieldNames
	 * @return string formatted as "value@fieldName^^value@fieldName"
	 */
	private String createFieldValueString(List<Field> currentFieldsInFile, List<String> currentFieldNames)
	{
		// Add the value and the field name to the return string only if it is found in the list of currentFieldNames
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < currentFieldsInFile.size(); i++)
		{
			Field field = currentFieldsInFile.get(i);
			if(currentFieldNames.contains(field.getId()))
			{
				if(builder.toString().length() > 0 && i < currentFieldsInFile.size())
				{
					builder.append(RESULT_DELIM);
				}

				if(field.getValue() != null && field.getValue().length() > 0)
				{
					switch(field.getId())
					{
					case HEIGHTP_FIELD:
					case WEIGHTP_FIELD:
					case BMIP_FIELD:
					case HCP_FIELD:
						// The fields in the cases above will all use this formatting								
						builder.append(" (").append(field.getValue()).append("%)");								
						break;
					case WEIGHTKG_FIELD:								
						builder.append(field.getValue()).append(" kg.");									
						break;
					case HC_FIELD:							
						builder.append(field.getValue()).append(" cm.");			
						break;
					case PULSEOX_FIELD:									
						builder.append(field.getValue()).append("%");								
						break;
					case TEMPERATURE_METHOD_FIELD:
					case BPP_FIELD:
					case PREV_WEIGHT_DATE_FIELD:
						// Temperature method and BPP just need parenthesis
						// DWE CHICA-677 Added Previous Weight Date to this case
						builder.append(" (").append(field.getValue()).append(")");
						break;
					default:
						builder.append(field.getValue());
						break;
					}
				}
				else
				{
					builder.append("");
				}

				builder.append(AT_CHAR);
				builder.append(field.getId());		
			}
		}

		return builder.toString();
	}

	/**
	 * Creates a list of fields for the current form definition of the PWS_PDF
	 * Currently only used to get Merge and Prioritized Merge
	 * @return
	 */
	private List<String> getFieldNamesCurrentFormDefinition()
	{
		List<String> currentFieldNames = new ArrayList<String>();
		FormService formService = Context.getFormService();
		Form form = formService.getForm(PWS_PDF);
		DynamicFormAccess formAccess = new DynamicFormAccess();
		FieldType mergeType = formAccess.getFieldType("Merge Field");
		FieldType priorMergeType = formAccess.getFieldType("Prioritized Merge Field");
		List<FieldType> fieldTypes = new ArrayList<FieldType>();
		fieldTypes.add(mergeType);
		fieldTypes.add(priorMergeType);
		
		List<FormField> currentFormFields = Context.getService(ChirdlUtilBackportsService.class).getFormFields(form, fieldTypes, true);

		for(FormField formField : currentFormFields)
		{
			org.openmrs.Field field = formField.getField();
			if(field != null)
			{
				currentFieldNames.add(field.getName());
			}
		}

		return currentFieldNames;
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
