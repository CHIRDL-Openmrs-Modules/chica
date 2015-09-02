/**
 * 
 */
package org.openmrs.module.chica.hl7;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
			Date todaysDate = new Date();
			
			//process A04 messages
			if (HL7_MESSAGE_TYPE_A04.equals(messageType)) {
				
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
			if (HL7_MESSAGE_TYPE_A08.equals(messageType)) {
				if (appointmentTime!=null&&appointmentTime.getDate() == todaysDate.getDate() && appointmentTime.getMonth() == todaysDate.getMonth()
				        && appointmentTime.getYear() == todaysDate.getYear()) {
					return false;
				}
			}
		}
		
		return true;
	}
}
