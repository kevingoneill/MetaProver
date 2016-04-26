package gui.truthtreevisualization;

import java.awt.Color;
import java.awt.Dimension;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.JPanel;

public class TreeViewer extends JPanel{
	private static final long serialVersionUID = -3510274108984179880L;

	private int width, height;
	private TruthTree tree;
	private Set<BranchViewer> branches;
	
	public TreeViewer(TruthTree t, int w, int h) {
		super();
		setOpaque(false);
		setBackground(new Color(0,0,0,0));
		setLayout(null);
		width = w;
		height = h;
		setPreferredSize(new Dimension(width, height));
		tree = t;
		branches = new LinkedHashSet<BranchViewer>();
		int x = width / 2;
		int y = height / 2;
		drawBranch(new BranchViewer(tree.getRoot().getStatements()), x, y);
	}
	
	private void drawBranch(BranchViewer branch, int x, int y) {
		branches.add(branch);
		this.add(branch);
		Dimension size = branch.getPreferredSize();
		branch.setBounds(x, y, size.width, size.height);
	}
}