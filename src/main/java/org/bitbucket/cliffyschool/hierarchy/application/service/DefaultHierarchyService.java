package org.bitbucket.cliffyschool.hierarchy.application.service;

import com.google.common.collect.Lists;
import org.bitbucket.cliffyschool.hierarchy.application.projection.HierarchyAsGrid;
import org.bitbucket.cliffyschool.hierarchy.application.projection.HierarchyAsGridProjection;
import org.bitbucket.cliffyschool.hierarchy.command.CreateNodeCommand;
import org.bitbucket.cliffyschool.hierarchy.domain.Hierarchy;
import org.bitbucket.cliffyschool.hierarchy.domain.HierarchyRepository;
import org.bitbucket.cliffyschool.hierarchy.event.Event;
import org.bitbucket.cliffyschool.hierarchy.event.HierarchyCreatedEvent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DefaultHierarchyService implements HierarchyService {

    private HierarchyRepository hierarchyRepository;
    private HierarchyAsGridProjection gridProjection;

    public DefaultHierarchyService(HierarchyRepository hierarchyRepository, HierarchyAsGridProjection gridProjection)
    {
        this.hierarchyRepository = hierarchyRepository;
        this.gridProjection = gridProjection;
    }

    @Override
    public void saveHierarchy(Hierarchy hierarchy) {
        Optional.ofNullable(hierarchy).ifPresent(hierarchyRepository::save);
        Optional.ofNullable(hierarchy).ifPresent(h -> gridProjection.write(Lists.newArrayList(new HierarchyCreatedEvent(h.getId()))));
    }

    @Override
    public void createNewNode(UUID hierarchyId, CreateNodeCommand createNodeCommand) {
        Optional<Hierarchy> hier = hierarchyRepository.findById(hierarchyId);

        List<Event> events = hier.map(h -> h.apply(createNodeCommand)).get();

        hier.ifPresent(hierarchyRepository::save);

        gridProjection.write(events);
    }

    @Override
    public Optional<HierarchyAsGrid> getHierarchyAsGrid(UUID hierarchyId) {
        return gridProjection.findHierarchyAsGrid(hierarchyId);
    }
}
