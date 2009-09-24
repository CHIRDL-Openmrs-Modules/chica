/**
 * 
 */
package org.openmrs.module.chica.hl7.mckesson;

import org.openmrs.PersonName;
import org.openmrs.module.chica.hl7.ZPV;
import org.openmrs.module.dss.util.Util;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.datatype.FN;
import ca.uhn.hl7v2.model.v25.datatype.ST;
import ca.uhn.hl7v2.model.v25.datatype.XCN;
import ca.uhn.hl7v2.model.v25.segment.IN1;
import ca.uhn.hl7v2.model.v25.segment.PV1;

/**
 * @author tmdugan
 * 
 */
public class HL7EncounterHandler25 extends
		org.openmrs.module.chica.hl7.sms.HL7EncounterHandler25
{
	//doctor name for mckesson messages separates first and last name
	//with ^
	@Override
	protected PersonName getDoctorName(Message message)
	{
		PersonName name = new PersonName();
		XCN doctor = null;
		PV1 pv1 = getPV1(message);
		try
		{
			doctor = pv1.getAttendingDoctor(0);
		} catch (HL7Exception e)
		{
			logger.warn("Unable to parse doctor name from PV1. Message: "
					+ e.getMessage());
		}
		if (doctor != null)
		{
			String lastName = null;
			String firstName = null;

			FN doctorFN = doctor.getFamilyName();

			if (doctorFN != null)
			{
				ST doctorST = doctorFN.getSurname();

				if (doctorST != null)
				{
					lastName = doctorST.getValue();
					if (lastName != null)
					{
						if (lastName.startsWith("MC - "))
						{
							lastName = lastName.substring(5);
						}
					}
				}
			}

			ST doctorGivenST = doctor.getGivenName();

			if (doctorGivenST != null)
			{
				firstName = doctorGivenST.getValue();
			}

			name.setGivenName(firstName);
			name.setFamilyName(lastName);
		}
		return name;
	}
	
	//for mckesson messages, printerLocation prefixed by 'PEDS'
	@Override
	public String getPrinterLocation(Message message,String incomingMessageString)
	{
		ZPV zpv = new ZPV();
		zpv.loadZPVSegment(incomingMessageString);
		return zpv.getPrinterLocation();
	}
	
	public String getInsurancePlan(Message message)
	{
		IN1 in1 = getIN1(message);
		return in1.getInsurancePlanID().getIdentifier().getValue();
	}
	
	public String getInsuranceCarrier(Message message)
	{
		try
		{
			IN1 in1 = getIN1(message);
			if(in1.getInsuranceCompanyID(0)!= null){
				return in1.getInsuranceCompanyID(0).getIDNumber().getValue();
			}
		} catch (HL7Exception e)
		{
			logger.error(e.getMessage());
			logger.error(Util.getStackTrace(e));
		}
		return null;
	}
}
