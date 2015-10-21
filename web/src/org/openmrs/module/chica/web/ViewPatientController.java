package org.openmrs.module.chica.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientIdentifierException;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.validator.PatientIdentifierValidator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;



public class ViewPatientController extends SimpleFormController
{

	/** Logger for this class and subclasses */
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
		Patient patient = new Patient();
		return patient;
	}

	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception
	{
		Map<String, Object> map = new HashMap<String, Object>();
		
		String mrn = request.getParameter("mrnLookup");
		String error = request.getParameter("error");
		if (error != null && error.equalsIgnoreCase("notExists") ){
			map.put("error", error);
		}
		else {
			map.put("error", "");
		}
		
		if (mrn != null){
			map.put("mrnLookup", mrn);
			map.put("validate", "valid");
		} 
		
		String validity = (String) request.getAttribute("validate");

		return map;
	}
	
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object obj, org.springframework.validation.BindException errors) throws Exception {
		
		Map<String, Object> map = new HashMap<String, Object>();
		String mrn = request.getParameter("mrnLookup");
		
		
		boolean valid = false;
		if (mrn != null && !mrn.contains("-") && mrn.length() > 1)
		{
			mrn = mrn.substring(0, mrn.length() - 1) + "-" + mrn.substring(mrn.length()-1);
		
		}
		map.put("mrnLookup", mrn);
		
		try {
			if (mrn!= null && mrn.length()> 0 && !mrn.endsWith("-")){
				PatientIdentifierType mrnIdent = 
						Context.getPatientService().getPatientIdentifierTypeByName(ChirdlUtilConstants.IDENTIFIER_TYPE_MRN);
				try {
					PatientIdentifierValidator.validateIdentifier(mrn, mrnIdent);
					valid = true;
				}  catch(PatientIdentifierException e) {
				}
			}
		} catch (Exception e) {
			log.error("Error validating MRN: " + mrn);
		}
		
		if (valid){
			
			String patientId = getPatientIdFromMRN(mrn);
			if (patientId == null || patientId.isEmpty()){
				map.put("error", "notExists");
				return new ModelAndView(new RedirectView("viewPatient.form"),map);
			}
			map.put("patientId", patientId);
			return new ModelAndView(new RedirectView("viewEncounter.form"),map);

			
		}

		return showForm(request, response, errors);
	}
	
	private String getPatientIdFromMRN(String mrn){
		  String patientid = "";
		  PatientService ps = Context.getPatientService();
		  List<Patient> patientList = ps.getPatients(mrn);
		  if (patientList != null && patientList.size() > 0)
		  {
			  Patient patient = patientList.get(0);
			  if (patient != null){
				  Integer id = patient.getPatientId();
				  if (id != null) patientid = id.toString();
			  }
		  }
		
		  return patientid;
	  }

	
	
}
