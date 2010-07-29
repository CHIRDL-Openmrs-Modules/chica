/**
 * 
 */
package org.openmrs.module.chica.xmlBeans;


/**
 * Object representation of <Field> xml
 * 
 * @author Tammy Dugan
 */
public class Field
{
	private String id = null;
	private String value = null;
	private String taborder = null;
	private String type = null;
    private String substituteEstimate;
	
	/**
	 * Constructor assigning id for the Field
	 * @param id id for the Field
	 */
	public Field(String id)
	{
		this.id = id;
	}

	/**
	 * Empty constructor
	 */
	public Field()
	{
		
	}
	
	/**
	 * @return the id
	 */
	public String getId()
	{
		return this.id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * @return the value
	 */
	public String getValue()
	{
		return this.value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	/**
	 * @return the taborder
	 */
	public String getTaborder()
	{
		return this.taborder;
	}

	/**
	 * @param taborder the taborder to set
	 */
	public void setTaborder(String taborder)
	{
		this.taborder = taborder;
	}

	/**
	 * @return the type
	 */
	public String getType()
	{
		return this.type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type)
	{
		this.type = type;
	}
   
    /** 
     * Get the 'substituteEstimate' attribute value.
     * 
     * @return value
     */
    public String getSubstituteEstimate() {
        return substituteEstimate;
    }

    /** 
     * Set the 'substituteEstimate' attribute value.
     * 
     * @param substituteEstimate
     */
    public void setSubstituteEstimate(String substituteEstimate) {
        this.substituteEstimate = substituteEstimate;
    }
}
