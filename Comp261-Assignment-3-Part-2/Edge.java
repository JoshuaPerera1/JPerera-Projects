/**
 * A directed edge in the graph, with associated data.
 * fromNode and toNode are the nodes at the beginning and end of the edge
  */

public class Edge {
    //private final String id;
    private final  Gnode fromNode;
    private final Gnode toNode;

   
    
    public Edge(Gnode fromNode, Gnode toNode){
        //this.id= id;
        this.fromNode = fromNode;
        this.toNode = toNode;
       
    }

    // todo add getters and setters
    //public String getId(){return id;} 
    public Gnode fromNode() {return fromNode;}

    public Gnode toNode() {return toNode;}


    public String toString() {return  "FROM " + 
        fromNode.getName() + "(" + fromNode.getId()+")  TO "+
        toNode.getName() + "(" + toNode.getId()+")"; }

}
