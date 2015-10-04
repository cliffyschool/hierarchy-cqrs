package org.bitbucket.cliffyschool.hierarchy.domain.repository;

import com.tangosol.net.NamedCache;
import org.bitbucket.cliffyschool.hierarchy.domain.Node;
import org.bitbucket.cliffyschool.hierarchy.domain.NodeRepository;

import java.util.UUID;

public class CoherenceNodeRepository extends CoherenceRepository<UUID,Node> implements NodeRepository {
    private NamedCache cache;

    public CoherenceNodeRepository(){
        super("nodeCache", Node.class);
    }
}
