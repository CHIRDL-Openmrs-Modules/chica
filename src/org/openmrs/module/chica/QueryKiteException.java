
package org.openmrs.module.chica;

import org.openmrs.module.atd.hibernateBeans.ATDError;


public class QueryKiteException extends Exception {

    private static final long serialVersionUID = -2985522122680870005L;
    private ATDError atdError = null;

    public QueryKiteException() {
        super();
    }

    public QueryKiteException(String message) {
        super(message);
    }
    
    public QueryKiteException(String message, ATDError error) {
        super(message);
        atdError = error;
    }

    public QueryKiteException(Throwable cause) {
        super(cause);
    }

    public QueryKiteException(String message, Throwable cause) {
        super(message, cause);
    }
    public ATDError getATDError (){
    	return atdError;
    }

}