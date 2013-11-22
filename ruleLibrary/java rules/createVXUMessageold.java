/*
 Title : createVXUMessage
 Filename:  createVXUMessage.java
 Version : 0 . 0
 Institution : Indiana University School of Medicine
 Author : Meena Sheley
 Specialist : 
 Date : 
 Validation :
 Purpose : Creates a VXU^V04 hl7 message to update a patients immuninzation records 
 the CHIRP registry
 Keywords : 
 Citations : 
 Links :
 */
package org.openmrs.module.chica.rule;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.ConceptName;
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
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.module.chirdlutilbackports





public class createVXUMessage implements Rule
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
		ImmunizationQueryConstructor constructor = new ImmunizationQueryConstructor();
		PatientService patientService = Context.getPatientService();
		EncounterService encounterService = Context.getService(EncounterService.class);
		ChirdlUtilBackportsService service = Context
		.getService(ChirdlUtilBackportsService.class);
		ObsService obsService = Context.getObsService();
		Obs vaccine = null;
		String leftRightString = "";
		String armThighString = "";
		String lotNumberString = "";
		
		int i = 1;
		Result results = Result.emptyResult();
		
	
		patientService.getPatient(patientId);
		//construct the main VXU 
		Integer encounterId = (Integer) parameters.get("encounterId");
		org.openmrs.Encounter encounter =   encounterService.getEncounter(encounterId);
		constructor.constructVXU(encounter); 
		String vxu = constructor.getVXUMessageString();
		System.out.println(vxu);
	
	 
		
		Object object = (Object) parameters.get("param1");
		if (object != null && object instanceof Result){
			results = (Result) object;
			for (Result result: results){
				vaccine = (Obs) result.toObject();
				if (vaccine != null){
					Concept concept = vaccine.getValueCoded();
					
					//get the site and lot number
					Integer obsId = vaccine.getObsId();
					ObsAttributeValue leftRightObsValue 
						= service.getObsAttributeValue(obsId, "site_left_right");
					ObsAttributeValue armThighObsValue 
					= service.getObsAttributeValue(obsId, "site_arm_thigh");
					ObsAttributeValue lotNumberObsValue 
					= service.getObsAttributeValue(obsId, "lot_number");
					
					if (leftRightObsValue != null){
						leftRightString  = leftRightObsValue.getValue();
					}else {
						leftRightString = "";
					}
					
					if (armThighObsValue != null){
						armThighString  = armThighObsValue.getValue();
					}else {
						armThighString = "";
					}
					
					if (lotNumberObsValue != null){
						lotNumberString  = lotNumberObsValue.getValue();
					}else {
						lotNumberString = "";
					}
					
					if (concept != null){
						ConceptName conceptName = concept.getName();
						String chicaVaccineName = conceptName.getName();
						String name = lookupVaccineName(chicaVaccineName);
						String code = this.lookupVaccineCode(name);
						constructor.addVaccine( vaccine, name, code);
 
					}
					
				}
			}
			
		}
		
		System.out.println(constructor.getVXUMessageString());
		vxu = constructor.getVXUMessageString();
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
	
	private String getVaccineCode(String name){
		String code = "";
		
		return code;
	}
	
	private HashMap<String, String> setupVaccineNameLookup() {

		HashMap<String, String> map = new HashMap<String, String>();

		map.put("DTaP/Hep B/IPV", "DTaP-Hep B-IPV");
		map.put("DTaP/Hib/IPV", "DTaP-Hib-IPV");
		map.put("DTaP/IPV", "DTaP-IPV");
		map.put("DTaP", "DTaP");
		map.put("Hep A 2 dose - Ped/Adol", "Hep A, ped/adol, 2 dose");
		map.put("Hep A 3 dose - Ped/Adol", "Hep A, ped/adol, 3 dose");
		map.put("Hep B", "Hep B, adolescent or pediatric");
		map.put("Hep B/Hib", "Hib-Hep B");
		map.put("Hib--PRP-T", "Hib (PRP-T)");
		map.put("Hib--PRP-OMP", "Hib (PRP-OMP)");
		map.put("IPV", "IPV");
		map.put("Influ Inact 48+ mos", "Influenza, seasonal, injectable, preservative free");
		map.put("Influenza Nasal Spray", "influenza, live, intranasal");
		map.put("Influenza Split", "Influenza, seasonal, injectable, preservative free");
		map.put("MMR", "MMR");
		map.put("MMR/Varicella", "MMRV");
		map.put("Mening. (MCV4O)", "Meningococcal MCV4O");
		map.put("Mening. (MCV4P)", "meningococcal MCV4P");
		map.put("Pneumococcal(PPSV)", "pneumococcal polysaccharide PPV23");
		map.put("Pneumococcal(PCV13)", "Pneumococcal conjugate PCV 13");
		map.put("Pneumococcal(PCV7)", "pneumococcal conjugate PCV 7");
		map.put("Rotavirus, monovalent RV1", "rotavirus, monovalent");
		map.put("Rotavirus, pentavalent RV5", "rotavirus, pentavalent");
		map.put("Rotavirus, tetravalent", "rotavirus, tetravalent");
		map.put("Varicella", "varicella");
		
		
		
		return map;
	}
	
	private HashMap<String, String> setupVaccineCodeLookup() {

		HashMap<String, String> map = new HashMap<String, String>();
	
		map.put("DTaP-Hep B-IPV", "110");
		map.put("DTaP-Hib-IPV", "120");
		map.put("DTaP-IPV", "130");
		map.put("DTaP", "20");
		map.put("Hep A, ped/adol, 2 dose", "83");
		map.put("Hep A, ped/adol, 3 dose","84");  // inactive
		map.put("Hep B, adolescent or pediatric", "08");
		map.put("Hib-Hep B", "51");
		map.put("Hib (PRP-T)","48");
		map.put("Hib (PRP-OMP)","49");
		map.put("IPV", "10");
		map.put("Influenza, seasonal, injectable, preservative free", "141" );
		map.put("influenza, live, intranasal", "111");
		map.put("MMR", "03");
		map.put("MMRV", "94");
		map.put("Meningococcal MCV4O", "136");
		map.put("meningococcal MCV4P", "114");
		map.put("pneumococcal polysaccharide PPV23", "33");
		map.put("Pneumococcal conjugate PCV 13","133");
		map.put("pneumococcal conjugate PCV 7","100");
		map.put("rotavirus, monovalent","119");
		map.put("rotavirus, pentavalent", "116");
		map.put("rotavirus, tetravalent", "74");
		map.put("Varicella", "21");

		return map;
	}
	
	private String lookupVaccineName(String chicaName) {
		HashMap<String, String> map = setupVaccineNameLookup();
		return map.get(chicaName);
	}
	
	private String lookupVaccineCode(String vaccineName) {
		HashMap<String, String> map = setupVaccineCodeLookup();
		return map.get(vaccineName);
	}
	
}
	