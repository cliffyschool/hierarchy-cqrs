package org.bitbucket.cliffyschool.hierarchy.domain.hierarchy;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import org.bitbucket.cliffyschool.hierarchy.domain.Hierarchy;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class PofHierarchy implements PortableObject {
    private UUID id;
    private long versionId;
    private Map<UUID, UUID> nodesById;
    private Map<String, UUID> nodesByName;
    private ListMultimap<UUID, UUID> childrenByParentId;
    private Map<UUID, String> nodePathsByNodeId;

    public PofHierarchy() {
        nodesById = Maps.newHashMap();
        nodesByName = Maps.newHashMap();
        childrenByParentId = ArrayListMultimap.create();
        nodePathsByNodeId = Maps.newHashMap();
    }

    public PofHierarchy(UUID id, long versionId, Map<UUID, UUID> nodesById, Map<String, UUID> nodesByName,
                        ListMultimap<UUID, UUID> childrenByParentId, Map<UUID, String> nodePathsByNodeId) {
        this.id = id;
        this.versionId = versionId;
        this.nodesById = nodesById;
        this.nodesByName = nodesByName;
        this.childrenByParentId = childrenByParentId;
        this.nodePathsByNodeId = nodePathsByNodeId;
    }

    @Override
    public void readExternal(PofReader pofReader) throws IOException {
        id = UUID.fromString(pofReader.readString(0));
        versionId = pofReader.readLong(1);
        nodesById = pofReader.readMap(2, Maps.newHashMap());
        nodesByName= pofReader.readMap(3, Maps.newHashMap());

        Map<UUID, Collection<UUID>> multiMap = pofReader.readMap(4, Maps.newHashMap());
        childrenByParentId = ArrayListMultimap.create(multiMap.size(), 10);
        multiMap.entrySet().forEach(e -> childrenByParentId.putAll(e.getKey(), e.getValue()));

        nodePathsByNodeId = pofReader.readMap(5, Maps.newHashMap());
    }

    @Override
    public void writeExternal(PofWriter pofWriter) throws IOException {
        pofWriter.writeString(0, id.toString());
        pofWriter.writeLong(1, versionId);
        pofWriter.writeMap(2, nodesById);
        pofWriter.writeMap(3, nodesByName);
        pofWriter.writeMap(4, childrenByParentId.asMap());
        pofWriter.writeMap(5, nodePathsByNodeId);
    }

    public Hierarchy asHierarchy(){
        HierarchyImpl ret = new HierarchyImpl(id);
        ret.nodesById = nodesById;
        ret.nodesByName = nodesByName;
        ret.nodePathsByNodeId = nodePathsByNodeId;
        ret.childrenByParentId = childrenByParentId;
        return ret.withVersionId(versionId);
    }
}
