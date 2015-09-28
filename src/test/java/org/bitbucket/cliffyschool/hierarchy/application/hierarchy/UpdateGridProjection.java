package org.bitbucket.cliffyschool.hierarchy.application.hierarchy;

import com.google.common.collect.Lists;
import org.bitbucket.cliffyschool.hierarchy.application.projection.childlist.ChildListProjection;
import org.bitbucket.cliffyschool.hierarchy.application.projection.childlist.HierarchyProjectionUpdater;
import org.bitbucket.cliffyschool.hierarchy.application.projection.grid.HierarchyAsGrid;
import org.bitbucket.cliffyschool.hierarchy.application.projection.grid.HierarchyAsGridProjection;
import org.bitbucket.cliffyschool.hierarchy.application.projection.grid.HierarchyAsGridProjectionUpdater;
import org.bitbucket.cliffyschool.hierarchy.command.ChangeNodeNameCommand;
import org.bitbucket.cliffyschool.hierarchy.command.CreateHierarchyCommand;
import org.bitbucket.cliffyschool.hierarchy.command.CreateNodeCommand;
import org.bitbucket.cliffyschool.hierarchy.domain.HierarchyRepository;
import org.bitbucket.cliffyschool.hierarchy.domain.NodeRepository;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.FakeBus;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateGridProjection {

    private HierarchyAsGridProjection gridProjection = new HierarchyAsGridProjection();
    private ChildListProjection childListProjection = new ChildListProjection();
    HierarchyService hierarchyService = new DefaultHierarchyService(
            new HierarchyRepository(),
            new NodeRepository(),
            childListProjection,
            gridProjection,
            new FakeBus(Lists.newArrayList(
                    new HierarchyProjectionUpdater(childListProjection),
                    new HierarchyAsGridProjectionUpdater(gridProjection))));
    UUID hierarchyId;
    UUID nodeId;

    @Before
    public void setUp() {
        hierarchyId = UUID.randomUUID();
        hierarchyService.createNewHierarchy(new CreateHierarchyCommand(hierarchyId, "testHierarchy"));
        nodeId = UUID.randomUUID();
    }

    @Test
    public void whenNodeIsCreatedThenGridViewShouldIncludeIt(){
        hierarchyService.createNewNode(new CreateNodeCommand(hierarchyId, 1L, nodeId, "myNode", ""));

        Optional<HierarchyAsGrid> hierarchyAsGrid = hierarchyService.getHierarchyAsGrid(hierarchyId);

        assertThat(hierarchyAsGrid).isPresent();
        assertThat(hierarchyAsGrid.get().getRows()).extracting("name").contains("myNode");

    }

    @Test
    public void whenNodeIsRenamedThenGridViewShouldReflectIt(){
        hierarchyService.createNewNode(new CreateNodeCommand(hierarchyId, 1L, nodeId, "myNode", ""));
        hierarchyService.changeNodeName(new ChangeNodeNameCommand(hierarchyId, 2L, nodeId, "newName"));
        Optional<HierarchyAsGrid> hierarchyAsGrid = hierarchyService.getHierarchyAsGrid(hierarchyId);

        assertThat(hierarchyAsGrid).isPresent();
        assertThat(hierarchyAsGrid.get().getRows()).extracting("name").contains("newName");
    }
}
