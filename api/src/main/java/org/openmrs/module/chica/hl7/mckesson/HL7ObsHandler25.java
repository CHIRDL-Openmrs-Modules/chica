/**
 * 
 */
package org.openmrs.module.chica.hl7.mckesson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.hibernate.Hibernate;
import org.openmrs.Concept;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptName;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.LocationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;
import org.openmrs.module.sockethl7listener.HL7EncounterHandler;
import org.openmrs.module.sockethl7listener.HL7ObsHandler;
import org.openmrs.module.sockethl7listener.HL7PatientHandler;
import org.openmrs.module.sockethl7listener.HL7SocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Varies;
import ca.uhn.hl7v2.model.v25.datatype.CE;
import ca.uhn.hl7v2.model.v25.datatype.NM;
import ca.uhn.hl7v2.model.v25.datatype.ST;
import ca.uhn.hl7v2.model.v25.datatype.TS;
import ca.uhn.hl7v2.model.v25.datatype.TX;
import ca.uhn.hl7v2.model.v25.message.ADT_A01;
import ca.uhn.hl7v2.model.v25.segment.MSH;
import ca.uhn.hl7v2.model.v25.segment.OBX;
import ca.uhn.hl7v2.model.v25.segment.PID;
import ca.uhn.hl7v2.parser.PipeParser;
import ca.uhn.hl7v2.validation.impl.NoValidation;

/**
 * @author tmdugan
 * 
 */
public class HL7ObsHandler25 implements HL7ObsHandler
{

	private static final Logger log = LoggerFactory.getLogger(HL7ObsHandler25.class);

	public static MSH getMSH(ADT_A01 adt)
	{
		return adt.getMSH();
	}

	public static PID getPID(ADT_A01 adt)
	{
		return adt.getPID();
	}

	private OBX getOBX(Message message, int orderRe, int obRep)
	{	
		if (message instanceof ADT_A01)
		{
			return getOBX((ADT_A01) message, obRep);
		}
		return null;
	}

	private OBX getOBX(ADT_A01 adt,int obRep)
	{

		OBX obx = null;
		try
		{
			obx = adt.getOBX(obRep);
		} catch (Exception e)
		{
			log.error("Error getting OBX segment for obRep: {}.", obRep, e);
		}

		return obx;
	}

	public static Date TranslateDate(TS ts)
	{
		int day = 0;
		int month = 0;
		int year = 0;
		int hour = 12;
		int minute = 0;
		int second = 0;

		String dateString = ts.getTime().getValue();

		if (dateString == null)
		{
			return null;
		}

		char[] dateChars = dateString.toCharArray();
		int length = dateChars.length;

		String yearString = "";
		String monthString = "";
		String dayString = "";
		String hourString = "";
		String minuteString = "";
		String secondString = "";

		for (int i = 0; i < length; i++)
		{
			if (i <= 3)
			{
				yearString += dateChars[i];
			}
			if (i >= 4 && i <= 5)
			{
				monthString += dateChars[i];
			}
			if (i >= 6 && i <= 7)
			{
				dayString += dateChars[i];
			}
			if (i >= 8 && i <= 9)
			{
				hourString += dateChars[i];
			}
			if (i >= 10 && i <= 11)
			{
				minuteString += dateChars[i];
			}
			if (i >= 12 && i <= 13)
			{
				secondString += dateChars[i];
			}
		}

		if (yearString.length() > 0)
		{
			try
			{
				year = Integer.parseInt(yearString);
			} catch (NumberFormatException e)
			{
			}
		}
		if (monthString.length() > 0)
		{
			try
			{
				month = Integer.parseInt(monthString);
			} catch (NumberFormatException e)
			{
			}
		}
		if (dayString.length() > 0)
		{
			try
			{
				day = Integer.parseInt(dayString);
			} catch (NumberFormatException e)
			{
			}
		}
		if (hourString.length() > 0)
		{
			try
			{
				hour = Integer.parseInt(hourString);
			} catch (NumberFormatException e)
			{
			}
		}
		if (minuteString.length() > 0)
		{
			try
			{
				minute = Integer.parseInt(minuteString);
			} catch (NumberFormatException e)
			{
			}
		}
		if (secondString.length() > 0)
		{
			try
			{
				second = Integer.parseInt(secondString);
			} catch (NumberFormatException e)
			{
			}
		}

		Calendar cal = Calendar.getInstance();
		if(year == 0){
			return null;
		}
		cal.set(year, month - 1, day, hour, minute, second);

		return cal.getTime();

	}

	public String getSendingFacility(Message message)
	{
		if (!(message instanceof ADT_A01))
		{
			return null;
		}

		MSH msh = getMSH((ADT_A01) message);
		return msh.getSendingFacility().getNamespaceID().getValue();
	}

	public Date getDateStarted(Message message)
	{
		if (!(message instanceof ADT_A01))
		{
			return null;
		}
		// OBR segment --Observation start time
		TS tsObsvStartDateTime = getMSH((ADT_A01) message)
					.getDateTimeOfMessage();

		Date sdt = TranslateDate(tsObsvStartDateTime);
		return sdt;
	}

	public String getObsValueType(Message message, int orderRep, int obxRep)
	{
	    OBX obx = getOBX(message, orderRep, obxRep);
	    if(obx != null){
	        return obx.getValueType().toString(); 
	    }
		return null;
	}

	public Date getObsDateTime(Message message, int orderRep, int obxRep)
	{
	    OBX obx = getOBX(message, orderRep, obxRep);
	    if(obx != null){
	        TS tsObsDateTime = obx.getDateTimeOfTheObservation();
	        Date obsDateTime = TranslateDate(tsObsDateTime);
	        return obsDateTime;
	    }
		
		return null;
	}

	public String getConceptId(Message message, int orderRep, int obxRep)
	{
	    OBX obx = getOBX(message, orderRep, obxRep);
	    if(obx != null){
	        CE ceObsIdentifier = obx.getObservationIdentifier();
	        return ceObsIdentifier.getIdentifier().toString();
	    }
		return null;
	}

	public String getConceptName(Message message, int orderRep, int obxRep)
	{
	    OBX obx = getOBX(message, orderRep, obxRep);
	    if(obx != null){
	        CE ceObsIdentifier = obx.getObservationIdentifier();
	        return ceObsIdentifier.getText().toString();
	    }
		
		return null;
	}

	public String getTextResult(Message message, int orderRep, int obxRep)
	{
	    OBX obx = getOBX(message, orderRep, obxRep);
	    if(obx != null){
	        Varies[] values = obx.getObservationValue();
	        Varies value = null;
	        String dataString = null;

	        if (values.length > 0)
	        {
	            value = values[0];

	            if (value.getData() instanceof TX)
	            {
	                TX data = (TX) value.getData();
	                dataString = data.getValue();
	            }

	            if (value.getData() instanceof ST)
	            {
	                ST data = (ST) value.getData();
	                dataString = data.getValue();
	            }

	            return dataString;
	        }
	    }
		
		return null;
	}

	public Date getDateResult(Message message, int orderRep, int obxRep)
	{
	    OBX obx = getOBX(message, orderRep, obxRep);
	    if(obx != null){
	        Varies[] values = obx .getObservationValue();
	        Varies value = null;
	        if (values.length > 0)
	        {

	            value = values[0];
	            TS ts = (TS) value.getData();
	            Date date = TranslateDate(ts);
	            return date;
	        }
	    }
		
		return null;
	}

	public Double getNumericResult(Message message, int orderRep, int obxRep)
	{
		double dVal = 0;
		OBX obx = getOBX(message, orderRep, obxRep);
		if(obx != null){
		    Varies[] values = obx.getObservationValue();
	        Varies value = null;
	        if (values.length > 0)
	        {
	            value = values[0];
	            String nmvalue = ((NM) value.getData()).getValue();

	            if (nmvalue != null)
	            {
	                try
	                {
	                    dVal = Double.parseDouble(nmvalue);
	                } catch (NumberFormatException ex)
	                {
	                    dVal = 0;
	                }
	            }
	        } 
		}
		
		return dVal;
	}

	private Concept processCEType(Varies value,
			String pIdentifierString, String conceptQuestionId)
	{
		String conceptName = ((CE) value.getData()).getText().toString();
		String stConceptId = ((CE) value.getData()).getIdentifier().toString();
		Integer intObxValueID = null;

		try
		{
			intObxValueID = Integer.parseInt(stConceptId);
		} catch (NumberFormatException ne)
		{
			intObxValueID = 1;
			conceptName = stConceptId;
		}

		try
		{
			Concept answer = new Concept();
			answer.setConceptId(intObxValueID);
			ConceptName name = new ConceptName();
			name.setName( conceptName);
			name.setLocale(new Locale("en_US"));
			answer.addName(name);

			return answer;
		} catch (RuntimeException e)
		{
			log.error(" Exception creating answer concept OBX segment for patient identifier: {} OBX coded value: {}; concept question id: {} concept name: {}", pIdentifierString, stConceptId, conceptQuestionId, conceptName, e);
		}

		return null;
	}

	public Concept getCodedResult(Message message, int orderRep, int obxRep,
			 String pIdentifierString, String obsvID,
			String obsValueType)
	{
	    OBX obx = getOBX(message, orderRep, obxRep);
	    if(obx != null){
	        Varies[] values = obx.getObservationValue();
	        Varies value = null;

	        if (values.length > 0)
	        {
	            value = values[0];

	            if (obsValueType.equals("CE"))
	            {
	                return processCEType(value, pIdentifierString, obsvID);
	            }
	        }
	    }

		return null;
	}
	
	public ArrayList<Obs> getObs(Message message,Patient patient) throws HL7Exception
	{
		ConceptService conceptService = Context.getConceptService();

		PipeParser parser = new PipeParser();
		parser.setValidationContext(new NoValidation());
		PatientHandler patientHandler = new PatientHandler();
		HL7PatientHandler hl7PatientHandler = new HL7PatientHandler25();
		HL7EncounterHandler hl7EncounterHandler = new HL7EncounterHandler25();
		HL7SocketHandler hl7SocketHandler = new HL7SocketHandler(parser,
				patientHandler, this, hl7EncounterHandler,
				hl7PatientHandler,null);
		ArrayList<Obs> allObs = new ArrayList<>();
		LocationService locationService = Context.getLocationService();
		String sendingFacility = getSendingFacility(message);
		Location existingLoc = locationService.getLocation(sendingFacility);
			
		if (message instanceof ADT_A01) {
			ADT_A01 adt = (ADT_A01) message;
			
			int numObs = adt.getOBXReps();
			ConceptDatatype codedDatatype = conceptService.getConceptDatatypeByName(ChirdlUtilConstants.CONCEPT_DATATYPE_CODED);
			ConceptDatatype numericDatatype = conceptService.getConceptDatatypeByName(ChirdlUtilConstants.CONCEPT_DATATYPE_NUMERIC);
			ConceptDatatype dateTimeDatatype = conceptService.getConceptDatatypeByName(ChirdlUtilConstants.CONCEPT_DATATYPE_DATETIME);
			ConceptDatatype textDatatype = conceptService.getConceptDatatypeByName(ChirdlUtilConstants.CONCEPT_DATATYPE_TEXT);
			
			// Initialize the objects in case they go to a caching mechanism.
			Hibernate.initialize(codedDatatype);
			Hibernate.initialize(numericDatatype);
			Hibernate.initialize(dateTimeDatatype);
			Hibernate.initialize(textDatatype);
			
			Map<String,ConceptDatatype> conceptDataTypeMap = new HashMap<String,ConceptDatatype>();
			for (int j = 0; j < numObs; j++) {
				Obs obs = hl7SocketHandler.CreateObservation(null, false, message, 0, j, existingLoc, patient);
				
				Concept obsConcept = obs.getConcept();
				//if the concept datatype is null,
				//infer the type from the data
				if (obsConcept.getDatatype() == null) {
					if (obs.getValueCoded() != null) {
						obsConcept.setDatatype(codedDatatype);
					}
					
					if (obs.getValueNumeric() != null) {
						obsConcept.setDatatype(numericDatatype);
					}
					
					if (obs.getValueDatetime() != null) {
						obsConcept.setDatatype(dateTimeDatatype);
					}
					
					if (obs.getValueText() != null) {
						obsConcept.setDatatype(textDatatype);
					}
				}
				if(obs.getObsDatetime()==null){
					//do not add obs without a date
				}else{
					allObs.add(obs);
				}
			}
			
			conceptDataTypeMap.clear();
		}
		
		return allObs;
	}
	
	/**
	 * Convenience method for retrieving a concept data type.
	 * 
	 * @param datatype The requested data type.
	 * @param conceptDataTypeMap Map used to stored previously queried data types.
	 * @param conceptService Concept Service used to retrieve the data types.
	 * @return ConceptDatatype object or null if one cannot be found.
	 */
	private ConceptDatatype getConceptDatatype(String datatype, Map<String,ConceptDatatype> conceptDataTypeMap,
	                                           ConceptService conceptService) {
		ConceptDatatype conceptDatatype = conceptDataTypeMap.get(datatype);
		if (conceptDatatype != null) {
			return conceptDatatype;
		}
		
		conceptDatatype = conceptService.getConceptDatatypeByName(datatype);
		conceptDataTypeMap.put(datatype, conceptDatatype);
		return conceptDatatype;
	}

	/* (non-Javadoc)
	 * @see org.openmrs.module.sockethl7listener.HL7ObsHandler#getReps(ca.uhn.hl7v2.model.Message)
	 */
	public int getReps(Message message)
	{
		int reps = 0;
		if ((message instanceof ADT_A01))
		{
			reps = ((ADT_A01) message).getOBXReps();
		}
		return reps;
	}

	/**
     * @see org.openmrs.module.sockethl7listener.HL7ObsHandler#getDateStopped(ca.uhn.hl7v2.model.Message)
     */
    @Override
    public Date getDateStopped(Message message) {
	    return null;
    }
    
    /**
     * @see org.openmrs.module.sockethl7listener.HL7ObsHandler#getUnits(Message, int, int)
     * DWE CHICA-635
     */
    public String getUnits(Message message, int orderRep, int obxRep)
    {
    	OBX obx = getOBX(message, orderRep, obxRep);
    	if(obx != null){
    		CE units = obx.getUnits();
    		return units.getIdentifier().getValue();
    	}
    				
		return "";
    } 
}
