import java.util.Arrays;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.text.Document;

class SudokuGUI {

  private JTextFieldLimit[][] textFieldMatrix = new JTextFieldLimit[SudokuController.SIZE][SudokuController.SIZE];
  private int[][] sudokuMatrix = new int[SudokuController.SIZE][SudokuController.SIZE];
  private int[][] sudokuMatrixSolution = new int[SudokuController.SIZE][SudokuController.SIZE];
  private SudokuController sudoku = new SudokuController();


  SudokuGUI() {
    this.initGUI();
    for (int row = 0; row < SudokuController.SIZE; row++) {
      for (int column = 0; column < SudokuController.SIZE; column++) {
        Document document = textFieldMatrix[row][column].getDocument();
        document.putProperty("row", row);
        document.putProperty("column", column);
        document.addDocumentListener(sudoku);
      }
    }
  }

  /**
   * Setzt die Lösunge des Sudokus als Matrix.
   *
   * @param sudokuMatrixSolution die Lösungsmatrix.
   */
  public void setSudokuMatrixSolution(int[][] sudokuMatrixSolution) {
    this.sudokuMatrixSolution = sudokuMatrixSolution;
  }

  /**
   * Setzt die Sudokumatrix.
   *
   * @param sudokuMatrix Die Sudokumatrix.
   */
  void setSudokuMatrix(int[][] sudokuMatrix) {
    this.sudokuMatrix = sudokuMatrix;
    this.transferMatrixToGui();
  }

  /**
   * Vergleich die Eingegebenen Zahlen mit der Lösung und markiert falsche Zahlen.
   *
   * @return True wenn das Sudoku gelöst wurde sonst False.
   */
  public boolean validateSudoku() {
    if (Arrays.deepEquals(sudokuMatrixSolution, sudokuMatrix)) {
      return true;
    }
    for (int row = 0; row < SudokuController.SIZE; row++) {
      for (int column = 0; column < SudokuController.SIZE; column++) {
        if (this.sudokuMatrix[row][column] != 0 && this.sudokuMatrix[row][column] != this.sudokuMatrixSolution[row][column]) {
          textFieldMatrix[row][column].setForeground(Color.red);
        }
      }
    }
    return false;
  }


  /**
   * Befüllt die Textfelder anhand der Sudokumatrix.
   */
  private void transferMatrixToGui() {
    for (int row = 0; row < SudokuController.SIZE; row++) {
      for (int column = 0; column < SudokuController.SIZE; column++) {
        textFieldMatrix[row][column].setText(Integer.toString(sudokuMatrix[row][column]));
        textFieldMatrix[row][column].setEditable(sudokuMatrix[row][column] == 0);
      }
    }
  }

  /**
   * Setzt den Wert für ein einzelnes Textfeld.
   *
   * @param row Reihe des Textfeldes.
   * @param column Spalte des Textfeldes.
   * @param value Der zu setzende Wert.
   */
  void setSudokuValue(int row, int column, int value) {
    this.sudokuMatrix[row][column] = value;
    this.textFieldMatrix[row][column].setForeground(Color.black);
  }

  /**
   * Inizalisiert die GUI-Elemente.
   */
  private void initGUI() {
    // Nimbus als LookAndFeel
    try {
      for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          UIManager.setLookAndFeel(info.getClassName());
          JFrame.setDefaultLookAndFeelDecorated(true);
          break;
        }
      }
    } catch (Exception e) {
      JFrame.setDefaultLookAndFeelDecorated(false);
    }

    JFrame window = new JFrame("Sudoku");
    // GridLayout
    JPanel board = new JPanel(new GridLayout(SudokuController.SIZE / 3, SudokuController.SIZE / 3));
    // Menü
    JMenuBar menuBar = new JMenuBar();
    // Start Button
    JButton startButton = new JButton(SudokuController.BUTTON_START);
    startButton.addActionListener(sudoku);
    menuBar.add(startButton);
    // Check Button
    JButton checkButton = new JButton(SudokuController.BUTTON_CHECK);
    checkButton.addActionListener(sudoku);
    menuBar.add(checkButton);

    JMenu menu = new JMenu("Einstellungen");
    menuBar.add(menu);
    JMenu submenu = new JMenu("Schwirigkeit");
    ButtonGroup group = new ButtonGroup();

    JRadioButtonMenuItem rbEasyMenuItem = new JRadioButtonMenuItem(SudokuController.EASY_STR);
    rbEasyMenuItem.setSelected(true);
    rbEasyMenuItem.setMnemonic(KeyEvent.VK_R);
    group.add(rbEasyMenuItem);
    rbEasyMenuItem.addItemListener(sudoku);
    submenu.add(rbEasyMenuItem);

    JRadioButtonMenuItem rbNormalMenuItem = new JRadioButtonMenuItem(SudokuController.NORMAL_STR);
    rbNormalMenuItem.setMnemonic(KeyEvent.VK_O);
    group.add(rbNormalMenuItem);
    rbNormalMenuItem.addItemListener(sudoku);
    rbNormalMenuItem.setSelected(true);
    submenu.add(rbNormalMenuItem);

    JRadioButtonMenuItem rbHardMenuItem = new JRadioButtonMenuItem(SudokuController.HARD_STR);
    rbHardMenuItem.setMnemonic(KeyEvent.VK_O);
    group.add(rbHardMenuItem);
    rbHardMenuItem.addItemListener(sudoku);
    submenu.add(rbHardMenuItem);

    menu.add(submenu);

    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setSize(400, 400);
    window.add(board);
    window.setJMenuBar(menuBar);

    Font font = new Font("SansSerif", Font.BOLD, 20);

    for (int row = 0; row < SudokuController.SIZE; row += 3) {
      for (int column = 0; column < SudokuController.SIZE; column += 3) {
        board.add(this.createBlock(row, column, font));
      }
    }

    window.validate();
    window.setVisible(true);
    board.setVisible(true);
  }

  /**
   * Erstellt ein Panel mit neun Textfeldern.
   *
   * @param paramRow Die Reihe des Textfeldes.
   * @param paramColumn Die Spalte des Textfeldes.
   * @param font Die Schriftart des Textfeldes.
   * @return Das Panel mit den neun Textfeldern
   */
  private JPanel createBlock(int paramRow, int paramColumn, Font font) {
    int row = paramRow - paramRow % (int) Math.sqrt(SudokuController.SIZE);
    int column = paramColumn - paramColumn % (int) Math.sqrt(SudokuController.SIZE);

    JPanel block = new JPanel(new GridLayout(3, 3));
    block.setBorder(BorderFactory.createLoweredBevelBorder());
    for (int i = row; i < row + 3; i++) {
      for (int j = column; j < column + 3; j++) {
        JTextFieldLimit textInput = new JTextFieldLimit(1);
        textInput.setFont(font);
        textInput.setHorizontalAlignment(JTextField.CENTER);
        block.add(textInput);
        textFieldMatrix[i][j] = textInput;
      }
    }
    return block;
  }
}
