package org.bitbucket.cliffyschool.hierarchy.application.projection.childlist;

import org.bitbucket.cliffyschool.hierarchy.infrastructure.DummyProjectionStore;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.Projection;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ChildListProjection extends DummyProjectionStore<ChildListProjectionKey,ChildList>
        implements Projection<ChildListProjectionKey,ChildList> {

        public List<ChildList> findListsContainingChild(UUID hierarchyId, UUID childId) {
                // TODO: this is terrible. It's meant to be similar to searching a cache by a secondary key
                List<ChildList> listsContainingChild =
                        this.objectsById.entrySet().stream()
                                .filter(e -> e.getKey().getHierarchyId().equals(hierarchyId))
                                .filter(e -> e.getValue().getNodes().stream().anyMatch(n -> n.getNodeId().equals(childId)))
                                .map(Map.Entry::getValue)
                                .collect(Collectors.toList());

                return listsContainingChild;
        }

}

