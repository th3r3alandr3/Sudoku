import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

public class SudokuController implements ActionListener, ItemListener, DocumentListener {

  static final String EASY_STR = "Einfach";
  static final String NORMAL_STR = "Normal";
  static final String HARD_STR = "Schwer";

  static final int EASY = 0;
  static final int NORMAL = 1;
  static final int HARD = 2;

  static final String BUTTON_START = "Start";
  static final String BUTTON_CHECK = "Prüfen";

  static final int SIZE = 9;

  private static SudokuGUI gui;
  private int difficulty = NORMAL;

  /**
   * Main
   */
  public static void main(String[] args) {
    gui = new SudokuGUI();
  }

  /**
   * Clickhandler Buttons
   *
   * @param e Event
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    switch (e.getActionCommand()) {
      case SudokuController.BUTTON_START:
        int[][] matrix;
        SudokuGenerator generator;
        do {
          generator = new SudokuGenerator(this.difficulty);
          matrix = generator.generateSudoku();
        } while (this.difficulty != generator.getDifficulty());

        gui.setSudokuMatrixSolution(generator.getSudokuMatrixSolution());
        gui.setSudokuMatrix(matrix);
        break;
      case SudokuController.BUTTON_CHECK:
        if (gui.validateSudoku()) {
          JOptionPane.showMessageDialog(new JFrame(), "Sudoku gelöst!");
        }
        break;
    }
  }

  /**
   * Handler für die Radiobuttons
   *
   * @param e Event
   */
  public void itemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) {
      switch (((JRadioButtonMenuItem) e.getItem()).getText()) {
        case SudokuController.EASY_STR:
          difficulty = SudokuController.EASY;
          break;
        case SudokuController.NORMAL_STR:
          difficulty = SudokuController.NORMAL;
          break;
        case SudokuController.HARD_STR:
          difficulty = SudokuController.HARD;
          break;
      }
    }
  }

  /**
   * Wird bei der Eingabe eines Wertes in die Textfelder ausgelöst.
   *
   * @param e Event
   */
  public void insertUpdate(DocumentEvent e) {
    try {
      Document document = e.getDocument();
      int row = (int) document.getProperty("row");
      int column = (int) document.getProperty("column");
      int value = Integer.parseInt(document.getText(0, document.getLength()));
      gui.setSudokuValue(row, column, value);
    } catch (Exception ex) {
      System.err.println(ex.getMessage());
    }
  }

  /**
   * Wird beim Löschen eines Wertes aus den Textfelder ausgelöst.
   *
   * @param e Event
   */
  public void removeUpdate(DocumentEvent e) {
    try {
      Document document = e.getDocument();
      int row = (int) document.getProperty("row");
      int column = (int) document.getProperty("column");
      gui.setSudokuValue(row, column, 0);
    } catch (Exception ex) {
      System.err.println(ex.getMessage());
    }
  }

  /**
   * Der vollständigkeit Halber
   *
   * @param e Event
   */
  public void changedUpdate(DocumentEvent e) {
    //Useless
  }
}