
package org.openmrs.module.chica;

import org.openmrs.module.chirdlutilbackports.hibernateBeans.Error;


public class QueryKiteException extends Exception {

    private static final long serialVersionUID = -2985522122680870005L;
    private Error Error = null;

    public QueryKiteException() {
        super();
    }

    public QueryKiteException(String message) {
        super(message);
    }
    
    public QueryKiteException(String message, Error error) {
        super(message);
        Error = error;
    }

    public QueryKiteException(Throwable cause) {
        super(cause);
    }

    public QueryKiteException(String message, Throwable cause) {
        super(message, cause);
    }
    public Error getError (){
    	return Error;
    }

}