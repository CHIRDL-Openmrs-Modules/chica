
package org.regenstrief.services;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getDump complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getDump">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="user" type="{http://www.regenstrief.org/services}EntityIdentifier"/>
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="role" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="purpose" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="patient" type="{http://www.regenstrief.org/services}EntityIdentifier"/>
 *         &lt;element name="minDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="maxDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="classesToInclude" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="classesToExclude" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="minimal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getDump", propOrder = {
    "user",
    "password",
    "role",
    "purpose",
    "patient",
    "minDate",
    "maxDate",
    "classesToInclude",
    "classesToExclude",
    "minimal"
})
public class GetDump {

    @XmlElement(required = true)
    protected EntityIdentifier user;
    @XmlElement(required = true)
    protected String password;
    protected String role;
    protected String purpose;
    @XmlElement(required = true)
    protected EntityIdentifier patient;
    protected String minDate;
    protected String maxDate;
    protected String classesToInclude;
    protected String classesToExclude;
    protected String minimal;

    /**
     * Gets the value of the user property.
     * 
     * @return
     *     possible object is
     *     {@link EntityIdentifier }
     *     
     */
    public EntityIdentifier getUser() {
        return user;
    }

    /**
     * Sets the value of the user property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityIdentifier }
     *     
     */
    public void setUser(EntityIdentifier value) {
        this.user = value;
    }

    /**
     * Gets the value of the password property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Gets the value of the role property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the value of the role property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRole(String value) {
        this.role = value;
    }

    /**
     * Gets the value of the purpose property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPurpose() {
        return purpose;
    }

    /**
     * Sets the value of the purpose property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPurpose(String value) {
        this.purpose = value;
    }

    /**
     * Gets the value of the patient property.
     * 
     * @return
     *     possible object is
     *     {@link EntityIdentifier }
     *     
     */
    public EntityIdentifier getPatient() {
        return patient;
    }

    /**
     * Sets the value of the patient property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityIdentifier }
     *     
     */
    public void setPatient(EntityIdentifier value) {
        this.patient = value;
    }

    /**
     * Gets the value of the minDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMinDate() {
        return minDate;
    }

    /**
     * Sets the value of the minDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMinDate(String value) {
        this.minDate = value;
    }

    /**
     * Gets the value of the maxDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaxDate() {
        return maxDate;
    }

    /**
     * Sets the value of the maxDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaxDate(String value) {
        this.maxDate = value;
    }

    /**
     * Gets the value of the classesToInclude property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClassesToInclude() {
        return classesToInclude;
    }

    /**
     * Sets the value of the classesToInclude property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClassesToInclude(String value) {
        this.classesToInclude = value;
    }

    /**
     * Gets the value of the classesToExclude property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClassesToExclude() {
        return classesToExclude;
    }

    /**
     * Sets the value of the classesToExclude property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClassesToExclude(String value) {
        this.classesToExclude = value;
    }

    /**
     * Gets the value of the minimal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMinimal() {
        return minimal;
    }

    /**
     * Sets the value of the minimal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMinimal(String value) {
        this.minimal = value;
    }

}
