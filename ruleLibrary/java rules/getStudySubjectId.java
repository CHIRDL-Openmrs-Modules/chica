package org.openmrs.module.chica.rule;

import java.util.Map;
import java.util.Set;

import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.LogicException;
import org.openmrs.logic.Rule;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.result.Result.Datatype;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.chica.hibernateBeans.Study;
import org.openmrs.module.chica.hibernateBeans.StudySubject;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;

/**
 * Get this existing or new subject ID for a patient based on study.
 *
 * @author Steve McKee
 */
public class getStudySubjectId implements Rule {
	
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
	 * @see org.openmrs.logic.Rule#eval(org.openmrs.logic.LogicContext, java.lang.Integer, java.util.Map)
	 */
	public Result eval(LogicContext context, Integer patientId, Map<String, Object> parameters) throws LogicException {
		if (parameters == null) {
			return Result.emptyResult();
		}
		
		String studyTitle = (String)parameters.get(ChirdlUtilConstants.PARAMETER_1);
		if (studyTitle == null) {
			return Result.emptyResult();
		}
		
		PatientService patientService = Context.getPatientService();
		Patient patient = patientService.getPatient(patientId);
		ChicaService chicaService = Context.getService(ChicaService.class);
		Study study = chicaService.getStudyByTitle(studyTitle);
		if (study == null) {
			return Result.emptyResult();
		}
		
		StudySubject subject = chicaService.getStudySubject(patient, study);
		if (subject == null) {
			return Result.emptyResult();
		}
		
		return new Result(subject.getSubjectId());
	}
}
