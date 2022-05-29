package chessGameLoader;

import app.checkers.Checkers;
import app.chess.Chess;
import app.core.game.Game;
import app.core.game.moves.Move;
import app.core.interactor.InteractiveGame;

import java.util.List;

@SuppressWarnings({"rawtypes", "unchecked"})
public class CoreGame {
    private final Game game;

    public CoreGame(Game gm) {
        game = gm;
    }

    public Game get() {
        return game;
    }

    private InteractiveGame ig;
    public InteractiveGame getInteractive() {
        if (ig == null)
            ig = new InteractiveGame(game);
        return ig;
    }

    public enum coreState {
        UNFINISHED, DRAW, WHITE_WON, BLACK_WON
    };

    public enum turn {
        WHITE, BLACK
    };

    public coreState getState() {
        if (game instanceof Chess ch) {
            int cp = ch.getCurrentPlayer();
            return switch (ch.getState(cp)) {
                case OK, CHECKED -> coreState.UNFINISHED;
                case DRAW -> coreState.DRAW;
                case MATED -> cp == 0 ? coreState.BLACK_WON : coreState.WHITE_WON;
            };
        }
        if (game instanceof Checkers ch) {
            return switch (ch.getResult()) {
                case NONE -> coreState.UNFINISHED;
                case WHITE_WON -> coreState.WHITE_WON;
                case BLACK_WON -> coreState.BLACK_WON;
            };
        }
        throw new RuntimeException();
    }

    public int getCurrentPlayer() {
        if (game instanceof Chess ch)
            return ch.getCurrentPlayer();
        if (game instanceof Checkers ch)
            return ch.getCurrentPlayer();
        throw new RuntimeException();
    }

    public turn getTurn() {
        return getCurrentPlayer() == 0 ? turn.WHITE : turn.BLACK;
    }

    public boolean isMoveLegal(int id) {
        return 0 <= id && id < game.getLegalMoves(getCurrentPlayer()).size();
    }

    public void makeMove(int id) {
        int cp = getCurrentPlayer();
        List<Move> lm = game.getLegalMoves(cp);
        if (ig == null)
            game.makeMove(cp, lm.get(id));
        else
            ig.makeMove(cp, lm.get(id));
    }

    public boolean isChess() {
        return game instanceof Chess;
    }

    public boolean isCheckers() {
        return game instanceof Checkers;
    }


    @Override
    public String toString() {
        return "CoreGame{" +
                "game=" + game +
                ", ig=" + ig +
                ", state=" + getState() +
                ", turn=" + getTurn() +
                ", isChess=" + isChess() +
                ", isCheckers=" + isCheckers() +
                '}';
    }


}
