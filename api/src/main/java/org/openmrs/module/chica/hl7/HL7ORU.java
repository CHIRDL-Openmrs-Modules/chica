package org.openmrs.module.chica.hl7;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptNumeric;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.hl7.HL7Constants;
import org.openmrs.module.chica.hibernateBeans.Encounter;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.chirdlutil.util.DateUtil;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.EncounterAttribute;
import org.openmrs.module.chirdlutilbackports.hibernateBeans.EncounterAttributeValue;
import org.openmrs.module.chirdlutilbackports.service.ChirdlUtilBackportsService;
import ca.uhn.hl7v2.model.v25.datatype.DT;
import ca.uhn.hl7v2.model.v25.datatype.NM;
import ca.uhn.hl7v2.model.v25.datatype.ST;
import ca.uhn.hl7v2.model.v25.datatype.TM;
import ca.uhn.hl7v2.model.v25.datatype.TS;
import ca.uhn.hl7v2.model.v25.message.ORU_R01;
import ca.uhn.hl7v2.model.v25.segment.MSH;
import ca.uhn.hl7v2.model.v25.segment.OBR;
import ca.uhn.hl7v2.model.v25.segment.OBX;
import ca.uhn.hl7v2.model.v25.segment.PID;
import ca.uhn.hl7v2.model.v25.segment.PV1;

/**
 * CHICA-1070
 * Class used to create a basic HL7 v2.5 ORU^R01 message
 * Extend this class as needed to populate additional fields in each segment
 */
public class HL7ORU
{
	private ORU_R01 oru;
	private Log log = LogFactory.getLog(this.getClass());
	private Encounter encounter = null;
	private Patient patient = null;
	
	/**
	 * Constructor
	 * @param patient
	 * @param encounter
	 */
	public HL7ORU(Patient patient, Encounter encounter)
	{
		oru = new ORU_R01();
		this.patient = patient;
		this.encounter = encounter;
	}
	
	/**
	 * @return the ORU_R01
	 */
	public ORU_R01 getORU()
	{
		return this.oru;
	}
	
	/**
	 * Adds MSH segment and populates with minimum data
	 * @return MSH
	 */
	public MSH addSegmentMSH() 
	{
		MSH msh = oru.getMSH();

		// Get current date
		String formattedDate = DateUtil.formatDate(new Date(), ChirdlUtilConstants.DATE_FORMAT_yyyy_MM_dd_HH_mm_ss);

		try
		{
			msh.getFieldSeparator().setValue(ChirdlUtilConstants.HL7_FIELD_SEPARATOR);
			msh.getEncodingCharacters().setValue(ChirdlUtilConstants.HL7_ENCODING_CHARS);
			msh.getDateTimeOfMessage().getTime().setValue(formattedDate);
			msh.getSendingApplication().getNamespaceID().setValue(ChirdlUtilConstants.CONCEPT_CLASS_CHICA);
			msh.getSendingFacility().getNamespaceID().setValue(encounter.getLocation().getName());
			msh.getMessageType().getMessageCode().setValue(ChirdlUtilConstants.HL7_ORU);
			msh.getMessageType().getTriggerEvent().setValue(ChirdlUtilConstants.HL7_EVENT_CODE_R01);
			msh.getVersionID().getVersionID().setValue(ChirdlUtilConstants.HL7_VERSION_2_5);
			msh.getProcessingID().getProcessingID().setValue(ChirdlUtilConstants.MSH_PROCESSING_ID);
			msh.getMessageControlID().setValue(ChirdlUtilConstants.CONCEPT_CLASS_CHICA + "-" + formattedDate);
		}
		catch (Exception e) 
		{
			log.error("Exception constructing MSH segment. EncounterId: " + encounter.getEncounterId(), e);
		}

		return msh;
	}
	
	/**
	 * Adds PID segment and populates with minimum data
	 * @return PID
	 */
	public PID addSegmentPID() 
	{
		PID pid = oru.getPATIENT_RESULT().getPATIENT().getPID();

		try 
		{
			// Name
			pid.getPatientName(0).getFamilyName().getSurname().setValue(patient.getFamilyName());
			pid.getPatientName(0).getGivenName().setValue(patient.getGivenName());

			// Identifiers
			PatientIdentifier pi = patient.getPatientIdentifier(ChirdlUtilConstants.IDENTIFIER_TYPE_MRN_EHR);

			// Identifier PID-3
			// MRN
			if (pi != null) {
				pid.getPatientIdentifierList(0).getIDNumber().setValue(pi.getIdentifier());
			}

			// Gender
			pid.getAdministrativeSex().setValue(patient.getGender());

			// DOB
			pid.getDateTimeOfBirth().getTime().setValue(DateUtil.formatDate(patient.getBirthdate(), ChirdlUtilConstants.DATE_FORMAT_yyyy_MM_dd));
			pid.getSetIDPID().setValue("1");

			return pid;

		}
		catch (Exception e) 
		{
			log.error("Exception constructing PID segment. PatientId: " + patient.getPatientId(), e);
		}
		
		return pid;
	}
	
	/**
	 * Adds PV1 segment and populates with minimum data
	 * @return PV1
	 */
	public PV1 addSegmentPV1()
	{
		PV1 pv1 = oru.getPATIENT_RESULT().getPATIENT().getVISIT().getPV1();
		try
		{
			ChirdlUtilBackportsService chirdlutilbackportsService = Context.getService(ChirdlUtilBackportsService.class);			
			EncounterAttribute encounterAttribute = chirdlutilbackportsService.getEncounterAttributeByName(ChirdlUtilConstants.ENCOUNTER_ATTRIBUTE_VISIT_NUMBER);
			EncounterAttributeValue encounterAttributeValue = chirdlutilbackportsService.getEncounterAttributeValueByAttribute(encounter.getEncounterId(), encounterAttribute);

			pv1.getSetIDPV1().setValue("1");
			pv1.getPatientClass().setValue(ChirdlUtilConstants.PV1_PATIENT_CLASS);
			
			if(encounterAttributeValue != null)
			{
				pv1.getVisitNumber().getIDNumber().setValue(encounterAttributeValue.getValueText());
			}
		}
		catch(Exception e)
		{
			log.error("Exception constructing PV1 segment. EncounterId: " + encounter.getEncounterId(), e);
		}	

		return pv1;
	}
	
	/**
	 * Adds OBR segment and populates with minimum data
	 * @param orderRep
	 * @return OBR
	 */
	public OBR addSegmentOBR(int orderRep)
	{
		OBR obr = oru.getPATIENT_RESULT().getORDER_OBSERVATION(orderRep).getOBR();
	
		try 
		{ 
			int reps = oru.getPATIENT_RESULT().getORDER_OBSERVATIONReps();
			
			String encDateStr = DateUtil.formatDate(encounter.getEncounterDatetime(), ChirdlUtilConstants.DATE_FORMAT_yyyy_MM_dd_HH_mm_ss);
			obr.getObservationDateTime().getTime().setValue(encDateStr);
			obr.getSetIDOBR().setValue(String.valueOf(reps));
		}
		catch (Exception e) 
		{
			log.error("Exception constructing OBR segment. EncounterId: " + encounter.getEncounterId(), e);
		}

		return obr;
	}
	
	/**
	 * Adds OBX segment and populates with minimum data
	 * Currently only supported for coded, numeric, text, date, time, and datetime
	 * @param concept
	 * @param obs
	 * @param orderRep
	 * @param obsRep
	 * @return OBX
	 */
	public OBX addSegmentOBX(ConceptMap conceptMap, Obs obs, int orderRep, int obsRep)
	{
		OBX obx = oru.getPATIENT_RESULT().getORDER_OBSERVATION(orderRep)
				.getOBSERVATION(obsRep).getOBX();
		try
		{
			String hl7Abbreviation = conceptMap.getConcept().getDatatype().getHl7Abbreviation();
			
			obx.getSetIDOBX().setValue(String.valueOf(obsRep + 1));
			obx.getObservationIdentifier().getIdentifier().setValue(conceptMap.getConceptReferenceTerm().getCode());
			obx.getObservationIdentifier().getText().setValue(conceptMap.getConcept().getName().getName());
			obx.getObservationResultStatus().setValue(ChirdlUtilConstants.HL7_RESULT_STATUS);
			
			String formattedDate = DateUtil.formatDate(obs.getObsDatetime(), ChirdlUtilConstants.DATE_FORMAT_yyyy_MM_dd_HH_mm_ss);
			obx.getDateTimeOfTheObservation().getTime().setValue(formattedDate);
			
			if(ConceptDatatype.CODED.equalsIgnoreCase(hl7Abbreviation))
			{
				obx.getValueType().setValue(HL7Constants.HL7_CODED);
				
				ST st = new ST(oru);
				st.setValue(obs.getValueCoded().getName().getName());
				obx.getObservationValue(0).setData(st);
			}
			else if(ConceptDatatype.NUMERIC.equalsIgnoreCase(hl7Abbreviation)) 
			{
				ConceptService cs = Context.getConceptService();
				ConceptNumeric numericConcept = cs.getConceptNumeric(conceptMap.getConcept().getConceptId());
				
				if (numericConcept == null)
				{
					log.error("Error creating OBX segment. Concept defined as numeric, but was not found. Concept ID: " + conceptMap.getConcept().getConceptId());
					return null;
				}
				
				obx.getValueType().setValue(HL7Constants.HL7_NUMERIC);
				
				NM nm = new NM(oru);
				nm.setValue(String.valueOf(obs.getValueNumeric()));
				obx.getObservationValue(0).setData(nm);
				obx.getUnits().getIdentifier().setValue(numericConcept.getUnits());
			}
			else if(ConceptDatatype.TEXT.equalsIgnoreCase(hl7Abbreviation))
			{
				obx.getValueType().setValue(HL7Constants.HL7_TEXT);
				
				ST st = new ST(oru);
				st.setValue(obs.getValueText());
				obx.getObservationValue(0).setData(st);
			}
			else if(ConceptDatatype.DATETIME.equalsIgnoreCase(hl7Abbreviation)) 
			{
				obx.getValueType().setValue(HL7Constants.HL7_DATETIME);
				
				String obsValueDateTime = DateUtil.formatDate(obs.getValueDatetime(), ChirdlUtilConstants.DATE_FORMAT_yyyy_MM_dd_HH_mm_ss);
				TS ts = new TS(oru);
				ts.getTime().setValue(obsValueDateTime);
				obx.getObservationValue(0).setData(ts);
			}
			else if(ConceptDatatype.DATE.equalsIgnoreCase(hl7Abbreviation))
			{
				obx.getValueType().setValue(HL7Constants.HL7_DATE);
				
				String obsValueDate = DateUtil.formatDate(obs.getValueDate(), ChirdlUtilConstants.DATE_FORMAT_yyyy_MM_dd);
				DT dt = new DT(oru);
				dt.setValue(obsValueDate);
				obx.getObservationValue(0).setData(dt);
			}
			else if(ConceptDatatype.TIME.equalsIgnoreCase(hl7Abbreviation))
			{
				obx.getValueType().setValue(HL7Constants.HL7_TIME);
				
				String obsValueTime = DateUtil.formatDate(obs.getValueTime(), ChirdlUtilConstants.DATE_FORMAT_HH_mm_ss);
				TM tm = new TM(oru);
				tm.setValue(obsValueTime);
				obx.getObservationValue(0).setData(tm);
			}
		}
		catch(Exception e)
		{
			log.error("Exception constructing OBX segment for concept: " + conceptMap.getConcept().getConceptId(), e);
		}
		
		return obx;
	}
}
