package chessGameLoader;

import GameLoader.client.Client;
import GameLoader.client.PlayViewModel;
import app.core.game.Field;
import app.core.game.Piece;
import app.core.game.moves.Move;
import app.core.interactor.InteractiveGame;
import static GameLoader.common.Messages.*;
import static chessGameLoader.MoveInfo.getPiece;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public class chessGLViewModel implements PlayViewModel {
    public chessGLViewModel(Client user, int id, chessGL game) {
        modelUser = user;
        modelGame = game;
        myPlayer = id;
        view = new chessGLView(this);

        int ourPlayer = playingAs() ^ (game.getZeroPlayerIsBlack() ? 1 : 0);

        InteractiveGame ig = game.getCore().getInteractive();

        Map<Piece, Field> oldPos = new HashMap<>();

        Runnable update = () -> {
            oldPos.clear();
            List<Move> lm = ig.getLegalMoves(ourPlayer);
            for (Move mv : lm) {
                Piece p = getPiece(mv);
                oldPos.put(p, p.getPosition());
            }
        };
        update.run();

        ig.connectSpectator((player, move, changedPieces) -> {
            if (player != ourPlayer) {
                update.run();
                return;
            }

            Piece p = getPiece(move);
            Field findPos = oldPos.get(p);
            MoveInfo mv = findPos != null ? new MoveInfo(findPos, move) : new MoveInfo(move);

            user.sendMessage(new MoveMessage(new chessGLCommand(playingAs(), mv)));
            update.run();
            game.forceRecalculation();
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
