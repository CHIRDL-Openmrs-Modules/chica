package org.openmrs.module.chica.xmlBeans;

import java.util.ArrayList;
import java.util.List;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:xs="http://www.w3.org/2001/XMLSchema" name="scores">
 *   &lt;xs:complexType>
 *     &lt;xs:sequence>
 *       &lt;xs:element ref="score" maxOccurs="unbounded"/>
 *     &lt;/xs:sequence>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 */
public class Scores
{
    private List<Score> scoreList = new ArrayList<Score>();

    /** 
     * Get the list of 'score' element items.
     * 
     * @return list
     */
    public List<Score> getScores() {
        return scoreList;
    }

    /** 
     * Set the list of 'score' element items.
     * 
     * @param list
     */
    public void setScores(List<Score> list) {
        scoreList = list;
    }
}
