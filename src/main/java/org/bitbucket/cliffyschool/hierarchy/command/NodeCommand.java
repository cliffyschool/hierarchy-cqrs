package org.bitbucket.cliffyschool.hierarchy.command;

public abstract class NodeCommand {
    private long lastNodeVersionLoaded;

    public NodeCommand(long lastNodeVersionLoaded) {
        this.lastNodeVersionLoaded = lastNodeVersionLoaded;
    }

    public long getLastNodeVersionLoaded() {
        return lastNodeVersionLoaded;
    }
}
