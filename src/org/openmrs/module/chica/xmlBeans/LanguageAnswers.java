/**
 * 
 */
package org.openmrs.module.chica.xmlBeans;

import java.util.ArrayList;

import org.openmrs.module.atd.xmlBeans.Language;

/**
 * Object representation of <language_answers> xml
 * This class is duplicated so that the jibx binding
 * will work
 * @author Tammy Dugan
 */
public class LanguageAnswers extends org.openmrs.module.atd.xmlBeans.LanguageAnswers
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
