/**
 * 
 */
package org.openmrs.module.chica.hl7;

import java.util.Set;

import org.openmrs.Location;
import org.openmrs.LocationTag;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.sockethl7listener.HL7EncounterHandler;
import org.openmrs.module.sockethl7listener.HL7Filter;
import org.openmrs.module.chica.hibernateBeans.LocationTagAttributeValue;
import org.openmrs.module.chica.service.ChicaService;

import ca.uhn.hl7v2.model.Message;

/**
 * @author tmdugan
 * 
 */
public class PrinterLocationHL7Filter implements HL7Filter
{
	public boolean ignoreMessage(HL7EncounterHandler hl7EncounterHandler,
			Message message,String incomingMessageString)
	{
		ChicaService chicaService = Context
				.getService(ChicaService.class);
		String printerLocation = null;
		String locationString = null;
		String locationTagAttributeName = "ActivePrinterLocation";

		if (hl7EncounterHandler instanceof org.openmrs.module.chica.hl7.sms.HL7EncounterHandler25)
		{

			printerLocation = ((org.openmrs.module.chica.hl7.sms.HL7EncounterHandler25) hl7EncounterHandler)
					.getPrinterLocation(message,incomingMessageString);
			locationString = ((org.openmrs.module.chica.hl7.sms.HL7EncounterHandler25) hl7EncounterHandler)
					.getLocation(message);
		}

		// get the location tag that matches the printer location
		LocationService locationService = Context.getLocationService();
		Location location = locationService.getLocation(locationString);
		Set<LocationTag> locationTags = location.getTags();
		
		//there are no location tags mapped for this location so
		//don't filter
		if(locationTags == null||locationTags.size()==0){
			return false;
		}
		
		LocationTag targetLocationTag = null;
		for (LocationTag locationTag : locationTags)
		{
			if (locationTag.getTag().equalsIgnoreCase(printerLocation))
			{
				targetLocationTag = locationTag;
				break;
			}
		}
		if (targetLocationTag != null)
		{
			LocationTagAttributeValue locationTagAttributeValue = chicaService.getLocationTagAttributeValue(targetLocationTag
					.getLocationTagId(), locationTagAttributeName);
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
		}

		return true;
	}
}
