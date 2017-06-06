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
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.validator.PatientIdentifierValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;


@Controller
@RequestMapping(value = "module/chica/viewPatient.form")
public class ViewPatientController
{

	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	/** Form view */
	private static final String FORM = "viewPatient.form";
	private static final String FORM_VIEW = "/module/chica/viewPatient";
	
	/** Success form view */
	private static final String SUCCESS_FORM_VIEW = "viewEncounter.form";

	@RequestMapping(method = RequestMethod.GET)
	protected String initForm(HttpServletRequest request, ModelMap map) throws Exception
	{	
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
		
		return FORM_VIEW;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	protected ModelAndView processSubmit(HttpServletRequest request, HttpServletResponse response, Object obj) throws Exception {
		
		Map<String, Object> map = new HashMap<String, Object>();
		String mrn = request.getParameter(ChirdlUtilConstants.PARAMETER_MRN); // Changed this to "mrn" instead of "mrnLookup" this would cause a conflict when submitting the form between the parameter used to display errors and what was being submitted
		
		
		boolean valid = false;
		
		if (mrn != null)
		{
			mrn = Util.removeLeadingZeros(mrn); // CHICA-1052 Remove leading zeros
			if(!mrn.contains("-") && mrn.length() > 1){
				mrn = mrn.substring(0, mrn.length() - 1) + "-" + mrn.substring(mrn.length()-1);
			}
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
				return new ModelAndView(new RedirectView(FORM),map);
			}
			map.put("patientId", patientId);
			return new ModelAndView(new RedirectView(SUCCESS_FORM_VIEW),map);			
		}

		return new ModelAndView(new RedirectView(FORM), map);
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
