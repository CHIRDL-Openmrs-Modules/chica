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

import org.openmrs.module.chica.ImmunizationQueryOutput;


/**
 * Constants class for chica modules specific items.
 * 
 * @author Steve McKee
 */
public class ChicaConstants {
	
	/*
	 * Constants for cache
	 */
	public static final String CACHE_IMMUNIZATION = "immunization";
	public static final Class<Integer> CACHE_IMMUNIZATION_KEY_CLASS = Integer.class;
    public static final Class<ImmunizationQueryOutput> CACHE_IMMUNIZATION_VALUE_CLASS = ImmunizationQueryOutput.class;
	/*
	 * 
	 */
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
}
