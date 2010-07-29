package org.openmrs.module.chica.xmlBeans;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="estimatedScoreValue">
 *   &lt;xs:complexType>
 *     &lt;xs:sequence>
 *       &lt;xs:element ref="value"/>
 *     &lt;/xs:sequence>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 */
public class EstimatedScoreValue
{
    private Value value;

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
