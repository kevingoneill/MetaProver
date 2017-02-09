package gui2;

import logicalreasoner.truthassignment.TruthAssignment;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.layout.HierarchicalLayout;
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
    graph = new SingleGraph("Proof");
    graph.addAttribute("ui.quality");
    graph.addAttribute("ui.antialias");
    URL fileURL = this.getClass().getClassLoader().getResource("");
    String filepath = fileURL.getPath() + "/gui2/graph_style.css";

    graph.addAttribute("ui.stylesheet", "url('file:" + filepath + "')");
    viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
    viewer.disableAutoLayout();
    viewer.enableAutoLayout(new HierarchicalLayout());

    add(viewer.addDefaultView(false));
    viewer.getDefaultView().setPreferredSize(new Dimension(1000, 750));

  }

  public Node makeNode(TruthAssignment truthAssignment) {
    // Create a node for the root and add attributes for each TruthValue
    Node root = graph.addNode(truthAssignment.getName());
    root.addAttribute("ui.label", truthAssignment.getName());
    truthAssignment.stream().forEach(truthValue -> root.addAttribute(truthValue.getSentence().toString(), truthValue.isModelled()));

    // make nodes for each child and add corresponding edges
    truthAssignment.getChildren().forEach(c -> graph.addEdge(truthAssignment.getName() + "-" + c.getName(), root, makeNode(c)));
    return root;
  }

}
