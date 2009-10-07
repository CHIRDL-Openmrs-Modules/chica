package org.openmrs.module.chica.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.FieldType;
import org.openmrs.Form;
import org.openmrs.FormField;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.FormService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.module.atd.ParameterHandler;
import org.openmrs.module.atd.TeleformTranslator;
import org.openmrs.module.atd.datasource.TeleformExportXMLDatasource;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.hibernateBeans.PatientATD;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.Session;
import org.openmrs.module.atd.hibernateBeans.State;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.ChicaParameterHandler;
import org.openmrs.module.chica.Percentile;
import org.openmrs.module.chica.db.ChicaDAO;
import org.openmrs.module.chica.hibernateBeans.Bmiage;
import org.openmrs.module.chica.hibernateBeans.Chica1Appointment;
import org.openmrs.module.chica.hibernateBeans.Chica1Patient;
import org.openmrs.module.chica.hibernateBeans.Chica1PatientObsv;
import org.openmrs.module.chica.hibernateBeans.ChicaError;
import org.openmrs.module.chica.hibernateBeans.ChicaHL7Export;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.hibernateBeans.Family;
import org.openmrs.module.chica.hibernateBeans.Hcageinf;
import org.openmrs.module.chica.hibernateBeans.Lenageinf;
import org.openmrs.module.chica.hibernateBeans.LocationTagAttributeValue;
import org.openmrs.module.chica.hibernateBeans.OldRule;
import org.openmrs.module.chica.hibernateBeans.PatientFamily;
import org.openmrs.module.chica.hibernateBeans.Statistics;
import org.openmrs.module.chica.hibernateBeans.Study;
import org.openmrs.module.chica.hibernateBeans.StudyAttributeValue;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chica.xmlBeans.Language;
import org.openmrs.module.chica.xmlBeans.LanguageAnswers;
import org.openmrs.module.chica.xmlBeans.PWSPromptAnswerErrs;
import org.openmrs.module.chica.xmlBeans.PWSPromptAnswers;
import org.openmrs.module.chica.xmlBeans.StatsConfig;
import org.openmrs.module.dss.DssElement;
import org.openmrs.module.dss.DssManager;
import org.openmrs.module.dss.hibernateBeans.Rule;
import org.openmrs.module.dss.service.DssService;
import org.openmrs.module.dss.util.Util;
import org.openmrs.module.dss.util.XMLUtil;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ChicaServiceImpl implements ChicaService
{

	private Log log = LogFactory.getLog(this.getClass());

	private ChicaDAO dao;

	/**
	 * 
	 */
	public ChicaServiceImpl()
	{
	}

	/**
	 * @return
	 */
	public ChicaDAO getChicaDAO()
	{
		return this.dao;
	}

	/**
	 * @param dao
	 */
	public void setChicaDAO(ChicaDAO dao)
	{
		this.dao = dao;
	}

	/**
	 * @should testPSFConsume
	 * @should testPWSConsume
	 */
	public void consume(InputStream input, Patient patient,
			Integer encounterId,Integer formId,
			Integer formInstanceId,Integer sessionId,
			List<FormField> fieldsToConsume)
	{
		try
		{
			DssService dssService = Context
					.getService(DssService.class);
			ATDService atdService = Context
					.getService(ATDService.class);
			ParameterHandler parameterHandler = new ChicaParameterHandler();

			// check that the medical record number in the xml file and the medical
			// record number of the patient match
			String patientMedRecNumber = patient.getPatientIdentifier()
					.getIdentifier();
			String xmlMedRecNumber = null;
			String xmlMedRecNumber2 = null;
			String formInstanceIdValue2 = null;
			String formInstanceIdValue = null;
			LogicService logicService = Context.getLogicService();

			TeleformExportXMLDatasource xmlDatasource = (TeleformExportXMLDatasource) logicService
					.getLogicDataSource("xml");
			HashMap<String, org.openmrs.module.atd.xmlBeans.Field> fieldMap = xmlDatasource
					.getParsedFile(formInstanceId, formId);

			if (fieldMap == null)
			{
				try
				{
					FormInstance formInstance = xmlDatasource.parse(input,
							formInstanceId, formId);
					formInstanceId = formInstance.getFormInstanceId();
					formId = formInstance.getFormId();
					fieldMap = xmlDatasource.getParsedFile(formInstanceId, formId);

				} catch (Exception e1)
				{
					ChicaError chicaError = new ChicaError("Error","XML Parsing", 
							" Error parsing XML file to be consumed. Form Instance Id = " + formInstanceId
							, Util.getStackTrace(e1), new Date(),sessionId);
					this.saveError(chicaError);
					return;
				}
			}

			String formInstanceIdTag  = org.openmrs.module.atd.util.Util
				.getFormAttributeValue(formId, "formInstanceIdTag");
			String formInstanceIdTag2  = org.openmrs.module.atd.util.Util
				.getFormAttributeValue(formId, "formInstanceIdTag2");
			
			if (formInstanceIdTag2!=null&&fieldMap.get(formInstanceIdTag2)!= null){
				formInstanceIdValue2 = fieldMap.get(formInstanceIdTag2).getValue();
			}
			if (formInstanceIdTag != null && fieldMap.get(formInstanceIdTag)!= null){
				formInstanceIdValue = fieldMap.get(formInstanceIdTag).getValue();
			}
			if ((formInstanceIdValue == null || formInstanceIdValue.equals("") )
					&& ( formInstanceIdValue2 == null || formInstanceIdValue2.equals(""))){
				ChicaError error = new ChicaError("Fatal", "Form Instance ID Validity", 
						"Form instance id bar codes are null or empty on front and back of form"
						,formInstanceIdTag + " : " + formInstanceIdValue  
						+ "\r\n " + formInstanceIdTag2 + " : " + formInstanceIdValue2 
						, new Date(), sessionId);
				this.saveError(error);
				return;
			}
			if (! formInstanceIdValue.equalsIgnoreCase(formInstanceIdValue2)){
				ChicaError error = new ChicaError("Warning", "Form Instance ID Validity", 
						"Form instance id bar codes on front and back of form do not match."
						,formInstanceIdTag + " : " + formInstanceIdValue 
						+ "\r\n " + formInstanceIdTag2 + " : " + formInstanceIdValue2 
						+ "\r\n " + "formId: " + formId 
						+ "\r\n Patient MRN: " + patientMedRecNumber
						, new Date(), sessionId);
				this.saveError(error);
			}
			
			String medRecNumberTag = org.openmrs.module.atd.util.Util
					.getFormAttributeValue(formId, "medRecNumberTag");

			String medRecNumberTag2 = org.openmrs.module.atd.util.Util
					.getFormAttributeValue(formId, "medRecNumberTag2");
			
			//MRN
			if (medRecNumberTag!=null&&fieldMap.get(medRecNumberTag) != null)
			{
				xmlMedRecNumber = fieldMap.get(medRecNumberTag).getValue();
			}
			
			if (medRecNumberTag2 != null &&fieldMap.get(medRecNumberTag2)!=null ){
				xmlMedRecNumber2 = fieldMap.get(medRecNumberTag2).getValue();
			}
			
			//Compare form MRNs to patient medical record number
			if (!Util.extractIntFromString(patientMedRecNumber).equalsIgnoreCase(
					Util.extractIntFromString(xmlMedRecNumber)))
			{
				//Compare patient MRN to MRN bar code from back of form.
				if (xmlMedRecNumber2 == null || !Util.extractIntFromString(patientMedRecNumber)
							.equalsIgnoreCase( Util.extractIntFromString(xmlMedRecNumber2))){
					ChicaError noMatch = new ChicaError("Fatal", "MRN Validity", "Patient MRN" 
							+ " does not match any form MRN bar codes (front or back) " 
							,"\r\n Form instance id: "  + formInstanceId 
							+ "\r\n " + "formId: " + formId 
							+ "\r\n Patient MRN: " + patientMedRecNumber
							+ " \r\n MRN barcode front: " + xmlMedRecNumber + "\r\n MRN barcode back: "
							+ xmlMedRecNumber2, new Date(), sessionId);
				    this.saveError(noMatch);
				    return;
					
				} 
				//Patient MRN does not match MRN bar code on front of form, but does match 
				// MRN bar code on back of form.
				ChicaError warning = new ChicaError("Warning", "MRN Validity", "Patient MRN matches" 
							+ " MRN bar code from back of form only. " 
							,"Form instance id: "  + formInstanceId 
							+ "\r\n " + "formId: " + formId 
							+ "\r\n Patient MRN: " + patientMedRecNumber
							+ " \r\n MRN barcode front: " + xmlMedRecNumber + "\r\n MRN barcode back: " 
							+ xmlMedRecNumber2, new Date(), sessionId);
				 this.saveError(warning);
				
				
			}else{
			
				//Check for conflicting front and back MRN bar codes.
				if (!Util.extractIntFromString(xmlMedRecNumber).equalsIgnoreCase(
						Util.extractIntFromString(xmlMedRecNumber2)))
				{
					ChicaError chicaError = new ChicaError("Warning", "MRN Validity", "Patient MRN matches " 
							+ " MRN bar code on front of form, but the front and back of the form do not match."
							+ " Possible scan error. "
							, "\r\n Form instance id: "  + formInstanceId + " \r\n MRN bar code front: " + xmlMedRecNumber + "\r\n MRN bar code back: "
							+ xmlMedRecNumber2, new Date(), sessionId);
				    this.saveError(chicaError);
				}
			}
			
			// make sure storeObs gets loaded before running consume
			// rules
			dssService.loadRule("CREATE_JIT",false);
			dssService.loadRule("ChicaAgeRule",false);
			dssService.loadRule("storeObs",false);
			dssService.loadRule("DDST", false);
			dssService.loadRule("LookupBPcentile", false);

			//only consume the question fields for one side of the PSF
			ArrayList<String> languageFieldsToConsume = 
				saveAnswers(fieldMap, formInstanceId, formId,encounterId);
			FormService formService = Context.getFormService();
			Form databaseForm = formService.getForm(formId);
			TeleformTranslator translator = new TeleformTranslator();

			if (fieldsToConsume == null)
			{
				fieldsToConsume = new ArrayList<FormField>();

				for (FormField currField : databaseForm.getOrderedFormFields())
				{
					FormField parentField = currField.getParent();
					String fieldName = currField.getField().getName();
					FieldType currFieldType = currField.getField()
							.getFieldType();
					// only process export fields
					if (currFieldType != null
							&& currFieldType.equals(translator
									.getFieldType("Export Field")))
					{
						if (parentField != null)
						{
							PatientATD patientATD = atdService.getPatientATD(
									formInstanceId, parentField.getField()
											.getFieldId(), formId);

							if (patientATD != null)
							{
								if (databaseForm.getName().equals("PSF"))
								{
									// consume only one side of questions for
									// PSF
									if (languageFieldsToConsume
											.contains(fieldName))
									{
										fieldsToConsume.add(currField);
									}
								} else
								{
									fieldsToConsume.add(currField);
								}
							} else
							{
								// consume all other fields with parents
								fieldsToConsume.add(currField);
							}
						} else
						{
							// consume all other fields
							fieldsToConsume.add(currField);
						}
					}
				}
			}
			atdService.consume(input, formInstanceId, formId, patient, encounterId,
					 null, null, parameterHandler,fieldsToConsume);
		} catch (Exception e)
		{
			log.error(e.getMessage());
			log.error(Util.getStackTrace(e));
		}
	}

	private void populateFieldNameArrays(
			HashMap<String, ArrayList<String>> languages,
			ArrayList<String> pwsAnswerChoices,
			ArrayList<String> pwsAnswerChoiceErr)
	{
		AdministrationService adminService = Context.getAdministrationService();
		StatsConfig statsConfig = null;
		String statsConfigFile = adminService
				.getGlobalProperty("chica.statsConfigFile");

		if(statsConfigFile == null){
			log.error("Could not find statsConfigFile. Please set global property chica.statsConfigFile.");
			return;
		}
		try
		{
			InputStream input = new FileInputStream(statsConfigFile);
			statsConfig = (StatsConfig) XMLUtil.deserializeXML(
					StatsConfig.class, input);
		} catch (IOException e1)
		{
			log.error(e1.getMessage());
			log.error(Util.getStackTrace(e1));
			return;
		}

		// process prompt answers
		PWSPromptAnswers pwsPromptAnswers = statsConfig.getPwsPromptAnswers();

		if (pwsPromptAnswers != null)
		{
			ArrayList<org.openmrs.module.chica.xmlBeans.Field> fields = pwsPromptAnswers
					.getFields();

			if (fields != null)
			{
				for (org.openmrs.module.chica.xmlBeans.Field currField : fields)
				{
					if (currField.getId() != null)
					{
						pwsAnswerChoices.add(currField.getId());
					}
				}
			}
		}

		// process prompt answer errs
		PWSPromptAnswerErrs pwsPromptAnswerErrs = statsConfig
				.getPwsPromptAnswerErrs();

		if (pwsPromptAnswerErrs != null)
		{
			ArrayList<org.openmrs.module.chica.xmlBeans.Field> fields = pwsPromptAnswerErrs
					.getFields();

			if (fields != null)
			{
				for (org.openmrs.module.chica.xmlBeans.Field currField : fields)
				{
					if (currField.getId() != null)
					{
						pwsAnswerChoiceErr.add(currField.getId());
					}
				}
			}
		}

		LanguageAnswers languageAnswers = statsConfig.getLanguageAnswers();

		if (languageAnswers != null)
		{
			ArrayList<Language> xmlLanguages = languageAnswers.getLanguages();

			for (Language currLanguage : xmlLanguages)
			{
				String languageName = currLanguage.getName();
				if (languageName != null)
				{
					ArrayList<String> currLanguageFields = new ArrayList<String>();
					languages.put(languageName, currLanguageFields);

					ArrayList<org.openmrs.module.chica.xmlBeans.Field> fields = currLanguage
							.getFields();
					for (org.openmrs.module.chica.xmlBeans.Field currField : fields)
					{
						if (currField.getId() != null)
						{
							currLanguageFields.add(currField.getId());
						}
					}
				}
			}
		}
	}

	private ArrayList<String> saveAnswers(
			HashMap<String, org.openmrs.module.atd.xmlBeans.Field> fieldMap,
			int formInstanceId, int formId, int encounterId)
	{
		ATDService atdService = Context
				.getService(ATDService.class);
		TeleformTranslator translator = new TeleformTranslator();
		FormService formService = Context.getFormService();
		Form databaseForm = formService.getForm(formId);
		if (databaseForm == null)
		{
			log.error("Could not consume teleform export xml because form "
					+ formId + " does not exist in the database");
			return null;
		}

		ArrayList<String> pwsAnswerChoices = new ArrayList<String>();
		ArrayList<String> pwsAnswerChoiceErr = new ArrayList<String>();

		HashMap<String, ArrayList<String>> languageToFieldnames = new HashMap<String, ArrayList<String>>();

		this.populateFieldNameArrays(languageToFieldnames, pwsAnswerChoices,
				pwsAnswerChoiceErr);

		HashMap<String, Integer> languageToNumAnswers = new HashMap<String, Integer>();
		HashMap<String, HashMap<Integer, String>> languageToAnswers = new HashMap<String, HashMap<Integer, String>>();

		Rule providerNameRule = new Rule();
		providerNameRule.setTokenName("providerName");
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("encounterId", encounterId);
		providerNameRule.setParameters(parameters);

		for (FormField currField : databaseForm.getFormFields())
		{
			FieldType currFieldType = currField.getField().getFieldType();
			// only process export fields
			if (currFieldType != null
					&& currFieldType.equals(translator
							.getFieldType("Export Field")))
			{
				String fieldName = currField.getField().getName();
				String answer = null;
				if (fieldMap.get(fieldName) != null)
				{
					answer = fieldMap.get(fieldName).getValue();
				}
				FormField parentField = currField.getParent();

				// if parent field is not null look at parent
				// field for rule to execute
				if (parentField != null)
				{
					PatientATD patientATD = atdService.getPatientATD(
							formInstanceId,
							parentField.getField().getFieldId(), formId);

					if (patientATD != null)
					{
						Rule rule = patientATD.getRule();

						if (answer == null || answer.length() == 0)
						{
							answer = "NoAnswer";
						}
						Integer ruleId = rule.getRuleId();

						String dsstype = databaseForm.getName();

						if (dsstype.equalsIgnoreCase("PSF"))
						{
							for (String currLanguage : languageToFieldnames
									.keySet())
							{
								ArrayList<String> currLanguageArray = languageToFieldnames
										.get(currLanguage);
								HashMap<Integer, String> answers = languageToAnswers
										.get(currLanguage);

								if (answers == null)
								{
									answers = new HashMap<Integer, String>();
									languageToAnswers
											.put(currLanguage, answers);
								}

								if (currLanguageArray.contains(currField
										.getField().getName()))
								{
									answers.put(ruleId, answer);
									if (!answer.equalsIgnoreCase("NoAnswer"))
									{
										if (languageToNumAnswers
												.get(currLanguage) == null)
										{
											languageToNumAnswers.put(
													currLanguage, 0);
										}

										languageToNumAnswers.put(currLanguage,
												languageToNumAnswers
														.get(currLanguage) + 1);
									}
								}
							}
						}

						if (dsstype.equalsIgnoreCase("PWS"))
						{
							List<Statistics> statistics = this.getChicaDAO()
									.getStatByIdAndRule(formInstanceId,
											ruleId,dsstype);

							if (statistics != null)
							{
								if (!answer.equalsIgnoreCase("NoAnswer"))
								{
									answer = formatAnswer(answer);
								}
								for (Statistics stat : statistics)
								{
									if (pwsAnswerChoices.contains(currField
											.getField().getName()))
									{
										stat.setAnswer(answer);
									}
									if (pwsAnswerChoiceErr.contains(currField
											.getField().getName()))
									{
										stat.setAnswerErr(answer);
									}

									this.updateStatistics(stat);
								}
							}
						}
					}

				}
			}
		}

		int maxNumAnswers = -1;
		String maxLanguage = null;
		HashMap<Integer, String> maxAnswers = null;

		for (String language : languageToNumAnswers.keySet())
		{
			Integer compareNum = languageToNumAnswers.get(language);

			if (compareNum != null && compareNum > maxNumAnswers)
			{
				maxNumAnswers = compareNum;
				maxLanguage = language;
				maxAnswers = languageToAnswers.get(language);
			}
		}

		if (maxNumAnswers > 0)
		{
			String languageResponse = maxLanguage;
			HashMap<Integer, String> answers = maxAnswers;

			for (Integer currRuleId : answers.keySet())
			{
				String answer = answers.get(currRuleId);
				List<Statistics> statistics = this.getChicaDAO()
						.getStatByIdAndRule(formInstanceId, currRuleId, "PSF");
				if (statistics != null)
				{
					for (Statistics stat : statistics)
					{
						stat.setAnswer(answer);
						stat.setLanguageResponse(languageResponse);

						this.updateStatistics(stat);
					}
				}
			}
		}
		
		if (maxNumAnswers > 0){
			return languageToFieldnames.get(maxLanguage);
		}else{
			return languageToFieldnames.get("English");
		}
	}

	private String formatAnswer(String answer)
	{
		final int NUM_ANSWERS = 6;
		String[] answers = new String[NUM_ANSWERS];

		// initialize all answers to N
		for (int i = 0; i < answers.length; i++)
		{
			answers[i] = "X";
		}

		char[] characters = answer.toCharArray();

		// convert the answer string from positional digits (1,2,3,4,5,6)
		// to Y
		for (int i = 0; i < characters.length; i++)
		{
			try
			{
				int intAnswer = Character.getNumericValue(characters[i]);
				int answerPos = intAnswer - 1;

				if (answerPos >= 0 && answerPos < answers.length)
				{
					answers[answerPos] = "Y";
				}
			} catch (Exception e)
			{
				log.error(e.getMessage());
				log.error(Util.getStackTrace(e));
			}
		}

		String newAnswer = "";

		for (String currAnswer : answers)
		{
			newAnswer += currAnswer;
		}

		return newAnswer;
	}

	/**
	 * @should testPSFProduce
	 * @should testPWSProduce
	 */
	public void produce(OutputStream output, PatientState state,
			Patient patient, Integer encounterId, String dssType,
			int maxDssElements) throws Exception
	{
		DssService dssService = Context
				.getService(DssService.class);
		ATDService atdService = Context
				.getService(ATDService.class);
		Integer formId = state.getFormId();
		Integer formInstanceId = state.getFormInstanceId();

		DssManager dssManager = new DssManager(patient);
		dssManager.setMaxDssElementsByType(dssType, maxDssElements);
		HashMap<String, Object> baseParameters = new HashMap<String, Object>();
		baseParameters.put("sessionId", state.getSessionId());
		dssService.loadRule("CREATE_JIT",false);
		dssService.loadRule("ChicaAgeRule",false);
		dssService.loadRule("storeObs",false);
		dssService.loadRule("DDST", false);
		dssService.loadRule("LookupBPcentile", false);

		atdService.produce(patient, formInstanceId, output, formId, dssManager,
				encounterId, baseParameters, null,true);

		this.saveStats(patient, formInstanceId, dssManager, encounterId);
	}

	private void saveStats(Patient patient, Integer formInstanceId,
			DssManager dssManager, Integer encounterId)
	{
		HashMap<String, ArrayList<DssElement>> dssElementsByType = dssManager
				.getDssElementsByType();
		EncounterService encounterService = Context
				.getService(EncounterService.class);
		Encounter encounter = (Encounter) encounterService
				.getEncounter(encounterId);
		String type = null;

		if (dssElementsByType == null)
		{
			return;
		}
		Iterator<String> iter = dssElementsByType.keySet().iterator();
		ArrayList<DssElement> dssElements = null;

		while (iter.hasNext())
		{
			type = iter.next();
			dssElements = dssElementsByType.get(type);
			for (int i = 0; i < dssElements.size(); i++)
			{
				DssElement currDssElement = dssElements.get(i);

					this.addStatistics(patient, currDssElement, formInstanceId, i,
							encounter, type);
				}
			}
		}

	public void updateStatistics(Statistics statistics)
	{
		getChicaDAO().updateStatistics(statistics);
	}
	
	public void createStatistics(Statistics statistics)
	{
		getChicaDAO().addStatistics(statistics);
	}

	private void addStatistics(Patient patient, DssElement currDssElement,
			Integer formInstanceId, int questionPosition, Encounter encounter,
			String formName)
	{
			DssService dssService = Context
					.getService(DssService.class);
			Integer ruleId = currDssElement.getRuleId();
			Rule rule = dssService.getRule(ruleId);

			Statistics statistics = new Statistics();
			statistics.setAgeAtVisit(org.openmrs.module.chica.util.Util
					.adjustAgeUnits(patient.getBirthdate(), null));
			statistics.setPriority(rule.getPriority());
			statistics.setFormInstanceId(formInstanceId);
			statistics.setPosition(questionPosition + 1);

			statistics.setRuleId(ruleId);
			statistics.setPatientId(patient.getPatientId());
			statistics.setFormName(formName);
			statistics.setEncounterId(encounter.getEncounterId());

			getChicaDAO().addStatistics(statistics);
	}

	public Percentile getWtageinf(double ageMos, int sex)
	{
		return getChicaDAO().getWtageinf(ageMos, sex);
	}

	public Bmiage getBmiage(double ageMos, int sex)
	{
		return getChicaDAO().getBmiage(ageMos, sex);
	}

	public Hcageinf getHcageinf(double ageMos, int sex)
	{
		return getChicaDAO().getHcageinf(ageMos, sex);
	}

	public Lenageinf getLenageinf(double ageMos, int sex)
	{
		return getChicaDAO().getLenageinf(ageMos, sex);
	}

	public List<Study> getActiveStudies()
	{
		return getChicaDAO().getActiveStudies();
	}

	public List<Statistics> getStatByFormInstance(int formInstanceId,String formName)
	{
		return getChicaDAO().getStatByFormInstance(formInstanceId,formName);
	}

	public StudyAttributeValue getStudyAttributeValue(Study study,
			String studyAttributeName)
	{
		return getChicaDAO().getStudyAttributeValue(study, studyAttributeName);
	}

	public List<Statistics> getStatByIdAndRule(int formInstanceId,int ruleId,String formName)	{
		return getChicaDAO().getStatByIdAndRule(formInstanceId,ruleId,formName);
	}

	public List<OldRule> getAllOldRules()
	{
		return getChicaDAO().getAllOldRules();
	}

	public List<Chica1PatientObsv> getChicaPatientObsByPSF(Integer psfId,
			Integer patientId)
	{
		return getChicaDAO().getChicaPatientObsByPSF(psfId, patientId);
	}

	public List<Chica1PatientObsv> getChicaPatientObsByPWS(Integer pwsId,
			Integer patientId)
	{
		return getChicaDAO().getChicaPatientObsByPWS(pwsId, patientId);
	}

	public List<Chica1Appointment> getChica1AppointmentsByPatient(
			Integer patientId)
	{
		return getChicaDAO().getChica1AppointmentsByPatient(patientId);
	}

	public List<Chica1Patient> getChica1Patients()
	{
		return getChicaDAO().getChica1Patients();
	}

	public PatientFamily getPatientFamily(Integer patientId)
	{
		return getChicaDAO().getPatientFamily(patientId);
	}

	public Family getFamilyByAddress(String address)
	{
		return getChicaDAO().getFamilyByAddress(address);

	}
	
	public Family getFamilyByPhone(String phone)
	{
		return getChicaDAO().getFamilyByPhone(phone);

	}

	public void savePatientFamily(PatientFamily patientFamily)
	{
		getChicaDAO().savePatientFamily(patientFamily);

	}

	public void saveFamily(Family family)
	{
		getChicaDAO().saveFamily(family);
	}

	public void updateFamily(Family family)
	{
		getChicaDAO().updateFamily(family);
	}
	
	public Obs getStudyArmObs(Integer familyId,Concept studyConcept){
		return getChicaDAO().getStudyArmObs(familyId, studyConcept);
	}
	
	public List<String> getInsCategories(){
		return getChicaDAO().getInsCategories();
	}
	
	public void updateChica1Patient(Chica1Patient patient){
		getChicaDAO().updateChica1Patient(patient);
	}
	
	public void updateChica1Appointment(Chica1Appointment appointment){
		getChicaDAO().updateChica1Appointment(appointment);
	}
	
	public void setChica1PatientObsvObsId(Chica1PatientObsv chica1PatientObsv){
		getChicaDAO().setChica1PatientObsvObsId(chica1PatientObsv);
	}
	
	public List<Chica1PatientObsv> getUnloadedChicaPatientObs(Integer patientId,String date){
		return getChicaDAO().getUnloadedChicaPatientObs(patientId, date);
	}
	
	public List<Chica1Appointment> getChica1AppointmentsByDate(Integer patientId, String date){
		return getChicaDAO().getChica1AppointmentsByDate(patientId, date);
	}
	public String getObsvNameByObsvId(String obsvId){
		return getChicaDAO().getObsvNameByObsvId(obsvId);
	}
	
	public String getInsCategoryByCarrier(String carrierCode){
		return getChicaDAO().getInsCategoryByCarrier(carrierCode);
	}
	
	public String getInsCategoryBySMS(String smsCode){
		return getChicaDAO().getInsCategoryBySMS(smsCode);
	}
	
	public String getInsCategoryByInsCode(String insCode){
		return getChicaDAO().getInsCategoryByInsCode(insCode);
	}
	
	public void saveError(ChicaError error){
		getChicaDAO().saveError(error);
	}
	
	public Integer getErrorCategoryIdByName(String name){
		return getChicaDAO().getErrorCategoryIdByName(name);
	}
	public List<ChicaError> getChicaErrorsByLevel(String errorLevel,Integer sessionId){
		return getChicaDAO().getChicaErrorsByLevel(errorLevel, sessionId);
	}
	
	public PatientState getPrevProducePatientState(Integer sessionId, Integer patientStateId ){
		ATDService atdService = Context.getService(ATDService.class);
		return atdService.getPrevPatientStateByAction(sessionId, patientStateId,"PRODUCE FORM INSTANCE");
	}
	
	public Double getHighBP(Patient patient, Integer bpPercentile,
			String bpType, org.openmrs.Encounter encounter)
	{
		List<Person> persons = new ArrayList<Person>();
		persons.add(patient);
		ConceptService conceptService = Context.getConceptService();
		Concept concept = conceptService.getConceptByName("HtCentile");
		List<org.openmrs.Encounter> encounters = new ArrayList<org.openmrs.Encounter>();
		encounters.add(encounter);
		Double heightPercentile = null;
		ObsService obsService = Context.getObsService();
		List<Concept> concepts = new ArrayList<Concept>();

		concepts.add(concept);

		List<Obs> obs = obsService
				.getObservations(persons, encounters, concepts, null, null,
						null, null, null, null, null, null, false);
		if (obs != null && obs.size() > 0)
		{
			heightPercentile = obs.get(0).getValueNumeric();
		}

		if (heightPercentile == null)
		{
			return null;
		}
		
		return getHighBP(patient,bpPercentile,bpType,heightPercentile, new Date());
	}
	
	/**
	 * @should checkHighBP
	 */
	public Double getHighBP(Patient patient, Integer bpPercentile, String bpType, 
			Double heightPercentile, Date onDate)
	{
		Integer lowerAge = patient.getAge(onDate);
		
		if(lowerAge > 17){
			return null;
		}
		
		Integer upperAge = lowerAge + 1;
		String sex = patient.getGender();

		Integer lowerPercentile = null;
		Integer upperPercentile = null;

		if (heightPercentile < 5)
		{
			lowerPercentile = 5;
			upperPercentile = 5;
		}

		if (heightPercentile >= 5)
		{
			lowerPercentile = 5;
			upperPercentile = 10;
		}

		if (heightPercentile >= 10)
		{
			lowerPercentile = 10;
			upperPercentile = 25;
		}

		if (heightPercentile >= 25)
		{
			lowerPercentile = 25;
			upperPercentile = 50;
		}

		if (heightPercentile >= 50)
		{
			lowerPercentile = 50;
			upperPercentile = 75;
		}

		if (heightPercentile >= 75)
		{
			lowerPercentile = 75;
			upperPercentile = 90;
		}

		if (heightPercentile >= 90)
		{
			lowerPercentile = 90;
			upperPercentile = 95;
		}

		if (heightPercentile >= 95)
		{
			lowerPercentile = 95;
			upperPercentile = 95;
		}
		
		Integer lowerAgeLowerPercentile = null;
		Integer lowerAgeUpperPercentile = null;
		Integer upperAgeLowerPercentile = null;
		Integer upperAgeUpperPercentile = null;

		if (lowerAge >=1)
		{
			lowerAgeLowerPercentile = getChicaDAO().getHighBP(lowerAge,
					sex, bpPercentile, bpType, lowerPercentile);
		}
		if (lowerAge >=1)
		{
			lowerAgeUpperPercentile = getChicaDAO().getHighBP(lowerAge,
					sex, bpPercentile, bpType, upperPercentile);
		}
		if (upperAge <=17)
		{
			upperAgeLowerPercentile = getChicaDAO().getHighBP(upperAge,
					sex, bpPercentile, bpType, lowerPercentile);
		}
		if (upperAge <=17)
		{
			upperAgeUpperPercentile = getChicaDAO().getHighBP(upperAge,
					sex, bpPercentile, bpType, upperPercentile);
		}
		
		Double lowerAgeInter = null;
		
		// prevents division by zero
		Integer denominator = (upperPercentile - lowerPercentile);
		Double firstOperand = (heightPercentile - lowerPercentile);

		if (denominator == 0)
		{
			firstOperand = 0.0;
		} else
		{
			firstOperand = firstOperand / denominator;
		}
		
		//interpolate lowerAgeLowerPercentile and lowerAgeUpperPercentile
		if(lowerAgeLowerPercentile != null && lowerAgeUpperPercentile != null){
					
			lowerAgeInter = (firstOperand * (lowerAgeUpperPercentile - lowerAgeLowerPercentile))
					+ lowerAgeLowerPercentile;
		}
		
		Double upperAgeInter = null;
		
		//interpolate upperAgeLowerPercentile and upperAgeUpperPercentile
		if (upperAgeLowerPercentile != null && upperAgeUpperPercentile != null)
		{
			upperAgeInter = (firstOperand * (upperAgeUpperPercentile - upperAgeLowerPercentile))
					+ upperAgeLowerPercentile;
		}
		
		double fractionalAge = Util.getFractionalAgeInUnits(patient.getBirthdate(), onDate,Util.YEAR_ABBR);
		
		double fraction = fractionalAge - lowerAge;
		
		if(lowerAgeInter == null && upperAgeInter == null){
			if(lowerAgeUpperPercentile != null&&upperAgeUpperPercentile != null){
				upperAgeInter = upperAgeUpperPercentile.doubleValue();
				lowerAgeInter = lowerAgeUpperPercentile.doubleValue();
			}
			if(lowerAgeLowerPercentile != null&&upperAgeLowerPercentile != null){
				upperAgeInter = upperAgeLowerPercentile.doubleValue();
				lowerAgeInter = lowerAgeLowerPercentile.doubleValue();
			}
		}
		
		if(lowerAgeInter == null){
			return Util.round(upperAgeInter,2);
		}
		
		if(upperAgeInter == null){
			return Util.round(lowerAgeInter,2);
		}
		
		return Util.round(lowerAgeInter+((upperAgeInter-lowerAgeInter)*fraction),2);
		}

		public String getDDSTLeaf(String category, Integer ageInDays){
			return getChicaDAO().getDDSTLeaf(category, ageInDays);
		}
		
		public List<Statistics> getStatsByEncounterForm(Integer encounterId,String formName){
			return getChicaDAO().getStatsByEncounterForm(encounterId, formName);
		}

		public List<Statistics> getStatsByEncounterFormNotPrioritized(Integer encounterId,String formName){
			return getChicaDAO().getStatsByEncounterFormNotPrioritized(encounterId, formName);
		}
	
		
		public void insertEncounterToHL7ExportQueue(ChicaHL7Export export) {
			getChicaDAO().insertEncounterToHL7ExportQueue(export);
		}

		public List<ChicaHL7Export> getPendingHL7Exports() {
			return getChicaDAO().getPendingHL7Exports();
			
		}

		public void saveChicaHL7Export(ChicaHL7Export export) {

			ATDService atdService = Context.getService(ATDService.class);
			getChicaDAO().saveChicaHL7Export(export);
			
		}
	
		public List<ChicaHL7Export> getPendingHL7ExportsByEncounterId(Integer encounterId) {
			return getChicaDAO().getPendingHL7ExportsByEncounterId(encounterId);
		}
		
		public List<PatientState> getReprintRescanStatesByEncounter(Integer encounterId, Date optionalDateRestriction){
			return getChicaDAO().getReprintRescanStatesByEncounter(encounterId, optionalDateRestriction);
		}
		
		public List<String> getPrinterStations(){
			List<String>  printerAttrNames = getChicaDAO().getPrinterStations();
			List<String>  stationNames = new ArrayList<String>();
			for (String name : printerAttrNames){
				if (name != null){
					Integer space = name.indexOf(" ");
					String station = name.substring(space + 1);
					stationNames.add(station);
				}
			}
			return stationNames;
		}
		
		public ArrayList<String> getImagesDirectory(String location){
			ArrayList<String> dirs = getChicaDAO().getImagesDirectory(location);
			return dirs;
		}
		
		public Chica1Appointment getChica1AppointmentByEncounterId(Integer encId){
			Chica1Appointment appt= getChicaDAO().getChica1AppointmentByEncounterId(encId);
			return appt;
			
		}

		public LocationTagAttributeValue getLocationTagAttributeValue(Integer locationTagId,
				String locationTagAttributeName){
			return getChicaDAO().
				getLocationTagAttributeValue(locationTagId, 
						locationTagAttributeName);
		}
}