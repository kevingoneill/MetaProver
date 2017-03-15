package gui2;

import logicalreasoner.truthassignment.TruthAssignment;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.view.Viewer;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * Created by kevin on 2/8/17.
 */
public class GraphPanel extends JPanel {

  private Graph graph;
  private Viewer viewer;

  public GraphPanel() {
    super(new GridLayout(1, 1));
    System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

    // Create a new graph using TruthAssignmentNodes instead of SingleNodes
    graph = new SingleGraph("Proof");
    graph.addAttribute("ui.quality");
    graph.addAttribute("ui.antialias");
    graph.addAttribute("layout.quality", 4);


    URL fileURL = this.getClass().getClassLoader().getResource("");
    String filepath = fileURL.getPath() + "/gui2/graph_style.css";

    graph.addAttribute("ui.stylesheet", "url('file:" + filepath + "')");
    viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
    //viewer.disableAutoLayout();
    viewer.enableAutoLayout();

    add(viewer.addDefaultView(false));
    viewer.getDefaultView().setPreferredSize(new Dimension(1000, 750));

    graph.addNode("A");
    graph.addNode("B");
    graph.addNode("C");
    graph.addNode("D");
    graph.addNode("E");
    graph.addNode("F");
    graph.addNode("G");
    graph.addNode("H");
    graph.addNode("I");
    graph.addNode("J");
    graph.addNode("K");
    graph.addNode("L");
    graph.addNode("M");
    graph.addNode("N");
    graph.addNode("O");
    graph.addNode("P");
    graph.addNode("Q");
    graph.addNode("R");
    graph.addNode("S");
    graph.addNode("T");

    graph.addEdge("AB", "A", "B", true);
    graph.addEdge("BE", "B", "E", true);
    graph.addEdge("EK", "E", "K", true);
    graph.addEdge("EL", "E", "L", true);
    graph.addEdge("BF", "B", "F", true);
    graph.addEdge("FM", "F", "M", true);
    graph.addEdge("FN", "F", "N", true);
    graph.addEdge("AC", "A", "C", true);
    graph.addEdge("CG", "C", "G", true);
    graph.addEdge("AD", "A", "D", true);
    graph.addEdge("DH", "D", "H", true);
    graph.addEdge("HO", "H", "O", true);
    graph.addEdge("HP", "H", "P", true);
    graph.addEdge("DI", "D", "I", true);
    graph.addEdge("IQ", "I", "Q", true);
    graph.addEdge("IR", "I", "R", true);
    graph.addEdge("DJ", "D", "J", true);
    graph.addEdge("JS", "J", "S", true);
    graph.addEdge("JT", "J", "T", true);

    SpriteManager manager = new SpriteManager(graph);
    manager.addSprite("spriteA").attachToNode("A");

  }

  public Node makeNode(TruthAssignment truthAssignment) {
    // Create a node for the root and add attributes for each TruthValue
    Node root = graph.addNode(truthAssignment.getName());
    root.addAttribute("ui.label", truthAssignment.getName());
    root.addAttribute("TA", truthAssignment);
    root.addAttribute("layout.weight", 10);
    truthAssignment.stream().forEach(truthValue -> root.addAttribute(truthValue.getSentence().toString(), truthValue.isModelled()));

    // make nodes for each child and add corresponding edges
    truthAssignment.getChildren().forEach(c -> graph.addEdge(truthAssignment.getName() + "-" + c.getName(), root, makeNode(c)));
    return root;
  }

}
