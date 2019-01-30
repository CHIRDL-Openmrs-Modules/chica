/**
 * 
 */
package org.openmrs.module.chica.hl7;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.ChirdlLocationAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.module.sockethl7listener.HL7EncounterHandler;
import org.openmrs.module.sockethl7listener.HL7Filter;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.Terser;
import ca.uhn.hl7v2.validation.impl.NoValidation;

/**
 * @author tmdugan
 */
public class CurrentDateHL7Filter implements HL7Filter {
	
	private static final String HL7_MESSAGE_TYPE_A04 = "A04";
	
	private static final String HL7_MESSAGE_TYPE_A08 = "A08";
	
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * @see org.openmrs.module.sockethl7listener.HL7Filter#ignoreMessage(
	 * org.openmrs.module.sockethl7listener.HL7EncounterHandler, ca.uhn.hl7v2.model.Message, java.lang.String)
	 */
	@Override
    public boolean ignoreMessage(HL7EncounterHandler hl7EncounterHandler, Message message, String incomingMessageString) {
		
		String messageType = null;
		//get msh-9-2 (message type)
		PipeParser pipeParser = new PipeParser();
		pipeParser.setValidationContext(new NoValidation());
		Terser terser = new Terser(message);
		try {
			messageType = terser.get("/.MSH-9-2");
		}
		catch (HL7Exception e) {
			log.error("HL7 error parsing message", e);
		}
		
		if (hl7EncounterHandler instanceof org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25) {
			
			Date appointmentTime = ((org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25) hl7EncounterHandler)
			        .getAppointmentTime(message);
			String locationString = ((org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25) hl7EncounterHandler)
			        .getLocation(message);
			
			if (StringUtils.isBlank(locationString)) {
			    return false;
			}
			
		    Location location = Context.getLocationService().getLocation(locationString);
		    if (location == null) {
		        return false;
		    }
		    
		    //Filter messages by appointment if location specifies
		    ChirdlUtilBackportsService cubService = Context.getService(ChirdlUtilBackportsService.class);
            ChirdlLocationAttributeValue filterByAppointment = cubService.getLocationAttributeValue(
                location.getLocationId(), ChirdlUtilConstants.LOCATION_ATTR_FILTER_HL7_BY_APPOINTMENT);
            
            if (filterByAppointment == null || 
                    !ChirdlUtilConstants.GENERAL_INFO_TRUE.equalsIgnoreCase(filterByAppointment.getValue())) {
                return false;
            }
			
			Calendar appointment = Calendar.getInstance();
			Calendar today = Calendar.getInstance();
			
			//process A04 messages
			if (HL7_MESSAGE_TYPE_A04.equals(messageType)) {
				
				//if there is no appointment time, process the A04
				//it is a walkin
				if (appointmentTime == null) {
					return false;
				}
				
				appointment.setTime(appointmentTime);
				
				//if there is an appointment time and it is today,
				//process the A04. If not, it is a preregistration and
				//should be ignored
				if (appointment.get(Calendar.YEAR) == today.get(Calendar.YEAR)
				        && appointment.get(Calendar.MONTH) == today.get(Calendar.MONTH)
				        && appointment.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)) {
					return false;
				}
			}
			
			//Only process A08's with a non-null appointment time
			//and an appointment time that is today
			//This will ignore updates on different days
			if (HL7_MESSAGE_TYPE_A08.equals(messageType)) {
				if (appointmentTime == null) {
					return true;
				}
				
				appointment.setTime(appointmentTime);
				
				if (appointment.get(Calendar.YEAR) == today.get(Calendar.YEAR)
				        && appointment.get(Calendar.MONTH) == today.get(Calendar.MONTH)
				        && appointment.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)) {
					return false;
				}
			}
		}
		
		return true;
	}
}
