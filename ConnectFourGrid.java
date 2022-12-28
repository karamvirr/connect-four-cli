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

  public void applyMove(Move move, String disc) {
    applyMove(move, disc, false);
  }

  public void applyMove(Move move, String disc, boolean printAfter) {
    grid[move.row][move.column] = disc;
    if (printAfter) {
      print();
    }
  }

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

  public void applyMoveByColumn(int column, String disc) {
    int columnIndex = column - 1;
    int rowIndex = ROWS - 1;

    while (rowIndex >= 0 && grid[rowIndex][columnIndex] != null) {
      rowIndex -= 1;
    }
    if (rowIndex >= 0) {
      applyMove(new Move(rowIndex, columnIndex), disc, true);
    }
  }

  public void undoMove(Move move) {
    grid[move.row][move.column] = null;
  }

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

  // returns an estimations of the likelihood of winning/losing from current
  // position.
  // positive value -> leaning towards computer
  // negative value -> leaning towards player
  public int heuristic() {
    int score = 0;
    // checking discs from the bottom up.
    for (int r = ROWS - 1; r >= 0; r--) {
      for (int c = COLUMNS - 1; c >= 0; c--) {
        int test = 0;
        // vertical
        if (r - 3 >= 0) {
          test += evaluateLine(grid[r][c], grid[r - 1][c], grid[r - 2][c], grid[r - 3][c]);
        }
        // horizontal
        if (c - 3 >= 0) {
          test += evaluateLine(grid[r][c], grid[r][c - 1], grid[r][c - 2], grid[r][c - 3]);
        }
        // diagonal
        if (c - 3 >= 0 && r - 3 >= 0) {
          test += evaluateLine(grid[r][c], grid[r - 1][c - 1], grid[r - 2][c - 2], grid[r - 3][c - 3]);
        }
        if (c - 3 >= 0 && r + 3 < ROWS) {
          test += evaluateLine(grid[r][c], grid[r + 1][c - 1], grid[r + 2][c - 2], grid[r + 3][c - 3]);
        }
        score += test;

      }
    }
    return score;
  }

  public int evaluateLine(String a, String b, String c, String d) {
    int reward = 50;
    int evaluation = 0;

    List<String> list = Arrays.asList(a, b, c, d);

    int emptyCount = (int) list
      .stream()
      .filter(disc -> disc == null)
      .count();
    if (emptyCount == 4) {
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
    if (computerSpots == 3 && emptyCount == 1) {
      evaluation += (reward * 15);
    }
    // add points for each piece that is one step away from a loss
    if (playerSpots == 3 && emptyCount == 1) {
      evaluation -= (reward * 15);
    }

    return evaluation;
  }

  public boolean equals(String a, String b, String c, String d) {
    if (a == null || b == null || c == null || d == null) {
      return false;
    }
    return a.equals(b) && b.equals(c) && c.equals(d);
  }
}
