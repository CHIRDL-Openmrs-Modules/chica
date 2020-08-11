package org.openmrs.module.chica.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.FieldType;
import org.openmrs.Form;
import org.openmrs.FormField;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.module.atd.ParameterHandler;
import org.openmrs.module.atd.TeleformTranslator;
import org.openmrs.module.atd.datasource.FormDatasource;
import org.openmrs.module.atd.hibernateBeans.PatientATD;
import org.openmrs.module.atd.hibernateBeans.Statistics;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.atd.xmlBeans.Field;
import org.openmrs.module.chica.ChicaParameterHandler;
import org.openmrs.module.chica.Percentile;
import org.openmrs.module.chica.db.ChicaDAO;
import org.openmrs.module.chica.hibernateBeans.Bmiage;
import org.openmrs.module.chica.hibernateBeans.Chica1Appointment;
import org.openmrs.module.chica.hibernateBeans.Chica1Patient;
import org.openmrs.module.chica.hibernateBeans.Chica1PatientObsv;
import org.openmrs.module.chica.hibernateBeans.ChicaHL7Export;
import org.openmrs.module.chica.hibernateBeans.ChicaHL7ExportMap;
import org.openmrs.module.chica.hibernateBeans.ChicaHL7ExportStatus;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.hibernateBeans.Family;
import org.openmrs.module.chica.hibernateBeans.Hcageinf;
import org.openmrs.module.chica.hibernateBeans.Lenageinf;
import org.openmrs.module.chica.hibernateBeans.PatientFamily;
import org.openmrs.module.chica.hibernateBeans.Study;
import org.openmrs.module.chica.hibernateBeans.StudyAttribute;
import org.openmrs.module.chica.hibernateBeans.StudyAttributeValue;
import org.openmrs.module.chica.hibernateBeans.StudySubject;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chica.study.dp3.DeviceSyncRunnable;
import org.openmrs.module.chica.study.dp3.NewGlookoUserRunnable;
import org.openmrs.module.chica.xmlBeans.LanguageAnswers;
import org.openmrs.module.chica.xmlBeans.PWSPromptAnswerErrs;
import org.openmrs.module.chica.xmlBeans.PWSPromptAnswers;
import org.openmrs.module.chica.xmlBeans.StatsConfig;
import org.openmrs.module.chirdlutil.threadmgmt.ThreadManager;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutil.util.XMLUtil;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.LocationTagAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.module.dss.hibernateBeans.Rule;

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
			Integer encounterId,FormInstance formInstance,
			Integer sessionId,
			List<FormField> fieldsToConsume,
			Integer locationTagId)
	{
		long startTime = System.currentTimeMillis();
		ATDService atdService = Context.getService(ATDService.class);
		try
		{
			ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
			ParameterHandler parameterHandler = new ChicaParameterHandler();

			LogicService logicService = Context.getLogicService();

			FormDatasource formDatasource = (FormDatasource) logicService
					.getLogicDataSource("form");
			HashMap<String, org.openmrs.module.atd.xmlBeans.Field> fieldMap = formDatasource
					.getFormFields(formInstance);

			Integer formInstanceId = formInstance.getFormInstanceId();

			if (fieldMap == null)
			{
				try
				{
					formInstance = formDatasource.parseTeleformXmlFormat(input,
							formInstance,locationTagId);
					fieldMap = formDatasource.getFormFields(formInstance);

				} catch (Exception e1)
				{
					org.openmrs.module.chirdlutilbackports.hibernateBeans.Error Error = new org.openmrs.module.chirdlutilbackports.hibernateBeans.Error("Error","XML Parsing", 
							" Error parsing XML file to be consumed. Form Instance Id = " + formInstanceId
							, Util.getStackTrace(e1), new Date(),sessionId);
					chirdlutilbackportsService.saveError(Error);
					return;
				}
			}

			startTime = System.currentTimeMillis();

			Integer formId = formInstance.getFormId();
			
			startTime = System.currentTimeMillis();
			//only consume the question fields for one side of the PSF
			Map<String,Field> languageFieldsToConsume = 
				saveAnswers(fieldMap, formInstance,encounterId,patient);
			FormService formService = Context.getFormService();
			Form databaseForm = formService.getForm(formId);
			TeleformTranslator translator = new TeleformTranslator();

			if (fieldsToConsume == null)
			{
				fieldsToConsume = new ArrayList<FormField>();
				String formType = org.openmrs.module.chirdlutil.util.Util.getFormType(formInstance.getFormId(), locationTagId, formInstance.getLocationId());
				
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
									formInstance, parentField.getField()
											.getFieldId());

							if (patientATD != null)
							{
								if (ChirdlUtilConstants.PATIENT_FORM_TYPE.equalsIgnoreCase(formType)) 
								{
									// consume only one side of questions for
									// PSF
									if (languageFieldsToConsume
											.get(fieldName)!=null)
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
			System.out.println("chicaService.consume: Fields to consume: "+
				(System.currentTimeMillis()-startTime));
			
			startTime = System.currentTimeMillis();
			atdService.consume(input, formInstance, patient, encounterId,
					 null, parameterHandler,
					 fieldsToConsume,locationTagId,sessionId);
			System.out.println("chicaService.consume: Time of atdService.consume: "+
				(System.currentTimeMillis()-startTime));
		} catch (Exception e)
		{
			log.error(e.getMessage());
			log.error(Util.getStackTrace(e));
		}
	}

	private void populateFieldNameArrays(
			HashMap<String, HashMap<String, Field>> languages,
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
		org.openmrs.module.atd.util.Util.populateFieldNameArrays(languages, languageAnswers);
	}
	
	public Map<String, Field> saveAnswers(Map<String, org.openmrs.module.atd.xmlBeans.Field> fieldMap, 
		FormInstance formInstance, int encounterId, Patient patient)
	{
		Integer formId = formInstance.getFormId();
		Form databaseForm = Context.getFormService().getForm(formId);
		if (databaseForm == null)
		{
			log.error("Could not consume teleform export xml because form "
					+ formId + " does not exist in the database");
			return null;
		}
		
		return saveAnswers(fieldMap, formInstance, encounterId, patient, databaseForm, databaseForm.getFormFields());
	}
	
	public Map<String, Field> saveAnswers(Map<String, org.openmrs.module.atd.xmlBeans.Field> fieldMap, 
		FormInstance formInstance, int encounterId, Patient patient, Set<FormField> formFieldsToSave)
	{
		Integer formId = formInstance.getFormId();
		Form databaseForm = Context.getFormService().getForm(formId);
		if (databaseForm == null)
		{
			log.error("Could not consume teleform export xml because form "
					+ formId + " does not exist in the database");
			return null;
		}
		
		return saveAnswers(fieldMap, formInstance, encounterId, patient, databaseForm, formFieldsToSave);
	}

	private Map<String, Field> saveAnswers(Map<String, org.openmrs.module.atd.xmlBeans.Field> fieldMap, 
		FormInstance formInstance, int encounterId, Patient patient, Form databaseForm, Set<FormField> formFieldsToSave)
	{
		ATDService atdService = Context
				.getService(ATDService.class);
		TeleformTranslator translator = new TeleformTranslator();

		ArrayList<String> pwsAnswerChoices = new ArrayList<String>();
		ArrayList<String> pwsAnswerChoiceErr = new ArrayList<String>();

		HashMap<String, HashMap<String, Field>> languageToFieldnames = new HashMap<String, HashMap<String, Field>>();

		this.populateFieldNameArrays(languageToFieldnames, pwsAnswerChoices,
				pwsAnswerChoiceErr);

		HashMap<String, Integer> languageToNumAnswers = new HashMap<String, Integer>();
		HashMap<String, HashMap<Integer, String>> languageToAnswers = new HashMap<String, HashMap<Integer, String>>();

		Rule providerNameRule = new Rule();
		providerNameRule.setTokenName("providerName");
		HashMap<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("encounterId", encounterId);
		providerNameRule.setParameters(parameters);

		Map<Integer, PatientATD> fieldIdToPatientATDMap = new HashMap<Integer, PatientATD>();
		EncounterService encounterService = Context.getService(EncounterService.class);
		Encounter encounter = (Encounter) encounterService.getEncounter(encounterId);
		Integer locationTagId = org.openmrs.module.chica.util.Util.getLocationTagId(encounter);
		String formType = org.openmrs.module.chirdlutil.util.Util.getFormType(formInstance.getFormId(), locationTagId, formInstance.getLocationId());

		for (FormField currField : formFieldsToSave)
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
					Integer parentFieldId = parentField.getField().getFieldId();
					PatientATD patientATD = fieldIdToPatientATDMap.get(parentFieldId);
					if (patientATD == null) {
						patientATD = atdService.getPatientATD(formInstance, parentFieldId);
						fieldIdToPatientATDMap.put(parentFieldId, patientATD);
					}

					if (patientATD != null)
					{
						Rule rule = patientATD.getRule(); 

						if (answer == null || answer.length() == 0)
						{
							answer = "NoAnswer";
						}
						Integer ruleId = rule.getRuleId();

						String dsstype = databaseForm.getName();
						
						if (ChirdlUtilConstants.PATIENT_FORM_TYPE.equalsIgnoreCase(formType))
						{
							for (String currLanguage : languageToFieldnames
									.keySet())
							{
								HashMap<String, Field> currLanguageArray = languageToFieldnames
										.get(currLanguage);
								HashMap<Integer, String> answers = languageToAnswers
										.get(currLanguage);

								if (answers == null)
								{
									answers = new HashMap<Integer, String>();
									languageToAnswers
											.put(currLanguage, answers);
								}
						
								if (currLanguageArray.get(currField
										.getField().getName())!= null)
								{
									answers.put(ruleId, answer);
									if (languageToNumAnswers
											.get(currLanguage) == null)
									{
										languageToNumAnswers.put(
												currLanguage, 0);
									}
									
									if (!answer.equalsIgnoreCase("NoAnswer"))
									{
										languageToNumAnswers.put(currLanguage,
												languageToNumAnswers
														.get(currLanguage) + 1);
									}
								}
							}
						}

						if (ChirdlUtilConstants.PHYSICIAN_FORM_TYPE.equalsIgnoreCase(formType))
						{
							Integer formInstanceId = formInstance.getFormInstanceId();
							Integer locationId = formInstance.getLocationId();
							List<Statistics> statistics = atdService
									.getStatByIdAndRule(formInstanceId,
											ruleId,dsstype,locationId);

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

									atdService.updateStatistics(stat);
								}
							}
						}
					}

				}
			}
		}

		fieldIdToPatientATDMap.clear();
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

		String languageResponse = null;
		if (maxNumAnswers > 0)
		{
			languageResponse = maxLanguage;
		}
		
		if (languageResponse != null) {
			HashMap<Integer, String> answers = maxAnswers;
			if (answers != null) {
				String patientForm = org.openmrs.module.chica.util.Util.getPrimaryFormNameByLocationTag((org.openmrs.module.chica.hibernateBeans.Encounter) encounter, ChirdlUtilConstants.LOC_TAG_ATTR_PRIMARY_PATIENT_FORM);
				Integer formInstanceId = formInstance.getFormInstanceId();
				Integer locationId = formInstance.getLocationId();
				for (Integer currRuleId : answers.keySet())
				{
					String answer = answers.get(currRuleId);
					List<Statistics> statistics = atdService
							.getStatByIdAndRule(formInstanceId, currRuleId, patientForm, locationId);
					if (statistics != null)
					{
						for (Statistics stat : statistics)
						{
							stat.setAnswer(answer);
							stat.setLanguageResponse(languageResponse);
		
							atdService.updateStatistics(stat);
						}
					}
				}
			}
		}
		
		//save language response to preferred language
		//language is determined by maximum number of answers
		//selected for a language on the PSF
		if (languageResponse != null&& ChirdlUtilConstants.PATIENT_FORM_TYPE.equalsIgnoreCase(formType)) {
			ObsService obsService = Context.getObsService();
			Obs obs = new Obs();
			String conceptName = "preferred_language";
			ConceptService conceptService = Context.getConceptService();
			Concept currConcept = conceptService.getConceptByName(conceptName);
			Concept languageConcept = conceptService.getConceptByName(languageResponse);
			if (currConcept == null || languageConcept == null) {
				log
					.error("Could not save preferred language for concept: " + conceptName + " and language: "
				        + languageResponse);
			} else {
				obs.setValueCoded(languageConcept);
		
				Location location = encounter.getLocation();
				
				obs.setPerson(patient);
				obs.setConcept(currConcept);
				obs.setLocation(location);
				obs.setEncounter(encounter);
				obs.setObsDatetime(new Date());
				obsService.saveObs(obs, null);
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
	
	/**
	 * @see org.openmrs.module.chica.service.ChicaService#getStudyAttributeByName(java.lang.String, boolean)
	 */
	public List<StudyAttribute> getStudyAttributeByName(String studyAttributeName, boolean includeRetired)
	{
		return getChicaDAO().getStudyAttributeByName(studyAttributeName, includeRetired);
	}
	
	public StudyAttributeValue getStudyAttributeValue(Study study,
			String studyAttributeName)
	{
		return getChicaDAO().getStudyAttributeValue(study, studyAttributeName);
	}

	/**
	 * @see org.openmrs.module.chica.service.ChicaService#getStudyAttributeValue(java.util.List, java.util.List, boolean)
	 */
	public List<StudyAttributeValue> getStudyAttributeValue(List<Study> studyList,
			List<StudyAttribute> studyAttributeList, boolean includeRetired)
	{
		return getChicaDAO().getStudyAttributeValue(studyList, studyAttributeList, includeRetired);
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
	
	public List<Chica1PatientObsv> getUnloadedChicaPatientObs(Integer patientId,String date){
		return getChicaDAO().getUnloadedChicaPatientObs(patientId, date);
	}
	
	public List<Chica1Appointment> getChica1AppointmentsByDate(Integer patientId, String date){
		return getChicaDAO().getChica1AppointmentsByDate(patientId, date);
	}
	
	public String getInsCategoryByCarrier(String carrierCode, String sendingFacility,String sendingApplication){
		return getChicaDAO().getInsCategoryByCarrier(carrierCode,sendingFacility,sendingApplication);
	}
	
	public String getInsCategoryByName(String insuranceName, String sendingFacility,String sendingApplication){
		return getChicaDAO().getInsCategoryByName(insuranceName,sendingFacility,sendingApplication);
	}
	
	public String getInsCategoryByInsCode(String insCode, String sendingFacility,String sendingApplication){
		return getChicaDAO().getInsCategoryByInsCode(insCode,sendingFacility,sendingApplication);
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
		
		public ChicaHL7Export insertEncounterToHL7ExportQueue(ChicaHL7Export export) {
			getChicaDAO().insertEncounterToHL7ExportQueue(export);
			return export;
		}

		public List<ChicaHL7Export> getPendingHL7Exports() {
			return getChicaDAO().getPendingHL7Exports();
			
		}

		public void saveChicaHL7Export(ChicaHL7Export export) {

			getChicaDAO().saveChicaHL7Export(export);
			
		}
	
		public List<ChicaHL7Export> getPendingHL7ExportsByEncounterId(Integer encounterId) {
			return getChicaDAO().getPendingHL7ExportsByEncounterId(encounterId);
		}
	
		public List<PatientState> getReprintRescanStatesByEncounter(Integer encounterId, Date optionalDateRestriction, 
				Integer locationTagId,Integer locationId){
			return getChicaDAO().getReprintRescanStatesByEncounter(encounterId, optionalDateRestriction, locationTagId,locationId);
		}
		
		public List<String> getPrinterStations(Location location){
			String locationTagAttributeName = ChirdlUtilConstants.LOC_TAG_ATTR_ACTIVE_PRINTER_STATION;
			ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);

			Set<LocationTag> tags = location.getTags();
			List<String>  stationNames = new ArrayList<String>();
			for (LocationTag tag : tags){
				LocationTagAttributeValue locationTagAttributeValue = 
					chirdlutilbackportsService.getLocationTagAttributeValue(tag
						.getLocationTagId(), locationTagAttributeName,location.getLocationId());
				if (locationTagAttributeValue != null)
				{
					String activePrinterLocationString = locationTagAttributeValue
							.getValue();
					//only display active printer locations
					if (activePrinterLocationString.equalsIgnoreCase("true"))
					{
						stationNames.add(tag.getName());
					}
				}
				
			}
			Collections.sort(stationNames);
			
			return stationNames;
		}
		
		/**
		 * @see org.openmrs.module.chica.service.ChicaService#getPrinterStations(org.openmrs.User)
		 */
        public List<String> getPrinterStations(User user) {
        	List<String> stationNames = new ArrayList<String>();
        	String locationProp = user.getUserProperty(ChirdlUtilConstants.USER_PROPERTY_LOCATION);
	        String locationTags = user.getUserProperty(ChirdlUtilConstants.USER_PROPERTY_LOCATION_TAGS);
	        if (locationProp == null || locationProp.trim().length() == 0 || locationTags == null || 
	        		locationTags.trim().length() == 0) {
	        	return stationNames;
	        }
	        
	        LocationService locationService =  Context.getLocationService();
	        Location location = locationService.getLocation(locationProp);
	        if (location == null) {
	        	return stationNames;
	        }
	        
	        ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
	        String locationTagAttributeName = ChirdlUtilConstants.LOC_TAG_ATTR_ACTIVE_PRINTER_STATION;
			StringTokenizer tokenizer = new StringTokenizer(locationTags, ",");
			while (tokenizer.hasMoreTokens()) {
				String locationTagName = tokenizer.nextToken();
				locationTagName = locationTagName.trim();
				LocationTag tag = locationService.getLocationTagByName(locationTagName);
				if (tag == null) {
					continue;
				}
				
				LocationTagAttributeValue locationTagAttributeValue = 
						chirdlutilbackportsService.getLocationTagAttributeValue(tag.getLocationTagId(), 
						locationTagAttributeName, location.getLocationId());
				if (locationTagAttributeValue == null || locationTagAttributeValue.getValue() == null) {
					continue;
				}
				
				String activePrinterLocationString = locationTagAttributeValue.getValue();
				//only display active printer locations
				if (activePrinterLocationString.equalsIgnoreCase("true")) {
					stationNames.add(tag.getName());
				}
			}
	        
	        return stationNames;
        }
		
		public Chica1Appointment getChica1AppointmentByEncounterId(Integer encId){
			Chica1Appointment appt= getChicaDAO().getChica1AppointmentByEncounterId(encId);
			return appt;
			
		}
		
		public void  saveHL7ExportMap (ChicaHL7ExportMap map){
			
			getChicaDAO().saveHL7ExportMap(map);
			
		}
		
		public ChicaHL7ExportMap getChicaExportMapByQueueId(Integer queue_id){
			return getChicaDAO().getChicaExportMapByQueueId(queue_id);
		}
		
		public ChicaHL7ExportStatus getChicaExportStatusByName (String name){
			return getChicaDAO().getChicaExportStatusByName(name);
		}
		
		public ChicaHL7ExportStatus getChicaExportStatusById (Integer id){
			return getChicaDAO().getChicaExportStatusById( id);
		}
		
		public List<Object[]> getFormsPrintedByWeek(String formName, String locationName) {
			return getChicaDAO().getFormsPrintedByWeek(formName, locationName);
		}
		
		public List<Object[]> getFormsScannedByWeek(String formName, String locationName) {
			return getChicaDAO().getFormsScannedByWeek(formName, locationName);
		}
		
		public List<Object[]> getFormsScannedAnsweredByWeek(String formName, String locationName) {
			return getChicaDAO().getFormsScannedAnsweredByWeek(formName, locationName);
		}
		public List<Object[]> getFormsScannedAnythingMarkedByWeek(String formName, String locationName){
			return getChicaDAO().getFormsScannedAnythingMarkedByWeek(formName,locationName);	
		}
		public List<Object[]> getQuestionsScanned(String formName, String locationName) {
			return getChicaDAO().getQuestionsScanned(formName, locationName);
		}

		public List<Object[]> getQuestionsScannedAnswered(String formName, String locationName) {
			return getChicaDAO().getQuestionsScannedAnswered(formName, locationName);
		}
            
		public List< org.openmrs.module.chica.hibernateBeans.Encounter> getEncountersForEnrolledPatients(Concept concept,
				Date startDateTime, Date endDateTime){
			return getChicaDAO().getEncountersForEnrolledPatients(concept, startDateTime, endDateTime);
    	}
		
		public List<Encounter> getEncountersForEnrolledPatientsExcludingConcepts(Concept includeConcept, Concept excludeConcept,
				Date startDateTime, Date endDateTime){
			return getChicaDAO().getEncountersForEnrolledPatientsExcludingConcepts(includeConcept, excludeConcept, startDateTime, endDateTime);
		}

		/**
		 * @see org.openmrs.module.chica.service.ChicaService#getStudySubject(org.openmrs.Patient, org.openmrs.module.chica.hibernateBeans.Study)
		 */
        public StudySubject getStudySubject(Patient patient, Study study) {
	        return getChicaDAO().getStudySubject(patient, study);
        }

		/**
		 * @see org.openmrs.module.chica.service.ChicaService#getStudyByTitle(java.lang.String)
		 */
        public Study getStudyByTitle(String studyTitle) {
	        return getChicaDAO().getStudyByTitle(studyTitle);
        }
        
        /**
		 * @see org.openmrs.module.chica.service.ChicaService#getStudyByTitle(java.lang.String, boolean)
		 */
        public List<Study> getStudyByTitle(String studyTitle, boolean includeRetired) {
	        return getChicaDAO().getStudyByTitle(studyTitle, includeRetired);
        }

        /**
    	 * DWE CHICA-761
    	 * @see org.openmrs.module.chica.service.ChicaService#getReprintRescanStatesBySessionId(Integer, Date, List, Integer)
    	 */
    	public List<PatientState> getReprintRescanStatesBySessionId(Integer sessionId, Date optionalDateRestriction, List<Integer> locationTagIds,Integer locationId)
    	{
    		return getChicaDAO().getReprintRescanStatesBySessionId(sessionId, optionalDateRestriction, locationTagIds, locationId);
    	}
    	
    	/**
    	 * CHICA-1063
    	 * @see org.openmrs.module.chica.service.ChicaService#createPatientStateQueryGlooko(String, String, String)
    	 */
    	public void createPatientStateQueryGlooko(String glookoCode, String syncTimestamp, String dataType)
    	{
    		ThreadManager threadManager = ThreadManager.getInstance();
			threadManager.execute(new DeviceSyncRunnable(glookoCode, syncTimestamp, dataType), 0);
    	}

		/**
		 * CHICA-1063
		 * @see org.openmrs.module.chica.service.ChicaService#addGlookoCodePersonAttribute(String, String, String, String)
		 */
		public void addGlookoCodePersonAttribute(String firstName, String lastName, String dateOfBirth, String glookoCode) 
		{
			ThreadManager threadManager = ThreadManager.getInstance();
			threadManager.execute(new NewGlookoUserRunnable(firstName, lastName, dateOfBirth, glookoCode), 0);
		}
		
		/**
		  * @see org.openmrs.module.chica.service.ChicaService#saveStudyAttribute(org.openmrs.module.chica.hibernateBeans.StudyAttribute)
		  */
		public StudyAttribute saveStudyAttribute(StudyAttribute studyAttribute) throws APIException {
			return getChicaDAO().saveStudyAttribute(studyAttribute);
		}

		/**
		  * @see org.openmrs.module.chica.service.ChicaService#retireStudyAttribute(org.openmrs.module.chica.hibernateBeans.StudyAttribute,
		  * java.lang.String)
		  */
		public StudyAttribute retireStudyAttribute(StudyAttribute studyAttribute, String reason) throws APIException {
		        return getChicaDAO().saveStudyAttribute(studyAttribute);
		}

		/**
		  * @see org.openmrs.module.chica.service.ChicaService#unretireStudyAttribute(org.openmrs.module.chica.hibernateBeans.StudyAttribute)
		  */
		public StudyAttribute unretireStudyAttribute(StudyAttribute studyAttribute) throws APIException {
		        return getChicaDAO().saveStudyAttribute(studyAttribute);
		}
		
		/**
		  * @see org.openmrs.module.chica.service.ChicaService#saveStudyAttributeValue(org.openmrs.module.chica.hibernateBeans.StudyAttributeValue)
		  */
		public StudyAttributeValue saveStudyAttributeValue(StudyAttributeValue studyAttributeValue) throws APIException {
			return getChicaDAO().saveStudyAttributeValue(studyAttributeValue);
		}	

		/**
		  * @see org.openmrs.module.chica.service.ChicaService#retireStudyAttributeValue(org.openmrs.module.chica.hibernateBeans.StudyAttributeValue,
		  * java.lang.String)
		  */
		public StudyAttributeValue retireStudyAttributeValue(StudyAttributeValue studyAttributeValue, String reason) throws APIException {
		        return getChicaDAO().saveStudyAttributeValue(studyAttributeValue);
		}

		/**
		  * @see org.openmrs.module.chica.service.ChicaService#unretireStudyAttributeValue(org.openmrs.module.chica.hibernateBeans.StudyAttributeValue)
		  */
		public StudyAttributeValue unretireStudyAttributeValue(StudyAttributeValue studyAttributeValue) throws APIException {
		        return getChicaDAO().saveStudyAttributeValue(studyAttributeValue);
		}
		
		/**
		  * @see org.openmrs.module.chica.service.ChicaService#saveStudy(org.openmrs.module.chica.hibernateBeans.Study)
		  */
		public Study saveStudy(Study study) throws APIException {
			return getChicaDAO().saveStudy(study);
		}	

		/**
		  * @see org.openmrs.module.chica.service.ChicaService#retireStudy(org.openmrs.module.chica.hibernateBeans.Study,
		  * java.lang.String)
		  */
		public Study retireStudy(Study study, String reason) throws APIException {
		        return getChicaDAO().saveStudy(study);
		}

		/**
		  * @see org.openmrs.module.chica.service.ChicaService#unretireStudy(org.openmrs.module.chica.hibernateBeans.Study)
		  */
		public Study unretireStudy(Study study) throws APIException {
		        return getChicaDAO().saveStudy(study);
		}
		
	
}