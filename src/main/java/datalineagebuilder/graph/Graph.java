package datalineagebuilder.graph;

import datalineagebuilder.databaseobject.ViewOrTableDefinition;
import datalineagebuilder.query.SQLServerViewDefinitionQuery;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Graph {
    public static Graph build(JdbcTemplate jdbcTemplate, List<String> objectNames) {
        Graph graph = new Graph();
        graph.buildRecursively(jdbcTemplate, objectNames);
        return graph;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    private List<Node> nodes = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();

    List<String> getNodeNames() {
        return getEdges()
                .stream()
                .map(x -> x.getFrom().getName())
                .distinct()
                .collect(Collectors.toList());
    }

    boolean addEdgeIfNotExists(Node fromNode, Node toNode) {
        for (Edge edge : getEdges()) {
            if (edge.getFrom().getName().equals(fromNode.getName()) && edge.getTo().getName().equals(toNode.getName())) {
                return false;
            }
        }
        getEdges().add(new Edge(fromNode, toNode));
        return true;
    }

    void addOrUpdate(Node newNode) {
        for (int i = 0; i < getNodes().size(); i ++) {
            Node node = getNodes().get(i);
            if (node.getName().equals(newNode.getName())) {
                getNodes().set(i, newNode);
                return;
            }
        }

        getNodes().add(newNode);
    }

    private Node addOrUpdate(String nodeName) {
        for (int i = 0; i < getNodes().size(); i ++) {
            Node node = getNodes().get(i);
            if (node.getName().equals(nodeName)) {
                return node;
            }
        }

        Node node = new Node(nodeName);
        getNodes().add(node);
        return node;
    }

    SQLServerViewDefinitionQuery query = new SQLServerViewDefinitionQuery();


    private void buildRecursively(JdbcTemplate jdbcTemplate, List<String> objectNames) {
        if (objectNames.size() == 0) {
            return;
        }
        List<String> existingFromNodes = getNodeNames();

        List<ViewOrTableDefinition> viewOrTables = query.query(jdbcTemplate, objectNames);

        if (viewOrTables.size() == 0) {
            return;
        }

        for (ViewOrTableDefinition viewOrTable : viewOrTables) {
            Node fromNode = new Node(viewOrTable.getName(), viewOrTable.getType());
            addOrUpdate(fromNode);

            Pattern fromPattern = Pattern.compile("\\s+from\\s+([^(].*?)[\\s;]+", Pattern.CASE_INSENSITIVE);
            Pattern joinPattern = Pattern.compile("\\s+join\\s+([^(].*?)[\\s;]+", Pattern.CASE_INSENSITIVE);

            Matcher fromMatcher = fromPattern.matcher(viewOrTable.getDefinition());
            while (fromMatcher.find()) {
                String nodeName = fromMatcher.group(1);
                Node node = addOrUpdate(nodeName);
                addEdgeIfNotExists(node, fromNode);
            }

            Matcher joinMatcher = joinPattern.matcher(viewOrTable.getDefinition());
            while (joinMatcher.find()) {
                String nodeName = joinMatcher.group(1);
                Node node = addOrUpdate(nodeName);
                addEdgeIfNotExists(node, fromNode);
            }
        }

        List<String> fromNodeNames = getNodeNames();

        if (existingFromNodes.size() == fromNodeNames.size()) {
            return;
        }

        buildRecursively(jdbcTemplate, fromNodeNames);
    }
}
