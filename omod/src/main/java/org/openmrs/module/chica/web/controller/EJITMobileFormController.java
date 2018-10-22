package org.openmrs.module.chica.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Mobile Controller for EJITs.
 *
 * @author Steve McKee
 */
@Controller
public class EJITMobileFormController {
    
    /** Form views */
    private static final String FORM_VIEW_PQH9_MOBILE = "/module/chica/phq9Mobile";
    private static final String FORM_VIEW_CRAFFT_MOBILE = "/module/chica/CRAFFTMobile";
    private static final String FORM_VIEW_SEX_RISK_MOBILE = "/module/chica/sexRiskMobile";
    private static final String FORM_VIEW_MCHAT_MOBILE = "/module/chica/MCHATMobile";
    private static final String FORM_VIEW_SCARED_PARENT_MOBILE = "/module/chica/SCAREDParentMobile";
    private static final String FORM_VIEW_ISQ_MOBILE = "/module/chica/ISQMobile";
    private static final String FORM_VIEW_ADDITIONAL_INFORMATION_MOBILE = "/module/chica/additionalInformationMobile";
    private static final String FORM_VIEW_PSQ_MOBILE = "/module/chica/PSQMobile";
    private static final String FORM_VIEW_MCHATR_MOBILE = "/module/chica/MCHATRMobile";
    private static final String FORM_VIEW_TRAQ_MOBILE = "/module/chica/TRAQMobile";
    private static final String FORM_VIEW_DIABETES_HISTORY = "/module/chica/diabetesHistory";
    private static final String FORM_VIEW_PARENT_PSYCHOSOCIAL = "/module/chica/parentPsychosocial";
    private static final String FORM_VIEW_EATING_DISORDER_MOBILE = "/module/chica/EatingDisorderMobile";
    private static final String FORM_VIEW_INSULIN_DOSING_MEDICATIONS = "/module/chica/insulinDosingMedications";
    private static final String FORM_VIEW_SUDEP_MOBILE = "/module/chica/SUDEPMobile";
    
    /**Success view */
    private static final String SUCCESS_VIEW = "finishFormsMobile.form";
    
    /**
     * Form initialization method.
     * 
     * @param request The HTTP request information
     * @param map The map to populate for return to the client
     * @return The form view name
     */
    @RequestMapping(value = "module/chica/phq9Mobile.form", method = RequestMethod.GET)
    protected String initPHQ9Form(HttpServletRequest request, ModelMap map) {
        return MobileFormControllerUtil.loadMobileFormInformation(request, map, FORM_VIEW_PQH9_MOBILE);
    }
    
    /**
     * Form initialization method.
     * 
     * @param request The HTTP request information
     * @param map The map to populate for return to the client
     * @return The form view name
     */
    @RequestMapping(value = "module/chica/CRAFFTMobile.form", method = RequestMethod.GET)
    protected String initCRAFFTForm(HttpServletRequest request, ModelMap map) {
        return MobileFormControllerUtil.loadMobileFormInformation(request, map, FORM_VIEW_CRAFFT_MOBILE);
    }
    
    /**
     * Form initialization method.
     * 
     * @param request The HTTP request information
     * @param map The map to populate for return to the client
     * @return The form view name
     */
    @RequestMapping(value = "module/chica/MCHATMobile.form", method = RequestMethod.GET)
    protected String initMCHATForm(HttpServletRequest request, ModelMap map) {
        return MobileFormControllerUtil.loadMobileFormInformation(request, map, FORM_VIEW_MCHAT_MOBILE);
    }
    
    /**
     * Form initialization method.
     * 
     * @param request The HTTP request information
     * @param map The map to populate for return to the client
     * @return The form view name
     */
    @RequestMapping(value = "module/chica/sexRiskMobile.form", method = RequestMethod.GET)
    protected String initSexRiskForm(HttpServletRequest request, ModelMap map) {
        return MobileFormControllerUtil.loadMobileFormInformation(request, map, FORM_VIEW_SEX_RISK_MOBILE);
    }
    
    /**
     * Form initialization method.
     * 
     * @param request The HTTP request information
     * @param map The map to populate for return to the client
     * @return The form view name
     */
    @RequestMapping(value = "module/chica/SCAREDParentMobile.form", method = RequestMethod.GET)
    protected String initSCAREDParentForm(HttpServletRequest request, ModelMap map) {
        return MobileFormControllerUtil.loadMobileFormInformation(request, map, FORM_VIEW_SCARED_PARENT_MOBILE);
    }
    
    /**
     * Form initialization method.
     * 
     * @param request The HTTP request information
     * @param map The map to populate for return to the client
     * @return The form view name
     */
    @RequestMapping(value = "module/chica/ISQMobile.form", method = RequestMethod.GET)
    protected String initISQForm(HttpServletRequest request, ModelMap map) {
        return MobileFormControllerUtil.loadMobileFormInformation(request, map, FORM_VIEW_ISQ_MOBILE);
    }
    
    /**
     * Form initialization method.
     * 
     * @param request The HTTP request information
     * @param map The map to populate for return to the client
     * @return The form view name
     */
    @RequestMapping(value = "module/chica/additionalInformationMobile.form", method = RequestMethod.GET)
    protected String initAdditionalInformationForm(HttpServletRequest request, ModelMap map) {
        return MobileFormControllerUtil.loadMobileFormInformation(request, map, FORM_VIEW_ADDITIONAL_INFORMATION_MOBILE);
    }
    
    /**
     * Form initialization method.
     * 
     * @param request The HTTP request information
     * @param map The map to populate for return to the client
     * @return The form view name
     */
    @RequestMapping(value = "module/chica/PSQMobile.form", method = RequestMethod.GET)
    protected String initPSQForm(HttpServletRequest request, ModelMap map) {
        return MobileFormControllerUtil.loadMobileFormInformation(request, map, FORM_VIEW_PSQ_MOBILE);
    }
    
    /**
     * Form initialization method.
     * 
     * @param request The HTTP request information
     * @param map The map to populate for return to the client
     * @return The form view name
     */
    @RequestMapping(value = "module/chica/MCHATRMobile.form", method = RequestMethod.GET)
    protected String initMCHATRForm(HttpServletRequest request, ModelMap map) {
        return MobileFormControllerUtil.loadMobileFormInformation(request, map, FORM_VIEW_MCHATR_MOBILE);
    }
    
    /**
     * Form initialization method.
     * 
     * @param request The HTTP request information
     * @param map The map to populate for return to the client
     * @return The form view name
     */
    @RequestMapping(value = "module/chica/TRAQMobile.form", method = RequestMethod.GET)
    protected String initTRAQForm(HttpServletRequest request, ModelMap map) {
        return MobileFormControllerUtil.loadMobileFormInformation(request, map, FORM_VIEW_TRAQ_MOBILE);
    }
    
    /**
     * Form initialization method.
     * 
     * @param request The HTTP request information
     * @param map The map to populate for return to the client
     * @return The form view name
     */
    @RequestMapping(value = "module/chica/diabetesHistory.form", method = RequestMethod.GET)
    protected String initDiabetesHistoryForm(HttpServletRequest request, ModelMap map) {
        return MobileFormControllerUtil.loadMobileFormInformation(request, map, FORM_VIEW_DIABETES_HISTORY);
    }
    
    /**
     * Form initialization method.
     * 
     * @param request The HTTP request information
     * @param map The map to populate for return to the client
     * @return The form view name
     */
    @RequestMapping(value = "module/chica/parentPsychosocial.form", method = RequestMethod.GET)
    protected String initParentPsychosocialForm(HttpServletRequest request, ModelMap map) {
        return MobileFormControllerUtil.loadMobileFormInformation(request, map, FORM_VIEW_PARENT_PSYCHOSOCIAL);
    }
    
    /**
     * Form initialization method.
     * 
     * @param request The HTTP request information
     * @param map The map to populate for return to the client
     * @return The form view name
     */
    @RequestMapping(value = "module/chica/EatingDisorderMobile.form", method = RequestMethod.GET)
    protected String initEatingDisorderForm(HttpServletRequest request, ModelMap map) {
        return MobileFormControllerUtil.loadMobileFormInformation(request, map, FORM_VIEW_EATING_DISORDER_MOBILE);
    }
    
    /**
     * Form initialization method.
     * 
     * @param request The HTTP request information
     * @param map The map to populate for return to the client
     * @return The form view name
     */
    @RequestMapping(value = "module/chica/insulinDosingMedications.form", method = RequestMethod.GET)
    protected String initInsulinDosingMedicationsForm(HttpServletRequest request, ModelMap map) {
        return MobileFormControllerUtil.loadMobileFormInformation(request, map, FORM_VIEW_INSULIN_DOSING_MEDICATIONS);
    }
    
    /**
     * Form initialization method.
     * 
     * @param request The HTTP request information
     * @param map The map to populate for return to the client
     * @return The form view name
     */
    @RequestMapping(value = "module/chica/SUDEPMobile.form", method = RequestMethod.GET)
    protected String initSUDEPForm(HttpServletRequest request, ModelMap map) {
        return MobileFormControllerUtil.loadMobileFormInformation(request, map, FORM_VIEW_SUDEP_MOBILE);
    }
    
    /**
     * Handles submission of the page.
     * 
     * @param request The HTTP request information
     * @return The name of the next view
     */
    @RequestMapping(method = RequestMethod.POST)
    protected ModelAndView processSubmit(HttpServletRequest request) {
        return MobileFormControllerUtil.handleMobileFormSubmission(request, SUCCESS_VIEW);
    }
}
