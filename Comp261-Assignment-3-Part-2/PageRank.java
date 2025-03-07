import javafx.util.Pair;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;

/**
 * Write a description of class PageRank here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class PageRank
{
    //class members 
    private static double dampingFactor = .85;
    private static int iter = 10;
    /**
     * build the fromLinks and toLinks 
     */
    //TODO: Build the data structure to support Page rank. Compute the fromLinks and toLinks for each node
    public static void computeLinks(Graph graph){
        // TODO
        //Iterating over the graph's original edges to retrieve the to and from nodes of the current edge
        for (Edge edge : graph.getOriginalEdges()) {
            Gnode fromNode = edge.fromNode();
            Gnode toNode = edge.toNode();
            fromNode.addToLinks(toNode); //add toNode to set of toLinks of fromNode
            toNode.addFromLinks(fromNode); // add fromNode to set of fromLinks of toNode
        }
        printPageRankGraphData(graph);  ////may help in debugging
        // END TODO
    }

    public static void printPageRankGraphData(Graph graph){
        System.out.println("\nPage Rank Graph");

        for (Gnode node : graph.getNodes().values()){
            System.out.print("\nNode: "+node.toString());
            //for each node display the in edges 
            System.out.print("\nIn links to nodes:");
            for(Gnode c:node.getFromLinks()){

                System.out.print("["+c.getId()+"] ");
            }

            System.out.print("\nOut links to nodes:");
            //for each node display the out edges 
            for(Gnode c: node.getToLinks()){
                System.out.print("["+c.getId()+"] ");
            }
            System.out.println();;

        }    
        System.out.println("=================");
    }
    //TODO: Compute rank of all nodes in the network and display them at the console
    public static void computePageRank(Graph graph, int iter, double dampingFactor) {
        int nNodes = graph.getNodes().size(); //number of nodes
        Map<Gnode, Double> pageRanks = new HashMap<>(); // map to hold pageRanks of each node
        for (Gnode node : graph.getNodes().values()) {
            pageRanks.put(node, 1.0 / nNodes);
        }
        //Pagerank calculation for the number of iterations
        int count = 1;
        while (count <= iter) {
            double noOutLinkShare = 0.0;
            //calculating share of rank contributed by nodes with no outgoing links
            for (Gnode node : graph.getNodes().values()) {
                if (node.getToLinks().isEmpty()) {
                    noOutLinkShare += dampingFactor * (pageRanks.get(node) / nNodes);
                }
            }
            //Calculate the new page rank for each node
            Map<Gnode, Double> newPageRanks = new HashMap<>();
            for (Gnode node : graph.getNodes().values()) {
                double nRank = noOutLinkShare + (1 - dampingFactor) / nNodes;
                double neighboursShare = 0.0;
                //Sum up the contribution of each back linking neighbour
                for (Gnode backNeighbor : node.getFromLinks()) {
                    neighboursShare += pageRanks.get(backNeighbor) / backNeighbor.getToLinks().size();
                }
                //Update the new edge rank for the node
                double newPageRank = nRank + dampingFactor * neighboursShare;
                newPageRanks.put(node, newPageRank);
            }
            //Update the page ranks map with the new values after each iteration
            pageRanks = newPageRanks;
            count++;
        }
        //displat the final page after the last iteration
        System.out.println("Iteration 10 : \n");
        int index = 0;
        for (Gnode node : graph.getNodes().values()) {
            System.out.printf("%s[%d]: %s\n", node.getName(), index++, pageRanks.get(node));
        }
        // Calculate and display sum of all page ranks
        double sum = pageRanks.values().stream().mapToDouble(Double::doubleValue).sum();
        System.out.printf("Sum: %s\n", sum);
        //calling the most important neighbour method 
        PageRank.computeMostImpNeighbour(graph);
    }

    public static void computeMostImpNeighbour(Graph graph){
        // TODO
        //Maps to store the rank of nodes without a specific neighbour and the most important neighbour for each node
        Map <Gnode, Double> rankWithoutCity = new HashMap<>();
        Map <Gnode, Gnode> mostImportant = new HashMap<>();

        //set initial ranks for each node
        for (String cityId : graph.getNodes().keySet()) {
            rankWithoutCity.put(graph.getGnode(cityId), 1.0/graph.getNodes().values().size());
        }
        //Perform the page rank calculation through a for loop
        int count = 1;
        do {
            //Iterate over each node in the graph
            for (Gnode node : graph.getNodes().values()) {
                //Skip nodes with no incoming links
                if (node.getFromLinks().isEmpty()) continue; 
                //Calculate the rank of the node without each of its neighbours
                for (Gnode xNode : node.getFromLinks()) {
                    pageRankWithoutList(node, xNode, new ArrayList<>(node.getFromLinks()), rankWithoutCity, graph);
                }
                //find the most important neighbour by determining which one, when removed, causes the greatest rank drop.
                Gnode mostImportantNode = node.getFromLinks().stream()
                    .min(Comparator.comparingDouble(c -> rankWithoutCity.get(node) - rankWithoutCity.get(c)))
                    .get();
                //if a most important node is found, add it to the map
                if (mostImportantNode != null) {
                    mostImportant.put(node, mostImportantNode);
                }
            }
            count++;
        }while (count < iter);
        //print the most important neighbour for each node
        mostImportant.forEach((key, value) -> System.out.println("NODE " + key.getName() + ": " + value.getName()));
    }

    public static void pageRankWithoutList(Gnode node, Gnode xNode, List<Gnode> fromLinks, Map<Gnode, Double> rankWithoutCity, Graph graph) {
        double neighbourRank = 0;
        //temporarily remove the specified neighbour from the list of incoming links
        fromLinks.remove(xNode);
        //Calculate the share of each remaining neighbour
        for(Gnode neighbourBack: fromLinks) {
            double neighbourShare = rankWithoutCity.get(neighbourBack) / neighbourBack.getToLinks().size();
            neighbourRank += neighbourShare;
        }
        //Compute the new rank of the node without the specified neighbour
        neighbourRank = ((1 - dampingFactor) / graph.getNodes().values().size()) + (dampingFactor * neighbourRank);
        //update the rankwithoutCity map with the new rank.
        rankWithoutCity.put(node, neighbourRank);
    }
}

