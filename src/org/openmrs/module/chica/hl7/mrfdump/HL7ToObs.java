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
package org.openmrs.module.chica.hl7.mrfdump;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.ConceptName;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.hl7.mckesson.HL7SocketHandler;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Error;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.validation.impl.NoValidation;


public class HL7ToObs {

	private static final String ESCAPE_SEQUENCE_AMPERSAND = "\\\\T\\\\";
	private static final String HL7_VERSION_2_3 = "2.3";
	protected final static Log log = LogFactory.getLog(HL7ToObs.class);

	public static void parseHL7ToObs(String hl7Message, Patient patient,
			String mrn, HashMap<Integer, HashMap<String, Set<Obs>>> patientObsMap) {

		try {
			BufferedReader reader = new BufferedReader(new StringReader(
					hl7Message));
			String line = null;

			// skip lines before hl7 message begins
			while ((line = reader.readLine()) != null && !line.startsWith(ChirdlUtilConstants.HL7_SEGMENT_MESSAGE_HEADER_MSH)) {}

			StringWriter output = new StringWriter();
			PrintWriter writer = new PrintWriter(output);

			if (line != null) {
				writer.println(line); // write out the first MSH line
			} else {
				log.info("MRF dump is empty for patient = " + patient.getId());
				return;
			}

			// Separate each hl7 message from the response string.
			while ((line = reader.readLine()) != null) {
				if (line.startsWith(ChirdlUtilConstants.HL7_SEGMENT_MESSAGE_HEADER_MSH)) {
					// start processing the new message
					processMessage(output.toString(), patient, patientObsMap);
					HL7SocketHandler.checkAlias(mrn, patient, output.toString());
					writer.flush();
					writer.close();
					output = new StringWriter();
					writer = new PrintWriter(output);
					writer.println(line);
				} else
					writer.println(line);
			}

			writer.flush();
			writer.close();

			// process last message
			processMessage(output.toString(), patient, patientObsMap);
			HL7SocketHandler.checkAlias(mrn, patient, output.toString());

		} catch (Exception e) {
			log.error(
					"Exception loading obs from all messages in MRF dump. Patient:  "
							+ patient.getId(), e);
		}
	}

	public static void processMessage(String messageString, Patient patient,
			HashMap<Integer, HashMap<String, Set<Obs>>> patientObsMap) {

		Integer patientId = patient.getPatientId();
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
		AdministrationService adminService = Context.getAdministrationService();

		if (messageString != null) {
			messageString = messageString.trim();
		}

		if (messageString == null || messageString.length() == 0) {
			return;
		}
		
		String newMessageString = messageString;
		PipeParser pipeParser = new PipeParser();
		pipeParser.setValidationContext(new NoValidation());
		newMessageString = replaceVersion(newMessageString);
		newMessageString = renameDxAndComplaints(newMessageString);
		Message message = null;
		
		try {
			message = pipeParser.parse(newMessageString);
		} catch (Exception e) {
			Error error = new Error(ChirdlUtilConstants.ERROR_LEVEL_ERROR,
					ChirdlUtilConstants.ERROR_HL7_PARSING,
					"Error parsing the MRF dump " + e.getMessage(),
					messageString, new Date(), null);
			chirdlutilbackportsService.saveError(error);
			String mrfParseErrorDirectory = 
					IOUtil.formatDirectoryName(adminService.getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_MRF_ERROR_DIRECTORY));
			
			if (mrfParseErrorDirectory != null) {
				
				String filename = "r" + Util.archiveStamp() + ChirdlUtilConstants.FILE_EXTENSION_HL7;
				FileOutputStream outputFile = null;

				try {
					outputFile = new FileOutputStream(new File(mrfParseErrorDirectory,filename));
				} catch (FileNotFoundException e1) {
					log.error("Could not find filename " + filename + " in directory " + mrfParseErrorDirectory );
				}
				
				if (outputFile != null) {
					try {
						ByteArrayInputStream input = new ByteArrayInputStream(newMessageString.getBytes());
						IOUtil.bufferedReadWrite(input, outputFile);
					} catch (IOException e1) {
						log.error("Error writing the errored mrf dump to an error directory.",e1);
					}

					try {
						outputFile.flush();
						outputFile.close();
					} catch (IOException e1) {
						log.error("Error flushing output stream after an MRF dump parsing error.", e1);
					}

				}
			}
			return;
		}

		HL7ObsHandler23 obsHandler = new HL7ObsHandler23();
		try {
			ArrayList<Obs> allObs = obsHandler.getObs(message, patient);
			HashMap<String, Set<Obs>> obsByConcept = patientObsMap
					.get(patientId);

			if (obsByConcept == null) {
				obsByConcept = new HashMap<String, Set<Obs>>();
				patientObsMap.put(patientId, obsByConcept);
			}

			for (Obs currObs : allObs) {
				String currConceptName = ((ConceptName) currObs.getConcept().getNames().toArray()[0]).getName();
				Set<Obs> obs = obsByConcept.get(currConceptName);
				if (obs == null) {
					obs = new HashSet<Obs>();
					obsByConcept.put(currConceptName, obs);
				}
				obs.add(currObs);
			}
		} catch (HL7Exception e) {
			log.error("Hl7 exception parsing observations from MRF for patient: "
						+ patient.getId(), e);
		} catch (Exception e) {
			log.error("Exception loading observations to obs map for patient: "
					+ patient.getId(), e);
		}
	}

	//MES CHICA-358 - New MRF format uses escape sequence ampersand. 
	public static String renameDxAndComplaints(String message) {
		message = message.replaceAll("DX & COMPLAINTS", "DX and COMPLAINTS");
		message = message.replaceAll("Dx " + ESCAPE_SEQUENCE_AMPERSAND + " Complaints", "DX and COMPLAINTS");
		return message;
	}

	/**
	 * Replace with version 2.3
	 * 
	 * @param message
	 * @return
	 */
	public static String replaceVersion(String message) {
		StringBuffer newMessage = new StringBuffer();
		BufferedReader reader = new BufferedReader(new StringReader(message));
		try {
			String firstLine = reader.readLine();

			if (firstLine == null) {
				return message;
			}

			String[] fields = PipeParser.split(firstLine, "|");
			if (fields != null) {
				int length = fields.length;

				for (int i = 0; i < length; i++) {
					if (fields[i] == null) {
						fields[i] = "";
					}
					if (i > 0) {
						newMessage.append("|");
					}
					if (i == 11) {
						newMessage.append(HL7_VERSION_2_3);
					} else {
						newMessage.append(fields[i]);
					}
				}
			}
			String line = null;

			while ((line = reader.readLine()) != null) {
				newMessage.append("\r\n");
				newMessage.append(line);
			}
		} catch (IOException e) {
			log.error(e.getMessage());
			log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}

		return newMessage.toString();
	}

}
