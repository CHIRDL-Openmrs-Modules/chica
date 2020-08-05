package org.openmrs.module.chica.hibernateBeans;

import java.util.Date;

/**
 * Holds information to store in the chica_study table
 * 
 * @author Tammy Dugan
 * @version 1.0
 */
public class Study implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields
	private Integer studyId = null;
	private Date startDate = null;
	private Date endDate = null;
	private String title = null;
	private String investigators = null;
	private Integer studyConceptId = null;
	private Boolean status = false;
	private String purpose = null;

	// Constructors

	/** default constructor */
	public Study() {
	}

	/**
	 * @return the studyId
	 */
	public Integer getStudyId()
	{
		return this.studyId;
	}

	/**
	 * @param studyId the studyId to set
	 */
	public void setStudyId(Integer studyId)
	{
		this.studyId = studyId;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate()
	{
		return this.startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate)
	{
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate()
	{
		return this.endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate)
	{
		this.endDate = endDate;
	}

	/**
	 * @return the title
	 */
	public String getTitle()
	{
		return this.title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * @return the investigators
	 */
	public String getInvestigators()
	{
		return this.investigators;
	}

	/**
	 * @param investigators the investigators to set
	 */
	public void setInvestigators(String investigators)
	{
		this.investigators = investigators;
	}

	/**
	 * @return the studyConceptId
	 */
	public Integer getStudyConceptId()
	{
		return this.studyConceptId;
	}

	/**
	 * @param studyConceptId the studyConceptId to set
	 */
	public void setStudyConceptId(Integer studyConceptId)
	{
		this.studyConceptId = studyConceptId;
	}

	/**
	 * @return the status
	 */
	public Boolean getStatus()
	{
		return this.status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Boolean status)
	{
		this.status = status;
	}

	/**
	 * @return the purpose
	 */
	public String getPurpose()
	{
		return this.purpose;
	}

	/**
	 * @param purpose the purpose to set
	 */
	public void setPurpose(String purpose)
	{
		this.purpose = purpose;
	}
	
	/**
     * @see java.lang.Object#toString()
     */
    public String toString() {
    	StringBuffer buffer = new StringBuffer("Subject:\n");
    	buffer.append("\tstudy_id: " + studyId + "\n");
    	buffer.append("\tstart_date: " + startDate + "\n");
    	buffer.append("\tend_date: " + endDate + "\n");
    	buffer.append("\ttitle: " + title + "\n");
    	buffer.append("\tinvestigators: " + investigators + "\n");
    	buffer.append("\tstudy_concept_id: " + studyConceptId + "\n");
    	buffer.append("\tstatus: " + status + "\n");
    	buffer.append("\tpurpose: " + purpose + "\n");
    	
    	return buffer.toString();
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int hash = 1;
        hash = hash * 17 + (studyId == null ? 0 : studyId.hashCode());
        hash = hash * 31 + (startDate == null ? 0 : startDate.hashCode());
        hash = hash * 46 + (endDate == null ? 0 : endDate.hashCode());
        hash = hash * 54 + (title == null ? 0 : title.hashCode());
        hash = hash * 67 + (investigators == null ? 0 : investigators.hashCode());
        hash = hash * 85 + (studyConceptId == null ? 0 : studyConceptId.hashCode());
        hash = hash * 92 + (status == null ? 0 : status.hashCode());
        hash = hash * 105 + (purpose == null ? 0 : purpose.hashCode());
        
        return hash;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Study)) {
            return false;
        }
        Study other = (Study) obj;
        if (endDate == null) {
            if (other.endDate != null) {
                return false;
            }
        } else if (!endDate.equals(other.endDate)) {
            return false;
        }
        if (investigators == null) {
            if (other.investigators != null) {
                return false;
            }
        } else if (!investigators.equals(other.investigators)) {
            return false;
        }
        if (purpose == null) {
            if (other.purpose != null) {
                return false;
            }
        } else if (!purpose.equals(other.purpose)) {
            return false;
        }
        if (startDate == null) {
            if (other.startDate != null) {
                return false;
            }
        } else if (!startDate.equals(other.startDate)) {
            return false;
        }
        if (status == null) {
            if (other.status != null) {
                return false;
            }
        } else if (!status.equals(other.status)) {
            return false;
        }
        if (studyConceptId == null) {
            if (other.studyConceptId != null) {
                return false;
            }
        } else if (!studyConceptId.equals(other.studyConceptId)) {
            return false;
        }
        if (studyId == null) {
            if (other.studyId != null) {
                return false;
            }
        } else if (!studyId.equals(other.studyId)) {
            return false;
        }
        if (title == null) {
            if (other.title != null) {
                return false;
            }
        } else if (!title.equals(other.title)) {
            return false;
        }
        return true;
    }

}