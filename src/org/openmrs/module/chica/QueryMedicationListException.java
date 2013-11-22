
package org.openmrs.module.chica;

import org.openmrs.module.chirdlutilbackports.hibernateBeans.Error;


public class QueryMedicationListException extends Exception {

    private static final long serialVersionUID = -2985522122680870005L;
    private Error Error = null;

    public QueryMedicationListException() {
        super();
    }

    public QueryMedicationListException(String message) {
        super(message);
    }
    
    public QueryMedicationListException(String message, Error error) {
        super(message);
        Error = error;
    }

    public QueryMedicationListException(Throwable cause) {
        super(cause);
    }

    public QueryMedicationListException(String message, Throwable cause) {
        super(message, cause);
    }
    public Error getError (){
    	return Error;
    }

}