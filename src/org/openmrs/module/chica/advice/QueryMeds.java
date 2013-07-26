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
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.MedicationListLookup;
import org.openmrs.module.chica.QueryMedicationListException;
import org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.Error;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.module.rgccd.Medication;
import org.openmrs.module.rgccd.service.CcdService;

/**
 * @author tmdugan
 */
public class QueryMeds implements  ChirdlRunnable {
	
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
			PatientService patientService = Context.getPatientService();
			Context.authenticate(adminService.getGlobalProperty("scheduler.username"), adminService
			        .getGlobalProperty("scheduler.password"));
			
			Patient patient = this.encounter.getPatient();
			patient = patientService.getPatient(patient.getPatientId());//lookup to prevent lazy initialization errors
			locationId = this.encounter.getLocation().getLocationId();
			
			CcdService ccdService = Context.getService(CcdService.class);
			mrn = patient.getPatientIdentifier().getIdentifier();
			
			String ccd = null;
			
				if (ps.getPersonAttributeTypeByName(PROVIDER_ID) != null) {
					UserService userService = Context.getUserService();
					List<User> providers = userService.getUsersByPerson(this.encounter.getProvider(), true);
					User providerUser = null;
					if(providers != null &&providers.size()>0){
						providerUser = providers.get(0);
					}
					Hibernate.initialize(providerUser); //prevent lazy initialization errors
					if(providerUser.getPerson().getAttribute(PROVIDER_ID) != null){
						providerId = providerUser.getPerson().getAttribute(PROVIDER_ID).toString();
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
			Error error = new Error("Error", "Query Medication List Connection", "mrn: "+mrn + " providerId: "+providerId+" locationId: "+locationId+" " + e.getMessage(), Util
			        .getStackTrace(e), new Date(), null);
			ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);
			chirdlutilbackportsService.saveError(error);
			this.exception = new QueryMedicationListException("Query Medication List Connection timed out", error);
		}
		finally {
			Context.closeSession();
		}
	}
	
	public QueryMedicationListException getException() {
		return this.exception;
	}

	/**
     * @see org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable#getName()
     */
    public String getName() {
	    return "Query Meds (Encounter: " + encounter.getEncounterId() + ")";

    }

	/**
     * @see org.openmrs.module.chirdlutil.threadmgmt.ChirdlRunnable#getPriority()
     */
    public int getPriority() {
    	return ChirdlRunnable.PRIORITY_FIVE;
    }
	
}
