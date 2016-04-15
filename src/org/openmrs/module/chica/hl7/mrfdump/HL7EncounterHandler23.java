/**
 * 
 */
package org.openmrs.module.chica.hl7.mrfdump;

import java.util.Date;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.PersonName;
import org.openmrs.module.chica.hl7.ZPV;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.sockethl7listener.HL7EncounterHandler;
import org.openmrs.module.sockethl7listener.Provider;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v23.datatype.CX;
import ca.uhn.hl7v2.model.v23.datatype.ST;
import ca.uhn.hl7v2.model.v23.datatype.TS;
import ca.uhn.hl7v2.model.v23.datatype.XCN;
import ca.uhn.hl7v2.model.v23.message.ADT_A01;
import ca.uhn.hl7v2.model.v23.message.ORU_R01;
import ca.uhn.hl7v2.model.v23.segment.IN1;
import ca.uhn.hl7v2.model.v23.segment.PV1;
import ca.uhn.hl7v2.model.v23.segment.PV2;
import ca.uhn.hl7v2.model.v23.segment.MSH;
import ca.uhn.hl7v2.model.v23.segment.OBR;

/**
 * @author tmdugan
 * 
 */
public class HL7EncounterHandler23 implements HL7EncounterHandler{
	
	protected final Log logger = LogFactory.getLog(getClass());
	
	//doctor name has a different format in chica messages
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
			this.logger.warn("Unable to parse doctor name from PV1. Message: "
					+ e.getMessage());
		}
		if (doctor != null)
		{
			String doctorName = null;
			String lastName = null;
			String firstName = null;

			ST doctorFN = doctor.getFamilyName();

			if (doctorFN != null)
			{
				doctorName = doctorFN.getValue();
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

			name.setGivenName(firstName);
			name.setFamilyName(lastName);
		}
		return name;
	}

	//-----Set additional chica only encounter attributes
	
	public Date getAppointmentTime(Message message)
	{
		PV2 pv2 = getPV2(message);
		return TranslateDate(pv2.getExpectedAdmitDate());
	}

	private Date TranslateDate(TS date){
		return HL7ObsHandler23.TranslateDate(date);
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
		
		return null;
	}

	private PV2 getPV2(ORU_R01 oru)
	{
		return oru.getRESPONSE().getPATIENT().getVISIT().getPV2();
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
	
	protected PV1 getPV1(Message message)
	{
		if (message instanceof ORU_R01)
		{
			return getPV1((ORU_R01) message);
		}
		
		return null;
	}
	
	private PV1 getPV1(ORU_R01 oru)
	{
		return oru.getRESPONSE().getPATIENT().getVISIT().getPV1();
	}

	public Date getEncounterDate(Message message)
	{
		MSH msh = getMSH(message);
		TS timeStamp = null;
		if (message instanceof ORU_R01)
		{
			OBR obr = HL7ObsHandler23.getOBR((ORU_R01) message, 0);
			timeStamp = obr.getObservationDateTime();
		} 
		if (timeStamp != null)
		{
			ST dtm = timeStamp.getTimeOfAnEvent();
			if (dtm == null || dtm.getValue() == null)
			{
				timeStamp = msh.getDateTimeOfMessage();
			}
		}
		if (timeStamp != null)
		{
			return TranslateDate(timeStamp);
		}
		return null;
	}

	public static MSH getMSH(Message message)
	{
		if (message instanceof ORU_R01)
		{
			return HL7ObsHandler23.getMSH((ORU_R01) message);
		}
		
		return null;
	}
	
	public Provider getProvider(Message message)
	{
		Provider provider = new Provider();
		XCN doctor = null;
		PV1 pv1 = getPV1(message);
		try
		{
			doctor = pv1.getAttendingDoctor(0);

			// Load provider object with PV1 information

			if (doctor != null)
			{
				PersonName name = getDoctorName(message);
				provider.setFirstName(name.getGivenName());
				provider.setLastName(name.getFamilyName());
				String id = "";
				if (doctor.getIDNumber() != null)
				{
					id = doctor.getIDNumber().toString();
				}

				provider.setId(id);

				return provider;

			}

		} catch (HL7Exception e2)
		{
			this.logger.error("Unable to collect provider id from PV1 segment");
			this.logger.error(e2.getMessage());
			this.logger.error(Util.getStackTrace(e2));
		}
		return null;
	}
	
	public static OBR getOBR(Message message, int orderRep)
	{
		if (message instanceof ORU_R01)
		{
			return HL7ObsHandler23.getOBR((ORU_R01) message, orderRep);
		}

		return null;
	}
	
	/**
	 * DWE CHICA-633 
	 * Get visit number from PV1-19
	 */
	@Override
	public String getVisitNumber(Message message)
	{
		CX visitNumber = null;
		PV1 pv1 = getPV1(message);
		try
		{
			visitNumber = pv1.getVisitNumber();
		} 
		catch (RuntimeException e)
		{
			logger.error("Unable to parse visit number from PV1-19.", e);
		}

		if (visitNumber != null)
		{
			try
			{
				return visitNumber.getID().toString(); // This appears to be the only difference between version 2.3 and 2.5
			} 
			catch (RuntimeException e1)
			{
				logger.error("Visit number not available in PV1-19 segment.", e1);
			}
		}
		return null;
	}
}
