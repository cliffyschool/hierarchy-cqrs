package org.bitbucket.cliffyschool.hierarchy.application.projection.hierarchy;

import org.bitbucket.cliffyschool.hierarchy.infrastructure.DummyProjectionStore;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.Projection;

import java.util.UUID;

public class HierarchyProjection extends DummyProjectionStore<HierarchyProjectionKey,Hierarchy>
        implements Projection<HierarchyProjectionKey,Hierarchy> {

}

class HierarchyProjectionKey
{
    private UUID hierarchyId;
    private UUID nodeId;

    public HierarchyProjectionKey(UUID hierarchyId, UUID nodeId) {
        this.hierarchyId = hierarchyId;
        this.nodeId = nodeId;
    }
}
