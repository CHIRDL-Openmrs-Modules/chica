/********************************************************************
 Translated from - mcad.mlm on Fri Dec 28 15:10:34 EST 2007

 Title : MCAD Reminder
 Filename:  mcad
 Version : 0 . 2
 Institution : Indiana University School of Medicine
 Author : Steve Downs
 Specialist : Pediatrics
 Date : 05 - 22 - 2007
 Validation :
 Purpose : Provides a specific reminder, tailored to the patient who identified one or more fatty acid disorders
 Explanation : Based on AAP screening recommendations
 Keywords : fatty, acid, fatty acid disorder
 Citations : Screening for fatty acid disorder AAP
 Links :

 ********************************************************************/
package org.openmrs.module.chica.rule;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

import org.openmrs.Obs;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chica.hl7.immunization.ImmunizationQueryConstructor;
import org.openmrs.module.chica.hl7.immunization.Vaccine;





public class addVaccineRecords implements Rule
{

	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#getParameterList()
	 */
	public Set<RuleParameterInfo> getParameterList()
	{
		return null;
	}

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#getDependencies()
	 */
	public String[] getDependencies()
	{
		return new String[]
		{};
	}

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#getTTL()
	 */
	public int getTTL()
	{
		return 0; // 60 * 30; // 30 minutes
	}

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#getDefaultDatatype()
	 */
	public Datatype getDefaultDatatype()
	{
		return Datatype.CODED;
	}

	public Result eval(LogicContext context, Integer patientId,
	       			Map<String, Object> parameters) throws LogicException
	{
		
		String dtapName = "DTaP, unspecified formulation";
		String dtapCode = "107";
		String hibName = "Hib, unspecified formulation";
		String hibCode = "17";
		String ipvName = "polio, unspecified formulation";
		String ipvCode = "89";
		String pcvName = "pneumococcal, unspecified formulation";
		String pcvCode = "109";
		String hepbCode = "45";
		String hepbName = "Hep B, unspecified formulation";
		String rotaCode = "122";
		String rotaName = "rotavirus, unspecified formulation";
		String mmrCode = "03";
		String mmrName = "MMR";
		String varCode = "21";
		String varName = "varicella";
		String hepaCode = "85";
		String hepaName = "Hep A, unspecified formulation";
		String infCode = "88";
		String infName = "influenza, unspecified formulation";
		String infNasalCode = "111";
		String infNasalName = "influenza, live, intranasal";
		
		
		PatientService patientService = Context.getPatientService();
		EncounterService encounterService = Context.getService(EncounterService.class);
		int i = 1;
		Result vaccines = Result.emptyResult();
				
		Object paramObj = "";
		
		
		patientService.getPatient(patientId);
		//construct the main VXU 
		Integer encounterId = (Integer) parameters.get("encounterId");
		org.openmrs.Encounter encounter =  encounterService.getEncounter(encounterId);
		
		
		
		//Get the VXU string and convert to VXU message
		Object object = (Object) parameters.get("param1");
		if (object == null || !(object instanceof Result)){
			return Result.emptyResult();
		}
		Result result = (Result) object;
		
			
		String vxuString = (String) result.toString();
		ImmunizationQueryConstructor constructor = new ImmunizationQueryConstructor(vxuString);
		if (constructor == null){
			return Result.emptyResult();
		}
		
		
		while(paramObj != null){
			paramObj = parameters.get("param"+i);
			if(paramObj instanceof Result){
				vaccines = (Result) parameters.get("param"+i);
			}else{
				i++;
				continue;
			}
			
			if(vaccines != null){
				
				for(Result vaccineResult :vaccines){
				//if vaccine result is an obs
					Obs vaccineObs = (Obs) vaccineResult.getResultObject();
					Vaccine vaccine = new Vaccine();
					String name = vaccineObs.getConcept().getName().getName();
					if (name != null && name.toLowerCase().contains("dtap")){
						vaccine.setVaccineCode(dtapCode);
						vaccine.setVaccineName(dtapName);
					}
					if (name != null && name.toLowerCase().contains("hib")){
						vaccine.setVaccineCode(hibCode);
						vaccine.setVaccineName(hibName);
					}
					if (name != null && name.toLowerCase().contains("hepa")){
						vaccine.setVaccineCode(hepaCode);
						vaccine.setVaccineName(hepaName);
					}
					if (name != null && name.toLowerCase().contains("hepb")){
						vaccine.setVaccineCode(hepbCode);
						vaccine.setVaccineName(hepbName);
					}
					if (name != null && name.toLowerCase().contains("var")){
						vaccine.setVaccineCode(varCode);
						vaccine.setVaccineName(varName);
					}
					if (name != null && name.toLowerCase().contains("ipv")){
						vaccine.setVaccineCode(ipvCode);
						vaccine.setVaccineName(ipvName);
					}
					if (name != null && name.toLowerCase().contains("pcv")){
						vaccine.setVaccineCode(pcvCode);
						vaccine.setVaccineName(pcvName);
					}
					if (name != null && name.toLowerCase().contains("rota")){
						vaccine.setVaccineCode(rotaCode);
						vaccine.setVaccineName(rotaName);
					}
					
					if (name != null && name.toLowerCase().contains("mmr")){
						vaccine.setVaccineCode(mmrCode);
						vaccine.setVaccineName(mmrName);
					}
					if (name != null && name.toLowerCase().contains("influenza date")){
						vaccine.setVaccineCode(infCode);
						vaccine.setVaccineName(infName);
					}
					if (name != null && name.toLowerCase().contains("influenza intranasal date")){
						vaccine.setVaccineCode(infNasalCode);
						vaccine.setVaccineName(infNasalName);
					}
					vaccine.setDateGiven(vaccineObs.getValueDatetime());
					vaccine.setVaccineDose("999");
					vaccine.setEncounterId(encounterId);
					constructor.addVaccine( vaccine);
				}
			}
			i++;
		}
		
		
	
		
		System.out.println(constructor.getVXUMessageString());
		String vxu = constructor.getVXUMessageString();
		System.out.println(vxu);
		if (vxu != null){
			return new Result(vxu);
		}
		
		return Result.emptyResult();
	}
	
	private String getData(String message){
		String data = "";
		try {
			AdministrationService adminService = Context.getAdministrationService();
			String userId = adminService
			.getGlobalProperty("chica.ImmunizationQueryUserId");
			String password = adminService
			.getGlobalProperty("chica.ImmunizationQueryPassword");
			String url = adminService
			.getGlobalProperty("chica.ImmunizationQueryURL");

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
		} catch (APIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return data;
	}
}