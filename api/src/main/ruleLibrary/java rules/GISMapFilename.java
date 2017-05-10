package org.openmrs.module.chica.rule;

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
import org.openmrs.module.chica.gis.PatientGISData;
import org.openmrs.module.chica.gis.PatientGISDataStorage;

/**
 * Retrieves the filename for a given patient and map type.
 */
public class GISMapFilename implements Rule {
	
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
		
		PatientGISData data = PatientGISDataStorage.getPatientGISData(patientId, mapType);
		if (data == null) {
			return Result.emptyResult();
		}
		
		String mapFileStr = data.getMapLocation();
		if (mapFileStr == null) {
			return Result.emptyResult();
		}
		
		return new Result(mapFileStr);
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
