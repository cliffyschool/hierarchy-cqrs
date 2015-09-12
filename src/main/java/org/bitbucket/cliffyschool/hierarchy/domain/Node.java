package org.bitbucket.cliffyschool.hierarchy.domain;

import java.util.UUID;

public class Node {
    private final UUID id;
    private String name;

    public Node(UUID id, String name) {
        this.id = id;
        this.name = name;
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
}
