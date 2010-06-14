package org.openmrs.module.chica.xmlBeans;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="choose">
 *   &lt;xs:complexType>
 *     &lt;xs:sequence>
 *       &lt;xs:element ref="if"/>
 *       &lt;xs:element ref="then"/>
 *     &lt;/xs:sequence>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 */
public class Choose
{
    private If _if;
    private Then then;

    /** 
     * Get the 'if' element value.
     * 
     * @return value
     */
    public If getIf() {
        return _if;
    }

    /** 
     * Set the 'if' element value.
     * 
     * @param _if
     */
    public void setIf(If _if) {
        this._if = _if;
    }

    /** 
     * Get the 'then' element value.
     * 
     * @return value
     */
    public Then getThen() {
        return then;
    }

    /** 
     * Set the 'then' element value.
     * 
     * @param then
     */
    public void setThen(Then then) {
        this.then = then;
    }
}
