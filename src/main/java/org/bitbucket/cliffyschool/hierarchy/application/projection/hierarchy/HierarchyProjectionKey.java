package org.bitbucket.cliffyschool.hierarchy.application.projection.hierarchy;

import java.util.UUID;

public class HierarchyProjectionKey
{
    private UUID hierarchyId;
    private UUID nodeId;

    public HierarchyProjectionKey(UUID hierarchyId, UUID nodeId) {
        this.hierarchyId = hierarchyId;
        this.nodeId = nodeId;
    }
}
