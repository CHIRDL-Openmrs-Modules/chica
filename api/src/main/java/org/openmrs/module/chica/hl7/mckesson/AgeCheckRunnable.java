package org.openmrs.module.chica.hl7.mckesson;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.threadmgmt.RunnableResult;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.LocationTagAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;

import ca.uhn.hl7v2.model.Message;

/**
 * Runnable to check if the patient meets the age limit restrictions to continue processing.
 * 
 * @author Steve McKee
 *
 */
public class AgeCheckRunnable implements RunnableResult<Boolean> {
	private Log log = LogFactory.getLog(this.getClass());
	private Boolean ageOk = Boolean.TRUE;
	private Message message;
	private String printerLocation;
	private String locationString;
	private Exception exception;
	
	/**
	 * Constructor method
	 * 
	 * @param message The HL7 message
	 * @param printerLocation The printer location
	 * @param locationString The location
	 */
	public AgeCheckRunnable(Message message, String printerLocation, String locationString) {
		this.message = message;
		this.printerLocation = printerLocation;
		this.locationString = locationString;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			ChirdlUtilBackportsService chirdlutilbackportsService = 
					Context.getService(ChirdlUtilBackportsService.class);
			LocationService locationService = Context.getLocationService();
			
			LocationTag locationTag = locationService.getLocationTagByName(this.printerLocation);
			Location location = locationService.getLocation(this.locationString);
			if (locationTag == null || location == null){
				return;
			}
			
			LocationTagAttributeValue ageLimitAttributeValue = chirdlutilbackportsService
					.getLocationTagAttributeValue(locationTag.getLocationTagId(), 
						ChirdlUtilConstants.LOC_TAG_ATTR_AGE_LIMIT_AT_CHECKIN ,location.getLocationId());
			
			if (ageLimitAttributeValue == null ) {
				return;
			}
			
			String ageLimitString = ageLimitAttributeValue.getValue();
			
			HL7PatientHandler25 patientHandler = new HL7PatientHandler25();
			Date dob = patientHandler.getBirthdate(this.message);
			int age = Util.getAgeInUnits(dob, new java.util.Date(), ChirdlUtilConstants.YEAR_ABBR);

			if (age >= Integer.parseInt(ageLimitString)){
				this.ageOk = Boolean.FALSE;
			}
		} catch (NumberFormatException e) {
			//String was either null, empty, or not a digit
			//No age limit value could be retrieved from attributes, so do not filter
			this.log.error("Error occurred parsing age limit string.", e);
			this.exception = e;
		} catch (Exception e){
			this.log.error("Exception while verifying patient age. ", e);
			this.exception = e;
		}
	}

	/**
	 * @see org.openmrs.module.chirdlutil.threadmgmt.RunnableResult#getResult()
	 */
	@Override
	public Boolean getResult() {
		return this.ageOk;
	}

	/**
	 * @see org.openmrs.module.chirdlutil.threadmgmt.RunnableResult#getException()
	 */
	@Override
	public Exception getException() {
		return this.exception;
	}
}
