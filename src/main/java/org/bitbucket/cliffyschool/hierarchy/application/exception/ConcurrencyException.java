package org.bitbucket.cliffyschool.hierarchy.application.exception;

public class ConcurrencyException extends RuntimeException {
    public ConcurrencyException(String msg){
        super(msg);
    }
}
