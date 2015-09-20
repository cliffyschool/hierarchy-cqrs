package org.bitbucket.cliffyschool.hierarchy.domain;

import org.bitbucket.cliffyschool.hierarchy.command.ChangeNodeNameCommand;
import org.bitbucket.cliffyschool.hierarchy.command.CreateNodeCommand;
import org.bitbucket.cliffyschool.hierarchy.event.HierarchyCreated;
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
    private ChangeNodeNameCommand changeNodeNameCommand;

    @Before
    public void setUp() {
        hierarchy = Hierarchy.apply(new HierarchyCreated(UUID.randomUUID()));
        nodeId = UUID.randomUUID();
        hierarchy.apply(new NodeCreated(hierarchy.getId(), nodeId, "originalNodeName", ""));
        changeNodeNameCommand = new ChangeNodeNameCommand(nodeId, hierarchy.getVersionId(), "newName");
    }

    @Test
    public void whenNodeNameChangedThenNewNameIsSet(){
        hierarchy.apply(hierarchy.changeNodeName(changeNodeNameCommand).getEvents());

        assertThat(hierarchy.nodeById(nodeId).getName()).isEqualTo(changeNodeNameCommand.getNewName());
    }

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Test
    public void whenNodeWithSameNameAlreadyExistsThenExceptionShouldBeThrown() {
        String nameOfExistingNode = "nameOfExistingNode";
        hierarchy.apply(new NodeCreated(hierarchy.getId(), UUID.randomUUID(), nameOfExistingNode, ""));

        thrown.expect(RuntimeException.class);
        thrown.expectMessage(containsString("exists"));

        hierarchy.changeNodeName(new ChangeNodeNameCommand(nodeId, 1L, nameOfExistingNode));
    }

}

