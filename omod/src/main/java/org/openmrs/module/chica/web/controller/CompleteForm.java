package org.openmrs.module.chica.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Form;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutilbackports.BaseStateActionHandler;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.module.dss.hibernateBeans.Rule;
import org.openmrs.module.dss.hibernateBeans.RuleEntry;
import org.openmrs.module.dss.service.DssService;

/**
 * Runs null priority rules for a patient/form and changes the state.
 *
 * @author Steve McKee
 */
public class CompleteForm implements Runnable {
    private Log log = LogFactory.getLog(this.getClass());
    private Integer patientId;
    private Integer formId;
    private Map<String, Object> parameters;
    private FormInstance formInstance;

    /**
     * Constructor method
     * 
     * @param patientId Patient identifier
     * @param formId Form identifier
     * @param parameters Map of parameters for the rule execution
     * @param formInstance The instance of the form
     */
    public CompleteForm(Integer patientId, Integer formId, Map<String, Object> parameters, 
                                  FormInstance formInstance) {
        this.patientId = patientId;
        this.formId = formId;
        this.parameters = parameters;
        this.formInstance = formInstance;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        try {
            Patient patient = Context.getPatientService().getPatient(this.patientId);
            Form form = Context.getFormService().getForm(this.formId);

            DssService dssService = Context.getService(DssService.class);
            List<RuleEntry> nonPriorRuleEntries = dssService.getNonPrioritizedRuleEntries(form.getName());
            
            for (RuleEntry currRuleEntry : nonPriorRuleEntries) {
                Rule currRule = currRuleEntry.getRule();
                if (currRule.checkAgeRestrictions(patient)) {
                    currRule.setParameters(this.parameters);
                    dssService.runRule(patient, currRule);
                }
            }
        } catch (Exception e) {
            this.log.error(e.getMessage());
            this.log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
        } 
    
        try {
            changeState(this.formInstance, this.parameters);
        } catch (Exception e) {
            this.log.error(e.getMessage());
            this.log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
        }
    }
    
    /**
     * Changes to the next state in the state flow process.
     * 
     * @param formInstanceToChange The FormInstance object containing relevant form information.
     * @param stateChangeParameters Map containing parameters needed for the rules to execute.
     */
    private void changeState(FormInstance formInstanceToChange, Map<String, Object> stateChangeParameters) {
        ChirdlUtilBackportsService service = Context.getService(ChirdlUtilBackportsService.class);
        List<PatientState> states = service.getPatientStatesByFormInstance(formInstanceToChange, false);
        if (states != null && !states.isEmpty()) {
            for (PatientState formInstState : states) {
                
                // only process unfinished states for this sessionId
                if (formInstState.getEndTime() != null) {
                    continue;
                }
                
                try {
                    BaseStateActionHandler.getInstance().changeState(formInstState, (HashMap)stateChangeParameters);
                }
                catch (Exception e) {
                    this.log.error(e.getMessage());
                    this.log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
                }
            }
        }
    }
}