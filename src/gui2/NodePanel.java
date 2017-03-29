package gui2;

import expression.sentence.Sentence;
import logicalreasoner.inference.Branch;
import logicalreasoner.inference.Decomposition;
import logicalreasoner.inference.Inference;
import logicalreasoner.prover.SemanticProver;
import logicalreasoner.truthassignment.TruthAssignment;
import logicalreasoner.truthassignment.TruthValue;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.IntStream;

import static gui2.TreeLayout.BUFFER;

/**
 * Created by kevin on 3/25/17.
 */
public class NodePanel extends JPanel {

  private static HashMap<Integer, NodePanel> instances = new HashMap<>();
  public static SemanticProver prover;
  public static int COLUMNS = 5;
  public static String[] HEADERS = {"#", "Sentence", "", "Justification", ""};
  private final JLabel nameLabel;

  private GraphPanel graphPanel;
  private TruthAssignment truthAssignment;
  private JTable jTable;
  private JLabel closed;
  private NodePanel parent = null;
  private ArrayList<NodePanel> children;
  private Line2D.Double parentEdge = null;
  private ArrayList<Line2D.Double> edges;
  private int x, y, xRank, depth;

  public NodePanel(TruthAssignment truthAssignment, GraphPanel panel) {
    super(new BorderLayout());
    setOpaque(true);
    setVisible(true);
    this.truthAssignment = truthAssignment;
    this.graphPanel = panel;

    jTable = new JTable(new UneditableTableModel(makeData(), HEADERS));
    jTable.setDragEnabled(false);
    jTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer());
    jTable.setOpaque(true);
    jTable.setBackground(Color.white);
    jTable.setGridColor(Color.black);
    jTable.setBorder(BorderFactory.createLineBorder(Color.black));
    jTable.getTableHeader().setReorderingAllowed(false);
    jTable.getTableHeader().setResizingAllowed(false);
    jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    pack();

    // Center align each cell
    DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
    renderer.setHorizontalAlignment(JLabel.CENTER);
    for (int i = 0; i < COLUMNS; ++i)
      jTable.getColumnModel().getColumn(i).setCellRenderer(renderer);

    closed = new JLabel("");
    closed.setHorizontalAlignment(JLabel.CENTER);
    closed.setOpaque(true);
    closed.setBackground(Color.white);
    closed.setForeground(Color.black);
    closed.setBorder(BorderFactory.createLineBorder(Color.black));
    setClosed();

    nameLabel = new JLabel("  " + truthAssignment.getName());
    nameLabel.setOpaque(true);
    nameLabel.setBackground(Color.white);
    nameLabel.setForeground(Color.black);
    nameLabel.setBorder(BorderFactory.createLineBorder(Color.black));
    JPanel header = new JPanel(new GridLayout(2, 1));
    header.add(nameLabel);
    header.add(jTable.getTableHeader());

    add(header, BorderLayout.NORTH);
    //add(nameLabel, BorderLayout.NORTH);
    //add(jTable.getTableHeader(), BorderLayout.NORTH);
    add(jTable, BorderLayout.CENTER);
    add(closed, BorderLayout.SOUTH);
    setPreferredSize(new Dimension(getWidth(), getHeight()));

    NodePanelListener listener = new NodePanelListener(this);
    nameLabel.addMouseListener(listener);
    nameLabel.addMouseMotionListener(listener);
    jTable.getTableHeader().addMouseListener(listener);
    jTable.getTableHeader().addMouseMotionListener(listener);
    jTable.addMouseListener(listener);
    jTable.addMouseMotionListener(listener);
    closed.addMouseListener(listener);
    closed.addMouseMotionListener(listener);

    if (graphPanel.isProofAssistantMode())
      jTable.addMouseListener(new InferenceListener());

    children = new ArrayList<>();
    edges = new ArrayList<>();
    xRank = 0;
    x = 0;
    depth = 0;
    y = 0;
    updateBounds();

    instances.put(truthAssignment.getUID(), this);
  }

  public NodePanel(TruthAssignment truthAssignment, int depth, GraphPanel graphPanel) {
    this(truthAssignment, graphPanel);
    this.depth = depth;
    y = this.depth;
    updateBounds();
  }

  public JTable getJTable() {
    return jTable;
  }

  public int getWidth() {
    return jTable.getPreferredSize().width;
  }

  public int getHeight() {
    return jTable.getPreferredSize().height + jTable.getTableHeader().getPreferredSize().height
            + closed.getPreferredSize().height + nameLabel.getPreferredSize().height;
  }

  public void addChild(NodePanel nodePanel) {
    children.add(nodePanel);
    nodePanel.parent = this;
  }

  public ArrayList<NodePanel> getChildren() {
    return children;
  }

  public int getNumChildren() {
    return children.size();
  }

  public int getXRank() {
    return xRank;
  }

  public int getDepth() {
    return depth;
  }

  public void setXRank(int newX) {
    xRank = newX;
    x = xRank * 200;
    updateBounds();
  }

  public int getX() {
    return x;
  }

  public void setX(int newX) {
    x = newX;
  }

  public int getY() {
    return y;
  }

  public void setY(int newY) {
    y = newY;
  }

  public void movePanel(int deltaX, int deltaY) {
    x += deltaX;
    y += deltaY;
  }

  /**
   * Recursively move this branch by deltaX and deltaY
   *
   * @param deltaX the x distance to move
   * @param deltaY the y distance to move
   */
  public void moveBranch(int deltaX, int deltaY) {
    movePanel(deltaX, deltaY);
    updateBounds();
    children.forEach(c -> c.moveBranch(deltaX, deltaY));
  }

  public void updateBounds() {
    setBounds(x, y, getWidth(), getHeight());
    graphPanel.updatePreferredSize(x, x + getWidth(), y, y + getHeight());
    updateLines();
  }

  public void updateLines() {
    if (parentEdge != null)
      parentEdge.setLine(parent.getBottomAnchor(), getTopAnchor());
    Point2D.Double bottomAnchor = getBottomAnchor();
    IntStream.range(0, children.size()).forEach(i -> edges.get(i).setLine(bottomAnchor, children.get(i).getTopAnchor()));
    graphPanel.repaint();
  }

  public Point2D.Double getTopAnchor() {
    return new Point2D.Double((double) x + getWidth() / 2, (double) y);
  }

  public Point2D.Double getBottomAnchor() {
    return new Point2D.Double((double) x + getWidth() / 2, (double) y + getHeight());
  }

  public void setParentEdge(Line2D.Double edge) {
    parentEdge = edge;
  }

  public void addEdge(Line2D.Double edge) {
    edges.add(edge);
  }

  public Object[] makeRow(int inferenceNum, int justificationNum, TruthValue v) {
    return new Object[]{
            inferenceNum > 0 ? Integer.toString(inferenceNum) : "P" + (-1 * inferenceNum),
            v.getSentence(),
            v.isModelled() ? 'T' : 'F',
            justificationNum > 0 ? justificationNum : "P" + (-1 * justificationNum),
            (v.isDecomposed() && prover.getBranchQueue().stream().noneMatch(b -> b.getOrigin() == v.getSentence())) ? '✓' : '✗'};
  }

  public Object[][] makeData() {
    ArrayList<Object[]> data = new ArrayList<>();
    truthAssignment.stream().forEach(v -> {
      int inferenceNum = v.getInferenceNum(v.isModelled());
      Inference inference = v.getJustification(inferenceNum);
      data.add(makeRow(inferenceNum, inference.getJustificationNum(), v));
    });

    // Sort data by inference number
    data.sort((e1, e2) -> compare((String) e1[0], (String) e2[0]));

    Object[][] arr = new Object[data.size()][HEADERS.length];
    for (int i = 0; i < data.size(); ++i)
      arr[i] = data.get(i);

    return arr;
  }

  private int compare(String s1, String s2) {
    if (s1.startsWith("P") && s2.startsWith("P")) {
      return Comparator.<String>reverseOrder().compare(s1.substring(1), s2.substring(1));
    } else if (s1.startsWith("P")) {
      return -1;
    } else if (s2.startsWith("P")) {
      return 1;
    }

    return Comparator.<String>naturalOrder().compare(s1, s2);
  }

  void pack() {
    for (int i = 0; i < jTable.getColumnCount(); i++) {
      DefaultTableColumnModel colModel = (DefaultTableColumnModel) jTable.getColumnModel();
      TableColumn col = colModel.getColumn(i);
      int width = 0;

      for (int r = 0; r < jTable.getRowCount(); r++) {
        Component comp = jTable.getCellRenderer(r, i).getTableCellRendererComponent(jTable, jTable.getValueAt(r, i),
                false, false, r, i);
        width = Math.max(width, comp.getPreferredSize().width);
      }
      Component comp = jTable.getTableHeader().getDefaultRenderer()
              .getTableCellRendererComponent(jTable, col.getHeaderValue(), false, false, 0, 0);

      width = Math.max(width, comp.getPreferredSize().width);
      col.setPreferredWidth(width + 10);
    }
  }

  public void setClosed() {
    if (!(truthAssignment.isConsistent() && truthAssignment.areParentsConsistent()))
      this.closed.setText("✗");
    else
      this.closed.setText("o");
  }

  public boolean isClosed() {
    return closed.getText().equals("✗");
  }

  public boolean isFinished() {
    return prover.reasoningCompleted();
  }

  public String toString() {
    return truthAssignment.getName();
  }

  public boolean equals(Object o) {
    return this == o;
  }

  class TableHeaderRenderer extends JLabel implements TableCellRenderer {
    public TableHeaderRenderer() {
      setBackground(Color.white);
      setOpaque(true);
      setHorizontalAlignment(JLabel.CENTER);
      setBorder(BorderFactory.createLineBorder(Color.black, 1, false));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      setText(value.toString());
      return this;
    }
  }

  class UneditableTableModel extends DefaultTableModel {
    public UneditableTableModel(Object[][] data, String[] headers) {
      super(data, headers);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
      return false;
    }
  }

  class NodePanelListener implements MouseListener, MouseMotionListener {
    private int x, y;
    private NodePanel nodePanel;

    public NodePanelListener(NodePanel panel) {
      nodePanel = panel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
      x = e.getXOnScreen();
      y = e.getYOnScreen();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
      nodePanel.movePanel(e.getXOnScreen() - x,
              e.getYOnScreen() - y);
      nodePanel.updateBounds();
      x = e.getXOnScreen();
      y = e.getYOnScreen();
      graphPanel.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
  }

  class InferenceListener extends MouseAdapter {
    public void mousePressed(MouseEvent me) {
      if (me.getClickCount() == 2) {
        int row = jTable.rowAtPoint(me.getPoint());
        Sentence s = (Sentence) jTable.getModel().getValueAt(row, 1);
        String justification = (String) jTable.getModel().getValueAt(row, 0);
        if (justification.startsWith("P"))
          justification = justification.replace("P", "-");
        int justificationNum = Integer.parseInt(justification);

        if (!truthAssignment.isDecomposed(s)) {
          jTable.getModel().setValueAt('✓', row, 4);
          handleInference(s.reason(truthAssignment, prover.getInferenceCount(), justificationNum));
        }
      }

    }

    private void handleInference(Inference i) {
      prover.incrementInferenceCount();
      DefaultTableModel model = (DefaultTableModel) jTable.getModel();
      if (i instanceof Decomposition) {
        // for decompositions, add the rows and resize the NodePanel
        Decomposition d = (Decomposition) i;
        int oldHeight = getHeight();
        prover.infer(d).forEach(p -> model.addRow(makeRow(i.getInferenceNum(),
                i.getJustificationNum(), truthAssignment.getTruthValue(p.sentence))));
        pack();
        children.forEach(c -> c.moveBranch(0, getHeight() - oldHeight));
        updateBounds();
      } else if (i instanceof Branch) {
        // for branches, create new NodePanels and edges for each child
        Branch b = (Branch) i;
        prover.infer(i);
        prover.addBranches();

        b.getInferredOver().forEach(t -> {
          NodePanel n = instances.get(t.getUID());
          /*
          double midX = n.x + n.getWidth()/2.0,
                  bufferHalf = TreeLayout.BUFFER/2.0;
          int numChildren = n.getNumChildren();
          */
          for (TruthAssignment c : t.getChildren()) {
            NodePanel child = graphPanel.makeTree(c, n.depth + 1);
            n.addChild(child);
            child.setY(n.y + n.getHeight() + BUFFER);
            child.setX(n.x);
            child.updateBounds();
            Line2D.Double edge = new Line2D.Double(n.getBottomAnchor(), child.getTopAnchor());
            n.addEdge(edge);
            child.setParentEdge(edge);
            graphPanel.addEdge(edge);
          }
        });

        new TreeLayout(graphPanel.getRoot()).run();
      }

      prover.closeBranches();
      graphPanel.updateClosedBranches();
      graphPanel.repaint();
    }
  }
}
