
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashMap;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Map;
import javafx.util.Pair;
import java.util.Comparator;

/** Edmond karp algorithm to find augmentation paths and network flow.
 * 
 * This would include building the supporting data structures:
 * 
 * a) Building the residual graph(that includes original and backward (reverse) edges.)
 *     - maintain a map of Edges where for every edge in the original graph we add a reverse edge in the residual graph.
 *     - The map of edges are set to include original edges at even indices and reverse edges at odd indices (this helps accessing the corresponding backward edge easily)
 *     
 *     
 * b) Using this residual graph, for each city maintain a list of edges out of the city (this helps accessing the neighbours of a node (both original and reverse))

 * The class finds : augmentation paths, their corresponing flows and the total flow
 * 
 * 
 */

public class EdmondKarp {
    // class members

    //data structure to maintain a list of forward and reverse edges - forward edges stored at even indices and reverse edges stored at odd indices
    private static Map<String,Edge> edges; 

    // Augmentation path and the corresponding flow
    private static ArrayList<Pair<ArrayList<String>, Integer>> augmentationPaths =null;

    //TODO:Build the residual graph that includes original and reverse edges 
    public static void computeResidualGraph(Graph graph){
        // TODO
        edges = new HashMap<>(); //Create hashmap for edges
        //Iterate through the original edges of the graph
        for (Edge edge : graph.getOriginalEdges()) {
            edges.put(edge.getId(), edge); //Adding the original edge
            edge.fromCity().addEdgeId(edge.getId());
            //This adds reverse edges
            Edge reverseEdge = new Edge(edge.getId() + 1, edge.toCity(), edge.fromCity(), 0, 0);
            edges.put(reverseEdge.getId(), reverseEdge);
            reverseEdge.fromCity().addEdgeId(edge.getId()+1);
            printResidualGraphData(graph);  //may help in debugging
            // END TODO
        }
    }

    // Method to print Residual Graph 
    public static void printResidualGraphData(Graph graph){
        System.out.println("\nResidual Graph");
        System.out.println("\n=============================\nCities:");
        for (City city : graph.getCities().values()){
            System.out.print(city.toString());

            // for each city display the out edges 
            for(String eId: city.getEdgeIds()){
                System.out.print("["+eId+"] ");
            }
            System.out.println();
        }
        System.out.println("\n=============================\nEdges(Original(with even Id) and Reverse(with odd Id):");
        edges.forEach((eId, edge)->
                System.out.println("["+eId+"] " +edge.toString()));

        System.out.println("===============");
    }

    //=============================================================================
    //  Methods to access data from the graph. 
    //=============================================================================
    /**
     * Return the corresonding edge for a given key
     */

    public static Edge getEdge(String id){
        return edges.get(id);
    }

    /** find maximum flow
     * 
     */
    // TODO: Find augmentation paths and their corresponding flows
    public static ArrayList<Pair<ArrayList<String>, Integer>> calcMaxflows(Graph graph, City from, City to) {
        //TODO
        augmentationPaths = new ArrayList<>(); //creating list to store augmentation paths
        computeResidualGraph(graph); //calling the residual graph method
        System.out.println("---------------------------------------------------------");
        while (true){
            System.out.println("##################################");
            Pair<ArrayList<String>, Integer> pathAndFlow = bfs(graph, from, to); //perform bfs search to find the shortest path from the "from" to "to" node.
            if(pathAndFlow == null){break;} //if no path is found break out of the loop
            augmentationPaths.add(pathAndFlow); //add the found path and flow to list of augmentation paths
            updateResidualGraph(pathAndFlow); // update graph based on path and flow found by bfs search
            printResidualGraphData(graph); //print the current state of the graph
        }
        // END TODO
        return augmentationPaths;
    }

    /**for each edge:
     * increase flow by bottleneck/flow
     * decrease capacity by bottleneck/flow
     * increase reverse capacity by bottleneck
     * @param pathAndFlow
     */
    private static void updateResidualGraph(Pair<ArrayList<String>, Integer> pathAndFlow){
        int flow = pathAndFlow.getValue();
        for(String edgeID : pathAndFlow.getKey()){
            Edge edge = edges.get(edgeID);
            edge.setCapacity(edge.capacity() - flow); //reduce current capacity by increased flow from lastest augmentation path
            edge.setFlow(edge.flow() + flow); //increase flow by increased flow //i think i need to consider handling a reveseedge using %
            Edge reverseEdge = edges.get(edgeID + 1);
            reverseEdge.setCapacity(reverseEdge.capacity() + flow);
        }
    }

    // TODO:Use BFS to find a path from s to t along with the correponding bottleneck flow
    public static Pair<ArrayList<String>, Integer> bfs(Graph graph, City s, City t) {
        if(s == null || t == null){ //here it is validating that the source and sink nodes are not null
            throw new IllegalArgumentException();
        }
        if(s.equals(t)){
            return null; //if source and sink nodes are the same return null since no paths will be found
        }

        ArrayList<String> augmentationPath = new ArrayList<>(); //path of edge IDs
        HashMap<String, String> backPointer = new HashMap<>(); //id of city, edge.
        // TODO
        //list to store the augmentation path and a map for backtracking the path
        Queue<City> cityQueue = new ArrayDeque<>();
        cityQueue.offer(s);

        for(City c : graph.getCities().values()){
            backPointer.put(c.getId(), null); //set all backpointers for all cities to null.
        } //s has the value of null and does not change.

        //bfs from source to sink node
        while(!cityQueue.isEmpty()){
            City current = cityQueue.poll(); //get the next city from the queue
            for(String edgeId : current.getEdgeIds()){
                Edge edge = getEdge(edgeId); //get the edge associated with the current city
                //debugging statements
                if(edge.toCity().getId().equals(s.getId())){
                    System.out.println("1");
                    System.out.println("->"+edgeId);
                }
                if(backPointer.get(edge.toCity().getId()) != null){
                    System.out.println("2");
                    System.out.println("->"+edgeId);
                }
                if(edge.capacity() == 0){
                    System.out.println("3");
                    System.out.println("->"+edgeId);
                }
                //check if the edge leads to a new city and the edge has capacity
                if(!edge.toCity().getId().equals(s.getId()) && backPointer.get(edge.toCity().getId()) == null && edge.capacity() != 0) {
                    backPointer.put(edge.toCity().getId(), edge.getId()); //create backpointer of ids of city, edge
                    cityQueue.offer(edge.toCity()); //add new city to queue
                }
            }
        }
        
        //check if a path to the sink was found
        if(backPointer.get(t.getId()) == null){
            System.out.println("returning early");
            return null; //no path from s to t
        } //searched finished, currently no path from s to t

        
        //Reconstruct the path from sink to source using the backpointers
        City current = t;
        while(backPointer.get(current.getId()) != null){
            String edgeID = backPointer.get(current.getId());
            augmentationPath.add(edgeID);
            current = getEdge(edgeID).fromCity(); //set current to previous city
        }
        Collections.reverse(augmentationPath); //switch to correct order.
        
        //Find bottleneck capacity in the path
        int bottleneck = Integer.MAX_VALUE;
        for(String edgeID : augmentationPath){
            Edge e = getEdge(edgeID);
            bottleneck = Math.min(bottleneck, e.capacity());
        }
        //debugging code
        System.out.println("augmentation path"+augmentationPath);
        System.out.println("bottleneck"+ bottleneck);
        System.out.println("VALUES");
        // END TODO
        return new Pair(augmentationPath,bottleneck);
    }
}


