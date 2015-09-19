package org.bitbucket.cliffyschool.hierarchy.event;

import java.util.UUID;

public abstract class Event {
    private UUID hierarchyId;
    private long versionId;

    public Event(UUID id, long versionId)
    {
        hierarchyId = id;
        this.versionId = versionId;
    }
    public long getVersionId() {
        return versionId;
    }

    public UUID getHierarchyId() {
        return hierarchyId;
    }

    public abstract Event copy(long newVersionId);
}
