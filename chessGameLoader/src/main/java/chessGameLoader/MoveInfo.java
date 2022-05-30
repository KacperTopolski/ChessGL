package chessGameLoader;

import app.chess.moves.ChessMove;
import app.chess.moves.Promotion;
import app.core.game.Field;
import app.core.game.Piece;
import app.core.game.moves.Move;
import app.core.game.moves.PieceMove;

import java.io.Serializable;

public record MoveInfo(int start_r, int start_f, int end_r, int end_f, int kind) implements Serializable {
    public MoveInfo(Field start, Field end, int pieceKind) {
        this(start.rank(), start.file(), end.rank(), end.file(), pieceKind);
    }

    public MoveInfo(Field start_orig, Move<?> mv) {
        this(start_orig, getField(mv), getPieceKind(mv));
    }

    public MoveInfo(Move<?> mv) {
        this(getPiece(mv).getPosition(), mv);
    }

    public static Field getField(Move<?> mv) {
        if (mv instanceof PieceMove<?> move)
            return move.getField();
        if (mv instanceof Promotion move)
            return move.getField();
        throw new RuntimeException();
    }
    public static Piece getPiece(Move<?> mv) {
        if (mv instanceof PieceMove<?> move)
            return move.getPiece();
        if (mv instanceof Promotion move)
            return move.getPiece();
        return null;
    }
    public static int getPieceKind(Move<?> mv) {
        if (mv instanceof ChessMove move)
            return move.getPiece().getKind().ordinal();
        return -1;
    }
}
