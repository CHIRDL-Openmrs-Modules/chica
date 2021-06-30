/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.chica.rule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.ObsComparator;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.ObsAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.module.dss.xmlBeans.physiciannote.HeadingOrder;
import org.openmrs.module.dss.xmlBeans.physiciannote.PhysicianNoteConfig;


/**
 *
 * @author Steve McKee
 */
public class physicianNoteObs implements Rule {
	
	private Log log = LogFactory.getLog(physicianNoteObs.class);
	private static PhysicianNoteConfig physicianNoteConfig = null;
	private static long lastUpdatedConfig = System.currentTimeMillis();
	private static final long UPDATE_CYCLE = 3600000; // 1 hour
	
	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer, java.util.Map)
	 */
	@Override
	public Result eval(LogicContext logicContext, Integer patientId, Map<String, Object> parameters) throws LogicException {
		long startTime = System.currentTimeMillis();
		Patient patient = Context.getPatientService().getPatient(patientId);
		if (patient == null) {
			this.log.error("Patient cannot be found with ID: " + patientId);
			System.out.println("chicaNoteObs: " + (System.currentTimeMillis() - startTime) + "ms");
			return Result.emptyResult();
		}
		
		Integer encounterId = null;
		Object encounterIdObj = parameters.get(ChirdlUtilConstants.PARAMETER_ENCOUNTER_ID);
		if (encounterIdObj instanceof Integer) {
			encounterId = (Integer)encounterIdObj;
		} else if (encounterIdObj instanceof String) {
			String encounterIdStr = (String)encounterIdObj;
			try {
				encounterId = Integer.valueOf(encounterIdStr);
			} catch (NumberFormatException e) {
				this.log.error("Error parsing value " + encounterIdStr + " into an encounter ID integer.", e);
				return Result.emptyResult();
			}
		} else {
			this.log.error("Cannot determine encounter ID.  No note will be created.");
			return Result.emptyResult();
		}
		
		String obsNote = buildObsNote(patient, encounterId);
		if (obsNote.trim().length() > 0) {
			System.out.println("chicaNoteObs: " + (System.currentTimeMillis() - startTime) + "ms");
			return new Result(obsNote);
		}
		
		System.out.println("chicaNoteObs: " + (System.currentTimeMillis() - startTime) + "ms");
		return Result.emptyResult();
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getDefaultDatatype()
	 */
	@Override
	public Datatype getDefaultDatatype() {
		return Datatype.CODED;
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getDependencies()
	 */
	@Override
	public String[] getDependencies() {
		return new String[]{};
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getParameterList()
	 */
	@Override
	public Set<RuleParameterInfo> getParameterList() {
		return new HashSet<>();
	}
	
	/**
	 * @see org.openmrs.logic.Rule#getTTL()
	 */
	@Override
	public int getTTL() {
		return 0; // 60 * 30; // 30 minutes
	}
	
	/**
     * Builds a note with all observations for the day containing the provided question Concept.
     * 
     * @param patient The patient used to retrieve the observations.
     * @param encounterId The current encounter identifier
     * @return String containing a note with the observations for the day for the provided patient and question Concept.  
     * This will not return null.
     */
    private String buildObsNote(Patient patient, Integer encounterId) {
    	StringBuilder noteBuffer = new StringBuilder();  
    	Concept noteConcept = Context.getConceptService().getConceptByName("CHICA_Note");
		if (noteConcept == null) {
			this.log.error(
				"Physician note observations cannot be constructed because concept \"CHICA_Note\" does not exist.");
			return noteBuffer.toString();
		}
		
		Encounter latestEncounter = Context.getEncounterService().getEncounter(encounterId);
		if (latestEncounter == null) {
			return noteBuffer.toString();
		}
		
		List<Encounter> encounterList = new ArrayList<>();
		encounterList.add(latestEncounter);
		
    	// Get Observations for the encounter.
		List<Person> persons = new ArrayList<>();
		persons.add(patient);
		
		List<Concept> questions = new ArrayList<>();
		questions.add(noteConcept);
		List<Obs> obs = Context.getObsService().getObservations(
			persons, encounterList, questions, null, null, null, null, null, null, null, null, false);
		if (obs == null || obs.isEmpty()) {
			return noteBuffer.toString();
		}
		
		Map<String,Map<String,List<Obs>>> headingMap = new HashMap<>();
		List<Obs> addtnlObs = new ArrayList<>();
		ChirdlUtilBackportsService service = Context.getService(ChirdlUtilBackportsService.class);
		Set<String> ruleIdOrder = new LinkedHashSet<>();
		// Order the list by observation ID
		Collections.sort(obs, new ObsComparator());
		for (Obs ob :obs) {
			// Get the heading attribute
			ObsAttributeValue obsAttrVal = service.getObsAttributeValue(ob.getObsId(), "primaryHeading");
			if (obsAttrVal == null || obsAttrVal.getValue() == null || obsAttrVal.getValue().trim().length() == 0) {
				// No heading attribute was found.  We'll just add it to a generic heading.
				addtnlObs.add(ob);
				continue;
			}
			
			String heading = obsAttrVal.getValue();
			Map<String,List<Obs>> obsMap = headingMap.get(heading);
			if (obsMap == null) {
				obsMap = new HashMap<>();
				obsMap.put("default", new ArrayList<Obs>());
				headingMap.put(heading, obsMap);
			}
			
			// Get the ruleId attribute
			ObsAttributeValue ruleIdObsAttrVal = service.getObsAttributeValue(ob.getObsId(), "ruleId");
			if (ruleIdObsAttrVal == null || ruleIdObsAttrVal.getValue() == null || 
					ruleIdObsAttrVal.getValue().trim().length() == 0) {
				// No ruleId attribute was found.  We'll just add it to a generic heading.
				List<Obs> defaultList = obsMap.get("default");
				defaultList.add(ob);
				continue;
			}
			
			String ruleId = ruleIdObsAttrVal.getValue();
			List<Obs> obsList = obsMap.get(ruleId);
			if (obsList == null) {
				obsList = new ArrayList<>();
				obsMap.put(ruleId, obsList);
			}
			
			ruleIdOrder.add(ruleId);
			obsList.add(ob);
		}
		
		// Get the physician note configuration file
		PhysicianNoteConfig config = null;
		try {
	        config = getPhysicianNoteConfig();
        }
        catch (FileNotFoundException e) {
	        this.log.error("Physician note configuration file could not be found", e);
        }
        catch (JiBXException e) {
	        this.log.error("Exception occurred loading the physician note configuration file", e);
        }
		
        if (config == null) {
        	buildObsNoteFromMap(headingMap, noteBuffer, ruleIdOrder);
        } else {
        	HeadingOrder headingOrder = config.getHeadingOrder();
        	if (headingOrder == null) {
        		buildObsNoteFromMap(headingMap, noteBuffer, ruleIdOrder);
        	} else {
        		String[] headings = headingOrder.getHeadings();
        		if (headings == null || headings.length == 0) {
        			buildObsNoteFromMap(headingMap, noteBuffer, ruleIdOrder);
        		} else {
        			// Build the note in the order of the headings in the configuration file.
        			buildObsNoteFromMapWithHeadings(headingMap, noteBuffer, headings, ruleIdOrder);
        			// Run this in case there are some headings left that aren't in the configuration file.
        			buildObsNoteFromMap(headingMap, noteBuffer, ruleIdOrder);
        		}
        	}
        }
        
		if (!addtnlObs.isEmpty()) {
			int counter = 1;
			noteBuffer.append("ADDITIONAL OBSERVATIONS\n");
			Set<String> noteSet = new HashSet<>();
			for (Obs ob : addtnlObs) {
				String value = ob.getValueText();
				if (value != null && value.trim().length() > 0) {
					value = replaceRiskIndicators(value);
					boolean newAddition = noteSet.add(value);
					if (newAddition) {
						noteBuffer.append(counter++);
						noteBuffer.append(". ");
						noteBuffer.append(value);
						noteBuffer.append("\n");
					}
				}
			}
			
			noteSet.clear();
		}
    	
    	return noteBuffer.toString();
    }
    
    private void buildObsNoteFromMap(Map<String,Map<String,List<Obs>>> headingMap, StringBuilder noteBuffer, 
                                     Set<String> ruleIdOrder) {
    	Set<Entry<String,Map<String,List<Obs>>>> mapEntries = headingMap.entrySet();
		Iterator<Entry<String,Map<String,List<Obs>>>> iter = mapEntries.iterator();
		while (iter.hasNext()) {
			Entry<String,Map<String,List<Obs>>> entry = iter.next();
			String heading = entry.getKey();
			Map<String,List<Obs>> obsMap = entry.getValue();
			writeObsToNote(heading, obsMap, noteBuffer,ruleIdOrder);
		}
    }
    
    private void buildObsNoteFromMapWithHeadings(Map<String,Map<String,List<Obs>>> headingMap, StringBuilder noteBuffer, 
                                                 String[] headings, Set<String> ruleIdOrder) {
    	for (String heading : headings) {
    		Map<String,List<Obs>> obsMap = headingMap.get(heading);
    		if (obsMap != null) {
	    		writeObsToNote(heading, obsMap, noteBuffer, ruleIdOrder);
				headingMap.remove(heading);
    		}
    	}
    }
    
    private void writeObsToNote(String heading, Map<String,List<Obs>> obsMap, StringBuilder noteBuffer, 
                                Set<String> ruleIdOrder) {
    	if (obsMap.isEmpty()) {
    		return;
    	}
    	
    	noteBuffer.append("==" + heading + "==");
    	noteBuffer.append("\n");
    	Iterator<String> iter = ruleIdOrder.iterator();
    	while (iter.hasNext()) {
    		String ruleId = iter.next();
    		List<Obs> obsList = obsMap.get(ruleId);
    		if (obsList != null && obsList.size() > 0) {
    			Set<String> noteSet = new HashSet<>();
				for (Obs ob : obsList) {
					String value = ob.getValueText();
					if (value != null && value.trim().length() > 0) {
						value = replaceRiskIndicators(value);
						boolean newAddition = noteSet.add(value);
						if (newAddition) {
							noteBuffer.append(value);
							noteBuffer.append("  ");
						}
					}
				}
				
				noteSet.clear();
				noteBuffer.append("\n");
    		}
    		
    	}
    	
    	noteBuffer.append("\n");
    }
    
    /**
     * Returns the physician note configuration object.
     * 
     * @return PhysicianNoteConfig object or null if the XML file cannot be found.
     * @throws JiBXException
     * @throws FileNotFoundException
     */
    private PhysicianNoteConfig getPhysicianNoteConfig() throws JiBXException, FileNotFoundException {
    	long currentTime = System.currentTimeMillis();
    	if (physicianNoteConfig == null || (currentTime - lastUpdatedConfig) > UPDATE_CYCLE) {
    		lastUpdatedConfig = currentTime;
			AdministrationService adminService = Context.getAdministrationService();
			String configFileStr = adminService.getGlobalProperty(
				"dss.physicianNoteConfigFile");
			if (configFileStr == null) {
				this.log.error("You must set a value for global property: "
					+ "dss.physicianNoteConfigFile");
				return physicianNoteConfig;
			}
			
			File configFile = new File(configFileStr);
			if (!configFile.exists()) {
				this.log.error("The file location specified for the global property "
					+ "dss.physicianNoteConfigFile does not exist.");
				return physicianNoteConfig;
			}
			
			IBindingFactory bfact = 
		        BindingDirectory.getFactory(PhysicianNoteConfig.class);
			
			IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
			physicianNoteConfig = (PhysicianNoteConfig)uctx.unmarshalDocument(
				new FileInputStream(configFile), null);
    	}
    	
    	return physicianNoteConfig;
	}
    
    /**
     * Replaces the high risk indicator symbols around a note.
     * 
     * @param note The String that will have the indicators replaced.
     * @return String with the indicators replaced.
     */
    private String replaceRiskIndicators(String note) {
    	if (note == null) {
    		return null;
    	}
    	
    	if (note.startsWith("***")) {
    		note = note.replaceFirst("\\*\\*\\*", "+++");
    	}
    	else if (note.startsWith("**")) {
    		note = note.replaceFirst("\\*\\*", "++");
    	}
    	else if (note.startsWith("*")) {
    		note = note.replaceFirst("\\*", "+");
    	}
    	
    	if (note.endsWith("***")) {
			note = note.substring(0, note.length() - 3);
			note = note + "+++";
		} else if (note.endsWith("**")) {
			note = note.substring(0, note.length() - 2);
			note = note + "++";
		} else if (note.endsWith("*")) {
			note = note.substring(0, note.length() - 1);
			note = note + "+";
		}
    	
    	return note;
    }
	
}
