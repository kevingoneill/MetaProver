package gui;

import gui.truthtreevisualization.TreeBranch;
import gui.truthtreevisualization.TreeViewer;
import gui.truthtreevisualization.TruthTree;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Top level window that holds all graphical components of the main window
 */
public class SemanticProverGui extends JFrame {
	private static final long serialVersionUID = 2445713372827434324L;
	
	final static String TITLE = "Semantic Prover";
	final static int WIDTH = 1100;
	final static int HEIGHT = 400;
	
	private JMenuBar menuBar;
	private JTextArea textOutput;
	private JScrollPane scroll;
	private TreeViewer treePanel;
	private JPanel mainOutputPanel;
	private Controller controller;
	
	public SemanticProverGui() {
		super(TITLE);
		controller = new Controller();
		
		// mainWindow initialization
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());
		this.setLocation(100, 100);
//		this.setResizable(false);
		
		menuBar = initMenuBar();
		this.setJMenuBar(menuBar);
		
		textOutput = new JTextArea();
		textOutput.setEditable(false);
//		textOutput.setPreferredSize(new Dimension(WIDTH/2, HEIGHT));
		scroll = new JScrollPane(textOutput);
		scroll.setBorder(new TitledBorder(new EtchedBorder(), "Text Output"));
//		scroll.setPreferredSize(new Dimension(WIDTH/2, HEIGHT));
		
		TreeBranch root = new TreeBranch();
		root.addStatement("premise1");
		root.addStatement("premise2");
		TreeBranch branch1 = new TreeBranch();
		branch1.addStatement("branch1");
		root.addChild(branch1);
		TreeBranch branch11 = new TreeBranch();
		branch11.addStatement("branch1.1");
		TreeBranch branch12 = new TreeBranch();
		branch12.addStatement("branch1.2");
		branch1.addChild(branch11);
		branch1.addChild(branch12);
		TreeBranch branch2 = new TreeBranch();
		branch2.addStatement("branch2");
		branch2.addStatement("branch2");
		TreeBranch branch21 = new TreeBranch();
		branch21.addStatement("branch2.1");
		TreeBranch branch22 = new TreeBranch();
		branch22.addStatement("branch2.2");
		branch2.addChild(branch21);
		branch2.addChild(branch22);
		root.addChild(branch2);
		
		TruthTree tree = new TruthTree(root);
		System.out.println(root.getChildren());
		treePanel = new TreeViewer(null, WIDTH/2, HEIGHT);
		
		mainOutputPanel = new JPanel();
		mainOutputPanel.setLayout(new GridLayout(1, 2));
		mainOutputPanel.add(scroll);
		mainOutputPanel.add(treePanel);
		this.add(mainOutputPanel, BorderLayout.CENTER);
	}
	
	public void showWindow() {
		this.pack();
		this.setSize(WIDTH, HEIGHT);
		this.setVisible(true);
	}
	
	/**
	 * Initialize the menu bar
	 * @return The initialized JMenu
	 */
	private JMenuBar initMenuBar() {
		// menubar initialization
		JMenuBar m = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
				
		JMenuItem newProofButton = new JMenuItem("New Proof");
		newProofButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newProofDialog();
			}
		});
		
		fileMenu.add(newProofButton);
		m.add(fileMenu);
		
		return m;
	}
	
	/**
	 * Opens a JDialog which prompts the user for information on what they
	 * want to prove
	 */
	private void newProofDialog() {
		JDialog dialog = new JDialog(this, "New Proof", true);
		NewProofInputPanel panel = new NewProofInputPanel(this);
		dialog.getContentPane().add(panel);
		dialog.setSize(1000, 1000);
		dialog.setResizable(false);
		dialog.pack();
		dialog.setVisible(true);
	}
	
	protected void prove(ArrayList<String> premises, String goal, boolean meta) {
		ProofInfo proof = null;
		if (meta) {
			System.out.println("not implemented yet");
			//proof = controller.MetaProve(premises, goal);
		} else {
			proof = controller.TruthFunctionalProve(premises, goal);
		}
		if (proof == null) {
			System.out.println("still need to implement meta-proof truth trees");
			return;
		}
		textOutput.setText(proof.text);
		treePanel.addTree(proof.tree);
		this.setSize(new Dimension(getWidth(), getHeight()-1)); // stupid fix to show jlabels in truth tree, idk why, but it works
	}
}

/**
 * Allows the user to specify premises and goals to prove
 */
class NewProofInputPanel extends JPanel {
	private static final long serialVersionUID = 2358456949144956315L;
	

	public NewProofInputPanel(SemanticProverGui mainWindow) {
		this.setLayout(new BorderLayout());
		
		// create panel for premise/goal input
		JPanel inputPane = new JPanel();
		inputPane.setLayout(new BoxLayout(inputPane, BoxLayout.Y_AXIS)); // top level layout for newProofInputPanel
		JButton addPremiseButton =  new JButton("Add");
		JButton removePremiseButton = new JButton("Remove");
		DefaultListModel<String> addedModel = new DefaultListModel<String>();
		JList<String> addedList = new JList<String>(addedModel); // holds added premises
		addedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		addedList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				removePremiseButton.setEnabled(true);
			}
		});
		JScrollPane premiseScrollPane = new JScrollPane(addedList);
		JLabel premiseLabel = new JLabel("New Premise:");
		JTextField premiseInputField = new JTextField("Add Premise Here", 25);
		premiseInputField.addFocusListener(new FocusListener () {
			@Override
			public void focusGained(FocusEvent e) { 
				if (premiseInputField.getText().equals("Add Premise Here")) {
					premiseInputField.setText(""); 
				}
			}
			@Override
			public void focusLost(FocusEvent e) { 
				if (premiseInputField.getText().equals("")) {
					premiseInputField.setText("Add Premise Here");
				}
			}
		});
		premiseInputField.getDocument().addDocumentListener(new LatexCommands(premiseInputField));
		premiseLabel.setLabelFor(premiseInputField);
		removePremiseButton.setEnabled(false);
		removePremiseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addedModel.remove(addedList.getSelectedIndex());
				removePremiseButton.setEnabled(false);
			}
		});
		addPremiseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addedModel.addElement(premiseInputField.getText());
				premiseInputField.setText("Add Premise Here");
			}
		});
		JPanel addRemoveButtonPanel = new JPanel();
		addRemoveButtonPanel.setLayout(new BoxLayout(addRemoveButtonPanel, BoxLayout.Y_AXIS));
		addRemoveButtonPanel.add(addPremiseButton);
		addRemoveButtonPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		addRemoveButtonPanel.add(removePremiseButton);
		JPanel newPremisePanel = new JPanel(); // holds input box and add button
		newPremisePanel.add(premiseLabel);
		newPremisePanel.add(premiseInputField);
		newPremisePanel.add(addRemoveButtonPanel);
		JLabel goalLabel = new JLabel("New Goal:");
		JTextField goalInputField = new JTextField("Add Goal Here", 25);
		goalInputField.addFocusListener(new FocusListener () {
			@Override
			public void focusGained(FocusEvent e) {
				if (goalInputField.getText().equals("Add Goal Here")) {
					goalInputField.setText("");
				}
			}
			@Override
			public void focusLost(FocusEvent e) { 
				if (goalInputField.getText().equals("")) {
					goalInputField.setText("Add Goal Here");
				}
			}
		});
		goalInputField.getDocument().addDocumentListener(new LatexCommands(goalInputField));
		goalLabel.setLabelFor(goalInputField);
		JPanel newGoalPanel = new JPanel();
		newGoalPanel.add(goalLabel);
		newGoalPanel.add(goalInputField);
		inputPane.add(newPremisePanel);
		inputPane.add(Box.createRigidArea(new Dimension(0, 5)));
		inputPane.add(premiseScrollPane);
		inputPane.add(Box.createRigidArea(new Dimension(0, 5)));
		inputPane.add(newGoalPanel);
		inputPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		// create radio button to choose truth-functional or semantic proof
		JPanel proofTypeSelectPane = new JPanel();
		proofTypeSelectPane.setLayout(new BoxLayout(proofTypeSelectPane, BoxLayout.X_AXIS));
		proofTypeSelectPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		JRadioButton truthFunctionalOption = new JRadioButton("Truth Functional Proof");
		JRadioButton metaOption	= new JRadioButton("Meta-logical Proof");
		metaOption.setSelected(true);
		ButtonGroup bg = new ButtonGroup();
		bg.add(truthFunctionalOption);
		bg.add(metaOption);
		proofTypeSelectPane.add(Box.createHorizontalGlue());
		proofTypeSelectPane.add(metaOption);
		proofTypeSelectPane.add(Box.createRigidArea(new Dimension(10,0)));
		proofTypeSelectPane.add(truthFunctionalOption);
		proofTypeSelectPane.add(Box.createHorizontalGlue());
		
		// create panel for buttons
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		JButton proveButton = new JButton("Prove!");
		proveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeWindow();
				ArrayList<String> premises = new ArrayList<String>();
				for (int i = 0; i < addedModel.size(); i++) {
					premises.add(addedModel.getElementAt(i));
				}
				String goal = goalInputField.getText();
				boolean meta = metaOption.isSelected();
				mainWindow.prove(premises, goal, meta);
			}
		});
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeWindow();
			}
		});
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(cancelButton);
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPane.add(proveButton);
		
		// Combines proof type selection pane with button pane
		JPanel bottomPane = new JPanel();
		bottomPane.setLayout(new BoxLayout(bottomPane, BoxLayout.Y_AXIS));
		bottomPane.add(proofTypeSelectPane);
		bottomPane.add(buttonPane);
		
		// Add everything to encapsulating JPanel (this)
		this.add(inputPane, BorderLayout.CENTER);
		this.add(bottomPane, BorderLayout.PAGE_END);
	}
	
	private void closeWindow() {
		this.getRootPane().getParent().setVisible(false);
	}
	
	private class LatexCommands implements DocumentListener {
		private JTextField focus;
		private Map <String, String> greekLetters = new HashMap<String, String>() {
			private static final long serialVersionUID = -8954285668593228620L;
		{
			put("\\alpha", "α"); put("\\beta", "β"); put("\\gamma", "γ");
			put("\\delta", "δ"); put("\\epsilon", "ε"); put("\\zeta", "ζ"); 
			put("\\eta", "η"); put("\\theta", "θ"); put("\\iota", "ι");
			put("\\kappa", "κ"); put("\\lambda", "λ"); put("\\mu", "μ");
			put("\\nu", "ν"); put("\\xi", "ξ"); put("\\omicron", "ο");
			put("\\pi", "π"); put("\\rho", "ρ"); put("\\sigma", "σ");
			put("\\tau", "τ"); put("\\upsilon", "υ"); put("\\phi", "φ");
			put("\\chi", "χ"); put("\\psi", "ψ"); put("\\omega", "ω");
		}};
		
		public LatexCommands(JTextField focus) {
			this.focus = focus;
		}
		
		@Override
		public void insertUpdate(DocumentEvent e) {
			String input = focus.getText();
			char lastChar = input.charAt(input.length()-1);
			
			if (!Character.isLetter(lastChar)) {
				int i = input.length() - 2;
				while (i > 0 && input.charAt(i) != ' ' && input.charAt(i) != '\\') {
					i--;
				}
				if (i >= 0 && input.charAt(i) == '\\') {
					String command = input.substring(i, input.length()-1);
					String replacement = greekLetters.get(command);
					if (replacement != null) {
						String newString = input.substring(0, i).concat(replacement).concat(input.substring(input.length()-1, input.length()));
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								focus.setText(newString);
							}
						});
					}
				}
			}
		}
		
		@Override
		public void removeUpdate(DocumentEvent e) { }
		
		@Override
		public void changedUpdate(DocumentEvent e) { }
	}
}
