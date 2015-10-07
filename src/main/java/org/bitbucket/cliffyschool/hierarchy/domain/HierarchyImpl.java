package org.bitbucket.cliffyschool.hierarchy.domain;

import com.google.common.collect.*;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import org.apache.commons.lang3.StringUtils;
import org.bitbucket.cliffyschool.hierarchy.application.exception.NameAlreadyUsedException;
import org.bitbucket.cliffyschool.hierarchy.command.ChangeNodeNameCommand;
import org.bitbucket.cliffyschool.hierarchy.event.HierarchyCreated;
import org.bitbucket.cliffyschool.hierarchy.event.NodeInserted;
import org.bitbucket.cliffyschool.hierarchy.event.NodeNameChanged;
import org.bitbucket.cliffyschool.hierarchy.event.NodePathChanged;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.BaseAggregateRoot;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.IAggregateRoot;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.SupportsPof;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class HierarchyImpl extends BaseAggregateRoot implements Hierarchy, SupportsPof {

    Map<UUID, UUID> nodesById = Maps.newHashMap();
    Map<String, UUID> nodesByName = Maps.newHashMap();
    ListMultimap<UUID, UUID> childrenByParentId = ArrayListMultimap.create();
    // example derived property?
    Map<UUID, String> nodePathsByNodeId = Maps.newHashMap();

    public HierarchyImpl(UUID id) {
        super(id);
    }

    @Override
    public Hierarchy withVersionId(long newVersionId) {
        HierarchyImpl ret = new HierarchyImpl(id);
        ret.nodesById = Maps.newHashMap(nodesById);
        ret.nodesByName = Maps.newHashMap(nodesByName);
        ret.childrenByParentId = ArrayListMultimap.create(childrenByParentId);
        ret.nodePathsByNodeId = Maps.newHashMap(nodePathsByNodeId);
        ret.versionId = newVersionId;
        return ret;
    }

    public HierarchyImpl(HierarchyImpl copyFrom, long newVersionId){
        this(copyFrom.id);
        this.nodesById = Maps.newHashMap(copyFrom.nodesById);
        this.nodesByName = Maps.newHashMap(copyFrom.nodesByName);
        this.childrenByParentId = ArrayListMultimap.create(copyFrom.childrenByParentId);
        this.nodePathsByNodeId = Maps.newHashMap(copyFrom.nodePathsByNodeId);
        this.versionId = newVersionId;
    }

    public static HierarchyImpl createHierarchy(UUID id) {
        HierarchyImpl hierarchy = new HierarchyImpl(id);
        hierarchy.changeEvents.append(new HierarchyCreated(id));
        return hierarchy;
    }

    @Override
    public boolean containsNode(UUID nodeId) {
        return nodesById.containsKey(nodeId);
    }

    public String nodePath(UUID nodeId){
        return nodePathsByNodeId.get(nodeId);
    }

    @Override
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
        changeEvents.append(new NodeInserted(id, parentNode.map(IAggregateRoot::getId), node.getId()));

        calculateDerivedValues(node.getId());
    }

    @Override
    public Optional<UUID> getParentId(UUID nodeId) {
        // TODO: replace with parent map
        return childrenByParentId.entries().stream()
                .filter(entry -> entry.getValue().equals(nodeId))
                .findFirst()
                .map(Map.Entry::getKey);
    }

    @Override
    public void changeNodeName(ChangeNodeNameCommand changeNodeNameCmd, NodeImpl node) {
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

        Optional<UUID> parentId = ancestorIds.stream().findFirst();
        String nodePath =  StringUtils.join(Lists.reverse(ancestorIds), "->");
        nodePathsByNodeId.put(nodeId, nodePath);
        changeEvents.append(new NodePathChanged(id, parentId, nodeId, nodePath));
    }

    private void collectAncestorIds(UUID nodeId, List<UUID> ancestorIds) {
        Optional<UUID> parentId = getParentId(nodeId);
        if (!parentId.isPresent())
            return;
        ancestorIds.add(parentId.get());
        collectAncestorIds(parentId.get(), ancestorIds);
    }

    @Override
    public Object asPof() {
        return new PofHierarchy(id, versionId, nodesById, nodesByName, childrenByParentId, nodePathsByNodeId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HierarchyImpl hierarchy = (HierarchyImpl) o;

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
