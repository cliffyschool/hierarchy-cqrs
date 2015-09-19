package org.bitbucket.cliffyschool.hierarchy.domain;

import org.bitbucket.cliffyschool.hierarchy.command.CreateNodeCommand;
import org.bitbucket.cliffyschool.hierarchy.event.HierarchyCreated;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.EventStream;
import org.bitbucket.cliffyschool.hierarchy.event.NodeCreated;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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
        createNodeCommand = new CreateNodeCommand(nodeId, hierarchy.getVersionId(), "blue", "circle", nodeId.toString());
    }

    @Test
    public void shouldReturnEvent() {
        EventStream eventStream = hierarchy.createNode(new CreateNodeCommand(nodeId, 1L, "blue", "circle", "myNode"));

        assertThat(eventStream.getEvents()).hasSize(1);
        assertThat(eventStream.getEvents().get(0)).isInstanceOf(NodeCreated.class);
        NodeCreated event = (NodeCreated) eventStream.getEvents().get(0);
        assertThat(event.getNodeId()).isEqualTo(nodeId);
        assertThat(event.getNodeName()).isEqualTo("myNode");
        assertThat(event.getNodeColor()).isEqualTo("blue");
        assertThat(event.getNodeShape()).isEqualTo("circle");
    }

    @Test
    public void whenNodeCreatedThenNodeByIdShouldReturnIt() {
    }

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Test
    public void whenNodeWithSameNameAlreadyExistsThenExceptionShouldBeThrown() {
        String nameOfExistingNode = "nameOfExistingNode";
        hierarchy.apply(new NodeCreated(hierarchy.getId(), 1L, UUID.randomUUID(), nameOfExistingNode, "", ""));

        thrown.expect(RuntimeException.class);
        thrown.expectMessage(containsString("exists"));

        hierarchy.createNode(new CreateNodeCommand(nodeId, 1L, "blue", "circle", nameOfExistingNode));
    }

    @Test
    public void whenNodeCreatedThenItCanBeLookedUpById() {
        UUID nodeId = UUID.randomUUID();
        hierarchy.apply(new NodeCreated(hierarchy.getId(), 1L, nodeId, "bob", "blue", "circle"));

        assertThat(hierarchy.nodeById(nodeId)).isNotNull();
    }
}

