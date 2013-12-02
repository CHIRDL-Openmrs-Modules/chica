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
package org.openmrs.module.chica.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.FieldType;
import org.openmrs.Form;
import org.openmrs.FormField;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.FormService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.logic.result.Result;
import org.openmrs.module.atd.TeleformTranslator;
import org.openmrs.module.atd.hibernateBeans.Statistics;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.atd.xmlBeans.Field;
import org.openmrs.module.atd.xmlBeans.Record;
import org.openmrs.module.atd.xmlBeans.Records;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.service.ChicaRmiService;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chica.util.PatientRow;
import org.openmrs.module.chica.util.PatientRowComparator;
import org.openmrs.module.chirdlutil.hibernateBeans.EventLog;
import org.openmrs.module.chirdlutil.service.ChirdlUtilService;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutil.util.XMLUtil;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormAttributeValue;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.FormInstance;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.PatientState;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Program;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Session;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.State;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.springframework.transaction.UnexpectedRollbackException;

/**
 * @author Steve McKee
 */
public class ChicaRmiServiceImpl extends RemoteServer implements ChicaRmiService {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see org.openmrs.module.chica.service.ChicaRmiService#autthenticateUser(java.lang.String,
	 *      java.lang.String)
	 */
	public boolean authenticateUser(String username, String password) throws RemoteException {
		Context.openSession();
		try {
			authenticate(username, password);
			logEvent("authenticateUser");
			return true;
		}
		catch (ContextAuthenticationException e) {
			log.error("Error authenticating user", e);
			logDenialOfService(username);
			return false;
		}
		catch (Throwable e) {
			log.error("Error authenticating user", e);
			return false;
		}
		finally {
			Context.closeSession();
		}
	}
	
	/**
	 * @see org.openmrs.module.chica.service.ChicaRmiService#validatePasscode(java.lang.String,
	 *      java.lang.String, int)
	 */
	public boolean validatePasscode(String username, String password, int passcode) throws RemoteException {
		Context.openSession();
		try {
			authenticate(username, password);
			logEvent("validatePasscode");
			return authenticatePasscode(passcode);
		}
		catch (Throwable e) {
			log.error("Error validating passcode", e);
			throw new RemoteException("Error validating passcode", e);
		}
		finally {
			Context.closeSession();
		}
	}
	
	/**
	 * @see org.openmrs.module.chica.service.ChicaRmiService#getPatientsWithForms(java.lang.String, java.lang.String, java.lang.String[])
	 */
	public List<PatientRow> getPatientsWithForms(String username, String password, String[] formNames) 
	throws RemoteException {
		Context.openSession();
		ArrayList<PatientRow> rows = new ArrayList<PatientRow>();
		try {
			authenticate(username, password);
			logEvent("getPatientsWithForms");
			User user = Context.getUserContext().getAuthenticatedUser();
			
			try {
				ChirdlUtilBackportsService chirdlUtilBackportsService = Context.getService(ChirdlUtilBackportsService.class);
				ATDService atdService = Context.getService(ATDService.class);
				ChicaService chicaService = Context.getService(ChicaService.class);
				EncounterService encounterService = Context.getService(EncounterService.class);
				
				Calendar todaysDate = Calendar.getInstance();
				
				todaysDate.set(Calendar.HOUR_OF_DAY, 0);
				todaysDate.set(Calendar.MINUTE, 0);
				todaysDate.set(Calendar.SECOND, 0);
				
				String locationTags = user.getUserProperty("locationTags");
				String locationString = user.getUserProperty("location");
				ArrayList<Integer> locationTagIds = new ArrayList<Integer>();
				LocationService locationService = Context.getLocationService();
				
				Integer locationId = null;
				Location location = null;
				if (locationString != null) {
					location = locationService.getLocation(locationString);
					if (location != null) {
						locationId = location.getLocationId();
						if (locationTags != null) {
							StringTokenizer tokenizer = new StringTokenizer(locationTags, ",");
							while (tokenizer.hasMoreTokens()) {
								String locationTagName = tokenizer.nextToken();
								locationTagName = locationTagName.trim();
								Set<LocationTag> tags = location.getTags();
								for (LocationTag tag : tags) {
									if (tag.getTag().equalsIgnoreCase(locationTagName)) {
										locationTagIds.add(tag.getLocationTagId());
									}
								}
							}
							
						}
					}
				}
				
				List<PatientState> unfinishedStates = new ArrayList<PatientState>();
				for (Integer locationTagId : locationTagIds) {
					Program program = chirdlUtilBackportsService.getProgram(locationTagId, locationId);
					List<PatientState> currUnfinishedStates = chirdlUtilBackportsService.getLastPatientStateAllPatients(
					    todaysDate.getTime(), program.getProgramId(), program.getStartState().getName(), locationTagId,
					    locationId);
					if (currUnfinishedStates != null) {
						unfinishedStates.addAll(currUnfinishedStates);
					}
				}
				
				Set<Integer> checkPatients = new HashSet<Integer>();
				for (PatientState currState : unfinishedStates) {
					Integer patientId = currState.getPatientId();
					if (checkPatients.contains(patientId)) {
						continue;
					}
					
					checkPatients.add(patientId);
					Integer sessionId = currState.getSessionId();
					Session session = chirdlUtilBackportsService.getSession(sessionId);
					Integer encounterId = session.getEncounterId();
					Map<Integer,List<PatientState>> formPatientStateCreateMap = new HashMap<Integer,List<PatientState>>();
					Map<Integer,List<PatientState>> formPatientStateProcessMap = new HashMap<Integer,List<PatientState>>();
					State startState = chirdlUtilBackportsService.getStateByName("JIT_create");
					State endState = chirdlUtilBackportsService.getStateByName("JIT_process");
					getPatientStatesByEncounterId(chirdlUtilBackportsService, formPatientStateCreateMap, encounterId, 
						startState.getStateId());
					getPatientStatesByEncounterId(chirdlUtilBackportsService, formPatientStateProcessMap, encounterId, 
						endState.getStateId());
					startState = chirdlUtilBackportsService.getStateByName("PSF_create");
					endState = chirdlUtilBackportsService.getStateByName("PSF_process");
					getPatientStatesByEncounterId(chirdlUtilBackportsService, formPatientStateCreateMap, encounterId, 
						startState.getStateId());
					getPatientStatesByEncounterId(chirdlUtilBackportsService, formPatientStateProcessMap, encounterId, 
						endState.getStateId());
					startState = chirdlUtilBackportsService.getStateByName("PWS_create");
					endState = chirdlUtilBackportsService.getStateByName("PWS_process");
					getPatientStatesByEncounterId(chirdlUtilBackportsService, formPatientStateCreateMap, encounterId, 
						startState.getStateId());
					getPatientStatesByEncounterId(chirdlUtilBackportsService, formPatientStateProcessMap, encounterId, 
						endState.getStateId());
					
					PatientRow row = new PatientRow();
					for (String formName : formNames) {
						Form form = Context.getFormService().getForm(formName);
						if (form == null) {
							continue;
						}
						
						Integer formId = form.getFormId();
						boolean containsStartState = formPatientStateCreateMap.containsKey(formId);
						boolean containsEndState = formPatientStateProcessMap.containsKey(formId);
						if (containsStartState && !containsEndState) {
							List<PatientState> patientStates = formPatientStateCreateMap.get(formId);
							PatientState patientState = patientStates.get(0);
							FormInstance formInstance = patientState.getFormInstance();
							row.addFormInstance(formInstance);
						}
					}
					
					formPatientStateCreateMap.clear();
					formPatientStateProcessMap.clear();
					if (row.getFormInstances() == null || row.getFormInstances().size() == 0) {
						continue;
					}
					
					Patient patient = currState.getPatient();
					String lastName = Util.toProperCase(patient.getFamilyName());
					String firstName = Util.toProperCase(patient.getGivenName());
					
					String mrn = atdService.evaluateRule("medicalRecordWithFormatting", patient, null).toString();
					
					String dob = atdService.evaluateRule("birthdate>fullDateFormat", patient, null).toString();
					String sex = patient.getGender();
					Encounter encounter = (Encounter) encounterService.getEncounter(encounterId);
					
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("encounterId", encounterId);
					String appointment = atdService.evaluateRule("scheduledTime>fullTimeFormat", patient, parameters)
					        .toString();
					Date encounterDate = null;
					
					if (encounter != null) {
						row.setEncounter(encounter);
						encounterDate = encounter.getEncounterDatetime();
						if (encounterDate != null && !Util.isToday(encounterDate)) {
							continue;
						}
						parameters.put("param0", new Result(encounter.getEncounterDatetime()));
						String checkin = atdService.evaluateRule("fullTimeFormat", patient, parameters).toString();
						List<User> providers = Context.getUserService().getUsersByPerson(encounter.getProvider(), true);
						String mdName = "";
						if (providers != null && providers.size() > 0) {
							User provider = providers.get(0);
							String firstInit = Util.toProperCase(provider.getGivenName());
							if (firstInit != null && firstInit.length() > 0) {
								firstInit = firstInit.substring(0, 1);
							} else {
								firstInit = "";
							}
							
							String middleInit = Util.toProperCase(encounter.getProvider().getMiddleName());
							if (middleInit != null && middleInit.length() > 0) {
								middleInit = middleInit.substring(0, 1);
							} else {
								middleInit = "";
							}
							if (firstInit != null && firstInit.length() > 0) {
								mdName += firstInit + ".";
								if (middleInit != null && middleInit.length() > 0) {
									mdName += " " + middleInit + ".";
								}
							}
							if (mdName.length() > 0) {
								mdName += " ";
							}
							String familyName = Util.toProperCase(provider.getFamilyName());
							if (familyName == null) {
								familyName = "";
							}
							mdName += familyName;
						}
						
						row.setCheckin(checkin);
						row.setMdName(mdName);
					}
					
					List<PatientState> reprintRescanStates = new ArrayList<PatientState>();
					
					for (Integer locationTagId : locationTagIds) {
						List<PatientState> currReprintRescanStates = chicaService.getReprintRescanStatesByEncounter(
						    encounterId, todaysDate.getTime(), locationTagId, locationId);
						if (currReprintRescanStates != null) {
							reprintRescanStates.addAll(currReprintRescanStates);
						}
					}
					boolean reprint = false;
					if (reprintRescanStates.size() > 0) {
						reprint = true;
					}
					row.setReprintStatus(reprint);
					row.setAppointment(appointment);
					row.setDob(dob);
					row.setFirstName(firstName);
					row.setLastName(lastName);
					
					row.setMrn(mrn);
					row.setSex(sex);
					row.setPatientId(patient.getPatientId());
					row.setSessionId(sessionId);
					
					rows.add(row);
				}
				
				checkPatients.clear();
				//sort arraylist by encounterDatetime
				Collections.sort(rows, new PatientRowComparator());
			}
			catch (UnexpectedRollbackException ex) {
				//ignore this exception since it happens with an APIAuthenticationException
			}
			catch (APIAuthenticationException ex2) {
				//ignore this exception. It happens during the redirect to the login page
			}
		} catch (Throwable e) {
			log.error("Error retrieving awaiting patients", e);
			throw new RemoteException("Error retrieving awaiting patients", e);
		} finally {
			Context.closeSession();
		}
		
		return rows;
	}
	
	/**
	 * @see org.openmrs.module.chica.service.ChicaRmiService#getFormXml(java.lang.String, java.lang.String, java.lang.Integer, java.lang.Integer, java.lang.Integer)
	 */
    public byte[] getFormXml(String username, String password, Integer patientId, Integer encounterId, Integer formId) 
    throws RemoteException {
	    if (patientId == null || encounterId == null) {
	    	throw new RemoteException("patientId and encounterId cannot be null.");
	    }
	    
	    byte[] bytes = null;
	    Context.openSession();
		try {
			authenticate(username, password);
			logEvent("getFormXml");
		    String filename = getFormXmlFilename(patientId, encounterId, formId);
			
			if (filename == null || !(new File(filename).exists())) {
				throw new RemoteException("Unable to location file: " + filename);
			}
			
			//Parse the merge file to get the field values to display
			FormService formService = Context.getFormService();
			HashSet<String> inputFields = new HashSet<String>();
			Form form = formService.getForm(formId);
			TeleformTranslator translator = new TeleformTranslator();
			Set<FormField> formFields = form.getFormFields();	
			for (FormField formField : formFields)
			{
				org.openmrs.Field currField = formField.getField();
				FieldType fieldType = currField.getFieldType();
				if (fieldType!=null&&fieldType.equals(
						translator.getFieldType("Export Field")))
				{
					inputFields.add(currField.getName());
				}
			}
			
			FileInputStream inputMergeFile = new FileInputStream(filename);
			Records records = (Records) XMLUtil.deserializeXML(Records.class, inputMergeFile);
			inputMergeFile.close();
			Record record = records.getRecord();
			for (String inputField : inputFields) {
				// See if the field exists in the XML
				boolean found = false;
				for (Field currField : record.getFields()) {
					String name = currField.getId();
					if (inputField.equals(name)) {
						found = true;
						break;
					}
				}
				
				if (!found) {
					// Create a new Field
					Field field = new Field();
					field.setId(inputField);
					record.addField(field);
				}
			}
			
			OutputStream output = new FileOutputStream(filename);
			XMLUtil.serializeXML(records, output);
			output.flush();
			output.close();
			
			StringBuilder text = new StringBuilder();
			FileInputStream in = new FileInputStream(filename);
			Scanner scanner = null;
			try {
				scanner = new Scanner(in, "ISO-8859-1");
				while (scanner.hasNextLine()){
			        text.append(scanner.nextLine());
			    }
			} finally {
				if (scanner != null) {
					scanner.close();
				}
			}

			bytes = text.toString().getBytes("ISO-8859-1");
			
			// Rename file so state changes
			File file = new File(filename);
			File parentDir = file.getParentFile();
			String name = IOUtil.getFilenameWithoutExtension(filename);
			name += ".20";
			File newFile = new File(parentDir, name);
			IOUtil.renameFile(filename, newFile.getAbsolutePath());
        }
        catch (Throwable e) {
        	log.error("Error reading XML file", e);
        	throw new RemoteException("Error reading XML file", e);
        } finally {
        	Context.closeSession();
        }
        
        return bytes;
    }
    
    /**
     * @see org.openmrs.module.chica.service.ChicaRmiService#submitFormXml(java.lang.String, java.lang.String, java.lang.Integer, byte[])
     */
    public void submitFormXml(String username, String password, Integer formId, byte[] bytes) throws RemoteException {
    	if (formId == null || bytes == null) {
	    	throw new RemoteException("formId and bytes cannot be null.");
	    }
	    
	    Context.openSession();
		try {
			authenticate(username, password);
			logEvent("submitFormXml");
			User user = Context.getUserContext().getAuthenticatedUser();
			String locationString = user.getUserProperty("location");
			if (locationString == null) {
				throw new RemoteException("Unable to find location for user: " + user.getUsername());
			}
			
			Form form = Context.getFormService().getForm(formId);
			if (form == null) {
				throw new RemoteException("Unable to find form: " + formId);
			}
			
			Location location = Context.getLocationService().getLocation(locationString);
			if (location == null) {
				throw new RemoteException("Unable to find location for string: " + locationString);
			}
			
			String locationTags = user.getUserProperty("locationTags");
			List<Integer> locationTagIds = new ArrayList<Integer>();
			if (locationTags != null) {
				StringTokenizer tokenizer = new StringTokenizer(locationTags, ",");
				while (tokenizer.hasMoreTokens()) {
					String locationTagName = tokenizer.nextToken();
					locationTagName = locationTagName.trim();
					Set<LocationTag> tags = location.getTags();
					for (LocationTag tag : tags) {
						if (tag.getTag().equalsIgnoreCase(locationTagName)) {
							locationTagIds.add(tag.getLocationTagId());
						}
					}
				}
				
			}
			
			ChirdlUtilBackportsService service = Context.getService(ChirdlUtilBackportsService.class);
			FormAttributeValue fav = null;
			Integer locationTagId = null;
			for (int i = 0; i < locationTagIds.size(); i++) {
				locationTagId = locationTagIds.get(i);
				fav = service.getFormAttributeValue(form.getFormId(), "defaultExportDirectory", locationTagId, 
					location.getLocationId());
				if (fav != null) {
					break;
				}
			}
			
			if (fav == null || fav.getValue() == null) {
				throw new RemoteException("Unable to find defaultExportDirectory for form: " + form.getName() + " ID: " + 
					form.getFormId() + " Location ID: " + location.getLocationId() + " Location Tag ID: " + locationTagId);
			}
			
			String exportDir = fav.getValue();
			File file = new File(exportDir, (String.valueOf(System.currentTimeMillis()) + ".xmle"));
			while (file.exists()) {
				file = new File(exportDir, (String.valueOf(System.currentTimeMillis()) + ".xmle"));
			}
			
			Writer writer = null;
			try {
				writer = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(file), "ISO-8859-1"));
				String text = new String(bytes, "ISO-8859-1");
				writer.write(text);
				writer.flush();
			}
			catch (Exception e) {
				log.error("Error writing XML file: " + file.getAbsolutePath(), e);
				throw e;
			} finally {
				if (writer != null) {
					writer.close();
				}
			}
        }
        catch (Throwable e) {
        	log.error("Error writing XML file", e);
        	throw new RemoteException("Error writing XML file", e);
        } finally {
        	Context.closeSession();
        }
    }
    
    /**
     * @see org.openmrs.module.chica.service.ChicaRmiService#reportError(java.lang.String, java.lang.String, java.lang.String)
     */
    public void reportError(String username, String password, String error) throws RemoteException {
    	Context.openSession();
 		try {
 			authenticate(username, password);
 			logEvent("reportError");
 			String ip = getIPAddress();
 			error = error + " IP: " + ip;
 			ChirdlUtilBackportsService service = Context.getService(ChirdlUtilBackportsService.class);
 			org.openmrs.module.chirdlutilbackports.hibernateBeans.Error errorObj = 
 				new org.openmrs.module.chirdlutilbackports.hibernateBeans.Error("Error", "Web Service Error", 
 					"Web service error", error, new Date(), null);
 			service.saveError(errorObj);
         }
         catch (Throwable e) {
         	log.error("Error reporting error", e);
         	throw new RemoteException("Error reporting error", e);
         } finally {
         	Context.closeSession();
         }
    }
    
    /**
     * @see org.openmrs.module.chica.service.ChicaRmiService#getFormName(java.lang.String, java.lang.String, java.lang.Integer)
     */
    public String getFormName(String username, String password, Integer formId) throws RemoteException {
    	Context.openSession();
 		try {
 			authenticate(username, password);
 			logEvent("getForm");
 			Form form = Context.getFormService().getForm(formId);
 			String formName = null;
 		    if (form != null) {
 		    	formName = form.getName();
 		    }
 		    
 		    return formName;
         }
         catch (Throwable e) {
         	log.error("Error retrieving form", e);
         	throw new RemoteException("Error retrieving form", e);
         } finally {
         	Context.closeSession();
         }
    }
	
	/**
	 * @see org.openmrs.module.chica.service.ChicaRmiService#testConnection()
	 */
	public boolean testConnection() throws RemoteException {
		return true;
	}
	
	/**
	 * Finds the form XML filename based on the data provided.
	 * 
	 * @param patientId ID of the patient that is the owner of the form.
	 * @param encounterId The encounter ID for the form.
	 * @param formId The ID of the form to find.
	 * @return String containing the absolute path to the XML form file.
	 * @throws RemoteException
	 */
	private String getFormXmlFilename(Integer patientId, Integer encounterId, 
	                                 Integer formId) throws RemoteException {
		Integer locationId = null;
		Integer formInstanceId = null;
		try {
			org.openmrs.api.EncounterService encounterService = Context.getEncounterService();
			org.openmrs.Encounter encounter = encounterService.getEncounter(encounterId);
			if (encounter == null) {
				throw new IllegalArgumentException("Must provide a valid encounter ID");
			}
			
			FormService formService = Context.getFormService();
			Form form = formService.getForm(formId);
			if (form == null) {
				throw new IllegalArgumentException("Must provide a valid form name");
			}
			
			String formName = form.getName();
			Location location = encounter.getLocation();
			locationId = location.getLocationId();
			
			ATDService atdService = Context.getService(ATDService.class);
			List<Statistics> stats = atdService.getAllStatsByEncounterForm(encounterId, formName);
			
			for (Statistics stat : stats) {
				formInstanceId = stat.getFormInstanceId();
				if (formInstanceId != null) {
					break;
				}
			}
			
			if (formInstanceId == null) {
				List<PatientState> states = Context.getService(
					ChirdlUtilBackportsService.class).getPatientStatesWithFormInstances(formName, encounterId);
				if (states != null) {
					for (PatientState state : states) {
						if (formId == null || !formId.equals(state.getFormId())) {
							continue;
						} else if (location == null || !locationId.equals(state.getLocationId())) {
							continue;
						} else if (state.getFormInstanceId() == null) {
							continue;
						}
						
						formInstanceId = state.getFormInstanceId();
						break;
					}
				}
			}
			
			if (formInstanceId == null) {
				throw new RemoteException("Unable to find a valid form instance ID");
			}
			
			User user = Context.getUserContext().getAuthenticatedUser();
			String locationTags = user.getUserProperty("locationTags");
			List<Integer> locationTagIds = new ArrayList<Integer>();
			if (locationTags != null) {
				StringTokenizer tokenizer = new StringTokenizer(locationTags, ",");
				while (tokenizer.hasMoreTokens()) {
					String locationTagName = tokenizer.nextToken();
					locationTagName = locationTagName.trim();
					Set<LocationTag> tags = location.getTags();
					for (LocationTag tag : tags) {
						if (tag.getTag().equalsIgnoreCase(locationTagName)) {
							locationTagIds.add(tag.getLocationTagId());
						}
					}
				}
				
			}
			
			ChirdlUtilBackportsService service = Context.getService(ChirdlUtilBackportsService.class);
			FormAttributeValue attrVal = null;
			Integer locationTagId = null;
			for (int i = 0; i < locationTagIds.size(); i++) {
				locationTagId = locationTagIds.get(i);
				attrVal = service.getFormAttributeValue(
					formId, "defaultMergeDirectory", locationTagId, locationId);
				if (attrVal != null) {
					break;
				}
			}
			
			if (attrVal == null || attrVal.getValue() == null) {
				throw new RemoteException("Unable to find defaultMergeDirectory for form: " + formName + " ID: " + 
					formId + " Location ID: " + location.getLocationId() + " Location Tag ID: " + locationTagId);
			}
			
			String directory = attrVal.getValue();
			
			String fileStr = directory + File.separator + locationId + "_" + formId + "_" + formInstanceId + ".20";
			File file = new File(fileStr);
			if (file.exists()) {
				return fileStr;
			}
			
			fileStr = directory + File.separator + "_" + locationId + "_" + formId + "_" + formInstanceId + "_.20";
			file = new File(fileStr);
			if (file.exists()) {
				return fileStr;
			}
			
			fileStr = directory + File.separator + locationId + "_" + formId + "_" + formInstanceId + ".22";
			file = new File(fileStr);
			if (file.exists()) {
				return fileStr;
			}
			
			fileStr = directory + File.separator + "_" + locationId + "_" + formId + "_" + formInstanceId + "_.22";
			file = new File(fileStr);
			if (file.exists()) {
				return fileStr;
			}
			
			fileStr = directory + File.separator + locationId + "_" + formId + "_" + formInstanceId + ".xml";
			file = new File(fileStr);
			if (file.exists()) {
				return fileStr;
			}
			
			fileStr = directory + File.separator + "_" + locationId + "_" + formId + "_" + formInstanceId + "_.xml";
			file = new File(fileStr);
			if (file.exists()) {
				return fileStr;
			}
			
			fileStr = directory + File.separator + "Pending" + File.separator + locationId + "_" + formId + "_" + 
				formInstanceId + ".xml";
			file = new File(fileStr);
			if (file.exists()) {
				return fileStr;
			}
			
			fileStr = directory + File.separator + "Pending" + File.separator + "_" + locationId + "_" + formId + "_" + 
				formInstanceId + "_.xml";
			file = new File(fileStr);
			if (file.exists()) {
				return fileStr;
			}
			
			throw new RemoteException("Unable to locate the desired " + formName + " XML file (Patient ID: " + patientId + 
				" Location ID: " + locationId + " Form ID: " + formId + " Form Instance ID: " + formInstanceId + ")");
		} catch (Throwable e) {
			log.error("Unable to locate the desired " + formId + " XML file (Patient ID: " + patientId + 
				" Location ID: " + locationId + " Form ID: " + formId + " Form Instance ID: " + formInstanceId + ")", e);
			throw new RemoteException("Unable to locate the desired " + formId + " XML file (Patient ID: " + patientId + 
				" Location ID: " + locationId + " Form ID: " + formId + " Form Instance ID: " + formInstanceId + ")", e);
		}
	}
	
	/**
	 * Checks to see if the patient is in the current status to be included.
	 * 
	 * @param state
	 * @param row
	 * @param sessionId
	 * @param currState
	 * @return
	 */
	private boolean getStatus(State state, PatientRow row, Integer sessionId, String expectedStateName) {
		String stateName = state.getName();
		if (stateName.equals(/*"PSF_wait_to_scan"*/expectedStateName)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Authenticates a user to the system.
	 * 
	 * @param username Used to login to the system.
	 * @param password Password for the username.
	 * @throws ContextAuthenticationException
	 */
	private void authenticate(String username, String password) throws ContextAuthenticationException {
		Context.authenticate(username, password);
	}
	
	/**
	 * Authenticates the provided passcode against the passcode stored in the database.
	 * 
	 * @param passcode The passcode used to authenticate.
	 * @return true if the provided passcode matches the passcode stored in the database, false
	 *         otherwise.
	 * @throws RuntimeException if the global property chica.passcode is not set in the database.
	 */
	private boolean authenticatePasscode(int passcode) throws RuntimeException {
		AdministrationService adminService = Context.getAdministrationService();
		String reqdPasscode = adminService.getGlobalProperty("chica.passcode");
		if (reqdPasscode == null) {
			log.error("Required global property chica.passcode is not set.  Please set a value for this property.");
			throw new RuntimeException(
			        "Required global property chica.passcode is not set.  Please set a value for this property.");
		} else if (reqdPasscode.equals(String.valueOf(passcode))) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Creates a log event for a method access.  All public methods in the class should call this method.
	 * 
	 * @param methodName The name of the method to record.
	 */
	private void logEvent(String methodName) {
		User user = Context.getUserContext().getAuthenticatedUser();
		String username = user.getUsername();
		String ip = getIPAddress();
		ChirdlUtilService service = Context.getService(ChirdlUtilService.class);
		EventLog log = new EventLog();
		log.setDescription("Service method called " + methodName + " with user: " + username + " IP: " + ip);
		log.setEvent("CHICA RMI SERVICE");
		log.setEventTime(new Date());
		log.setUserId(user.getUserId());
		
		service.logEvent(log);
	}
	
	/**
	 * Creates a log event for denial of service.
	 * 
	 * @param username The username attempting to authenticate.
	 */
	private void logDenialOfService(String username) {
		ChirdlUtilService service = Context.getService(ChirdlUtilService.class);
		String ip = getIPAddress();
		EventLog log = new EventLog();
		log.setDescription("Denial of service for user: " + username + " IP: " + ip);
		log.setEvent("CHICA RMI DENIAL OF SERVICE");
		log.setEventTime(new Date());
		
		service.logEvent(log);
	}
	
	
	/**
	 * Returns the IP address of the requesting client.
	 * 
	 * @return IP address of the requesting client.
	 */
	private String getIPAddress() {
		String ip = "";
        try {
	        ip = RemoteServer.getClientHost();
        }
        catch (ServerNotActiveException e) {
	        log.error("Error determining client IP address", e);
        }
		
		return ip;
	}
	
	private void getPatientStatesByEncounterId(ChirdlUtilBackportsService chirdlUtilBackportsService, 
	                                           Map<Integer,List<PatientState>> formIdToPatientStateMap, 
	                                           Integer encounterId, Integer stateId) {
		List<PatientState> patientStates = chirdlUtilBackportsService.getPatientStateByEncounterState(
			encounterId, stateId);
		for (PatientState patientState : patientStates) {
			Integer formId = patientState.getFormId();
			List<PatientState> foundStates = formIdToPatientStateMap.get(formId);
			if (foundStates == null) {
				foundStates = new ArrayList<PatientState>();
			}
			
			foundStates.add(patientState);
			formIdToPatientStateMap.put(formId, foundStates);
		}
	}
}
