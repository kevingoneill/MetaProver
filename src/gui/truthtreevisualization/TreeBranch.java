package gui.truthtreevisualization;

import java.util.LinkedHashSet;
import java.util.Set;

public class TreeBranch {
	private Set<String> statements;
	private Set<TreeBranch> children;
	
	public TreeBranch() {
		statements = new LinkedHashSet<String>();
		children = new LinkedHashSet<TreeBranch>();
	}
	
	public TreeBranch(TreeBranch t) {
		statements = new LinkedHashSet<String>(t.getStatements());
		children = new LinkedHashSet<TreeBranch>(t.getChildren());
	}
	
	public void addStatement(String s) {
		statements.add(s);
	}
	
	public void addChild(TreeBranch c) {
		children.add(c);
	}
	
	protected Set<String> getStatements() {
		return new LinkedHashSet<String>(statements);
	}
	
	private Set<TreeBranch> getChildren() {
		return new LinkedHashSet<TreeBranch>(children);
	}
	
	protected void print(String prefix) {
		String output = prefix + statements.toString();
		System.out.println(output);
		children.forEach(child -> child.print(prefix + "\t"));
	}
}
