package org.bitbucket.cliffyschool.hierarchy.application.projection;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.UUID;

public class Node extends FlatNode {

    private List<Node> children;

    public Node(UUID nodeId, String name, String color) {
        super(nodeId, name, color);
        children = Lists.newArrayList();
    }

    public List<Node> getChildren(){
        return children;
    }
}
