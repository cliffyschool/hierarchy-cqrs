package org.bitbucket.cliffyschool.hierarchy.domain;

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ChangeNodePropertyValue {

    private UUID nodeId;
    private UUID hierarchyId;
    private Hierarchy hierarchy;
    private Node node;

    @Before
    public void setUp(){
        nodeId = UUID.randomUUID();
        hierarchyId = UUID.randomUUID();
        node = new Node(nodeId, hierarchyId, "name", "blue");
    }

    @Test
    public void whenNodePropertyValueChangedThenNewValueIsSet(){
       node.changePropertyValue("color", "red");

        assertThat(node.getColor()).isEqualTo("red");
    }
}
