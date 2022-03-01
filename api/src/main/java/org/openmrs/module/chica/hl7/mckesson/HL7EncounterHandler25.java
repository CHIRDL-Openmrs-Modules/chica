/**
 * 
 */
package org.openmrs.module.chica.hl7.mckesson;

import java.util.Date;

import org.openmrs.PersonName;
import org.openmrs.module.chica.hl7.ZPV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.datatype.FN;
import ca.uhn.hl7v2.model.v25.datatype.ST;
import ca.uhn.hl7v2.model.v25.datatype.TS;
import ca.uhn.hl7v2.model.v25.datatype.XCN;
import ca.uhn.hl7v2.model.v25.message.ADT_A01;
import ca.uhn.hl7v2.model.v25.message.ORU_R01;
import ca.uhn.hl7v2.model.v25.segment.IN1;
import ca.uhn.hl7v2.model.v25.segment.MSH;
import ca.uhn.hl7v2.model.v25.segment.OBR;
import ca.uhn.hl7v2.model.v25.segment.PV1;
import ca.uhn.hl7v2.model.v25.segment.PV2;

/**
 * @author tmdugan
 * 
 */
public class HL7EncounterHandler25 extends
	org.openmrs.module.sockethl7listener.HL7EncounterHandler25
{
	//-----Set additional chica only encounter attributes
	private static final Logger log = LoggerFactory.getLogger(HL7EncounterHandler25.class);
	
	public Date getAppointmentTime(Message message)
	{
		MSH msh = getMSH(message);
		String sendingFacility = msh.getSendingFacility().getNamespaceID().getValue();
		if ("ECW".equalsIgnoreCase(sendingFacility)) {
			PV1 pv1 = getPV1(message);
			return TranslateDate(pv1.getAdmitDateTime());
		}
		
		PV2 pv2 = getPV2FromMessage(message);
		if(pv2 != null){
		    return TranslateDate(pv2.getExpectedAdmitDateTime()); 
		}
		return null;
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

	private PV2 getPV2FromMessage(Message message)
	{
		if (message instanceof ORU_R01)
		{
			return getPV2FromORU((ORU_R01) message);
		}
		if (message instanceof ADT_A01)
		{
			return getPV2FromADT((ADT_A01) message);
		}
		return null;
	}

	private PV2 getPV2FromORU(ORU_R01 oru)
	{
		return oru.getPATIENT_RESULT().getPATIENT().getVISIT().getPV2();
	}

	private PV2 getPV2FromADT(ADT_A01 adt)
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
		} catch (Exception e)
		{
			log.warn("Unable to parse doctor name from PV1.", e);
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
	
	public String getInsuranceName(Message message)
	{
		try
		{
			IN1 in1 = getIN1(message);
			if(in1.getInsuranceCompanyName(0)!= null){
				return in1.getInsuranceCompanyName(0).getOrganizationName().getValue();
			}
		} catch (Exception e)
		{
			log.error("Error parsing insurance name from IN1 segment.", e);
		}
		return null;
	}
	
	public String getInsuranceCarrier(Message message)
	{
		try
		{
			IN1 in1 = getIN1(message);
			if(in1.getInsuranceCompanyID(0)!= null){
				return in1.getInsuranceCompanyID(0).getIDNumber().getValue();
			}
		} catch (Exception e)
		{
			log.error("Error parsing insurance carrier from IN1 segment", e);
		}
		return null;
	}
	
	public Date getEncounterDate(Message message) {
		TS timeStamp = null;
		Date datetime = null;

		try {
			MSH msh = getMSH(message);
			OBR obr = getOBR(message, 0);
			timeStamp = null;
			String sendingFacility = msh.getSendingFacility().getNamespaceID().getValue();
			if ("ECW".equalsIgnoreCase(sendingFacility) || "HNA500".equalsIgnoreCase(sendingFacility)) {
				if (message instanceof ORU_R01) {
					if (obr != null)
						timeStamp = obr.getObservationDateTime();
				} else if ((message instanceof ADT_A01)) {
					 if (msh != null){
						 timeStamp = msh.getDateTimeOfMessage();
				 	}
				}
			} else {
				if (message instanceof ORU_R01) {
					if (obr != null)
						timeStamp = obr.getObservationDateTime();
				} else if ((message instanceof ADT_A01)) {
					 if (msh != null){
						 PV1 pv1 = getPV1(message);
						 timeStamp = pv1.getAdmitDateTime();
				 	}
				}
			}
			
			if (timeStamp != null && timeStamp.getTime()!= null) { 
				datetime = TranslateDate(timeStamp);
			}else {
				log.error("A valid encounter date timestamp could not be " +
						"determined from MSH segment (for ADT messages)" +
						" or OBR segment (for ORU messages)");
			}
			
		} catch (Exception e) {
			log.error("Exception occurred parsing encounter date time from Hl7.",e);
		}

		return datetime;

	}
	
	/**
	 * DWE CHICA-492
	 * 
	 * Get insurance plan code from IN1-35 for IUH Cerner integration
	 * @param message
	 * @return insurance plan code
	 */
	public String getInsuranceCompanyPlan(Message message)
	{
		IN1 in1 = getIN1(message);
		return in1.getCompanyPlanCode().getValue();
	}
}
