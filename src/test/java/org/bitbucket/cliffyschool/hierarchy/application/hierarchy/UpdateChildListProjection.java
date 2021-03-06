package org.bitbucket.cliffyschool.hierarchy.application.hierarchy;

import com.google.common.collect.Lists;
import org.bitbucket.cliffyschool.hierarchy.application.HierarchyService;
import org.bitbucket.cliffyschool.hierarchy.application.projection.childlist.ChildList;
import org.bitbucket.cliffyschool.hierarchy.application.projection.childlist.ChildListProjection;
import org.bitbucket.cliffyschool.hierarchy.application.projection.childlist.ChildListProjectionUpdater;
import org.bitbucket.cliffyschool.hierarchy.application.projection.childlist.Node;
import org.bitbucket.cliffyschool.hierarchy.application.projection.grid.HierarchyAsGridProjection;
import org.bitbucket.cliffyschool.hierarchy.application.projection.grid.HierarchyAsGridProjectionUpdater;
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

public class UpdateChildListProjection {

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
    public void whenRootNodeIsCreatedThenChildListShouldReflectIt(){
        hierarchyService.createNewNode(new CreateNodeCommand(hierarchyId, 1L, nodeId, "rootNode", ""));

        Optional<ChildList> hierarchyView = hierarchyService.getChildList(hierarchyId, Optional.empty());

        assertThat(hierarchyView).isPresent();
        assertThat(hierarchyView.get().getNodes()).extracting("name").contains("rootNode");

    }

    @Test
    public void whenNodeIsCreatedAsGrandChildThenChildListViewShouldReflectIt(){
        UUID childId = UUID.randomUUID();
        UUID grandChildId = UUID.randomUUID();
        hierarchyService.createNewNode(new CreateNodeCommand(hierarchyId, 1L, nodeId, "parent", "blue", Optional.empty()));
        hierarchyService.createNewNode(new CreateNodeCommand(hierarchyId, 2L, childId, "child", "blue", Optional.of(nodeId)));
        hierarchyService.createNewNode(new CreateNodeCommand(hierarchyId, 3L, grandChildId, "grandchild", "blue", Optional.of(childId)));


        ChildList list = hierarchyService.getChildList(hierarchyId, Optional.empty()).get();
        Node node = list.getNodes().get(0);
        assertThat(node).isNotNull();
        assertThat(node.getNodeId()).isEqualTo(nodeId);

        list = hierarchyService.getChildList(hierarchyId, Optional.of(nodeId)).get();
        Node childNode = list.getNodes().get(0);
        assertThat(childNode).isNotNull();
        assertThat(childNode.getName()).isEqualTo("child");

        list = hierarchyService.getChildList(hierarchyId, Optional.of(childId)).get();
        Node grandChild = list.getNodes().get(0);
        assertThat(grandChild).isNotNull();
        assertThat(grandChild.getName()).isEqualTo("grandchild");
    }

    @Test
    public void whenNodeIsCreatedAsChildItShouldHaveAnEmptyChildList(){
        UUID childId = UUID.randomUUID();
        hierarchyService.createNewNode(new CreateNodeCommand(hierarchyId, 1L, nodeId, "parent", "blue", Optional.empty()));
        hierarchyService.createNewNode(new CreateNodeCommand(hierarchyId, 2L, childId, "child", "blue", Optional.of(nodeId)));

        Optional<ChildList> list = hierarchyService.getChildList(hierarchyId, Optional.of(childId));

        assertThat(list).isPresent();
        assertThat(list.get().getNodes()).isEmpty();
    }

    @Test
    public void whenNodeIsRenamedThenChildListViewShouldReflectIt(){
        hierarchyService.createNewNode(new CreateNodeCommand(hierarchyId, 1L, nodeId, "myNode", ""));
        hierarchyService.changeNodeName(new ChangeNodeNameCommand(hierarchyId, 2L, nodeId, "newName"));
        Optional<ChildList> hierarchy = hierarchyService.getChildList(hierarchyId, Optional.empty());

        assertThat(hierarchy).isPresent();
        assertThat(hierarchy.get().getNodes()).extracting("name").contains("newName");
    }


    @Test
    public void whenNodeIsInsertedThenChildListViewShouldIncludeNodePathProperty(){
        hierarchyService.createNewNode(new CreateNodeCommand(hierarchyId, 1L, nodeId, "node", ""));
        UUID childNodeId = UUID.randomUUID();
        hierarchyService.createNewNode(new CreateNodeCommand(hierarchyId, 2L, childNodeId, "childNode",
                "", Optional.of(nodeId)));
        UUID grandChildNodeId = UUID.randomUUID();
        hierarchyService.createNewNode(new CreateNodeCommand(hierarchyId, 3L, grandChildNodeId, "grandChildNode",
                "", Optional.of(childNodeId)));

        Optional<ChildList> childList = hierarchyService.getChildList(hierarchyId, Optional.of(childNodeId));
        Node grandChild = childList.get().getNodes().stream()
                .filter(n -> n.getNodeId().equals(grandChildNodeId))
                .findFirst().get();
        String expectedNodePath = String.format("%s->%s", nodeId, childNodeId);
        assertThat(grandChild.getNodePath()).isEqualTo(expectedNodePath);
    }

    @Test
    public void whenNodeColorIsChangedThenChildListViewShouldReflectIt(){
        UUID childId = UUID.randomUUID();
        hierarchyService.createNewNode(new CreateNodeCommand(hierarchyId, 1L, nodeId, "parent", "blue", Optional.empty()));
        hierarchyService.createNewNode(new CreateNodeCommand(hierarchyId, 2L, childId, "child", "blue", Optional.of(nodeId)));

        hierarchyService.changeNodePropertyValue(new ChangeNodePropertyCommand(childId, 1L, "color", "red"));
        Optional<ChildList> list = hierarchyService.getChildList(hierarchyId, Optional.of(nodeId));

        assertThat(list).isPresent();
        Node node = list.get().getNodes().stream().filter(n -> n.getNodeId().equals(childId)).findFirst().get();
        assertThat(node.getColor()).isEqualTo("red");
    }
}
