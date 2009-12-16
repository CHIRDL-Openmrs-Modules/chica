
package org.openmrs.module.chica;

import org.openmrs.module.atd.hibernateBeans.ATDError;


public class QueryMedicationListException extends Exception {

    private static final long serialVersionUID = -2985522122680870005L;
    private ATDError atdError = null;

    public QueryMedicationListException() {
        super();
    }

    public QueryMedicationListException(String message) {
        super(message);
    }
    
    public QueryMedicationListException(String message, ATDError error) {
        super(message);
        atdError = error;
    }

    public QueryMedicationListException(Throwable cause) {
        super(cause);
    }

    public QueryMedicationListException(String message, Throwable cause) {
        super(message, cause);
    }
    public ATDError getATDError (){
    	return atdError;
    }

}