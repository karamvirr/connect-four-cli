public class Move {
  public int row; 
  public int column; 

  public Move(int row, int column) {
    this.row = row;
    this.column = column;
  }

  @Override
  public String toString() {
    return "(row: " + row + " column: " + column + ")";
  }
}
