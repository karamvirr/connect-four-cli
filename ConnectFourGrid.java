import java.util.Arrays;
import java.util.List;

public class ConnectFourGrid {
  public static final int ROWS    = 6;
  public static final int COLUMNS = 7;

  public static final String PLAYER_DISC   = "🟡";
  public static final String COMPUTER_DISC = "🔴";

  private String[][] grid;

  public ConnectFourGrid() {
    grid = new String[ROWS][COLUMNS];
    print();
  }

  /**
   * @return  String containing game over information (such as winner, or if
   *          there is a tie). Returns null if game is still in progress.
   */
  public String checkGameOver() {
    if (isWinner(PLAYER_DISC)) {
      return "Congratulations, you've won!";
    } else if (isWinner(COMPUTER_DISC)) {
      return "Uh-oh, maybe next time!";
    } else if (isTiedGame()) {
      return "Game over, a tie!";
    }
    return null;
  }

  /**
   * @return  true if there is four in a row of the given disc in the grid
   *          either vertically, horizontally. false otherwise.
   */
  public boolean isWinner(String disc) {
    // checking discs from the bottom up.
    for (int r = ROWS - 1; r >= 0; r--) {
      for (int c = COLUMNS - 1; c >= 0; c--) {
        if (grid[r][c] == disc) {
          // vertical
          if (r - 3 >= 0) {
            if (equals(grid[r][c], grid[r - 1][c], grid[r - 2][c], grid[r - 3][c])) {
              return true;
            }
          }
          // horizontal
          if (c - 3 >= 0) {
            if (equals(grid[r][c], grid[r][c - 1], grid[r][c - 2], grid[r][c - 3])) {
              return true;
            }
          }
          // diagonal
          if (c - 3 >= 0 && r - 3 >= 0) {
            if (equals(grid[r][c], grid[r - 1][c - 1], grid[r - 2][c - 2], grid[r - 3][c - 3])) {
              return true;
            }
          }
          if (c - 3 >= 0 && r + 3 < ROWS) {
            if (equals(grid[r][c], grid[r + 1][c - 1], grid[r + 2][c - 2], grid[r + 3][c - 3])) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  /**
   * @return  true if tied game, false otherwise.
   *          a game is tied if there are no moves to be made and there is no
   *          winner.
   */
  public boolean isTiedGame() {
    for (int row = 0; row < ROWS; row++) {
      for (int column = 0; column < COLUMNS; column++) {
        if (grid[row][column] == null) {
          return false;
        }
      }
    }
    return true;
  }

  /*
   * Sets the string at the location specified by 'move' to 'disc'.
   */
  public void applyMove(Move move, String disc) {
    applyMove(move, disc, false);
  }

  /*
   * Sets the string at the location specified by 'move' to 'disc'.
   * If 'printAfter' is true, the grid is printed after the move is made.
   */
  public void applyMove(Move move, String disc, boolean printAfter) {
    grid[move.row][move.column] = disc;
    if (printAfter) {
      print();
    }
  }

  /**
   * @return  returns the row index of where a disc would fall to if dropped
   *          from the given grid column index.
   *          if the column is full of discs, returns -1.
   */
  public int getDropIndex(int column) {
    int columnIndex = column - 1;
    int rowIndex = ROWS - 1;

    while (rowIndex >= 0 && grid[rowIndex][columnIndex] != null) {
      rowIndex -= 1;
    }
    if (rowIndex >= 0) {
      return rowIndex;
    }
    return -1;
  }


  /**
   * @return true if the move was successfully made, false otherwise.
   */
  public boolean applyMoveByColumn(int column, String disc) {
    int columnIndex = column - 1;
    int rowIndex = getDropIndex(column);

    if (rowIndex == -1) {
      return false;
    }

    applyMove(new Move(rowIndex, columnIndex), disc, true);
    return true;
  }

  /**
   * Sets location specified by param to null (empty).
   */
  public void undoMove(Move move) {
    grid[move.row][move.column] = null;
  }

  /**
   * @return  estimation of the likelihood of winning/losing from the perspective
   *          of the computer from the current grid.
   *          +x -> leaning towards computer.
   *          -x -> leaning towards player.
   */
  public int heuristic() {
    int score = 0;
    // checking discs from the bottom up.
    for (int r = ROWS - 1; r >= 0; r--) {
      for (int c = COLUMNS - 1; c >= 0; c--) {
        // vertical
        if (r - 3 >= 0) {
          score += evaluateLine(grid[r][c], grid[r - 1][c], grid[r - 2][c], grid[r - 3][c]);
        }
        // horizontal
        if (c - 3 >= 0) {
          score += evaluateLine(grid[r][c], grid[r][c - 1], grid[r][c - 2], grid[r][c - 3]);
        }
        // diagonal
        if (c - 3 >= 0 && r - 3 >= 0) {
          score += evaluateLine(grid[r][c], grid[r - 1][c - 1], grid[r - 2][c - 2], grid[r - 3][c - 3]);
        }
        if (c - 3 >= 0 && r + 3 < ROWS) {
          score += evaluateLine(grid[r][c], grid[r + 1][c - 1], grid[r + 2][c - 2], grid[r + 3][c - 3]);
        }
      }
    }
    return score;
  }

  /**
   * Given a line comprised of four discs (or empty spots), returns an estimation
   * representing the likelihood of the line benefiting the computer vs the player.
   *
   * @return  +x -> line benefits computer.
   *          -x -> line benefits player.
   */
  private int evaluateLine(String a, String b, String c, String d) {
    int reward = 50;
    int evaluation = 0;

    List<String> list = Arrays.asList(a, b, c, d);

    int emptySpots = (int) list
      .stream()
      .filter(disc -> disc == null)
      .count();
    if (emptySpots == 4) {
      return evaluation;
    }
    int computerSpots = (int) list
      .stream()
      .filter(disc -> disc != null && disc.equals(COMPUTER_DISC))
      .count();
    int playerSpots = (int) list
      .stream()
      .filter(disc -> disc != null && disc.equals(PLAYER_DISC))
      .count();

    // add points for each open spot controlled by computer
    evaluation += (computerSpots * reward);
    // subtract points for each open spot controlled by player
    evaluation -= (playerSpots * reward);

    // add points for each piece that is one step away from a win
    if (computerSpots == 3 && emptySpots == 1) {
      evaluation += 10000;
    }
    // add points for each piece that is one step away from a loss
    if (playerSpots == 3 && emptySpots == 1) {
      evaluation -= 10000;
    }

    return evaluation;
  }

  /**
   * @return true if all given string params are non-null and equal to each
   *         other, false otherwise.
   */
  public boolean equals(String a, String b, String c, String d) {
    if (a == null || b == null || c == null || d == null) {
      return false;
    }
    return a.equals(b) && b.equals(c) && c.equals(d);
  }

  /**
   * prints ROWSxCOLUMNS grid containing all the discs that have been placed
   * by both the player and computer.
   */
  public void print() {
    System.out.println();
    printHeader();
    for (int i = 0; i < ROWS; i++) {
      printRow(grid[i]);
    }
    System.out.println();
  }

  private void printHeader() {
    for (int i = 1; i <= COLUMNS; i++) {
      System.out.print("  " + i + "  ");
    }
    System.out.println();
    printPartition();
  }

  private void printRow(String[] row) {
    System.out.print("|");
    for (int i = 0; i < COLUMNS; i++) {
      String piece = (row[i] != null) ? row[i] : "  ";
      System.out.print(" " + piece + " |");
    }
    System.out.println();
    printPartition();
  }

  private void printPartition() {
    System.out.print("+");
    for (int i = 0; i < COLUMNS; i++) {
      System.out.print("----+");
    }
    System.out.println();
  }
}
