package org.openmrs.module.chica.rule;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

/**
 * Retrieves a piece of form metadata given a patient, map type, index, and data field name.
 */
public class GISMetadata implements Rule {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer, java.util.Map)
	 */
	public Result eval(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		String mapType = (String) parameters.get("param0");
		if (mapType == null) {
			log.error("Map type string not specified.");
			return Result.emptyResult();
		}
		
		String indexStr = (String) parameters.get("param1");
		if (indexStr == null) {
			log.error("Index string not specified.");
			return Result.emptyResult();
		}
		
		int index = -1;
		try {
			index = Integer.parseInt(indexStr);
		} catch (NumberFormatException e) {
			log.error("Invalid string provided for index", e);
			return Result.emptyResult();
		}
		
		String dataRequested = (String) parameters.get("param2");
		if (dataRequested == null) {
			return Result.emptyResult();
		}
		
		PatientGISData data = PatientGISDataStorage.getPatientGISData(patientId, mapType);
		if (data == null) {
			return Result.emptyResult();
		}
		
		GeocodeLocations locations = data.getGeocodeLocations();
		if (locations == null) {
			return Result.emptyResult();
		}
		
        Placemark location = null;
		try {
			Collection<Placemark> placemarks = locations.Placemark;
			Placemark[] arry = new Placemark[placemarks.size()];
			arry = placemarks.toArray(arry);
			location = arry[index];
		} catch (IndexOutOfBoundsException e) {
			return Result.emptyResult();
		}
		
		String value = getValue(location, dataRequested);
		if (value == null) {
			return Result.emptyResult();
		}
		
		return new Result(value);
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
	
	/**
	 * Returns the piece of metadata requested from the GISLocation object
	 * 
	 * @param location The Placemark object used for data retrieval.
	 * @param dataRequested The piece of metadata requested.
	 * @return
	 */
	private String getValue(Placemark location, String dataRequested) {
		if ("rank".equalsIgnoreCase(dataRequested)) {
			return String.valueOf(location.rank);
		} else if ("id".equalsIgnoreCase(dataRequested)) {
			return String.valueOf(location.ID);
		} else if ("orgName".equalsIgnoreCase(dataRequested)) {
			return location.OrgName;
		} else if ("distance".equalsIgnoreCase(dataRequested)) {
			if (location.Mi != null) {
				return String.valueOf(location.Mi);
			}
			
			return null;
		} else if ("street".equalsIgnoreCase(dataRequested)) {
			return location.stAdd;
		} else if ("city".equalsIgnoreCase(dataRequested)) {
			return location.City;
		} else if ("zip".equalsIgnoreCase(dataRequested)) {
			return location.Zip;
		} else if ("address".equalsIgnoreCase(dataRequested)) {
			return location.address;
		} else if ("category".equalsIgnoreCase(dataRequested)) {
			return location.Category;
		} else if ("type".equalsIgnoreCase(dataRequested)) {
			return location.Type;
		} else if ("cost".equalsIgnoreCase(dataRequested)) {
			return location.Cost;
		} else if ("costDetails".equalsIgnoreCase(dataRequested)) {
			return location.CostDetails;
		} else if ("latitude".equalsIgnoreCase(dataRequested)) {
			if (location.Latitude != null) {
				return String.valueOf(location.Latitude);
			}
			
			return null;
		} else if ("longitude".equalsIgnoreCase(dataRequested)) {
			if (location.Longitude != null) {
				return String.valueOf(location.Longitude);
			}
			
			return null;
		} else if ("phone".equals(dataRequested)) {
			return location.Phone;
		} else if ("description".equals(dataRequested)) {
			return location.Description;
		} else if ("ages".equals(dataRequested)) {
			return location.Ages;
		} else if ("times".equals(dataRequested)) {
			return location.Times;
		} else if ("webAddress".equals(dataRequested)) {
			return location.WebAdd;
		}
		
		return null;
	}
}
