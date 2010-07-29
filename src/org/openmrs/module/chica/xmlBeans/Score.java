package org.openmrs.module.chica.xmlBeans;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="score">
 *   &lt;xs:complexType>
 *     &lt;xs:sequence>
 *       &lt;xs:element type="xs:string" name="maxBlankFieldsAllowed"/>
 *       &lt;xs:element ref="concept"/>
 *       &lt;xs:element ref="value"/>
 *       &lt;xs:element ref="estimatedScoreValue"/>
 *     &lt;/xs:sequence>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 */
public class Score
{
    private String maxBlankFieldsAllowed;
    private Concept concept;
    private Value value;
    private EstimatedScoreValue estimatedScoreValue;

    /** 
     * Get the 'maxBlankFieldsAllowed' element value.
     * 
     * @return value
     */
    public String getMaxBlankFieldsAllowed() {
        return maxBlankFieldsAllowed;
    }

    /** 
     * Set the 'maxBlankFieldsAllowed' element value.
     * 
     * @param maxBlankFieldsAllowed
     */
    public void setMaxBlankFieldsAllowed(String maxBlankFieldsAllowed) {
        this.maxBlankFieldsAllowed = maxBlankFieldsAllowed;
    }

    /** 
     * Get the 'concept' element value.
     * 
     * @return value
     */
    public Concept getConcept() {
        return concept;
    }

    /** 
     * Set the 'concept' element value.
     * 
     * @param concept
     */
    public void setConcept(Concept concept) {
        this.concept = concept;
    }

    /** 
     * Get the 'value' element value.
     * 
     * @return value
     */
    public Value getValue() {
        return value;
    }

    /** 
     * Set the 'value' element value.
     * 
     * @param value
     */
    public void setValue(Value value) {
        this.value = value;
    }

    /** 
     * Get the 'estimatedScoreValue' element value.
     * 
     * @return value
     */
    public EstimatedScoreValue getEstimatedScoreValue() {
        return estimatedScoreValue;
    }

    /** 
     * Set the 'estimatedScoreValue' element value.
     * 
     * @param estimatedScoreValue
     */
    public void setEstimatedScoreValue(EstimatedScoreValue estimatedScoreValue) {
        this.estimatedScoreValue = estimatedScoreValue;
    }
}
