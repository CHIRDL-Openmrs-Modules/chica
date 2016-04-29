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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.DecoderException;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;


/**
 *
 * @author Steve McKee
 */
public class ServletUtil {
	
	public static void isUserAuthenticated(HttpServletResponse response) throws IOException {
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		// The servlet has already checked authentication by this point.
		PrintWriter pw = response.getWriter();
		pw.write("<userAuthenticated>");
		pw.write("<result>");
		pw.write("true");
		pw.write("</result>");
		pw.write("</userAuthenticated>");
	}
	
	public static void authenticateUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter pw = response.getWriter();
		pw.write("<userAuthenticated>");
		pw.write("<result>");
		pw.write("true");
		pw.write("</result>");
		pw.write("</userAuthenticated>");
	}
	
	public static boolean authenticateUser(HttpServletRequest request) throws IOException {
		if (Context.getAuthenticatedUser() != null) {
			return true;
		}
		
		String auth = request.getHeader(ChirdlUtilConstants.HTTP_AUTHORIZATION_HEADER);
		if (auth == null) {
            return false;  // no auth
        }
        if (!auth.toUpperCase().startsWith("BASIC ")) { 
            return false;  // we only do BASIC
        }
        // Get encoded user and password, comes after "BASIC "
        String userpassEncoded = auth.substring(6);
        // Decode it, using any base 64 decoder
              
        /**
         * Edited sun.misc.Base64Decoder to org.apache.commons.codec.binary.Base64.decodeBase64
         */
        byte[] bytes = userpassEncoded.getBytes();//"UTF-8");
		byte[] b = org.apache.commons.codec.binary.Base64.decodeBase64(bytes);
		String userpassDecoded = new String(b);
        
        String[] userpass = userpassDecoded.split(":");
        if (userpass.length != 2) {
        	return false;
        }
        
        String user = userpass[0];
        String pass = userpass[1];
        try {
        	Context.authenticate(user, pass);
        } catch (ContextAuthenticationException e) {
        	return false;
        }
        
        return true;
	}
	
	public static String escapeXML(String str) {
		if (str == null) {
			return str;
		}
		
		return str.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&quot;")
		        .replaceAll("'", "&apos;");
	}
	
	public static void writeTag(String tagName, Object value, PrintWriter pw) {
		pw.write("<" + tagName + ">");
		if (value != null) {
			pw.write(value.toString());
		}
		
		pw.write("</" + tagName + ">");
	}
	
}
