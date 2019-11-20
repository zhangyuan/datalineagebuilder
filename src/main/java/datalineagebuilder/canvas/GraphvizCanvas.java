package datalineagebuilder.canvas;

import datalineagebuilder.graph.Edge;
import datalineagebuilder.graph.Graph;
import datalineagebuilder.graph.Node;
import org.apache.commons.io.IOUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.stream.Collectors;

public class GraphvizCanvas implements Canvas {
    private String filepath;
    FileOutputStream out = null;

    public GraphvizCanvas(String filepath) {
        this.filepath = filepath;
    }

    private void println(String text) throws IOException {
        if (out == null) {
            out = new FileOutputStream(filepath);
        }
        IOUtils.write(text + System.lineSeparator(), out, "UTF-8");
    }

    public void draw(Graph graph) throws IOException {
        println("digraph dependencies {");
        println("\trankdir=LR;");
        println("\tgraph [pad=\".75\", ranksep=\"3\"];");

        for (Node node : graph.getNodes()) {
            String color = "#f2eabf";
            if (!node.getType().equals("TABLE")) {
                color = "#a6dbf5";
            }
            println("\t" + node.getName() + " [shape=box, style=filled, width=3.5, fillcolor=\"" + color + "\", label=\"" + node.label() + "\"]");
        }

        for (Edge edge : graph.getEdges()) {
            println(String.format("\t%s -> %s", edge.getFrom().getName(), edge.getTo().getName()));
        }

        String otrRank = "\t{rank = same;" + graph.getNodes().stream().filter(
                x -> !(x.getName().startsWith("dw_") || x.getName().startsWith("src_") )
        ).map(x -> x.getName() + ";").collect(Collectors.joining(" ")) + "}";

        println(otrRank);

        String srcRank = "\t{rank = same;" + graph.getNodes().stream().filter(x -> x.getName().startsWith("src_"))
                .map(x -> x.getName() + ";").collect(Collectors.joining(" ")) + "}";
        println(srcRank);

        println("}");
    }
}
