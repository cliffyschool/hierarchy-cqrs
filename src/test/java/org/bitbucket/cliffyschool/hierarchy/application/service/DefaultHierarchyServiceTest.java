package org.bitbucket.cliffyschool.hierarchy.application.service;

import com.google.common.collect.Lists;
import org.bitbucket.cliffyschool.hierarchy.application.projection.*;
import org.bitbucket.cliffyschool.hierarchy.command.ChangeNodeNameCommand;
import org.bitbucket.cliffyschool.hierarchy.command.CreateNodeCommand;
import org.bitbucket.cliffyschool.hierarchy.domain.InMemoryHierarchyRepository;
import org.bitbucket.cliffyschool.hierarchy.command.CreateHierarchyCommand;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.FakeBus;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultHierarchyServiceTest {

    private HierarchyAsGridProjection gridProjection = new HierarchyAsGridProjection();
    private HierarchyProjection hierarchyProjection = new HierarchyProjection();
    HierarchyService hierarchyService = new DefaultHierarchyService(
            new InMemoryHierarchyRepository(),
            hierarchyProjection,
            gridProjection,
            new FakeBus(Lists.newArrayList(
                    new HierarchyProjectionUpdater(hierarchyProjection),
                    new HierarchyAsGridProjectionUpdater(gridProjection))));
    UUID hierarchyId;

    @Before
    public void setUp() {
        hierarchyId = UUID.randomUUID();
        hierarchyService.createNewHierarchy(new CreateHierarchyCommand(hierarchyId, 0L, "testHierarchy"));
    }


    @Test
    public void whenNodeIsCreatedThenGridViewShouldIncludeIt(){
        UUID nodeId = UUID.randomUUID();
        hierarchyService.createNewNode(hierarchyId, new CreateNodeCommand(nodeId, 1L, "", "myNode"));

        Optional<HierarchyAsGrid> hierarchyAsGrid = hierarchyService.getHierarchyAsGrid(hierarchyId);

        assertThat(hierarchyAsGrid).isPresent();
        assertThat(hierarchyAsGrid.get().getNodes()).extracting("name").contains("myNode");

    }

    @Test
    public void whenNodeIsRenamedThenGridViewShouldReflectIt(){
        UUID nodeId = UUID.randomUUID();
        hierarchyService.createNewNode(hierarchyId, new CreateNodeCommand(nodeId, 1L, "","myNode"));
        hierarchyService.changeNodeName(hierarchyId, new ChangeNodeNameCommand(nodeId, 2L, "newName"));
        Optional<HierarchyAsGrid> hierarchyAsGrid = hierarchyService.getHierarchyAsGrid(hierarchyId);

        assertThat(hierarchyAsGrid).isPresent();
        assertThat(hierarchyAsGrid.get().getNodes()).extracting("name").contains("newName");
    }

    @Test
    public void whenNodeIsCreatedThenHierarchyViewShouldReflectIt(){
        UUID nodeId = UUID.randomUUID();
        hierarchyService.createNewNode(hierarchyId, new CreateNodeCommand(nodeId, 1L, "", "myNode"));

        Optional<Hierarchy> hierarchyView = hierarchyService.getHierarchy(hierarchyId);

        assertThat(hierarchyView).isPresent();
        assertThat(hierarchyView.get().getNodes()).extracting("name").contains("myNode");

    }

    @Test
    public void whenNodeIsRenamedThenHierarchyViewShouldReflectIt(){
        UUID nodeId = UUID.randomUUID();
        hierarchyService.createNewNode(hierarchyId, new CreateNodeCommand(nodeId, 1L, "","myNode"));
        hierarchyService.changeNodeName(hierarchyId, new ChangeNodeNameCommand(nodeId, 2L, "newName"));
        Optional<Hierarchy> hierarchy = hierarchyService.getHierarchy(hierarchyId);

        assertThat(hierarchy).isPresent();
        assertThat(hierarchy.get().getNodes()).extracting("name").contains("newName");
    }
}
