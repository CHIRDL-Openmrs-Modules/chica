package org.openmrs.module.chica.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.patient.impl.LuhnIdentifierValidator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

public class ForcePrintSSNMRNController extends SimpleFormController {
	
	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request) throws Exception {
		return "testing";
	}
	
	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		
		String mrn = request.getParameter("mrnLookup");
		
		if (mrn != null) {
			map.put("mrnLookup", mrn);
			map.put("validate", "valid");
		}
		
		return map;
	}
	
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object obj,
	                                org.springframework.validation.BindException errors) throws Exception {
		
		HttpSession httpSession = request.getSession();
		
		String mrn = request.getParameter("mrnLookup");
		
		LuhnIdentifierValidator luhn = new LuhnIdentifierValidator();
		boolean valid = false;
		if (mrn != null && !mrn.contains("-") && mrn.length() > 1) {
			mrn = mrn.substring(0, mrn.length() - 1) + "-" + mrn.substring(mrn.length() - 1);
			
		}
		
		try {
			if (mrn != null && mrn.length() > 0 && !mrn.endsWith("-")) {
				valid = luhn.isValid(mrn);
			}
		}
		catch (Exception e) {
			log.error("Error validating MRN: " + mrn);
		}
		
		if (valid) {
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("mrnLookup", mrn);
			PatientService patientService = Context.getPatientService();
			List<Patient> patients = patientService.getPatients(null, mrn, null, true);
			
			Integer patientId = null;
			
			if (patients != null && patients.size() > 0) {
				Patient patient = patients.get(0);
				patientId = patient.getPatientId();
				EncounterService encounterService = Context.getEncounterService();
				List<org.openmrs.Encounter> list = encounterService.getEncountersByPatientId(patientId);
				if (list != null && list.size() > 0) {
					Encounter encounter = list.get(0);
					ChirdlUtilBackportsService chirdlUtilBackportsService = Context
					        .getService(ChirdlUtilBackportsService.class);
					State checkinState = chirdlUtilBackportsService.getStateByName("CHECKIN");
					Integer encounterId = encounter.getEncounterId();
					List<PatientState> checkinStates = chirdlUtilBackportsService.getPatientStateByEncounterState(
					    encounterId, checkinState.getStateId());
					if (checkinStates != null && checkinStates.size() > 0) {
						PatientState patientState = checkinStates.get(0);
						Integer sessionId = patientState.getSessionId();
						model.put("sessionId", sessionId);					
						model.put("patientId", patientId);
						return new ModelAndView(new RedirectView(getSuccessView()), model);	
					}
				}
			}
		}
		
		return showForm(request, response, errors);
	}
	
}
