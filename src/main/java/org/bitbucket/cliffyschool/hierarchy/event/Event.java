package org.bitbucket.cliffyschool.hierarchy.event;

import java.util.UUID;

public class Event {
    private UUID hierarchyId;

    public Event(UUID hierarchyId)
    {
        this.hierarchyId = hierarchyId;
    }

    public UUID getHierarchyId() {
        return hierarchyId;
    }
}
