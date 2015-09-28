package org.bitbucket.cliffyschool.hierarchy.command;

public abstract class HierarchyCommand extends Command {
    protected long lastHierarchyVersionLoaded;

    public HierarchyCommand(long lastHierarchyVersionLoaded) {
        this.lastHierarchyVersionLoaded = lastHierarchyVersionLoaded;
    }

    public long getLastHierarchyVersionLoaded() {
        return lastHierarchyVersionLoaded;
    }
}
