package org.bitbucket.cliffyschool.hierarchy.infrastructure;

import java.util.List;

public class FakeBus {

    private List<ProjectionHandler> projectionHandlers;

    public FakeBus(List<ProjectionHandler> projectionHandlers){
        this.projectionHandlers = projectionHandlers;
    }

    public void publish(EventStream stream){
        projectionHandlers.forEach(handler -> handler.write(stream));

    }
}
