package org.bitbucket.cliffyschool.hierarchy.application.projection.childlist;

import java.util.Optional;
import java.util.UUID;

public class ChildListProjectionKey
{
    private UUID hierarchyId;
    private Optional<UUID> parentNodeId;

    public ChildListProjectionKey(UUID hierarchyId, Optional<UUID> parentNodeId) {
        this.hierarchyId = hierarchyId;
        this.parentNodeId = parentNodeId;
    }
}
