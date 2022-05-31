package chessGameLoader;

import GameLoader.client.PlayView;
import app.core.interactor.InteractiveGame;
import app.core.interactor.Player;
import app.ui.Style;
import app.ui.board.GraphicalBoard;
import app.ui.board.boards.InvertedBoard;
import app.ui.board.boards.NormalBoard;
import app.ui.checkers.CheckersConnector;
import app.ui.chess.ChessConnector;
import app.ui.styles.CutePink;
import app.utils.pieceplayer.StandalonePiecePlayer;
import javafx.scene.layout.VBox;

@SuppressWarnings({"rawtypes", "unchecked"})
public class chessGLView extends VBox implements PlayView {
    static final Style STYLE = new CutePink();

    public chessGLView(chessGLViewModel model) {
        chessGL game = model.getGame();
        CoreGame core = game.getCore();
        InteractiveGame ig = core.getInteractive();

        int ourPlayerId = model.playingAs() ^ (game.getZeroPlayerIsBlack() ? 1 : 0);

        // ourPlayer connects automatically to interactive game
        StandalonePiecePlayer ourPlayer = new StandalonePiecePlayer(ig, ourPlayerId);

        // connect second player
        ig.connectPlayer(1 - ourPlayerId, new Player());

        GraphicalBoard playerBoard = ourPlayerId == 0 ?
                new NormalBoard(40, STYLE) :
                new InvertedBoard(40, STYLE);

        if (core.isChess())
            ChessConnector.connect(playerBoard, ourPlayer);
        if (core.isCheckers())
            CheckersConnector.connect(playerBoard, ourPlayer);

        getChildren().add(playerBoard);
    }
}
