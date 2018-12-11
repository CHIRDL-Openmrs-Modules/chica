package org.openmrs.module.chica.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chirdlutil.util.IdentifierDateComparator;

public class SIIDIdentifier implements Rule
{

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
		
		PatientIdentifierType siisIdentifierType = 
			patientService.getPatientIdentifierTypeByName("Immunization Registry");
		
		if (siisIdentifierType == null){
			return Result.emptyResult();
		}
		
		List<PatientIdentifierType>  typeList = new ArrayList<PatientIdentifierType>();
		typeList.add(siisIdentifierType);
		List<Patient> patients = new ArrayList<Patient>();
		patients.add(patient);
		
		List<PatientIdentifier> identifiers = 
			patientService.getPatientIdentifiers(null, typeList, null, patients, null);
		
		if (identifiers == null || identifiers.size()== 0){
			return Result.emptyResult();
		}
			//we want dates descending order to get the most recent date first
			Collections.sort(identifiers, new IdentifierDateComparator() );
				
		//Collections.sort(identifiers);
		PatientIdentifier first = identifiers.get(0);
		if (first != null){
			return new Result(first.getIdentifier());
		}
		
		return Result.emptyResult();
	}
	
	
}