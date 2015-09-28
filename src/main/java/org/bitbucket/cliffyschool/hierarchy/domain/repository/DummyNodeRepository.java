package org.bitbucket.cliffyschool.hierarchy.domain.repository;

import org.bitbucket.cliffyschool.hierarchy.domain.Node;
import org.bitbucket.cliffyschool.hierarchy.domain.NodeRepository;
import org.bitbucket.cliffyschool.hierarchy.infrastructure.DummyRepository;

import java.util.UUID;

public class DummyNodeRepository extends DummyRepository<UUID,Node> implements NodeRepository{
}
