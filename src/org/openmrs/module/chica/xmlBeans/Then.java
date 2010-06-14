package org.openmrs.module.chica.xmlBeans;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="then">
 *   &lt;xs:complexType>
 *     &lt;xs:sequence>
 *       &lt;xs:element ref="cn"/>
 *     &lt;/xs:sequence>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 */
public class Then
{
    private String cn;

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
