package gui;

import expression.sentence.ForAll;
import expression.sentence.Sentence;
import logicalreasoner.inference.Branch;
import logicalreasoner.inference.Decomposition;
import logicalreasoner.inference.Inference;
import logicalreasoner.inference.UniversalInstantiation;
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
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static gui.GraphPanel.prover;
import static gui.TreeLayout.BUFFER;

/**
 * Created by kevin on 3/25/17.
 */
public class NodePanel extends JPanel {

  private static HashMap<Integer, NodePanel> instances = new HashMap<>();
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

  public TruthAssignment getTruthAssignment() { return truthAssignment; }

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
            v.containsTrue() && v.containsFalse() ? "T/F" : v.containsTrue() ? "T" : "F",
            justificationNum > 0 ? justificationNum : "P" + (-1 * justificationNum),
            (v.isDecomposed() && (prover == null || prover.getBranchQueue().stream().noneMatch(b -> b.getOrigin() == v.getSentence()))) ? '✓' : '✗'};
  }

  public Object[] makeRow(List<String> inferenceNum, List<String> justificationNum, TruthValue v) {
    return new Object[]{
            inferenceNum,
            v.getSentence(),
            v.containsTrue() && v.containsFalse() ? "T/F" : v.containsTrue() ? "T" : "F",
            justificationNum,
            (v.isDecomposed() && (prover == null || prover.getBranchQueue().stream().noneMatch(b -> b.getOrigin() == v.getSentence()))) ? '✓' : '✗'};
  }

  public Object[][] makeData() {
    ArrayList<Object[]> data = new ArrayList<>();
    truthAssignment.stream().forEach(v -> {
      Integer inferenceNum = null;

      if (!v.containsTrue() || !v.containsFalse()) {
        inferenceNum = v.getInferenceNum(v.isModelled());
        Inference inference = v.getJustification(inferenceNum);
        data.add(makeRow(inferenceNum, inference.getJustificationNum(), v));
      } else {
        List<String> inferenceNums = v.getValues().values().stream()
                .map(i -> Integer.toString(i)).collect(Collectors.toList());
        List<String> justificationNums = inferenceNums.stream()
                .map(i -> Integer.toString(v.getJustification(Integer.parseInt(i))
                        .getJustificationNum())).collect(Collectors.toList());
        data.add(makeRow(inferenceNums, justificationNums, v));
      }
    });

    // Sort data by inference number
    data.sort((e1, e2) -> compare(e1[0], e2[0]));

    Object[][] arr = new Object[data.size()][HEADERS.length];
    for (int i = 0; i < data.size(); ++i)
      arr[i] = data.get(i);

    return arr;
  }

  /**
   * Compare two objects: o1 and o2 can be Strings, or ArrayList<String>s
   * @param o1
   * @param o2
   * @return
   */
  private int compare(Object o1, Object o2) {
    String s1 = null,
            s2 = null;
    if (o1 instanceof String)
      s1 = (String) o1;
    else if (o1 instanceof ArrayList)
      s1 = (String) ((ArrayList)o1).get(0);
    if (o2 instanceof String)
      s2 = (String) o2;
    else if (o2 instanceof ArrayList)
      s2 = (String) ((ArrayList)o2).get(0);

    if (s1 == null || s2 == null)
      return 0;

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
    if (isClosed())
      this.closed.setText("✗");
    else
      this.closed.setText("o");
  }

  public boolean isClosed() {
    return !truthAssignment.isConsistent() || !truthAssignment.areParentsConsistent();
  }

  public boolean isFinished() {
    return prover == null || prover.reasoningCompleted();
  }

  public Stream<NodePanel> getLeaves() {
    if (children.isEmpty())
      return Stream.of(this);
    return children.stream().flatMap(NodePanel::getLeaves);
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
      if (e.isShiftDown() || e.isControlDown()) {
        nodePanel.moveBranch(e.getXOnScreen() - x,
                e.getYOnScreen() - y);
      } else {
        nodePanel.movePanel(e.getXOnScreen() - x,
                e.getYOnScreen() - y);
      }

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
        Object justification = jTable.getModel().getValueAt(row, 0);
        String justificationString = null;
        if (justification instanceof String)
          justificationString = (String) justification;
        else if (justification instanceof List)
          justificationString = ((List) justification).get(0).toString();
        if (justificationString == null)
          return;
        if (justificationString.startsWith("P"))
          justificationString = justificationString.replace("P", "-");
        int justificationNum = Integer.parseInt(justificationString);

        if (!truthAssignment.isDecomposed(s)) {
          if (s instanceof ForAll && truthAssignment.models(s))
            jTable.getModel().setValueAt(truthAssignment.getTruthValue(s).getInstantiatedConstants(), row, 4);
          else
            jTable.getModel().setValueAt('✓', row, 4);
          handleInference(s.reason(truthAssignment, prover.getInferenceCount(), justificationNum));
        }
      }
    }

    private void handleInference(Inference i) {
      prover.incrementInferenceCount();
      DefaultTableModel model = (DefaultTableModel) jTable.getModel();

      if (i instanceof Decomposition || i instanceof UniversalInstantiation) {
        // for decompositions, add the rows and resize the NodePanel
        int oldHeight = getHeight();
        prover.infer(i).forEach(p -> {
          OptionalInt optInt = IntStream.range(0, model.getRowCount()).filter(row
                  -> model.getValueAt(row, 1).equals(p.sentence)).findFirst();
          if (!optInt.isPresent()) {
            model.addRow(makeRow(i.getInferenceNum(),
                    i.getJustificationNum(), truthAssignment.getTruthValue(p.sentence)));
          } else {
            int row = optInt.getAsInt();
            if (!(model.getValueAt(row, 0) instanceof ArrayList)) {
              ArrayList<String> inferenceNums = new ArrayList<>(),
                      justificationNums = new ArrayList<>();

              inferenceNums.add((String) model.getValueAt(row, 0));
              justificationNums.add((String) model.getValueAt(row, 3));
              model.setValueAt(inferenceNums, row, 0);
              model.setValueAt(justificationNums, row, 3);
              model.setValueAt(p.truthAssignment.getTruthValue(p.sentence).getValues()
                      .keySet().stream().map(b -> b ? 'T' : 'F').collect(Collectors.toList()), row, 2);
            }

            ((ArrayList) model.getValueAt(row, 0)).add(Integer.toString(i.getInferenceNum()));
            ((ArrayList) model.getValueAt(row, 3)).add(Integer.toString(i.getJustificationNum()));
          }
        });
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


      }

      new TreeLayout(graphPanel.getRoot()).run();
      prover.closeBranches();
      graphPanel.updateInferences();
      graphPanel.updateClosedBranches();
      graphPanel.repaint();
    }
  }
}
