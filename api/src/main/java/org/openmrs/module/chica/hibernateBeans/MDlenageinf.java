package org.openmrs.module.chica.hibernateBeans;

import org.openmrs.module.chica.MDPercentile;

/**
 * Holds information to store in the chica_md_lenaginf table
 * 
 * @author Seema Sarala
 * @version 1.0
 */
public class MDlenageinf implements java.io.Serializable, MDPercentile {

    private static final long serialVersionUID = 1L;
    // Fields
    private Integer mdlenageinfId = null;
    private Double ageYears = null;
    private Double meanAge = null;
    private Double sample = null;
    private Double mean = null;
    private Double sd = null;
    private Double p10 = null;
    private Double p25 = null;
    private Double p50 = null;
    private Double p75 = null;
    private Double p90 = null;

    /**
     * @return the mdlenageinfId
     */
    public Integer getMdlenageinfId()
    {
        return this.mdlenageinfId;
    }

    /**
     * @param mdlenageinfId the mdlenageinfId to set
     */
    public void setMdlenageinfId(Integer mdlenageinfId)
    {
        this.mdlenageinfId = mdlenageinfId;
    }

    /**
     * @return the ageYears
     */
    public Double getAgeYears()
    {
        return this.ageYears;
    }

    /**
     * @param ageYears the ageYears to set
     */
    public void setAgeYears(Double ageYears)
    {
        this.ageYears = ageYears;
    }

    /**
     * @return the meanAge
     */
    @Override
    public Double getMeanAge()
    {
        return this.meanAge;
    }

    /**
     * @param meanAge the meanAge to set
     */
    public void setMeanAge(Double meanAge)
    {
        this.meanAge = meanAge;
    }

    /**
     * @return the sample
     */
    @Override
    public Double getSample()
    {
        return this.sample;
    }

    /**
     * @param sample the sample to set
     */
    public void setSample(Double sample)
    {
        this.sample = sample;
    }

    /**
     * @return the mean
     */
    @Override
    public Double getMean()
    {
        return this.mean;
    }

    /**
     * @param mean the mean to set
     */
    public void setMean(Double mean)
    {
        this.mean = mean;
    }

    /**
     * @return the sd
     */
    @Override
    public Double getSd()
    {
        return this.sd;
    }

    /**
     * @param sd the sd to set
     */
    public void setSd(Double sd)
    {
        this.sd = sd;
    }
    
    /**
     * @return the p10
     */
    @Override
    public Double getP10()
    {
        return this.p10;
    }

    /**
     * @param p10 the p10 to set
     */
    public void setP10(Double p10)
    {
        this.p10 = p10;
    }
    
    /**
     * @return the p25
     */
    @Override
    public Double getP25()
    {
        return this.p25;
    }

    /**
     * @param p25 the p25 to set
     */
    public void setP25(Double p25)
    {
        this.p25 = p25;
    }
    
    /**
     * @return the p50
     */
    @Override
    public Double getP50()
    {
        return this.p50;
    }

    /**
     * @param p50 the p50 to set
     */
    public void setP50(Double p50)
    {
        this.p50 = p50;
    }
    
    /**
     * @return the p75
     */
    @Override
    public Double getP75()
    {
        return this.p75;
    }

    /**
     * @param p75 the p75 to set
     */
    public void setP75(Double p75)
    {
        this.p75 = p75;
    }
    
    /**
     * @return the p90
     */
    @Override
    public Double getP90()
    {
        return this.p90;
    }

    /**
     * @param p90 the p90 to set
     */
    public void setP90(Double p90)
    {
        this.p90 = p90;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.ageYears == null ? 0 : this.ageYears.hashCode());
        result = prime * result + (this.mean == null ? 0 : this.mean.hashCode());
        result = prime * result + (this.meanAge == null ? 0 : this.meanAge.hashCode());
        result = prime * result + (this.p10 == null ? 0 : this.p10.hashCode());
        result = prime * result + (this.p25 == null ? 0 : this.p25.hashCode());
        result = prime * result + (this.p50 == null ? 0 : this.p50.hashCode());
        result = prime * result + (this.p75 == null ? 0 : this.p75.hashCode());
        result = prime * result + (this.p90 == null ? 0 : this.p90.hashCode());
        result = prime * result + (this.sample == null ? 0 : this.sample.hashCode());
        result = prime * result + (this.sd == null ? 0 : this.sd.hashCode());
        result = prime * result + (this.mdlenageinfId == null ? 0 : this.mdlenageinfId.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof MDlenageinf))
            return false;
        MDlenageinf other = (MDlenageinf) obj;
        if (this.ageYears == null) {
            if (other.ageYears != null)
                return false;
        } else if (!this.ageYears.equals(other.ageYears))
            return false;
        if (this.mean == null) {
            if (other.mean != null)
                return false;
        } else if (!this.mean.equals(other.mean))
            return false;
        if (this.meanAge == null) {
            if (other.meanAge != null)
                return false;
        } else if (!this.meanAge.equals(other.meanAge))
            return false;
        if (this.p10 == null) {
            if (other.p10 != null)
                return false;
        } else if (!this.p10.equals(other.p10))
            return false;
        if (this.p25 == null) {
            if (other.p25 != null)
                return false;
        } else if (!this.p25.equals(other.p25))
            return false;
        if (this.p50 == null) {
            if (other.p50 != null)
                return false;
        } else if (!this.p50.equals(other.p50))
            return false;
        if (this.p75 == null) {
            if (other.p75 != null)
                return false;
        } else if (!this.p75.equals(other.p75))
            return false;
        if (this.p90 == null) {
            if (other.p90 != null)
                return false;
        } else if (!this.p90.equals(other.p90))
            return false;
        if (this.sample == null) {
            if (other.sample != null)
                return false;
        } else if (!this.sample.equals(other.sample))
            return false;
        if (this.sd == null) {
            if (other.sd != null)
                return false;
        } else if (!this.sd.equals(other.sd))
            return false;
        if (this.mdlenageinfId == null) {
            if (other.mdlenageinfId != null)
                return false;
        } else if (!this.mdlenageinfId.equals(other.mdlenageinfId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "MDlenageinf [mdlenageinfId=" + this.mdlenageinfId + ", ageYears=" + this.ageYears + ", meanAge=" + this.meanAge
                + ", sample=" + this.sample + ", mean=" + this.mean + ", sd=" + this.sd + ", p10=" + this.p10 + ", p25=" + this.p25 + ", p50=" + this.p50
                + ", p75=" + this.p75 + ", p90=" + this.p90 + "]";
    }
    
}