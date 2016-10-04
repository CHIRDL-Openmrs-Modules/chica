/**
 * 
 */
package org.openmrs.module.chica.web;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
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
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.hibernateBeans.Chica1Appointment;
import org.openmrs.module.chica.hibernateBeans.Chica1Patient;
import org.openmrs.module.chica.hibernateBeans.Chica1PatientObsv;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chica.util.Util;
import org.openmrs.module.chirdlutil.SSNValidator;
import org.openmrs.module.sockethl7listener.Provider;
import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 * @author tmdugan
 * 
 */
public class LoadObsController extends SimpleFormController
{

	private Log log = LogFactory.getLog(this.getClass());

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

	@Override
	protected Map referenceData(HttpServletRequest request) throws Exception
	{
		long startTime = System.currentTimeMillis();
		Map<String, Object> map = new HashMap<String, Object>();
		String loadObs = request.getParameter("loadObs");
		HashSet<String> missingQuestions = new HashSet<String>();
		HashMap<String, String> missingAnswers = new HashMap<String, String>();

		if (loadObs != null)
		{
			LocationService locationService = Context.getLocationService();
			EncounterService encounterService = Context
					.getService(EncounterService.class);
			PatientService patientService = Context.getPatientService();
			PersonService personService = Context.getPersonService();
			UserService userService = Context.getUserService();
			int numPatients = 0;

			ChicaService chicaService = Context.getService(ChicaService.class);

			Date minEncounterTime = null;
			Date maxEncounterTime = null;

			String minEncTimeString = request.getParameter("minEncounterTime");
			String maxEncTimeString = request.getParameter("maxEncounterTime");

			SimpleDateFormat formatEncTime = new SimpleDateFormat("MM/dd/yyyy");
			try
			{
				minEncounterTime = formatEncTime.parse(minEncTimeString);
			} catch (Exception e1)
			{
			}
			try
			{
				maxEncounterTime = formatEncTime.parse(maxEncTimeString);
			} catch (Exception e1)
			{
			}
			List<Chica1Patient> patients = chicaService.getChica1Patients();
			Integer totalPatients = patients.size();
			for (Chica1Patient currPatient : patients)
			{
				numPatients++;

				try
				{
					org.openmrs.Patient openmrsPatient = new org.openmrs.Patient();
					SimpleDateFormat format = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					Location location = locationService.getLocation("PCPS");
					try
					{

						PersonAddress address = new PersonAddress();
						if (currPatient.getCity() != null)
						{
							address
									.setCityVillage(currPatient.getCity()
											.trim());
						}
						if (currPatient.getState() != null)
						{
							address.setStateProvince(currPatient.getState()
									.trim());
						}
						if (currPatient.getStreetAddress() != null)
						{
							address.setAddress1(currPatient.getStreetAddress()
									.trim());
						}
						if (currPatient.getStreetAddress2() != null)
						{
							address.setAddress2(currPatient.getStreetAddress2()
									.trim());
						}
						if (currPatient.getZip() != null)
						{
							address.setPostalCode(currPatient.getZip().trim());
						}
						openmrsPatient.addAddress(address);

						if (currPatient.getGender() != null)
						{
							openmrsPatient.setGender(currPatient.getGender()
									.trim());
						}

						PersonName name = new PersonName();
						if (currPatient.getNameFirst() != null)
						{
							name
									.setGivenName(currPatient.getNameFirst()
											.trim());
						}
						if (currPatient.getNameLast() != null)
						{
							name
									.setFamilyName(currPatient.getNameLast()
											.trim());
						}
						if (currPatient.getNameMiddle() != null)
						{
							name.setMiddleName(currPatient.getNameMiddle()
									.trim());
						}
						openmrsPatient.addName(name);

						PersonAttribute attribute = new PersonAttribute();
						PersonAttributeType attributeType = personService
								.getPersonAttributeType("Telephone Number");
						attribute.setAttributeType(attributeType);
						if (currPatient.getDayPhoneNumber() != null)
						{
							attribute.setValue(currPatient.getDayPhoneNumber()
									.trim());
							openmrsPatient.addAttribute(attribute);
						}

						attribute = new PersonAttribute();
						attributeType = personService
								.getPersonAttributeType("Civil Status");
						attribute.setAttributeType(attributeType);
						if (currPatient.getMaritalStatus() != null)
						{
							attribute.setValue(currPatient.getMaritalStatus()
									.trim());
							openmrsPatient.addAttribute(attribute);
						}

						attribute = new PersonAttribute();
						attributeType = personService
								.getPersonAttributeType("Mother's maiden name");
						attribute.setAttributeType(attributeType);
						if (currPatient.getMotherMaidenName() != null)
						{
							attribute.setValue(currPatient
									.getMotherMaidenName().trim());
							openmrsPatient.addAttribute(attribute);
						}

						attribute = new PersonAttribute();
						attributeType = personService
								.getPersonAttributeType("Race");
						attribute.setAttributeType(attributeType);
						if (currPatient.getRace() != null)
						{
							attribute.setValue(currPatient.getRace().trim());
							openmrsPatient.addAttribute(attribute);
						}

						String ssn = currPatient.getSocialSecurityNumber();

						// patient ssn
						if (ssn != null)
						{
							ssn = ssn.trim();
							if (ssn.length() > 0)
							{
								if ((new SSNValidator()).isValid(ssn))
								{
									PatientIdentifierType type = patientService
											.getPatientIdentifierTypeByName("SSN");
									PatientIdentifier pi = new PatientIdentifier(
											ssn, type, location);
									pi.setCreator(Context.getAuthenticatedUser());
									pi.setPatient(openmrsPatient);
									openmrsPatient.addIdentifier(pi);
								} else
								{
									// Only create the person's attribute ssn if it
									// was an invalid string
									// if it was invalid because it was null, don't
									// create the attr.
									PersonAttributeType ssnType = personService
											.getPersonAttributeTypeByName("SSN");
									PersonAttribute pa = new PersonAttribute(
											ssnType, ssn);
									openmrsPatient.addAttribute(pa);
								}
							}
						}

						attribute = new PersonAttribute();
						attributeType = personService
								.getPersonAttributeType("Religion");
						attribute.setAttributeType(attributeType);
						if (currPatient.getReligion() != null)
						{
							attribute
									.setValue(currPatient.getReligion().trim());
							openmrsPatient.addAttribute(attribute);
						}

						PatientIdentifierType identType = patientService
								.getPatientIdentifierType("MRN_OTHER");
						if (currPatient.getMedicalRecordNumber() != null)
						{
							String mrn = currPatient.getMedicalRecordNumber()
									.trim();
							if (!mrn.contains("-") && mrn.length() > 1)
							{
								mrn = mrn.substring(0, mrn.length() - 1) + "-"
										+ mrn.substring(mrn.length() - 1);
							}
							PatientIdentifier patientIdentifier = new PatientIdentifier(
									mrn, identType, location);
							patientIdentifier.setPreferred(true);
							openmrsPatient.addIdentifier(patientIdentifier);
						}

						if (currPatient.getDateOfBirth() != null)
						{
							Date dateOfBirth = format.parse(currPatient
									.getDateOfBirth().trim());
							openmrsPatient.setBirthdate(dateOfBirth);
						}

						List<Patient> prevOpenmrsPatients = patientService
								.getPatientsByIdentifier(
										openmrsPatient.getPatientIdentifier()
												.getIdentifier(), false);

						if (prevOpenmrsPatients.size() == 0)
						{
							patientService.savePatient(openmrsPatient);
						} else
						{
							openmrsPatient = prevOpenmrsPatients.get(0);
						}

						currPatient.setOpenmrsPatientId(openmrsPatient
								.getPatientId());
						chicaService.updateChica1Patient(currPatient);
					} catch (Exception e)
					{
						log.error("Error for chica1 patient_id: "
								+ currPatient.getPatientId());
						log.error(e.getMessage());
						log.error(org.openmrs.module.chirdlutil.util.Util
								.getStackTrace(e));
						//If there is a problem loading the chica1 patient,
						//don't try to create encounters or obs
						continue;
					}

					Integer patientId = currPatient.getPatientId();
					List<Chica1Appointment> appointments = chicaService
							.getChica1AppointmentsByPatient(patientId);

					for (Chica1Appointment currAppointment : appointments)
					{
						Encounter encounter = null;
						try
						{
							encounter = new Encounter();

							if (currAppointment.getDateOfAppt() != null)
							{
								Date encounterDate = format
										.parse(currAppointment.getDateOfAppt()
												.trim());
								encounter.setEncounterDatetime(encounterDate);
							}
							User provider = userService
									.getUserByUsername(".Other.");

							encounter.setProvider(provider);
							encounter.setLocation(location);
							encounter.setPatient(openmrsPatient);
							if (minEncounterTime != null
									&& encounter.getEncounterDatetime()
											.compareTo(minEncounterTime) < 0)
							{
								continue;
							}
							if (maxEncounterTime != null
									&& encounter.getEncounterDatetime()
											.compareTo(maxEncounterTime) > 0)
							{
								continue;
							}
							Integer openmrsEncounterId = currAppointment
									.getOpenmrsEncounterId();
							if (openmrsEncounterId == null)
							{
								encounterService.saveEncounter(encounter);

								currAppointment.setOpenmrsEncounterId(encounter
										.getEncounterId());
								chicaService
										.updateChica1Appointment(currAppointment);
							} else
							{
								encounter = (Encounter) encounterService
										.getEncounter(openmrsEncounterId);
							}
						} catch (Exception e)
						{
							log.error("Error for chica1 appointment id: "
									+ currAppointment.getApptId());
							log.error(e.getMessage());
							log.error(org.openmrs.module.chirdlutil.util.Util
									.getStackTrace(e));
							//If there is a problem loading the chica1 appointment,
							//don't try to create obs
							continue;
						}

							Integer psfId = currAppointment.getApptPsfId();
							Integer pwsId = currAppointment.getApptPwsId();
							List<Chica1PatientObsv> obs = chicaService
									.getChicaPatientObsByPSF(psfId, patientId);

							obs.addAll(chicaService.getChicaPatientObsByPWS(
									pwsId, patientId));

							// load obs by psf/pws id
							for (Chica1PatientObsv currObs : obs)
							{
								createObs(currObs, format, location,
										openmrsPatient, encounter,
										missingQuestions, missingAnswers);
							}
							// try to link obs to appointment by patient_id and
							// date_stamp
							String date = currAppointment.getDateOfAppt();
							obs = chicaService.getUnloadedChicaPatientObs(
									patientId, date);
							for (Chica1PatientObsv currObs : obs)
							{
								String obsvName = chicaService
										.getObsvNameByObsvId(currObs
												.getObsvId());
								if (obsvName != null)
								{
									obsvName = obsvName.trim();
								} else
								{
									continue;
								}
								if (obsvName.equals("VisitDoctor"))
								{
									if (currObs.getObsvVal() != null)
									{
										createProvider(currObs.getObsvVal()
												.trim());
									}
								}

								// update the encounter with the insurance
								// information
								if (obsvName.equals("Insurance"))
								{
									encounter = (Encounter) encounterService
											.getEncounter(encounter
													.getEncounterId());
									encounter.setInsuranceSmsCode(currObs
											.getObsvVal());
									encounterService.saveEncounter(encounter);
									
									currObs.setObsvVal(chicaService
											.getInsCategoryByInsCode(currObs
													.getObsvVal(),"SMS","Eskenazi"));
								}
								createObs(currObs, format, location,
										openmrsPatient, encounter,
										missingQuestions, missingAnswers);

							}
						
						Context.clearSession();
					}
					

				} catch (Exception e)
				{
					log.error(e.getMessage() + ": patient_id "
							+ currPatient.getPatientId());
					log
							.error(org.openmrs.module.chirdlutil.util.Util
									.getStackTrace(e));
				}

				if(numPatients % 100 == 0){
					log.info("Processed "+numPatients+" patients out of "+totalPatients+" patients in loadObs.");
				}
			}
			log.info(((System.currentTimeMillis() - startTime) / 1000)
					+ " final elapsed seconds for loadObs.");
			
			Iterator<String> iter = null;
			
			if (missingQuestions.size() > 0)
			{
				log
						.error("Question concepts in chica1 but missing from chica2 dictionary:");

				iter = missingQuestions.iterator();
				while (iter.hasNext())
				{
					String question = iter.next();
					log.error(question);
				}
			}
			
			if (missingAnswers.size() > 0)
			{
				log
						.error("Answer concepts in chica1 but missing from chica2 dictionary. This could also mean that the concept type is incorrectly marked as coded in chica2:");
				iter = missingAnswers.keySet().iterator();
				while (iter.hasNext())
				{
					String answer = iter.next();
					String question = missingAnswers.get(answer);
					log.error(answer + " (" + question + ")");
				}
			}
		}
		
		return map;
	}

	private void createObs(Chica1PatientObsv currObs, SimpleDateFormat format,
			Location location, Patient openmrsPatient, Encounter encounter,
			HashSet<String> missingQuestions,
			HashMap<String, String> missingAnswers)
	{
		ConceptService conceptService = Context.getConceptService();
		ObsService obsService = Context.getObsService();
		ChicaService chicaService = Context.getService(ChicaService.class);
		try
		{
			Obs openmrsObs = new Obs();
			if (currObs.getDateStamp() != null)
			{
				Date obsDate = format.parse(currObs.getDateStamp().trim());
				openmrsObs.setObsDatetime(obsDate);
			}

			openmrsObs.setLocation(location);
			Concept question = null;
			String obsName = chicaService.getObsvNameByObsvId(currObs
					.getObsvId());
			if (obsName != null)
			{
				obsName = obsName.trim();
			}
			if (obsName != null)
			{
				question = conceptService.getConceptByName(obsName);
			}
			if (question != null)
			{
				openmrsObs.setConcept(question);

				if (currObs.getObsvVal() != null
						&& currObs.getObsvVal().trim().length() > 0)
				{
					String obsVal = currObs.getObsvVal().trim();
					if (question.getDatatype().getHl7Abbreviation().equals(
							ConceptDatatype.CODED))
					{
						Concept answer = conceptService
								.getConceptByName(obsVal);

						if (answer != null)
						{
							openmrsObs.setValueCoded(answer);
						} else
						{
							missingAnswers.put(obsVal, obsName);
							//This obs will not be loaded but don't log this
							//because it will clutter up the logs considerably
							return;
						}
					} else if (question.getDatatype().getHl7Abbreviation()
							.equals(ConceptDatatype.NUMERIC))
					{
						openmrsObs.setValueNumeric(Double.parseDouble(obsVal));
					} else
					{
						openmrsObs.setValueText(obsVal);
					}
				}
			} else
			{
				missingQuestions.add(obsName);
				//This obs will not be loaded but do not
				//log the error because it could clutter up
				//the logs considerably
				return;
			}

			openmrsObs.setEncounter(encounter);
			openmrsObs.setPerson(openmrsPatient);
			obsService.createObs(openmrsObs);
			currObs.setOpenmrsObsId(openmrsObs.getObsId());
			chicaService.setChica1PatientObsvObsId(currObs);
		} catch (Exception e)
		{
			log.error(e.getMessage());
			log.error(org.openmrs.module.chirdlutil.util.Util.getStackTrace(e));
		}
	}

	public Provider createProvider(String nameString)
	{
		Provider provider = new Provider();

		StringTokenizer tokenizer = new StringTokenizer(nameString);
		if (tokenizer.hasMoreTokens())
		{
			provider.setFirstName(tokenizer.nextToken());
		}
		if (tokenizer.hasMoreTokens())
		{
			provider.setLastName(tokenizer.nextToken());
		}
		provider.createUserForProvider(provider);
		return provider;
	}
}
