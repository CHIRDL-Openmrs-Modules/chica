/**
 * 
 */
package org.openmrs.module.chica.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicService;
import org.openmrs.logic.result.Result;
import org.openmrs.module.chica.QueryKite;
import org.openmrs.module.chirdlutilbackports.datasource.ObsInMemoryDatasource;
import org.openmrs.module.dss.hibernateBeans.Rule;
import org.openmrs.module.dss.service.DssService;
import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 * @author tmdugan
 * 
 */
public class ChicaRuleTesterController extends SimpleFormController
{

	protected final Log log = LogFactory.getLog(getClass());

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception
	{
		return "testing";
	}

	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception
	{
		Map<String, Object> map = new HashMap<String, Object>();
		DssService dssService = Context
				.getService(DssService.class);

		String mode = "PRODUCE";

		String ruleName = request.getParameter("ruleName");
		String mrn = request.getParameter("mrn");

		map.put("lastMRN", mrn);

		if (mrn != null)
		{
			try
			{
				PatientService patientService = Context.getPatientService();
				List<Patient> patients = patientService
						.getPatientsByIdentifier(mrn, false);
				Patient patient = null;
				Integer patientId = null;
				if (patients != null && patients.size() > 0)
				{
					patient = patients.get(0);
					patientId = patient.getPatientId();
				}
				if (patient != null)
				{
					HashMap<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("mode", mode);
					Rule rule = new Rule();
					rule.setTokenName(ruleName);
					List<Rule> rules = dssService.getRules(rule, false, false,
							null);
					Rule currRule = null;
					if (rules.size() > 0)
					{
						currRule = rules.get(0);
					}

					if (currRule != null
							&& currRule.checkAgeRestrictions(patient))
					{
						currRule.setParameters(parameters);
						try
						{
							QueryKite.mrfQuery(mrn, patient,false);
						} catch (Exception e)
						{
							//ignore the error if the kite query fails
							//since this is just testing rules
						}
						// query and add to
						// datasource
												
						Result result = dssService.runRule(patient, currRule);
						
						LogicService logicService = Context.getLogicService();
						ObsInMemoryDatasource xmlDatasource = (ObsInMemoryDatasource) logicService
							.getLogicDataSource("RMRS");
						//purge these obs from the datasource
						xmlDatasource.deleteObsByPatientId(patientId);

						if (result.size() < 2)
						{
							map.put("runResult", result.toString());
						} else
						{
							String resultString = "";
							for (Result currResult : result)
							{
								resultString += currResult.toString();
								resultString += "<br/><br/>";
							}
							map.put("runResult", resultString);
						}
					}
				}

			} catch (Exception e)
			{
				this.log.error(e.getMessage());
				this.log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
			}
		}
		if (ruleName != null && ruleName.length() > 0)
		{
			map.put("lastRuleName", ruleName);
		}

		List<Rule> rules = dssService.getRules(new Rule(), true, true,
				"tokenName");

		map.put("rules", rules);

		return map;
	}

}
