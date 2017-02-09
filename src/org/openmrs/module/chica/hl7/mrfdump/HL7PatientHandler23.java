/**
 * 
 */
package org.openmrs.module.chica.hl7.mrfdump;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAddress;
import org.openmrs.PersonName;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.sockethl7listener.HL7PatientHandler;

import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v23.datatype.CX;
import ca.uhn.hl7v2.model.v23.datatype.IS;
import ca.uhn.hl7v2.model.v23.datatype.ST;
import ca.uhn.hl7v2.model.v23.datatype.XPN;
import ca.uhn.hl7v2.model.v23.segment.PID;
import ca.uhn.hl7v2.model.v25.datatype.CE;
import ca.uhn.hl7v2.model.v23.message.ORU_R01;
import ca.uhn.hl7v2.model.v23.datatype.TS;
import ca.uhn.hl7v2.model.v23.datatype.XAD;
import ca.uhn.hl7v2.model.v23.segment.NK1;
import ca.uhn.hl7v2.model.v23.datatype.XTN;

/**
 * @author tmdugan
 * 
 */
public class HL7PatientHandler23 implements HL7PatientHandler
{

	protected final Log logger = LogFactory.getLog(getClass());
	
	public String getMothersName(Message message)
	{
		return null;
	}

	//------get additional person attributes for chica patients
	public String getSSN(Message message)
	{
		PID pid = this.getPID(message);
		ST ssnST = pid.getSSNNumberPatient();
		String ssn = null;
		if (ssnST != null)
		{
			try
			{
				ssn = ssnST.getValue();

				// Manual checkin is writing this incorrectly.. VA 11-17-05
				if (ssn != null && ssn.equals("--"))
				{
					ssn = "";
				}
			} catch (RuntimeException e1)
			{
				this.logger
						.debug("Warning: SSN information not available in PID segment.");
			}
		}
		return ssn;
	}

	public String getReligion(Message message)
	{
		PID pid = this.getPID(message);
		IS religionCE = pid.getReligion();
		String religion = null;
		if (religionCE != null)
		{
			try
			{
				religion = religionCE.getValue();
			} catch (RuntimeException e1)
			{
				this.logger
						.debug("Warning: religion information not available in PID segment.");
			}
		}
		return religion;
	}

	public String getMaritalStatus(Message message)
	{
		PID pid = getPID(message);
		IS[] maritalIS = pid.getMaritalStatus();
		String marital = null;

		if (maritalIS != null)
		{
			try
			{
				marital = maritalIS[0].getValue();

			} catch (RuntimeException e1)
			{
				this.logger
						.debug("Warning: marital information not available in PID segment.");
			}

		}
		return marital;
	}

	public String getMothersMaidenName(Message message)
	{
		PID pid = getPID(message);
		String maiden = null;
		XPN maidenXPN = pid.getMotherSMaidenName();

		if (maidenXPN != null)
		{
			try
			{
				ST maidenFN = maidenXPN.getFamilyName();

				if (maidenFN != null)
				{
					maiden = maidenFN.getValue();
				}
			} catch (RuntimeException e1)
			{
				this.logger
						.debug("Warning: maiden information not available in PID segment.");
			}

		}
		return maiden;
	}

	//----MRN for chica messages has an explicit check digit
	public String getMRN(CX ident)
	{
		String stIdent = null;

		ST id;
		if ((id = ident.getID()) != null)
		{
			stIdent = id.getValue();
			ST checkDigitST = ident.getCheckDigit();

			if (checkDigitST!=null&&checkDigitST.getValue() != null)
			{
				String checkDigit = checkDigitST.getValue();
				stIdent += "-" + checkDigit;
			}
			stIdent = Util.removeLeadingZeros(stIdent);
		}
		return stIdent;
	}
	
	public PID getPID(Message message)
	{
		if (message instanceof ORU_R01)
		{
			return HL7ObsHandler23.getPID((ORU_R01) message);
		}
		
		return null;
	}

	public List<PersonAddress> getAddresses(Message message)
	{
		// ***For Newborn screening we are using the next-of-kin address
		// and not the patient address from PID
		PID pid = getPID(message);
		NK1 nk1 = getNK1(message);
		List<PersonAddress> addresses = new ArrayList<PersonAddress>();
		try
		{
			XAD[] xadAddresses = pid.getPatientAddress(); // PID address
			if (xadAddresses.length == 0)
			{
				xadAddresses = nk1.getAddress();
			}
			if (xadAddresses.length == 0)
			{
				XAD xadAddress = null;
				addresses.add(getAddress(xadAddress));
			} else
			{
				for (XAD xadAddress : xadAddresses)
				{
					addresses.add(getAddress(xadAddress));
				}
			}

		} catch (RuntimeException e)
		{
			this.logger.warn("Unable to collect  address from PID);", e);
		}
		return addresses;
	}
	
	protected PersonAddress getAddress(XAD xad)
	{
		PersonAddress address = new PersonAddress();
		ST streetAddress;
		String city = "";
		String stateProvince = "";
		String country = "";
		String postalCode = "";
		String streetAddrString = "";
		String otherDesignation = "";

		if (xad != null)
		{
			streetAddress = xad.getStreetAddress();
			streetAddrString = streetAddress.getValue();
			otherDesignation = xad.getOtherDesignation().toString();
			city = xad.getCity().toString();

			stateProvince = xad.getStateOrProvince().toString();
			postalCode = xad.getZipOrPostalCode().toString();
			country = xad.getCountry().toString();

		}

		address.setAddress1(streetAddrString);
		address.setAddress2(otherDesignation);
		address.setCityVillage(city);
		address.setStateProvince(stateProvince);
		address.setCountry(country);
		address.setPostalCode(postalCode);
		address.setPreferred(true);

		return address;
	}

	public Date getBirthdate(Message message)
	{
		PID pid = getPID(message);
		TS DOB = pid.getDateOfBirth();
		return TranslateDate(DOB);
	}
	
	private Date TranslateDate(TS date){
		return HL7ObsHandler23.TranslateDate(date);
	}

	public String getBirthplace(Message message)
	{
		PID pid = getPID(message);
		String birthPlace = "";
		try
		{
			birthPlace = pid.getBirthPlace().toString();
			if (birthPlace == null)
			{
				birthPlace = " ";
			}
		} catch (RuntimeException e)
		{
			this.logger.warn("Unable to parse birthplace from PID. Message: ", e);
		}
		return birthPlace;
	}

	public String getCitizenship(Message message)
	{
		PID pid = getPID(message);
		IS ceCitizen = null;
		ceCitizen = pid.getCitizenship();
		String citizenString = ceCitizen.getValue();

		return citizenString;
	}

	public Date getDateChanged(Message message)
	{
		return null;
	}

	public Date getDeathDate(Message message)
	{
		PID pid = getPID(message);
		TS DDT = pid.getPatientDeathDateAndTime();
		Date ddt = TranslateDate(DDT);
		if (DDT.getTimeOfAnEvent().getValue() == null)
		{
			ddt = null;
		}
		return ddt;
	}

	public String getGender(Message message)
	{
		// Gender -- Based on meeting 04/10/2007
		// HL7 for newborn screening will contain F,M, or U, with no preceding
		// codes
		String g = "";
		PID pid = getPID(message);
		try
		{
			g = pid.getSex().getValue();
			if (g != null
					&& (g.toLowerCase().equals("m")
							|| g.toLowerCase().equals("f") || g.toLowerCase()
							.equals("u")))
			{
				return g;
			}
		} catch (RuntimeException e)
		{
			this.logger.warn("Unable to parse gender from PID. Message: ", e);
		}
		return null;
	}

	public Set<PatientIdentifier> getIdentifiers(Message message)
	{
		PID pid = getPID(message);
		CX[] identList = null;
		PatientService patientService = Context.getPatientService();
		Set<PatientIdentifier> identifiers = new TreeSet<org.openmrs.PatientIdentifier>();

		try
		{

			identList = pid.getPatientIDInternalID();
		} catch (RuntimeException e2)
		{
			// Unable to extract identifier from PID segment
			this.logger
					.error("Error extracting identifier from PID segment (MRN). ");
			// Still need to continue. Execute find match without the identifer
		}
		if (identList == null)
		{
			this.logger.warn(" No patient identifier available for this message.");
			// Still need to continue. Execute find match without the identifer
			return identifiers;
		}

		if (identList.length != 0)
		{
			// personAttrList ="mrn:";

			for (CX ident : identList)
			{
				// First set up the identifier type; We currently use MRN
				// Get the id number for the authorizing facility

				PatientIdentifierType pit = new PatientIdentifierType();
				PatientIdentifier pi = new PatientIdentifier();
				String stIdent = getMRN(ident);
				String assignAuth = "";

				if (stIdent != null)
				{
					assignAuth = ident.getAssigningAuthority().getNamespaceID()
							.getValue();

					if ((pit = patientService
							.getPatientIdentifierTypeByName("MRN_" + assignAuth)) == null)
					{
						pit = patientService
								.getPatientIdentifierTypeByName("MRN_OTHER");
						// this is temporary for gathering data
						this.logger
								.error("insert into patient_identifier_type"
										+ "(name, description, format, check_digit, "
										+ "creator, date_created, required, format_description) "
										+ "values('MRN_" + assignAuth + "', '"
										+ assignAuth
										+ "',null,0,1,'2008-07-03',0,null);");
					}
					pi.setIdentifierType(pit);
					pi.setIdentifier(stIdent);
					pi.setPreferred(true);

					identifiers.add(pi);

				} else
				{
					this.logger.error("No MRN in PID segement. ");
				}

			}
		}
		return identifiers;
	}

	public String[] getPatientIdentifierList(Message message)
	{
		if (!(message instanceof ORU_R01))
		{
			return null;
		}

		CX[] pIdentifierList = getPID(message)
				.getPatientIDInternalID();
		String[] patientIdentsAsString = new String[pIdentifierList.length];

		for (int i = 0; i < pIdentifierList.length; i++)
		{
			CX patId = pIdentifierList[i];
			patientIdentsAsString[i] = getMRN(patId);
		}
		return patientIdentsAsString;
	}

	public PersonName getPatientName(Message message)
	{
		PersonName name = new PersonName();
		PID pid = getPID(message);
		String ln = "", fn = "", mn = "";
		XPN[] xpn = null;
		try
		{
			xpn = pid.getPatientName();
			ST STln = xpn[0].getFamilyName();
			ST STfn = xpn[0].getGivenName();
			ST STmn = xpn[0].getMiddleInitialOrName();

			if (STln != null && STln.getValue() != null)
			{
				String lnvalue = org.openmrs.module.chirdlutil.util.Util
						.toProperCase(STln.getValue());
				if (lnvalue != null)
					ln = lnvalue;
			}

			if (STfn != null)
			{
				String fnvalue =  org.openmrs.module.chirdlutil.util.Util.toProperCase(STfn.getValue());
				if (fnvalue != null)
					fn = fnvalue;
			}

			if (STmn != null)
			{
				String mnvalue =  org.openmrs.module.chirdlutil.util.Util.toProperCase(STmn.getValue());
				if (mnvalue != null)
					mn = mnvalue;
			}

			name.setFamilyName(ln.replaceAll("\"", ""));
			name.setGivenName(fn.replaceAll("\"", ""));
			name.setMiddleName(mn.replaceAll("\"", ""));
			// set preferred to true because this method
			// deliberately just processes the first person name
			name.setPreferred(true);

		} catch (RuntimeException e)
		{
			this.logger.warn("Unable to parse patient name. Message: "
					+ e.getMessage());
		}

		return name;
	}

	public String getRace(Message message)
	{
		IS ceRace = null;
		PID pid = getPID(message);
		try
		{
			ceRace = pid.getRace();
		} catch (RuntimeException e)
		{
			this.logger.warn("Unable to parse race from PID. Message: "
					+ e.getMessage());
		}

		if (ceRace != null)
		{
			try
			{
				return ceRace.getValue();
			} catch (RuntimeException e1)
			{
				this.logger
						.debug("Warning: Race information not available in PID segment.");
			}
		}
		return null;
	}
	public String getTelephoneNumber(Message message)
	{
		PID pid = getPID(message);
		NK1 nk1 = getNK1(message);
		String tNumber = "";
		XTN[] telnumbers = pid.getPhoneNumberHome();

		if (telnumbers.length == 0&&nk1!=null)
		{
			telnumbers = nk1.getPhoneNumber();
		}

		if (telnumbers.length > 0)
		{
			tNumber = telnumbers[0].getPhoneNumber().toString();
		}
		return tNumber;
	}

	public Boolean isDead(Message message)
	{
		PID pid = getPID(message);
		boolean isDead = false;
		if (pid.getPatientDeathIndicator().getValue() != null)
		{
			isDead = pid.getPatientDeathIndicator().getValue().equals("1");
		}
		return isDead;
	}
	
	protected NK1 getNK1(Message message)
	{
		if (message instanceof ORU_R01)
		{
			return getNK1((ORU_R01) message);
		}
		
		return null;
	}
	
	private NK1 getNK1(ORU_R01 oru)
	{
		return null;
	}
	
	/**
	 * DWE CHICA-406
	 * @param message
	 */
	public String getAccountNumber(Message message)
	{
		return null;
	}

	/**
	 * @see org.openmrs.module.sockethl7listener.HL7PatientHandler#getNextOfKin(ca.uhn.hl7v2.model.Message)
	 */
    public String getNextOfKin(Message arg0) {
	    return null;
    }
    
    /**
	 * DWE CHICA-702
	 * 
	 * NOT IMPLEMENTED
	 * HL7 version 2.3 Parse ethnicity code from PID-22
	 * 
	 * @param message
	 * @return ethnicity code
	 */
	public String getEthnicity(Message message)
	{
		// Intentionally left empty
		return null;
	}
}
