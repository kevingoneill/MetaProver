package gui2;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import logicalreasoner.inference.Inference;
import logicalreasoner.prover.Prover;
import logicalreasoner.truthassignment.TruthAssignment;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * A TruthAssignmentPanel contains a JTable inside of a
 * JScrollPane, and is used to display TruthAssignmentData inside
 * of a single JComponent
 */
public class TruthAssignmentPanel extends JPanel {

  public static Prover prover;
  public static int COLUMNS = 5;
  public static String[] HEADERS = {"#", "Sentence", "", "Justification", ""};

  private TruthAssignment truthAssignment;
  private JTable jTable;
  private JLabel closed;

  public TruthAssignmentPanel(mxCell cell, TruthAssignment truthAssignment) {
    super(new BorderLayout());
    setOpaque(true);
    setBorder(BorderFactory.createEmptyBorder());
    this.truthAssignment = truthAssignment;

    jTable = new JTable(makeData(), HEADERS);
    jTable.getTableHeader().setDefaultRenderer(new TableHeaderRenderer());
    jTable.setOpaque(true);
    jTable.setBackground(Color.white);
    jTable.setGridColor(Color.black);
    jTable.setBorder(BorderFactory.createLineBorder(Color.black));

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
    //closed.setPreferredSize(new Dimension(getWidth(), closed.getPreferredSize().height));
    setClosed(!this.truthAssignment.isConsistent());

    add(jTable.getTableHeader(), BorderLayout.PAGE_START);
    add(jTable, BorderLayout.CENTER);
    add(closed, BorderLayout.SOUTH);

    setPreferredSize(new Dimension(getWidth(), getHeight()));
    cell.setGeometry(new mxGeometry(getX(), getY(), getWidth(), getHeight()));
  }

  public JTable getJTable() {
    return jTable;
  }

  public int getWidth() {
    return jTable.getPreferredSize().width;
  }

  public int getHeight() {
    return jTable.getPreferredSize().height + jTable.getTableHeader().getPreferredSize().height + closed.getPreferredSize().height;
  }

  public Object[][] makeData() {
    ArrayList<Object[]> data = new ArrayList<>();
    truthAssignment.stream().forEach(v -> {
      int inferenceNum = v.getInferenceNum(v.isModelled());
      Inference inference = v.getJustification(inferenceNum);
      data.add(new Object[]{
              inferenceNum > 0 ? Integer.toString(inferenceNum) : "P" + (-1 * inferenceNum),
              v.getSentence().toString(),
              v.isModelled() ? 'T' : 'F',
              inference.getJustificationNum() > 0 ? inference.getJustificationNum() : "P" + (-1 * inference.getJustificationNum()),
              (v.isDecomposed() && prover.getBranchQueue().stream().noneMatch(b -> b.getOrigin() == v.getSentence())) ? '✓' : '✗'
      });
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

  public void setClosed(boolean closed) {
    if (closed)
      this.closed.setText("✗");
    else
      this.closed.setText("o");
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
      //setBorder(BorderFactory.createEtchedBorder(Color.white, Color.black));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      setText(value.toString());
      return this;
    }
  }
}
