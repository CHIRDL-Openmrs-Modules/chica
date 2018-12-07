package org.openmrs.module.chica.action;

import org.openmrs.api.context.Context;
import org.openmrs.module.chirdlutil.util.ChirdlUtilConstants;

public class ExportRegenstriefObs extends ExportObs {
       
    /**
     * Gets the Concept Source for Outbound Regenstrief Obs
     * @return Concept Source Outbound Regenstrief Obs
     */
    public String getConceptSource() {
        return ChirdlUtilConstants.CONCEPT_SOURCE_OUTBOUND_REGENSTRIEF_OBS;
    }
    
    /**
     * Gets the Host for Export Regenstrief Obs
     * @return exportRegenstriefObsHost
     */
    public String getHost() {
        return Context.getAdministrationService().getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_EXPORT_REGENSTRIEF_OBS_HOST) ;
    }
    
    /**
     * Gets the Port for Export Regenstrief Obs
     * @return exportRegenstriefObsPort
     */
    public String getPort() {
        return Context.getAdministrationService().getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_EXPORT_REGENSTRIEF_OBS_PORT);
    }
 }
