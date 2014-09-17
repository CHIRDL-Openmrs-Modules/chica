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
package org.openmrs.module.chica.web;

import org.openmrs.module.chirdlutil.util.XMLUtil;


/**
 * Controller to display scanned forms to the end user.  This will apply an XSLT stylesheet to the scan XML file to 
 * produce an HTML view of the form.
 * 
 * @author Steve McKee
 */
public class DisplayScanFormController extends DisplayMergeFormController {
	
	/**
	 * @see org.openmrs.module.chica.web.DisplayMergeFormController#getLocationAttributeDirectoryName()
	 */
	protected String getLocationAttributeDirectoryName() {
		return XMLUtil.DEFAULT_EXPORT_DIRECTORY;
	}
}
