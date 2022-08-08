/**
 * 
 */
package org.openmrs.module.chica.xmlBeans;

import java.util.ArrayList;

/**
 * Object representation of <pws_prompt_answer_errs> xml
 *
 * @author Tammy Dugan
 */
public class PWSPromptAnswerErrs
{
	private ArrayList<Field> fields = null;
	
	/**
	 * Empty constructor
	 */
	public PWSPromptAnswerErrs()
	{
		// This constructor is intentionally left empty.
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
