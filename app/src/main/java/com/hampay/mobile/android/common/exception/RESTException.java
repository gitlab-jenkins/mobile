package com.hampay.mobile.android.common.exception;

public class RESTException extends Exception {

    public RESTException() {
        
    }

    public RESTException( String message ) {
        super( message );
    }

    public RESTException( Throwable cause ) {
        super( cause );
    }
}
