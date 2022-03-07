/**
 * 
 */
package org.openmrs.module.chica.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.logic.result.Result;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
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
 */
@Controller
@RequestMapping(value = "module/chica/chicaNoteTester.form")
public class ChicaNoteTesterController {
	
	private static final Logger log = LoggerFactory.getLogger(ChicaNoteTesterController.class);
	
	private static final String PHYSICIAN_NOTE = "PhysicianNote";
	
	private static final String PRODUCE = "PRODUCE";
	
	private static final String MODE = "mode";
	
	/** Form view */
    private static final String FORM_VIEW = "/module/chica/chicaNoteTester";
    
    /** Parameters */
    private static final String PARAMETER_NOTE = "note";
	
	/**
     * Form initialization method.
     * 
     * @param request The HTTP request information
     * @param map The map to populate for return to the client
     * @return The form view name
     */
    @RequestMapping(method = RequestMethod.GET)
    protected String initForm(HttpServletRequest request, ModelMap map) {
		DssService dssService = Context.getService(DssService.class);
			
		String mrn = request.getParameter(ChirdlUtilConstants.PARAMETER_MRN);
		
		map.put(ChirdlUtilConstants.PARAMETER_LAST_MRN, mrn);
		
		if (mrn != null) {
			try {
				PatientService patientService = Context.getPatientService();
				// CHICA-1151 Add the same fix from CHICA-977 Use getPatientsByIdentifier() as a temporary solution to 
				// openmrs TRUNK-5089
				List<Patient> patients = patientService.getPatientsByIdentifier(null, mrn, null, true); 
				Patient patient = null;
				if (patients != null && !patients.isEmpty()) {
					patient = patients.get(0);
				}
				if (patient != null) {
					
					// Get the note
					Map<String, Object> ruleParams = new HashMap<>();
					ruleParams.put(MODE, PRODUCE);
					
					Rule rule = new Rule();
					rule.setTokenName(PHYSICIAN_NOTE);
					rule.setParameters(ruleParams);
					
					Result result = dssService.runRule(patient, rule);
					String note = result.toString();
					map.put(PARAMETER_NOTE, note);
				}
				
			}
			catch (Exception e) {
				log.error("Error intializing form for patient id {}", request.getParameter(ChirdlUtilConstants.PARAMETER_PATIENT_ID),e);
			}
		}
		
		return FORM_VIEW;
	}
	
}
