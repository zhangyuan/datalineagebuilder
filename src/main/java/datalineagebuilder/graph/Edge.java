package datalineagebuilder.graph;

public class Edge {
    public Node getFrom() {
        return from;
    }

    public Node getTo() {
        return to;
    }

    private Node from;

    public Edge(Node from, Node to) {
        this.from = from;
        this.to = to;
    }

    private Node to;
}
