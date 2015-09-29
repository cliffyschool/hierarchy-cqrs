package org.bitbucket.cliffyschool.hierarchy.application.hierarchy;

import com.google.common.collect.Lists;
import org.bitbucket.cliffyschool.hierarchy.application.HierarchyService;
import org.bitbucket.cliffyschool.hierarchy.application.projection.childlist.ChildList;
import org.bitbucket.cliffyschool.hierarchy.application.projection.childlist.ChildListProjection;
import org.bitbucket.cliffyschool.hierarchy.application.projection.childlist.HierarchyProjectionUpdater;
import org.bitbucket.cliffyschool.hierarchy.application.projection.childlist.Node;
import org.bitbucket.cliffyschool.hierarchy.application.projection.grid.HierarchyAsGridProjection;
import org.bitbucket.cliffyschool.hierarchy.application.projection.grid.HierarchyAsGridProjectionUpdater;
import org.bitbucket.cliffyschool.hierarchy.command.ChangeNodeNameCommand;
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
    public void whenNodeIsRenamedThenChildListViewShouldReflectIt(){
        hierarchyService.createNewNode(new CreateNodeCommand(hierarchyId, 1L, nodeId, "myNode", ""));
        hierarchyService.changeNodeName(new ChangeNodeNameCommand(hierarchyId, 2L, nodeId, "newName"));
        Optional<ChildList> hierarchy = hierarchyService.getChildList(hierarchyId, Optional.empty());

        assertThat(hierarchy).isPresent();
        assertThat(hierarchy.get().getNodes()).extracting("name").contains("newName");
    }
}
