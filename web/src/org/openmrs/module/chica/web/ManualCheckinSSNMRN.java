package org.openmrs.module.chica.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientIdentifierException;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.validator.PatientIdentifierValidator;

public class ManualCheckinSSNMRN {
	
	/** Logger for this class and subclasses */
	protected static final Log log = LogFactory.getLog(ManualCheckinSSNMRN.class);
	private static final String PARAM_MRN = "mrn";
	private static final String XML_MRN_VERIFICATION_START = "<mrnVerification>";
	private static final String XML_MRN_VERIFICATION_END = "</mrnVerification>";
	private static final String XML_RESULT_START = "<result>";
	private static final String XML_RESULT_END = "</result>";
	private static final String XML_TRUE = "true";
	private static final String XML_FALSE = "false";
	
	public static void verifyMRN(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_XML);
		response.setHeader(ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL,
		    ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL_NO_CACHE);
		PrintWriter pw = response.getWriter();
		pw.write(XML_MRN_VERIFICATION_START);
		String mrn = request.getParameter(PARAM_MRN);
		mrn = Util.removeLeadingZeros(mrn);
		
		boolean valid = false;
		if (mrn != null && !mrn.contains("-") && mrn.length() > 1) {
			mrn = mrn.substring(0, mrn.length() - 1) + "-" + mrn.substring(mrn.length() - 1);
		}
		
		try {
			if (mrn != null && mrn.length() > 0 && !mrn.endsWith("-")) {
				PatientIdentifierType mrnIdent = 
						Context.getPatientService().getPatientIdentifierTypeByName(ChirdlUtilConstants.IDENTIFIER_TYPE_MRN);
				try {
					PatientIdentifierValidator.validateIdentifier(mrn, mrnIdent);
					valid = true;
				}  catch(PatientIdentifierException e) {
				}
			}
		}
		catch (Exception e) {
			log.error("Error validating MRN: " + mrn);
		}
		
		if (valid) {
			pw.write(XML_RESULT_START + XML_TRUE + XML_RESULT_END);
		} else {
			pw.write(XML_RESULT_START + XML_FALSE + XML_RESULT_END);
		}
		
		pw.write(XML_MRN_VERIFICATION_END);
	}
}
