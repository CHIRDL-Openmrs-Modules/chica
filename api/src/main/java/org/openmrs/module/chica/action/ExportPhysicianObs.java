package org.openmrs.module.chica.action;

import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;

/**
 * CHICA-1070 Used to create HL7 ORU message and store in the sockethl7listener_hl7_out_queue table to be picked up by the HL7OutboundHandler task
 */
public class ExportPhysicianObs extends ExportObs 
{
    /**
     * Gets the Concept Source for Outbound Physician Obs
     * @return Concept Source Outbound Physician Obs
     */
    public String getConceptSource() {
        return ChirdlUtilConstants.CONCEPT_SOURCE_OUTBOUND_PHYSICIAN_OBS;
    }
    
    /**
     * Gets the Host for Export Physician Obs
     * @return exportPhysicianObsHost
     */
    public String getHost() {
        return ChirdlUtilConstants.GLOBAL_PROP_EXPORT_PHYSICIAN_OBS_HOST ;
    }
    
    /**
     * Gets the Port for Export Physician Obs
     * @return exportPhysicianObsPort
     */
    public String getPort() {
        return ChirdlUtilConstants.GLOBAL_PROP_EXPORT_PHYSICIAN_OBS_PORT;
    }
    
}
