import java.io.File;
import java.util.*;

public class TestGraph{

    public static void main(String[] args){
        // load the input files
        System.out.println("Loading Graph Data...");
        Map<String, Gnode> NodeMap = Controller.loadNodes(new File("data/node.csv"));
        Map<String,Edge> lines = Controller.loadLines(new File("data/edge.csv"), NodeMap);
                               
        System.out.println("Loaded Graph Data; constructing graph....");
        Graph graph = new Graph(NodeMap, lines.values());

        graph.printGraphData();
        
    }
}
