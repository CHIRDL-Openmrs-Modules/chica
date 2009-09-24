/**
 * 
 */
package org.openmrs.module.chica.hl7.sms;

import java.util.Date;
import java.util.StringTokenizer;

import org.openmrs.PersonName;
import org.openmrs.module.chica.hl7.ZPV;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.datatype.FN;
import ca.uhn.hl7v2.model.v25.datatype.ST;
import ca.uhn.hl7v2.model.v25.datatype.XCN;
import ca.uhn.hl7v2.model.v25.message.ADT_A01;
import ca.uhn.hl7v2.model.v25.message.ORU_R01;
import ca.uhn.hl7v2.model.v25.segment.IN1;
import ca.uhn.hl7v2.model.v25.segment.PV1;
import ca.uhn.hl7v2.model.v25.segment.PV2;

/**
 * @author tmdugan
 * 
 */
public class HL7EncounterHandler25 extends
		org.openmrs.module.sockethl7listener.HL7EncounterHandler25
{
	//doctor name has a different format in chica messages
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
			String doctorName = null;
			String lastName = null;
			String firstName = null;

			FN doctorFN = doctor.getFamilyName();

			if (doctorFN != null)
			{
				ST doctorST = doctorFN.getSurname();

				if (doctorST != null)
				{
					doctorName = doctorST.getValue();
					if (doctorName != null)
					{
						if (doctorName.startsWith("MC - "))
						{
							doctorName = doctorName.substring(5);
						}

						StringTokenizer tokenizer = new StringTokenizer(
								doctorName, " ");
						if (tokenizer.hasMoreTokens())
						{
							lastName = tokenizer.nextToken();
						}
						if (tokenizer.hasMoreTokens())
						{
							firstName = tokenizer.nextToken();
						}
					}
				}
			}

			name.setGivenName(firstName);
			name.setFamilyName(lastName);
		}
		return name;
	}

	//-----Set additional chica only encounter attributes
	
	public Date getAppointmentTime(Message message)
	{
		PV2 pv2 = getPV2(message);
		return TranslateDate(pv2.getExpectedAdmitDateTime());
	}

	public String getInsuranceCode(Message message)
	{
		IN1 in1 = getIN1(message);
		return in1.getInsurancePlanID().getIdentifier().getValue();
	}

	public String getLocation(Message message)
	{
		PV1 pv1 = getPV1(message);
		return pv1.getAssignedPatientLocation().getPointOfCare().getValue();
	}

	public String getPrinterLocation(Message message,String incomingMessageString)
	{
		String printerLocation = null;
		ZPV zpv = new ZPV();
		zpv.loadZPVSegment(incomingMessageString);
		String printerLocationField = zpv.getPrinterLocation();
		int index = printerLocationField.indexOf("PED");
		if (index > -1 && printerLocationField.length() > index + 1)
		{
			printerLocation = printerLocationField.substring(index + 3);

		}
		return printerLocation;
	}

	protected PV2 getPV2(Message message)
	{
		if (message instanceof ORU_R01)
		{
			return getPV2((ORU_R01) message);
		}
		if (message instanceof ADT_A01)
		{
			return getPV2((ADT_A01) message);
		}
		return null;
	}

	private PV2 getPV2(ORU_R01 oru)
	{
		return oru.getPATIENT_RESULT().getPATIENT().getVISIT().getPV2();
	}

	private PV2 getPV2(ADT_A01 adt)
	{
		return adt.getPV2();
	}
	
	protected IN1 getIN1(Message message)
	{
		if (message instanceof ADT_A01)
		{
			return getIN1((ADT_A01) message);
		}
		return null;
	}

	private IN1 getIN1(ADT_A01 adt)
	{
		return adt.getINSURANCE().getIN1();
	}
}
