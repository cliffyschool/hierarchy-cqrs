package org.bitbucket.cliffyschool.hierarchy.application.hierarchy;

import com.google.common.collect.Lists;
import org.bitbucket.cliffyschool.hierarchy.application.HierarchyService;
import org.bitbucket.cliffyschool.hierarchy.application.projection.childlist.ChildList;
import org.bitbucket.cliffyschool.hierarchy.application.projection.childlist.ChildListProjection;
import org.bitbucket.cliffyschool.hierarchy.application.projection.childlist.ChildListProjectionUpdater;
import org.bitbucket.cliffyschool.hierarchy.application.projection.childlist.Node;
import org.bitbucket.cliffyschool.hierarchy.application.projection.grid.HierarchyAsGrid;
import org.bitbucket.cliffyschool.hierarchy.application.projection.grid.HierarchyAsGridProjection;
import org.bitbucket.cliffyschool.hierarchy.application.projection.grid.HierarchyAsGridProjectionUpdater;
import org.bitbucket.cliffyschool.hierarchy.application.projection.grid.NodeAsRow;
import org.bitbucket.cliffyschool.hierarchy.application.service.DefaultHierarchyService;
import org.bitbucket.cliffyschool.hierarchy.command.ChangeNodeNameCommand;
import org.bitbucket.cliffyschool.hierarchy.command.ChangeNodePropertyCommand;
import org.bitbucket.cliffyschool.hierarchy.command.CreateHierarchyCommand;
import org.bitbucket.cliffyschool.hierarchy.command.CreateNodeCommand;
import org.bitbucket.cliffyschool.hierarchy.domain.repository.DummyHierarchyRepository;
import org.bitbucket.cliffyschool.hierarchy.domain.repository.DummyNodeRepository;
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
            new DummyHierarchyRepository(),
            new DummyNodeRepository(),
            childListProjection,
            gridProjection,
            new FakeBus(Lists.newArrayList(
                    new ChildListProjectionUpdater(childListProjection),
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

    @Test
    public void whenNodeIsInsertedThenGridViewShouldIncludeNodePathProperty(){
        hierarchyService.createNewNode(new CreateNodeCommand(hierarchyId, 1L, nodeId, "node", ""));
        UUID childNodeId = UUID.randomUUID();
        hierarchyService.createNewNode(new CreateNodeCommand(hierarchyId, 2L, childNodeId, "childNode",
                "", Optional.of(nodeId)));
        UUID grandChildNodeId = UUID.randomUUID();
        hierarchyService.createNewNode(new CreateNodeCommand(hierarchyId, 3L, grandChildNodeId, "grandChildNode",
                "", Optional.of(childNodeId)));

        Optional<HierarchyAsGrid> hierarchyAsGrid = hierarchyService.getHierarchyAsGrid(hierarchyId);
        Optional<NodeAsRow> grandChildRow = hierarchyAsGrid.get().getRows().stream()
                .filter(row -> row.getNodeId().equals(grandChildNodeId)).findFirst();

        String expectedNodePath = String.format("%s->%s", nodeId, childNodeId);
        assertThat(grandChildRow.get().getNodePath()).isEqualTo(expectedNodePath);

    }
    @Test
    public void whenNodeColorIsChangedThenGridViewShouldReflectIt(){
        UUID childId = UUID.randomUUID();
        hierarchyService.createNewNode(new CreateNodeCommand(hierarchyId, 1L, nodeId, "parent", "blue", Optional.empty()));
        hierarchyService.createNewNode(new CreateNodeCommand(hierarchyId, 2L, childId, "child", "blue", Optional.of(nodeId)));

        hierarchyService.changeNodePropertyValue(new ChangeNodePropertyCommand(childId, 1L, "color", "red"));
        Optional<HierarchyAsGrid> grid = hierarchyService.getHierarchyAsGrid(hierarchyId);

        assertThat(grid).isPresent();
        NodeAsRow node = grid.get().getRows().stream().filter(n -> n.getNodeId().equals(childId)).findFirst().get();
        assertThat(node.getColor()).isEqualTo("red");
    }

}
