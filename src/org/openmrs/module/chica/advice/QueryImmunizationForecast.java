/**
 * 
 */
package org.openmrs.module.chica.advice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Patient;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.hibernateBeans.ATDError;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.ImmunizationForecastLookup;
import org.openmrs.module.chica.QueryImmunizationsException;
import org.openmrs.module.chirdlutil.util.IOUtil;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.rgccd.Immunization;
import org.openmrs.module.rgccd.service.ImmunizationService;

/**
 * @author tmdugan
 * This thread class runs the immunization forecasting service query
 */
public class QueryImmunizationForecast implements Runnable {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private QueryImmunizationsException exception = null;	
	private org.openmrs.Encounter encounter = null;
	private String queryInputFilename = null;
	
	public QueryImmunizationForecast(org.openmrs.Encounter encounter,String queryInputFilename) {
		this.encounter = encounter;
		this.queryInputFilename = queryInputFilename;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		Context.openSession();
		String mrn = null;
		
		try {
			AdministrationService adminService = Context.getAdministrationService();
			PatientService patientService = Context.getPatientService();
			Context.authenticate(adminService.getGlobalProperty("scheduler.username"), adminService
			        .getGlobalProperty("scheduler.password"));
			
			Patient patient = this.encounter.getPatient();
			patient = patientService.getPatient(patient.getPatientId());//lookup to prevent lazy initialization errors
			
			ImmunizationService immunizationService = Context.getService(ImmunizationService.class);
			mrn = patient.getPatientIdentifier().getIdentifier();
			
			String queryResponse = null;
			
			FileInputStream input = new FileInputStream(this.queryInputFilename);
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			IOUtil.bufferedReadWrite(input, output);
			String inputString = output.toString();
			
			//query the immunization forecasting service
			queryResponse = immunizationService.getImmunization(inputString, mrn);
			
			if (queryResponse != null) {
				
				//write the response to a file
				String directory = IOUtil.getDirectoryName(adminService.getGlobalProperty("chica.immunizationOutputDirectory"));
				String filename = "immunization_output_" + Util.archiveStamp() + "_" + mrn + ".xml";
				FileOutputStream immunFileOutput = new FileOutputStream(directory + "/" + filename);
				ByteArrayInputStream responseInput = new ByteArrayInputStream(queryResponse.getBytes());
				IOUtil.bufferedReadWrite(responseInput, immunFileOutput);
				
				//parse the immunization forecasting service response into a list of immunizations
				List<Immunization> immunizationList = immunizationService.createImmunizationList(queryResponse, mrn);
				Integer patientId = patient.getPatientId();
				//add the immunization list for the patient to the cached lookup list
				ImmunizationForecastLookup.addImmunizationList(patientId, immunizationList);
			}
			
		}
		catch (Exception e) {
			ATDError error = new ATDError("Error", "Query Immunization List Connection", "mrn: "+mrn + " "+ e.getMessage(), Util
			        .getStackTrace(e), new Date(), null);
			ATDService atdService = Context.getService(ATDService.class);
			atdService.saveError(error);
			this.exception = new QueryImmunizationsException("Query Immunization List Connection timed out", error);
		}
		finally {
			Context.closeSession();
		}
	}
	
	public QueryImmunizationsException getException() {
		return this.exception;
	}
	
}
