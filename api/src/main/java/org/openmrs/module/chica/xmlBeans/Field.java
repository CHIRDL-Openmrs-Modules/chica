/**
 * 
 */
package org.openmrs.module.chica.xmlBeans;


/**
 * Object representation of <Field> xml
 * This class is duplicated so that the jibx binding
 * will work
 * @author Tammy Dugan
 */
public class Field extends org.openmrs.module.atd.xmlBeans.Field
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
	@Override
	public String getId()
	{
		return this.id;
	}

	/**
	 * @param id the id to set
	 */
	@Override
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * @return the value
	 */
	@Override
	public String getValue()
	{
		return this.value;
	}

	/**
	 * @param value the value to set
	 */
	@Override
	public void setValue(String value)
	{
		this.value = value;
	}

	/**
	 * @return the taborder
	 */
	@Override
	public String getTaborder()
	{
		return this.taborder;
	}

	/**
	 * @param taborder the taborder to set
	 */
	@Override
	public void setTaborder(String taborder)
	{
		this.taborder = taborder;
	}

	/**
	 * @return the type
	 */
	@Override
	public String getType()
	{
		return this.type;
	}

	/**
	 * @param type the type to set
	 */
	@Override
	public void setType(String type)
	{
		this.type = type;
	}
   
    /** 
     * Get the 'substituteEstimate' attribute value.
     * 
     * @return value
     */
    @Override
	public String getSubstituteEstimate() {
        return substituteEstimate;
    }

    /** 
     * Set the 'substituteEstimate' attribute value.
     * 
     * @param substituteEstimate
     */
    @Override
	public void setSubstituteEstimate(String substituteEstimate) {
        this.substituteEstimate = substituteEstimate;
    }
}
