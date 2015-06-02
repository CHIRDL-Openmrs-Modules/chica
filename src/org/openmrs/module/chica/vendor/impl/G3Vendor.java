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
package org.openmrs.module.chica.vendor.impl;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.vendor.Vendor;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;


/**
 * G3 vendor implementation for receiving data from external sources.
 * 
 * @author Steve McKee
 */
public class G3Vendor extends VendorImpl implements Vendor {
	
	private static Log log = LogFactory.getLog(G3Vendor.class);
	
	/**
	 * Constructor method
	 * 
	 * @param request HttpServletRequest object for accessing URL parameters.
	 */
	public G3Vendor(HttpServletRequest request) {
		super(request);
	}
	
	/**
	 * @see org.openmrs.module.chica.vendor.Vendor#getPassword()
	 */
	public String getPassword() {
		String password = request.getParameter(PARAM_PASSWORD);
		if (password == null || password.trim().length() == 0) {
			log.error("No " + PARAM_PASSWORD + " parameter found in HTTP request.");
			return null;
		}
		
		String key = Context.getAdministrationService().getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_G3_ENCRYPTION_KEY);
		if (key == null || key.trim().length() == 0) {
			log.error("Cannot find value for global property " + ChirdlUtilConstants.GLOBAL_PROP_G3_ENCRYPTION_KEY + ".  Cannot continue.");
			return null;
		}
		
		return decryptValue(password, key);
	}
	
	/**
	 * @see org.openmrs.module.chica.vendor.impl.VendorImpl#getUsername()
	 */
	public String getUsername() {
		String username = request.getParameter(PARAM_USERNAME);
		if (username == null || username.trim().length() == 0) {
			log.error("No " + PARAM_USERNAME + " parameter found in HTTP request.");
			return null;
		}
		
		String key = Context.getAdministrationService().getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_G3_ENCRYPTION_KEY);
		if (key == null || key.trim().length() == 0) {
			log.error("Cannot find value for global property " + ChirdlUtilConstants.GLOBAL_PROP_G3_ENCRYPTION_KEY + ".  Cannot continue.");
			return null;
		}
		
		return decryptValue(username, key);
	}
	
	/**
	 * Decrypts an encrypted value with the provided key.
	 * 
	 * @param encryptedValue The value to decrypt
	 * @param key The key used to decrypt the value
	 * @return The decrypted value or null if it can't be decrypted
	 */
	private String decryptValue(String encryptedValue, String key) {
		// Decrypt the password
		Cipher cipher;
        try {
	        cipher = Cipher.getInstance(ChirdlUtilConstants.ENCRYPTION_AES);
        }
        catch (NoSuchAlgorithmException e) {
	        log.error("Error creating " + ChirdlUtilConstants.ENCRYPTION_AES + " Cipher instance", e);
	        return null;
        }
        catch (NoSuchPaddingException e) {
	        log.error("Error creating " + ChirdlUtilConstants.ENCRYPTION_AES + " Cipher instance", e);
	        return null;
        }
        
		byte[] keyBytes;
        try {
	        keyBytes = key.getBytes(ChirdlUtilConstants.ENCODING_UTF8);
        }
        catch (UnsupportedEncodingException e) {
	        log.error("Unsupported Encoding: " + ChirdlUtilConstants.ENCODING_UTF8, e);
	        return null;
        }
        
		keyBytes = Arrays.copyOf(keyBytes, 16);
		Key secretKey = new SecretKeySpec(keyBytes, ChirdlUtilConstants.ENCRYPTION_AES);
		try {
	        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        }
        catch (InvalidKeyException e) {
	        log.error("Invalid Cipher Key", e);
	        return null;
        }
		
		String decrypted;
        try {
	        decrypted = new String(cipher.doFinal(Base64.decodeBase64(encryptedValue.getBytes())));
        }
        catch (IllegalBlockSizeException e) {
	        log.error("Illegal Block Size", e);
	        return null;
        }
        catch (BadPaddingException e) {
	        log.error("Bad Padding", e);
	        return null;
        }
        
		return decrypted;
	}
}
