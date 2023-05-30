import java.util.Scanner;
import java.util.Random;

public class ConnectFourClient {
  public static ConnectFourGrid grid;
  public static void main(String[] args) throws InterruptedException {
    introduction();

    grid = new ConnectFourGrid();
    Scanner console = new Scanner(System.in);
    boolean inputInvalid = false;

    // starting player is determined by a 'coin flip'.
    if (new Random().nextBoolean()) {
      ConnectFourBot.applyMove(grid, ConnectFourGrid.COMPUTER_DISC);
    }

    while (true) {
      checkGameOver();
      ConnectFourBot.applyMove(grid, ConnectFourGrid.PLAYER_DISC);
      checkGameOver();
      ConnectFourBot.applyMove(grid, ConnectFourGrid.COMPUTER_DISC);
    }
  }

  /**
   * Checks if the game is over (player or opponent get four discs in a row)
   * or tied. If so, exits execution of current program.
   */
  public static void checkGameOver() {
    String gameOverMessage = grid.checkGameOver();
    if (gameOverMessage != null) {
      System.out.println(gameOverMessage);
      System.exit(0);
    }
  }

  /**
   * Introductory message containing details on how the game is to be played.
   */
  public static void introduction() {
    System.out.println("  _____                            _        ______");
    System.out.println(" / ____|                          | |      |  ____|");
    System.out.println("| |     ___  _ __  _ __   ___  ___| |_ ____| |__ ___  _   _ _ __");
    System.out.println("| |    / _ \\| '_ \\| '_ \\ / _ \\/ __| __|____|  __/ _ \\| | | | '__|");
    System.out.println("| |___| (_) | | | | | | |  __/ (__| |_     | | | (_) | |_| | |");
    System.out.println(" \\_____\\___/|_| |_|_| |_|\\___|\\___|\\__|    |_|  \\___/ \\__,_|_|");
    System.out.println();

    System.out.println("Welcome to Connect-Four!");
    System.out.print("The classic two player game where you take turns ");
    System.out.println("dropping 'discs' from the top of the board.");
    System.out.println("You will face off against the computer, a formidable foe.");
    System.out.print("As the name implies, to win you will have to get four ");
    System.out.print("discs in a row, either vertically, horizontally ");
    System.out.println("or diagonally.");
    System.out.println();
  }
}
