package org.openmrs.module.chica.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptSource;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientIdentifierException;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.hl7.mckesson.HL7EncounterHandler25;
import org.openmrs.module.chica.hl7.mckesson.HL7PatientHandler25;
import org.openmrs.module.chica.hl7.mckesson.HL7SocketHandler;
import org.openmrs.module.chica.hl7.mckesson.PatientHandler;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.Util;
import org.openmrs.module.sockethl7listener.HL7ObsHandler25;
import org.openmrs.module.sockethl7listener.Provider;
import org.openmrs.parameter.EncounterSearchCriteria;
import org.openmrs.parameter.EncounterSearchCriteriaBuilder;
import org.openmrs.validator.PatientIdentifierValidator;

import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.validation.impl.NoValidation;

public class ManualCheckin
{

	/** Logger for this class and subclasses */
	protected static final Log log = LogFactory.getLog(ManualCheckin.class);
	private static final String PARAM_MRN = "mrn";
	
	public static void getManualCheckinPatient(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_XML);
		response.setHeader(ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL, ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL_NO_CACHE);
		PrintWriter pw = response.getWriter();
		pw.write("<manualCheckin>");
		
		PatientService patientService = Context.getPatientService();
		ChicaService chicaService = Context.getService(ChicaService.class);
		
		//set stations
		User user = Context.getAuthenticatedUser();
		List<String> stationNames =  chicaService.getPrinterStations(user);
		pw.write("<stations>");
		if (stationNames != null) {
			for (String stationName : stationNames) {
				pw.write("<station>" + stationName + "</station>");
			}
		}
		pw.write("</stations>");
		
		// process mrn 
		String mrn = request.getParameter(PARAM_MRN);
		if (mrn != null) {
			mrn = Util.removeLeadingZeros(mrn);
		}
		
		if (mrn != null && !mrn.contains("-") && mrn.length() > 1)
		{
			mrn = mrn.substring(0, mrn.length() - 1) + "-" + mrn.substring(mrn.length()-1);

		}
		
		boolean valid = false;
		PatientIdentifierType identifierType = patientService
				.getPatientIdentifierTypeByName(ChirdlUtilConstants.IDENTIFIER_TYPE_MRN);
		if (mrn != null && !mrn.isEmpty()){
			try {
				PatientIdentifierValidator.validateIdentifier(mrn, identifierType);
				valid = true;
			}  catch(PatientIdentifierException e) {
			}
		}
		
		if (!valid) {
			pw.write("<validated>false</validated>");
		}
		else {
			pw.write("<validated>true</validated>");
		}
		
		// see if there is a patient that already has the mrn
		if (mrn != null)
		{
			List<PatientIdentifierType> identifierTypes = new ArrayList<PatientIdentifierType>();
			identifierTypes.add(identifierType);
			List<Patient> patients = patientService.getPatientsByIdentifier(null, mrn,
					identifierTypes,true); // CHICA-977 Use getPatientsByIdentifier() as a temporary solution to openmrs TRUNK-5089
			if (patients.size() == 0){
				patients = patientService.getPatientsByIdentifier(null, "0" + mrn,
						identifierTypes,true); // CHICA-977 Use getPatientsByIdentifier() as a temporary solution to openmrs TRUNK-5089
			}

			if (patients.size() > 0)
			{
				Patient patient = patients.get(0);
				populatePatient(pw, patient);
				pw.write("<newPatient>false</newPatient>");

			} else {
				pw.write("<newPatient>true</newPatient>");
			}
		}
		
		TreeMap<String,String> raceCodes = new TreeMap<String,String>();
		ConceptService conceptService = Context.getConceptService();
		ConceptSource conceptSource = conceptService.getConceptSourceByName("Wishard Race Codes");
		List<ConceptMap> conceptMaps = conceptService.getConceptMappingsToSource(conceptSource); // CHICA-1151 replace getConceptsByConceptSource() with getConceptMappingsToSource()
		for(ConceptMap conceptMap:conceptMaps){
			String generalRaceCategory = conceptMap.getConcept().getName().getName();
			String raceCode = conceptMap.getConceptReferenceTerm().getCode(); // CHICA-1151 replace getSourceCode() with getConceptReferenceTerm().getCode()
			raceCodes.put(generalRaceCategory, raceCode);
		}
		
		pw.write("<raceCodes>");
		Set<Entry<String,String>> raceSet = raceCodes.entrySet();
		Iterator<Entry<String,String>> raceIter = raceSet.iterator();
		while (raceIter.hasNext()) {
			Entry<String,String> raceEntry = raceIter.next();
			pw.write("<raceCode category=\"" + ServletUtil.escapeXML(raceEntry.getKey()) + "\">" + raceEntry.getValue() + "</raceCode>");
		}
		pw.write("</raceCodes>");

		List<String> categories = chicaService.getInsCategories();
		Collections.sort(categories);
		pw.write("<insuranceCategories>");
		for (String category : categories) {
			ServletUtil.writeTag("insuranceCategory", ServletUtil.escapeXML(category), pw);
		}
		pw.write("</insuranceCategories>");

		//Sort provider by lastname,firstname, and remove duplicates
		//Ensure proper case.
		ProviderService providerService = Context.getProviderService();
		List<org.openmrs.Provider> doctors = providerService.getAllProviders();
		
		List<org.openmrs.Provider> doctorList = new ArrayList<org.openmrs.Provider>();
		
		Comparator<org.openmrs.Provider> comparator = new Comparator<org.openmrs.Provider>(){		 
            public int compare(org.openmrs.Provider p1, org.openmrs.Provider p2) 
            {
               Person person1 = p1.getPerson();
               Person person2 = p2.getPerson();
               
               String p1FirstName = null;
               String p2FirstName = null;
               String p1LastName = null;
               String p2LastName = null;
               
               if(person1 != null){
                   p1FirstName = person1.getGivenName(); 
                   p1LastName = person1.getFamilyName();
              }
               
               if(person2 != null){
                   p2FirstName = person2.getGivenName();
                   p2LastName = person2.getFamilyName();
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
		ArrayList<org.openmrs.Provider> noDups  = new ArrayList<org.openmrs.Provider>(size);
		if(size > 0)
		{
			org.openmrs.Provider prev =  doctors.get(0);
			noDups.add(prev);
		
			for (Integer index = 1; index < size; index++){
				org.openmrs.Provider nextdoc = doctors.get(index);
				if (compareOnlyName(nextdoc, prev) != 0){
					noDups.add(nextdoc);
					prev = nextdoc;
				}
	
			}
		}
		for(org.openmrs.Provider currDoc:noDups){
			Person person = currDoc.getPerson();
			if(!(person.getFamilyName() == null||person.getFamilyName().length()==0)){
				String lastName = Util.toProperCase(person.getFamilyName());
				String firstName = Util.toProperCase(person.getGivenName());
				person.getPersonName().setFamilyName(lastName);
				person.getPersonName().setGivenName(firstName);
				doctorList.add(currDoc);
			}
		}
		
		pw.write("<doctors>");
		for (org.openmrs.Provider doctor : doctorList) {
			pw.write("<doctor>");
			ServletUtil.writeTag("lastName", ServletUtil.escapeXML(doctor.getPerson().getFamilyName()), pw);
			ServletUtil.writeTag("firstName", ServletUtil.escapeXML(doctor.getPerson().getGivenName()), pw);
			ServletUtil.writeTag("middleName", ServletUtil.escapeXML(doctor.getPerson().getMiddleName()), pw);
			ServletUtil.writeTag("providerId", doctor.getId(), pw); // CHICA-221 Changed this parameter from userId to providerId and in the .jsp
			pw.write("</doctor>");
		}
		pw.write("</doctors>");
		pw.write("</manualCheckin>");
	}
	
	public static void saveManualCheckinPatient(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_XML);
		response.setHeader(ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL, ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL_NO_CACHE);
		
		checkin(request, response);
	}
	
    private static int compareOnlyName(Object o1, Object o2) 
    {
    	org.openmrs.Provider p1 = (org.openmrs.Provider) o1;
    	org.openmrs.Provider p2 = (org.openmrs.Provider) o2;
       PersonName pn1 = new PersonName();
       PersonName pn2 = new PersonName();
       if (p1 != null && p1.getPerson() != null){
    	   pn1 = p1.getPerson().getPersonName();
       }
       if (p2!= null && p2.getPerson() != null){
    	   pn2 = p2.getPerson().getPersonName();
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

	private static void checkin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		Date encounterDate = new Date();

		PersonService personService = Context.getPersonService();
		PatientService patientService = Context.getPatientService();
		EncounterService encounterService = Context.getService(EncounterService.class);
		
		Patient checkinPatient = new Patient();
		PersonName name = new PersonName();
		name.setGivenName(request.getParameter("manualCheckinFirstName"));
		name.setMiddleName(request.getParameter("manualCheckinMiddleName"));
		name.setFamilyName(request.getParameter("manualCheckinLastName"));
		name.setPreferred(true);
		name.setDateCreated(new Date());
		checkinPatient.addName(name);
		
		PersonAddress address = new PersonAddress();
		address.setAddress1(request.getParameter("manualCheckinStreetAddress"));
		address.setAddress2(request.getParameter("manualCheckinStreetAddress2"));
		address.setCityVillage(request.getParameter("manualCheckinCity"));
		address.setPostalCode(request.getParameter("manualCheckinZip"));
		address.setStateProvince(request.getParameter("manualCheckinState"));
		address.setDateCreated(encounterDate);
		address.setCreator(Context.getAuthenticatedUser());
		address.setPreferred(true);
		checkinPatient.addAddress(address);
		
		checkinPatient.setGender(request.getParameter("manualCheckinSex"));

		String race = request.getParameter("manualCheckinRace");
		if (race != null && race.length()>0)
		{
			PersonAttribute attribute = new PersonAttribute();
			PersonAttributeType attributeType = personService
					.getPersonAttributeTypeByName("Race");
			attribute.setAttributeType(attributeType);
			attribute.setValue(race);
			checkinPatient.addAttribute(attribute);
		}
		
		String nextOfKinFirstName = request.getParameter("manualCheckinNOKFirstName");
		String nextOfKinLastName = request.getParameter("manualCheckinNOKLastName");
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

		String dayPhone = request.getParameter("manualCheckinPhone");
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
		User user = Context.getAuthenticatedUser();

		String locationString = user.getUserProperty(ChirdlUtilConstants.USER_PROPERTY_LOCATION);
		Location encounterLocation = locationService
			.getLocation(locationString);
		if (encounterLocation == null){
			encounterLocation = locationService.getDefaultLocation();
			//Default location is created by openmrs and 
			//is called "Unknown Location"
			log.warn("The location requested during manual check-in "+
					" was not found in Location table. Default location " +
					" was used for this checkin.");
		}
		String ssn1 = request.getParameter("manualCheckinSSNOne");
		String ssn2 = request.getParameter("manualCheckinSSNTwo");
		String ssn3 = request.getParameter("manualCheckinSSNThree");

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

		String mrn = request.getParameter("manualCheckinMrn");
		PatientIdentifierType identifierType = patientService
				.getPatientIdentifierTypeByName(ChirdlUtilConstants.IDENTIFIER_TYPE_MRN);
		if (mrn != null)
		{
			PatientIdentifier pi = new PatientIdentifier();
			mrn = Util.removeLeadingZeros(mrn);
			if (!mrn.contains("-") && mrn.length() > 1) {
				mrn = mrn.substring(0, mrn.length() - 1) + "-" + mrn.substring(mrn.length() - 1);
			}
			
			pi.setIdentifier(mrn);
			pi.setIdentifierType(identifierType);
			pi.setLocation(encounterLocation);
			pi.setPreferred(true);
			checkinPatient.addIdentifier(pi);
		}

		String dob = request.getParameter("manualCheckinDob");

		if (dob != null)
		{
			SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
			try
			{
				Date birthdate = dateFormatter.parse(dob);
				checkinPatient.setBirthdate(birthdate);
			} catch (Exception e)
			{
				log.error("Error parsing date: "+dob);
			}
		}
		Provider provider = new Provider();
		Integer providerId = null;
		try
		{
			providerId = Integer.parseInt(request.getParameter("manualCheckinDoctor"));
			ProviderService providerService = Context.getProviderService();
			org.openmrs.Provider openmrsProvider = providerService.getProvider(providerId);
			provider.setProvider(openmrsProvider);
		} catch (Exception e)
		{
			log.error("Could not assign provider: "+providerId);
			log.error(e.getMessage());
			log.error(Util.getStackTrace(e));
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
		newEncounter.setPrinterLocation(request.getParameter("manualCheckinStation"));
		newEncounter.setEncounterDatetime(encounterDate);
		EncounterType encType = encounterService.getEncounterType("ManualCheckin");
		if (encType == null){
			encType = new EncounterType("ManualCheckin", "Arrival from hl7 message.");
		}
		newEncounter.setEncounterType(encType);
		
		//checkin
		boolean validIdentifier = true;
		try {
			PatientIdentifierValidator.validateIdentifier(checkinPatient.getPatientIdentifier().getIdentifier(), identifierType);
		}  catch(PatientIdentifierException e) {
			validIdentifier = false;
		}
		
		boolean checkinSuccess = false;
		HashMap<String,Object> parameters = new HashMap<String,Object>();
		if (validIdentifier){
			Encounter enc = (Encounter) socketHandler.checkin(provider, checkinPatient, encounterDate,
					 null, null, newEncounter,parameters);
			if (enc != null){
				checkinSuccess = true;
				// Set the insurance category
				String insuranceCategory = request.getParameter("manualCheckinInsuranceCategory");
				if (insuranceCategory != null && insuranceCategory.trim().length() > 0) {
					Concept concept = Context.getConceptService().getConcept("Insurance");
		    		org.openmrs.module.chirdlutil.util.Util.saveObs(enc.getPatient(), concept, enc.getEncounterId(), 
		    			insuranceCategory, enc.getEncounterDatetime());
				}
			}
		}
		
		response.setContentType(ChirdlUtilConstants.HTTP_CONTENT_TYPE_TEXT_XML);
		response.setHeader(
			ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL, ChirdlUtilConstants.HTTP_HEADER_CACHE_CONTROL_NO_CACHE);
		PrintWriter pw = response.getWriter();
		pw.write("<manualCheckinResult><result>");
		if (checkinSuccess) {
			pw.write("success");
		} else {
			pw.write("fail");
		}
		
		pw.write("</result></manualCheckinResult>");
	}
	
	//put all the patient attributes in the jsp map
	private static void populatePatient(PrintWriter pw, Patient patient)
	{
		pw.write("<patient>");
		ServletUtil.writeTag("firstName", ServletUtil.escapeXML(patient.getGivenName()), pw);
		ServletUtil.writeTag("middleName", ServletUtil.escapeXML(patient.getMiddleName()), pw);
		ServletUtil.writeTag("lastName", ServletUtil.escapeXML(patient.getFamilyName()), pw);
		PersonAddress address = patient.getPersonAddress();
		if (address != null)
		{
			ServletUtil.writeTag("address1", ServletUtil.escapeXML(address.getAddress1()), pw);
			ServletUtil.writeTag("address2", ServletUtil.escapeXML(address.getAddress2()), pw);
			ServletUtil.writeTag("city", ServletUtil.escapeXML(address.getCityVillage()), pw);
			ServletUtil.writeTag("zip", ServletUtil.escapeXML(address.getPostalCode()), pw);
			ServletUtil.writeTag("state", ServletUtil.escapeXML(address.getStateProvince()), pw);
		}
		
		ServletUtil.writeTag("sex", ServletUtil.escapeXML(patient.getGender()), pw);
		Date dob = patient.getBirthdate();
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		ServletUtil.writeTag("dob", ServletUtil.escapeXML(format.format(dob)), pw);
		
		PersonAttribute attribute = patient.getAttribute(ChirdlUtilConstants.PERSON_ATTRIBUTE_RACE);
		
		ConceptService conceptService = Context.getConceptService();
		ConceptSource conceptSource = conceptService.getConceptSourceByName("Wishard Race Codes");
		
		String generalPatientRaceCategory = null;
		
		if(attribute != null){
			Concept patientRaceConcept = conceptService.getConceptByMapping(attribute.getValue(), conceptSource.getName());
			if(patientRaceConcept!=null){
				generalPatientRaceCategory = patientRaceConcept.getName().getName();
			}
		}
		
		pw.write("<race>");
		pw.write(ServletUtil.escapeXML("" + generalPatientRaceCategory));
		pw.write("</race>");
		
		attribute = patient.getAttribute("Telephone Number");
		if (attribute != null)
		{
			ServletUtil.writeTag("dayPhone", ServletUtil.escapeXML(attribute.getValue()), pw);
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
				ServletUtil.writeTag("ssn1", ServletUtil.escapeXML(ssn.substring(0, 3)), pw);
			}
			if (ssn.length() >= 5)
			{
				ServletUtil.writeTag("ssn2", ServletUtil.escapeXML(ssn.substring(3, 5)), pw);
			}
			if (ssn.length() >= 9)
			{
				ServletUtil.writeTag("ssn3", ServletUtil.escapeXML(ssn.substring(5, 9)), pw);
			}
		}

		attribute = patient.getAttribute("Next of Kin");
		if (attribute != null)
		{
			String value = attribute.getValue();
			StringTokenizer tokenizer = new StringTokenizer(value, "|");
			if (tokenizer.hasMoreTokens()) {
				ServletUtil.writeTag("nextOfKinFirstName", ServletUtil.escapeXML(tokenizer.nextToken()), pw);
			}
			
			if (tokenizer.hasMoreTokens()) {
				ServletUtil.writeTag("nextOfKinLastName", ServletUtil.escapeXML(tokenizer.nextToken()), pw);
			}
		}

		PatientIdentifier patientIdentifier = patient.getPatientIdentifier();

		if (patientIdentifier != null)
		{
			String mrn = patientIdentifier.getIdentifier();
			mrn = Util.removeLeadingZeros(mrn);
			ServletUtil.writeTag("mrn", ServletUtil.escapeXML(mrn), pw);
		}

		//List<org.openmrs.Encounter> encounters = encounterService
		//		.getEncounters(patient, null, null, null, null, null, null,null,null,false); // CHICA-1151 Add null parameters for Collection<VisitType> and Collection<Visit>


		EncounterSearchCriteria encounterSearchCriteria = new EncounterSearchCriteriaBuilder().setPatient(patient).setIncludeVoided(false)
				.createEncounterSearchCriteria();
		List<org.openmrs.Encounter> encounters = Context.getService(EncounterService.class).getEncounters(encounterSearchCriteria); 
	
		if (encounters != null && encounters.size() > 0)
		{
			Encounter encounter = (Encounter) encounters.get(0);
			
			// CHICA-221 Use the provider that has the "Attending Provider" role for the encounter
			org.openmrs.Provider provider = org.openmrs.module.chirdlutil.util.Util.getProviderByAttendingProviderEncounterRole(encounter);
			
			if (provider != null)
			{
				ServletUtil.writeTag("doctor", provider.getId(), pw);
			}

			ServletUtil.writeTag("station", ServletUtil.escapeXML(encounter.getPrinterLocation()), pw);
			ServletUtil.writeTag("insuranceCode", ServletUtil.escapeXML(encounter.getInsuranceSmsCode()), pw);
		}
		
		pw.write("</patient>");
	}
}
