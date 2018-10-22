package org.openmrs.module.chica.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.parameter.EncounterSearchCriteriaBuilder;

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
	private static final String XML_VALID_ENCOUNTER_START = "<validEncounter>";
	private static final String XML_VALID_ENCOUNTER_END = "</validEncounter>";

	public static void verifyMRN(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_XML);
		response.setHeader(ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL,
		    ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL_NO_CACHE);
		PrintWriter pw = response.getWriter();
		pw.write(XML_MRN_VERIFICATION_START);
		String mrn = request.getParameter(PARAM_MRN);
		Patient patient = Util.getPatientByMRNOther(mrn);
		boolean validPatient = false;
		boolean validIdentifier = StringUtils.isNotEmpty(mrn);
		List<Encounter> encounters = null;
		if (patient != null) {
			validPatient = true;
		}
		
		if (validPatient) {
			EncounterService encounterService = Context.getEncounterService();
			EncounterSearchCriteriaBuilder criteriaBuilder = new EncounterSearchCriteriaBuilder();
			criteriaBuilder.setPatient(patient).setIncludeVoided(false);
			encounters = encounterService.getEncounters(criteriaBuilder.createEncounterSearchCriteria());
		}
						
		if (validPatient || validIdentifier) { // CHICA-1239 Added check for validIdentifier so that we can manually register new patients as long as the mrn entered by the user is valid (not null and not empty)
			pw.write(XML_RESULT_START + XML_TRUE + XML_RESULT_END);
		} else {
			pw.write(XML_RESULT_START + XML_FALSE + XML_RESULT_END);
		}
		
		if (validPatient && encounters != null && encounters.size() > 0) {
			pw.write(XML_VALID_ENCOUNTER_START + XML_TRUE + XML_VALID_ENCOUNTER_END);
		}else{
			pw.write(XML_VALID_ENCOUNTER_START + XML_FALSE + XML_VALID_ENCOUNTER_END);
		}
		pw.write(XML_MRN_VERIFICATION_END);

	}
	
}
