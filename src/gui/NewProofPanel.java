package gui;

import expression.Sort;
import expression.metasentence.MetaSentence;
import expression.metasentence.MetaSentenceReader;
import expression.sentence.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by kevin on 1/19/17.
 */
public class NewProofPanel extends JPanel {
  public static int PROPOSITIONAL_MODE = 0,
          FOL_MODE = 1,
          META_MODE = 2;

  private GUIWindow window;

  private int mode;
  private JPanel declPanel, premisePanel;
  private JTextField declField, premiseField, goalField;
  private JButton addDeclButton, removeDeclButton,
          addPremiseButton, removePremiseButton, runProofButton;
  private JList<String> declList, premiseList;
  private DefaultListModel<String> declListModel, premiseListModel;

  public NewProofPanel(GUIWindow window, int mode) {
    super();
    this.window = window;
    this.mode = mode;
    this.setLayout(new GridLayout(0, 2));

    declPanel = initDeclPanel();

    premisePanel = new JPanel(new BorderLayout());
    premisePanel.add(initPremisePanel(), BorderLayout.NORTH);
    premiseListModel = new DefaultListModel<>();
    premiseList = new JList<>(premiseListModel);
    JScrollPane premiseScrollPane = new JScrollPane(premiseList);
    premisePanel.add(premiseScrollPane);
    premisePanel.add(initGoalPanel(), BorderLayout.SOUTH);

    this.add(declPanel);
    this.add(premisePanel);
  }

  public int getMode() {
    return mode;
  }

  private JPanel initDeclPanel() {
    JPanel panel = new JPanel(new BorderLayout()),
            npanel = new JPanel(new GridLayout(0, 1));

    npanel.add(new JLabel("Enter Sort and Function Declarations Here: "));
    declField = new JTextField();
    declField.setPreferredSize(new Dimension(250, 25));
    declField.addKeyListener(new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) {
      }

      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
          addDeclButton.doClick();
      }

      @Override
      public void keyReleased(KeyEvent e) {
      }
    });
    npanel.add(declField);
    declListModel = new DefaultListModel<>();

    addDeclButton = new JButton("Add Declaration");
    addDeclButton.addActionListener((ActionEvent e) -> {
      String decl = declField.getText();
      if (declListModel.contains(decl)) {
        JOptionPane.showMessageDialog(null, "ERROR: Declaration already entered.\n");
        return;
      }
      try {
        DeclarationParser.parseDeclaration(decl);
        declListModel.addElement(decl);
        declField.setText("");
        declField.requestFocus();
        declField.setCursor(Cursor.getDefaultCursor());
      } catch (ParserException pe) {
        JOptionPane.showMessageDialog(null, "ERROR: failed to parse declaration \""
                + decl + "\":\n" + pe.getMessage());
      }
    });
    removeDeclButton = new JButton("Remove Declaration");
    removeDeclButton.addActionListener((ActionEvent e) -> {
      int index = declList.getSelectedIndex();
      while (index >= 0) {
        removeDeclaration(index);
        index = declList.getSelectedIndex();
      }
    });

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(addDeclButton);
    buttonPanel.add(removeDeclButton);
    npanel.add(buttonPanel);
    panel.add(npanel, BorderLayout.NORTH);


    declList = new JList<>(declListModel);
    JScrollPane declScrollPane = new JScrollPane(declList);
    panel.add(declScrollPane, BorderLayout.CENTER);

    return panel;
  }

  private JPanel initPremisePanel() {
    JPanel panel = new JPanel(new GridLayout(0, 1));

    premiseField = new JTextField();
    premiseField.setPreferredSize(new Dimension(250, 25));
    premiseField.addKeyListener(new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) {
      }

      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
          addPremiseButton.doClick();
      }

      @Override
      public void keyReleased(KeyEvent e) {
      }
    });
    addPremiseButton = new JButton("Add Premise");
    addPremiseButton.addActionListener((ActionEvent e) -> {
      String expr = premiseField.getText();
      if (premiseListModel.contains(expr)) {
        JOptionPane.showMessageDialog(null, "ERROR: Premise already entered.\n");
        return;
      }
      try {
        Object s;
        if (mode != META_MODE)
          s = Sentence.makeSentence(expr);
        else
          s = MetaSentenceReader.parse(expr);
        if (mode == PROPOSITIONAL_MODE && ((Sentence) s).getSubSentences().anyMatch(Sentence::isQuantifier)) {
          JOptionPane.showMessageDialog(null, "ERROR: cannot use FOL statements in a \npropositional or meta-logical proof.");
        } else {
          premiseListModel.addElement(expr);
          premiseField.setText("");
          premiseField.requestFocus();
          premiseField.setCursor(Cursor.getDefaultCursor());
        }
      } catch (Exception exception) {
        JOptionPane.showMessageDialog(null, "ERROR: failed to parse expression \""
                + expr + "\":\n" + exception.getMessage());
      }
    });

    removePremiseButton = new JButton("Remove Premise");
    removePremiseButton.addActionListener((ActionEvent e) -> {
      int index = premiseList.getSelectedIndex();
      while (index >= 0) {
        premiseListModel.removeElementAt(index);
        index = premiseList.getSelectedIndex();
      }
    });

    panel.add(new JLabel("Enter Premise Here: "));
    panel.add(premiseField);

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(addPremiseButton);
    buttonPanel.add(removePremiseButton);
    panel.add(buttonPanel);
    return panel;
  }

  private JPanel initGoalPanel() {
    JPanel goalPanel = new JPanel(new GridLayout(0, 1));
    goalField = new JTextField();
    goalField.setPreferredSize(new Dimension(250, 25));
    goalField.addKeyListener(new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) {
      }

      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
          runProofButton.doClick();
      }

      @Override
      public void keyReleased(KeyEvent e) {
      }
    });

    runProofButton = new JButton("Run Proof");
    runProofButton.addActionListener((ActionEvent e) -> {
      String goalStr = goalField.getText();
      Set<Sentence> premises = null;
      Sentence goal = null;
      ArrayList<MetaSentence> metaPremises = null;
      MetaSentence metaGoal = null;

      try {
        if (mode == META_MODE) {
          Object m = MetaSentenceReader.parse(goalStr);
          if (!(m instanceof MetaSentence))
            throw new RuntimeException("ERROR: the goal must be a meta-logical statement.");
          metaGoal = (MetaSentence) m;
        } else
          goal = Sentence.makeSentence(goalStr);
      } catch (Exception exception) {
        JOptionPane.showMessageDialog(null, "ERROR: failed to parse goal \""
                + goalStr + "\":\n" + exception.getMessage());
        return;
      }

      if (mode == PROPOSITIONAL_MODE && goal != null && goal.getSubSentences().anyMatch(Sentence::isQuantifier)) {
        JOptionPane.showMessageDialog(null, "ERROR: cannot use FOL statements in a \npropositional or meta-logical proof.");
        return;
      }

      Object[] arr = premiseListModel.toArray();

      if (mode == META_MODE) {
        metaPremises = new ArrayList<>();
        MetaSentence s;
        for (int i = 0; i < arr.length; ++i) {
          try {
            s = (MetaSentence) MetaSentenceReader.parse((String) arr[i]);
            metaPremises.add(s);
          } catch (Exception exception) {
            JOptionPane.showMessageDialog(null, "ERROR: failed to parse expression \""
                    + arr[i] + "\":\n" + exception.getMessage());
            return;
          }
        }
      } else {
        Sentence s;
        premises = new HashSet<>();
        for (int i = 0; i < arr.length; ++i) {
          try {
            s = Sentence.makeSentence((String) arr[i]);
            premises.add(s);
          } catch (Exception exception) {
            JOptionPane.showMessageDialog(null, "ERROR: failed to parse expression \""
                    + arr[i] + "\":\n" + exception.getMessage());
            return;
          }
        }
      }

      Component c = SwingUtilities.getRoot(this);
      c.dispatchEvent(new WindowEvent((Window) c, WindowEvent.WINDOW_CLOSING));

      if (mode == META_MODE)
        window.setProver(metaPremises, metaGoal);
      else
        window.setProver(premises, goal, mode);
    });

    goalPanel.add(new JLabel("Enter Goal Here: "));
    goalPanel.add(goalField);
    goalPanel.add(runProofButton);
    return goalPanel;
  }

  private void removeDeclaration(int index) {
    removeDeclaration(index, true);

    for (int i = 0; i < premiseListModel.size(); ++i) {
      boolean parseException = false;
      try {
        Sentence s = Sentence.makeSentence(premiseListModel.getElementAt(i));
        System.out.println(s);
      } catch (AbstractSentenceReader.SentenceParseException e) {
        parseException = true;
        System.out.println("Failed to parse: " + premiseListModel.getElementAt(i));
      }

      if (parseException) {
        premiseListModel.remove(i);
        --i;
      }
    }
  }

  private int removeDeclaration(int index, boolean toplevel) {
    String s = declListModel.getElementAt(index);
    String name = DeclarationParser.getName(s);
    int dialog_result = JOptionPane.YES_OPTION;
    int remove_count = 0;

    if (DeclarationParser.isSortDeclaration(s)) {
      if (!Sort.isSort(name))
        throw new RuntimeException("Attempting to remove a non-existent sort");
      Sort sort = Sort.getSort(name);
      if (toplevel)
        dialog_result = JOptionPane.showConfirmDialog(null, "Removing Sort " + name
                        + " will remove all subsorts, and all functions and premises containing those sorts Continue?.",
                "Warning", JOptionPane.YES_NO_OPTION);

      if (dialog_result == JOptionPane.YES_OPTION) {
        //System.out.println("Removing " + declListModel.elementAt(index) + "\t" + Arrays.toString(declListModel.toArray()));
        declListModel.removeElementAt(index);
        ++remove_count;

        // Remove all subsorts & function declarations
        for (int i = 0; i < declListModel.size(); ++i) {
          String decl = declListModel.getElementAt(i);
          if ((DeclarationParser.isSortDeclaration(decl) && Sort.getSort(DeclarationParser.getName(decl)).isSubSort(sort))
                  || (!DeclarationParser.isSortDeclaration(decl) && Function.getDeclaration(DeclarationParser.getName(decl)).contains(sort))) {
            int removed = removeDeclaration(i, false);
            i -= removed;
            if (i < -1)
              i = -1;
            remove_count += removed;
          }
        }
      }
    } else {
      declListModel.removeElementAt(index);
      ++remove_count;
    }

    DeclarationParser.removeDeclaration(s);

    return remove_count;
  }

}
