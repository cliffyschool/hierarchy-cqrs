package org.bitbucket.cliffyschool.hierarchy.application.projection;

public class NodeDto {
    private String name;
    private String color;
    private String shape;

    public NodeDto(String name, String color, String shape) {
        this.name = name;
        this.color = color;
        this.shape = shape;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public String getShape() {
        return shape;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }
}
