/**
 * 
 */
package org.openmrs.module.chica.xmlBeans;

import java.util.ArrayList;

/**
 * Object representation of <pws_prompt_answers> xml
 *
 * @author Tammy Dugan
 */
public class ObsvDictionary
{
	private ArrayList<FindObsvTerm> terms = null;
	
	/**
	 * Empty constructor
	 */
	public ObsvDictionary()
	{
		
	}
	
	/**
	 * Adds a term to the list of terms
	 * @param term FindObsvTerm to add to term list
	 */
	public void addTerm(FindObsvTerm term)
	{
		if(this.terms == null)
		{
			this.terms = new ArrayList<FindObsvTerm>();
		}
		this.terms.add(term);
	}
	
	/**
	 * @return the terms
	 */
	public ArrayList<FindObsvTerm> getTerms()
	{
		return this.terms;
	}
}
