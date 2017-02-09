package gui2;

import logicalreasoner.prover.SemanticProver;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;

/**
 * GUIWindow is a window which displays all proof information.
 */
public class GUIWindow extends JFrame {
  private JMenuBar menuBar;
  private Runnable prover;
  private GraphPanel graphPanel;

  public GUIWindow() {
    super("SemanticProver");
    setSize(1000, 750);
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    initMenu();
    graphPanel = new GraphPanel();
    add(graphPanel);
    setVisible(true);
  }

  private void initMenu() {
    JMenuBar menuBar = new JMenuBar();
    JMenu file = new JMenu("file");
    JMenuItem newPropProof = new JMenuItem("New Propositional Proof");
    newPropProof.addActionListener((ActionEvent e) ->
            displayDialog(new NewProofPanel(this, NewProofPanel.PROPOSITIONAL_MODE)));
    JMenuItem newPropProofFromFile = new JMenuItem("New Propositional Proof From File");
    file.add(newPropProof);
    file.add(newPropProofFromFile);

    JMenuItem newFOLProof = new JMenuItem("New First-Order Proof");
    newFOLProof.addActionListener((ActionEvent e) ->
            displayDialog(new NewProofPanel(this, NewProofPanel.FOL_MODE)));
    JMenuItem newFOLProofFromFile = new JMenuItem("New Proof From File");
    file.add(newFOLProof);
    file.add(newFOLProofFromFile);

    JMenuItem newMetaProof = new JMenuItem("New Meta-Proof");
    newMetaProof.addActionListener((ActionEvent e) ->
            displayDialog(new NewProofPanel(this, NewProofPanel.META_MODE)));
    JMenuItem newMetaProofFromFile = new JMenuItem("New MetaProof From File");
    file.add(newMetaProof);
    file.add(newMetaProofFromFile);

    newPropProof.setAccelerator(KeyStroke.getKeyStroke('N', InputEvent.CTRL_DOWN_MASK));
    menuBar.add(file);
    this.setJMenuBar(menuBar);
  }

  private void displayDialog(NewProofPanel panel) {
    String title = null;
    if (panel.getMode() == NewProofPanel.PROPOSITIONAL_MODE)
      title = "New Propositional Proof";
    else if (panel.getMode() == NewProofPanel.FOL_MODE)
      title = "New FOL Proof";
    else if (panel.getMode() == NewProofPanel.META_MODE)
      title = "New Meta-Proof";

    if (title == null)
      throw new RuntimeException("Invalid proof mode");

    JDialog dialog = new JDialog(this, title, true);
    dialog.getContentPane().add(panel);
    dialog.setSize(1000, 750);
    dialog.setResizable(true);
    dialog.setVisible(true);
  }

  /**
   * Set the prover of this window
   *
   * @param r the SemanticProver or MetaProver to run
   */
  public void setProver(SemanticProver r) {
    this.prover = r;
    prover.run();
    graphPanel.makeNode(r.getTruthAssignment());
  }


}