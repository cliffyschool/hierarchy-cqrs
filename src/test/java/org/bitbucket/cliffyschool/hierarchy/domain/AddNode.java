package org.bitbucket.cliffyschool.hierarchy.domain;

import org.bitbucket.cliffyschool.hierarchy.command.CreateNodeCommand;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

public class AddNode {

    private Hierarchy hierarchy;
    private UUID nodeId;
    private CreateNodeCommand createNodeCommand;
    private Node node;

    @Before
    public void setUp() {
        hierarchy = new Hierarchy(UUID.randomUUID());
        nodeId = UUID.randomUUID();
        createNodeCommand = new CreateNodeCommand(nodeId, 0L, nodeId.toString(), "blue");
        node = new Node(nodeId, hierarchy.getId(), "node", "blue");
    }

    @Test
    public void whenNodeAddedThenNodeByIdShouldReturnIt() {
        hierarchy.addNode(Optional.empty(), node);

        assertThat(hierarchy.nodeById(nodeId)).isNotNull();
    }

    @Test
    public void whenNodeAddedThenCanRetrieveParentId(){
        hierarchy.addNode(Optional.empty(), node);
        Node child = new Node(UUID.randomUUID(), hierarchy.getId(), "child", "blue");

        hierarchy.addNode(Optional.of(node.getId()), child);
        Optional<UUID> parentId = hierarchy.getParentId(child.getId());

        assertThat(parentId).isEqualTo(Optional.of(node.getId()));
    }

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Test
    public void whenDuplicateNodeNameIsUsedThenAddNodeShouldThrowAnException() {
        hierarchy.addNode(Optional.empty(), node);
        String nameOfExistingNode = node.getName();

        thrown.expect(RuntimeException.class);
        thrown.expectMessage(containsString("exists"));

        hierarchy.addNode(Optional.empty(), new Node(UUID.randomUUID(), hierarchy.getId(), nameOfExistingNode, node.getColor()));
    }

    @Test
    public void whenChildNodeAddedThenChildCountShouldIncrement(){
        UUID childNodeId = UUID.randomUUID();
        hierarchy.addNode(Optional.empty(), node);

        hierarchy.addNode(Optional.of(nodeId), new Node(childNodeId, hierarchy.getId(), "child", "red"));

        Node parent = hierarchy.nodeById(nodeId);
        assertThat(parent.getChildCount()).isEqualTo(1);
    }
}

