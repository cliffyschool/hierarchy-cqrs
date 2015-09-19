package org.bitbucket.cliffyschool.hierarchy.event;

import org.bitbucket.cliffyschool.hierarchy.command.Command;

import java.util.UUID;

public class CreateHierarchyCommand extends Command {
    private UUID hierarchyId;
    private String name;

    public CreateHierarchyCommand(UUID hierarchyId, String name, long originalVersionId) {
        super(originalVersionId);
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
