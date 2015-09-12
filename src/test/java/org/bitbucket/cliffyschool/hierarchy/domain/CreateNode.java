package org.bitbucket.cliffyschool.hierarchy.domain;

import org.bitbucket.cliffyschool.hierarchy.command.ChangeNodeNameCommand;
import org.bitbucket.cliffyschool.hierarchy.command.CreateNodeCommand;
import org.bitbucket.cliffyschool.hierarchy.event.Event;
import org.bitbucket.cliffyschool.hierarchy.event.NodeCreated;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateNode {

    private Hierarchy hier = new Hierarchy(UUID.randomUUID());

    @Test
    public void createNodeShouldCreateEvent(){
        List<Event> events = hier.apply(new CreateNodeCommand("myNode", "blue", "circle"));

        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isInstanceOf(NodeCreated.class);
        NodeCreated event = (NodeCreated)events.get(0);
        assertThat(event.getNodeId()).isNotNull();
        assertThat(event.getNodeName()).isEqualTo("myNode");
        assertThat(event.getNodeColor()).isEqualTo("blue");
        assertThat(event.getNodeShape()).isEqualTo("circle");
    }

    @Test
    public void whenNodeIsCreatedThenHierarchyShouldContainIt(){
        NodeCreated nc = (NodeCreated)hier.apply(new CreateNodeCommand("myNode", "blue", "circle"))
                .get(0);

        assertThat(hier.nodeById(nc.getNodeId())).isNotNull();
    }

    @Test
    public void changeNodeName(){
        NodeCreated event = (NodeCreated)hier.apply(new CreateNodeCommand("myNode", "", "")).get(0);
        String newName = "secondName";
        hier.apply(new ChangeNodeNameCommand(event.getNodeId(),  newName));

        assertThat(hier.nodeById(event.getNodeId()).getName()).isEqualTo( newName);
    }
}

