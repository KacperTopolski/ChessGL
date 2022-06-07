package chessGameLoader;

import GameLoader.client.Client;
import GameLoader.client.PlayViewModel;
import static GameLoader.common.Messages.*;

public class chessGLViewModel implements PlayViewModel {
    public chessGLViewModel(Client user, int id, chessGL game) {
        modelUser = user;
        modelGame = game;
        myPlayer = id;
        view = new chessGLView(this);

        int ourPlayer = playingAs() ^ (game.getZeroPlayerIsBlack() ? 1 : 0);

        game.getCore().connectSpectator((player, move, changedPieces) -> {
            MoveInfo info = game.getCore().getLastMove();
            if (player == ourPlayer && info != null)
                user.sendMessage(new MoveMessage(new chessGLCommand(playingAs(), info)));
        }, false);
    }

    private final Client modelUser;
    private final chessGL modelGame;
    private final int myPlayer;
    private final chessGLView view;

    @Override
    public chessGL getGame() {
        return modelGame;
    }

    @Override
    public int playingAs() {
        return myPlayer;
    }

    @Override
    public Client getModelUser() {
        return modelUser;
    }

    @Override
    public chessGLView createView() {
        return view;
    }
}
