/**
 * 
 */
package org.openmrs.module.chica.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.FieldType;
import org.openmrs.Form;
import org.openmrs.FormField;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.FormService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.module.atd.TeleformTranslator;
import org.openmrs.module.atd.datasource.TeleformExportXMLDatasource;
import org.openmrs.module.atd.hibernateBeans.FormAttributeValue;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.hibernateBeans.Statistics;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chica.xmlBeans.Choose;
import org.openmrs.module.chica.xmlBeans.Field;
import org.openmrs.module.chica.xmlBeans.FormConfig;
import org.openmrs.module.chica.xmlBeans.Geq;
import org.openmrs.module.chica.xmlBeans.If;
import org.openmrs.module.chica.xmlBeans.Language;
import org.openmrs.module.chica.xmlBeans.LanguageAnswers;
import org.openmrs.module.chica.xmlBeans.Mean;
import org.openmrs.module.chica.xmlBeans.Plus;
import org.openmrs.module.chica.xmlBeans.Score;
import org.openmrs.module.chica.xmlBeans.Scores;
import org.openmrs.module.chica.xmlBeans.Then;
import org.openmrs.module.chica.xmlBeans.Value;
import org.openmrs.module.chirdlutil.util.XMLUtil;

/**
 * @author Tammy Dugan
 * 
 */
public class Util
{
	public static final String YEAR_ABBR = "yo";
	public static final String MONTH_ABBR = "mo";
	public static final String WEEK_ABBR = "wk";
	public static final String DAY_ABBR = "do";
	
	private static Log log = LogFactory.getLog( Util.class );
	public static final Random GENERATOR = new Random();
	
	public synchronized static int getMaxDssElements(Integer formId,
			Integer locationTagId,Integer locationId)
	{
		String propertyValue = null;
		int maxDssElements = 0;
		FormService formService = Context.getFormService();
		Form form = formService.getForm(formId);
		String formName = form.getName();
		
		if (formName.equals("PSF"))
		{
			propertyValue = org.openmrs.module.atd.util.Util
					.getFormAttributeValue(formId, "numQuestions",locationTagId,locationId);
		}else{
			propertyValue = org.openmrs.module.atd.util.Util
				.getFormAttributeValue(formId, "numPrompts",locationTagId,locationId);
		}
		
		try
		{
			maxDssElements = Integer.parseInt(propertyValue);
		} catch (NumberFormatException e)
		{
		}
		
		return maxDssElements;
	}
	
	public synchronized static void saveObs(Patient patient, Concept currConcept,
			int encounterId, String value, FormInstance formInstance,
			Integer ruleId, Integer locationTagId)
	{
		if (value == null || value.length() == 0)
		{
			return;
		}
		
		String formName = null;
		if (formInstance != null)
		{
			if (formInstance.getFormId() == null)
			{
				log.error("Could not find form for statistics update");
				return;
			}

			FormService formService = Context.getFormService();
			Form form = formService.getForm(formInstance.getFormId());
			formName = form.getName();
		}

		ObsService obsService = Context.getObsService();
		Obs obs = new Obs();
		String datatypeName = currConcept.getDatatype().getName();

		if (datatypeName.equalsIgnoreCase("Numeric"))
		{
			try
			{
				obs.setValueNumeric(Double.parseDouble(value));
			} catch (NumberFormatException e)
			{
				log.error("Could not save value: " + value
						+ " to the database for concept "+currConcept.getName().getName());
			}
		} else if (datatypeName.equalsIgnoreCase("Coded"))
		{
			ConceptService conceptService = Context.getConceptService();
			Concept answer = conceptService.getConceptByName(value);
			if(answer == null){
				log.error(value+" is not a valid concept name. "+value+" will be stored as text.");
				obs.setValueText(value);
			}else{
				obs.setValueCoded(answer);
			}
		} else
		{
			obs.setValueText(value);
		}

		EncounterService encounterService = Context
				.getService(EncounterService.class);
		Encounter encounter = (Encounter) encounterService
				.getEncounter(encounterId);

		Location location = encounter.getLocation();
		ChicaService chicaService = Context.getService(ChicaService.class);
		Date obsDate = null;
				
		obs.setPerson(patient);
		obs.setConcept(currConcept);
		obs.setLocation(location);
		obs.setEncounter(encounter);
		
		if(formInstance == null){
			obs.setObsDatetime(new Date());
			obsService.saveObs(obs, null);
		}

		if (formInstance != null)
		{
			Integer formInstanceId = formInstance.getFormInstanceId();
			Integer locationId = formInstance.getLocationId();
			//Since PWS forms get scanned much later, sometimes the next day, 
			//set the observation time as the time the form is printed
			if(formName != null && formName.equalsIgnoreCase("PWS")){
				List<Statistics> stats = chicaService.getStatByFormInstance(formInstanceId, formName,locationId);
				if(stats != null&&stats.size()>0){
					obsDate = stats.get(0).getPrintedTimestamp(); 
				}
			}
			
			if(obsDate == null){
				obsDate = new Date();
			}
			
			obs.setObsDatetime(obsDate);
			obsService.saveObs(obs, null);
			
			if (ruleId != null)
			{
				List<Statistics> statistics = chicaService.getStatByIdAndRule(
						formInstanceId, ruleId, formName,locationId);

				if (statistics != null)
				{
					Statistics stat = statistics.get(0);

					if (stat.getObsvId() == null)
					{
						stat.setObsvId(obs.getObsId());
						chicaService.updateStatistics(stat);
					} else
					{
						stat = new Statistics(stat);
						stat.setObsvId(obs.getObsId());
						chicaService.createStatistics(stat);
					}
				}
			} else
			{
				List<Statistics> statistics = chicaService.getStatByFormInstance(formInstanceId, formName,locationId);
				Statistics stat = new Statistics();

				stat.setAgeAtVisit(org.openmrs.module.chica.util.Util
						.adjustAgeUnits(patient.getBirthdate(), null));
				stat.setEncounterId(encounterId);
				stat.setFormInstanceId(formInstanceId);
				stat.setLocationTagId(locationTagId);
				stat.setFormName(formName);
				stat.setObsvId(obs.getObsId());
				stat.setPatientId(patient.getPatientId());
				stat.setRuleId(ruleId);
				stat.setLocationId(locationId);
				if(statistics != null&&statistics.size()>0){
					Statistics oldStat = statistics.get(0);
					stat.setPrintedTimestamp(oldStat.getPrintedTimestamp());
					stat.setScannedTimestamp(oldStat.getScannedTimestamp());
				}
				chicaService.createStatistics(stat);
			}
		}
	}
	
	
	/**
	 * Calculates age to a precision of days, weeks, months, or years based on a
	 * set of rules
	 * 
	 * @param birthdate patient's birth date
	 * @param cutoff date to calculate age from
	 * @return String age with units 
	 */
	public synchronized static String adjustAgeUnits(Date birthdate, Date cutoff)
	{
		int years = org.openmrs.module.chirdlutil.util.Util.getAgeInUnits(birthdate, cutoff, YEAR_ABBR);
		int months = org.openmrs.module.chirdlutil.util.Util.getAgeInUnits(birthdate, cutoff, MONTH_ABBR);
		int weeks = org.openmrs.module.chirdlutil.util.Util.getAgeInUnits(birthdate, cutoff, WEEK_ABBR);
		int days = org.openmrs.module.chirdlutil.util.Util.getAgeInUnits(birthdate, cutoff, DAY_ABBR);

		if (years >= 2)
		{
			return years + " " + YEAR_ABBR;
		}

		if (months >= 2)
		{
			return months + " " + MONTH_ABBR;
		}

		if (days > 30)
		{
			return weeks + " " + WEEK_ABBR;
		}

		return days + " " + DAY_ABBR;
	}
	
	public synchronized static boolean isValidSSN( String ssnFull){
		boolean valid = true;
		String ssn = null;
		
		if(ssnFull != null){
			ssn = ssnFull.replace("-", "");
		}
		
		if (ssn  == null||ssn.length()==0
				|| ssn.equals("000000000")
				|| ssn.equals("111111111")
				|| ssn.equals("222222222")
				|| ssn.equals("333333333")
				|| ssn.equals("444444444")
				|| ssn.equals("555555555")
				|| ssn.equals("666666666")
				|| ssn.equals("777777777")
				|| ssn.equals("888888888")
				|| ssn.equals("999999999")){
			valid = false;
		}
		return valid;
	}
	
	public static Properties getProps(String filename)
	{
		try
		{

			Properties prop = new Properties();
			InputStream propInputStream = new FileInputStream(filename);
			prop.loadFromXML(propInputStream);
			return prop;

		} catch (FileNotFoundException e)
		{

		} catch (InvalidPropertiesFormatException e)
		{

		} catch (IOException e)
		{
			// TODO Auto-generated catch block

		}
		return null;
	}
	
	public static HashMap<String, Field> getLanguageFieldsToConsume(
	                                                                HashMap<String, org.openmrs.module.atd.xmlBeans.Field> fieldMap,
	                                                                FormInstance formInstance,	                                                               
	                                                                LanguageAnswers answersByLanguage) {
		TeleformTranslator translator = new TeleformTranslator();
		FormService formService = Context.getFormService();
		Integer formId = formInstance.getFormId();
		Form databaseForm = formService.getForm(formId);
		if (databaseForm == null) {
			log.error("Could not consume teleform export xml because form " + formId + " does not exist in the database");
			return null;
		}
		
		HashMap<String, HashMap<String, Field>> languageToFieldnames = new HashMap<String, HashMap<String, Field>>();
				
		populateFieldNameArrays( languageToFieldnames,  answersByLanguage);
		
		HashMap<String, Integer> languageToNumAnswers = new HashMap<String, Integer>();
		
		for (FormField currField : databaseForm.getFormFields()) {
			FieldType currFieldType = currField.getField().getFieldType();
			// only process export fields
			if (currFieldType != null && currFieldType.equals(translator.getFieldType("Export Field"))) {
				String fieldName = currField.getField().getName();
				
				for (String currLanguage : languageToFieldnames.keySet()) {
					
					HashMap<String, Field> currLangMap = languageToFieldnames.get(currLanguage);
					
					if (currLangMap.get(currField.getField().getName()) != null) {
						String value = null;
						if (fieldMap.get(fieldName) != null) {
							value = fieldMap.get(fieldName).getValue();
						}
						if (value != null) {
							if (languageToNumAnswers.get(currLanguage) == null) {
								languageToNumAnswers.put(currLanguage, 0);
							}
							
							languageToNumAnswers.put(currLanguage, languageToNumAnswers.get(currLanguage) + 1);
						}
					}
					
				}
			}
			
		}
		
		int maxNumAnswers = -1;
		String maxLanguage = null;
		
		for (String language : languageToNumAnswers.keySet()) {
			Integer compareNum = languageToNumAnswers.get(language);
			
			if (compareNum != null && compareNum > maxNumAnswers) {
				maxNumAnswers = compareNum;
				maxLanguage = language;
			}
		}
		if (maxNumAnswers > 0) {
			return languageToFieldnames.get(maxLanguage);
		} else {
			return languageToFieldnames.get("English");
		}
	}
	
	public static void populateFieldNameArrays(HashMap<String, HashMap<String, Field>> languages, 
	                                           LanguageAnswers answersByLanguage) {
		
		
		if (answersByLanguage != null) {
			ArrayList<Language> xmlLanguages = answersByLanguage.getLanguages();
			
			for (Language currLanguage : xmlLanguages) {
				String languageName = currLanguage.getName();
				if (languageName != null) {
					HashMap<String, Field> currLanguageFields = new HashMap<String, Field>();
					languages.put(languageName, currLanguageFields);
					
					ArrayList<org.openmrs.module.chica.xmlBeans.Field> fields = currLanguage.getFields();
					for (org.openmrs.module.chica.xmlBeans.Field currField : fields) {
						if (currField.getId() != null) {
							currLanguageFields.put(currField.getId(), currField);
						}
					}
				}
			}
		}
	}
	
	public static void scoreJit(FormInstance formInstance, Integer locationTagId, Integer encounterId,
	                            Patient patient) {
		
		ATDService atdService = Context.getService(ATDService.class);
		Integer locationId = formInstance.getLocationId();
		Integer formId = formInstance.getFormId();
		
		
		//parse the scan xml
		LogicService logicService = Context.getLogicService();
		TeleformExportXMLDatasource xmlDatasource = (TeleformExportXMLDatasource) logicService.getLogicDataSource("xml");
		HashMap<String, org.openmrs.module.atd.xmlBeans.Field> fieldMap = xmlDatasource.getParsedFile(formInstance);
		
		//map fields to languages
		FormAttributeValue scorableFormConfigAttrVal = atdService.getFormAttributeValue(formId, "scorableFormConfigFile",
		    locationTagId, locationId);
		
		if(fieldMap == null){
			return;
		}
		
		String scorableFormConfigFile = null;
		
		if (scorableFormConfigAttrVal != null) {
			scorableFormConfigFile = scorableFormConfigAttrVal.getValue();
		}
		
		if (scorableFormConfigFile == null) {
			log.error("Could not find scorableFormConfigFile for locationId: " + locationId + " and locationTagId: "
			        + locationTagId);
			return;
		}
		
		LanguageAnswers answersByLanguage = null;
		FormConfig formConfig = null;
		InputStream input = null;
		try {
			input = new FileInputStream(scorableFormConfigFile);
			formConfig = (FormConfig) XMLUtil.deserializeXML(FormConfig.class, input);
			answersByLanguage = formConfig.getLanguageAnswers();
		}
		catch (IOException e1) {
			log.error("", e1);
			return;
		}
		HashMap<String, Field> langFieldsToConsume = Util.getLanguageFieldsToConsume(fieldMap, formInstance,
		    answersByLanguage);
		
		HashMap<String, HashMap<String, FormField>> formFieldsMap = new HashMap<String, HashMap<String, FormField>>();
		FormService formService = Context.getFormService();
		Form form = formService.getForm(formId);
		//make a map of child to parent fields. This is used when figuring out
		//whether to score the spanish or english side
		//we assume the configuration file always configures using the english fields
		HashMap<String, FormField> childFields = null;
		
		for (org.openmrs.FormField currFormField : form.getFormFields()) {
			FormField parentField = currFormField.getParent();
			if (parentField != null) {
				String fieldName = currFormField.getField().getName();
				String parentName = parentField.getField().getName();
				childFields = formFieldsMap.get(parentName);
				if (childFields == null) {
					childFields = new HashMap<String, FormField>();
					formFieldsMap.put(parentName, childFields);
				}
				
				childFields.put(fieldName, currFormField);
			}
		}
		
		//parse the form configuration file
		if (scorableFormConfigAttrVal != null) {
			
				try {
					Scores scores = formConfig.getScores();
					
					//compute each score and save it to a concept in the database
					for (Score score : scores.getScores()) {
						
						Value value = score.getValue(); //value that should be saved to the concept
						Plus plus = value.getPlus();
						Mean mean = value.getMean();
						
						//compute the sum
						if (plus != null) {
							Double scoreTotal = null;
							
							List<Choose> choices = plus.getChooses();
							
							//process conditional logic
							if (choices != null) {
								
								for (Choose choose : choices) {
									boolean ifSatisfied = false;
									If ifObject = choose.getIf();
									Then thenObject = choose.getThen();
									
									if (ifObject != null) {
										Geq geq = ifObject.getGeq();
										
										if (geq != null) {
											Field fieldOperand = geq.getField();
											String cnOperand = geq.getCn();
											
											if (fieldOperand != null && cnOperand != null) {
												Field matchingField = pickFieldLanguage(fieldOperand, childFields,
												    langFieldsToConsume, formFieldsMap);
												if (matchingField != null && fieldMap != null) {
													org.openmrs.module.atd.xmlBeans.Field scorableFormField = fieldMap
													        .get(matchingField.getId());
													
													if (scorableFormField!=null&&
															scorableFormField.getValue() != null) {
														if (Integer.parseInt(scorableFormField.getValue()) >= Integer
														        .parseInt(cnOperand)) {
															ifSatisfied = true;
														}
													}
												}
											}
										}
									}
									
									if (thenObject != null) {
										String cnResult = thenObject.getCn();
										
										if (cnResult != null && ifSatisfied) {
											if (scoreTotal == null) {
												scoreTotal = 0D;
											}
											scoreTotal += Integer.parseInt(cnResult);
										}
									}
								}
							}
							
							List<Field> fields = plus.getFields();
							
							//sum the fields
							if (fields != null) {
								if (scoreTotal == null) {
									scoreTotal = 0D;
								}
								Double computeSumResult = computeSum(fields, childFields, langFieldsToConsume, fieldMap, formFieldsMap);
								if(computeSumResult != null){
									scoreTotal += computeSumResult;
								}
							}
							
							if (scoreTotal != null) {
								saveScore(score, scoreTotal, encounterId, patient);
							}
						}
						//compute the average
						if (mean != null) {
							
							List<Field> fields = mean.getFields();
							Double computeSumResult = computeSum(fields, childFields, langFieldsToConsume, fieldMap, formFieldsMap);
							Double scoreTotal = null;
							
							if(computeSumResult != null){
								
								scoreTotal = computeSumResult;
							}
							
							if (scoreTotal != null) {
								
								saveScore(score, scoreTotal / fields.size(), encounterId, patient);
							}
						}
					}
				}
				catch (Exception e) {
					log.error("", e);
				}
		}
		
	}
	
	private static Double computeSum(List<Field> fields, HashMap<String, FormField> childFields,
	                          HashMap<String, Field> langFieldsToConsume,
	                          HashMap<String, org.openmrs.module.atd.xmlBeans.Field> fieldMap,
	                          HashMap<String, HashMap<String, FormField>> formFieldsMap) {
		Double scoreTotal = null;
		
		for (Field currField : fields) {
			try {
				
				Field matchingField = pickFieldLanguage(currField, childFields, langFieldsToConsume, formFieldsMap);
				org.openmrs.module.atd.xmlBeans.Field scorableFormField = fieldMap.get(matchingField.getId());
				
				if(scorableFormField.getValue() != null){
					if(scoreTotal == null){
						scoreTotal = 0D;
					}
					scoreTotal += Integer.parseInt(scorableFormField.getValue());
				}
				
			}
			catch (Exception e) {}
		}
		return scoreTotal;
	}
	
	private static void saveScore(Score score, Double scoreTotal, Integer encounterId, Patient patient) {
		
		org.openmrs.module.chica.xmlBeans.Concept xmlConcept = score.getConcept();
		String conceptName = xmlConcept.getName();
				
		ConceptService conceptService = Context.getConceptService();
		Concept concept = conceptService.getConcept(conceptName);
		if (concept != null) {
			ObsService obsService = Context.getObsService();
			EncounterService encounterService = Context.getService(EncounterService.class);
			org.openmrs.Encounter encounter = encounterService.getEncounter(encounterId);
			Obs obs = new Obs(patient, concept, new java.util.Date(), encounter.getLocation());
			obs.setValueNumeric(scoreTotal);
			obs.setEncounter(encounter);
			obsService.saveObs(obs, "");
		} else {
			log.error("Concept " + conceptName + " does not exist to save score");
		}
		
	}
	
	private static Field pickFieldLanguage(Field currField, HashMap<String, FormField> childFields,
	                                HashMap<String, Field> langFieldsToConsume,
	                                HashMap<String, HashMap<String, FormField>> formFieldsMap) {
		String fieldName = currField.getId();
		
		//field name in config file matches the preferred language
		//field name
		childFields = formFieldsMap.get(fieldName);
		Field matchingField = null;
		
		if (childFields != null) {
			
			//see which of the child fields is in the language list
			for (String currChildFieldName : childFields.keySet()) {
				
				matchingField = langFieldsToConsume.get(currChildFieldName);
				if (matchingField != null) {
					break;
				}
			}
		}
		if (matchingField == null) {
			matchingField = currField;
		}
		return matchingField;
	}
}
