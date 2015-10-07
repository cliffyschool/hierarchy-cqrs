package org.bitbucket.cliffyschool.hierarchy.domain;

import org.bitbucket.cliffyschool.hierarchy.command.ChangeNodeNameCommand;
import org.bitbucket.cliffyschool.hierarchy.domain.node.NodeImpl;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.IAggregateRoot;

import java.util.Optional;
import java.util.UUID;

public interface Hierarchy extends IAggregateRoot{
    boolean containsNode(UUID nodeId);

    void insertNode(Optional<Node> parentNode, Node node);

    Optional<UUID> getParentId(UUID nodeId);

    void changeNodeName(ChangeNodeNameCommand changeNodeNameCmd, NodeImpl node);
}
