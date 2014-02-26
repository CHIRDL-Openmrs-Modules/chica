package org.openmrs.module.chica.rule;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chica.gis.GeocodeLocations;
import org.openmrs.module.chica.gis.PatientGISData;
import org.openmrs.module.chica.gis.PatientGISDataStorage;
import org.openmrs.module.chica.gis.Placemark;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.ObsAttribute;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.ObsAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

/**
 * Stores the recommended resources as observations.
 */
public class GISStoreResources implements Rule {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer, java.util.Map)
	 */
	public Result eval(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		String mapType = (String) parameters.get("param1");
		if (mapType == null) {
			log.error("Map type string not specified.");
			return Result.emptyResult();
		}
		
		String resourceConceptStr = (String) parameters.get("param2");
		if (resourceConceptStr == null) {
			log.error("Resource concept string not specified.");
			return Result.emptyResult();
		}
		
		Concept resourceConcept = Context.getConceptService().getConceptByName(resourceConceptStr);
		if (resourceConcept == null) {
			log.error("No concept found with name " + resourceConceptStr);
			return Result.emptyResult();
		}
		
		PatientGISData data = PatientGISDataStorage.getPatientGISData(patientId, mapType);
		if (data == null) {
			return Result.emptyResult();
		}
		
		GeocodeLocations locations = data.getGeocodeLocations();
		if (locations == null || locations.Placemark == null) {
			return Result.emptyResult();
		}
		
		ChirdlUtilBackportsService service = Context.getService(ChirdlUtilBackportsService.class);
		ObsAttribute rankAttr = service.getObsAttributeByName("resourceRank");
		if (rankAttr == null) {
			log.error("No observation attribute found with name resourceRank");
			return Result.emptyResult();
		}
		
		Integer obsAttrId = rankAttr.getObsAttributeId();
		Integer encounterId = (Integer) parameters.get("encounterId");
		
		for (Placemark placemark : locations.Placemark) {
			int rank = placemark.rank;
			String name = placemark.OrgName;
			// Save the organization name to an observation
			Obs obs = org.openmrs.module.chirdlutil.util.Util.saveObs(Context.getPatientService().getPatient(patientId), 
				resourceConcept, encounterId, name, new Date());
			if (obs == null) {
				return Result.emptyResult();
			}
			
			// Save the observation attribute with the resource rank
			Integer obsId = obs.getObsId();
			ObsAttributeValue rankAttrVal = new ObsAttributeValue();
			rankAttrVal.setObsId(obsId);
			rankAttrVal.setValue(String.valueOf(rank));
			rankAttrVal.setObsAttributeId(obsAttrId);
			service.saveObsAttributeValue(rankAttrVal);
		}
		
		return Result.emptyResult();
	}
	
	/**
	 * @see org.openmrs.logic.rule.Rule#getParameterList()
	 */
	public Set<RuleParameterInfo> getParameterList() {
		return null;
	}
	
	/**
	 * @see org.openmrs.logic.rule.Rule#getDependencies()
	 */
	public String[] getDependencies() {
		return new String[] {};
	}
	
	/**
	 * @see org.openmrs.logic.rule.Rule#getTTL()
	 */
	public int getTTL() {
		return 0;
	}
	
	/**
	 * @see org.openmrs.logic.rule.Rule#getDatatype(String)
	 */
	public Datatype getDefaultDatatype() {
		return Datatype.TEXT;
	}
}
