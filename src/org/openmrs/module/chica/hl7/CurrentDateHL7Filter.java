/**
 * 
 */
package org.openmrs.module.chica.hl7;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.sockethl7listener.HL7EncounterHandler;
import org.openmrs.module.sockethl7listener.HL7Filter;

import ca.uhn.hl7v2.model.Message;

/**
 * @author tmdugan
 */
public class CurrentDateHL7Filter implements HL7Filter {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	public boolean ignoreMessage(HL7EncounterHandler hl7EncounterHandler, Message message, String incomingMessageString) {
		
		if (hl7EncounterHandler instanceof org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25) {
			
			Date appointmentTime = ((org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25) hl7EncounterHandler)
			        .getAppointmentTime(message);
			Date todaysDate = new Date();
			
			//process A04 messages
			if (message instanceof ca.uhn.hl7v2.model.v22.message.ADT_A04) {
				
				//if there is no appointment time, process the A04
				//it is a walkin
				if (appointmentTime == null) {
					return false;
				}
				
				//if there is an appointment time and it is today,
				//process the A04. If not, it is a preregistration and
				//should be ignored
				if (appointmentTime.getDate() == todaysDate.getDate() && appointmentTime.getMonth() == todaysDate.getMonth()
				        && appointmentTime.getYear() == todaysDate.getYear()) {
					return false;
				}
			}
			
			//Only process A08's with a non-null appointment time
			//and an appointment time that is today
			//This will ignore updates on different days
			if (message instanceof ca.uhn.hl7v2.model.v22.message.ADT_A08) {
				if (appointmentTime!=null&&appointmentTime.getDate() == todaysDate.getDate() && appointmentTime.getMonth() == todaysDate.getMonth()
				        && appointmentTime.getYear() == todaysDate.getYear()) {
					return false;
				}
			}
		}
		
		return true;
	}
}
