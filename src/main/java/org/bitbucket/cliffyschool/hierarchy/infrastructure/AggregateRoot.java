package org.bitbucket.cliffyschool.hierarchy.infrastructure;

import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

public abstract class AggregateRoot implements Serializable {
    protected UUID id;
    protected long versionId;
    protected transient EventStream changeEvents;

    public AggregateRoot(){
        this(null);
    }

    public AggregateRoot(UUID id){
        this.id = id;
        changeEvents = EventStream.from(Lists.newArrayList());
    }

    // used by Java serialization. get rid of this when moving to POF
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        changeEvents = EventStream.from(Lists.newArrayList());
    }

    public void setId(UUID id){
        this.id = id;
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

    public abstract AggregateRoot withVersionId(long newVersionId);
}
