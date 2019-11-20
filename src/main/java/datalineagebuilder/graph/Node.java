package datalineagebuilder.graph;

import java.util.Objects;

public class Node {
    private String name;

    public String getName() {
        return name;
    }

    public String label() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    String type;

    public Node(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public Node(String name) {
        this.name = name;
        this.type = "TABLE";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        Node node = (Node) o;
        return Objects.equals(getName(), node.getName()) &&
                Objects.equals(getType(), node.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getType());
    }
}
