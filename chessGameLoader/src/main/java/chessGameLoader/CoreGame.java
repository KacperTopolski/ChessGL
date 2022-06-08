package chessGameLoader;

import static GameLoader.common.Utility.runtimeAssert;
import app.checkers.Checkers;
import app.chess.Chess;
import app.core.game.Game;
import app.core.game.moves.Move;
import app.core.interactor.InteractiveGame;
import javafx.beans.Observable;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings({"rawtypes", "unchecked"})
public class CoreGame {
    private final Game game;
    private InteractiveGame igame;

    public CoreGame(Game gm) {
        game = gm;
    }

    public Game getGame() {
        return igame == null ? game : igame;
    }

    public InteractiveGame getInteractive() {
        if (igame == null)
            igame = new InteractiveGame(game);
        return igame;
    }

    public enum coreState {
        UNFINISHED, DRAW, WHITE_WON, BLACK_WON
    }

    public enum turn {
        WHITE, BLACK
    }

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

    public boolean isMoveLegal(MoveInfo info) {
        Stream<Move> moves = game.getLegalMoves(getCurrentPlayer()).stream();
        return moves.anyMatch(m -> info.equals(new MoveInfo(m)));
    }

    public void makeMove(MoveInfo info) {
        Stream<Move> moves = game.getLegalMoves(getCurrentPlayer()).stream();
        List<Move> mvs = moves.filter(m -> info.equals(new MoveInfo(m))).toList();

        runtimeAssert(mvs.size() == 1);
        getGame().makeMove(getCurrentPlayer(), mvs.get(0));
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
                ", ig=" + igame +
                ", state=" + getState() +
                ", turn=" + getTurn() +
                ", isChess=" + isChess() +
                ", isCheckers=" + isCheckers() +
                '}';
    }

    SimpleIntegerProperty s;

    public void updateObs() {
        if (s != null)
            s.set(s.get() + 1);
    }

    public Observable getObs() {
        if (s == null)
            s = new SimpleIntegerProperty(0);
        return s;
    }
}
