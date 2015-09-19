package org.bitbucket.cliffyschool.hierarchy.command;

import java.util.UUID;

public class CreateHierarchyCommand extends Command {
    private UUID hierarchyId;
    private String name;

    public CreateHierarchyCommand(UUID hierarchyId, long baseVersionId, String name) {
        super(baseVersionId);
        this.hierarchyId = hierarchyId;
        this.name = name;
    }

    public UUID getHierarchyId() {
        return hierarchyId;
    }

    public String getName() {
        return name;
    }
}
