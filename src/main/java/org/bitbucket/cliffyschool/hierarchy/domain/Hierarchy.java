package org.bitbucket.cliffyschool.hierarchy.domain;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bitbucket.cliffyschool.hierarchy.event.Event;
import org.bitbucket.cliffyschool.hierarchy.event.NodeCreated;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Hierarchy {

    private Map<String, Node> nodesByName = Maps.newHashMap();
    private UUID id;

    public Hierarchy(UUID id) {
        this.id = id;
    }

    public List<Event> createNewNode(String nodeName) {
        Node node = new Node(nodeName);
        nodesByName.put(nodeName, node);
        return Lists.newArrayList(new NodeCreated(id, nodeName, "blue", "circle"));
    }

    public boolean hasNodeWithName(String nodeName) {
        return nodesByName.containsKey(nodeName);
    }

    public UUID getId() {
        return id;
    }
}
