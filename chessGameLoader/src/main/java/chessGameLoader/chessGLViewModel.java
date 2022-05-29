package chessGameLoader;

import GameLoader.client.Client;
import GameLoader.client.PlayViewModel;
import app.core.game.moves.Move;
import app.core.interactor.InteractiveGame;
import static GameLoader.common.Messages.*;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"rawtypes", "unchecked"})
public class chessGLViewModel implements PlayViewModel {
    public chessGLViewModel(Client user, int id, chessGL game) {
        modelUser = user;
        modelGame = game;
        myPlayer = id;
        view = new chessGLView(this);

        int ourPlayer = playingAs() ^ (game.getZeroPlayerIsBlack() ? 1 : 0);

        InteractiveGame ig = game.getCore().getInteractive();

        List<Move> lm = new ArrayList<>(ig.getLegalMoves(ourPlayer));

        ig.connectSpectator((player, move, changedPieces) -> {
            if (player != ourPlayer) {
                lm.clear();
                lm.addAll(ig.getLegalMoves(ourPlayer));
                return;
            }

            for (int i = 0; i < lm.size(); ++i) {
                if (lm.get(i).toString().equals(move.toString())) {
                    chessGLCommand cmd = new chessGLCommand(playingAs(), i);
                    user.sendMessage(new MoveMessage(cmd));

                    return;
                }
            }

            System.out.println(game);
            System.out.println(move);
            System.out.println(lm);

            throw new RuntimeException();
        });
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
