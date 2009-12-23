/**
 * 
 */
package org.openmrs.module.chica.advice;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.openmrs.Patient;
import org.openmrs.User;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.atd.hibernateBeans.ATDError;
import org.openmrs.module.atd.service.ATDService;
import org.openmrs.module.chica.MedicationListLookup;
import org.openmrs.module.chica.QueryMedicationListException;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.rgccd.Medication;
import org.openmrs.module.rgccd.service.CcdService;

/**
 * @author tmdugan
 */
public class QueryMeds implements Runnable {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private QueryMedicationListException exception = null;	
	private org.openmrs.Encounter encounter = null;
	
	private static final String PROVIDER_ID = "Provider ID";
	
	public QueryMeds(org.openmrs.Encounter encounter) {
		this.encounter = encounter;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		Context.openSession();
		String mrn = null;
		Integer locationId = null;
		String providerId = null;
		
		try {
			AdministrationService adminService = Context.getAdministrationService();
			PersonService ps = Context.getPersonService();
			Context.authenticate(adminService.getGlobalProperty("scheduler.username"), adminService
			        .getGlobalProperty("scheduler.password"));
			
			Patient patient = this.encounter.getPatient();
			Hibernate.initialize(patient); //prevent lazy initialization errors
			locationId = this.encounter.getLocation().getLocationId();
			
			CcdService ccdService = Context.getService(CcdService.class);
			mrn = patient.getPatientIdentifier().getIdentifier();
			
			String ccd = null;
			
				if (ps.getPersonAttributeTypeByName(PROVIDER_ID) != null) {
					User providerUser = this.encounter.getProvider();
					Hibernate.initialize(providerUser); //prevent lazy initialization errors
					if(providerUser.getAttribute(PROVIDER_ID) != null){
						providerId = providerUser.getAttribute(PROVIDER_ID).toString();
						if(providerId.length()==0){
							providerId = null;
						}
					}
					if (providerId == null) {
						providerId = adminService.getGlobalProperty("chica.genericProviderId");
					}
					ccd = ccdService.getCcdByMRN(mrn, providerId, locationId);
				}
			if (ccd != null) {
				List<Medication> medicationList = ccdService.createMedicationList(ccd, mrn);
				Integer patientId = patient.getPatientId();
				MedicationListLookup.addMedicationList(patientId, medicationList);
			}
			
		}
		catch (Exception e) {
			ATDError error = new ATDError("Error", "Query Medication List Connection", "mrn: "+mrn + " providerId: "+providerId+" locationId: "+locationId+" " + e.getMessage(), Util
			        .getStackTrace(e), new Date(), null);
			ATDService atdService = Context.getService(ATDService.class);
			atdService.saveError(error);
			this.exception = new QueryMedicationListException("Query Medication List Connection timed out", error);
		}
		finally {
			Context.closeSession();
		}
	}
	
	public QueryMedicationListException getException() {
		return this.exception;
	}
	
}
