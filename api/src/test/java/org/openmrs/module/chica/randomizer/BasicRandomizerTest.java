package org.openmrs.module.chica.randomizer;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chica.hibernateBeans.Study;
import org.openmrs.module.chica.service.ChicaService;
import org.openmrs.module.chica.service.EncounterService;
import org.openmrs.module.chica.test.TestUtil;
import org.openmrs.module.chica.util.Util;
import org.openmrs.test.BaseModuleContextSensitiveTest;


public class BasicRandomizerTest extends BaseModuleContextSensitiveTest{
    
    
    /**
     * Set up the database with the initial dataset before every test method in
     * this class.
     * 
     * Require authorization before every test method in this class
     * 
     */
    @Before
    public void runBeforeEachTest() throws Exception 
    {
        // create the basic user and give it full rights
        initializeInMemoryDatabase();
        executeDataSet(TestUtil.PATIENT_FORMS_FILE);
       // executeDataSet(TestUtil.PATIENT_PROVIDER_FILE);
        executeDataSet(TestUtil.ENCOUNTERS_FILE);
        executeDataSet(TestUtil.STUDY_SETUP_FILE);
        // authenticate to the temp database
        authenticate();
    }
    
    @Test
    public final void testRandomize() {
        ConceptService conceptService = Context.getConceptService();
        Study study = Context.getService(ChicaService.class).getStudyByTitle("Smoking Cessation Study");
        Integer studyConceptId = study.getStudyConceptId();
        Concept studyConcept = conceptService.getConcept(studyConceptId);
        PersonService personService = Context.getPersonService();
        System.out.println("000: " + studyConcept.getName().getName() );
        // make sure the patient gets randomized to a given study once
        String studyConceptName = studyConcept.getName().getName();//smoking_study_arm
        String studyConceptIdString = String.valueOf(studyConcept
                .getConceptId());//15452
        String studyAttributeName = studyConceptName + " ("
                + studyConceptIdString + ")"; //smoking_study_arm (15452)
        System.out.println("studyAttributeName: " + studyAttributeName );
        PersonAttributeType studyConceptIdAttr = personService
                .getPersonAttributeTypeByName(studyAttributeName);
        PersonAttribute studyAttribute = null;
        PatientService patientService = Context.getPatientService();
        int patientId = 2298;
        Patient patient = patientService.getPatient(patientId);
        if (studyConceptIdAttr != null)
        {
            studyAttribute = patient.getAttribute(studyConceptIdAttr
                    .getPersonAttributeTypeId());
        }

        ObsService obsService = Context.getObsService();
        List<Obs> obsList = obsService.getObservationsByPersonAndConcept(patient, studyConcept);
        
        //make sure the patient doesn't have a person attribute or obs stored for the study
        if (studyAttribute == null&&(obsList==null||obsList.size()==0))
        {
            // get the possible categories for the study
            Collection<ConceptAnswer> answers = studyConcept.getAnswers();
            System.out.println("111: " + answers );
            ConceptAnswer[] answerArray = new ConceptAnswer[answers.size()];
            System.out.println("222: " + answerArray.length );
            answers.toArray(answerArray);
            // randomize to a study category
            int randomNum = Util.GENERATOR.nextInt(answers.size());
            System.out.println("aaa: " + randomNum );
            ConceptAnswer randomAnswer = answerArray[randomNum];
            System.out.println("bbb: " + randomAnswer.getConcept().getName().getName() );
            //PersonAttributeType studyConceptIdAttribute = getStudyPersonAttributeType(studyConcept);
            String randomAnswerName = randomAnswer.getAnswerConcept().getName()
                    .getName();
            String randomAnswerId = String.valueOf(randomAnswer.getAnswerConcept().getConceptId());
            String studyAnswerName = randomAnswerName + " (" + randomAnswerId + ")";
            //PatientService patientService = Context.getPatientService();
            PersonAttribute attribute = new PersonAttribute();
            attribute.setAttributeType(studyConceptIdAttr);
            attribute.setValue(studyAnswerName);
            patient.addAttribute(attribute);
            patientService.savePatient(patient);

            Obs obs = new Obs();
            obs.setConcept(studyConcept);
            obs.setPerson(patient);
            obs.setValueCoded(randomAnswer.getAnswerConcept());
            obs.setObsDatetime(new java.util.Date());
            
            EncounterService encounterService = Context.getService(EncounterService.class);
            obs.setLocation(encounterService.getEncounter(30).getLocation());
            obs.setEncounter(encounterService.getEncounter(30));
            obsService.saveObs(obs, null);
         }
    }
    
    @Test
    public final void testSaveStudyPersonAttribute() {
        //fail("Not yet implemented"); // TODO
    }
    
    @Test
    public final void testSaveStudyObs() {
        //fail("Not yet implemented"); // TODO
    }
    
}
