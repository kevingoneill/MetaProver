package gui;

import javax.swing.*;

/**
 * This main function creates a new GUI Window, and drives the whole program
 */
public class GUIMain {

  public static void main(String[] args) {
    /*
    Causes null pointer exception when vertices are moved...
    */
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      e.printStackTrace();
    }

    GUIWindow window = new GUIWindow();
  }
}
