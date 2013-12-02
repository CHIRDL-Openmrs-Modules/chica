/**
 * 
 */
package org.openmrs.module.chica.xmlBeans;

import java.util.ArrayList;

import org.openmrs.module.chica.xmlBeans.Field;

/**
 * Object representation of <pws_prompt_answers> xml
 *
 * @author Tammy Dugan
 */
public class PWSPromptAnswers
{
	private ArrayList<Field> fields = null;
	
	/**
	 * Empty constructor
	 */
	public PWSPromptAnswers()
	{
		
	}
	
	/**
	 * Adds a field to the list of fields
	 * @param field Field to add to field list
	 */
	public void addField(Field field)
	{
		if(this.fields == null)
		{
			this.fields = new ArrayList<Field>();
		}
		this.fields.add(field);
	}
	
	/**
	 * @return the fields
	 */
	public ArrayList<Field> getFields()
	{
		return this.fields;
	}
}
