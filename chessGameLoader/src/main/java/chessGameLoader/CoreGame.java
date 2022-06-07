package chessGameLoader;

import static GameLoader.common.Utility.runtimeAssert;
import app.checkers.Checkers;
import app.chess.Chess;
import app.chess.moves.Promotion;
import app.core.game.*;
import app.core.game.moves.Move;
import app.core.interactor.*;
import app.ui.Style;
import app.ui.board.GraphicalBoard;
import app.ui.board.boards.*;
import app.ui.checkers.CheckersConnector;
import app.ui.chess.ChessConnector;
import app.ui.styles.CutePink;
import app.utils.pieceplayer.StandalonePiecePlayer;
import javafx.scene.layout.VBox;
import static chessGameLoader.MoveInfo.getPiece;

import java.util.*;
import java.util.stream.Stream;

@SuppressWarnings({"rawtypes", "unchecked"})
public class CoreGame {
    // due to "clean architecture", you actually need access to both InteractiveGame and normal Game
    private final InteractiveGame igame;
    private final Game game;

    private final Map<Piece, Field> mpp = new HashMap<>();
    private final Map<Field, Piece> mpf = new HashMap<>();

    private int next_turn = 0, last_capture = -1, last_pawn_move = -1;
    private MoveInfo lastMove;

    public CoreGame(Game gm) {
        igame = new InteractiveGame(gm);
        game = gm;

        List<Piece> pieces = game.getAllPieces();
        for (Piece p : pieces) {
            Field pos = p.getPosition();

            mpp.put(p, pos);
            mpf.put(pos, p);
        }

        igame.connectSpectator((player, move, changedPieces) -> {
            System.out.println(this);

            if (isForced()) {
                lastMove = null;
                return;
            }

            // as I said, clean architecture
            Piece p = getPiece(move);
            Field pos = p.getPosition();
            Field oldPos = move instanceof Promotion ? pos : mpp.get(p);

            lastMove = new MoveInfo(oldPos, move);

            // clean old pos (this has to be done this way due to promotion changing pieces)
            mpp.remove(mpf.remove(oldPos));

            // get piece at current position (q != null iff we have a capture)
            Piece q = mpf.remove(pos);
            mpp.remove(q);

            // put piece back
            mpp.put(p, pos);
            mpf.put(pos, p);

            if (isCheckers()) // checkers don't have three folds / 50 move rule
                return;

            if (q != null)
                last_capture = next_turn;

            if (lastMove.kind() == 0)
                last_pawn_move = next_turn;

            boolean promotion_pending = lastMove.kind() == 0 && (lastMove.end_r() == 1 || lastMove.end_r() == 8);

            if (!promotion_pending)
                ++next_turn;

            // TODO three fold
        });
    }

    public enum coreState {
        UNFINISHED, DRAW, WHITE_WON, BLACK_WON
    }

    public enum turn {
        WHITE, BLACK
    }

    private final List<Spectator> forcedSpectators = new ArrayList<>();
    private coreState forcedState;
    public void forceState(coreState state) {
        runtimeAssert(forcedState == null && getState() == coreState.UNFINISHED);
        forcedState = state;

        for (Spectator s : forcedSpectators)
            s.update(-1, null, null);
    }

    public boolean isForced() {
        return forcedState != null;
    }

    // the next two methods are pure agony
    public coreState getState() {
        if (forcedState != null)
            return forcedState;
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

        connectBoilerplate();
        igame.makeMove(getCurrentPlayer(), mvs.get(0));

        if (isCheckers())
            return;

        int chess50MoveRule = 1;

        if (next_turn - last_capture > 2 * chess50MoveRule && next_turn - last_pawn_move > 2 * chess50MoveRule
                && getState() == coreState.UNFINISHED)
            forceState(coreState.DRAW);
    }

    public boolean isChess() {
        return game instanceof Chess;
    }

    public boolean isCheckers() {
        return game instanceof Checkers;
    }

    private final boolean[] connectedPlayer = new boolean[]{false, false};

    // this is what peak clean architecture is
    private void connectBoilerplate() {
        for (int i = 0; i < 2; ++i) {
            if (connectedPlayer[i])
                continue;
            connectedPlayer[i] = true;
            igame.connectPlayer(i, new Player());
        }
    }

    static final Style STYLE = new CutePink();
    public VBox getGUI(int who) {
        runtimeAssert(!connectedPlayer[who]);
        connectedPlayer[who] = true;

        StandalonePiecePlayer player = new StandalonePiecePlayer(igame, who);

        GraphicalBoard board = who == 0 ?
                new NormalBoard(40, STYLE) :
                new InvertedBoard(40, STYLE);

        if (isChess())
            ChessConnector.connect(board, player);
        if (isCheckers())
            CheckersConnector.connect(board, player);

        connectBoilerplate();

        return board;
    }

    public void connectSpectator(Spectator s, boolean updateOnForced) {
        igame.connectSpectator(s);
        if (updateOnForced)
            forcedSpectators.add(s);
    }

    public MoveInfo getLastMove() {
        return lastMove;
    }

    @Override
    public String toString() {
        return "CoreGame{" +
                "igame=" + igame +
                ", game=" + game +
//                ", mpp=" + mpp +
//                ", mpf=" + mpf +
                ", next_turn=" + next_turn +
                ", last_capture=" + last_capture +
                ", last_pawn_move=" + last_pawn_move +
                ", lastMove=" + lastMove +
                ", forcedSpectators=" + forcedSpectators +
                ", forcedState=" + forcedState +
                ", actualState=" + getState() +
                ", connectedPlayer=" + Arrays.toString(connectedPlayer) +
                '}';
    }
}
