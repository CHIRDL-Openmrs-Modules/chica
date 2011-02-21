package org.openmrs.module.chica.xmlBeans;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="eq">
 *   &lt;xs:complexType>
 *     &lt;xs:sequence>
 *       &lt;xs:element ref="Field"/>
 *       &lt;xs:element ref="cn"/>
 *     &lt;/xs:sequence>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 */
public class Eq
{
    private Field field;
    private String result;

    /** 
     * Get the 'Field' element value.
     * 
     * @return value
     */
    public Field getField() {
        return field;
    }

    /** 
     * Set the 'Field' element value.
     * 
     * @param field
     */
    public void setField(Field field) {
        this.field = field;
    }

    /** 
     * Get the 'result' element value.
     * 
     * @return value
     */
    public String getResult() {
        return result;
    }

    /** 
     * Set the 'result' element value.
     * 
     * @param result
     */
    public void setResult(String result) {
        this.result = result;
    }
}
