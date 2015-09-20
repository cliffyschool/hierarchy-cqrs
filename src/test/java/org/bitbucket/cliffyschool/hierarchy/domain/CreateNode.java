package org.bitbucket.cliffyschool.hierarchy.domain;

import org.bitbucket.cliffyschool.hierarchy.command.CreateNodeCommand;
import org.bitbucket.cliffyschool.hierarchy.event.HierarchyCreated;
import org.bitbucket.cliffyschool.hierarchy.event.NodeCreated;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

public class CreateNode {

    private Hierarchy hierarchy;
    private UUID nodeId;
    private CreateNodeCommand createNodeCommand;

    @Before
    public void setUp() {
        hierarchy = Hierarchy.apply(new HierarchyCreated(UUID.randomUUID()));
        nodeId = UUID.randomUUID();
        createNodeCommand = new CreateNodeCommand(nodeId, hierarchy.getVersionId(), nodeId.toString(), "blue");
    }

    @Test
    public void whenNodeCreatedThenNodeByIdShouldReturnIt() {
        hierarchy.apply(hierarchy.createNode(createNodeCommand).getEvents());

        assertThat(hierarchy.nodeById(nodeId)).isNotNull();
    }

    @Test
    public void whenNodeCreatedThenPropertiesShouldBeSet() {
        hierarchy.apply(hierarchy.createNode(createNodeCommand).getEvents());

        Node node = hierarchy.nodeById(nodeId);
        assertThat(node.getName()).isEqualTo(createNodeCommand.getNodeName());
        assertThat(node.getColor()).isEqualTo(createNodeCommand.getColor());
    }

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Test
    public void whenDuplicateNodeNameIsUsedThenCreateNodeShouldThrowAnException() {
        String nameOfExistingNode = "nameOfExistingNode";
        hierarchy.apply(new NodeCreated(hierarchy.getId(), 1L, UUID.randomUUID(), nameOfExistingNode, ""));

        thrown.expect(RuntimeException.class);
        thrown.expectMessage(containsString("exists"));

        hierarchy.createNode(new CreateNodeCommand(nodeId, 1L, nameOfExistingNode, "blue"));
    }

    @Test
    public void whenChildNodeCreatedThenChildCountShouldIncrement(){
        UUID childNodeId = UUID.randomUUID();
        hierarchy.apply(new NodeCreated(hierarchy.getId(), hierarchy.getVersionId(), nodeId, "parent", "blue"));

        hierarchy.apply(hierarchy.createNode(new CreateNodeCommand(childNodeId, hierarchy.getVersionId(), "child", "red", Optional.of(nodeId))).getEvents());

        Node parent = hierarchy.nodeById(nodeId);
        assertThat(parent.getChildCount()).isEqualTo(1);
    }
}

