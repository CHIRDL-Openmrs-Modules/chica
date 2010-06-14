package org.openmrs.module.chica.xmlBeans;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="value">
 *   &lt;xs:complexType>
 *     &lt;xs:choice>
 *       &lt;xs:element ref="plus"/>
 *       &lt;xs:element ref="mean"/>
 *     &lt;/xs:choice>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 */
public class Value
{
    private int valueSelect = -1;
    private static final int PLUS_CHOICE = 0;
    private static final int MEAN_CHOICE = 1;
    private Plus plus;
    private Mean mean;

    private void setValueSelect(int choice) {
        if (valueSelect == -1) {
            valueSelect = choice;
        } else if (valueSelect != choice) {
            throw new IllegalStateException(
                    "Need to call clearValueSelect() before changing existing choice");
        }
    }

    /** 
     * Clear the choice selection.
     */
    public void clearValueSelect() {
        valueSelect = -1;
    }

    /** 
     * Check if Plus is current selection for choice.
     * 
     * @return <code>true</code> if selection, <code>false</code> if not
     */
    public boolean ifPlus() {
        return valueSelect == PLUS_CHOICE;
    }

    /** 
     * Get the 'plus' element value.
     * 
     * @return value
     */
    public Plus getPlus() {
        return plus;
    }

    /** 
     * Set the 'plus' element value.
     * 
     * @param plus
     */
    public void setPlus(Plus plus) {
        setValueSelect(PLUS_CHOICE);
        this.plus = plus;
    }

    /** 
     * Check if Mean is current selection for choice.
     * 
     * @return <code>true</code> if selection, <code>false</code> if not
     */
    public boolean ifMean() {
        return valueSelect == MEAN_CHOICE;
    }

    /** 
     * Get the 'mean' element value.
     * 
     * @return value
     */
    public Mean getMean() {
        return mean;
    }

    /** 
     * Set the 'mean' element value.
     * 
     * @param mean
     */
    public void setMean(Mean mean) {
        setValueSelect(MEAN_CHOICE);
        this.mean = mean;
    }
}
