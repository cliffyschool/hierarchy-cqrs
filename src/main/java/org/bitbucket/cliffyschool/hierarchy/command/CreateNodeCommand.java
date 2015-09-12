package org.bitbucket.cliffyschool.hierarchy.command;

public class CreateNodeCommand implements Command {
    private final String shape;
    private final String color;
    private String nodeName;

    public CreateNodeCommand(String nodeName, String color, String shape) {
        this.nodeName = nodeName;
        this.color = color;
        this.shape = shape;
    }

    public String getColor() {
        return color;
    }

    public String getShape() {
        return shape;
    }

    public String getNodeName() {
        return nodeName;
    }
}
