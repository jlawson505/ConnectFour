package GraphicalRendering;

import Player.PlayerState;

/**
 * Created by James Lawson on 2/20/2017.
 *
 * interface for describing how the client player code
 * interacts with a Graphical user interface
 */
public interface GameGUI
{

   int getClick();

    PlayerState newGame();

    void updateBoard(int col, boolean player);

    void allowClicks(boolean click);

    void doWin();

    void doLoss();

    void doDraw();

    void cleanUp();
}
