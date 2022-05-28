package chessGameLoader;
import GameLoader.games.DotsAndBoxes.*;
import app.chess.Chess;
import app.chess.board.StandardChessBoard;

public class chessGameLoader extends DotsAndBoxes {
    Chess c = new Chess(new StandardChessBoard());
    @Override
    public String getName() {
        return "Dots and boxes2";
    }
    String chessify() {
        return c.toString();
    }
}
