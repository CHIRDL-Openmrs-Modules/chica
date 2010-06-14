package org.openmrs.module.chica.xmlBeans;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="score">
 *   &lt;xs:complexType>
 *     &lt;xs:sequence>
 *       &lt;xs:element ref="concept"/>
 *       &lt;xs:element ref="value"/>
 *     &lt;/xs:sequence>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 */
public class Score
{
    private Concept concept;
    private Value value;

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
}
