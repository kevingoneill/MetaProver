package gui.truthtreevisualization;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class TreeBranch extends JPanel {
	private static final long serialVersionUID = -5667752505914020168L;
	
	private Map<String, JLabel> statements;
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
		this.parent = parent;
		this.leftChild = isLeft;
	}
	
	public void addStatement(String s) {
		JLabel sLabel = new JLabel(s);
		sLabel.setFont(sLabel.getFont().deriveFont(16.0f));
		statements.put(s, sLabel);
		this.add(sLabel);
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
				statements.toString();
		System.out.println(output);
		children.forEach(child -> child.print(prefix + "\t"));
	}
}
