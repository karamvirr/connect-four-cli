import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.lang.Thread;

public class ConnectFourBot {
  private static final int MAX_REWARD = 1000000;
  private static final int SEARCH_DEPTH = 7;

  /**
   * Computer computes and applies the best move possible on the grid.
   *
   * @param grid  Connect-Four grid.
   */
  public static void applyMove(ConnectFourGrid grid, String disc) throws InterruptedException {
    long start = System.currentTimeMillis();
    ArrayList<Move> bestMoves = new ArrayList<>();

    boolean isMax = disc.equals(ConnectFourGrid.COMPUTER_DISC);
    int bestScore = isMax ? -Integer.MAX_VALUE : Integer.MAX_VALUE;

    for (Move move : getMoves(grid)) {
      // choose
      grid.applyMove(move, disc);
      // explore
      int score = alphabeta(
        grid, SEARCH_DEPTH, -Integer.MAX_VALUE, Integer.MAX_VALUE, !isMax
      );
      // unchoose
      grid.undoMove(move);

      if (isMax) {
        if (score > bestScore) {
          bestScore = score;
          bestMoves = new ArrayList<>(Arrays.asList(move));
        }
      } else {
        if (score < bestScore) {
          bestScore = score;
          bestMoves = new ArrayList<>(Arrays.asList(move));
        }
      }
      if (score == bestScore) {
        bestMoves.add(move);
      }
    }

    int index = new Random().nextInt(bestMoves.size());
    Move move = bestMoves.get(index);
    grid.applyMove(move, disc, true);

    long finish = System.currentTimeMillis();
    float timeElapsed = (finish - start) / 1000F;
    System.out.print("🤖 dropped " + disc + " in column " + (move.column + 1) + " ");
    System.out.println("(" + timeElapsed + " seconds)");
    System.out.println();
    if (timeElapsed < 0.1) {
      Thread.sleep(500);
    }
  }

  /**
   * source: https://en.wikipedia.org/wiki/Minimax
   *         https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning
   *
   * @param grid              Connect-Four grid.
   * @param depth             depth of the given grid from its root state.
   * @param alpha             highest move score encountered.
   * @param beta              lowest move score encountered.
   * @param maximizingPlayer  true if maximizing move score, false otherwise.
   *                          if we are maximizing, that means we are looking
   *                          for the most optimal move for the computer.
   * @return                  heuristic value for the given grid.
   */
  public static int alphabeta(ConnectFourGrid grid, int depth, int alpha, int beta, boolean maximizingPlayer) {
    if (grid.isWinner(ConnectFourGrid.COMPUTER_DISC)) {
      return MAX_REWARD - (SEARCH_DEPTH - depth);
    } else if (grid.isWinner(ConnectFourGrid.PLAYER_DISC)) {
      return -MAX_REWARD - (SEARCH_DEPTH - depth);
    } else if (grid.isTiedGame()) {
      return 0;
    } else if (depth == 0) {
      return grid.heuristic() - SEARCH_DEPTH;
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
        if (maxValue > beta) {
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
        if (minValue < alpha) {
          break;
        }
      }
      return minValue;
    }
  }

  /**
   * @param grid  Connect-Four grid.
   * @return      List of all moves that can be made on the grid.
   */
  public static ArrayList<Move> getMoves(ConnectFourGrid grid) {
    ArrayList<Move> result = new ArrayList<>();
    if (grid.isTiedGame()) {
      return result;
    }
    for (int column = 1; column <= ConnectFourGrid.COLUMNS; column++) {
      int row = grid.getDropIndex(column);
      if (row != -1) {
        result.add(new Move(row, column - 1));
      }
    }
    return result;
  }
}
