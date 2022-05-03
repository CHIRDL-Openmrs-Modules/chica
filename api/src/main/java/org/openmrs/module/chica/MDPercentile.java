package org.openmrs.module.chica;

/**
 * @author Seema Sarala
 *
 */
public interface MDPercentile
{

    /**
     * @return mean age
     */
    Double getMeanAge();
    
    /**
     * @return sample
     */
    Double getSample();

    /**
     * @return mean
     */
    Double getMean();
    
    /**
     * @return sd
     */
    Double getSd();

    /**
     * @return p10
     */
    Double getP10();
    
    /**
     * @return p25
     */
    Double getP25();
    
    /**
     * @return p50
     */
    Double getP50();
    
    /**
     * @return p75
     */
    Double getP75();
    
    /**
     * @return p90
     */
    Double getP90();

}