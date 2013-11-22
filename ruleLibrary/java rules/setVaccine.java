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

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.hl7.immunization.Vaccine;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.LocationAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;


public class setVaccine implements Rule
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
		EncounterService encounterService = Context.getService(EncounterService.class);
		ChirdlUtilBackportsService service = Context.getService(ChirdlUtilBackportsService.class);
		LocationService locationService = Context.getLocationService();
		ConceptService conceptService = Context.getConceptService();
		String lotNumber = "";
		String providerFN = "";
		String providerLN = "";
		String providerMN = "";
		String LR = null;
		String AT = null;
		String source = null;
		String displayLocation = "";
		String vaccineName = null;
		String vaccineCode = null;
		String address1 = "";
		String address2 = "";
		String city = "";
		String state = "";
		String zipcode = "";
		Date dateGiven = null;
		String route = null;
		String routeCode = null;
		Result finalResult = new Result();
		Vaccine vaccine = new Vaccine();
		
		Object object = parameters.get("param1");
		if (object != null && object instanceof Result){
			Result result = (Result) parameters.get("param1");
			if (result != null && result.toObject() instanceof Obs){
				Obs obs = ((Obs) result.toObject());
				if (obs != null){
					dateGiven = obs.getValueDatetime();
				}
			}
			parameters.put("param1", null);
		}
		// get the observations for given, site, lot number, and set vaccine object
		
		object = parameters.get("param2");
		if (object != null && object instanceof Result){
			Result result = (Result) parameters.get("param2");
			if (result != null && result.toObject() instanceof Obs){
				Obs obs = ((Obs) result.toObject());
				if (obs != null){
					Concept concept = obs.getValueCoded();
					if (concept != null && concept.getName()!= null){
						AT = concept.getName().getName();
					}
				}
			}
			parameters.put("param2", null);
		} else if (object != null && object instanceof String){
			AT = object.toString();
		}
		
		object = parameters.get("param3");
		if (object != null && object instanceof Result){
			Result result = (Result) parameters.get("param3");
			if (result != null && result.toObject() instanceof Obs){
				Obs obs = ((Obs) result.toObject());
				if (obs != null){
					Concept concept = obs.getValueCoded();
					if (concept != null && concept.getName()!= null){
						LR = concept.getName().getName();
					}
				}
			}
			parameters.put("param3", null);
		} else if (object != null && object instanceof String){
			LR = object.toString();;
		}
		
		object = parameters.get("param4");
		if (object != null && object instanceof Result){
			Result result = (Result) parameters.get("param4");
			if (result != null && result.toObject() instanceof Obs){
				Obs obs = ((Obs) result.toObject());
				if (obs != null){
					Concept concept = obs.getValueCoded();
					if (concept != null && concept.getName()!= null){
						source = concept.getName().getName();
					}
				}
			}
			parameters.put("param4", null);
		} else if (object != null && object instanceof String){
			source = object.toString();
		}
		
		
		object = parameters.get("param5");
		if (object != null && object instanceof Result){
			Result result = (Result) parameters.get("param5");
			if (result != null && result.toObject() instanceof Obs){
				Obs obs = ((Obs) result.toObject());
				if (obs != null){
					lotNumber = obs.getValueText();
				}
			}
			parameters.put("param5", null);
		} else if (object != null && object instanceof String){
			lotNumber = object.toString();
		}
		
		object = parameters.get("param6");
		if (object != null && object instanceof Result){
			Result result = (Result) parameters.get("param6");
			if (result != null && result.toObject() instanceof Obs){
				Obs obs = ((Obs) result.toObject());
				if (obs != null){
					vaccineCode = obs.getValueText();
				}
			}
			parameters.put("param6", null);
		} else if (object != null && object instanceof String){
			vaccineCode = object.toString();
		}
		
		object = parameters.get("param7");
		if (object != null && object instanceof Result){
			Result result = (Result) parameters.get("param7");
			if (result != null && result.toObject() instanceof Obs){
				Obs obs = ((Obs) result.toObject());
				if (obs != null){
					vaccineName = obs.getValueText();
				}
			}
			parameters.put("param7", null);
		} else if (object != null && object instanceof String){
			vaccineName = object.toString();
		}
		
		object = parameters.get("param8");
		if (object != null && object instanceof Result){
			Result result = (Result) parameters.get("param8");
			if (result != null && result.toObject() instanceof Obs){
				Obs obs = ((Obs) result.toObject());
				if (obs != null){
					routeCode = obs.getValueText();
				}
			}
			parameters.put("param8", null);
		} else if (object != null && object instanceof String){
			routeCode = object.toString();
		}
		
		object = parameters.get("param9");
		if (object != null && object instanceof Result){
			Result result = (Result) parameters.get("param9");
			if (result != null && result.toObject() instanceof Obs){
				Obs obs = ((Obs) result.toObject());
				if (obs != null){
					route = obs.getValueText();
				}
			}
			parameters.put("param9", null);
		} else if (object != null && object instanceof String){
			route = object.toString();
		}
		
		
		
		vaccine.setLocationId(patientId);
		
		Integer encounterId = (Integer) parameters.get("encounterId");
		Encounter encounter = (Encounter) encounterService.getEncounter(encounterId);
		Person provider = encounter.getProvider();
		providerLN = provider.getFamilyName();
		providerFN = provider.getGivenName();
		providerMN = provider.getMiddleName();
		
		
		Location location = encounter.getLocation();
		if (location != null){
			address1 = location.getAddress1();
			address2 = location.getAddress2();
			city = location.getCityVillage();
			state = location.getStateProvince();
			zipcode = location.getPostalCode();			
		}
		LocationAttributeValue attrvalue = service.getLocationAttributeValue(location.getLocationId(), "clinicDisplayName");
		if (attrvalue != null){
			displayLocation = attrvalue.getValue();
		}
		
		
		vaccine = new Vaccine();
		vaccine.setDateGiven(dateGiven);
		vaccine.setVaccineCode(vaccineCode);
		vaccine.setVaccineName(vaccineName);
		
		vaccine.setLotNumber(lotNumber);
		vaccine.setAT(AT);
		vaccine.setLR(LR);
		vaccine.setSource(source);
		if (LR != null && AT != null){
			vaccine.setSiteCombined(LR + " " + AT);
		}
		String site = "";
		//add code get code for site.
		if (LR != null && AT != null && !LR.equalsIgnoreCase("") 
				&& ! AT.equalsIgnoreCase("")){
			site = LR.substring(0, 1) + AT.substring(0, 1);
		}
		vaccine.setSiteCode(site.toUpperCase());
		vaccine.setEncounterId(encounterId);
		vaccine.setFacility(displayLocation);
		vaccine.setAddress1(address1);
		vaccine.setAddress2(address2);
		vaccine.setCity(city);
		vaccine.setZipcode(zipcode); 
		vaccine.setState(state);
		vaccine.setRoute(route);
		vaccine.setRouteCode(routeCode);
		//VACCINE provider is unknown from our scanned form. 
		//This is usually not the ordering physician.
		/*vaccine.setProviderFN(providerFN);
		vaccine.setProviderLN(providerLN);
		vaccine.setProviderMN(providerMN);*/
		finalResult.setResultObject(vaccine);
		
		return finalResult;
	}
	
	private void clearParameters(Map<String, Object> parameters){
		
	}
}