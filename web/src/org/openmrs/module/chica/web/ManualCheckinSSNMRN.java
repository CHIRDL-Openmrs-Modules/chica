package org.openmrs.module.chica.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientIdentifierException;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
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
	private static final String XML_SESSION_RESULT_START = "<sessionResult>";
	private static final String XML_SESSION_RESULT_END = "</sessionResult>";
	private static final String XML_SESSION_TRUE = "true";
	private static final String XML_SESSION_FALSE = "false";
	
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
		
		Patient patient = null;
		Integer sessionId = null;
		if (valid) {
			pw.write(XML_RESULT_START + XML_TRUE + XML_RESULT_END);
			patient = getPatientByMRN(mrn);
			try{
				sessionId = getEncounterSessionId(patient.getPatientId());
			}catch(NullPointerException exp){}
		} else {
			pw.write(XML_RESULT_START + XML_FALSE + XML_RESULT_END);
		}
		
		boolean sessionValid = false;
		if (sessionId != null) {
			sessionValid = true;
		}
		
		if (sessionValid) {
			pw.write(XML_SESSION_RESULT_START + XML_TRUE + XML_SESSION_RESULT_END);
		}else{
			pw.write(XML_SESSION_RESULT_START + XML_FALSE + XML_SESSION_RESULT_END);
		}
		pw.write(XML_MRN_VERIFICATION_END);

	}
	
	/**
	 * Retrieves a patient based on MRN.
	 * 
	 * @param mrn MRN used to find a patient.
	 * @return The patient or null if a patient cannot be found with the specified MRN.
	 */
	private static Patient getPatientByMRN(String mrn) {
		PatientService patientService = Context.getPatientService();
		Patient patient = null;
		mrn = Util.removeLeadingZeros(mrn);
		if (mrn != null && !mrn.contains("-") && mrn.length() > 1) {
			mrn = mrn.substring(0, mrn.length() - 1) + "-" + mrn.substring(mrn.length() - 1);
		}
		
		PatientIdentifierType identifierType = patientService
				.getPatientIdentifierTypeByName(ChirdlUtilConstants.IDENTIFIER_TYPE_MRN);
		List<PatientIdentifierType> identifierTypes = new ArrayList<PatientIdentifierType>();
		identifierTypes.add(identifierType);
		List<Patient> patients = patientService.getPatients(null, mrn,
				identifierTypes,true);
		if (patients.size() == 0){
			patients = patientService.getPatients(null, "0" + mrn,
					identifierTypes,true);
		}

		if (patients.size() > 0) {
			patient = patients.get(0);
		}
		
		return patient;
	}
	
	/**
	 * Returns the latest session ID for a patient based on their latest encounter with a checkin state.
	 * 
	 * @param patientId The patient ID used to determine the session ID returned.
	 * @return session ID.
	 */
	private static Integer getEncounterSessionId(Integer patientId) {
		EncounterService encounterService = Context.getEncounterService();
		List<org.openmrs.Encounter> list = encounterService.getEncountersByPatientId(patientId);
		if (list != null && list.size() > 0) {
			Encounter encounter = list.get(0);
			ChirdlUtilBackportsService chirdlUtilBackportsService = Context
			        .getService(ChirdlUtilBackportsService.class);
			State checkinState = chirdlUtilBackportsService.getStateByName(ChirdlUtilConstants.STATE_CHECKIN);
			Integer encounterId = encounter.getEncounterId();
			List<PatientState> checkinStates = chirdlUtilBackportsService.getPatientStateByEncounterState(
			    encounterId, checkinState.getStateId());
			if (checkinStates != null && checkinStates.size() > 0) {
				PatientState patientState = checkinStates.get(0);
				return patientState.getSessionId();
			}
		}
		
		return null;
	}
}
