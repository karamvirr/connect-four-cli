import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ConnectFourBot {
  private static final int MAX_REWARD = 10000;
  private static final int SEARCH_DEPTH = 7;

  public static void applyMove(ConnectFourGrid grid) throws InterruptedException {
    long start = System.currentTimeMillis();
    int bestScore = -Integer.MAX_VALUE;
    ArrayList<Move> bestMoves = new ArrayList<>();
    for (Move move : getMoves(grid)) {
      // choose
      grid.applyMove(move, ConnectFourGrid.COMPUTER_DISC);
      // explore
      int score = alphabeta(
        grid, SEARCH_DEPTH, -Integer.MAX_VALUE, Integer.MAX_VALUE, false
      );
      // unchoose
      grid.undoMove(move);

      if (score > bestScore) {
        bestScore = score;
        bestMoves = new ArrayList<>(Arrays.asList(move));
      } else if (score == bestScore) {
        bestMoves.add(move);
      }
    }

    int index = new Random().nextInt(bestMoves.size());
    Move move = bestMoves.get(index);
    grid.applyMove(move, ConnectFourGrid.COMPUTER_DISC, true);

    long finish = System.currentTimeMillis();
    float timeElapsed = (finish - start) / 1000F;
    System.out.print("🤖 dropped a disc at column " + (move.column + 1) + " ");
    System.out.println("(" + timeElapsed + " seconds)");
    System.out.println();
  }

  public static int alphabeta(ConnectFourGrid grid, int depth, int alpha, int beta, boolean maximizingPlayer) {
    if (grid.isWinner(ConnectFourGrid.COMPUTER_DISC)) {
      return MAX_REWARD;
    } else if (grid.isWinner(ConnectFourGrid.PLAYER_DISC)) {
      return -MAX_REWARD;
    } else if (grid.isTiedGame()) {
      return 0;
    } else if (depth == 0) {
      return grid.heuristic();
    }

    if (maximizingPlayer) {
      int maxValue = -Integer.MAX_VALUE;
      for (Move move : getMoves(grid)) {
        // choose
        grid.applyMove(move, ConnectFourGrid.COMPUTER_DISC);
        // explore
        int value = alphabeta(grid, depth - 1, alpha, beta, false);
        // unchoose
        grid.undoMove(move);
        maxValue = Math.max(maxValue, value);
        alpha = Math.max(alpha, value);
        if (beta <= alpha) {
          break;
        }
      }
      return maxValue;
    } else {
      int minValue = Integer.MAX_VALUE;
      for (Move move : getMoves(grid)) {
        // choose
        grid.applyMove(move, ConnectFourGrid.PLAYER_DISC);
        // explore
        int value = alphabeta(grid, depth - 1, alpha, beta, true);
        // unchoose
        grid.undoMove(move);
        minValue = Math.min(minValue, value);
        beta = Math.min(beta, value);
        if (beta <= alpha) {
          break;
        }
      }
      return minValue;
    }
  }

  public static ArrayList<Move> getMoves(ConnectFourGrid grid) {
    ArrayList<Move> result = new ArrayList<>();
    if (grid.isTiedGame()) {
      return result;
    }
    for (int column = 1; column <= ConnectFourGrid.COLUMNS; column++) {
      int row = grid.getDropIndex(column);
      if (row != -1) {
        result.add(new Move(grid.getDropIndex(column), column - 1));
      }
    }
    return result;
  }
}
