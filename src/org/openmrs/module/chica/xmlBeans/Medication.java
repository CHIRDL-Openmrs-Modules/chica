package org.openmrs.module.chica.xmlBeans;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="medication">
 *   &lt;xs:complexType>
 *     &lt;xs:attribute type="xs:string" use="required" name="name"/>
 *     &lt;xs:attribute type="xs:string" use="required" name="system"/>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 */
public class Medication
{
    private String name;
    private String system;

    /** 
     * Get the 'name' attribute value.
     * 
     * @return value
     */
    public String getName() {
        return name;
    }

    /** 
     * Set the 'name' attribute value.
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /** 
     * Get the 'system' attribute value.
     * 
     * @return value
     */
    public String getSystem() {
        return system;
    }

    /** 
     * Set the 'system' attribute value.
     * 
     * @param system
     */
    public void setSystem(String system) {
        this.system = system;
    }
}
