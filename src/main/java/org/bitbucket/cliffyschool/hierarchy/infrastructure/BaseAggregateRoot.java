package org.bitbucket.cliffyschool.hierarchy.infrastructure;

import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.UUID;

public abstract class BaseAggregateRoot implements Serializable, IAggregateRoot {
    protected UUID id;
    protected long versionId;
    protected transient EventStream changeEvents;

    public BaseAggregateRoot(){
        this(null);
    }

    public BaseAggregateRoot(UUID id){
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

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public long getVersionId() {
        return versionId;
    }

    @Override
    public EventStream getChangeEvents(){
        return changeEvents;
    }

}
