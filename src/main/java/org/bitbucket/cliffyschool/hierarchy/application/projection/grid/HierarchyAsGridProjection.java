package org.bitbucket.cliffyschool.hierarchy.application.projection.grid;

import org.bitbucket.cliffyschool.hierarchy.infrastructure.DummyProjectionStore;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.Projection;

import java.util.UUID;

public class HierarchyAsGridProjection extends DummyProjectionStore<UUID,HierarchyAsGrid>
        implements Projection<UUID,HierarchyAsGrid> {
}
