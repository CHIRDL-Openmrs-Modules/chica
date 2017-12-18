/**
 * 
 */
package org.openmrs.module.chica.hl7;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.LocationTagAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import org.openmrs.module.sockethl7listener.HL7EncounterHandler;
import org.openmrs.module.sockethl7listener.HL7Filter;

import ca.uhn.hl7v2.model.Message;

/**
 * @author tmdugan
 * 
 */
public class PrinterLocationHL7Filter implements HL7Filter
{
	protected final Log log = LogFactory.getLog(getClass());
	
	public boolean ignoreMessage(HL7EncounterHandler hl7EncounterHandler,
			Message message,String incomingMessageString)
	{
		ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);

		String printerLocation = null;
		String locationString = null;
		String locationTagAttributeName = "ActivePrinterLocation";

		if (hl7EncounterHandler instanceof org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25)
		{

			printerLocation = ((org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25) hl7EncounterHandler)
					.getPrinterLocation(message,incomingMessageString);
			locationString = ((org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25) hl7EncounterHandler)
					.getLocation(message);
		}

		// get the location tag that matches the printer location
		LocationService locationService = Context.getLocationService();
		Location location = locationService.getLocation(locationString);
		
		if(location == null){
			log.error("Location "+locationString+" does not exist. Cannot process this message.");
			return true;
		}
		
		Set<LocationTag> locationTags = location.getTags();
		
		//there are no location tags mapped for this location so
		//don't filter
		if(locationTags == null||locationTags.size()==0){
			return false;
		}
		
		LocationTag targetLocationTag = null;
		for (LocationTag locationTag : locationTags)
		{ 
			if (locationTag.getName().equalsIgnoreCase(printerLocation)) // CHICA-1151 replace getTag() with getName()
			{
				targetLocationTag = locationTag;
				break;
			}
		}
		
		if (targetLocationTag != null)
		{
			LocationTagAttributeValue locationTagAttributeValue = chirdlutilbackportsService.getLocationTagAttributeValue(targetLocationTag
					.getLocationTagId(), locationTagAttributeName,location.getLocationId());
			if (locationTagAttributeValue != null)
			{
				String activePrinterLocationString = locationTagAttributeValue
						.getValue();
				if (activePrinterLocationString.equalsIgnoreCase("true"))
				{
					return false; // don't ignore this location because it is
									// active
				}
			}
			
			log.error("Location tag "+printerLocation+" for location "+locationString+" is not set as an active " +
				"printer location (ActivePrinterLocation). Cannot process this message.");
			return true;
		}

		log.error("No location tag "+printerLocation+" found for location "+locationString+". Cannot process this message.");
		return true;
	}
}
