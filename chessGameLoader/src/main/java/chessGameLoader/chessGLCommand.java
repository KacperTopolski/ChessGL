package chessGameLoader;

import static GameLoader.common.Serializables.Command;

public class chessGLCommand extends Command {
    private final MoveInfo info;

    public chessGLCommand(int player, MoveInfo moveInfo) {
        super(player);
        info = moveInfo;
    }

    public MoveInfo getInfo() {
        return info;
    }

    @Override
    public String toString() {
        return "chessGLCommand{" +
                "info=" + info +
                '}';
    }
}
