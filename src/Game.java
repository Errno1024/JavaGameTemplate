import core.MainFrame;
import settings.Constants;
//import game.MyScene;

public final class Game {
    private Game() {}

    public static void main(String[] args) {
        MainFrame frame = new MainFrame(Constants.gameTitle, Constants.windowWidth, Constants.windowHeight);
        //frame.startGame(new MyScene());
    }
}
