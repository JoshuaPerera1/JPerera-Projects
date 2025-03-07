import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.HashSet;

/**
 * Graph is the data structure that stores the collection of nodes and connections(edges). 
 * The Graph constructor is passed a Map of the nodes, indexed by node Id and
 *  a collection of the (origninal) Edges.
 * The nodes in the map have their id and name.
 * The Edges are defined by their ids (which are defaulted to 0 in the Edge class), node from which it originates (fromNode), node to which it ends (toNode)
 */
public class Graph {

    // Nodes data picked from node file

    private Map<String,Gnode> nodes = new HashMap<String,Gnode>();

    // Original edges between nodes - the ones that are passed over from the edge file

    private Collection<Edge> originalEdges = new HashSet<Edge>();

    /**
     * Construct a new graph given a collection of nodes and a collection of edges
     */
    public Graph(Map<String,Gnode> nodes, Collection<Edge> originalEdges) {

        this.nodes = nodes;

        this.originalEdges = originalEdges;

        //printGraphData();   // uncomment this to help in debugging your code
    }

    //=============================================================================
    //  Method to prith graph data - nodes and edges. 
    //=============================================================================
    public void printGraphData(){
        System.out.println("\nOriginal Graph");
        System.out.println("\n=============================\nNodes:");
        for (Gnode node : nodes.values()){
            System.out.println(node.toString());
        }
        System.out.println("\n=============================\nEdges:");
        for (Edge e : originalEdges){
            System.out.println(e.toString());
        }
        System.out.println("===============");
    }

    //=============================================================================
    //  Methods to access data from the graph. 
    //=============================================================================
    /**
     * Return a map of all the nodes in the network
     */        
    public Map<String,Gnode> getNodes() {
        return Collections.unmodifiableMap(nodes);
    }

    /**
     * Return a collection of all the edges in the network
     */ 

    public Collection<Edge> getOriginalEdges(){

        return Collections.unmodifiableCollection(originalEdges);
    }

    /**
     * Return the node id 
     */ 
    public Gnode getGnode(String id){
        return nodes.get(id);
    }

}
