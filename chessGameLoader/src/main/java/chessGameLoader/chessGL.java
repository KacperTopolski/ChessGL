package chessGameLoader;
import GameLoader.client.Client;
import app.checkers.Checkers;
import app.chess.Chess;
import app.chess.board.StandardChessBoard;
import GameLoader.common.Game;

import static GameLoader.common.Serializables.*;

import java.util.List;

public class chessGL implements Game {
    private final String CHESS = "Chess", CHECKERS = "Checkers";
    private final List<String> settingsList = List.of(CHESS, CHECKERS);

    @Override
    public String getName() {
        return "chess";
    }

    @Override
    public List<String> possibleSettings() {
        return settingsList;
    }

    private String settings;
    private state currState = state.UNFINISHED;
    private CoreGame core;
    private boolean zeroPlayerIsBlack;

    @Override
    public String getSettings() {
        return settings;
    }

    @Override
    public state getState() {
        return currState;
    }

    private state calcState() {
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
            currState = res.getPlayer() == 0 ? state.P1_WON : state.P0_WON;
            core.updateObs();
            return;
        }
        if (move instanceof chessGLCommand cmd) {
            core.makeMove(cmd.getInfo());
            forceRecalculation();
            core.updateObs();
            return;
        }
        throw new RuntimeException();
    }

    public void forceRecalculation() {
        if (getState() == state.UNFINISHED)
            currState = calcState();
    }

    @Override
    public boolean isMoveLegal(Command move) {
        if (move instanceof ResignationCommand)
            return getState() == state.UNFINISHED;
        if (move instanceof chessGLCommand cmd) {
            int pl = cmd.getPlayer();
            return getState() == state.UNFINISHED && pl == getTurn() && core.isMoveLegal(cmd.getInfo());
        }
        return false;
    }

    @Override
    public int getTurn() {
        return core.getCurrentPlayer() ^ (zeroPlayerIsBlack ? 1 : 0);
    }

    @Override
    public void start(String settings, int seed) { // TODO chess960
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
        if (viewModel.getModelUser() != user || viewModel.playingAs() != id)
            throw new RuntimeException();
        return viewModel;
    }

    public CoreGame getCore() {
        return core;
    }

    @Override
    public String toString() {
        return "chessGL{" +
                "settings='" + settings + '\'' +
                ", currState=" + currState +
                ", core=" + core +
                ", zeroPlayerIsBlack=" + zeroPlayerIsBlack +
                '}';
    }

    public boolean getZeroPlayerIsBlack() {
        return zeroPlayerIsBlack;
    }
}
