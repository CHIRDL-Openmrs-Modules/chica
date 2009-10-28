package org.openmrs.module.chica.rule;

import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Form;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicCriteria;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.Rule;
import org.openmrs.logic.op.Operator;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.atd.hibernateBeans.FormInstance;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.util.Util;
import org.openmrs.module.dss.util.IOUtil;
import org.openmrs.module.atd.hibernateBeans.PatientState;
import org.openmrs.module.atd.hibernateBeans.State;

public class CREATE_JIT implements Rule
{
	private Log log = LogFactory.getLog(this.getClass());
	private LogicService logicService = Context.getLogicService();

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getParameterList()
	 */
	public Set<RuleParameterInfo> getParameterList()
	{
		return null;
	}

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getDependencies()
	 */
	public String[] getDependencies()
	{
		return new String[]
		{};
	}

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getTTL()
	 */
	public int getTTL()
	{
		return 0; // 60 * 30; // 30 minutes
	}

	/**
	 * *
	 * 
	 * @see org.openmrs.logic.rule.Rule#getDatatype(String)
	 */
	public Datatype getDefaultDatatype()
	{
		return Datatype.CODED;
	}

	public Result eval(LogicContext context, Patient patient,
			Map<String, Object> parameters) throws LogicException
	{
		ATDService atdService = (ATDService) Context.getService(ATDService.class);
		FormService formService = Context.getFormService();
		String formName = (String) parameters.get("param1");
		Form form = formService.getForms(formName,null,null,false,null,null,null).get(0);
		Integer formId = form.getFormId();
		Integer sessionId = (Integer) parameters.get("sessionId");
		Integer locationTagId = (Integer) parameters.get("locationTagId"); 
		FormInstance formInstance = (FormInstance) parameters.get("formInstance");
		//we don't know the formInstanceId yet because the JIT hasn't been created
		formInstance = new FormInstance(formInstance.getLocationId(),formId,null);

		Integer locationId = formInstance.getLocationId();
		if(sessionId != null){
			State currState = atdService.getStateByName("JIT");
			PatientState patientState = atdService.addPatientState(patient, currState, sessionId, locationTagId,locationId);
			patientState.setFormInstance(formInstance);
			atdService.updatePatientState(patientState);
		}	
		return Result.emptyResult();
	}
}