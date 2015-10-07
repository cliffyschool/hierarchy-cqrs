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

    public UUID getHierarchyId() {
        return hierarchyId;
    }

    public Optional<UUID> getParentNodeId() {
        return parentNodeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChildListProjectionKey that = (ChildListProjectionKey) o;

        if (!hierarchyId.equals(that.hierarchyId)) return false;
        return parentNodeId.equals(that.parentNodeId);

    }

    @Override
    public int hashCode() {
        int result = hierarchyId.hashCode();
        result = 31 * result + parentNodeId.hashCode();
        return result;
    }
}
