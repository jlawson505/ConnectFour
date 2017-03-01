package Game;

/**
 * enum type to keep track of the game state
 *
 * @author James Lawson
 * @version 1.0 12/20/2016
 */
public enum GameState {
    PLAYER1_WIN,
    PLAYER2_WIN,
    DRAW,
    PLAYING,
    BAD_MOVE,
    WAITING_TO_START,
    FORCED_GAME_OVER,
    NO_MOVE
}
