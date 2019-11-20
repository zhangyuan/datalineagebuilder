package datalineagebuilder;

import datalineagebuilder.canvas.GraphvizCanvas;
import datalineagebuilder.graph.Graph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.util.Arrays;

@SpringBootApplication
public class Application implements CommandLineRunner  {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws IOException {
        Graph graph = Graph.build(jdbcTemplate, Arrays.asList(
          // PUT THE TABLE/VIEWS HERE
        ));
        new GraphvizCanvas("./graph.dot").draw(graph);
    }

}