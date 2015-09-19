package org.bitbucket.cliffyschool.hierarchy.command;

public abstract class Command {
    private long baseVersionId;

    public Command(long baseVersionId) {
        this.baseVersionId = baseVersionId;
    }

    public long getBaseVersionId() {
        return baseVersionId;
    }
}
