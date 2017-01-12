/**
 * 
 */
package org.openmrs.module.chica.hl7.mckesson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.openmrs.PersonAddress;
import org.openmrs.module.chirdlutil.util.Util;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.datatype.CE;
import ca.uhn.hl7v2.model.v25.datatype.CX;
import ca.uhn.hl7v2.model.v25.datatype.FN;
import ca.uhn.hl7v2.model.v25.datatype.SAD;
import ca.uhn.hl7v2.model.v25.datatype.ST;
import ca.uhn.hl7v2.model.v25.datatype.XAD;
import ca.uhn.hl7v2.model.v25.datatype.XPN;
import ca.uhn.hl7v2.model.v25.segment.NK1;
import ca.uhn.hl7v2.model.v25.segment.PID;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.util.Terser;
import ca.uhn.hl7v2.validation.impl.NoValidation;

/**
 * @author tmdugan
 * 
 */
public class HL7PatientHandler25 extends
		org.openmrs.module.sockethl7listener.HL7PatientHandler25
{
	
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
				logger
						.debug("Warning: SSN information not available in PID segment.");
			}
		}
		return ssn;
	}

	public String getReligion(Message message)
	{
		PID pid = this.getPID(message);
		CE religionCE = pid.getReligion();
		String religion = null;
		if (religionCE != null)
		{
			try
			{
				ST religionST = religionCE.getIdentifier();

				if (religionST != null)
				{
					religion = religionST.getValue();
				}

			} catch (RuntimeException e1)
			{
				logger
						.debug("Warning: religion information not available in PID segment.");
			}
		}
		return religion;
	}

	public String getMaritalStatus(Message message)
	{
		PID pid = getPID(message);
		CE maritalCE = pid.getMaritalStatus();
		String marital = null;

		if (maritalCE != null)
		{
			try
			{
				ST maritalST = maritalCE.getIdentifier();

				if (maritalST != null)
				{
					marital = maritalST.getValue();
				}

			} catch (RuntimeException e1)
			{
				logger
						.debug("Warning: marital information not available in PID segment.");
			}

		}
		return marital;
	}

	public String getMothersMaidenName(Message message)
	{
		PID pid = getPID(message);
		String maiden = null;
		try
		{
			XPN maidenXPN = pid.getMotherSMaidenName(0);

			if (maidenXPN != null)
			{
				try
				{
					FN maidenFN = maidenXPN.getFamilyName();

					if (maidenFN != null)
					{
						ST maidenST = maidenFN.getSurname();
						if (maidenST != null)
						{
							maiden = maidenST.getValue();
						}
					}
				} catch (RuntimeException e1)
				{
					logger
							.debug("Warning: maiden information not available in PID segment.");
				}

			}
		} catch (HL7Exception e)
		{
			logger.error(e.getMessage());
			logger.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}
		return maiden;
	}

	//----MRN for chica messages has an explicit check digit
	@Override
	protected String getMRN(CX ident)
	{
		String stIdent = null;

		ST id;
		if ((id = ident.getIDNumber()) != null)
		{
			stIdent = id.getValue();
			ST checkDigitST = ident.getCheckDigit();

			if (checkDigitST != null && checkDigitST.getValue() != null 
					&& checkDigitST.getValue().length()>0)
			{
				String checkDigit = checkDigitST.getValue();
				stIdent += "-" + checkDigit;
			}else{
				if(stIdent!=null&&stIdent.length()>=2){
					stIdent = stIdent.substring(0, stIdent.length()-1)+"-"+stIdent.substring(stIdent.length()-1);
				}
			}
			stIdent = Util.removeLeadingZeros(stIdent);
		}
		return stIdent;
	}
	
	protected PersonAddress getAddress(XAD xad){

		PersonAddress address = new PersonAddress();
	
		if (xad == null){
			return null;
		}
		
		SAD streetAddress = xad.getStreetAddress();
		String streetAddrString = streetAddress.getStreetOrMailingAddress()
					.toString();
		String otherDesignation = xad.getOtherDesignation().toString();
		String city = xad.getCity().toString();
	
		String stateProvince = xad.getStateOrProvince().toString();
		String postalCode = xad.getZipOrPostalCode().toString();
		String country = xad.getCountry().toString();
		address.setAddress1(streetAddrString);
		address.setAddress2(otherDesignation);
		address.setCityVillage(city);
		address.setStateProvince(stateProvince);
		address.setCountry(country);
		address.setPostalCode(postalCode);
		UUID uuid = UUID.randomUUID();
		address.setUuid(uuid.toString());
		address.setPreferred(true);
	
		return address;
	}
	
	@Override
	public List<PersonAddress> getAddresses(Message message)
	{
		List<PersonAddress> addresses = new ArrayList<PersonAddress>();
		XAD[] xadAddresses = null;
		
		try
		{
			PID pid = getPID(message);
			NK1 nk1 = getNK1(message);
			
			if (pid != null ){
				
				xadAddresses = pid.getPatientAddress(); 
			}
			if ((xadAddresses == null || xadAddresses.length == 0) && nk1 != null){
					xadAddresses = nk1.getAddress();
			}
				
			for (XAD xadAddress : xadAddresses)
			{
				addresses.add(getAddress(xadAddress));
			}

		} catch (Exception e)
		{
			logger.warn("Unable to collect address from PID or NK1 for", e);
		}
		return addresses;
	}
	

	public String getIdentifierString(Message newMessage){
		String pid = null;
		try {
			//get pid-3-1 (mrn)
			PipeParser pipeParser = new PipeParser();
			pipeParser.setValidationContext(new NoValidation());
			Terser terser = new Terser(newMessage);
			pid = terser.get("/.PID-3-1");
		} catch (Exception e) {
			logger.error("MRF dump encoding error for Terser getting identifier from MRF dump." , e);
		} 
		return pid;
	}
	
	
	
}


