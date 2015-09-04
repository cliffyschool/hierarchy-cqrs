package org.bitbucket.cliffyschool.hierarchy.infrastructure;

import com.google.common.collect.Maps;
import org.bitbucket.cliffyschool.hierarchy.domain.Hierarchy;
import org.bitbucket.cliffyschool.hierarchy.domain.HierarchyRepository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryHierarchyRepository implements HierarchyRepository {

    private Map<UUID,Hierarchy> hierarchyMap = Maps.newHashMap();

    @Override
    public void save(Hierarchy hierarchy) {
        Optional.of(hierarchy).map(h -> hierarchyMap.put(hierarchy.getId(), hierarchy));
    }

    @Override
    public Optional<Hierarchy> findById(UUID id) {
        return Optional.ofNullable(hierarchyMap.get(id));
    }
}
