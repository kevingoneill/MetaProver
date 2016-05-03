package gui.truthtreevisualization;

import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import logicalreasoner.inference.Branch;
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
		  if (statements.containsKey(iOriginTrue)) {
			 this.add(statements.get(iOriginTrue));
			 JLabel label = statements.get(iOriginTrue);
			 String prefix = "";// = ((Integer)i.getInferenceNum()).toString();
			 String suffix = "";
			 if (i.getJustificationNum() == 0) {
				 if (i.getInferenceNum() == 0) {
					 prefix = "Goal";
				 } else {
					 prefix = "Premise " + (-1 * i.getInferenceNum());
				 }
//				 label.setText(prefix + ". " + label.getText());// + "  " + (i.getJustificationNum() < 0 ? "Premise" : ""));
			  	 addedLabels.add(label);
			 }
			 else if (i.getJustificationNum() != 0) {
			  	 if (i instanceof Decomposition) {
					 ((Decomposition) i).getAdditions().keySet().forEach(s -> {
						 String inferenceResult = s.toSymbol();
						 JLabel infResLabel = statements.get(inferenceResult + " [true]");
						 if (infResLabel == null) {
							 infResLabel = statements.get(inferenceResult + " [false]");
						 }
						 if (infResLabel != null) {
//							 infResLabel.setText(infResLabel.getText() + " " + ((Decomposition) i).getJustificationNum());
						 }
					 });
				 }
			  	
			 }
			 

//		  	 if (i instanceof Branch) {
//		  		 ((Branch) i).getBranches().forEach(branch -> {
//		  			 branch.keySet().forEach(s -> {
//		  				 children.forEach(child -> {
//							 String inferenceResult = s.toSymbol();
//							 child.searchForBranchInference(inferenceResult, ((Branch) i).getJustificationNum());
//		  				 });
//		  			 });
//		  		 });
//		  	 }
//		  	++num;
		  }
		  
		  if (statements.containsKey(iOriginFalse)) {
			 this.add(statements.get(iOriginFalse));
			 JLabel label = statements.get(iOriginFalse);
			 String prefix;// = ((Integer)i.getInferenceNum()).toString();
			 String suffix = "";
			 if (i.getJustificationNum() == 0) {
				 if (i.getInferenceNum() == 0) {
					 prefix = "Goal";
				 } else {
					 prefix = "Premise " + (-1 * i.getInferenceNum());
				 }
//				 label.setText(prefix + ". " + label.getText());// + "  " + (i.getJustificationNum() < 0 ? "Premise" : ""));
			  	 addedLabels.add(label);
			 }
			 else if (i.getJustificationNum() != 0) {
			  	 if (i instanceof Decomposition) {
					 ((Decomposition) i).getAdditions().keySet().forEach(s -> {
						 String inferenceResult = s.toSymbol();
						 JLabel infResLabel = statements.get(inferenceResult + " [true]");
						 if (infResLabel == null) {
							 infResLabel = statements.get(inferenceResult + " [false]");
						 }
						 if (infResLabel != null) {
//							 infResLabel.setText(infResLabel.getText() + " " + ((Decomposition) i).getJustificationNum());
						 }
					 });
				 }
			  	
			 }

//			 if (i instanceof Branch) {
//		  		 ((Branch) i).getBranches().forEach(branch -> {
//		  			 branch.keySet().forEach(s -> {
//		  				 children.forEach(child -> {
//							 String inferenceResult = s.toSymbol();
//							 child.searchForBranchInference(inferenceResult, ((Branch) i).getJustificationNum());
//		  				 });
//		  			 });
//		  		 });
//		  	 }
//			 ++num;
		  }
		  
		  
		  
	  }
	  for (JLabel label : statements.values()) {
		  if (!addedLabels.contains(label)) {
//			  if (!(label.getText().equals("✓") || label.getText().equals("✗"))) {
//				  label.setText(num + ". " + label.getText());
//			  }
			  this.add(label);
//			  ++num;
		  }
	  }
	  children.forEach(c -> c.placeStatements());
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
    JLabel sLabel = new JLabel(s);
    sLabel.setOpaque(true);
    if (s.equals("✓")) {
      sLabel.setForeground(Color.GREEN);
    } else if (s.equals("✗")) {
      sLabel.setForeground(Color.RED);
    }
//		sLabel.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseEntered(MouseEvent e) {
//				System.out.println("entered");
//				sLabel.setBackground(Color.CYAN);
//				sLabel.setText("hi");
////				sLabel.repaint();
//			}
//
//			@Override
//			public void mouseExited(MouseEvent e) {
//				System.out.println("exited");
//				sLabel.setBackground(Color.white);
//				sLabel.setText("bye");
////				sLabel.repaint();
//			}
//		});
    sLabel.setFont(sLabel.getFont().deriveFont(16.0f));
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

//	@Override
//	public void paintComponent(Graphics g) {
//		super.paintComponent(g);
//		
//		for (JLabel s : statements.values()) {
//			s.revalidate();
//			s.repaint();
//		}
//	}

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
