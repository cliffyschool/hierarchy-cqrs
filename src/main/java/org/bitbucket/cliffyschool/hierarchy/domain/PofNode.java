package org.bitbucket.cliffyschool.hierarchy.domain;

import com.tangosol.io.pof.annotation.Portable;
import com.tangosol.io.pof.annotation.PortableProperty;

import java.util.UUID;

@Portable
public class PofNode  {

    @PortableProperty(0)
    private UUID id;
    @PortableProperty(1)
    private long versionId;
    @PortableProperty(2)
    private UUID hierarchyId;
    @PortableProperty(3)
    private String name;
    @PortableProperty(4)
    private String color;

    public PofNode() {
    }

    public PofNode(UUID id, long versionId, UUID hierarchyId, String name, String color) {
        this.id = id;
        this.versionId = versionId;
        this.hierarchyId = hierarchyId;
        this.name = name;
        this.color = color;
    }

    public UUID getId() {
        return id;
    }

    public UUID getHierarchyId() {
        return hierarchyId;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Node asNode() {
        NodeImpl ret = new NodeImpl(id, hierarchyId, name, color);
        return ret.withVersionId(versionId);
    }
}
