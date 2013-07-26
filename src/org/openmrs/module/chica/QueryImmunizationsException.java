
package org.openmrs.module.chica;

import org.openmrs.module.chirdlutilbackports.hibernateBeans.Error;

/**
 * @author tmdugan
 */
public class QueryImmunizationsException extends Exception {

    private static final long serialVersionUID = -2985522122680870005L;
    private Error Error = null;

    public QueryImmunizationsException() {
        super();
    }

    public QueryImmunizationsException(String message) {
        super(message);
    }
    
    public QueryImmunizationsException(String message, Error error) {
        super(message);
        Error = error;
    }

    public QueryImmunizationsException(Throwable cause) {
        super(cause);
    }

    public QueryImmunizationsException(String message, Throwable cause) {
        super(message, cause);
    }
    public Error getError (){
    	return Error;
    }

}