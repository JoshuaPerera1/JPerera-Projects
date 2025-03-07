import java.util.Collection;
import java.util.Collections;
import javafx.geometry.Point2D;
import java.util.HashSet;
import javafx.util.Pair;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;


/**
 * Structure for holding node information
 */

public class Gnode implements Comparable<Gnode> {
    // location of the node
    private Point2D loc;
    private String name;
    private String id;


    
    //data structures  for maintaining the count and set of inlinks and outlinks for each node in the graph to support PageRank
    private  Set<Gnode> fromLinks = new HashSet<Gnode>();
    private  Set<Gnode>toLinks = new HashSet<Gnode>();

    /**
     * Constructor for a node
     * 
     * @param id   4  digit node id
     * @param name Long name for the node
     * @param lat
     * @param lon
     */
    public Gnode(double x, double y,String name, String id) {
        this.loc = new Point2D(x,y);
        this.name = name;
        this.id = id;

    }

    //--------------------------------------------
    //  Getters and basic properties of a Node
    //--------------------------------------------

     /**
     * Get the location of the node
     */
     public Point2D getPoint() {
         return this.loc;
     }
     
    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

   

    /**
     * Compare by alphabetic order of name,
     */
    public int compareTo(Gnode other){
        return this.name.compareTo(other.name);
    }

    /** 
     * Display a Gnode
     * 
     * @return string of the node information in the format: XXXX: name 
     */
    public String toString() {
               return id + ": " + name;
    }

    
    /** Get the collection of toLinks  */
    public Collection<Gnode> getToLinks() {
        return Collections.unmodifiableCollection(toLinks);
    }
    /** Get the collection of fromLinks */
    public Collection<Gnode> getFromLinks() {
        return Collections.unmodifiableCollection(fromLinks);
    }

    
    /**
     * add a node to the set of fromlinks 
     */
    public void addFromLinks(Gnode c){
        this.fromLinks.add(c);
        
    }

    /**
     * add a node to the set of tolinks 
     */
    public void addToLinks(Gnode c){
        this.toLinks.add(c);
        
    }
    
}
