public class SudokuSolver {

  private int[][] sudokuMatrix;

  SudokuSolver(int[][] sudokuMatrix) {

    this.sudokuMatrix = sudokuMatrix;
  }

  /**
   * Gibt das Sudokufeld als Matrix zurück.
   *
   * @return Die Sudokumatrix.
   */
  int[][] getSudokuMatrix() {

    return this.sudokuMatrix;
  }

  /**
   * Setzt die gewünschte Sudokumatrix.
   *
   * @param sudokuMatrix Sudokumatrix.
   */
  void setSudokuMatrix(int[][] sudokuMatrix) {
    this.sudokuMatrix = sudokuMatrix;
  }

  /**
   * Prüfte ob das Sudoku nur einen Lösungsweg hat.
   *
   * @return true|false
   */
  boolean isUnique() {

    return this.solve() == 1;
  }

  /**
   * Hilfsfunktion
   *
   * @return Die Anzahl der gefundenen Lösungen.
   */
  int solve() {
    return this.solve(0, 0, 0);
  }

  /**
   * @param row Die Reihe in die eine Zahl eingesetzt werden soll.
   * @param column Die Spalte in die eine Zahl eingesetzt werden soll.
   * @param solutions Gefundene Lösungen
   * @return Die Anzahl der gefundenen Lösungen.
   */
  private int solve(int row, int column, int solutions) {
    // Prüft ob das Ende der Reihe erreicht wurde.
    if (row == SudokuController.SIZE) {
      // Wieder auf anfang
      row = 0;
      // Springt zur nächsten Spalte sofern möglich.
      if (++column == SudokuController.SIZE) {
        // Es wurde sowohl das Ende der Spalte als auch Reihe erreicht das Sudoku ist gelöst.
        return solutions + 1;
      }
    }
    if (sudokuMatrix[row][column] != 0) {
      return solve(row + 1, column, solutions);
    }
    // Probiert die mgölichen Zahlen aus und validiert diese.
    for (int number = 1; number <= 9 && solutions < 2; ++number) {
      if (validate(row, column, number)) {
        sudokuMatrix[row][column] = number;
        solutions = solve(row + 1, column, solutions);
      }
    }
    // Änderungen rückängig machen da wir ja nur prüfen und nicht lösen.
    sudokuMatrix[row][column] = 0;
    return solutions;
  }

  /**
   * Mathode zum befüllen des Sudokus
   *
   * @return True wenn es Fertig ist. False wenn der Lösungsweg falsch ist.
   */
  public boolean fill() {
    for (int row = 0; row < 9; row++) {
      for (int column = 0; column < 9; column++) {
        if (sudokuMatrix[row][column] == 0) {
          for (int number = 1; number <= 9; number++) {
            if (validate(row, column, number)) {
              sudokuMatrix[row][column] = number;
              if (fill()) {
                return true;
              } else {
                sudokuMatrix[row][column] = 0;
              }
            }
          }
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Überprüft ob die Nummer in dem gewählten Feld gegen die Regeln verstoßen würde.
   *
   * @param row Die zu prüfende Reihe.
   * @param column Die zu prüfende Spalte.
   * @param number Die zu prüfende Nummer.
   */
  private boolean validate(int row, int column, int number) {
    return !isNumberInRow(row, number) && !isNumberInColumn(column, number) && !isNumberInBlock(row, column, number);
  }

  /**
   * Überprüft ob die Nummer in der Reihe schon vorhanden ist.
   *
   * @param row Die zu prüfende Reihe.
   * @param number Die zu prüfende Nummer.
   * @return True wenn die Nummer schon vorhanden ist sonst False.
   */
  private boolean isNumberInRow(int row, int number) {
    for (int column = 0; column < SudokuController.SIZE; column++) {
      if (sudokuMatrix[row][column] == number) {
        return true;
      }
    }

    return false;
  }

  /**
   * Überprüft ob die Nummer in der Spalte schon vorhanden ist.
   *
   * @param column Die zu prüfende Spalte.
   * @param number Die zu prüfende Nummer.
   * @return True wenn die Nummer schon vorhanden ist sonst False.
   */
  private boolean isNumberInColumn(int column, int number) {
    for (int row = 0; row < SudokuController.SIZE; row++) {
      if (sudokuMatrix[row][column] == number) {
        return true;
      }
    }

    return false;
  }

  /**
   * Überprüft ob die Nummer in dem Block schon vorhanden ist.
   *
   * @param row Die zu prüfende Reihe.
   * @param column Die zu prüfende Spalte.
   * @param number Die zu prüfende Nummer.
   * @return True wenn die Nummer schon vorhanden ist sonst False.
   */
  private boolean isNumberInBlock(int row, int column, int number) {
    int r = row - row % (int) Math.sqrt(SudokuController.SIZE);
    int c = column - column % (int) Math.sqrt(SudokuController.SIZE);

    for (int i = r; i < r + 3; i++) {
      for (int j = c; j < c + 3; j++) {
        if (sudokuMatrix[i][j] == number) {
          return true;
        }
      }
    }
    return false;
  }
}
