package org.openmrs.module.chica.hibernateBeans;

import java.util.Date;

import org.openmrs.api.context.Context;
import org.openmrs.module.chica.service.ChicaService;


public class ChicaError implements java.io.Serializable {
	
	// Fields
	private Integer chicaErrorID = null;
	private Integer errorCategory = null;
	private Integer sessionId = null;
	private String message = null;
	private String details = null;
	private Date dateTime = null;
	private String level = null;
	
	/**
	 * @return the level
	 */
	public String getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(String level) {
		this.level = level;
	}

	//constructor
	public ChicaError(){}
	
	public ChicaError( String errorLevel, String catString, String desc
			, String details, Date date, Integer sid){
		ChicaService cs = Context.getService(ChicaService.class);
		errorCategory = cs.getErrorCategoryIdByName(catString);
		if (errorCategory == null){
			errorCategory = cs.getErrorCategoryIdByName("General Error");
		}
		sessionId = sid;
		message = desc;
		this.details = details;
		dateTime = date;
		level = errorLevel;
	}

	/**
	 * @return the chicaErrorID
	 */
	public Integer getChicaErrorID() {
		return chicaErrorID;
	}

	/**
	 * @param chicaErrorID the chicaErrorID to set
	 */
	public void setChicaErrorID(Integer chicaErrorID) {
		this.chicaErrorID = chicaErrorID;
	}

	/**
	 * @return the errorCategory
	 */
	public Integer getErrorCategory() {
		return errorCategory;
	}

	/**
	 * @param errorCategory the errorCategory to set
	 */
	public void setErrorCategory(Integer errorCategory) {
		this.errorCategory = errorCategory;
	}

	/**
	 * @return the sessionId
	 */
	public Integer getSessionId() {
		return sessionId;
	}

	/**
	 * @param sessionId the sessionId to set
	 */
	public void setSessionId(Integer sessionId) {
		this.sessionId = sessionId;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the details
	 */
	public String getDetails() {
		return details;
	}

	/**
	 * @param details the details to set
	 */
	public void setStackTrace(String details) {
		this.details = details;
	}

	/**
	 * @return the dateTime
	 */
	public Date getDateTime() {
		return dateTime;
	}

	/**
	 * @param now the dateTime to set
	 */
	public void setDateTime(Date now) {
		this.dateTime = now;
	}

	public void setDetails(String details)
	{
		this.details = details;
	}
	

}