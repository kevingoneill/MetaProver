package gui.truthtreevisualization;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import logicalreasoner.inference.Decomposition;
import logicalreasoner.inference.Inference;

public class TreeBranch extends JPanel {
  private static final long serialVersionUID = -5667752505914020168L;

  private Map<String, JLabel> statements;
  private Map<Inference, String> inferences;
  private List<TreeBranch> children;
  private TreeBranch parent;
  private boolean leftChild;
  private Point bottomAnchor;

  public TreeBranch() {
    this(null, false);
  }

  public TreeBranch(TreeBranch parent, boolean isLeft) {
    // gui things
    super();
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    this.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 3, true),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));

    // truth tree things
    statements = new LinkedHashMap<String, JLabel>();
    children = new ArrayList<TreeBranch>();
    inferences = new LinkedHashMap<Inference, String>();
    this.parent = parent;
    this.leftChild = isLeft;
    
    setVisible(true);
  }
  
  public void setInferences(List<Inference> infers) {
	  infers.forEach(i -> {
		  if (i.getOrigin() == null) {
			  ((Decomposition)i).getAdditions().keySet().forEach(sen -> {
				  // should only be one sentance	
				 inferences.put(i, sen.toSymbol()); 
			  });
		  } else {
			  inferences.put(i, i.getOrigin().toSymbol());
		  }
		  
	  });
	  children.forEach(c -> c.setInferences(infers));
  }
  
  public void placeStatements() {
	  Set<JLabel> addedLabels = new HashSet<JLabel>(); 
	  for (Inference i : inferences.keySet()) {
		  
		  String iOriginTrue = inferences.get(i) + " [true]";
		  String iOriginFalse = inferences.get(i) + " [false]";
		  
		  if (placeStatementHelper(i, iOriginTrue, addedLabels)) {}
		  else {placeStatementHelper(i, iOriginFalse, addedLabels);}
	  }

	  for (JLabel label : statements.values()) {
		  if (!addedLabels.contains(label)) {
			  this.add(label);
		  }
	  }
	  children.forEach(c -> c.placeStatements());
  }
  
  private boolean placeStatementHelper(Inference i, String origin, Set<JLabel> addedLabels) {
	 if (!statements.containsKey(origin)) {
	    return false;
	 }
	 
	 JLabel label = statements.get(origin);
	 String prefix = "";
	 if (i.getJustificationNum() == 0) {
		 int inference = i.getInferenceNum();
		 if (inference == 0) {
			 prefix = "Goal";
		 } else if (inference <= -1) {
			 prefix = "Premise " + (-1 * inference);
			 System.out.println("here");
		 }
		 if (!prefix.equals("")) {
			 label.setText(prefix + ". " + label.getText());
		 }
		 
		 this.add(label);
	  	 addedLabels.add(label);
	 }
	 
	 return true;
  }
  
  
  public void searchForBranchInference(String statement, int infNum) {
	  JLabel infResLabel = statements.get(statement + " [true]");
		 if (infResLabel == null) {
			 infResLabel = statements.get(statement + " [false]");
		 }
		 if (infResLabel != null) {
			 infResLabel.setText(infResLabel.getText() + " " + infNum);
		 } else {
			 children.forEach(c -> searchForBranchInference(statement, infNum));
		 }
  }

  public void addStatement(String s) {
    Statement sLabel = new Statement(s);
    statements.put(s, sLabel);
  }

  public void addChild(TreeBranch c) {
    c.setIsLeft(children.isEmpty()); // first child added is left child
    c.setParent(this);
    children.add(c);
  }

  public void setIsLeft(boolean isLeft) {
    this.leftChild = isLeft;
  }

  public void setParent(TreeBranch parent) {
    this.parent = parent;
  }

  public boolean isLeftChild() {
    return leftChild;
  }

  public TreeBranch getParent() {
    return parent;
  }

  public void setBottomAnchor(Point p) {
    bottomAnchor = p;
  }

  public Point getBottomAnchor() {
    if (bottomAnchor != null) {
      return bottomAnchor;
    } else {
      return new Point(0, 0);
    }
  }

  public List<TreeBranch> getChildren() {
    return new ArrayList<TreeBranch>(children);
  }

  public boolean isRoot() {
    return (parent == null);
  }

  public boolean isLeaf() {
    return children.isEmpty();
  }

  public Map<String, JLabel> getStatements() {
    return statements;
  }

  @Override
  public String toString() {
    String toReturn = "[";
    for (String s : statements.keySet()) {
      toReturn += s + ", ";
    }
    return toReturn + "]";
  }

  public void print(String prefix) {
    String output = prefix + (leftChild ? "[left] " : "[right] ") +
            statements.keySet().toString();
    System.out.println(output);
    children.forEach(child -> child.print(prefix + "\t"));
  }
}

class Statement extends JLabel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 924561991566707885L;

	Statement(String s) {
		super(s);
	    this.setOpaque(true);
	    if (s.equals("✓")) {
    	  this.setForeground(Color.GREEN);
    	} else if (s.equals("✗")) {
    	  this.setForeground(Color.RED);
    	}
//	    this.addMouseListener(this);
	    this.setFont(this.getFont().deriveFont(16.0f));
	}
}
