package org.bitbucket.cliffyschool.hierarchy.domain;

import org.bitbucket.cliffyschool.hierarchy.domain.hierarchy.HierarchyImpl;
import org.bitbucket.cliffyschool.hierarchy.domain.node.NodeImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

public class InsertNode {

    private HierarchyImpl hierarchy;
    private UUID nodeId;
    private Node node;

    @Before
    public void setUp() {
        hierarchy = new HierarchyImpl(UUID.randomUUID());
        nodeId = UUID.randomUUID();
        node = new NodeImpl(nodeId, hierarchy.getId(), "node", "blue");
    }

    @Test
    public void whenNodeAddedThenNodeByIdShouldReturnIt() {
        hierarchy.insertNode(Optional.empty(), node);

        assertThat(hierarchy.containsNode(nodeId)).isNotNull();
    }

    @Test
    public void whenNodeAddedThenCanRetrieveParentId(){
        hierarchy.insertNode(Optional.empty(), node);
        Node child = new NodeImpl(UUID.randomUUID(), hierarchy.getId(), "child", "blue");

        hierarchy.insertNode(Optional.of(node), child);
        Optional<UUID> parentId = hierarchy.getParentId(child.getId());

        assertThat(parentId).isEqualTo(Optional.of(node.getId()));
    }

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Test
    public void whenDuplicateNodeNameIsUsedThenAddNodeShouldThrowAnException() {
        hierarchy.insertNode(Optional.empty(), node);
        String nameOfExistingNode = node.getName();

        thrown.expect(RuntimeException.class);
        thrown.expectMessage(containsString("exists"));

        hierarchy.insertNode(Optional.empty(), new NodeImpl(UUID.randomUUID(), hierarchy.getId(), nameOfExistingNode, node.getColor()));
    }

    @Test
    public void whenChildNodeAddedThenChildCountShouldIncrement(){
        UUID childNodeId = UUID.randomUUID();
        hierarchy.insertNode(Optional.empty(), node);

        hierarchy.insertNode(Optional.of(node), new NodeImpl(childNodeId, hierarchy.getId(), "child", "red"));

        assertThat(hierarchy.nodePath(childNodeId)).isEqualTo(node.getId().toString());
    }
}

