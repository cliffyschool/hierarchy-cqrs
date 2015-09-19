package org.bitbucket.cliffyschool.hierarchy.command;

public abstract class Command {
    private long originalVersionId;

    public Command(long originalVersionId) {
        this.originalVersionId = originalVersionId;
    }

    public long getOriginalVersionId() {
        return originalVersionId;
    }
}
