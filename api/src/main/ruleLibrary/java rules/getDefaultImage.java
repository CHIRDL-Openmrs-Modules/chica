package org.openmrs.module.chica.rule;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmrs.api.context.Context;
import org.openmrs.logic.Duration;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.impl.LogicCriteriaImpl;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;

public class getDefaultImage implements Rule {
	
	private static final Logger log = LoggerFactory.getLogger(getDefaultImage.class);
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getParameterList()
	 */
	public Set<RuleParameterInfo> getParameterList() {
		return null;
	}
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getDependencies()
	 */
	public String[] getDependencies() {
		return new String[] {};
	}
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getTTL()
	 */
	public int getTTL() {
		return 0; // 60 * 30; // 30 minutes
	}
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getDatatype(String)
	 */
	public Datatype getDefaultDatatype() {
		return Datatype.CODED;
	}
	
	/**
	 * *
	 * 
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer, java.util.Map)
	 */
	public Result eval(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		String imageType  = (String) parameters.get("param1");
		if (imageType == null) {
			log.error("No imageType variable specified");
			return Result.emptyResult();
		}
		
		String defaultImageDirectory = Context.getAdministrationService().getGlobalProperty("atd.defaultTifImageDirectory");
		if (defaultImageDirectory == null) {
			log.error("Value not specified for global property atd.defaultTifImageDirectory");
			return Result.emptyResult();
		}
		
		if ("atSymbol".equalsIgnoreCase(imageType)) {
			return new Result(defaultImageDirectory + "AtSymbol.jpg");
		}
		
		return Result.emptyResult();
	}
}
