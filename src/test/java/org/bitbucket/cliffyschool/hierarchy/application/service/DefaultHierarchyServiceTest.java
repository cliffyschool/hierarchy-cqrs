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
    UUID nodeId;

    @Before
    public void setUp() {
        hierarchyId = UUID.randomUUID();
        hierarchyService.createNewHierarchy(new CreateHierarchyCommand(hierarchyId, "testHierarchy"));
        nodeId = UUID.randomUUID();
    }


    @Test
    public void whenNodeIsCreatedThenGridViewShouldIncludeIt(){
        hierarchyService.createNewNode(hierarchyId, new CreateNodeCommand(nodeId, 1L, "myNode", ""));

        Optional<HierarchyAsGrid> hierarchyAsGrid = hierarchyService.getHierarchyAsGrid(hierarchyId);

        assertThat(hierarchyAsGrid).isPresent();
        assertThat(hierarchyAsGrid.get().getNodes()).extracting("name").contains("myNode");

    }

    @Test
    public void whenNodeIsRenamedThenGridViewShouldReflectIt(){
        hierarchyService.createNewNode(hierarchyId, new CreateNodeCommand(nodeId, 1L, "myNode", ""));
        hierarchyService.changeNodeName(hierarchyId, new ChangeNodeNameCommand(nodeId, 2L, "newName"));
        Optional<HierarchyAsGrid> hierarchyAsGrid = hierarchyService.getHierarchyAsGrid(hierarchyId);

        assertThat(hierarchyAsGrid).isPresent();
        assertThat(hierarchyAsGrid.get().getNodes()).extracting("name").contains("newName");
    }

    @Test
    public void whenNodeIsCreatedThenHierarchyViewShouldReflectIt(){
        hierarchyService.createNewNode(hierarchyId, new CreateNodeCommand(nodeId, 1L, "myNode", ""));

        Optional<Hierarchy> hierarchyView = hierarchyService.getHierarchy(hierarchyId);

        assertThat(hierarchyView).isPresent();
        assertThat(hierarchyView.get().getNodes()).extracting("name").contains("myNode");

    }

    @Test
    public void whenNodeIsCreatedAsGrandChildThenHierarchyViewShouldReflectIt(){
        UUID childId = UUID.randomUUID();
        UUID grandChildId = UUID.randomUUID();
        hierarchyService.createNewNode(hierarchyId, new CreateNodeCommand(nodeId, 1L, "parent", "blue", Optional.empty()));
        hierarchyService.createNewNode(hierarchyId, new CreateNodeCommand(childId, 2L, "child", "blue", Optional.of(nodeId)));
        hierarchyService.createNewNode(hierarchyId, new CreateNodeCommand(grandChildId, 3L, "grandchild", "blue", Optional.of(childId)));


        Hierarchy hierarchy = hierarchyService.getHierarchy(hierarchyId).get();
        Node node = hierarchy.getNodes().get(0);
        assertThat(node).isNotNull();
        assertThat(node.getChildren()).hasSize(1);
        Node childNode = node.getChildren().get(0);
        assertThat(childNode).isNotNull();
        assertThat(childNode.getName()).isEqualTo("child");
        assertThat(childNode.getChildren()).hasSize(1);
        Node grandChild = childNode.getChildren().get(0);
        assertThat(grandChild).isNotNull();
        assertThat(grandChild.getName()).isEqualTo("grandchild");
    }

    @Test
    public void whenNodeIsRenamedThenHierarchyViewShouldReflectIt(){
        hierarchyService.createNewNode(hierarchyId, new CreateNodeCommand(nodeId, 1L, "myNode", ""));
        hierarchyService.changeNodeName(hierarchyId, new ChangeNodeNameCommand(nodeId, 2L, "newName"));
        Optional<Hierarchy> hierarchy = hierarchyService.getHierarchy(hierarchyId);

        assertThat(hierarchy).isPresent();
        assertThat(hierarchy.get().getNodes()).extracting("name").contains("newName");
    }
}
