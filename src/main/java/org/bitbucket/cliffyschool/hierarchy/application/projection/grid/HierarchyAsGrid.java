package org.bitbucket.cliffyschool.hierarchy.application.projection.grid;

import java.util.List;
import java.util.UUID;

public class HierarchyAsGrid {
    private UUID id;
    private List<NodeAsRow> rows;

    public HierarchyAsGrid(UUID id, List<NodeAsRow> rows)
    {
        this.id = id;
        this.rows = rows;
    }

    public UUID getId() {
        return id;
    }

    public List<NodeAsRow> getRows() {
        return rows;
    }

    public void setRows(List<NodeAsRow> rows) {
        this.rows = rows;
    }
}
