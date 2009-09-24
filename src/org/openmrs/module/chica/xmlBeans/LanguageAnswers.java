/**
 * 
 */
package org.openmrs.module.chica.xmlBeans;

import java.util.ArrayList;

/**
 * Object representation of <language_answers> xml
 *
 * @author Tammy Dugan
 */
public class LanguageAnswers
{
	private ArrayList<Language> languages = null;
	
	/**
	 * Empty constructor
	 */
	public LanguageAnswers()
	{
		
	}
	
	/**
	 * Adds a language to the list of languages
	 * @param language Language to add to language list
	 */
	public void addLanguage(Language language)
	{
		if(this.languages == null)
		{
			this.languages = new ArrayList<Language>();
		}
		this.languages.add(language);
	}
	
	/**
	 * @return the languages
	 */
	public ArrayList<Language> getLanguages()
	{
		return this.languages;
	}
}
