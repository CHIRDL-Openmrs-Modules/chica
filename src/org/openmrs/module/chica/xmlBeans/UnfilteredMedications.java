package org.openmrs.module.chica.xmlBeans;

import java.util.ArrayList;
import java.util.List;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="unfilteredMedications">
 *   &lt;xs:complexType>
 *     &lt;xs:sequence>
 *       &lt;xs:element ref="medication" maxOccurs="unbounded"/>
 *     &lt;/xs:sequence>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 */
public class UnfilteredMedications
{
    private List<Medication> unfilteredMedicationList = new ArrayList<Medication>();

    /** 
     * Get the list of 'medication' element items.
     * 
     * @return list
     */
    public List<Medication> getUnfilteredMedications() {
        return unfilteredMedicationList;
    }

    /** 
     * Set the list of 'medication' element items.
     * 
     * @param list
     */
    public void setUnfilteredMedications(List<Medication> list) {
        unfilteredMedicationList = list;
    }
}
