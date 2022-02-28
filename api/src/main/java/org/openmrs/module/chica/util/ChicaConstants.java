/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.chica.util;

/**
 * Constants class for chica modules specific items.
 * 
 * @author Steve McKee
 */
public class ChicaConstants {
	
    /** Form views */
	public static final String FORM_VIEW_EXTERNAL_FORM_LOADER = "/module/chica/externalFormLoader";
	public static final String FORM_VIEW_GREASE_BOARD = "/module/chica/greaseBoard";
	
	/** Parameters used in CHICA */
	public static final String PARAMETER_RIGHT_FORM_LOCATION_ID = "rightFormLocationId";
	public static final String PARAMETER_RIGHT_FORM_FORM_ID = "rightFormFormId";
	public static final String PARAMETER_RIGHT_FORM_FORM_INSTANCE_ID = "rightFormFormInstanceId";
	public static final String PARAMETER_RIGHT_FORM_STYLESHEET = "rightFormStylesheet";
	public static final String PARAMETER_RIGHT_FORM_DIRECTORY = "rightFormDirectory";
	public static final String PARAMETER_LEFT_FORM_LOCATION_ID = "leftFormLocationId";
	public static final String PARAMETER_LEFT_FORM_FORM_ID = "leftFormFormId";
	public static final String PARAMETER_LEFT_FORM_FORM_INSTANCE_ID = "leftFormFormInstanceId";
	public static final String PARAMETER_LEFT_FORM_STYLESHEET = "leftFormStylesheet";
	public static final String PARAMETER_LEFT_FORM_DIRECTORY = "leftFormDirectory";
    public static final String PARAMETER_LANGUAGE = "language";
    public static final String PARAMETER_USER_QUIT_FORM = "userQuitForm";
    public static final String PARAMETER_ERROR_PREVIOUS_SUBMISSION = "errorPreviousSubmission";
    public static final String PARAMETER_SESSION_TIMEOUT_WARNING = "sessionTimeoutWarning";
    
    /** Session attributes */
    public static final String SESSION_ATTRIBUTE_SUBMITTED_FORM_INSTANCES = "submittedFormInstances";
    
    /** Form concepts */
    public static final String PROVIDER_VIEW = "_provider_view";
    public static final String PROVIDER_SUBMIT = "_provider_submit";
    
    /** Rule names **/
    public static final String RULE_NAME_DEPRESSION_SUICIDE_PWS = "Depression_SuicidePWS";
    public static final String RULE_NAME_BF_SUICIDE_PWS = "bf_suicide_PWS";
    public static final String RULE_NAME_ABUSE_CONCERN_PWS = "Abuse_Concern_PWS";
    public static final String RULE_NAME_DOM_VIOL_PWS = "Dom_Viol_PWS";

    /**Concepts**/;
    public static final String CONCEPT_SUICIDE_CONCERNS =  "suicide_concerns";
}
