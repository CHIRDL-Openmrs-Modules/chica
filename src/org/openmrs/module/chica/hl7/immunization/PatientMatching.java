/**
 * 
 */
package org.openmrs.module.chica.hl7.immunization;

import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.codec.language.Soundex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.api.EncounterService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v231.datatype.FN;
import ca.uhn.hl7v2.model.v231.datatype.XPN;
import ca.uhn.hl7v2.model.v231.group.VXX_V02_PIDNK1;
import ca.uhn.hl7v2.model.v231.message.VXR_V03;
import ca.uhn.hl7v2.model.v231.message.VXX_V02;
import ca.uhn.hl7v2.model.v231.segment.PID;
import ca.uhn.hl7v2.model.v231.segment.QRD;

/**
 * @author msheley
 *
 */


public class PatientMatching {

	
	private Log log = LogFactory.getLog(this.getClass());
	private String IMMUNIZATION_REGISTRY = "Immunization Registry";
	
	
	public Patient verifyPatientMatch( Patient chicaPatient,  Set<Patient> chirpPatients){
	
		
		Set<PersonName> chicaNames = chicaPatient.getNames();
		Set<PersonAddress> chicaAddresses = chicaPatient.getAddresses();
		int i = 1;
		
		//first check exact name matches
		for (Patient chirpPatient : chirpPatients){
					
					
			if (matchDOB(chirpPatient, chicaPatient) && 
					(compareId(chirpPatient, chicaPatient)
					 ||  compareNames(chirpPatient, chicaNames) ))
			{
				//CHIRP  and CHICA patients match.
				chirpPatient.getPatientIdentifier();
				log.info("Immunization: CHIRP SIIS# " + chirpPatient.getPatientIdentifier() + " and CHICA patient match exactly. Patient_id: " 
						+ chicaPatient.getPatientId());
				return chirpPatient;
			}
			 i++;			
		}
		
		//There is not an exact match.  Add check for soundex name, st, zip, nk..
		 i= 1;
		for (Patient chirpPatient : chirpPatients){
			
			
			if (matchDOB(chirpPatient, chicaPatient) && 
					(compareFirstNames(chirpPatient, chicaNames) &&
							(compareStreet(chirpPatient, chicaAddresses)
							+ compareZip(chirpPatient, chicaAddresses)
							+ compareNK1(chirpPatient, chicaPatient)) >=2 ))
			{
				//CHIRP  and CHICA patients match.
				log.info("Immunization: CHIRP SIIS#" + chirpPatient.getPatientIdentifier() + " and CHICA patient match with added checks. Patient_id: " 
					+ chicaPatient.getPatientId());
				return chirpPatient;
			}
			 i++;				
		}
		
		
		return null;
	}
	
	private boolean compareFirstNames( Patient chirpPatient, Set<PersonName> chicaNames){
			Soundex soundex = new Soundex();
			PersonName chirpName = chirpPatient.getPersonName();
			if (chirpName == null){
				return false;
			}
			String chirpFn = chirpName.getGivenName();
			//No chirp first name - cannot confirm match and prevents match 
			//chica and chirp empty strings.
			if (chirpFn == null || chirpFn.trim().equalsIgnoreCase("")){
				return false;
			}
			
			//check all chica names
			for (PersonName chicaName: chicaNames){
				String chicaFn = chicaName.getGivenName();
				Integer patient_id = chicaName.getPerson().getPersonId();
				
				log.info("Immunization: CHICA first name: " + soundex.encode(chicaFn) 
						+ " CHIRP first name: " + soundex.encode(chirpFn));
				
				if (chicaFn != null  &&  chicaFn.replaceAll("[^a-zA-Z0-9]+","")
							.equalsIgnoreCase(chirpFn.replaceAll("[^a-zA-Z0-9]+",""))){
					log.info("Immunization: First name matches. CHIRP= " + chirpFn + " CHICA= " + chicaFn 
							+ "PatientId = " + patient_id);
					return true;

				}
				
				if (chicaFn != null  &&  soundex.encode(chicaFn).equals(soundex.encode(chirpFn)))
				{
					log.info("Immunization: First name does not match but first name soundex matches."
							+ " PatientId = " + patient_id);
					return true;
				}
				log.info("Immunization: First names do not match. CHIRP= " + chirpFn + " CHICA= " + chicaFn 
						+ " PatientId = " + patient_id);

			}
							
			return false;
		}
	
		 boolean compareId( Patient chirpPatient
			, Patient chicaPatient){
		
		PatientIdentifier chirpIdentifier = chirpPatient.getPatientIdentifier();
	
		PatientIdentifier chicaIdentifier = 
			chicaPatient.getPatientIdentifier(IMMUNIZATION_REGISTRY);
		if (chicaIdentifier == null){
			return false;
		}
		
		
		if (chirpIdentifier != null  && 
				chicaIdentifier.getIdentifier()
				.equalsIgnoreCase(chirpIdentifier.getIdentifier())){
			log.info("Immunization: Registry IDs match for" + chicaPatient.getFamilyName()
					+ ", " + chicaPatient.getGivenName() + "," + chicaIdentifier.getIdentifier());
			return true;
		}

		return false;
	}
		
		private boolean compareNames( Patient chirpPatient, Set<PersonName> chicaNames){
			
			
			//PersonName chirpName = chirpPatient.getPersonName();
			Set<PersonName> names = chirpPatient.getNames();
			Integer personId = null;
			
		
			for (PersonName chirpName : names){
				
				String chirpLn = chirpName.getFamilyName();
				String chirpFn = chirpName.getGivenName();
				String chirpMn = chirpName.getMiddleName();
				for (PersonName chicaName: chicaNames){
					
					String chicaLn = chicaName.getFamilyName();
					String chicaFn = chicaName.getGivenName();
					String chicaMn = chicaName.getMiddleName();
					personId = chicaName.getPerson().getPersonId();
		
					if (chirpLn != null 
							&& chicaLn != null
							&& chicaLn.replaceAll("[^a-zA-Z0-9]+","")
								.equalsIgnoreCase(chirpLn.replaceAll("[^a-zA-Z0-9]+",""))){
						
					
						if ( chirpFn != null 
								&& chicaFn != null 
								&& chicaFn.replaceAll("[^a-zA-Z0-9]+","")
								.equalsIgnoreCase(chirpFn.replaceAll("[^a-zA-Z0-9]+",""))){
							log.info("Immunization: Both first and last name match. "+  chirpFn + " " + chirpLn);
							return true;
		
						}
							
					}
					
				}
				log.info("Immunization:  CHIRP fn: " + chirpFn + " and ln: " + chirpLn +
						"  do not match do not match any CHICA names.  " );
			}
			
			return false;
		}
		
		private Integer compareNK1(Patient chirpPatient, Patient chicaPatient){
			
			PersonAttribute chirpAttribute = chirpPatient.getAttribute("Next of Kin");
			PersonAttribute chicaAttribute = chicaPatient.getAttribute("Next of Kin");
			Integer patientId = chicaPatient.getPatientId();
			
			if (chirpAttribute == null || chicaAttribute == null ) {
				//unable to compare. 
				log.info("Immunization: NK attribute is null. PatientId = " + patientId);
				return 0;
			}
			
			String chirpNK = chirpAttribute.getValue();
			String chicaNK = chicaAttribute.getValue();
			
			if (chirpNK == null || chicaNK == null 
					|| chirpNK.equalsIgnoreCase("") || chicaNK.equalsIgnoreCase("")){
				log.info("Immunization: NK string is empty. PatientId = " + patientId);
				return 0;
			}
			
			String chirpFN;
			String chicaFN;
			String chicaLN;
			String chirpLN;
			
			int index = chirpNK.indexOf("|");
			if (index != -1)
			{
				chirpFN = chirpNK.substring(0, index);
				chirpLN = chirpNK.substring(index + 1);
			} else
			{
				chirpFN = "";
				chirpLN = chirpNK;
			}
			
			int index1 = chicaNK.indexOf("|");
			if (index1 != -1)
			{
				chicaFN = chicaNK.substring(0, index1);
				chicaLN = chicaNK.substring(index1 + 1);
			} else
			{
				chicaFN = "";
				chicaLN = chicaNK;
			}
			
			//compare strings
			if ( chicaLN.replaceAll("[^a-zA-Z0-9]+","")
					.equalsIgnoreCase(chirpLN.replaceAll("[^a-zA-Z0-9]+",""))){
				log.info("Immunization: NK last name matches. PatientId = " + patientId);
			
				return 1;
		}
		
			log.info("Immunization: NK last name does not match.PatientId = " + patientId);
			return 0;
		}
		
		private Integer compareStreet( Patient chirpPatient
				, Set<PersonAddress> chicaAddresses){
			
			PersonAddress chirpAddress = chirpPatient.getPersonAddress();
			Integer personId = null;
			
			if (chirpAddress == null){
				//chirp address is null and chica address is not null;
				log.info("Immunization: CHIRP address is null.");
				return 0;
			}
			
			String chirpStreet = chirpAddress.getAddress1();
			
			if (chirpStreet == null || chirpStreet.trim().equalsIgnoreCase("")){
				return 0;
			}
			
			for (PersonAddress chicaAddress: chicaAddresses){
				
				personId = chicaAddress.getPerson().getPersonId();
				String chicaStreet = chicaAddress.getAddress1();
				//We already know chirp street is not an empty string
				if (chicaStreet != null){
						if (chicaStreet.replaceAll("[^a-zA-Z0-9]+","")
						.equalsIgnoreCase(chirpStreet.replaceAll("[^a-zA-Z0-9]+",""))){
							log.info("Immunization: Street matches exactly. PersonId = " + personId);
							return 1;
						}
						String chicaStreet2 = chicaStreet.toLowerCase().replace(" drive", " dr");
						String chirpStreet2 = chirpStreet.toLowerCase().replace(" drive", " dr");
						chicaStreet2 = chicaStreet2.toLowerCase().replace(" street", " st");
						chirpStreet2 = chirpStreet2.toLowerCase().replace(" street", " st");
						chicaStreet2 = chicaStreet2.toLowerCase().replace(" road", " rd");
						chirpStreet2 = chirpStreet2.toLowerCase().replace(" road", " rd");
						chicaStreet2 = chicaStreet2.toLowerCase().replace(" court", " ct");
						chirpStreet2 = chirpStreet2.toLowerCase().replace(" court", " ct");
						chicaStreet2 = chicaStreet2.toLowerCase().replace(" avenue", " ave");
						chirpStreet2 = chirpStreet2.toLowerCase().replace(" avenue", " ave");
						chicaStreet2 = chicaStreet2.toLowerCase().replace(" lane", " ln");
						chirpStreet2 = chirpStreet2.toLowerCase().replace(" lane", " ln");
						if (chicaStreet2.replaceAll("[^a-zA-Z0-9]+","")
							.equalsIgnoreCase(chirpStreet2.replaceAll("[^a-zA-Z0-9]+",""))){
							log.info("Immunization: Street matches with adjustments. PersonId = " + personId);
							return 1;
						}
				}	
				
			}
			log.info("Immunization: Street does not match. PersonId = " + personId);
			return 0;
		}
		
		private Integer compareZip( Patient chirpPatient
				, Set<PersonAddress> chicaAddresses){
			
			Integer personId = null;
			PersonAddress chirpAddress = chirpPatient.getPersonAddress();
			if (chirpAddress == null || chirpAddress.getPostalCode() == null
					|| chirpAddress.getPostalCode().trim().equalsIgnoreCase("")){
				//chirp address or zip code is  null;
				log.info("Immunization: CHIRP SIIS# " + chirpPatient.getPatientIdentifier() +
						" Zip code or address is null.");
				return 0;
			}
			
			String chirpZip  = chirpAddress.getPostalCode();
			chirpZip = chirpZip.split("-")[0].trim();
				
			for (PersonAddress chicaAddress: chicaAddresses){
				
				personId = chicaAddress.getPerson().getPersonId();
				String chicaZip = chicaAddress.getPostalCode();
				
				if(chicaZip == null) chicaZip = "";
				chicaZip = chicaZip.split("-")[0].trim();
				
				if (chirpZip.equals(chicaZip)){
					  log.info("Immunization: SIIS# " + chirpPatient.getPatientIdentifier() + " Zip codes match. PatientId= " + personId);
						return 1;
				}		
			}
			
			log.info("Immunization: SIIS# " + chirpPatient.getPatientIdentifier() + "Zip codes do not match. PersonId = " + personId);
			return 0;
		}
		
		private boolean matchDOB( Patient chirpPatient
				, Patient chicaPatient){

			if (chirpPatient.getBirthdate() != null  && chicaPatient.getBirthdate() != null
					&& chirpPatient.getBirthdate().compareTo(chicaPatient.getBirthdate()) == 0){
				return true;
			}
			log.info("Immunization: DOB does not match. SIIS#: " + chirpPatient.getPatientIdentifier()
					+ " CHICA id:  " + chicaPatient.getPatientId());
			return false;
		}
		
		private void logCHICAInfo (Encounter encounter){
			
			Patient patient = encounter.getPatient();
			PatientService patientService = Context.getPatientService();
			PersonService personService = Context.getPersonService();
			patient = patientService.getPatient(patient.getPatientId());
			if (patient == null){
				return;
			}
			
			//Identifiers
			PatientIdentifierType pidtype = patientService
					.getPatientIdentifierTypeByName("MRN_OTHER");
			Set<PatientIdentifier> pids = patient.getIdentifiers();
			String pidString = "";
			String address = "";
			int i = 1;
			for (PatientIdentifier pid : pids){
				pidString += " MRN_" + i + ": " + pid.getIdentifier() + "\n";
				i++;
			}
			
			Set<PersonName> names = patient.getNames();
			String personNameString = "";
			
			i = 1;
			for (PersonName name : names){
				personNameString += " LN" + i + ":"  + name.getFamilyName() + "\n";
				personNameString += " FN" +  i + ":" + name.getGivenName() + "\n"; 
				personNameString += " MN" +  i + ":" + name.getMiddleName() + "\n"; 
				i++;
			}
			
			
			
			//address
			Set<PersonAddress> addresses = patient.getAddresses();
			int j = 1;
			String personAddressString = "";
			for (PersonAddress personAddress : addresses){
				if (personAddress != null){
					personAddressString += " ADDRESS1_"+ j +": " + personAddress.getAddress1() + "\n"
					+ " CITY_"+ j +": "  + personAddress.getCityVillage() + "\n"
					+ " ZIP_"+ j +": " + personAddress.getPostalCode()+ "\n"
					+ " Address date: " + personAddress.getDateCreated().toString() + "\n";
				}
				j++;
			}
			
			String dateFormat = "yyyyMMdd";
			SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
			String birthDate = " DOB: " + formatter.format(patient.getBirthdate()) + "\n";
			
			
		
			
			//next of kin
			String nextOfKin = "";
			PersonAttributeType pat = personService
			.getPersonAttributeTypeByName("Next of Kin");
			Person person = personService.getPerson(patient.getPatientId());
			PersonAttribute nextOfKinAttribute = person.getAttribute(pat);

			if (nextOfKinAttribute != null) {
			
				String name = nextOfKinAttribute.getValue();
				StringTokenizer nknames = new StringTokenizer(name, "|", false);
				if (nknames.hasMoreTokens()) {
					nextOfKin += "NKFN: " + nknames.nextToken() + "\n";
				}
				if (nknames.hasMoreTokens()) {
					nextOfKin  += "NKLN: " + nknames.nextToken() + "\n";
				}
			
				 String patientString = personNameString + pidString + personAddressString
				+ birthDate + nextOfKin;
				 
				 log.info("\n" + patientString);
				
			} 
			
			return;
		}
		
		
		private void logCHICADemographics (Encounter encounter){
			//Used for logging patient matching
			Patient patient = encounter.getPatient();
			PatientService patientService = Context.getPatientService();
			PersonService personService = Context.getPersonService();
			patient = patientService.getPatient(patient.getPatientId());
			if (patient == null){
				return;
			}
			
			//Identifiers
			
			Set<PatientIdentifier> pids = patient.getIdentifiers();
			String pidString = "";
			int i = 1;
			for (PatientIdentifier pid : pids){
				pidString += " MRN_" + i + ": " + pid.getIdentifier() + "\n";
				i++;
			}
			
			Set<PersonName> names = patient.getNames();
			String personNameString = "";
			
			i = 1;
			for (PersonName name : names){
				personNameString += " LN" + i + ":"  + name.getFamilyName() + "\n";
				personNameString += " FN" +  i + ":" + name.getGivenName() + "\n"; 
				personNameString += " MN" +  i + ":" + name.getMiddleName() + "\n"; 
				i++;
			}
			
			
			
			//address
			Set<PersonAddress> addresses = patient.getAddresses();
			int j = 1;
			String personAddressString = "";
			for (PersonAddress personAddress : addresses){
				if (personAddress != null){
					personAddressString += " ADDRESS1_"+ j +": " + personAddress.getAddress1() + "\n"
					+ " CITY_"+ j +": "  + personAddress.getCityVillage() + "\n"
					+ " ZIP_"+ j +": " + personAddress.getPostalCode()+ "\n"
					+ " Address date: " + personAddress.getDateCreated().toString() + "\n";
				}
				j++;
			}
			
			String dateFormat = "yyyyMMdd";
			SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
			String birthDate = " DOB: " + formatter.format(patient.getBirthdate()) + "\n";
			
			//next of kin
			String nextOfKin = "";
			PersonAttributeType pat = personService
			.getPersonAttributeTypeByName("Next of Kin");
			Person person = personService.getPerson(patient.getPatientId());
			PersonAttribute nextOfKinAttribute = person.getAttribute(pat);

			if (nextOfKinAttribute != null) {
			
				String name = nextOfKinAttribute.getValue();
				StringTokenizer nknames = new StringTokenizer(name, "|", false);
				if (nknames.hasMoreTokens()) {
					nextOfKin += "NKFN: " + nknames.nextToken() + "\n";
				}
				if (nknames.hasMoreTokens()) {
					nextOfKin  += "NKLN: " + nknames.nextToken() + "\n";
				}
			
				 String patientString = personNameString + pidString + personAddressString
				+ birthDate + nextOfKin;
				 
				 log.info("\n" + patientString);
			
			} 
				
			
			return;
		} 
		
		private void logResponse(Integer encounterId, Message message, Integer queryCount ){
			try {
				
				//Patient matching
				EncounterService encounterService = Context.getEncounterService();
				
				if (queryCount == null){
					log.info("NO RESPONSE: EID:" + encounterId );
				}
				
				if (message == null){
					// get patient name
					Encounter enc = encounterService.getEncounter(encounterId);
				    Patient patient = enc.getPatient(); 
					log.info("QCK :EID:" + encounterId + " CHICA: LN: " + patient.getFamilyName()
							+ " FN: " +  patient.getGivenName()
							+ " MN: " + patient.getMiddleName());
				}
				if (message instanceof VXX_V02) {
					QRD qrd =((VXX_V02) message).getQRD();
					if (qrd != null ){
						FN fn = qrd.getWhoSubjectFilter(0).getFamilyLastName();
						if (fn != null){
							String last  = fn.getFamilyName().getValue();
							String middle = qrd.getWhoSubjectFilter(0).getMiddleInitialOrName().toString();
							String first = qrd.getWhoSubjectFilter(0).getGivenName().getValue();
							log.info("VXQ" + queryCount+ ": EID:" + encounterId + " CHICA: LN: " + last + " FN: " + first
									+ " MN: " + middle);
						}
					}
					
				 Integer count =  ((VXX_V02) message).getPIDNK1Reps();
					 for (int i = 0 ; i < count; i++){
						 VXX_V02_PIDNK1  pidnk1 = ((VXX_V02) message).getPIDNK1(i);
						 if (pidnk1 != null){
							 XPN xpn = pidnk1.getPID().getPatientName(0);
							 if (xpn != null){
								 String last = xpn.getFamilyLastName().getFamilyName().getValue();
								 String first = xpn.getGivenName().getValue();
								 String middle = xpn.getMiddleInitialOrName().getValue();
								 log.info("VXX " + queryCount+ ": EID:" + encounterId + " CHIRP: LN: " + last + " FN: " + first
											+ " MN: " + middle);
							 }
						 }
					 }
				}
				
				if (message instanceof VXR_V03) {
					QRD qrd = ((VXR_V03) message).getQRD();
					if (qrd != null) {
						String last = qrd.getWhoSubjectFilter(0)
								.getFamilyLastName().getFamilyName().getValue();
						String middle = qrd.getWhoSubjectFilter(0)
								.getMiddleInitialOrName().toString();
						String first = qrd.getWhoSubjectFilter(0).getGivenName()
								.getValue();
						log.info("VXQ" + queryCount + ": EID:" + encounterId
								+ " CHICA: LN: " + last + " FN: " + first + " MN: "
								+ middle);
					}
					PID pid = ((VXR_V03) message).getPID();

					if (pid != null) {
						String last = pid.getPatientName(0).getFamilyLastName()
								.getFamilyName().getValue();
						String first = pid.getPatientName(0).getGivenName()
								.getValue();
						String middle = pid.getPatientName(0)
								.getMiddleInitialOrName().getValue();
						log.info("VXR : EID:" + encounterId + " CHIRP: LN: " + last
								+ " FN: " + first + " MN: " + middle);

					}

				}
			} catch (Exception e) {
				log.error("Exception logging response", e);
			}
			return;
		}
		
		private void logDetail(String message, Integer encounterId, String name){
			
			if (message == null){
				return;
			}
			if (name != null && name.equalsIgnoreCase("QCK") ) {
				log.info(" \n" + name + " for eid:" + encounterId + " \n" +  message.substring(message.indexOf("QRD"), message.indexOf("QRF"))
						+ message.substring(message.indexOf("QRF")));
			}
			
			if (name != null && name.startsWith("VXX") ) {
				log.info(" \n" + name + " for eid:" + encounterId + " \n" + message.substring(message.indexOf("QRD")));
				
			}
			
			if (name != null && name.startsWith("VXR") ) {
				log.info(" \n" + name + " for eid:" + encounterId + " \n" +  message.substring(message.indexOf("QRD"), message.indexOf("ORC")));
				
			}
		}

}
