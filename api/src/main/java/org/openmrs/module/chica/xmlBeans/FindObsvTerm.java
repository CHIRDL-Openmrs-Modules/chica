/**
 * 
 */
package org.openmrs.module.chica.xmlBeans;


/**
 * Object representation of <FindObsvTerm> xml
 * 
 * @author Tammy Dugan
 */
public class FindObsvTerm
{
	private String name = null;
	private String description = null;
	private String type = null;
	private String units = null;
	private String answers = null;
	private String export = null;
	
	public String getExport()
	{
		return this.export;
	}

	public void setExport(String export)
	{
		this.export = export;
	}

	public String getAnswers()
	{
		return this.answers;
	}

	public void setAnswers(String answers)
	{
		this.answers = answers;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return this.description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getType()
	{
		return this.type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getUnits()
	{
		return this.units;
	}

	public void setUnits(String units)
	{
		this.units = units;
	}
	
}
