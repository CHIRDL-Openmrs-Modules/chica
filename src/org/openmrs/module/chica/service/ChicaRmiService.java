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
package org.openmrs.module.chica.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.openmrs.module.chica.util.PatientRow;


/**
 * Interface to expose CHICA functionality to other consumers via RMI.
 *
 * @author Steve McKee
 */
public interface ChicaRmiService extends Remote {
	
	/**
	 * Authenticates a user to the system.
	 * 
	 * @param username The Username.
	 * @param password Password for the username.
	 * @return true if the username/password is authenticated, false otherwise.
	 * @throws RemoteException
	 */
	public boolean authenticateUser(String username, String password) throws RemoteException;

	/**
	 * Validates the CHICA passcode.
	 * 
	 * @param username User used to meet the required credentials to validate the passcode.
	 * @param password Password for the username.
	 * @param passcode The passcode to validate.
	 * @return true if the provided passcode validates successfully, false otherwise.
	 * @throws RemoteException
	 */
	public boolean validatePasscode(String username, String password, int passcode) throws RemoteException;
	
	/**
	 * Returns a list of patients having at least one of the specified forms available.
	 * 
	 * @param username User used to meet the required credentials to pull the information.
	 * @param password Password for the username.
	 * @param formNames The names of forms to check for.
	 * @return List of PatientRow objects containing the information of patients awaiting their initial forms.
	 * @throws RemoteException
	 */
	public List<PatientRow> getPatientsWithForms(String username, String password, String[] formNames) 
	throws RemoteException;
	
	/**
	 * Retrieves a byte array of data from a form XML.
	 * 
	 * @param username User used to meet the required credentials to pull the information.
	 * @param password Password for the username.
	 * @param patientId ID of the patient that is the owner of the form.
	 * @param encounterId The encounter ID for the form.
	 * @param formId The identifier of the form to process.
	 * @return byte array of data read from the XML file.
	 * @throws RemoteException
	 */
	public byte[] getFormXml(String username, String password, Integer patientId, Integer encounterId, Integer formId) 
	throws RemoteException;
	
	/**
	 * Submits a byte array of data for a form XML.
	 * 
	 * @param username User used to meet the required credentials to load the information.
	 * @param password Password for the username.
	 * @param formId The identifier of the form being submitted.
	 * @param bytes Array of bytes containing the form XML information.
	 * @throws RemoteException
	 */
	public void submitFormXml(String username, String password, Integer formId, byte[] bytes) throws RemoteException;
	
	/**
	 * Utility method available to consumers to test the connection to this service.
	 * 
	 * @return true if the connection is available.
	 * @throws RemoteException
	 */
	public boolean testConnection() throws RemoteException;
	
	/**
	 * Mechanism to report any errors to the system.
	 * 
	 * @param username User used to meet the required credentials to report the errors.
	 * @param password Password for the username.
	 * @param error The error to report.
	 * @throws RemoteException
	 */
	public void reportError(String username, String password, String error) throws RemoteException;
	
	/**
	 * Returns a form name based on a form ID.
	 * 
	 * @param username User used to meet the required credentials to retrieve a form name.
	 * @param password Password for the username.
	 * @param formId The form identifier.
	 * @return Form name.
	 * @throws RemoteException
	 */
	public String getFormName(String username, String password, Integer formId) throws RemoteException;
}
