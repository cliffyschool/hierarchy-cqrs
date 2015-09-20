package org.bitbucket.cliffyschool.hierarchy.command;

import java.util.UUID;

public class CreateHierarchyCommand extends Command {
    private UUID hierarchyId;
    private String name;
    private long baseVersionId;

    public CreateHierarchyCommand(UUID hierarchyId, String name) {
        this.hierarchyId = hierarchyId;
        this.name = name;
    }

    public UUID getHierarchyId() {
        return hierarchyId;
    }

    public String getName() {
        return name;
    }

    public long getBaseVersionId() {
        return baseVersionId;
    }
}
