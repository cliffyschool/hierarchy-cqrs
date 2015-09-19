package org.bitbucket.cliffyschool.hierarchy.event;

import java.util.Iterator;
import java.util.UUID;

public class HierarchyCreated extends Event {
    public HierarchyCreated(UUID id, long versionId) {
        super(id, versionId);
    }

    @Override
    public Event copy(long newVersionId) {
        return new HierarchyCreated(getHierarchyId(), newVersionId);
    }
}
