package chessGameLoader;
import GameLoader.client.Client;
import app.checkers.Checkers;
import app.chess.Chess;
import app.chess.board.StandardChessBoard;
import GameLoader.common.Game;

import static GameLoader.common.Serializables.*;
import static GameLoader.common.Utility.runtimeAssert;

import java.util.List;

public class chessGL implements Game {
    private final static String CHESS = "Chess", CHECKERS = "Checkers";
    private final static List<String> settingsList = List.of(CHESS, CHECKERS);

    @Override
    public String getName() {
        return "chess";
    }

    @Override
    public List<String> possibleSettings() {
        return settingsList;
    }

    private String settings;
    private CoreGame core;
    private boolean zeroPlayerIsBlack;

    @Override
    public String getSettings() {
        return settings;
    }

    @Override
    public state getState() {
        CoreGame.coreState st = core.getState();
        return switch (st) {
            case UNFINISHED -> state.UNFINISHED;
            case DRAW -> state.DRAW;
            case WHITE_WON, BLACK_WON ->
                    (st == CoreGame.coreState.BLACK_WON) == zeroPlayerIsBlack ? state.P0_WON : state.P1_WON;
        };
    }

    @Override
    public void makeMove(Command move) {
        if (move instanceof ResignationCommand res) {
            core.forceState((res.getPlayer() == 0) ^ zeroPlayerIsBlack ?
                    CoreGame.coreState.BLACK_WON : CoreGame.coreState.WHITE_WON);
            return;
        }
        if (move instanceof chessGLCommand cmd) {
            core.makeMove(cmd.getInfo());
            return;
        }
        throw new RuntimeException();
    }

    @Override
    public boolean isMoveLegal(Command move) {
        if (getState() != state.UNFINISHED)
            return false;
        if (move instanceof ResignationCommand)
            return true;
        if (move instanceof chessGLCommand cmd)
            return cmd.getPlayer() == getTurn() && core.isMoveLegal(cmd.getInfo());
        return false;
    }

    public int getTurn() {
        return core.getCurrentPlayer() ^ (zeroPlayerIsBlack ? 1 : 0);
    }

    @Override
    public void start(String settings, int seed) {
        if (!settingsList.contains(settings))
            throw new IllegalArgumentException("these settings are not permitted");

        core = new CoreGame(settings.equals(CHECKERS) ? new Checkers() : new Chess(new StandardChessBoard()));
        this.settings = settings;
        zeroPlayerIsBlack = (seed & 1) == 1;
    }

    private chessGLViewModel viewModel;
    @Override
    public chessGLViewModel createViewModel(Client user, int id) {
        if (viewModel == null)
            viewModel = new chessGLViewModel(user, id, this);

        runtimeAssert(viewModel.getModelUser() == user && viewModel.playingAs() == id);
        return viewModel;
    }

    public CoreGame getCore() {
        return core;
    }

    public boolean getZeroPlayerIsBlack() {
        return zeroPlayerIsBlack;
    }

    @Override
    public String toString() {
        return "chessGL{" +
                "settings='" + settings + '\'' +
                ", core=" + core +
                ", zeroPlayerIsBlack=" + zeroPlayerIsBlack +
                '}';
    }
}
