
package org.openmrs.module.chica;

import org.openmrs.module.atd.hibernateBeans.ATDError;

/**
 * @author tmdugan
 */
public class QueryImmunizationsException extends Exception {

    private static final long serialVersionUID = -2985522122680870005L;
    private ATDError atdError = null;

    public QueryImmunizationsException() {
        super();
    }

    public QueryImmunizationsException(String message) {
        super(message);
    }
    
    public QueryImmunizationsException(String message, ATDError error) {
        super(message);
        atdError = error;
    }

    public QueryImmunizationsException(Throwable cause) {
        super(cause);
    }

    public QueryImmunizationsException(String message, Throwable cause) {
        super(message, cause);
    }
    public ATDError getATDError (){
    	return atdError;
    }

}