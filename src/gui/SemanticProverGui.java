package gui;

import gui.truthtreevisualization.TreeViewer;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Top level window that holds all graphical components of the main window
 */
public class SemanticProverGui extends JFrame {
  private static final long serialVersionUID = 2445713372827434324L;

  final static String TITLE = "Semantic Prover";
  final static int WIDTH = 1100;
  final static int HEIGHT = 400;

  private JMenuBar menuBar;
  private ShortcutButtonPanel shortcutButtons;
  private JTextArea textOutput;
  private JScrollPane scroll;
  private JTabbedPane treePanel;
  private JSplitPane mainOutputPanel;
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
    
    shortcutButtons = new ShortcutButtonPanel();

    textOutput = new JTextArea();
    textOutput.setEditable(false);
    scroll = new JScrollPane(textOutput);
    scroll.setBorder(new TitledBorder(new EtchedBorder(), "Text Output"));

    treePanel = new JTabbedPane();

    mainOutputPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll, treePanel);
    mainOutputPanel.setDividerLocation(500);
    this.add(shortcutButtons, BorderLayout.PAGE_START);
    this.add(mainOutputPanel, BorderLayout.CENTER);
  }

  public void showWindow() {
    this.pack();
    this.setSize(WIDTH, HEIGHT);
    this.setVisible(true);
  }

  /**
   * Initialize the menu bar
   *
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
  
  private void exampleProofDialog() {
	  JDialog dialog = new JDialog(this, "New Proof", true);
	  ExampleProofInputPanel panel = new ExampleProofInputPanel(this);
	  dialog.getContentPane().add(panel);
	  dialog.setSize(1000, 1000);
	  dialog.setResizable(false);
	  dialog.pack();
	  dialog.setVisible(true);
  }

  protected void prove(ArrayList<String> premises, String goal, boolean meta) {
    textOutput.setText(null);
    while (treePanel.getTabCount() > 0) {
    	treePanel.remove(0);
    }
    

    ProofInfo result = null;
    if (meta) {
      result = controller.MetaProve(premises, goal);
    } else {
      result = controller.TruthFunctionalProve(premises, goal);
    }
    final ProofInfo proof = result;
    textOutput.setText(proof.text);
    proof.trees.forEach((n, tt) -> {
    	treePanel.add(new TreeViewer(tt, n));
    });
    this.setSize(new Dimension(getWidth(), getHeight() - 1)); // stupid fix to show jlabels in truth tree, idk why, but it works
  }
    
  private class ShortcutButtonPanel extends JPanel {
		private static final long serialVersionUID = 5054744071773955105L;

		private JButton newProofButton;
		private JButton exampleProofButton;
		
		public ShortcutButtonPanel() {
			super();
			this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			newProofButton = new JButton("New Proof");
			newProofButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					newProofDialog();
				}
			});
			exampleProofButton = new JButton("Example Proofs");
			exampleProofButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					exampleProofDialog();
				}
			});
			this.add(newProofButton);
			this.add(exampleProofButton);
		}
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
    JButton addPremiseButton = new JButton("Add");
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
    premiseInputField.addFocusListener(new FocusListener() {
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
    goalInputField.addFocusListener(new FocusListener() {
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
    JRadioButton metaOption = new JRadioButton("Meta-logical Proof");
    metaOption.setSelected(true);
    ButtonGroup bg = new ButtonGroup();
    bg.add(truthFunctionalOption);
    bg.add(metaOption);
    proofTypeSelectPane.add(Box.createHorizontalGlue());
    proofTypeSelectPane.add(metaOption);
    proofTypeSelectPane.add(Box.createRigidArea(new Dimension(10, 0)));
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
    private Map<String, String> greekLetters = new HashMap<String, String>() {
      private static final long serialVersionUID = -8954285668593228620L;

      {
        put("\\alpha", "α");
        put("\\beta", "β");
        put("\\gamma", "γ");
        put("\\delta", "δ");
        put("\\epsilon", "ε");
        put("\\zeta", "ζ");
        put("\\eta", "η");
        put("\\theta", "θ");
        put("\\iota", "ι");
        put("\\kappa", "κ");
        put("\\lambda", "λ");
        put("\\mu", "μ");
        put("\\nu", "ν");
        put("\\xi", "ξ");
        put("\\omicron", "ο");
        put("\\pi", "π");
        put("\\rho", "ρ");
        put("\\sigma", "σ");
        put("\\tau", "τ");
        put("\\upsilon", "υ");
        put("\\phi", "φ");
        put("\\chi", "χ");
        put("\\psi", "ψ");
        put("\\omega", "ω");
      }
    };

    public LatexCommands(JTextField focus) {
      this.focus = focus;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
      String input = focus.getText();
      char lastChar = input.charAt(input.length() - 1);

      if (!Character.isLetter(lastChar)) {
        int i = input.length() - 2;
        while (i > 0 && input.charAt(i) != ' ' && input.charAt(i) != '\\') {
          i--;
        }
        if (i >= 0 && input.charAt(i) == '\\') {
          String command = input.substring(i, input.length() - 1);
          String replacement = greekLetters.get(command);
          if (replacement != null) {
            String newString = input.substring(0, i).concat(replacement).concat(input.substring(input.length() - 1, input.length()));
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
    public void removeUpdate(DocumentEvent e) {
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
    }
  }
}

class ExampleProofInputPanel extends JPanel{
	private static final long serialVersionUID = -7203033069586485578L;
	Map<String, ProofTemplate> truthFunctionalProofs;
	Map<String, ProofTemplate> metaProofs;
	boolean meta;
	public ExampleProofInputPanel(SemanticProverGui mainWindow) {
		this.setLayout(new BorderLayout());
		meta = false;
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));
		
		// create the selection lists
		truthFunctionalProofs = new HashMap<String, ProofTemplate>();
		getTruthFuncProofs();
		metaProofs = new HashMap<String, ProofTemplate>();
		getMetaProofs();
		
		DefaultListModel<String> tfProofModel = new DefaultListModel<String>();
	    JList<String> tfProofList = new JList<String>(tfProofModel); // holds added premises
	    tfProofList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    tfProofList.addListSelectionListener(new ListSelectionListener() {
	      @Override
	      public void valueChanged(ListSelectionEvent e) {
	        
	      }
	    });
	    JScrollPane tfProofScrollPane = new JScrollPane(tfProofList);
	    truthFunctionalProofs.keySet().forEach(p -> tfProofModel.addElement(p));
	    
	    DefaultListModel<String> mProofModel = new DefaultListModel<String>();
	    JList<String> mProofList = new JList<String>(mProofModel); // holds added premises
	    mProofList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    mProofList.addListSelectionListener(new ListSelectionListener() {
	      @Override
	      public void valueChanged(ListSelectionEvent e) {
	        
	      }
	    });
	    JScrollPane mProofScrollPane = new JScrollPane(mProofList);
	    metaProofs.keySet().forEach(p -> mProofModel.addElement(p));
	    
	    final String tfLabel = "Truth Functional Proofs";
	    final String mLabel = "Meta Proofs";
	    JPanel proofSelectionPane = new JPanel(new CardLayout());
	    proofSelectionPane.add(tfProofScrollPane, tfLabel);
	    proofSelectionPane.add(mProofScrollPane, mLabel);
	    
	    JPanel comboBoxPane = new JPanel(); //use FlowLayout
	    String comboBoxItems[] = {tfLabel, mLabel};
	    JComboBox<String> cb = new JComboBox<String>(comboBoxItems);
	    cb.setEditable(false);
	    cb.addItemListener(new ItemListener() {
	    	@Override
	    	public void itemStateChanged(ItemEvent evt) {
		        CardLayout cl = (CardLayout)(proofSelectionPane.getLayout());
		        cl.show(proofSelectionPane, (String)evt.getItem());
		        if (((String)evt.getItem()).equals(mLabel)) {
		        	meta = true;
		        } else { 
		        	meta = false; 
		        }
		    }
	    });
	    comboBoxPane.add(cb);
	    JPanel selectionPaneWithCBox = new JPanel();
	    selectionPaneWithCBox.setLayout(new BoxLayout(selectionPaneWithCBox, BoxLayout.Y_AXIS));
	    selectionPaneWithCBox.add(comboBoxPane);
	    selectionPaneWithCBox.add(proofSelectionPane);
	    
	    
	    // create panel for buttons
	    JPanel buttonPane = new JPanel();
	    buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
	    buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
	    JButton proveButton = new JButton("Prove!");
	    proveButton.addActionListener(new ActionListener() {
	      @Override
	      public void actionPerformed(ActionEvent e) {
	        closeWindow();
	        
	        ProofTemplate currTemp;
	        if (meta) {
	        	currTemp = metaProofs.get(mProofList.getSelectedValue());
	        } else {
	        	currTemp = truthFunctionalProofs.get(tfProofList.getSelectedValue());
	        }
	        ArrayList<String> premises = new ArrayList<String>();
	        for (String premise : currTemp.premises) {
	          premises.add(premise);
	        }
	        String goal = currTemp.goal;
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
	    
	    
	    centerPanel.add(selectionPaneWithCBox);
	    
	    this.add(centerPanel, BorderLayout.CENTER);
	    this.add(buttonPane, BorderLayout.PAGE_END);
	}
	
	private void closeWindow() {
	  this.getRootPane().getParent().setVisible(false);
	}
	
	private void getTruthFuncProofs() {
		Set<String> premises = new HashSet<String>();
		premises.add("(implies A (and B C))");
	    premises.add("(iff C B)");
	    premises.add("(not C)");
		truthFunctionalProofs.put("Problem 1A", new ProofTemplate(premises, "(not A)"));
		premises.clear();
		
		premises.add("(implies (or J M) (not (and J M)))");
	    premises.add("(iff M (implies M J))");
		truthFunctionalProofs.put("Problem 8A", new ProofTemplate(premises, "(implies M J)"));
		premises.clear();
		
		premises.add("(and B (or H Z))");
	    premises.add("(implies (not Z) K)");
	    premises.add("(implies (iff B Z) (not Z))");
	    premises.add("(not K)");
		truthFunctionalProofs.put("Problem 15A", new ProofTemplate(premises, "(and M N)"));
		premises.clear();
		
		premises.add("(or (iff G H) (iff (not G) H))");
		truthFunctionalProofs.put("Problem 15A", new ProofTemplate(premises, "(or (iff (not G) (not H)) (not (iff G H)))"));
		premises.clear();
	
		premises.add("(iff K (not L))");
	    premises.add("(not (and L (not K)))");
	    truthFunctionalProofs.put("Problem 5B", new ProofTemplate(premises, "(implies K L)"));
		premises.clear();
		
		premises.add("(implies J (implies K L))");
	    premises.add("(implies K (implies J L))");
	    truthFunctionalProofs.put("Problem 10B", new ProofTemplate(premises, "(implies (or J K) L)"));
		premises.clear();

		premises.add("(implies W X)");
	    premises.add("(implies X W)");
	    premises.add("(implies X Y)");
	    premises.add("(implies Y X)");
	    truthFunctionalProofs.put("Problem 17B", new ProofTemplate(premises, "(iff W Y)"));
	    premises.clear();
	    
	    premises.add("(and (or A B) (not C))");
	    premises.add("(implies (not C) (and D (not A)))");
	    premises.add("(implies B (or A E))");
	    truthFunctionalProofs.put("Problem 17B", new ProofTemplate(premises, "(or E F)"));
	    premises.clear();
	    
	    premises.add("(implies (or (not A) B) (not (and C D)))");
	    premises.add("(implies (and A C) E)");
	    premises.add("(and A (not E))");
	    truthFunctionalProofs.put("Problem 20D", new ProofTemplate(premises, "(not (or D E))"));
	    premises.clear();
	}
	
	private void getMetaProofs() {
		Set<String> premises = new HashSet<String>();
		premises.add("[EQUIVALENT φ ψ]");
	    metaProofs.put("Example 1", new ProofTemplate(premises, "[AND [SUBSUMES φ ψ] [SUBSUMES ψ φ]]"));
	    premises.clear();
	    
	    premises.add("[IS φ TAUTOLOGY]");
	    metaProofs.put("Example 2", new ProofTemplate(premises, "[IS (not φ) CONTRADICTION]"));
	    premises.clear();
	    
	    premises.add("[IS φ CONTRADICTION]");
	    metaProofs.put("Example 3", new ProofTemplate(premises, "[IS (not φ) TAUTOLOGY]"));
	    premises.clear();

	    premises.add("[IS φ CONTINGENCY]");
	    metaProofs.put("Example 4", new ProofTemplate(premises, "[IS (not φ) CONTINGENCY]"));
	    premises.clear();
	    
	    premises.add("[IS ψ TAUTOLOGY]");
	    metaProofs.put("Example 5", new ProofTemplate(premises, "[SUBSUMES true ψ]"));
	    premises.clear();
	    
	    premises.add("[IS φ CONTRADICTION]");
	    metaProofs.put("Example 6", new ProofTemplate(premises, "[SUBSUMES φ false]"));
	    premises.clear();
	    
	    premises.add("[IS (implies φ ψ) TAUTOLOGY]");
	    metaProofs.put("Example 7", new ProofTemplate(premises, "[SUBSUMES φ ψ]"));
	    premises.clear();
	    
	    premises.add("[CONTRADICTORY φ ψ]");
	    metaProofs.put("Example 8", new ProofTemplate(premises, "[AND [CONTRARY φ ψ] [SUBCONTRARY φ ψ]]"));
	    premises.clear();
	    
	    premises.add("[EQUIVALENT φ ψ]");
	    metaProofs.put("Example 9", new ProofTemplate(premises, "[CONTRADICTORY φ (not ψ)]"));
	    premises.clear();
	    
	    premises.add("[SUBSUMES φ ψ]");
	    metaProofs.put("Example 10", new ProofTemplate(premises, "[SUBSUMES (not ψ) (not φ)]"));
	    premises.clear();
	}
	
	private class ProofTemplate {
		Set<String> premises;
		String goal;
		
		ProofTemplate(Set<String> premises, String goal) {
			this.premises = new LinkedHashSet<String>(premises);
			this.goal = new String(goal);
		}
		
	}
}
