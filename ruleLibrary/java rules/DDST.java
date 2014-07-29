package org.openmrs.module.chica.rule;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;

public class DDST implements Rule
{
	private Log log = LogFactory.getLog(this.getClass());

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

	public Result eval(LogicContext context, Integer patientId,
			Map<String, Object> parameters) throws LogicException
	{
		PatientService patientService = Context.getPatientService();
		Patient patient = patientService.getPatient(patientId);
		ChicaService chicaService = (ChicaService) Context
				.getService(ChicaService.class);
		FormInstance formInstance = null;

		if (parameters != null)
		{
			formInstance = (FormInstance) parameters.get("formInstance");
			String category = (String) parameters.get("param1");
			PatientState patientState = org.openmrs.module.atd.util.Util.getProducePatientStateByFormInstanceAction(
				formInstance);

			if (patientState != null)
			{
				Date formPrintedTime = patientState.getStartTime();
				
				if (formPrintedTime != null)
				{
					try
					{
						Integer ageInDays = getAgeInDays(formPrintedTime,
								context, patient);
						if (ageInDays != null)
						{
							String milestone = chicaService.getDDSTLeaf(
									category, ageInDays);
							return new Result(milestone);
						}
					} catch (Exception e)
					{
						log.error(org.openmrs.module.chirdlutil.util.Util
								.getStackTrace(e));
					}
				}
			}
		}

		return Result.emptyResult();

	}

	private Integer getAgeInDays(Date formPrintedTime,
			LogicContext context,Patient patient)
	{
		try{
		Date birthdate = context.read(patient.getPatientId(),
				context.getLogicDataSource("person"), "BIRTHDATE").toDatetime();

		if (birthdate == null || formPrintedTime == null)
		{
			return null;
		}

		Calendar bdate = Calendar.getInstance();
		bdate.setTime(birthdate);

		Calendar now = Calendar.getInstance();
		now.setTime(formPrintedTime);

		return org.openmrs.module.chirdlutil.util.Util.getAgeInUnits(birthdate, now
				.getTime(), Util.DAY_ABBR);
		}catch(Exception e){
			log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}
		return null;
	}
}