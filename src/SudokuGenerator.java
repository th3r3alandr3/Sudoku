import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SudokuGenerator {

  private int difficulty;
  private int depth;
  private SudokuSolver solver = null;
  private int[][] sudokuMatrix = new int[SudokuController.SIZE][SudokuController.SIZE];
  private int[][] sudokuMatrixSolution = new int[SudokuController.SIZE][SudokuController.SIZE];
  private int counter;

  private List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);

  /**
   * Setzt den Schwierigkeitsgrad fest.
   *
   * @param targetDifficulty Der gewünschte Schwierigkeitsgrad.
   */
  public SudokuGenerator(int targetDifficulty) {
    switch (targetDifficulty) {
      case SudokuController.EASY:
        // Maximale Suchtiefe.
        this.depth = 81 - 33;
        break;
      case SudokuController.NORMAL:
        // Maximale Suchtiefe.
        this.depth = 81 - 25;
        break;
      case SudokuController.HARD:
        // Maximale Suchtiefe.
        this.depth = 81 - 17;
        break;
    }
  }

  /**
   * Gibt den Ermittelten Schwierigkeitsgrad zurück.
   */
  public int getDifficulty() {
    return difficulty;
  }

  /**
   * Erstellt das Sudoku.
   *
   * @return Das erstellte Sudoku.
   */
  int[][] generateSudoku() {
    // Fülle unabhängige Blöcke.
    List<Integer> list = this.list;
    for (int i = 0; i < SudokuController.SIZE; i += 3) {
      Collections.shuffle(list);
      setBlockByList(i, i, list);
    }
    this.solver = new SudokuSolver(sudokuMatrix);
    solver.fill();
    sudokuMatrix = solver.getSudokuMatrix();
    this.copyArray(sudokuMatrix, sudokuMatrixSolution);

    List<Integer> randomList = new ArrayList<>();
    for (int i = 0; i < Math.pow(SudokuController.SIZE, 2); i++) {
      randomList.add(i);
    }
    this.counter = 0;
    this.removeRandomNumber(this.depth, randomList);
    int[][] solveMatrix = new int[SudokuController.SIZE][SudokuController.SIZE];

    this.copyArray(sudokuMatrix, solveMatrix);
    HumanSolver humanSolver = new HumanSolver(solveMatrix);
    this.difficulty = humanSolver.getDifficulty();
    return sudokuMatrix;
  }

  /**
   * Gibt das gelöste Sudoku zurück.
   *
   * @return Das gelöste Sudoku.
   */
  public int[][] getSudokuMatrixSolution() {
    return this.sudokuMatrixSolution;
  }

  /**
   * Setzt die Werte für einen Block anhand einer Liste.
   *
   * @param paramRow Die Reihe des Blocks.
   * @param paramColumn Die Spalte des Blcoks.
   * @param list Die zu setzenden Werte.
   */
  private void setBlockByList(int paramRow, int paramColumn, List<Integer> list) {
    paramRow -= paramRow % 3;
    paramColumn -= paramColumn % 3;
    int index = 0;
    for (int row = paramRow; row < paramRow + 3; row++) {
      for (int column = paramColumn; column < paramColumn + 3; column++) {
        sudokuMatrix[row][column] = list.get(index);
        index++;
      }
    }
  }

  /**
   * Kopiert die Werte eines Array.
   *
   * @param srcArray Quellarray.
   * @param destArray Zielarray.
   */
  private void copyArray(int[][] srcArray, int[][] destArray) {
    for (int row = 0; row < SudokuController.SIZE; row++) {
      for (int column = 0; column < SudokuController.SIZE; column++) {
        destArray[row][column] = srcArray[row][column];
      }
    }
  }

  /**
   * Rekursive Methode zum entfernen der Zahlen des Sudokus.
   *
   * @param depth Die Rekursionstiefe.
   * @param randomList Eine list von 0-80 in zufälliger Reihenfolge bei der jede Zahl  ein Feld darstellt.
   * @return Gibt True zurück wenn die gewünschte Tiefe erreicht ist oder ein Limit erreicht wurde. False wenn die Liste keine Werte mehr hat.
   */
  private boolean removeRandomNumber(int depth, final List<Integer> randomList) {
    this.counter++;
    List<Integer> list = new ArrayList<>(randomList);
    // Endet wenn die Zieltiefe erreicht ist oder zu viele Versuche gebraucht wurden.
    if (depth == 0 || counter > 100) {
      return true;
    }
    // Endet wenn die Liste leer ist. Zieltiefe wurde nicht erreicht.
    if (list.isEmpty()) {
      return false;
    }

    Collections.shuffle(list);
    int randomNummer = list.get(0);
    list.remove(0);

    // Reihe und Spalte anhand der Feld nummer.
    int row = randomNummer / SudokuController.SIZE;
    int column = randomNummer % SudokuController.SIZE;

    // Zwischenspeichern der aktuellen Nummer.
    int previousNumber = sudokuMatrix[row][column];
    sudokuMatrix[row][column] = 0;
    this.solver.setSudokuMatrix(sudokuMatrix);
    // Prüft ob das Sudoku noch eindeutig ist nach der leerung des Feldes.
    if (this.solver.isUnique() && removeRandomNumber(depth - 1, list)) {
      // Es ist nicht mehr gültig daher einen Schritt zurück.
      return true;
    }
    // Zurücksetzten auf die vorherige Nummer.
    sudokuMatrix[row][column] = previousNumber;
    return removeRandomNumber(depth, list);
  }
}
