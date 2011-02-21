package org.openmrs.module.chica.xmlBeans;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="then">
 *   &lt;xs:complexType>
 *     &lt;xs:choice>
 *       &lt;xs:element ref="cn"/>
 *       &lt;xs:element ref="ccode"/>
 *     &lt;/xs:choice>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 */
public class Then
{
    private String result;
    

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
