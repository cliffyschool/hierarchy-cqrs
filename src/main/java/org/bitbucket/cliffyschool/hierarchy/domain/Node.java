package org.bitbucket.cliffyschool.hierarchy.domain;

import java.util.UUID;

public class Node {
    private final UUID id;
    private String name;
    private String color;
    private int childCount;

    public Node(UUID id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public int getChildCount() {
        return childCount;
    }

    public <T> void applyPropertyValueChange(String propertyName, T newValue) {
       if ("childcount".equalsIgnoreCase(propertyName))
           childCount = (Integer)newValue;
    }
}
