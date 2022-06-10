package chess;

public interface ChessDelegate {
	ChessPiece pieceAt(int col, int row);
	public void movePiece(int fromCol, int fromRow, int toCol, int toRow);
}
