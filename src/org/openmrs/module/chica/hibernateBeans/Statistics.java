package org.openmrs.module.chica.hibernateBeans;

import java.util.Date;

/**
 * Holds information to store in the chica_statistics table
 * 
 * @author Tammy Dugan
 * @version 1.0
 */
public class Statistics implements java.io.Serializable {

	// Fields
	private Integer statisticsId=null;
	private Integer patientId=null;
	private String formName=null;
	private Integer ruleId=null;
	private Integer formInstanceId=null;
	private Integer encounterId=null;
	private Integer position=null;
	private String answer=null;
	private String answerErr=null;
	private Date printedTimestamp=null;
	private Date scannedTimestamp=null;
	private Integer priority=null;
	private Integer obsvId=null;
	private String languageResponse=null;
	private String ageAtVisit=null;
	private Integer locationTagId = null;
	private Integer locationId = null;
	
	// Constructors

	/** default constructor */
	public Statistics() {
	}
	
	public Statistics(Statistics stat)
	{
		this.patientId = stat.getPatientId();
		this.formName = stat.getFormName();
		this.ruleId = stat.getRuleId();
		this.formInstanceId = stat.getFormInstanceId();
		this.locationTagId = stat.getLocationTagId();
		this.encounterId = stat.getEncounterId();
		this.position = stat.getPosition();
		this.answer = stat.getAnswer();
		this.answerErr = stat.getAnswerErr();
		this.printedTimestamp = stat.getPrintedTimestamp();
		this.scannedTimestamp = stat.getScannedTimestamp();
		this.priority = stat.getPriority();
		this.obsvId = stat.getObsvId();
		this.languageResponse = stat.getLanguageResponse();
		this.ageAtVisit = stat.getAgeAtVisit();
		this.locationId = stat.getLocationId();
	}

	public Integer getStatisticsId()
	{
		return this.statisticsId;
	}

	public void setStatisticsId(Integer statisticsId)
	{
		this.statisticsId = statisticsId;
	}

	public Integer getPatientId()
	{
		return this.patientId;
	}

	public void setPatientId(Integer patientId)
	{
		this.patientId = patientId;
	}

	public String getFormName()
	{
		return this.formName;
	}

	public void setFormName(String formName)
	{
		this.formName = formName;
	}

	public Integer getRuleId()
	{
		return this.ruleId;
	}

	public void setRuleId(Integer ruleId)
	{
		this.ruleId = ruleId;
	}

	public Integer getFormInstanceId()
	{
		return this.formInstanceId;
	}

	public void setFormInstanceId(Integer formInstanceId)
	{
		this.formInstanceId = formInstanceId;
	}

	public Integer getEncounterId()
	{
		return this.encounterId;
	}

	public void setEncounterId(Integer encounterId)
	{
		this.encounterId = encounterId;
	}

	public Integer getPosition()
	{
		return this.position;
	}

	public void setPosition(Integer position)
	{
		this.position = position;
	}

	public String getAnswer()
	{
		return this.answer;
	}

	public void setAnswer(String answer)
	{
		this.answer = answer;
	}

	public String getAnswerErr()
	{
		return this.answerErr;
	}

	public void setAnswerErr(String answerErr)
	{
		this.answerErr = answerErr;
	}

	public Date getPrintedTimestamp()
	{
		return this.printedTimestamp;
	}

	public void setPrintedTimestamp(Date printedTimestamp)
	{
		this.printedTimestamp = printedTimestamp;
	}

	public Date getScannedTimestamp()
	{
		return this.scannedTimestamp;
	}

	public void setScannedTimestamp(Date scannedTimestamp)
	{
		this.scannedTimestamp = scannedTimestamp;
	}

	public Integer getPriority()
	{
		return this.priority;
	}

	public void setPriority(Integer priority)
	{
		this.priority = priority;
	}

	public Integer getObsvId()
	{
		return this.obsvId;
	}

	public void setObsvId(Integer obsvId)
	{
		this.obsvId = obsvId;
	}

	public String getLanguageResponse()
	{
		return this.languageResponse;
	}

	public void setLanguageResponse(String languageResponse)
	{
		this.languageResponse = languageResponse;
	}

	public String getAgeAtVisit()
	{
		return this.ageAtVisit;
	}

	public void setAgeAtVisit(String ageAtVisit)
	{
		this.ageAtVisit = ageAtVisit;
	}

	public Integer getLocationTagId()
	{
		return this.locationTagId;
	}

	public void setLocationTagId(Integer locationTagId)
	{
		this.locationTagId = locationTagId;
	}

	public Integer getLocationId()
	{
		return this.locationId;
	}

	public void setLocationId(Integer locationId)
	{
		this.locationId = locationId;
	}
}