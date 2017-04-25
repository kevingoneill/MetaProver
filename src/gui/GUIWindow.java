package gui;

import expression.metasentence.MetaSentence;
import expression.metasentence.TruthAssignmentVar;
import expression.sentence.Sentence;
import logicalreasoner.prover.FOLProver;
import logicalreasoner.prover.Prover;
import logicalreasoner.prover.ProverMain;
import logicalreasoner.truthassignment.TruthAssignment;
import metareasoner.MetaProver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * GUIWindow is a window which displays all proof information.
 */
public class GUIWindow extends JFrame {
  private JMenuBar menuBar;
  private JSplitPane splitPane;
  private JPanel proofPanel;
  private JScrollPane proofScrollPane;
  private GraphPanel graphPanel;

  public GUIWindow() {
    super("MetaProver");
    setVisible(true);
    setSize(1000, 800);
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    initMenu();
    graphPanel = new GraphPanel(this);
    graphPanel.setPreferredSize(new Dimension(1000, 600));

    proofPanel = new JPanel(new BorderLayout());
    proofPanel.setBackground(Color.white);
    proofPanel.setVisible(true);
    proofScrollPane = new JScrollPane(proofPanel);
    proofScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    proofScrollPane.setPreferredSize(new Dimension(1000, 200));

    JScrollPane scrollPane = new JScrollPane(graphPanel);
    scrollPane.setPreferredSize(this.getSize());

    splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, scrollPane, proofScrollPane);
    splitPane.setDividerLocation(600);
    splitPane.setResizeWeight(1);

    //splitPane.add(scrollPane, BorderLayout.CENTER);
    //splitPane.add(proofScrollPane, BorderLayout.SOUTH);

    add(splitPane);
    setVisible(true);
  }

  private void initMenu() {
    JMenuBar menuBar = new JMenuBar();
    JMenu file = new JMenu("file");
    JMenuItem newPropProof = new JMenuItem("New Propositional Proof");
    newPropProof.addActionListener((ActionEvent e) ->
            displayDialog(new NewProofPanel(this, NewProofPanel.PROPOSITIONAL_MODE)));
    file.add(newPropProof);

    JMenuItem newFOLProof = new JMenuItem("New FOL Proof");
    newFOLProof.addActionListener((ActionEvent e) ->
            displayDialog(new NewProofPanel(this, NewProofPanel.FOL_MODE)));
    file.add(newFOLProof);


    JMenuItem newMetaProof = new JMenuItem("New Meta-Proof");
    newMetaProof.addActionListener((ActionEvent e) ->
            displayDialog(new NewProofPanel(this, NewProofPanel.META_MODE)));
    file.add(newMetaProof);

    JMenuItem loadProof = new JMenuItem("Load Proof From File");
    loadProof.addActionListener((ActionEvent e) -> {
      selectMode();

      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
      if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        Set<Sentence> premises = new HashSet<>();
        Sentence goal = null;
        try {
          goal = ProverMain.readInputFile(fileChooser.getSelectedFile(), premises);
        } catch (FileNotFoundException e1) {
          e1.printStackTrace();
        }
        setProver(premises, goal, NewProofPanel.FOL_MODE);
      }

    });
    file.add(loadProof);


    newPropProof.setAccelerator(KeyStroke.getKeyStroke('N', InputEvent.CTRL_DOWN_MASK));
    newFOLProof.setAccelerator(KeyStroke.getKeyStroke('N', InputEvent.SHIFT_DOWN_MASK + InputEvent.CTRL_DOWN_MASK));
    loadProof.setAccelerator(KeyStroke.getKeyStroke('O', InputEvent.CTRL_DOWN_MASK));
    newMetaProof.setAccelerator(KeyStroke.getKeyStroke('M', InputEvent.CTRL_DOWN_MASK));
    menuBar.add(file);
    this.setJMenuBar(menuBar);
  }

  public JPanel getProofPanel() { return proofPanel; }

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

    // Prompt the user for auto mode/proof assistant mode
    if ((panel.getMode() == NewProofPanel.PROPOSITIONAL_MODE
            || panel.getMode() == NewProofPanel.FOL_MODE)
            && ! selectMode())
      return;

    JDialog dialog = new JDialog(this, title, true);
    dialog.getContentPane().add(panel);
    dialog.setSize(1000, 750);
    dialog.setResizable(true);
    dialog.setVisible(true);
  }

  protected boolean selectMode() {
      int n = JOptionPane.showOptionDialog(this, "Please select the proof mode: ",
              "Proof Mode", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Proof Assistant", "Auto-Generate Proof"},
              "Proof Assistant");
    if (n < 0)
      return false;

    if (n == 0)
        graphPanel.setProofAssistantMode();
    else
      graphPanel.setAutoMode();
    return true;
  }

  /**
   * Set the prover of this window
   * @param premises the premises of the proof
   * @param goal the goal of the proof
   * @param mode the expressivity of the proof (FOL or Propositional)
   */
  public void setProver(Set<Sentence> premises, Sentence goal, int mode) {
    proofPanel.removeAll();

    if (mode == NewProofPanel.PROPOSITIONAL_MODE)
      GraphPanel.prover = new Prover(premises, goal, false, 60);
    else
      GraphPanel.prover = new FOLProver(premises, goal, false, 60);
    if (graphPanel.isAutoMode())
      GraphPanel.prover.run();
    graphPanel.removeAll();
    graphPanel.setRoot(GraphPanel.prover.getTruthAssignment());

    graphPanel.updateInferences();

    if (graphPanel.isAutoMode())
      graphPanel.updateClosedBranches();
  }

  /**
   * Set the metaprover of this window
   * @param premises the premises
   * @param goal the goal
   */
  public void setProver(ArrayList<MetaSentence> premises, MetaSentence goal) {
    GraphPanel.prover = null;
    graphPanel.removeAll();
    proofPanel.removeAll();

    ByteArrayOutputStream stepStream = new ByteArrayOutputStream(),
            justificationStream = new ByteArrayOutputStream();
    MetaProver prover = new MetaProver(premises, goal,
            new PrintStream(stepStream), new PrintStream(justificationStream));

    prover.run();

    List<TruthAssignment> tas = prover.getTruthAssignments().values().stream()
            .map(TruthAssignmentVar::getTruthAssignment)
            .collect(Collectors.toList());
    graphPanel.makeTrees(tas);

    //prover.getTruthAssignments().values().forEach(v -> System.out.println(v.getInferences()));
    //tas.forEach(TruthAssignment::print);

    proofPanel.add(new JTextArea(stepStream.toString()), BorderLayout.WEST);
    JTextArea justifications = new JTextArea(justificationStream.toString());
    justifications.setPreferredSize(new Dimension(200, 200));
    proofPanel.add(justifications, BorderLayout.EAST);
    pack();
  }

  public void loadProof() {

  }


}