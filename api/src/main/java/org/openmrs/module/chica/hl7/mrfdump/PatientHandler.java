package org.openmrs.module.chica.hl7.mrfdump;

import java.util.Date;
import java.util.Properties;
import java.util.Set;

import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.sockethl7listener.HL7PatientHandler;

import ca.uhn.hl7v2.model.Message;

public class PatientHandler extends org.openmrs.module.sockethl7listener.PatientHandler
{

	private static final String ATTRIBUTE_RELIGION = "Religion";
	private static final String ATTRIBUTE_MARITAL = "Civil Status";
	private static final String ATTRIBUTE_MAIDEN = "Mother's maiden name";

	public PatientHandler()
	{
		super();
	}

	public PatientHandler(Properties prop)
	{
		this();
	}

	//save literal hl7 race value instead of mapping it
	@Override
	protected void setRace(Message message, Patient hl7Patient,
			Date encounterDate, HL7PatientHandler hl7PatientHandler)
	{
		String race = hl7PatientHandler.getRace(message);
		addAttribute(hl7Patient, ATTRIBUTE_RACE, race, encounterDate);
	}

	//set additional patient attributes
	@Override
	public Patient setPatientFromHL7(Message message, Date encounterDate,
			Location sendingFacility, HL7PatientHandler hl7PatientHandler)
	{
		Patient hl7Patient = super.setPatientFromHL7(message, encounterDate,
				sendingFacility, hl7PatientHandler);

		if (hl7PatientHandler instanceof org.openmrs.module.chica.hl7.mrfdump.HL7PatientHandler23)
		{
			// patient ssn
			String ssn = ((org.openmrs.module.chica.hl7.mrfdump.HL7PatientHandler23) hl7PatientHandler)
					.getSSN(message);
			if (ssn != null)
			{
				PatientIdentifierType type = this.patientService.getPatientIdentifierTypeByName(ChirdlUtilConstants.IDENTIFIER_TYPE_SSN); // CHICA-1151 replace getPatientIdentifierType() with getPatientIdentifierTypeByName()
				PatientIdentifier pi = new PatientIdentifier(ssn,type,sendingFacility);
				pi.setDateCreated(encounterDate);
				pi.setCreator(Context.getAuthenticatedUser());
				pi.setPatient(hl7Patient);
				hl7Patient.addIdentifier(pi);
			}
			// patient religion
			String religion = ((org.openmrs.module.chica.hl7.mrfdump.HL7PatientHandler23) hl7PatientHandler)
					.getReligion(message);
			if (religion != null)
			{
				addAttribute(hl7Patient, ATTRIBUTE_RELIGION, religion,
						encounterDate);
			}
			String marital = ((org.openmrs.module.chica.hl7.mrfdump.HL7PatientHandler23) hl7PatientHandler)
					.getMaritalStatus(message);
			if (marital != null)
			{
				addAttribute(hl7Patient, ATTRIBUTE_MARITAL, marital,
						encounterDate);
			}
			String maiden = ((org.openmrs.module.chica.hl7.mrfdump.HL7PatientHandler23) hl7PatientHandler)
					.getMothersMaidenName(message);
			if (maiden != null)
			{
				addAttribute(hl7Patient, ATTRIBUTE_MAIDEN, maiden,
						encounterDate);
			}
		}
		return hl7Patient;
	}

	//replace name of Inf with Baby
	@Override
	protected void setPatientName(Message message, Patient hl7Patient,
			Date encounterDate, HL7PatientHandler hl7PatientHandler)
	{
		super.setPatientName(message, hl7Patient, encounterDate,
				hl7PatientHandler);

		// check all patient names and replace 'Inf' with 'Baby'
		Set<PersonName> names = hl7Patient.getNames();

		for (PersonName name : names)
		{
			if (name.getGivenName().equals("Inf"))
			{
				name.setGivenName("Baby");
			}
		}
	}
}
