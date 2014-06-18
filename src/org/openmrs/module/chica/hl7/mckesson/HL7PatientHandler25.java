/**
 * 
 */
package org.openmrs.module.chica.hl7.mckesson;

import org.openmrs.module.chirdlutil.util.Util;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.datatype.CE;
import ca.uhn.hl7v2.model.v25.datatype.CX;
import ca.uhn.hl7v2.model.v25.datatype.FN;
import ca.uhn.hl7v2.model.v25.datatype.ST;
import ca.uhn.hl7v2.model.v25.datatype.XPN;
import ca.uhn.hl7v2.model.v25.segment.PID;

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
	
	
}
