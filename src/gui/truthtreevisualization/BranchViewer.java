package gui.truthtreevisualization;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BranchViewer extends JPanel {
	private static final long serialVersionUID = 3750691110231960974L;
	
	private List<JLabel> statements;
	
	public BranchViewer(Set<String> initialStatements) {
		super();
		statements = new ArrayList<JLabel>();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		initialStatements.forEach(s -> this.addStatement(s));
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3, true));
	}
	
	public void addStatement(String s) {
		JLabel newStatement = new JLabel(s);
		newStatement.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		this.add(newStatement);
		statements.add(newStatement);
	}
}
