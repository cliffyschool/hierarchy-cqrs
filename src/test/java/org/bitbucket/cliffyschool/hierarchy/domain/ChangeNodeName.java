package org.bitbucket.cliffyschool.hierarchy.domain;

import org.bitbucket.cliffyschool.hierarchy.command.ChangeNodeNameCommand;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.EventStream;
import org.bitbucket.cliffyschool.hierarchy.event.NodeCreated;
import org.bitbucket.cliffyschool.hierarchy.event.NodeNameChanged;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

public class ChangeNodeName {

    private Hierarchy hierarchy;
    private UUID nodeId;

    @Before
    public void setUp() {
        hierarchy = new Hierarchy(UUID.randomUUID());
        nodeId = UUID.randomUUID();
    }

    @Test
    public void shouldReturnEvent(){
        UUID nodeId = UUID.randomUUID();
        hierarchy.apply(new NodeCreated(hierarchy.getId(), 1L, nodeId, "firstName", "blue", "circle"));
        String newName = "secondName";
        EventStream eventStream = hierarchy.changeNodeName(new ChangeNodeNameCommand(nodeId, 1L, newName));

        assertThat(eventStream.getEvents()).hasSize(1);
        assertThat(eventStream.getEvents().get(0)).isInstanceOf(NodeNameChanged.class);
        NodeNameChanged event = (NodeNameChanged) eventStream.getEvents().get(0);
        assertThat(event.getNodeId()).isEqualTo(nodeId);
        assertThat(event.getNewName()).isEqualTo(newName);
    }

    @Test
    public void whenNodeNameChangedThenNewNameIsSet(){
        String newName = "secondName";
        UUID nodeId = UUID.randomUUID();
        hierarchy.apply(new NodeCreated(hierarchy.getId(), 1L, nodeId, "firstName", "blue", "circle"));
        hierarchy.apply(new NodeNameChanged(hierarchy.getId(), 1L, nodeId, newName));

        assertThat(hierarchy.nodeById(nodeId).getName()).isEqualTo(newName);
    }
    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Test
    public void whenNodeWithSameNameAlreadyExistsThenExceptionShouldBeThrown() {
        String nameOfExistingNode = "nameOfExistingNode";
        hierarchy.apply(new NodeCreated(hierarchy.getId(), 1L, UUID.randomUUID(), nameOfExistingNode, "", ""));

        thrown.expect(RuntimeException.class);
        thrown.expectMessage(containsString("exists"));

        hierarchy.changeNodeName(new ChangeNodeNameCommand(nodeId, 1L, nameOfExistingNode));
    }

}

