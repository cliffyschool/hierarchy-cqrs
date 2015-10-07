package org.bitbucket.cliffyschool.hierarchy.infrastructure;

import java.util.UUID;

public interface IAggregateRoot {
    UUID getId();

    long getVersionId();

    EventStream getChangeEvents();

    IAggregateRoot withVersionId(long newVersionId);
}
