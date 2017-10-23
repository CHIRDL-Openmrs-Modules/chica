package org.openmrs.module.chica.study.dp3.reading;

public class GenericReading 
{
	// TODO CHICA-1029 Are all of these fields actually generic enough??? Need to review the spec for all data types
	// We may be able to get rid of some of these and create one generic constructor, but we'll have to wait until
	// further details are provided
	private String timestamp;
	private Integer timeOffset; // Is this needed?
	private String utcOffset; // Is this needed?
	private String displayTime; // Is this needed?
	private String syncTimestamp;
	private String guid;
	private String updatedAt; // What is this?
	private Integer value;
	private String units;
	private String mealTagSource; // Is this needed?
	private String mealTag; // Is this needed?
	private String trendArrow; // Do we actually care about this one?
	
	/**
	 * Default constructor
	 */
	public GenericReading()
	{
		
	}
	
	/**
	 * // TODO CHICA-1029 - try to come up with one generic constructor after we determine what fields are actually going to be used
	 * Constructor - currently used by BGMeterReadings
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
	 * // TODO CHICA-1029 - try to come up with one generic constructor after we determine what fields are actually going to be used
	 * Constructor - currently used by CGMReadings
	 * @param timestamp
	 * @param displayTime
	 * @param syncTimestamp
	 * @param guid
	 * @param updatedAt
	 * @param value
	 * @param units
	 * @param trendArrow
	 */
	public GenericReading(String timestamp, String displayTime, String syncTimestamp, String guid, String updatedAt, Integer value, String units, String trendArrow)
	{
		this.timestamp = timestamp;
		this.displayTime = displayTime;
		this.syncTimestamp = syncTimestamp;
		this.guid = guid;
		this.updatedAt = updatedAt;
		this.value = value;
		this.units = units;
		this.trendArrow = trendArrow;
	}
	
	/**
	 * // TODO CHICA-1029 - try to come up with one generic constructor after we determine what fields are actually going to be used
	 * Constructor - currently used by PumpReadings
	 * @param timestamp
	 * @param utcOffset
	 * @param syncTimestamp
	 * @param guid
	 * @param updatedAt
	 * @param value
	 * @param units
	 * @param mealTagSource
	 * @param mealTag
	 */
	public GenericReading(String timestamp, String utcOffset, String syncTimestamp, String guid, String updatedAt, Integer value, String units, String mealTagSource, String mealTag)
	{
		this.timestamp = timestamp;
		this.utcOffset = utcOffset;
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
	 * @return utcOffset
	 */
	public String getUTCOffset() {
		return utcOffset;
	}
	
	/**
	 * @return displayTime
	 */
	public String getDisplayTime() {
		return displayTime;
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
	 * @return trendArrow
	 */
	public String getTrendArrow() {
		return trendArrow;
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
	 * @param utcOffset the utcOffset to set
	 */
	public void setUTCOffset(String utcOffset) {
		this.utcOffset = utcOffset;
	}
	
	/**
	 * @param displayTime the displayTime to set
	 */
	public void setDisplayTime(String displayTime) {
		this.displayTime = displayTime;
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
	
	/**
	 * @param trendArrow the trendArrow to set
	 */
	public void setTrendArrow(String trendArrow) {
		this.trendArrow = trendArrow;
	}
	
	// TODO CHICA-1029 Generate hashCode() and equals() after we determine what fields we are actually going to keep in this class
}
