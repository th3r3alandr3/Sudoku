import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.lang.*;

class HumanSolver {

  private int[][] sudokuMatrix;
  // Die dritte Dimension enthält die Möglichen Lösungen
  private int[][][] possibleSolutions = new int[SudokuController.SIZE][SudokuController.SIZE][];
  private int difficultyCounter = 0;
  private final List<Integer> possibleNumbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);


  HumanSolver(int[][] sudokuMatrix) {
    this.sudokuMatrix = sudokuMatrix;
  }

  /**
   * Errmittelt den Schwierigkeitsgrad
   *
   * @return Schwierigkeitsgrad
   */
  int getDifficulty() {
    this.solve();
    if (!this.isSolved()) {
      return SudokuController.HARD;
    }
    if (difficultyCounter > 65) {
      return SudokuController.NORMAL;
    }
    return SudokuController.EASY;
  }

  /**
   * Versucht das Sudoku mit verschiedenen Metoden zu lösen und definiert damit den Schwierigkeitsgrad.
   */
  private void solve() {
    boolean solutionsFound;
    do {
      findPossibleSolutions();
      solutionsFound = solveByPossibilitys(1);
      if (!solutionsFound) {
        findHiddenSingles();
        solutionsFound = solveByPossibilitys(2);
      }
      if (!solutionsFound) {
        findNakedPairs();
        solutionsFound = solveByPossibilitys(5);
      }
    } while (solutionsFound);

  }

  /**
   * Überprüft ob das Sudoku noch ungelöst Felder enthält
   *
   * @return true|false
   */
  private boolean isSolved() {
    for (int[] row : this.sudokuMatrix) {
      for (int field : row) {
        if (field == 0) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * A Naked Pair is a set of two candidate numbers sited in two cells that belong to at least one unit in common. That is, they reside in the same row, column or box.
   */
  private void findNakedPairs() {
    for (int row = 0; row < SudokuController.SIZE; row++) {
      //HashSet zum Prüfen ob ein Element schon vorhanden ist.
      Set<List> setRow = new HashSet<>();
      for (int column = 0; column < SudokuController.SIZE; column++) {
        int[] possibleSolutions = this.possibleSolutions[row][column];
        if (possibleSolutions != null && possibleSolutions.length == 2) {
          //Aus dem Array wird zur Prüfung eine Liste gemacht.
          List<Integer> list = Arrays.stream(possibleSolutions).boxed().collect(Collectors.toList());
          //True wenn schon vorhanden.
          if (!setRow.add(list)) {
            // Aktualisierung der möglichen Lösungen.
            this.possibleSolutions[row] = this.handeleNakedPairArray(this.possibleSolutions[row], possibleSolutions);
          }
        }
      }
    }

    for (int c = 0; c < SudokuController.SIZE; c++) {
      //HashSet zum Prüfen ob ein Element schon vorhanden ist.
      Set<List> setCol = new HashSet<>();
      int[][] column = this.getColumn(this.possibleSolutions, c);
      for (int[] possibleSolutions : column) {
        if (possibleSolutions != null && possibleSolutions.length == 2) {
          //Aus dem Array wird zur Prüfung eine Liste gemacht
          List<Integer> list = Arrays.stream(possibleSolutions).boxed().collect(Collectors.toList());
          //True wenn schon vorhanden
          if (!setCol.add(list)) {
            // Aktualisierung der möglichen Lösungen.
            this.setColumn(c, this.handeleNakedPairArray(column, possibleSolutions));
          }
        }
      }
    }

    for (int i = 0; i < 9; i++) {
      //HashSet zum Prüfen ob ein Element schon vorhanden ist.
      Set<List> setBlock = new HashSet<>();
      int[][] block = getBlockAsArray(i, this.possibleSolutions);
      for (int[] possibleSolutions : block) {
        if (possibleSolutions != null && possibleSolutions.length == 2) {
          //Aus dem Array wird zur Prüfung eine Liste gemacht
          List<Integer> list = Arrays.stream(possibleSolutions).boxed().collect(Collectors.toList());
          //True wenn schon vorhanden
          if (!setBlock.add(list)) {
            // Aktualisierung der möglichen Lösungen.
            this.setBlockByArray(i, this.handeleNakedPairArray(block, possibleSolutions));
          }
        }
      }
    }
  }

  /**
   * Macht aus einem Block (3D-Array) eine Reihe (2D-Array) zur weiteren Verarbeitung.
   *
   * @param blockNumber Die Nummer des Blocks der als Reihe ausgegeben werden soll.
   * @param array Sudoku mit den möglichen Lösungen.
   * @return Die Reihe die aus dem Block erzeugt wurde.
   */
  private int[][] getBlockAsArray(int blockNumber, int[][][] array) {
    int[][] blockMapping = {{0, 0}, {0, 3}, {0, 6}, {3, 0}, {3, 3}, {3, 6}, {6, 0}, {6, 3}, {6, 6}};
    int[][] block = new int[9][];
    int index = 0;
    for (int i = blockMapping[blockNumber][0]; i < blockMapping[blockNumber][0] + 3; i++) {
      for (int j = blockMapping[blockNumber][1]; j < blockMapping[blockNumber][1] + 3; j++) {
        block[index] = array[i][j];
        index++;
      }
    }
    return block;
  }

  /**
   * Macht aus der Reihe (2D-Array) wieder einem Block (3D-Array).
   *
   * @param blockNumber Die Nummer des Blocks der geändert werden soll.
   * @param array Die Reihe die eingefügt werden soll.
   */
  private void setBlockByArray(int blockNumber, int[][] array) {
    int[][] blockMapping = {{0, 0}, {0, 3}, {0, 6}, {3, 0}, {3, 3}, {3, 6}, {6, 0}, {6, 3}, {6, 6}};
    int index = 0;
    for (int i = blockMapping[blockNumber][0]; i < blockMapping[blockNumber][0] + 3; i++) {
      for (int j = blockMapping[blockNumber][1]; j < blockMapping[blockNumber][1] + 3; j++) {
        this.possibleSolutions[i][j] = array[index];
        index++;
      }
    }
  }

  /**
   * Entfernt Zahlen aus der Reihe die nicht mehr in Frage kommen.
   *
   * @param array Die Reihe aus der die Zahlen entfernt werden sollen
   * @param possibleSolutions Die zu entfernenden Zahlen
   * @return Die aktuallisierte Reihe
   */
  private int[][] handeleNakedPairArray(int[][] array, int[] possibleSolutions) {
    List<Integer> possibleSolutionsList = Arrays.stream(possibleSolutions).boxed().collect(Collectors.toList());
    for (int i = 0; i < array.length; i++) {
      // Prüft ob es überhaupt Lösungen gibt und ob diese das Naked Pair sind.
      if (array[i] != null && !Arrays.equals(array[i], possibleSolutions)) {
        for (int possibleSolution : array[i]) {
          // Wenn die möglichen Lösungen eines Feldes zahlen des Naked Pairs enthält werden diese Entfernt.
          if (possibleSolutionsList.contains(possibleSolution)) {
            List<Integer> list = Arrays.stream(array[i]).boxed().collect(Collectors.toList());
            list.remove(list.indexOf(possibleSolution));
            array[i] = list.stream().mapToInt(o -> o).toArray();
          }
        }
      }
    }
    return array;
  }

  /**
   * Hidden Single bedeutet, dass es für eine bestimmte Ziffer in einem bestimmten Haus nur noch eine Zelle gibt, wo diese Ziffer möglich ist. Für die Zelle selbst sind noch mehrere Kandidaten
   * möglich, die richtige Ziffer ist also zwischen den anderen Kandidaten versteckt.
   */
  private void findHiddenSingles() {
    for (int row = 0; row < SudokuController.SIZE; row++) {
      ArrayList<int[]> solutions = this.findHiddenSingles(this.possibleSolutions[row]);
      for (int[] number : solutions) {
        if (number[1] < this.possibleSolutions[row][number[0]].length) {
          int[] solution = {this.possibleSolutions[row][number[0]][number[1]]};
          this.possibleSolutions[row][number[0]] = solution;
        }
      }
    }
    for (int column = 0; column < SudokuController.SIZE; column++) {
      ArrayList<int[]> solutions = this.findHiddenSingles(getColumn(this.possibleSolutions, column));
      for (int[] number : solutions) {
        if (number[1] < this.possibleSolutions[number[0]][column].length) {
          int[] solution = {this.possibleSolutions[number[0]][column][number[1]]};
          this.possibleSolutions[number[0]][column] = solution;
        }
      }
    }
  }

  /**
   * Zählt das vorkommen der möglichen Zahlen in einer Reihe.
   *
   * @param possibleSolutionsRow Die möhlichen Zahlen für die jeweiligen Felder.
   * @return Array aus Zahlen die nur einmal vorkommen.
   */
  private ArrayList<int[]> findHiddenSingles(int[][] possibleSolutionsRow) {
    ArrayList<int[]>[] counter = new ArrayList[9];
    for (int i = 0; i < 9; i++) {
      counter[i] = new ArrayList<>();
    }

    for (int i = 0; i < possibleSolutionsRow.length; i++) {
      int[] possibleSolutions = possibleSolutionsRow[i];
      if (possibleSolutions != null) {
        for (int j = 0; j < possibleSolutions.length; j++) {
          int[] index = {i, j};
          counter[possibleSolutions[j] - 1].add(index);

        }
      }
    }
    ArrayList<int[]> found = new ArrayList<int[]>();
    for (int i = 0; i < counter.length; i++) {
      if (counter[i].size() == 1) {
        found.add(counter[i].get(0));
      }
    }
    return found;
  }

  /**
   * Methode um eine beliebige Spalte zu erhalten
   *
   * @param matrix Das Sudoku inklusive möglicher Lösungen für die einzelnen Felder.
   * @param column Die Spalte die ausgewählt werden soll.
   * @return Die ausgewählte Spalte
   */
  private int[][] getColumn(int[][][] matrix, int column) {
    int[][] columnArray = new int[SudokuController.SIZE][];
    for (int row = 0; row < matrix.length; row++) {
      columnArray[row] = matrix[row][column];
    }
    return columnArray;
  }

  /**
   * Methode um eine beliebige Spalte zu setzen
   *
   * @param column Die Spalte die gesetzt werden soll.
   * @param columnArray Die zu setzenden werte.
   */
  private void setColumn(int column, int[][] columnArray) {
    for (int row = 0; row < this.possibleSolutions.length; row++) {
      this.possibleSolutions[row][column] = columnArray[row];
    }
  }

  /**
   * Setzt für die Felder die nur noch über eine Lösungen verfügen die entsprechende Zahl ein
   *
   * @param addend Zur ermittlung des Schwierigkeitsgrades
   * @return true wenn Lösungen gefunden wurden.
   */
  private boolean solveByPossibilitys(int addend) {
    boolean solutionsFound = false;
    for (int row = 0; row < SudokuController.SIZE; row++) {
      for (int column = 0; column < SudokuController.SIZE; column++) {
        if (this.sudokuMatrix[row][column] == 0) {
          int[] possibleSolutions = this.possibleSolutions[row][column];
          // Wenn es nur noch eine mögliche Lösung gibt wird dies gesetzt.
          if (possibleSolutions != null && possibleSolutions.length == 1) {
            this.sudokuMatrix[row][column] = possibleSolutions[0];
            // Ermittelt den Schwirigkeitsgrad.
            this.difficultyCounter += addend;
            solutionsFound = true;
          }
        }
      }
    }
    return solutionsFound;
  }

  /**
   * Ermittellt alle möglichen Lösungen für das jeweilige Feld.
   */
  private void findPossibleSolutions() {
    // Geht jedes Feld durch.
    for (int row = 0; row < SudokuController.SIZE; row++) {
      for (int column = 0; column < SudokuController.SIZE; column++) {
        // Nur leere Felder werden berücksichtigt.
        if (sudokuMatrix[row][column] == 0) {
          // Zünachst sind alle Nummer möglich.
          List<Integer> possibleNumbers = new ArrayList<>(this.possibleNumbers);
          // Diese werden dann aber entfernt sofern diese Schon vorhanden sind in Reihe, Spalte oder Block.
          numbersNotInColumn(row, possibleNumbers);
          numbersNotInRow(column, possibleNumbers);
          numbersNotInBlock(row, column, possibleNumbers);
          this.possibleSolutions[row][column] = toIntArray(possibleNumbers);
        }
      }
    }
  }

  /**
   * Prüft ob die Nummer schon in der Spalte vorhanden ist.
   *
   * @param row Die Reihe der zu prüfende Spalte.
   * @param possibleNumbers Die möglichen Lösungen für die Spalte.
   */
  private void numbersNotInColumn(int row, List<Integer> possibleNumbers) {
    for (int column = 0; column < SudokuController.SIZE; column++) {
      if (possibleNumbers.contains(sudokuMatrix[row][column])) {
        possibleNumbers.remove(new Integer(sudokuMatrix[row][column]));
      }
    }
  }

  /**
   * Prüft ob die Nummer schon in der Reihe vorhanden ist.
   *
   * @param column Die Spalte der zu prüfende Reihe.
   * @param possibleNumbers Die möglichen Lösungen für die Reihe.
   */
  private void numbersNotInRow(int column, List<Integer> possibleNumbers) {
    for (int row = 0; row < SudokuController.SIZE; row++) {
      if (possibleNumbers.contains(sudokuMatrix[row][column])) {
        possibleNumbers.remove(new Integer(sudokuMatrix[row][column]));
      }
    }
  }

  /**
   * @param row Die Reihe des zu prüfenden Blocks.
   * @param column Die Spalte des zu prüfenden Blocks.
   * @param possibleNumbers Die möglichen Lösungen für die Reihe.
   */
  private void numbersNotInBlock(int row, int column, List<Integer> possibleNumbers) {
    int r = row - row % (int) Math.sqrt(SudokuController.SIZE);
    int c = column - column % (int) Math.sqrt(SudokuController.SIZE);

    for (int i = r; i < r + 3; i++) {
      for (int j = c; j < c + 3; j++) {
        if (possibleNumbers.contains(sudokuMatrix[i][j])) {
          possibleNumbers.remove(new Integer(sudokuMatrix[i][j]));
        }
      }
    }
  }

  /**
   * Konvertiert eine Liste in ein Array.
   *
   * @param list Die zu konvertierende Liste
   * @return Das Array welches aus der Liste enstanden ist.
   */
  private int[] toIntArray(List<Integer> list) {
    int[] array = new int[list.size()];
    int i = 0;
    for (Integer e : list) {
      array[i++] = e;
    }
    return array;
  }
}
