package org.bitbucket.cliffyschool.hierarchy.application.hierarchy;

import org.bitbucket.cliffyschool.hierarchy.application.exception.ObjectNotFoundException;
import org.bitbucket.cliffyschool.hierarchy.application.projection.childlist.ChildListProjection;
import org.bitbucket.cliffyschool.hierarchy.application.projection.grid.HierarchyAsGridProjection;
import org.bitbucket.cliffyschool.hierarchy.command.CreateHierarchyCommand;
import org.bitbucket.cliffyschool.hierarchy.command.CreateNodeCommand;
import org.bitbucket.cliffyschool.hierarchy.command.InsertNodeCommand;
import org.bitbucket.cliffyschool.hierarchy.domain.Hierarchy;
import org.bitbucket.cliffyschool.hierarchy.domain.Node;
import org.bitbucket.cliffyschool.hierarchy.domain.repository.DummyHierarchyRepository;
import org.bitbucket.cliffyschool.hierarchy.domain.repository.DummyNodeRepository;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.FakeBus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.mock;

public class UpdateDomain {

    private DummyHierarchyRepository hierarchyRepository;
    private DummyNodeRepository nodeRepository;
    private HierarchyService hierarchyService;
    private UUID hierarchyId;
    private Hierarchy hierarchy;
    private UUID nodeId;

    public UpdateDomain() {
    }

    @Before
    public void setUp() {
        hierarchyRepository = new DummyHierarchyRepository();
        nodeRepository = new DummyNodeRepository();
        hierarchyService = new DefaultHierarchyService(hierarchyRepository, nodeRepository,
                mock(ChildListProjection.class), mock(HierarchyAsGridProjection.class),
                mock(FakeBus.class));
        hierarchyId = UUID.randomUUID();
        hierarchyService.createNewHierarchy(new CreateHierarchyCommand(hierarchyId, "test hierarchy"));
        hierarchy = hierarchyRepository.findById(hierarchyId).get();
        nodeId = UUID.randomUUID();
    }

    @Test
    public void createNodeWithoutParentShouldAddNodeToHierarchyRoot() {
        hierarchyService.createNewNode(new CreateNodeCommand(hierarchyId, 1L, nodeId, "node name", "blue"));

        hierarchy = hierarchyRepository.findById(hierarchyId).get();

        assertThat(hierarchy.containsNode(nodeId)).isTrue();
        assertThat(hierarchy.getParentId(nodeId)).isEmpty();
    }

    @Test
    public void createNodeWithParentShouldAddNodeUnderParent() {
        Optional<UUID> parentId = Optional.of(UUID.randomUUID());
        hierarchyService.createNewNode(new CreateNodeCommand(hierarchyId, 1L, parentId.get(),
                "parent name", "blue"));
        hierarchyService.createNewNode(new CreateNodeCommand(hierarchyId, 2L, nodeId,
                "node name", "blue", parentId));

        hierarchy = hierarchyRepository.findById(hierarchyId).get();

        assertThat(hierarchy.containsNode(nodeId)).isTrue();
        assertThat(hierarchy.getParentId(nodeId)).isEqualTo(parentId);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void createNodeWithNonExistentHierarchyShouldThrowException() {
        thrown.expect(ObjectNotFoundException.class);
        thrown.expectMessage(containsString("Hierarchy"));
        hierarchyService.createNewNode(new CreateNodeCommand(UUID.randomUUID(), 1L, nodeId, "node name", "blue"));
    }

    @Test
    public void createNodeWithNonExistentParentShouldThrowException() {
        Optional<UUID> parentId = Optional.of(UUID.randomUUID());

        thrown.expect(ObjectNotFoundException.class);
        thrown.expectMessage(containsString("Node"));
        hierarchyService.createNewNode(new CreateNodeCommand(hierarchyId, 1L, nodeId, "node name", "blue", parentId));
    }

    @Test
    public void insertNodeShouldMoveNodeUnderParent() {
        Optional<UUID> parentId = Optional.of(UUID.randomUUID());
        hierarchyService.createNewNode(new CreateNodeCommand(hierarchyId, 1L, parentId.get(), "parent name", "blue"));
        hierarchyService.createNewNode(new CreateNodeCommand(hierarchyId, 2L, nodeId, "node name", "blue"));
        hierarchyService.insertNode(new InsertNodeCommand(hierarchyId, 3L, parentId.get(), nodeId));

        hierarchy = hierarchyRepository.findById(hierarchyId).get();

        assertThat(hierarchy.containsNode(nodeId)).isTrue();
        assertThat(hierarchy.getParentId(nodeId)).isEqualTo(parentId);
    }

    @Test
    public void insertNodeShouldIncrementParentsChildCount() {
        Optional<UUID> parentId = Optional.of(UUID.randomUUID());
        hierarchyService.createNewNode(new CreateNodeCommand(hierarchyId, 1L, parentId.get(), "parent name", "blue"));
        hierarchyService.createNewNode(new CreateNodeCommand(hierarchyId, 2L, nodeId, "node name", "blue"));
        hierarchyService.insertNode(new InsertNodeCommand(hierarchyId, 3L, parentId.get(), nodeId));

        Node node = nodeRepository.findById(parentId.get()).get();

        assertThat(node.getChildCount()).isEqualTo(1);
    }

    @Test
    public void insertNodeWithNonExistentHierarchyShouldThrowException() {
        thrown.expect(ObjectNotFoundException.class);
        thrown.expectMessage(containsString("Hierarchy"));
        hierarchyService.insertNode(new InsertNodeCommand(UUID.randomUUID(), 1L, nodeId, nodeId));
    }

    @Test
    public void insertNodeWithNonExistentParentShouldThrowException() {
        thrown.expect(ObjectNotFoundException.class);
        thrown.expectMessage(containsString("Node"));
        hierarchyService.insertNode(new InsertNodeCommand(hierarchyId, 1L, UUID.randomUUID(), nodeId));
    }

    @Test
    public void insertNodeWithNonExistentNodeShouldThrowException() {
        thrown.expect(ObjectNotFoundException.class);
        thrown.expectMessage(containsString("Node"));
        hierarchyService.insertNode(new InsertNodeCommand(hierarchyId, 1L, nodeId, UUID.randomUUID()));
    }

}