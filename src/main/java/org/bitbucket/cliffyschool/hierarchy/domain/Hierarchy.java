package org.bitbucket.cliffyschool.hierarchy.domain;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tangosol.io.pof.annotation.Portable;
import com.tangosol.io.pof.annotation.PortableProperty;
import org.apache.commons.lang3.StringUtils;
import org.bitbucket.cliffyschool.hierarchy.application.exception.NameAlreadyUsedException;
import org.bitbucket.cliffyschool.hierarchy.command.ChangeNodeNameCommand;
import org.bitbucket.cliffyschool.hierarchy.event.HierarchyCreated;
import org.bitbucket.cliffyschool.hierarchy.event.NodeInserted;
import org.bitbucket.cliffyschool.hierarchy.event.NodeNameChanged;
import org.bitbucket.cliffyschool.hierarchy.event.NodePathChanged;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.AggregateRoot;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Hierarchy extends AggregateRoot implements Serializable {

    private Map<UUID, UUID> nodesById = Maps.newHashMap();
    private Map<String, UUID> nodesByName = Maps.newHashMap();
    private ListMultimap<UUID, UUID> childrenByParentId = ArrayListMultimap.create();
    // example derived property?
    private Map<UUID, String> nodePathsByNodeId = Maps.newHashMap();

    public Hierarchy(){
        super(null);
    }

    public Hierarchy(UUID id) {
        super(id);
    }

    public static Hierarchy createHierarchy(UUID id) {
        Hierarchy hierarchy = new Hierarchy(id);
        hierarchy.changeEvents.append(new HierarchyCreated(id));
        return hierarchy;
    }

    public boolean containsNode(UUID nodeId) {
        return nodesById.containsKey(nodeId);
    }

    public String nodePath(UUID nodeId){
        return nodePathsByNodeId.get(nodeId);
    }

    public void insertNode(Optional<Node> parentNode, Node node) {
        if (node == null)
            throw new NullPointerException("node");
        if (nodesByName.containsKey(node.getName()) && !nodesByName.get(node.getName()).equals(node.getId()))
            throw new NameAlreadyUsedException("Node", node.getName());

        nodesById.put(node.getId(), node.getId());
        nodesByName.put(node.getName(), node.getId());
        parentNode.ifPresent(pNode -> {
            childrenByParentId.put(pNode.getId(), node.getId());
        });
        changeEvents.append(new NodeInserted(id, parentNode.map(AggregateRoot::getId), node.getId()));

        calculateDerivedValues(node.getId());
    }

    public Optional<UUID> getParentId(UUID nodeId) {
        // TODO: replace with parent map
        return childrenByParentId.entries().stream()
                .filter(entry -> entry.getValue().equals(nodeId))
                .findFirst()
                .map(Map.Entry::getKey);
    }

    public void changeNodeName(ChangeNodeNameCommand changeNodeNameCmd, Node node) {
        if (nodesByName.containsKey(changeNodeNameCmd.getNewName()))
            throw new NameAlreadyUsedException("Node", changeNodeNameCmd.getNewName());

        String oldName= node.getName();
        node.changeNodeName(changeNodeNameCmd);
        nodesByName.remove(oldName);
        nodesByName.put(node.getName(), node.getId());

        Optional<UUID> parentId = getParentId(node.getId());
        changeEvents.append(new NodeNameChanged(id, node.getId(), parentId, changeNodeNameCmd.getNewName()));
    }

    private void calculateDerivedValues(UUID nodeId) {
        List<UUID> ancestorIds = Lists.newArrayList();
        collectAncestorIds(nodeId, ancestorIds);

        String nodePath =  StringUtils.join(Lists.reverse(ancestorIds), "->");
        nodePathsByNodeId.put(nodeId, nodePath);
        changeEvents.append(new NodePathChanged(id, nodeId, nodePath));
    }

    private void collectAncestorIds(UUID nodeId, List<UUID> ancestorIds) {
        Optional<UUID> parentId = getParentId(nodeId);
        if (!parentId.isPresent())
            return;
        ancestorIds.add(parentId.get());
        collectAncestorIds(parentId.get(), ancestorIds);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hierarchy hierarchy = (Hierarchy) o;

        if (!nodesById.equals(hierarchy.nodesById)) return false;
        if (!nodesByName.equals(hierarchy.nodesByName)) return false;
        return childrenByParentId.equals(hierarchy.childrenByParentId);

    }

    @Override
    public int hashCode() {
        int result = nodesById.hashCode();
        result = 31 * result + nodesByName.hashCode();
        result = 31 * result + childrenByParentId.hashCode();
        return result;
    }
}
