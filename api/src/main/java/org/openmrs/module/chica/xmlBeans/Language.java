/**
 * 
 */
package org.openmrs.module.chica.xmlBeans;

import java.util.ArrayList;

import org.openmrs.module.atd.xmlBeans.Field;

/**
 * Object representation of <language> xml
 * This class is duplicated so that the jibx binding
 * will work
 * @author Tammy Dugan
 */
public class Language extends org.openmrs.module.atd.xmlBeans.Language
{	
	private ArrayList<Field> fields = null;
	private String name = null;
	
	/**
	 * Empty constructor
	 */
	public Language()
	{
		// This constructor is intentionally left empty.
	}
	
	/**
	 * Adds a field to the list of fields
	 * @param field Field to add to field list
	 */
	@Override
	public void addField(Field field) 
	{
		if(this.fields == null)
		{
			this.fields = new ArrayList<>();
		}
		this.fields.add(field);
	}
	
	/**
	 * @return the fields
	 */
	@Override
	public ArrayList<Field> getFields()
	{
		return this.fields;
	}

	/**
	 * @return the name
	 */
	@Override
	public String getName()
	{
		return this.name;
	}
}
