package org.bitbucket.cliffyschool.hierarchy.domain;

import org.bitbucket.cliffyschool.hierarchy.infrastructure.IAggregateRoot;

import java.util.UUID;

public interface Node extends IAggregateRoot {
    String getName();

    String getColor();

    UUID getHierarchyId();

    void changePropertyValue(String propertyName, String value);
}
