package org.openmrs.module.chica.xmlBeans;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="form_config">
 *   &lt;xs:complexType>
 *     &lt;xs:sequence>
 *       &lt;xs:element ref="scores"/>
 *       &lt;xs:element ref="language_answers" minOccurs="0"/>
 *     &lt;/xs:sequence>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 */
public class FormConfig
{
    private Scores scores;
    private LanguageAnswers languageAnswers;

    /** 
     * Get the 'scores' element value.
     * 
     * @return value
     */
    public Scores getScores() {
        return scores;
    }

    /** 
     * Set the 'scores' element value.
     * 
     * @param scores
     */
    public void setScores(Scores scores) {
        this.scores = scores;
    }

    /** 
     * Get the 'language_answers' element value.
     * 
     * @return value
     */
    public LanguageAnswers getLanguageAnswers() {
        return languageAnswers;
    }

    /** 
     * Set the 'language_answers' element value.
     * 
     * @param languageAnswers
     */
    public void setLanguageAnswers(LanguageAnswers languageAnswers) {
        this.languageAnswers = languageAnswers;
    }
}
