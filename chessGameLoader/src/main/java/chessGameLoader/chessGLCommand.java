package chessGameLoader;

import static GameLoader.common.Serializables.Command;

public class chessGLCommand extends Command {
    int id;

    public chessGLCommand(int player, int idx) {
        super(player);
        id = idx;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "chessGLCommand{" +
                "id=" + id +
                ", player=" + getPlayer() +
                '}';
    }
}
