package org.bitbucket.cliffyschool.hierarchy.event;

import java.util.UUID;

public class NodePropertyValueChanged<T> extends Event {

    private UUID nodeId;
    private String propertyName;
    private T oldValue;
    private T newValue;

    public NodePropertyValueChanged(UUID hierarchyId, UUID nodeId, String propertyName, T newValue, T oldValue)
    {
        super(hierarchyId);
        this.nodeId = nodeId;
        this.propertyName = propertyName;
        this.newValue = newValue;
        this.oldValue = oldValue;
    }

    public UUID getNodeId() {
        return nodeId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public T getOldValue() {
        return oldValue;
    }

    public T getNewValue() {
        return newValue;
    }

    @Override
    public Event withVersionId(long newVersionId) {
        return null;
    }
}
