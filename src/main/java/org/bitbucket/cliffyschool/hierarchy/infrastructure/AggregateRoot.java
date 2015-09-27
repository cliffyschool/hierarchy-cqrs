package org.bitbucket.cliffyschool.hierarchy.infrastructure;

import com.google.common.collect.Lists;
import org.bitbucket.cliffyschool.hierarchy.event.Event;

import java.util.UUID;

public abstract class AggregateRoot {
    protected UUID id;
    protected long versionId;
    protected EventStream changeEvents;

    public AggregateRoot(UUID id){
        this.id = id;
        changeEvents = EventStream.from(Lists.newArrayList());
    }

    public UUID getId() {
        return id;
    }

    public long getVersionId() {
        return versionId;
    }

    public EventStream getChangeEvents(){
        return changeEvents;
    }

    public abstract Event creationEvent();

}