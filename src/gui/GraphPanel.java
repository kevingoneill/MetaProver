package gui;

import logicalreasoner.prover.Prover;
import logicalreasoner.truthassignment.TruthAssignment;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A GraphPanel is a custom JPanel for displaying TruthAssignment
 * trees in a graphical format. TruthAssignments are represented by
 * NodePanels, and edges are drawn as simple lines
 */
public class GraphPanel extends JPanel {
  public static Prover prover;
  public static int AUTO_MODE = 0,
          PROOF_ASSISTANT_MODE = 1;

  private GUIWindow window;
  private NodePanel root = null;
  private ArrayList<Line2D.Double> edges;
  private int minX, maxX, minY, maxY;
  private int mode;
  private boolean treeFinished = false;

  public GraphPanel(GUIWindow window) {
    super(null);
    setVisible(true);
    setBackground(Color.white);
    minX = 0;
    minY = 0;
    maxX = getPreferredSize().width;
    maxY = getPreferredSize().height;
    mode = AUTO_MODE;

    GraphPanel.prover = null;
    edges = new ArrayList<>();
    root = null;
    this.window = window;
  }

  public void setAutoMode() { mode = AUTO_MODE; }

  public boolean isAutoMode() {
    return mode == AUTO_MODE;
  }

  public void setProofAssistantMode() {
    mode = PROOF_ASSISTANT_MODE;
  }

  public boolean isProofAssistantMode() {
    return mode == PROOF_ASSISTANT_MODE;
  }

  public void addEdge(Line2D.Double edge) {
    edges.add(edge);
  }

  public NodePanel getRoot() {
    return root;
  }

  public void setRoot(TruthAssignment truthAssignment) { root = makeTree(truthAssignment); }

  public NodePanel makeTree(TruthAssignment root) {
    NodePanel n = makeTree(root, 0);
    new TreeLayout(n).run();
    return n;
  }

  public NodePanel makeTree(TruthAssignment root, int depth) {
    NodePanel node = new NodePanel(root, depth, this);
    add(node);
    node.setBounds(0, 0, node.getWidth(), node.getHeight());
    int newDepth = depth + 1;
    root.getChildren().forEach(c -> {
      NodePanel child = makeTree(c, newDepth);
      node.addChild(child);

      Line2D.Double edge = new Line2D.Double(node.getBottomAnchor(), child.getTopAnchor());
      node.addEdge(edge);
      child.setParentEdge(edge);
      edges.add(edge);
    });

    updateUI();
    return node;
  }

  public List<NodePanel> makeTrees(Collection<TruthAssignment> roots) {
    List<NodePanel> nodePanels = roots.stream().map(n -> makeTree(n, 0))
            .collect(Collectors.toList());
    new TreeLayout(nodePanels).run();
    return nodePanels;
  }

  /**
   * If there are negative coordinates, we need to translate the
   * coordinates of every contained Component leftwards/downwards
   */
  public void validate() {
    if (minX < 0 || minY < 0) {
      // the distance to translate x and y
      int deltaX = (minX < 0) ? -minX : 0,
              deltaY = (minY < 0) ? -minY : 0;

      minX += deltaX;
      minY += deltaY;
      maxX += deltaX;
      maxY += deltaY;
      updatePreferredSize(minX, maxX, minY, maxY);

      // translate each NodePanel relative to deltaX and deltaY
      Component[] components = getComponents();
      Arrays.stream(components).forEach(c -> {
        NodePanel n = (NodePanel) c;
        n.movePanel(deltaX, deltaY);
        n.updateBounds();
      });
    }
  }

  public void updateClosedBranches() {
    Arrays.stream(getComponents()).forEach(c ->
            ((NodePanel) c).setClosed());

    if (!treeFinished) {
      if (root.isClosed()) {
        treeFinished = true;
        JOptionPane.showMessageDialog(this, "All branches are closed, so the argument is valid.",
                "Argument Valid", JOptionPane.INFORMATION_MESSAGE);
      } else if (root.getLeaves().anyMatch(l -> l.getTruthAssignment().decomposedAll()
              && l.getTruthAssignment().isConsistent())) {
        treeFinished = true;
        JOptionPane.showMessageDialog(this,
                "There exists an open and complete branch.\nTherefore, the argument is invalid.",
                "Sentences Satisfied", JOptionPane.INFORMATION_MESSAGE);
      }
    }
  }

  public void updateInferences() {
    // Add text for inferences at the bottom
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintStream stream = new PrintStream(out);
    prover.getInferenceList().forEach(stream::println);
    window.getProofPanel().removeAll();
    window.getProofPanel().add(new JTextArea(out.toString()), BorderLayout.CENTER);
    window.pack();
  }

  protected void paintComponent(Graphics g) {
    // Afterwards, paint the nodes
    super.paintComponent(g);

    // Paint all edges
    Graphics2D g2D = (Graphics2D) g;
    g2D.setColor(Color.black);
    edges.forEach(g2D::draw);
    validate();
  }

  /**
   * Change the preferred size of this JPanel, adjusting the presence
   * of JScrollBars on the enclosing JScrollPane.
   *
   * @param left   the leftmost x value of the NodePanel
   * @param right  the rightmost x value of the NodePanel
   * @param top    the uppermost y value of the NodePanel
   * @param bottom the bottommost y value of the NodePanel
   */
  public void updatePreferredSize(int left, int right, int top, int bottom) {
    int stepSize = 100;
    boolean changed = false;
    if (left < minX) {
      minX = left - stepSize;
      changed = true;
    }
    if (right > maxX) {
      maxX = right + stepSize;
      changed = true;
    }
    if (top < minY) {
      minY = top - stepSize;
      changed = true;
    }
    if (bottom > maxY) {
      maxY = bottom + stepSize;
      changed = true;
    }

    if (changed)
      setPreferredSize(new Dimension(maxX - minX, maxY - minY));
    validate();
  }

  public void removeAll() {
    super.removeAll();
    edges.clear();
    treeFinished = false;
  }

}
