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
    private String cn;

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
     * Get the 'cn' element value.
     * 
     * @return value
     */
    public String getCn() {
        return cn;
    }

    /** 
     * Set the 'cn' element value.
     * 
     * @param cn
     */
    public void setCn(String cn) {
        this.cn = cn;
    }
}
