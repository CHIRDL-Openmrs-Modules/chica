/**
 * 
 */
package org.openmrs.module.chica;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.module.atd.ParameterHandler;
import org.openmrs.module.atd.datasource.FormDatasource;
import org.openmrs.module.atd.xmlBeans.Field;
import org.openmrs.module.chica.util.Util;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Error;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

/**
 * @author tmdugan
 * 
 */
public class ChicaParameterHandler implements ParameterHandler
{
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * @see org.openmrs.module.atd.ParameterHandler#addParameters(java.util.Map)
	 */
	public void addParameters(Map<String, Object> parameters)
	{
		FormInstance formInstance = (FormInstance) parameters.get(ChirdlUtilConstants.PARAMETER_FORM_INSTANCE);
		if (formInstance == null)
		{
			return;
		}
		
		LogicService logicService = Context.getLogicService();
		FormDatasource formDatasource = (FormDatasource) logicService
				.getLogicDataSource("form");
		HashMap<String,Field> fieldMap = formDatasource.getFormFields(formInstance);

		String formType = Util.getFormType(formInstance.getFormId(), 
			(Integer) parameters.get(ChirdlUtilConstants.PARAMETER_LOCATION_TAG_ID), formInstance.getLocationId());
		if (ChirdlUtilConstants.PATIENT_FORM_TYPE.equalsIgnoreCase(formType))
		{
			processPSFParameters(parameters,fieldMap);
		}

		if (ChirdlUtilConstants.PHYSICIAN_FORM_TYPE.equalsIgnoreCase(formType))
		{
			processPWSParameters(parameters,fieldMap);
		}
		
		if (StringUtils.isBlank(formType)) {
			log.info("A valid formType was not provided to the CHICA system for Form ID: " + formInstance.getFormId() 
					+ ", Location ID: "+formInstance.getLocationId() + " " + "and Location Tag ID: " + 
					(Integer) parameters.get(ChirdlUtilConstants.PARAMETER_LOCATION_TAG_ID) + ".");
			return;
		}
	}
	
	/**
	 * @see org.openmrs.module.atd.ParameterHandler#addParameters(java.util.Map, java.util.Map)
	 */
    public void addParameters(Map<String, Object> parameters, Map<String, Field> fieldMap) 
    {
    	FormInstance formInstance = (FormInstance) parameters.get(ChirdlUtilConstants.PARAMETER_FORM_INSTANCE);

		if (formInstance == null)
		{
			return;
		}
		
		String formType = Util.getFormType(formInstance.getFormId(), 
			(Integer) parameters.get(ChirdlUtilConstants.PARAMETER_LOCATION_TAG_ID), formInstance.getLocationId());
		if (ChirdlUtilConstants.PATIENT_FORM_TYPE.equalsIgnoreCase(formType))
		{
			processPSFParameters(parameters,fieldMap);
		}

		if (ChirdlUtilConstants.PHYSICIAN_FORM_TYPE.equalsIgnoreCase(formType))
		{
			processPWSParameters(parameters,fieldMap);
		}
		
		if (StringUtils.isBlank(formType)) {
			log.info("A valid formType was not provided to the CHICA system for Form ID: " + formInstance.getFormId() 
					+ ", Location ID: "+formInstance.getLocationId() + " " + "and Location Tag ID: " 
					+ (Integer) parameters.get(ChirdlUtilConstants.PARAMETER_LOCATION_TAG_ID) + ".");
			return;
		}
    }

	protected void processPSFParameters(Map<String, Object> parameters,
			Map<String,Field> fieldMap)
	{
		
		if(fieldMap == null){
			log.info("Field map is null!");
			return;
		}
		
		//TODO figure out which child value to read from
		String child0Val = (String) parameters.get("child0");
		String child1Val = (String) parameters.get("child1");
		
		if (child0Val != null && fieldMap.get(child0Val) == null) {
			log.info("The fieldMap object for child0Val is null!");
		}
		
		if (child1Val != null && fieldMap.get(child1Val) == null) {
			log.info("The fieldMap object for child1Val is null!");
		}
		
		if(child0Val != null&&fieldMap.get(child0Val) != null){
			String answer = fieldMap.get(child0Val).getValue();
			if(answer != null){
				answer = answer.trim();
				
				if(answer.equalsIgnoreCase("Y")){
					parameters.put("Box1", "true");
					parameters.put("box1", "true");
				}
				
				if(answer.equalsIgnoreCase("N")){
					parameters.put("Box2", "true");
					parameters.put("box2", "true");
				}
			}
		}
		
		if(child1Val != null&&fieldMap.get(child1Val) != null){
			String answer = fieldMap.get(child1Val).getValue();
			if(answer != null){
				answer = answer.trim();
				
				if(answer.equalsIgnoreCase("Y")){
					parameters.put("Box1", "true");
					parameters.put("box1", "true");
				}
				
				if(answer.equalsIgnoreCase("N")){
					parameters.put("Box2", "true");
					parameters.put("box2", "true");
				}
			}
		}
	}

	protected void processPWSParameters(Map<String, Object> parameters,
			Map<String,Field> fieldMap)
	{
		
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		String child0Val = (String) parameters.get("child0");
		String child1Val = (String) parameters.get("child1");
		String answerValues = null;
		String errorValues = null;
		
		if(fieldMap == null){
			return;
		}
		
		if(child0Val.contains("Err")){
			answerValues = child1Val;
			errorValues = child0Val;
		}else{
			answerValues = child0Val;
			errorValues = child1Val;
		}
		
		if(answerValues != null&&fieldMap.get(answerValues)!=null){
			String answer = fieldMap.get(answerValues).getValue();
			String errorString = fieldMap.get(errorValues).getValue();
			Integer numBoxes = 0;
			if(answer != null){
				answer = answer.trim();
				
				if(answer.contains("1")&&!(errorString!=null&&errorString.contains("1"))){
					parameters.put("Box1", "true");
					parameters.put("box1", "true");
					numBoxes++;
				}
				if(answer.contains("2")&&!(errorString!=null&&errorString.contains("2"))){
					parameters.put("Box2", "true");
					parameters.put("box2", "true");
					numBoxes++;
				}
				if(answer.contains("3")&&!(errorString!=null&&errorString.contains("3"))){
					parameters.put("Box3", "true");
					parameters.put("box3", "true");
					numBoxes++;
				}
				if(answer.contains("4")&&!(errorString!=null&&errorString.contains("4"))){
					parameters.put("Box4", "true");
					parameters.put("box4", "true");
					numBoxes++;
				}
				if(answer.contains("5")&&!(errorString!=null&&errorString.contains("5"))){
					parameters.put("Box5", "true");
					parameters.put("box5", "true");
					numBoxes++;
				}
				if(answer.contains("6")&&!(errorString!=null&&errorString.contains("6"))){
					parameters.put("Box6", "true");
					parameters.put("box6", "true");
					numBoxes++;
				}
				if (numBoxes == 6){
					
					Error error = new Error("Warning", "PWS Scan"
							, "All six PWS boxes were checked - possible scan error. "
							,null, new Date(), null);
					//Get the session id
					Integer sessionId = (Integer) parameters.get("sessionId");
					error.setSessionId(sessionId);
					chirdlutilbackportsService.saveError(error);
					
				}
			}
		}
	}
}
