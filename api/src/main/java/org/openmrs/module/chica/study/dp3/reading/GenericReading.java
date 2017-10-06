package org.openmrs.module.chica.study.dp3.reading;

public class GenericReading 
{
	// TODO CHICA-1029 Are all of these fields actually generic enough??? Need to review the spec for all data types
	private String timestamp;
	private Integer timeOffset;
	private String syncTimestamp;
	private String guid;
	private String updatedAt;
	private Integer value;
	private String units;
	private String mealTagSource;
	private String mealTag;
	
	/**
	 * Default constructor
	 */
	public GenericReading()
	{
		
	}
	
	/**
	 * Constructor
	 * @param timestamp
	 * @param timeOffset
	 * @param syncTimestamp
	 * @param guid
	 * @param updatedAt
	 * @param value
	 * @param units
	 * @param mealTagSource
	 * @param mealTag
	 */
	public GenericReading(String timestamp, Integer timeOffset, String syncTimestamp, String guid, String updatedAt, Integer value, String units, String mealTagSource, String mealTag)
	{
		this.timestamp = timestamp;
		this.timeOffset = timeOffset;
		this.syncTimestamp = syncTimestamp;
		this.guid = guid;
		this.updatedAt = updatedAt;
		this.value = value;
		this.units = units;
		this.mealTagSource = mealTagSource;
		this.mealTag = mealTag;
	}

	/**
	 * @return the timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * @return the timeOffset
	 */
	public Integer getTimeOffset() {
		return timeOffset;
	}

	/**
	 * @return the syncTimestamp
	 */
	public String getSyncTimestamp() {
		return syncTimestamp;
	}

	/**
	 * @return the guid
	 */
	public String getGuid() {
		return guid;
	}

	/**
	 * @return the updatedAt
	 */
	public String getUpdatedAt() {
		return updatedAt;
	}

	/**
	 * @return the value
	 */
	public Integer getValue() {
		return value;
	}

	/**
	 * @return the units
	 */
	public String getUnits() {
		return units;
	}

	/**
	 * @return the mealTagSource
	 */
	public String getMealTagSource() {
		return mealTagSource;
	}

	/**
	 * @return the mealTag
	 */
	public String getMealTag() {
		return mealTag;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @param timeOffset the timeOffset to set
	 */
	public void setTimeOffset(Integer timeOffset) {
		this.timeOffset = timeOffset;
	}

	/**
	 * @param syncTimestamp the syncTimestamp to set
	 */
	public void setSyncTimestamp(String syncTimestamp) {
		this.syncTimestamp = syncTimestamp;
	}

	/**
	 * @param guid the guid to set
	 */
	public void setGuid(String guid) {
		this.guid = guid;
	}

	/**
	 * @param updatedAt the updatedAt to set
	 */
	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(Integer value) {
		this.value = value;
	}

	/**
	 * @param units the units to set
	 */
	public void setUnits(String units) {
		this.units = units;
	}

	/**
	 * @param mealTagSource the mealTagSource to set
	 */
	public void setMealTagSource(String mealTagSource) {
		this.mealTagSource = mealTagSource;
	}

	/**
	 * @param mealTag the mealTag to set
	 */
	public void setMealTag(String mealTag) {
		this.mealTag = mealTag;
	}
}
