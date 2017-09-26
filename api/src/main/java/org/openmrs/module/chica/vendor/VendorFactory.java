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
package org.openmrs.module.chica.vendor;

import javax.servlet.http.HttpServletRequest;
import org.openmrs.module.chica.vendor.impl.VendorImpl;


/**
 * Factory for creating instances of Vender implementations.
 * 
 * @author Steve McKee
 */
public class VendorFactory {
	
	private static final String VENDOR_CERNER = "iuh_cerner";
	private static final String VENDOR_EPIC = "eskenazi_epic";
	
	/**
	 * Returns the correct vendor implementation based on name.
	 * 
	 * @param vendorName The vendor name.
	 * @param request HttpServletRequest object for accessing URL parameters.
	 * @return A Vendor object for the requested name or null if one cannot be found.
	 */
	public static Vendor getVendor(String vendorName, HttpServletRequest request) {
		if (VENDOR_EPIC.equalsIgnoreCase(vendorName) || VENDOR_CERNER.equalsIgnoreCase(vendorName)) {
			return new VendorImpl(request);
		} 	
		return null;
	}
}
