package org.openmrs.module.chica.xmlBeans;

import java.util.ArrayList;
import java.util.List;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="mean">
 *   &lt;xs:complexType>
 *     &lt;xs:sequence>
 *       &lt;xs:element ref="Field" maxOccurs="unbounded"/>
 *     &lt;/xs:sequence>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 */
public class Mean
{
    private List<Field> fields = new ArrayList<Field>();

	
    /**
     * @return the fields
     */
    public List<Field> getFields() {
    	return this.fields;
    }

	
    /**
     * @param fields the fields to set
     */
    public void setFields(List<Field> fields) {
    	this.fields = fields;
    }

}
