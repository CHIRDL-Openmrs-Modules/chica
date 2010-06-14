package org.openmrs.module.chica.xmlBeans;

import java.util.ArrayList;
import java.util.List;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="plus">
 *   &lt;xs:complexType>
 *     &lt;xs:choice>
 *       &lt;xs:element ref="Field" maxOccurs="unbounded"/>
 *       &lt;xs:element ref="choose" maxOccurs="unbounded"/>
 *     &lt;/xs:choice>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 */
public class Plus
{
    private int plusSelect = -1;
    private static final int FIELD_CHOICE = 0;
    private static final int CHOOSE_CHOICE = 1;
    private List<Field> fieldList = new ArrayList<Field>();
    private List<Choose> chooseList = new ArrayList<Choose>();

    private void setPlusSelect(int choice) {
        if (plusSelect == -1) {
            plusSelect = choice;
        } else if (plusSelect != choice) {
            throw new IllegalStateException(
                    "Need to call clearPlusSelect() before changing existing choice");
        }
    }

    /** 
     * Clear the choice selection.
     */
    public void clearPlusSelect() {
        plusSelect = -1;
    }

    /** 
     * Check if Fields is current selection for choice.
     * 
     * @return <code>true</code> if selection, <code>false</code> if not
     */
    public boolean ifField() {
        return plusSelect == FIELD_CHOICE;
    }

    /** 
     * Get the list of 'Field' element items.
     * 
     * @return list
     */
    public List<Field> getFields() {
        return fieldList;
    }

    /** 
     * Set the list of 'Field' element items.
     * 
     * @param list
     */
    public void setFields(List<Field> list) {
        setPlusSelect(FIELD_CHOICE);
        fieldList = list;
    }

    /** 
     * Check if Chooses is current selection for choice.
     * 
     * @return <code>true</code> if selection, <code>false</code> if not
     */
    public boolean ifChoose() {
        return plusSelect == CHOOSE_CHOICE;
    }

    /** 
     * Get the list of 'choose' element items.
     * 
     * @return list
     */
    public List<Choose> getChooses() {
        return chooseList;
    }

    /** 
     * Set the list of 'choose' element items.
     * 
     * @param list
     */
    public void setChooses(List<Choose> list) {
        setPlusSelect(CHOOSE_CHOICE);
        chooseList = list;
    }
}
