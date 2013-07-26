package org.openmrs.module.chica.web;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25;
import org.openmrs.module.chica.hl7.mckesson.HL7PatientHandler25;
import org.openmrs.module.chica.hl7.mckesson.HL7SocketHandler;
import org.openmrs.module.chica.hl7.mckesson.PatientHandler;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.sockethl7listener.HL7ObsHandler25;
import org.openmrs.module.sockethl7listener.Provider;
import org.openmrs.patient.impl.LuhnIdentifierValidator;
import org.springframework.web.servlet.mvc.SimpleFormController;

import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.validation.impl.NoValidation;

public class ManualCheckinController extends SimpleFormController
{

	/** Logger for this class and subclasses */
	protected final Log log = LogFactory.getLog(getClass());

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.mvc.AbstractFormController#formBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception
	{
		return "testing";
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception
	{
		PatientService patientService = Context.getPatientService();
		ChicaService chicaService = Context.getService(ChicaService.class);
		Map<String, Object> map = new HashMap<String, Object>();
		LuhnIdentifierValidator luhn = new LuhnIdentifierValidator();
		
		//set stations
		User user = Context.getAuthenticatedUser();
		LocationService locationService = Context.getLocationService();
		String locationString = user.getUserProperty("location");

		Location location = locationService
			.getLocation(locationString);
		List<String> stationNames =  chicaService.getPrinterStations(location);
		map.put("stations", stationNames);
		
		String checkin = request.getParameter("checkin");

		if (checkin != null)
		{
			checkin(request,map,location);
		}
		
		// process mrn 
		String mrn = request.getParameter("mrnLookup");
		if (mrn != null && !mrn.contains("-") && mrn.length() > 1)
		{
			mrn = mrn.substring(0, mrn.length() - 1) + "-" + mrn.substring(mrn.length()-1);

		}
		
		boolean valid = false;
		
		if (mrn != null && !mrn.isEmpty()){
			valid = luhn.isValid(mrn);
		}
		
		if (!valid) {
			map.put("validate", "");
		}
		else {
			map.put("validate", "valid");
		}
		
		// see if there is a patient that already has the mrn
		if (mrn != null)
		{
			PatientIdentifierType identifierType = patientService
					.getPatientIdentifierTypeByName("MRN_OTHER");
			List<PatientIdentifierType> identifierTypes = new ArrayList<PatientIdentifierType>();
			identifierTypes.add(identifierType);
			List<Patient> patients = patientService.getPatients(null, mrn,
					identifierTypes,false);
		

			if (patients.size() > 0)
			{
				Patient patient = patients.get(0);
				patientFound(map, patient);
				map.put("checkinButton", "Checkin");

			} else
			{
				map.put("checkinButton", "Add + Checkin");
				map.put("mrn", mrn);
				map.put("newPatient", "true");
			}
		}

		List<String> categories = chicaService.getInsCategories();
		Collections.sort(categories);
		map.put("insuranceCategories", categories);

		//Sort provider by lastname,firstname, and remove duplicates
		//Ensure proper case.
		UserService userService = Context.getUserService();
		List<User> doctors = userService.getUsersByRole(userService
				.getRole("Provider"));
		
		List<User> doctorList = new ArrayList<User>();
		
		Comparator comparator = new Comparator(){
			 
            public int compare(Object o1, Object o2) 
            {
               User p1 = (User) o1;
               User p2 = (User) o2;
               PersonName pn1 = new PersonName();
               PersonName pn2 = new PersonName();
               if (p1 != null){
            	   pn1 = p1.getPersonName();
               }
               if (p2!= null){
            	   pn2 = p2.getPersonName();
               }
               String p1FirstName = null;
               String p2FirstName = null;
               String p1LastName = null;
               String p2LastName = null;
               
               if(pn1 != null){
                   p1FirstName = pn1.getGivenName(); 
                   p1LastName = pn1.getFamilyName();
              }
               
               if(pn2 != null){
                   p2FirstName = pn2.getGivenName();
                   p2LastName = pn2.getFamilyName();
               }
               
               if(p1FirstName == null){
            	   p1FirstName = "";
               }
               
               if(p1LastName == null){
            	   p1LastName = "";
               }
               
               if(p2FirstName == null){
            	   p2FirstName = "";
               }
               
               if(p2LastName == null){
            	   p2LastName = "";
               }
               //Make sure duplicate name is detected as a duplicate regardless of case.  
               //NOTE: Comparing to ignore case as a safety check
               //User should have been stored in proper case, but
               //entries before update might not be in proper case
               
               if (p1LastName.compareToIgnoreCase(p2LastName)==0){	  
            	   if(p1FirstName.compareToIgnoreCase(p2FirstName)==0){
            		   return p2.getDateCreated().compareTo(p1.getDateCreated());
            	   }
            	   return p1FirstName.compareToIgnoreCase(p2FirstName);
               }
               
               return p1LastName.compareTo(p2LastName);
            }
		};
 
		Collections.sort(doctors, comparator);

		//weed out duplicates from already sorted list
		Integer size = doctors.size();
		ArrayList<User> noDups  = new ArrayList(size);
		if(size > 0)
		{
			User prev =  doctors.get(0);
			noDups.add(prev);
		
			for (Integer index = 1; index < size; index++){
				User nextdoc = doctors.get(index);
				if (compareOnlyName(nextdoc, prev) != 0){
					noDups.add(nextdoc);
					prev = nextdoc;
				}
	
			}
		}
		for(User currDoc:noDups){
			if(!(currDoc.getFamilyName() == null||currDoc.getFamilyName().length()==0)){
				String lastName = Util.toProperCase(currDoc.getFamilyName());
				String firstName = Util.toProperCase(currDoc.getGivenName());
				currDoc.getPersonName().setFamilyName(lastName);
				currDoc.getPersonName().setGivenName(firstName);
				doctorList.add(currDoc);
			}
		}
		
		map.put("doctors", doctorList);

		return map;
	}
	
    private int compareOnlyName(Object o1, Object o2) 
    {
       User p1 = (User) o1;
       User p2 = (User) o2;
       PersonName pn1 = new PersonName();
       PersonName pn2 = new PersonName();
       if (p1 != null){
    	   pn1 = p1.getPersonName();
       }
       if (p2!= null){
    	   pn2 = p2.getPersonName();
       }
       String p1FirstName = null;
       String p2FirstName = null;
       String p1LastName = null;
       String p2LastName = null;
       
       if(pn1 != null){
           p1FirstName = pn1.getGivenName(); 
           p1LastName = pn1.getFamilyName();
      }
       
       if(pn2 != null){
           p2FirstName = pn2.getGivenName();
           p2LastName = pn2.getFamilyName();
       }
       
       if(p1FirstName == null){
    	   p1FirstName = "";
       }
       
       if(p1LastName == null){
    	   p1LastName = "";
       }
       
       if(p2FirstName == null){
    	   p2FirstName = "";
       }
       
       if(p2LastName == null){
    	   p2LastName = "";
       }
       //Make sure duplicate name is detected as a duplicate regardless of case.  
       //NOTE: Comparing to ignore case as a safety check
       //User should have been stored in proper case, but
       //entries before update might not be in proper case
       
       if (p1LastName.compareToIgnoreCase(p2LastName)==0){	  
    	   return p1FirstName.compareToIgnoreCase(p2FirstName);
       }
       
       return p1LastName.compareTo(p2LastName);
    }

	private void checkin(HttpServletRequest request,Map<String, Object> map,
			Location encounterLocation){
		
		Date encounterDate = new Date();

		PersonService personService = Context.getPersonService();
		PatientService patientService = Context.getPatientService();
		EncounterService encounterService = Context.getService(EncounterService.class);
		
		Patient checkinPatient = new Patient();
		PersonName name = new PersonName();
		name.setGivenName(request.getParameter("firstName"));
		name.setMiddleName(request.getParameter("middleName"));
		name.setFamilyName(request.getParameter("lastName"));
		name.setDateCreated(new Date());
		checkinPatient.addName(name);
		
		PersonAddress address = new PersonAddress();
		address.setAddress1(request.getParameter("address1"));
		address.setAddress2(request.getParameter("address2"));
		address.setCityVillage(request.getParameter("city"));
		address.setPostalCode(request.getParameter("zip"));
		address.setStateProvince(request.getParameter("state"));
		address.setDateCreated(encounterDate);
		address.setCreator(Context.getAuthenticatedUser());
		checkinPatient.addAddress(address);
		
		checkinPatient.setGender(request.getParameter("sex"));

		String race = request.getParameter("race");
		if (race != null && race.length()>0)
		{
			PersonAttribute attribute = new PersonAttribute();
			PersonAttributeType attributeType = personService
					.getPersonAttributeTypeByName("Race");
			attribute.setAttributeType(attributeType);
			attribute.setValue(race);
			checkinPatient.addAttribute(attribute);
		}
		
		String nextOfKinFirstName = request.getParameter("nextOfKinFirstName");
		String nextOfKinLastName = request.getParameter("nextOfKinLastName");
		if (nextOfKinFirstName != null && nextOfKinFirstName.trim().length()>0 && nextOfKinLastName != null && 
				nextOfKinLastName.trim().length()>0)
		{
			PersonAttribute attribute = new PersonAttribute();
			PersonAttributeType attributeType = personService
					.getPersonAttributeTypeByName("Next of Kin");
			if (attributeType == null) {
				attributeType = new PersonAttributeType();
				attributeType.setDateCreated(new Date());
				attributeType.setName("Next of Kin");
				attributeType.setDescription("Next of Kin");
				attributeType.setUuid(UUID.randomUUID().toString());
				attributeType = personService.savePersonAttributeType(attributeType);
			}
			
			attribute.setAttributeType(attributeType);
			attribute.setValue(nextOfKinFirstName + "|" + nextOfKinLastName);
			checkinPatient.addAttribute(attribute);
		}

		String dayPhone = request.getParameter("dayPhone");
		if (dayPhone != null && dayPhone.length()>0)
		{
			PersonAttribute attribute = new PersonAttribute();
			PersonAttributeType attributeType = personService
					.getPersonAttributeTypeByName("Telephone Number");
			attribute.setAttributeType(attributeType);
			attribute.setValue(dayPhone);
			checkinPatient.addAttribute(attribute);
		}
		LocationService locationService = Context.getLocationService();
		Integer locationId = null;
		User user = Context.getAuthenticatedUser();

		if (encounterLocation == null){
			encounterLocation = locationService.getDefaultLocation();
			//Default location is created by openmrs and 
			//is called "Unknown Location"
			log.warn("The location requested during manual check-in "+
					" was not found in Location table. Default location " +
					" was used for this checkin.");
		}
		String ssn1 = request.getParameter("ssn1");
		String ssn2 = request.getParameter("ssn2");
		String ssn3 = request.getParameter("ssn3");

		if (ssn1 != null && ssn2 != null && ssn3 != null&&ssn1.length()>0&&ssn2.length()>0&&ssn3.length()>0)
		{
			//removed "-" between segments for consistancy with hl7 SSM MSheley 2/29/2009
			String ssn = ssn1 + ssn2 + ssn3;
			PatientIdentifierType identifierType = patientService
					.getPatientIdentifierTypeByName("SSN");
			PatientIdentifier pi = new PatientIdentifier();
			pi.setIdentifier(ssn);
			pi.setIdentifierType(identifierType);
			pi.setLocation(encounterLocation);
			checkinPatient.addIdentifier(pi);
		}

		String mrn = request.getParameter("mrn");
		if (mrn != null)
		{
			PatientIdentifierType identifierType = patientService
					.getPatientIdentifierTypeByName("MRN_OTHER");
			PatientIdentifier pi = new PatientIdentifier();
			pi.setIdentifier(mrn);
			pi.setIdentifierType(identifierType);
			pi.setLocation(encounterLocation);
			pi.setPreferred(true);
			checkinPatient.addIdentifier(pi);
		}

		String dob1 = request.getParameter("dob1");
		String dob2 = request.getParameter("dob2");
		String dob3 = request.getParameter("dob3");

		if (dob1 != null && dob2 != null && dob3 != null)
		{
			while(dob1.length()<2){
				dob1="0"+dob1;
			}
			while(dob2.length()<2){
				dob2="0"+dob2;
			}
			
			while(dob3.length()<3){
				dob3="0"+dob3;
			}
			if(dob3.length()<4){
				dob3="2"+dob3;
			}
			String dob = dob1 + "/" + dob2 + "/" + dob3;
			SimpleDateFormat dateFormatter = new SimpleDateFormat(
					"MM/dd/yyyy");
			try
			{
				Date birthdate = dateFormatter.parse(dob);
				checkinPatient.setBirthdate(birthdate);
			} catch (Exception e)
			{
				this.log.error("Error parsing date: "+dob);
			}
		}
		Provider provider = new Provider();
		Integer userId = null;
		try
		{
			userId = Integer.parseInt(request.getParameter("doctor"));
			UserService userService = Context.getUserService();
			user = userService.getUser(userId);
			provider.setProviderfromUser(user);
		} catch (Exception e)
		{
			this.log.error("Could not assign provider: "+userId);
			this.log.error(e.getMessage());
			this.log.error(Util.getStackTrace(e));
		}
		
		PipeParser parser = new PipeParser();
		parser.setValidationContext(new NoValidation());
		PatientHandler patientHandler = new PatientHandler();
		HL7SocketHandler socketHandler = new HL7SocketHandler(parser,
				patientHandler, new HL7ObsHandler25(),
				new HL7EncounterHandler25(), new HL7PatientHandler25(),
				null);
		
		//create encounter
		org.openmrs.module.chica.hibernateBeans.Encounter newEncounter = new org.openmrs.module.chica.hibernateBeans.Encounter();
		newEncounter.setLocation(encounterLocation);
		newEncounter.setEncounterDatetime(encounterDate);
		newEncounter.setPrinterLocation(request.getParameter("station"));
		newEncounter.setEncounterDatetime(encounterDate);
		EncounterType encType = encounterService.getEncounterType("ManualCheckin");
		if (encType == null){
			encType = new EncounterType("ManualCheckin", "Arrival from hl7 message.");
		}
		newEncounter.setEncounterType(encType);
		
		//checkin
		LuhnIdentifierValidator luhn = new LuhnIdentifierValidator();
		boolean validIdentifier = luhn.isValid(checkinPatient.getPatientIdentifier().getIdentifier());
		boolean checkinSuccess = false;
		HashMap<String,Object> parameters = new HashMap<String,Object>();
		if (validIdentifier){
			Encounter enc = (Encounter) socketHandler.checkin(provider, checkinPatient, encounterDate,
					 null, null, newEncounter,parameters);
			if (enc != null){
				checkinSuccess = true;
				// Set the insurance category
				String insuranceCategory = request.getParameter("insuranceCategory");
				if (insuranceCategory != null && insuranceCategory.trim().length() > 0) {
					Concept concept = Context.getConceptService().getConcept("Insurance");
		    		org.openmrs.module.chirdlutil.util.Util.saveObs(enc.getPatient(), concept, enc.getEncounterId(), 
		    			insuranceCategory, enc.getEncounterDatetime());
				}
			}
		}
		
		String checkinPatientName = checkinPatient.getFamilyName();
		if(checkinPatient.getGivenName() != null){
			checkinPatientName+=", "+checkinPatient.getGivenName();
		}
			
		map.put("checkinSuccess", checkinSuccess);
		map.put("checkinPatient",checkinPatientName);
		map.put("checkinMRN",checkinPatient.getPatientIdentifier().getIdentifier());
	}
	
	//put all the patient attributes in the jsp map
	private void patientFound(Map<String, Object> map, Patient patient)
	{
		map.put("firstName", patient.getGivenName());
		map.put("middleName", patient.getMiddleName());
		map.put("lastName", patient.getFamilyName());
		PersonAddress address = patient.getPersonAddress();
		if (address != null)
		{
			map.put("address1", address.getAddress1());
			map.put("address2", address.getAddress2());
			map.put("city", address.getCityVillage());
			map.put("zip", address.getPostalCode());
			map.put("state", address.getStateProvince());
		}
		map.put("sex", patient.getGender());
		Date dob = patient.getBirthdate();
		SimpleDateFormat format = new SimpleDateFormat("MM");
		map.put("dob1", format.format(dob));
		format = new SimpleDateFormat("dd");
		map.put("dob2", format.format(dob));
		format = new SimpleDateFormat("yyyy");
		map.put("dob3", format.format(dob));
		PersonAttribute attribute = patient.getAttribute("Race");
		if (attribute != null)
		{
			map.put("race", attribute.getValue());
		}

		attribute = patient.getAttribute("Religion");
		if (attribute != null)
		{
			map.put("religion", attribute.getValue());
		}

		attribute = patient.getAttribute("Telephone Number");
		if (attribute != null)
		{
			map.put("dayPhone", attribute.getValue());
		}
		PatientIdentifier ssnIdent = patient.getPatientIdentifier("SSN");
		String ssn = null;
		if(ssnIdent != null){
			ssn = ssnIdent.getIdentifier();
		}
		if (ssn != null)
		{
			ssn = ssn.replaceAll("-", "");

			if (ssn.length() >= 3)
			{
				map.put("ssn1", ssn.substring(0, 3));
			}
			if (ssn.length() >= 5)
			{
				map.put("ssn2", ssn.substring(3, 5));
			}
			if (ssn.length() >= 9)
			{
				map.put("ssn3", ssn.substring(5, 9));
			}
		}

		attribute = patient.getAttribute("Next of Kin");
		if (attribute != null)
		{
			String value = attribute.getValue();
			StringTokenizer tokenizer = new StringTokenizer(value, "|");
			if (tokenizer.hasMoreTokens()) {
				map.put("nextOfKinFirstName", tokenizer.nextToken());
			}
			
			if (tokenizer.hasMoreTokens()) {
				map.put("nextOfKinLastName", tokenizer.nextToken());
			}
		}

		PatientIdentifier patientIdentifier = patient.getPatientIdentifier();

		if (patientIdentifier != null)
		{
			map.put("mrn", patientIdentifier.getIdentifier());
		}

		EncounterService encounterService = Context
				.getService(EncounterService.class);
		List<org.openmrs.Encounter> encounters = encounterService
				.getEncounters(patient, null, null, null, null, null, null,false);

		if (encounters != null && encounters.size() > 0)
		{
			Encounter encounter = (Encounter) encounters.get(0);
			UserService userService = Context.getUserService();
			List<User> providers = userService.getUsersByPerson(encounter.getProvider(), true);
			User provider = null;
			if(providers != null&& providers.size()>0){
				provider = providers.get(0);
			}
			if (provider != null)
			{
				map.put("doctor", provider.getUserId());
			}

			map.put("station", encounter.getPrinterLocation());
			map.put("insuranceCode", encounter.getInsuranceSmsCode());
		}
	}
}
