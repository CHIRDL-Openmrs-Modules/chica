/**
 * 
 */
package org.openmrs.module.chica.web.controller;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.result.Result;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.datasource.ObsInMemoryDatasource;
import org.openmrs.module.dss.hibernateBeans.Rule;
import org.openmrs.module.dss.service.DssService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author tmdugan
 * 
 */
@Controller
@RequestMapping(value = "module/chica/chicaRuleTester.form")
public class ChicaRuleTesterController
{
    

    /** Form view */
    private static final String FORM_VIEW = "/module/chica/chicaRuleTester";

    /** Logger for this class and subclasses */

    private static final Logger log = LoggerFactory.getLogger(ChicaRuleTesterController.class);
	
	/** Parameters */
	private static final String PARAMETER_RULE_NAME = "ruleName";
	private static final String PARAMETER_RULES = "rules";
	private static final String PARAMETER_LAST_RULE_NAME = "lastRuleName";
    private static final String PARAMETER_RUN_RESULT = "runResult";
    
    private static final String DOUBLE_BREAK = "<br/><br/>";
    private static final String TOKEN_NAME = "tokenName";

	/**
     * Form initialization method.
     * 
     * @param request The HTTP request information
     * @param map The map to populate for return to the client
     * @return The form view name
     */
    @RequestMapping(method = RequestMethod.GET)
    protected String initForm(HttpServletRequest request, ModelMap map)
	{
		DssService dssService = Context
				.getService(DssService.class);

		String ruleName = request.getParameter(PARAMETER_RULE_NAME);
		String mrn = request.getParameter(ChirdlUtilConstants.PARAMETER_MRN);

		map.put(ChirdlUtilConstants.PARAMETER_LAST_MRN, mrn);

		if (mrn != null)
		{
			runRule(dssService, ruleName, mrn, map);
		}
		if (ruleName != null && ruleName.length() > 0)
		{
			map.put(PARAMETER_LAST_RULE_NAME, ruleName);
		}

		List<Rule> rules = dssService.getRules(new Rule(), true, true, TOKEN_NAME);

		map.put(PARAMETER_RULES, rules);

		return FORM_VIEW;
	}

    /**
     * Runs the provided rule for the provided patient.
     * 
     * @param dssService DSS service used for obtaining rule information
     * @param ruleName The name of the rule to execute
     * @param mrn The MRN of the patient to run the rule against
     * @param map The model map to populate for client use
     */
    private void runRule(DssService dssService, String ruleName, String mrn, ModelMap map) 
    {
        try
        {
            PatientService patientService = Context.getPatientService();
            // CHICA-1151 Add the same fix from CHICA-977 Use getPatientsByIdentifier() as a temporary solution to 
            // openmrs TRUNK-5089
            List<Patient> patients = patientService.getPatientsByIdentifier(null, mrn, null, true); 
            Patient patient = null;
            Integer patientId = null;
            if (patients != null && !patients.isEmpty())
            {
                patient = patients.get(0);
                patientId = patient.getPatientId();
            }
            if (patient != null)
            {
                HashMap<String, Object> parameters = new HashMap<>();
                parameters.put(ChirdlUtilConstants.PARAMETER_MODE, ChirdlUtilConstants.PARAMETER_VALUE_PRODUCE);
                Rule currRule = dssService.getRule(ruleName);
                if (currRule != null && currRule.checkAgeRestrictions(patient))
                {
                    currRule.setParameters(parameters);
                    // query and add to
                    // datasource
                                            
                    Result result = dssService.runRule(patient, currRule);
                    
                    LogicService logicService = Context.getLogicService();
                    ObsInMemoryDatasource xmlDatasource = (ObsInMemoryDatasource) logicService
                        .getLogicDataSource(ChirdlUtilConstants.DATA_SOURCE_IN_MEMORY);
                    //purge these obs from the datasource
                    xmlDatasource.deleteObsByPatientId(patientId);

                    if (result.size() < 2)
                    {
                        map.put(PARAMETER_RUN_RESULT, result.toString());
                    } else
                    {
                        StringBuilder resultString = new StringBuilder();
                        for (Result currResult : result)
                        {
                            resultString.append(currResult.toString());
                            resultString.append(DOUBLE_BREAK);
                        }
                        map.put(PARAMETER_RUN_RESULT, resultString.toString());
                    }
                }
            }

        } catch (Exception e)
        {
            log.error("Exception running rule {}",ruleName,e);
        }
    }
}
