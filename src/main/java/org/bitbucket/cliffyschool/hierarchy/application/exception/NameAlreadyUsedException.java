package org.bitbucket.cliffyschool.hierarchy.application.exception;

public class NameAlreadyUsedException extends RuntimeException {
    public NameAlreadyUsedException(String objectType, String name) {
        super(String.format("Object '%s' with name '%s' already exists.", objectType, name));
    }
}
