package org.bitbucket.cliffyschool.hierarchy.application.service;

import org.bitbucket.cliffyschool.hierarchy.application.projection.HierarchyAsGrid;
import org.bitbucket.cliffyschool.hierarchy.application.projection.HierarchyAsGridProjection;
import org.bitbucket.cliffyschool.hierarchy.application.projection.HierarchyAsGridProjectionUpdater;
import org.bitbucket.cliffyschool.hierarchy.command.ChangeNodeNameCommand;
import org.bitbucket.cliffyschool.hierarchy.command.CreateNodeCommand;
import org.bitbucket.cliffyschool.hierarchy.domain.Hierarchy;
import org.bitbucket.cliffyschool.hierarchy.domain.InMemoryHierarchyRepository;
import org.bitbucket.cliffyschool.hierarchy.event.CreateHierarchyCommand;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.FakeBus;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultHierarchyServiceTest {

    private HierarchyAsGridProjection gridProjection = new HierarchyAsGridProjection();
    HierarchyService hierarchyService = new DefaultHierarchyService(
            new InMemoryHierarchyRepository(),
            gridProjection,
            new FakeBus(new HierarchyAsGridProjectionUpdater(gridProjection)));
    UUID hierarchyId;

    @Before
    public void setUp() {
        hierarchyId = UUID.randomUUID();
        hierarchyService.createNewHierarchy(new CreateHierarchyCommand(hierarchyId, "testHierarchy", 0L));
    }


    @Test
    public void whenNodeIsCreatedThenGridViewShouldIncludeIt(){
        UUID nodeId = UUID.randomUUID();
        hierarchyService.createNewNode(hierarchyId, new CreateNodeCommand(nodeId, "myNode", "", "", 1L));

        Optional<HierarchyAsGrid> hierarchyAsGrid = hierarchyService.getHierarchyAsGrid(hierarchyId);

        assertThat(hierarchyAsGrid).isPresent();
        assertThat(hierarchyAsGrid.get().getNodes()).extracting("name").contains("myNode");

    }

    @Test
    public void whenNodeIsRenamedThenGridViewShouldReflectIt(){
        UUID nodeId = UUID.randomUUID();
        hierarchyService.createNewNode(hierarchyId, new CreateNodeCommand(nodeId, "myNode", "", "", 1L));
        hierarchyService.changeNodeName(hierarchyId, new ChangeNodeNameCommand(nodeId, "newName", 2L));
        Optional<HierarchyAsGrid> hierarchyAsGrid = hierarchyService.getHierarchyAsGrid(hierarchyId);

        assertThat(hierarchyAsGrid).isPresent();
        assertThat(hierarchyAsGrid.get().getNodes()).extracting("name").contains("newName");
    }
}
