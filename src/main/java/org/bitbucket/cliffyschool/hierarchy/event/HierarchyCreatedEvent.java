package org.bitbucket.cliffyschool.hierarchy.event;

import java.util.Iterator;
import java.util.UUID;

public class HierarchyCreatedEvent extends Event {
    public HierarchyCreatedEvent(UUID id) {
        super(id);
    }
}
