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
import org.openmrs.logic.result.Result;
import org.openmrs.module.dss.hibernateBeans.Rule;
import org.openmrs.module.dss.service.DssService;
import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 * @author tmdugan
 */
public class ChicaNoteTesterController extends SimpleFormController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	private static final String PHYSICIAN_NOTE = "PhysicianNote";
	
	private static final String PRODUCE = "PRODUCE";
	
	private static final String MODE = "mode";
	
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
		DssService dssService = Context.getService(DssService.class);
			
		String mrn = request.getParameter("mrn");
		
		map.put("lastMRN", mrn);
		
		if (mrn != null) {
			try {
				PatientService patientService = Context.getPatientService();
				List<Patient> patients = patientService.getPatientsByIdentifier(mrn, false);
				Patient patient = null;
				Integer patientId = null;
				if (patients != null && patients.size() > 0) {
					patient = patients.get(0);
					patientId = patient.getPatientId();
				}
				if (patient != null) {
					
					// Get the note
					Map<String, Object> ruleParams = new HashMap<String, Object>();
					ruleParams.put(MODE, PRODUCE);
					
					Rule rule = new Rule();
					rule.setTokenName(PHYSICIAN_NOTE);
					rule.setParameters(ruleParams);
					
					Result result = dssService.runRule(patient, rule);
					String note = result.toString();
					map.put("note", note);
				}
				
			}
			catch (Exception e) {
				this.log.error(e.getMessage());
				this.log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
			}
		}
		
		return map;
	}
	
}
