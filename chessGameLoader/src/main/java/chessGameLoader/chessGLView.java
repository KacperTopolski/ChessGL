package chessGameLoader;

import GameLoader.client.PlayView;
import app.ui.menu.DerpyButton;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class chessGLView extends VBox implements PlayView {
    public chessGLView(chessGLViewModel model) {
        int ourPlayerId = model.playingAs() ^ (model.getGame().getZeroPlayerIsBlack() ? 1 : 0);
        CoreGame core = model.getGame().getCore();

        VBox board = core.getGUI(ourPlayerId);
        Label l = new Label("White to play!");
        l.setFont(DerpyButton.font);
        l.setAlignment(Pos.CENTER);

        getChildren().addAll(l, board);

        core.connectSpectator((_a, _b, _c) -> l.setText(switch (core.getState()) {
            case UNFINISHED -> (core.getCurrentPlayer() == 0 ? "White" : "Black") + " to play!";
            case DRAW -> "Draw";
            case WHITE_WON -> "White won";
            case BLACK_WON -> "Black won";
        }), true);
    }
}
