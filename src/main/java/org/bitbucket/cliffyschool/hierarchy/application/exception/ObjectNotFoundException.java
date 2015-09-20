package org.bitbucket.cliffyschool.hierarchy.application.exception;

import java.util.UUID;

public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(String objectType, UUID objectId) {
        super(String.format("Object '%s' of type '%s' not found.", objectType, objectId));
    }
}
