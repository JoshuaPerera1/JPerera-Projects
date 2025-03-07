/** Top level program controlling the interface */ 

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import java.util.Locale;
import java.util.ResourceBundle;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.*;
import javafx.util.Pair;

public class Controller {

    public Graph graph;

    // names from the items defined in the FXML file

    @FXML
    private Button Quit;
    @FXML
    private Button english_bt;
    @FXML
    private Button maori_bt;
    @FXML
    private Button networkflow_bt;
    @FXML
    private Button pagerank_bt;
    @FXML
    private Canvas mapCanvas;
    @FXML
    private Label nodeDisplay;
    @FXML
    private TextArea lineText;

    // These are used to map the nodes to the location on screen
    private double scale = 3.5; 
    private Point2D origin = new Point2D(100, 80);
    private static final double ratioxy = .5;
    private static int nodeSize = 10;

    private static final int Gnode_SIZE = 5; // drawing size of Nodes

    // used to prevent drag from creating a click
    private Boolean dragActive = false;

    // set up connections between the buttons and the methods
    public void initialize() {

        // load the input files
        Map<String, Gnode> NodeMap = loadNodes(new File("data/node.csv"));
        Map<String, Edge> lines = loadLines(new File("data/edge.csv"), NodeMap);

        this.graph = new Graph(NodeMap, lines.values());
        System.out.println("Loaded Graph Data");

        drawGraph(graph);
    }

    // get scale
    public double getScale() {
        return scale;
    }

    // get mapCanvas
    public Canvas getMapCanvas() {
        return mapCanvas;
    }

    //get Origin
    public Point2D getOrigin(){
        return origin;
    }

    // load button pressed - load Node file and edge file in that order
    public void handleLoad(ActionEvent event) {
        Stage stage = (Stage) mapCanvas.getScene().getWindow();
        System.out.println("Handling event " + event.getEventType());
        FileChooser fileChooser = new FileChooser();
        // Set to user directory or go to default
        File defaultNodePath = new File("data/");
        if (!defaultNodePath.canRead()) {
            defaultNodePath = new File("C:/");
        }
        fileChooser.setInitialDirectory(defaultNodePath);
        FileChooser.ExtensionFilter extentionFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extentionFilter);

        fileChooser.setTitle("Open Node File");
        File nodeFile = fileChooser.showOpenDialog(stage);

        fileChooser.setTitle("Open Edge File");
        File edgeFile = fileChooser.showOpenDialog(stage);
        Map<String, Gnode> NodeMap = loadNodes(nodeFile);
        Map<String,Edge> lines = loadLines(edgeFile, NodeMap);
        System.out.println("Loaded Graph Data; constructing graph....");
        graph = new Graph(NodeMap, lines.values());
        drawGraph(graph);
        event.consume();
    }

    /** handle the quit button being pressed connected using FXML */
    public void handleQuit(ActionEvent event) {
        System.out.println("Quitting with event " + event.getEventType());
        event.consume();
        System.exit(0); // system exit with status 0 - normal
    }

    // handle the english button being pressed connected using FXML
    public void handleEnglish(ActionEvent event)throws IOException  {
        //switch to english using setting resource bundle
        Main.setLocale(new Locale("en", "NZ")); // change to english
        Main.stage.close();
        Main reload = new Main();
        reload.reload();
        event.consume();
    }

    // handle the maori button being pressed connected using FXML
    public void handleMaori(ActionEvent event) throws IOException  {
        Main.setLocale(new Locale("mi", "NZ")); // change to english
        Main.stage.close();
        Main reload = new Main();
        reload.reload();
        event.consume();
    }

    // Mouse scroll for zoom
    public void mouseScroll(ScrollEvent event) {
        // change the zoom level
        double changefactor = 1 + (event.getDeltaY() / 400);
        scale *= changefactor;
        // update the graph
        drawGraph(graph);
        event.consume();
    }

    public double dragStartX = 0;
    public double dragStartY = 0;
    // handle starting drag on canvas
    public void handleMousePressed(MouseEvent event) {
        dragStartX = event.getX();
        dragStartY = event.getY();
        event.consume();
    }

    // handleMouse Drag
    public void handleMouseDrag(MouseEvent event) {
        // pan the map
        double dx = event.getX() - dragStartX;
        double dy = event.getY() - dragStartY;
        dragStartX = event.getX();
        dragStartY = event.getY();
        origin= new Point2D(origin.getY()-dx / (scale * ratioxy), origin.getY()+(dy / scale));

        drawGraph(graph);
        // set drag active true to avoid clicks highlighting nodes
        dragActive = true;
        event.consume();
    }

    /*
     * Drawing the graph on the canvas
     */
    public void drawCircle(double x, double y, double radius) {
        GraphicsContext gc = mapCanvas.getGraphicsContext2D();
        gc.fillOval(x - (radius / 2), (y - radius / 2), radius, radius);
    }

    public void drawCircle(double x, double y, double radius, Color color) {
        GraphicsContext gc = mapCanvas.getGraphicsContext2D();
        gc.setFill(color);
        gc.fillOval(x - (radius / 2), (y - radius / 2), radius, radius);
    }

    public void drawLine(double x1, double y1, double x2, double y2) {
        mapCanvas.getGraphicsContext2D().strokeLine(x1, y1, x2, y2);
        //get the slope and find its angle
        double slope;
        if(x1==x2){
            slope=Integer.MAX_VALUE;
        }
        else{
            slope = (y1 - y2) / (x1 - x2);
        }
        double lineAngle = Math.atan(slope);
        double intercept = y1-slope*x1;
        double arrowAngle = x1 > x2 ? Math.toRadians(45) : -Math.toRadians(215);

        double arrowLength = 10;
        mapCanvas.getGraphicsContext2D().setStroke(Color.RED);
        double arrowX, arrowY;
        //this is for network flow
        if(x1<x2){
            arrowX= 7+x1+(x2-x1)/2;
            arrowY=slope*arrowX+intercept;
        }
        else{
            if(x1>x2){
                arrowX= 7+x2+(x1-x2)/2;
                arrowY=slope*arrowX+intercept;
            }

            else{
                arrowX=x2;
                arrowY=y1>y2?y1+(y2-y1)/2:y2+(y1-y2)/2;
            }}

        //  this is for pagerank
        // arrowX =x2;
        // arrowY = y2;
        mapCanvas.getGraphicsContext2D().strokeLine(arrowX, arrowY, arrowX+arrowLength*Math.cos(lineAngle - arrowAngle),arrowY+arrowLength * Math.sin(lineAngle - arrowAngle));
        mapCanvas.getGraphicsContext2D().strokeLine(arrowX, arrowY, arrowX+arrowLength*Math.cos(lineAngle + arrowAngle),arrowY+arrowLength * Math.sin(lineAngle + arrowAngle));

    }

    public void drawGraph(Graph graph) {
        if (graph == null) {
            return;
        }
        // upDateGraph(graph);
        GraphicsContext gc = mapCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
        // store node list so we can use nodes to find edge end points.
        Collection<Gnode> nodeList = graph.getNodes().values();

        // draw nodes

        nodeList.forEach(node -> {
                    double size = nodeSize;

                    gc.setFill(Color.ROYALBLUE); 
                    Point2D screenPoint = Projection.model2Screen(node.getPoint(),this);
                    drawCircle(screenPoint.getX(), screenPoint.getY(), size);
                    // if (nodeNames_ch.isSelected()) {
                    gc.setFont(new Font("Arial", 12));
                    gc.fillText("[" + node.getId() + "]" + node.getName(), screenPoint.getX() + 15,
                        screenPoint.getY() - 10);
                    //}
            });

        // draw edges
        graph.getOriginalEdges().forEach(edge -> {
                    gc.setStroke(Color.BLACK);

                    Point2D startPoint = Projection.model2Screen(edge.fromNode().getPoint(), this);
                    Point2D endPoint = Projection.model2Screen(edge.toNode().getPoint(),this);
                    drawLine(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());

                    double textPosX, textPosY, slope, intercept;
                    if(startPoint.getX()==endPoint.getX()){
                        slope=Integer.MAX_VALUE;
                    }
                    //   else
                    {
                        slope = (endPoint.getY() - startPoint.getY()) / (endPoint.getX() - startPoint.getX());}
                    intercept = startPoint.getY()-slope*startPoint.getX();
                    if(startPoint.getX()<endPoint.getX()){
                        textPosX= 10+startPoint.getX()+(endPoint.getX()-startPoint.getX())/2;
                        textPosY=slope*textPosX+intercept;
                    }
                    else{
                        if(startPoint.getX()>endPoint.getX()){
                            textPosX= 10+endPoint.getX()+(startPoint.getX()-endPoint.getX())/2;
                            textPosY=slope*textPosX+intercept;
                        }
                        else{
                            textPosX=10+startPoint.getX();
                            textPosY=startPoint.getY()>endPoint.getY()?startPoint.getY()+(endPoint.getY()-startPoint.getY())/2:endPoint.getY()+(startPoint.getY()-endPoint.getY())/2;

                        }}

                    if (Math.abs(startPoint.getY() - endPoint.getY()) < 20) {   
                        textPosY-=2;
                    }             

            });
    }

    //-------------------------------------------------------
    //  methodes to load Gnode and Line data from files
    //-------------------------------------------------------

    /** Load the node data from the node file
     * file contains:
     *     node_id, node_name, location_x,
     *     location_y
     */
    public static Map<String, Gnode> loadNodes(File NodesFile) {
        Map<String, Gnode> nodeMap= new HashMap<String, Gnode>();
        try {
            List<String> dataLines = Files.readAllLines(NodesFile.toPath());
            dataLines.remove(0);// throw away the top line of the file
            for (String dataLine : dataLines){
                // tokenise the dataLine by splitting it on tabs
                String[] tokens = dataLine.split(",");
                if (tokens.length >= 4) {
                    // process the tokens
                    String GnodeId = tokens[0];
                    String GnodeName = tokens[1];
                    double x = Double.valueOf(tokens[2]);
                    double y = Double.valueOf(tokens[3]);
                    nodeMap.put(GnodeId, new Gnode(x, y, GnodeName, GnodeId));
                }
            }
            System.out.println("Loaded "+ nodeMap.size()+" Nodes");
        } catch (IOException e) {
            throw new RuntimeException("Reading the Nodes file failed.");
        }
        return nodeMap;
    }

    /** Load the edge data from the edge file
     * File contains: edge_id, fromGnode_Id, toGnode_Id
     * Uses the NodeMap to turn the Gnode_id's into Gnodes
     */
    public static Map<String,Edge> loadLines(File lineFile, Map<String,Gnode> NodeMap) {
        if (NodeMap.isEmpty()){
            throw new RuntimeException("loadLines given an empty NodeMap.");
        }
        Map<String, Edge> edgeMap = new HashMap<String, Edge>();
        try {
            System.out.println("Reading data from: "+lineFile);
            List<String> dataLines = Files.readAllLines(lineFile.toPath());
            dataLines.remove(0); //throw away the top line of the file.
            for (String dataLine : dataLines){// read in each line of the file
                // tokenise the line by splitting it on tabs".
                String[] tokens = dataLine.split(",");
                if (tokens.length >= 4) {
                    // process the tokens
                    String edgeId = tokens[0];
                    String nodeId1 = tokens[1];
                    String nodeId2 = tokens[2];
                    int capanode = Integer.parseInt(tokens[3]);
                    Gnode node1 = NodeMap.get(nodeId1);
                    Gnode node2 = NodeMap.get(nodeId2);
                    if (node1==null || node2 == null){
                        System.out.println("Edge "+edgeId+" has unknown node "+nodeId1+" at "+capanode);
                    }
                    else {
                        Edge e = new Edge(node1, node2);
                        edgeMap.put(edgeId,e);
                    }
                }
                else {
                    System.out.println("Line file has broken entry: "+dataLine);
                }

            }
            System.out.println("Loaded "+ edgeMap.size()+" edges");
        } catch (IOException e) {throw new RuntimeException("Loading the lines file failed.");}
        return edgeMap;
    }

    //handle Page Rank
    public void handlePageRank(ActionEvent event){
        int iter = 10;
        double dampingFactor = .85;
        PageRank.computeLinks(graph);
        PageRank.computePageRank(graph, iter, dampingFactor);
    }
}