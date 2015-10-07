package org.bitbucket.cliffyschool.hierarchy.domain;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateNode {

    private UUID nodeId;
    private UUID hierarchyId;

    @Before
    public void setUp(){
        nodeId = UUID.randomUUID();
        hierarchyId = UUID.randomUUID();
    }

    @Test
    public void whenNodeCreatedThenPropertiesShouldBeSet() {
        Node node = new NodeImpl(nodeId, hierarchyId, "node1", "blue");

        assertThat(node.getName()).isEqualTo("node1");
        assertThat(node.getColor()).isEqualTo("blue");
    }
}
