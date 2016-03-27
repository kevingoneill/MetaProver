package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Top level window that holds all graphical components of the main window
 */
public class SemanticProverGui {
	final static String TITLE = "Semantic Prover";
	final static int WIDTH = 600;
	final static int HEIGHT = 400;
	
	private JFrame mainWindow;
	private JMenuBar menuBar;
	private JTextArea proofOutput;
	private Controller controller;
	
	public SemanticProverGui() {
		controller = new Controller();
		
		// mainWindow initialization
		mainWindow = new JFrame(TITLE);
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.setLayout(new BorderLayout());
		
		menuBar = initMenuBar();
		mainWindow.setJMenuBar(menuBar);
		
		proofOutput = new JTextArea();
		mainWindow.add(proofOutput, BorderLayout.CENTER);
	}
	
	public void showWindow() {
		mainWindow.pack();
		mainWindow.setSize(WIDTH, HEIGHT);
		mainWindow.setVisible(true);
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
		JDialog dialog = new JDialog(mainWindow, "New Proof", true);
		NewProofInputPanel panel = new NewProofInputPanel(this);
		dialog.getContentPane().add(panel);
		dialog.setSize(1000, 1000);
		dialog.setResizable(false);
		dialog.pack();
		dialog.setVisible(true);
	}
	
	protected void prove(ArrayList<String> premises, String goal) {
		String proof = controller.MetaProve(premises, goal);
		proofOutput.setText(proof);
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
		inputPane.setLayout(new BoxLayout(inputPane, BoxLayout.Y_AXIS));
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
				premiseInputField.setText(""); 
			}
			@Override
			public void focusLost(FocusEvent e) { 
				if(premiseInputField.getText().equals("")) {
					premiseInputField.setText("Add Premise Here");
				}
			}
		});
		premiseLabel.setLabelFor(premiseInputField);
		removePremiseButton.setEnabled(false);
		removePremiseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addedModel.remove(addedList.getSelectedIndex());
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
				goalInputField.setText(""); 
			}
			@Override
			public void focusLost(FocusEvent e) { 
				if(goalInputField.getText().equals("")) {
					goalInputField.setText("Add Goal Here");
				}
			}
		});
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
				mainWindow.prove(premises, goal);
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
		
		// Add everything to encapsulating JPanel (this)
		this.add(inputPane, BorderLayout.CENTER);
		this.add(buttonPane, BorderLayout.PAGE_END);
	}
	
	private void closeWindow() {
		this.getRootPane().getParent().setVisible(false);
	}
}
