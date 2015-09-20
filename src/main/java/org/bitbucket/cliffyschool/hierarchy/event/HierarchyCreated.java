package org.bitbucket.cliffyschool.hierarchy.event;

import java.util.UUID;

public class HierarchyCreated extends Event {
    public HierarchyCreated(UUID id) {
        super(id);
    }

    @Override
    public Event withVersionId(long newVersionId) {
        HierarchyCreated hc = new HierarchyCreated(getHierarchyId());
        hc.versionId = newVersionId;
        return hc;
    }
}
