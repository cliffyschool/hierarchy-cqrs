package org.bitbucket.cliffyschool.hierarchy.domain;

import org.bitbucket.cliffyschool.hierarchy.domain.Hierarchy;
import org.bitbucket.cliffyschool.hierarchy.event.Event;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateNode {

    private Hierarchy hier = new Hierarchy(UUID.randomUUID());

    @Test
    public void createNodeShouldGenerateAnEvent(){
        List<Event> events = hier.createNewNode("myNode");

        assertThat(events)
                .hasSize(1)
                .extracting("nodeName")
                .contains("myNode");
    }

    @Test
    public void whenNodeIsCreatedThenHierarchyShouldContainIt(){
        hier.createNewNode("myNode");

        assertThat(hier.hasNodeWithName("myNode")).isTrue();
    }
}

