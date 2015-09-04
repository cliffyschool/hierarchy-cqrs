package org.bitbucket.cliffyschool.hierarchy.application.service;

import org.bitbucket.cliffyschool.hierarchy.application.projection.HierarchyAsGrid;
import org.bitbucket.cliffyschool.hierarchy.application.projection.HierarchyAsGridProjection;
import org.bitbucket.cliffyschool.hierarchy.domain.Hierarchy;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.InMemoryHierarchyRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultHierarchyServiceTest {

    HierarchyService hierarchyService = new DefaultHierarchyService(new InMemoryHierarchyRepository(), new HierarchyAsGridProjection());
    Hierarchy hierarchy;

    @Before
    public void setUp() {
        hierarchy = new Hierarchy(UUID.randomUUID());
        hierarchyService.saveHierarchy(hierarchy);
    }


    @Test
    public void whenNodeIsCreatedThenGridViewShouldIncludeIt(){
        hierarchyService.createNewNode(hierarchy.getId(), "newNodeName");

        Optional<HierarchyAsGrid> hierarchyAsGrid = hierarchyService.getHierarchyAsGrid(hierarchy.getId());

        assertThat(hierarchyAsGrid).isPresent();
        assertThat(hierarchyAsGrid.get().getNodes()).extracting("name").contains("newNodeName");

    }
}
