package Game;

/**
 * @author James Lawson
 * @version 1.0 - 12/20/2016
 */
public interface GameModel {

    /**
     * resets all variables to default values to
     * start a new game
     */
    void newGame();

    /**
     *  PLAYER1_WIN if red wins
     *  PLAYER2_WIN if black wins
     *  DRAW if the board is full
     *  NO_WINNER_YET if valid move and the game is ingoing
     *  BAD_MOVE if the column is full
     *
     * @param col to perform the turn at
     * @return the appropriate game state
     */
    GameState doTurn(int col);

    /**
     * @return number of rows
     */
    int getRows();

    /**
     * @return number of columns
     */
    int getColumns();

    /**
     * @return the current player
     */
    PieceType getPlayer();
}
