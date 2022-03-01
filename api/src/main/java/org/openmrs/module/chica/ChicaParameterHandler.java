/**
 * 
 */
package org.openmrs.module.chica;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.module.atd.ParameterHandler;
import org.openmrs.module.atd.datasource.FormDatasource;
import org.openmrs.module.atd.xmlBeans.Field;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tmdugan
 * 
 */
public class ChicaParameterHandler implements ParameterHandler
{
	/** Logger for this class and subclasses */
	private static final Logger log = LoggerFactory.getLogger(ChicaParameterHandler.class);
	
	/**
	 * @see org.openmrs.module.atd.ParameterHandler#addParameters(java.util.Map)
	 */
	public void addParameters(Map<String, Object> parameters, String formType)
	{
		FormInstance formInstance = (FormInstance) parameters.get(ChirdlUtilConstants.PARAMETER_FORM_INSTANCE);
		if (formInstance == null || StringUtils.isBlank(formType)) // CHICA-1234 Check formType before continuing
		{
			return;
		}
		
		LogicService logicService = Context.getLogicService();
		FormDatasource formDatasource = (FormDatasource) logicService
				.getLogicDataSource(ChirdlUtilConstants.DATA_SOURCE_FORM);
		HashMap<String,Field> fieldMap = formDatasource.getFormFields(formInstance);

		if (ChirdlUtilConstants.PATIENT_FORM_TYPE.equalsIgnoreCase(formType))
		{
			processPSFParameters(parameters,fieldMap);
		}

		if (ChirdlUtilConstants.PHYSICIAN_FORM_TYPE.equalsIgnoreCase(formType))
		{
			processPWSParameters(parameters,fieldMap);
		}
	}
	
	/**
	 * @see org.openmrs.module.atd.ParameterHandler#addParameters(java.util.Map, java.util.Map)
	 */
    public void addParameters(Map<String, Object> parameters, Map<String, Field> fieldMap, String formType) 
    {
    	FormInstance formInstance = (FormInstance) parameters.get(ChirdlUtilConstants.PARAMETER_FORM_INSTANCE);

		if (formInstance == null || StringUtils.isBlank(formType)) // CHICA-1234 Check for formType before continuing
		{
			return;
		}
		
		if (ChirdlUtilConstants.PATIENT_FORM_TYPE.equalsIgnoreCase(formType))
		{
			processPSFParameters(parameters,fieldMap);
		}

		if (ChirdlUtilConstants.PHYSICIAN_FORM_TYPE.equalsIgnoreCase(formType))
		{
			processPWSParameters(parameters,fieldMap);
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
		
		String answerValues = (String) parameters.get("child0");
		
		if(fieldMap == null){
			return;
		}
		
		if(answerValues != null&&fieldMap.get(answerValues)!=null){
			String answer = fieldMap.get(answerValues).getValue();
			Integer numBoxes = 0;
			if(answer != null){
				answer = answer.trim();
				
				if(answer.contains("1")){
					parameters.put("Box1", "true");
					parameters.put("box1", "true");
					numBoxes++;
				}
				if(answer.contains("2")){
					parameters.put("Box2", "true");
					parameters.put("box2", "true");
					numBoxes++;
				}
				if(answer.contains("3")){
					parameters.put("Box3", "true");
					parameters.put("box3", "true");
					numBoxes++;
				}
				if(answer.contains("4")){
					parameters.put("Box4", "true");
					parameters.put("box4", "true");
					numBoxes++;
				}
				if(answer.contains("5")){
					parameters.put("Box5", "true");
					parameters.put("box5", "true");
					numBoxes++;
				}
				if(answer.contains("6")){
					parameters.put("Box6", "true");
					parameters.put("box6", "true");
					numBoxes++;
				}
			}
		}
	}
}
