package org.bitbucket.cliffyschool.hierarchy.event;

import java.util.UUID;

public class HierarchyCreated extends Event {
    public HierarchyCreated(UUID id) {
        this(id, 1L);
    }

    private HierarchyCreated(UUID id, long newVersionId){
        super(id, newVersionId);
    }

    @Override
    public Event copy(long newVersionId) {
        return new HierarchyCreated(getHierarchyId(), newVersionId);
    }
}
