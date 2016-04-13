package org.openmrs.module.chica.hl7.immunization;

/**
 * 
 */

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.ImmunizationForecast;
import org.openmrs.module.chica.ImmunizationForecastLookup;
import org.openmrs.module.chica.ImmunizationPrevious;
import org.openmrs.module.chica.ImmunizationQueryOutput;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chirdlutil.util.HttpUtil;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Error;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.model.v231.datatype.CE;
import ca.uhn.hl7v2.model.v231.datatype.CX;
import ca.uhn.hl7v2.model.v231.datatype.FN;
import ca.uhn.hl7v2.model.v231.datatype.NM;
import ca.uhn.hl7v2.model.v231.datatype.ST;
import ca.uhn.hl7v2.model.v231.datatype.TS;
import ca.uhn.hl7v2.model.v231.datatype.XAD;
import ca.uhn.hl7v2.model.v231.datatype.XPN;
import ca.uhn.hl7v2.model.v231.group.VXX_V02_PIDNK1;
import ca.uhn.hl7v2.model.v231.message.VXQ_V01;
import ca.uhn.hl7v2.model.v231.message.VXR_V03;
import ca.uhn.hl7v2.model.v231.message.VXU_V04;
import ca.uhn.hl7v2.model.v231.message.VXX_V02;
import ca.uhn.hl7v2.model.v231.segment.NK1;
import ca.uhn.hl7v2.model.v231.segment.OBX;
import ca.uhn.hl7v2.model.v231.segment.PID;
import ca.uhn.hl7v2.model.v231.segment.RXA;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.Terser;
import ca.uhn.hl7v2.validation.impl.NoValidation;

/**
 * @author tmdugan
 * 
 */
public class ImmunizationRegistryQuery
{
	private static Log log = LogFactory.getLog(ImmunizationRegistryQuery.class);

	private static String CHIRP_NOT_AVAILABLE = "CHIRP_not_available";
	private static String NOT_IN_CHIRP = "not_in_chirp_registry";
	private static String NOT_MATCHED = "CHIRP_match_not_found";
	private static String NEXT_OF_KIN = "Next of Kin";
	private static String IMMUNIZATION_REGISTRY = "Immunization Registry";
	private static String MATCHED = "CHIRP_match_found";
	private static String CREATED = "CHIRP_patient_created";
	private static  String SOURCE = "CVX";
	private static String GENERIC = "CVX unspecified";
	private static String ERROR_CATEGORY = "Query Immunization List Connection";
	private static String LOINC_DOSE_NUMBER_CODE = "30973-2";
	private static String LOINC_EARLIEST_TIME_TO_GIVE = "30981-5";
	private static String LOINC_DATE_VACCINE_DUE = "30980-7";
	private static String LOINC_VACCINE_TYPE = "30956-7";
	private static String CHIRP_ERROR = "CHIRP is not accessible.";
	private static String CHIRP_PARSING_ERROR = "CHIRP HL7 parsing error.";
	private static String CHIRP_UPDATE_FAILED = "CHIRP is accessible, but the immunization update failed.";
	private static String CHIRP_RESPONSE_INVALID = "CHIRP response was empty or invalid";
	
	/**
	 * 
	 * Auto generated method comment
	 * 
	 * @param messageString
	 * @param mrn
	 * @param patientId
	 */
	private static void createImmunizationList(String messageString, String mrn,
			Integer patientId) {

		if (messageString == null) {
			log.info("immunization list is null for mrn: " + mrn
					+ " so immunization list could not be created");
			return;
		}

		try {

			messageString = messageString.trim();

			if (messageString.length() == 0) {
				return;
			}
			String newMessageString = messageString;
			PipeParser pipeParser = new PipeParser();
			pipeParser.setValidationContext(new NoValidation());
			Message message = null;
			try {
				message = pipeParser.parse(newMessageString);
			} catch (Exception e) {
				
				logError( CHIRP_PARSING_ERROR, 
						"HL7 response = " + messageString ,
						null, patientId);
				return;
			}
			parseImmunizations(message, patientId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void parseImmunizations(Message message, Integer patientId)
			throws Exception {
		
		ConceptService conceptService = Context.getConceptService();
		ChicaService chicaService = Context.getService(org.openmrs.module.chica.service.ChicaService.class);	
		ImmunizationForecastLookup.removeImmunizationList(patientId);
	
		if (message instanceof VXR_V03) {
			VXR_V03 vxr = (VXR_V03) message;
			int numReps = vxr.getORCRXARXROBXNTEReps();
			for (int i = 0; i < numReps; i++) {
				HashMap<String, ImmunizationPrevious> prevImmunizationList = new HashMap<String, ImmunizationPrevious>();
				HashMap<String, ImmunizationForecast> forecastedImmunizationList = new HashMap<String, ImmunizationForecast>();
			
				RXA rxa = vxr.getORCRXARXROBXNTE(i).getRXA();
				
				String vaccineCode = null;
				String vaccineName = null;
				String vaccineDate = null;
				NM doseNM = null;
				
				
				ImmunizationPrevious previousImmunization = null;
				ImmunizationForecast forecastedImmunization = null;

				// previously given vaccines
				if (rxa != null) {
					vaccineCode = rxa.getAdministeredCode().getIdentifier()
							.getValue();
					vaccineName = rxa.getAdministeredCode().getText()
							.getValue();
					doseNM = rxa.getAdministrationSubIDCounter();
					
					
					//2013-05-08 - Format for forecasted messaegs no longer has 
					// 0 for the dose number.  Now that same field has 999 just like the 
					//vaccine history.  The forecasting messages now have 
					//vaccine code = 998 and name = "no vaccine administered".
					if (vaccineCode != null && !vaccineCode.equalsIgnoreCase("998")){
						// This is a previous immunization	
						Integer doseNumber = Integer.valueOf(doseNM.getValue());
						vaccineDate = rxa.getDateTimeEndOfAdministration()
						.getTimeOfAnEvent().getValue();
						
						System.out.println("history: " + vaccineCode + " " 
								+ vaccineName + " " 
								+ doseNM.getValue() + " " 
								+ vaccineDate);
					
					  	// Use Concept maps vaccine category
						// Combination vaccines will map to multiple codes
						Concept cvxVaccineConcept = conceptService.getConceptByMapping(vaccineCode, SOURCE );
						if (cvxVaccineConcept == null ){
							log.info("Immunization: CVX vaccine code " + vaccineCode + " (" + vaccineName + ") " + "not mapped to a concept" );
							continue;
						}
						
						//Get all general (unspecified) mapped vaccine concepts for each RXA vaccine code.
						List<ConceptMap> conceptMaps = chicaService.getConceptMapsByVaccine(cvxVaccineConcept, GENERIC);
						if (conceptMaps == null || conceptMaps.size() == 0){
							continue;
						}
						
						for (ConceptMap conceptMap: conceptMaps){
							String unspecifiedVaccineCode = conceptMap.getSourceCode();
							String unspecifiedVaccineName = "";
							Concept unspecifiedVaccineConcept = conceptService.getConceptByMapping(unspecifiedVaccineCode, SOURCE );
							if (unspecifiedVaccineConcept != null && unspecifiedVaccineConcept.getName() != null){
								unspecifiedVaccineName = unspecifiedVaccineConcept.getName().getName();	
							}
							if (vaccineCode != null) {
								previousImmunization = new ImmunizationPrevious();
	
								previousImmunization.setVaccineName(unspecifiedVaccineName);
								previousImmunization
										.setVaccineCode(unspecifiedVaccineCode);
								previousImmunization.setPatientId(patientId);
								previousImmunization.setDate(vaccineDate);
								previousImmunization.setDose(doseNumber);
								prevImmunizationList.put(unspecifiedVaccineCode,
										previousImmunization);
							}
						} 
						
						// add the previous immunizations
						for (ImmunizationPrevious prevImmunization : prevImmunizationList
								.values()) {

							Integer immundDoseNumber = prevImmunization.getDose();

							if (immundDoseNumber != null && immundDoseNumber > 0) {
								ImmunizationQueryOutput immuneOutput = ImmunizationForecastLookup
										.getImmunizationList(patientId);

								if (immuneOutput == null) {
									immuneOutput = new ImmunizationQueryOutput();
									ImmunizationForecastLookup.addImmunizationList(
											patientId, immuneOutput);
								}

								immuneOutput.addImmunizationPrevious(prevImmunization);
							}
						}
						
					}
					else {
					
						// get the rxa information for forecasting
							// 2013-05-08 - CHIRP changed - vaccine code and vaccine code are useless.
							// Get the vaccine name from a value in an OBX.
							// Now we need to through forecast OBX information for forecasting 
						forecastedImmunization = new ImmunizationForecast();
						
						int numObs = vxr.getORCRXARXROBXNTE(i).getOBXNTEReps();
						for (int j = 0; j < numObs; j++) {
							OBX obx = vxr.getORCRXARXROBXNTE(i).getOBXNTE(j).getOBX();
							if (obx == null){
								continue;
							}
							String obxCode = obx.getObservationIdentifier()
								.getIdentifier().getValue();
							if (obxCode == null) {
								continue;
							}
							Type obxValue = obx.getObservationValue(0).getData();
							if (obxValue == null){
								continue;
							}
							String obsValueType = obxValue.getName();
							
						
							// this code indicates the type of vaccine
							if (obxCode.equals(LOINC_VACCINE_TYPE)) {
								if (obsValueType.equals("CE")) {
									CE data = (CE) obxValue;
									if (data == null || data.getIdentifier() == null
											||data.getText() == null){
										//Must have a vaccine type
										continue;
									}
									
									//Note: newer hl7 has a junky vaccine name
									String obxVaccineName = data.getText()
										.getValue();
									if (obxVaccineName != null && forecastedImmunization != null) {
										forecastedImmunization
											.setVaccineName(obxVaccineName);
									}	
									
									String obxVaccineCode = data.getIdentifier()
										.getValue();
									if (obxVaccineCode != null && forecastedImmunization != null) {
										forecastedImmunization
											.setVaccineCode(obxVaccineCode);
									}	
								}
							}
							// this code indicates the due date of the next
							// vaccination
							if (obxCode.equals(LOINC_DATE_VACCINE_DUE)) {
								if (obsValueType.equals("TS")) {
									TS data = (TS) obxValue;
									String dateString = data.getTimeOfAnEvent()
										.getValue();
									if (dateString != null && forecastedImmunization != null) {
										forecastedImmunization
											.setDateDue(dateString);
									}	
								}
							}
	
						// this code indicates the dose of the next vaccination
						//if (obxCode.equals("30979-9&30973-2")) {
							if (obxCode.equals(LOINC_DOSE_NUMBER_CODE)) {
								if (obsValueType.equals("NM")) {
									
									NM data = (NM) obxValue;
									String dataString = data.getValue();
									try {
										if (dataString != null && forecastedImmunization != null) {
											forecastedImmunization.setDose(Integer
												.parseInt(dataString));
										}
									} catch (NumberFormatException  e) {
										log.info("Invalid number format for Dose number: " + dataString);
										forecastedImmunization.setDose(0);
									}
									
									
								}
							}
	
							// this code indicates the earliest date of the next
							// vaccination
							//if (obxCode.equals("30979-9&30981-5")) {
							if (obxCode.equals(LOINC_EARLIEST_TIME_TO_GIVE)) {
								if (obsValueType.equals("TS")) {
									TS data = (TS) obxValue;
									String dateString = data.getTimeOfAnEvent()
											.getValue();
									if (dateString != null && forecastedImmunization != null ) {
										forecastedImmunization
												.setEarliestDate(dateString);
									}
								}
							}
							
							
						}
						
						//Create the forecasted list 
						
						String forecastVaccineCode = forecastedImmunization.getVaccineCode();
						//get the concept for hl7 CVX vaccine code
						Concept vaccineConcept = conceptService.getConceptByMapping(forecastVaccineCode, SOURCE );
						if (vaccineConcept == null){
							log.error("ImmunizationForecast: CVX vaccine code not mapped to a concept : " + forecastVaccineCode + " ("  + forecastedImmunization.getVaccineName() );
							continue;
						}
						
						//Find the mapped codes for the concept
						List<ConceptMap> conceptMaps = chicaService.getConceptMapsByVaccine(vaccineConcept, GENERIC); 
						if (conceptMaps == null || conceptMaps.size() == 0){
							log.info("ImmunizationForecast: No unspecified CVX code exists for concept: " 
									+ vaccineConcept.getName().getName() + ",  CVX: " + forecastVaccineCode);
						}
						
						//Get the unspecified name and cvx code to add to forecasting list
						for (ConceptMap conceptMap : conceptMaps) {
							
							String unspecfiedCode = conceptMap.getSourceCode();
							if (unspecfiedCode == null || unspecfiedCode.trim().equalsIgnoreCase("") ){
								log.info("ImmunizationForecast: Concept map exists, but no source_code defined.  Concept id: " 
										+ conceptMap.getConcept().getConceptId());
								continue;
							}
							Concept unspecifiedConcept = conceptService.getConceptByMapping(unspecfiedCode, SOURCE );
							if (unspecifiedConcept == null || unspecifiedConcept.getName() == null ){
								log.info("ImmunizationForecast: There is no concept for CVX code: "
										+ unspecfiedCode);
								continue;
							}
							
							String unspecifiedVaccineName = unspecifiedConcept.getName().getName();

							forecastedImmunization.setVaccineName(unspecifiedVaccineName);
							forecastedImmunization
									.setVaccineCode(unspecfiedCode);
							forecastedImmunization.setPatientId(patientId);
							forecastedImmunizationList.put(unspecfiedCode,
									forecastedImmunization);
							
						}
												
						//loop through forecast immunization obx
						// added the forecasted immunizations
						for (ImmunizationForecast foreImmunization : forecastedImmunizationList
								.values()) {
	
							Integer doseNumber = foreImmunization.getDose();
	
							if (doseNumber != null && doseNumber > 0) {
								ImmunizationQueryOutput immuneOutput = ImmunizationForecastLookup
										.getImmunizationList(patientId);
	
								if (immuneOutput == null) {
									immuneOutput = new ImmunizationQueryOutput();
									ImmunizationForecastLookup.addImmunizationList(
											patientId, immuneOutput);
								}
	
								immuneOutput.addImmunizationForecast(foreImmunization);
							}
						}
					}// end of forecast parsing
				}// rxa exists
			}//end of loop through all rxa's
		}// is VXR
	}
	
	private static String getData(String message){
		String data = "";
		try {
			AdministrationService adminService = Context.getAdministrationService();
			String userId = adminService
			.getGlobalProperty("chica.ImmunizationQueryUserId");
			String password = adminService
			.getGlobalProperty("chica.ImmunizationQueryPassword");

			data = URLEncoder.encode("USERID", "UTF-8") + "="
			+ URLEncoder.encode(userId, "UTF-8");
			data += "&" + URLEncoder.encode("PASSWORD", "UTF-8") + "="
			+ URLEncoder.encode(password, "UTF-8");
			
			if (message != null && message.contains("VXU")){
				data += "&" + URLEncoder.encode("debug", "UTF-8") + "="
				+ URLEncoder.encode("debug", "UTF-8");
				data += "&" + URLEncoder.encode("deduplication", "UTF-8") + "="
				+ URLEncoder.encode("deduplication", "UTF-8");
			}
			data += "&" + URLEncoder.encode("MESSAGEDATA", "UTF-8") + "="
			+ URLEncoder.encode(message, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return data;		
		
	}
	
	public static String queryCHIRP( Encounter encounter){
		
		AdministrationService adminService = Context.getAdministrationService();
		ConceptService conceptService = Context.getConceptService();
		PatientService patientService = Context.getPatientService();
		String dir = adminService.getGlobalProperty("chica.ImmunizationOutputDirectory");
		String url = adminService.getGlobalProperty("chica.ImmunizationQueryURL");
		Integer timeout = Integer.parseInt(adminService.getGlobalProperty("chica.immunizationListTimeout"));
		String activateQuery = adminService.getGlobalProperty("chica.ImmunizationQueryActivated");
		boolean OkToQuery = false;
		if (activateQuery != null &&
				 (activateQuery.equalsIgnoreCase("true") 
						|| activateQuery.equalsIgnoreCase("yes") 
						|| activateQuery.equalsIgnoreCase("T") 
						|| activateQuery.equalsIgnoreCase("Y"))){
			OkToQuery = true;

		}
		
		Concept statusConcept = conceptService.getConceptByName("CHIRP_Status");
		timeout = timeout*1000;
		String queryResponse = null;
		long queryTime = 0;
		Integer encounterId = encounter.getEncounterId();;
		Patient chicaPatient = encounter.getPatient();
		PipeParser parser = new PipeParser();
		
		try {
			
			// Get identifier
			chicaPatient = patientService.getPatient(chicaPatient.getPatientId());
			Hibernate.initialize(chicaPatient);
			String mrn = chicaPatient.getPatientIdentifier().getIdentifier();
			
			//construct vxq
		
			ImmunizationQueryConstructor constructor = new ImmunizationQueryConstructor();
			VXQ_V01 vxq = new VXQ_V01();
			String vxqString = ImmunizationQueryConstructor.constructVXQ(vxq, encounter);
			if (vxqString == null || vxqString.trim().equals("")){
				log.error("Immunization: VXQ string for " + mrn + " is null or empty.");
				return null;
			}
			
			String data = getData(vxqString);
			log.info("Immunization: vxq = " +  vxqString);
			if (!OkToQuery){
				log.info("Immunization: Activate query: " + OkToQuery);
				return null;
			}
			
			//post VXQ
			long queryStartTime = System.currentTimeMillis();
			
			
			try {
				queryResponse = HttpUtil.post(url, data,timeout,timeout);
				queryTime = System.currentTimeMillis()-queryStartTime;
			} catch (Exception e1) {
				logError(CHIRP_ERROR 
						, Util.getStackTrace(e1),
						url, encounter.getPatientId());
				Util.saveObs(chicaPatient, statusConcept, encounterId, CHIRP_NOT_AVAILABLE
						, new Date());
				return null;
			}
			
			// log post timing
			log.info("Immunization:  Encounter = " + encounter.getEncounterId() + 
					" CHIRP response time = " + queryTime);
			log.info("Immunization:  CHIRP response text = " + queryResponse);
			
			//log errors
			if (queryResponse == null ){
				logError( CHIRP_ERROR, "",  url, encounter.getPatientId());
				Util.saveObs(chicaPatient, statusConcept, encounterId, CHIRP_NOT_AVAILABLE
						, new Date());
				return null;
			}
			
			if (queryResponse.isEmpty() || queryResponse.trim().equals("")) {
				logError(CHIRP_RESPONSE_INVALID, "", url, encounter.getPatientId());
				Util.saveObs(chicaPatient, statusConcept, encounterId, CHIRP_NOT_AVAILABLE
						, new Date());
				return null;
			}
			
			//CHIRP does not send QCK MSH segment in proper format!! 
			//FIRST do a string search for "QCK" to avoid parsing errors,
			//THEN, if not "QCK", continue parse the response.
			
			//patient not found
			
			if (queryResponse.indexOf("|QCK") > 0){
				
				
				//save the obs
				Util.saveObs(chicaPatient, statusConcept, encounterId, NOT_IN_CHIRP, new Date());
				
				//create the patient in chirp with VXU if VXU is activated.
					
				VXU_V04 vxu = new VXU_V04();
				ImmunizationQueryConstructor.constructVXU(vxu,encounter); 
				String vxuString = ImmunizationQueryConstructor.getVXUMessageString(vxu);
				data = getData(vxuString);
				
				//Only send VXU if VXU update is enabled in global property
				
				String activateVXUToCreatePatient = adminService.getGlobalProperty("chica.activateVXUToCreatePatient");
				if (activateVXUToCreatePatient != null && 
					(activateVXUToCreatePatient.equalsIgnoreCase("true") 
							|| activateVXUToCreatePatient.equalsIgnoreCase("yes") 
							|| activateVXUToCreatePatient.equalsIgnoreCase("T") 
							|| activateVXUToCreatePatient.equalsIgnoreCase("Y"))){
					try {
						queryResponse = HttpUtil.post(url, data,timeout,timeout);	
						log.info("Immunization: Create patient response = " +  queryResponse);
						if (queryResponse != null && queryResponse.contains("accepted")){
							Util.saveObs(chicaPatient, statusConcept, encounterId, CREATED, new Date());
						} else {
							//Chirp status remains at "not in chirp"
							logError(CHIRP_UPDATE_FAILED, 
									 queryResponse, url, encounter.getPatientId());
						}
						
					} catch (IOException e) {
						logError(CHIRP_ERROR, 
								Util.getStackTrace(e),
								url, encounter.getPatientId());
						Util.saveObs(chicaPatient, statusConcept, encounterId, CHIRP_NOT_AVAILABLE
								, new Date());	
						return null;
					}
						
				}				
				return null;
			}
						
			
			//Parse to get MSA for error information.
			//CHIRP  sends invalid characters!  Remove before parsing.
			String msaControlId = "";
			queryResponse = removeIllegalCharacter(queryResponse);
			Message queryResponseMessage;
			try {
				parser.setValidationContext(new NoValidation());
				queryResponseMessage = parser.parse(queryResponse);
				Terser terser = new Terser(queryResponseMessage);
				msaControlId = terser.get("/MSA-1-1");
				if (msaControlId == null || !msaControlId.equalsIgnoreCase("AA") )
				{
					logError(CHIRP_ERROR,
							"HL7 response: " + queryResponse, url, encounter.getPatientId());
					Util.saveObs(chicaPatient, statusConcept, encounterId, CHIRP_NOT_AVAILABLE
							, new Date());
					return null;
				}
			} catch (HL7Exception hl7e){
				logError( CHIRP_PARSING_ERROR, 
						"HL7 response = " + queryResponse ,
						null, encounter.getPatientId());
				Util.saveObs(chicaPatient, statusConcept, encounterId, CHIRP_NOT_AVAILABLE
						, new Date());
				return null;
			}
		
			//VXX possible match
			
			if (queryResponseMessage instanceof VXX_V02) {
				
				
				//match check
				
				Set<Patient> chirpPatients = getChirpPatients(queryResponseMessage);
				PatientMatching patientMatching = new PatientMatching();
				Patient matchPatient = patientMatching.verifyPatientMatch(chicaPatient, chirpPatients);
				if (matchPatient == null){
					log.info("Immunization: Patient not matched in CHIRP. Encounter id = " + encounterId);
					Util.saveObs(chicaPatient, statusConcept, encounterId, NOT_MATCHED, new Date());
					return null;
				}				

				/*CHICA-552 MSHELEY In the past CHIRP has reassigned identifiers and merged patients in their database.
				 *Before saving SIIS identifier, check if identifier exists for another patient, and void the existing identifier.
				 */
				PatientIdentifierType immunIdentifierType = patientService.getPatientIdentifierTypeByName(IMMUNIZATION_REGISTRY);
				PatientIdentifier chirpIdentifier = matchPatient.getPatientIdentifier(immunIdentifierType);
				PatientIdentifier chicaIdentifier = chicaPatient.getPatientIdentifier(immunIdentifierType);
				
				if (chicaIdentifier == null || !chirpIdentifier.getIdentifier().equals(chicaIdentifier.getIdentifier())){
					//Match found..  Save identifier for patient that matches.
					checkIdentifier(chicaPatient, chirpIdentifier);
					
					//Do not save as preferred
					try {

						chirpIdentifier.setPreferred(false);
						chicaPatient.addIdentifier(chirpIdentifier);
						patientService.savePatient(chicaPatient);

					} catch (Exception e) {
						log.error(Util.getStackTrace(e));
						//CHICA cannot requery after VXX if SIIS not available - save obs as matching error
						Util.saveObs(chicaPatient, statusConcept, encounterId, NOT_MATCHED, new Date());
						return null;
					}
				}
				
				//Construct the 2nd vxq
				
				vxqString = constructor.updateVXQ(vxqString, matchPatient);
				log.info("Immunization: 2nd Vxq: " +  vxqString);
					
				//requery
				
				data = getData(vxqString);
				queryStartTime = System.currentTimeMillis();
				queryResponse = HttpUtil.post(url, data, timeout, timeout);	
				
				log.info("Immunization: Requery CHIRP response time = " + encounter.getEncounterId()
						+ ": " + (System.currentTimeMillis()-queryStartTime) + "msec");
				
				if (queryResponse == null || queryResponse.trim().equals("")
						|| !queryResponse.contains("MSH")) {
					Util.saveObs(chicaPatient, statusConcept, encounterId, NOT_MATCHED, new Date());
					return null;
				}
				
				//remove possible illegal character from CHIRP
				try {
					queryResponse = removeIllegalCharacter(queryResponse);
					PipeParser pipeParser = new PipeParser();
					pipeParser.setValidationContext(new NoValidation());
					queryResponseMessage = pipeParser.parse(queryResponse);
				} catch (HL7Exception e) {
					logError( CHIRP_PARSING_ERROR, 
							"HL7 response = " + queryResponse ,
							null, encounter.getPatientId());
					Util.saveObs(chicaPatient, statusConcept, encounterId, CHIRP_NOT_AVAILABLE
							, new Date());
					return null;
				}
				
				log.info("Immunization: CHIRP response after requery = " + queryResponse);
				//VXX 2nd time after requery indicates no confirmed match
				if (queryResponseMessage instanceof VXX_V02) {
					Util.saveObs(chicaPatient, statusConcept, encounterId, NOT_MATCHED, new Date());
					return null;
						
				}
				
			}
			
			//VXR returned from query or requery. Create immunization list
			
			if (queryResponseMessage instanceof VXR_V03) {
				
				Set<Patient> chirpPatients = getChirpPatientFromVXR(queryResponseMessage);
				PatientMatching patientMatching = new PatientMatching();
				Patient matchPatient = patientMatching.verifyPatientMatch(chicaPatient, chirpPatients);
				if (matchPatient == null){
					log.info("Immunization: VXR Patient is not a match. Encounter id = " + encounterId);
					Util.saveObs(chicaPatient, statusConcept, encounterId, NOT_MATCHED, new Date());
					return null;
				}
				Util.saveObs(chicaPatient, statusConcept, encounterId, MATCHED,
						new Date());
				
				/* CHICA-552 MSHELEY  If SIIS is not known, we send VXQ with MRN. 
				 * Sometimes, CHIRP will return VXR with immediately (skipping VXX) that contains the SIIS identifier. 
				 * Before saving, check if identifier exists already for different patient.
				 */
				PatientIdentifierType immunIdentifierType = patientService.getPatientIdentifierTypeByName(IMMUNIZATION_REGISTRY);
				PatientIdentifier chirpIdentifier = matchPatient.getPatientIdentifier(immunIdentifierType);
				PatientIdentifier chicaIdentifier = chicaPatient.getPatientIdentifier(immunIdentifierType);
				
				if (chicaIdentifier == null || !chirpIdentifier.getIdentifier().equals(chicaIdentifier.getIdentifier())){
					
					checkIdentifier(chicaPatient, chirpIdentifier);
					
					//Do not save immunization registry ids as preferred
					try {

						chirpIdentifier.setPreferred(false);
						chicaPatient.addIdentifier(chirpIdentifier);
						patientService.savePatient(chicaPatient);

					} catch (Exception e) {
						// For VXR, no need to store obs with chica status. Ok to continue adding records to list.
						log.error(Util.getStackTrace(e));
					}
				}
				
				createImmunizationList(queryResponse, mrn, encounter
						.getPatientId());
				return queryResponse;
			}
			
		}  catch (IOException e) {
			
			logError( CHIRP_ERROR, Util.getStackTrace(e),
					url, encounter.getPatientId());
			Util.saveObs(chicaPatient, statusConcept, encounterId, CHIRP_NOT_AVAILABLE
					, new Date());
		}
				
		return queryResponse;
	}
	
	private static Set<Patient> getChirpPatients(Message message){
		PatientService patientService = Context.getPatientService();
		PersonService personService = Context.getPersonService();
		LocationService locationService = Context.getLocationService();
	Set<Patient> chirpPatients = new HashSet<Patient>();
		
		try {
			Integer count =  ((VXX_V02) message).getPIDNK1Reps();
			 for (int i = 0 ; i < count; i++){
				 
				 Patient patient = new Patient();
				 VXX_V02_PIDNK1  pidnk1 = ((VXX_V02) message).getPIDNK1(i);
				 
				 if (pidnk1 != null){
					 XPN[] xpns = pidnk1.getPID().getPatientName();
					 XPN xpn_alias = pidnk1.getPID().getPatientAlias(0);
					 for (XPN xpn : xpns){
						  xpn = pidnk1.getPID().getPatientName(0);
						 if (xpn != null){
							 PersonName personName = new PersonName();
							 String last = xpn.getFamilyLastName().getFamilyName().getValue();
							 String first = xpn.getGivenName().getValue();
							 String middle = xpn.getMiddleInitialOrName().getValue();
							 personName.setFamilyName(last);
							 personName.setGivenName(first);
							 personName.setMiddleName(middle);
							 patient.addName(personName);
						 }
						 
						 CX cx = pidnk1.getPID().getPatientIdentifierList(0);
						 PatientIdentifier pident = new PatientIdentifier();
						 if (cx != null){
							 String id = cx.getID().getValue();
							PatientIdentifierType type = patientService.getPatientIdentifierTypeByName(IMMUNIZATION_REGISTRY);
							pident.setIdentifierType(type);
							 pident.setIdentifier(id);;
							 pident.setLocation(locationService.getLocation("Unknown Location"));
							 patient.addIdentifier(pident);
						 }
						 patient.addIdentifier(pident);
						 
						XAD xad =  pidnk1.getPID().getPatientAddress(0);
						PersonAddress address = new PersonAddress();
						if (xad != null){
							address.setCityVillage(xad.getCity().getValue());
							address.setAddress1(xad.getStreetAddress().getValue());
							address.setPostalCode(xad.getZipOrPostalCode().getValue());
							patient.addAddress(address);
						}
						
						 
						 Integer nkReps =  pidnk1.getNK1Reps();
						 PersonAttributeType patype = personService.getPersonAttributeTypeByName(NEXT_OF_KIN);
						 for (int j = 0; j < nkReps; j++){
							 String lastNameString = "";
							 String firstNameString = "";
							 NK1 nk1 = pidnk1.getNK1(j);
							 XPN xpname = nk1.getNKName(0);
							 if (xpname != null){
								 FN nkfname = xpname.getFamilyLastName();
								 ST nkfirstName = xpname.getGivenName();
								 ST lastName = nkfname.getFamilyName();
								 if (lastName != null){
									 lastNameString = lastName.getValue();
								 }
								if (nkfirstName != null){
									 firstNameString = nkfirstName.getValue();
							 	}
							 }
							
							 if (lastNameString != null || firstNameString != null){
								 String concatNKString = firstNameString + "|" +  lastNameString;
								 if (!concatNKString.equalsIgnoreCase("")) {
									 PersonAttribute nkpersonAttr = 
										 new PersonAttribute(patype, concatNKString);
									 patient.addAttribute(nkpersonAttr);
								 }
							 }
							 
						 }
												 
					 }
					 //Check the alias name PID-9 (not in the list of names in PID-5)
					 if (xpn_alias != null){
						 PersonName personName = new PersonName();
						 String last = xpn_alias.getFamilyLastName().getFamilyName().getValue();
						 String first = xpn_alias.getGivenName().getValue();
						 String middle = xpn_alias.getMiddleInitialOrName().getValue();
						 personName.setFamilyName(last);
						 personName.setGivenName(first);
						 personName.setMiddleName(middle);
						 patient.addName(personName);
					 }
					 
					 TS dob = pidnk1.getPID().getDateTimeOfBirth();
					 
					 if (dob != null && dob.getTimeOfAnEvent() != null){
						 String dobString = dob.getTimeOfAnEvent().getValue();    
						 Date birthDate = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).parse(dobString);
						 patient.setBirthdate(birthDate);
					 }
				 }
				 chirpPatients.add(patient);
			 }
		} catch (Exception e) {
			log.error(Util.getStackTrace(e));
		}
			
		return chirpPatients;
	}
	
	private static String removeIllegalCharacter(String queryResponse){
		
		if (queryResponse != null) {
			if (queryResponse.indexOf("Dose number in series^LN||B") > 0){
				log.info("Immunization: Replaced invalid character in VXR");
				queryResponse = queryResponse
				.replace("Dose number in series^LN||B", "Dose number in series^LN||1");
			}
			 
		}
		return queryResponse;
		
	}
	
	private static void logError(String text, String details, String url, Integer patientId){
		if (details == null){
			details = "No details";
		}
		if (url == null){
			url = "No url";
		}
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		Error error = new Error("ERROR", ERROR_CATEGORY, 
				text , " URL: " + url + " Patient ID: " 
				+ patientId + ". " + details, new Date(), null);
		chirdlutilbackportsService.saveError(error);
		log.error("Immunization: " + text + "Details:" + details );
		return;
	}

	private static Set<Patient> getChirpPatientFromVXR(Message message){
		//Past errors with CHIRP require that we check the matched patient 
		// for VXRs also.
		
		//VXR only has one PID
		PatientService patientService = Context.getPatientService();
		LocationService locationService = Context.getLocationService();
		Set<Patient> chirpPatients = new HashSet<Patient>();
		
		try {
			PID pid =  ((VXR_V03) message).getPID();
			if (pid == null){
				return null;
			}
			Patient patient = new Patient();	 
			XPN[] xpns = pid.getPatientName();
			for (XPN xpn : xpns){
				xpn = pid.getPatientName(0);
				if (xpn != null){
					PersonName personName = new PersonName();
					String last = xpn.getFamilyLastName().getFamilyName().getValue();
					String first = xpn.getGivenName().getValue();
					String middle = xpn.getMiddleInitialOrName().getValue();
					personName.setFamilyName(last);
					personName.setGivenName(first);
					personName.setMiddleName(middle);
					patient.addName(personName);
				}

				CX cx = pid.getPatientIdentifierList(0);
				PatientIdentifier pident = new PatientIdentifier();
				if (cx != null){
					String id = cx.getID().getValue();
					PatientIdentifierType type = patientService.getPatientIdentifierTypeByName(IMMUNIZATION_REGISTRY);
					pident.setIdentifierType(type);
					pident.setIdentifier(id);;
					pident.setLocation(locationService.getLocation("Unknown Location"));
					patient.addIdentifier(pident);
				}
				patient.addIdentifier(pident);

				XAD xad =  pid.getPatientAddress(0);
				PersonAddress address = new PersonAddress();
				if (xad != null){
					address.setCityVillage(xad.getCity().getValue());
					address.setAddress1(xad.getStreetAddress().getValue());
					address.setPostalCode(xad.getZipOrPostalCode().getValue());
					patient.addAddress(address);
				}

			}
			
			TS dob = pid.getDateTimeOfBirth();

			if (dob != null && dob.getTimeOfAnEvent() != null){
				String dobString = dob.getTimeOfAnEvent().getValue();    
				Date birthDate = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).parse(dobString);
				patient.setBirthdate(birthDate);
			}

			chirpPatients.add(patient);

		} catch (Exception e) {
			log.error(Util.getStackTrace(e));
		}

		return chirpPatients;
	}
	
	/*CHICA-552 -MSHELEY Check if SIIS identifier exists for a different CHICA patient.
	 *If a different patient exists with this identifier, we need to void that existing identifier, 
	 *because CHIRP has indicated it is not correct.
	 */
	private static void checkIdentifier(Patient chicaPatient, PatientIdentifier identifier){
		
		PatientService patientService = Context.getPatientService();
		
			try {
				List<PatientIdentifierType> identifiersTypes = Collections.singletonList(identifier.getIdentifierType());
				List<PatientIdentifier> allMatchedIdentifiers = patientService.getPatientIdentifiers(identifier.getIdentifier(), identifiersTypes, null, null, false);
				for (PatientIdentifier matchingIdentifier : allMatchedIdentifiers){
					
					if (chicaPatient.getPatientId() != matchingIdentifier.getPatient().getPatientId()){
						/* Another patient already has this SIIS identifier. This id due to CHIRP changing identifiers."
						 * Void the existing identifier, since it is no longer correct according to CHIRP.
						 */
						log.info("CHIRP SIIS identifier (" + identifier.getIdentifier() + ") for Patient " + chicaPatient.getPatientId()
								+ " already exists for Patient " +  matchingIdentifier.getPatient().getPatientId() );
						matchingIdentifier.setVoided(true);
						matchingIdentifier.setVoidReason("Conflicting immunization registry identifier");
						matchingIdentifier.setVoidedBy(Context.getAuthenticatedUser());
						matchingIdentifier.setDateVoided(new Date());
						patientService.savePatientIdentifier(matchingIdentifier);
					}
				}
			} catch (Exception e) {
				log.error("Exception verifying and saving CHIRP identifier.", e);
			}
	}
	
}
