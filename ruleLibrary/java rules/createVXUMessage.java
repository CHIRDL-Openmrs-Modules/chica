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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptName;
import org.openmrs.Obs;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.Duration;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.datasource.LogicDataSource;
import org.openmrs.logic.impl.LogicContextImpl;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chica.hl7.immunization.ImmunizationQueryConstructor;
import org.openmrs.module.chica.service.ChicaService;

import ca.uhn.hl7v2.model.v231.message.VXU_V04; 




public class createVXUMessage implements Rule
{
	private Log log = LogFactory.getLog(this.getClass());
	private LogicService logicService = Context.getLogicService();
	private final String SOURCE = "CVX";
	private String  action = "U";
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
		try{ 
			PatientService patientService = Context.getPatientService();
			EncounterService encounterService = Context.getService(EncounterService.class);
			System.out.println("Running createvxumessage");
			Integer encounterId = (Integer) parameters.get("encounterId");
			
			org.openmrs.Encounter encounter =   encounterService.getEncounter(encounterId);
			
			VXU_V04 vxu = new VXU_V04();
			ImmunizationQueryConstructor.constructVXU(vxu,encounter); 
			String vxuString = ImmunizationQueryConstructor.getVXUMessageString(vxu);
			
			vxuString = addVaccine(vxuString,  "DTaP", context, patientId, encounterId, action );
			vxuString = addVaccine(vxuString,  "Varicella",context, patientId, encounterId,action);
			vxuString = addVaccine(vxuString,  "MMR", context, patientId, encounterId, action);
			vxuString = addVaccine(vxuString,  "Influenza", context, patientId, encounterId, action);
			vxuString = addVaccine(vxuString,  "Pneumococcal", context, patientId, encounterId, action);
			vxuString = addVaccine(vxuString,  "MCV", context, patientId, encounterId, action);
			vxuString = addVaccine(vxuString,  "IPV", context, patientId, encounterId, action);
			vxuString = addVaccine(vxuString,  "HepA", context, patientId, encounterId, action);
			vxuString = addVaccine(vxuString,  "HepB", context, patientId, encounterId, action);
			vxuString = addVaccine(vxuString,  "Hib", context, patientId, encounterId, action);
			vxuString = addVaccine(vxuString,  "Rotavirus", context, patientId, encounterId, action);
			vxuString = addVaccine(vxuString,  "PCV", context, patientId, encounterId, action);
			vxuString = addVaccine(vxuString,  "Tdap", context, patientId, encounterId, action);
			vxuString = addVaccine(vxuString,  "HPV", context, patientId, encounterId, action);
			vxuString = addVaccine(vxuString,  "PPD", context, patientId, encounterId, action);
			
			vxuString = addVaccineHistory(vxuString, "DTaP", context, patientId,encounterId, action);
			vxuString = addVaccineHistory(vxuString,  "Varicella",context, patientId, encounterId,action);
			vxuString = addVaccineHistory(vxuString,  "MMR", context, patientId, encounterId, action);
			vxuString = addVaccineHistory(vxuString,  "Pneumococcal", context, patientId, encounterId, action);
			vxuString = addVaccineHistory(vxuString,  "IPV", context, patientId, encounterId, action);
			vxuString = addVaccineHistory(vxuString,  "HepA", context, patientId, encounterId, action);
			vxuString = addVaccineHistory(vxuString,  "HepB", context, patientId, encounterId, action);
			vxuString = addVaccineHistory(vxuString,  "Hib", context, patientId, encounterId, action);
			vxuString = addVaccineHistory(vxuString,  "Rotavirus", context, patientId, encounterId, action);
			vxuString = addVaccineHistory(vxuString,  "PCV", context, patientId, encounterId, action);
			vxuString = addVaccineHistory(vxuString,  "HPV", context, patientId, encounterId, action);
			vxuString = addVaccineHistory(vxuString,  "Tdap", context, patientId, encounterId, action);
			vxuString = addVaccineHistory(vxuString,  "Influenza", context, patientId, encounterId, action);
			vxuString = addVaccineHistory(vxuString,  "MCV", context, patientId, encounterId, action);
			
			
			
		
			System.out.println(new Date() + " " + vxu);
			
			if (vxuString != null){
				return new Result(vxuString);
			}
			
		} catch (Exception e){
			log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}
		return Result.emptyResult();
	}
	
	/*
	 * Add the vaccines given at the patient's encounter to the VXU string.
	 * Lookup CVX and CPT codes from concept mapping
	 *
	 */
	private String addVaccine(String vxu, String vaccineType, LogicContext context, Integer patientId, 
			Integer encounterId, String action){
		
		
		LogicDataSource obsDataSource = context.getLogicDataSource("obs");
		ConceptService conceptService = Context.getConceptService();
		String newVxuString = vxu;
		//obsDataSource = context.getLogicDataSource("obs");
		
		try {
			String conceptName = "CHICA " + vaccineType + " Given";
			
			LogicCriteria conceptCriteria  = new LogicCriteriaImpl(conceptName);
			LogicCriteria fullCriteria = null;
			
			if(encounterId != null)
			{
				LogicCriteria encounterCriteria = 
					new LogicCriteriaImpl("encounterId").equalTo(encounterId.intValue());
				fullCriteria = conceptCriteria.and(encounterCriteria);
			}else
			{
				fullCriteria = conceptCriteria;
			}
			
			String armThigh = "";
			String leftRight = "";
			String routeCode = "";
			String routeName = "";
			String cvxName = "";
			String cptCode = "";
			String cptName = "";
			String cvxCode = "";
			String lotNumber = "";
			Obs vaccineObs = null; 
			Concept answer = null;
		
			Result result = context.read(patientId, this.logicService
					.getLogicDataSource("CHICA"), fullCriteria.last());
				
			
		//	Result result = context.read(patientId, context.getLogicDataSource("obs"),
		//			fullCriteria.last());
		//	Result result = context.read(patientId, obsDataSource, 
		//			new LogicCriteriaImpl(conceptName).within(Duration.days(-20)).last());
			
			if (result == null || result.toObject() == null || !(result.toObject() instanceof Obs) ){
				log.info("Immunization: There are no observations for vaccine: " + vaccineType);
				return vxu;
			}
			
			if (result != null && result.toObject() != null 
					|| result.toObject() instanceof Obs){
				
				vaccineObs = ((Obs) result.toObject());
				
				//get the cvx and cpt codes from concept maps
				answer = vaccineObs.getValueCoded();
				if (answer == null){
					log.error("Immunization: An obs exists for concept " + conceptName
							+ ", but there is no value coded for that obs. PatientId: " + patientId);
				}
				cvxCode = getCode(answer, "CVX");
				
				if (cvxCode == null){
					return vxu;
				}
		
				Concept cvxConcept = conceptService.getConceptByMapping(cvxCode, "CVX");
				
				if (cvxConcept == null){
					log.error("Immunization: No concepts found for CVX code: " + cvxCode);
					return vxu;
				}
					
				ConceptName cvxConceptName = cvxConcept.getName();
				if (cvxConceptName == null){
					log.error("Immunization: There is not a conceptName for Conceptid = "  + cvxConcept.getConceptId());
					return vxu;
				}
				
				cvxName = cvxConceptName.getName();
					
				//Get the CPT code if available
				cptCode = getCode(answer, "CPT");
				cptName = cvxName;
						
			}
				
			conceptName = "CHICA " + vaccineType + " Arm/Thigh";
			result = context.read(patientId, obsDataSource, 
					new LogicCriteriaImpl(conceptName).within(Duration.days(-2)).last());
			if (result != null && !result.isEmpty()) {
				armThigh = result.toString();
			}
			
			conceptName = "CHICA " + vaccineType + " Left/Right";
			result = context.read(patientId, obsDataSource, 
					new LogicCriteriaImpl(conceptName).within(Duration.days(-2)).last());
			if (result != null && !result.isEmpty()) {
				leftRight = result.toString();
			}
			
			/*conceptName = "CHICA " + vaccineType + " Lot Number";
			result = context.read(patientId, obsDataSource, 
					new LogicCriteriaImpl(conceptName).within(Duration.days(-2)).last());
			if (result != null && !result.isEmpty()) {
				// Since lot number cannot be entered on form, send a bogus number to be
				//updated later by staff
				//lotNumber = result.toString();
				lotNumber = "9999999999";
			}
			*/
			conceptName = "CHICA " + vaccineType + " Route";
			result = context.read(patientId, obsDataSource, 
					new LogicCriteriaImpl(conceptName).within(Duration.days(-5)).last());
			if (result != null && !result.isEmpty()) {
				routeName = result.toString().toUpperCase();
			} else {
				routeName = "OTHER/MISCELLANEOUS";
			}
			
			routeCode = getRouteCode(routeName);

			
	    	newVxuString = ImmunizationQueryConstructor
	    		.addVaccine(vxu, vaccineObs, cvxName, cvxCode, cptName, cptCode, armThigh, 
	    			leftRight, lotNumber, routeCode, routeName, action, false, true);
			System.out.println(new Date() + " " + newVxuString);
			
		} catch (Exception e){
			log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		} 
		return newVxuString;
		
	}
	
	/* Vaccine history contains historical doses (dates) entered on the front side of the form
	 * 
	 */
	private String addVaccineHistory(String vxu, String vaccineType, LogicContext context, 
			Integer patientId, Integer encounterId, String action){
		
		ConceptService conceptService = Context.getConceptService();
		LogicDataSource obsDataSource = context.getLogicDataSource("obs");
		String conceptName = "CHICA " + vaccineType + " Date";
		LogicCriteria conceptCriteria = new LogicCriteriaImpl(conceptName);
		LogicCriteria fullCriteria = null;
		LogicCriteria encounterCriteria = null;
		Obs vaccineObs = null;

		try{ 
			if (encounterId != null)
			{
				encounterCriteria = 
					new LogicCriteriaImpl("encounterId").equalTo(encounterId.intValue());
				fullCriteria = conceptCriteria.and(encounterCriteria);
			} else
			{
				fullCriteria = conceptCriteria;
			}

	    	String name = getUnspecifiedVaccineName(vaccineType);
	    	if (name == null || name.trim().equals("")){
	    		log.error(vaccineType + " does not exist in unspecified vaccine map.");
	    		return vxu;
	    	}
	    	Concept concept = conceptService.getConceptByName(name);
	    	String cvxCode = getCode(concept, "CVX");
	    	String cptCode = getCode(concept, "CPT");
	    	
	    	//Get value_datetime for all vaccines entered by the physician for this vaccine type
			Result results = context.read(patientId, obsDataSource, fullCriteria);
			
			for (Result result : results){
				//Result is a set of observations with a datetime value
				if (result.toObject() != null 
						&& result.toObject() instanceof Obs){
					vaccineObs = (Obs)result.toObject();
					
					vxu = ImmunizationQueryConstructor.addVaccineHistory(vxu, vaccineObs,
		    			name, cvxCode, name, cptCode, action, false, false);
				}
		 
				System.out.println(new Date() + " " + vxu);
			}
			
		} catch (Exception e){
			log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		} 
		return vxu;
		
	}
	
	/*
	 * Use the general vaccine for the historical entries, since
	 * the observation value is the date the general vaccine was given.
	 */
	private HashMap<String, String> setupUnspecifiedVaccineNames() {

		HashMap<String, String> map = new HashMap<String, String>();

		map.put("DTaP", "DTaP, unspecified formulation");
		map.put("Hep A", "Hep A, unspecified formulation");
		map.put("HepA", "Hep A, unspecified formulation");
		map.put("Hep B", "Hep B, unspecified formulation");
		map.put("HepB", "Hep B, unspecified formulation");
		map.put("Hib", "Hib, unspecified formulation");
		map.put("influenza", "influenza, unspecified formulation");
		map.put("Influenza", "influenza, unspecified formulation");
		map.put("MMR", "MMR");
		map.put("PCV", "pneumococcal, unspecified formulation");
		map.put("IPV", "polio, unspecified formulation");
		map.put("Rotavirus", "rotavirus, unspecified formulation");
		map.put("varicella", "varicella");
		map.put("Varicella", "varicella");
		map.put("pneumococcal", "pneumococcal polysaccharide PPV23");
		map.put("Pneumococcal", "pneumococcal polysaccharide PPV23");
		map.put("influenza nasal", "influenza, live, intranasal");
		map.put("Tdap","Td(adult) unspecified formulation");
		map.put("HPV","HPV, unspecified formulation");
		map.put("PPD", "TST-PPD intradermal");
		map.put("MCV", "meningococcal MCV4, unspecified formulation");

		return map;
	}
	
	
	private HashMap<String, String> setupRouteCode() {
		//IM = intramuscular  SC = Subcutaneous  IN = intranasal
		HashMap<String, String> map = new HashMap<String, String>();

		/*map.put("IM", "Intramuscular");
		map.put("SC", "Subcutaneous");
		map.put("OTH", "Other/Miscellaneous");
		map.put("ID", "Intradermal");
		map.put("IN", "Intranasal");
		map.put("IV", "Intravenous");
		map.put("PO", "Oral");
		map.put("TD", "Transdermal");*/
		
		map.put("INTRAMUSCULAR", "IM");
		map.put("SUBCUTANEOUS", "SC");
		map.put("OTHER/MISCELLANEOUS", "OTH");
		map.put("INTRADERMAL", "ID");
		map.put("INTRANASAL", "IN");
		map.put("INTRAVENOUS", "IV");
		map.put("ORAL ROUTE", "PO");
		map.put("TRANSDERMAL", "TD");
		
		
		return map;
	}
	// Vaccine names for addition of unspecified/general vaccine names
	private String getUnspecifiedVaccineName(String name){
		HashMap<String, String> map = setupUnspecifiedVaccineNames();
		return map.get(name);
	}
	
	private String getRouteCode(String name){
		HashMap<String, String> map = setupRouteCode();
		if (name == null){
			return "";
		}
		return map.get(name);
	}
	
	
	
	private String getCode(Concept concept, String dictionary){
		ChicaService chicaService = Context.getService(ChicaService.class);
		String code = null;
		//Get the CVX code and name
		List<ConceptMap> conceptMaps = 
			chicaService.getConceptMapsByVaccine(concept, dictionary);
		
		if (concept == null){
		
			log.error("Immunization: get code failed because concept is null");
			return null;
		}
		if (concept.getName() == null){
			log.error("Immunization: concept had no name");
			return null;
		}

		if (conceptMaps == null || conceptMaps.size() == 0){
		
			return null;
		
		}
		//Some concepts have more than one CVX code to address CHIRP values of "03" and "3"
		//Either is fine for VXU updates to CHIRP.
		ConceptMap conceptMap = conceptMaps.get(0);
		code = conceptMap.getSourceCode();
		return code;
	}
	
	
}
	