package org.bitbucket.cliffyschool.hierarchy.event;

import java.util.UUID;

public abstract class Event {
    private UUID hierarchyId;
    protected long versionId;

    public Event(UUID id)
    {
        hierarchyId = id;
    }

    public long getVersionId() {
        return versionId;
    }

    public UUID getHierarchyId() {
        return hierarchyId;
    }

    public abstract Event withVersionId(long newVersionId);
}
