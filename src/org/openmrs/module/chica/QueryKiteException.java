
package org.openmrs.module.chica;

import org.openmrs.module.chica.hibernateBeans.ChicaError;


public class QueryKiteException extends Exception {

    private static final long serialVersionUID = -2985522122680870005L;
    private ChicaError chicaError = null;

    public QueryKiteException() {
        super();
    }

    public QueryKiteException(String message) {
        super(message);
    }
    
    public QueryKiteException(String message, ChicaError error) {
        super(message);
        chicaError = error;
    }

    public QueryKiteException(Throwable cause) {
        super(cause);
    }

    public QueryKiteException(String message, Throwable cause) {
        super(message, cause);
    }
    public ChicaError getChicaError (){
    	return chicaError;
    }

}