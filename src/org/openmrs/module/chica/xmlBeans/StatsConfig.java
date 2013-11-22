/**
 * 
 */
package org.openmrs.module.chica.xmlBeans;



/**
 * Object representation of <stats_config> xml
 * @author Tammy Dugan
 */
public class StatsConfig
{
	private PWSPromptAnswers pwsPromptAnswers = null;
	private PWSPromptAnswerErrs pwsPromptAnswerErrs = null;
	private LanguageAnswers languageAnswers = null;
	
	/**
	 * 
	 * @param pwsPromptAnswers
	 * @param pwsPromptAnswerErrs
	 * @param languageAnswers
	 */
	public StatsConfig(PWSPromptAnswers pwsPromptAnswers,
			PWSPromptAnswerErrs pwsPromptAnswerErrs,
			LanguageAnswers languageAnswers)
	{
		this.pwsPromptAnswerErrs = pwsPromptAnswerErrs;
		this.pwsPromptAnswers = pwsPromptAnswers;
		this.languageAnswers = languageAnswers;
	}
	
	/**
	 * Empty constructor
	 */
	public StatsConfig()
	{
		
	}

	/**
	 * @return the pwsPromptAnswers
	 */
	public PWSPromptAnswers getPwsPromptAnswers()
	{
		return this.pwsPromptAnswers;
	}

	/**
	 * @return the pwsPromptAnswerErrs
	 */
	public PWSPromptAnswerErrs getPwsPromptAnswerErrs()
	{
		return this.pwsPromptAnswerErrs;
	}

	/**
	 * @return the languageAnswers
	 */
	public LanguageAnswers getLanguageAnswers()
	{
		return this.languageAnswers;
	}
}
