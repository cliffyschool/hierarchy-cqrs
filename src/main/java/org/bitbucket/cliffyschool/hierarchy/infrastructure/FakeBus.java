package org.bitbucket.cliffyschool.hierarchy.infrastructure;

import org.bitbucket.cliffyschool.hierarchy.application.projection.HierarchyAsGridProjectionUpdater;
import org.bitbucket.cliffyschool.hierarchy.event.Event;

import java.util.List;

public class FakeBus {

    private HierarchyAsGridProjectionUpdater gridUpdater;

    public FakeBus(HierarchyAsGridProjectionUpdater gridUpdater){
        this.gridUpdater = gridUpdater;
    }

    public void publish(EventStream stream){
        gridUpdater.write(stream);
    }
}
