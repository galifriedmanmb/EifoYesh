package com.gali.apps.eifoyesh.exceptions;

/**
 * Created by 1 on 4/21/2017.
 */

public class NullLocationException extends Exception {
    @Override
    public String getMessage() {
        return "Location is null";
    }
}
