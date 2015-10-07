package org.bitbucket.cliffyschool.hierarchy.domain.repository;

import org.bitbucket.cliffyschool.hierarchy.domain.Node;
import org.bitbucket.cliffyschool.hierarchy.domain.NodeImpl;
import org.bitbucket.cliffyschool.hierarchy.domain.NodeRepository;
import org.bitbucket.cliffyschool.hierarchy.domain.PofNode;

import java.util.UUID;

public class CoherenceNodeRepository extends CoherenceRepository<UUID,Node> implements NodeRepository {

    public CoherenceNodeRepository(){
        super("nodeCache", NodeImpl.class);
    }

    @Override
    void put(UUID key, Node value) {
        PofNode dto = (PofNode)((NodeImpl)value).asPof();
        cache.put(key, dto);
    }

    @Override
    Node get(UUID key) {
        PofNode pof = (PofNode)cache.get(key);
        return pof == null ? null : pof.asNode();
    }
}
