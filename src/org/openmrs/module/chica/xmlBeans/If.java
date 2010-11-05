package org.openmrs.module.chica.xmlBeans;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="if">
 *   &lt;xs:complexType>
 *     &lt;xs:sequence>
 *       &lt;xs:choice>
 *         &lt;xs:element ref="geq"/>
 *         &lt;xs:element ref="eq"/>
 *       &lt;/xs:choice>
 *     &lt;/xs:sequence>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 */
public class If
{
    private Geq geq;
    private Eq eq;   

    /** 
     * Get the 'geq' element value.
     * 
     * @return value
     */
    public Geq getGeq() {
        return geq;
    }

    /** 
     * Set the 'geq' element value.
     * 
     * @param geq
     */
    public void setGeq(Geq geq) {
        this.geq = geq;
    }
    
    /** 
     * Get the 'eq' element value.
     * 
     * @return value
     */
    public Eq getEq() {
        return eq;
    }
    
    /** 
     * Set the 'eq' element value.
     * 
     * @param eq
     */
    public void setEq(Eq eq) {
        this.eq = eq;
    }
}
