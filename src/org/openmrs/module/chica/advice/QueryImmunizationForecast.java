/**
 * 
 */
package org.openmrs.module.chica.advice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.ImmunizationForecastLookup;
import org.openmrs.module.chica.QueryImmunizationsException;
import org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Error;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.module.chica.ImmunizationForecast;
import org.openmrs.module.chica.ImmunizationPrevious;
import org.openmrs.module.chica.ImmunizationQueryOutput;

import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.model.v231.datatype.CE;
import ca.uhn.hl7v2.model.v231.datatype.NM;
import ca.uhn.hl7v2.model.v231.datatype.ST;
import ca.uhn.hl7v2.model.v231.datatype.TS;
import ca.uhn.hl7v2.model.v231.message.VXR_V03;
import ca.uhn.hl7v2.model.v231.segment.OBX;
import ca.uhn.hl7v2.model.v231.segment.RXA;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.validation.impl.NoValidation;

/**
 * @author tmdugan This thread class runs the immunization forecasting service query
 */
public class QueryImmunizationForecast implements ChirdlRunnable {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private QueryImmunizationsException exception = null;
	
	private org.openmrs.Encounter encounter = null;
	
	public QueryImmunizationForecast(org.openmrs.Encounter encounter) {
		this.encounter = encounter;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		Context.openSession();
		String mrn = null;
		
		try {
			AdministrationService adminService = Context.getAdministrationService();
			PatientService patientService = Context.getPatientService();
			Context.authenticate(adminService.getGlobalProperty("scheduler.username"), adminService
			        .getGlobalProperty("scheduler.password"));
			
			Patient patient = this.encounter.getPatient();
			patient = patientService.getPatient(patient.getPatientId());//lookup to prevent lazy initialization errors
			
			mrn = patient.getPatientIdentifier().getIdentifier();
			
			String queryResponse = null;
			
			//query the immunization forecasting service
			//Meena will write this to send a VXQ to CHIRP
			//and give back a VXR hl7 message
			//queryResponse = immunizationService.getImmunization(encounter);
			
			String filename = "C:\\Users\\tmdugan\\Desktop\\test_VXR.hl7";
			FileInputStream fileInputStream = new FileInputStream(filename);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			IOUtil.bufferedReadWrite(fileInputStream, outputStream);
			queryResponse = outputStream.toString();
			
			if (queryResponse != null) {
				
				//write the response to a file
				String directory = IOUtil.getDirectoryName(adminService
				        .getGlobalProperty("chica.immunizationOutputDirectory"));
				filename = "immunization_output_" + Util.archiveStamp() + "_" + mrn + ".hl7";
				FileOutputStream immunFileOutput = new FileOutputStream(directory + "/" + filename);
				ByteArrayInputStream responseInput = new ByteArrayInputStream(queryResponse.getBytes());
				IOUtil.bufferedReadWrite(responseInput, immunFileOutput);
				
				//parse the immunization forecasting service response into a list of immunizations
				Integer patientId = patient.getPatientId();
				createImmunizationList(queryResponse, mrn,patientId);
			}
			
		}
		catch (Exception e) {
			Error error = new Error("Error", "Query Immunization List Connection", "mrn: " + mrn + " " + e.getMessage(),
			        Util.getStackTrace(e), new Date(), null);
			ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
			chirdlutilbackportsService.saveError(error);
			this.exception = new QueryImmunizationsException("Query Immunization List Connection timed out", error);
		}
		finally {
			Context.closeSession();
		}
	}
	
	/**
	 * 
	 * Auto generated method comment
	 * 
	 * @param messageString
	 * @param mrn
	 * @param patientId
	 */
	private void createImmunizationList(String messageString, String mrn, Integer patientId) {
		
		if (messageString == null) {
			log.info("immunization list is null for mrn: " + mrn + " so immunization list could not be created");
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
			}
			catch (Exception e) {
				Error error = new Error("Error", "Hl7 Parsing", "Error parsing the Immunization forecast " + e.getMessage(),
				        messageString, new Date(), null);
				ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
				chirdlutilbackportsService.saveError(error);
				
				return;
			}
			parseImmunizations(message, patientId);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void parseImmunizations(Message message, Integer patientId) throws Exception {
		
		if (message instanceof VXR_V03) {
			VXR_V03 vxr = (VXR_V03) message;
			int numReps = vxr.getORCRXARXROBXNTEReps();
			for (int i = 0; i < numReps; i++) {
				
				RXA rxa = vxr.getORCRXARXROBXNTE(i).getRXA();
				HashMap<String, ImmunizationPrevious> prevImmunizationList = new HashMap<String, ImmunizationPrevious>();
				HashMap<String, ImmunizationForecast> forecastedImmunizationList = new HashMap<String, ImmunizationForecast>();

				String vaccineCode = null;
				String vaccineName = null;
				String vaccineDate = null;
				ImmunizationPrevious previousImmunization = null;
				ImmunizationForecast forecastedImmunization = null;
				
				//previously given vaccines
				if (rxa != null) {
					vaccineCode = rxa.getAdministeredCode().getIdentifier().getValue();
					vaccineName = rxa.getAdministeredCode().getText().getValue();
					vaccineDate = rxa.getDateTimeEndOfAdministration().getTimeOfAnEvent().getValue();
					
					//map each vaccineCode to the generic list
					//combination vaccines will map to multiple codes
					ArrayList<String> vaccineCodeList = getMappedVaccineCodes(vaccineCode);
					for (String currVaccineCode : vaccineCodeList) {
						
						vaccineCode = currVaccineCode;
						vaccineName = lookupVaccineName(vaccineCode);
						
						if (vaccineCode != null) {
							previousImmunization = new ImmunizationPrevious();
							
							previousImmunization.setVaccineName(vaccineName);
							previousImmunization.setVaccineCode(currVaccineCode);
							previousImmunization.setPatientId(patientId);
							previousImmunization.setDate(vaccineDate);
							prevImmunizationList.put(currVaccineCode, previousImmunization);
						}
					}
				}
				
				//use any obx that go with the RXA to get the dose numbers
				//if these are combination vaccines
				int numObs = vxr.getORCRXARXROBXNTE(i).getOBXNTEReps();
				
				for (int j = 0; j < numObs; j++) {
					OBX obx = vxr.getORCRXARXROBXNTE(i).getOBXNTE(j).getOBX();
					
					if (obx != null) {
						String obxCode = obx.getObservationIdentifier().getIdentifier().getValue();
						if(obxCode == null){
							continue;
						}
						Type obxValue = obx.getObservationValue(0).getData();
						String obsValueType = obxValue.getName();
						
						//this code indicates which component vaccine this obx is referring
						//to in a combination vaccine
						if (obxCode.equals("38890-0")) {
							if (obsValueType.equals("CE")) {
								CE obxValueCE = (CE) obxValue;
								vaccineName = obxValueCE.getText().toString();
								vaccineCode = obxValueCE.getIdentifier().toString();
								
								//this vaccine needs to be mapped to the generic code
								//list so it can be matched with the vaccine information
								//from the RXA segment
								ArrayList<String> vaccineCodeList = getMappedVaccineCodes(vaccineCode);
								
								//since this is NOT a combination vaccine, it should only
								//map to one vaccine code
								if (vaccineCodeList != null && vaccineCodeList.size() > 0) {
									String prevVaccineCode = vaccineCodeList.get(0);
									previousImmunization = prevImmunizationList.get(prevVaccineCode);
								}
							}
						}
						
						//this code indicates the dose number of the component
						//vaccine in this series
						if (obxCode.equals("38890-0&30973-2")) {
							if (obsValueType.equals("ST")) {
								ST data = (ST) obxValue;
								String doseString = data.getValue();
								if (doseString != null) {
									previousImmunization.setDose(Integer.parseInt(doseString));
								}
							}
						}
						
						//this code is a special codes for dose counts in combination vaccines
						if (obxCode.equals("30936-9") || obxCode.equals("30937-7") ||

						obxCode.equals("30938-5") || obxCode.equals("30939-3") || obxCode.equals("30940-1")
						        || obxCode.equals("30941-9") || obxCode.equals("30942-7") 
						        || obxCode.equals("30943-5")) {
							ArrayList<String> vaccineCodeList = getMappedVaccineCodes(obxCode);
							if (vaccineCodeList != null && vaccineCodeList.size() > 0) {
								String prevVaccineCode = vaccineCodeList.get(0);
								previousImmunization = prevImmunizationList.get(prevVaccineCode);
							}
							
							if (obsValueType.equals("NM")) {
								NM data = (NM) obxValue;
								
								previousImmunization.setDose(Integer.parseInt(data.getValue()));
							}
						}
						
						//this code indicates that this obx is referring to a forecasted vaccine
						if (obxCode.equals("30979-9")) {
							if (obsValueType.equals("CE")) {
								CE obxValueCE = (CE) obxValue;
								vaccineName = obxValueCE.getText().toString();
								vaccineCode = obxValueCE.getIdentifier().toString();
								
								//this vaccine needs to be mapped to the generic code
								ArrayList<String> vaccineCodeList = getMappedVaccineCodes(vaccineCode);
								
								//since this is NOT a combination vaccine, it should only
								//map to one vaccine code
								if (vaccineCodeList != null && vaccineCodeList.size() > 0) {
									vaccineCode = vaccineCodeList.get(0);
									vaccineName = lookupVaccineName(vaccineCode);
									forecastedImmunization = new ImmunizationForecast();
									
									forecastedImmunization.setVaccineName(vaccineName);
									forecastedImmunization.setVaccineCode(vaccineCode);
									forecastedImmunization.setPatientId(patientId);
									forecastedImmunizationList.put(vaccineCode, forecastedImmunization);
								}
							}
						}
						
						//this code indicates the due date of the next vaccination
						if (obxCode.equals("30979-9&30980-7")) {
							if (obsValueType.equals("TS")) {
								TS data = (TS) obxValue;
								String dateString = data.getTimeOfAnEvent().getValue();
								if (dateString != null) {
									forecastedImmunization.setDateDue(dateString);
								}
							}
						}
						
						//this code indicates the dose of the next vaccination
						if (obxCode.equals("30979-9&30973-2")) {
							if (obsValueType.equals("NM")) {
								NM data = (NM) obxValue;
								String dataString = data.getValue();
								if (dataString != null) {
									forecastedImmunization.setDose(Integer.parseInt(dataString));
								}
							}
						}
						
						//this code indicates the earliest date of the next vaccination
						if (obxCode.equals("30979-9&30981-5")) {
							if (obsValueType.equals("TS")) {
								TS data = (TS) obxValue;
								String dateString = data.getTimeOfAnEvent().getValue();
								if (dateString != null) {
									forecastedImmunization.setEarliestDate(dateString);
								}
							}
						}
					}
				}
				
				//see if any previous immunizations are missing doses
				//if so, pull the dose number from the RXA segment
				if (prevImmunizationList.size() == 1) {
					previousImmunization = (ImmunizationPrevious) prevImmunizationList.values().toArray()[0];
					Integer doseNumber = previousImmunization.getDose();
					
					if (doseNumber == null && rxa != null) {
						NM doseNM = rxa.getAdministrationSubIDCounter();
						doseNumber = Integer.parseInt(doseNM.getValue());
						previousImmunization.setDose(doseNumber);
					}
				}
				
				//add the previous immunizations
				for (ImmunizationPrevious prevImmunization : prevImmunizationList.values()) {
					
					Integer doseNumber = prevImmunization.getDose();
					
					if (doseNumber != null && doseNumber > 0) {
						ImmunizationQueryOutput immuneOutput = ImmunizationForecastLookup.getImmunizationList(patientId);
						
						if (immuneOutput == null) {
							immuneOutput = new ImmunizationQueryOutput();
							ImmunizationForecastLookup.addImmunizationList(patientId, immuneOutput);
						}
						
						immuneOutput.addImmunizationPrevious(prevImmunization);
					}
				}
				
				//added the forecasted immunizations
				for (ImmunizationForecast foreImmunization : forecastedImmunizationList.values()) {
					
					Integer doseNumber = foreImmunization.getDose();
					
					if (doseNumber != null && doseNumber > 0) {
						ImmunizationQueryOutput immuneOutput = ImmunizationForecastLookup.getImmunizationList(patientId);
						
						if (immuneOutput == null) {
							immuneOutput = new ImmunizationQueryOutput();
							ImmunizationForecastLookup.addImmunizationList(patientId, immuneOutput);
						}
						
						immuneOutput.addImmunizationForecast(foreImmunization);
					}
				}
			}
		}
		ImmunizationQueryOutput output = ImmunizationForecastLookup.getImmunizationList(patientId);
		
		Collection<ImmunizationForecast> forecastList = output.getImmunizationForecast().values();
		
		System.out.println("-------------------begin forecasted immunization--------------");
		
		for(ImmunizationForecast forecast:forecastList){
			
			System.out.println("vaccineCode: "+forecast.getVaccineCode());
			System.out.println("vaccineName: "+forecast.getVaccineName());
			System.out.println("dose: "+forecast.getDose());
			System.out.println("dateDue: "+forecast.getDateDue());
			System.out.println("earliest date: "+forecast.getEarliestDate());
		}
		
		System.out.println("-------------------end forecasted immunization--------------");
		
		Collection<HashMap<Integer,ImmunizationPrevious>> prevImmunizationList = 
			output.getImmunizationPrevious().values();
		
		System.out.println("-------------------begin previous immunization--------------");
		
		for(HashMap<Integer,ImmunizationPrevious> currMap:prevImmunizationList){
			Collection<ImmunizationPrevious> prevList = currMap.values();
			for(ImmunizationPrevious prevImmune:prevList){
				System.out.println("vaccineCode: "+prevImmune.getVaccineCode());
				System.out.println("vaccineName: "+prevImmune.getVaccineName());
				System.out.println("dose: "+prevImmune.getDose());
				System.out.println("date: "+prevImmune.getDate());
			}
		}
		
		System.out.println("-------------------end previous immunization--------------");
	}

	private ArrayList<String> getMappedVaccineCodes(String codeName){
		ArrayList<HashMap<String,String>> maps = setupVaccineCodeMap();
		ArrayList<String> mappedCodes = new ArrayList<String>();
		
		for(HashMap<String,String> currMap:maps){
			String mappedCode = currMap.get(codeName);
			if(mappedCode != null){
				mappedCodes.add(mappedCode);
			}
		}
		return mappedCodes;
	}
	
	private String lookupVaccineName(String codeName){
		HashMap<String,String> map = setupVaccineNameLookup();
		return map.get(codeName);
	}
	
	private ArrayList<HashMap<String,String>> setupVaccineCodeMap() {
		
		ArrayList<HashMap<String,String>> maps = new ArrayList<HashMap<String,String>>();
		
		HashMap<String, String> dtapMap = new HashMap<String, String>();
		dtapMap.put("12", "107");
		dtapMap.put("28", "107");
		dtapMap.put("20", "107");
		dtapMap.put("106", "107");
		dtapMap.put("107", "107");
		dtapMap.put("110", "107");
		dtapMap.put("50", "107");
		dtapMap.put("120", "107");
		dtapMap.put("130", "107");
		dtapMap.put("132", "107");
		dtapMap.put("01", "107");
		dtapMap.put("22", "107");
		dtapMap.put("102", "107");
		dtapMap.put("11", "107");
		dtapMap.put("115", "107");
		
		maps.add(dtapMap);
		
		HashMap<String, String> hepAMap = new HashMap<String, String>();
		hepAMap.put("83", "85");
		hepAMap.put("84", "85");
		hepAMap.put("31", "85");
		hepAMap.put("85", "85");
		hepAMap.put("104", "85");
		
		maps.add(hepAMap);
		
		HashMap<String, String> hepBMap = new HashMap<String, String>();
		hepBMap.put("30", "45");
		hepBMap.put("08", "45");
		hepBMap.put("42", "45");
		hepBMap.put("44", "45");
		hepBMap.put("45", "45");
		hepBMap.put("110", "45");
		hepBMap.put("104", "45");
		hepBMap.put("51", "45");
		hepBMap.put("102", "45");
		hepBMap.put("132", "45");
		
		maps.add(hepBMap);
		
		HashMap<String, String> hibMap = new HashMap<String, String>();
		hibMap.put("47", "17");
		hibMap.put("46", "17");
		hibMap.put("49", "17");
		hibMap.put("48", "17");
		hibMap.put("17", "17");
		hibMap.put("51", "17");
		hibMap.put("50", "17");
		hibMap.put("120", "17");
		hibMap.put("22","17");
		hibMap.put("102", "17");
		hibMap.put("132", "17");
		
		maps.add(hibMap);
		
		HashMap<String, String> influenzaMap = new HashMap<String, String>();
		influenzaMap.put("135", "88");
		influenzaMap.put("111", "88");
		influenzaMap.put("141", "88");
		influenzaMap.put("140", "88");
		influenzaMap.put("144", "88");
		influenzaMap.put("15", "88");
		influenzaMap.put("88", "88");
		influenzaMap.put("16", "88");
		
		maps.add(influenzaMap);
		
		HashMap<String, String> mmrMap = new HashMap<String, String>();
		mmrMap.put("04", "03");
		mmrMap.put("05", "03");
		mmrMap.put("03", "03");
		mmrMap.put("94", "03");
		mmrMap.put("07", "03");
		mmrMap.put("06", "03");
		mmrMap.put("38", "03");
		
		maps.add(mmrMap);
		
		HashMap<String, String> pcvMap = new HashMap<String, String>();
		pcvMap.put("133", "109");
		pcvMap.put("100", "109");
		pcvMap.put("109", "109");
		
		maps.add(pcvMap);
		
		HashMap<String, String> pneumoMap = new HashMap<String, String>();
		pneumoMap.put("33", "33");
		
		maps.add(pneumoMap);
		
		HashMap<String, String> polioMap = new HashMap<String, String>();
		polioMap.put("10", "89");
		polioMap.put("02", "89");
		polioMap.put("89", "89");
		polioMap.put("130", "89");
		polioMap.put("132", "89");
		polioMap.put("110", "89");
		polioMap.put("120", "89");
		
		maps.add(polioMap);
		
		HashMap<String, String> rotavirusMap = new HashMap<String, String>();
		rotavirusMap.put("119", "122");
		rotavirusMap.put("116", "122");
		rotavirusMap.put("74", "122");
		rotavirusMap.put("122", "122");
		
		maps.add(rotavirusMap);
		
		HashMap<String, String> varicellaMap = new HashMap<String, String>();
		varicellaMap.put("21", "21");
		varicellaMap.put("94", "21");
		
		maps.add(varicellaMap);
		
		HashMap<String, String> loincMap = new HashMap<String,String>();
		loincMap.put("30936-9", "107");
		loincMap.put("30937-7", "45");
		loincMap.put("30938-5", "17");
		loincMap.put("30939-3", "03");
		loincMap.put("30940-1", "03");
		loincMap.put("30941-9", "03");
		loincMap.put("30942-7", "03");
		loincMap.put("30943-5", "21");
		
		maps.add(loincMap);
		
		return maps;
		
	}
	
	private HashMap<String,String> setupVaccineNameLookup() {

		HashMap<String, String> map = new HashMap<String, String>();
		
		map.put("107", "DTaP, unspecified formulation");
		map.put("85", "Hep A, unspecified formulation");
		map.put("45", "Hep B, unspecified formulation");
		map.put("17", "Hib, unspecified formulation");
		map.put("88", "influenza, unspecified formulation");
		map.put("03", "MMR");
		map.put("109", "pneumococcal, unspecified formulation");
		map.put("89", "polio, unspecified formulation");
		map.put("122", "rotavirus, unspecified formulation");
		map.put("21", "varicella");
		map.put("33", "pneumococcal polysaccharide PPV23");
		
		return map;
	}
	
	public QueryImmunizationsException getException() {
		return this.exception;
	}

	/**
     * @see org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable#getName()
     */
    public String getName() {
	    return "Query Immunization Forecast (Encounter: " + encounter.getEncounterId() + ")";

    }

	/**
     * @see org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable#getPriority()
     */
    public int getPriority() {
    	return ChirdlRunnable.PRIORITY_FIVE;
    }
	
}
